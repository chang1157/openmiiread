package com.moses.miiread.help;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;

import androidx.core.content.FileProvider;
import com.moses.miiread.BuildConfig;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.BaseModelImpl;
import com.moses.miiread.base.observer.SimpleObserver;
import com.moses.miiread.bean.UpdateInfoBean;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.model.analyzeRule.AnalyzeHeaders;
import com.moses.miiread.model.impl.IHttpGetApi;
import com.moses.miiread.utils.MD5Utils;
import com.moses.miiread.view.activity.UpdateActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpdateManager {
    private Activity activity;

    public static UpdateManager getInstance(Activity activity) {
        return new UpdateManager(activity);
    }

    private UpdateManager(Activity activity) {
        this.activity = activity;
    }

    public void checkUpdate(boolean showMsg) {
        BaseModelImpl.getInstance().getRetrofitString((BuildConfig.DEBUG ? ConfigMng.OSS_DOMAIN_DBG : ConfigMng.OSS_DOMAIN_RLS) + "." + MApplication.getInstance().getString(R.string.latest_release_api_subfix))
                .create(IHttpGetApi.class)
                .getWebContent((BuildConfig.DEBUG ? ConfigMng.OSS_DOMAIN_DBG : ConfigMng.OSS_DOMAIN_RLS) + "." + MApplication.getInstance().getString(R.string.latest_release_url_subfix), AnalyzeHeaders.getMap(null))
                .flatMap(response -> analyzeLastReleaseApi(response.body()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<UpdateInfoBean>() {
                    @Override
                    public void onNext(UpdateInfoBean updateInfo) {
                        if (updateInfo.getUpDate()) {
                            UpdateActivity.startThis(activity, updateInfo);
                        } else if (showMsg) {
                            Toast.makeText(activity, "已是最新版本", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (showMsg) {
                            Toast.makeText(activity, "检测新版本出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Observable<UpdateInfoBean> analyzeLastReleaseApi(String jsonStr) {
        return Observable.create(emitter -> {
            try {
                UpdateInfoBean updateInfo = new UpdateInfoBean();
                JsonObject version = new JsonParser().parse(jsonStr).getAsJsonObject();
                boolean prerelease = version.get("prerelease").getAsBoolean();
                if (prerelease)
                    return;
                JsonArray assets = version.get("assets").getAsJsonArray();
                if (assets.size() > 0) {
                    String thisVersion = version.get("tag_name").getAsString();
                    String url = assets.get(0).getAsJsonObject().get("browser_download_url").getAsString();
                    String apkMd5 = assets.get(0).getAsJsonObject().get("MD5").getAsString();
                    String detail = version.get("body").getAsString();
                    int installedVersionCode = BuildConfig.VERSION_CODE;
                    int thisVersionCode = version.get("version_code").getAsInt();
                    updateInfo.setUrl(url);
                    updateInfo.setLastVersion(thisVersion);
                    updateInfo.setDetail("# " + thisVersion + "\n" + detail);
                    updateInfo.setApkMd5(apkMd5);
                    checkMd5(updateInfo);
                    if (thisVersionCode > installedVersionCode) {
                        updateInfo.setUpDate(true);
                    } else {
                        updateInfo.setUpDate(false);
                    }
                }
                emitter.onNext(updateInfo);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
                emitter.onComplete();
            }
        });
    }

    /**
     * 安装apk
     */
    public void installApk(File apkFile) {
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.d("wwd", "Failed to launcher installing activity");
        }
    }

    public static String getSavePath(String fileName) {
        return Environment.getExternalStoragePublicDirectory(DOWNLOAD_SERVICE).getPath() + fileName;
    }

    public static boolean checkMd5(UpdateInfoBean updateInfo) {
        File apkFile = new File(UpdateManager.getSavePath(updateInfo.getUrl().substring(updateInfo.getUrl().lastIndexOf("/"))));
        if (!apkFile.exists())
            return false;
        String localMD5 = MD5Utils.getFileMD5(apkFile);
        if (localMD5 == null) {
            //noinspection ResultOfMethodCallIgnored
            apkFile.delete();
            return false;
        }
        boolean match = localMD5.compareTo(updateInfo.getApkMd5()) == 0;
        if (!match) {
            //noinspection ResultOfMethodCallIgnored
            apkFile.delete();
            return false;
        }
        return true;
    }

    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) return;

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}