package com.wit.example.helpers;

import static io.realm.Realm.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import com.wit.example.activities.StartRideActivity;

public class Countdown implements Runnable{

    public interface OnCountdownFinishListener {
        void onCountdownFinish();
    }
    private OnCountdownFinishListener onCountdownFinishListener;
    public int seconds;
    StartRideActivity sra;
    public Countdown(int seconds, StartRideActivity sra) {
        this.seconds = seconds;
        this.sra = sra;
    }
    public Countdown(int seconds, OnCountdownFinishListener listener) {
        this.seconds = seconds;
        this.onCountdownFinishListener = listener;
    }
    private static final boolean ENABLE_LOGGING = false;

    public static void log(String tag, String message) {
        if (ENABLE_LOGGING) {
            Log.v(tag, message);
        }
    }

    @Override
    public void run() {
        while (seconds > 0) {
            log("COUNTDOWN", "seconds remaining: " + seconds);
            seconds--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }

        if (onCountdownFinishListener != null) {
            onCountdownFinishListener.onCountdownFinish();
        }

    }
}
