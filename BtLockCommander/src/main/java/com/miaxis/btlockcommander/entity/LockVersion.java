package com.miaxis.btlockcommander.entity;

import java.io.Serializable;

public class LockVersion extends BaseLockPacket implements Serializable {

    private static final long serialVersionUID = 4966749319251516286L;

    //锁硬件型号+版本号
    private String lockHardwareVersion;
    //锁固件型号+版本号
    private String lockFirmwareVersion;
    //指纹模块硬件型号+版本号
    private String fingerHardwareVersion;
    //指纹模块固件型号+版本号
    private String fingerFirmwareVersion;
    //NB模块SN15位数字/字母
    private String nbSerialNumber;
    //SIM卡串号
    private String simICCID;
    //NB信号强度
    private String nbSignalIntensity;
    //NB模块版本号
    private String nbVersion;
    //SIM卡IMSI号
    private String simIMSI;

    public LockVersion() {
    }

    public LockVersion(String lockHardwareVersion, String lockFirmwareVersion, String fingerHardwareVersion, String fingerFirmwareVersion, String nbSerialNumber, String simICCID, String nbSignalIntensity, String nbVersion, String simIMSI) {
        this.lockHardwareVersion = lockHardwareVersion;
        this.lockFirmwareVersion = lockFirmwareVersion;
        this.fingerHardwareVersion = fingerHardwareVersion;
        this.fingerFirmwareVersion = fingerFirmwareVersion;
        this.nbSerialNumber = nbSerialNumber;
        this.simICCID = simICCID;
        this.nbSignalIntensity = nbSignalIntensity;
        this.nbVersion = nbVersion;
        this.simIMSI = simIMSI;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getLockHardwareVersion() {
        return lockHardwareVersion;
    }

    public void setLockHardwareVersion(String lockHardwareVersion) {
        this.lockHardwareVersion = lockHardwareVersion;
    }

    public String getLockFirmwareVersion() {
        return lockFirmwareVersion;
    }

    public void setLockFirmwareVersion(String lockFirmwareVersion) {
        this.lockFirmwareVersion = lockFirmwareVersion;
    }

    public String getFingerHardwareVersion() {
        return fingerHardwareVersion;
    }

    public void setFingerHardwareVersion(String fingerHardwareVersion) {
        this.fingerHardwareVersion = fingerHardwareVersion;
    }

    public String getFingerFirmwareVersion() {
        return fingerFirmwareVersion;
    }

    public void setFingerFirmwareVersion(String fingerFirmwareVersion) {
        this.fingerFirmwareVersion = fingerFirmwareVersion;
    }

    public String getNbSerialNumber() {
        return nbSerialNumber;
    }

    public void setNbSerialNumber(String nbSerialNumber) {
        this.nbSerialNumber = nbSerialNumber;
    }

    public String getSimICCID() {
        return simICCID;
    }

    public void setSimICCID(String simICCID) {
        this.simICCID = simICCID;
    }

    public String getNbSignalIntensity() {
        return nbSignalIntensity;
    }

    public void setNbSignalIntensity(String nbSignalIntensity) {
        this.nbSignalIntensity = nbSignalIntensity;
    }

    public String getNbVersion() {
        return nbVersion;
    }

    public void setNbVersion(String nbVersion) {
        this.nbVersion = nbVersion;
    }

    public String getSimIMSI() {
        return simIMSI;
    }

    public void setSimIMSI(String simIMSI) {
        this.simIMSI = simIMSI;
    }
}