package com.miaxis.btlockcommander.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class BleUtil {

    public static final String OPEN_TYPE_FINGER = "01"; // 指纹开门
    public static final String OPEN_TYPE_IDCARD = "02"; // 身份证开门
    public static final String OPEN_TYPE_PASSWORD = "03"; // 密码开门
    public static final String OPEN_TYPE_KEY = "04"; // 机械钥匙开门
    public static final String OPEN_TYPE_TEMPORARY_PASSWORD  = "05"; // 临时密码开门
    public static final String OPEN_TYPE_EID = "06"; // eID开门
    public static final String OPEN_TYPE_COERCION = "07"; // eID开门
    public static final String OPEN_TYPE_ADD_PERSON = "0d"; // 添加用户
    public static final String OPEN_TYPE_UPDATE_PERSON = "0e"; // 更新用户
    public static final String OPEN_TYPE_DELETE_PERSON = "0f"; // 删除用户

    public static final String ALARM_TYPE_LOW_POWER = "10"; // 低电量
    public static final String ALARM_TYPE_ILLEGAL_OPERATION = "20"; // 非法操作
    public static final String ALARM_TYPE_PRYING_LOCK = "30"; // 撬锁
    public static final String ALARM_TYPE_OTHER = "40"; // 其它
    public static final String ALARM_TYPE_CLOSE_TIMEOUT = "50"; // 其它
    public static final String ALARM_TYPE_OPEN_TIMEOUT = "60"; // 其它
    public static final String ALARM_TYPE_DOOR_LOCKED = "70"; // 其它

    public static final int LOCK_LOG_PAGE_SIZE = 15;

    /**
     * 判断是否支持BLE
     * @param context
     * @return
     */
    public static boolean isSupportBle(Context context) {
        return context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static String convertOpenType(String openType) {
        switch (openType) {
            case OPEN_TYPE_FINGER:
                return "指纹开门";
            case OPEN_TYPE_IDCARD:
                return "IC卡开门";
            case OPEN_TYPE_PASSWORD:
                return "密码开门";
            case OPEN_TYPE_KEY:
                return "机械钥匙开门";
            case OPEN_TYPE_TEMPORARY_PASSWORD:
                return "临时密码开门";
            case OPEN_TYPE_COERCION:
                return "胁迫指纹开门";
            case OPEN_TYPE_EID:
                return "eID开门";
            case OPEN_TYPE_ADD_PERSON:
                return "添加用户";
            case OPEN_TYPE_UPDATE_PERSON:
                return "更新用户信息";
            case OPEN_TYPE_DELETE_PERSON:
                return "删除用户";
        }
        return "";
    }

    public static String convertAlarmType(String alarmType) {
        switch (alarmType) {
            case ALARM_TYPE_LOW_POWER:
                return "低电量";
            case ALARM_TYPE_ILLEGAL_OPERATION:
                return "非法操作";
            case ALARM_TYPE_PRYING_LOCK:
                return "撬锁";
            case ALARM_TYPE_OTHER:
                return "其它";
            case ALARM_TYPE_CLOSE_TIMEOUT:
                return "关门超时";
            case ALARM_TYPE_OPEN_TIMEOUT:
                return "开门超时";
            case ALARM_TYPE_DOOR_LOCKED:
                return "门已锁";
        }
        return "";
    }

}
