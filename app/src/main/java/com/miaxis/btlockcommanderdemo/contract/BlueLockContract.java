package com.miaxis.btlockcommanderdemo.contract;

import com.miaxis.btlockcommanderdemo.model.entity.NbUpdateFirmwareDto;

public interface BlueLockContract {
    interface View extends BaseContract.View {
        void getLockUpdateCallback(NbUpdateFirmwareDto nbUpdateFirmwareDto);
        void getFingerprintUpdateCallback(NbUpdateFirmwareDto nbUpdateFirmwareDto);
    }

    interface Presenter extends BaseContract.Presenter {
        void getUpdate(String serialNumber, String version);
    }
}
