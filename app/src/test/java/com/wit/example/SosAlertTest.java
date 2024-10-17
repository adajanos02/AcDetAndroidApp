package com.wit.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.wit.example.activities.StartRideActivity;
import com.wit.example.helpers.AlertSendManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class SosAlertTest {

//    @Mock
//    FusedLocationProviderClient fusedLocationClient;
//
//    @Mock
//    LocationRequest locationRequest;
//
//    @Mock
//    LocationCallback locationCallback;
//
//    @Mock
//    AlertSendManager sendManager;
//
//    @Mock
//    Location location;
//
//    @InjectMocks
//    StartRideActivity myActivity;
//
//    @Before
//    public void setup() {
//        // Valódi LocationRequest példány létrehozása a teszt során
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        fusedLocationClient = Mockito.mock(FusedLocationProviderClient.class);
//        locationCallback = Mockito.mock(LocationCallback.class);
//        sendManager = Mockito.mock(AlertSendManager.class);
//
//        myActivity = Mockito.spy(new StartRideActivity());
//    }
//
//
//    @Test
//    public void testSosAlert_withValidLocation() {
//        // Mock-oljuk az engedély meglétét
//        Mockito.doReturn(PackageManager.PERMISSION_GRANTED)
//                .when(myActivity).checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//
//        // Mock-oljuk a visszaadott helyadatokat
//        Location mockLocation = Mockito.mock(Location.class);
//        Mockito.when(mockLocation.getLatitude()).thenReturn(40.7128);
//        Mockito.when(mockLocation.getLongitude()).thenReturn(-74.0060);
//
//        // Szimuláljuk a locationResult-ot a mock helyadatokkal
//        LocationResult locationResult = Mockito.mock(LocationResult.class);
//        Mockito.when(locationResult.getLocations()).thenReturn(Collections.singletonList(mockLocation));
//
//        // Manuálisan meghívjuk a callback-et
//        myActivity.sosAlert();
//        locationCallback.onLocationResult(locationResult);
//
//        // Ellenőrizzük, hogy a sendManager metódusai a megfelelő paraméterekkel lettek-e meghívva
//        Mockito.verify(sendManager).userAlert(40.7128, -74.0060);
//        Mockito.verify(sendManager).sendSmsToContacts(40.7128, -74.0060);
//    }
//
//    @Test
//    public void testSosAlert_withoutPermissions() {
//        // Mock-oljuk, ha az engedélyek nincsenek megadva
//        Mockito.doReturn(PackageManager.PERMISSION_DENIED)
//                .when(myActivity).checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//
//        // Meghívjuk a metódust
//        myActivity.sosAlert();
//
//        // Ellenőrizzük, hogy az engedélykérést meghívták-e
//        Mockito.verify(myActivity).requestPermissions(
//                Mockito.any(String[].class), Mockito.eq(1001));
//    }
}

