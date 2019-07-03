package com.moses.miiread.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ThemeCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;
        if (intent.getAction().compareTo(ACTION_THEME_CHANGE_NIGHT) == 0 && callback != null)
            callback.onThemeChange(true);
        else if (intent.getAction().compareTo(ACTION_THEME_CHANGE_LIGHT) == 0 && callback != null)
            callback.onThemeChange(false);
    }

    public ThemeCheckReceiver(ThemeCheckCallback callback) {
        this.callback = callback;
    }

    ThemeCheckCallback callback;

    public interface ThemeCheckCallback {
        void onThemeChange(boolean night);
    }

    public static final String ACTION_THEME_CHANGE_NIGHT = "ACTION_THEME_CHANGE_NIGHT";
    public static final String ACTION_THEME_CHANGE_LIGHT = "ACTION_THEME_CHANGE_LIGHT";
}