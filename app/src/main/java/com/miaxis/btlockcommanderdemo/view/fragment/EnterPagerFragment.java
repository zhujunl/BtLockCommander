package com.miaxis.btlockcommanderdemo.view.fragment;


import android.content.Context;
import android.widget.ImageView;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.MyFragmentPagerAdapter;
import com.miaxis.btlockcommanderdemo.contract.EnterPagerContract;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.presenter.EnterPagerPresenter;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnterPagerFragment extends BaseFragment implements EnterPagerContract.View {

    @BindView(R.id.vp_enter)
    ViewPager vpEnter;
    @BindView(R.id.iv_scan)
    ImageView ivScan;

    private EnterPagerContract.Presenter presenter;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private OnFragmentInteractionListener mListener;

    public static EnterPagerFragment newInstance() {
        return new EnterPagerFragment();
    }

    public EnterPagerFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_enter_pager;
    }

    @Override
    protected void initData() {
        presenter = new EnterPagerPresenter(this, this);
    }

    @Override
    protected void initView() {
        ivScan.setOnClickListener(v -> mListener.enterAnotherFragment(ScanFragment.newInstance()));
        presenter.loadBtDevice(ConfigManager.getInstance().getConfig().getUsername());
    }

    @Override
    public void loadBtDeviceCallback(List<BtDevice> btDeviceList) {
        if (btDeviceList != null && !btDeviceList.isEmpty()) {
            List<Fragment> fragmentList = new ArrayList<>();
            for (BtDevice btDevice : btDeviceList) {
                fragmentList.add(EnterFragment.newInstance(btDevice, onPagerItemClickListener));
            }
            if (fragmentList.size() == 1) {
                ((EnterFragment) fragmentList.get(0)).setArrow(false, false);
            } else {
                ((EnterFragment) fragmentList.get(0)).setArrow(false, true);
                ((EnterFragment) fragmentList.get(fragmentList.size() - 1)).setArrow(true, false);
            }
            myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
            vpEnter.setAdapter(myFragmentPagerAdapter);
        } else {
            mListener.enterAnotherFragment(ScanFragment.newInstance());
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
    }

    public interface OnPagerItemClickListener {
        void onPagerItemClick(BtDevice btDevice);
    }

    private OnPagerItemClickListener onPagerItemClickListener = btDevice -> {
        mListener.enterAnotherFragment(ConnectFragment.newInstance(btDevice));
    };

}
