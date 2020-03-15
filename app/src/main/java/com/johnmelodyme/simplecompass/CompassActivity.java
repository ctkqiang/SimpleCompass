package com.johnmelodyme.simplecompass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.johnmelodyme.simplecompass.Service.LocationService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

/**
 * @Author : John Melody Melissa
 * @Copyright: John Melody Melissa  © Copyright 2020
 * @INPIREDBYGF : Sin Dee <3
 */

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    public static final String TAG = "Compass";
    public static DecimalFormat DECIMAL_FORMATTER;
    private static FusedLocationProviderClient fusedLocationClient;
    private static FlatDialog flatDialog;
    private static SensorManager compassSensor, magneticField;
    public static LocationManager locationManager;
    public static ImageView compassImage;
    public static float degreeStart = 0f;
    public static TextView degreeTV, mField, Lat, Long, distance, time, speed;
    private static Button button_start, button_stop;
    public static LocationService locationService;
    public static boolean status;
    public static long startTime, endTime;
    public static ProgressDialog locate;
    public static int P = 0x0;
    public static final int REQUEST_LOCATION = 1;
    
    // TODO declaractionInit()
    private void declaractionInit(){
        compassImage = findViewById(R.id.compass);
        degreeTV = findViewById(R.id.degree);
        mField = findViewById(R.id.magneticField);
        Lat = findViewById(R.id.la);
        Long = findViewById(R.id.lo);
        distance = findViewById(R.id.distance);
        time = findViewById(R.id.time);
        speed = findViewById(R.id.speed);
        button_start = findViewById(R.id.btn_start);
        button_stop = findViewById(R.id.btn_stop);
        compassSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        //magneticField = (SensorManager) getSystemService(SENSOR_SERVICE);
        flatDialog = new FlatDialog(CompassActivity.this);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.UK);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat("#.000", symbols);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_LOCATION);
        } else {
            Log.d(TAG, "declaractionInit: " + Arrays.hashCode(CompassActivity.class.getSimpleName().getBytes()));
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    // TODO onCreate
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starting " + CompassActivity.class.getName().toUpperCase());
        declaractionInit();


        // TODO OnClick button_start
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //The method below checks if Location is enabled on device or not.
                // If not, then an alert dialog box appears with option to
                // enable the gps.
                checkTheGps();
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    return;
                }

                if (status == false){
                    //Here, the Location Service gets bound and the GPS Speedometer gets Active.
                    bindService();
                }

                Context Main = CompassActivity.this;
                locate = new ProgressDialog(Main);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Getting Location...");
                locate.show();
                button_start.setVisibility(View.GONE);
                button_stop.setVisibility(View.VISIBLE);
            }
        });

        // TODO btn_stop
        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == true) {
                    unBindService();
                }
                button_start.setVisibility(View.VISIBLE);
                button_stop.setVisibility(View.GONE);
                P = 0;
            }
        });
    }

    // TODO checkTheGps()
    public void checkTheGps() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showAlertDialogueTouUser();
        }
    }

    // TODO showAlertDialogueTouUser()
    private void showAlertDialogueTouUser() {
        Context context = this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent CALL_DEVICE_GPS_SETTING;
                        CALL_DEVICE_GPS_SETTING = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(CALL_DEVICE_GPS_SETTING);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogue, int which) {
                    dialogue.cancel();
                }
        }).create().show();
    }

    @Override
    // TODO onPause
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        compassSensor.unregisterListener(this);
        //magneticField.unregisterListener(this);
    }

    @Override
    //TODO onResume
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
    }

    @SuppressLint("SetTextI18n")
    @Override
    // TODO onSensorChanged:
    public void onSensorChanged(SensorEvent event) {
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        String heading, degrees;
        heading = "Heading: ";
        degrees = " °degrees";
        degreeTV.setText(heading + degree + degrees);
        Log.d(TAG, "Compass ==========>  Heading: " + degree + " °degrees");
        RotateAnimation ra = new RotateAnimation(
                degreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place:
        ra.setDuration(210);
        // Start animation of compass image
        compassImage.startAnimation(ra);
        degreeStart =- degree;
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
    // TODO onAccuracyChanged
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not Available
    }

    @Override
    // TODO onCreateOptionMenu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    // TODO onOptionsItemSelected
    public boolean onOptionsItemSelected(MenuItem item) {
        int ID = item.getItemId();

        if (ID == R.id.about){
            flatDialog.setTitle("About")
                    .setSubtitle("Created By John Melody Melissa & Tan Sin Dee ❤")
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
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }).show();
        }

        if (ID == R.id.source){
            String url;
            url = "https://github.com/johnmelodyme/SimpleCompass";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    // TODO onBackPressed
    public void onBackPressed(){
        super.onBackPressed();
        if (status == false)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }

    // TODO serviceConnection
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder localBinder = (LocationService.LocalBinder) service;
            locationService = localBinder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false ;
        }
    };

    // TODO bindService
    public void bindService(){
        if (status == true)
            return;
        Intent i;
        i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, serviceConnection, BIND_AUTO_CREATE);
        startTime = System.currentTimeMillis();
    }

    // TODO unBindService
    public void unBindService(){
        if (status == false)
            return;
        Intent i;
        i = new Intent(getApplicationContext(), LocationService.class);
        unBindService();
        status = false;
    }

    // TODO onDestroy
    protected void onDestroy(){
        super.onDestroy();
        if (status == true) {
            unBindService();
        }
        DestroyAll();
    }

    // TODO DestroyAll()
    public void DestroyAll() {
        finish();
        System.gc();
    }
}