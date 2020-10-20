package com.thssh.smsdispatcher.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.thssh.smsdispatcher.net.PermissionsDelegate;

public abstract class PermissionsActivity extends AppCompatActivity implements PermissionsDelegate.Callback {


    private PermissionsDelegate permissionsDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int viewId = contentViewId();
        if (viewId != 0) {
            setContentView(viewId);
        }
        onPermissions();
        onCreate(savedInstanceState, false);
    }

    protected void requestPermissions(int requestCode, String[] permissions) {
        getPermissionDelegate().require(requestCode, permissions);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        String[] permissions = requestPermissions();
//        for (String permission: permissions) {
//            getPermissionDelegate().require(permission);
//        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState, boolean isFinished) {}

    abstract int contentViewId();
    void onPermissions() {
    }

    public PermissionsDelegate getPermissionDelegate() {
        if (permissionsDelegate == null) {
            permissionsDelegate = new PermissionsDelegate(this, this);
        }
        return permissionsDelegate;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPermissionDelegate().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
