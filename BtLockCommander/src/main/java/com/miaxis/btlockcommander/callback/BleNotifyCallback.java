package com.miaxis.btlockcommander.callback;

public interface BleNotifyCallback {

    void onNotifySuccess();

    void onNotifyFailure(String message);

    void onCharacteristicChanged(byte[] data);

}