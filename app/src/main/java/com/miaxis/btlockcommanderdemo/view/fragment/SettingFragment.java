package com.miaxis.btlockcommanderdemo.view.fragment;


import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.contract.SettingContract;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.presenter.SettingPresenter;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements SettingContract.View {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_save_config)
    ImageView ivSaveConfig;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.et_base_url)
    EditText etBaseUrl;
    @BindView(R.id.et_host_certificate)
    EditText etHostCertificate;
    @BindView(R.id.ll_host_certificate)
    LinearLayout llHostCertificate;

    private Config config;
    private OnFragmentInteractionListener mListener;
    private SettingContract.Presenter presenter;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initData() {
        presenter = new SettingPresenter(this, this);
        config = ConfigManager.getInstance().getConfig();
    }

    @Override
    protected void initView() {
        tvVersion.setText(ValueUtil.getCurVersion(getContext()) + (ValueUtil.APP_VERSION ? "_CS" : "_IS"));
        etBaseUrl.setText(config.getBaseUrl());
        etHostCertificate.setText(config.getHostCertificate());
        ivBack.setOnClickListener(v -> mListener.backToStack(null));
        ivSaveConfig.setOnClickListener(v -> saveConfig());
        if (ValueUtil.APP_VERSION) {
            llHostCertificate.setVisibility(View.GONE);
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

    private void saveConfig() {
        if (!ValueUtil.isHttpFormat(etBaseUrl.getText().toString())) {
            ToastManager.toast(getContext(), "锁平台地址校验失败，请输入\"http://host:port/\"格式", ToastManager.INFO);
            return;
        }
        if (etHostCertificate.getText().toString().length() != 32) {
            ToastManager.toast(getContext(), "公司证书校验失败，请输入完整数据", ToastManager.INFO);
            return;
        }
        config.setBaseUrl(etBaseUrl.getText().toString());
        config.setHostCertificate(etHostCertificate.getText().toString());
        ConfigManager.getInstance().saveConfig(config, aBoolean -> {
            if (aBoolean) {
                if (ValueUtil.APP_VERSION) {
                    ToastManager.toast(getContext(), "设置保存成功，请重新登录", ToastManager.SUCCESS);
                    mListener.backToStack(LoginFragment.class);
                } else {
                    ToastManager.toast(getContext(), "设置保存成功", ToastManager.SUCCESS);
                }
            } else {
                ToastManager.toast(getContext(), "设置保存失败", ToastManager.ERROR);
            }
        });
    }

}
