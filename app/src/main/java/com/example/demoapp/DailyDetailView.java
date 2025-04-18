package com.example.demoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 显示一天中每小时的活动记录详情的视图
 */
public class DailyDetailView extends View {
    private static final String TAG = "DailyDetailView";
    private static final int HOURS_IN_DAY = 24;
    private static final int HOUR_HEIGHT = 40; // dp
    private static final int TEXT_SIZE = 12; // sp
    private static final int PADDING = 16; // dp
    
    private static final int COLOR_POMODORO = Color.rgb(255, 99, 71); // 番茄红色
    private static final int COLOR_SHORT_BREAK = Color.rgb(144, 238, 144); // 浅绿色
    private static final int COLOR_LONG_BREAK = Color.rgb(0, 128, 0); // 深绿色
    private static final int COLOR_HOUR_TEXT = Color.BLACK;
    private static final int COLOR_HOUR_LINE = Color.LTGRAY;
    
    private Paint hourTextPaint;
    private Paint activityPaint;
    private Paint hourLinePaint;
    
    private List<ActivityRecord> records;
    private Date selectedDate;
    private int viewWidth;
    
    public DailyDetailView(Context context) {
        super(context);
        init();
    }
    
    public DailyDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public DailyDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        float density = getResources().getDisplayMetrics().density;
        
        hourTextPaint = new Paint();
        hourTextPaint.setColor(COLOR_HOUR_TEXT);
        hourTextPaint.setTextSize(TEXT_SIZE * density);
        hourTextPaint.setAntiAlias(true);
        
        activityPaint = new Paint();
        activityPaint.setAntiAlias(true);
        
        hourLinePaint = new Paint();
        hourLinePaint.setColor(COLOR_HOUR_LINE);
        hourLinePaint.setStrokeWidth(1 * density);
    }
    
    public void setDate(Date date, List<ActivityRecord> records) {
        this.selectedDate = date;
        this.records = records;
        
        // 调试信息
        if (records != null) {
            Log.d(TAG, "设置记录: " + records.size() + "条");
            for (ActivityRecord record : records) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String type = record.getType() == ActivityRecord.TYPE_POMODORO ? "番茄" : 
                              record.getType() == ActivityRecord.TYPE_SHORT_BREAK ? "短休息" : "长休息";
                
                Log.d(TAG, type + ": " + 
                     timeFormat.format(record.getStartTime()) + " - " + 
                     timeFormat.format(record.getEndTime()));
            }
        } else {
            Log.d(TAG, "设置记录: null");
        }
        
        invalidate();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float density = getResources().getDisplayMetrics().density;
        int desiredHeight = (int) ((HOUR_HEIGHT * HOURS_IN_DAY + PADDING * 2) * density);
        
        int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (records == null || selectedDate == null) {
            Log.d(TAG, "onDraw: 无记录或日期为空");
            return;
        }
        
        float density = getResources().getDisplayMetrics().density;
        float hourHeight = HOUR_HEIGHT * density;
        float padding = PADDING * density;
        float hourLabelWidth = 50 * density; // 小时标签宽度
        float timelineWidth = viewWidth - hourLabelWidth - padding * 2;
        
        // 显示重要调试信息
        if (records.isEmpty()) {
            Toast.makeText(getContext(), "没有活动记录数据可显示", Toast.LENGTH_SHORT).show();
        }
        
        int totalDrawn = 0;
        
        // 绘制每个小时的横条
        for (int hour = 0; hour < HOURS_IN_DAY; hour++) {
            float y = padding + hour * hourHeight;
            
            // 绘制小时标签
            String hourLabel = String.format("%02d:00", hour);
            canvas.drawText(hourLabel, padding, y + hourHeight / 2 + hourTextPaint.getTextSize() / 3, hourTextPaint);
            
            // 绘制小时横线
            canvas.drawLine(
                    hourLabelWidth + padding, 
                    y + hourHeight, 
                    viewWidth - padding, 
                    y + hourHeight, 
                    hourLinePaint);
            
            // 获取该小时的活动记录
            List<ActivityRecord> hourRecords = getRecordsForHour(hour);
            
            // 调试信息
            if (!hourRecords.isEmpty()) {
                Log.d(TAG, hour + "点有 " + hourRecords.size() + " 条记录");
            }
            
            // 绘制活动记录
            for (ActivityRecord record : hourRecords) {
                drawActivityBar(canvas, record, hour, hourLabelWidth + padding, y, timelineWidth, hourHeight);
                totalDrawn++;
            }
        }
        
        // 如果记录非空但没有绘制任何内容，显示错误信息
        if (!records.isEmpty() && totalDrawn == 0) {
            Toast.makeText(getContext(), "有记录但未能绘制: " + records.size() + "条", Toast.LENGTH_LONG).show();
        } else if (totalDrawn > 0) {
            Toast.makeText(getContext(), "成功绘制了 " + totalDrawn + " 条记录", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void drawActivityBar(Canvas canvas, ActivityRecord record, int hour, 
                               float startX, float startY, float timelineWidth, float hourHeight) {
        // 记录日志
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String type = record.getType() == ActivityRecord.TYPE_POMODORO ? "番茄" : 
                      record.getType() == ActivityRecord.TYPE_SHORT_BREAK ? "短休息" : "长休息";
        Log.d(TAG, "绘制 " + hour + "点的 " + type + ": " + 
              timeFormat.format(record.getStartTime()) + " - " + 
              timeFormat.format(record.getEndTime()));
        
        int startMinute = record.getStartMinute();
        int endMinute = record.getEndMinute();
        
        // 如果开始和结束时间跨小时，需要调整
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(record.getStartTime());
        if (startCal.get(Calendar.HOUR_OF_DAY) < hour) {
            startMinute = 0;
            Log.d(TAG, "调整开始时间为00分");
        }
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(record.getEndTime());
        if (endCal.get(Calendar.HOUR_OF_DAY) > hour) {
            endMinute = 60;
            Log.d(TAG, "调整结束时间为60分");
        }
        
        // 计算在时间轴上的位置
        float startPos = startX + (startMinute / 60f) * timelineWidth;
        float endPos = startX + (endMinute / 60f) * timelineWidth;
        
        Log.d(TAG, "绘制位置: " + startPos + " - " + endPos + ", 开始分钟: " + startMinute + ", 结束分钟: " + endMinute);
        
        // 设置活动类型对应的颜色
        switch (record.getType()) {
            case ActivityRecord.TYPE_POMODORO:
                activityPaint.setColor(COLOR_POMODORO);
                break;
            case ActivityRecord.TYPE_SHORT_BREAK:
                activityPaint.setColor(COLOR_SHORT_BREAK);
                break;
            case ActivityRecord.TYPE_LONG_BREAK:
                activityPaint.setColor(COLOR_LONG_BREAK);
                break;
        }
        
        // 绘制活动条
        RectF rect = new RectF(startPos, startY + hourHeight * 0.2f, 
                             endPos, startY + hourHeight * 0.8f);
        canvas.drawRect(rect, activityPaint);
    }
    
    private List<ActivityRecord> getRecordsForHour(int hour) {
        if (records == null) {
            return new ArrayList<>();
        }
        
        List<ActivityRecord> result = new ArrayList<>();
        
        for (ActivityRecord record : records) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(record.getStartTime());
            
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(record.getEndTime());
            
            // 获取开始和结束小时
            int startHour = startCal.get(Calendar.HOUR_OF_DAY);
            int endHour = endCal.get(Calendar.HOUR_OF_DAY);
            
            // 如果活动时间范围与当前小时有重叠，则添加
            if ((startHour <= hour && endHour >= hour) ||
                (startHour == hour) ||
                (endHour == hour)) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String type = record.getType() == ActivityRecord.TYPE_POMODORO ? "番茄" : 
                              record.getType() == ActivityRecord.TYPE_SHORT_BREAK ? "短休息" : "长休息";
                
                Log.d(TAG, "小时 " + hour + " 匹配到记录: " + type + " " +
                     timeFormat.format(record.getStartTime()) + " - " + 
                     timeFormat.format(record.getEndTime()) + 
                     " (startHour=" + startHour + ", endHour=" + endHour + ")");
                
                result.add(record);
            }
        }
        
        return result;
    }
} 