package com.thssh.smsdispatcher.dispatcher;

import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.model.AppManager;
import com.thssh.smsdispatcher.net.RemoteService;
import com.thssh.smsdispatcher.settings.Settings;

import java.util.Set;

/**
 * 魅族手机
 */
public class MeiZuDispatcher implements Dispatcher {

    @Override
    public void dispatch(StatusBarNotification sbn, Type type) {

        Bundle extras = sbn.getNotification().extras;
        String title = "";
        String content = sbn.getNotification().tickerText.toString();
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
        Log.i("MeizuDispatcher", "dispatch: " + packageName + "[" + title + "]" + content + "==" + subText);
        Set<String> includeSet = getSettings().getIncludeSet();
        Set<String> excludeSet = getSettings().getExcludeSet();
//        String packageName = appInfo.packageName;
        if (includeSet != null && includeSet.size() < 1
                && excludeSet != null && excludeSet.contains(packageName)) return;
        if (includeSet != null && !includeSet.contains(packageName)) return;

        if (type == Type.POST) {
            RemoteService.get().sendMessage(title, content);
            NotificationManagerCompat.from(App.getAppContext()).cancel(sbn.getId());
        }
    }

    public Settings getSettings() {
        return AppManager.getInstance().getSettings();
    }

}
