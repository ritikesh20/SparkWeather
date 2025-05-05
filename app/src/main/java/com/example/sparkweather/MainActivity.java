package com.example.sparkweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.sparkweather.model.WeatherResponse;
import com.example.sparkweather.room.WeatherDatabase;
import com.example.sparkweather.room.WeatherEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 100;

    private RecyclerView recyclerView;
    private FastItemAdapter<WeatherXadapter> fastAdapter;
    private EditText cityInput;
    private ImageButton fetchButton;
    private final String API_KEY = "d4f89a278f449917407116ed2c070315";

    TextView homeCityName, homeCityTemp, homeCityCondition, homeCityMaxTemp, homeCityMinTemp;

    EditText searchEditText;

    private WeatherDatabase db;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        homeCityName = findViewById(R.id.homeCityName);
        homeCityTemp = findViewById(R.id.homeCityTemp);
        homeCityCondition = findViewById(R.id.homeCityWeatherConditions);
        homeCityMaxTemp = findViewById(R.id.homeCityMaxTemp);
        homeCityMinTemp = findViewById(R.id.homeCityMinTemp);

        db = WeatherDatabase.getInstance(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fastAdapter = new FastItemAdapter<>();
        recyclerView.setAdapter(fastAdapter);

        loadWeatherFromDb();

        checkInternetConnection();

        searchEditText = findViewById(R.id.btnSercingCityName);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String city = searchEditText.getText().toString().toLowerCase().trim();

                    if (!city.isEmpty()) {
                        fetchWeatherData(city);
                        setUpPeriodWork(city);
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a city name or pin Code", Toast.LENGTH_SHORT).show();
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                    return true;

                }
                return false;
            }
        });

        xClick();
        preClicked();
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_myLocation) {
                checkLocationPermission();
            } else if (id == R.id.menu_Notification) {
                new Thread(() -> {
                    db.weatherDao().deleteAll();
                    runOnUiThread(() -> {
                        fastAdapter.clear();
                        Toast.makeText(this, "Data cleared", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            } else if (id == R.id.menu_RefreshData) {
                loadWeatherFromDb();
            }
            return false;
        });

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    fetchCurrentLocation(lat, lon);
                }
            });
        }
    }

    private void fetchCurrentLocation(double lat, double lon) {
        WeatherApi weatherApi = ApiClient.getClient().create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(lat, lon, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    WeatherResponse weatherResponse = response.body();

                    WeatherEntity entity = new WeatherEntity(
                            weatherResponse.name,
                            String.valueOf(weatherResponse.getMain().getTemp()),
                            weatherResponse.getWeather().get(0).getDescription(),
                            String.valueOf(weatherResponse.getMain().getTemp_max()),
                            String.valueOf(weatherResponse.getMain().getTemp_min())
                    );

//                    cityNameSender(weatherResponse.name);
                    new Thread(() -> {
                        db.weatherDao().insert(entity);
                        loadWeatherFromDb();
                    }).start();

                    if (weatherResponse.getMain() != null) {
                        homeCityName.setText(weatherResponse.name);
                        homeCityTemp.setText("" + (int) weatherResponse.getMain().getTemp() + "°");
                        homeCityMaxTemp.setText(weatherResponse.getMain().getTemp_max() + "");
                        homeCityMinTemp.setText(weatherResponse.getMain().getTemp_min() + "");

                    }

                    if (weatherResponse.getWeather() != null) {
                        homeCityCondition.setText(weatherResponse.getWeather().get(0).getDescription());
                    }

                } else {
                    Toast.makeText(MainActivity.this, "City not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Oops, something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeatherData(String city) {
        WeatherApi api = ApiClient.getClient().create(WeatherApi.class);
        Call<WeatherResponse> call = api.getWeatherData(city, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    WeatherEntity entity = new WeatherEntity(
                            weatherResponse.name,
                            String.valueOf(weatherResponse.getMain().getTemp()),
                            weatherResponse.getWeather().get(0).getDescription(),
                            String.valueOf(weatherResponse.getMain().getTemp_max()),
                            String.valueOf(weatherResponse.getMain().getTemp_min())
                    );

                    new Thread(() -> {
                        db.weatherDao().insert(entity);
                        loadWeatherFromDb();
                    }).start();


                    if (weatherResponse.getMain() != null) {
                        homeCityName.setText(weatherResponse.name);
                        homeCityTemp.setText("" + (int) weatherResponse.getMain().getTemp() + "°");
                        homeCityMaxTemp.setText(weatherResponse.getMain().getTemp_max() + "");
                        homeCityMinTemp.setText(weatherResponse.getMain().getTemp_min() + "");

                    }

                    if (weatherResponse.getWeather() != null) {
                        homeCityCondition.setText(weatherResponse.getWeather().get(0).getDescription());
                    }


                } else {
                    Toast.makeText(MainActivity.this, "City not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch weather", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWeatherFromDb() {
        new Thread(() -> {
            List<WeatherEntity> data = db.weatherDao().getAllWeather();

            runOnUiThread(() -> {
                List<WeatherXadapter> items = new ArrayList<>();
                for (WeatherEntity entity : data) {
                    items.add(new WeatherXadapter(entity));
                }
                fastAdapter.setNewList(items);
            });
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    private void setUpPeriodWork(String cityName) {
        Data inputData = new Data.Builder()
                .putString("City_Name", cityName)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(WeatherWorker.class, 1, TimeUnit.HOURS)
                .setInputData(inputData).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "weather_update",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
        );

    }

    void xClick() {
        fastAdapter.withOnClickListener(new OnClickListener<WeatherXadapter>() {
            @Override
            public boolean onClick(View v, IAdapter<WeatherXadapter> adapter, WeatherXadapter item, int position) {
                Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);

//                String cityName = String.valueOf(item.getWeatherEntity().cityName);
                String cityName = item.getWeatherEntity().cityName;

                intent.putExtra("cityNameX", cityName);
                startActivity(intent);
                return false;
            }
        });
    }

    void preClicked() {
//        fastAdapter.withOnLongClickListener(new OnLongClickListener<WeatherXadapter>() {
//            @Override
//            public boolean onLongClick(View v, IAdapter<WeatherXadapter> adapter, WeatherXadapter item, int position) {
//                Toast.makeText(MainActivity.this, "Long Clicked", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

//        fastAdapter.withOnPreClickListener(new OnClickListener<WeatherXadapter>() {
//            @Override
//            public boolean onClick(View v, IAdapter<WeatherXadapter> adapter, WeatherXadapter item, int position) {
//                Toast.makeText(MainActivity.this, "On Pre Clicked", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

//        fastAdapter.withOnPreLongClickListener(new OnLongClickListener<WeatherXadapter>() {
//            @Override
//            public boolean onLongClick(View v, IAdapter<WeatherXadapter> adapter, WeatherXadapter item, int position) {
//                Toast.makeText(MainActivity.this, "Long Clicked", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });


    }

    void checkInternetConnection() {
        if (NetworkUtil.isNetworkAvailable(this)) {

        } else {
            Toast.makeText(this, " Opps No Internet Connection", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, NoInternetActivity.class);
                startActivity(intent);
                finish();
            }, 1500);

        }
    }


}
