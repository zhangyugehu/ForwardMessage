package com.thssh.smsdispatcher;


import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends PermissionsActivity {

    abstract class InputCallback {
        abstract void onResult(String text);
        void onCancel() {}
    }

    private static final int ID_APP_KEY = 0;

    private TextView appKeyTextView;

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
        appKeyTextView = findViewById(R.id.tv_app_key);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ID_APP_KEY, 0, "AppKey");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case ID_APP_KEY:
                setAppKey();
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
        appKeyTextView.setText("当前AppKey: \r\n" + appKey);
        Log.d(TAG, "startListenService: startService");
        startService(new Intent(this, NotificationWatcherService.class));
        Log.d(TAG, "startListenService: service started");
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
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onCancel();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onResult(et.getText().toString());
                        dialog.dismiss();
                    }
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
