package com.moses.miiread.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import androidx.annotation.Nullable;
import com.hwangjr.rxbus.RxBus;
import com.moses.miiread.core.AsyncTaskPlain;
import com.moses.miiread.help.ReadBookControl;
import com.moses.miiread.utils.TimeUtils;

public class ThemeCheckService extends Service {

    private ReadBookControl readBookControl;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readBookControl = ReadBookControl.getInstance();
        run();
        RxBus.get().register(this);
    }

    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public MBinder onBind(Intent intent) {
        return new MBinder();
    }

    public class MBinder extends Binder {

        MBinder() {

        }

        public ThemeCheckService getService() {
            return ThemeCheckService.this;
        }
    }

    @SuppressWarnings("RedundantSuppression")
    private void run() {
        new AsyncTaskPlain(() -> {
            // noinspection InfiniteLoopStatement
            for (; ; ) {
                try {
                    if (readBookControl.getCustomNightLightOn()) {
                        boolean inNightTime = TimeUtils.isInNightTime();
                        if (readBookControl.getIsNightTheme() && !inNightTime) {
                            sendBroadcast(new Intent(ThemeCheckReceiver.ACTION_THEME_CHANGE_LIGHT));
                        } else if (!readBookControl.getIsNightTheme() && inNightTime) {
                            sendBroadcast(new Intent(ThemeCheckReceiver.ACTION_THEME_CHANGE_NIGHT));
                        }
                    }
                    Thread.sleep(1500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }
}