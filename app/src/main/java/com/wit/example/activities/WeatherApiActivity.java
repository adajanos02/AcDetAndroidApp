package com.wit.example.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.wit.example.R;

import org.json.JSONObject;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class  WeatherApiActivity extends AppCompatActivity {

    private TextView temperatureTextView,
            precipitationTextView,
            windSpeedTextView,
            humidityTextView,
            temperatureApparentTextView,
            visibilityTextView,
            windGustTextView  ;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        temperatureTextView = findViewById(R.id.temperatureTextView);
        precipitationTextView = findViewById(R.id.precipitationTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        temperatureApparentTextView = findViewById(R.id.temperatureApparentTextView);
        visibilityTextView = findViewById(R.id.visibilityTextView);
        windGustTextView = findViewById(R.id.windGustTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location.getLatitude() != 0) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                        fetchWeatherData(latitude, longitude);

                    } else {
                        Log.e("LocationError", "Nem sikerült a helyzet lekérés.");
                    }
                })
                .addOnFailureListener(e -> Log.e("LocationError", "Helylekérés sikertelen: " + e.getMessage()));



    }

    private void fetchWeatherData(double latitude, double longitude) {
        new Thread(() -> {
            String lat = String.valueOf(latitude).replaceAll(",", ".");
            String lon = String.valueOf(longitude).replaceAll(",", ".");
            OkHttpClient client = new OkHttpClient();
            String url = String.format(
                    "https://api.tomorrow.io/v4/weather/realtime?location=%s,%s&apikey=eQllGVYWswmSGfB96xvcHg8zIPWJUc8g",
                    lat, lon
            );
            Log.d("WeatherAPI", "Request URL: " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("accept", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject values = data.getJSONObject("values");

                    double temperature = values.getDouble("temperature");
                    double precipitationProbability = values.getDouble("precipitationProbability");
                    double humidity = values.getDouble("humidity");
                    double temperatureApparent = values.getDouble("temperatureApparent");
                    double visibility = values.getDouble("visibility");
                    double windGust = values.getDouble("windGust");
                    double windSpeed = values.getDouble("windSpeed");

                    new Handler(Looper.getMainLooper()).post(() -> {
                        temperatureTextView.setText("Hőmérséklet: " + temperature + " °C");
                        precipitationTextView.setText("Csapadék valószínűsége: " + precipitationProbability + " %");
                        windSpeedTextView.setText("Szélsebesség: " + windSpeed + " m/s");
                        humidityTextView.setText("Páratartalom: " + humidity + " %");
                        temperatureApparentTextView.setText("Hőérzet: " + temperatureApparent + " °C");
                        visibilityTextView.setText("Látótávolság: " + visibility + " m");
                        windGustTextView.setText("Széllökés: " + windGust + " m/s");

                    });
                }
            } catch (IOException | org.json.JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
