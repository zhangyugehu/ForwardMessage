package com.thssh.smsdispatcher.dispatcher;

import android.os.Build;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

public interface Dispatcher {
    enum Type {
        POST, REMOVE
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void dispatch(StatusBarNotification sbn, Type type);
}
