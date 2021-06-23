package com.miaxis.btlockcommanderdemo.manager;

import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ToastManager {

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String INFO = "INFO";

    private static Toast toast;

    public static void toast(Context context, String message, String toastMode) {
        if (toast != null) {
            toast.cancel();
        }
        switch (toastMode) {
            case SUCCESS:
                toast = Toasty.success(context, message, Toast.LENGTH_SHORT, true);
                break;
            case ERROR:
                toast = Toasty.error(context, message, Toast.LENGTH_SHORT, true);
                break;
            case INFO:
                toast = Toasty.info(context, message, Toast.LENGTH_SHORT, true);
                break;
            default:
                toast = Toasty.info(context, "Error ToastMode", Toast.LENGTH_SHORT, false);
        }
        toast.show();
    }

}
