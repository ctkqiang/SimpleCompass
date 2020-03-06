package com.johnmelodyme.simplecompass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @Author : John Melody Melissa
 * @Copyright: John Melody Melissa  © Copyright 2020
 * @INPIREDBYGF : Sin Dee <3
 */

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "Compass";
    public static DecimalFormat DECIMAL_FORMATTER;
    private FusedLocationProviderClient fusedLocationClient;
    private FlatDialog flatDialog;
    private SensorManager compassSensor, magneticField;
    private ImageView compassImage;
    private float degreeStart = 0f;
    private TextView degreeTV, mField, Lat, Long;
    private int REQUEST = 0x2c;

    // TODO Declaraction:
    private void declaractionInit(){
        compassImage = findViewById(R.id.compass);
        degreeTV = findViewById(R.id.degree);
        mField = findViewById(R.id.magneticField);
        Lat = findViewById(R.id.la);
        Long = findViewById(R.id.lo);
        compassSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        //magneticField = (SensorManager) getSystemService(SENSOR_SERVICE);
        flatDialog = new FlatDialog(CompassActivity.this);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.UK);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat("#.000", symbols);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starting " + CompassActivity.class.getName().toUpperCase());
        declaractionInit();
        getUserLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        compassSensor.unregisterListener(this);
        //magneticField.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        compassSensor.registerListener(this,
                compassSensor.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        /*
        magneticField.registerListener(this,
                magneticField.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        */

        if (checkPermissions()){
            getUserLocation();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO onSensorChanged:
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        degreeTV.setText("Heading: " + degree + " °degrees");
        Log.d(TAG, "Compass =============>  Heading: " + degree + " °degrees");
        RotateAnimation ra = new RotateAnimation(
                degreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassImage.startAnimation(ra);
        degreeStart = -degree;


//        // Magnetic field :
//        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            // get values for each axes X,Y,Z
//            float magX = event.values[0];
//            float magY = event.values[1];
//            float magZ = event.values[2];
//            double magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));
//            mField.setText(DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla");
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not Available
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int ID = item.getItemId();

        if (ID == R.id.about){
            flatDialog.setTitle("About")
                    .setSubtitle("Created By John Melody Melissa & Tan Sin Dee")
                    .setFirstButtonText("Okay")
                    .setSecondButtonText("Author's Portfolio")
                    .withFirstButtonListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent refresh;
                            refresh = new Intent(CompassActivity.this, CompassActivity.class);
                            startActivity(refresh);
                        }
                    }).withSecondButtonListner(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url;
                    url = "https://johnmelodyme.github.io/";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }).show();
        }

        if (ID == R.id.source){
            String url;
            url = "https://github.com/johnmelodyme/SimpleCompass";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void getUserLocation(){
        if(checkPermissions()){
            if (isLocationEnabled()) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<Location> LOCATION_TASK) {
                        Location location = LOCATION_TASK.getResult();
                        if (location == null ){
                            requestNewLocationData();
                        } else {
                            Lat.setText(location.getLatitude()+"");
                            Long.setText(location.getLongitude()+"");
                        }
                    }
                });
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location lastLocation = locationResult.getLastLocation();
            Lat.setText(lastLocation.getLatitude()+"");
            Long.setText(lastLocation.getLongitude()+"");
        }
    };
}