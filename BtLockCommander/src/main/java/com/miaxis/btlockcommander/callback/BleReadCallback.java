package com.miaxis.btlockcommander.callback;

public interface BleReadCallback {

    void onReadSuccess(byte[] data);

    void onReadFailure(String message);

}