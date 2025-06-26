package com.example.knzwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TravelMemory extends AppCompatActivity {
    private TravelNoteAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("travel_notes");
    private ImageButton topButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_memory);

        topButton = findViewById(R.id.btnTop);
        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TravelNoteAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        databaseRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TravelNoteEntity> notes = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TravelNoteEntity note = data.getValue(TravelNoteEntity.class);
                    if (note != null) notes.add(note);
                }
                adapter = new TravelNoteAdapter(notes);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TravelMemory.this, "Failed to load notes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        topButton.setOnClickListener(v -> startActivity(new Intent(TravelMemory.this, MainActivity.class)));
    }
}