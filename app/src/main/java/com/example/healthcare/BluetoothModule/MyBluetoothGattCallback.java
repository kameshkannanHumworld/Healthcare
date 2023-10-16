package com.example.healthcare.BluetoothModule;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;

import static com.example.healthcare.BleDevices.BloodGlucometer.onCharacteristicChangedMethodBloodGlucometer;
import static com.example.healthcare.BleDevices.UrionBp.*;
import static com.example.healthcare.BleDevices.UrionBp.onCharacteristicChangedMethodUrionBp;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.deviceConnected;

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

import androidx.annotation.NonNull;

import com.example.healthcare.Converters.ConverterClass;
import com.example.healthcare.DeviceInfoActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.UUID;

public class MyBluetoothGattCallback extends BluetoothGattCallback {
    BluetoothGatt bluetoothGatt;


    private static final String TAG = "TAGi";

    private static final int REQUEST_CODE = 100;
    public static final int RESULT_REFRESH = 101;
    private Handler handler = new Handler();

    public static final int REQUEST_CODE_DEVICE_INFO = 101;
    Context context;

    public MyBluetoothGattCallback(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Activity activity = (Activity) context;
        String deviceAddress = gatt.getDevice().getAddress();

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // Device connected, start discovering services
            Log.w(TAG, "Sucessfully connected to " + deviceAddress);
            deviceConnected = true;

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
            deviceConnected = false;
            if ((activity.getCurrentFocus()) != null) {
                Snackbar.make((activity.getCurrentFocus()), "Device Disconnected", Snackbar.LENGTH_SHORT).show();
            }
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

            //intent to Device information Activity
            Intent intent = new Intent(context, DeviceInfoActivity.class);
            intent.putExtra("DEVICE_NAME", gatt.getDevice().getName());
            context.startActivity(intent);
            ((Activity) context).getParentActivityIntent();

            //Get Readings Method
            printGattTable(gatt);
        }
    }

    @SuppressLint("MissingPermission")
    private void printGattTable(BluetoothGatt gatt) {
        if (gatt.getServices().isEmpty()) {
            Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?");
            return;
        }

        // Urion Blood pressure
        if (gatt.getDevice().getName() != null && gatt.getDevice().getName().equals(URION_BP_DEVICE_NAME)) {
            BluetoothGattCharacteristic urionBpcharacteristic = gatt.getService(UUID.fromString(URION_BP_UUID_SERVICE)).getCharacteristic(UUID.fromString(URION_BP_UUID_NOTIFY));
            gatt.setCharacteristicNotification(urionBpcharacteristic, true);
            BluetoothGattDescriptor descriptor = urionBpcharacteristic.getDescriptor(UUID.fromString(URION_BP_UUID_NOTIFY));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        //Blood Glucometer
        if (gatt.getDevice().getName() != null && (gatt.getDevice().getName().equals(BLOOD_GLUCOMETER_DEVICE_NAME1) || gatt.getDevice().getName().equals(BLOOD_GLUCOMETER_DEVICE_NAME2))) {
            //Notify
            BluetoothGattCharacteristic bloodGlucometerNotifyCharacteristic = gatt.getService(UUID.fromString(BLOOD_GLUCOMETER_UUID_SERVICE)).getCharacteristic(UUID.fromString(BLOOD_GLUCOMETER_UUID_NOTIFY));
            if (bloodGlucometerNotifyCharacteristic != null) {
                gatt.setCharacteristicNotification(bloodGlucometerNotifyCharacteristic, true);
                BluetoothGattDescriptor descriptor = bloodGlucometerNotifyCharacteristic.getDescriptor(UUID.fromString(BLOOD_GLUCOMETER_UUID_NOTIFY_DESCRIPTOR));
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                } else {
                    Log.d(TAG, "Descriptor is null");
                }
            } else {
                Log.d(TAG, "Notify Characteristic is null");
            }

            //check notification enabled
            assert bloodGlucometerNotifyCharacteristic != null;
            BluetoothGattDescriptor descriptor = bloodGlucometerNotifyCharacteristic.getDescriptor(UUID.fromString(BLOOD_GLUCOMETER_UUID_NOTIFY_DESCRIPTOR));
            if (descriptor != null) {
                boolean isNotificationEnabled = descriptor.getValue()[0] == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE[0];

                if (isNotificationEnabled) {
                    Log.d(TAG, "Notifications are enabled");

                    //write characteristics for time
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            writeCharacteristicBloodGlucometer(gatt);
                        }
                    }, 5000); // Delay for 4 seconds before writing


                } else {
                    Log.d(TAG, "Notifications are disabled");
                }

            } else {
                Log.d(TAG, "CCC descriptor is null");
            }


        }

        //use the below method to understand the UUID read/write/notify
        DeviceCharacteristicsMethod(gatt);

    }

    @SuppressLint("MissingPermission")
    private void writeCharacteristicBloodGlucometer(BluetoothGatt gatt) {
        BluetoothGattCharacteristic writeCharacteristic = getWriteCharacteristic(gatt);
        assert writeCharacteristic != null;
        setCurrentDateTimeInByteArray();
        writeCharacteristic.setValue(BLOOD_GLUCOMETER_TIME_SET_BYTE_ARRAY);
        boolean isWrite = gatt.writeCharacteristic(writeCharacteristic);
        gatt.setCharacteristicNotification(writeCharacteristic, true);

        if (isWrite) {
            Log.w(TAG, "writeCharacteristic Time: success");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Now Send result command to device
                    writeCharacteristic.setValue(BLOOD_GLUCOMETER_STRIP_IN_BYTE_ARRAY);
                    boolean isWriteStripIn = gatt.writeCharacteristic(writeCharacteristic);
                    gatt.setCharacteristicNotification(writeCharacteristic, true);
                    if (isWriteStripIn) {
                        Log.w(TAG, "writeCharacteristic Result : success");
                    } else {
                        Log.w(TAG, "writeCharacteristic Result : Fail");
                    }
                }
            }, 5000); // Delay for 5 seconds

        } else {
            Log.w(TAG, "writeCharacteristic Time: fail");
        }
    }


    @SuppressLint("MissingPermission")
    private BluetoothGattCharacteristic getWriteCharacteristic(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(UUID.fromString(BLOOD_GLUCOMETER_UUID_SERVICE));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(BLOOD_GLUCOMETER_UUID_WRITE_SERIALNUMBER));


            if (characteristic != null) {
                return characteristic;
            } else {
                Log.e(TAG, "Write characteristic not found.");
            }
        } else {
            Log.e(TAG, "Service not found.");
        }
        return null;
    }

    @Override
    public void onCharacteristicRead(@NonNull BluetoothGatt
                                             gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
        super.onCharacteristicRead(gatt, characteristic, value, status);
        byte[] valueRead = characteristic.getValue();
        Log.d(TAG, "onCharacteristicRead: " + Arrays.toString(valueRead));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        Log.w(TAG, "onCharacteristicWrite " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Write successful
            Log.d(TAG, "Characteristic " + characteristic.getUuid() + " written");
            Log.d(TAG, "onCharacteristicWrite: " + ConverterClass.byteToHexadecimal(characteristic.getValue(), true));
        } else {
            // Handle write failure
            Log.d(TAG, "onCharacteristicWrite failed");
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        byte[] byteArray = characteristic.getValue();

        //Urion Bp
        if ((gatt.getDevice().getName()).equals(URION_BP_DEVICE_NAME)) {
            onCharacteristicChangedMethodUrionBp(byteArray);
        }

        //BloodGlucoMeter
        if ((gatt.getDevice().getName()).equals(BLOOD_GLUCOMETER_DEVICE_NAME1) || (gatt.getDevice().getName()).equals(BLOOD_GLUCOMETER_DEVICE_NAME2)) {
            if (characteristic.getUuid().equals(UUID.fromString(BLOOD_GLUCOMETER_UUID_NOTIFY))) {
                onCharacteristicChangedMethodBloodGlucometer(byteArray);
            }
        }


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

    @SuppressLint("MissingPermission")
    public void disconnectDevice() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            deviceConnected = false;
        }
    }


}
