package com.thssh.smsdispatcher.net;

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
    public void sendMessage(long timestamp, String title, String content) {
        for (Api api: apis) {
            if (null == api) continue;
            api.sendMessage(timestamp, title, content);
        }
    }
}
