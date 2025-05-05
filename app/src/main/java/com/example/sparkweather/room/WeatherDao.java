package com.example.sparkweather.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherEntity weather);

    @Query("SELECT * FROM weatherx ORDER BY id DESC")
    List<WeatherEntity> getAllWeather();

    @Query("DELETE FROM weatherx")
    void deleteAll();

    @Query("SELECT EXISTS(SELECT 1 FROM Weatherx WHERE cityName = :cityName)")
    boolean exists(String cityName);

}

