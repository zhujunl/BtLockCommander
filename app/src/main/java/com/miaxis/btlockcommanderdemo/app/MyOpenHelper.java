package com.miaxis.btlockcommanderdemo.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.miaxis.btlockcommanderdemo.model.local.greenDao.gen.BtDeviceDao;
import com.miaxis.btlockcommanderdemo.model.local.greenDao.gen.ConfigDao;
import com.miaxis.btlockcommanderdemo.model.local.greenDao.gen.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * 适配数据库升级时数据迁移
 * Created by tang.yf on 2018/8/30.
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            }, ConfigDao.class, BtDeviceDao.class);
        }
    }
}
