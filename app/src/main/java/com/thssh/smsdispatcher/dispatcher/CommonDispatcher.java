package com.thssh.smsdispatcher.dispatcher;

/**
 * 通用手机
 */
public abstract class CommonDispatcher implements Dispatcher {


    protected String beatify(String content) {
        int start = content.indexOf("[");
        int end = content.indexOf("]");
        if (start < end && start != -1) {
            return content.substring(end + 1);
        }

        return content;
    }
}
