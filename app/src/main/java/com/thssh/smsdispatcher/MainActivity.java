package com.thssh.smsdispatcher;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends PermissionsActivity {

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
        if (!Util.isNotificationListenersEnabled(this)) {
            Toast.makeText(this, "没权限", Toast.LENGTH_LONG).show();
            Util.gotoNotificationAccessSetting(this);
            return;
        }
        Log.d(TAG, "onCreate: startService");
        startService(new Intent(this, NotificationWatcherService.class));
        Log.d(TAG, "onCreate: service started");
    }

    @Override
    public void onPermissionGranted(int requestCode, String permission) {

    }

    @Override
    public void onPermissionDenied(int requestCode, String permission) {
        Toast.makeText(this, "没有权限哦", Toast.LENGTH_LONG).show();
    }
}
