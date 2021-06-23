package com.miaxis.btlockcommander.entity;

public class OpenLog {

    private String time;
    private int powerLevel;
    private String openType;
    private String alarmType;
    private String personId;
    private String name;
    private int tempId;

    public OpenLog() {
    }

    public OpenLog(String time, int powerLevel, String openType, String alarmType, String personId, String name, int tempId) {
        this.time = time;
        this.powerLevel = powerLevel;
        this.openType = openType;
        this.alarmType = alarmType;
        this.personId = personId;
        this.name = name;
        this.tempId = tempId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(int powerLevel) {
        this.powerLevel = powerLevel;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    @Override
    public String toString() {
        return "OpenLog{" +
                "time='" + time + '\'' +
                ", powerLevel=" + powerLevel +
                ", openType='" + openType + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", personId='" + personId + '\'' +
                ", name='" + name + '\'' +
                ", tempId=" + tempId +
                '}';
    }
}
