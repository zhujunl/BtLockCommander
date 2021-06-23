package com.miaxis.btlockcommanderdemo.view.fragment;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.btlockcommander.manager.BtLockCommander;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.BtDeviceAdapter;
import com.miaxis.btlockcommanderdemo.adapter.BtDeviceManageAdapter;
import com.miaxis.btlockcommanderdemo.app.GlideApp;
import com.miaxis.btlockcommanderdemo.contract.ScanContract;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.presenter.ScanPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends BaseFragment implements ScanContract.View {

    private static final int SCAN_TIME = 30;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_local_drop)
    ImageView ivLocalDrop;
    @BindView(R.id.ll_local_device)
    LinearLayout llLocalDevice;
    @BindView(R.id.rv_local_device)
    RecyclerView rvLocalDevice;
    @BindView(R.id.iv_scan_drop)
    ImageView ivScanDrop;
    @BindView(R.id.ll_scan_device)
    LinearLayout llScanDevice;
    @BindView(R.id.rv_scan_device)
    RecyclerView rvScanDevice;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_select_all)
    ImageView ivSelectAll;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;

    private OnFragmentInteractionListener mListener;
    private ScanContract.Presenter presenter;
    private BtDeviceManageAdapter<BtDevice> localDeviceAdapter;
    private BtDeviceAdapter<BtDevice> scanDeviceAdapter;
    private Disposable countDownDisposable;

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    public ScanFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_scan;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        presenter = new ScanPresenter(this, this);
        presenter.loadBtDevice(ConfigManager.getInstance().getConfig().getUsername());
    }

    @Override
    protected void initView() {
        ivBack.setOnClickListener(v -> mListener.backToStack(null));
        ivSetting.setOnClickListener(v -> mListener.enterAnotherFragment(SettingFragment.newInstance()));
        btnScan.setOnClickListener(v -> {
            if (TextUtils.equals(btnScan.getText(), "扫描")) {
                if (BtLockCommander.getInstance().startScan(callback)) {
                    onScanSuccess();
                } else {
                    BtLockCommander.getInstance().openBluetooth();
                }
            } else if (btnScan.getText().toString().contains("正在扫描")) {
                if (countDownDisposable != null) {
                    countDownDisposable.dispose();
                }
                btnScan.setText("扫描");
                BtLockCommander.getInstance().stopScan(callback);
            }
        });
        llLocalDevice.setOnClickListener(v -> {
            rvLocalDevice.setVisibility(rvLocalDevice.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            GlideApp.with(ScanFragment.this).load(rvLocalDevice.getVisibility() == View.VISIBLE
                    ? getResources().getDrawable(R.drawable.ic_action_drop_down)
                    : getResources().getDrawable(R.drawable.ic_action_drop_up))
                    .into(ivLocalDrop);
        });
        llScanDevice.setOnClickListener(v -> {
            rvScanDevice.setVisibility(rvScanDevice.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            GlideApp.with(ScanFragment.this).load(rvScanDevice.getVisibility() == View.VISIBLE
                    ? getResources().getDrawable(R.drawable.ic_action_drop_down)
                    : getResources().getDrawable(R.drawable.ic_action_drop_up))
                    .into(ivScanDrop);
        });
        localDeviceAdapter = new BtDeviceManageAdapter<>(getContext(), new ArrayList<>());
        rvLocalDevice.setAdapter(localDeviceAdapter);
        rvLocalDevice.setLayoutManager(new LinearLayoutManager(getContext()));
        localDeviceAdapter.setOnItemClickListener(localListener);
        scanDeviceAdapter = new BtDeviceAdapter<>(getContext(), new ArrayList<>());
        rvScanDevice.setAdapter(scanDeviceAdapter);
        rvScanDevice.setLayoutManager(new LinearLayoutManager(getContext()));
        scanDeviceAdapter.setOnItemClickListener(scanListener);
        ivSelectAll.setOnClickListener(v -> localDeviceAdapter.selectAll());
        ivDelete.setOnClickListener(v -> {
            List<BtDevice> selectList = localDeviceAdapter.getSelectList();
            if (selectList.size() != 0) {
                new MaterialDialog.Builder(getContext())
                        .title("确认删除？")
                        .positiveText("确认")
                        .onPositive((dialog, which) -> {
                            mListener.showWaitDialog("删除中，请稍后。。。");
                            presenter.deleteBtDeviceList(selectList);
                        })
                        .negativeText("取消")
                        .show();
            }
        });
//        PlusItemSlideCallback callback = new PlusItemSlideCallback();
//        WItemTouchHelperPlus extension = new WItemTouchHelperPlus(callback);
//        extension.attachToRecyclerView(rvLocalDevice);
    }

    @Override
    public void loadBtDeviceCallback(List<BtDevice> btDeviceList) {
        if (btDeviceList != null && !btDeviceList.isEmpty()) {
            llLocalDevice.setVisibility(View.VISIBLE);
            rvLocalDevice.setVisibility(View.VISIBLE);
            localDeviceAdapter.setDataList(btDeviceList);
            localDeviceAdapter.notifyDataSetChanged();
        } else {
            localDeviceAdapter.setDataList(new ArrayList<>());
            localDeviceAdapter.notifyDataSetChanged();
            llLocalDevice.setVisibility(View.GONE);
            rvLocalDevice.setVisibility(View.GONE);
            btnScan.performClick();
            ivBack.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void deleteBtDeviceListCallback(boolean result) {
        mListener.dismissWaitDialog();
        if (result) {
            presenter.loadBtDevice(ConfigManager.getInstance().getConfig().getUsername());
            ToastManager.toast(getContext(), "删除成功", ToastManager.SUCCESS);
        } else {
            ToastManager.toast(getContext(), "删除失败", ToastManager.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        switch (event.getType()) {
            case BluetoothEvent.ON_STATE_ON:
                if (btnScan.getText().equals("扫描")) {
                    btnScan.performClick();
                }
                break;
            case BluetoothEvent.ON_STATE_TURNING_ON:
                ToastManager.toast(getContext(), "蓝牙开启中", ToastManager.INFO);
                break;
            case BluetoothEvent.ON_STATE_TURNING_OFF:
                btnScan.setText("扫描");
                if (countDownDisposable != null) {
                    countDownDisposable.dispose();
                }
                break;
            default:
                break;
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
        EventBus.getDefault().unregister(this);
        presenter.doDestroy();
        BtLockCommander.getInstance().stopScan(callback);
    }

    private void onScanSuccess() {
        btnScan.setText("正在扫描");
        llScanDevice.setVisibility(View.VISIBLE);
        rvScanDevice.setVisibility(View.VISIBLE);
        scanDeviceAdapter.setDataList(new ArrayList<>());
        scanDeviceAdapter.notifyDataSetChanged();
        scanCountDown(SCAN_TIME);
    }

    private void scanCountDown(int count) {
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
                        btnScan.setText("正在扫描（ " + num + "S ）");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("asd", "倒计时出错");
                    }

                    @Override
                    public void onComplete() {
                        if (btnScan!=null){
                            btnScan.setText("扫描");
                        }
                        BtLockCommander.getInstance().stopScan(callback);
                    }
                });
    }

    private void stopScan() {
        if (btnScan.getText().toString().contains("正在扫描")) {
            btnScan.performClick();
        }
    }

    private BluetoothAdapter.LeScanCallback callback = (device, rssi, scanRecord) -> {
        if (TextUtils.isEmpty(device.getName())) {
            return;
        }
        BtDevice btDevice = new BtDevice.Builder()
                .name(device.getName())
                .mac(device.getAddress())
                .build();
        if (localDeviceAdapter.contains(btDevice)) {
            return;
        }
        if (!scanDeviceAdapter.contains(btDevice)) {
            scanDeviceAdapter.addData(btDevice);
            scanDeviceAdapter.notifyDataSetChanged();
        }
    };

    private BtDeviceManageAdapter.OnItemClickListener localListener = new BtDeviceManageAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (BtLockCommander.getInstance().isBluetoothOpen()) {
                stopScan();
                mListener.enterAnotherFragment(ConnectFragment.newInstance(localDeviceAdapter.getData(position)));
            } else {
                BtLockCommander.getInstance().openBluetooth();
                ToastManager.toast(getContext(), "请先打开蓝牙", ToastManager.INFO);
            }
        }
    };

    private BtDeviceAdapter.OnItemClickListener scanListener = (view, position) -> {
        if (BtLockCommander.getInstance().isBluetoothOpen()) {
            stopScan();
            mListener.enterAnotherFragment(ConnectFragment.newInstance(scanDeviceAdapter.getData(position)));
        } else {
            BtLockCommander.getInstance().openBluetooth();
            ToastManager.toast(getContext(), "请先打开蓝牙", ToastManager.INFO);
        }
    };

}
