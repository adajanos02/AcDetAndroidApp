package com.wit.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.helpers.Countdown;

public class AreYouOkayCheckActivity extends AppCompatActivity {

    StartRideActivity sra = new StartRideActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.areyouokay_check);

        Button cancelBtn = findViewById(R.id.okaybtn);

        sra.accidentHappend = true;

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sra.fakeAccidentListener();
                sra.accidentHappend = false;
                StartRideActivity.accelerationFlag = false;
                Log.v("COUNTDOWN", "Megszakitva!");
                Intent intent = new Intent(AreYouOkayCheckActivity.this, StartRideActivity.class);
                startActivity(intent);
            }

        });




    }
}
