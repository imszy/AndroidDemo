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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    // UI elements
    private TextView timerTextView;
    private TextView pomodoroModeButton;
    private TextView shortBreakModeButton;
    private TextView longBreakModeButton;
    private Button startButton;
    private Button resetButton;
    private Button settingsButton;
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
    
    // Current timer mode
    private String currentMode = "pomodoro";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize helper classes
        timerPreferences = new TimerPreferences(this);
        
        // Initialize UI elements
        timerTextView = findViewById(R.id.timerTextView);
        pomodoroModeButton = findViewById(R.id.pomodoroModeButton);
        shortBreakModeButton = findViewById(R.id.shortBreakModeButton);
        longBreakModeButton = findViewById(R.id.longBreakModeButton);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);
        settingsButton = findViewById(R.id.settingsButton);
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
        
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_privacy_policy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
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
            
            // Auto switch to break
            if (sessions % 4 == 0) {
                // After 4 pomodoros, take a long break
                setTimerModeFromPreferences("longBreak");
            } else {
                setTimerModeFromPreferences("shortBreak");
            }
        } else {
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
        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
    }
    
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(5000);
        }
    }
    
    /**
     * Shows the settings dialog
     */
    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        
        EditText pomodoroTimeEditText = dialogView.findViewById(R.id.pomodoroTimeEditText);
        EditText shortBreakTimeEditText = dialogView.findViewById(R.id.shortBreakTimeEditText);
        EditText longBreakTimeEditText = dialogView.findViewById(R.id.longBreakTimeEditText);
        
        // Set current values
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
                
                // Validate values
                if (pomodoroTime < 1 || shortBreakTime < 1 || longBreakTime < 1) {
                    Toast.makeText(MainActivity.this, R.string.invalid_duration, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Save settings
                timerPreferences.saveTimerSettings(pomodoroTime, shortBreakTime, longBreakTime);
                
                // Update current timer if it's not running
                if (!isRunning) {
                    setTimerModeFromPreferences(currentMode);
                }
                
                Toast.makeText(MainActivity.this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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