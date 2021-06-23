package com.miaxis.btlockcommanderdemo.contract;

import com.miaxis.btlockcommanderdemo.model.entity.PersonDto;

import java.util.List;

public interface LockPersonContract {
    interface View extends BaseContract.View {
//        void downPersonListByIdListCallback(List<PersonDto> personDtoList);
    }

    interface Presenter extends BaseContract.Presenter {
//        void downPersonListByIdList(List<String> personIdList);
    }
}
