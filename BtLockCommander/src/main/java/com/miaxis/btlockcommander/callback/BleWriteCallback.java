package com.miaxis.btlockcommander.callback;

public interface BleWriteCallback {

    void onWriteSuccess(int current, int total, byte[] justWrite);

    void onWriteFailure(String message);

}