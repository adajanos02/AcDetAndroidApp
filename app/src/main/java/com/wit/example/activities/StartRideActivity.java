package com.wit.example.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.wit.example.R;
import com.wit.example.helpers.AlertSendManager;
import com.wit.example.helpers.Countdown;
import com.wit.example.helpers.DistanceCalculator;
import com.wit.example.helpers.MongoDbInitializer;
import com.wit.example.helpers.SmsHelper;
import com.wit.witsdk.modular.sensor.device.exceptions.OpenDeviceException;
import com.wit.witsdk.modular.sensor.example.ble5.Bwt901ble;
import com.wit.witsdk.modular.sensor.example.ble5.interfaces.IBwt901bleRecordObserver;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.BluetoothBLE;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.BluetoothSPP;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.WitBluetoothManager;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.exceptions.BluetoothBLEException;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.interfaces.IBluetoothFoundObserver;
import com.wit.witsdk.modular.sensor.modular.processor.constant.WitSensorKey;

import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;


public class StartRideActivity extends AppCompatActivity implements IBluetoothFoundObserver, IBwt901bleRecordObserver {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private static final String TAG = "MainActivity";
    private List<Bwt901ble> bwt901bleList = new ArrayList<>();
    public boolean destroyed = true;
    public boolean accidentHappend = false;
    public static SmsHelper smsHelper = new SmsHelper();
    MongoDbInitializer mongodb = new MongoDbInitializer();
    AlertSendManager sendManager = new AlertSendManager(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "Location");
        smsHelper.smsTextBuilder(mongoCollection);

        startLocationUpdates();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        WitBluetoothManager.initInstance(this);
        if (!bwt901bleList.isEmpty()) {
            refreshDataTh();
        }

        // 开始搜索按钮
        Button startSearchButton = findViewById(R.id.startSearchButton);
        startSearchButton.setOnClickListener((v) -> {
            startDiscovery();

        });

        // 停止搜索按钮
        Button stopSearchButton = findViewById(R.id.stopSearchButton);
        stopSearchButton.setOnClickListener((v) -> {
            stopDiscovery();
            stopLocationUpdates();
            destroyed = true;
        });

        // 自动刷新数据线程
        Thread thread = new Thread(this::refreshDataTh);
        destroyed = false;
        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void startDiscovery() {

        // 关闭所有设备
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            bwt901ble.removeRecordObserver(this);
            bwt901ble.close();
        }

        bwt901bleList.clear();

        try {
            WitBluetoothManager bluetoothManager = WitBluetoothManager.getInstance();
            bluetoothManager.registerObserver(this);
            bluetoothManager.startDiscovery();
        } catch (BluetoothBLEException e) {
            e.printStackTrace();
        }
    }

    public void stopDiscovery() {
        try {
            WitBluetoothManager bluetoothManager = WitBluetoothManager.getInstance();
            bluetoothManager.removeObserver(this);
            bluetoothManager.stopDiscovery();
        } catch (BluetoothBLEException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFoundBle(BluetoothBLE bluetoothBLE) {
        Bwt901ble bwt901ble = new Bwt901ble(bluetoothBLE);

        for (int i = 0; i < bwt901bleList.size(); i++) {
            if (Objects.equals(bwt901bleList.get(i).getDeviceName(), bwt901ble.getDeviceName())) {
                return;
            }
        }

        bwt901bleList.add(bwt901ble);

        bwt901ble.registerRecordObserver(this);

        try {
            bwt901ble.open();
        } catch (OpenDeviceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFoundSPP(BluetoothSPP bluetoothSPP) {
    }

    @Override
    public void onRecord(Bwt901ble bwt901ble) {
        String deviceData = getDeviceData(bwt901ble);
        Log.d(TAG, "device data [ " + bwt901ble.getDeviceName() + "] = " + deviceData);
    }

    private void refreshDataTh() {

        while (!destroyed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < bwt901bleList.size(); i++) {
                Bwt901ble bwt901ble = bwt901bleList.get(i);
                String deviceData = getDeviceData(bwt901ble);
                text.append(deviceData);
            }

            TextView deviceDataTextView = findViewById(R.id.deviceDataTextView);
            runOnUiThread(() -> {
                deviceDataTextView.setText(text.toString());
            });
        }
    }

    private boolean inFallAngelInterval(double xplus, double xminus, double currentAngelX) {
        if (currentAngelX >= xminus && currentAngelX <= xplus) {
            return false;
        } else {
            return true;
        }
    }

    public void accidentHappendListener() {
        if (accelerationFlag) {

            Countdown countdown = new Countdown(10, new Countdown.OnCountdownFinishListener() {
                @Override
                public void onCountdownFinish() {
                    Intent intent = new Intent(StartRideActivity.this, SosAlertActivity.class);
                    startActivity(intent);
                    sosAlert();
                }
            });
            thread = new Thread(countdown);
            thread.start();

            Intent intent = new Intent(StartRideActivity.this, AreYouOkayCheckActivity.class);
            startActivity(intent);

        }
    }

    public void fakeAccidentListener() {
        if (!thread.isInterrupted()) {
            thread.interrupt();
            timer.seconds = 10;
            accidentHappend = false;
        }
    }
    double data;
    double min = 0;
    private String getDeviceData(Bwt901ble bwt901ble) {

        List<Double> list = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        if (bwt901ble.getDeviceData(WitSensorKey.AngleX) != null) {
            if (firstAngelData) {
                angleThreshold1 = Double.parseDouble(bwt901ble.getDeviceData(WitSensorKey.AngleX).replaceAll(",", ".")) + 70;
                angleThreshold2 = Double.parseDouble(bwt901ble.getDeviceData(WitSensorKey.AngleX).replaceAll(",", ".")) - 70;
                firstAngelData = false;
            }
            if (inFallAngelInterval(angleThreshold1, angleThreshold2, Double.parseDouble(bwt901ble.getDeviceData(WitSensorKey.AngleX).replaceAll(",", "."))) && !timerActive) {
                startTimer();
               stopLocationUpdates();
            }

            if (!inFallAngelInterval(angleThreshold1, angleThreshold2, Double.parseDouble(bwt901ble.getDeviceData(WitSensorKey.AngleX).replaceAll(",", "."))) && timerActive) {
                if (!thread.isInterrupted()) {
                    thread.interrupt();
                    timer.seconds = 10;
                    startLocationUpdates();
                    timerActive = false;
                }
            }

        }
        builder.append(bwt901ble.getDeviceName()).append("\n\n");
        if (!Objects.equals(bwt901ble.getDeviceData(WitSensorKey.AccX), null) &&
                !Objects.equals(bwt901ble.getDeviceData(WitSensorKey.AccY), null) &&
                !Objects.equals(bwt901ble.getDeviceData(WitSensorKey.AccZ), null)){
            data =  Math.round(Double.parseDouble(bwt901ble.getDeviceData(WitSensorKey.AccY).replaceAll(",", ".")) * 9.81 * 100.0) / 100.0;
            list.add(data);
            if (accelerationThreshold > data && timerActive ) {
                accelerationFlag = true;
            }
            if (data < min) {
                min = data;
            }

            builder.append(getString(R.string.accY)).append(":").append(data).append(" m/s2 \n\n");


        }
        builder.append(getString(R.string.angleX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleX)).append("° \n\n");
        builder.append(getString(R.string.angleY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleY)).append("° \n\n");
        builder.append(getString(R.string.angleZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleZ)).append("° \n\n");
        builder.append(min).append(" m/s2\n");

        builder.append(accelerationFlag);
        return builder.toString();
    }

    private void handleAppliedCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            bwt901ble.unlockReg();
            bwt901ble.appliedCalibration();
        }
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
    }

    private void handleStartFieldCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            bwt901ble.unlockReg();
            bwt901ble.startFieldCalibration();
        }
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
    }

    private void handleEndFieldCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            bwt901ble.unlockReg();
            bwt901ble.endFieldCalibration();
        }
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
    }

    private void handleReadReg03() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            int waitTime = 200;
            bwt901ble.sendProtocolData(new byte[]{(byte) 0xff, (byte) 0xAA, (byte) 0x27, (byte) 0x03, (byte) 0x00}, waitTime);
            String reg03Value = bwt901ble.getDeviceData("03");
            Toast.makeText(this, bwt901ble.getDeviceName() + " reg03Value: " + reg03Value, Toast.LENGTH_LONG).show();
        }
    }



    private boolean timerActive = false;

    private LocationManager locationManager;
    private double accelerationThreshold = -6.6;
    Countdown timer = new Countdown(10, this);
    private boolean firstAngelData = true;

    private double angleThreshold1 = 0;
    private double angleThreshold2 = 0;
    public static Thread thread;
    public static boolean accelerationFlag = false;

    private void startTimer() {
        Countdown countdown = new Countdown(10, new Countdown.OnCountdownFinishListener() {
            @Override
            public void onCountdownFinish() {
                accidentHappendListener();
            }
        });
        timerActive = true;
        // 10 másodperces számláló
        thread = new Thread(countdown);
        thread.start();

    }

    public void sosAlert() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    // Hely koordináták lekérése
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    sendManager.userAlert(latitude, longitude);
                    sendManager.sendSmsToContacts(latitude, longitude);
                    sendManager.pushAccidentInfo(latitude, longitude);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    public void startLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(20000); // 20 másodperc
        locationRequest.setFastestInterval(20000); // 20 másodperc
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    // Hely koordináták lekérése
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    if (latitude != 0 && longitude != 0) {
                        mongodb.updateLocation(latitude, longitude);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


}

