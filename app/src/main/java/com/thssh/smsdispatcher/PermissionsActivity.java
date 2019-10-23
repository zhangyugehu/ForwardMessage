package com.thssh.smsdispatcher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class PermissionsActivity extends AppCompatActivity implements PermissionsDelegate.Callback {


    private PermissionsDelegate permissionsDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int viewId = contentViewId();
        if (viewId != 0) {
            setContentView(viewId);
        }
        String[] permissions = requestPermissions();
        for (String permission: permissions) {
            getPermissionDelegate().require(permission);
        }
        onCreate(savedInstanceState, false);
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
    abstract int requestCode();
    abstract String[] requestPermissions();

    public PermissionsDelegate getPermissionDelegate() {
        if (permissionsDelegate == null) {
            permissionsDelegate = new PermissionsDelegate(requestCode(), this, this);
        }
        return permissionsDelegate;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPermissionDelegate().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
