package com.miaxis.btlockcommanderdemo.model;

import com.miaxis.btlockcommanderdemo.manager.DaoManager;
import com.miaxis.btlockcommanderdemo.model.entity.NbCmdDto;
import com.miaxis.btlockcommanderdemo.model.local.greenDao.gen.NbCmdDtoDao;

import java.util.List;

public class NbCmdDtoModel {

    public static synchronized void saveNbCmdDtoList(List<NbCmdDto> nbCmdDtoList) {
        for (NbCmdDto nbCmdDto : nbCmdDtoList) {
            saveNbCmdDto(nbCmdDto);
        }
    }

    public static void saveNbCmdDto(NbCmdDto nbCmdDto) {
        NbCmdDtoDao nbCmdDtoDao = DaoManager.getInstance().getDaoSession().getNbCmdDtoDao();
        NbCmdDto unique = nbCmdDtoDao.queryBuilder()
                .where(NbCmdDtoDao.Properties.SerialNumber.eq(nbCmdDto.getSerialNumber()))
                .where(NbCmdDtoDao.Properties.PersonId.eq(nbCmdDto.getPersonId()))
                .unique();
        if (unique != null) {
            if (unique.getCreateTime() < nbCmdDto.getCreateTime()) {
                nbCmdDtoDao.delete(unique);
                nbCmdDtoDao.insert(nbCmdDto);
            }
        } else {
            nbCmdDtoDao.insert(nbCmdDto);
        }
    }

    public static List<NbCmdDto> loadNbCmdDtoListBySerialNUmber(String serialNumber) {
        List<NbCmdDto> list = DaoManager.getInstance().getDaoSession().getNbCmdDtoDao().queryBuilder()
                .where(NbCmdDtoDao.Properties.SerialNumber.eq(serialNumber))
                .orderDesc(NbCmdDtoDao.Properties.CreateTime)
                .list();
        return list;
    }

    public static void deleteNbCmdDto(String serialNumber, String personId) {
        NbCmdDtoDao nbCmdDtoDao = DaoManager.getInstance().getDaoSession().getNbCmdDtoDao();
        NbCmdDto unique = nbCmdDtoDao.queryBuilder()
                .where(NbCmdDtoDao.Properties.SerialNumber.eq(serialNumber))
                .where(NbCmdDtoDao.Properties.PersonId.eq(personId))
                .unique();
        if (unique != null) {
            nbCmdDtoDao.delete(unique);
        }
    }

}
