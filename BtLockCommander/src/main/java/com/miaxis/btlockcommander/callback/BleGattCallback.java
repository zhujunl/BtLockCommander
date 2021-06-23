package com.miaxis.btlockcommander.callback;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public interface BleGattCallback {

    void onStartConnect();

    void onConnectFail(BluetoothDevice bluetoothDevice, String message);

    void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status);

    void onDisConnected(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status);

}