package com.moses.miiread.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;

import java.io.File;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.bean.UpdateInfoBean;
import com.moses.miiread.constant.RxBusTag;
import com.moses.miiread.help.UpdateManager;
import com.moses.miiread.utils.download.DownloadUtils;
import com.moses.miiread.utils.download.JsDownloadListener;
import com.moses.miiread.view.activity.UpdateActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class UpdateService extends Service {
    public static boolean isRunning = false;
    private static final String startDownload = "startDownload";
    private static final String stopDownload = "stopDownload";
    private UpdateInfoBean updateInfo;
    private Disposable disposableDown;

    public static void startThis(Context context, UpdateInfoBean updateInfoBean) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(startDownload);
        intent.putExtra("updateInfo", updateInfoBean);
        context.startService(intent);
    }

    public static void stopThis(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(stopDownload);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        //创建 Notification.Builder 对象
        updateNotification(0);
        RxBus.get().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (disposableDown != null) {
            disposableDown.dispose();
        }
        stopForeground(true);
        RxBus.get().post(RxBusTag.UPDATE_APK_STATE, -1);
        RxBus.get().unregister(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) {
                stopSelf();
            } else {
                switch (action) {
                    case startDownload:
                        updateInfo = intent.getParcelableExtra("updateInfo");
                        downloadApk(updateInfo.getUrl());
                        break;
                    case stopDownload:
                        stopDownload();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopDownload() {
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 更新通知
     */
    private void updateNotification(int state) {
        RxBus.get().post(RxBusTag.UPDATE_APK_STATE, state);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MApplication.channelIdReadAloud)
                .setSmallIcon(R.drawable.ic_download)
                .setOngoing(true)
                .setContentTitle(getString(R.string.download_update))
                .setContentText(String.format(getString(R.string.progress_show), state, 100))
                .setContentIntent(getActivityPendingIntent(""));
        builder.addAction(R.drawable.ic_stop_black_24dp, getString(R.string.cancel), getThisServicePendingIntent(stopDownload));
        builder.setProgress(100, state, false);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Notification notification = builder.build();
        int notificationId = 3425;
        startForeground(notificationId, notification);
    }

    private PendingIntent getActivityPendingIntent(String actionStr) {
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.setAction(actionStr);
        intent.putExtra("updateInfo", updateInfo);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getThisServicePendingIntent(String actionStr) {
        Intent intent = new Intent(this, this.getClass());
        intent.setAction(actionStr);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void downloadApk(String apkUrl) {
        if (disposableDown != null) {
            return;
        }
        String apkFilePath = UpdateManager.getSavePath(apkUrl.substring(apkUrl.lastIndexOf("/")));
        File apkFile = new File(apkFilePath);
        DownloadUtils downloadUtils = new DownloadUtils("https://github.com", new JsDownloadListener() {
            @Override
            public void onStartDownload(long length) {

            }

            @Override
            public void onProgress(int progress) {
                updateNotification(progress);
            }

            @Override
            public void onFail(String errorInfo) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), errorInfo, Toast.LENGTH_SHORT).show());
                UpdateService.this.stopSelf();
            }
        });
        downloadUtils.download(apkUrl, apkFile, new Observer<InputStream>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposableDown = d;
            }

            @Override
            public void onNext(InputStream inputStream) {
            }

            @Override
            public void onError(Throwable e) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "下载更新出错\n" + e.getMessage(), Toast.LENGTH_SHORT).show());
                UpdateService.this.stopSelf();
            }

            @Override
            public void onComplete() {
                UpdateService.this.stopSelf();
                //check
                if(!UpdateManager.checkMd5(updateInfo))
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "APK文件MD5校验失败！请使用书迷正版软件", Toast.LENGTH_SHORT).show());
            }
        });

    }

}
