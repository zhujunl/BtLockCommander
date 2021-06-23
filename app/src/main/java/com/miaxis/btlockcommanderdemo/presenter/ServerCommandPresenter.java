package com.miaxis.btlockcommanderdemo.presenter;

import android.text.TextUtils;

import com.miaxis.btlockcommander.entity.BindPersonResult;
import com.miaxis.btlockcommander.util.LoggerFactory;
import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.contract.ServerCommandContract;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.model.NbCmdDtoModel;
import com.miaxis.btlockcommanderdemo.model.PassThroughModel;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.model.entity.MyException;
import com.miaxis.btlockcommanderdemo.model.entity.NbCmdDto;
import com.miaxis.btlockcommanderdemo.model.entity.NbLockCmdDto;
import com.miaxis.btlockcommanderdemo.model.entity.NbPerson;
import com.miaxis.btlockcommanderdemo.model.net.NetLockNet;
import com.miaxis.btlockcommanderdemo.model.net.ResponseEntity;
import com.miaxis.btlockcommanderdemo.util.RSAUtil;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ServerCommandPresenter extends BasePresenter<FragmentEvent> implements ServerCommandContract.Presenter {

    private ServerCommandContract.View view;
    private List<NbCmdDto> nbCmdDtoList;

    public ServerCommandPresenter(LifecycleProvider<FragmentEvent> provider, ServerCommandContract.View view) {
        super(provider);
        this.view = view;
    }

    private LoggerFactory mLoggerFactory = new LoggerFactory(this);

    @Override
    public void downNbCmdDto(String serialNumber) {
        Config config = ConfigManager.getInstance().getConfig();
        Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap((Function<Retrofit, ObservableSource<ResponseEntity<List<NbCmdDto>>>>) retrofit -> {
                    NetLockNet netLockNet = retrofit.create(NetLockNet.class);
                    String hostCertificate = RSAUtil.getHostCertificate(config.getHostCertificate());
                    return netLockNet.downNbCmdDto(hostCertificate, serialNumber);
                })
                .doOnNext(listResponseEntity -> {
                    if (TextUtils.equals(listResponseEntity.getCode(), ValueUtil.NET_SUCCESS)) {
                        if (listResponseEntity.getData() != null) {
                            NbCmdDtoModel.saveNbCmdDtoList(listResponseEntity.getData());
                        }
                    } else {
                        throw new MyException(listResponseEntity.getError());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResponseEntity -> {
                    mLoggerFactory.e(listResponseEntity.toString());
                    if (view != null) {
                        view.downNbCmdDtoCallback(true, "成功");
                    }
                }, throwable -> {
                    mLoggerFactory.e(throwable.getMessage());
                    if (view != null) {
                        if (throwable instanceof MyException) {
                            view.downNbCmdDtoCallback(false, "锁平台：" + throwable.getMessage());
                        } else {
                            view.downNbCmdDtoCallback(false, handleErrorMessage(throwable, null));
                        }
                    }
                });
    }

    @Override
    public void loadNbCmdDto(String serialNumber) {
        Observable.create((ObservableOnSubscribe<List<NbCmdDto>>) emitter
                -> emitter.onNext(NbCmdDtoModel.loadNbCmdDtoListBySerialNUmber(serialNumber)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(nbCmdDtoList -> ServerCommandPresenter.this.nbCmdDtoList = nbCmdDtoList)
                .map(this::makeNbPersonList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nbCmdDtoList -> {
                    if (view != null) {
                        view.loadNbCmdDtoCallback(nbCmdDtoList);
                    }
                }, throwable -> {
                    if (view != null) {
                        view.loadNbCmdDtoCallback(new ArrayList<>());
                    }
                });
    }

    @Override
    public void bindNbPerson(NbPerson nbPerson) {
        Observable.create((ObservableOnSubscribe<NbCmdDto>) emitter
                -> emitter.onNext(getNbCmdByNbPerson(nbPerson)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(nbCmdDto -> {
                    List<byte[]> commandList = new ArrayList<>();
                    for (NbLockCmdDto lockCmd : nbCmdDto.getLockCmds()) {
                        commandList.add(lockCmd.getPackageData().getBytes());
                    }
                    if (!BluetoothManager.getInstance().writeServerCommand(commandList)) {
                        throw new Exception("写入失败");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nbCmdDto -> {
                    if (view != null) {
                        view.bindNbPersonCallback(true);
                    }
                }, throwable -> {
                    if (view != null) {
                        view.bindNbPersonCallback(false);
                    }
                });
    }

    @Override
    public void bindPersonResultPassThrough(String serialNumber, BindPersonResult bindPersonResult) {
        Config config = ConfigManager.getInstance().getConfig();
        Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap((Function<Retrofit, ObservableSource<ResponseEntity>>) retrofit -> {
                    NetLockNet netLockNet = retrofit.create(NetLockNet.class);
                    return netLockNet.passThrough(RSAUtil.getHostCertificate(config.getHostCertificate()), serialNumber, new String(bindPersonResult.getData()));
                })
                .doOnNext(responseEntity -> {
                    if (bindPersonResult.getResult() == 0) {
                        NbCmdDtoModel.deleteNbCmdDto(serialNumber, bindPersonResult.getPersonId());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseEntity -> {
                    if (view != null) {
                        view.bindPersonResultPassThroughCallback(bindPersonResult.getResult(), true, bindPersonResult.getPersonId());
                    }
                }, throwable -> {
                    PassThroughModel.savePassThrough(serialNumber, new String(bindPersonResult.getData()));
                    if (view != null) {
                        view.bindPersonResultPassThroughCallback(bindPersonResult.getResult(), false, "");
                    }
                });
    }

    @Override
    public String getNameByPersonId(String personId) {
        for (NbCmdDto nbCmdDto : nbCmdDtoList) {
            if (nbCmdDto.getPersonId().equals(personId)) {
                return nbCmdDto.getName();
            }
        }
        return "";
    }

    private NbCmdDto getNbCmdByNbPerson(NbPerson nbPerson) {
        for (NbCmdDto nbCmdDto : nbCmdDtoList) {
            if (TextUtils.equals(nbCmdDto.getPersonId(), nbPerson.getPersonId())) {
                return nbCmdDto;
            }
        }
        return null;
    }

    private List<NbPerson> makeNbPersonList(List<NbCmdDto> nbCmdDtoList) {
        List<NbPerson> nbPersonList = new ArrayList<>();
        for (NbCmdDto nbCmdDto : nbCmdDtoList) {
            NbPerson nbPerson = new NbPerson.Builder()
                    .name(nbCmdDto.getName())
                    .personId(nbCmdDto.getPersonId())
                    .status(false)
                    .open(true)
                    .build();
            nbPersonList.add(nbPerson);
        }
        return nbPersonList;
    }

    @Override
    public void doDestroy() {
        view = null;
    }
}