package com.example.demoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 测试活动记录功能
 */
public class TestActivityRecordActivity extends AppCompatActivity {
    
    private ActivityRecordManager recordManager;
    private TextView resultTextView;
    private Button addPomodoroButton;
    private Button addShortBreakButton;
    private Button addLongBreakButton;
    private Button viewRecordsButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_record);
        
        // 初始化视图
        resultTextView = findViewById(R.id.resultTextView);
        addPomodoroButton = findViewById(R.id.addPomodoroButton);
        addShortBreakButton = findViewById(R.id.addShortBreakButton);
        addLongBreakButton = findViewById(R.id.addLongBreakButton);
        viewRecordsButton = findViewById(R.id.viewRecordsButton);
        
        // 初始化记录管理器
        recordManager = new ActivityRecordManager(this);
        
        // 设置按钮点击事件
        addPomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTestRecord(ActivityRecord.TYPE_POMODORO);
            }
        });
        
        addShortBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTestRecord(ActivityRecord.TYPE_SHORT_BREAK);
            }
        });
        
        addLongBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTestRecord(ActivityRecord.TYPE_LONG_BREAK);
            }
        });
        
        viewRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTodayRecords();
            }
        });
        
        // 显示当前记录
        displayTodayRecords();
    }
    
    /**
     * 添加测试记录
     */
    private void addTestRecord(int type) {
        // 使用修复后的方法添加记录
        recordManager.addRecord(type);
        
        // 显示更新后的记录
        displayTodayRecords();
    }
    
    /**
     * 显示今天的记录
     */
    private void displayTodayRecords() {
        // 获取今天的记录
        List<ActivityRecord> todayRecords = recordManager.getRecordsForDate(new Date());
        
        // 显示结果
        StringBuilder result = new StringBuilder();
        result.append("今日记录: ").append(todayRecords.size()).append("条\n\n");
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        for (ActivityRecord record : todayRecords) {
            String typeStr;
            switch (record.getType()) {
                case ActivityRecord.TYPE_POMODORO:
                    typeStr = "番茄工作";
                    break;
                case ActivityRecord.TYPE_SHORT_BREAK:
                    typeStr = "短休息";
                    break;
                case ActivityRecord.TYPE_LONG_BREAK:
                    typeStr = "长休息";
                    break;
                default:
                    typeStr = "未知类型";
                    break;
            }
            
            // 计算持续时间（分钟）
            long durationMs = record.getEndTime().getTime() - record.getStartTime().getTime();
            int durationMinutes = (int) (durationMs / (1000 * 60));
            
            result.append(typeStr)
                  .append(": ")
                  .append(timeFormat.format(record.getStartTime()))
                  .append(" - ")
                  .append(timeFormat.format(record.getEndTime()))
                  .append(" (")
                  .append(durationMinutes)
                  .append("分钟)")
                  .append("\n");
        }
        
        resultTextView.setText(result.toString());
    }
} 