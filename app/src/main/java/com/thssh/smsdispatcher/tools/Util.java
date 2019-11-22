package com.thssh.smsdispatcher.tools;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.thssh.smsdispatcher.model.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Util {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    public static boolean isNotificationListenersEnabled(Context context) {
        String pkgName = context.getApplicationContext().getPackageName();
        final String flat = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private static List<AppInfo> sPkgCache;
    public static List<AppInfo> getPackages(Context context) {
        return getPackages(context, false);
    }
    public static List<AppInfo> getPackages(Context context, boolean force) {
        if (!force && sPkgCache != null) return sPkgCache;
        // 获取已经安装的所有应用, PackageInfo　系统类，包含应用信息
        List<AppInfo> infoList = new ArrayList<>();
        List<AppInfo> sysList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);

            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(
                    packageInfo.applicationInfo.loadLabel(packageManager).toString());//获取应用名称
            appInfo.setPackageName(packageInfo.packageName); //获取应用包名，可用于卸载和启动应用
            appInfo.setVersionName(packageInfo.versionName);//获取应用版本名
            appInfo.setVersionCode(packageInfo.versionCode);//获取应用版本号
            appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(packageManager));//获取应用图标
            if ((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                // AppInfo 自定义类，包含应用信息
                appInfo.setSysApp(false);
                infoList.add(appInfo);
            } else { // 系统应用
                appInfo.setSysApp(true);
                sysList.add(appInfo);
            }
        }
        infoList.addAll(sysList);
        sPkgCache = infoList;
        return infoList;
    }
}
