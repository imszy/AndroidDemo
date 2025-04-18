package com.example.demoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * 显示用户活动历史的热图界面
 */
public class HeatmapActivity extends BaseActivity {
    
    private CalendarHeatmapView heatmapView;
    private TextView pomodoroCountTextView;
    private TextView shortBreakCountTextView;
    private TextView longBreakCountTextView;
    private Button backButton;
    
    private ActivityRecordManager recordManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_history);
        }
        
        // 初始化视图组件
        heatmapView = findViewById(R.id.heatmapView);
        pomodoroCountTextView = findViewById(R.id.pomodoroCountTextView);
        shortBreakCountTextView = findViewById(R.id.shortBreakCountTextView);
        longBreakCountTextView = findViewById(R.id.longBreakCountTextView);
        backButton = findViewById(R.id.backButton);
        
        // 初始化记录管理器
        recordManager = new ActivityRecordManager(this);
        
        // 加载并显示记录
        loadAndDisplayRecords();
        
        // 设置返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    /**
     * 加载并显示活动记录
     */
    private void loadAndDisplayRecords() {
        // 获取最近30天的记录
        List<ActivityRecord> records = recordManager.getRecentRecords(30);
        
        // 设置热图数据
        heatmapView.setRecords(records);
        
        // 统计各类型的记录数量
        int pomodoroCount = 0;
        int shortBreakCount = 0;
        int longBreakCount = 0;
        
        for (ActivityRecord record : records) {
            switch (record.getType()) {
                case ActivityRecord.TYPE_POMODORO:
                    pomodoroCount += record.getCount();
                    break;
                case ActivityRecord.TYPE_SHORT_BREAK:
                    shortBreakCount += record.getCount();
                    break;
                case ActivityRecord.TYPE_LONG_BREAK:
                    longBreakCount += record.getCount();
                    break;
            }
        }
        
        // 更新统计文本显示
        pomodoroCountTextView.setText(getString(R.string.pomodoro_count, pomodoroCount));
        shortBreakCountTextView.setText(getString(R.string.short_break_count, shortBreakCount));
        longBreakCountTextView.setText(getString(R.string.long_break_count, longBreakCount));
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 