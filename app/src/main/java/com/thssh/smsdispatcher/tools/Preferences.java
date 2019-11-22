package com.thssh.smsdispatcher.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class Preferences {
    private static final String NAME = "common_config";

    private SharedPreferences mPreferences;

    public Preferences(Context context, String name) {
        mPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void save(String key, String value) {
        if (mPreferences == null) return;
        mPreferences.edit().putString(key, value).apply();
    }
    public String restore(String key, String defVal) {
        if (mPreferences == null) return defVal;
        return mPreferences.getString(key, defVal);
    }

    public void save(String key, float value) {
        if (mPreferences == null) return;
        mPreferences.edit().putFloat(key, value).apply();
    }
    public float restore(String key, float defVal) {
        if (mPreferences == null) return defVal;
        return mPreferences.getFloat(key, defVal);
    }

    public void save(String key, int value) {
        if (mPreferences == null) return;
        mPreferences.edit().putInt(key, value).apply();
    }
    public int restore(String key, int defVal) {
        if (mPreferences == null) return defVal;
        return mPreferences.getInt(key, defVal);
    }

    public void save(String key, long value) {
        if (mPreferences == null) return;
        mPreferences.edit().putLong(key, value).apply();
    }
    public long restore(String key, long defVal) {
        if (mPreferences == null) return defVal;
        return mPreferences.getLong(key, defVal);
    }

    public void save(String key, Set<String> value) {
        if (mPreferences == null) return;
        mPreferences.edit().putStringSet(key, value).apply();
    }
    public Set<String> restore(String key, Set<String> defVal) {
        if (mPreferences == null) return defVal;
        return mPreferences.getStringSet(key, defVal);
    }
}
