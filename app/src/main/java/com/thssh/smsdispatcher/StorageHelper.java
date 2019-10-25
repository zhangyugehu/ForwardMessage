package com.thssh.smsdispatcher;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageHelper {
    private static final StorageHelper ourInstance = new StorageHelper();

    private static final String DEFAULT_NAME = "app_storage";

    public static StorageHelper getInstance() {
        return ourInstance;
    }

    private SharedPreferences preferences;
    private String name;

    private StorageHelper() {
        name = DEFAULT_NAME;
    }

    public SharedPreferences getPreferences() {
        if (null == preferences) {
            preferences = App.getAppContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public void saveString(String key, String value) {
        getPreferences().edit()
                .putString(key, value)
                .apply();
    }

    public String getString(String key, String defaultValue) {
        return getPreferences().getString(key, defaultValue);
    }
}
