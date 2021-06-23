package com.miaxis.btlockcommanderdemo.model.entity;

public class ResponseWriteProgress {

    private int current;
    private int total;

    public ResponseWriteProgress() {
    }

    public ResponseWriteProgress(int current, int total) {
        this.current = current;
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
