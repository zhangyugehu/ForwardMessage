package com.thssh.smsdispatcher.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.thssh.smsdispatcher.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClockActivity extends AppCompatActivity {

    private static final String INDEX = ":";
    private static final String TIME_INIT = "--";
    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("MM/dd/yyyy");
    private static final long UPDATE_STEP = 50;
    private static final Map<Integer, String> WEEK_DIC = new HashMap<>();
    static {
        WEEK_DIC.put(1, "周一");
        WEEK_DIC.put(2, "周二");
        WEEK_DIC.put(3, "周三");
        WEEK_DIC.put(4, "周四");
        WEEK_DIC.put(5, "周五");
        WEEK_DIC.put(6, "周六");
        WEEK_DIC.put(7, "周日");
    }

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, ClockActivity.class));
    }

    private TextView hours, minutes, seconds, hoursIndex, minutesIndex, microSeconds, date;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setKeepScreenOn(true);
        setContentView(R.layout.activity_clock);
        initViews();
        mHandler = new Handler(Looper.getMainLooper());
        work();
    }

    private void fullScreen() {
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
        }
    }

    private int mLastSeconds = -1;
    private void work() {
        Date time = Calendar.getInstance().getTime();
        int seconds = time.getSeconds();
        if (seconds != mLastSeconds) {
            updateUI(time);
            mLastSeconds = seconds;
        }
        setVisible(updateMicroSeconds(time), hoursIndex, minutesIndex);
        mHandler.postDelayed(this::work, UPDATE_STEP);
    }

    private boolean updateMicroSeconds(Date time) {
        int micro = (int) (time.getTime() % 1000);
        microSeconds.setText(formatMicro(micro));
        return micro >= 500;
    }

    private void updateUI(Date time) {
        hours.setText(format(time.getHours()));
        minutes.setText(format(time.getMinutes()));
        seconds.setText(format(time.getSeconds()));
        date.setText(String.format("%s %s", FORMAT_DATE.format(time), WEEK_DIC.get(time.getDay())));
    }

    private String formatMicro(int t) {
        if (t < 10) {
            return String.format(Locale.getDefault(), "00%d", t);
        }
        if (t < 100) {
            return String.format(Locale.getDefault(), "0%d", t);
        }
        return String.valueOf(t);
    }

    private String format(int t) {
        if (t < 10) {
            return String.format(Locale.getDefault(), "0%d", t);
        }
        return String.valueOf(t);
    }

    private void toggleVisible(View... views) {
        for (View view : views) {
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void setVisible(boolean visible, View... views) {
        for (View view : views) {
            view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void initViews() {
        (hours = findViewById(R.id.txt_hour)).setText(TIME_INIT);
        (hoursIndex = findViewById(R.id.txt_hour_split)).setText(INDEX);
        (minutes = findViewById(R.id.txt_minutes)).setText(TIME_INIT);
        (minutesIndex = findViewById(R.id.txt_minutes_split)).setText(INDEX);
        (seconds = findViewById(R.id.txt_second)).setText(TIME_INIT);
        (microSeconds = findViewById(R.id.txt_micro_seconds)).setText(TIME_INIT);
        (date = findViewById(R.id.txt_date)).setText("--/--/----");
    }
}
