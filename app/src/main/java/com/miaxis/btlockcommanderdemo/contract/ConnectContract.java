package com.miaxis.btlockcommanderdemo.contract;

import com.miaxis.btlockcommander.entity.LockVersion;

public interface ConnectContract {
    interface View extends BaseContract.View {
        void checkAuthCallback(boolean result, LockVersion lockVersion, String message);
    }

    interface Presenter extends BaseContract.Presenter {
        void checkAuth(LockVersion lockVersion, String serialNumber);
    }
}
