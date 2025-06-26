package com.example.knzwapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class TouristSpots extends FragmentActivity implements OnMapReadyCallback, LandmarkAdapter.OnLandmarkClickListener {

    private GoogleMap mMap;
    private ImageButton topButton;
    private RecyclerView rvLandmarks;
    private List<Landmark> landmarkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tourist_spots);

        topButton = findViewById(R.id.btnTop);
        rvLandmarks = findViewById(R.id.rvLandmarks);

        initializeLandmarks();

        rvLandmarks.setLayoutManager(new LinearLayoutManager(this));
        rvLandmarks.setAdapter(new LandmarkAdapter(landmarkList, this));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        topButton.setOnClickListener(v -> {
            Intent intent = new Intent(TouristSpots.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void initializeLandmarks() {
        landmarkList.add(new Landmark("Kenrokuen Garden", R.drawable.kenrokuen, 36.5620, 136.6625));
        landmarkList.add(new Landmark("Kanazawa Castle", R.drawable.kanazawa_castle, 36.5659, 136.6591));
        landmarkList.add(new Landmark("Ohmicho Ichiba", R.drawable.ohmicho_ichiba, 36.5717, 136.6557));
        landmarkList.add(new Landmark("Ishikawa Library", R.drawable.ishikawa_library, 36.5488, 136.6806));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (!landmarkList.isEmpty()) {
            LatLng defaultLocation = new LatLng(landmarkList.get(0).getLatitude(), landmarkList.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(defaultLocation).title(landmarkList.get(0).getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
        }
    }

    @Override
    public void onLandmarkClick(Landmark landmark) {
        LatLng location = new LatLng(landmark.getLatitude(), landmark.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location).title(landmark.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
}