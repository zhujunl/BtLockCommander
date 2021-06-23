package com.miaxis.btlockcommanderdemo.model.entity;

public class DownDataPacketsDto {

    private int id;
    private int cmdId;
    private int seq;
    private String data;

    public DownDataPacketsDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCmdId() {
        return cmdId;
    }

    public void setCmdId(int cmdId) {
        this.cmdId = cmdId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DownDataPacketsDto{" +
                "id=" + id +
                ", cmdId=" + cmdId +
                ", seq=" + seq +
                ", data='" + data + '\'' +
                '}';
    }
}
