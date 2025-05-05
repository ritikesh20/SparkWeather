package com.example.sparkweather.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weatherx")
public class WeatherEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String cityName;
    public String temperature;
    public String description;
    public String max_temp;
    public String min_temp;


    public WeatherEntity(String cityName, String temperature, String description, String max_temp, String min_temp) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.description = description;
        this.max_temp = max_temp;
        this.min_temp = min_temp;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(String max_temp) {
        this.max_temp = max_temp;
    }

    public String getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(String min_temp) {
        this.min_temp = min_temp;
    }
}

