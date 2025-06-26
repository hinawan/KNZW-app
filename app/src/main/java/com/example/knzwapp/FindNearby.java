package com.example.knzwapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindNearby extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private GoogleMap mMap;
    private EditText etSearch;
    private Button btnSearch;
    ImageButton topButton;

    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private LatLng currentLatLng;
    private RecyclerView rvNearby;
    private List<Place> nearbyList = new ArrayList<>();
    private NearbyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        topButton = findViewById(R.id.btnTop);

        rvNearby = findViewById(R.id.rvNearbyLandmarks);
        rvNearby.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NearbyAdapter(nearbyList, latLng -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        });
        rvNearby.setAdapter(adapter);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.PLACES_API_KEY);
        }
        placesClient = Places.createClient(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                searchNearby(keyword);
            } else {
                Toast.makeText(this, "Please enter a search keyword", Toast.LENGTH_SHORT).show();
            }
        });

        topButton.setOnClickListener(v -> startActivity(new Intent(FindNearby.this, MainActivity.class)));
    }

    private void searchNearby(String keyword) {
        if (currentLatLng == null) {
            Toast.makeText(this, "Waiting for location...", Toast.LENGTH_SHORT).show();
            return;
        }

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(keyword)
                .setLocationRestriction(RectangularBounds.newInstance(
                        new LatLng(currentLatLng.latitude - 0.045, currentLatLng.longitude - 0.045),
                        new LatLng(currentLatLng.latitude + 0.045, currentLatLng.longitude + 0.045)
                ))
                .build();


        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    mMap.clear();
                    nearbyList.clear();

                    for (var prediction : response.getAutocompletePredictions()) {
                        String placeId = prediction.getPlaceId();
                        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId,
                                Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG)).build();

                        placesClient.fetchPlace(placeRequest).addOnSuccessListener(fetchResponse -> {
                            Place place = fetchResponse.getPlace();
                            if (place.getLatLng() != null) {
                                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                                nearbyList.add(place);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("FindNearby", "Prediction failed", e);
                    Toast.makeText(this, "Failed to find places: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        enableUserLocation();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
        }
    }

}
