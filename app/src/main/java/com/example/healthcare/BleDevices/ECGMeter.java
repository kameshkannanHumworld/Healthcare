package com.example.healthcare.BleDevices;

import static com.example.healthcare.BluetoothModule.MyBluetoothGattCallback.ecgMeterDelayWriteMethod;
import static com.example.healthcare.Converters.ConverterClass.convertDateToHex;
import static com.example.healthcare.DatePicker.CurrentDateTime.getCurrentDateFullYear;
import static com.example.healthcare.DatePicker.CurrentDateTime.getCurrentTime;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.healthcare.BleDevices.CRC.CrcCalcEcg;
import com.example.healthcare.Converters.ConverterClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ECGMeter {
    private static final String TAG = "TAGi";
    public static byte ECG_INCREMENT_NUMBER = 0x02;
//    public static final String ECG_DEVICE_NAME1 = "BP2 1875";
//    public static final String ECG_DEVICE_NAME2 = "BP2 1840";
    public static final List<String> ECG_DEVICE_NAME= Arrays.asList("BP2 1875", "BP2 1840");
    public static final String ECG_UUID_SERVICE = ("14839ac4-7d7e-415c-9a42-167340cf2339");
    public static final String ECG_UUID_NOTIFY = ("0734594a-a8e7-4b1a-a6b1-cd5243059a57");
    public static final String ECG_UUID_NOTIFY_DESCRIPTOR = ("00002902-0000-1000-8000-00805f9b34fb");
    public static final String ECG_UUID_WRITE = ("8b00ace7-eb0b-49b0-bbe9-9aee0a26e1a3");
    public static byte[] ECG_SET_TIME_ARRAY;
    public static byte[] ECG_SET_SWITCH_DEVICE = {(byte) 0xA5, (byte) 0x09, (byte) ~0x09, 0x00, 0x01, 0x01, 0x00, 0x01, (byte) 0x08};
    public static byte[] ECG_SET_READING_CMD;
    private static  BluetoothGatt bluetoothGattECG;


    //On characteristics Method - ECG meter
    public static void onCharacteristicChangedMethodEcgMeter(byte[] byteArray, BluetoothGatt gatt) {
        bluetoothGattECG = gatt;
        Log.w(TAG, "onCharacteristicChangedMethod_EcgMeter: " + ConverterClass.byteToHexadecimal(byteArray, true));

        //append arrays
//        appendByteArray(byteArray);
//
//        byte[] combinedArray = getCombinedByteArray();
//        Log.w(TAG, "onCharacteristicChangedMethod_EcgMeter_CombinedArray: " + ConverterClass.byteToHexadecimal(combinedArray, true));
    }


    //dynamic data-Time set for device with CRC
    public static void dynamicTimeDateEcgMeter() {
        String hexDateTime = convertDateToHex(getCurrentDateFullYear(), getCurrentTime());
        List<String> values = ConverterClass.getValuesFromPairs(hexDateTime);
        Log.d(TAG, "dynamicTimeDateEcgMeter: " + hexDateTime);

        //seperate year and make it reverse (Ex: 7e7 - e707)
        String yearInput = values.get(2);
        List<String> l1 = ConverterClass.reverseHexaDecimal(yearInput);
//        Log.d(TAG, "dynamicTimeDateEcgMeter: "+l1.get(0));  //output - 07
//        Log.d(TAG, "dynamicTimeDateEcgMeter: "+l1.get(1));  //output - e7

        //Seperate Byte values
        byte header = (byte) 0xA5;
        byte[] dateTimeCommand = {(byte) 0xEC, (byte) ~0xEC};
        byte[] packageNumberAndType = {0x00, 0x00};
        byte[] datalength = {0x07, 0x00};
        byte[] dateTime = {(byte) Integer.parseInt(l1.get(1), 16), //year (one bit)
                (byte) Integer.parseInt(l1.get(0), 16), //year (another bit)
                (byte) Integer.parseInt(values.get(1), 16), //month
                (byte) Integer.parseInt(values.get(0), 16), //day
                (byte) Integer.parseInt(values.get(3), 16), //Hour
                (byte) Integer.parseInt(values.get(4), 16), //Minute
                (byte) Integer.parseInt(values.get(5), 16)}; //second};

        // Set CRC in Time and date Byte Array
        ECG_SET_TIME_ARRAY = setCrcTimeDateMethodEcgMeter(header, dateTimeCommand, packageNumberAndType, datalength, dateTime);

        //just print final output
        Log.d(TAG, "Final output dynamicTimeDateEcgMeter: " + ConverterClass.byteToHexadecimal(ECG_SET_TIME_ARRAY, true));

    }

    // Set CRC in Time and date Byte Array - Header byte(1 byte), dateTimeCommand with inverse cmd(2 byte), packageNumberAndType (2 byte), datalength(2byte) , dateTime(7 byte)
    private static byte[] setCrcTimeDateMethodEcgMeter(byte header, byte[] dateTimeCommand, byte[] packageNumberAndType, byte[] datalength, byte[] dateTime) {
        int length = 1 + dateTimeCommand.length + packageNumberAndType.length + datalength.length + dateTime.length;

        byte[] combinedArray = new byte[length];
        int index = 0;
        combinedArray[index++] = header;
        System.arraycopy(dateTimeCommand, 0, combinedArray, index, dateTimeCommand.length);
        index += dateTimeCommand.length;
        System.arraycopy(packageNumberAndType, 0, combinedArray, index, packageNumberAndType.length);
        index += packageNumberAndType.length;
        System.arraycopy(datalength, 0, combinedArray, index, datalength.length);
        index += datalength.length;
        System.arraycopy(dateTime, 0, combinedArray, index, dateTime.length);

        Log.d(TAG, "setCrcTimeDateMethod: " + ConverterClass.byteToHexadecimal(combinedArray, true));

        // Calculate CRC value
        byte crc_result = CrcCalcEcg.crc8_compute(combinedArray, length, (byte) 0x00);

        // Create the result array with  combinedArray, and CRC byte
        byte[] resultWithCRC = new byte[combinedArray.length + 1];
        System.arraycopy(combinedArray, 0, resultWithCRC, 0, combinedArray.length);
        resultWithCRC[resultWithCRC.length - 1] = crc_result;

        return resultWithCRC;
    }

    //Call this method to turn off the ECG device
    public static  void ecgDisconnectDeviceMethod(){
        BluetoothGattService service = bluetoothGattECG.getService(UUID.fromString(ECG_UUID_SERVICE));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(ECG_UUID_WRITE));

            if (characteristic != null) {

                byte[] thirdData = {(byte) 0xA5, (byte) 0x09, (byte) ~0x09, 0x00, 0x00, 0x01, 0x00, 0x04, (byte) 0x05};
                int i = ecgMeterDelayWriteMethod(thirdData, characteristic, bluetoothGattECG);
                Log.w(TAG, "writeCharacteristic_EcgMeter Disconnected Sucessfully - "+i );

            } else {
                Log.e(TAG, "Write characteristic not found.");
            }

        } else {
            Log.e(TAG, "Service not found.");
        }
    }

//    private static void appendByteArray(byte[] byteArray) {
//        if (combinedArray == null) {
//            combinedArray = byteArray.clone();
//        } else {
//            byte[] tempArray = new byte[combinedArray.length + byteArray.length];
//            System.arraycopy(combinedArray, 0, tempArray, 0, combinedArray.length);
//            System.arraycopy(byteArray, 0, tempArray, combinedArray.length, byteArray.length);
//            combinedArray = tempArray;
//        }
//    }


}
