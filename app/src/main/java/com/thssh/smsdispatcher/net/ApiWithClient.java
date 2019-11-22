package com.thssh.smsdispatcher.net;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public abstract class ApiWithClient implements Api {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;

    protected OkHttpClient getClient() {
        if (null == client) {
            createClient();
        }
        return client;
    }

    private synchronized void createClient() {
        if (null == client) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
        }
    }
}
