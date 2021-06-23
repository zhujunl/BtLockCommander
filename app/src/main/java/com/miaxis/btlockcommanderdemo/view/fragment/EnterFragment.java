package com.miaxis.btlockcommanderdemo.view.fragment;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;

import androidx.fragment.app.Fragment;
import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnterFragment extends BaseFragment {

    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_device_mac)
    TextView tvDeviceMac;
    @BindView(R.id.ll_profile)
    LinearLayout llProfile;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;

    private EnterPagerFragment.OnPagerItemClickListener listener;
    private BtDevice btDevice;
    private boolean left = true;
    private boolean right = true;

    public static EnterFragment newInstance(BtDevice btDevice, EnterPagerFragment.OnPagerItemClickListener listener) {
        EnterFragment enterFragment = new EnterFragment();
        enterFragment.setBtDevice(btDevice);
        enterFragment.setListener(listener);
        return enterFragment;
    }

    public EnterFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_enter;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        tvDeviceName.setText("设备名称：" + btDevice.getName());
        tvDeviceMac.setText("设备地址：" + btDevice.getMac());
        tvSerialNumber.setText("锁序列号：" + btDevice.getSerialNumber());
        ivLeft.setVisibility(left ? View.VISIBLE : View.INVISIBLE);
        ivRight.setVisibility(right ? View.VISIBLE : View.INVISIBLE);
        profileImage.setOnClickListener(v -> listener.onPagerItemClick(btDevice));
    }

    public void setListener(EnterPagerFragment.OnPagerItemClickListener listener) {
        this.listener = listener;
    }

    public void setBtDevice(BtDevice btDevice) {
        this.btDevice = btDevice;
    }

    public void setArrow(boolean left, boolean right) {
        this.left = left;
        this.right = right;
    }
}
