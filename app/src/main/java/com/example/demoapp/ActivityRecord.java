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
    private Date startTime;
    private Date endTime;
    
    public ActivityRecord(Date date, int type) {
        this.date = date;
        this.type = type;
        this.count = 1;
        this.startTime = date;
        this.endTime = date; // Will be updated when activity ends
    }
    
    public ActivityRecord(Date date, int type, int count) {
        this.date = date;
        this.type = type;
        this.count = count;
        this.startTime = date;
        this.endTime = date;
    }
    
    public ActivityRecord(Date date, int type, Date startTime, Date endTime) {
        this.date = date;
        this.type = type;
        this.count = 1;
        this.startTime = startTime;
        this.endTime = endTime;
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
    
    public Date getStartTime() {
        return startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
    
    /**
     * 获取活动发生的小时
     */
    public int getHour() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 获取活动开始的分钟
     */
    public int getStartMinute() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        return cal.get(Calendar.MINUTE);
    }
    
    /**
     * 获取活动结束的分钟
     */
    public int getEndMinute() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(endTime);
        return cal.get(Calendar.MINUTE);
    }
} 