package com.miaxis.btlockcommander.entity;

public class BtCommand {
    public static final byte LOCK_VERSION_DATA = (byte) 0x1C;
    public static final byte BIND_PERSON_ID = (byte) 0x1E;
    public static final byte LOCK_FIRMWARE_UPDATE_RESULT =(byte) 0x1D;
    public static final byte FINGERPRINT_FIRMWARE_UPDATE_RESULT =(byte) 0x1F;
    public static final byte QUERY_OPEN_LOG_COUNT_OF_DAY =(byte) 0x17;
    public static final byte QUERY_OPEN_LOG =(byte) 0x20;
    public static final byte BASE_STATION =(byte) 0x2B;
    public static final byte SYNC_TIME = (byte) 0x51;
}
