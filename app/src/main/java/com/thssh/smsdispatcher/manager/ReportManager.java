package com.thssh.smsdispatcher.manager;

import com.thssh.smsdispatcher.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportManager {
    public interface Listener {
        void onReport(List<Report> reports);
    }

    static class Singleton {
        public static final ReportManager INSTANCE = new ReportManager();
    }

    public static ReportManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static List<Listener> mChangeListeners = new ArrayList<>();

    public static void registerOnChangeListener(Listener listener) {
        if (!mChangeListeners.contains(listener)) {
            mChangeListeners.add(listener);
        }
    }

    public static void unregisterOnChangeListener(Listener listener) {
        if (mChangeListeners.contains(listener)) {
            mChangeListeners.remove(listener);
        }
    }

    private long mLimit = 10;

    private List<Report> mReports;

    private ReportManager() {
        mReports = new ArrayList<>();
    }

    public void report(String title, String message) {
        if (mReports.size() > mLimit) {
            mReports.remove(0);
        }
        mReports.add(new Report(title, message, System.currentTimeMillis()));
        for (Listener listener : mChangeListeners) {
            if (listener != null) {
                listener.onReport(mReports);
            }
        }
    }

    public List<Report> getReports() {
        return mReports;
    }
}
