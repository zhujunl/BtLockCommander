package com.miaxis.btlockcommanderdemo.model.entity;

public class NbPerson {

    private String personId;
    private String name;
    private boolean status;
    private boolean open;

    public NbPerson() {
    }

    public NbPerson(String personId, String name, boolean status, boolean open) {
        this.personId = personId;
        this.name = name;
        this.status = status;
        this.open = open;
    }

    private NbPerson(Builder builder) {
        setPersonId(builder.personId);
        setName(builder.name);
        setStatus(builder.status);
        setOpen(builder.open);
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public static final class Builder {
        private String personId;
        private String name;
        private boolean status;
        private boolean open;

        public Builder() {
        }

        public Builder personId(String val) {
            personId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder status(boolean val) {
            status = val;
            return this;
        }

        public Builder open(boolean val) {
            open = val;
            return this;
        }

        public NbPerson build() {
            return new NbPerson(this);
        }
    }
}
