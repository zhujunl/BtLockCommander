package com.miaxis.btlockcommander.callback;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.miaxis.btlockcommander.entity.BtLockNotifyData;

public interface BtLockCommanderCallback {

    /**
     * 蓝牙已开启
     */
    void onStateOn();

    /**
     * 蓝牙正在开启
     */
    void onStateTurningOn();

    /**
     * 蓝牙已关闭
     */
    void onStateOff();

    /**
     * 蓝牙正在关闭
     */
    void onStateTurningOff();

    /**
     * 开始连接蓝牙
     */
    void onConnectStart();

    /**
     * 连接蓝牙失败
     * @param bluetoothDevice 蓝牙设备
     * @param message 失败信息
     */
    void onConnectFail(BluetoothDevice bluetoothDevice, String message);

    /**
     * 连接蓝牙成功
     * @param bluetoothDevice 蓝牙设备
     * @param gatt 蓝牙协议
     * @param status 状态
     */
    void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status);

    /**
     * 蓝牙连接已断开
     * @param bluetoothDevice 蓝牙设备
     * @param gatt 蓝牙协议
     * @param status 状态
     */
    void onDisConnected(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status);

    /**
     * 打开通知服务成功
     */
    void onNotifySuccess();

    /**
     * 打开通知服务失败
     * @param message 失败信息
     */
    void onNotifyFailure(String message);

    /**
     * 接收到蓝牙发送的消息，根据BtLockNotifyData.mode进行分类，对data进行泛型转换
     * @param btLockNotifyData 蓝牙数据信息实体
     */
    void onBtLockNotify(BtLockNotifyData<?> btLockNotifyData);

    /**
     * 写入蓝牙成功
     * @param current 当前进度
     * @param total 总进度
     * @param justWrite 写入的数据
     */
    void onWriteSuccess(int current, int total, byte[] justWrite);

    /**
     * 写入蓝牙失败
     * @param message 失败信息
     */
    void onWriteFailure(String message);

    /**
     * 应答分包写入成功
     * @param current 当前进度
     * @param total 总进度
     * @param justWrite 写入的数据
     */
    void onResponseWriteSuccess(int current, int total, byte[] justWrite);

    /**
     * 应答分包写入失败
     * @param message 失败信息
     */
    void onResponseWriteFailed(String message);

    /**
     * 收到蓝牙回应，应答分包继续写入
     */
    void onResponseWriteContinue();

    /**
     * 文件应答分包写入成功
     * @param current 当前进度
     * @param total 总进度
     * @param justWrite 写入的数据
     */
    //
    void onFileResponseWriteSuccess(int current, int total, byte[] justWrite);

    /**
     * 文件应答分包写入失败
     * @param message 失败信息
     */
    void onFileResponseWriteFailed(String message);

    /**
     * 收到蓝牙回应，文件应答分包继续写入
     */
    void onFileResponseWriteContinue();

}
