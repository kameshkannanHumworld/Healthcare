package com.example.healthcare.BleDevices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.example.healthcare.BluetoothModule.BluetoothGattCharacteristicUtils;
import com.example.healthcare.BluetoothModule.MyBluetoothGattCallback;
import com.example.healthcare.Permissions.BluetoothUtil;

import java.util.UUID;
@SuppressLint("MissingPermission")
public class UrionBp {
    private MyBluetoothGattCallback mGattCallback;
    public static final String URION_BP_UUID_SERVICE = ("0000FFF0-0000-1000-8000-00805F9B34FB");
    public static final String URION_BP_UUID_NOTIFY = "0000FFF1-0000-1000-8000-00805F9B34FB";
    public static final String URION_BP_UUID_WRITE = "0000FFF2-0000-1000-8000-00805F9B34FB";

    public static Integer URION_BP_SYSTOLIC_READINGS=null;
    public static Integer URION_BP_DIASTOLIC_READINGS=null;
    public static Integer URION_BP_PULSE_READINGS=null;
//    private BluetoothGatt mBluetoothGatt;
//
//    public void connectToDevice(BluetoothDevice device, Context context) {
//
//        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
//    }
//
//    public void disconnectDevice() {
//        if (mBluetoothGatt != null) {
//            mBluetoothGatt.disconnect();
//        }
//
//    }
}
