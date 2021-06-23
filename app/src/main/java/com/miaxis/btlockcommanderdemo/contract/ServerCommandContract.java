package com.miaxis.btlockcommanderdemo.contract;

import com.miaxis.btlockcommander.entity.BindPersonResult;
import com.miaxis.btlockcommanderdemo.model.entity.NbCmdDto;
import com.miaxis.btlockcommanderdemo.model.entity.NbPerson;

import java.util.List;

public interface ServerCommandContract {
    interface View extends BaseContract.View {
        void downNbCmdDtoCallback(boolean result, String message);
        void loadNbCmdDtoCallback(List<NbPerson> nbPersonList);
        void bindNbPersonCallback(boolean result);
        void bindPersonResultPassThroughCallback(int lockResult, boolean platformResult, String personId);
    }

    interface Presenter extends BaseContract.Presenter {
        void downNbCmdDto(String serialNumber);
        void loadNbCmdDto(String serialNumber);
        void bindNbPerson(NbPerson nbPerson);
        void bindPersonResultPassThrough(String serialNumber, BindPersonResult bindPersonResult);
        String getNameByPersonId(String personId);
    }
}
