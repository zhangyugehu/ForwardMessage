package com.thssh.smsdispatcher;

public class RemoteService implements Api {

    private ApiDelegate delegate;

    private RemoteService() {
    }

    public ApiDelegate getDelegate() {
        if (null == delegate) {
            delegate = new ApiDelegate();
        }
        return delegate;
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
        getDelegate().sendMessage(title, content);
    }
}
