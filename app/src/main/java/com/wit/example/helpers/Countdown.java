package com.wit.example.helpers;

import android.util.Log;

public class Countdown implements Runnable{

    public int seconds;

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
        Log.v("COUNTDOWN", "DONE!");
    }
}
