package com.miaxis.btlockcommander.manager;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.miaxis.btlockcommander.callback.BleFileResponseWriterCallback;
import com.miaxis.btlockcommander.callback.BleGattCallback;
import com.miaxis.btlockcommander.callback.BleIndicateCallback;
import com.miaxis.btlockcommander.callback.BleNotifyCallback;
import com.miaxis.btlockcommander.callback.BleReadCallback;
import com.miaxis.btlockcommander.callback.BleResponseWriterCallback;
import com.miaxis.btlockcommander.callback.BleWriteCallback;
import com.miaxis.btlockcommander.callback.BtLockCommanderCallback;
import com.miaxis.btlockcommander.entity.BaseLockPacket;
import com.miaxis.btlockcommander.entity.BindPersonResult;
import com.miaxis.btlockcommander.entity.BtCommand;
import com.miaxis.btlockcommander.entity.BtLockNotifyData;
import com.miaxis.btlockcommander.entity.FirmwareUpdateResult;
import com.miaxis.btlockcommander.entity.UpdatePersonResult;
import com.miaxis.btlockcommander.util.BleUtil;
import com.miaxis.btlockcommander.util.BluetoothUUID;
import com.miaxis.btlockcommander.util.LoggerFactory;
import com.miaxis.btlockcommander.util.SHA1Util;
import com.miaxis.btlockcommander.util.packet.GetTime;
import com.miaxis.btlockcommander.util.packet.Packet;
import com.miaxis.btlockcommander.util.packet.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public final class BtLockCommander {

    private BtLockCommander() {
    }

    public static BtLockCommander getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final BtLockCommander instance = new BtLockCommander();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private Application context;
    private BleManager bleManager;
    private DataAnalysis dataAnalysis;
    private SplitResponseWriter splitResponseWriter;
    private BtLockCommanderCallback btLockCommanderCallback;

    /**
     * 初始化<br>
     * 通过在上层使用单例模式构建全局唯一的蓝牙消息中心，提供范例“BluetoothManager类”<br>
     * <p>
     * 注意事项：<br>
     * 1、保证全局唯一蓝牙连接，当连接锁蓝牙成功后，请先断开当前蓝牙连接再进行其他锁蓝牙的连接操作<br>
     * 2、当单次传输任务未完成时，请勿插入其他指令，请以蓝牙写入回调为准，回调前请于前台通过对话框等方式禁止用户操作<br>
     * {@link BtLockCommanderCallback#onWriteSuccess(int, int, byte[])}单包写入回调<br>
     * {@link BtLockCommanderCallback#onResponseWriteSuccess(int, int, byte[])}多包写入回调，目前仅用于{@link BtLockCommander#writeServerCommander(List)}方法<br>
     *
     * @param app                     applicationContext
     * @param btLockCommanderCallback {@link BtLockCommanderCallback}蓝牙消息回调接口
     * @return result 判断设备是否支持Ble
     */
    public boolean init(@NonNull Application app, @NonNull BtLockCommanderCallback btLockCommanderCallback) {
        if (BleUtil.isSupportBle(app)) {
            context = app;
            this.btLockCommanderCallback = btLockCommanderCallback;
            bleManager = new BleManager(context);
            if (bleManager.getBluetoothAdapter() != null) {
                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                context.registerReceiver(bluetoothOpenReceiver, filter);
                dataAnalysis = new DataAnalysis();
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 判断蓝牙是否打开
     */
    public boolean isBluetoothOpen() {
        return bleManager.isBlueEnable();
    }

    /**
     * 打开蓝牙<br>
     * 打开蓝牙操作的结果通过{@link BtLockCommanderCallback}回调来通知调用方<br>
     * {@link BtLockCommanderCallback#onStateOn()} 蓝牙设备已打开<br>
     * {@link BtLockCommanderCallback#onStateTurningOn()} 蓝牙设备正在打开<br>
     * {@link BtLockCommanderCallback#onStateOff()} 蓝牙设备已关闭<br>
     * {@link BtLockCommanderCallback#onStateTurningOff()} 蓝牙设备正在关闭<br>
     */
    public void openBluetooth() {
        if (!bleManager.isBlueEnable()) {
            bleManager.enableBluetooth();
        }
    }

    /**
     * 开始扫描，扫描结果通过{@link BluetoothAdapter.LeScanCallback}接口回调
     *
     * @param callback 回调接口
     */
    public boolean startScan(@NonNull BluetoothAdapter.LeScanCallback callback) {
        return bleManager.startScan(callback);
    }

    /**
     * 结束扫描，需要传入调用{@link BtLockCommander#startScan(BluetoothAdapter.LeScanCallback)}时传入的参数
     *
     * @param callback 回调接口
     */
    public void stopScan(@NonNull BluetoothAdapter.LeScanCallback callback) {
        bleManager.stopScan(callback);
    }

    /**
     * 通过MAC连接蓝牙<br>
     * 限制使用者一次仅能连接一个蓝牙<br>
     * 连接蓝牙操作的结果通过{@link BtLockCommanderCallback}回调来通知调用方<br>
     * {@link BtLockCommanderCallback#onConnectStart()} 开始连接蓝牙<br>
     * {@link BtLockCommanderCallback#onConnectFail(BluetoothDevice, String)} 蓝牙连接失败<br>
     * {@link BtLockCommanderCallback#onConnectSuccess(BluetoothDevice, BluetoothGatt, int)} 蓝牙连接成功<br>
     *
     * @param mac 物理地址
     */
    public void conncet(@NonNull String mac) {
        BluetoothDevice bluetoothDevice = bleManager.getRemoteDevice(mac);
        bleManager.connect(context, bluetoothDevice, bleGattCallback);
    }

    /**
     * 通过扫描出的实体连接蓝牙<br>
     * 限制使用者一次仅能连接一个蓝牙<br>
     * 连接蓝牙操作的结果通过{@link BtLockCommanderCallback}回调来通知调用方<br>
     * {@link BtLockCommanderCallback#onConnectStart()} 开始连接蓝牙<br>
     * {@link BtLockCommanderCallback#onConnectFail(BluetoothDevice, String)} 蓝牙连接失败<br>
     * {@link BtLockCommanderCallback#onConnectSuccess(BluetoothDevice, BluetoothGatt, int)} 蓝牙连接成功<br>
     *
     * @param bluetoothDevice 蓝牙实体
     */
    public void connectGatt(@NonNull BluetoothDevice bluetoothDevice) {
        bleManager.connect(context, bluetoothDevice, bleGattCallback);
    }

    /**
     * 断开当前连接的蓝牙<br>
     * 断开当前连接的蓝牙操作的结果通过{@link BtLockCommanderCallback}回调来通知调用方<br>
     * {@link BtLockCommanderCallback#onDisConnected(BluetoothDevice, BluetoothGatt, int)} 蓝牙已断开连接<br>
     */
    public void disconnect() {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            bleManager.disConnected();
        }
    }

    /**
     * 获取蓝牙连接超时时间
     *
     * @return 超时时间
     */
    public int getConnectTimeOut() {
        return BleBluetooth.TIME_OUT;
    }

    /**
     * 读取基础时间
     *
     * @return 超时时间
     */
    public int getReadBaseInfoTimeOut() {
        return BleBluetooth.TIME_OUT_READ_BASE_INFO;
    }

    /**
     * 打开通知<br>
     * 蓝牙连接成功，并不代表我们已经可以和锁进行数据传输了<br>
     * 我们需要在蓝牙连接成功后调用该方法调用蓝牙的通知服务，使得数据可以进行双向传输<br>
     * 打开蓝牙通知服务操作的结果通过{@link BtLockCommanderCallback}回调来通知调用方<br>
     * {@link BtLockCommanderCallback#onNotifySuccess()} 打开蓝牙通知服务成功<br>
     * {@link BtLockCommanderCallback#onNotifyFailure(String)} 打开蓝牙通知服务失败<br>
     */
    public boolean startNotify() {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            return bleManager.notify(BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9601_UUID_STR, true, false, bleNotifyCallback);
        }
        return false;
    }

    /**
     * 关闭通知
     */
    public boolean stopNotify() {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            return bleManager.notify(BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9601_UUID_STR, false, false, bleNotifyCallback);
        }
        return false;
    }

    /**
     * 打开指示
     * 缺少硬件认证UUID
     */
    private boolean startIndicate() {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            return bleManager.indicate(BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9601_UUID_STR, true, false, bleIndicateCallback);
        }
        return false;
    }

    /**
     * 关闭指示
     * 缺少硬件认证UUID
     */
    private boolean stopIndicate() {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            return bleManager.indicate(BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9601_UUID_STR, false, false, bleIndicateCallback);
        }
        return false;
    }

    /**
     * 写入蓝牙命令，读取锁的基本信息<br>
     * 执行操作后，相关回调通知如下<br>
     * {@link BtLockCommanderCallback#onWriteSuccess(int, int, byte[])} 指令写入蓝牙成功<br>
     * {@link BtLockCommanderCallback#onWriteFailure(String)} 指令写入蓝牙失败<br>
     * {@link BtLockCommanderCallback#onBtLockNotify(BtLockNotifyData)} 在收到该回调时,
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#LOCK_VERSION}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.LockVersion}
     *
     * @return 当前蓝牙连接是否可用
     * @throws Exception 异常
     */
    public boolean writeLockVersionCommand() throws Exception {
        String lockVersionDataPacket = Packet.makePacket(false, null, BtCommand.LOCK_VERSION_DATA, 1, (byte) 1, (byte) 0, new byte[0], 0, 0);
        byte[] lockVersionCmd = DataAnalysis.addHeadAndTail(lockVersionDataPacket.getBytes());
        return write(lockVersionCmd);
    }

    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public boolean writeSyncTimeCommand() throws Exception {
        //        long l = System.currentTimeMillis() - 1514736000L;
        //        if (l <= 0) {
        //            return false;
        //        }
        //        Log.e("writeSyncTimeCommand", "" + l);
        //        int syncTime = (int) (l / 1000);
        //        String valueOf = String.valueOf(syncTime);
        //        Log.e("writeSyncTimeCommand", "" + valueOf);


        GetTime getTime = new GetTime((byte) 0, sdf1.format(new Date()));
        byte[] bytes = getTime.makeBody();
        String syncTimeDataPacket = Packet.makePacket(false, null, BtCommand.SYNC_TIME, 1, (byte) 1, (byte) 0, bytes, 0, bytes.length);
        Log.e("writeSyncTimeCommand", "" + syncTimeDataPacket);
        Log.e("writeSyncTimeCommand", "" + syncTimeDataPacket.getBytes().length);
        byte[] lockVersionCmd = DataAnalysis.addHeadAndTail(syncTimeDataPacket.getBytes());
        return write(lockVersionCmd);
    }

    public boolean writeBaseStationCommand() throws Exception {
        String baseStationDataPacket = Packet.makePacket(false, null, BtCommand.BASE_STATION, 1, (byte) 1, (byte) 0, new byte[0], 0, 0);
        byte[] baseStationCmd = DataAnalysis.addHeadAndTail(baseStationDataPacket.getBytes());
        return write(baseStationCmd);
    }

    /**
     * 写入蓝牙命令，读取所内绑定的人员ID<br>
     * 执行操作后，相关回调通知如下<br>
     * {@link BtLockCommanderCallback#onWriteSuccess(int, int, byte[])} 指令写入蓝牙成功<br>
     * {@link BtLockCommanderCallback#onWriteFailure(String)} 指令写入蓝牙失败<br>
     * {@link BtLockCommanderCallback#onBtLockNotify(BtLockNotifyData)} 在收到该回调时,
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#BIND_PERSON_ID}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.BindPersonId}
     *
     * @return 当前蓝牙连接是否可用
     */
    public boolean writeBindPersonIdCommand() throws Exception {
        byte[] sha1 = SHA1Util.eccryptSHA1(SHA1Util.TEMPORARY_PASSWORD);
        byte[] secretKey = SHA1Util.flip(sha1);
        String bindPersonIdPacket = Packet.makePacket(false, null, BtCommand.BIND_PERSON_ID, 1, (byte) 1, (byte) 0, secretKey, 0, secretKey.length);
        byte[] bindPersonIdCmd = DataAnalysis.addHeadAndTail(bindPersonIdPacket.getBytes());
        if (bindPersonIdCmd != null) {
            return write(bindPersonIdCmd);
        }
        return false;
    }

    /**
     * 写入蓝牙命令，读取指定日期的日志数量<br>
     * 执行操作后，相关回调通知如下<br>
     * {@link BtLockCommanderCallback#onWriteSuccess(int, int, byte[])} 指令写入蓝牙成功<br>
     * {@link BtLockCommanderCallback#onWriteFailure(String)} 指令写入蓝牙失败<br>
     * {@link BtLockCommanderCallback#onBtLockNotify(BtLockNotifyData)} 在收到该回调时,
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#OPEN_LOG_COUNT_OF_DAY}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.OpenLogCount}
     *
     * @param date yyyy-MM-dd HH:mm:ss
     * @return 当前蓝牙连接是否可用
     */
    public boolean writeQueryOpenLogCountOfDayCommand(@NonNull String date) throws Exception {
        byte[] days = Utils.compTime(date);
        String queryOpenLogPacket = Packet.makePacket(false, null, BtCommand.QUERY_OPEN_LOG_COUNT_OF_DAY, 1, (byte) 1, (byte) 0, days, 0, days.length);
        byte[] queryOpenLogCmd = DataAnalysis.addHeadAndTail(queryOpenLogPacket.getBytes());
        if (queryOpenLogCmd != null) {
            return write(queryOpenLogCmd);
        }
        return false;
    }

    /**
     * 写入蓝牙命令，读取指定日期的日志<br>
     * 执行操作后，相关回调通知如下<br>
     * {@link BtLockCommanderCallback#onWriteSuccess(int, int, byte[])} 指令写入蓝牙成功<br>
     * {@link BtLockCommanderCallback#onWriteFailure(String)} 指令写入蓝牙失败<br>
     * {@link BtLockCommanderCallback#onBtLockNotify(BtLockNotifyData)} 在收到该回调时,
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#OPEN_LOG_RESPONSE}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.OpenLogResponse}
     *
     * @param date yyyy-MM-dd HH:mm:ss
     * @param page 1 - 15
     * @return 当前蓝牙连接是否可用
     */
    public boolean writeQueryOpenLogCommand(@NonNull String date, int page) throws Exception {
        byte[] days = Utils.compTime(date);
        byte[] body = new byte[5];
        System.arraycopy(body, 0, days, 0, days.length);
        body[4] = (byte) (page & 0xff);
        String queryOpenLogPacket = Packet.makePacket(false, null, BtCommand.QUERY_OPEN_LOG, 1, (byte) 1, (byte) 0, body, 0, body.length);
        byte[] queryOpenLogCmd = DataAnalysis.addHeadAndTail(queryOpenLogPacket.getBytes());
        if (queryOpenLogCmd != null) {
            return write(queryOpenLogCmd);
        }
        return false;
    }

    /**
     * 写入蓝牙命令，固件升级，即写入升级文件，文件分包写入固件升级，总包数根据文件大小，一应一答<br>
     * 执行操作后，相关回调通知如下<br>
     * {@link BtLockCommanderCallback#onFileResponseWriteSuccess(int, int, byte[])} 文件分包写入蓝牙成功<br>
     * {@link BtLockCommanderCallback#onFileResponseWriteFailed(String)} 文件分包写入蓝牙失败，失败一次视为整个传输失败<br>
     * {@link BtLockCommanderCallback#onFileResponseWriteContinue()} 收到蓝牙回执，开始下一文件分包的写入<br>
     * {@link BtLockCommanderCallback#onBtLockNotify(BtLockNotifyData)} 在收到该回调时,
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#FIRMWARE_UPDATE_RESULT}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.FirmwareUpdateResult}
     *
     * @param type     当 type == 0 时，发送升级锁固件命令，当type == 1 时，发送升级指纹固件命令
     * @param fileData 锁固件升级文件
     * @return 当前蓝牙连接是否可用
     */
    public boolean writeFile(int type, @NonNull byte[] fileData) throws Exception {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            byte command;
            if (type == 0) {
                command = BtCommand.LOCK_FIRMWARE_UPDATE_RESULT;
            } else if (type == 1) {
                command = BtCommand.FINGERPRINT_FIRMWARE_UPDATE_RESULT;
            } else {
                return false;
            }
            List<byte[]> packetCmdList = dataAnalysis.makeFirmwareUpdateCmdList(command, fileData);
            if (packetCmdList.size() > 0) {
                try {
                    splitResponseWriter = new SplitResponseWriter(bleManager, BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9600_UUID_STR);
                    splitResponseWriter.splitFileResponseWrite(packetCmdList, bleFileResponseWriterCallback);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 写入蓝牙命令，此命令来源于锁平台相关接口<br>
     * 绑定人员命令和更新人员命令一般由两包组成，有与其对应的蓝牙响应{@link BindPersonResult}和{@link UpdatePersonResult}<br>
     * 其他指令一般由一包组成，蓝牙响应统一为{@link com.miaxis.btlockcommander.entity.BaseLockPacket}<br>
     * 执行操作后，相关回调通知如下<br>
     * {@link BtLockCommanderCallback#onResponseWriteSuccess(int, int, byte[])} 数据分包写入蓝牙成功<br>
     * {@link BtLockCommanderCallback#onResponseWriteFailed(String)} 数据分包写入蓝牙失败，失败一次视为整个传输失败<br>
     * {@link BtLockCommanderCallback#onResponseWriteContinue()} 收到蓝牙回执，开始下一数据分包的写入<br>
     * <p>
     * {@link BtLockCommanderCallback#onBtLockNotify(BtLockNotifyData)} 在收到该回调时，上层可进行如下处理
     * <p>
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#PASS_THROUGH}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.BaseLockPacket}<br>
     * <p>
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#BIND_PERSON_RESULT}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.BindPersonResult}<br>
     * 当{@link BindPersonResult#getResult()}的值为0时，视为锁绑定人员成功<br>
     * <p>
     * 当{@link BtLockNotifyData#getMode()}的值为{@value BtLockNotifyData#UPDATE_PERSON_RESULT}时,
     * 其{@link BtLockNotifyData#getData()}的泛型为{@link com.miaxis.btlockcommander.entity.UpdatePersonResult}<br>
     * 当{@link BindPersonResult#getResult()}的值为0时，视为锁更新人员成功<br>
     * <p>
     * 为了使锁和锁平台同步，此时需要调用方将结果告知联网锁平台，请将{@link BaseLockPacket#getData()}
     * 通过POST方式请求接口"http://ip:port/netlock/api/nb/v2/Bluetooth/handleBindReply"(注意：请以锁平台接口文档为准)
     * 如不进行此步骤，则联网锁平台对数据的正确性不做保证
     *
     * @param commandList 从锁平台获取的packageData，依照commandId排序
     * @return 当前蓝牙连接是否可用
     */
    public boolean writeServerCommander(@NonNull List<byte[]> commandList) {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            List<byte[]> bluetoothCommandList = new ArrayList<>();
            for (byte[] data : commandList) {
                bluetoothCommandList.add(DataAnalysis.addHeadAndTail(data));
            }
            if (bluetoothCommandList.size() > 0) {
                try {
                    splitResponseWriter = new SplitResponseWriter(bleManager, BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9600_UUID_STR);
                    splitResponseWriter.splitDataResponseWrite(bluetoothCommandList, bleResponseWriterCallback);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 一应一答写入
     */
    private boolean writeRestFile() {
        if (splitResponseWriter != null) {
            return splitResponseWriter.continueRestTransmission();
        }
        return false;
    }

    /**
     * 写入数据
     *
     * @param data data
     */
    private boolean write(byte[] data) {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            bleManager.write(BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9600_UUID_STR, data, bleWriteCallback);
            return true;
        }
        return false;
    }

    /**
     * 读取数据
     * 缺少硬件认证UUID
     */
    private boolean read() {
        if (bleManager != null && bleManager.checkUsefulBle()) {
            bleManager.read(BluetoothUUID.SERVICE_UUID_STR, BluetoothUUID.CH_9601_UUID_STR, bleReadCallback);
            return true;
        }
        return false;
    }

    private BroadcastReceiver bluetoothOpenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        btLockCommanderCallback.onStateOff();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        btLockCommanderCallback.onStateTurningOff();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        btLockCommanderCallback.onStateOn();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        btLockCommanderCallback.onStateTurningOn();
                        break;
                }
            }
        }
    };

    private BleGattCallback bleGattCallback = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            logger.e("onStartConnect");
            btLockCommanderCallback.onConnectStart();
        }

        @Override
        public void onConnectFail(BluetoothDevice bluetoothDevice, String message) {
            logger.e("onConnectFail" + message);
            btLockCommanderCallback.onConnectFail(bluetoothDevice, message);
        }

        @Override
        public void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status) {
            logger.e("onConnectSuccess");
            btLockCommanderCallback.onConnectSuccess(bluetoothDevice, gatt, status);
        }

        @Override
        public void onDisConnected(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status) {
            logger.e("onDisConnected");
            btLockCommanderCallback.onDisConnected(bluetoothDevice, gatt, status);
        }
    };
    private LoggerFactory logger = new LoggerFactory(this);
    private BleNotifyCallback bleNotifyCallback = new BleNotifyCallback() {
        @Override
        public void onNotifySuccess() {
            logger.e("onNotifySuccess");
            btLockCommanderCallback.onNotifySuccess();
        }

        @Override
        public void onNotifyFailure(String message) {
            logger.e("onNotifyFailure" + message);
            btLockCommanderCallback.onNotifyFailure(message);
        }

        @Override
        public void onCharacteristicChanged(byte[] data) {
            BtLockNotifyData btLockNotifyData = dataAnalysis.analysisData(data);
            logger.e("onCharacteristicChanged:" + new String(data));
            if (btLockNotifyData == null) {
                return;
            }
            logger.e("btLockNotifyData.getMode:" + btLockNotifyData.getMode());
            if (btLockNotifyData.getMode() == BtLockNotifyData.FIRMWARE_UPDATE_RESULT) {
                FirmwareUpdateResult firmwareUpdateResult = (FirmwareUpdateResult) btLockNotifyData.getData();
                if (firmwareUpdateResult.getResult() == 0) {
                    if (writeRestFile()) {
                        btLockCommanderCallback.onFileResponseWriteContinue();
                        return;
                    }
                }
            } else if (btLockNotifyData.getMode() == BtLockNotifyData.BIND_PERSON_RESULT) {
                BindPersonResult bindPersonResult = (BindPersonResult) btLockNotifyData.getData();
                logger.e("BindPersonResult:" + bindPersonResult.getResult());
                if (bindPersonResult.getResult() == 0 || bindPersonResult.getResult() == 6) {
                    boolean writeRestFile = writeRestFile();
                    logger.e("BindPersonResult:     writeRestFile:" + writeRestFile);
                    if (writeRestFile) {
                        btLockCommanderCallback.onResponseWriteContinue();
                        logger.e("BindPersonResult:onResponseWriteContinue");
                        return;
                    }
                }
            } else if (btLockNotifyData.getMode() == BtLockNotifyData.UPDATE_PERSON_RESULT) {
                UpdatePersonResult updatePersonResult = (UpdatePersonResult) btLockNotifyData.getData();
                if (updatePersonResult.getResult() == 0) {
                    if (writeRestFile()) {
                        btLockCommanderCallback.onResponseWriteContinue();
                        return;
                    }
                }
            }
            //            else if (btLockNotifyData.getMode() == BtLockNotifyData.SYNC_TIME) {
            //                UpdatePersonResult updatePersonResult = (UpdatePersonResult) btLockNotifyData.getData();
            //                if (updatePersonResult.getResult() == 0) {
            //                    if (writeRestFile()) {
            //                        btLockCommanderCallback.onResponseWriteContinue();
            //                        return;
            //                    }
            //                }
            //            }
            btLockCommanderCallback.onBtLockNotify(btLockNotifyData);
        }

    };

    private BleIndicateCallback bleIndicateCallback = new BleIndicateCallback() {
        @Override
        public void onIndicateSuccess() {
            logger.e("onIndicateSuccess");
        }

        @Override
        public void onIndicateFailure(String message) {
            logger.e("onIndicateFailure" + message);
        }

        @Override
        public void onCharacteristicChanged(byte[] data) {
            logger.e("onCharacteristicChanged" + new String(data));
        }
    };

    private BleWriteCallback bleWriteCallback = new BleWriteCallback() {
        @Override
        public void onWriteSuccess(int current, int total, byte[] justWrite) {
            btLockCommanderCallback.onWriteSuccess(current, total, justWrite);
            logger.e("onWriteSuccess");
        }

        @Override
        public void onWriteFailure(String message) {
            btLockCommanderCallback.onWriteFailure(message);
            logger.e("onWriteFailure" + message);
        }
    };

    private BleReadCallback bleReadCallback = new BleReadCallback() {
        @Override
        public void onReadSuccess(byte[] data) {
            logger.e("onReadSuccess");
        }

        @Override
        public void onReadFailure(String message) {
            logger.e("onReadFailure" + message);
        }
    };

    private BleResponseWriterCallback bleResponseWriterCallback = new BleResponseWriterCallback() {
        @Override
        public void onResponseWriteSuccess(int current, int total, byte[] justWrite) {
            btLockCommanderCallback.onResponseWriteSuccess(current, total, justWrite);
            logger.e("onResponseWriteSuccess");
        }

        @Override
        public void onResponseWriteFailed(String message) {
            btLockCommanderCallback.onResponseWriteFailed(message);
            logger.e("onResponseWriteFailed");
        }
    };

    private BleFileResponseWriterCallback bleFileResponseWriterCallback = new BleFileResponseWriterCallback() {
        @Override
        public void onFileResponseWriteSuccess(int current, int total, byte[] justWrite) {
            btLockCommanderCallback.onFileResponseWriteSuccess(current, total, justWrite);
            logger.e("onResponseWriteSuccess");
        }

        @Override
        public void onFileResponseWriteFailed(String message) {
            btLockCommanderCallback.onFileResponseWriteFailed(message);
            logger.e("onResponseWriteFailed");
        }
    };

}
