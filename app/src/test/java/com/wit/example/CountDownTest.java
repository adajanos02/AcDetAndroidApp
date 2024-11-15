package com.wit.example;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wit.example.helpers.Countdown;

import org.junit.Test;

public class CountDownTest {
    @Test
    public void testCountdownCompletesWithListener() throws InterruptedException {
        Countdown.OnCountdownFinishListener listener = mock(Countdown.OnCountdownFinishListener.class);
        Countdown timer = new Countdown(5, listener);

        Thread thr = new Thread(timer);
        thr.start();

        thr.join();
        verify(listener, times(1)).onCountdownFinish();
    }


    @Test
    public void testCountdownStopsOnInterrupt() throws InterruptedException {
        Countdown.OnCountdownFinishListener listener = mock(Countdown.OnCountdownFinishListener.class);
        Countdown timer = new Countdown(5, listener);

        Thread thr = new Thread(timer);
        thr.start();

        Thread.sleep(2000);
        thr.interrupt();
        thr.join();

        verify(listener, times(0)).onCountdownFinish();
    }
}
