package com.example.healthcare.BleDevices;

import static com.example.healthcare.BleDevices.ECGMeter.ECG_INCREMENT_NUMBER;
import static com.example.healthcare.BleDevices.ECGMeter.ECG_UUID_SERVICE;
import static com.example.healthcare.BleDevices.ECGMeter.ECG_UUID_WRITE;
import static com.example.healthcare.BluetoothModule.MyBluetoothGattCallback.ecgMeterDelayWriteMethod;
import static com.example.healthcare.Converters.ConverterClass.convertDateToHex;
import static com.example.healthcare.DatePicker.CurrentDateTime.*;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.healthcare.BleDevices.CRC.CRCUtil;
import com.example.healthcare.Converters.ConverterClass;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class BloodGlucometer {
    public static String BLOOD_GLUCOMETER_DEVICE_NAME1 = "Vivaguard";
    public static String BLOOD_GLUCOMETER_DEVICE_NAME2 = "VivaGuard";
    public static String BLOOD_GLUCOMETER_MAC_ADDRESS = "34:B9:1F:00:6F:59";
    public static String BLOOD_GLUCOMETER_UUID_NOTIFY = "0003cdd1-0000-1000-8000-00805f9b0131";
    public static String BLOOD_GLUCOMETER_UUID_NOTIFY_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";
    public static String BLOOD_GLUCOMETER_UUID_SERVICE = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static String BLOOD_GLUCOMETER_UUID_WRITE_SERIALNUMBER = "0003cdd2-0000-1000-8000-00805f9b0131";
    public static String BLOOD_GLUCOMETER_RESULT = null;
    public static String BLOOD_GLUCOMETER_RESULT_DATE_TIME = null;
    public static final byte[] BLOOD_GLUCOMETER_SERIAL_NUMBER_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x77, 0x55, 0x00, 0x00, 0x01, 0x0B, 0x0B, 0x04, 0x7D};
    public static final byte[] BLOOD_GLUCOMETER_STRIP_IN_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x12, (byte) 0x99, 0x00, 0x00, 0x0C, 0x05, 0x04, 0x07, 0x7D};
    public static final byte[] BLOOD_GLUCOMETER_READ_TIME_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x44, 0x55, 0x00, 0x00, 0x01, 0x04, 0x0F, 0x00, 0x7D};
    public static final byte[] TIME_SET_BYTE_ARRAY = {0x7B, 0x01, 0x10, 0x01, 0x20, 0x44, 0x66, 0x00, 0x06, 0x10, 0x07, 0x0B, 0x0F, 0x32, 0x2A, 0x07, 0x04, 0x03, 0x08, 0x7D};
    public static byte[] BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY;


    private static final String TAG = "TAGi";


    //on characteristics change method
    public static void onCharacteristicChangedMethodBloodGlucometer(byte[] byteArray, BluetoothGatt gatt) {
        Log.w(TAG, "onCharacteristicChangedMethodBloodGlucometer: " + ConverterClass.byteToHexadecimal(byteArray, true));
        List<String> pairs = ConverterClass.getPairsFromHexString(byteArray);

            String firstValue = pairs.get(0);
            String lastValue = pairs.get(pairs.size() - 1);
            String address = pairs.get(1) + " " + pairs.get(2) + " " + pairs.get(3) + " " + pairs.get(4);
            String protocoalCode = pairs.get(5) + " " + pairs.get(6);
            String frameLength = pairs.get(8);

            //check first value is 7b or 7B
            if (firstValue.equalsIgnoreCase("7B") && lastValue.equalsIgnoreCase("7D") && address.equalsIgnoreCase("01 20 01 10")) {
                BLOOD_GLUCOMETER_RESULT = "please Insert the Strip..";

                //check device ready for test by protocoal code
                if (protocoalCode.equalsIgnoreCase("12 66")) {

                    //ready for test
                    if (frameLength.equalsIgnoreCase("05")) {
                        String crcValue = pairs.get(14) + " " + pairs.get(15) + " " + pairs.get(16) + " " + pairs.get(17);
                        String lastTestResult_HexaDecimal_FirstBit = pairs.get(10) ;
                        String lastTestResult_HexaDecimal_SecondBit = pairs.get(11);

                        //check for last test result
                        if (pairs.get(9).equals("10")) {
                            //strip in
                            Log.w(TAG, "please Insert the Strip..");
                            BLOOD_GLUCOMETER_RESULT = "please Insert the Strip..";

                        } else if (pairs.get(9).equals("11") && pairs.get(13).equals("00")) {
                            //Test Ready
                            Log.w(TAG, "Strip is Valid. your device is ready for the test");
                            BLOOD_GLUCOMETER_RESULT = "Strip is Valid. your device is ready for the test";

                        } else if (pairs.get(9).equals("22") && pairs.get(13).equals("00")) {
                            //Blood in
                            Log.w(TAG, "please Insert the Blood in the Strip..");
                            BLOOD_GLUCOMETER_RESULT = "please Insert the Blood in the Strip..";

                        } else if (pairs.get(9).equals("33") && pairs.get(13).equals("00")) {
                            //calculate result
                            Log.w(TAG, "please wait..");
                            BLOOD_GLUCOMETER_RESULT = "please wait..";

                        } else if (pairs.get(9).equals("44") && pairs.get(13).equals("00")) {
                            //fetch  result
                            fetchLastTestResultMethod(lastTestResult_HexaDecimal_FirstBit,lastTestResult_HexaDecimal_SecondBit);

                        } else if (pairs.get(9).equals("55") && pairs.get(13).equals("02")) {
                            //Error
                            Log.w(TAG, "Blood Glucometer Strip Error ");
                            BLOOD_GLUCOMETER_RESULT = "Blood Glucometer Strip Error ";
                        } else {
                            Log.w(TAG, "Blood Glucometer other Result set..");
                        }
                    } else {
                        Log.w(TAG, "Blood Glucometer other frame length..");
                    }
                } else if (protocoalCode.equalsIgnoreCase("44 99")) {
                    // set time protocoal
                    Log.w(TAG, "Blood Glucometer setting Time sucessfully..");

                } else if (protocoalCode.equalsIgnoreCase("D2 66")) {
                    // Disconnect protocoal
                    Log.w(TAG, "Blood Glucometer disconnected..");
                    BLOOD_GLUCOMETER_RESULT = "Blood Glucometer disconnected..";
                } else if (protocoalCode.equalsIgnoreCase("44 AA")) {
                    // Result DateTime protocoal
                    if (frameLength.equals("06")) {
                        String dateAndTime = pairs.get(9) + " " + pairs.get(10) + " " + pairs.get(11) + " " + pairs.get(12) + " " + pairs.get(13) + " " + pairs.get(14);
                        Log.w(TAG, "Blood Glucometer Result DateTime : " + ConverterClass.decodeHexDateTime(dateAndTime));
                        BLOOD_GLUCOMETER_RESULT_DATE_TIME = "Blood Glucometer Result DateTime : " + ConverterClass.decodeHexDateTime(dateAndTime);
                    }
                } else {
                    Log.w(TAG, "Blood Glucometer Other protocoal code..");
                }

            } else {
                Log.w(TAG, "Please Connect Our Valid Device... ");
            }


    }


    //get result value here
    public static void fetchLastTestResultMethod(String lastTestResultHexaDecimal_FirstBit, String lastTestResult_HexaDecimal_SecondBit) {
        String lastTestResult_Decimal_FirstBit = String.valueOf(ConverterClass.hexadecimalToDecimal(lastTestResultHexaDecimal_FirstBit));
        String lastTestResult_Decimal_SecondBit = String.valueOf(ConverterClass.hexadecimalToDecimal(lastTestResult_HexaDecimal_SecondBit));
        String finalOutput = lastTestResult_Decimal_FirstBit + lastTestResult_Decimal_SecondBit;

        Log.d(TAG, "lastTestResult_Decimal(mg/dL): " + finalOutput);
        BLOOD_GLUCOMETER_RESULT = "Blood Glucometer Reading: "+finalOutput+ "mg/dL";
    }


    //set dynamic datetime in byte array
    public static void setCurrentDateTimeInByteArray() {

        String hexDateTime = convertDateToHex(getCurrentDate(), getCurrentTime());
        List<String> values = ConverterClass.getValuesFromPairs(hexDateTime);

        //Seperate Byte values
        byte header = 0x7B;
        byte[] address = {0x01, 0x10, 0x01, 0x20};
        byte[] protocolCode = {0x44, 0x66};
        byte[] frameLength = {0x00, 0x06};
        byte[] dateTime = {(byte) Integer.parseInt(values.get(2), 16), //year
                (byte) Integer.parseInt(values.get(1), 16), //month
                (byte) Integer.parseInt(values.get(0), 16), //day
                (byte) Integer.parseInt(values.get(3), 16), //Hour
                (byte) Integer.parseInt(values.get(4), 16), //Minute
                (byte) Integer.parseInt(values.get(5), 16)}; //second};
        byte footer = 0x7D;

        // Set CRC in Time and date Byte Array
        byte[] setCrcByteArray = setCrcTimeDateMethod(address, protocolCode, frameLength, dateTime);

        // Create the final array
        BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY = new byte[1 + setCrcByteArray.length + 1];

        // Set the header and footer
        BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY[0] = header;
        BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY[BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY.length - 1] = footer;

        // Copy the contents from setCrcByteArray
        System.arraycopy(setCrcByteArray, 0, BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY, 1, setCrcByteArray.length);

        Log.d(TAG, "setCurrentDateTimeInByteArray: " + ConverterClass.byteToHexadecimal(BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY, true));
    }


    //set CRC for the byte array
    private static byte[] setCrcTimeDateMethod(byte[] address, byte[] protocolCode, byte[] frameLength, byte[] dateTime) {
        int length = address.length + protocolCode.length + frameLength.length + dateTime.length;
        ByteBuffer combinedData = ByteBuffer.allocate(length);
        combinedData.put(address);
        combinedData.put(protocolCode);
        combinedData.put(frameLength);
        combinedData.put(dateTime);

        Log.d(TAG, "setCrcTimeDateMethod: " + ConverterClass.byteToHexadecimal(combinedData.array(), true));

        // Calculate CRC16 value
        int crcValue = CRCUtil.calcCrc16(combinedData.array());

        int o3 = (crcValue >> 12) & 0xF;
        int o4 = (crcValue >> 8) & 0xF;
        int o1 = (crcValue >> 4) & 0xF;
        int o2 = crcValue & 0xF;
        Log.d(TAG, String.format("CRC Result: %X%X%X%X", o1, o2, o3, o4));
        Log.d(TAG, String.format("CRC Result: %X", o1));
        Log.d(TAG, String.format("CRC Result: %X", o2));
        Log.d(TAG, String.format("CRC Result: %X", o3));
        Log.d(TAG, String.format("CRC Result: %X", o4));

        //convert input to Byte array
        byte[] crcBytes = new byte[4];
        crcBytes[2] = (byte) ((crcValue >> 12) & 0xF);
        crcBytes[3] = (byte) ((crcValue >> 8) & 0xF);
        crcBytes[0] = (byte) ((crcValue >> 4) & 0xF);
        crcBytes[1] = (byte) (crcValue & 0xF);

        // Append CRC bytes
        length = address.length + protocolCode.length + frameLength.length + dateTime.length + crcBytes.length;
        combinedData = ByteBuffer.allocate(length);
        combinedData.put(address);
        combinedData.put(protocolCode);
        combinedData.put(frameLength);
        combinedData.put(dateTime);
        combinedData.put(crcBytes);

        return combinedData.array();
    }



}
