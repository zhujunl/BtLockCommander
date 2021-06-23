package com.miaxis.btlockcommander.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.miaxis.btlockcommander.callback.BleGattCallback;
import com.miaxis.btlockcommander.callback.BleNotifyCallback;
import com.miaxis.btlockcommander.callback.BleWriteCallback;
import com.miaxis.btlockcommander.util.BluetoothUUID;

import java.lang.reflect.Method;

class BleBluetooth {

    private final static boolean AUTO_CONNECT = false;
    public final static int TIME_OUT = 15 * 1000;
    public final static int TIME_OUT_READ_BASE_INFO = 15 * 1000;
    public final static int TIME_OUT_RESPONSE = 15 * 1000;


    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private BleGattCallback bleGattCallback;

    private boolean useful = false;
    private Handler handler = new Handler();

    // 写入数据蓝牙回调
    private BleWriteCallback bleWriteCallback;
    private byte[] writeCallbackData;
    //蓝牙通知数据回调
    private BleNotifyCallback bleNotifyCallback;

    BleBluetooth(BluetoothDevice bluetoothDevice, BleGattCallback bleGattCallback) {
        this.bluetoothDevice = bluetoothDevice;
        this.bleGattCallback = bleGattCallback;
    }

    BleConnector getConnector() {
        if (useful) {
            return new BleConnector(this);
        }
        return null;
    }

    BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    boolean isUseful() {
        return useful;
    }

    synchronized void disConnected() {
        bluetoothGatt.disconnect();
        releaseGatt();
    }

    /**
     * 连接
     */
    synchronized void connectGatt(Context context) {
        releaseGatt();
        bleGattCallback.onStartConnect();
        handler.postDelayed(timeOutRunnable, TIME_OUT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = bluetoothDevice.connectGatt(context, AUTO_CONNECT, coreGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothGatt = bluetoothDevice.connectGatt(context, AUTO_CONNECT, coreGattCallback);
        }
    }

    private int MTU = 300;

    void setCharacteristicWriteCallback(BleWriteCallback bleWriteCallback, byte[] writeCallbackData) {
        this.bleWriteCallback = bleWriteCallback;
        this.writeCallbackData = writeCallbackData;
    }

    void setBleNotifyCallback(BleNotifyCallback bleNotifyCallback) {
        this.bleNotifyCallback = bleNotifyCallback;
    }

    private final BluetoothGattCallback coreGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            bluetoothGatt = gatt;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (bluetoothGatt == null || !bluetoothGatt.discoverServices()) {
                    releaseGatt();
                    bleGattCallback.onConnectFail(bluetoothDevice, "GATT discover services exception occurred!");
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                releaseGatt();
                bleGattCallback.onDisConnected(bluetoothDevice, gatt, status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            bluetoothGatt = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handler.removeCallbacks(timeOutRunnable);
                useful = true;
                bleGattCallback.onConnectSuccess(bluetoothDevice, bluetoothGatt, status);
            } else {
                releaseGatt();
                bleGattCallback.onConnectFail(bluetoothDevice, "GATT discover services exception occurred!");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                gatt.requestMtu(MTU);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (BluetoothUUID.CH_9600_UUID_STR.equals(characteristic.getUuid().toString())) {
                if (bleWriteCallback != null && writeCallbackData != null) {
                    bleWriteCallback.onWriteSuccess(1, 1, writeCallbackData);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (BluetoothUUID.CH_9601_UUID_STR.equals(characteristic.getUuid().toString())) {
                if (bleNotifyCallback != null) {
                    bleNotifyCallback.onCharacteristicChanged(characteristic.getValue());
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (BluetoothUUID.CH_9601_UUID_STR.equals(descriptor.getCharacteristic().getUuid().toString())) {
                if (bleNotifyCallback != null) {
                    bleNotifyCallback.onNotifySuccess();
                }
            }
        }
    };

    private Runnable timeOutRunnable = () -> {
        releaseGatt();
        bleGattCallback.onConnectFail(bluetoothDevice, "time out");
    };

    private synchronized void releaseGatt() {
        useful = false;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            refreshDeviceCache();
            bluetoothGatt.close();
        }
    }

    private void refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null && bluetoothGatt != null) {
                boolean success = (Boolean) refresh.invoke(bluetoothGatt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
