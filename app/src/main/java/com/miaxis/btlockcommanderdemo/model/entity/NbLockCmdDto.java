package com.miaxis.btlockcommanderdemo.model.entity;

public class NbLockCmdDto {

    private Long id;
    private Long commandId;
    private String packageData;

    public NbLockCmdDto() {
    }

    public NbLockCmdDto(Long id, Long commandId, String packageData) {
        this.id = id;
        this.commandId = commandId;
        this.packageData = packageData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public String getPackageData() {
        return packageData;
    }

    public void setPackageData(String packageData) {
        this.packageData = packageData;
    }
}
