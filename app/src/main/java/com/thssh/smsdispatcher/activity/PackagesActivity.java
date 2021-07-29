package com.thssh.smsdispatcher.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.adapter.PackagesAdapter;
import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.tools.Storage;
import com.thssh.smsdispatcher.tools.Util;
import com.thssh.smsdispatcher.model.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class PackagesActivity extends AppCompatActivity {
    static class UIHandler extends Handler {
        private boolean isDestroy;
        void onDestroy() {
            isDestroy = true;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (isDestroy) return;
            super.handleMessage(msg);
        }
    }

    private static final int ID_SAVE = 0;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PackagesActivity.class));
    }

    private TextView mLoadingView;
    private EditText mSearchInput;
    private RecyclerView mPackagesList;
    private PackagesAdapter mPackagesAdapter;
    private List<AppInfo> mAppInfoList;
    private boolean isDestroy;
    private UIHandler mHandler;
    private List<AppInfo> mAppPackages;

    public List<String> getCheckedList() {
        return mPackagesAdapter.getAllChecked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler();
        setContentView(R.layout.activity_packages);
        mLoadingView = findViewById(R.id.pkg_loading);
        mSearchInput = findViewById(R.id.input_search);
        mAppInfoList = new ArrayList<>();
        mPackagesAdapter = new PackagesAdapter(mAppInfoList);
        mPackagesList = findViewById(R.id.list_packages);
        mPackagesList.setAdapter(mPackagesAdapter);
        mPackagesList.setLayoutManager(new LinearLayoutManager(this));
        mPackagesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        initListener();
        initDate();
    }

    private String mSearchText;
    private String mLatestText;
    List<AppInfo> mSearchResult = new ArrayList<>();

    private final Runnable doSearch = () -> {
        if (!TextUtils.isEmpty(mSearchText) && !TextUtils.equals(mSearchText, mLatestText)) {
            mLatestText = mSearchText;
            mSearchResult.clear();
            for (AppInfo app : mAppPackages) {
                if (doQuery(app, mSearchText)) {
                    mSearchResult.add(app);
                }
            }
            mAppInfoList.clear();
            mAppInfoList.addAll(mSearchResult);
            mPackagesAdapter.notifyDataSetChanged();
        }
    };

    private boolean doQuery(AppInfo app, String target) {
        return app != null && !TextUtils.isEmpty(target) &&
                (app.getAppName() != null && app.getAppName().contains(target) ||
                app.getPackageName() != null && app.getPackageName().contains(target))
                ;
    }

    private void initListener() {
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchText = s.toString();
                mHandler.removeCallbacks(doSearch);
                mHandler.postDelayed(doSearch, 300);
            }
        });
    }

    private void initDate() {
        Executors.newSingleThreadExecutor().execute(() -> {
            mAppPackages = Util.getPackages(App.getAppContext());
            Set<String> includes = Storage.getIns().getAllInclude();
            List<AppInfo> checkedList = new ArrayList<>();
            for (AppInfo info : mAppPackages) {
                if (includes.contains(info.getPackageName())) {
                    checkedList.add(info);
                }
            }
            for (AppInfo info : checkedList) {
                mAppPackages.remove(info);
                mAppPackages.add(0, info);
            }
            if (isDestroy) return;
            mHandler.post(() -> {
                if (isDestroy) return;
                if (mAppPackages == null || mAppPackages.size() < 1) {
                    mLoadingView.setText("空空如也~");
                    return;
                } else if (mLoadingView.getVisibility() == View.VISIBLE){
                    mLoadingView.setVisibility(View.GONE);
                }
                mAppInfoList.clear();
                mAppInfoList.addAll(mAppPackages);
                mPackagesAdapter.notifyDataSetChanged();
//                Toast.makeText(PackagesActivity.this, Thread.currentThread().getName() + "[size: " + mAppInfoList.size(), Toast.LENGTH_SHORT).show();
            });
        });
        mPackagesAdapter.addAllCheckPackageName(Storage.getIns().getAllInclude());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ID_SAVE, 0, "保存");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case ID_SAVE:
                doSave();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Tip")
                .setMessage("是否保存修改")
                .setNegativeButton("下次再说", (dialog, i) -> {
                    dialog.dismiss();
                    super.onBackPressed();
                })
                .setPositiveButton("确定", (dialog, i) -> {
                    doSave();
                    dialog.dismiss();
                    super.onBackPressed();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.onDestroy();
        isDestroy = true;
    }

    private void doSave() {
        Storage.getIns().addAllInclude(mPackagesAdapter.getAllChecked());
    }
}
