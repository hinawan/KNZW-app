package com.example.knzwapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherForecast extends AppCompatActivity {
    String weatherKey = BuildConfig.WEATHER_API_KEY;
    private static final String CITY = "Kanazawa";
    private static final String COUNTRY = "JP";
    private RecyclerView recyclerView;
    ImageButton topButton;
    private WeatherForecastAdapter adapter;
    private List<WeatherItem> weatherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        recyclerView = findViewById(R.id.rvWeather);
        topButton = findViewById(R.id.btnTop);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherForecastAdapter(weatherList);
        recyclerView.setAdapter(adapter);

        fetchWeather();
        topButton.setOnClickListener(v -> startActivity(new Intent(WeatherForecast.this, MainActivity.class)));
    }

    private void fetchWeather() {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + CITY + "," + COUNTRY +
                "&appid=" + weatherKey + "&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray list = response.getJSONArray("list");
                        weatherList.clear();

                        for (int i = 0; i < list.length(); i += 8) {
                            JSONObject obj = list.getJSONObject(i);
                            JSONObject main = obj.getJSONObject("main");
                            JSONArray weatherArray = obj.getJSONArray("weather");
                            JSONObject weather = weatherArray.getJSONObject(0);

                            String dt_txt = obj.getString("dt_txt");
                            String icon = weather.getString("icon");
                            String description = weather.getString("description");
                            double temp = main.getDouble("temp");

                            weatherList.add(new WeatherItem(dt_txt, temp, icon, description));
                        }
                        adapter.notifyDataSetChanged();

                        if (weatherList.stream().anyMatch(w -> w.getDescription().toLowerCase().contains("rain"))) {
                            Toast.makeText(this, "Remember to bring umbrella", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Parse weather data failed", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Retrieve weather data failed", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

}
