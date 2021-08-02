package com.thssh.smsdispatcher.dispatcher;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.thssh.smsdispatcher.manager.ReportManager;

import java.text.SimpleDateFormat;
import java.util.Set;

public class CompatibleDispatcher extends CommonDispatcher {
    private static final String TAG = "CompatibleDispatcher";

    SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void dispatch(StatusBarNotification sbn, Type type) {
        String packageName = sbn.getPackageName();
        if (shouldDispatch(packageName)) {
            Notification notification = sbn.getNotification();
            if (notification != null) {
                Bundle extras = notification.extras;
                long when = notification.when;
                report("when: " + FORMAT.format(when));
                report("extras >>>>>");
                Set<String> extraKeys = extras.keySet();
                for (String key : extraKeys) {
                    Object value = extras.get(key);
                    report(key + " = " + value);
                }
                report("extras <<<<<");
            } else {
                report("not-care package: " + packageName);
            }
        }
    }
}
