package com.thssh.smsdispatcher.net;

import com.thssh.smsdispatcher.manager.AppManager;
import com.thssh.smsdispatcher.model.Message;
import com.thssh.smsdispatcher.model.Mode;
import com.thssh.smsdispatcher.model.ResponseCard;

public class RemoteService implements Api {

    private Api serverChanApi;
    private Api privateServerApi;
    private Api multiServerApi;

    private RemoteService() {
    }

    public Api getApi() {
        Api api = null;
        switch (getMode()) {
            case Mode.SERVER_CHAN:
                api = getServerChanAPi();
                break;
            case Mode.SERVER_SELF:
                api = getServerSelfAPi();
                break;
            case Mode.SERVER_MULTI:
                api = getServerMultiAPi();
                break;
            default: break;
        }
        return api;
    }

    private Api getServerMultiAPi() {
        if (null == multiServerApi) {
            multiServerApi = new MultiServerApi();
        }
        return multiServerApi;
    }

    private Api getServerSelfAPi() {
        if (null == privateServerApi) {
            privateServerApi = new PrivateServerApi();
        }
        return privateServerApi;
    }

    private Api getServerChanAPi() {
        if (null == serverChanApi) {
            serverChanApi = new ServerChanApi();
        }
        return serverChanApi;
    }

    private int getMode() {
        return AppManager.getInstance().getSettings().getMode();
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
    public void sendMessage(Message message) {
        getApi().sendMessage(message);
    }

    @Override
    public void login(String username, String passwd, Callback callback) {
        getApi().login(username, passwd, callback);
    }
}
