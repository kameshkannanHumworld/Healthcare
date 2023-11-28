package com.example.healthcare.BluetoothModule;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;

import static com.example.healthcare.BleDevices.BloodGlucometer.onCharacteristicChangedMethodBloodGlucometer;
import static com.example.healthcare.BleDevices.ECGMeter.*;
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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.healthcare.BleDevices.ECGMeter;
import com.example.healthcare.Converters.ConverterClass;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MyBluetoothGattCallback extends BluetoothGattCallback {
    BluetoothGatt bluetoothGatt;


    private static final String TAG = "TAGi";

    private static final int REQUEST_CODE = 100;
    public static final int RESULT_REFRESH = 101;
    private Handler handler = new Handler();

    public static final int REQUEST_CODE_DEVICE_INFO = 101;
    Context context;


    //contructor [ params1 - context ]
    public MyBluetoothGattCallback(Context context) {
        this.context = context;
    }


        @SuppressLint("MissingPermission")
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Activity activity = (Activity) context;
        String deviceAddress = gatt.getDevice().getAddress();


        //Bluetooth connected here
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // Device connected, start discovering services
            Log.w(TAG, "Sucessfully connected to " + deviceAddress);
            deviceConnected = true;  //set connected flag

            bluetoothGatt = gatt;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //discoverServices will call the override method
                    bluetoothGatt.discoverServices();
                }
            });
        }

        //bluetooth disconnected here
        else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // Device disconnected, handle accordingly
            Log.d(TAG, "onConnectionStateChange: STATE_DISCONNECTED " + deviceAddress);
            deviceConnected = false; //Set connected Flag
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

//            //Device information Activity from Intent - only for testing purpose
//            Intent intent = new Intent(context, DeviceInfoActivity.class);
//            intent.putExtra("DEVICE_NAME", gatt.getDevice().getName());
//            context.startActivity(intent);
//            ((Activity) context).getParentActivityIntent();

            //Get Readings Method
            bleDevicesConnection(gatt);
        }
    }

    @SuppressLint("MissingPermission")
    private void bleDevicesConnection(BluetoothGatt gatt) {
        if (gatt.getServices().isEmpty()) {
            Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?");
            return;
        }

        //gatt table
//        discoverServicesLoopMethod(gatt);

        //use the below method to understand the UUID read/write/notify
//        DeviceCharacteristicsMethod(gatt);

        // Urion Blood pressure
        urionBloodPressureMethod(gatt);

        // Blood Glucometer
        BloodGlucometerMethod(gatt);

        // ECG meter
        ecgMeterMethod(gatt);

    }

    @SuppressLint("MissingPermission")
    private void BloodGlucometerMethod(BluetoothGatt gatt) {
        if (gatt.getDevice().getName() != null && (BLOOD_GLUCOMETER_DEVICE_NAME.contains(gatt.getDevice().getName()))) {
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
                    }, 500); // Delay for 0.5 seconds before writing


                } else {
                    Log.d(TAG, "Notifications are disabled");
                }

            } else {
                Log.d(TAG, "CCC descriptor is null");
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void ecgMeterMethod(BluetoothGatt gatt) {
        if (gatt.getDevice().getName() != null && (ECG_DEVICE_NAME.contains(gatt.getDevice().getName()))) {
            //Notify
            BluetoothGattCharacteristic ecgMeterNotifyCharacteristic = gatt.getService(UUID.fromString(ECG_UUID_SERVICE)).getCharacteristic(UUID.fromString(ECG_UUID_NOTIFY));
            if (ecgMeterNotifyCharacteristic != null) {
                gatt.setCharacteristicNotification(ecgMeterNotifyCharacteristic, true);
                BluetoothGattDescriptor descriptor = ecgMeterNotifyCharacteristic.getDescriptor(UUID.fromString(ECG_UUID_NOTIFY_DESCRIPTOR));
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
            assert ecgMeterNotifyCharacteristic != null;
            BluetoothGattDescriptor descriptor = ecgMeterNotifyCharacteristic.getDescriptor(UUID.fromString(ECG_UUID_NOTIFY_DESCRIPTOR));
            if (descriptor != null) {
                boolean isNotificationEnabled = descriptor.getValue()[0] == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE[0];

                if (isNotificationEnabled) {
                    Log.d(TAG, "Notifications are enabled");

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //write characteristics
                            writeCharacteristicEcgMeter(gatt);
                        }
                    }, 500); // Delay for 5 seconds before writing


                } else {
                    Log.d(TAG, "Notifications are disabled");
                }

            } else {
                Log.d(TAG, "CCC descriptor is null");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void writeCharacteristicEcgMeter(BluetoothGatt gatt) {
        BluetoothGattCharacteristic writeCharacteristic = getWriteCharacteristicEcgMeter(gatt);
        assert writeCharacteristic != null;

        //writing first command - Date time
        dynamicTimeDateEcgMeter();
        if (ecgMeterDelayWriteMethod(ECG_SET_TIME_ARRAY, writeCharacteristic, gatt) == 0) {
            Log.w(TAG, "writeCharacteristic_EcgMeter Time set Sucessfully" );

            //writing second command - Switch device
            if (ecgMeterDelayWriteMethod(ECG_SET_SWITCH_DEVICE, writeCharacteristic, gatt) == 0) {
                Log.w(TAG, "writeCharacteristic_EcgMeter switching device set Sucessfully" );

                //writing third command - Real time data
                byte[] thirdData = {(byte) 0xA5, 0x08, (byte) ~0x08, 0x00, ECG_INCREMENT_NUMBER, 0x00, 0x00,0x38};
                int i = ecgMeterDelayWriteMethod(thirdData, writeCharacteristic, gatt);
                Log.w(TAG, "writeCharacteristic_EcgMeter Time set Sucessfully - "+i );


            }else{
                Log.d(TAG, "writeCharacteristic_EcgMeter Switch device command failed");
            }

        } else {
            Log.d(TAG, "writeCharacteristic_EcgMeter Data time command failed");
        }

    }



    @SuppressLint("MissingPermission")
    public static int ecgMeterDelayWriteMethod(byte[] byteArray, BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        final int[] result = new int[1]; // Initialize an array to hold the result
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                characteristic.setValue(byteArray);
                boolean isWrite = gatt.writeCharacteristic(characteristic);
//                gatt.setCharacteristicNotification(characteristic, true);
                byte[] logOutput = new byte[]{byteArray[1]};
                if (isWrite) {
                    Log.w(TAG, "writeCharacteristic command ECG: success- " + ConverterClass.byteToHexadecimal(logOutput,true));
                    result[0] = 0; // Success
                } else {
                    Log.w(TAG, "writeCharacteristic command ECG: fail- " + ConverterClass.byteToHexadecimal(logOutput,true));
                    result[0] = 1; // Failure
                }
            }
        }, 500); // Delay for 0.5 seconds before writing

        return result[0]; // Return the result
    }


    private BluetoothGattCharacteristic getWriteCharacteristicEcgMeter(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(UUID.fromString(ECG_UUID_SERVICE));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(ECG_UUID_WRITE));


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

    @SuppressLint("MissingPermission")
    private static void urionBloodPressureMethod(BluetoothGatt gatt) {
        if (gatt.getDevice().getName() != null && gatt.getDevice().getName().equals(URION_BP_DEVICE_NAME.get(0))) {
            BluetoothGattCharacteristic urionBpcharacteristic = gatt.getService(UUID.fromString(URION_BP_UUID_SERVICE)).getCharacteristic(UUID.fromString(URION_BP_UUID_NOTIFY));
            gatt.setCharacteristicNotification(urionBpcharacteristic, true);
            BluetoothGattDescriptor descriptor = urionBpcharacteristic.getDescriptor(UUID.fromString(URION_BP_UUID_NOTIFY));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }
    }

    private static void discoverServicesLoopMethod(BluetoothGatt gatt) {
        for (BluetoothGattService service : gatt.getServices()) {
            StringBuilder characteristicsTable = new StringBuilder();
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                characteristicsTable.append("|--").append(characteristic.getUuid()).append("\n");

                UUID desiredUuid = UUID.fromString(ECGMeter.ECG_UUID_NOTIFY);

                // Check if the characteristic matches the desired UUID
                if (characteristic.getUuid().equals(desiredUuid)) {

                    // Get the descriptors for this characteristic
                    List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();

                    // Loop through the descriptors and do something with them
                    for (BluetoothGattDescriptor descriptor : descriptors) {
                        UUID descriptorUuid = descriptor.getUuid();
                        Log.d(TAG, "Descriptor UUID: " + descriptorUuid.toString());
                    }
                } else {
                    Log.d(TAG, "Characteristic does not match the desired UUID");
                }
            }
            Log.i("printGattTable", "\nService " + service.getUuid() + "\nCharacteristics:\n" + characteristicsTable.toString());
        }
    }

    @SuppressLint("MissingPermission")
    private void writeCharacteristicBloodGlucometer(BluetoothGatt gatt) {
        BluetoothGattCharacteristic writeCharacteristic = getWriteCharacteristicBloodGlucometer(gatt);
        assert writeCharacteristic != null;
        setCurrentDateTimeInByteArray(); //dynamic date and time method
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
            }, 500); // Delay for 5 seconds

        } else {
            Log.w(TAG, "writeCharacteristic Time: fail");
        }
    }


    @SuppressLint("MissingPermission")
    private BluetoothGattCharacteristic getWriteCharacteristicBloodGlucometer(BluetoothGatt gatt) {
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
    public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
        super.onCharacteristicRead(gatt, characteristic, value, status);
        byte[] valueRead = characteristic.getValue();
        Log.w(TAG, "onCharacteristicRead: " + Arrays.toString(valueRead));
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
        if ((gatt.getDevice().getName()).equals(URION_BP_DEVICE_NAME.get(0))) {
            onCharacteristicChangedMethodUrionBp(byteArray,gatt);
        }

        //BloodGlucoMeter
        else if (BLOOD_GLUCOMETER_DEVICE_NAME.contains(gatt.getDevice().getName())) {
            if (characteristic.getUuid().equals(UUID.fromString(BLOOD_GLUCOMETER_UUID_NOTIFY))) {
                onCharacteristicChangedMethodBloodGlucometer(byteArray,gatt,context);
            }
        }

        //ECG meter
        else if (ECG_DEVICE_NAME.contains(gatt.getDevice().getName())) {
            if (characteristic.getUuid().equals(UUID.fromString(ECG_UUID_NOTIFY))) {
                onCharacteristicChangedMethodEcgMeter(byteArray,gatt);
            }
        }


        //Handle others
        else{
            Log.d(TAG, "onCharacteristicChanged: other devices");
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


}
