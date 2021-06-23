package com.miaxis.btlockcommanderdemo.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class BtDevice implements Serializable {

    private static final long serialVersionUID = -9218024215837095637L;

    @Id(autoincrement = true)
    private Long id;
    private String name;
    @Unique
    private String mac;
    @Unique
    private String serialNumber;
    private String keeper;
    @Transient
    private boolean select;

    @Generated(hash = 1888444552)
    public BtDevice(Long id, String name, String mac, String serialNumber,
            String keeper) {
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.serialNumber = serialNumber;
        this.keeper = keeper;
    }
    @Generated(hash = 571545188)
    public BtDevice() {
    }

    private BtDevice(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setMac(builder.mac);
        setSerialNumber(builder.serialNumber);
        setKeeper(builder.keeper);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getSerialNumber() {
        return this.serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getKeeper() {
        return this.keeper;
    }
    public void setKeeper(String keeper) {
        this.keeper = keeper;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String mac;
        private String serialNumber;
        private String keeper;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder mac(String val) {
            mac = val;
            return this;
        }

        public Builder serialNumber(String val) {
            serialNumber = val;
            return this;
        }

        public Builder keeper(String val) {
            keeper = val;
            return this;
        }

        public BtDevice build() {
            return new BtDevice(this);
        }
    }
}
