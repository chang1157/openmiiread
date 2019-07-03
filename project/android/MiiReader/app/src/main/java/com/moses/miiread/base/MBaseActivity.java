//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.base;

import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.snackbar.Snackbar;
import com.hwangjr.rxbus.RxBus;
import com.kunfei.basemvplib.BaseActivity;
import com.kunfei.basemvplib.impl.IPresenter;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.service.ThemeCheckReceiver;
import com.moses.miiread.service.ThemeCheckService;
import com.moses.miiread.utils.ColorUtil;
import com.moses.miiread.utils.UmengStat;
import com.moses.miiread.utils.bar.ImmersionBar;
import com.moses.miiread.utils.theme.MaterialValueHelper;
import com.moses.miiread.utils.theme.ThemeStore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MBaseActivity<T extends IPresenter> extends BaseActivity<T> {
    private static final String TAG = MBaseActivity.class.getSimpleName();
    public final SharedPreferences preferences = MApplication.getConfigPreferences();
    protected ImmersionBar mImmersionBar;
    private Snackbar snackbar;

    private AtomicBoolean isDarkTheme = new AtomicBoolean(false);
    private static final boolean hideNavigationDivider = true;

    private ServiceConnection serviceConnection;
    protected ThemeCheckService themeCheckService;
    private ThemeCheckReceiver themeCheckReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        initTheme();
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        mImmersionBar = ImmersionBar.with(this);
        initImmersionBar();

        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ThemeCheckService.MBinder themeCheckBinder = (ThemeCheckService.MBinder) service;
                themeCheckService = themeCheckBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(new Intent(this, ThemeCheckService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        RxBus.get().register(this);
        themeCheckReceiver = new ThemeCheckReceiver(this::handleThemeChange);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ThemeCheckReceiver.ACTION_THEME_CHANGE_NIGHT);
        filter.addAction(ThemeCheckReceiver.ACTION_THEME_CHANGE_LIGHT);
        registerReceiver(themeCheckReceiver, filter);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 如果你的app可以横竖屏切换，并且适配4.4或者emui3手机请务必在onConfigurationChanged方法里添加这句话
        ImmersionBar.with(this).init();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        RxBus.get().unregister(this);
        unregisterReceiver(themeCheckReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MApplication.m_isUmengInit) {
            UmengStat.onPageStart(getClass().getSimpleName());
            UmengStat.onResume(this);
        }
        boolean isDark = preferences.getBoolean("nightTheme", isDarkTheme.get());
        if (isDarkTheme.get() != isDark) {
            isDarkTheme.set(isDark);
            initTheme();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MApplication.m_isUmengInit) {
            UmengStat.onPageEnd(getClass().getSimpleName());
            UmengStat.onPause(this);
        }
        boolean isDark = preferences.getBoolean("nightTheme", isDarkTheme.get());
        isDarkTheme.set(isDark);
    }

    @Override
    public void setSupportActionBar(@Nullable androidx.appcompat.widget.Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        }
        super.setSupportActionBar(toolbar);
    }

    /**
     * 设置MENU图标颜色
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.setColorFilter(MaterialValueHelper.getPrimaryTextColor(this, ColorUtil.isColorLight(ThemeStore.primaryColor(this))), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("PrivateApi")
    @SuppressWarnings("unchecked")
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            //展开菜单显示图标
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                    method = menu.getClass().getDeclaredMethod("getNonActionItems");
                    ArrayList<MenuItem> menuItems = (ArrayList<MenuItem>) method.invoke(menu);
                    if (!menuItems.isEmpty()) {
                        for (MenuItem menuItem : menuItems) {
                            Drawable drawable = menuItem.getIcon();
                            if (drawable != null) {
                                drawable.setColorFilter(getResources().getColor(R.color.tv_text_default), PorterDuff.Mode.SRC_ATOP);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 沉浸状态栏
     */
    protected void initImmersionBar() {
        if (hideNavigationDivider) {
            try {
                mImmersionBar.hideBarDivider();
            } catch (Exception ignore) {
            }
        }
        try {
            View actionBar = findViewById(R.id.action_bar);
            if (isImmersionBarEnabled()) {
                if (getSupportActionBar() != null && actionBar != null && actionBar.getVisibility() == View.VISIBLE) {
                    mImmersionBar.statusBarColorInt(ThemeStore.primaryColor(this));
                } else {
                    mImmersionBar.transparentStatusBar();
                }
            } else {
                if (getSupportActionBar() != null && actionBar != null && actionBar.getVisibility() == View.VISIBLE) {
                    mImmersionBar.statusBarColorInt(ThemeStore.statusBarColor(this));
                } else {
                    mImmersionBar.statusBarColor(R.color.status_bar_bag);
                }
            }
        } catch (Exception ignore) {

        }
        try {
            if (isImmersionBarEnabled() && ColorUtil.isColorLight(ThemeStore.primaryColor(this))) {
                mImmersionBar.statusBarDarkFont(true, 0.2f);
            } else if (ColorUtil.isColorLight(ThemeStore.primaryColorDark(this))) {
                mImmersionBar.statusBarDarkFont(true, 0.2f);
            } else {
                mImmersionBar.statusBarDarkFont(false);
            }
            if (!preferences.getBoolean("navigationBarColorChange", true)) {
                mImmersionBar.navigationBarColor(R.color.black);
                mImmersionBar.navigationBarDarkFont(false);
            } else if (ImmersionBar.canNavigationBarDarkFont()) {
                mImmersionBar.navigationBarColorInt(ThemeStore.backgroundColor(this));
                if (ColorUtil.isColorLight(ThemeStore.primaryColor(this))) {
                    mImmersionBar.navigationBarDarkFont(true);
                } else {
                    mImmersionBar.navigationBarDarkFont(false);
                }
            }
            mImmersionBar.init();
        } catch (Exception ignore) {

        }
    }

    /**
     * @return 是否沉浸
     */
    protected boolean isImmersionBarEnabled() {
        return preferences.getBoolean("immersionStatusBar", true);
    }

    /**
     * 设置屏幕方向
     */
    public void setOrientation(int screenDirection) {
        switch (screenDirection) {
            case 0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            case 1:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 2:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 3:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
        }
    }

    /**
     * @return 是否夜间模式
     */
    public boolean isNightTheme() {
        return preferences.getBoolean("nightTheme", false);
    }

    public void setNightTheme(boolean isNightTheme) {
        preferences.edit()
                .putBoolean("nightTheme", isNightTheme)
                .apply();
        MApplication.getInstance().upThemeStore();
        initTheme();
    }

    protected void initTheme() {
        if (ColorUtil.isColorLight(ThemeStore.primaryColor(this))) {
            setTheme(R.style.CAppTheme);
            isDarkTheme.set(false);
        } else {
            setTheme(R.style.CAppThemeBarDark);
            isDarkTheme.set(true);
        }
        if (isNightTheme()) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void showSnackBar(View view, String msg) {
        showSnackBar(view, msg, Snackbar.LENGTH_SHORT);
    }

    public void showSnackBar(View view, String msg, int length) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, msg, length);
        } else {
            snackbar.setText(msg);
            snackbar.setDuration(length);
        }
        snackbar.show();
    }

    public void hideSnackBar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public void handleThemeChange(boolean night) {
        if (isNightTheme() && night)
            return;
        if (!isNightTheme() && !night)
            return;
        setNightTheme(night);
    }
}