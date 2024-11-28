package com.wit.example.helpers;

import com.wit.example.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TomorrowApiService {
    @GET("v4/timelines")
    Call<WeatherResponse> getWeatherData(
            @Query("location") String location,
            @Query("fields") String fields,
            @Query("timesteps") String timesteps,
            @Query("apikey") String apikey
    );
}
