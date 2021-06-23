package com.miaxis.btlockcommander.util;

import java.util.UUID;

public interface BluetoothUUID {

    UUID SERVICE = UUID.fromString("0000ff92-0000-1000-8000-00805f9b34fb");

    UUID CH_9600 = UUID.fromString("00009600-0000-1000-8000-00805f9b34fb");
    UUID CH_9601 = UUID.fromString("00009601-0000-1000-8000-00805f9b34fb");

    String SERVICE_UUID_STR = "0000ff92-0000-1000-8000-00805f9b34fb";

    String CH_9600_UUID_STR = "00009600-0000-1000-8000-00805f9b34fb";
    String CH_9601_UUID_STR = "00009601-0000-1000-8000-00805f9b34fb";
}