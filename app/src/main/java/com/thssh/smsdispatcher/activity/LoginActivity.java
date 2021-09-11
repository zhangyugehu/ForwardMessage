package com.thssh.smsdispatcher.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.manager.TokenManager;
import com.thssh.smsdispatcher.model.ResponseCard;
import com.thssh.smsdispatcher.net.Api;
import com.thssh.smsdispatcher.net.RemoteService;
import com.thssh.smsdispatcher.tools.Storage;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameInput, mPasswdInput;

    private ProgressDialog mLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameInput = findViewById(R.id.input_username);
        mPasswdInput = findViewById(R.id.input_passwd);
        mLoading = new ProgressDialog(this);
    }

    public void onLoginClick(View view) {
        String username = mUsernameInput.getText().toString();
        String passwd = mPasswdInput.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwd)) {
            alert("用户名/密码不能为空");
        } else {
            mLoading.setMessage("登录中...");
            mLoading.show();
            RemoteService.get().login(username, passwd, (result, t) -> runOnUiThread(() -> onLoginResult(result, t)));
        }
    }

    private void onLoginResult(ResponseCard result, Throwable t) {
        mLoading.dismiss();
        if (t != null) {
            alert("登录失败：" + t.getMessage());
        } else if (result != null && result.isSuccess()) {
            String token = parseToken(result);
            Storage.getIns().saveToken(token);
            TokenManager.getInstance().setToken(token);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            alert("登录失败");
        }
    }

    private String parseToken(ResponseCard result) {
        return (String) result.result.get("token");
    }

    private void alert(String message) {
        new AlertDialog.Builder(this).setMessage(message).show();
    }
}
