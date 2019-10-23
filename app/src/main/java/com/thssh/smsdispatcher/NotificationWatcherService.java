package com.thssh.smsdispatcher;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;
import java.util.TreeSet;

public class NotificationWatcherService extends NotificationListenerService {
    private static final String TAG = "NotificationWatcher";

    private Set<String> mExcludePackageList = new TreeSet<>();
    private boolean isRunning;

    public NotificationWatcherService() {
        Log.d(TAG, "NotificationWatcherService: ");
        mExcludePackageList.add("com.thssh.smsdispatcher");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            Log.d(TAG, "onStartCommand: ");
            Intent nf = new Intent(this, MainActivity.class);
            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentIntent(PendingIntent.getActivity(this, 0, nf, 0))
                    .setContentTitle("通知转发")
                    .setContentText("监控通知栏并转发")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .build();
            notification.defaults = Notification.DEFAULT_SOUND;

            startForeground(1220, notification);
            toggleNotificationListenerService(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected: ");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        logSBN(sbn, "onNotificationPosted");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        logSBN(sbn, "onNotificationRemoved");
    }

    void logSBN(StatusBarNotification sbn, String tag) {

        Bundle extras = sbn.getNotification().extras;
        String title = null;
        String content = null;
        String subText = null;
        if (extras.get(Notification.EXTRA_TITLE) != null) {
            title = extras.get(Notification.EXTRA_TITLE).toString();
        }
        if (extras.get(Notification.EXTRA_TEXT) != null) {
            content = extras.get(Notification.EXTRA_TEXT).toString();
        }
        if (extras.get(Notification.EXTRA_SUB_TEXT) != null) {
            subText = extras.get(Notification.EXTRA_SUB_TEXT).toString();
        }
        ApplicationInfo appInfo = (ApplicationInfo) extras.get("android.rebuild.applicationInfo");
        if (appInfo != null) {
            String packageName = appInfo.packageName;
            if (mExcludePackageList.contains(packageName)) return;
            if (TextUtils.equals(packageName, "com.android.mms")) {

            }
        }

        String contentText = buildContent(tag, title, content);
        Log.d(TAG, contentText);

        if (TextUtils.equals(tag, "onNotificationPosted")) {
            String preview = "预览：" + title.substring(0, 4) + " | " + content;
            RemoteService.get().sendMessage(preview, contentText);
        }
    }

    private String buildContent(String tag, String title, String content) {
        StringBuilder sb = new StringBuilder("### 标题")
                .append("\r\n")
                .append(title)
                .append("\r\n")
                .append("### 内容")
                .append("\r\n")
                .append(content);
        return sb.toString();
    }

    private void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationWatcherService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NotificationWatcherService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
