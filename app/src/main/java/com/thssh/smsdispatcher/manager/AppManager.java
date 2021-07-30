package com.thssh.smsdispatcher.manager;

import android.content.ClipboardManager;
import android.content.Context;

import com.thssh.smsdispatcher.settings.CustomSettings;
import com.thssh.smsdispatcher.settings.Settings;
import com.thssh.smsdispatcher.tools.StorageHelper;

public class AppManager {
    private static AppManager ins;

    private ClipboardManager cm;
    private Settings settings;

    private AppManager() {
        settings = new CustomSettings();
    }
    public static AppManager getInstance() {
        if (null == ins) {
            synchronized (AppManager.class) {
                if (null == ins) {
                    ins = new AppManager();
                }
            }
        }
        return ins;
    }

    static class Key {
        static final String APP_KEY = "key_app_key";
    }

    public void saveAppkey(String appKey) {
        StorageHelper.getInstance().saveString(Key.APP_KEY, appKey);
    }

    public String getAppKey() {
        return StorageHelper.getInstance().getString(Key.APP_KEY, "");
    }

    public ClipboardManager getClipboardManager(Context context) {
        if (null == cm) {
            cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        return cm;
    }

    public Settings getSettings() {
        return settings;
    }
}
