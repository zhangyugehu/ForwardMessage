package com.thssh.smsdispatcher.model.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.thssh.smsdispatcher.BuildConfig;
import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.activity.MainActivity;
import com.thssh.smsdispatcher.dispatcher.Dispatcher;
import com.thssh.smsdispatcher.dispatcher.MeiZuDispatcher;
import com.thssh.smsdispatcher.tools.ServiceCheckWorker;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class NotificationWatcherService extends NotificationListenerService {
    private static final String TAG = "NotificationWatcher";

    private boolean isRunning;

    public static boolean sIsRunning;
    private Dispatcher dispatcher;

    public NotificationWatcherService() {
        Log.d(TAG, "NotificationWatcherService: ");
    }

    public static void start(Context context) {
        context.startService(new Intent(context, NotificationWatcherService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            sIsRunning = isRunning = true;
            Log.d(TAG, "onStartCommand: ");
            Intent nf = new Intent(this, MainActivity.class);
            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentIntent(PendingIntent.getActivity(this, 0, nf, 0))
                    .setContentTitle(String.format(Locale.CHINA, "%s%s", "通知转发", BuildConfig.DEBUG?"(DEBUG)":""))
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
        sIsRunning = isRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dispatcher = new MeiZuDispatcher();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected: ");

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(ServiceCheckWorker.class, 16, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueue(workRequest);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        dispatch(sbn, Dispatcher.Type.POST);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        dispatch(sbn, Dispatcher.Type.REMOVE);
    }

    void dispatch(StatusBarNotification sbn, Dispatcher.Type type) {
        if (dispatcher == null) return;
        dispatcher.dispatch(sbn, type);
    }

    private void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationWatcherService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NotificationWatcherService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
