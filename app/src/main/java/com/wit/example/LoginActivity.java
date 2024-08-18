package com.wit.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wit.witsdk.modular.sensor.device.exceptions.OpenDeviceException;
import com.wit.witsdk.modular.sensor.example.ble5.Bwt901ble;
import com.wit.witsdk.modular.sensor.example.ble5.interfaces.IBwt901bleRecordObserver;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.BluetoothBLE;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.BluetoothSPP;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.WitBluetoothManager;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.exceptions.BluetoothBLEException;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.interfaces.IBluetoothFoundObserver;
import com.wit.witsdk.modular.sensor.modular.processor.constant.WitSensorKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;


public class LoginActivity extends AppCompatActivity{

    private EditText email;
    private EditText password;
    static String AppId = "android_project-ibyjncm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Realm.init(this);

        Button submitButton = findViewById(R.id.login_btn);
        submitButton.setOnClickListener((v) -> {
            EditText text = (EditText)findViewById(R.id.username_input);
            String username = text.getText().toString();
            EditText text2 = (EditText)findViewById(R.id.password_input);
            String password = text2.getText().toString();
            Login(username,password);
        });


    }

    public void Login(String email, String password) {

        App app = new App(new AppConfiguration.Builder(LoginActivity.AppId).build());

        Credentials credentials = Credentials.emailPassword(email, password);
        app.loginAsync(credentials, new App.Callback<io.realm.mongodb.User>() {
            @Override
            public void onResult(App.Result<io.realm.mongodb.User> result) {
                if (result.isSuccess()) {
                    Log.v("User", "Logged in successfully");
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);

                }
                else {
                    Log.v("User", "Failed to login");
                }
            }
        });
    }

}