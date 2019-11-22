package com.thssh.smsdispatcher.net;

public class RemoteService implements Api {

    private Api api;

    private RemoteService() {
    }

    public Api getApi() {
        if (null == api) {
            api = new ServerChanApi();
        }
        return api;
    }

    private static RemoteService instance;
    public static RemoteService get() {
        if (null == instance) {
            synchronized (RemoteService.class) {
                if (null == instance) {
                    instance = new RemoteService();
                }
            }
        }
        return instance;
    }

    @Override
    public void sendMessage(String title, String content) {
        getApi().sendMessage(title, content);
    }
}
