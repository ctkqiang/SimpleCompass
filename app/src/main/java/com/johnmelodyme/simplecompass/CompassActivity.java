package com.johnmelodyme.simplecompass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flatdialoglibrary.dialog.FlatDialog;

/**
 * @Author : John Melody Melissa
 * @Copyright: John Melody Melissa  © Copyright 2020
 * @INPIREDBYGF : Sin Dee <3
 */

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "Compass";
    private FlatDialog flatDialog;
    private SensorManager sensorManager;
    private ImageView compassImage;
    private float degreeStart = 0f;
    private TextView degreeTV;

    // TODO Declaraction:
    private void declaractionInit(){
        compassImage = findViewById(R.id.compass);
        degreeTV = findViewById(R.id.degree);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        flatDialog = new FlatDialog(CompassActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starting " + CompassActivity.class.getName().toUpperCase());
        declaractionInit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO onSensorChanged:
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        degreeTV.setText("Heading: " + Float.toString(degree) + " °  degrees");
        Log.d(TAG, "Compass =============>  Heading: " + Float.toString(degree) + " °  degrees");
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
}