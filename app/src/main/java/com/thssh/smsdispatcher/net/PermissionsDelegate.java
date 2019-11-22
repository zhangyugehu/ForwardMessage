package com.thssh.smsdispatcher.net;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsDelegate {

    public interface Callback {
        void onPermissionGranted(int requestCode, String permission);
        void onPermissionDenied(int requestCode, String permission);
    }
    private Activity activity;
    private Callback callback;
    private int requestCode;

    public PermissionsDelegate(int requestCode, Activity activity, Callback callback) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.callback = callback;
    }

    public void require(String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            callback.onPermissionGranted(requestCode, permission);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                Toast.makeText(activity, "shouldShowRequestPermissionRationale", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != this.requestCode) return;
        if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callback.onPermissionGranted(requestCode, permissions[0]);
        } else {
            callback.onPermissionDenied(requestCode, permissions[0]);
        }
    }
}
