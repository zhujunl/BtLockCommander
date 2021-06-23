package com.miaxis.btlockcommander.manager;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.miaxis.btlockcommander.callback.BleIndicateCallback;
import com.miaxis.btlockcommander.callback.BleNotifyCallback;
import com.miaxis.btlockcommander.callback.BleReadCallback;
import com.miaxis.btlockcommander.callback.BleWriteCallback;

import java.util.UUID;

class BleConnector {

    private static final String UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    private BleBluetooth bleBluetooth;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService gattService;
    private BluetoothGattCharacteristic characteristic;

    BleConnector(BleBluetooth bleBluetooth) {
        this.bleBluetooth = bleBluetooth;
        this.bluetoothGatt = bleBluetooth.getBluetoothGatt();
    }

    BleConnector withUUID(String serviceUUID, String characteristicUUID) {
        return withUUID(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
    }

    private BleConnector withUUID(UUID serviceUUID, UUID characteristicUUID) {
        gattService = bluetoothGatt.getService(serviceUUID);
        if (gattService != null) {
            characteristic = gattService.getCharacteristic(characteristicUUID);
        }
        return this;
    }

    /**
     * 通知
     */
    boolean setCharacteristicNotify(BleNotifyCallback bleNotifyCallback, boolean enable, boolean userCharacteristicDescriptor) {
        if (characteristic != null && (characteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            bleBluetooth.setBleNotifyCallback(bleNotifyCallback);
            return setCharacteristicNotification(bluetoothGatt, characteristic, userCharacteristicDescriptor, enable, bleNotifyCallback);
        } else {
            bleNotifyCallback.onNotifyFailure("this characteristic is null or not support notify!");
            return false;
        }
    }

    /**
     * 设置通知
     */
    private boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean useCharacteristicDescriptor, boolean enable, BleNotifyCallback bleNotifyCallback) {
        if (!gatt.setCharacteristicNotification(characteristic, enable)) {
            bleNotifyCallback.onNotifyFailure("gatt setCharacteristicNotification fail");
            return false;
        }
        BluetoothGattDescriptor descriptor;
        if (useCharacteristicDescriptor) {
            descriptor = characteristic.getDescriptor(characteristic.getUuid());
        } else {
            descriptor = characteristic.getDescriptor(UUID.fromString(UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR));
        }
        if (descriptor == null) {
            bleNotifyCallback.onNotifyFailure("descriptor equals null");
            return false;
        } else {
            descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean success = gatt.writeDescriptor(descriptor);
            if (!success) {
                bleNotifyCallback.onNotifyFailure("gatt writeDescriptor fail");
            }
            return success;
        }
    }

    /**
     * 指示
     */
    boolean setCharacteristicIndicate(BleIndicateCallback bleIndicateCallback, boolean enable, boolean useCharacteristicDescriptor) {
        if (characteristic != null && (characteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            return setCharacteristicIndication(bluetoothGatt, characteristic, useCharacteristicDescriptor, enable, bleIndicateCallback);
        } else {
            bleIndicateCallback.onIndicateFailure("this characteristic not support indicate!");
            return false;
        }
    }

    /**
     * 设置指示
     */
    private boolean setCharacteristicIndication(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic,
                                                boolean useCharacteristicDescriptor,
                                                boolean enable,
                                                BleIndicateCallback bleIndicateCallback) {
        if (!gatt.setCharacteristicNotification(characteristic, enable)) {
            bleIndicateCallback.onIndicateFailure("gatt setCharacteristicNotification fail");
            return false;
        }
        BluetoothGattDescriptor descriptor;
        if (useCharacteristicDescriptor) {
            descriptor = characteristic.getDescriptor(characteristic.getUuid());
        } else {
            descriptor = characteristic.getDescriptor(UUID.fromString(UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR));
        }
        if (descriptor == null) {
            bleIndicateCallback.onIndicateFailure("descriptor equals null");
            return false;
        } else {
            descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean success = gatt.writeDescriptor(descriptor);
            if (!success) {
                bleIndicateCallback.onIndicateFailure("gatt writeDescriptor fail");
            }
            return success;
        }
    }

    void writeCharacteristic(byte[] data, BleWriteCallback bleWriteCallback) {
        if (data == null || data.length <= 0) {
            bleWriteCallback.onWriteFailure("the data to be written is empty");
            return;
        }
        if (characteristic == null || (characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) {
            bleWriteCallback.onWriteFailure("this characteristic not support write!");
            return;
        }
        bleBluetooth.setCharacteristicWriteCallback(bleWriteCallback, data);
        if (characteristic.setValue(data)) {
            if (!bluetoothGatt.writeCharacteristic(characteristic)) {
                bleWriteCallback.onWriteFailure("gatt writeCharacteristic fail");
            }
        } else {
            bleWriteCallback.onWriteFailure("Updates the locally stored value of this characteristic fail");
        }
    }

    void readCharacteristic(BleReadCallback bleReadCallback) {
        if (characteristic != null && (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (!bluetoothGatt.readCharacteristic(characteristic)) {
                bleReadCallback.onReadFailure("gatt readCharacteristic fail");
            }
        } else {
            bleReadCallback.onReadFailure("this characteristic not support read!");
        }
    }

}
