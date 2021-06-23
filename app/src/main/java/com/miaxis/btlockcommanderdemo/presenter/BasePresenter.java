package com.miaxis.btlockcommanderdemo.presenter;

import android.text.TextUtils;

import com.miaxis.btlockcommander.util.LoggerFactory;
import com.miaxis.btlockcommanderdemo.model.entity.MyException;
import com.trello.rxlifecycle3.LifecycleProvider;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

/**
 * Created by 一非 on 2018/4/9.
 */

public class BasePresenter<T> {
    private LifecycleProvider<T> provider;

    public BasePresenter(LifecycleProvider<T> provider) {
        this.provider = provider;
    }

    public LifecycleProvider<T> getProvider() {
        return provider;
    }

    private LoggerFactory mLoggerFactory = new LoggerFactory(this);

    protected String handleErrorMessage(Throwable throwable, String errorMessage) {
        if (throwable == null) {
            return "错误";
        }
        throwable.printStackTrace();
        String error = throwable.getMessage() + "";
        mLoggerFactory.e("handleErrorMessage:" + throwable.getMessage());
        if (isNetException(throwable)) {
            return "联网错误";
        } else if (throwable instanceof MyException) {
            return error;
        } else if (error.contains("403")) {
            return "证书错误";
        } else {
            return TextUtils.isEmpty(errorMessage) ? "出现错误" : errorMessage;
        }
    }

    protected static boolean isNetException(Throwable throwable) {
        return throwable instanceof SocketTimeoutException
                || throwable instanceof ConnectException
                || throwable instanceof HttpException
                || throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
    }

}
