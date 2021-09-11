package com.thssh.smsdispatcher.manager;

import com.thssh.smsdispatcher.tools.Storage;

public class TokenManager {

    public static final String TOKEN_KEY = "token";

    static class Singleton {
        public static final TokenManager INSTANCE = new TokenManager();
    }

    public static TokenManager getInstance() {
        return TokenManager.Singleton.INSTANCE;
    }

    private TokenManager() {}

    private String token;

    public String getToken() {
        if (token == null) {
            token = Storage.getIns().restoreToken();
        }
        return token;
    }

    public void setToken(String token) {
        Storage.getIns().saveToken(token);
        this.token = token;
    }
}
