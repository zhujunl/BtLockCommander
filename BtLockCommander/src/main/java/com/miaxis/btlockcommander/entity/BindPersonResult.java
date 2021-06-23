package com.miaxis.btlockcommander.entity;

import java.io.Serializable;

public class BindPersonResult extends BaseLockPacket implements Serializable {

    private static final long serialVersionUID = 1693950333821489014L;

    private String personId;
    private int result;

    public BindPersonResult() {
    }

    public BindPersonResult(String personId, int result) {
        this.personId = personId;
        this.result = result;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}
