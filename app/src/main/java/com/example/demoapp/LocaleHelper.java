package com.example.demoapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

/**
 * 语言帮助类，用于切换应用内语言
 */
public class LocaleHelper {
    
    /**
     * 设置应用的语言
     * @param context 上下文
     * @param language 语言代码：如"zh"，"en"，或"system"表示跟随系统
     * @return 包含更新后区域设置的Context
     */
    public static Context setLocale(Context context, String language) {
        if (language.equals(TimerPreferences.LANGUAGE_SYSTEM)) {
            return resetToSystemLocale(context);
        }
        
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
    
    /**
     * 重置为系统默认语言设置
     * @param context 上下文
     * @return 包含系统区域设置的Context
     */
    private static Context resetToSystemLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(config);
        } else {
            config.locale = Locale.getDefault();
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
} 