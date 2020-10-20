package com.thssh.smsdispatcher.net;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.collection.ArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Map;

public class PermissionsDelegate {

    public interface Callback {
        /**
         * 权限被允许
         * @param requestCode
         * @param results
         */
        void onPermissionGranted(int requestCode, Map<String, Integer> results);

        /**
         * 权限被拒绝
         * @param requestCode
         * @param results
         */
        void onPermissionDenied(int requestCode, Map<String, Integer> results);

        /**
         * 权限被禁用
         * @param requestCode
         * @param results
         */
        void onPermissionRationale(int requestCode, Map<String, Integer> results);
    }
    private Activity activity;
    private Callback callback;
//    private int requestCode;

    public PermissionsDelegate(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void require(int requestCode, String[] permissions) {
        Map<String, Integer> grantedMap = obtainGrantedMap();
        Map<String, Integer> deniedMap = obtainDeniedMap();
        Map<String, Integer> rationaleMap = obtainRationaleMap();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                grantedMap.put(permission, result);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                rationaleMap.put(permission, result);
            } else {
                deniedMap.put(permission, result);
            }
        }
        if (!grantedMap.isEmpty()) {
            callback.onPermissionGranted(requestCode, grantedMap);
        }
        if (!deniedMap.isEmpty()) {
            ActivityCompat.requestPermissions(activity, deniedMap.keySet().toArray(new String[0]), requestCode);
        }
        if (!rationaleMap.isEmpty()) {
            callback.onPermissionRationale(requestCode, rationaleMap);
        }
    }

    private Map<String, Integer> mGrantedMap, mDeniedMap, mRationaleMap;

    private Map<String, Integer> obtainGrantedMap() {
        if (mGrantedMap == null) {
            mGrantedMap = new ArrayMap<>();
        }
        mGrantedMap.clear();
        return mGrantedMap;
    }
    private Map<String, Integer> obtainDeniedMap() {
        if (mDeniedMap == null) {
            mDeniedMap = new ArrayMap<>();
        }
        mDeniedMap.clear();
        return mDeniedMap;
    }
    private Map<String, Integer> obtainRationaleMap() {
        if (mRationaleMap == null) {
            mRationaleMap = new ArrayMap<>();
        }
        mRationaleMap.clear();
        return mRationaleMap;
    }

    private static final String TAG = "PermissionsDelegate";
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: " + permissions + "|" + grantResults);
        if (grantResults != null && grantResults.length > 0) {
            Map<String, Integer> grantedMap = obtainGrantedMap();
            Map<String, Integer> deniedMap = obtainDeniedMap();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantedMap.put(permissions[i], grantResults[i]);
                } else {
                    deniedMap.put(permissions[i], grantResults[i]);
                }
            }
            if (!grantedMap.isEmpty()) {
                callback.onPermissionGranted(requestCode, grantedMap);
            }
            if (!deniedMap.isEmpty()) {
                callback.onPermissionDenied(requestCode, deniedMap);
            }
        }
    }
}
