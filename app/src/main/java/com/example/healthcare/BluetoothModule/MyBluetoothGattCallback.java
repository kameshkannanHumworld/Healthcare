package com.example.healthcare.BluetoothModule;

import static com.example.healthcare.BleDevices.UrionBp.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.healthcare.BottomSheetDialog.MyBottomSheetDialogFragment;
import com.example.healthcare.Converters.ConverterClass;
import com.example.healthcare.DeviceInfoActivity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MyBluetoothGattCallback extends BluetoothGattCallback {
    BluetoothGatt bluetoothGatt;
    MyBottomSheetDialogFragment bottomSheetDialogFragment;


    private static final String TAG = "TAGi";
    Context context;

    public MyBluetoothGattCallback(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        String deviceAddress = gatt.getDevice().getAddress();

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // Device connected, start discovering services
            Log.w(TAG, "Sucessfully connected to " + deviceAddress);

            bluetoothGatt = gatt;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    bluetoothGatt.discoverServices();
                }


            });
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // Device disconnected, handle accordingly
            Log.d(TAG, "onConnectionStateChange: STATE_DISCONNECTED " + deviceAddress);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Services discovered, you can now communicate with characteristics
            Log.d(TAG, "onConnectionStateChange: GATT_SUCCESS " + status);
            Log.d("BluetoothGattCallback", "Discovered " + gatt.getServices().size() + " services for " + gatt.getDevice().getAddress());
            Intent intent = new Intent(context, DeviceInfoActivity.class);
            context.startActivity(intent);
//            ((Activity)context).finish();
            ((Activity) context).getParentActivityIntent();
            printGattTable(gatt);


        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        byte[] byteArray = characteristic.getValue();

        List<String> pairs = ConverterClass.getPairsFromHexString(byteArray);

        if (pairs.size() > 5 && Objects.equals(pairs.get(2), "fc")) {
            Log.w(TAG, "Systolic reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(3)));
            Log.w(TAG, "Diastolic reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(4)));
            Log.w(TAG, "Pulse reading: " + ConverterClass.hexadecimalToDecimal(pairs.get(5)));

            //Assign Value
            URION_BP_SYSTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(3));
            URION_BP_DIASTOLIC_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(4));
            URION_BP_PULSE_READINGS = ConverterClass.hexadecimalToDecimal(pairs.get(5));
        }


//        Log.d(TAG, "onCharacteristicChanged: "+ ConverterClass.byteToHexadecimal(byteArray,true));
    }

    @SuppressLint("MissingPermission")
    private void printGattTable(BluetoothGatt gatt) {

        if (gatt.getServices().isEmpty()) {
            Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?");
            return;
        }

        for (BluetoothGattService service : gatt.getServices()) {
            StringBuilder characteristicsTable = new StringBuilder();
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                characteristicsTable.append("|--").append(characteristic.getUuid()).append("\n");

                gatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(URION_BP_UUID_NOTIFY));
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                } else {
                    // Handle the case where the descriptor is null
                    Log.d(TAG, "printGattTable: Descriptor is null");
                }


                Log.w(TAG, "printGattTable: --" + characteristic.getUuid());
            }
            Log.i("printGattTable", "\nService " + service.getUuid() + "\nCharacteristics:\n" + characteristicsTable.toString());
        }

        //use the below method to understand the UUID read/write/notify
//        DeviceCharacteristicsMethod(gatt);
    }

    private static void DeviceCharacteristicsMethod(BluetoothGatt gatt) {
        for (BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (BluetoothGattCharacteristicUtils.isReadable(characteristic)) {
                    Log.d(TAG, "Characteristic " + characteristic.getUuid() + " is readable.");
                }

                if (BluetoothGattCharacteristicUtils.isWritable(characteristic)) {
                    Log.d(TAG, "Characteristic " + characteristic.getUuid() + " is writable.");
                }

                if (BluetoothGattCharacteristicUtils.isWritableWithoutResponse(characteristic)) {
                    Log.d(TAG, "Characteristic " + characteristic.getUuid() + " is writable without response.");
                }

                if (BluetoothGattCharacteristicUtils.isNotify(characteristic)) {
                    Log.d(TAG, "Characteristic " + characteristic.getUuid() + " is Notify.");
                }
            }
        }
    }

}
