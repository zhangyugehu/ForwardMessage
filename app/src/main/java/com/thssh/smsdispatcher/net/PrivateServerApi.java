package com.thssh.smsdispatcher.net;

import android.annotation.SuppressLint;
import android.util.Log;

import com.thssh.smsdispatcher.manager.ReportManager;

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

    private static final String FORMAT_URL = "http://api.thssh.tech:8000%s";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Override
    public void sendMessage(long timestamp, String title, String content) {
        getClient().newCall(new Request.Builder()
                .post(new FormBody.Builder()
                        .add("title", title)
                        .add("message", content)
                        .build())
                .header("Content-Type", "application/json")
                .url(String.format(FORMAT_URL, "/notification"))
                .build()
        ).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                ReportManager.getInstance().report("[FAILURE]sendMessage", content);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
                ReportManager.getInstance().report("[SUCCESS]sendMessage", content);
            }
        });
    }
}
