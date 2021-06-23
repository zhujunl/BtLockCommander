package com.miaxis.btlockcommanderdemo.contract;

import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;

import java.util.List;

public interface ScanContract {
    interface View extends BaseContract.View {
        void loadBtDeviceCallback(List<BtDevice> btDeviceList);
        void deleteBtDeviceListCallback(boolean result);
    }

    interface Presenter extends BaseContract.Presenter {
        void loadBtDevice(String keeper);
        void deleteBtDeviceList(List<BtDevice> btDeviceList);
    }
}
