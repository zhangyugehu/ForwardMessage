package com.thssh.smsdispatcher.activity;


import android.Manifest;
import android.content.ClipData;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.tencent.bugly.beta.Beta;
import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.manager.AppManager;
import com.thssh.smsdispatcher.manager.ReportManager;
import com.thssh.smsdispatcher.model.Mode;
import com.thssh.smsdispatcher.model.Report;
import com.thssh.smsdispatcher.service.NotificationWatcherService;
import com.thssh.smsdispatcher.tools.Storage;
import com.thssh.smsdispatcher.tools.Util;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends PermissionsActivity implements ReportManager.Listener {

    private static final int REQUEST_CODE = 0x001;
    private static final String[] PERMISSION_NEEDED = new String[] {
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PRIVILEGED
    };

    private void report(String message) {
        ReportManager.getInstance().report(TAG, message);
    }

    private final Handler mHandler  = new Handler(Looper.getMainLooper());
    @Override
    public void onReport(List<Report> reports) {
        mReports = reports;
        mHandler.removeCallbacks(mRefreshReports);
        mHandler.postDelayed(mRefreshReports, 500);
    }

    private static final SimpleDateFormat F = new SimpleDateFormat("hh:mm:ss.sss");
    private View createItem(Report report) {
        TextView textView = new TextView(this);
        textView.setText("[" + F.format(report.timestamp) + "]" + report.title + "\r\n" + report.message);
        textView.setMinHeight(90);
        return textView;
    }

    abstract static class InputCallback {
        abstract void onResult(String text);
        void onCancel() {}
    }

    private static final int ID_APP_KEY = 0;
    private static final int ID_PACKAGE_ACTIVITY = 1;
    private static final int ID_CHANGE_MODE = 2;
    private static final int ID_CLOCK = 3;

    private TextView mLogTxt;
    private LinearLayout mScrollList;
    private ScrollView mScroll;

    private List<Report> mReports;

    final Runnable mRefreshReports = () -> {
        mScrollList.removeAllViews();
        for (Report report : mReports) {
            mScrollList.addView(createItem(report));
        }
        int offset = mScrollList.getMeasuredHeight() - mScroll.getHeight();
        mScroll.scrollTo(0, offset);
    };

    @Override
    int contentViewId() {
        return R.layout.activity_main;
    }

    @Override
    void onPermissions() {
        requestPermissions(REQUEST_CODE, PERMISSION_NEEDED);
    }

    private static final String TAG = "MainActivity";

    private boolean useServerChan() {
        return AppManager.getInstance().getSettings().getMode() == Mode.SERVER_CHAN || useServerMulti();
    }
    private boolean useServerMulti() {
        return AppManager.getInstance().getSettings().getMode() == Mode.SERVER_MULTI;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean isFinished) {
        super.onCreate(savedInstanceState, isFinished);
        mLogTxt = findViewById(R.id.tv_log);
        mScrollList = findViewById(R.id.box_list);
        mScroll = findViewById(R.id.scroll);
        ReportManager.registerOnChangeListener(this);
        if (!Util.isNotificationListenersEnabled(this)) {
            Toast.makeText(this, "没权限", Toast.LENGTH_LONG).show();
            Util.gotoNotificationAccessSetting(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLogger();
        Beta.checkUpgrade(false, false);
    }

    private void initLogger() {
        print(AppManager.getInstance().getAppKey(), Storage.getIns().getAllInclude(), null);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (useServerChan()) {
            menu.add(0, ID_APP_KEY, 0, "AppKey");
        }
        menu.add(0, ID_PACKAGE_ACTIVITY, 0, "通知列表");
        menu.add(0, ID_CHANGE_MODE, 0, "切换模式(当前:" + getModeStr() + ")");
        menu.add(0, ID_CLOCK, 0, "待机时钟");
        return super.onPrepareOptionsMenu(menu);
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
            case ID_CHANGE_MODE:
                changeMode();
                break;
            case ID_CLOCK:
                ClockActivity.start(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getModeStr() {
        int mode = AppManager.getInstance().getSettings().getMode();
        switch (mode) {
            case Mode.SERVER_CHAN:
                return "serverChan";
            case Mode.SERVER_SELF:
                return "privateServer";
            case Mode.SERVER_MULTI:
                return "Multi";
            default: return "";
        }
    }

    private void changeMode() {
        int mode = AppManager.getInstance().getSettings().getMode();
        mode ++;
        if (mode > Mode.SERVER_MULTI) mode = Mode.SERVER_CHAN;
        AppManager.getInstance().getSettings().setMode(mode);
        initLogger();
    }

    private void startListenService() {
        startListenService(null);
    }
    private void startListenService(String appKey) {
        if (useServerChan()) {
            if (TextUtils.isEmpty(appKey)) {
                appKey = AppManager.getInstance().getAppKey();
            }
            if (TextUtils.isEmpty(appKey)) {
                setAppKey();
                return;
            }
        }
//        mLogTxt.setText("当前AppKey: \r\n" + appKey);
        report("startListenService: startService");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationWatcherService.start(this);
        }
        report("startListenService: service started");
    }

    private void print(String appKey, Set<String> includes, Set<String> excludes) {
        if (useServerChan()) {
            if (TextUtils.isEmpty(appKey)) {
                mLogTxt.setText("未设置AppKey\n");
            } else {
                mLogTxt.setText(String.format("AppKey: %s\n", appKey));
            }
        } else if (useServerMulti()){
            mLogTxt.setText("同时使用多个服务器\n");
        } else {
            mLogTxt.setText("使用私有服务器\n");
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
        if (!useServerChan()) {
            Toast.makeText(this, "无需设置AppKey", Toast.LENGTH_SHORT).show();
            return;
        }
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
    public void onPermissionGranted(int requestCode, Map<String, Integer> results) {
        report("onPermissionGranted: " + results.toString());
        if (results.get(PERMISSION_NEEDED[1]) == PackageManager.PERMISSION_GRANTED) {
            startListenService();
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, Map<String, Integer> results) {
        report("onPermissionDenied: " + results.toString());
    }

    @Override
    public void onPermissionRationale(int requestCode, Map<String, Integer> results) {
        report("onPermissionRationale: " + results.toString());
    }

}
