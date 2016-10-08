package dev.roviloapps.hackupcfall2016.controllers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by LaQuay on 08/10/2016.
 */


public final class LocationController implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = LocationController.class.getSimpleName();
    // The minimum distance to change Updates in meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 5;
    private static LocationController instance;
    private final Context mContext;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private OnNewLocationCallback callback;
    private Location location;

    private LocationController(Context context) {
        this.mContext = context;
    }

    public static LocationController getInstance(Context ctx) {
        if (instance == null) {
            instance = new LocationController(ctx);
        }
        return instance;
    }

    public boolean startLocation(OnNewLocationCallback callback) {
        try {
            this.callback = callback;
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = new LocationRequest();

            // Sets the desired interval for active location updates.
            mLocationRequest.setInterval(MIN_TIME_BW_UPDATES);

            // Sets the fastest rate for active location updates.
            mLocationRequest.setFastestInterval(MIN_TIME_BW_UPDATES * 2);

            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mGoogleApiClient.connect();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void stopLocation() {
        if (mGoogleApiClient != null) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (location != null) {
            callback.onNewLocation(location);
            stopLocation();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected");
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectedSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectedFailed");
        Log.e(TAG, connectionResult.getErrorMessage() + " code: " + connectionResult.getErrorCode());
    }

    public interface OnNewLocationCallback {
        void onNewLocation(Location location);
    }
}