package com.example.biketripapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

public class Haversine {

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitudes and longitudes from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the differences between latitudes and longitudes
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Apply the Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    public static double getTimeDifference(String time1, String time2){
        Instant instant1 = Instant.parse(time1);
        Instant instant2 = Instant.parse(time2);

        Duration duration = Duration.between(instant1, instant2);

        long totalMillis = duration.toMillis();

        return totalMillis / 1000.0;
    }

    public static double computeSpeed(double distance, double time){
        if(time==0){
            return 0;
        }
        double speed = distance / time;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("##.##", symbols);

        return Double.parseDouble(df.format(speed));
    }


}
