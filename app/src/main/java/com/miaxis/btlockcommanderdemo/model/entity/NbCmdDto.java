package com.miaxis.btlockcommanderdemo.model.entity;

import com.miaxis.btlockcommanderdemo.util.NbLockCmdDtoConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class NbCmdDto {

    @Id
    private Long id;
    private String serialNumber;
    private String personId;
    private String name;
    @Convert(columnType = String.class, converter = NbLockCmdDtoConverter.class)
    private List<NbLockCmdDto> lockCmds;
    private Long createTime;
    @Generated(hash = 137360057)
    public NbCmdDto(Long id, String serialNumber, String personId, String name,
            List<NbLockCmdDto> lockCmds, Long createTime) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.personId = personId;
        this.name = name;
        this.lockCmds = lockCmds;
        this.createTime = createTime;
    }
    @Generated(hash = 1425804306)
    public NbCmdDto() {
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
    public String getPersonId() {
        return this.personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<NbLockCmdDto> getLockCmds() {
        return this.lockCmds;
    }
    public void setLockCmds(List<NbLockCmdDto> lockCmds) {
        this.lockCmds = lockCmds;
    }
    public Long getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

}
