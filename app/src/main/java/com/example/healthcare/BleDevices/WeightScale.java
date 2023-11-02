package com.example.healthcare.BleDevices;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.healthcare.BluetoothModule.BluetoothScanner;
import com.example.healthcare.Converters.ConverterClass;

import java.util.Objects;

public class WeightScale {
    public static final String WEIGHT_SCALE_DEVICE_NAME = "WEIGHT_SCALE";
    public static final String WEIGHT_SCALE_UNIQUE_ID = "ffffff3003030733";
    public static final String WEIGHT_SCALE_MAC_ADDRESS = "6a00343867ed";
    public static final String WEIGHT_SCALE_CONSTANT_VALUE1 = "aa";
    public static final String WEIGHT_SCALE_CONSTANT_VALUE2 = "bb";
    public static Float WEIGHT_SCALE_READING = null;
    public static Boolean WEIGHT_SCALE_IS_CONNECTED = false;
    private static Boolean conditionAlreadyMet = false;
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

    /*
        get the weight scale readings here
            params1 - scanresult from BLE advertiser in scancallback class
            params2 - context
    */
    public static void weightScaleReadingsMethod(ScanResult result, Context context) {
        String weightScaleByteArray = ConverterClass.byteToHexadecimal(Objects.requireNonNull(result.getScanRecord()).getBytes(), false);
        String uniqueId = weightScaleByteArray.substring(8, 24);
        String macAddress = weightScaleByteArray.substring(24, 36);
        String constant = weightScaleByteArray.substring(36, 38);
        String reading = weightScaleByteArray.substring(41, 48);


        if (uniqueId.equals(WEIGHT_SCALE_UNIQUE_ID) && macAddress.equals(WEIGHT_SCALE_MAC_ADDRESS)) {
            String ByteArray = ConverterClass.byteToHexadecimal(Objects.requireNonNull(result.getScanRecord()).getBytes(), false);
            String reading1 = weightScaleByteArray.substring(41, 48);
            //device discovered
            WEIGHT_SCALE_IS_CONNECTED = true;
            Log.d(TAG, "Weight Scale Device discovered ");

            //reading convert to decimal
            float decimalReading = (float) ConverterClass.hexadecimalToDecimal(reading);

            //Assign Values
            WEIGHT_SCALE_READING = decimalReading / 10; // in kg

            //Condition to checking the constant reading value
            if (constant.equals(WEIGHT_SCALE_CONSTANT_VALUE1) || constant.equals(WEIGHT_SCALE_CONSTANT_VALUE2)) {

                //if condition working once
                if (!conditionAlreadyMet) {
                    //Assign Values
                    WEIGHT_SCALE_READING = decimalReading / 10; // in kg

                    //log
                    Log.d(TAG, "uniqueId: " + uniqueId);
                    Log.d(TAG, "macAddress: " + macAddress);
                    Log.d(TAG, "constant: " + constant);
                    Log.d(TAG, "reading Hexa: " + reading);
                    Log.d(TAG, "reading decimal : " + WEIGHT_SCALE_READING);


                    // disconnect device  after 15 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: weight scale post delay");
                            WEIGHT_SCALE_IS_CONNECTED = false;

                        }
                    }, 15000); // 15 seconds in milliseconds

                    //condition flag
                    conditionAlreadyMet = true;
                }else{
                    Log.d(TAG, "weightScaleReadingsMethod: conditionAlreadyMet else part");
                }
            }
        }
    }
}
