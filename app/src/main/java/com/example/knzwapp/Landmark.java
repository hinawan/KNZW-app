package com.example.knzwapp;

public class Landmark {
    private String name;
    private int imageResId;
    private double latitude;
    private double longitude;

    public Landmark(String name, int imageResId, double latitude, double longitude) {
        this.name = name;
        this.imageResId = imageResId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}