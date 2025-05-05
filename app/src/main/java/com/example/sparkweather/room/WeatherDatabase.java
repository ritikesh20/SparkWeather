package com.example.sparkweather.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WeatherEntity.class}, version = 1)
public abstract class WeatherDatabase extends RoomDatabase {

    public abstract WeatherDao weatherDao();

    public static WeatherDatabase INSTANCE;

    public static synchronized WeatherDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, WeatherDatabase.class, "weather_dbx")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

}

