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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClockActivity extends AppCompatActivity {

    private static final String INDEX = ":";
    private static final String TIME_INIT = "--";

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, ClockActivity.class));
    }

    private TextView hours, minutes, seconds, hoursIndex, minutesIndex;
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
        toggleVisible(hoursIndex, minutesIndex);
        mHandler.postDelayed(this::work, 500);
    }

    private void updateUI(Date time) {
        hours.setText(format(time.getHours()));
        minutes.setText(format(time.getMinutes()));
        seconds.setText(format(time.getSeconds()));
    }

    private String format(int t) {
        if (t < 10) return String.format(Locale.getDefault(), "0%d", t);
        return String.valueOf(t);
    }

    private void toggleVisible(View... views) {
        for (View view : views) {
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void initViews() {
        (hours = findViewById(R.id.txt_hour)).setText(TIME_INIT);
        (hoursIndex = findViewById(R.id.txt_hour_split)).setText(INDEX);
        (minutes = findViewById(R.id.txt_minutes)).setText(TIME_INIT);
        (minutesIndex = findViewById(R.id.txt_minutes_split)).setText(INDEX);
        (seconds = findViewById(R.id.txt_second)).setText(TIME_INIT);
    }
}
