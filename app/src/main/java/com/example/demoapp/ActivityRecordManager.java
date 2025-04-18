package com.example.demoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 管理用户活动记录的工具类
 */
public class ActivityRecordManager {
    private static final String TAG = "ActivityRecordManager";
    private static final String PREF_NAME = "activity_records";
    private static final String RECORDS_KEY = "records";
    
    private SharedPreferences preferences;
    private List<ActivityRecord> records;
    private Gson gson;
    
    public ActivityRecordManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadRecords();
    }
    
    /**
     * 从SharedPreferences加载记录
     */
    private void loadRecords() {
        String json = preferences.getString(RECORDS_KEY, null);
        if (json == null) {
            records = new ArrayList<>();
            Log.d(TAG, "没有找到已保存的记录，创建新列表");
        } else {
            Type type = new TypeToken<ArrayList<ActivityRecord>>(){}.getType();
            records = gson.fromJson(json, type);
            Log.d(TAG, "从SharedPreferences加载了 " + records.size() + " 条记录");
        }
    }
    
    /**
     * 保存记录到SharedPreferences
     */
    private void saveRecords() {
        String json = gson.toJson(records);
        preferences.edit().putString(RECORDS_KEY, json).apply();
        Log.d(TAG, "保存了 " + records.size() + " 条记录到SharedPreferences");
    }
    
    /**
     * 添加一个活动记录的开始
     * @return 返回新创建的记录ID，用于后续更新结束时间
     */
    public int startActivity(int type) {
        Date now = new Date();
        ActivityRecord newRecord = new ActivityRecord(now, type);
        records.add(newRecord);
        saveRecords();
        Log.d(TAG, "开始了新活动: 类型=" + type + ", 时间=" + now);
        return records.size() - 1; // 返回索引作为ID
    }
    
    /**
     * 更新活动记录的结束时间
     */
    public void endActivity(int recordId) {
        if (recordId >= 0 && recordId < records.size()) {
            Date now = new Date();
            records.get(recordId).setEndTime(now);
            saveRecords();
            Log.d(TAG, "结束活动: ID=" + recordId + ", 时间=" + now);
        } else {
            Log.e(TAG, "无效的记录ID: " + recordId);
        }
    }
    
    /**
     * 添加一个活动记录（包含开始和结束时间）
     */
    public void addRecord(int type, Date startTime, Date endTime) {
        ActivityRecord newRecord = new ActivityRecord(startTime, type, startTime, endTime);
        records.add(newRecord);
        saveRecords();
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String typeStr = type == ActivityRecord.TYPE_POMODORO ? "番茄" : 
                         type == ActivityRecord.TYPE_SHORT_BREAK ? "短休息" : "长休息";
        
        Log.d(TAG, "添加了一条新记录: " + typeStr + " " + 
              timeFormat.format(startTime) + " - " + timeFormat.format(endTime));
    }
    
    /**
     * 添加一个活动记录（兼容旧方法）
     */
    public void addRecord(int type) {
        Date now = new Date();
        // 查找是否已存在当天同类型的记录
        boolean foundExisting = false;
        
        for (ActivityRecord record : records) {
            if (record.getType() == type && record.isSameDay(now)) {
                record.incrementCount();
                foundExisting = true;
                Log.d(TAG, "增加了已存在记录的计数: 类型=" + type);
                break;
            }
        }
        
        // 如果不存在，创建新记录
        if (!foundExisting) {
            records.add(new ActivityRecord(now, type));
            Log.d(TAG, "添加了新记录: 类型=" + type + ", 时间=" + now);
        }
        
        // 保存更新后的记录
        saveRecords();
    }
    
    /**
     * 获取指定日期范围内的记录
     */
    public List<ActivityRecord> getRecordsInRange(Date startDate, Date endDate) {
        List<ActivityRecord> result = new ArrayList<>();
        
        for (ActivityRecord record : records) {
            Date recordDate = record.getDate();
            if (recordDate.compareTo(startDate) >= 0 && recordDate.compareTo(endDate) <= 0) {
                result.add(record);
            }
        }
        
        Log.d(TAG, "获取了范围内的记录: " + result.size() + " 条 (从 " + startDate + " 到 " + endDate + ")");
        return result;
    }
    
    /**
     * 获取最近N天的记录
     */
    public List<ActivityRecord> getRecentRecords(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days + 1); // 包含今天
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        
        List<ActivityRecord> result = getRecordsInRange(startDate, new Date());
        Log.d(TAG, "获取了最近 " + days + " 天的记录: " + result.size() + " 条");
        return result;
    }
    
    /**
     * 获取指定日期的所有记录
     */
    public List<ActivityRecord> getRecordsForDate(Date date) {
        if (date == null) {
            Log.e(TAG, "getRecordsForDate: 日期为空");
            return new ArrayList<>();
        }
        
        List<ActivityRecord> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String targetDateStr = dateFormat.format(date);
        
        for (ActivityRecord record : records) {
            if (record.getStartTime() != null) {
                String recordDateStr = dateFormat.format(record.getStartTime());
                if (recordDateStr.equals(targetDateStr)) {
                    result.add(record);
                    Log.d(TAG, "匹配到日期记录: " + dateFormat.format(record.getStartTime()));
                }
            } else {
                Log.w(TAG, "记录的开始时间为空");
            }
        }
        
        Log.d(TAG, "获取了日期 " + targetDateStr + " 的记录: " + result.size() + " 条");
        return result;
    }
    
    /**
     * 获取指定日期指定小时的记录
     */
    public List<ActivityRecord> getRecordsForHour(Date date, int hour) {
        List<ActivityRecord> result = new ArrayList<>();
        List<ActivityRecord> dateRecords = getRecordsForDate(date);
        
        for (ActivityRecord record : dateRecords) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(record.getStartTime());
            int startHour = cal.get(Calendar.HOUR_OF_DAY);
            
            cal.setTime(record.getEndTime());
            int endHour = cal.get(Calendar.HOUR_OF_DAY);
            
            // 如果记录时间跨越该小时，则添加到结果中
            if ((startHour <= hour && endHour >= hour) || startHour == hour || endHour == hour) {
                result.add(record);
            }
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Log.d(TAG, "获取了日期 " + dateFormat.format(date) + " " + hour + "时的记录: " + result.size() + " 条");
        return result;
    }
    
    /**
     * 获取所有记录
     */
    public List<ActivityRecord> getAllRecords() {
        Log.d(TAG, "获取了所有记录: " + records.size() + " 条");
        return new ArrayList<>(records);
    }
    
    /**
     * 清除所有记录
     */
    public void clearAllRecords() {
        Log.d(TAG, "清除了所有记录");
        records.clear();
        saveRecords();
    }
} 