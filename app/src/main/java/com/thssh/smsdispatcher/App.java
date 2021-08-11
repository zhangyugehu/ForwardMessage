package com.thssh.smsdispatcher;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.Bugly;
import com.thssh.smsdispatcher.contracts.Config;

public class App extends Application {
    private static Context appContext;

    public static Context getAppContext() {
        if (null == appContext) {
            throw new RuntimeException("Application is not init");
        }
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
//        CrashReport.initCrashReport(getApplicationContext(), Config.BUGLY_APP_ID, false);
        Bugly.init(getApplicationContext(), Config.BUGLY_APP_ID, false);
    }
}
