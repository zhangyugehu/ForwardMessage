package com.thssh.smsdispatcher;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ServiceCheckWorker extends Worker {

    private static final String TAG = "ServiceCheckWorker";
    public ServiceCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d(TAG, "ServiceCheckWorker: init...");
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!NotificationWatcherService.sIsRunning) {
            Log.d(TAG, "doWork: service is not running, starting...");
            NotificationWatcherService.start(App.getAppContext());
        } else {
            Log.d(TAG, "doWork: service is running.");
        }
        return Result.success();
    }
}
