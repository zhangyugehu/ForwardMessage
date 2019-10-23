package com.thssh.smsdispatcher;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiDelegate implements Api {
    private static final String TAG = "ApiDelegate";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String BASE_URL = "https://sc.ftqq.com/";
    private OkHttpClient client;
//    https://sc.ftqq.com/SCU25792T613524a3a2b2636bcaf0f2b2cafffe755aea6d088d6b8.send

    private String API_SEND = "SCU25792T613524a3a2b2636bcaf0f2b2cafffe755aea6d088d6b8.send";

    public ApiDelegate() {
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void sendMessage(String title, String content) {
        FormBody body = new FormBody.Builder()
                .add("text", title)
                .add("desp", content)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + API_SEND)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }
}
