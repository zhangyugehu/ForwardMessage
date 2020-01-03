package com.thssh.smsdispatcher.settings;

import com.thssh.smsdispatcher.model.AppManager;
import com.thssh.smsdispatcher.model.Mode;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class DefaultSettings implements Settings {

    private Set<String> mInclude;
    private Set<String> mExclude;
    private int mode = Mode.SERVER_CHAN;

    public DefaultSettings() {
        this.mExclude = new TreeSet<>();
        this.mInclude = new TreeSet<>();
    }

    @Override
    public void addInclude(String packageName) {
        mInclude.add(packageName);
    }

    @Override
    public void addAllInclude(Collection<String> list, boolean clear) {
        if (clear) mInclude.clear();
        mInclude.addAll(list);
    }

    @Override
    public void removeInclude(String packageName) {
        mInclude.remove(packageName);
    }

    @Override
    public Set<String> getIncludeSet() {
        return mInclude;
    }

    @Override
    public void addExclude(String packageName) {
        mExclude.add(packageName);
    }

    @Override
    public void addAllExclude(Collection<String> list, boolean clear) {
        if (clear) mExclude.clear();
        mExclude.addAll(list);
    }

    @Override
    public void removeExclude(String packageName) {
        mExclude.remove(packageName);
    }

    @Override
    public Set<String> getExcludeSet() {
        return mExclude;
    }

    @Override
    public void setAppKey(String key) {
        AppManager.getInstance().saveAppkey(key);
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;
    }
}
