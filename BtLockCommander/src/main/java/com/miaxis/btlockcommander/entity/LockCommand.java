package com.miaxis.btlockcommander.entity;

public class LockCommand {

    public static final byte LOCK_VERSION_DATA = (byte) 0xFC;
    public static final byte BIND_PERSON_ID = (byte) 0xFE;
    public static final byte FIRMWARE_UPDATE_RESULT = (byte) 0xFD;
    public static final byte PERSON_BIND_RESPONSE = (byte) 0xF1;
    public static final byte OPEN_LOG_COUNT_OF_DAY_RESPONSE = (byte) 0xF7;
    public static final byte OPEN_LOG_RESPONSE = (byte) 0xC0;
    public static final byte BASE_STATION = (byte) 0xCB;
    public static final byte PERSON_UPDATE_REPONSE = (byte) 0xF3;
}
