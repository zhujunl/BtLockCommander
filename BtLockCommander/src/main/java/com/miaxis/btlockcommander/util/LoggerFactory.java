package com.miaxis.btlockcommander.util;

import android.util.Log;

/**
 * @author Admin
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 */
public class LoggerFactory {

    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        LoggerFactory.debug = debug;
        Log.e("Logger", "debug:" + debug);
    }

    private String TAG = null;

    public LoggerFactory() {
        this(LoggerFactory.class);
    }

    public LoggerFactory(Object ob) {
        this(ob == null ? null : ob.getClass());
    }

    public LoggerFactory(Class clazz) {
        if (debug) {
            this.TAG = clazz == null ? LoggerFactory.class.getSimpleName() : clazz.getSimpleName();
        }
    }

    public void e(String msg) {
        if (debug) {
            Log.e(this.TAG, String.valueOf(msg));
        }
    }

    public void w(String msg) {
        if (debug) {
            Log.w(this.TAG, String.valueOf(msg));
        }
    }

    public void i(String msg) {
        if (debug) {
            Log.i(this.TAG, String.valueOf(msg));
        }
    }

    public void d(String msg) {
        if (debug) {
            Log.d(this.TAG, String.valueOf(msg));
        }
    }

    public void v(String msg) {
        if (debug) {
            Log.v(this.TAG, String.valueOf(msg));
        }
    }
}
