package com.example.healthcare.Permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class LocationUtil {

    public static final int REQUEST_ENABLE_LOCATION = 123;
    public static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 130;

    /*
        Method to check location enabled or not
            params1 - Activity
    */
    public static boolean isLocationEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /*
        Method to request enable location
            params1 - Activity
    */
    public static void requestLocationEnable(Activity activity) {
        if (!isLocationEnabled(activity)) {
//            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            activity.startActivityForResult(locationSettingsIntent, REQUEST_ENABLE_LOCATION);
            GpsUtil.requestLocationPermission(activity);

        }
    }


    /*
        Method to check ACCESS_FINE_LOCATION permission granted or not
            params1 - Activity
    */
    public static boolean isFineLocationPermissionGranted(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /*
        Method to request ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
            params1 - Activity
    */
    public static void requestFineLocationConnectPermission(Activity activity) {
        if (!isFineLocationPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public static void requestLocationEnableAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location Services Required");
        builder.setMessage("Please enable location services to proceed.");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }


}
