package com.miaxis.btlockcommanderdemo.presenter;

import android.text.TextUtils;

import com.miaxis.btlockcommander.util.LoggerFactory;
import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.contract.LoginContract;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.model.ConfigModel;
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

public class LoginPresenter extends BasePresenter<FragmentEvent> implements LoginContract.Presenter {

    private LoginContract.View view;

    public LoginPresenter(LifecycleProvider<FragmentEvent> provider, LoginContract.View view) {
        super(provider);
        this.view = view;
    }

    private LoggerFactory mLoggerFactory = new LoggerFactory(this);

    @Override
    public void login(String username, String password, boolean remember) {
        Config config = ConfigManager.getInstance().getConfig();
        Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(userResponseEntity -> {
                    config.setUsername(username);
                    config.setPassword(remember ? password : "");
                    ConfigModel.saveConfig(config);
                })
                .flatMap((Function<Retrofit, ObservableSource<ResponseEntity<String>>>) retrofit -> {
                    NetLockNet nbCmdNet = retrofit.create(NetLockNet.class);
                    String encryptPassword = RSAUtil.encryptPassword(username, password);
                    return nbCmdNet.login(username, encryptPassword);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseEntity -> {
                    mLoggerFactory.e("login:" + responseEntity.toString());
                    if (view != null) {
                        if (TextUtils.equals(responseEntity.getCode(), ValueUtil.NET_SUCCESS)) {
                            if (responseEntity.getData().length() == 32) {
                                config.setHostCertificate(responseEntity.getData());
                                view.loginCallback(true, "登录成功");
                            } else {
                                view.loginCallback(false, "公司证书长度校验不通过");
                            }
                        } else {
                            view.loginCallback(false, "登录失败");
                        }
                    }
                }, throwable -> {
                    mLoggerFactory.e("login:" + throwable.getMessage());
                    throwable.printStackTrace();
                    if (view != null) {
                        view.loginCallback(false, handleErrorMessage(throwable, null));
                    }
                });
    }

    @Override
    public void doDestroy() {
        this.view = null;
    }
}
