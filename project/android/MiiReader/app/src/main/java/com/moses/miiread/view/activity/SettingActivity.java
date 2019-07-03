package com.moses.miiread.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hwangjr.rxbus.RxBus;
import com.kunfei.basemvplib.impl.IPresenter;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.bean.SettingChildBean;
import com.moses.miiread.bean.SettingGrpBean;
import com.moses.miiread.constant.RxBusTag;
import com.moses.miiread.help.DataBackup;
import com.moses.miiread.help.ReadBookControl;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.utils.PrefUtil;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.adapter.SettingAdapter;
import com.moses.miiread.widget.bottomsheet.BottomSheetRadioPickUtil;
import com.moses.miiread.widget.recycler.expandable.OnRecyclerViewListener;
import com.moses.miiread.widget.recycler.expandable.bean.RecyclerViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by origin on 2017/12/16.
 * 设置
 */
public class SettingActivity extends MBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    SettingAdapter adapter;
    ReadBookControl control;

    @BindView(R.id.theme_grp)
    RadioGroup themeGrp;

    public static void startThis(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar(getString(R.string.setting));
    }

    @Override
    protected void initData() {

    }

    //设置ToolBar
    public void setupActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    //菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindView() {
        super.bindView();
        setPresData();
    }

    private void setPresData() {
        control = ReadBookControl.getInstance();
        List<RecyclerViewData<SettingGrpBean, SettingChildBean>> datas = new ArrayList<>();

        if (MApplication.getInstance().accountToken != null) {
            //账户
            SettingGrpBean grpAccountBean = new SettingGrpBean(getString(R.string.account));
            List<SettingChildBean> cldAccountBeans = new ArrayList<>();
            SettingChildBean account = new SettingChildBean(getString(R.string.my_account), "", "234xx1164@qq.com", false, false, "");
            account.setRightArrow(true);
            SettingChildBean history = new SettingChildBean(getString(R.string.read_his), "", "最近阅读：圣墟", false, false, "");//todo 模拟登录后
            history.setRightArrow(true);
            cldAccountBeans.add(account);
            cldAccountBeans.add(history);
            RecyclerViewData<SettingGrpBean, SettingChildBean> accData = new RecyclerViewData<>(grpAccountBean, cldAccountBeans, true);
            datas.add(accData);
        }
        //常规
        SettingGrpBean grpGeneralBean = new SettingGrpBean(getString(R.string.normal_setting));
        List<SettingChildBean> cldGeneralBeans = new ArrayList<>();
        int threadNum = PrefUtil.getInstance().getInt(getResources().getString(R.string.pk_threads_num), ConfigMng.DEFAULT_THREAD_NUM);
        SettingChildBean threadControl = new SettingChildBean(String.format(getString(R.string.threads_num), String.valueOf(threadNum)), getString(R.string.update_search_threads_num), "", false, false, "");
        cldGeneralBeans.add(threadControl);
        boolean isEditSourceEnable = PrefUtil.getInstance().getBoolean("isEditSourceEnable", false);
        SettingChildBean editSourceEnable = new SettingChildBean(getString(R.string.edit_source_enable), getString(R.string.edit_source_enable_desc), "", true, isEditSourceEnable, "");
        if (isEditSourceEnable)
            cldGeneralBeans.add(editSourceEnable);
        SettingChildBean wifiOnly = new SettingChildBean(getString(R.string.wifi_only_setting), getString(R.string.wifi_only_setting_desc), "", true, control.getDownloadWifiOnly(), "");
        cldGeneralBeans.add(wifiOnly);
        SettingChildBean localEnable = new SettingChildBean(getString(R.string.enable_local_book_setting), getString(R.string.enable_local_book_setting_desc), "", true, control.getLocalPdfTxtEnable(), "");
        cldGeneralBeans.add(localEnable);
        RecyclerViewData<SettingGrpBean, SettingChildBean> generalData = new RecyclerViewData<>(grpGeneralBean, cldGeneralBeans, true);
        datas.add(generalData);

        //阅读习惯
        SettingGrpBean grpReadingBean = new SettingGrpBean(getString(R.string.read_habit_setting));
        List<SettingChildBean> cldReadingBeans = new ArrayList<>();
        //
        String rotateDesc = "";
        int direction = control.getScreenDirection();
        switch (direction) {
            case 0:
                rotateDesc = getString(R.string.screen_unspecified);
                break;
            case 1:
                rotateDesc = getString(R.string.screen_portrait);
                break;
            case 2:
                rotateDesc = getString(R.string.screen_landscape);
                break;
            default:
                break;
        }
        SettingChildBean autoRotate = new SettingChildBean(getString(R.string.screen_direction), rotateDesc, "", false, false, "");
        cldReadingBeans.add(autoRotate);
        //
        String pageModeDesc = "";
        int pageMode = control.getPageMode();
        switch (pageMode) {
            case 0:
                pageModeDesc = getString(R.string.page_mode_COVER);
                break;
            case 1:
                pageModeDesc = getString(R.string.page_mode_SIMULATION);
                break;
            case 2:
                pageModeDesc = getString(R.string.page_mode_SLIDE);
                break;
            case 3:
                pageModeDesc = getString(R.string.page_mode_SCROLL);
                break;
            case 4:
                pageModeDesc = getString(R.string.page_mode_NONE);
                break;
            default:
                break;
        }
        SettingChildBean pageTransMode = new SettingChildBean(getString(R.string.page_mode), pageModeDesc, "", false, false, "");
        cldReadingBeans.add(pageTransMode);
        //
        String clickModeDesc = "";
        int clickMode = control.getClickMode();
        switch (clickMode) {
            case 0:
                clickModeDesc = getString(R.string.click_mode_right_hand);
                break;
            case 1:
                clickModeDesc = getString(R.string.click_mode_left_hand);
                break;
            case 2:
                clickModeDesc = getString(R.string.click_mode_screen);
                break;
            default:
                break;
        }
        SettingChildBean pageClickMode = new SettingChildBean(getString(R.string.click_mode), clickModeDesc, "", false, false, "");
        cldReadingBeans.add(pageClickMode);
        //
        String vKeyTurnDesc = "";
        int vKTurnMode = control.getVolumeKeyTurnPageMode();
        switch (vKTurnMode) {
            case 0:
                vKeyTurnDesc = getString(R.string.use_volume_key_turn_page0_never);
                break;
            case 1:
                vKeyTurnDesc = getString(R.string.use_volume_key_turn_page1_only_zoom);
                break;
            case 2:
                vKeyTurnDesc = getString(R.string.use_volume_key_turn_page2_both_zoom_speech);
                break;
            default:
                break;
        }
        SettingChildBean vKeyTurnPage = new SettingChildBean(getString(R.string.use_volume_key_turn_page), vKeyTurnDesc, "", false, false, "");
        cldReadingBeans.add(vKeyTurnPage);
        //
        String jfConvertDesc = "";
        int jfConvertMode = control.getTextConvert();
        switch (jfConvertMode) {
            case 0:
                jfConvertDesc = getString(R.string.jf_convert_o);
                break;
            case 1:
                jfConvertDesc = getString(R.string.jf_convert_f);
                break;
            case 2:
                jfConvertDesc = getString(R.string.jf_convert_j);
                break;
            default:
                break;
        }
        SettingChildBean jf_convert = new SettingChildBean(getString(R.string.jf_convert), jfConvertDesc, "", false, false, "");
        cldReadingBeans.add(jf_convert);
        //消除个别机型底部白条
        SettingChildBean fixBottomNav = new SettingChildBean(getString(R.string.fix_bottom_nav), getString(R.string.fix_bottom_nav_desc), "", true, control.getFixBottomNav(), "");
        cldReadingBeans.add(fixBottomNav);
        //
        SettingChildBean hideStatubarWhenRead = new SettingChildBean(getString(R.string.ps_hide_status_bar), getString(R.string.ps_hide_status_bar_desc), "", true, control.getHideStatusBar(), "");
        cldReadingBeans.add(hideStatubarWhenRead);
        //
        SettingChildBean showTimeBattery = new SettingChildBean(getString(R.string.showTimeBattery), "", "", true, control.getShowTimeBattery(), "");
        if (control.getHideStatusBar()) {
            cldReadingBeans.add(showTimeBattery);
        }
        //
        String screenTimeoutDesc = "";
        int screenTimeOutMode = control.getScreenTimeOut();
        String[] tDescArray = getResources().getStringArray(R.array.screen_time_out);
        for (int i = 0; i < tDescArray.length; i++) {
            if (screenTimeOutMode == i) {
                screenTimeoutDesc = tDescArray[i];
                break;
            }
        }
        SettingChildBean screenTimeout = new SettingChildBean(getString(R.string.keep_light), screenTimeoutDesc, "", false, false, "");
        cldReadingBeans.add(screenTimeout);
        //
        RecyclerViewData<SettingGrpBean, SettingChildBean> readingData = new RecyclerViewData<>(grpReadingBean, cldReadingBeans, true);
        datas.add(readingData);
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SettingAdapter(this, datas);
        recyclerView.setAdapter(adapter);
        adapter.setAlwaysExpandGrp(true);
        adapter.setOnItemClickListener(new OnRecyclerViewListener.OnItemClickListener() {
            @Override
            public void onGroupItemClick(int position, int groupPosition, View view) {

            }

            @Override
            public void onChildItemClick(int position, int groupPosition, int childPosition, View view) {
                SettingChildBean childBean = adapter.getAllDatas().get(groupPosition).getChild(childPosition);
                if (childBean.isCheckable()) {
                    childBean.setChecked(!childBean.isChecked());
                    CheckBox checkBox = view.findViewById(R.id.check);
                    if (checkBox != null && checkBox.getVisibility() == View.VISIBLE) {
                        checkBox.setChecked(childBean.isChecked());
                        if (childBean.getTitle().compareTo(wifiOnly.getTitle()) == 0) {//仅wifi下载
                            control.setDownloadWifiOnly(checkBox.isChecked());
                        } else if (childBean.getTitle().compareTo(localEnable.getTitle()) == 0) {//本地pdf/txt
                            control.setLocalPdfTxtEnable(checkBox.isChecked());
                        } else if (childBean.getTitle().compareTo(fixBottomNav.getTitle()) == 0) {//消除nokia x7等机型底部白条
                            control.setFixBottomNav(checkBox.isChecked());
                        } else if (childBean.getTitle().compareTo(hideStatubarWhenRead.getTitle()) == 0) {//阅读时隐藏状态栏
                            control.setHideStatusBar(checkBox.isChecked());
                            RxBus.get().post(RxBusTag.READ_BOOK_REFRESH_PAGE, true);
                            if (checkBox.isChecked()) {
                                //隐藏状态栏时，显示时间、电量选项
                                adapter.getAllDatas().get(groupPosition).getChildList().add(childPosition + 1, showTimeBattery);
                            } else {
                                //显示状态栏时，隐藏时间、电量选项
                                adapter.getAllDatas().get(groupPosition).getChildList().remove(showTimeBattery);
                            }
                            adapter.notifyRecyclerViewData();
                        } else if (childBean.getTitle().compareTo(showTimeBattery.getTitle()) == 0) {//显示时间、电量
                            control.setShowTimeBattery(checkBox.isChecked());
                            RxBus.get().post(RxBusTag.READ_BOOK_REFRESH_PAGE, true);
                        } else if (childBean.getTitle().compareTo(editSourceEnable.getTitle()) == 0) {//允许编辑书源
                            setEditSourceDisableDlg(position, groupPosition, childPosition);
                        }
                    }
                } else {
                    if (childBean.getTitle().compareTo(autoRotate.getTitle()) == 0) {//旋转屏幕
                        final String title = getResources().getString(R.string.screen_direction);
                        final String[] array = getResources().getStringArray(R.array.screen_derection_mode);
                        BottomSheetRadioPickUtil.handleBottomSheetRadioPick(getSupportFragmentManager(), SettingActivity.this,
                                title, array, control.getScreenDirection(),
                                index -> {
                                    control.setScreenDirection(index);
                                    childBean.setDesc(array[index]);
                                    adapter.notifyItemChanged(position);
                                    RxBus.get().post(RxBusTag.READ_BOOK_RECREATE, true);
                                });
                    } else if (childBean.getTitle().compareTo(pageTransMode.getTitle()) == 0) {//翻页模式
                        final String title = getResources().getString(R.string.page_mode);
                        final String[] array = getResources().getStringArray(R.array.page_mode);
                        BottomSheetRadioPickUtil.handleBottomSheetRadioPick(getSupportFragmentManager(), SettingActivity.this,
                                title, array, control.getPageMode(),
                                index -> {
                                    control.setPageMode(index);
                                    childBean.setDesc(array[index]);
                                    adapter.notifyItemChanged(position);
                                    RxBus.get().post(RxBusTag.READ_BOOK_REFRESH_PAGE, true);
                                    if (array[index].compareTo(getResources().getString(R.string.page_mode_SIMULATION)) == 0)
                                        showSnackBar(recyclerView, "仿真模式向前翻页存在bug，即将修复。建议暂时使用其他翻页模式！", 2500);
                                });
                    } else if (childBean.getTitle().compareTo(pageClickMode.getTitle()) == 0) {//翻页点击规则
                        final String title = getResources().getString(R.string.click_mode);
                        final String[] array = getResources().getStringArray(R.array.click_mode);
                        BottomSheetRadioPickUtil.handleBottomSheetRadioPick(getSupportFragmentManager(), SettingActivity.this,
                                title, array, control.getClickMode(),
                                index -> {
                                    control.setClickMode(index);
                                    childBean.setDesc(array[index]);
                                    adapter.notifyItemChanged(position);
                                });
                    } else if (childBean.getTitle().compareTo(vKeyTurnPage.getTitle()) == 0) {//使用音量键翻页
                        final String title = getResources().getString(R.string.use_volume_key_turn_page);
                        final String[] array = getResources().getStringArray(R.array.volume_turn_page_mode);
                        BottomSheetRadioPickUtil.handleBottomSheetRadioPick(getSupportFragmentManager(), SettingActivity.this,
                                title, array, control.getVolumeKeyTurnPageMode(),
                                index -> {
                                    control.setVolumeKeyTurnPageMode(index);
                                    childBean.setDesc(array[index]);
                                    adapter.notifyItemChanged(position);
                                });
                    } else if (childBean.getTitle().compareTo(jf_convert.getTitle()) == 0) {//简繁转换
                        final String title = getResources().getString(R.string.jf_convert);
                        final String[] array = getResources().getStringArray(R.array.jf_convert_mode);
                        BottomSheetRadioPickUtil.handleBottomSheetRadioPick(getSupportFragmentManager(), SettingActivity.this,
                                title, array, control.getTextConvert(),
                                index -> {
                                    control.setTextConvert(index);
                                    childBean.setDesc(array[index]);
                                    adapter.notifyItemChanged(position);
                                    RxBus.get().post(RxBusTag.READ_BOOK_REFRESH_PAGE, true);
                                });
                    } else if (childBean.getTitle().compareTo(screenTimeout.getTitle()) == 0) {//屏幕超时
                        final String title = getResources().getString(R.string.keep_light);
                        final String[] array = getResources().getStringArray(R.array.screen_time_out);
                        BottomSheetRadioPickUtil.handleBottomSheetRadioPick(getSupportFragmentManager(), SettingActivity.this,
                                title, array, control.getScreenTimeOut(),
                                index -> {
                                    control.setScreenTimeOut(index);
                                    childBean.setDesc(array[index]);
                                    adapter.notifyItemChanged(position);
                                    RxBus.get().post(RxBusTag.READ_BOOK_KEEP_SCREEN_ON_CHANGE, index);
                                });
                    } else if (childBean.getTitle().compareTo(threadControl.getTitle()) == 0) {//线程数
                        setThreadNum(position, groupPosition, childPosition);
                    }
                }
            }
        });
        setThemeGrp();
    }

    private void setEditSourceDisableDlg(int posi, int grpPosi, int cldPosi) {
        Dialog[] dialog = new Dialog[1];
        View pop = LayoutInflater.from(this).inflate(R.layout.dialog_normal_confirm, null, false);
        TextView title = pop.findViewById(R.id.title);
        TextView msg = pop.findViewById(R.id.msg_tv);
        TextView cancel = pop.findViewById(R.id.cancel);
        TextView confirm = pop.findViewById(R.id.confirm);
        title.setText(R.string.edit_source_disable);
        msg.setText(R.string.edit_source_disable_desc);
        cancel.setText(R.string.no);
        confirm.setText(R.string.edit_source_disable_yes);
        cancel.setOnClickListener(view -> {
            dialog[0].dismiss();
            adapter.getAllDatas().get(grpPosi).getChild(cldPosi).setChecked(true);
            adapter.notifyItemChanged(posi);
        });
        confirm.setOnClickListener(view -> {
            dialog[0].dismiss();
            PrefUtil.getInstance().setBoolean("isEditSourceEnable", false);
            DataBackup.getInstance().backupBookSourceOnly();
            RxBus.get().post(RxBusTag.RESTORE_BOOK_SOURCE_FROM_SERVER, true);
            recreate();
        });
        dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                .setView(pop)
                .create();
        dialog[0].setOnDismissListener(dialog1 -> {
            adapter.getAllDatas().get(grpPosi).getChild(cldPosi).setChecked(true);
            adapter.notifyItemChanged(posi);
        });
        dialog[0].show();
    }

    private void setThreadNum(int posi, int grpPosi, int cldPosi) {
        //pk_threads_num
        Dialog[] dialog = new Dialog[1];
        View pop = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null, false);
        NumberPicker numberPicker = pop.findViewById(R.id.number_picker);
        int threadNum = PrefUtil.getInstance().getInt(getResources().getString(R.string.pk_threads_num), ConfigMng.DEFAULT_THREAD_NUM);
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            String title = String.format(getString(R.string.threads_num), String.valueOf(newVal));
            adapter.getAllDatas().get(grpPosi).getChild(cldPosi).setTitle(title);
            adapter.notifyItemChanged(posi);
            PrefUtil.getInstance().setInt(getResources().getString(R.string.pk_threads_num), newVal);
        });
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(threadNum);
        dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                .setView(pop)
                .create();
        dialog[0].setOnDismissListener(dialog1 -> {
            adapter.getAllDatas().get(grpPosi).getChild(0).setChecked(true);
            adapter.notifyItemChanged(posi);
        });
        dialog[0].show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void initImmersionBar() {
        super.initImmersionBar();
    }

    private void setThemeGrp() {
        findViewById(R.id.ll_header).setVisibility(View.GONE);
    }
}