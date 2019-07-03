//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDex;
import com.moses.miiread.constant.AppConstant;
import com.moses.miiread.help.AppFrontBackHelper;
import com.moses.miiread.help.CrashHandler;
import com.moses.miiread.help.FileHelp;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.model.UpLastChapterModel;
import com.moses.miiread.service.tts.TtsProviderFactory;
import com.moses.miiread.service.tts.TtsProviderImpl;
import com.moses.miiread.utils.UmengStat;
import com.moses.miiread.utils.theme.ThemeStore;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.umeng.analytics.MobclickAgent;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MApplication extends Application {
    public final static String channelIdDownload = "channel_download";
    public final static String channelIdReadAloud = "channel_read_aloud";
    public final static String[] PerList = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public final static int RESULT__PERMS = 263;
    public static String downloadPath;
    private static MApplication instance;
    private static String versionName;
    private static int versionCode;
    private SharedPreferences configPreferences;
    private boolean donateHb;
    public static boolean m_isUmengInit = false;
    public String accountToken = null;

    private TtsProviderFactory ttsProvider;

    public static MApplication getInstance() {
        return instance;
    }

    public TtsProviderFactory getTtsProvider() {
        if (ttsProvider.getTts() == null)
            initTts();
        return ttsProvider;
    }

    public static int getVersionCode() {
        return versionCode;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static Resources getAppResources() {
        return getInstance().getResources();
    }

    private String channel = "内测";

    @Override
    public void onCreate() {
        super.onCreate();

        // Base.
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (!BuildConfig.DEBUG && appInfo != null)
                channel = appInfo.metaData.getString("CHANNEL_NAME");
        } catch (PackageManager.NameNotFoundException e) {
        }

        // UM.
        String bugly = ConfigMng.APPID_BUGLY_DBG;
        if (!BuildConfig.DEBUG) {
            bugly = ConfigMng.APPID_BUGLY_RLS;
        }
        MobclickAgent.setCatchUncaughtExceptions(true);

        // Bugly.
        BuglyStrategy strategy = new BuglyStrategy();
        strategy.setAppChannel(channel);
        Bugly.init(this, bugly, BuildConfig.DEBUG, strategy);

        try {
            UmengStat.initUmeng(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //
        instance = this;
        CrashHandler.getInstance().init(this);
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = 0;
            versionName = "0.0.0";
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelIdDownload();
            createChannelIdReadAloud();
        }
        configPreferences = getSharedPreferences("CONFIG", 0);
        downloadPath = configPreferences.getString(getString(R.string.pk_download_path), "");
        if (TextUtils.isEmpty(downloadPath) | Objects.equals(downloadPath, FileHelp.getCachePath())) {
            setDownloadPath(null);
        }
        if (!ThemeStore.isConfigured(this, versionCode)) {
            upThemeStore();
        }
        AppFrontBackHelper.getInstance().register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                donateHb = System.currentTimeMillis() - configPreferences.getLong("DonateHb", 0) <= TimeUnit.DAYS.toMillis(3);
            }

            @Override
            public void onBack() {
                if (UpLastChapterModel.model != null) {
                    UpLastChapterModel.model.onDestroy();
                }
            }
        });
        RxJavaPlugins.setErrorHandler(e ->
                {
                    if (e instanceof UndeliverableException) {
                        //noinspection SingleStatementInBlock
                        Log.e("RxJavaPlugins:: err", e.getMessage());
                    } else {
                        // Forward all others to current thread's uncaught exception handler
                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    }
                }
        );

        //tts
        initTts();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        clearTts();
    }

    private void clearTts() {
        if (ttsProvider != null) {
            try {
                ttsProvider.getTts().stop();
                ttsProvider.getTts().shutdown();
                ttsProvider.setTts(null);
            } catch (Exception ignore) {

            }
        }
    }

    private void initTts() {
        ttsProvider = TtsProviderImpl.getInstance();
        if (ttsProvider != null) {
            ttsProvider.init(this);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 初始化主题
     */
    public void upThemeStore() {
        if (configPreferences.getBoolean("nightTheme", false)) {
            ThemeStore.editTheme(this)
                    .primaryColor(configPreferences.getInt("colorPrimaryNight", getResources().getColor(R.color.md_grey_800)))
                    .accentColor(configPreferences.getInt("colorAccentNight", getResources().getColor(R.color.md_pink_800)))
                    .backgroundColor(configPreferences.getInt("colorBackgroundNight", getResources().getColor(R.color.md_grey_800)))
                    .apply();
        } else {
            ThemeStore.editTheme(this)
                    .primaryColor(configPreferences.getInt("colorPrimary", getResources().getColor(R.color.md_grey_100)))
                    .accentColor(configPreferences.getInt("colorAccent", getResources().getColor(R.color.colorControlActivated)))
                    .backgroundColor(configPreferences.getInt("colorBackground", getResources().getColor(R.color.md_grey_100)))
                    .apply();
        }
    }

    /**
     * 设置下载地址
     */
    public void setDownloadPath(String path) {
        if (TextUtils.isEmpty(path)) {
            downloadPath = FileHelp.getFilesPath();
        } else {
            downloadPath = path;
        }
        AppConstant.BOOK_CACHE_PATH = downloadPath + File.separator + "book_cache" + File.separator;
        configPreferences.edit()
                .putString(getString(R.string.pk_download_path), path)
                .apply();
    }

    public static SharedPreferences getConfigPreferences() {
        return getInstance().configPreferences;
    }

    public boolean getDonateHb() {
        return donateHb || BuildConfig.DEBUG;
    }

    public void upDonateHb() {
        configPreferences.edit()
                .putLong("DonateHb", System.currentTimeMillis())
                .apply();
        donateHb = true;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannelIdDownload() {
        //用唯一的ID创建渠道对象
        NotificationChannel firstChannel = new NotificationChannel(channelIdDownload,
                getString(R.string.download_offline),
                NotificationManager.IMPORTANCE_LOW);
        //初始化channel
        firstChannel.enableLights(false);
        firstChannel.enableVibration(false);
        firstChannel.setSound(null, null);
        //向notification manager 提交channel
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(firstChannel);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannelIdReadAloud() {
        //用唯一的ID创建渠道对象
        NotificationChannel firstChannel = new NotificationChannel(channelIdReadAloud,
                getString(R.string.read_aloud),
                NotificationManager.IMPORTANCE_LOW);
        //初始化channel
        firstChannel.enableLights(false);
        firstChannel.enableVibration(false);
        firstChannel.setSound(null, null);
        //向notification manager 提交channel
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(firstChannel);
        }
    }
}