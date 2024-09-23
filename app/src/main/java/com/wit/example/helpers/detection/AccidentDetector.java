package com.wit.example.helpers.detection;

import com.wit.example.helpers.detection.AccelerationData;
import com.wit.example.helpers.detection.KMeans;

import java.util.List;

public class AccidentDetector {

    private static final int CLUSTERS = 3; // Normál, Esésszerű, Esés
    private final List<AccelerationData> accelerationDataList;

    public AccidentDetector(List<AccelerationData> data) {
        this.accelerationDataList = data;
    }

    public int detectAccident() {
        KMeans kMeans = new KMeans(CLUSTERS, accelerationDataList);
        kMeans.fit();
        int[] labels = kMeans.getLabels();

        // Kiértékelés
        for (int label : labels) {
            switch (label) {
                case 0:
                    return 0;
                    //normal allapot
                case 1:
                    return 1;
                    //esesszeru allapot
                case 2:
                    return 2;
                    //eses allapot
            }
        }
        return 0;
    }
}