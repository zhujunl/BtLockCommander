package com.miaxis.btlockcommanderdemo.model;

import com.miaxis.btlockcommanderdemo.manager.DaoManager;
import com.miaxis.btlockcommanderdemo.model.entity.PassThrough;

public class PassThroughModel {

    public static void savePassThrough(String serialNumber, String data) {
        PassThrough passThrough = new PassThrough();
        passThrough.setSerialNumber(serialNumber);
        passThrough.setData(data);
        DaoManager.getInstance().getDaoSession().getPassThroughDao().insert(passThrough);
    }

    public static void deletePassThrough(PassThrough passThrough) {
        DaoManager.getInstance().getDaoSession().getPassThroughDao().delete(passThrough);
    }

}
