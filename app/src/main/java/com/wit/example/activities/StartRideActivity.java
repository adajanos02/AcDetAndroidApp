package com.wit.example.activities;



import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import com.wit.example.helpers.LocationUtil;
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


public class StartRideActivity extends AppCompatActivity implements IBluetoothFoundObserver, IBwt901bleRecordObserver, LocationUtil.LocationCallbackListener {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private static final String TAG = "MainActivity";
    private List<Bwt901ble> bwt901bleList = new ArrayList<>();

    private MongoCollection<Document> mongoCollection;

    private boolean destroyed = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //startLocationUpdates();

        WitBluetoothManager.initInstance(this);


        // 开始搜索按钮
        Button startSearchButton = findViewById(R.id.startSearchButton);
        startSearchButton.setOnClickListener((v) -> {
            //startDiscovery();
            //stopLocationUpdates();
            //userAlert("37.4219983$-122.084");
            //SmsHelper.sendSms("06309502820", "Szia");
            sosAlert();
        });

        // 停止搜索按钮
        Button stopSearchButton = findViewById(R.id.stopSearchButton);
        stopSearchButton.setOnClickListener((v) -> {
            stopDiscovery();
            stopLocationUpdates();
        });

        // 加计校准按钮
        Button appliedCalibrationButton = findViewById(R.id.appliedCalibrationButton);
        appliedCalibrationButton.setOnClickListener((v) -> {
            handleAppliedCalibration();
        });

        // 开始磁场校准按钮
        Button startFieldCalibrationButton = findViewById(R.id.startFieldCalibrationButton);
        startFieldCalibrationButton.setOnClickListener((v) -> {
            handleStartFieldCalibration();
        });

        // 结束磁场校准按钮
        Button endFieldCalibrationButton = findViewById(R.id.endFieldCalibrationButton);
        endFieldCalibrationButton.setOnClickListener((v) -> {
            handleEndFieldCalibration();
        });

        // 读取03寄存器按钮
        Button readReg03Button = findViewById(R.id.readReg03Button);
        readReg03Button.setOnClickListener((v) -> {
            handleReadReg03();
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

        // 清除所有设备
        bwt901bleList.clear();

        // 开始搜索蓝牙
        try {
            // 获得蓝牙管理器
            WitBluetoothManager bluetoothManager = WitBluetoothManager.getInstance();
            // 注册监听蓝牙
            bluetoothManager.registerObserver(this);
            // 开始搜索
            bluetoothManager.startDiscovery();
        } catch (BluetoothBLEException e) {
            e.printStackTrace();
        }
    }

    public void stopDiscovery() {
        // 停止搜索蓝牙
        try {
            // 获得蓝牙管理器
            WitBluetoothManager bluetoothManager = WitBluetoothManager.getInstance();
            // 取消注册监听蓝牙
            bluetoothManager.removeObserver(this);
            // 停止搜索
            bluetoothManager.stopDiscovery();
        } catch (BluetoothBLEException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFoundBle(BluetoothBLE bluetoothBLE) {
        // 创建蓝牙5.0传感器连接对象
        Bwt901ble bwt901ble = new Bwt901ble(bluetoothBLE);

        // 避免重复连接
        for (int i = 0; i < bwt901bleList.size(); i++) {
            if (Objects.equals(bwt901bleList.get(i).getDeviceName(), bwt901ble.getDeviceName())) {
                return;
            }
        }

        // 添加到设备列表
        bwt901bleList.add(bwt901ble);

        // 注册数据记录
        bwt901ble.registerRecordObserver(this);

        // 打开设备
        try {
            bwt901ble.open();
        } catch (OpenDeviceException e) {
            // 打开设备失败
            e.printStackTrace();
        }
    }

    @Override
    public void onFoundSPP(BluetoothSPP bluetoothSPP) {
        // 不做任何处理，这个示例程序只演示如何连接蓝牙5.0设备
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
                // 让所有设备进行加计校准
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

    private String getDeviceData(Bwt901ble bwt901ble) {
        StringBuilder builder = new StringBuilder();
        builder.append(bwt901ble.getDeviceName()).append("\n");
        builder.append(getString(R.string.accX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AccX)).append("g \t");
        builder.append(getString(R.string.accY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AccY)).append("g \t");
        builder.append(getString(R.string.accZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AccZ)).append("g \n");
        builder.append(getString(R.string.asX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AsX)).append("°/s \t");
        builder.append(getString(R.string.asY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AsY)).append("°/s \t");
        builder.append(getString(R.string.asZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AsZ)).append("°/s \n");
        builder.append(getString(R.string.angleX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleX)).append("° \t");
        builder.append(getString(R.string.angleY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleY)).append("° \t");
        builder.append(getString(R.string.angleZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleZ)).append("° \n");
        builder.append(getString(R.string.hX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.HX)).append("\t");
        builder.append(getString(R.string.hY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.HY)).append("\t");
        builder.append(getString(R.string.hZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.HZ)).append("\n");
        builder.append(getString(R.string.t)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.T)).append("\n");
        builder.append(getString(R.string.electricQuantityPercentage)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.ElectricQuantityPercentage)).append("\n");
        builder.append(getString(R.string.versionNumber)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.VersionNumber)).append("\n");
        return builder.toString();
    }

    private void handleAppliedCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // 解锁寄存器
            bwt901ble.unlockReg();
            // 发送命令
            bwt901ble.appliedCalibration();
        }
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
    }

    private void handleStartFieldCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // 解锁寄存器
            bwt901ble.unlockReg();
            // 发送命令
            bwt901ble.startFieldCalibration();
        }
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
    }

    private void handleEndFieldCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // 解锁寄存器
            bwt901ble.unlockReg();
            // 发送命令
            bwt901ble.endFieldCalibration();
        }
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
    }

    private void handleReadReg03() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // 必须使用 sendProtocolData 方法，使用此方法设备才会将寄存器值读取上来
            int waitTime = 200;
            // 发送指令的命令,并且等待200ms
            bwt901ble.sendProtocolData(new byte[]{(byte) 0xff, (byte) 0xAA, (byte) 0x27, (byte) 0x03, (byte) 0x00}, waitTime);
            // 获得寄存器03的值
            String reg03Value = bwt901ble.getDeviceData("03");
            // 如果读上来了 reg03Value 就是寄存器的值，如果没有读上来可以将 waitTime 放大,或者多读几次
            Toast.makeText(this, bwt901ble.getDeviceName() + " reg03Value: " + reg03Value, Toast.LENGTH_LONG).show();
        }
    }

    private void startLocationUpdates() {
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
                    String address = String.valueOf(latitude) + "$" + String.valueOf(longitude);
                    if (address != null) {
                        updateLocation(address);
                        //Toast.makeText(StartRideActivity.this, "Address: " + address, Toast.LENGTH_LONG).show();
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

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Teljes cím
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateLocation(String location) {
        LoginActivity.mongoClient = LoginActivity.user.getMongoClient("mongodb-atlas");
        LoginActivity.mongoDatabase = LoginActivity.mongoClient.getDatabase("User");
        mongoCollection = LoginActivity.mongoDatabase.getCollection("Location");

        //LoginActivity.user = LoginActivity.app.currentUser();


        Document query = new Document().append("userId", LoginActivity.user.getId());
        Document update = new Document().append("$set",
                new Document().append("location", location));
        Log.v("userId", LoginActivity.user.getId());


        mongoCollection.updateOne(query, update).getAsync(result -> {
            if (result.isSuccess())
            {
                Log.v("UpdateFunction","Updated Data");

            }
            else
            {
                Log.v("UpdateFunction","Error"+result.getError().toString());
            }
        });


        }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void sosAlert() {
        stopLocationUpdates();
        LocationUtil locationUtil = new LocationUtil(LocationServices.getFusedLocationProviderClient(this));
        locationUtil.getCurrentLocation(this);
    }

    private void userAlert(String location){

        LoginActivity.mongoClient = LoginActivity.user.getMongoClient("mongodb-atlas");
        LoginActivity.mongoDatabase = LoginActivity.mongoClient.getDatabase("User");
        mongoCollection = LoginActivity.mongoDatabase.getCollection("Location");

        String[] locationArray = location.split("$");
        double latitude;
        //latitude = Double.parseDouble(locationArray[0]);
        //double longitude = Double.parseDouble(locationArray[1]);


        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find().iterator();

        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                while (results.hasNext()){
                    Document currentDoc = results.next();
                    if (currentDoc.getString("location")!= null) {
                        String[] resultLocationArr = currentDoc.getString("location").split("$");
//                        float distance = calculateDistance(
//                                latitude,
//                                latitude,
//                                latitude,
//                                latitude);

                        Toast.makeText(getApplicationContext(), "valami", Toast.LENGTH_LONG).show();
                        if (true) {
                            //SmsHelper.sendSms(currentDoc.getString("phone"), "Baleset történt ezen a címen: ");
                        }
                    }

                }


            }
            else{
                Log.v("Task error",task.getError().toString());
            }
        });
    }

    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Létrehozzuk az első Location objektumot
        Location location1 = new Location("pointA");
        location1.setLatitude(lat1);
        location1.setLongitude(lon1);

        // Létrehozzuk a második Location objektumot
        Location location2 = new Location("pointB");
        location2.setLatitude(lat2);
        location2.setLongitude(lon2);

        // Kiszámoljuk a távolságot a két pont között
        return location1.distanceTo(location2);
    }


    @Override
    public void onLocationReceived(String location) {
        if (location != null) {
            String loc = "37.4219983$-122.084";
              userAlert(loc);
            Toast.makeText(getApplicationContext(), location, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Activity getContext() {
        return this;
    }
}
