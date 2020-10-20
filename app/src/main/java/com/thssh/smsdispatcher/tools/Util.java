package com.thssh.smsdispatcher.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.model.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
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

    public static String o2c(@Nullable Object o) {
        return o2c(o, "");
    }

    public static String o2c(@Nullable Object o, String def) {
        if (o == null) {
            return def;
        } else {
            return o.toString();
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Nullable
    public static String getPhoneNumber() {
        try {
            TelephonyManager tm = (TelephonyManager) App.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getLine1Number();
            }
        } catch (Throwable ignored) {}
        return "";
    }

    public static void getSimInfo() {

        Uri uri = Uri.parse("content://telephony/siminfo");
        Cursor cursor = null;
        ContentResolver contentResolver = App.getAppContext().getContentResolver();
        cursor = contentResolver.query(uri,
                new String[]{"_id", "sim_id", "icc_id", "display_name"}, "0=0",
                new String[]{}, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                String icc_id = cursor.getString(cursor.getColumnIndex("icc_id"));
                String display_name = cursor.getString(cursor.getColumnIndex("display_name"));
                int sim_id = cursor.getInt(cursor.getColumnIndex("sim_id"));
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));

                Log.d("Q_M", "icc_id-->" + icc_id);
                Log.d("Q_M", "sim_id-->" + sim_id);
                Log.d("Q_M", "display_name-->" + display_name);
                Log.d("Q_M", "subId或者说是_id->" + _id);
                Log.d("Q_M", "---------------------------------");
            }
        }
    }
}
