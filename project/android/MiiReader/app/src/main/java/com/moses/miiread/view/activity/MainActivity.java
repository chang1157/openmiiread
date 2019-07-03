//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.hwangjr.rxbus.RxBus;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.base.observer.SimpleObserver;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.constant.RxBusTag;
import com.moses.miiread.core.AsyncTaskPlain;
import com.moses.miiread.help.*;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.model.BookSourceManager;
import com.moses.miiread.model.UpLastChapterModel;
import com.moses.miiread.presenter.MainPresenter;
import com.moses.miiread.presenter.contract.MainContract;
import com.moses.miiread.utils.*;
import com.moses.miiread.utils.theme.NavigationViewUtil;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.fragment.BookHisFragment;
import com.moses.miiread.view.fragment.BookListFragment;
import com.moses.miiread.view.fragment.FindBookFragment;
import com.moses.miiread.view.fragment.FindBookFragmentMoses;
import com.moses.miiread.widget.modialog.MoDialogHUD;
import io.reactivex.Observable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends MBaseActivity<MainContract.Presenter> implements MainContract.View, BookListFragment.CallBackValue {
    private static final int FILE_SELECT_RESULT = 13;

    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.card_search)
    CardView cardSearch;

    @BindView(R.id.menu_book_source_manage)
    View sourceManage;
    @BindView(R.id.menu_drawer_download)
    View drawerMenuDownload;
    @BindView(R.id.menu_drawer_setting)
    View drawerMenuSetting;
    @BindView(R.id.menu_drawer_new)
    View drawerMenuNew;
    @BindView(R.id.menu_drawer_subscription)
    View drawerMenuSubscription;
    @BindView(R.id.menu_drawer_backup_shelf)
    View drawerShelpBackup;
    @BindView(R.id.menu_drawer_restore_shelf)
    View drawerShelfRestore;
    @BindView(R.id.action_about)
    View actionAbout;
    @BindView(R.id.action_booklist)
    View actionBookList;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.content_ll)
    ViewGroup content_ll;
    @BindView(R.id.rl_bottom)
    View drawBottomV;
    private AppCompatImageButton vwNightTheme;
    private int group;
    private boolean viewIsList;
    private ActionBarDrawerToggle mDrawerToggle;
    private MoDialogHUD moDialogHUD;
    private long exitTime = 0;
    private boolean resumed = false;
    private Handler handler = new Handler();

    private BookListFragment bookListFrg;
    private BookHisFragment bookHisFrg;
    private FindBookFragmentMoses findBookFrgMoses;
    private FindBookFragment findBookFrg;

    private AtomicInteger curBottomTab = new AtomicInteger(-1);

    @Override
    protected MainContract.Presenter initInjector() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        group = preferences.getInt("bookshelfGroup", 0);
        super.onCreate(savedInstanceState);

        if (ConfigMng.UPDATE_ENABLE)
            UpdateManager.getInstance(this).checkUpdate(false);
        MarketSourceManager.getInstance(this).initMarketSources();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("resumed", resumed);
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        String shared_url = preferences.getString("shared_url", "");
        assert shared_url != null;
        if (shared_url.length() > 1) {
            moDialogHUD.showInputBox(getString(R.string.add_book_url),
                    shared_url,
                    null,
                    inputText -> {
                        inputText = StringUtils.trim(inputText);
                        mPresenter.addBookUrl(inputText);
                    });

            preferences.edit()
                    .putString("shared_url", "")
                    .apply();
        }

        boolean editSourceEnable = PrefUtil.getInstance().getBoolean("isEditSourceEnable", false);
        boolean fixBottomByUIChange = false;
        if (editSourceEnable && sourceManage.getVisibility() == View.GONE) {
            sourceManage.setVisibility(View.VISIBLE);
            fixBottomByUIChange = true;
            ((TextView) drawerShelpBackup.findViewById(R.id.menu_hint_5)).setHint(R.string.bookshelf_source);
            ((TextView) drawerShelfRestore.findViewById(R.id.menu_hint_6)).setHint(R.string.bookshelf_source);
        } else if (!editSourceEnable && sourceManage.getVisibility() == View.VISIBLE) {
            sourceManage.setVisibility(View.GONE);
            fixBottomByUIChange = true;
            ((TextView) drawerShelpBackup.findViewById(R.id.menu_hint_5)).setHint(R.string.bookshelf);
            ((TextView) drawerShelfRestore.findViewById(R.id.menu_hint_6)).setHint(R.string.bookshelf);
        }
        if (fixBottomByUIChange) {
            ViewTreeObserver vto = sourceManage.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    sourceManage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    fixDrawerBottom();
                }
            });
            sourceManage.requestLayout();
        } else
            fixDrawerBottom();
    }

    @Override
    public void fixDrawerBottom() {
        ViewTreeObserver vto = drawBottomV.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                drawBottomV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int drawerH = drawer.getMeasuredHeight();
                float top = drawBottomV.getTop();
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) drawBottomV.getLayoutParams();
                if (top + drawBottomV.getMeasuredHeight() < drawer.getTop() + drawerH) {
                    lp.height = (int) (drawer.getTop() + drawerH - top - ((ConstraintLayout.LayoutParams) findViewById(R.id.drawer_tab_bottom).getLayoutParams()).bottomMargin);
                } else {
                    lp.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                }
                drawBottomV.setLayoutParams(lp);
            }
        });
        drawBottomV.requestLayout();
    }

    /**
     * 沉浸状态栏
     */
    @Override
    public void initImmersionBar() {
        super.initImmersionBar();
    }

    @Override
    protected void initData() {
        viewIsList = preferences.getBoolean("bookshelfIsList", true);
        bookListFrg = new BookListFragment();
        bookHisFrg = new BookHisFragment();
        findBookFrgMoses = new FindBookFragmentMoses();
        findBookFrg = new FindBookFragment();
    }

    @Override
    public boolean isRecreate() {
        return isRecreate;
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void bindView() {
        super.bindView();
        setSupportActionBar(toolbar);
        setupActionBar();
        initDrawer();
        initBottomNavigationView();
        upGroup(group);
        moDialogHUD = new MoDialogHUD(this);
        //禁止toolbar 滚动
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        //点击跳转搜索页
        cardSearch.setOnClickListener(view -> {
            ActivityOptionsCompat compat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            view, "sharedView");
            ActivityCompat.startActivity(this,
                    new Intent(this, SearchBookActivity.class), compat.toBundle());
        });
//        cardSearch.setOnClickListener(view -> startActivityByAnim(new Intent(MainActivity.this, SearchBookActivity.class),
//                toolbar, "sharedView", android.R.anim.fade_in, android.R.anim.fade_out));

        //禁止编辑书源时，每次启动自动更新书源
        boolean editSourceEnable = PrefUtil.getInstance().getBoolean("isEditSourceEnable", false);
        if (!editSourceEnable) {
            long curTime = System.currentTimeMillis();
            long lastUpdateBookSourceTime = PrefUtil.getInstance().getLong("lastUpdateBookSourceTime", -1);
            if (curTime - lastUpdateBookSourceTime > 12 * 3600 * 1000) {//时间间隔超过12个小时时，自动更新书源
                handler.postDelayed(() -> mPresenter.importBookSource(ConfigMng.DEFAULT_SOURCE_URL, false), 400);
                PrefUtil.getInstance().setLong("lastUpdateBookSourceTime", curTime);
            }
            ((TextView) drawerShelpBackup.findViewById(R.id.menu_hint_5)).setHint(R.string.bookshelf);
            ((TextView) drawerShelfRestore.findViewById(R.id.menu_hint_6)).setHint(R.string.bookshelf);
        } else {

            ((TextView) drawerShelpBackup.findViewById(R.id.menu_hint_5)).setHint(R.string.bookshelf_source);
            ((TextView) drawerShelfRestore.findViewById(R.id.menu_hint_6)).setHint(R.string.bookshelf_source);
        }
        updateNaviViewWidth(getResources().getConfiguration().orientation);
    }

    private void initBottomNavigationView() {
        //禁止bottomNavigationView动画
        BottomNavigationViewHelper.disableShiftingMode(bottomNavigationView);
        BottomNavigationViewHelper.disableItemScale(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fm = getSupportFragmentManager();
            switch (item.getItemId()) {
                case R.id.navi_shelf: {
                    if (curBottomTab.get() == 0)
                        break;
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.content_ll, bookListFrg);
                    ft.commitAllowingStateLoss();
                    curBottomTab.set(0);
                    break;
                }
                case R.id.navi_his: {
                    if (curBottomTab.get() == 2)
                        break;
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.content_ll, bookHisFrg);
                    ft.commitAllowingStateLoss();
                    curBottomTab.set(2);
                    break;
                }
                case R.id.navi_find: {
                    if (curBottomTab.get() == 1)
                        break;
                    FragmentTransaction ft = fm.beginTransaction();
                    if (ConfigMng.USE_MOSES_FIND_FRG)
                        ft.replace(R.id.content_ll, findBookFrgMoses);
                    else ft.replace(R.id.content_ll, findBookFrg);
                    ft.commitAllowingStateLoss();
                    curBottomTab.set(1);
                    break;
                }
                default:
                    break;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.navi_shelf);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //开启的话则使用三个横条作为NavigationIcon
        //mDrawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_add_local).setVisible(ReadBookControl.getInstance().getLocalPdfTxtEnable());
        MenuItem pauseMenu = menu.findItem(R.id.action_list_grid);
        if (curBottomTab.get() == 0) {
            if (viewIsList) {
                pauseMenu.setTitle(R.string.action_grid);
                pauseMenu.setIcon(R.drawable.ic_view_mode_grid);
            } else {
                pauseMenu.setIcon(R.drawable.ic_view_mode_list);
                pauseMenu.setTitle(R.string.action_list);
            }
            pauseMenu.setVisible(true);
        } else
            pauseMenu.setVisible(false);

        //book source manage
        boolean editSourceEnable = PrefUtil.getInstance().getBoolean("isEditSourceEnable", false);
        menu.findItem(R.id.action_restore_source).setVisible(!editSourceEnable);
        return super.onPrepareOptionsMenu(menu);
    }

    // 添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = preferences.edit();
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_local:
                PermissionUtils.checkMorePermissions(this, MApplication.PerList, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        startActivity(new Intent(MainActivity.this, ImportBookActivity.class));
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        MainActivity.this.toast(R.string.import_per);
                    }

                    @Override
                    public void onAlreadyTurnedDownAndNoAsk(String... permission) {
                        PermissionUtils.requestMorePermissions(MainActivity.this, permission, FILE_SELECT_RESULT);
                    }
                });
                break;
            case R.id.action_list_grid:
                viewIsList = !viewIsList;
                editor.putBoolean("bookshelfIsList", viewIsList);
                editor.apply();
                bookListFrg.toggleAdapterMode(viewIsList);
                break;
            case R.id.action_restore_source: {
                mPresenter.importBookSource(ConfigMng.DEFAULT_SOURCE_URL, true);
                break;
            }
            case R.id.action_clear_cache: {
                Dialog[] dialog = new Dialog[1];
                View pop = LayoutInflater.from(this).inflate(R.layout.dialog_normal_confirm, null, false);
                TextView title = pop.findViewById(R.id.title);
                TextView msg = pop.findViewById(R.id.msg_tv);
                TextView cancel = pop.findViewById(R.id.cancel);
                TextView confirm = pop.findViewById(R.id.confirm);
                title.setText(R.string.clear_content);
                msg.setText(R.string.sure_del_download_book);
                cancel.setText(R.string.no);
                confirm.setText(R.string.yes);

                cancel.setOnClickListener(v -> {
                    BookshelfHelp.clearCaches(false);
                    dialog[0].dismiss();
                });
                confirm.setOnClickListener(v -> {
                    BookshelfHelp.clearCaches(true);
                    dialog[0].dismiss();
                });
                dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                        .setView(pop)
                        .show();
                break;
            }
            case R.id.action_clearBookshelf: {
                Dialog[] dialog = new Dialog[1];
                View pop = LayoutInflater.from(this).inflate(R.layout.dialog_normal_confirm, null, false);
                TextView title = pop.findViewById(R.id.title);
                TextView msg = pop.findViewById(R.id.msg_tv);
                TextView cancel = pop.findViewById(R.id.cancel);
                TextView confirm = pop.findViewById(R.id.confirm);
                title.setText(R.string.clear_bookshelf);
                msg.setText(R.string.clear_bookshelf_s);
                cancel.setText(R.string.cancel);
                confirm.setText(R.string.ok);

                cancel.setOnClickListener(v -> dialog[0].dismiss());
                confirm.setOnClickListener(v -> {
                    mPresenter.clearBookshelf();
                    dialog[0].dismiss();
                });
                dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                        .setView(pop)
                        .show();
                break;
            }
            case android.R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.START)
                ) {
                    drawer.closeDrawers();
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化侧边栏
    private void initDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        setUpNavigationView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        fixDrawerBottom();
        updateNaviViewWidth(newConfig.orientation);
    }

    private void upGroup(int group) {
        if (this.group != group) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("bookshelfGroup", group);
            editor.apply();
        }
        this.group = group;
        RxBus.get().post(RxBusTag.UPDATE_GROUP, group);
        RxBus.get().post(RxBusTag.REFRESH_BOOK_LIST, false);
    }

    /**
     * 侧边栏按钮
     */
    private void setUpNavigationView() {
        NavigationViewUtil.setItemTextColors(navigationView, getResources().getColor(R.color.tv_text_default), ThemeStore.accentColor(this));
        NavigationViewUtil.setItemIconColors(navigationView, getResources().getColor(R.color.tv_text_default), ThemeStore.accentColor(this));
        NavigationViewUtil.disableScrollbar(navigationView);
        vwNightTheme = navigationView.findViewById(R.id.iv_theme_day_night);
        upThemeVw();
    }

    @Override
    protected void bindEvent() {
        super.bindEvent();
        vwNightTheme.setOnClickListener(view -> {
            bottomNavigationView.setSelectedItemId(R.id.navi_shelf);
            ReadBookControl.getInstance().setCustomNightLightOn(false);
            setNightTheme(!isNightTheme());
        });
        drawerMenuSetting.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            handler.postDelayed(() -> SettingActivity.startThis(this), 200);
        });
        actionAbout.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            handler.postDelayed(() -> AboutActivity.startThis(this), 200);
        });
        sourceManage.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            handler.postDelayed(() -> BookSourceActivity.startThis(this), 200);
        });
        drawerMenuDownload.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            handler.postDelayed(() -> DownloadActivity.startThis(this), 200);
        });
        drawerMenuSubscription.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            handler.postDelayed(() -> SubscriptionActivity.startThis(this), 200);
        });
        drawerShelpBackup.setOnClickListener(view -> {
            Dialog[] dialog = new Dialog[1];
            View pop = LayoutInflater.from(this).inflate(R.layout.dialog_normal_confirm, null, false);
            TextView title = pop.findViewById(R.id.title);
            TextView msg = pop.findViewById(R.id.msg_tv);
            TextView cancel = pop.findViewById(R.id.cancel);
            TextView confirm = pop.findViewById(R.id.confirm);
            title.setText(R.string.backup_confirmation);
            msg.setText(R.string.backup_message);
            cancel.setText(R.string.cancel);
            confirm.setText(R.string.backup);

            cancel.setOnClickListener(v -> dialog[0].dismiss());
            confirm.setOnClickListener(v -> {
                mPresenter.backupData();
                dialog[0].dismiss();
            });
            dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                    .setView(pop)
                    .show();
        });
        drawerShelfRestore.setOnClickListener(view -> {
            Dialog[] dialog = new Dialog[1];
            View pop = LayoutInflater.from(this).inflate(R.layout.dialog_normal_confirm, null, false);
            TextView title = pop.findViewById(R.id.title);
            TextView msg = pop.findViewById(R.id.msg_tv);
            TextView cancel = pop.findViewById(R.id.cancel);
            TextView confirm = pop.findViewById(R.id.confirm);
            title.setText(R.string.restore_confirmation);
            msg.setText(R.string.restore_message);
            cancel.setText(R.string.cancel);
            confirm.setText(R.string.restore);

            cancel.setOnClickListener(v -> dialog[0].dismiss());
            confirm.setOnClickListener(v -> {
                mPresenter.restoreData();
                dialog[0].dismiss();
            });
            dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                    .setView(pop)
                    .show();
        });
    }

    /**
     * 更新主题切换按钮
     */
    private void upThemeVw() {
        if (isNightTheme()) {
            vwNightTheme.setImageResource(R.drawable.ic_daytime_24dp);
            vwNightTheme.setContentDescription(getString(R.string.click_to_day));
        } else {
            vwNightTheme.setImageResource(R.drawable.ic_brightness);
            vwNightTheme.setContentDescription(getString(R.string.click_to_night));
        }
        vwNightTheme.getDrawable().mutate().setColorFilter(ThemeStore.accentColor(this), PorterDuff.Mode.SRC_ATOP);
    }


    /**
     * 新版本运行
     */
    private void versionUpRun() {
        if (preferences.getInt("versionCode", 0) != MApplication.getVersionCode()) {
            //保存版本号
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("versionCode", MApplication.getVersionCode());
            editor.apply();
            //更新日志
            //            moDialogHUD.showAssetMarkdown("updateLog.md");
            //
            initBookSource();
        }
    }

    /**
     * 初始化书源
     */
    private void initBookSource() {
        ACache.get(this).put("sourceUrl", ConfigMng.DEFAULT_SOURCE_URL);
        Observable<List<BookSourceBean>> observable = BookSourceManager.importSource(ConfigMng.DEFAULT_SOURCE_URL);
        if (observable != null) {
            observable.subscribe(new SimpleObserver<List<BookSourceBean>>() {
                @SuppressLint({"DefaultLocale", "LogNotTimber"})
                @Override
                public void onNext(List<BookSourceBean> bookSourceBeans) {
                    if (bookSourceBeans.size() > 0) {
                        Log.e("TAG", "MainActivity::initBookSource::onNext::bookSourceSize = " + bookSourceBeans.size());
                    } else {
                        Log.e("TAG", "MainActivity::initBookSource::onNext = 格式不对");
                    }
                }

                @SuppressLint("LogNotTimber")
                @Override
                public void onError(Throwable e) {
                    Log.e("TAG", "MainActivity::initBookSource::onError = " + e.toString());
                }
            });
        }
    }

    /**
     * 获取权限
     */
    private void requestPermission() {
        List<String> per = PermissionUtils.checkMorePermissions(this, MApplication.PerList);
        if (per.size() > 0) {
            toast(R.string.get_storage_per);
            PermissionUtils.requestMorePermissions(this, per, MApplication.RESULT__PERMS);
        }
    }

    @Override
    protected void firstRequest() {
        if (!isRecreate) {
            versionUpRun();
            requestPermission();
            handler.postDelayed(this::preloadReader, 200);
        }
        handler.postDelayed(() -> UpLastChapterModel.getInstance().startUpdate(), 60 * 1000);
    }

    @Override
    public void dismissHUD() {
        moDialogHUD.dismiss();
    }

    public void onRestore(String msg) {
        moDialogHUD.showLoading(msg);
    }

    private void updateNaviViewWidth(int orientation) {
        int[] wh = ScreenUtils.getScreenSize(MainActivity.this);
        if (wh[0] == 0 || wh[1] == 0) {
            ViewTreeObserver vto = content_ll.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int[] sWH = ScreenUtils.getScreenSize(MainActivity.this);
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        wh[0] = Math.min(sWH[0], sWH[1]);
                        wh[1] = Math.max(sWH[0], sWH[1]);
                    } else {
                        wh[0] = Math.max(sWH[0], sWH[1]);
                        wh[1] = Math.min(sWH[0], sWH[1]);
                    }
                    content_ll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    float rate = wh[0] <= wh[1] ? ConfigMng.NAVIGAITON_WIDTH_RATE_TO_CONENT_vertical : ConfigMng.NAVIGAITON_WIDTH_RATE_TO_CONENT_horizontal;
                    int width = (int) (wh[0] * rate);
                    DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
                    lp.width = width;
                    navigationView.setLayoutParams(lp);
                }
            });
        } else {
            int[] sWH = new int[2];
            sWH[0] = wh[0];
            sWH[1] = wh[1];
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                wh[0] = Math.min(sWH[0], sWH[1]);
                wh[1] = Math.max(sWH[0], sWH[1]);
            } else {
                wh[0] = Math.max(sWH[0], sWH[1]);
                wh[1] = Math.min(sWH[0], sWH[1]);
            }
            float rate = wh[0] <= wh[1] ? ConfigMng.NAVIGAITON_WIDTH_RATE_TO_CONENT_vertical : ConfigMng.NAVIGAITON_WIDTH_RATE_TO_CONENT_horizontal;
            int width = (int) (wh[0] * rate);
            DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
            lp.width = width;
            navigationView.setLayoutParams(lp);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.checkMorePermissions(this, MApplication.PerList, new PermissionUtils.PermissionCheckCallBack() {
            @SuppressWarnings("SwitchStatementWithTooFewBranches")
            @Override
            public void onHasPermission() {
                switch (requestCode) {
                    case FILE_SELECT_RESULT:
                        startActivity(new Intent(MainActivity.this, ImportBookActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @SuppressWarnings("SwitchStatementWithTooFewBranches")
            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                switch (requestCode) {
                    case FILE_SELECT_RESULT:
                        MainActivity.this.toast(R.string.import_book_per);
                        break;
                    default:
                        break;
                }
            }

            @SuppressWarnings("SwitchStatementWithTooFewBranches")
            @Override
            public void onAlreadyTurnedDownAndNoAsk(String... permission) {
                switch (requestCode) {
                    case FILE_SELECT_RESULT:
                        MainActivity.this.toast(R.string.import_book_per);
                        break;
                    default:
                        break;
                }
                PermissionUtils.toAppSetting(MainActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else
            exit();
    }

    /**
     * 退出
     */
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showSnackBar(bottomNavigationView, getString(R.string.double_click_exit));
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        UpLastChapterModel.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void preloadReader() {
        new AsyncTaskPlain(() -> {
            ReadBookControl.getInstance();
            ChapterContentHelp.getInstance();
        }).execute();
    }

    @Override
    public Snackbar getSnackBar(String msg, int length) {
        return Snackbar.make(bottomNavigationView, msg, length);
    }

    @Override
    public void showSnackBar(String msg, int length) {
        super.showSnackBar(bottomNavigationView, msg, length);
    }
}