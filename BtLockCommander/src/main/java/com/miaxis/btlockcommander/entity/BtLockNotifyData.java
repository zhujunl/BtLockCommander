package com.miaxis.btlockcommander.entity;

public class BtLockNotifyData<T> {

    public static final int PASS_THROUGH = -1;
    public static final int LOCK_VERSION = 1;
    public static final int BIND_PERSON_ID = 2;
    public static final int FIRMWARE_UPDATE_RESULT = 3;
    public static final int BIND_PERSON_RESULT = 4;
    public static final int OPEN_LOG_COUNT_OF_DAY = 5;
    public static final int OPEN_LOG_RESPONSE = 6;
    public static final int BASE_STATION = 7;
    public static final int UPDATE_PERSON_RESULT = 8;
    public static final int SYNC_TIME = 9;

    private int mode;
    private T data;

    public BtLockNotifyData(int mode, T data) {
        this.mode = mode;
        this.data = data;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
