package com.example.knzwapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.LandmarkViewHolder> {
    private List<Landmark> landmarkList;
    private OnLandmarkClickListener listener;

    public interface OnLandmarkClickListener {
        void onLandmarkClick(Landmark landmark);
    }

    public LandmarkAdapter(List<Landmark> landmarkList, OnLandmarkClickListener listener) {
        this.landmarkList = landmarkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LandmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landmark, parent, false);
        return new LandmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LandmarkViewHolder holder, int position) {
        Landmark landmark = landmarkList.get(position);
        holder.imageView.setImageResource(landmark.getImageResId());
        holder.textView.setText(landmark.getName());
        holder.itemView.setOnClickListener(v -> listener.onLandmarkClick(landmark));
    }

    @Override
    public int getItemCount() {
        return landmarkList.size();
    }

    static class LandmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public LandmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivLandmarkImage);
            textView = itemView.findViewById(R.id.tvLandmarkName);
        }
    }
}