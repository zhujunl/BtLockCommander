package com.miaxis.btlockcommanderdemo.presenter;

import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.contract.BlueLockContract;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.model.entity.NbUpdateFirmwareDto;
import com.miaxis.btlockcommanderdemo.model.net.NetLockNet;
import com.miaxis.btlockcommanderdemo.model.net.ResponseEntity;
import com.miaxis.btlockcommanderdemo.util.FileUtil;
import com.miaxis.btlockcommanderdemo.util.RSAUtil;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class BlueLockPresenter extends BasePresenter<FragmentEvent> implements BlueLockContract.Presenter {

    private BlueLockContract.View view;

    public BlueLockPresenter(LifecycleProvider<FragmentEvent> provider, BlueLockContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void getUpdate(String serialNumber, String version) {
        if (!ValueUtil.APP_VERSION && new File(FileUtil.LOCK_UPDATE_FILE).list().length > 0) {
            Observable.create((ObservableOnSubscribe<File>) emitter -> {
                File[] fileList = new File(FileUtil.LOCK_UPDATE_FILE).listFiles();
                File file = fileList[fileList.length - 1];
                emitter.onNext(file);
            })
                    .subscribeOn(Schedulers.io())
                    .compose(getProvider().bindToLifecycle())
                    .observeOn(Schedulers.io())
                    .map(file -> {
                        NbUpdateFirmwareDto nbUpdateFirmwareDto = new NbUpdateFirmwareDto();
                        nbUpdateFirmwareDto.setVersion(file.getName());
                        nbUpdateFirmwareDto.setData(FileUtil.fileToBase64(file));
                        nbUpdateFirmwareDto.setLocal(true);
                        return nbUpdateFirmwareDto;
                    })
                    .subscribe(nbUpdateFirmwareDto -> {
                        if (view != null) {
                            view.getLockUpdateCallback(nbUpdateFirmwareDto);
                        }
                    }, throwable -> {
                        if (view != null) {
                            view.getLockUpdateCallback(null);
                        }
                    });
            // TODO: 2020/5/27
            //重复代码片段20200527
            //            Observable.create((ObservableOnSubscribe<File>) emitter -> {
            //                File[] fileList = new File(FileUtil.FINGERPRINT_UPDATE_FILE).listFiles();
            //                File file = fileList[fileList.length - 1];
            //                emitter.onNext(file);
            //            })
            //                    .subscribeOn(Schedulers.io())
            //                    .compose(getProvider().bindToLifecycle())
            //                    .observeOn(Schedulers.io())
            //                    .map(file -> {
            //                        NbUpdateFirmwareDto nbUpdateFirmwareDto = new NbUpdateFirmwareDto();
            //                        nbUpdateFirmwareDto.setVersion(file.getName());
            //                        nbUpdateFirmwareDto.setData(FileUtil.fileToBase64(file));
            //                        nbUpdateFirmwareDto.setLocal(true);
            //                        return nbUpdateFirmwareDto;
            //                    })
            //                    .subscribe(nbUpdateFirmwareDto -> {
            //                        if (view != null) {
            //                            view.getFingerprintUpdateCallback(nbUpdateFirmwareDto);
            //                        }
            //                    }, throwable -> {
            //                        if (view != null) {
            //                            view.getFingerprintUpdateCallback(null);
            //                        }
            //                    });
        } else {
            Config config = ConfigManager.getInstance().getConfig();
            Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                    emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                    .subscribeOn(Schedulers.io())
                    .compose(getProvider().bindToLifecycle())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<Retrofit, ObservableSource<ResponseEntity<NbUpdateFirmwareDto>>>) retrofit -> {
                        NetLockNet netLockNet = retrofit.create(NetLockNet.class);
                        return netLockNet.getUpdate(RSAUtil.getHostCertificate(config.getHostCertificate()), serialNumber, version);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseEntity -> {
                        if (responseEntity.getCode().equals(ValueUtil.NET_SUCCESS) && responseEntity.getData() != null) {
                            NbUpdateFirmwareDto data = responseEntity.getData();
                            data.setLocal(false);
                            if (view != null) {
                                view.getLockUpdateCallback(data);
                            }
                        }
                    }, throwable -> {
                        if (view != null) {
                            view.getLockUpdateCallback(null);
                        }
                    });
        }
    }

    @Override
    public void doDestroy() {
        view = null;
    }
}
