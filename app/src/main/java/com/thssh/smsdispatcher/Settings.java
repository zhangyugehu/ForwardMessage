package com.thssh.smsdispatcher;

import java.util.Set;

public interface Settings {

    void addInclude(String packageName);
    void removeInclude(String packageName);
    Set<String> getIncludeSet();

    void addExclude(String packageName);
    void removeExclude(String packageName);
    Set<String> getExcludeSet();


    void setAppKey(String key);
}
