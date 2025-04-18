package com.example.demoapp;

import java.util.Calendar;
import java.util.Date;

/**
 * 存储用户活动记录的模型类
 */
public class ActivityRecord {
    public static final int TYPE_POMODORO = 0;
    public static final int TYPE_SHORT_BREAK = 1;
    public static final int TYPE_LONG_BREAK = 2;
    
    private Date date;
    private int type;
    private int count;
    
    public ActivityRecord(Date date, int type) {
        this.date = date;
        this.type = type;
        this.count = 1;
    }
    
    public ActivityRecord(Date date, int type, int count) {
        this.date = date;
        this.type = type;
        this.count = count;
    }
    
    public Date getDate() {
        return date;
    }
    
    public int getType() {
        return type;
    }
    
    public int getCount() {
        return count;
    }
    
    public void incrementCount() {
        count++;
    }
    
    /**
     * 检查两个日期是否是同一天
     */
    public boolean isSameDay(Date otherDate) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(otherDate);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
} 