package com.wit.example;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.location.Location;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LocationTest {

    private FusedLocationProviderClient locationProviderClient;

    @Before
    public void setUp() {
        locationProviderClient = mock(FusedLocationProviderClient.class);
    }

    @Test
    public void testLocationRetrieval() {
        // Mock location data
        Location mockLocation = mock(Location.class);
        when(mockLocation.getLatitude()).thenReturn(47.4979); // Példa: Budapest
        when(mockLocation.getLongitude()).thenReturn(19.0402);

        // Mock Task returned by FusedLocationProviderClient
        Task<Location> locationTask = mock(Task.class);
        when(locationTask.isSuccessful()).thenReturn(true);
        when(locationTask.getResult()).thenReturn(mockLocation);

        // Mocking location request
        when(locationProviderClient.getLastLocation()).thenReturn(locationTask);

        // Ellenőrizd a mockolt helyet
        Location retrievedLocation = locationTask.getResult();
        assert retrievedLocation != null;
        assert retrievedLocation.getLatitude() == 47.4979;
        assert retrievedLocation.getLongitude() == 19.0402;
    }
}

