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
     * 添加一个活动记录
     */
    public void addRecord(int type) {
        Date now = new Date();
        // 查找是否已存在当天同类型的记录
        boolean foundExisting = false;
        
        for (ActivityRecord record : records) {
            if (record.getType() == type && record.isSameDay(now)) {
                record.incrementCount();
                foundExisting = true;
                break;
            }
        }
        
        // 如果不存在，创建新记录
        if (!foundExisting) {
            records.add(new ActivityRecord(now, type));
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