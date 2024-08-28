package com.wit.example.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationUtil {

    private FusedLocationProviderClient fusedLocationClient;

    public LocationUtil(FusedLocationProviderClient fusedLocationClient) {
        this.fusedLocationClient = fusedLocationClient;
    }

    public void getCurrentLocation(final LocationCallbackListener listener) {
        if (ActivityCompat.checkSelfPermission(listener.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(listener.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ha nincs engedély, akkor kérjük
            ActivityCompat.requestPermissions(listener.getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            return;
        }

        // Lekérés az utolsó ismert helyről
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String locationString = latitude + "$" + longitude;

                            // Visszatérés a helyszínnel
                            listener.onLocationReceived(locationString);
                        } else {
                            // Ha nincs elérhető helyzet
                            listener.onLocationReceived(null);
                        }
                    }
                });
    }

    public interface LocationCallbackListener {
        void onLocationReceived(String location);
        Activity getContext();
    }
}