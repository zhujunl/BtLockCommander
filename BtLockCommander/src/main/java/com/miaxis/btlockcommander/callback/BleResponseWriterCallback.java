package com.miaxis.btlockcommander.callback;

public interface BleResponseWriterCallback {

    void onResponseWriteSuccess(int current, int total, byte[] justWrite);

    void onResponseWriteFailed(String message);

}
