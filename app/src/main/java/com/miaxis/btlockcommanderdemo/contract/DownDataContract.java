package com.miaxis.btlockcommanderdemo.contract;

import com.miaxis.btlockcommanderdemo.model.entity.DownDataDto;

import java.util.List;

public interface DownDataContract {
    interface View extends BaseContract.View {
        void getDownDataListCallback(List<DownDataDto> downDataDtoList, String message);

        void downDataSynchronizedCallback(boolean result, DownDataDto downDataDto, String message);

        void sendUpDataCallback(boolean result, String command, DownDataDto downDataDto, String message);
    }

    interface Presenter extends BaseContract.Presenter {
        void getDownDataList(String serialNumber);

        void downDataSynchronized(DownDataDto downDataDto);

        boolean checkRelationship(byte code);

        void sendUpData(String serialNumber, String command, String upData);
    }
}
