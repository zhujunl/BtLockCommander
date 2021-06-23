package com.miaxis.btlockcommanderdemo.view.fragment;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.btlockcommander.entity.BaseStation;
import com.miaxis.btlockcommander.entity.FirmwareUpdateResult;
import com.miaxis.btlockcommander.entity.LockVersion;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.contract.BlueLockContract;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.model.entity.NbUpdateFirmwareDto;
import com.miaxis.btlockcommanderdemo.model.entity.PersonDto;
import com.miaxis.btlockcommanderdemo.model.entity.ResponseWriteProgress;
import com.miaxis.btlockcommanderdemo.presenter.BlueLockPresenter;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;
import com.miaxis.btlockcommanderdemo.view.dialog.BaseStationDialogFragment;
import com.miaxis.btlockcommanderdemo.view.dialog.LockVersionDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlueLockFragment extends BaseFragment implements BlueLockContract.View {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_connect_state)
    TextView tvConnectState;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.iv_lock_update_tip)
    ImageView ivLockUpdateTip;
    @BindView(R.id.iv_fingerprint_lock_update_tip)
    ImageView ivFingerprintLockUpdateTip;
    @BindView(R.id.tv_fingerprint_firmware_version)
    TextView tvFingerprintFirmwareVersion;
    @BindView(R.id.fl_blue_lock)
    FrameLayout flBlueLock;
    @BindView(R.id.iv_local_time)
    TextView iv_local_time;
    @BindView(R.id.tv_local_time)
    TextView tv_local_time;


    private PersonPagerFragment personPagerFragment;
    private OnFragmentInteractionListener mListener;
    private MaterialDialog disconnectDialog;
    private MaterialDialog waitDialog;
    private MaterialDialog updateDialog;
    private BlueLockContract.Presenter presenter;
    private Disposable countDownDisposable;
    private BtDevice btDevice;
    private LockVersion lockVersion;
    private NbUpdateFirmwareDto nbLockUpdateFirmwareDto;
    private NbUpdateFirmwareDto nbFingerprintUpdateFirmwareDto;
    private List<PersonDto> personDtoList;

    public static BlueLockFragment newInstance(BtDevice btDevice, LockVersion lockVersion) {
        BlueLockFragment blueLockFragment = new BlueLockFragment();
        blueLockFragment.setBtDevice(btDevice);
        blueLockFragment.setLockVersion(lockVersion);
        return blueLockFragment;
    }

    public BlueLockFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_blue_lock;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        presenter = new BlueLockPresenter(this, this);
        presenter.getUpdate(lockVersion.getNbSerialNumber(), lockVersion.getLockFirmwareVersion());
    }

    @OnClick(R.id.iv_local_time)
    public void sendLocalTime() {
        //ToastManager.toast(getContext(), "时间同步", ToastManager.INFO);
        boolean writeSyncTimeData = BluetoothManager.getInstance().writeSyncTimeData();
        if (writeSyncTimeData) {
            showWaitDialogWithMessage("锁端时间同步中,请稍后");

        } else {
            showWaitDialogWithMessage("发送时间同步指令失败");
        }
    }

    @Override
    protected void initView() {
        initDialog();
        profileImage.setOnClickListener(v -> {
            LockVersionDialogFragment.newInstance(lockVersion, () -> {
                if (BluetoothManager.getInstance().writeBaseStationData()) {
                    showWaitDialogWithMessage("蓝牙写入成功，正在等待响应");
                    connectCountDown(15);
                }
            }).show(getChildFragmentManager(), "LockVersionDialogFragment");
        });
        ivBack.setOnClickListener(v -> disconnectDialog.show());
        tvTitle.setText(btDevice.getName());
        tvSerialNumber.setText("锁序列号：" + lockVersion.getNbSerialNumber());
        tvLockFirmwareVersion.setText("锁  固  件：" + lockVersion.getLockFirmwareVersion());
        tvFingerprintFirmwareVersion.setText("指纹固件：" + lockVersion.getFingerFirmwareVersion());
        tvLockFirmwareVersion.setOnClickListener(v -> onUpdate(nbLockUpdateFirmwareDto, 0));
        tv_local_time.setText("本机时间：" + ValueUtil.simpleDateFormat.format(new Date()));
        tvFingerprintFirmwareVersion.setOnClickListener(v -> onUpdate(nbFingerprintUpdateFirmwareDto, 1));
        ivRefresh.setOnClickListener(v -> {
            if (BluetoothManager.getInstance().writeLockVersionData()) {
                showWaitDialogWithMessage("蓝牙写入成功，正在等待响应");
                connectCountDown(15);
            } else {
                ToastManager.toast(getContext(), "蓝牙写入失败", ToastManager.ERROR);
            }
        });
        personPagerFragment = PersonPagerFragment.newInstance(btDevice, pagerItemListener);
        getChildFragmentManager().beginTransaction().replace(R.id.fl_blue_lock, personPagerFragment).commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        Log.e("onBluetoothEvent", "BluetoothEvent:"+event.getType());
        switch (event.getType()) {
            case BluetoothEvent.ON_WRITE_SUCCESS:
                showWaitDialogWithMessage("蓝牙写入成功，正在等待响应");
                connectCountDown(15);
                break;
            case BluetoothEvent.ON_WRITE_FAILED:
                dismissWaitDialogAndCountDown();
                ToastManager.toast(getContext(), "蓝牙写入失败", ToastManager.ERROR);
                break;
            case BluetoothEvent.ON_FILE_RESPONSE_WRITE_SUCCESS:
                ResponseWriteProgress responseWriteProgress = (ResponseWriteProgress) event.getData();
                showWaitDialogWithMessage("当前进度：" + responseWriteProgress.getCurrent() + " / " + responseWriteProgress.getTotal() + "\n" + "传输中");
                connectCountDown(15);
                if (responseWriteProgress.getCurrent() == responseWriteProgress.getTotal()) {
                    ToastManager.toast(getContext(), "升级文件写入成功，请等待锁处理升级", ToastManager.SUCCESS);
                }
                break;
            case BluetoothEvent.ON_FILE_RESPONSE_WRITE_FAILED:
                dismissWaitDialogAndCountDown();
                ToastManager.toast(getContext(), "文件传输失败", ToastManager.ERROR);
                break;
            case BluetoothEvent.ON_FILE_RESPONSE_WRITE_CONTINUE:
                //                String text = waitDialog.getContentView().getText().toString();
                //                text = text.substring(0, text.indexOf("\n") + 1) + "传输中";
                //                showWaitDialogWithMessage(text);
                break;
            case BluetoothEvent.ON_NOTIFY_LOCK_VERSION:
                dismissWaitDialogAndCountDown();
                LockVersion lockVersion = (LockVersion) event.getData();
                btDevice.setSerialNumber(lockVersion.getNbSerialNumber());
                mListener.enterAnotherFragment(BlueLockFragment.newInstance(btDevice, lockVersion));
                break;
            case BluetoothEvent.ON_NOTIFY_BIND_PERSON_ID:
            case BluetoothEvent.ON_NOTIFY_BIND_PERSON_RESULT:
            case BluetoothEvent.ON_OPEN_LOG_COUNT_OF_DAY:
            case BluetoothEvent.ON_OPEN_LOG_RESPONSE:
                dismissWaitDialogAndCountDown();
                break;
            case BluetoothEvent.ON_NOTIFY_FIRMWARE_UPDATE_RESULT:
                FirmwareUpdateResult firmwareUpdateResult = (FirmwareUpdateResult) event.getData();
                dismissWaitDialogAndCountDown();
                ToastManager.toast(getContext(),
                        firmwareUpdateResult.getResult() != 0 ? "升级文件写入失败,CODE:" + (firmwareUpdateResult.getResult()) : "升级文件写入成功",
                        firmwareUpdateResult.getResult() != 0 ? ToastManager.ERROR : ToastManager.SUCCESS);
                break;
            case BluetoothEvent.ON_NOTIFY_BASE_STATION_DATA:
                dismissWaitDialogAndCountDown();
                BaseStation baseStation = (BaseStation) event.getData();
                BaseStationDialogFragment.newInstance(baseStation, () -> {
                    if (BluetoothManager.getInstance().writeBaseStationData()) {
                        showWaitDialogWithMessage("蓝牙写入成功，正在等待响应");
                        connectCountDown(15);
                    }
                }).show(getChildFragmentManager(), "BaseStationDialogFragment");
                break;
            case BluetoothEvent.ON_SYNC_TIME_RESPONSE:
                ToastManager.toast(getContext(), "同步成功", ToastManager.SUCCESS);
                dismissWaitDialogAndCountDown();
                break;
            default:
                break;
        }
    }

    @Override
    public void getLockUpdateCallback(NbUpdateFirmwareDto nbUpdateFirmwareDto) {
        if (nbUpdateFirmwareDto != null) {
            ivLockUpdateTip.setVisibility(View.VISIBLE);
            this.nbLockUpdateFirmwareDto = nbUpdateFirmwareDto;
        }
    }

    @Override
    public void getFingerprintUpdateCallback(NbUpdateFirmwareDto nbUpdateFirmwareDto) {
        if (nbUpdateFirmwareDto != null) {
            ivFingerprintLockUpdateTip.setVisibility(View.VISIBLE);
            this.nbFingerprintUpdateFirmwareDto = nbUpdateFirmwareDto;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disconnectDialog != null) {
            disconnectDialog.dismiss();
        }
        if (waitDialog != null) {
            waitDialog.dismiss();
        }
        if (updateDialog != null) {
            updateDialog.dismiss();
        }
        if (countDownDisposable != null) {
            countDownDisposable.dispose();
        }
        EventBus.getDefault().unregister(this);
        presenter.doDestroy();
    }

    private final PersonPagerFragment.OnPersonPagerItemListener pagerItemListener = new PersonPagerFragment.OnPersonPagerItemListener() {
        @Override
        public void close() {
            mListener.backToStack(EnterPagerFragment.class);
            BluetoothManager.getInstance().disConnected();
        }

        @Override
        public void showWaitDialog(String message, boolean count, int time) {
            showWaitDialogWithMessage(message);
            if (count) {
                connectCountDown(time);
            } else {
                if (countDownDisposable != null) {
                    countDownDisposable.dispose();
                }
            }
        }

        @Override
        public void dismissWaitDialog() {
            dismissWaitDialogAndCountDown();
        }

        @Override
        public void onSynchronizedMode(boolean mode) {
            try {
                if (mode && EventBus.getDefault().isRegistered(BlueLockFragment.this)) {
                    EventBus.getDefault().unregister(BlueLockFragment.this);
                    if (personPagerFragment != null) {
                        personPagerFragment.onSynchronizedMode(true);
                    }
                } else if (!mode && !EventBus.getDefault().isRegistered(BlueLockFragment.this)) {
                    EventBus.getDefault().register(BlueLockFragment.this);
                    if (personPagerFragment != null) {
                        personPagerFragment.onSynchronizedMode(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPersonDtoDownload(List<PersonDto> personDtoList) {
            BlueLockFragment.this.personDtoList = personDtoList;
        }

        @Override
        public List<PersonDto> getPersonDtoList() {
            return BlueLockFragment.this.personDtoList;
        }

        @Override
        public String getWaitDialogMessage() {
            return waitDialog.getContentView().getText().toString();
        }
    };

    public void setBtDevice(BtDevice btDevice) {
        this.btDevice = btDevice;
    }

    public void setLockVersion(LockVersion lockVersion) {
        this.lockVersion = lockVersion;
    }

    private void initDialog() {
        waitDialog = new MaterialDialog.Builder(getContext())
                .content("")
                .progress(true, 100)
                .cancelable(false)
                .autoDismiss(false)
                .build();
        disconnectDialog = new MaterialDialog.Builder(getContext())
                .title("断开连接？")
                .positiveText("确定")
                .onPositive((dialog, which) -> {
                    mListener.backToStack(EnterPagerFragment.class);
                    //mListener.enterAnotherFragment(EnterPagerFragment.newInstance());
                    BluetoothManager.getInstance().disConnected();
                })
                .negativeText("取消")
                .build();
    }

    private void connectCountDown(int count) {
        if (countDownDisposable != null) {
            countDownDisposable.dispose();
        }
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .take(count + 1)
                .map(aLong -> count - aLong)
                .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        countDownDisposable = d;
                    }

                    @Override
                    public void onNext(Long num) {
                        String text = waitDialog.getContentView().getText().toString();
                        int split = text.indexOf("（");
                        String oriText = split != -1 ? text.substring(0, split) : text;
                        waitDialog.setContent(oriText + "（ " + num + "S ）");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("asd", "倒计时出错");
                    }

                    @Override
                    public void onComplete() {
                        waitDialog.dismiss();
                        ToastManager.toast(getContext(), "未收到响应，超时", ToastManager.ERROR);
                    }
                });
    }

    private void showWaitDialogWithMessage(String message) {
        if (waitDialog != null) {
            waitDialog.setContent(message);
            if (!waitDialog.isShowing()) {
                waitDialog.show();
            }
        }
        if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.dismiss();
        }
    }


    private void dismissWaitDialogAndCountDown() {
        if (waitDialog != null) {
            waitDialog.dismiss();
            if (countDownDisposable != null) {
                countDownDisposable.dispose();
            }
        }
        if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.dismiss();
        }
    }

    private void onUpdate(NbUpdateFirmwareDto nbUpdateFirmwareDto, int type) {
        if (nbUpdateFirmwareDto != null && !TextUtils.isEmpty(nbUpdateFirmwareDto.getData())) {
            String title = type == 0 ? "确认升级锁固件？" : "确认升级指纹固件？";
            String versionDescription = nbUpdateFirmwareDto.isLocal()
                    ? "版本来源：本地路径\n" + "固件名：" + nbUpdateFirmwareDto.getVersion() + "\n"
                    : "版本来源：锁平台\n" + "固件名：" + nbUpdateFirmwareDto.getVersion() + "\n";
            updateDialog = new MaterialDialog.Builder(getContext())
                    .title(title)
                    .content(versionDescription + "升级可能会占用您一些时间，请勿断开蓝牙连接或远离锁体。")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        showWaitDialogWithMessage("文件传输中");
                        BluetoothManager.getInstance().writeLockFirmwareUpdateCommand(nbUpdateFirmwareDto);
                    })
                    .negativeText("取消")
                    .show();
        } else {
            ToastManager.toast(getContext(), "暂无升级数据,请稍后再试", ToastManager.INFO);
        }
    }

}
