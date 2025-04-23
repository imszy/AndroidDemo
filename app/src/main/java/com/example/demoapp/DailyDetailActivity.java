package com.example.demoapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 显示一天的详细活动记录
 */
public class DailyDetailActivity extends AppCompatActivity implements DailyDetailView.OnActivityClickListener {
    
    public static final String EXTRA_DATE = "com.example.demoapp.EXTRA_DATE";
    
    private DailyDetailView dailyDetailView;
    private TextView dateTextView;
    private ActivityRecordManager recordManager;
    private Date selectedDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);
        
        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.daily_detail);
        }
        
        // 初始化视图
        dailyDetailView = findViewById(R.id.daily_detail_view);
        dateTextView = findViewById(R.id.text_selected_date);
        
        // 设置活动点击监听器
        dailyDetailView.setOnActivityClickListener(this);
        
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
        
        // 显示记录信息
        if (records.isEmpty()) {
            Toast.makeText(this, "该日期没有活动记录", Toast.LENGTH_SHORT).show();
        } /*else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            StringBuilder info = new StringBuilder();
            info.append("找到").append(records.size()).append("条记录");
            
            ActivityRecord sample = records.get(0);
            String typeStr = "";
            switch (sample.getType()) {
                case ActivityRecord.TYPE_POMODORO:
                    typeStr = "番茄工作";
                    break;
                case ActivityRecord.TYPE_SHORT_BREAK:
                    typeStr = "短休息";
                    break;
                case ActivityRecord.TYPE_LONG_BREAK:
                    typeStr = "长休息";
                    break;
            }
            
            long durationMs = sample.getEndTime().getTime() - sample.getStartTime().getTime();
            int durationMinutes = (int) (durationMs / (1000 * 60));
            
            info.append("，例如：").append(typeStr)
                .append("：").append(timeFormat.format(sample.getStartTime()))
                .append("-").append(timeFormat.format(sample.getEndTime()))
                .append(" (").append(durationMinutes).append("分钟)");
            
            Toast.makeText(this, info.toString(), Toast.LENGTH_LONG).show();
        }*/
        
        // 更新视图
        dailyDetailView.setDate(selectedDate, records);
    }
    
    /**
     * 处理菜单项点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 处理返回按钮点击
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 当用户点击活动记录时调用
     */
    @Override
    public void onActivityClick(ActivityRecord record) {
        // 显示详细的活动信息对话框
        showActivityDetailDialog(record);
    }
    
    /**
     * 显示活动记录详情对话框
     */
    private void showActivityDetailDialog(ActivityRecord record) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        
        String typeStr;
        int iconResId;
        
        switch (record.getType()) {
            case ActivityRecord.TYPE_POMODORO:
                typeStr = "番茄工作";
                iconResId = R.drawable.pomodoro_indicator;
                break;
            case ActivityRecord.TYPE_SHORT_BREAK:
                typeStr = "短休息";
                iconResId = R.drawable.short_break_indicator;
                break;
            case ActivityRecord.TYPE_LONG_BREAK:
                typeStr = "长休息";
                iconResId = R.drawable.long_break_indicator;
                break;
            default:
                typeStr = "未知类型";
                iconResId = android.R.drawable.ic_dialog_info;
                break;
        }
        
        // 计算持续时间
        long durationMs = record.getEndTime().getTime() - record.getStartTime().getTime();
        int durationMinutes = (int) (durationMs / (1000 * 60));
        int durationSeconds = (int) ((durationMs / 1000) % 60);
        
        String message = String.format("类型: %s\n\n日期: %s\n\n开始时间: %s\n\n结束时间: %s\n\n持续时间: %d分%d秒",
                typeStr,
                dateFormat.format(record.getDate()),
                timeFormat.format(record.getStartTime()),
                timeFormat.format(record.getEndTime()),
                durationMinutes,
                durationSeconds);
        
        new AlertDialog.Builder(this)
                .setTitle("活动详情")
                .setIcon(iconResId)
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }
} 