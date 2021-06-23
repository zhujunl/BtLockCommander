package com.miaxis.btlockcommanderdemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.manager.DaoManager;
import com.miaxis.btlockcommanderdemo.model.PassThroughModel;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.model.entity.PassThrough;
import com.miaxis.btlockcommanderdemo.model.net.NetLockNet;
import com.miaxis.btlockcommanderdemo.model.net.ResponseEntity;
import com.miaxis.btlockcommanderdemo.util.RSAUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PassThroughService extends IntentService {

    private static final String ACTION_PASS_THROUGH = "com.miaxis.btlockcommanderdemo.service.action.passthrough";
    private static final String ACTION_RESUME_PASS_THROUGH = "com.miaxis.btlockcommanderdemo.service.action.resume";
    private static final String DATA = "data";
    private static final String SERIAL_NUMBER = "serialNumber";

    public PassThroughService() {
        super("PassThroughService");
    }

    public static void startActionPassThrough(Context context, String serialNumber, byte[] data) {
        Intent intent = new Intent(context, PassThroughService.class);
        intent.setAction(ACTION_PASS_THROUGH);
        intent.putExtra(DATA, data);
        intent.putExtra(SERIAL_NUMBER, serialNumber);
        context.startService(intent);
    }

    public static void startActionResumePassThrough(Context context) {
        Intent intent = new Intent(context, PassThroughService.class);
        intent.setAction(ACTION_RESUME_PASS_THROUGH);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (ACTION_PASS_THROUGH.equals(intent.getAction())) {
                byte[] data = intent.getByteArrayExtra(DATA);
                String serialNumber = intent.getStringExtra(SERIAL_NUMBER);
                handleActionPassThrough(serialNumber, data);
            } else if (ACTION_RESUME_PASS_THROUGH.equals(intent.getAction())) {
                handleActionResumePassThrough();
            }
        }
    }

    private void handleActionPassThrough(String serialNumber, byte[] data) {
        Config config = ConfigManager.getInstance().getConfig();
        Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
            emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Function<Retrofit, ObservableSource<ResponseEntity>>) retrofit -> {
                    NetLockNet netLockNet = retrofit.create(NetLockNet.class);
                    return netLockNet.passThrough(RSAUtil.getHostCertificate(config.getHostCertificate()), serialNumber, new String(data));
                })
                .subscribe(responseEntity -> Log.e("asd", "缺省透传成功" + new String(data))
                        , throwable -> {
                            PassThroughModel.savePassThrough(serialNumber, new String(data));
                            Log.e("asd", "缺省透传失败" + new String(data));
                        });
    }

    private void handleActionResumePassThrough() {
        Config config = ConfigManager.getInstance().getConfig();
        List<PassThrough> passThroughList = DaoManager.getInstance().getDaoSession().getPassThroughDao().loadAll();
        for (PassThrough passThrough : passThroughList) {
            Observable.create((ObservableOnSubscribe<Retrofit>) emitter ->
                emitter.onNext(BtLockCommanderApp.getRetrofitBuilder().baseUrl(config.getBaseUrl()).build()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<Retrofit, ObservableSource<ResponseEntity>>) retrofit -> {
                        NetLockNet netLockNet = retrofit.create(NetLockNet.class);
                        return netLockNet.passThrough(RSAUtil.getHostCertificate(config.getHostCertificate()), passThrough.getSerialNumber(), passThrough.getData());
                    })
                    .subscribe(responseEntity -> {
                        PassThroughModel.deletePassThrough(passThrough);
                        Log.e("asd", "指令续传成功" + passThrough.getData());
                    }, throwable -> {
                        Log.e("asd", "指令续传失败" + passThrough.getData());
                    });
        }
    }

}
