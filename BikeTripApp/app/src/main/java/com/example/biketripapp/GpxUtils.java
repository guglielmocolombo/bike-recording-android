package com.example.biketripapp;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GpxUtils {

    private static List<DataPoint> dataPoints = new ArrayList<>();

    public static void addDataPoint(double latitude, double longitude, String localTime){

        double speed = 0;
        if(!dataPoints.isEmpty()){
            double distance = Haversine.haversine(latitude, longitude, dataPoints.get(dataPoints.size()-1).getLatitude(), dataPoints.get(dataPoints.size()-1).getLongitude());
            double time = Haversine.getTimeDifference(dataPoints.get(dataPoints.size()-1).getTime(), localTime);
            speed = Haversine.computeSpeed(distance, time);
        }
        DataPoint dataPoint = new DataPoint(latitude, longitude, localTime, speed);
        dataPoints.add(dataPoint);
    }
    public static String generateGpxContent() {
        StringBuilder gpxContent = new StringBuilder();
        gpxContent.append("<?xml version=\"1.0\"?>\n");
        gpxContent.append("<gpx version=\"1.1\" creator=\"GeolocationRecorderApp\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
        gpxContent.append("  <trk>\n");
        gpxContent.append("    <name>Example Track</name>\n");
        gpxContent.append("    <trkseg>\n");

        for (DataPoint location : dataPoints) {
            gpxContent.append("      <trkpt lat=\"").append(location.getLatitude()).append("\" lon=\"").append(location.getLongitude()).append("\">\n");
            gpxContent.append("        <time>").append(location.getTime()).append("</time>\n");
            gpxContent.append("        <speed>").append(location.getSpeed()).append("</speed>\n");
            gpxContent.append("      </trkpt>\n");
        }

        gpxContent.append("    </trkseg>\n");
        gpxContent.append("  </trk>\n");
        gpxContent.append("</gpx>\n");

        return gpxContent.toString();
    }

    public static void saveGpxFile(Context context, String gpxContent, String fileName) {
        saveGpxFileToDownloads(context, gpxContent, fileName);
    }

    private static void saveGpxFileToDownloads(Context context, String gpxContent, String fileName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/gpx+xml");
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        }

        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    outputStream.write(gpxContent.getBytes());
                    Toast.makeText(context, "GPX file saved to Downloads", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to open OutputStream", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save GPX file", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Failed to create file in Downloads", Toast.LENGTH_LONG).show();
        }
    }
}

