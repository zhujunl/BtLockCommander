package com.miaxis.btlockcommanderdemo.view.fragment;


import com.miaxis.btlockcommander.entity.BindPersonId;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.LockPersonAdapter;
import com.miaxis.btlockcommanderdemo.contract.LockPersonContract;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.presenter.LockPersonPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockPersonFragment extends BaseFragment implements LockPersonContract.View {

    @BindView(R.id.rv_lock_person)
    RecyclerView rvLockPerson;
    @BindView(R.id.srl_lock_person)
    SwipeRefreshLayout srlLockPerson;

    private boolean isVisibleToUser;
    private LockPersonContract.Presenter presenter;
    private LockPersonAdapter<String> lockPersonAdapter;
    private PersonPagerFragment.OnPersonPagerItemListener listener;
    private BtDevice btDevice;
    private BindPersonId bindPersonId;

    public static LockPersonFragment newInstance(BtDevice btDevice, PersonPagerFragment.OnPersonPagerItemListener listener) {
        LockPersonFragment lockPersonFragment = new LockPersonFragment();
        lockPersonFragment.setBtDevice(btDevice);
        lockPersonFragment.setListener(listener);
        return lockPersonFragment;
    }

    public LockPersonFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && lockPersonAdapter != null && lockPersonAdapter.getDataList().isEmpty()) {
            if (BluetoothManager.getInstance().writeBindPersonId()) {
                listener.showWaitDialog("正在查询人员", true, 8);
            } else {
                ToastManager.toast(getContext(), "查询人员失败", ToastManager.ERROR);
            }
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_lock_person;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        presenter = new LockPersonPresenter(this, this);
    }

    @Override
    protected void initView() {
        lockPersonAdapter = new LockPersonAdapter<>(getContext(), new ArrayList<>());
        rvLockPerson.setAdapter(lockPersonAdapter);
        rvLockPerson.setLayoutManager(new LinearLayoutManager(getContext()));
        srlLockPerson.setOnRefreshListener(() -> {
            srlLockPerson.setRefreshing(false);
            lockPersonAdapter.setDataList(new ArrayList<>());
            lockPersonAdapter.notifyDataSetChanged();
            if (BluetoothManager.getInstance().writeBindPersonId()) {
                listener.showWaitDialog("正在查询人员", true, 8);
            } else {
                ToastManager.toast(getContext(), "查询人员失败", ToastManager.ERROR);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        switch (event.getType()) {
            case BluetoothEvent.ON_NOTIFY_BIND_PERSON_ID:
                bindPersonId = (BindPersonId) event.getData();
                if (bindPersonId.getCount() == 0) {
                    ToastManager.toast(getContext(), "锁上无人员", ToastManager.INFO);
                } else {
                    lockPersonAdapter.setDataList(bindPersonId.getPersonIdList());
                    lockPersonAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

//    @Override
//    public void downPersonListByIdListCallback(List<PersonDto> personDtoList) {
//        listener.dismissWaitDialog();
//        List<PersonDto> personDtoListCache;
//        if (personDtoList != null) {
//            personDtoListCache = personDtoList;
//        } else if (bindPersonId != null) {
//            personDtoListCache = new ArrayList<>();
//            for (String personId : bindPersonId.getPersonIdList()) {
//                personDtoListCache.add(new PersonDto(0L, personId, personId, ""));
//            }
//        } else {
//            ToastManager.toast(getContext(), "人员数据错误", ToastManager.INFO);
//            return;
//        }
//        personDtoLockPersonAdapter.setDataList(personDtoListCache);
//        personDtoLockPersonAdapter.notifyDataSetChanged();
//        listener.onPersonDtoDownload(personDtoListCache);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        presenter.doDestroy();
    }

    public void onSynchronizedMode(boolean mode) {
        try {
            if (mode) {
                if (EventBus.getDefault().isRegistered(LockPersonFragment.this)) {
                    EventBus.getDefault().unregister(LockPersonFragment.this);
                }
            } else {
                if (!EventBus.getDefault().isRegistered(LockPersonFragment.this)) {
                    EventBus.getDefault().register(LockPersonFragment.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBtDevice(BtDevice btDevice) {
        this.btDevice = btDevice;
    }

    public void setListener(PersonPagerFragment.OnPersonPagerItemListener listener) {
        this.listener = listener;
    }
}
