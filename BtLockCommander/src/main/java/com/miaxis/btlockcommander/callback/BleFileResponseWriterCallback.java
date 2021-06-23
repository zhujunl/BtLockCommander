package com.miaxis.btlockcommander.callback;

public interface BleFileResponseWriterCallback {

    void onFileResponseWriteSuccess(int current, int total, byte[] justWrite);

    void onFileResponseWriteFailed(String message);

}
