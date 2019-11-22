package com.thssh.smsdispatcher.model;

import android.content.ClipboardManager;
import android.content.Context;

import com.thssh.smsdispatcher.tools.StorageHelper;

public class AppManager {
    private static AppManager ins;

    private ClipboardManager cm;

    private AppManager() {}
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
}
