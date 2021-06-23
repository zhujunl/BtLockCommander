package com.miaxis.btlockcommanderdemo.presenter;

import com.miaxis.btlockcommanderdemo.contract.LockPersonContract;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

public class LockPersonPresenter extends BasePresenter<FragmentEvent> implements LockPersonContract.Presenter {

    private LockPersonContract.View view;

    public LockPersonPresenter(LifecycleProvider<FragmentEvent> provider, LockPersonContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void doDestroy() {
        view = null;
    }
}
