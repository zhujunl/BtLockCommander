package com.miaxis.btlockcommanderdemo.view.dialog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.miaxis.btlockcommander.entity.BaseStation;
import com.miaxis.btlockcommanderdemo.R;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseStationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;
    @BindView(R.id.tv_mode)
    TextView tvMode;
    @BindView(R.id.tv_earfcn)
    TextView tvEarfcn;
    @BindView(R.id.tv_earfcn_offset)
    TextView tvEarfcnOffset;
    @BindView(R.id.tv_pci)
    TextView tvPci;
    @BindView(R.id.tv_cellid)
    TextView tvCellid;
    @BindView(R.id.tv_rsrp)
    TextView tvRsrp;
    @BindView(R.id.tv_rsrq)
    TextView tvRsrq;
    @BindView(R.id.tv_rssi)
    TextView tvRssi;
    @BindView(R.id.tv_band)
    TextView tvBand;
    @BindView(R.id.tv_tac)
    TextView tvTac;
    @BindView(R.id.tv_ecl)
    TextView tvEcl;
    @BindView(R.id.tv_tx_pwr)
    TextView tvTxPwr;
    @BindView(R.id.tv_snr)
    TextView tvSnr;

    private BaseStation baseStation;
    private OnRefreshClickListener listener;

    public static BaseStationDialogFragment newInstance(BaseStation baseStation, OnRefreshClickListener listener) {
        BaseStationDialogFragment baseStationDialogFragment = new BaseStationDialogFragment();
        baseStationDialogFragment.setBaseStation(baseStation);
        baseStationDialogFragment.setListener(listener);
        return baseStationDialogFragment;
    }

    public BaseStationDialogFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_base_station_dialog;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        tvMode.setText(baseStation.getMode());
        tvEarfcn.setText(baseStation.getEarfcn());
        tvEarfcnOffset.setText(baseStation.getEarfcnOffset());
        tvPci.setText(baseStation.getPci());
        tvCellid.setText(baseStation.getCellid());
        tvRsrp.setText(baseStation.getRsrp());
        tvRsrq.setText(baseStation.getRsrq());
        tvRssi.setText(baseStation.getRssi());
        tvSnr.setText(baseStation.getSnr());
        tvBand.setText(baseStation.getBand());
        tvTac.setText(baseStation.getTac());
        tvEcl.setText(baseStation.getEcl());
        tvTxPwr.setText(baseStation.getTxPwr());
        ivRefresh.setOnClickListener(v -> {
            dismiss();
            listener.onClick();
        });
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
        params.height = (int) (dm.heightPixels * 0.65);
        win.setAttributes(params);
    }

    public void setBaseStation(BaseStation baseStation) {
        this.baseStation = baseStation;
    }

    public void setListener(OnRefreshClickListener listener) {
        this.listener = listener;
    }

    public interface OnRefreshClickListener {
        void onClick();
    }

}
