package com.thssh.smsdispatcher.net;

import com.thssh.smsdispatcher.model.Message;
import com.thssh.smsdispatcher.model.ResponseCard;

public interface Api {

    interface Callback {
        void onResult(ResponseCard result, Throwable t);
    }

    void sendMessage(Message message);

    void login(String username, String passwd, Callback callback);
}
