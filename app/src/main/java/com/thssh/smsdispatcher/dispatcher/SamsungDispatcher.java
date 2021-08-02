package com.thssh.smsdispatcher.dispatcher;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

import com.thssh.smsdispatcher.tools.Util;

import java.util.Locale;

public class SamsungDispatcher extends CommonDispatcher {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void dispatch(StatusBarNotification sbn, Type type) {

        if (shouldDispatch(sbn.getPackageName())) {
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

            sendMessage(type, when, packageName, combinedTitle, content, sbn.getId());
        }
    }

}
