package com.miaxis.btlockcommanderdemo.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miaxis.btlockcommanderdemo.model.entity.NbLockCmdDto;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

public class NbLockCmdDtoConverter implements PropertyConverter<List<NbLockCmdDto>, String> {

    @Override
    public List<NbLockCmdDto> convertToEntityProperty(String databaseValue) {
        if (TextUtils.isEmpty(databaseValue)) {
            return null;
        }
        else {
            return new Gson().fromJson(databaseValue, new TypeToken<List<NbLockCmdDto>>(){}.getType());
        }
    }

    @Override
    public String convertToDatabaseValue(List<NbLockCmdDto> entityProperty) {
        if(entityProperty == null){
            return null;
        }
        else{
            return new Gson().toJson(entityProperty);
        }
    }
}