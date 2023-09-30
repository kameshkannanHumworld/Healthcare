package com.example.healthcare.BluetoothModule;

import android.bluetooth.BluetoothGattCharacteristic;

public class BluetoothGattCharacteristicUtils {

    public static boolean isReadable(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_READ);
    }
    public static boolean isNotify(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY);
    }
    public static boolean isWritable(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE);
    }

    public static boolean isWritableWithoutResponse(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
    }

    public static boolean containsProperty(BluetoothGattCharacteristic characteristic, int property) {
        return (characteristic.getProperties() & property) != 0;
    }
}
