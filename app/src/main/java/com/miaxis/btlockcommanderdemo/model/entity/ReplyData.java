package com.miaxis.btlockcommanderdemo.model.entity;

public class ReplyData {

    private String reply;

    public ReplyData() {
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }


    @Override
    public String toString() {
        return "ReplyData{" +
                "reply='" + reply + '\'' +
                '}';
    }
}
