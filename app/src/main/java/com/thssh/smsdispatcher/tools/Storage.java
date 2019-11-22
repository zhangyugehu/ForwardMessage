package com.thssh.smsdispatcher.tools;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.contracts.StorageKey;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Storage {
    private static Storage ins;
    public static Storage getIns() {
        if (null == ins) {
            synchronized (Storage.class) {
                if (null == ins) {
                    ins = new Storage();
                }
            }
        }
        return ins;
    }

    private Storage() { }

    private static final String NAME_DEFAULT = "def_config";
    private static final String NAME_INCLUDE = "include_pkg";

    private Preferences mDefault;
    private Preferences mIncludePkg;

    private Preferences getDefault() {
        if (mDefault == null) {
            mDefault = new Preferences(App.getAppContext(), NAME_DEFAULT);
        }
        return mDefault;
    }

    private Preferences getIncludePkg() {
        if (mIncludePkg == null) {
            mIncludePkg = new Preferences(App.getAppContext(), NAME_INCLUDE);
        }
        return mIncludePkg;
    }

    private static final Set<String> EMPTY_INCLUDES = new HashSet<>();
    private Set<String> mIncludesCache;
    public void addAllInclude(Collection<String> includes) {
        if (mIncludesCache == null) {
            mIncludesCache = new HashSet<>();
        } else {
            mIncludesCache.clear();
            mIncludesCache.addAll(includes);
        }
        getIncludePkg().save(StorageKey.KEY_INCLUDE_PKG, mIncludesCache);
    }
    public void addInclude(String include) {
        if (mIncludesCache == null) {
            mIncludesCache = new HashSet<>();
        }
        mIncludesCache.add(include);
        getIncludePkg().save(StorageKey.KEY_INCLUDE_PKG, mIncludesCache);
    }
    public Set<String> getAllInclude() {
        if (mIncludesCache == null) {
            mIncludesCache = getIncludePkg().restore(StorageKey.KEY_INCLUDE_PKG, EMPTY_INCLUDES);
        }
        return mIncludesCache;
    }
}
