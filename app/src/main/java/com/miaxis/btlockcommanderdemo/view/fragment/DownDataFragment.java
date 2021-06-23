package com.miaxis.btlockcommanderdemo.view.fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.btlockcommander.entity.BaseLockPacket;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.DownDataAdapter;
import com.miaxis.btlockcommanderdemo.contract.DownDataContract;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.model.entity.DownDataDto;
import com.miaxis.btlockcommanderdemo.presenter.DownDataPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class DownDataFragment extends BaseFragment implements DownDataContract.View {

    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.rv_down_data)
    RecyclerView rvDownData;

    private DownDataContract.Presenter presenter;
    private PersonPagerFragment.OnPersonPagerItemListener listener;
    private DownDataAdapter<DownDataDto> downDataAdapter;
    private Handler handler;

    private boolean isVisibleToUser;
    private BtDevice btDevice;
    private int cursor = 0;

    private String contactSignal = "";
    private int waitTimeout = 0;
    private boolean synchronizedMode = false;

    public static DownDataFragment newInstance(BtDevice btDevice, PersonPagerFragment.OnPersonPagerItemListener listener) {
        DownDataFragment fragment = new DownDataFragment();
        fragment.setBtDevice(btDevice);
        fragment.setListener(listener);
        return fragment;
    }

    public DownDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_down_data;
    }

    @Override
    protected void initData() {
        presenter = new DownDataPresenter(this, this);
        handler = new Handler(Looper.getMainLooper());
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        downDataAdapter = new DownDataAdapter<>(getContext(), new ArrayList<>());
        rvDownData.setAdapter(downDataAdapter);
        rvDownData.setLayoutManager(new LinearLayoutManager(getContext()));
        //        srlDownData.setOnRefreshListener(() -> {
        //            srlDownData.setRefreshing(false);
        //            presenter.getDownDataList(btDevice.getSerialNumber());
        //        });
        //        tvSynchronized.setOnClickListener(v -> {
        //            if (!downDataAdapter.getDataList().isEmpty()) {
        //                listener.showWaitDialog("第" + cursor + "条指令开始同步", true, 10);
        //                downDataSynchronized();
        //            }
        //        });
        if (isVisibleToUser && downDataAdapter.getDataList().isEmpty()) {
            contactSignal = "";
            presenter.getDownDataList(btDevice.getSerialNumber());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        switch (event.getType()) {
            case BluetoothEvent.ON_DIS_CONNECT:
                ToastManager.toast(getContext(), "连接已断开", ToastManager.INFO);
                listener.close();
                break;
            case BluetoothEvent.ON_RESPONSE_WRITE_SUCCESS:
                //                ResponseWriteProgress fileWriteProgress = (ResponseWriteProgress) event.getData();
                //                listener.showWaitDialog("当前进度：" + fileWriteProgress.getCurrent() + " / " + fileWriteProgress.getTotal() + "\n" + "传输中", true, 5);
                //                if (fileWriteProgress.getCurrent() == fileWriteProgress.getTotal()) {
                //                    ToastManager.toast(getContext(), "指令写入成功", ToastManager.SUCCESS);
                //                }
                break;
            case BluetoothEvent.ON_RESPONSE_WRITE_FAILED:
                listener.onSynchronizedMode(false);
                listener.dismissWaitDialog();
                ToastManager.toast(getContext(), "同步指令传输失败", ToastManager.ERROR);
                break;
            case BluetoothEvent.ON_RESPONSE_WRITE_CONTINUE:
                break;
            case BluetoothEvent.ON_NOTIFY_LOCK_VERSION:
            case BluetoothEvent.ON_NOTIFY_BIND_PERSON_ID:
            case BluetoothEvent.ON_OPEN_LOG_COUNT_OF_DAY:
            case BluetoothEvent.ON_OPEN_LOG_RESPONSE:
            case BluetoothEvent.ON_NOTIFY_BASE_STATION_DATA:
                if (synchronizedMode) {
                    BaseLockPacket baseLockPacket = (BaseLockPacket) event.getData();
                    handlerUpData(baseLockPacket.getCode(), baseLockPacket.getData());
                }
                break;
            case BluetoothEvent.ON_NOTIFY_BIND_PERSON_RESULT:
            case BluetoothEvent.ON_NOTIFY_UPDATE_PERSON_RESULT:
            case BluetoothEvent.ON_BT_LOCK_NOTIFY_DATA:
                BaseLockPacket baseLockPacket = (BaseLockPacket) event.getData();
                handlerUpData(baseLockPacket.getCode(), baseLockPacket.getData());
                break;
            default:
                break;
        }
    }

    @Override
    public void getDownDataListCallback(List<DownDataDto> downDataDtoList, String message) {
        Log.e("测试","getDownDataListCallback:         downDataDtoList:" + (downDataDtoList == null ? null : downDataDtoList.size()) + "      message:" + message);
        if (downDataDtoList != null) {
            cursor = 0;
            String text = "当前待同步指令共 " + downDataDtoList.size() + " 条";
            tvStatus.setText(text);
            downDataAdapter.appendDataList(downDataDtoList);
            downDataAdapter.notifyDataSetChanged();
            times_error = 0;
        }
        handler.post(passThroughRunnable);
    }

    @Override
    public void downDataSynchronizedCallback(boolean result, DownDataDto downDataDto, String message) {
        Log.e("测试","downDataSynchronizedCallback:         result:" + result + "    downDataDto:" + downDataDto + "      message:" + message);
        if (result) {
            times_error = 0;
            downDataAdapter.removeData(0);
            if (downDataDto.isNoNeedWait()) {
                if (downDataAdapter.getDataList().isEmpty()) {
                    ToastManager.toast(getContext(), "第" + cursor + "条指令写入完成", ToastManager.SUCCESS);
                    listener.onSynchronizedMode(false);
                    listener.dismissWaitDialog();
                } else {
                    listener.showWaitDialog("第" + cursor + "条指令写入完成", true, 15);
                }
            } else {
                listener.showWaitDialog("第" + cursor + "条指令写入完成，正在等待锁端响应", true, 15);
            }
        } else {
            ToastManager.toast(getContext(), message, ToastManager.ERROR);
            if (times_error >= 3) {
                listener.onSynchronizedMode(false);
                listener.dismissWaitDialog();
                listener.close();
            }
            //            listener.onSynchronizedMode(false);
            //            listener.dismissWaitDialog();
            //            ToastManager.toast(getContext(), message, ToastManager.ERROR);
            //            listener.close();
        }
    }

    @Override
    public void sendUpDataCallback(boolean result, String command, DownDataDto downDataDto, String message) {
        Log.e("测试","sendUpDataCallback:   command:"+command+"   result:"+result + "    downDataDto:" + downDataDto + "      message:" + message);
        if (command == null) {
            command = "";
        }
        if (contactSignal == null) {
            contactSignal = "";
        }
        if (result) {
            if (TextUtils.equals(command.toLowerCase(), contactSignal.toLowerCase())) {
                contactSignal = "";
                listener.showWaitDialog("第" + cursor + "条指令同步完成", true, 15);
            } else {
                listener.showWaitDialog("逆向指令发送成功", true, 15);
            }
            if (downDataDto != null) {
                downDataAdapter.addData(downDataDto);
            }
            String text = "当前待同步指令共 " + downDataAdapter.getDataList().size() + " 条";
            tvStatus.setText(text);
            if (downDataAdapter.getDataList().isEmpty()) {
                listener.onSynchronizedMode(false);
                listener.dismissWaitDialog();
            }
        } else {
            handler.removeCallbacks(passThroughRunnable);
            tvStatus.setText("蓝牙透传通道已关闭");
            listener.onSynchronizedMode(false);
            listener.dismissWaitDialog();
            downDataAdapter.setDataList(new ArrayList<>());
            downDataAdapter.notifyDataSetChanged();
            ToastManager.toast(getContext(), message, ToastManager.ERROR);
            new MaterialDialog.Builder(getContext())
                    .title("遇到错误")
                    .content("锁端数据同步至锁平台时遇到错误。\n错误原因：" + message)
                    .positiveText("确认")
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.doDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacks(passThroughRunnable);
        handler.removeCallbacks(updateRunnable);
        handler = null;
    }

    private void handlerUpData(byte command, @NonNull byte[] data) {
        if (command != (byte) 0x1D) {
            listener.showWaitDialog("收到锁端上行数据，正在将其上传至锁平台", true, 15);
            String commandStr = Integer.toHexString(command & 0xff);
            Log.e("测试","commandStr:"+commandStr);
            presenter.sendUpData(btDevice.getSerialNumber(), commandStr, new String(data));
        }
    }

    private Runnable passThroughRunnable = new Runnable() {
        @Override
        public void run() {
            if (downDataAdapter == null) {
                return;
            }
            List<DownDataDto> dataList = downDataAdapter.getDataList();
            if (dataList.isEmpty() && TextUtils.isEmpty(contactSignal)) {
                listener.onSynchronizedMode(false);
                //                if (synchronizedMode) {
                //                    listener.dismissWaitDialog();
                //                }
                synchronizedMode = false;
                handler.postDelayed(updateRunnable, 2000);
                return;
            }
            if (TextUtils.isEmpty(contactSignal)) {
                if (!dataList.isEmpty()) {
                    DownDataDto data = downDataAdapter.getData(0);
                    if (data != null) {
                        if (!data.isNoNeedWait()) {
                            contactSignal = data.getReplyCode();
                        } else {
                            contactSignal = "";
                        }
                        listener.onSynchronizedMode(true);
                        synchronizedMode = true;
                        listener.showWaitDialog("第" + (++cursor) + "条指令开始同步", true, 15);
                        presenter.downDataSynchronized(data);
                    }
                }
                waitTimeout = 0;
                handler.postDelayed(passThroughRunnable, 1000);
            } else {
                waitTimeout++;
                if (waitTimeout >= 14) {
                    ToastManager.toast(getContext(), "等待锁端响应同步指令超时", ToastManager.ERROR);
                    if (cursor_error != cursor) {
                        cursor--;
                        cursor_error = cursor;
                    }
                    times_error++;
                    if (times_error >= 3) {
                        listener.onSynchronizedMode(false);
                        listener.dismissWaitDialog();
                        listener.close();
                        return;
                    }
                } else {
                    // handler.postDelayed(passThroughRunnable, 1000);
                }
                handler.postDelayed(passThroughRunnable, 1000);
            }
        }
    };

    private int times_error = 0;
    private int cursor_error = -1;
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            presenter.getDownDataList(btDevice.getSerialNumber());
        }
    };

    public void setListener(PersonPagerFragment.OnPersonPagerItemListener listener) {
        this.listener = listener;
    }

    public void setBtDevice(BtDevice btDevice) {
        this.btDevice = btDevice;
    }

}
