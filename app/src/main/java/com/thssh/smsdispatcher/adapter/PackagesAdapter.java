package com.thssh.smsdispatcher.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thssh.smsdispatcher.R;
import com.thssh.smsdispatcher.model.AppInfo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.InnerViewHolder> {

    private List<AppInfo> mData;
    private List<String> mCheckPackageNames;

    public PackagesAdapter(List<AppInfo> mData) {
        this.mData = mData;
        this.mCheckPackageNames = new LinkedList<>();
    }

    public void addCheckPackageName(String pkgName) {
        if (mCheckPackageNames == null) return;
        mCheckPackageNames.add(pkgName);
        notifyItemChanged(getPositionByPkgName(pkgName));
    }

    private int getPositionByPkgName(String pkgName) {
        for (int i=0; i<mData.size()-1; i++) {
            AppInfo info = mData.get(i);
            if (TextUtils.equals(info.getPackageName(), pkgName)) return i;
        }
        return 0;
    }

    public void addAllCheckPackageName(Collection<String> list) {
        if (mCheckPackageNames == null) return;
        mCheckPackageNames.addAll(list);
        notifyDataSetChanged();
    }
    public List<String> getAllChecked() {
        return mCheckPackageNames;
    }

    @NonNull
    @Override
    public InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InnerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_packages, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        String curPkgName = mData.get(position).getPackageName();
        holder.setData(mData.get(position), mCheckPackageNames.contains(curPkgName));
        holder.setClickListener(v -> {
            if (mCheckPackageNames.contains(curPkgName)) {
                mCheckPackageNames.remove(curPkgName);
            } else {
                mCheckPackageNames.add(curPkgName);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class InnerViewHolder extends RecyclerView.ViewHolder {

        private TextView appNameTxt;
        private TextView pkgNameTxt;
        private View sysAppFlag;
        private ImageView pkgIcon;
        private CheckBox mChk;

        public InnerViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTxt = itemView.findViewById(R.id.item_app_name);
            pkgNameTxt = itemView.findViewById(R.id.item_package_name);
            sysAppFlag = itemView.findViewById(R.id.item_packages_sys_app_flag);
            pkgIcon = itemView.findViewById(R.id.item_packages_icon);
            mChk = itemView.findViewById(R.id.item_packages_chk);
        }

        public void setClickListener(View.OnClickListener l) {
            itemView.setOnClickListener(l);
        }

        public void setData(AppInfo appInfo, boolean checked) {
            appNameTxt.setText(appInfo.getAppName());
            pkgNameTxt.setText(appInfo.getPackageName());
            sysAppFlag.setVisibility(appInfo.isSysApp()?View.VISIBLE: View.INVISIBLE);
            pkgIcon.setImageDrawable(appInfo.getAppIcon());
            mChk.setChecked(checked);
        }
    }
}
