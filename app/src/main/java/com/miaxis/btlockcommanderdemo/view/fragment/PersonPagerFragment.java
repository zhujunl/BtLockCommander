package com.miaxis.btlockcommanderdemo.view.fragment;


import com.google.android.material.tabs.TabLayout;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.MyFragmentPagerAdapter;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.model.entity.PersonDto;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonPagerFragment extends BaseFragment {

    @BindView(R.id.tl_person)
    TabLayout tlPerson;
    @BindView(R.id.vp_person)
    ViewPager vpPerson;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private BtDevice btDevice;
    private OnPersonPagerItemListener onPersonPagerItemListener;
    private DownDataFragment downDataFragment;
    private LockPersonFragment lockPersonFragment;
    private LockLogFragment lockLogFragment;

    public static PersonPagerFragment newInstance(BtDevice btDevice, OnPersonPagerItemListener onPersonPagerItemListener) {
        PersonPagerFragment personPagerFragment = new PersonPagerFragment();
        personPagerFragment.setBtDevice(btDevice);
        personPagerFragment.setOnPersonPagerItemListener(onPersonPagerItemListener);
        return personPagerFragment;
    }

    public PersonPagerFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_person_pager;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(downDataFragment = DownDataFragment.newInstance(btDevice, onPersonPagerItemListener));
        fragmentList.add(lockPersonFragment = LockPersonFragment.newInstance(btDevice, onPersonPagerItemListener));
        fragmentList.add(lockLogFragment = LockLogFragment.newInstance(btDevice, onPersonPagerItemListener));
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        vpPerson.setAdapter(myFragmentPagerAdapter);
        tlPerson.setupWithViewPager(vpPerson);
        TabLayout.Tab serverCommand = tlPerson.getTabAt(0);
        TabLayout.Tab lockLocalPerson = tlPerson.getTabAt(1);
        TabLayout.Tab lockLog = tlPerson.getTabAt(2);
        serverCommand.setText("锁平台指令");
        lockLocalPerson.setText("锁内人员");
        lockLog.setText("查询日志");
        vpPerson.setOffscreenPageLimit(3);
    }

    public void onSynchronizedMode(boolean mode) {
        if (lockPersonFragment != null) {
            lockPersonFragment.onSynchronizedMode(mode);
        }
        if (lockLogFragment != null) {
            lockLogFragment.onSynchronizedMode(mode);
        }
    }

    public void setBtDevice(BtDevice btDevice) {
        this.btDevice = btDevice;
    }

    public void setOnPersonPagerItemListener(OnPersonPagerItemListener onPersonPagerItemListener) {
        this.onPersonPagerItemListener = onPersonPagerItemListener;
    }

    interface OnPersonPagerItemListener {
        void showWaitDialog(String message, boolean count, int time);

        void dismissWaitDialog();

        void onSynchronizedMode(boolean mode);

        void close();

        void onPersonDtoDownload(List<PersonDto> personDtoList);

        List<PersonDto> getPersonDtoList();

        String getWaitDialogMessage();
    }

}
