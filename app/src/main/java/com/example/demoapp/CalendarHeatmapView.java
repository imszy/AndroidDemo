package com.example.demoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 显示类似GitHub贡献日历的自定义视图
 */
public class CalendarHeatmapView extends View {
    
    // 颜色常量
    private static final int COLOR_POMODORO = Color.parseColor("#e74c3c"); // 番茄色
    private static final int COLOR_SHORT_BREAK = Color.parseColor("#2ecc71"); // 浅绿色
    private static final int COLOR_LONG_BREAK = Color.parseColor("#27ae60"); // 深绿色
    private static final int COLOR_ACTIVITY = Color.parseColor("#2ecc71"); // 活动标记颜色（绿色）
    private static final int COLOR_EMPTY = Color.parseColor("#f5f5f5"); // 空格子颜色
    private static final int COLOR_TEXT = Color.parseColor("#7f8c8d"); // 文字颜色
    
    // 视图属性
    private static final int DAYS_TO_SHOW = 30; // 显示最近30天
    private static final int DAYS_PER_ROW = 7; // 每行显示7天
    private static final int ROWS = (DAYS_TO_SHOW + DAYS_PER_ROW - 1) / DAYS_PER_ROW; // 计算行数
    private static final float CELL_PADDING_RATIO = 0.1f; // 单元格内边距比例
    
    private Paint cellPaint; // 绘制单元格
    private Paint textPaint; // 绘制文本
    private List<ActivityRecord> records = new ArrayList<>(); // 活动记录
    private Map<String, Boolean> dateHasActivity = new HashMap<>(); // 日期是否有活动的映射
    
    private float cellSize; // 单元格大小
    
    // 创建日期格式化工具
    private SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    private SimpleDateFormat keyFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    
    // 存储每个单元格的日期和位置
    private List<DateCell> dateCells = new ArrayList<>();
    
    // 日期点击监听器
    private OnDateClickListener onDateClickListener;
    
    // 定义日期点击监听器接口
    public interface OnDateClickListener {
        void onDateClick(Date date);
    }
    
    // 日期单元格内部类
    private class DateCell {
        RectF rect;
        Date date;
        
        DateCell(RectF rect, Date date) {
            this.rect = rect;
            this.date = date;
        }
    }
    
    public CalendarHeatmapView(Context context) {
        super(context);
        init();
    }
    
    public CalendarHeatmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public CalendarHeatmapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        // 初始化绘制单元格的画笔
        cellPaint = new Paint();
        cellPaint.setAntiAlias(true);
        
        // 初始化绘制文本的画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(COLOR_TEXT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        // 使视图可点击
        setClickable(true);
    }
    
    /**
     * 设置日期点击监听器
     */
    public void setOnDateClickListener(OnDateClickListener listener) {
        this.onDateClickListener = listener;
    }
    
    /**
     * 设置活动记录
     */
    public void setRecords(List<ActivityRecord> records) {
        this.records = records;
        // 创建日期到活动状态的映射
        dateHasActivity.clear();
        for (ActivityRecord record : records) {
            String key = keyFormat.format(record.getDate());
            dateHasActivity.put(key, true); // 只需要知道该日期有活动即可
        }
        invalidate(); // 重绘视图
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // 计算单元格大小（宽度/列数 和 高度/行数 中的较小值）
        float cellWidth = w / (float) DAYS_PER_ROW;
        float cellHeight = h / (float) ROWS;
        cellSize = Math.min(cellWidth, cellHeight);
        
        // 调整文本大小
        textPaint.setTextSize(cellSize * 0.4f);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // 清空日期单元格列表
        dateCells.clear();
        
        // 计算开始日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -(DAYS_TO_SHOW - 1)); // 从30天前开始
        
        // 绘制每个单元格
        float padding = cellSize * CELL_PADDING_RATIO;
        
        for (int i = 0; i < DAYS_TO_SHOW; i++) {
            Date date = calendar.getTime();
            String key = keyFormat.format(date);
            
            // 计算行和列
            int row = i / DAYS_PER_ROW;
            int col = i % DAYS_PER_ROW;
            
            // 计算单元格位置
            float left = col * cellSize + padding;
            float top = row * cellSize + padding;
            float right = left + cellSize - 2 * padding;
            float bottom = top + cellSize - 2 * padding;
            RectF rect = new RectF(left, top, right, bottom);
            
            // 保存日期单元格
            dateCells.add(new DateCell(rect, (Date) date.clone()));
            
            // 设置单元格颜色 - 有活动的日期统一使用绿色，没有活动的日期使用空颜色
            if (dateHasActivity.containsKey(key)) {
                cellPaint.setColor(COLOR_ACTIVITY);
            } else {
                cellPaint.setColor(COLOR_EMPTY);
            }
            
            // 绘制圆角矩形
            canvas.drawRoundRect(rect, cellSize * 0.2f, cellSize * 0.2f, cellPaint);
            
            // 绘制日期数字
            String dayText = dayFormat.format(date);
            float textX = left + (cellSize - 2 * padding) / 2;
            float textY = top + (cellSize - 2 * padding) / 2 + textPaint.getTextSize() / 3; // 垂直居中
            canvas.drawText(dayText, textX, textY, textPaint);
            
            // 移动到下一天
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * ROWS / (float) DAYS_PER_ROW); // 保持行列比例
        
        setMeasuredDimension(width, height);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            
            // 检查点击的单元格
            for (DateCell cell : dateCells) {
                if (cell.rect.contains(x, y)) {
                    if (onDateClickListener != null) {
                        onDateClickListener.onDateClick(cell.date);
                    }
                    return true;
                }
            }
        }
        
        return super.onTouchEvent(event);
    }
} 