package com.example.healthcare.BleDevices;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.healthcare.Converters.ConverterClass;
import com.example.healthcare.DeviceInfoActivity;

import java.util.Objects;

public class WeightScale {
    public static final String WEIGHT_SCALE_DEVICE_NAME = "WEIGHT_SCALE";
    public static final String WEIGHT_SCALE_UNIQUE_ID = "ffffff3003030733";
    public static final String WEIGHT_SCALE_MAC_ADDRESS = "6a00343867ed";
    public static final String WEIGHT_SCALE_CONSTANT_VALUE1 = "aa";
    public static final String WEIGHT_SCALE_CONSTANT_VALUE2 = "bb";
    public static Float WEIGHT_SCALE_READING = null;
    public static Boolean WEIGHT_SCALE_IS_CONNECTED = false;
    private static final String TAG = "TAGi";
    Context context;

    public WeightScale(Context context) {
        this.context = context;
    }

    public static double convertkgToLbs(double kg) {
        return kg * 2.20462;
    }

    public double kgToStone(double kg) {
        return kg * 0.157473;
    }



    public static void weightScaleReadingsMethod(ScanResult result, Context context) {
        String weightScaleByteArray = ConverterClass.byteToHexadecimal(Objects.requireNonNull(result.getScanRecord()).getBytes(), false);
//        Log.d(TAG, "weightScaleByteArray: " + weightScaleByteArray);
        String uniqueId = weightScaleByteArray.substring(8, 24);
        String macAddress = weightScaleByteArray.substring(24, 36);
        String constant = weightScaleByteArray.substring(36, 38);
        String reading = weightScaleByteArray.substring(41, 48);


        if (uniqueId.equals(WEIGHT_SCALE_UNIQUE_ID) && macAddress.equals(WEIGHT_SCALE_MAC_ADDRESS)) {
            //device discovered
            Log.d(TAG, "Weight Scale Device discovered ");

            //reading convert to decimal
            float decimalReading = (float) ConverterClass.hexadecimalToDecimal(reading);
            WEIGHT_SCALE_IS_CONNECTED = true;

            //Assign Values
            WEIGHT_SCALE_READING = decimalReading / 10; // in kg


            if (constant.equals(WEIGHT_SCALE_CONSTANT_VALUE1) || constant.equals(WEIGHT_SCALE_CONSTANT_VALUE2)) {

                //Assign Values
                WEIGHT_SCALE_READING = decimalReading / 10; // in kg

                //log
                Log.d(TAG, "uniqueId: " + uniqueId);
                Log.d(TAG, "macAddress: " + macAddress);
                Log.d(TAG, "constant: " + constant);
                Log.d(TAG, "reading Hexa: " + reading);
                Log.d(TAG, "reading decimal : " + WEIGHT_SCALE_READING);

            }
        }
    }
}
