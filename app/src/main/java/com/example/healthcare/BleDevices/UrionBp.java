package com.example.healthcare.BleDevices;

import android.annotation.SuppressLint;

import static com.example.healthcare.BluetoothModule.MyBluetoothGattCallback.*;

import android.app.Activity;
import android.util.Log;

import com.example.healthcare.BluetoothModule.MyBluetoothGattCallback;
import com.example.healthcare.Converters.ConverterClass;
import com.example.healthcare.DeviceInfoActivity;

import java.util.List;
import java.util.Objects;

@SuppressLint("MissingPermission")
public class UrionBp {
    public static final String URION_BP_UUID_SERVICE = ("0000FFF0-0000-1000-8000-00805F9B34FB");
    public static final String URION_BP_UUID_NOTIFY = "0000FFF1-0000-1000-8000-00805F9B34FB";
    public static final String URION_BP_UUID_WRITE = "0000FFF2-0000-1000-8000-00805F9B34FB";
    public static final String URION_BP_DEVICE_NAME = "BLUETOOTH BP";
    public static String DEVICE_INFO_CLASS_SET_TEXT = null;
    public static Integer URION_BP_SYSTOLIC_READINGS = null;
    public static Integer URION_BP_DIASTOLIC_READINGS = null;
    public static Integer URION_BP_PULSE_READINGS = null;


    private static final String TAG = "TAGi";




    //Urion Bp  onCharacteristicChanged method
    public static void onCharacteristicChangedMethodUrionBp(byte[] byteArray) {
        if (Objects.equals(ConverterClass.byteToHexadecimal(byteArray, false), "a5")) {
            DEVICE_INFO_CLASS_SET_TEXT = "Please Start the Device to take reading";
        } else {
            DEVICE_INFO_CLASS_SET_TEXT = "Please wait Device is Taking reading";
        }
        List<String> pairs = ConverterClass.getPairsFromHexString(byteArray);
        URION_BP_DIASTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(4));

        if (pairs.size() > 5 && Objects.equals(pairs.get(2), "fc")) {
            Log.w(TAG, "Systolic reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(3)));
            Log.w(TAG, "Diastolic reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(4)));
            Log.w(TAG, "Pulse reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(5)));

            //Assign Value
            URION_BP_SYSTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(3));
            URION_BP_DIASTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(4));
            URION_BP_PULSE_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(5));

        }
    }
}




