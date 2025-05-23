package com.example.demoapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 管理用户活动记录的工具类
 */
public class ActivityRecordManager {
    private static final String PREF_NAME = "activity_records";
    private static final String RECORDS_KEY = "records";
    
    private SharedPreferences preferences;
    private List<ActivityRecord> records;
    private Gson gson;
    private Context context;
    
    public ActivityRecordManager(Context context) {
        this.context = context;
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
        } else {
            Type type = new TypeToken<ArrayList<ActivityRecord>>(){}.getType();
            records = gson.fromJson(json, type);
        }
    }
    
    /**
     * 保存记录到SharedPreferences
     */
    private void saveRecords() {
        String json = gson.toJson(records);
        preferences.edit().putString(RECORDS_KEY, json).apply();
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
        return records.size() - 1; // 返回索引作为ID
    }
    
    /**
     * 更新活动记录的结束时间
     */
    public void endActivity(int recordId) {
        if (recordId >= 0 && recordId < records.size()) {
            records.get(recordId).setEndTime(new Date());
            saveRecords();
        }
    }
    
    /**
     * 添加一个活动记录（包含开始和结束时间）
     */
    public void addRecord(int type, Date startTime, Date endTime) {
        records.add(new ActivityRecord(startTime, type, startTime, endTime));
        saveRecords();
    }
    
    /**
     * 添加一个活动记录（包含默认持续时间）
     */
    public void addRecord(int type) {
        Date startTime = new Date();
        // 设置默认持续时间（根据类型和用户设置）
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        
        // 获取用户设置的时间
        TimerPreferences timerPrefs = new TimerPreferences(context);
        
        switch (type) {
            case ActivityRecord.TYPE_POMODORO:
                cal.add(Calendar.MINUTE, timerPrefs.getPomodoroTime()); // 用户设置的番茄时间
                break;
            case ActivityRecord.TYPE_SHORT_BREAK:
                cal.add(Calendar.MINUTE, timerPrefs.getShortBreakTime()); // 用户设置的短休息时间
                break;
            case ActivityRecord.TYPE_LONG_BREAK:
                cal.add(Calendar.MINUTE, timerPrefs.getLongBreakTime()); // 用户设置的长休息时间
                break;
        }
        
        Date endTime = cal.getTime();
        records.add(new ActivityRecord(startTime, type, startTime, endTime));
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
        
        return getRecordsInRange(startDate, new Date());
    }
    
    /**
     * 获取指定日期的所有记录
     */
    public List<ActivityRecord> getRecordsForDate(Date date) {
        List<ActivityRecord> result = new ArrayList<>();
        
        for (ActivityRecord record : records) {
            if (record.isSameDay(date)) {
                result.add(record);
            }
        }
        
        return result;
    }
    
    /**
     * 获取指定日期指定小时的记录
     */
    public List<ActivityRecord> getRecordsForHour(Date date, int hour) {
        List<ActivityRecord> result = new ArrayList<>();
        
        for (ActivityRecord record : records) {
            if (record.isSameDay(date) && record.getHour() == hour) {
                result.add(record);
            }
        }
        
        return result;
    }
    
    /**
     * 获取所有记录
     */
    public List<ActivityRecord> getAllRecords() {
        return new ArrayList<>(records);
    }
    
    /**
     * 清除所有记录
     */
    public void clearAllRecords() {
        records.clear();
        saveRecords();
    }
} 