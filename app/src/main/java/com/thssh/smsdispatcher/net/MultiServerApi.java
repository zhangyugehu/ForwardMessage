package com.thssh.smsdispatcher.net;

import com.thssh.smsdispatcher.model.Message;
import com.thssh.smsdispatcher.model.ResponseCard;

import java.util.ArrayList;
import java.util.List;

public class MultiServerApi implements Api {

    private List<Api> apis;

    public MultiServerApi() {
        apis = new ArrayList<>();
        apis.add(new PrivateServerApi());
        apis.add(new ServerChanApi());
    }

    @Override
    public void sendMessage(Message message) {
        for (Api api: apis) {
            if (null == api) continue;
            api.sendMessage(message);
        }
    }

    @Override
    public void login(String username, String passwd, Callback callback) {
        for (Api api: apis) {
            if (null == api) continue;
            api.login(username, passwd, callback);
        }
    }
}
