package com.miaxis.btlockcommanderdemo.model.entity;

import java.util.List;

public class DownDataListDto {

    private List<DownDataDto> downDatas;

    public DownDataListDto() {
    }

    public List<DownDataDto> getDownDatas() {
        return downDatas;
    }

    public void setDownDatas(List<DownDataDto> downDatas) {
        this.downDatas = downDatas;
    }

    @Override
    public String toString() {
        return "DownDataListDto{" +
                "downDatas=" + downDatas +
                '}';
    }
}
