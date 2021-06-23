package com.miaxis.btlockcommanderdemo.manager;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Base64;
import android.util.Log;

import com.miaxis.btlockcommander.callback.BtLockCommanderCallback;
import com.miaxis.btlockcommander.entity.BtLockNotifyData;
import com.miaxis.btlockcommander.manager.BtLockCommander;
import com.miaxis.btlockcommander.util.LoggerFactory;
import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.model.BtDeviceModel;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.model.entity.NbUpdateFirmwareDto;
import com.miaxis.btlockcommanderdemo.model.entity.ResponseWriteProgress;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothManager {

    private BluetoothManager() {
    }

    public static BluetoothManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final BluetoothManager instance = new BluetoothManager();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public boolean init(Application app) {
        return BtLockCommander.getInstance().init(app, btLockCommanderCallback);
    }

    public void connect(String mac) {
        BtLockCommander.getInstance().conncet(mac);
    }

    public void connect(BluetoothDevice bluetoothDevice) {
        BtLockCommander.getInstance().connectGatt(bluetoothDevice);
    }

    public void disConnected() {
        BtLockCommander.getInstance().disconnect();
    }

    public void startNotify() {
        BtLockCommander.getInstance().startNotify();
    }

    public boolean writeServerCommand(List<byte[]> commandList) {
        return BtLockCommander.getInstance().writeServerCommander(commandList);
    }

    private LoggerFactory mLoggerFactory = new LoggerFactory(this);

    public boolean writeQueryLogCountOfDayCommand(String date) {
        try {
            return BtLockCommander.getInstance().writeQueryOpenLogCountOfDayCommand(date);
        } catch (Exception e) {
            e.printStackTrace();
            mLoggerFactory.e("writeQueryLogCountOfDayCommand:"+e.getMessage());
        }
        return false;
    }

    public boolean writeQueryLogCommand(String date, int page) {
        try {
            return BtLockCommander.getInstance().writeQueryOpenLogCommand(date, page);
        } catch (Exception e) {
            e.printStackTrace();
            mLoggerFactory.e("writeQueryLogCommand:"+e.getMessage());
        }
        return false;
    }

    public boolean writeBindPersonId() {
        try {
            return BtLockCommander.getInstance().writeBindPersonIdCommand();
        } catch (Exception e) {
            e.printStackTrace();
            mLoggerFactory.e("writeBindPersonId:"+e.getMessage());
        }
        return false;
    }

    public boolean writeSyncTimeData() {
        try {
            return BtLockCommander.getInstance().writeSyncTimeCommand();
        } catch (Exception e) {
            e.printStackTrace();
            mLoggerFactory.e("writeSyncTimeData:"+e.getMessage());
        }
        return false;
    }

    public boolean writeLockVersionData() {
        try {
            return BtLockCommander.getInstance().writeLockVersionCommand();
        } catch (Exception e) {
            e.printStackTrace();
            mLoggerFactory.e("writeLockVersionData:"+e.getMessage());
        }
        return false;
    }

    public boolean writeBaseStationData() {
        try {
            return BtLockCommander.getInstance().writeBaseStationCommand();
        } catch (Exception e) {
            e.printStackTrace();
            mLoggerFactory.e(e.getMessage());
        }
        return false;
    }

    public void writeLockFirmwareUpdateCommand(NbUpdateFirmwareDto nbUpdateFirmwareDto) {
        new Thread(() -> {
            byte[] fileData = Base64.decode(nbUpdateFirmwareDto.getData(), Base64.NO_WRAP);
            boolean result = false;
            try {
                result = BtLockCommander.getInstance().writeFile(0, fileData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!result) {
                EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.ON_WRITE_FAILED));
            }
        }).start();
    }

    public void writeFingerprintFirmwareUpdateCommand(NbUpdateFirmwareDto nbUpdateFirmwareDto) {
        new Thread(() -> {
            byte[] fileData = Base64.decode(nbUpdateFirmwareDto.getData(), Base64.NO_WRAP);
            boolean result = false;
            try {
                result = BtLockCommander.getInstance().writeFile(1, fileData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!result) {
                EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.ON_WRITE_FAILED));
            }
        }).start();
    }

    public void saveBtDevice(BtDevice btDevice, String keeper) {
        Observable.just(btDevice)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(mBtDevice -> BtDeviceModel.saveBtDevice(btDevice, keeper))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mBtDevice1 -> {
                        },
                        throwable -> ToastManager.toast(BtLockCommanderApp.getInstance().getApplicationContext(),
                                "设备保存失败", ToastManager.ERROR));
    }

    private final BtLockCommanderCallback btLockCommanderCallback = new BtLockCommanderCallback() {

        @Override
        public void onStateOn() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_STATE_ON));
        }

        @Override
        public void onStateTurningOn() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_STATE_TURNING_ON));
        }

        @Override
        public void onStateOff() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_STATE_OFF));
        }

        @Override
        public void onStateTurningOff() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_STATE_TURNING_OFF));
        }

        @Override
        public void onConnectStart() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_CONNECT_START));
        }

        @Override
        public void onConnectFail(BluetoothDevice bluetoothDevice, String message) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_CONNECT_FAILED));
        }

        @Override
        public void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_CONNECT_SUCCESS));
        }

        @Override
        public void onDisConnected(BluetoothDevice bluetoothDevice, BluetoothGatt gatt, int status) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_DIS_CONNECT));
        }

        @Override
        public void onNotifySuccess() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_SUCCESS));
        }

        @Override
        public void onNotifyFailure(String message) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_FAILED));
        }

        @Override
        public void onBtLockNotify(BtLockNotifyData<?> btLockNotifyData) {
            Log.e("onBtLockNotify", "BtLockNotifyData:"+btLockNotifyData.getMode());
            switch (btLockNotifyData.getMode()) {
                case BtLockNotifyData.LOCK_VERSION:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_LOCK_VERSION, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.SYNC_TIME:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_SYNC_TIME_RESPONSE, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.BIND_PERSON_ID:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_BIND_PERSON_ID, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.FIRMWARE_UPDATE_RESULT:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_FIRMWARE_UPDATE_RESULT, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.BIND_PERSON_RESULT:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_BIND_PERSON_RESULT, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.UPDATE_PERSON_RESULT:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_UPDATE_PERSON_RESULT, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.PASS_THROUGH:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_BT_LOCK_NOTIFY_DATA, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.OPEN_LOG_COUNT_OF_DAY:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_OPEN_LOG_COUNT_OF_DAY, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.OPEN_LOG_RESPONSE:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_OPEN_LOG_RESPONSE, btLockNotifyData.getData()));
                    break;
                case BtLockNotifyData.BASE_STATION:
                    EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_NOTIFY_BASE_STATION_DATA, btLockNotifyData.getData()));
                    break;
            }
        }

        @Override
        public void onWriteSuccess(int current, int total, byte[] justWrite) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_WRITE_SUCCESS));
        }

        @Override
        public void onWriteFailure(String message) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_WRITE_FAILED));
        }

        @Override
        public void onResponseWriteSuccess(int current, int total, byte[] justWrite) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_RESPONSE_WRITE_SUCCESS, new ResponseWriteProgress(current, total)));
        }

        @Override
        public void onResponseWriteFailed(String message) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_RESPONSE_WRITE_FAILED));
        }

        @Override
        public void onResponseWriteContinue() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_RESPONSE_WRITE_CONTINUE));
        }

        @Override
        public void onFileResponseWriteSuccess(int current, int total, byte[] justWrite) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_FILE_RESPONSE_WRITE_SUCCESS, new ResponseWriteProgress(current, total)));
        }

        @Override
        public void onFileResponseWriteFailed(String message) {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_FILE_RESPONSE_WRITE_FAILED));
        }

        @Override
        public void onFileResponseWriteContinue() {
            EventBus.getDefault().post(new BluetoothEvent<>(BluetoothEvent.ON_FILE_RESPONSE_WRITE_CONTINUE));
        }
    };

}
