package com.thssh.smsdispatcher.net;

public interface Api {

    void sendMessage(long timestamp, String title, String content);
}
