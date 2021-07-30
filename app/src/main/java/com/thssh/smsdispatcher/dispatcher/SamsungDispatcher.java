package com.thssh.smsdispatcher.dispatcher;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.manager.AppManager;
import com.thssh.smsdispatcher.net.RemoteService;
import com.thssh.smsdispatcher.settings.Settings;
import com.thssh.smsdispatcher.tools.Util;

import java.util.Locale;
import java.util.Set;

public class SamsungDispatcher extends CommonDispatcher {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void dispatch(StatusBarNotification sbn, Type type) {

        Bundle extras = sbn.getNotification().extras;
        long when = sbn.getNotification().when;
        String title = "";
        String content = "";
        if (sbn.getNotification().tickerText != null) {
            content = sbn.getNotification().tickerText.toString();
        }
        String subText = "";
        if (extras.get(Notification.EXTRA_TITLE) != null) {
            title = extras.get(Notification.EXTRA_TITLE).toString();
        }
//        if (extras.get(Notification.EXTRA_TEXT) != null) {
//            content = extras.get(Notification.EXTRA_TEXT).toString();
//        }
        if (extras.get(Notification.EXTRA_SUB_TEXT) != null) {
            subText = extras.get(Notification.EXTRA_SUB_TEXT).toString();
        }
//        ApplicationInfo appInfo = (ApplicationInfo) extras.get("android.rebuild.applicationInfo");
        String packageName = sbn.getPackageName();
        String combinedTitle = String.format(Locale.getDefault(), "%s|%s|%s", Util.getPhoneNumber(), packageName, title);
        Log.i("MeizuDispatcher", "dispatch: " + packageName + "[" + combinedTitle + "]" + content + "==" + subText);
        Set<String> includeSet = getSettings().getIncludeSet();
        Set<String> excludeSet = getSettings().getExcludeSet();
//        String packageName = appInfo.packageName;
        if (includeSet != null && includeSet.size() < 1
                && excludeSet != null && excludeSet.contains(packageName)) return;
        if (includeSet != null && !includeSet.contains(packageName)) return;

        if (type == Type.POST) {
            RemoteService.get().sendMessage(when, combinedTitle, content);
            NotificationManagerCompat.from(App.getAppContext()).cancel(sbn.getId());
        }
    }

    public Settings getSettings() {
        return AppManager.getInstance().getSettings();
    }

}
