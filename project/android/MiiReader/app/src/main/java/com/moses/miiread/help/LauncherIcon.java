package com.moses.miiread.help;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import com.moses.miiread.MApplication;

/**
 * Created by origin on 2018/2/27.
 * 更换图标
 */

public class LauncherIcon {
    public static void Change() {
        PackageManager packageManager = MApplication.getInstance().getPackageManager();
        ComponentName componentNameMain = new ComponentName(MApplication.getInstance(), "com.moses.miiread.view.activity.WelcomeActivity");
        ComponentName componentNameBookMain = new ComponentName(MApplication.getInstance(), "com.moses.miiread.view.activity.WelcomeBookActivity");

        if (packageManager.getComponentEnabledSetting(componentNameBookMain) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            //启用
            packageManager.setComponentEnabledSetting(componentNameMain,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            //禁用
            packageManager.setComponentEnabledSetting(componentNameBookMain,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else {
            //启用
            packageManager.setComponentEnabledSetting(componentNameBookMain,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            //禁用
            packageManager.setComponentEnabledSetting(componentNameMain,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }
}
