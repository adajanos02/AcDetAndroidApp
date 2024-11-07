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

        Location mockLocation = mock(Location.class);
        when(mockLocation.getLatitude()).thenReturn(47.499);
        when(mockLocation.getLongitude()).thenReturn(19.402);

        Task<Location> locationTask = mock(Task.class);
        when(locationTask.isSuccessful()).thenReturn(true);
        when(locationTask.getResult()).thenReturn(mockLocation);

        when(locationProviderClient.getLastLocation()).thenReturn(locationTask);

        Location retrievedLocation = locationTask.getResult();
        assert retrievedLocation != null;
        assert retrievedLocation.getLatitude() == 47.499;
        assert retrievedLocation.getLongitude() == 19.402;
    }
}

