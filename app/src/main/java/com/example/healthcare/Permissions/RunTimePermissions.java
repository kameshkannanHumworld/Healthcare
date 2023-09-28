package com.example.healthcare.Permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class RunTimePermissions {

//    static Context context;
//    public RunTimePermissions(Context context) {
//        this.context = context;
//    }
//
//    public static  boolean hasRequiredRuntimePermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            return hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
//                    hasPermission(Manifest.permission.BLUETOOTH_CONNECT);
//        } else {
//            return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//    }
//
//    public static boolean hasPermission( String permissionType) {
//        return ContextCompat.checkSelfPermission(context, permissionType) ==
//                PackageManager.PERMISSION_GRANTED;
//    }
//


}
