package com.example.biketripapp;

public class DataPoint {
    private double latitude;
    private double longitude;
    private String time;
    private double speed;

    public DataPoint(double latitude, double longitude, String time, double speed){
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.speed = speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getTime() {
        return time;
    }

    public double getSpeed() {return speed;}
}
