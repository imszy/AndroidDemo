package com.example.demoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 显示一天中每小时的活动记录详情的视图
 */
public class DailyDetailView extends View {
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
            return;
        }
        
        float density = getResources().getDisplayMetrics().density;
        float hourHeight = HOUR_HEIGHT * density;
        float padding = PADDING * density;
        float hourLabelWidth = 50 * density; // 小时标签宽度
        float timelineWidth = viewWidth - hourLabelWidth - padding * 2;
        
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
            
            // 绘制活动记录
            for (ActivityRecord record : hourRecords) {
                drawActivityBar(canvas, record, hour, hourLabelWidth + padding, y, timelineWidth, hourHeight);
            }
        }
    }
    
    private void drawActivityBar(Canvas canvas, ActivityRecord record, int hour, 
                               float startX, float startY, float timelineWidth, float hourHeight) {
        int startMinute = record.getStartMinute();
        int endMinute = record.getEndMinute();
        
        // 如果开始和结束时间跨小时，需要调整
        if (record.getHour() < hour) {
            startMinute = 0;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(record.getEndTime());
        if (cal.get(Calendar.HOUR_OF_DAY) > hour) {
            endMinute = 60;
        }
        
        // 计算在时间轴上的位置
        float startPos = startX + (startMinute / 60f) * timelineWidth;
        float endPos = startX + (endMinute / 60f) * timelineWidth;
        
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
        ActivityRecordManager manager = new ActivityRecordManager(getContext());
        return manager.getRecordsForHour(selectedDate, hour);
    }
} 