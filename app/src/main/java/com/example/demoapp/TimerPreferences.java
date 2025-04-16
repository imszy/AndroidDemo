package com.example.demoapp;

import android.content.Context;
import android.content.SharedPreferences;

public class TimerPreferences {
    private static final String PREF_NAME = "timer_prefs";
    private static final String POMODORO_TIME_KEY = "pomodoro_time";
    private static final String SHORT_BREAK_TIME_KEY = "short_break_time";
    private static final String LONG_BREAK_TIME_KEY = "long_break_time";

    // Default durations in minutes
    private static final int DEFAULT_POMODORO_TIME = 25;
    private static final int DEFAULT_SHORT_BREAK_TIME = 5;
    private static final int DEFAULT_LONG_BREAK_TIME = 15;

    private final SharedPreferences preferences;

    public TimerPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get the pomodoro duration in minutes
     * @return Duration in minutes
     */
    public int getPomodoroTime() {
        return preferences.getInt(POMODORO_TIME_KEY, DEFAULT_POMODORO_TIME);
    }

    /**
     * Get the short break duration in minutes
     * @return Duration in minutes
     */
    public int getShortBreakTime() {
        return preferences.getInt(SHORT_BREAK_TIME_KEY, DEFAULT_SHORT_BREAK_TIME);
    }

    /**
     * Get the long break duration in minutes
     * @return Duration in minutes
     */
    public int getLongBreakTime() {
        return preferences.getInt(LONG_BREAK_TIME_KEY, DEFAULT_LONG_BREAK_TIME);
    }

    /**
     * Save all timer settings
     * @param pomodoroTime Pomodoro duration in minutes
     * @param shortBreakTime Short break duration in minutes
     * @param longBreakTime Long break duration in minutes
     */
    public void saveTimerSettings(int pomodoroTime, int shortBreakTime, int longBreakTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(POMODORO_TIME_KEY, pomodoroTime);
        editor.putInt(SHORT_BREAK_TIME_KEY, shortBreakTime);
        editor.putInt(LONG_BREAK_TIME_KEY, longBreakTime);
        editor.apply();
    }
} 