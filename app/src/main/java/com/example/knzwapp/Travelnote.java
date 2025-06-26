package com.example.knzwapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class Travelnote extends AppCompatActivity {
    ImageView photo;
    EditText note;
    TextView tvlocation;
    Button camera, save;
    ImageButton topButton;
    Bitmap capturedImage;
    String currentLocation = "";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    ActivityResultLauncher<Intent> cameraLauncher;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("travel_notes");
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("images");
    FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelnote);

        photo = findViewById(R.id.ivPhoto);
        note = findViewById(R.id.etNote);
        tvlocation = findViewById(R.id.tvLocation);
        camera = findViewById(R.id.btnCamera);
        save = findViewById(R.id.btnSave);
        topButton = findViewById(R.id.btnTop);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        capturedImage = (Bitmap) extras.get("data");
                        photo.setImageBitmap(capturedImage);
                    }
                });

        camera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        });


        fetchLocation();

        save.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = mAuth.getCurrentUser().getUid();
            String travelNote = note.getText().toString().trim();
            if (capturedImage == null || travelNote.isEmpty()) {
                Toast.makeText(this, "Please capture a photo and write a note.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentLocation.isEmpty()) {
                Toast.makeText(this, "Location unavailable. Please enable GPS.", Toast.LENGTH_SHORT).show();
                return;
            }

            String imageFileName = "photo_" + System.currentTimeMillis() + ".jpg";
            StorageReference imageRef = storageRef.child(imageFileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] data = baos.toByteArray();

            imageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    TravelNoteEntity noteEntity = new TravelNoteEntity(
                            travelNote, uri.toString(), currentLocation, System.currentTimeMillis()
                    );
                    String noteId = databaseRef.push().getKey();
                    if (noteId != null) {
                        databaseRef.child(noteId).setValue(noteEntity)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Memory saved " + currentLocation, Toast.LENGTH_LONG).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, "Image URL failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        topButton.setOnClickListener(v -> startActivity(new Intent(Travelnote.this, MainActivity.class)));
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location permission needed for Travel Note", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location.getLatitude() + ", " + location.getLongitude();
                        tvlocation.setText("Location: " + currentLocation);
                    } else {
                        currentLocation = "";
                        tvlocation.setText("Location: Unknown");
                        Toast.makeText(this, "Unable to get location. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    currentLocation = "";
                    tvlocation.setText("Location: Unknown");
                    Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            currentLocation = "";
            tvlocation.setText("Location: Unknown");
            Toast.makeText(this, "Security error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                currentLocation = "";
                tvlocation.setText("Location: Unknown");
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}