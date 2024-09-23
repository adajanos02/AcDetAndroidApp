package com.wit.example.helpers.detection;

public class AccelerationData {

    private float ax;
    private float ay;
    private float az;

    public AccelerationData(float ax, float ay, float az) {
        this.ax = ax;
        this.ay = ay;
        this.az = az;
    }

    public float[] toArray() {
        return new float[]{ax, ay, az};
    }
}
