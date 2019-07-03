package com.moses.miiread.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import com.moses.miiread.BuildConfig;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.logic.ConfigMng;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.*;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.Map;

public class UmengStat {
    private static boolean UMENG_SWITCH = true;

    public static void initUmeng(Context context) throws PackageManager.NameNotFoundException {
        if (!UMENG_SWITCH)
            return;

        ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getApplicationInfo().packageName, PackageManager.GET_META_DATA);
        String apkChannel = ai.metaData.getString("CHANNEL_NAME");
        UMConfigure.setLogEnabled(false);
        {//是否需要打开授权页面， 为false直接授权
            UMShareConfig config = new UMShareConfig();
            config.isNeedAuthOnGetUserInfo(true);
            UMShareAPI.get(context).setShareConfig(config);
        }
        UMConfigure.init(context, BuildConfig.DEBUG ? "" : "", apkChannel, UMConfigure.DEVICE_TYPE_PHONE, null);
        UMShareAPI.get(context);

        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.enableEncrypt(true);
        MApplication.m_isUmengInit = true;
    }

    public static void onEvent(Context context, String strKey) {
        if (!UMENG_SWITCH)
            return;

        MobclickAgent.onEvent(context, strKey);
    }

    public static void onEventValue(Context context, String strKey, Map<String, String> keyMap, int nValue) {
        if (!UMENG_SWITCH)
            return;

        MobclickAgent.onEventValue(context, strKey, keyMap, nValue);
    }

    public static void onPause(Context context) {
        if (!UMENG_SWITCH)
            return;

        MobclickAgent.onPause(context);
    }

    public static void onResume(Context context) {
        if (!UMENG_SWITCH)
            return;

        MobclickAgent.onResume(context);
    }

    public static void onPageStart(String strPageName) {
        if (!UMENG_SWITCH || TextUtils.isEmpty(strPageName))
            return;

        MobclickAgent.onPageStart(strPageName);
    }

    public static void onPageEnd(String strPageName) {
        if (!UMENG_SWITCH || TextUtils.isEmpty(strPageName))
            return;

        MobclickAgent.onPageEnd(strPageName);
    }

    public interface OnShowCallBack {
        void onShow();

        void onDismiss();
    }

    /**
     * Umeng分享
     *
     * @param msgTitle 标题
     * @param msgText  说明
     * @param imgObj   imgUrl地址或Bitmap
     * @param callBack umeng分享回调
     */
    public static void doShareUmeng(final Activity act, String msgTitle, String msgText, Object imgObj, final OnShowCallBack callBack) {
        boolean permissionOk = PermissionUtils.checkPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionOk) {
            //doShare
            UMShareListener shareListener = new UMShareListener() {

                /**
                 * @descrption 分享开始的回调
                 * @param platform 平台类型
                 */
                @Override
                public void onStart(SHARE_MEDIA platform) {
                    if (callBack != null)
                        callBack.onShow();
                }

                /**
                 * @descrption 分享成功的回调
                 * @param platform 平台类型
                 */
                @Override
                public void onResult(SHARE_MEDIA platform) {
                    Log.e("TAG", "umeng share 成功");
                    if (callBack != null)
                        callBack.onDismiss();
                }

                /**
                 * @descrption 分享失败的回调
                 * @param platform 平台类型
                 * @param t 错误原因
                 */
                @Override
                public void onError(SHARE_MEDIA platform, Throwable t) {
                    Log.e("TAG", "umeng share err: " + t.getMessage());
                    if (callBack != null)
                        callBack.onDismiss();
                }

                /**
                 * @descrption 分享取消的回调
                 * @param platform 平台类型
                 */
                @Override
                public void onCancel(SHARE_MEDIA platform) {
                    Log.e("TAG", "umeng share 取消");
                    if (callBack != null)
                        callBack.onDismiss();
                }
            };

            UMWeb web = new UMWeb("");
            UMImage image = null;
            boolean unable = false;
            if (imgObj != null) {
                if (imgObj instanceof String)
                    image = new UMImage(act, (String) imgObj);
                else if (imgObj instanceof Bitmap)
                    image = new UMImage(act, (Bitmap) imgObj);
                else unable = true;
            } else unable = true;
            if (unable) {
                BitmapDrawable icon = (BitmapDrawable) act.getResources().getDrawable(R.mipmap.ic_launcher);
                image = new UMImage(act, icon.getBitmap());
            }
            image.compressStyle = UMImage.CompressStyle.QUALITY;
            web.setTitle(msgTitle);//标题
            web.setDescription(msgText);
            web.setThumb(image);  //缩略图
            new ShareAction(act).withMedia(web).withText("").
                    setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE).
                    setCallback(shareListener).open();
            if (callBack != null)
                callBack.onShow();
        } else
            PermissionUtils.requestPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1024);
    }
}
