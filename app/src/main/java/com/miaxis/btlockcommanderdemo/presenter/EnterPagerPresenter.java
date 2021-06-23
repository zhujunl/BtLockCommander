package com.miaxis.btlockcommanderdemo.presenter;

import com.miaxis.btlockcommanderdemo.contract.EnterPagerContract;
import com.miaxis.btlockcommanderdemo.model.BtDeviceModel;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EnterPagerPresenter extends BasePresenter<FragmentEvent> implements EnterPagerContract.Presenter {

    private EnterPagerContract.View view;

    public EnterPagerPresenter(LifecycleProvider<FragmentEvent> provider, EnterPagerContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void loadBtDevice(String keeper) {
        Observable.create((ObservableOnSubscribe<List<BtDevice>>) emitter -> emitter.onNext(BtDeviceModel.loadBtDeviceList(keeper)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(btDeviceList -> {
                    if (view != null) {
                        view.loadBtDeviceCallback(btDeviceList);
                    }
                }, throwable -> {
                    if (view != null) {
                        view.loadBtDeviceCallback(null);
                    }
                });
    }

    @Override
    public void doDestroy() {
        view = null;
    }
}
