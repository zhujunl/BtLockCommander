package com.miaxis.btlockcommanderdemo.contract;

public interface LoginContract {
    interface View extends BaseContract.View {
        void loginCallback(boolean result, String message);
    }

    interface Presenter extends BaseContract.Presenter {
        void login(String username, String password, boolean remember);
    }
}
