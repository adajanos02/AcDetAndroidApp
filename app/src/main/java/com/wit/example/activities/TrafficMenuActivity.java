package com.wit.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;

import io.realm.Realm;

public class TrafficMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_selector);

        Button policeBtn = findViewById(R.id.button_policeinfo);
        Button userBtn = findViewById(R.id.button_userinfo);
        Button weatherBtn = findViewById(R.id.button_weather);

        policeBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(TrafficMenuActivity.this, PoliceTrafficInfoActivity.class);
            startActivity(intent);
        });

        userBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(TrafficMenuActivity.this, TrafficInfoActivity.class);
            startActivity(intent);
        });

        weatherBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(TrafficMenuActivity.this, WeatherApiActivity.class);
            startActivity(intent);
        });

        Realm.init(getApplicationContext());
    }
}
