package com.thssh.smsdispatcher.settings;

import com.thssh.smsdispatcher.tools.Storage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class CustomSettings implements Settings {

    @Override
    public void addInclude(String packageName) {
        Storage.getIns().addInclude(packageName);
    }

    @Override
    public void addAllInclude(Collection<String> list, boolean clear) {
        Storage.getIns().addAllInclude(list);
    }

    @Override
    public void removeInclude(String packageName) {

    }

    @Override
    public Set<String> getIncludeSet() {
        return Storage.getIns().getAllInclude();
    }

    @Override
    public void addExclude(String packageName) {
    }

    @Override
    public void addAllExclude(Collection<String> list, boolean clear) {

    }

    @Override
    public void removeExclude(String packageName) {

    }

    @Override
    public Set<String> getExcludeSet() {
        return Collections.emptySet();
    }

    @Override
    public void setAppKey(String key) {

    }
}
