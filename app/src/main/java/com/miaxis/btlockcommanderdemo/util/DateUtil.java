package com.miaxis.btlockcommanderdemo.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static int getYear() {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        return date.get(Calendar.YEAR);
    }

}
