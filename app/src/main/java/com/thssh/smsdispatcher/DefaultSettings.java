package com.thssh.smsdispatcher;

import java.util.Set;
import java.util.TreeSet;

public class DefaultSettings implements Settings {

    private Set<String> mInclude;
    private Set<String> mExclude;

    public DefaultSettings() {
        this.mExclude = new TreeSet<>();
        this.mInclude = new TreeSet<>();
    }

    @Override
    public void addInclude(String packageName) {
        mInclude.add(packageName);
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
}
