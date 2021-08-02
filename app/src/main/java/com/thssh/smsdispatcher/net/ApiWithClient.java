package com.thssh.smsdispatcher.net;

import com.thssh.smsdispatcher.model.ResponseCard;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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
                    .addInterceptor(new HeaderInterceptor())
                    .build();
        }
    }


}
