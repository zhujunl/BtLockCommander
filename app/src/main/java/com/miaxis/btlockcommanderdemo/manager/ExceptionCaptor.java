package com.miaxis.btlockcommanderdemo.manager;

import android.app.Application;
import android.util.Log;

import com.miaxis.btlockcommanderdemo.event.UncaughtExceptionEvent;

import org.greenrobot.eventbus.EventBus;

public class ExceptionCaptor implements Thread.UncaughtExceptionHandler {

    private ExceptionCaptor() {}

    public static ExceptionCaptor getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ExceptionCaptor instance = new ExceptionCaptor();
    }

    /** ================================ 静态内部类单例写法 ================================ **/

    private Application application;

    public void init(Application application) {
        this.application = application;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        Log.e("asd", throwable.getMessage());
        EventBus.getDefault().post(new UncaughtExceptionEvent());
    }
}