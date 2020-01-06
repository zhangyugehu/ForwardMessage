package com.thssh.smsdispatcher.dispatcher;

import android.service.notification.StatusBarNotification;

public interface Dispatcher {
    enum Type {
        POST, REMOVE
    }
    void dispatch(StatusBarNotification sbn, Type type);
}
