package com.miaxis.btlockcommanderdemo.model;

import android.text.TextUtils;

import com.miaxis.btlockcommanderdemo.manager.DaoManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.model.local.greenDao.gen.BtDeviceDao;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import java.util.List;

public class BtDeviceModel {

    public static List<BtDevice> loadBtDeviceList(String keeper) {
        if (TextUtils.isEmpty(keeper)) {
            return DaoManager.getInstance().getDaoSession().getBtDeviceDao().loadAll();
        } else {
            return DaoManager.getInstance().getDaoSession().getBtDeviceDao().queryBuilder()
                    .where(BtDeviceDao.Properties.Keeper.eq(keeper))
                    .list();
        }
    }

    public static void deleteBtDevice(BtDevice btDevice) {
        DaoManager.getInstance().getDaoSession().getBtDeviceDao().delete(btDevice);
    }

    public static void deleteBtDeviceList(List<BtDevice> btDeviceList) {
        DaoManager.getInstance().getDaoSession().getBtDeviceDao().deleteInTx(btDeviceList);
    }

    public static void saveBtDevice(BtDevice btDevice, String keeper) {
        btDevice.setKeeper(keeper);
        DaoManager.getInstance().getDaoSession().getBtDeviceDao().insertOrReplace(btDevice);
    }

}
