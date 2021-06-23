package com.miaxis.btlockcommander.entity;

public class BaseStation extends BaseLockPacket {

    private String mode;
    private String earfcn;
    private String earfcnOffset;
    private String pci;
    private String cellid;
    private String rsrp;
    private String rsrq;
    private String rssi;
    private String snr;
    private String band;
    private String tac;
    private String ecl;
    private String txPwr;

    public BaseStation() {
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getEarfcn() {
        return earfcn;
    }

    public void setEarfcn(String earfcn) {
        this.earfcn = earfcn;
    }

    public String getEarfcnOffset() {
        return earfcnOffset;
    }

    public void setEarfcnOffset(String earfcnOffset) {
        this.earfcnOffset = earfcnOffset;
    }

    public String getPci() {
        return pci;
    }

    public void setPci(String pci) {
        this.pci = pci;
    }

    public String getCellid() {
        return cellid;
    }

    public void setCellid(String cellid) {
        this.cellid = cellid;
    }

    public String getRsrp() {
        return rsrp;
    }

    public void setRsrp(String rsrp) {
        this.rsrp = rsrp;
    }

    public String getRsrq() {
        return rsrq;
    }

    public void setRsrq(String rsrq) {
        this.rsrq = rsrq;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getSnr() {
        return snr;
    }

    public void setSnr(String snr) {
        this.snr = snr;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getEcl() {
        return ecl;
    }

    public void setEcl(String ecl) {
        this.ecl = ecl;
    }

    public String getTxPwr() {
        return txPwr;
    }

    public void setTxPwr(String txPwr) {
        this.txPwr = txPwr;
    }
}
