//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.view.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kunfei.basemvplib.impl.IPresenter;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.core.AsyncTaskPlain;
import com.moses.miiread.dao.DbHelper;
import com.moses.miiread.help.ReadBookControl;
import com.moses.miiread.presenter.ReadBookPresenter;
import com.moses.miiread.utils.bar.BarHide;
import com.moses.miiread.utils.bar.ImmersionBar;

public class WelcomeActivity extends MBaseActivity {

    @BindView(R.id.iv_bg)
    ImageView ivBg;

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //noinspection Convert2MethodRef
        new AsyncTaskPlain(() -> DbHelper.getDaoSession()).execute();
        ButterKnife.bind(this);
        ValueAnimator welAnimator = ValueAnimator.ofFloat(1f, 0f).setDuration(500);
        welAnimator.setStartDelay(1500);
        welAnimator.addUpdateListener(animation -> {
            float alpha = (Float) animation.getAnimatedValue();
            ((ViewGroup) ivBg.getParent()).setAlpha(alpha);
        });
        welAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (preferences.getBoolean(getString(R.string.pk_default_read), false)) {
                    startReadActivity();
                } else {
                    startBookshelfActivity();
                }
                finish();
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        welAnimator.start();
    }

    private void startBookshelfActivity() {
        startActivityByAnim(new Intent(this, MainActivity.class), R.anim.fade_in_main, R.anim.fade_out_main);
    }

    private void startReadActivity() {
        Intent intent = new Intent(this, ReadBookActivity.class);
        intent.putExtra("openFrom", ReadBookPresenter.OPEN_FROM_APP);
        startActivity(intent);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.hideBarDivider();
        ReadBookControl readBookControl = ReadBookControl.getInstance();
        if (!isImmersionBarEnabled()) {
            mImmersionBar.statusBarDarkFont(false);
        } else if (readBookControl.getDarkStatusIcon()) {
            mImmersionBar.statusBarDarkFont(true, 0.2f);
        } else {
            mImmersionBar.statusBarDarkFont(false);
        }
        if (ImmersionBar.getNotchBarHeight(this) > 0) {
            // TODO: 2019-06-28

            mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR);
        } else
            mImmersionBar.hideBar(BarHide.FLAG_HIDE_BAR);
        mImmersionBar.init();
    }

    @Override
    protected void initData() {

    }
}
