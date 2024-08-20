package com.wit.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        Button startRideButton = findViewById(R.id.button_start_ride);
        Button contactsButton = findViewById(R.id.button_contacts);
        Button findMeButton = findViewById(R.id.button_find_me);
        Button sosButton = findViewById(R.id.button_sos);



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
                Intent intent = new Intent(MenuActivity.this, FindMeActivity.class);
                startActivity(intent);
            }
        });

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SOSActivity.class);
                startActivity(intent);
            }
        });
    }
}
