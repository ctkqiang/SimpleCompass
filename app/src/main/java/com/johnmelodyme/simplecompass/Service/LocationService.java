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

@SuppressLint("Registered")
public class LocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "Compass";
    private static final long INTERVAL = 0x3e8 * 0x2;
    private static final long FASTEST_INTERVAL = 0x3e8;
    private final IBinder binder = new LocalBinder()
;    LocationRequest locationRequest;
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
    public void onLocationChanged(Location location) {
        //CompassActivity.
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
            return LocationService.this;
        }
    }
}
