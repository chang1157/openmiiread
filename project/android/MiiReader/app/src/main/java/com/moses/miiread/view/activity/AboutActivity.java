package com.moses.miiread.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kunfei.basemvplib.impl.IPresenter;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.help.UpdateManager;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.utils.PrefUtil;
import com.moses.miiread.utils.ScreenUtils;
import com.moses.miiread.utils.UmengStat;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.adapter.JoinQQGroupAdapter;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;
import com.moses.miiread.widget.bottomsheet.BaseBottomSheetDialogFragment;
import com.moses.miiread.widget.evaluator.BezierEvaluator;
import com.moses.miiread.widget.modialog.MoDialogHUD;

import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by origin on 2017/12/15.
 * 关于
 */

public class AboutActivity extends MBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_disclaimer)
    TextView tvDisclaimer;
    @BindView(R.id.l_disclaimer)
    View vwDisclaimer;
    @BindView(R.id.ll_content)
    ConstraintLayout llContent;
    @BindView(R.id.tv_mail)
    TextView tvMail;
    @BindView(R.id.l_email)
    View vwMail;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.l_checkupdate)
    View vwUpdate;
    @BindView(R.id.tv_qq)
    TextView tvQq;
    @BindView(R.id.l_qq)
    View vwQq;
    @BindView(R.id.tv_app_summary)
    TextView tvAppSummary;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.l_share)
    View vwShare;
    @BindView(R.id.l_baseinfo)
    View baseInfo;
    @BindView(R.id.icon)
    ImageView icon;

    private MoDialogHUD moDialogHUD;
    private String[] allQQ;
    private String[] allQQKeys;

    //彩蛋容器
    private Queue<ImageView> eggshellQueue = new ArrayBlockingQueue<>(10);
    private Random random = new Random();
    private Point iconPoint;

    public static void startThis(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void initData() {
        moDialogHUD = new MoDialogHUD(this);
        allQQ = getResources().getStringArray(R.array.qq_group);
        allQQKeys = getResources().getStringArray(R.array.qq_group_keys);
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar();
        tvVersion.setText(getString(R.string.version_name, MApplication.getVersionName()));
        if (!ConfigMng.UPDATE_ENABLE)
            vwUpdate.setVisibility(View.GONE);
        ViewTreeObserver vto = icon.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                icon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] xy = new int[2];
                icon.getLocationOnScreen(xy);
                iconPoint = new Point(xy[0], xy[1] - ScreenUtils.getStatusBarHeight());
            }
        });
    }


    @Override
    protected void bindEvent() {
        vwMail.setOnClickListener(view -> openIntent(Intent.ACTION_SENDTO, "mailto:" + getString(R.string.email)));
        vwDisclaimer.setOnClickListener(view -> {
            Intent intent = new Intent(AboutActivity.this, SettingLicenseActivity.class);
            intent.putExtra("type", "disclaimer");
            startActivity(intent);
        });
        vwUpdate.setOnClickListener(view -> {
            ApplicationInfo ai;
            try {
                ai = getPackageManager().getApplicationInfo(getApplicationInfo().packageName, PackageManager.GET_META_DATA);
                String apkChannel = ai.metaData.getString("CHANNEL_NAME");
                if (apkChannel != null && apkChannel.compareTo("谷歌") == 0)
                    UpdateManager.getInstance(this).launchAppDetail(getApplicationInfo().packageName, "");
                else
                    UpdateManager.getInstance(this).checkUpdate(true);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });
        vwQq.setOnClickListener(view -> {
            final View v = LayoutInflater.from(AboutActivity.this).inflate(R.layout.dialog_qq, null, false);
            BaseBottomSheetDialogFragment qqFrg = new BaseBottomSheetDialogFragment();
            qqFrg.setContentView(v);
            RecyclerView rv_list = v.findViewById(R.id.rv_list);
            rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_list.setAdapter(new JoinQQGroupAdapter(getContext(), allQQ, getResources().getStringArray(R.array.qq_group_name), new OnItemClickListenerTwo() {
                @Override
                public void onClick(View view, int index) {
                    joinGroup(allQQ[index]);
                    view.post(qqFrg::dismiss);
                }

                @Override
                public void onLongClick(View view, int index) {
                    onClick(view, index);
                }
            }, "立即加入"));
            qqFrg.show(getSupportFragmentManager(), "qqFragment");
        });
        vwShare.setOnClickListener(view -> UmengStat.doShareUmeng(AboutActivity.this, "分享书迷给朋友",
                getString(R.string.read_summary), null, new UmengStat.OnShowCallBack() {
                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onDismiss() {

                    }
                }));
        AtomicInteger continueClick = new AtomicInteger(0);
        AtomicLong lastBaseInfoClickTime = new AtomicLong(-1);
        baseInfo.setOnClickListener(v -> {
            long curTime = System.currentTimeMillis();
            if (lastBaseInfoClickTime.get() == -1 || curTime - lastBaseInfoClickTime.get() <= 500) {
                lastBaseInfoClickTime.set(curTime);
                continueClick.incrementAndGet();
            } else {
                lastBaseInfoClickTime.set(-1);
                continueClick.set(0);
                handleClearEggs();
            }
            if (continueClick.get() > 1)
                handleShowEggs();
            if (continueClick.get() >= 5 + random.nextInt(5)) {
                boolean isEditSourceEnable = PrefUtil.getInstance().getBoolean("isEditSourceEnable", false);
                if (isEditSourceEnable)
                    toast(R.string.edit_source_enable_already);
                else
                    handleSetEditSourceEnable();
            }
        });
    }

    //开启书源编辑功能确认弹窗
    private void handleSetEditSourceEnable() {
        Dialog[] dialog = new Dialog[1];
        View pop = LayoutInflater.from(this).inflate(R.layout.dialog_normal_confirm, null, false);
        TextView title = pop.findViewById(R.id.title);
        TextView msg = pop.findViewById(R.id.msg_tv);
        TextView cancel = pop.findViewById(R.id.cancel);
        TextView confirm = pop.findViewById(R.id.confirm);
        title.setText(R.string.edit_source_enable_title);
        msg.setText(R.string.edit_source_enable_msg);
        cancel.setText(R.string.no);
        confirm.setText(R.string.edit_source_enable_yes);
        cancel.setOnClickListener(view -> dialog[0].dismiss());
        confirm.setOnClickListener(view -> {
            dialog[0].dismiss();
            PrefUtil.getInstance().setBoolean("isEditSourceEnable", true);
        });
        dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                .setView(pop)
                .show();
        dialog[0].setCanceledOnTouchOutside(false);
    }

    //连续点击开启彩蛋
    private void handleShowEggs() {
        ImageView egg = new ImageView(this);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(icon.getWidth(), icon.getHeight());
        lp.topToTop = R.id.ll_content;
        lp.leftToLeft = R.id.ll_content;
        egg.setTranslationX(random.nextInt(llContent.getWidth()));
        egg.setTranslationY(random.nextInt(llContent.getHeight()));
        egg.setPivotY(icon.getHeight() / 2f);
        egg.setPivotX(icon.getWidth() / 2f);
        eggshellQueue.offer(egg);

        ImageView popEgg = eggshellQueue.poll();
        if (popEgg != null) {
            popEgg.setImageResource(R.mipmap.ic_app_icon_txt);
            llContent.addView(popEgg, lp);

            ValueAnimator animator = ValueAnimator.ofObject(
                    new BezierEvaluator(new Point((int) (egg.getTranslationX() + baseInfo.getX()) / 2, (int) (egg.getTranslationY() + baseInfo.getY()) / 2)),
                    new Point((int) egg.getTranslationX(), (int) egg.getTranslationY()),
                    iconPoint);
            animator.addUpdateListener(animation -> {
                Point val = (Point) animation.getAnimatedValue();
                float alpha = animation.getAnimatedFraction();
                alpha = alpha < 0.3f ? 0.3f : alpha;
                popEgg.setAlpha(alpha);
                popEgg.setTranslationX(val.x);
                popEgg.setTranslationY(val.y);
                popEgg.setScaleX(alpha);
                popEgg.setScaleY(alpha);
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    llContent.removeView(popEgg);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    llContent.removeView(popEgg);
                }

                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    llContent.removeView(popEgg);
                }
            });
            animator.setDuration(1200);
            animator.start();
        }
    }

    //清空彩蛋
    private void handleClearEggs() {
        for (Iterator<ImageView> iterator = eggshellQueue.iterator(); iterator.hasNext(); ) {
            if (iterator.hasNext()) {
                ImageView egg = iterator.next();
                llContent.removeView(egg);
            }
        }
        eggshellQueue.clear();
    }

    private void joinGroup(String name) {
        String key;
        if (name.equals(allQQ[0])) {
            key = allQQKeys[0];
            if (joinQQGroupError(key)) {
                copyName(name.substring(5));
            }
        } else if (name.equals(allQQ[1])) {
            key = allQQKeys[1];
            if (joinQQGroupError(key)) {
                copyName(name.substring(5));
            }
        } else if (name.equals(allQQ[2])) {
            key = allQQKeys[2];
            if (joinQQGroupError(key)) {
                copyName(name.substring(5));
            }
        } else if (name.equals(allQQ[3])) {
            key = allQQKeys[3];
            if (joinQQGroupError(key)) {
                copyName(name.substring(5));
            }
        } else {
            copyName(name.substring(5));
        }
    }

    private void copyName(String name) {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, name);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clipData);
            toast(R.string.copy_complete);
        }
    }

    private boolean joinQQGroupError(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @SuppressWarnings("SameParameterValue")
    void openIntent(String intentName, String address) {
        try {
            Intent intent = new Intent(intentName);
            intent.setData(Uri.parse(address));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            toast(R.string.can_not_open, ERROR);
        }
    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
    }

    //菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
