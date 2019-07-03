package com.moses.miiread.widget.modialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import androidx.appcompat.app.AlertDialog;
import com.moses.miiread.R;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.bean.BookmarkBean;
import com.moses.miiread.bean.ReplaceRuleBean;
import com.moses.miiread.utils.SoftInputUtil;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;

/**
 * 对话框
 */
public class MoDialogHUD {
    private Boolean isFinishing = false;

    private Context context;
    private Dialog dialog;//activity的根View
    private MoDialogView mSharedView;

    private OnDismissListener dismissListener;
    private Animation inAnim;
    private Animation outAnim;

    private Animation.AnimationListener outAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isFinishing = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dismissImmediately();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public MoDialogHUD(Context context) {
        this.context = context;
        initViews();
        initCenter();
        initAnimation();
    }

    private void initAnimation() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    private void initFromTopRight() {
        inAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_in_top_right);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_out_top_right);
    }

    private void initFromBottomRight() {
        inAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_in_bottom_right);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_out_bottom_right);
    }

    private void initFromBottomAnimation() {
        inAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_bottom_in);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_bottom_out);
    }

    private void initCenter() {
        mSharedView.setGravity(Gravity.CENTER);
        if (mSharedView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSharedView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.setMargins(0, 0, 0, 0);
                mSharedView.setLayoutParams(layoutParams);
            }
            mSharedView.setPadding(0, 0, 0, 0);
        }
    }

    private void initBottom() {
        mSharedView.setGravity(Gravity.BOTTOM);
        if (mSharedView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSharedView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.setMargins(0, 0, 0, 0);
                mSharedView.setLayoutParams(layoutParams);
            }
            mSharedView.setPadding(0, 0, 0, 0);
        }
    }

    private void initMarRightTop() {
        mSharedView.setGravity(Gravity.END | Gravity.TOP);
        if (mSharedView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSharedView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.setMargins(0, 0, 0, 0);
                mSharedView.setLayoutParams(layoutParams);
            }
            mSharedView.setPadding(0, 0, 0, 0);
        }
    }

    private void initViews() {
        mSharedView = new MoDialogView(context);
        dialog = new AlertDialog.Builder(context, R.style.alertDialogTheme).setView(mSharedView).create();
        dialog.setOnDismissListener(dialog -> dismissImmediately());
    }

    private Animation getInAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.moprogress_in);
    }

    private Animation getOutAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.moprogress_out);
    }

    private void onAttached() {
        dialog.show();
        isFinishing = false;
    }

    public void dismiss() {
        //消失动画
        if (mSharedView != null && mSharedView.getParent() != null) {
            if (!isFinishing) {
//                new Handler().post(() -> {
//                    outAnim.setAnimationListener(outAnimListener);
//                    mSharedView.startAnimation(outAnim);
//                });
                dismissImmediately();
            }
        }
    }

    public Boolean isShow() {
        return (mSharedView != null && mSharedView.getParent() != null);
    }

    private void dismissImmediately() {
        if (dismissListener != null) {
            dismissListener.onDismiss();
            dismissListener = null;
        }
        if (mSharedView != null && mSharedView.getParent() != null) {
            dialog.dismiss();
            new Handler().postDelayed(() -> SoftInputUtil.hideIMM(((Activity) context).getWindow().getDecorView()), 400L);
        }
        isFinishing = false;
    }

    /**
     * 加载动画
     */
    public void showLoading(String msg) {
        initCenter();
        initAnimation();
        onAttached();
        mSharedView.showLoading(msg);
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 单个按钮的提示信息
     */
    public void showInfo(String msg) {
        initCenter();
        initAnimation();
        mSharedView.showInfo(msg, v -> dismiss());
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 单个按钮的提示信息
     */
    public void showInfo(String msg, String btnText, View.OnClickListener listener) {
        initCenter();
        initAnimation();
        mSharedView.showInfo(msg, btnText, listener);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 两个不同等级的按钮
     */
    public void showTwoButton(String msg, String b_f, View.OnClickListener c_f, String b_s, View.OnClickListener c_s) {
        initCenter();
        initAnimation();
        mSharedView.showTwoButton(msg, b_f, c_f, b_s, c_s);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 显示一段文本
     */
    public void showText(String text) {
        initCenter();
        initAnimation();
        mSharedView.showText(text);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 显示asset Markdown
     */
    public void showAssetMarkdown(String assetFileName) {
        initCenter();
        initAnimation();
        mSharedView.showAssetMarkdown(assetFileName);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 显示单一类型list选择
     */
    public void showSimpleList(String[] items, OnItemClickListenerTwo onItemClickListenerTwo, String... extraActionTxt) {
        initCenter();
        initAnimation();
        mSharedView.showSimpleList(items, onItemClickListenerTwo, extraActionTxt);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 离线下载
     */
    public void showDownloadList(int startIndex, int endIndex, int all, DownLoadView.OnClickDownload clickDownload) {
        initCenter();
        initAnimation();
        DownLoadView.getInstance(mSharedView)
                .showDownloadList(startIndex, endIndex, all, clickDownload, v -> dismiss());
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 换源
     */
    public void showChangeSource(BookShelfBean bookShelf, ChangeSourceView.OnClickSource clickSource) {
        initCenter();
        initAnimation();
        ChangeSourceView changeSourceView = ChangeSourceView.getInstance(mSharedView);
        changeSourceView.showChangeSource(bookShelf, clickSource, this);
        dismissListener = changeSourceView::onDestroy;
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 弹出输入框
     */
    public void showInputBox(String title, String defaultValue, String[] adapterValues, InputView.OnInputOk onInputOk) {
        initCenter();
        initAnimation();
        InputView.getInstance(mSharedView)
                .showInputView(onInputOk, this, title, defaultValue, adapterValues);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 编辑替换规则
     */
    public void showPutReplaceRule(ReplaceRuleBean replaceRuleBean, EditReplaceRuleView.OnSaveReplaceRule onSaveReplaceRule) {
        initCenter();
        initAnimation();
        EditReplaceRuleView.getInstance(mSharedView)
                .showEditReplaceRule(replaceRuleBean, onSaveReplaceRule, this);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    /**
     * 书签
     */
    public void showBookmark(BookmarkBean bookmarkBean, boolean isAdd, EditBookmarkView.OnBookmarkClick bookmarkClick) {
        initCenter();
        initAnimation();
        EditBookmarkView.getInstance(mSharedView)
                .showBookmark(bookmarkBean, isAdd, bookmarkClick, this);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    public void showImageText(Bitmap bitmap, String text) {
        initCenter();
        initAnimation();
        mSharedView.showImageText(bitmap, text);
        onAttached();
        //mSharedView.startAnimation(inAnim);
    }

    private interface OnDismissListener {
        void onDismiss();
    }
}
