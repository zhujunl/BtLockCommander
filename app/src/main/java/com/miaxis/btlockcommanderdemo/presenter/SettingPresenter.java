package com.miaxis.btlockcommanderdemo.presenter;

import com.miaxis.btlockcommanderdemo.contract.SettingContract;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

public class SettingPresenter extends BasePresenter<FragmentEvent> implements SettingContract.Presenter  {

    private SettingContract.View view;

    public SettingPresenter(LifecycleProvider<FragmentEvent> provider, SettingContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void doDestroy() {
        view = null;
    }
}
