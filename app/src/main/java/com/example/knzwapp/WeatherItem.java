package com.example.knzwapp;

public class WeatherItem {
    private final String date;
    private final double temp;
    private final String iconCode;
    private final String description;

    public WeatherItem(String date, double temp, String iconCode, String description) {
        this.date = date;
        this.temp = temp;
        this.iconCode = iconCode;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public double getTemp() {
        return temp;
    }

    public String getIconCode() {
        return iconCode;
    }

    public String getDescription() {
        return description;
    }
}
