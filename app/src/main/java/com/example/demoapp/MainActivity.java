package com.example.demoapp;

import android.media.ToneGenerator;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
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
    
    // Timer settings (in milliseconds)
    private static final long POMODORO_TIME = 25 * 60 * 1000;
    private static final long SHORT_BREAK_TIME = 5 * 60 * 1000;
    private static final long LONG_BREAK_TIME = 15 * 60 * 1000;
    
    // Current timer mode
    private String currentMode = "pomodoro";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        
        // Set initial mode
        timeLeftInMillis = POMODORO_TIME;
        totalTimeInMillis = POMODORO_TIME;
        updateTimerDisplay();
        updateModeSelection();
        
        // Set click listeners
        pomodoroModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    currentMode = "pomodoro";
                    timeLeftInMillis = POMODORO_TIME;
                    totalTimeInMillis = POMODORO_TIME;
                    updateTimerDisplay();
                    updateModeSelection();
                    updateProgressBar();
                }
            }
        });
        
        shortBreakModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    currentMode = "shortBreak";
                    timeLeftInMillis = SHORT_BREAK_TIME;
                    totalTimeInMillis = SHORT_BREAK_TIME;
                    updateTimerDisplay();
                    updateModeSelection();
                    updateProgressBar();
                }
            }
        });
        
        longBreakModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    currentMode = "longBreak";
                    timeLeftInMillis = LONG_BREAK_TIME;
                    totalTimeInMillis = LONG_BREAK_TIME;
                    updateTimerDisplay();
                    updateModeSelection();
                    updateProgressBar();
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
        
        if (currentMode.equals("pomodoro")) {
            timeLeftInMillis = POMODORO_TIME;
            totalTimeInMillis = POMODORO_TIME;
        } else if (currentMode.equals("shortBreak")) {
            timeLeftInMillis = SHORT_BREAK_TIME;
            totalTimeInMillis = SHORT_BREAK_TIME;
        } else {
            timeLeftInMillis = LONG_BREAK_TIME;
            totalTimeInMillis = LONG_BREAK_TIME;
        }
        
        updateTimerDisplay();
        updateProgressBar();
        isRunning = false;
        startButton.setText(R.string.start);
    }
    
    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
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
            
            // Auto switch to break
            if (sessions % 4 == 0) {
                // After 4 pomodoros, take a long break
                switchMode("longBreak");
            } else {
                switchMode("shortBreak");
            }
        } else {
            // After break, go back to pomodoro
            switchMode("pomodoro");
        }
        
        // Play notification sound
        playNotificationSound();
        
        isRunning = false;
        startButton.setText(R.string.start);
    }
    
    private void switchMode(String mode) {
        currentMode = mode;
        
        if (mode.equals("pomodoro")) {
            timeLeftInMillis = POMODORO_TIME;
            totalTimeInMillis = POMODORO_TIME;
        } else if (mode.equals("shortBreak")) {
            timeLeftInMillis = SHORT_BREAK_TIME;
            totalTimeInMillis = SHORT_BREAK_TIME;
        } else {
            timeLeftInMillis = LONG_BREAK_TIME;
            totalTimeInMillis = LONG_BREAK_TIME;
        }
        
        updateTimerDisplay();
        updateModeSelection();
        updateProgressBar();
    }
    
    private void playNotificationSound() {
        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }
} 