package com.wit.example.helpers;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class DistanceCalculator {
    static double rad = Math.PI / 180.0;

    public double greatCircleInKilometers(double lat1, double long1, double lat2, double long2) {
        double n1 = lat1 * rad;
        double n2 = lat2 * rad;
        double n3 = long1 * rad;
        double n4 = long2 * rad;

        return 6371.01 * acos(sin(n1) * sin(n2) + cos(n1) * cos(n2) * cos(n4 - n3));
    }
}
