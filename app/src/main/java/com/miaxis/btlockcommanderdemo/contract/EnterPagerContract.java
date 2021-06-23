package com.miaxis.btlockcommanderdemo.contract;

import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;

import java.util.List;

public interface EnterPagerContract {
    interface View extends BaseContract.View {
        void loadBtDeviceCallback(List<BtDevice> btDeviceList);
    }

    interface Presenter extends BaseContract.Presenter {
        void loadBtDevice(String keeper);
    }
}
