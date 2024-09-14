package com.example.biketripapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {


    private Button startRecordingButton;
    private Button downloadGpxButton;
    private AWSInstance aws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRecordingButton = findViewById(R.id.startRecordingButton);
        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationService();
            }
        });

        downloadGpxButton = findViewById(R.id.downloadGpxButton);
        downloadGpxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationService();
                generateAndSaveGpxFile();
            }
        });

        aws = new AWSInstance();
    }

    private void startLocationService() {
        Log.d("Main Activity", "Start Location Service");
        Intent serviceIntent = new Intent(this, LocationService.class);
        startForegroundService(serviceIntent);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        */
    }

    private void stopLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
    }

    private void generateAndSaveGpxFile() {
        String gpxContent = GpxUtils.generateGpxContent();
        GpxUtils.saveGpxFile(this, gpxContent, "example_track.gpx");

        String trackGpx = GpxUtils.generateGpxContent();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    aws.sendEntryWrapper(trackGpx);
                } catch (Exception e) {
                    Log.e("MainActivity", "Error sending entry", e);
                }
            }
        });

    }
}
