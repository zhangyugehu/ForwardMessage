package com.thssh.smsdispatcher.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.activity.AppStartActivity;
import com.thssh.smsdispatcher.activity.ClockActivity;
import com.thssh.smsdispatcher.activity.LoginActivity;
import com.thssh.smsdispatcher.activity.MainActivity;
import com.thssh.smsdispatcher.manager.ReportManager;

import java.util.HashMap;
import java.util.Map;

public class Route {
    public interface Name {
        String LOGIN = "route_login";
        String START = "route_app_start";
        String MAIN = "route_main";
        String CLOCK = "route_clock";
    }
    static final class Singleton {
        public static final Route INSTANCE = new Route();
    }

    public static Route getInstance() {
        return Singleton.INSTANCE;
    }

    private final Map<String, Class<? extends Activity>> mMap;

    private static final String TAG = "Route";
    private Route() {
        mMap = new HashMap<>();
        mMap.put(Name.START, AppStartActivity.class);
        mMap.put(Name.MAIN, MainActivity.class);
        mMap.put(Name.LOGIN, LoginActivity.class);
        mMap.put(Name.CLOCK, ClockActivity.class);
    }

    public void registerRoute(String key, Class<? extends Activity> clazz) {
        mMap.put(key, clazz);
    }

    public void push(String key, Bundle bundle, int... flags) {
        Class<? extends Activity> clazz = mMap.get(key);
        if (clazz == null) {
            ReportManager.getInstance().report(TAG, "route [" + key + "] not found!!!");
        } else {
            Intent intent = new Intent(App.getAppContext(), clazz);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (int flag : flags) {
                intent.addFlags(flag);
            }
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            App.getAppContext().startActivity(intent);
        }
    }
}
