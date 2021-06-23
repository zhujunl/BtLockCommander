package com.miaxis.btlockcommanderdemo.manager;

import com.annimon.stream.function.Consumer;
import com.miaxis.btlockcommanderdemo.model.ConfigModel;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigManager {

    private ConfigManager() {}

    public static ConfigManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ConfigManager instance = new ConfigManager();
    }

    /** ================================ 静态内部类单例写法 ================================ **/

    private Config config;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void checkConfig() {
        config = DaoManager.getInstance().getDaoSession().getConfigDao().load(1L);
        if (config == null) {
            config = new Config.Builder()
                    .id(1L)
                    .baseUrl(ValueUtil.DEFAULT_BASE_URL)
                    .hostCertificate(ValueUtil.HOST_CERTIFICATE)
                    .username("")
                    .password("")
                    .build();
            DaoManager.getInstance().getDaoSession().getConfigDao().insert(config);
        }
        if (!ValueUtil.APP_VERSION) {
            config.setUsername("");
            config.setPassword("");
        }
    }

    public void saveConfig(Config config, Consumer<Boolean> consumer) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            ConfigModel.saveConfig(config);
            this.config = config;
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer::accept, throwable -> consumer.accept(Boolean.FALSE));
    }

}
