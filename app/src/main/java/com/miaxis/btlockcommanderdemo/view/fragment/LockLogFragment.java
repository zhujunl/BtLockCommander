package com.miaxis.btlockcommanderdemo.view.fragment;

import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.miaxis.btlockcommander.entity.OpenLog;
import com.miaxis.btlockcommander.entity.OpenLogCount;
import com.miaxis.btlockcommander.entity.OpenLogResponse;
import com.miaxis.btlockcommander.util.BleUtil;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.LockLogAdapter;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.model.entity.PersonDto;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockLogFragment extends BaseFragment {

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_last_page)
    TextView tvLastPage;
    @BindView(R.id.tv_next_page)
    TextView tvNextPage;
    @BindView(R.id.rv_lock_log)
    RecyclerView rvLockLog;
    @BindView(R.id.srl_lock_log)
    ScrollView srlLockLog;
    @BindView(R.id.tv_page_current_count)
    TextView tvPageCurrentCount;
    @BindView(R.id.tv_page_total_count)
    TextView tvPageTotalCount;

    private boolean isVisibleToUser;
    private LockLogAdapter lockLogAdapter;
    private DatePickerDialog datePickerDialog;
    private DatePicker datePicker;
    private BtDevice btDevice;
    private PersonPagerFragment.OnPersonPagerItemListener listener;

    private int year;
    private int month;
    private int day;
    private String date = ValueUtil.simpleDateFormat.format(new Date());
    private int logCountOfDay = 0;
    private int currentPage = 1;
    private int totalPage = 0;

    public static LockLogFragment newInstance(BtDevice btDevice, PersonPagerFragment.OnPersonPagerItemListener listener) {
        LockLogFragment lockLogFragment = new LockLogFragment();
        lockLogFragment.setBtDevice(btDevice);
        lockLogFragment.setListener(listener);
        return lockLogFragment;
    }

    public LockLogFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && lockLogAdapter != null && lockLogAdapter.getDataList().isEmpty()) {
            if (BluetoothManager.getInstance().writeQueryLogCountOfDayCommand(date)) {
                listener.showWaitDialog("正在查询日志数目", true, 8);
            } else {
                ToastManager.toast(getContext(), "查询日志失败", ToastManager.ERROR);
            }
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_lock_log;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        lockLogAdapter = new LockLogAdapter(getContext(), new ArrayList<>());
        rvLockLog.setAdapter(lockLogAdapter);
        rvLockLog.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true));
    }

    @Override
    protected void initView() {
        initTimePicker();
        tvDate.setText(ValueUtil.dateFormat.format(new Date()));
        tvDate.setOnClickListener(v -> {
            datePicker.updateDate(year, month, day);
            datePickerDialog.show();
        });
        tvLastPage.setOnClickListener(v -> {
            if (logCountOfDay == 0) {
                return;
            }
            if (currentPage > 1) {
                lockLogAdapter.setDataList(new ArrayList<>());
                lockLogAdapter.notifyDataSetChanged();
                currentPage--;
                tvPageCurrentCount.setText(currentPage + "");
                if (BluetoothManager.getInstance().writeQueryLogCommand(date, currentPage)) {
                    listener.showWaitDialog("正在查询日志", true, 8);
                } else {
                    ToastManager.toast(getContext(), "查询日志失败", ToastManager.ERROR);
                }
            }
        });
        tvNextPage.setOnClickListener(v -> {
            if (logCountOfDay == 0) {
                return;
            }
            if (currentPage < totalPage) {
                lockLogAdapter.setDataList(new ArrayList<>());
                lockLogAdapter.notifyDataSetChanged();
                currentPage++;
                tvPageCurrentCount.setText(currentPage + "");
                if (BluetoothManager.getInstance().writeQueryLogCommand(date, currentPage)) {
                    listener.showWaitDialog("正在查询日志", true, 8);
                } else {
                    ToastManager.toast(getContext(), "查询日志失败", ToastManager.ERROR);
                }
            }
        });
        //        if (lockLogAdapter.getDataList().isEmpty()) {
        //            listener.showWaitDialog("正在查询日志数目", true, 8);
        //            BluetoothManager.getInstance().writeQueryLogCountOfDayCommand(date);
        //        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        Log.e("LockLogFragment", "" + event);
        switch (event.getType()) {
            case BluetoothEvent.ON_OPEN_LOG_COUNT_OF_DAY:
                OpenLogCount openLogCount = (OpenLogCount) event.getData();
                if (openLogCount.getCount() == 0) {
                    ToastManager.toast(getContext(), "锁上无该天日志", ToastManager.INFO);
                }
                totalPage = openLogCount.getCount() % BleUtil.LOCK_LOG_PAGE_SIZE == 0
                        ? openLogCount.getCount() / BleUtil.LOCK_LOG_PAGE_SIZE
                        : openLogCount.getCount() / BleUtil.LOCK_LOG_PAGE_SIZE + 1;
                tvPageTotalCount.setText("/ " + totalPage);
                this.logCountOfDay = openLogCount.getCount();
                if (logCountOfDay > 0) {
                    if (BluetoothManager.getInstance().writeQueryLogCommand(date, 1)) {
                        listener.showWaitDialog("正在查询日志", true, 8);
                    } else {
                        ToastManager.toast(getContext(), "查询日志失败", ToastManager.ERROR);
                    }
                    currentPage = 1;
                    tvPageCurrentCount.setText(currentPage + "");
                }
                break;
            case BluetoothEvent.ON_OPEN_LOG_RESPONSE:
                OpenLogResponse openLogResponse = (OpenLogResponse) event.getData();
                //List<PersonDto> personDtoList = listener.getPersonDtoList();
                List<OpenLog> openLogList = openLogResponse.getOpenLogList();
                //if (personDtoList == null) {
                //   ToastManager.toast(getContext(), "未找到锁上人员信息", ToastManager.INFO);
                //} else {
                //   openLogList = combineLogName(personDtoList, openLogResponse);
                //}
                lockLogAdapter.setDataList(openLogList);
                lockLogAdapter.notifyDataSetChanged();
                if (openLogList != null && !openLogList.isEmpty()) {
                    //rvLockLog.smoothScrollToPosition(openLogList.size()-1);
                    rvLockLog.postDelayed(() -> rvLockLog.smoothScrollToPosition(openLogList.size() - 1), 200);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onSynchronizedMode(boolean mode) {
        try {
            if (mode) {
                if (EventBus.getDefault().isRegistered(LockLogFragment.this)) {
                    EventBus.getDefault().unregister(LockLogFragment.this);
                }
            } else {
                if (!EventBus.getDefault().isRegistered(LockLogFragment.this)) {
                    EventBus.getDefault().register(LockLogFragment.this);
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

    private void initTimePicker() {
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getActivity(), R.style.MyDatePickerDialogTheme, (view, year, month, dayOfMonth) -> {
            this.year = year;
            this.month = month;
            this.day = dayOfMonth;
            String mDate = year + "-" + (month + 1 < 10 ? "0" : "") + (month + 1) + "-" + (dayOfMonth < 10 ? "0" : "") + dayOfMonth;
            tvDate.setText(mDate);
            date = mDate + " 23:59:59";
            listener.showWaitDialog("正在查询日志数目", true, 8);
            lockLogAdapter.setDataList(new ArrayList<>());
            lockLogAdapter.notifyDataSetChanged();
            BluetoothManager.getInstance().writeQueryLogCountOfDayCommand(date);
        }, year, month, day);
        datePicker = datePickerDialog.getDatePicker();
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, 2018);
        minDate.set(Calendar.MONTH, 0);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        datePicker.setMinDate(minDate.getTimeInMillis());
        datePicker.setMaxDate(today.getTimeInMillis());
    }

    private List<OpenLog> combineLogName(List<PersonDto> personDtoList, OpenLogResponse openLogResponse) {
        List<OpenLog> openLogList = openLogResponse.getOpenLogList();
        for (OpenLog openLog : openLogList) {
            for (PersonDto personDto : personDtoList) {
                if (TextUtils.equals(String.valueOf(personDto.getId()), openLog.getPersonId())) {
                    openLog.setName(personDto.getName());
                    break;
                }
            }
        }
        return openLogList;
    }
}
