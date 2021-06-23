package com.miaxis.btlockcommanderdemo.app;

import android.app.Application;
import android.util.Log;

import com.annimon.stream.function.Consumer;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.btlockcommander.util.LoggerFactory;
import com.miaxis.btlockcommanderdemo.MyEventBusIndex;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.manager.DaoManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.util.FileUtil;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import androidx.multidex.MultiDex;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BtLockCommanderApp extends Application {

    private static BtLockCommanderApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LoggerFactory.setDebug(true);
        MultiDex.install(this);
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        CrashCatch.getInstance().setCrashHandler((t, e) -> {
            try {
                ToastManager.toast(this, "CrashException:\nThread:" + t.getName() + " ,Exception:" + e.getMessage(), ToastManager.ERROR);
                Log.e("CrashException", "Thread:" + t.getName() + " ,Exception:" + e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
                Log.e("CrashException", "Exception:" + e1.getMessage());
            }
        });
    }

    public static BtLockCommanderApp getInstance() {
        return instance;
    }

    public void initApplicationAsync(Consumer<Boolean> consumer) {
        FileUtil.initDirectory();
        DaoManager.getInstance().initDbHelper(getApplicationContext(), "BtLockCommander" + (ValueUtil.APP_VERSION ? "CS" : "IS") + ".db");
        ConfigManager.getInstance().checkConfig();
        consumer.accept(BluetoothManager.getInstance().init(this));
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                //.retryOnConnectionFailure(true)//重试一次
                .build();
        return new Retrofit.Builder().client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

}
