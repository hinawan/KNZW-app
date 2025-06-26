package com.example.knzwapp;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class TravelNoteAdapter extends RecyclerView.Adapter<TravelNoteAdapter.NoteViewHolder> {

    private final List<TravelNoteEntity> noteList;

    public TravelNoteAdapter(List<TravelNoteEntity> noteList) {
        this.noteList = noteList;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.travel_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        TravelNoteEntity note = noteList.get(position);
        holder.tvNote.setText(note.getNoteText());
        holder.tvLocation.setText(note.getLocation());
        holder.tvDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", note.getTimestamp()));

        Glide.with(holder.ivPhoto.getContext()).load(note.getImagePath()).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvNote, tvLocation, tvDate;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivNotePhoto);
            tvNote = itemView.findViewById(R.id.tvNoteText);
            tvLocation = itemView.findViewById(R.id.tvNoteLocation);
            tvDate = itemView.findViewById(R.id.tvNoteDate);
        }
    }



}
