package com.thssh.smsdispatcher.tools;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.service.NotificationWatcherService;

public class ServiceCheckWorker extends Worker {

    private static final String TAG = "ServiceCheckWorker";
    public ServiceCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d(TAG, "ServiceCheckWorker: init...");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @NonNull
    @Override
    public Result doWork() {
        if (!NotificationWatcherService.sIsRunning) {
            Log.d(TAG, "doWork: service is not running, starting...");
            try {
                NotificationWatcherService.start(App.getAppContext());
            } catch (Throwable t) {
                Log.d(TAG, "doWork: service start failure. [" + t.getMessage() + "]");
            }
        } else {
            Log.d(TAG, "doWork: service is running.");
        }
        return Result.success();
    }

    public static class Bus {
        static class Singleton {
            public static final Bus INSTANCE = new Bus();
        }

        public static Bus getInstance() {
            return Singleton.INSTANCE;
        }

        private Bus() {}

        public void register() {

        }
        public void unregister() {

        }
        public void event() {

        }
    }
}
