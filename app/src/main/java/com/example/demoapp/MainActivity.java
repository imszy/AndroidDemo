package com.example.demoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.ToneGenerator;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends BaseActivity {
    
    // UI elements
    private TextView timerTextView;
    private TextView pomodoroModeButton;
    private TextView shortBreakModeButton;
    private TextView longBreakModeButton;
    private Button startButton;
    private Button resetButton;
    private ProgressBar timerProgressBar;
    private TextView sessionCountTextView;
    
    // Timer variables
    private CountDownTimer timer;
    private boolean isRunning = false;
    private long timeLeftInMillis;
    private long totalTimeInMillis;
    private int sessions = 0;
    
    // Helper classes
    private TimerPreferences timerPreferences;
    private ActivityRecordManager recordManager;
    
    // Current timer mode
    private String currentMode = "pomodoro";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化首选项并应用暗色模式设置
        timerPreferences = new TimerPreferences(this);
        recordManager = new ActivityRecordManager(this);
        applyDarkMode(timerPreferences.getDarkMode());
        
        setContentView(R.layout.activity_main);
        
        // Initialize UI elements
        timerTextView = findViewById(R.id.timerTextView);
        pomodoroModeButton = findViewById(R.id.pomodoroModeButton);
        shortBreakModeButton = findViewById(R.id.shortBreakModeButton);
        longBreakModeButton = findViewById(R.id.longBreakModeButton);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);
        timerProgressBar = findViewById(R.id.timerProgressBar);
        sessionCountTextView = findViewById(R.id.sessionCountTextView);
        
        // Set content descriptions for accessibility
        timerTextView.setContentDescription(getString(R.string.timer_content_description, "25", "00"));
        
        // Set initial mode
        setTimerModeFromPreferences("pomodoro");
        
        // Set click listeners
        pomodoroModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    setTimerModeFromPreferences("pomodoro");
                }
            }
        });
        
        shortBreakModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    setTimerModeFromPreferences("shortBreak");
                }
            }
        });
        
        longBreakModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    setTimerModeFromPreferences("longBreak");
                }
            }
        });
        
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTimer();
            }
        });
        
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }
    
    /**
     * 应用选定的深色模式设置
     * @param darkMode 深色模式设置选项
     */
    private void applyDarkMode(int darkMode) {
        switch (darkMode) {
            case TimerPreferences.DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case TimerPreferences.DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case TimerPreferences.DARK_MODE_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        } else if (id == R.id.action_privacy_policy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (id == R.id.action_history) {
            startActivity(new Intent(this, HeatmapActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Show information about the app
     */
    private void showAboutDialog() {
        String versionName = "1.0";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.about_app) + "\n\n" + 
                            getString(R.string.app_version, versionName))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(R.string.privacy_policy, (dialog, which) -> {
                    startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                })
                .show();
    }
    
    /**
     * Set the timer mode using values from preferences
     * @param mode The timer mode ("pomodoro", "shortBreak", or "longBreak")
     */
    private void setTimerModeFromPreferences(String mode) {
        currentMode = mode;
        
        // Get duration from preferences based on mode
        int durationMinutes;
        if (mode.equals("pomodoro")) {
            durationMinutes = timerPreferences.getPomodoroTime();
        } else if (mode.equals("shortBreak")) {
            durationMinutes = timerPreferences.getShortBreakTime();
        } else {
            durationMinutes = timerPreferences.getLongBreakTime();
        }
        
        timeLeftInMillis = durationMinutes * 60 * 1000;
        totalTimeInMillis = timeLeftInMillis;
        updateTimerDisplay();
        updateModeSelection();
        updateProgressBar();
    }
    
    private void toggleTimer() {
        if (isRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }
    
    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
                updateProgressBar();
            }
            
            @Override
            public void onFinish() {
                timerComplete();
            }
        }.start();
        
        isRunning = true;
        startButton.setText(R.string.pause);
    }
    
    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        startButton.setText(R.string.resume);
    }
    
    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        
        setTimerModeFromPreferences(currentMode);
        isRunning = false;
        startButton.setText(R.string.start);
    }
    
    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
        
        // Update content description for accessibility
        timerTextView.setContentDescription(
            getString(R.string.timer_content_description, 
                    String.valueOf(minutes), 
                    String.valueOf(seconds)));
    }
    
    private void updateProgressBar() {
        int progress = (int) (100 - (timeLeftInMillis * 100 / totalTimeInMillis));
        timerProgressBar.setProgress(progress);
    }
    
    private void updateModeSelection() {
        pomodoroModeButton.setSelected(currentMode.equals("pomodoro"));
        shortBreakModeButton.setSelected(currentMode.equals("shortBreak"));
        longBreakModeButton.setSelected(currentMode.equals("longBreak"));
        
        if (pomodoroModeButton.isSelected()) {
            pomodoroModeButton.setTextColor(getResources().getColor(R.color.colorWhite));
            pomodoroModeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            pomodoroModeButton.setTextColor(getResources().getColor(R.color.colorText));
            pomodoroModeButton.setBackgroundColor(getResources().getColor(R.color.colorGrayBackground));
        }
        
        if (shortBreakModeButton.isSelected()) {
            shortBreakModeButton.setTextColor(getResources().getColor(R.color.colorWhite));
            shortBreakModeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            shortBreakModeButton.setTextColor(getResources().getColor(R.color.colorText));
            shortBreakModeButton.setBackgroundColor(getResources().getColor(R.color.colorGrayBackground));
        }
        
        if (longBreakModeButton.isSelected()) {
            longBreakModeButton.setTextColor(getResources().getColor(R.color.colorWhite));
            longBreakModeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            longBreakModeButton.setTextColor(getResources().getColor(R.color.colorText));
            longBreakModeButton.setBackgroundColor(getResources().getColor(R.color.colorGrayBackground));
        }
    }
    
    private void timerComplete() {
        if (currentMode.equals("pomodoro")) {
            sessions++;
            sessionCountTextView.setText(sessions + " " + getString(R.string.sessions_completed));
            
            // 记录番茄钟完成
            recordManager.addRecord(ActivityRecord.TYPE_POMODORO);
            
            // Auto switch to break
            if (sessions % 4 == 0) {
                // After 4 pomodoros, take a long break
                setTimerModeFromPreferences("longBreak");
            } else {
                setTimerModeFromPreferences("shortBreak");
            }
        } else if (currentMode.equals("shortBreak")) {
            // 记录短休息完成
            recordManager.addRecord(ActivityRecord.TYPE_SHORT_BREAK);
            
            // After break, go back to pomodoro
            setTimerModeFromPreferences("pomodoro");
        } else if (currentMode.equals("longBreak")) {
            // 记录长休息完成
            recordManager.addRecord(ActivityRecord.TYPE_LONG_BREAK);
            
            // After break, go back to pomodoro
            setTimerModeFromPreferences("pomodoro");
        }
        
        // Play notification sound and vibrate
        playNotificationSound();
        vibrate();
        
        isRunning = false;
        startButton.setText(R.string.start);
    }
    
    private void switchMode(String mode) {
        currentMode = mode;
        setTimerModeFromPreferences(mode);
    }
    
    private void playNotificationSound() {
        // Play sound 5 times with delay between each
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 5; i++) {
                        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
                        Thread.sleep(500); // Wait between sounds
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            // Create a pattern for 5 vibrations with pauses between
            // {delay before start, vibrate duration, pause duration, vibrate duration, ...}
            long[] pattern = {0, 500, 500, 500, 500, 500, 500, 500, 500, 500};
            vibrator.vibrate(pattern, -1); // -1 means don't repeat the pattern
        }
    }
    
    /**
     * Show settings dialog with timer durations and dark mode options
     */
    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        
        EditText pomodoroTimeEditText = dialogView.findViewById(R.id.pomodoroTimeEditText);
        EditText shortBreakTimeEditText = dialogView.findViewById(R.id.shortBreakTimeEditText);
        EditText longBreakTimeEditText = dialogView.findViewById(R.id.longBreakTimeEditText);
        Spinner darkModeSpinner = dialogView.findViewById(R.id.darkModeSpinner);
        Spinner languageSpinner = dialogView.findViewById(R.id.languageSpinner);
        
        // 设置深色模式下拉列表适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dark_mode_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        darkModeSpinner.setAdapter(adapter);
        
        // 设置当前选中的深色模式
        int currentDarkMode = timerPreferences.getDarkMode();
        darkModeSpinner.setSelection(currentDarkMode);
        
        // 设置语言下拉列表
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        
        // 设置当前语言选择
        String currentLanguage = timerPreferences.getLanguage();
        if (currentLanguage.equals(TimerPreferences.LANGUAGE_ENGLISH)) {
            languageSpinner.setSelection(1); // English
        } else if (currentLanguage.equals(TimerPreferences.LANGUAGE_CHINESE)) {
            languageSpinner.setSelection(2); // Chinese
        } else {
            languageSpinner.setSelection(0); // System
        }
        
        // Set current timer values
        pomodoroTimeEditText.setText(String.valueOf(timerPreferences.getPomodoroTime()));
        shortBreakTimeEditText.setText(String.valueOf(timerPreferences.getShortBreakTime()));
        longBreakTimeEditText.setText(String.valueOf(timerPreferences.getLongBreakTime()));
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from inputs
                int pomodoroTime = Integer.parseInt(pomodoroTimeEditText.getText().toString());
                int shortBreakTime = Integer.parseInt(shortBreakTimeEditText.getText().toString());
                int longBreakTime = Integer.parseInt(longBreakTimeEditText.getText().toString());
                int selectedDarkMode = darkModeSpinner.getSelectedItemPosition();
                
                // 获取选择的语言
                int selectedLanguagePosition = languageSpinner.getSelectedItemPosition();
                String selectedLanguage;
                switch (selectedLanguagePosition) {
                    case 1:
                        selectedLanguage = TimerPreferences.LANGUAGE_ENGLISH;
                        break;
                    case 2:
                        selectedLanguage = TimerPreferences.LANGUAGE_CHINESE;
                        break;
                    default:
                        selectedLanguage = TimerPreferences.LANGUAGE_SYSTEM;
                        break;
                }
                
                // 检查语言是否已更改
                boolean languageChanged = !selectedLanguage.equals(timerPreferences.getLanguage());
                
                // Validate values
                if (pomodoroTime < 1 || shortBreakTime < 1 || longBreakTime < 1) {
                    Toast.makeText(MainActivity.this, R.string.invalid_duration, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 保存设置
                timerPreferences.saveTimerSettings(pomodoroTime, shortBreakTime, longBreakTime);
                timerPreferences.saveDarkMode(selectedDarkMode);
                timerPreferences.saveLanguage(selectedLanguage);
                
                // 立即应用深色模式
                applyDarkMode(selectedDarkMode);
                
                // Update current timer if it's not running
                if (!isRunning) {
                    setTimerModeFromPreferences(currentMode);
                }
                
                Toast.makeText(MainActivity.this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                
                // 如果语言已更改，重启Activity以应用新语言
                if (languageChanged) {
                    Toast.makeText(MainActivity.this, R.string.restart_to_apply, Toast.LENGTH_SHORT).show();
                    
                    // 延迟重启活动
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    }, 1000);
                }
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }
} 