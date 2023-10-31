package com.example.healthcare.BleDevices;

import android.annotation.SuppressLint;

import static com.example.healthcare.BluetoothModule.MyBluetoothGattCallback.*;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.healthcare.Converters.ConverterClass;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class UrionBp {
    public static final String URION_BP_UUID_SERVICE = ("0000FFF0-0000-1000-8000-00805F9B34FB");
    public static final String URION_BP_UUID_NOTIFY = "0000FFF1-0000-1000-8000-00805F9B34FB";
    public static final String URION_BP_UUID_WRITE = "0000FFF2-0000-1000-8000-00805F9B34FB";
    public static final String URION_BP_DEVICE_NAME = "Bluetooth BP";
    public static String URION_BP_DEVICE_ERROR_MESSAGES = null;
    public static String DEVICE_INFO_CLASS_SET_TEXT = null;
    public static Integer URION_BP_SYSTOLIC_READINGS = null;
    public static Integer URION_BP_DIASTOLIC_READINGS = null;
    public static Integer URION_BP_PULSE_READINGS = null;


    private static final String TAG = "TAGi";
    private static  BluetoothGatt bluetoothGattUrionBp;


    //Urion Bp  onCharacteristicChanged method
    public static void onCharacteristicChangedMethodUrionBp(byte[] byteArray, BluetoothGatt gatt) {
        bluetoothGattUrionBp = gatt;
        if (Objects.equals(ConverterClass.byteToHexadecimal(byteArray, false), "a5")) {
            DEVICE_INFO_CLASS_SET_TEXT = "Please Start the Device to take reading";
            URION_BP_DEVICE_ERROR_MESSAGES = null;
        } else {
            DEVICE_INFO_CLASS_SET_TEXT = "Please wait Device is Taking reading";
            URION_BP_DEVICE_ERROR_MESSAGES = null;

//            Log.d(TAG, "onCharacteristicChangedMethodUrionBp: " + ConverterClass.getPairsFromHexString(byteArray));
            List<String> pairs = ConverterClass.getPairsFromHexString(byteArray);

            //Reading value
            for (int i = 0; i < pairs.size(); i++) {

                //Running Diastolic value
                if (Objects.equals(pairs.get(i), "fb")) {
                    URION_BP_DIASTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(4));
                }


                //constant value
                if (Objects.equals(pairs.get(i), "fc")) {
                    Log.w(TAG, "Systolic reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(3)));
                    Log.w(TAG, "Diastolic reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(4)));
                    Log.w(TAG, "Pulse reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(5)));

                    //Assign Value
                    URION_BP_SYSTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(3));
                    URION_BP_DIASTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(4));
                    URION_BP_PULSE_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(5));
                }
            }

            //EEPROM abnormal (E-E)
            if (Objects.equals(pairs.get(3), "0e")) {
                URION_BP_DEVICE_ERROR_MESSAGES = "please wear the CUFF again according to the instruction manual. \n" +
                        "Keep quiet and re-measure.";
            }

            //heartbeat signal is too small (E-1)
            if (Objects.equals(pairs.get(3), "01")) {
                URION_BP_DEVICE_ERROR_MESSAGES = "please wear the CUFF again according to the instruction manual. \n" +
                        "Keep quiet and re-measure.";
            }

            //Noise interference (E-2)
            if (Objects.equals(pairs.get(3), "02")) {
                URION_BP_DEVICE_ERROR_MESSAGES = "please wear the CUFF again according to the instruction manual. \n" +
                        "Keep quiet and re-measure.";
            }

            //Inflation time is too long (E-3)
            if (Objects.equals(pairs.get(3), "03")) {
                URION_BP_DEVICE_ERROR_MESSAGES = "please wear the CUFF again according to the instruction manual. \n" +
                        "Keep quiet and re-measure.";
            }

            //The measured result is abnormal (E-4)
            if (Objects.equals(pairs.get(3), "04")) {
                URION_BP_DEVICE_ERROR_MESSAGES = "please wear the CUFF again according to the instruction manual. \n" +
                        "Keep quiet and re-measure.";
            }

            //Correction anomalies (E-c)
            if (Objects.equals(pairs.get(3), "0c")) {
                URION_BP_DEVICE_ERROR_MESSAGES = "please wear the CUFF again according to the instruction manual. \n" +
                        "Keep quiet and re-measure.";
            }

            //The battery is low, please replace the battery(E-B)
            if (Objects.equals(pairs.get(3), "0b")) {
                URION_BP_DEVICE_ERROR_MESSAGES = "The battery is low, please replace the battery.";
            }
        }
    }

    public static  void urionBpDisconnectDeviceMethod(){
        BluetoothGattService service = bluetoothGattUrionBp.getService(UUID.fromString(URION_BP_UUID_SERVICE));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(URION_BP_UUID_WRITE));

            if (characteristic != null) {

                byte[] byteArray = {(byte) 0xFD, (byte) 0xFD, (byte) 0xFA, 0x06, 0x0D, 0x0A};
                characteristic.setValue(byteArray);
                boolean isWrite = bluetoothGattUrionBp.writeCharacteristic(characteristic);
                bluetoothGattUrionBp.setCharacteristicNotification(characteristic, true);
                Log.w(TAG, "Urion Bp Disconnected Sucessfully - "+isWrite  );

            } else {
                Log.e(TAG, "Write characteristic not found.");
            }

        } else {
            Log.e(TAG, "Service not found.");
        }
    }
}




