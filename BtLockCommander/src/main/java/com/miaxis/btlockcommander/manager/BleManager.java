package com.miaxis.btlockcommander.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.miaxis.btlockcommander.callback.BleGattCallback;
import com.miaxis.btlockcommander.callback.BleIndicateCallback;
import com.miaxis.btlockcommander.callback.BleNotifyCallback;
import com.miaxis.btlockcommander.callback.BleReadCallback;
import com.miaxis.btlockcommander.callback.BleWriteCallback;

class BleManager {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BleBluetooth bleBluetooth;

    BleManager(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    /**
     * 蓝牙是否打开
     */
    boolean isBlueEnable() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     */
    void enableBluetooth() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }
    }

    /**
     * 开始扫描
     */
    boolean startScan(BluetoothAdapter.LeScanCallback callback) {
        return bluetoothAdapter.startLeScan(callback);
    }

    /**
     * 结束扫描
     */
    void stopScan(BluetoothAdapter.LeScanCallback callback) {
        bluetoothAdapter.stopLeScan(callback);
    }

    /**
     * 连接
     */
    void connect(Context context, BluetoothDevice bluetoothDevice, BleGattCallback bleGattCallback) {
        bleBluetooth = new BleBluetooth(bluetoothDevice, bleGattCallback);
        bleBluetooth.connectGatt(context);
    }

    void disConnected() {
        bleBluetooth.disConnected();
    }

    boolean checkUsefulBle() {
        if (bleBluetooth != null && bleBluetooth.isUseful()) {
            return true;
        }
        return false;
    }

    /**
     * 设置通知
     */
    boolean notify(String uuid_service, String uuid_notify, boolean enable, boolean userCharacteristicDescriptor, BleNotifyCallback bleNotifyCallback) {
        BleConnector bleConnector = bleBluetooth.getConnector();
        if (bleConnector != null) {
            return bleConnector.withUUID(uuid_service, uuid_notify).setCharacteristicNotify(bleNotifyCallback, enable, userCharacteristicDescriptor);
        }
        return false;
    }

    /**
     * 设置指示
     */
    boolean indicate(String uuid_service, String uuid_indicate, boolean enable, boolean useCharacteristicDescriptor, BleIndicateCallback bleIndicateCallback) {
        BleConnector bleConnector = bleBluetooth.getConnector();
        if (bleConnector != null) {
            return bleConnector.withUUID(uuid_service, uuid_indicate).setCharacteristicIndicate(bleIndicateCallback, enable, useCharacteristicDescriptor);
        }
        return false;
    }

    /**
     * 写入
     */
    void write(String uuid_service, String uuid_write, byte[] data, BleWriteCallback bleWriteCallback) {
        if (data == null) {
            return;
        }
        if (data.length > SplitWriter.DEFAULT_WRITE_DATA_SPLIT_COUNT) {
            new SplitWriter(bleBluetooth, uuid_service, uuid_write)
                    .splitWrite(data,
                            true,
                            false,
                            0,
                            bleWriteCallback);
        } else {
            BleConnector bleConnector = bleBluetooth.getConnector();
            if (bleConnector != null) {
                bleConnector.withUUID(uuid_service, uuid_write).writeCharacteristic(data, bleWriteCallback);
            }
        }
    }

    /**
     * 读取
     */
    void read(String uuid_service, String uuid_read, BleReadCallback bleReadCallback) {
        BleConnector bleConnector = bleBluetooth.getConnector();
        if (bleConnector != null) {
            bleConnector.withUUID(uuid_service, uuid_read).readCharacteristic(bleReadCallback);
        }
    }

    /**
     * 通过物理地址获得蓝牙设备实体
     */
    BluetoothDevice getRemoteDevice(String mac) {
        return bluetoothAdapter.getRemoteDevice(mac);
    }

}
