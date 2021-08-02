package com.thssh.smsdispatcher.dispatcher;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

public class GoogleDispatcher extends CommonDispatcher {

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void dispatch(StatusBarNotification sbn, Type type) {
        String packageName = sbn.getPackageName();
        if (shouldDispatch(packageName))  {
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;
            sendMessage(type, notification.when, packageName, extras.getString(EXTRA_TITLE, NO_TITLE), extras.getString(EXTRA_TEXT, NO_CONTENT), sbn.getId());
        } else {
//            report("[" + packageName + "] is not care.");
        }
    }
}
