package com.thssh.smsdispatcher.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.PackagesAdapter;
import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.Util;
import com.thssh.smsdispatcher.model.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PackagesActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, PackagesActivity.class));
    }

    private RecyclerView mPackagesList;
    private PackagesAdapter mPackagesAdapter;
    private List<AppInfo> mAppInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages);
        mAppInfoList = new ArrayList<>();
        mPackagesAdapter = new PackagesAdapter(mAppInfoList);
        mPackagesList = findViewById(R.id.list_packages);
        mPackagesList.setAdapter(mPackagesAdapter);
        mPackagesList.setLayoutManager(new LinearLayoutManager(this));
        mPackagesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Executors.newSingleThreadExecutor().execute(() -> {
            List<AppInfo> packages = Util.getPackages(App.getAppContext());
            if (isFinishing()) return;
            mPackagesList.post(() -> {
                mAppInfoList.clear();
                mAppInfoList.addAll(packages);
                mPackagesAdapter.notifyDataSetChanged();
                Toast.makeText(PackagesActivity.this, Thread.currentThread().getName() + "[size: " + mAppInfoList.size(), Toast.LENGTH_SHORT).show();
            });
        });
    }
}
