package com.thssh.smsdispatcher.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Response;

public class ResponseCard {
    private static final String KEY_CODE = "code";
    private static final String KEY_DATA = "data";
    private static final String KEY_MESSAGE = "message";

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR_TOKEN = 1006;
    public int code;
    public String message;
    public final Map<String, Object> result = new HashMap<>();

    public static ResponseCard createSuccess() {
        ResponseCard card = new ResponseCard();
        card.code = CODE_SUCCESS;
        return card;
    }

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }

    public boolean isTokenError() {
        return code == CODE_ERROR_TOKEN;
    }

    public static ResponseCard fromJson(String json) {
        ResponseCard card = new ResponseCard();
        try {
            JSONObject jObj = new JSONObject(json);
            if (jObj.has(KEY_MESSAGE)) {
                card.message = jObj.getString(KEY_MESSAGE);
            }
            if (jObj.has(KEY_CODE)) {
                card.code = jObj.getInt(KEY_CODE);
            }
            if (jObj.has(KEY_DATA)) {
                JSONObject result = jObj.getJSONObject(KEY_DATA);
                for (Iterator<String> it = result.keys(); it.hasNext(); ) {
                    String key = it.next();
                    card.result.put(key, result.get(key));
                }
            }
        } catch (Exception e) {
            card.code = -1;
            card.message = e.getMessage();
        }
        return card;
    }
}
