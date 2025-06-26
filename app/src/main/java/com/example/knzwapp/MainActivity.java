package com.example.knzwapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    Button travelNote, findNearby, touristSpots, travelMemory, weather, btnLogout;
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
        weather = findViewById(R.id.btnWeather);
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

        weather.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeatherForecast.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "battery_warning_channel",
                    "Battery Warnings",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notify when battery is low.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }


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

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) (level * 100 / (float)scale);

            if (batteryPct < 30) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "battery_warning_channel")
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("Battery Low")
                        .setContentText("Battery is at " + batteryPct + "%. Please charge your device for exploring Kanazawa!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(1001, builder.build());
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryReceiver);
    }

}