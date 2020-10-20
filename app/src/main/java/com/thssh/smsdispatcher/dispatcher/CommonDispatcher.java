package com.thssh.smsdispatcher.dispatcher;

import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.model.AppManager;
import com.thssh.smsdispatcher.net.RemoteService;
import com.thssh.smsdispatcher.settings.Settings;

import java.util.Set;

/**
 * 通用手机
 */
public abstract class CommonDispatcher implements Dispatcher {


    protected String beatify(String content) {
        int start = content.indexOf("[");
        int end = content.indexOf("]");
        if (start < end && start != -1) {
            return content.substring(end + 1);
        }

        return content;
    }
}
