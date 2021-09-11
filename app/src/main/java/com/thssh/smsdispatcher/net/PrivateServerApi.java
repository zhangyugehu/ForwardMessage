package com.thssh.smsdispatcher.net;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;

import com.thssh.smsdispatcher.activity.AppStartActivity;
import com.thssh.smsdispatcher.manager.ReportManager;
import com.thssh.smsdispatcher.manager.TokenManager;
import com.thssh.smsdispatcher.model.Message;
import com.thssh.smsdispatcher.model.ResponseCard;
import com.thssh.smsdispatcher.tools.Route;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class PrivateServerApi extends ApiWithClient {

    private static final String TAG = "PrivateServerApi";

    private static final String FORMAT_URL = "http://api.thssh.tech:8000%s";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Override
    public void sendMessage(Message message) {
        getClient().newCall(new Request.Builder()
                .post(new FormBody.Builder()
                        .add("title", message.getTitle())
                        .add("message", message.getMessage())
                        .add("packageName", message.getPackageName())
                        .add("phone", message.getPhone())
                        .add("timestamp", String.valueOf(message.getTimestamp()))
                        .build())
                .url(String.format(FORMAT_URL, "/notification"))
                .build()
        ).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                reportFailure(message, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException  {
                if (response.isSuccessful()) {
                    ResponseCard responseCard = ResponseCard.fromJson(response.body().string());
                    if (responseCard.isTokenError()) {
                        TokenManager.getInstance().setToken(null);
                        Route.getInstance().push(Route.Name.START, null, Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    } else if (responseCard.isSuccess()) {
                        reportSuccess(message);
                    } else {
                        reportFailure(message, new Exception(responseCard.message));
                    }
                } else {
                    reportFailure(message, new Exception(response.message()));
                }
            }
        });
    }

    @Override
    public void login(String username, String passwd, Callback callback) {
        getClient()
            .newCall(new Request.Builder()
                .post(new FormBody.Builder()
                        .add("username", username)
                        .add("passwd", passwd)
                        .build()
                )
                .url(String.format(FORMAT_URL, "/login"))
                .build()
            )
            .enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onResult(null, e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callback.onResult(ResponseCard.fromJson(response.body().string()), null);
                }
            });
    }

    private void reportFailure(Message message, Throwable e) {
        ReportManager.getInstance().report("❌", "🗂" + message.getPackageName() + "👉" + message.getTitle() + " 📝" + thinString(message.getMessage()) + "‼️" + e.getMessage());
    }

    private void reportSuccess(Message message) {
        ReportManager.getInstance().report("✅", "🗂" + message.getPackageName() + "👉" + message.getTitle() + " 📝" + thinString(message.getMessage()));
    }

    private String thinString(String fatString) {
        if (TextUtils.isEmpty(fatString)) {
            return "No Content!!!";
        } else {
            int exceptSize = 15;
            if (fatString.length() < exceptSize) {
                return fatString;
            }
            return fatString.substring(0, exceptSize);
        }
    }
}
