package com.miaxis.btlockcommanderdemo.model.entity;

import java.util.List;

public class DownDataDto {

    private int id;
    private String requestSeq;
    private int type;
    private String typeName;
    private long createTime;
    private String replyCode;
    private List<DownDataPacketsDto> packets;

    private boolean noNeedWait=false;//更改默认值为true  删除或者添加可一次性执行

    public DownDataDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestSeq() {
        return requestSeq;
    }

    public void setRequestSeq(String requestSeq) {
        this.requestSeq = requestSeq;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(String replyCode) {
        this.replyCode = replyCode;
    }

    public List<DownDataPacketsDto> getPackets() {
        return packets;
    }

    public void setPackets(List<DownDataPacketsDto> packets) {
        this.packets = packets;
    }

    public boolean isNoNeedWait() {
        return noNeedWait;
    }

    public void setNoNeedWait(boolean noNeedWait) {
        this.noNeedWait = noNeedWait;
    }

    @Override
    public String toString() {
        return "DownDataDto{" +
                "id=" + id +
                ", requestSeq='" + requestSeq + '\'' +
                ", type=" + type +
                ", typeName='" + typeName + '\'' +
                ", createTime=" + createTime +
                ", replyCode='" + replyCode + '\'' +
                ", packets=" + packets +
                ", noNeedWait=" + noNeedWait +
                '}';
    }

}
