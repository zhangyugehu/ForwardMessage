package com.thssh.smsdispatcher.net;

import android.text.TextUtils;

import com.thssh.smsdispatcher.manager.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    private final TokenManager tokenManager;
    public HeaderInterceptor() {
        tokenManager = TokenManager.getInstance();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder().addHeader("Content-Type", "application/json");
        if (tokenManager.getToken() != null) {
            builder.addHeader(TokenManager.TOKEN_KEY, tokenManager.getToken());
        }
        Response response = chain.proceed(builder.build());
        String token = response.header(TokenManager.TOKEN_KEY);
        if (!TextUtils.isEmpty(token)) {
            tokenManager.setToken(token);
        }
        return response;
    }
}
