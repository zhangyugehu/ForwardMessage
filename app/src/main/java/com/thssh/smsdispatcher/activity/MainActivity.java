package com.thssh.smsdispatcher.activity;


import android.Manifest;
import android.content.ClipData;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.model.AppManager;
import com.thssh.smsdispatcher.model.service.NotificationWatcherService;
import com.thssh.smsdispatcher.tools.Storage;
import com.thssh.smsdispatcher.tools.Util;

import java.util.Set;

public class MainActivity extends PermissionsActivity {

    abstract class InputCallback {
        abstract void onResult(String text);
        void onCancel() {}
    }

    private static final int ID_APP_KEY = 0;
    private static final int ID_PACKAGE_ACTIVITY = 1;

    private TextView mLogTxt;

    @Override
    int contentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int requestCode() {
        return 0x001;
    }

    @Override
    String[] requestPermissions() {
        return new String[] {
                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
        };
    }

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean isFinished) {
        super.onCreate(savedInstanceState, isFinished);
        Log.d(TAG, "onCreate: ");
        mLogTxt = findViewById(R.id.tv_log);
        if (!Util.isNotificationListenersEnabled(this)) {
            Toast.makeText(this, "没权限", Toast.LENGTH_LONG).show();
            Util.gotoNotificationAccessSetting(this);
            return;
        }
        startListenService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        print(AppManager.getInstance().getAppKey(), Storage.getIns().getAllInclude(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ID_APP_KEY, 0, "AppKey");
        menu.add(0, ID_PACKAGE_ACTIVITY, 0, "通知列表");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case ID_APP_KEY:
                setAppKey();
                break;
            case ID_PACKAGE_ACTIVITY:
                PackagesActivity.start(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startListenService() {
        startListenService(null);
    }
    private void startListenService(String appKey) {
        if (TextUtils.isEmpty(appKey)) {
            appKey = AppManager.getInstance().getAppKey();
        }
        if (TextUtils.isEmpty(appKey)) {
            setAppKey();
            return;
        }
//        mLogTxt.setText("当前AppKey: \r\n" + appKey);
        Log.d(TAG, "startListenService: startService");
        NotificationWatcherService.start(this);
        Log.d(TAG, "startListenService: service started");
    }

    private void print(String appKey, Set<String> includes, Set<String> excludes) {
        if (TextUtils.isEmpty(appKey)) {
            mLogTxt.setText("未设置AppKey\n");
        } else {
            mLogTxt.setText(String.format("AppKey: %s\n", appKey));
        }
        if (includes != null && includes.size() > 0) {
            mLogTxt.append("\n需要转发的App\n");
            for (String pkg: includes) {
                mLogTxt.append(pkg);
                mLogTxt.append("\n");
            }
        }
        if (excludes != null && excludes.size() > 0) {
            mLogTxt.append("\n不转发的App\n");
            for (String pkg: excludes) {
                mLogTxt.append(pkg);
                mLogTxt.append("\n");
            }
        }
    }

    private void setAppKey() {
        waitUserInput(new InputCallback() {
            @Override
            void onResult(String text) {
                AppManager.getInstance().saveAppkey(text);
                startListenService(text);
            }
        });
    }

    private void waitUserInput(final InputCallback callback) {
        if (callback == null) return;
//        if (dialog != null && dialog.isShowing()) dialog.dismiss();

        final EditText et = new EditText(this);
        ClipData clipData = AppManager.getInstance().getClipboardManager(this).getPrimaryClip();
        if (null != clipData) {
            et.setText(clipData.getItemAt(0).getText());
        }
        new AlertDialog.Builder(this)
                .setTitle("输入AppKey")
                .setView(et)
                .setNegativeButton("取消", (dialog, which) -> {
                    callback.onCancel();
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    callback.onResult(et.getText().toString());
                    dialog.dismiss();
                })
                .create().show();
    }

    @Override
    public void onPermissionGranted(int requestCode, String permission) {

    }

    @Override
    public void onPermissionDenied(int requestCode, String permission) {
//        Toast.makeText(this, "没有权限哦", Toast.LENGTH_LONG).show();
    }
}
