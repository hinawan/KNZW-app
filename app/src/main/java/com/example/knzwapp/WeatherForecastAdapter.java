package com.example.knzwapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.WeatherViewHolder> {

    private final List<WeatherItem> items;

    public WeatherForecastAdapter(List<WeatherItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_forecast, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherItem item = items.get(position);

        holder.tvDate.setText(item.getDate());
        holder.tvDesc.setText(item.getDescription());
        holder.tvTemp.setText(String.format("%.0f°C", item.getTemp()));

        String iconUrl = "https://openweathermap.org/img/wn/" + item.getIconCode() + "@2x.png";
        Glide.with(holder.ivIcon.getContext()).load(iconUrl).into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvDate, tvDesc, tvTemp;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvDate = itemView.findViewById(R.id.tvWeatherDate);
            tvDesc = itemView.findViewById(R.id.tvWeatherDesc);
            tvTemp = itemView.findViewById(R.id.tvWeatherTemp);
        }
    }
}
