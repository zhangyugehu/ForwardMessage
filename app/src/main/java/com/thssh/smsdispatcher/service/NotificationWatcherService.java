package com.thssh.smsdispatcher.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Process;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.thssh.smsdispatcher.BuildConfig;
import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.activity.AppStartActivity;
import com.thssh.smsdispatcher.activity.MainActivity;
import com.thssh.smsdispatcher.dispatcher.CompatibleDispatcher;
import com.thssh.smsdispatcher.dispatcher.Dispatcher;
import com.thssh.smsdispatcher.dispatcher.DispatcherFactory;
import com.thssh.smsdispatcher.dispatcher.SamsungDispatcher;
import com.thssh.smsdispatcher.manager.ReportManager;
import com.thssh.smsdispatcher.tools.ServiceCheckWorker;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationWatcherService extends NotificationListenerService {
    private static final String TAG = "NotificationWatcher";

    private static final String CHANNEL_NAME = "短信转发";
    private static final String CHANNEL_ID = "9527";

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
            Intent nf = new Intent(this, AppStartActivity.class);

            Notification notification;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                assert manager != null;
                manager.createNotificationChannel(notificationChannel);

                notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
//                        .setContentIntent(PendingIntent.getActivity(this, 0, nf, PendingIntent.FLAG_CANCEL_CURRENT))
                        .setContentTitle(String.format(Locale.CHINA, "%s%s", "通知转发", BuildConfig.DEBUG?"(DEBUG)":""))
                        .setContentText("监控通知栏并转发")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .build();
            } else {

                notification = new Notification.Builder(getApplicationContext())
//                        .setContentIntent(PendingIntent.getActivity(this, 0, nf, PendingIntent.FLAG_CANCEL_CURRENT))
                        .setContentTitle(String.format(Locale.CHINA, "%s%s", "通知转发", BuildConfig.DEBUG ? "(DEBUG)" : ""))
                        .setContentText("监控通知栏并转发")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .build();
            }
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
        try {
            dispatcher = DispatcherFactory.createDispatcher();
        } catch (DispatcherFactory.UnSupportedDeviceException e) {
            dispatcher = new CompatibleDispatcher();
            report(e.getMessage());
        }
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

    private void report(String message) {
        ReportManager.getInstance().report(TAG, message);
    }

    void dispatch(StatusBarNotification sbn, Dispatcher.Type type) {
        if (dispatcher == null) return;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dispatcher.dispatch(sbn, type);
        } else {
            report("Android Version Un-supported");
        }
    }

    private void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationWatcherService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NotificationWatcherService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
