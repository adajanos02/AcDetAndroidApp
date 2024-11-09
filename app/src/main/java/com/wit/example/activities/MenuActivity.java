package com.wit.example.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wit.example.R;

public class MenuActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);
        checkForSmsPermission();

        Button startRideButton = findViewById(R.id.button_start_ride);
        Button contactsButton = findViewById(R.id.button_contacts);
        Button findMeButton = findViewById(R.id.button_find_me);
        Button trafficButton = findViewById(R.id.trafficInfo);
        Button logoutButton = findViewById(R.id.logout_btn);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginActivity.user.logOut();
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });


        startRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuActivity.this, StartRideActivity.class);
                startActivity(intent);
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });

        findMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

        trafficButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TrafficMenuActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkForSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }
}
