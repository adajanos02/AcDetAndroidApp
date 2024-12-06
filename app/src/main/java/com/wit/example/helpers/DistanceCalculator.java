package com.wit.example.helpers;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class DistanceCalculator {

    public double greatCircleInKilometers(double lat1, double long1, double lat2, double long2) {
        double la = Math.toRadians(lat2 - lat1);
        double lo = Math.toRadians(long2 - long1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.cos(rLat1) * Math.cos(rLat2) * Math.sin(la / 2) * Math.sin(la / 2) +
                Math.sin(lo / 2) * Math.sin(lo / 2);
        double n = 2 * Math.asin(Math.sqrt(a));

        return 6371.0 * n;
    }
}
