package com.miaxis.btlockcommanderdemo.view.dialog;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.miaxis.btlockcommander.entity.LockVersion;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockVersionDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_lock_hardware_version)
    TextView tvLockHardwareVersion;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.tv_finger_hardware_version)
    TextView tvFingerHardwareVersion;
    @BindView(R.id.tv_finger_firmware_version)
    TextView tvFingerFirmwareVersion;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_imei)
    TextView tvImei;
    @BindView(R.id.tv_rssi)
    TextView tvRssi;
    @BindView(R.id.tv_nb_version)
    TextView tvNbVersion;
    @BindView(R.id.tv_base_station)
    TextView tvBaseStation;
    @BindView(R.id.tv_imsi)
    TextView tvImsi;

    private LockVersion lockVersion;
    private OnBaseStationClickListener listener;

    public static LockVersionDialogFragment newInstance(@NonNull LockVersion lockVersion, @NonNull OnBaseStationClickListener listener) {
        LockVersionDialogFragment lockVersionDialogFragment = new LockVersionDialogFragment();
        lockVersionDialogFragment.setLockVersion(lockVersion);
        lockVersionDialogFragment.setListener(listener);
        return lockVersionDialogFragment;
    }

    public LockVersionDialogFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_lock_version_dialog;
    }

    @Override
    protected void initData() {
        tvLockHardwareVersion.setText(lockVersion.getLockHardwareVersion());
        tvLockFirmwareVersion.setText(lockVersion.getLockFirmwareVersion());
        tvFingerHardwareVersion.setText(lockVersion.getFingerHardwareVersion());
        tvFingerFirmwareVersion.setText(lockVersion.getFingerFirmwareVersion());
        tvSerialNumber.setText(lockVersion.getNbSerialNumber());
        tvImei.setText(lockVersion.getSimICCID());
        tvRssi.setText(ValueUtil.getRssiString(lockVersion.getNbSignalIntensity()));
        tvNbVersion.setText(lockVersion.getNbVersion());
        tvImsi.setText(TextUtils.isEmpty(lockVersion.getSimIMSI()) ? "" : lockVersion.getSimIMSI());
        tvBaseStation.setOnClickListener(v -> {
            dismiss();
            listener.onClick();
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    public void setLockVersion(LockVersion lockVersion) {
        this.lockVersion = lockVersion;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setListener(OnBaseStationClickListener listener) {
        this.listener = listener;
    }

    public interface OnBaseStationClickListener {
        void onClick();
    }

}
