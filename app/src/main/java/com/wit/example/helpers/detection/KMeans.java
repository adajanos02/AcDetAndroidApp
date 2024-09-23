package com.wit.example.helpers.detection;

import java.util.ArrayList;
import java.util.List;

public class KMeans {
    private static final int MAX_ITERATIONS = 100;
    private int k;
    private List<AccelerationData> data;
    private List<float[]> centroids;
    private int[] labels;

    public KMeans(int k, List<AccelerationData> data) {
        this.k = k;
        this.data = data;
        this.centroids = new ArrayList<>();
        this.labels = new int[data.size()];
    }

    public void fit() {
        initializeCentroids();

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            boolean changed = assignClusters();
            if (!changed) break; // Stop if no changes
            updateCentroids();
        }
    }

    private void initializeCentroids() {
        for (int i = 0; i < k; i++) {
            centroids.add(data.get(i).toArray());
        }
    }

    private boolean assignClusters() {
        boolean changed = false;
        for (int i = 0; i < data.size(); i++) {
            float[] point = data.get(i).toArray();
            int closestCentroid = findClosestCentroid(point);
            if (labels[i] != closestCentroid) {
                labels[i] = closestCentroid;
                changed = true; // A cluster változott
            }
        }
        return changed;
    }

    private int findClosestCentroid(float[] point) {
        int closest = 0;
        double minDistance = calculateDistance(point, centroids.get(0));

        for (int i = 1; i < centroids.size(); i++) {
            double distance = calculateDistance(point, centroids.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closest = i;
            }
        }
        return closest;
    }

    private double calculateDistance(float[] point, float[] centroid) {
        return Math.sqrt(Math.pow(point[0] - centroid[0], 2) +
                Math.pow(point[1] - centroid[1], 2) +
                Math.pow(point[2] - centroid[2], 2));
    }

    private void updateCentroids() {
        float[][] newCentroids = new float[k][3];
        int[] counts = new int[k];

        for (int i = 0; i < data.size(); i++) {
            int cluster = labels[i];
            float[] point = data.get(i).toArray();
            newCentroids[cluster][0] += point[0];
            newCentroids[cluster][1] += point[1];
            newCentroids[cluster][2] += point[2];
            counts[cluster]++;
        }

        for (int i = 0; i < k; i++) {
            if (counts[i] > 0) {
                newCentroids[i][0] /= counts[i];
                newCentroids[i][1] /= counts[i];
                newCentroids[i][2] /= counts[i];
            }
            centroids.set(i, newCentroids[i]);
        }
    }

    public int[] getLabels() {
        return labels;
    }
}
