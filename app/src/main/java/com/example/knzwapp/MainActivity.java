package com.example.knzwapp;

import com.example.knzwapp.BuildConfig;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    Button travelNote, findNearby, touristSpots, travelMemory, btnLogout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView tvWeather;
    String weatherKey = BuildConfig.WEATHER_API_KEY;
    private static final String CITY = "Kanazawa";
    private static final String COUNTRY = "JP";

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        travelNote = findViewById(R.id.btnTravelNote);
        findNearby = findViewById(R.id.btnNearby);
        touristSpots = findViewById(R.id.btnTouristSpots);
        travelMemory = findViewById(R.id.btnTravelMemory);
        btnLogout = findViewById(R.id.btnLogout);
        tvWeather = findViewById(R.id.tvWeather);

        fetchWeather();

        travelNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Travelnote.class);
            startActivity(intent);
        });

        findNearby.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindNearby.class);
            startActivity(intent);
        });

        touristSpots.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TouristSpots.class);
            startActivity(intent);
        });

        travelMemory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TravelMemory.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchWeather() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + CITY + "," + COUNTRY + "&appid=" + weatherKey + "&units=metric";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject main = response.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    String weatherDesc = response.getJSONArray("weather").getJSONObject(0).getString("description");
                    tvWeather.setText("Kanazawa Weather: " + temp + "°C, " + weatherDesc);
                } catch (JSONException e) {
                    tvWeather.setText("Kanazawa Weather: Error loading");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    tvWeather.setText("Kanazawa Weather: Invalid API Key");
                } else if (statusCode == 0) {
                    tvWeather.setText("Kanazawa Weather: No internet");
                } else {
                    tvWeather.setText("Kanazawa Weather: Network error");
                }
                throwable.printStackTrace();
            }
        });
    }
}