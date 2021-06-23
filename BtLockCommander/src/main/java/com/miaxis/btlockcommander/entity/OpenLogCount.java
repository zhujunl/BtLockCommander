package com.miaxis.btlockcommander.entity;

public class OpenLogCount extends BaseLockPacket {

    private int count;

    public OpenLogCount() {
    }

    public OpenLogCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public String toString() {
        return "OpenLogCount{" +
                "count=" + count +
                '}';
    }
}
