package com.miaxis.btlockcommanderdemo.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Deprecated
@Entity
public class PassThrough {

    @Id(autoincrement = true)
    private Long id;
    private String serialNumber;
    private String data;
    @Generated(hash = 159962790)
    public PassThrough(Long id, String serialNumber, String data) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.data = data;
    }
    @Generated(hash = 83828749)
    public PassThrough() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSerialNumber() {
        return this.serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }

}
