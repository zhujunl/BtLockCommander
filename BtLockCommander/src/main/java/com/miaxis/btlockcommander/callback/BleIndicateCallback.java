package com.miaxis.btlockcommander.callback;

public interface BleIndicateCallback {

    void onIndicateSuccess();

    void onIndicateFailure(String message);

    void onCharacteristicChanged(byte[] data);
}