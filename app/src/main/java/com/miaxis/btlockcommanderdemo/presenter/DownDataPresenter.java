package com.miaxis.btlockcommanderdemo.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.miaxis.btlockcommander.util.LoggerFactory;
import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.contract.DownDataContract;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.model.entity.DownDataDto;
import com.miaxis.btlockcommanderdemo.model.entity.DownDataListDto;
import com.miaxis.btlockcommanderdemo.model.entity.DownDataPacketsDto;
import com.miaxis.btlockcommanderdemo.model.entity.MyException;
import com.miaxis.btlockcommanderdemo.model.entity.ReplyData;
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

public class DownDataPresenter extends BasePresenter<FragmentEvent> implements DownDataContract.Presenter {

    private DownDataContract.View view;

    public DownDataPresenter(LifecycleProvider<FragmentEvent> provider, DownDataContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void getDownDataList(String serialNumber) {
        Config config = ConfigManager.getInstance().getConfig();
        Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap((Function<Retrofit, ObservableSource<ResponseEntity<DownDataListDto>>>) retrofit -> {
                    NetLockNet netLockNet = retrofit.create(NetLockNet.class);
                    String hostCertificate = RSAUtil.getHostCertificate(config.getHostCertificate());
                    return netLockNet.getDownData(hostCertificate, serialNumber);
                })
                .doOnNext(responseEntity -> {
                    if (!TextUtils.equals(responseEntity.getCode(), ValueUtil.NET_SUCCESS) || responseEntity.getData() == null) {
                        throw new MyException(responseEntity.getError());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseEntity -> {
                    mLoggerFactory.e("getDownDataList:" + responseEntity.toString());
                    if (view != null) {
                        List<DownDataDto> downDataDtoList = responseEntity.getData().getDownDatas();
                        view.getDownDataListCallback(downDataDtoList == null ? new ArrayList<>() : downDataDtoList, "下载成功");
                    }
                }, throwable -> {
                    mLoggerFactory.e("getDownDataList:" + throwable.getMessage());
                    if (view != null) {
                        if (throwable instanceof MyException) {
                            view.getDownDataListCallback(null, "锁平台：" + throwable.getMessage());
                        } else {
                            view.getDownDataListCallback(null, handleErrorMessage(throwable, "获取同步指令时出现错误"));
                        }
                    }
                });
    }

    private LoggerFactory mLoggerFactory = new LoggerFactory(this);

    @Override
    public void downDataSynchronized(DownDataDto downDataDto) {
        Observable.create((ObservableOnSubscribe<DownDataDto>) emitter -> emitter.onNext(downDataDto))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(mDownDataDto -> {
                    List<byte[]> commandList = new ArrayList<>();
                    for (DownDataPacketsDto lockCmd : mDownDataDto.getPackets()) {
                        commandList.add(lockCmd.getData().getBytes());
                    }
                    if (!BluetoothManager.getInstance().writeServerCommand(commandList)) {
                        throw new Exception("写入失败");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nbCmdDto -> {
                    mLoggerFactory.e("downDataSynchronized:" + nbCmdDto.toString());
                    if (view != null) {
                        view.downDataSynchronizedCallback(true, downDataDto, "");
                    }
                }, throwable -> {
                    mLoggerFactory.e("downDataSynchronized:" + throwable.getMessage());
                    throwable.printStackTrace();
                    mLoggerFactory.e("" + throwable.getMessage());
                    if (view != null) {
                        view.downDataSynchronizedCallback(false, downDataDto, "" + throwable.getMessage());
                    }
                });
    }

    @Override
    public boolean checkRelationship(byte code) {
        return true;
    }

    @Override
    public void sendUpData(String serialNumber, String command, String upData) {
        Log.e("测试", "command0:" + command);
        Log.e("测试", "responseEntity0:" + upData);
        Config config = ConfigManager.getInstance().getConfig();
        Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap((Function<Retrofit, ObservableSource<ResponseEntity<ReplyData>>>) retrofit -> {
                    NetLockNet netLockNet = retrofit.create(NetLockNet.class);
                    String hostCertificate = RSAUtil.getHostCertificate(config.getHostCertificate());
                    long currentTimeMillis = System.currentTimeMillis();
                    mLoggerFactory.e("==============sendUpData============");
                    mLoggerFactory.e("sendUpData:Header==hostCertificate:" + hostCertificate);
                    mLoggerFactory.e("sendUpData:Field==serialNumber:" + serialNumber);
                    mLoggerFactory.e("sendUpData:Field==upData:" + upData);
                    mLoggerFactory.e("sendUpData:Field==upTime:" + currentTimeMillis);
                    return netLockNet.sendUpData(hostCertificate, serialNumber, upData, currentTimeMillis);
                })
                .doOnNext(responseEntity -> {
                    if (responseEntity != null) {
                        mLoggerFactory.e("测试:sendUpData:doOnNext:" + responseEntity.toString());
                    }
                    if (responseEntity==null){
                        throw new MyException("接口返回数据为空");
                    }
                    if (!TextUtils.equals(responseEntity.getCode(), ValueUtil.NET_SUCCESS)) {
                        throw new MyException(responseEntity.getError());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseEntity -> {
                    if (responseEntity != null) {
                        mLoggerFactory.e("测试:sendUpData:subscribe:" + responseEntity.toString());
                    }
                    if (view != null) {
                        DownDataDto downDataDto = null;
                        ReplyData data = responseEntity.getData();
                        if (data != null && !TextUtils.isEmpty(data.getReply())) {
                            downDataDto = makeDownDataDto(data.getReply());
                        }
                        view.sendUpDataCallback(true, command, downDataDto, "成功");
                    }
                }, throwable -> {
                    //DownDataPresenter: sendUpData:Use JsonReader.setLenient(true) to accept malformed JSON at line 3 column 1 path $
                    mLoggerFactory.e("测试:sendUpData:throwable:" + throwable.getMessage());
                    if (view != null) {
                        if (throwable instanceof MyException) {
                            view.sendUpDataCallback(false, command, null, "锁平台：" + throwable.getMessage());
                        } else {
                            if (throwable.getMessage().contains("failed to connect to")) {
                                view.sendUpDataCallback(false, command, null, handleErrorMessage(throwable, "上传锁端数据时出现错误"));
                            } else {
                                view.sendUpDataCallback(false, command, null, throwable.getMessage());
                            }
                        }
                    }
                });
    }

    @Override
    public void doDestroy() {
        this.view = null;
    }

    private DownDataDto makeDownDataDto(String data) {
        DownDataDto downDataDto = new DownDataDto();
        downDataDto.setId(-1);
        downDataDto.setRequestSeq("-1");
        downDataDto.setType(-1);
        downDataDto.setTypeName("逆传数据");
        downDataDto.setCreateTime(System.currentTimeMillis());
        downDataDto.setReplyCode("");
        downDataDto.setNoNeedWait(true);
        DownDataPacketsDto downDataPacketsDto = new DownDataPacketsDto();
        downDataPacketsDto.setId(-1);
        downDataPacketsDto.setCmdId(-1);
        downDataPacketsDto.setSeq(-1);
        downDataPacketsDto.setData(data);
        List<DownDataPacketsDto> downDataPacketsDtoList = new ArrayList<>();
        downDataPacketsDtoList.add(downDataPacketsDto);
        downDataDto.setPackets(downDataPacketsDtoList);
        return downDataDto;
    }

}
