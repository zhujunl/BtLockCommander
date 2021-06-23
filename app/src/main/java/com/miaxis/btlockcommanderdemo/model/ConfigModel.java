package com.miaxis.btlockcommanderdemo.model;

import com.miaxis.btlockcommanderdemo.manager.DaoManager;
import com.miaxis.btlockcommanderdemo.model.entity.Config;

public class ConfigModel {

    public static void saveConfig(Config config) {
        DaoManager.getInstance().getDaoSession().getConfigDao().deleteByKey(1L);
        DaoManager.getInstance().getDaoSession().getConfigDao().insert(config);
    }

}
