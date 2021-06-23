package com.miaxis.btlockcommanderdemo.presenter;

import android.text.TextUtils;

import com.miaxis.btlockcommander.entity.LockVersion;
import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.contract.ConnectContract;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.model.net.NetLockNet;
import com.miaxis.btlockcommanderdemo.model.net.ResponseEntity;
import com.miaxis.btlockcommanderdemo.util.RSAUtil;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ConnectPresenter extends BasePresenter<FragmentEvent> implements ConnectContract.Presenter {

    private ConnectContract.View view;

    public ConnectPresenter(LifecycleProvider<FragmentEvent> provider, ConnectContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void checkAuth(LockVersion lockVersion, String serialNumber) {
        Config config = ConfigManager.getInstance().getConfig();
        Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap((Function<Retrofit, ObservableSource<ResponseEntity>>) retrofit -> {
                    NetLockNet nbCmdNet = retrofit.create(NetLockNet.class);
                    String hostCertificate = RSAUtil.getHostCertificate(config.getHostCertificate());
                    return nbCmdNet.checkAuth(hostCertificate, serialNumber);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseEntity -> {
                    if (view != null) {
                        if (responseEntity != null && TextUtils.equals(responseEntity.getCode(), ValueUtil.NET_SUCCESS)) {
                            view.checkAuthCallback(true, lockVersion, "鉴权通过");
                        } else {
                            view.checkAuthCallback(false, lockVersion, "锁平台返回鉴权失败。\n锁序列号为：" + serialNumber);
                        }
                    }
                }, throwable -> {
                    if (view != null) {
                        view.checkAuthCallback(false, lockVersion,
                                handleErrorMessage(throwable, throwable.getMessage()) + "\n锁序列号为：" + serialNumber);
                    }
                });
    }

    @Override
    public void doDestroy() {
        this.view = null;
    }

}
