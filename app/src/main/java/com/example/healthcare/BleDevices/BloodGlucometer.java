package com.example.healthcare.BleDevices;

import android.util.Log;

import com.example.healthcare.Converters.ConverterClass;

import java.util.Objects;

public class BloodGlucometer {
    public static String BLOOD_GLUCOMETER_DEVICE_NAME = "VivaGuard";
    public static String BLOOD_GLUCOMETER_MAC_ADDRESS = "34:B9:1F:00:6F:59";
    public static String BLOOD_GLUCOMETER_UUID_NOTIFY = "0003cdd1-0000-1000-8000-00805f9b0131";
    public static String BLOOD_GLUCOMETER_UUID_NOTIFY_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";
    public static String BLOOD_GLUCOMETER_UUID_SERVICE = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static String BLOOD_GLUCOMETER_UUID_WRITE_SERIALNUMBER = "0003cdd2-0000-1000-8000-00805f9b0131";
    public static final byte[] BLOOD_GLUCOMETER_SERIAL_NUMBER_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x77, 0x55, 0x00, 0x00, 0x01, 0x0B, 0x0B, 0x04, 0x7D};
    public static final byte[] BLOOD_GLUCOMETER_STRIP_IN_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x12, (byte) 0x99, 0x00, 0x00, 0x0C, 0x05, 0x04, 0x07, 0x7D};
    public static final byte[] BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x44, 0x66, 0x00, 0x06, 0x10, 0x07, 0x0B, 0x0F, 0x32, 0x2A, 0x07, 0x04, 0x03, 0x08, 0x7D};
    public static final byte[] BLOOD_GLUCOMETER_READ_TIME_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x44, 0x55, 0x00, 0x00, 0x01, 0x04, 0x0F, 0x00, 0x7D};


//    public static final String VIVA_UUID_SERVICE_1 = "0003cdd0-0000-1000-8000-00805f9b0131";
//    public static final String VIVA_UUID_CHARACTERISTIC_1_NOTIFY = "0003cdd1-0000-1000-8000-00805f9b0131";
//    public static final String VIVA_UUID_CHARACTERISTIC_1_READ_WRITE = "0003cdd2-0000-1000-8000-00805f9b0131";

    private static final String TAG = "TAGi";

    public static void onCharacteristicChangedMethodBloodGlucometer(byte[] byteArray) {
        Log.d(TAG, "onCharacteristicChangedMethodBloodGlucometer: " + ConverterClass.byteToHexadecimal(byteArray, false));

    }
}
