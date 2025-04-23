package com.example.demoapp;

import android.os.Bundle;
import android.widget.TextView;

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
        
        // 更新UI
        updateUI();
    }
    
    private void updateUI() {
        // 设置日期标题
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        dateTextView.setText(dateFormat.format(selectedDate));
        
        // 获取该日期的所有记录
        List<ActivityRecord> records = recordManager.getRecordsForDate(selectedDate);
        
        // 更新视图
        dailyDetailView.setDate(selectedDate, records);
    }
} 