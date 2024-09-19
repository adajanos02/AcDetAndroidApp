package com.wit.example.helpers;

import android.util.Log;

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


    public Countdown(int seconds) {
        this.seconds = seconds;
    }
    @Override
    public void run() {
        while (seconds > 0) {
            Log.v("COUNTDOWN", "seconds remaining: " + seconds);
            seconds--;
            try {
                Thread.sleep(1000); // 1 másodperc várakozás
            } catch (InterruptedException e) {
                Log.v("COUNTDOWN", "Megszakitva!");
                return;
            }
        }
        Log.v("COUNTDOWN", "ALERT SEND!");

        if (onCountdownFinishListener != null) {
            onCountdownFinishListener.onCountdownFinish();
        }
//        if (sra.accidentHappend) {
//            sra.sosAlert();
//
//        }
//        else if (StartRideActivity.accelerationFlag) {
//            sra.accidentHappend = true;
//            //sra.destroyed = true;
//            sra.accidentHappendListener();
//
//
//
//        }

    }
}
