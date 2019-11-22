package com.thssh.smsdispatcher.settings;

import java.util.Collection;
import java.util.Set;

public interface Settings {

    void addInclude(String packageName);
    void addAllInclude(Collection<String> list, boolean clear);
    void removeInclude(String packageName);
    Set<String> getIncludeSet();

    void addExclude(String packageName);
    void addAllExclude(Collection<String> list, boolean clear);
    void removeExclude(String packageName);
    Set<String> getExcludeSet();


    void setAppKey(String key);
}
