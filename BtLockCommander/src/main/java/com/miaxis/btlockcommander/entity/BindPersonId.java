package com.miaxis.btlockcommander.entity;

import java.io.Serializable;
import java.util.List;

public class BindPersonId extends BaseLockPacket implements Serializable {

    private static final long serialVersionUID = -8024393689998914234L;

    private int count;
    private List<String> personIdList;

    public BindPersonId() {
    }

    public BindPersonId(int count, List<String> personIdList) {
        this.count = count;
        this.personIdList = personIdList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getPersonIdList() {
        return personIdList;
    }

    public void setPersonIdList(List<String> personIdList) {
        this.personIdList = personIdList;
    }
}
