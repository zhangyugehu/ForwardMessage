package com.thssh.smsdispatcher.dispatcher;

import androidx.core.app.NotificationManagerCompat;

import com.thssh.smsdispatcher.App;
import com.thssh.smsdispatcher.manager.AppManager;
import com.thssh.smsdispatcher.manager.ReportManager;
import com.thssh.smsdispatcher.model.Message;
import com.thssh.smsdispatcher.net.RemoteService;
import com.thssh.smsdispatcher.settings.Settings;

import java.util.Set;

/**
 * 通用手机
 */
public abstract class CommonDispatcher implements Dispatcher {

    protected static final String EXTRA_TITLE = "android.title";
    protected static final String EXTRA_TEXT = "android.text";
    protected static final String EXTRA_SUB_TEXT = "android.subText";
    protected static final String EXTRA_INFO_TEXT = "android.infoText";

    protected static final String NO_TITLE = "no title";
    protected static final String NO_CONTENT = "no content";

    private static final String REPORT_TITLE = "Dispatcher";

    protected String beatify(String content) {
        int start = content.indexOf("[");
        int end = content.indexOf("]");
        if (start < end && start != -1) {
            return content.substring(end + 1);
        }

        return content;
    }

    protected boolean shouldDispatch(String packageName) {
        Set<String> includeSet = getSettings().getIncludeSet();
        Set<String> excludeSet = getSettings().getExcludeSet();
//        String packageName = appInfo.packageName;
        if (includeSet != null && includeSet.size() < 1
                && excludeSet != null && excludeSet.contains(packageName)) return false;
        if (includeSet != null && !includeSet.contains(packageName)) return false;
        return true;
    }

    public Settings getSettings() {
        return AppManager.getInstance().getSettings();
    }

    protected void report(String message) {
        ReportManager.getInstance().report(REPORT_TITLE, message);
    }

    protected void sendMessage(Type type, long when, String packageName, String title, String content, int notificationId) {
        if (type == Type.POST) {
            RemoteService.get().sendMessage(new Message(when, title, content, packageName));
            NotificationManagerCompat.from(App.getAppContext()).cancel(notificationId);
        }
    }
}
