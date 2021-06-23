package com.miaxis.btlockcommander.entity;

import java.io.Serializable;

public class FirmwareUpdateResult extends BaseLockPacket implements Serializable {

    private static final long serialVersionUID = -7036640832631159314L;

    private int result;

    public FirmwareUpdateResult() {
    }

    public FirmwareUpdateResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
