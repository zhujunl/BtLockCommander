package com.miaxis.btlockcommander.entity;

import java.util.List;

public class OpenLogResponse extends BaseLockPacket {

    private List<OpenLog> openLogList;

    public OpenLogResponse() {
    }

    public OpenLogResponse(List<OpenLog> openLogList) {
        this.openLogList = openLogList;
    }

    public List<OpenLog> getOpenLogList() {
        return openLogList;
    }

    public void setOpenLogList(List<OpenLog> openLogList) {
        this.openLogList = openLogList;
    }


    @Override
    public String toString() {
        return "OpenLogResponse{" +
                "openLogList=" + openLogList +
                '}';
    }
}
