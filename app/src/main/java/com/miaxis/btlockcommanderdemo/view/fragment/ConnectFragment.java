package com.miaxis.btlockcommanderdemo.view.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.btlockcommander.entity.BaseStation;
import com.miaxis.btlockcommander.entity.LockVersion;
import com.miaxis.btlockcommander.manager.BtLockCommander;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.contract.ConnectContract;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.presenter.ConnectPresenter;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;
import com.miaxis.btlockcommanderdemo.view.dialog.BaseStationDialogFragment;
import com.miaxis.btlockcommanderdemo.view.dialog.LockVersionDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectFragment extends BaseFragment implements ConnectContract.View {

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.ll_profile)
    LinearLayout llProfile;
    @BindView(R.id.tv_connect)
    TextView tvConnect;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.ll_connect_failed)
    LinearLayout llConnectFailed;

    private OnFragmentInteractionListener mListener;
    private ConnectContract.Presenter presenter;
    private BtDevice btDevice;
    private Disposable countDownDisposable;
    private MaterialDialog waitDialog;

    public static ConnectFragment newInstance(BtDevice btDevice) {
        ConnectFragment connectFragment = new ConnectFragment();
        connectFragment.setBtDevice(btDevice);
        return connectFragment;
    }

    public ConnectFragment() {
        //Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_connect;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        presenter = new ConnectPresenter(this, this);
        connectDevice();
    }

    @Override
    protected void initView() {
        btnBack.setOnClickListener(v -> {
            BluetoothManager.getInstance().disConnected();
            mListener.backToStack(null);
        });
        btnRetry.setOnClickListener(v -> {
            llConnectFailed.setVisibility(View.INVISIBLE);
            BtLockCommander.getInstance().disconnect();
            connectDevice();
        });
        waitDialog = new MaterialDialog.Builder(getContext())
                .progress(true, 100)
                .content("读取蓝牙中，请稍后...")
                .cancelable(false)
                .autoDismiss(false)
                .build();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        switch (event.getType()) {
            case BluetoothEvent.ON_STATE_ON:
                connectDevice();
                break;
            case BluetoothEvent.ON_STATE_TURNING_ON:
                ToastManager.toast(getContext(), "蓝牙开启中", ToastManager.INFO);
                break;
            case BluetoothEvent.ON_CONNECT_SUCCESS:
                closeCount();
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvConnect.setText("连接成功，正在开启通知服务");
                BluetoothManager.getInstance().startNotify();
                break;
            case BluetoothEvent.ON_CONNECT_FAILED:
                closeCount();
                tvConnect.setText("连接失败");
                llConnectFailed.setVisibility(View.VISIBLE);
                break;
            case BluetoothEvent.ON_DIS_CONNECT:
                tvConnect.setText("连接已断开");
                llConnectFailed.setVisibility(View.VISIBLE);
                break;
            case BluetoothEvent.ON_NOTIFY_SUCCESS:
                closeCount();
                tvConnect.setText("正在读取基础信息");
                if (!BluetoothManager.getInstance().writeLockVersionData()) {
                    tvConnect.setText("读取基础信息失败");
                    llConnectFailed.setVisibility(View.VISIBLE);
                } else {
                    connectCountDown(BtLockCommander.getInstance().getReadBaseInfoTimeOut() / 1000, tvConnect);
                }
                break;
            case BluetoothEvent.ON_NOTIFY_FAILED:
                closeCount();
                tvConnect.setText("通知服务开启失败");
                llConnectFailed.setVisibility(View.VISIBLE);
                break;
            case BluetoothEvent.ON_NOTIFY_LOCK_VERSION:
                closeCount();
                LockVersion lockVersion = (LockVersion) event.getData();
                btDevice.setSerialNumber(lockVersion.getNbSerialNumber());
                if (ValueUtil.APP_VERSION) {
                    tvConnect.setText("正在联网鉴权");
                    presenter.checkAuth(lockVersion, lockVersion.getNbSerialNumber());
                } else {
                    tvConnect.setText("基础信息读取成功");
                    BluetoothManager.getInstance().saveBtDevice(btDevice, "");
                    mListener.enterAnotherFragment(BlueLockFragment.newInstance(btDevice, lockVersion));
                }
                break;
            case BluetoothEvent.ON_NOTIFY_BASE_STATION_DATA:
                waitDialog.dismiss();
                BaseStation baseStation = (BaseStation) event.getData();
                BaseStationDialogFragment.newInstance(baseStation, () -> {
                    if (BluetoothManager.getInstance().writeBaseStationData()) {
                        waitDialog.show();
                        connectCountDown(15, waitDialog.getContentView());
                    }
                }).show(getChildFragmentManager(), "BaseStationDialogFragment");
                break;
            default:
                break;
        }
    }

    @Override
    public void checkAuthCallback(boolean result, LockVersion lockVersion, String message) {
        if (result) {
            BluetoothManager.getInstance().saveBtDevice(btDevice, ConfigManager.getInstance().getConfig().getUsername());
            mListener.enterAnotherFragment(BlueLockFragment.newInstance(btDevice, lockVersion));
        } else {
            tvConnect.setText("鉴权失败\n原因：" + message);
            llConnectFailed.setVisibility(View.VISIBLE);
            new MaterialDialog.Builder(getContext())
                    .title("鉴权失败")
                    .content("原因：" + message + "\n您可以查看关于该锁的基础信息，并以此向锁平台寻求帮助。")
                    .positiveText("查看基础信息")
                    .onPositive((dialog, which) -> {
                        LockVersionDialogFragment.newInstance(lockVersion, () -> {
                            if (BluetoothManager.getInstance().writeBaseStationData()) {
                                waitDialog.show();
                                connectCountDown(BtLockCommander.getInstance().getReadBaseInfoTimeOut() / 1000, waitDialog.getContentView());
                            }
                        }).show(getChildFragmentManager(), "LockVersionDialogFragment");
                    })
                    .negativeText("取消")
                    .show();
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
        presenter.doDestroy();
        EventBus.getDefault().unregister(this);
        closeCount();
        BtLockCommander.getInstance().stopScan(callback);
    }

    public void setBtDevice(BtDevice btDevice) {
        this.btDevice = btDevice;
    }

    private void connectDevice() {
        if (BtLockCommander.getInstance().isBluetoothOpen()) {
            BluetoothManager.getInstance().disConnected();
            tvConnect.setText("扫描中");
            connectCountDown(BtLockCommander.getInstance().getConnectTimeOut() / 1000, tvConnect);
            BtLockCommander.getInstance().startScan(callback);
            llConnectFailed.setVisibility(View.INVISIBLE);
        } else {
            BtLockCommander.getInstance().openBluetooth();
        }
    }

    private void closeCount() {
        if (countDownDisposable != null) {
            countDownDisposable.dispose();
        }
    }

    private void connectCountDown(int count, TextView textView) {
        closeCount();
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
                        if (textView != null) {
                            String text = textView.getText().toString();
                            int split = text.indexOf("（");
                            String oriText = split != -1 ? text.substring(0, split) : text;
                            textView.setText(oriText + "（ " + num + "S ）");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("asd", "倒计时出错");
                    }

                    @Override
                    public void onComplete() {
                        if (textView != null) {
                            textView.setText("连接失败");
                            BtLockCommander.getInstance().stopScan(callback);
                            llConnectFailed.setVisibility(View.VISIBLE);
                            if (waitDialog.isShowing()) {
                                waitDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private BluetoothAdapter.LeScanCallback callback = this::onLeScan;

    private void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (btDevice != null && TextUtils.equals(device.getAddress(), btDevice.getMac())) {
            BtLockCommander.getInstance().stopScan(callback);
            tvConnect.setText("连接中");
            connectCountDown(BtLockCommander.getInstance().getConnectTimeOut() / 1000, tvConnect);
            BluetoothManager.getInstance().connect(device);
        }
    }
}
