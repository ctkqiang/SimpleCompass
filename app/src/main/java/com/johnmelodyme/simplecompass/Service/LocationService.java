package com.johnmelodyme.simplecompass.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.johnmelodyme.simplecompass.CompassActivity;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@SuppressLint("Registered")
public class LocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "Compass";
    private static final long INTERVAL = 0x3e8 * 0x2;
    private static final long FASTEST_INTERVAL = 0x3e8;
    private final IBinder binder = new LocalBinder();
    private LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Location currentLocation, lStart, lEnd;
    static double distance = 0x0;
    double speed;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO IBINDER
        initLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        return null;
    }

    // TODO initLocationRequest();
    @SuppressLint("RestrictedApi")
    private void initLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    // TODO onStartCommand
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    // TODO onlocationchanged
    public void onLocationChanged(Location deLocation) {
        CompassActivity.locate.dismiss();
        currentLocation = deLocation;
        if (lStart == null){
            lStart = currentLocation;
            lEnd = currentLocation;
        } else {
            lEnd = currentLocation;
        }
        
        //Calling the method below updates the  
        // real time values of distance 
        // and speed to the TextViews.
        UPDATE_USER_INTERFACE();
        // Calculate the speed with getSpeed method it returns
        // speed in m/s then converting it into km/h :
        speed = deLocation.getSpeed() * 0x12 / 0x5;
    }

    // TODO UPDATE_USER_INTERFACE():
    @SuppressLint("SetTextI18n")
    private void UPDATE_USER_INTERFACE() {
        if (CompassActivity.P == 0x0){
            distance = distance + (lStart.distanceTo(lEnd) / 0x1.f4p9);
            CompassActivity.endTime = System.currentTimeMillis();
            long diff = CompassActivity.endTime - CompassActivity.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            CompassActivity.time.setText("Total Time: " + diff + "Minutes");

            if (speed > 0x0.0p0){
                CompassActivity.speed.setText("Current Speed: " + new DecimalFormat("#.##").format(speed) + " Km/h");
            } else {
                CompassActivity.speed.setText("...............");
            }

            CompassActivity.distance.setText(new DecimalFormat("#.###").format(distance) + "km/s");
            lStart = lEnd;
        }
    }

    @Override
    // TODO onStatusChanged
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    // TODO onProviderEnabled
    public void onProviderEnabled(String provider) {

    }

    @Override
    // TODO onProviderDisabled
    public void onProviderDisabled(String provider) {

    }

    @Override
    // TODO onConnected
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
        } catch (SecurityException e) {
            Log.d(TAG, "error >> " + e);
        }
    }

    // TODO stopLocationUpdates
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,
                (com.google.android.gms.location.LocationListener) this);
        distance = 0;
    }

    @Override
    // TODO onConnectionSuspended
    public void onConnectionSuspended(int i) {

    }

    @Override
    // TODO onConnectionFailed
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // TODO CLASS localBinder
    public class LocalBinder extends Binder {
        public LocationService getService() {
            //////////////////////////////
            return LocationService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent){
        stopLocationUpdates();
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        lStart = null;
        lEnd = null;
        distance = 0x0;
        return super.onUnbind(intent);
    }
}
