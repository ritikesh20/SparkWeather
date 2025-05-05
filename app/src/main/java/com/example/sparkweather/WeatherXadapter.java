package com.example.sparkweather;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sparkweather.room.WeatherEntity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.Calendar;
import java.util.List;

public class WeatherXadapter extends AbstractItem<WeatherXadapter, WeatherXadapter.ViewHolder> {

    private WeatherEntity weatherEntity;

    public WeatherXadapter(WeatherEntity weatherEntity) {
        this.weatherEntity = weatherEntity;
    }

    public WeatherEntity getWeatherEntity() {
        return weatherEntity;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.cityCount;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.city_list_ui;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<WeatherXadapter> {

        TextView tvCityName, tvTempWeather, tvCurrentTimeCard,
                tvMaxTempWeather, tvMinTempWeather, tvWeatherResult;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCityName = itemView.findViewById(R.id.textViewCityWeather);
            tvTempWeather = itemView.findViewById(R.id.textViewTempWeather);
            tvMaxTempWeather = itemView.findViewById(R.id.textViewMaxTempWeather);
            tvMinTempWeather = itemView.findViewById(R.id.textViewMinTempWeather);
            tvWeatherResult = itemView.findViewById(R.id.textViewWeatherConditionWeather);
            tvCurrentTimeCard = itemView.findViewById(R.id.currentTime);

        }

        @Override
        public void bindView(WeatherXadapter item, List<Object> payloads) {
            WeatherEntity entity = item.weatherEntity;

            tvCityName.setText(entity.getCityName());
            tvTempWeather.setText(entity.getTemperature() + "°");
            tvMaxTempWeather.setText(entity.getMax_temp() + "°");
            tvMinTempWeather.setText(entity.getMin_temp() + "°");
            tvWeatherResult.setText(entity.getDescription());

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR); // 12-hour format (0–11)
            int minute = calendar.get(Calendar.MINUTE);
            int am_pm = calendar.get(Calendar.AM_PM);

            String amPmStr = (am_pm == Calendar.AM) ? "AM" : "PM";

            if (hour == 0) {
                hour = 12;
            }

            String currentTime = String.format("%02d:%02d %s", hour, minute, amPmStr);

            tvCurrentTimeCard.setText(currentTime);

        }

        @Override
        public void unbindView(WeatherXadapter item) {
            tvCityName.setText(null);
            tvTempWeather.setText(null);
            tvMinTempWeather.setText(null);
            tvMaxTempWeather.setText(null);
            tvWeatherResult.setText(null);
            tvCurrentTimeCard.setText(null);
        }
    }
}


/*
//    WeatherResponse weatherResponse;
//
//    public WeatherXadapter(WeatherResponse weatherResponse) {
//        this.weatherResponse = weatherResponse;
//    }
//
//    public WeatherResponse getWeatherResponse() {
//        return weatherResponse;
//    }



//            if (item.weatherResponse.getMain() != null) {
//                tvCityName.setText(item.weatherResponse.name);
//                tvTempWeather.setText((int) item.weatherResponse.getMain().getTemp() + "°");
//                tvMinTempWeather.setText(String.valueOf(item.weatherResponse.getMain().getTemp_min()));
//                tvMaxTempWeather.setText(String.valueOf(item.weatherResponse.getMain().getTemp_max()));
//
String description = "";
//                if (item.weatherResponse.getWeather() != null && !item.weatherResponse.getWeather().isEmpty()) {
//                    description = item.weatherResponse.getWeather().get(0).getDescription();
//                }
//                tvWeatherResult.setText(description);
//
//                Calendar calendar = Calendar.getInstance();
//
//                int hour = calendar.get(Calendar.HOUR); // 12-hour format (0–11)
//                int minute = calendar.get(Calendar.MINUTE);
//                int am_pm = calendar.get(Calendar.AM_PM);
//
//                String amPmStr = (am_pm == Calendar.AM) ? "AM" : "PM";
//
//                if (hour == 0) {
//                    hour = 12;
//                }
//
//                String currentTime = String.format("%02d:%02d %s", hour, minute, amPmStr);
//
//                tvCurrentTimeCard.setText(currentTime);
//
//            }


 */