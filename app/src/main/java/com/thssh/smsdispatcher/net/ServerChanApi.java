package com.thssh.smsdispatcher.net;

import android.text.TextUtils;
import android.util.Log;

import com.thssh.smsdispatcher.exception.NoAppKeyException;
import com.thssh.smsdispatcher.manager.AppManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * By Server酱
 * http://sc.ftqq.com
 */
public class ServerChanApi extends ApiWithClient {
    private static final String TAG = "ServerChanApi";

    private String BASE_URL = "https://sc.ftqq.com/";
    private String API_SEND = ".send";

    private String getAppKey() throws NoAppKeyException {
        String appKey = AppManager.getInstance().getAppKey();
        if (TextUtils.isEmpty(appKey)) throw new NoAppKeyException();
        return appKey;
    }

    @Override
    public void sendMessage(long timestamp, String title, String content) {
        String appKey = null;
        try {
            appKey = getAppKey();
        } catch (NoAppKeyException e) {
            Log.d(TAG, "onException: " + e.getMessage());
            return;
        }
        FormBody body = new FormBody.Builder()
                .add("text", title)
                .add("desp", buildContent(title, content))
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + appKey + API_SEND)
                .post(body)
                .build();
        getClient().newCall(request).enqueue(new Callback() {
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

    private String buildContent(String title, String content) {
        return new StringBuilder("### 标题")
                .append("\r\n")
                .append(title)
                .append("\r\n")
                .append("### 内容")
                .append("\r\n")
                .append(content)
                .toString();
    }

}

