package com.miaxis.btlockcommanderdemo.view.fragment;


import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.btlockcommander.entity.BindPersonResult;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.ServerCommandAdapter;
import com.miaxis.btlockcommanderdemo.contract.ServerCommandContract;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.model.entity.NbPerson;
import com.miaxis.btlockcommanderdemo.model.entity.ResponseWriteProgress;
import com.miaxis.btlockcommanderdemo.presenter.ServerCommandPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServerCommandFragment extends BaseFragment implements ServerCommandContract.View {

    @BindView(R.id.rv_server_command)
    RecyclerView rvServerCommand;
    @BindView(R.id.srl_server_command)
    SwipeRefreshLayout srlServerCommand;

//    private MaterialDialog retryDialog;
    private MaterialDialog resultDialog;
    private boolean isVisibleToUser;
    private PersonPagerFragment.OnPersonPagerItemListener listener;
    private ServerCommandContract.Presenter presenter;
    private ServerCommandAdapter<NbPerson> serverCommandAdapter;

    private BtDevice btDevice;

    public static ServerCommandFragment newInstance(BtDevice btDevice, PersonPagerFragment.OnPersonPagerItemListener listener) {
        ServerCommandFragment serverCommandFragment = new ServerCommandFragment();
        serverCommandFragment.setBtDevice(btDevice);
        serverCommandFragment.setListener(listener);
        return serverCommandFragment;
    }

    public ServerCommandFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_server_command;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        presenter = new ServerCommandPresenter(this, this);
    }

    @Override
    protected void initView() {
        initDialog();
        serverCommandAdapter = new ServerCommandAdapter<>(getContext(), new ArrayList<>());
        rvServerCommand.setAdapter(serverCommandAdapter);
        rvServerCommand.setLayoutManager(new LinearLayoutManager(getContext()));
        serverCommandAdapter.setOnItemClickListener((view, position) -> {
            listener.showWaitDialog("正在同步", true, 8);
            NbPerson nbPerson = serverCommandAdapter.getData(position);
            presenter.bindNbPerson(nbPerson);
        });
        srlServerCommand.setOnRefreshListener(() -> {
            srlServerCommand.setRefreshing(false);
            presenter.downNbCmdDto(btDevice.getSerialNumber());
        });
        if (isVisibleToUser && serverCommandAdapter.getDataList().isEmpty()) {
            presenter.downNbCmdDto(btDevice.getSerialNumber());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        switch (event.getType()) {
            case BluetoothEvent.ON_RESPONSE_WRITE_SUCCESS:
                ResponseWriteProgress fileWriteProgress = (ResponseWriteProgress) event.getData();
                listener.showWaitDialog("当前进度：" + fileWriteProgress.getCurrent() + " / " + fileWriteProgress.getTotal() + "\n" + "传输中", true, 8);
                if (fileWriteProgress.getCurrent() == fileWriteProgress.getTotal()) {
                    ToastManager.toast(getContext(), "绑定人员指令写入成功，请等待锁锁完成绑定", ToastManager.SUCCESS);
                }
                break;
            case BluetoothEvent.ON_RESPONSE_WRITE_FAILED:
                listener.dismissWaitDialog();
                ToastManager.toast(getContext(), "绑定人员指令传输失败", ToastManager.ERROR);
                break;
            case BluetoothEvent.ON_RESPONSE_WRITE_CONTINUE:
//                String text = listener.getWaitDialogMessage();
//                text = text.substring(0, text.indexOf("\n") + 1) + "传输中";
//                listener.showWaitDialog(text, false, 0);
                break;
            case BluetoothEvent.ON_BT_LOCK_NOTIFY_DATA:
                // 未在解析内容之内的数据不做透传
//                PassThroughService.startActionPassThrough(BtLockCommanderApp.getInstance().getApplicationContext(),
//                        btDevice.getSerialNumber(), (byte[]) event.getData());
                break;
            case BluetoothEvent.ON_NOTIFY_BIND_PERSON_RESULT:
                listener.showWaitDialog("蓝牙同步完成，正在同步平台", true, 8);
                BindPersonResult bindPersonResult = (BindPersonResult) event.getData();
                if (bindPersonResult.getResult() == 0) {
                    presenter.bindPersonResultPassThrough(btDevice.getSerialNumber(), bindPersonResult);
                } else {
                    bindPersonResultPassThroughCallback(bindPersonResult.getResult(), false, bindPersonResult.getPersonId());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void downNbCmdDtoCallback(boolean result, String message) {
        if (result) {
            presenter.loadNbCmdDto(btDevice.getSerialNumber());
        } else {
            ToastManager.toast(getContext(), message, ToastManager.INFO);
//            retryDialog.show();
        }
    }

    @Override
    public void loadNbCmdDtoCallback(List<NbPerson> nbPersonList) {
        serverCommandAdapter.setDataList(nbPersonList);
        serverCommandAdapter.notifyDataSetChanged();
    }

    @Override
    public void bindNbPersonCallback(boolean result) {
        if (result) {
            ToastManager.toast(getContext(), "蓝牙写入命令成功", ToastManager.SUCCESS);
        } else {
            listener.dismissWaitDialog();
            ToastManager.toast(getContext(), "写入失败，请检查蓝牙状态", ToastManager.ERROR);
        }
    }

    @Override
    public void bindPersonResultPassThroughCallback(int lockResult, boolean platformResult, String personId) {
        listener.dismissWaitDialog();
        if (lockResult == 0 && platformResult) {
            presenter.loadNbCmdDto(btDevice.getSerialNumber());
        }
        switch (lockResult) {
            case 0://成功
                showResultDialog("锁：同步成功\n平台：同步" + (platformResult ? "成功-" + presenter.getNameByPersonId(personId) : "失败-联网错误"));
                break;
            case 1://通讯出错
                showResultDialog("锁：同步失败-通讯出错\n平台：同步" + (platformResult ? "成功-" + presenter.getNameByPersonId(personId) : "失败-联网错误"));
                break;
            case 2://设置数据出错
                showResultDialog("锁：同步失败-数据出错\n平台：同步" + (platformResult ? "成功-" + presenter.getNameByPersonId(personId) : "失败-联网错误"));
                break;
            case 3://绑定错误
                showResultDialog("锁：同步失败-绑定错误\n平台：同步" + (platformResult ? "成功-" + presenter.getNameByPersonId(personId) : "失败-联网错误"));
                break;
            default://其他，失败
                showResultDialog("锁：同步成功-其他错误\n平台：同步" + (platformResult ? "成功-" + presenter.getNameByPersonId(personId) : "失败-联网错误"));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        presenter.doDestroy();
    }

    private void showResultDialog(String message) {
        if (resultDialog.isShowing()) {
            resultDialog.dismiss();
        }
        resultDialog.setContent(message);
        resultDialog.show();
    }

    private void initDialog() {
        resultDialog = new MaterialDialog.Builder(getContext())
                .title("指令执行结果")
                .positiveText("确定")
                .build();
//        retryDialog = new MaterialDialog.Builder(getContext())
//                .title("锁平台指令同步失败")
//                .content("是否重试？")
//                .positiveText("重试")
//                .cancelable(false)
//                .onPositive((dialog, which) -> presenter.downNbCmdDto(btDevice.getSerialNumber()))
//                .negativeText("断开连接")
//                .onNegative((dialog, which) -> listener.close())
//                .build();
    }

    public void setBtDevice(BtDevice btDevice) {
        this.btDevice = btDevice;
    }

    public void setListener(PersonPagerFragment.OnPersonPagerItemListener listener) {
        this.listener = listener;
    }

}
