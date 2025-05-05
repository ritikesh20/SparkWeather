package com.example.sparkweather;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkweather.model.WeatherResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDetailActivity extends AppCompatActivity {

    TextView cityname, citytemp, citymintemp, citymaxtemp, citycondition,
            daymin, daymax, feelsLike, avgHH, avgAvg,
            citywindSpeed, citywindDeg, citywindGust,
            cityUVindex, citySunRiseTIme, citysunSetTime,
            cityPreMm, cityPreMMresutl, cityVisibilitykm, cityHumidity, citypressure;

    final String API_KEY = "d4f89a278f449917407116ed2c070315";
    RecyclerView recyclerViewTime;
    FastAdapter<TimeXAdapter> timeFastAdapter;
    ItemAdapter<TimeXAdapter> timeItemAdapter;
    List<TimeXAdapter> listTimeAdapter;

    ImageView imageMode;

    MaterialToolbar toolbar;

    private CardView cardViewAvg;
    private CardView cardViewFeelslike;
    private CardView cardViewWinds;
    private CardView cardViewUvIndex;
    private CardView cardViewSunSet;
    private CardView cardViewPreci;
    private CardView cardViewVisibility;
    private CardView cardViewHumidity;
    private CardView cardViewPressure;


    String name;
    String temp;
    String condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        toolbar = findViewById(R.id.topAppBarDetails);
        setSupportActionBar(toolbar);

        cardViewFeelslike = findViewById(R.id.cardViewFeelsLike);
        cardViewWinds = findViewById(R.id.cardViewWind);
        cardViewUvIndex = findViewById(R.id.cardViewUVid);
        cardViewSunSet = findViewById(R.id.cardViewSunRise);
        cardViewPreci = findViewById(R.id.cardViewPrecipitation);
        cardViewVisibility = findViewById(R.id.cardViewVISIBILITY);
        cardViewHumidity = findViewById(R.id.cardViewHUMIDITY);
        cardViewPressure = findViewById(R.id.cardViewPRESSURE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);

        imageMode = findViewById(R.id.imageViewMode);
        cardViewAvg = findViewById(R.id.cardViewAverages);

        cityname = findViewById(R.id.cityName);
        citytemp = findViewById(R.id.cityTemp);

        citymaxtemp = findViewById(R.id.cityMaxTemp);
        citymintemp = findViewById(R.id.cityMinTemp);
        feelsLike = findViewById(R.id.tvFeels_likeTemp);
        cityHumidity = findViewById(R.id.tvHumidty);

        citycondition = findViewById(R.id.cityWeatherConditions);

        daymin = findViewById(R.id.dayMin);
        daymax = findViewById(R.id.dayMax);

        avgHH = findViewById(R.id.avgHH);
        avgAvg = findViewById(R.id.avgAvg);

        citywindSpeed = findViewById(R.id.tvWindSpeed);
        citywindDeg = findViewById(R.id.tvDirection);
        citywindGust = findViewById(R.id.tvGust);

        citySunRiseTIme = findViewById(R.id.tvSunRiseTime);
        citysunSetTime = findViewById(R.id.tvSunSetTime);
        cityUVindex = findViewById(R.id.tvUVindex);

        cityPreMm = findViewById(R.id.tvPreMM);
        cityPreMMresutl = findViewById(R.id.tvPreMMResult);

        cityVisibilitykm = findViewById(R.id.tvVisibilityKM);

        citypressure = findViewById(R.id.cityPressure);

//        WeatherResponse weatherData = (WeatherResponse) getIntent().getSerializableExtra("weather_data");

        Intent data = getIntent();
        String xLocationName = data.getStringExtra("cityNameX");
        fetchWeather(xLocationName);

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) { // night mode

            imageMode.setImageResource(R.drawable.moonbluesky);

        } else { // day mode
            changeCardViewColor();
            imageMode.setImageResource(R.drawable.nightsky);

        }


        timeFunction();
    }

    private String convertUnixToTime(long unixSeconds) {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("hh::mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.detailMenu_ShareLoction) {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR); // 12-hour format (0–11)
            int minute = calendar.get(Calendar.MINUTE);
            int am_pm = calendar.get(Calendar.AM_PM);

            String amPmStr = (am_pm == Calendar.AM) ? "AM" : "PM";

// Agar hour 0 ho to use 12 dikhaye
            if (hour == 0) {
                hour = 12;
            }

            String currentTime = String.format("%02d:%02d %s", hour, minute, amPmStr);

            String meLocation =
                    "CurrentTime :" + currentTime + "\n" +
                            "City Name : " + name
                            + "\n" + "City Temp  : " + temp +
                            "\n" + "Weather Condition : " +
                            condition;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Weather update");
            shareIntent.putExtra(Intent.EXTRA_TEXT, meLocation);

            startActivity(Intent.createChooser(shareIntent, "MyLocation Info"));

            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.detailMenu_ChangeMode) {

            int mode = AppCompatDelegate.getDefaultNightMode();

            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void changeCardViewColor() {
        ViewCompat.setBackgroundTintList(cardViewAvg, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewFeelslike, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewWinds, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewUvIndex, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewSunSet, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewPreci, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewVisibility, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewHumidity, ColorStateList.valueOf(Color.parseColor("#2A394E")));
        ViewCompat.setBackgroundTintList(cardViewPressure, ColorStateList.valueOf(Color.parseColor("#2A394E")));
    }

    private void fetchWeather(String cityNameX) {


        WeatherApi api = ApiClient.getClient().create(WeatherApi.class);
        Call<WeatherResponse> call = api.getWeatherData(cityNameX, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    WeatherResponse weatherData = response.body();

                    assert weatherData != null;
                    cityname.setText(weatherData.name);

                    if (weatherData.getMain() != null) {

                        citytemp.setText("" + (int) weatherData.getMain().getTemp() + "°");
                        citymintemp.setText((weatherData.getMain().getTemp_min()) + "°");
                        citymaxtemp.setText((weatherData.getMain().getTemp_max()) + "°");
                        daymax.setText((weatherData.getMain().getTemp_min()) + "°");
                        daymin.setText((weatherData.getMain().getTemp_max()) + "°");
                        avgHH.setText((weatherData.getMain().getTemp_min()) + "°");
                        avgAvg.setText((weatherData.getMain().getTemp_max()) + "°");
                        feelsLike.setText((weatherData.getMain().getFeels_like()) + "°");
                        cityHumidity.setText((weatherData.getMain().getHumidity()) + " %");
                        citypressure.setText((weatherData.getMain().getPressure()) + " hPa");

                        name = weatherData.name;
                        temp = weatherData.getMain().getTemp() + "°";
                        condition = weatherData.getWeather().get(0).getDescription();


                    } else {
                        Toast.makeText(WeatherDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    if (weatherData.getWind() != null) {
                        citywindSpeed.setText((weatherData.getWind().getSpeed()) + " kph");
                        citywindGust.setText((weatherData.getWind().getGust()) + "");
                        citywindDeg.setText((weatherData.getWind().getDeg()) + "");
                    }


                    if (weatherData.getSys() != null) {
                        long sunriseUnix = weatherData.getSys().getSunrise();
                        long sunsetUnix = weatherData.getSys().getSunset();
                        String sunriseTime = convertUnixToTime(sunriseUnix);
                        String sunSetTime = convertUnixToTime(sunsetUnix);

                        citySunRiseTIme.setText(sunriseTime);
                        citysunSetTime.setText(sunSetTime);
                    }


                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {


            }
        });

    }

    void timeFunction() {

        recyclerViewTime = findViewById(R.id.recyclerViewTime);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTime.setLayoutManager(layoutManager);

        timeFastAdapter = new FastAdapter<>();
        timeItemAdapter = new ItemAdapter<>();

        timeFastAdapter = FastAdapter.with(timeItemAdapter);

        listTimeAdapter = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {

            if (i <= 11) {
                listTimeAdapter.add(new TimeXAdapter(i + " Am", R.drawable.sunset, temp));
            }
            else {
                listTimeAdapter.add(new TimeXAdapter(i + " Pm", R.drawable.sunset, temp));
            }


        }

        timeItemAdapter.set(listTimeAdapter);

        recyclerViewTime.setAdapter(timeFastAdapter);

    }
}