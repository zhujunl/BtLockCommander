package com.miaxis.btlockcommanderdemo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.HttpException;

public class ValueUtil {

    public static final boolean APP_VERSION = false; // true-用户版，false-内部版

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public static final String DEFAULT_BASE_URL = "http://218.27.147.198:8086/";
    //    public static final String DEFAULT_BASE_URL = "http://116.62.155.39:8080/";
    //    public static final String DEFAULT_BASE_URL = "http://222.242.224.75:9001/";
    public static final String NET_SUCCESS = "0";
    //    public static final String HOST_CERTIFICATE = "f6e01e7afd812c2334baa556ca044fe5";
    //public static final String HOST_CERTIFICATE = "082122bcb37e340664f5917d8f63c6d5";
    public static final String HOST_CERTIFICATE = "ef5f4a26bccc6cb8b52357ea9532031c";
    //    public static final String HOST_CERTIFICATE = "8dd8dfe264a0a621a67a9c2b24ff6836";

    public static String getCurVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isNetException(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException
                || throwable instanceof ConnectException
                || throwable instanceof HttpException
                || throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            return true;
        }
        return false;
    }

    public static boolean isHttpFormat(String str) {
        Pattern pattern = Pattern.compile("^(http://)([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}(:)(\\d{1,5})(/)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getRssiString(String rssi) {
        switch (rssi) {
            case "0":
                return "网络连接失败";
            case "99":
                return "寻网中";
            default:
                return rssi;
        }
    }

}
