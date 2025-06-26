package com.example.knzwapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(LatLng latLng);
    }

    private final List<Place> placeList;
    private final OnItemClickListener listener;

    public NearbyAdapter(List<Place> placeList, OnItemClickListener listener) {
        this.placeList = placeList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(android.R.id.text1);
        }

        public void bind(Place place, OnItemClickListener listener) {
            textView.setText(place.getName());
            itemView.setOnClickListener(v -> {
                if (place.getLatLng() != null) {
                    listener.onItemClick(place.getLatLng());
                }
            });
        }
    }

    @NonNull
    @Override
    public NearbyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyAdapter.ViewHolder holder, int position) {
        holder.bind(placeList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }
}
