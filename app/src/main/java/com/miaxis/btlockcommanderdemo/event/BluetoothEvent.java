package com.miaxis.btlockcommanderdemo.event;

public class BluetoothEvent<T> {

    public static final int ON_STATE_ON = 1;
    public static final int ON_STATE_TURNING_ON = 2;
    public static final int ON_STATE_OFF = 3;
    public static final int ON_STATE_TURNING_OFF = 4;
    public static final int ON_CONNECT_START = 5;
    public static final int ON_CONNECT_SUCCESS = 6;
    public static final int ON_CONNECT_FAILED = 7;
    public static final int ON_DIS_CONNECT = 8;
    public static final int ON_NOTIFY_SUCCESS = 9;
    public static final int ON_NOTIFY_FAILED = 10;
    public static final int ON_WRITE_SUCCESS = 11;
    public static final int ON_WRITE_FAILED = 12;
    public static final int ON_RESPONSE_WRITE_SUCCESS = 13;
    public static final int ON_RESPONSE_WRITE_FAILED = 14;
    public static final int ON_RESPONSE_WRITE_CONTINUE = 15;
    public static final int ON_BT_LOCK_NOTIFY_DATA = 16;
    public static final int ON_NOTIFY_LOCK_VERSION = 17;
    public static final int ON_NOTIFY_BIND_PERSON_ID = 18;
    public static final int ON_NOTIFY_FIRMWARE_UPDATE_RESULT = 19;
    public static final int ON_NOTIFY_BIND_PERSON_RESULT = 20;
    public static final int ON_OPEN_LOG_COUNT_OF_DAY = 21;
    public static final int ON_OPEN_LOG_RESPONSE = 22;
    public static final int ON_FILE_RESPONSE_WRITE_SUCCESS = 23;
    public static final int ON_FILE_RESPONSE_WRITE_FAILED = 24;
    public static final int ON_FILE_RESPONSE_WRITE_CONTINUE = 25;
    public static final int ON_NOTIFY_BASE_STATION_DATA = 26;
    public static final int ON_NOTIFY_UPDATE_PERSON_RESULT = 27;
    public static final int ON_SYNC_TIME_RESPONSE = 28;

    private int type;
    private T data;

    public BluetoothEvent(int type) {
        this.type = type;
    }

    public BluetoothEvent(int type, T data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "BluetoothEvent{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
