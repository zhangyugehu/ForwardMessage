package com.thssh.smsdispatcher;

import android.app.Application;
import android.content.Context;

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
    }
}
