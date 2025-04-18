package com.example.demoapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 显示一天的详细活动记录
 */
public class DailyDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_DATE = "com.example.demoapp.EXTRA_DATE";
    
    private DailyDetailView dailyDetailView;
    private TextView dateTextView;
    private ActivityRecordManager recordManager;
    private Date selectedDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);
        
        // 初始化视图
        dailyDetailView = findViewById(R.id.daily_detail_view);
        dateTextView = findViewById(R.id.text_selected_date);
        
        // 获取传入的日期
        long dateMillis = getIntent().getLongExtra(EXTRA_DATE, 0);
        if (dateMillis == 0) {
            selectedDate = new Date(); // 默认为今天
        } else {
            selectedDate = new Date(dateMillis);
        }
        
        // 初始化数据管理器
        recordManager = new ActivityRecordManager(this);
        
        // 添加调试记录
        // 仅用于测试，在实际发布时应删除
        if (recordManager.getRecordsForDate(selectedDate).isEmpty()) {
            addDebugRecords();
            Toast.makeText(this, "已添加测试记录数据", Toast.LENGTH_SHORT).show();
        }
        
        // 更新UI
        updateUI();
    }
    
    /**
     * 添加调试用的记录数据
     */
    private void addDebugRecords() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startTime = cal.getTime();
        
        cal.set(Calendar.MINUTE, 25);
        Date endTime = cal.getTime();
        
        // 添加一个上午9:00-9:25的番茄记录
        recordManager.addRecord(ActivityRecord.TYPE_POMODORO, startTime, endTime);
        
        // 添加一个上午9:25-9:30的短休息
        cal.set(Calendar.MINUTE, 25);
        startTime = cal.getTime();
        cal.set(Calendar.MINUTE, 30);
        endTime = cal.getTime();
        recordManager.addRecord(ActivityRecord.TYPE_SHORT_BREAK, startTime, endTime);
        
        // 添加一个14:00-14:25的番茄记录
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 0);
        startTime = cal.getTime();
        cal.set(Calendar.MINUTE, 25);
        endTime = cal.getTime();
        recordManager.addRecord(ActivityRecord.TYPE_POMODORO, startTime, endTime);
        
        // 添加一个14:25-14:40的长休息
        cal.set(Calendar.MINUTE, 25);
        startTime = cal.getTime();
        cal.set(Calendar.MINUTE, 40);
        endTime = cal.getTime();
        recordManager.addRecord(ActivityRecord.TYPE_LONG_BREAK, startTime, endTime);
    }
    
    private void updateUI() {
        // 设置日期标题
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        dateTextView.setText(dateFormat.format(selectedDate));
        
        // 获取该日期的所有记录
        List<ActivityRecord> records = recordManager.getRecordsForDate(selectedDate);
        
        // 显示调试信息
        if (records.isEmpty()) {
            Toast.makeText(this, "没有找到记录数据", Toast.LENGTH_LONG).show();
        } else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            StringBuilder recordInfo = new StringBuilder();
            recordInfo.append("找到记录: ").append(records.size()).append("条\n");
            
            for (int i = 0; i < Math.min(records.size(), 3); i++) {
                ActivityRecord record = records.get(i);
                String type = "";
                switch (record.getType()) {
                    case ActivityRecord.TYPE_POMODORO:
                        type = "番茄";
                        break;
                    case ActivityRecord.TYPE_SHORT_BREAK:
                        type = "短休息";
                        break;
                    case ActivityRecord.TYPE_LONG_BREAK:
                        type = "长休息";
                        break;
                }
                recordInfo.append(type)
                          .append(": ")
                          .append(timeFormat.format(record.getStartTime()))
                          .append(" - ")
                          .append(timeFormat.format(record.getEndTime()))
                          .append("\n");
            }
            
            Toast.makeText(this, recordInfo.toString(), Toast.LENGTH_LONG).show();
        }
        
        // 更新视图
        dailyDetailView.setDate(selectedDate, records);
    }
} 