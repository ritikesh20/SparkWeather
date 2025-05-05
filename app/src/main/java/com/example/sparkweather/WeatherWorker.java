package com.example.sparkweather;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sparkweather.model.WeatherResponse;
import com.example.sparkweather.room.WeatherDatabase;
import com.example.sparkweather.room.WeatherEntity;

import retrofit2.Response;

public class WeatherWorker extends Worker {

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String city = getInputData().getString("City_Name");

        if (city == null || city.isEmpty()) {
            return Result.failure();
        }

        WeatherApi weatherApi = ApiClient.getClient().create(WeatherApi.class);

        try {
            String API_KEY = "d4f89a278f449917407116ed2c070315";
            Response<WeatherResponse> response = weatherApi.getWeatherData(city, API_KEY, "metric").execute();

            if (response.isSuccessful() && response.body() != null) {


                WeatherResponse weather = response.body();

                WeatherDatabase db = WeatherDatabase.getInstance(getApplicationContext());

//                WeatherEntity entity = new WeatherEntity(weather.getName(), weather.getMain().getTemp(), "BlueSky");

//                db.weatherDao().insert(entity);

                showNotification(weather.getName(), weather.getMain().getTemp());

            } else {
                return Result.retry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
        return Result.success();

    }

    public void showNotification(String title, double temp) {
        Context context = getApplicationContext();
        String channelId = "Weather_channel";

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Weather Updates", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Refresh Weathers");
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText("Current Temp : " + (int) temp + "°C")
                .setSmallIcon(R.drawable.sunset)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

}
