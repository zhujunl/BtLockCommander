package com.miaxis.btlockcommander.entity;

import java.util.Arrays;

public class BaseLockPacket {

    private byte code;
    private byte[] data;

    public BaseLockPacket() {
    }

    public BaseLockPacket(byte code, byte[] data) {
        this.code = code;
        this.data = data;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setLockPacket(byte code, byte[] data) {
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseLockPacket{" +
                "code=" + code +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
