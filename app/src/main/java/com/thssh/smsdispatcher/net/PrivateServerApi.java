package com.thssh.smsdispatcher.net;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class PrivateServerApi extends ApiWithClient {

    private static final String TAG = "PrivateServerApi";

    private static final String URL = "http://98.142.141.188:8000/notes";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Override
    public void sendMessage(long timestamp, String title, String content) {
        getClient().newCall(new Request.Builder()
                .post(new FormBody.Builder()
                        .add("text", content)
                        .add("desp", String.format("[%s|%s]", FMT.format(timestamp), title))
                        .build())
                .header("Content-Type", "application/json")
                .url(URL)
                .build()
        ).enqueue(new Callback() {
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
