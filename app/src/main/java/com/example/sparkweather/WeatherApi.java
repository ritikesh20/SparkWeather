package com.example.sparkweather;


import com.example.sparkweather.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("weather")
    Call<WeatherResponse> getWeatherData(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );


    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

}

