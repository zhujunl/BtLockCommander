package com.miaxis.btlockcommanderdemo.view.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.contract.LoginContract;
import com.miaxis.btlockcommanderdemo.manager.ConfigManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.model.entity.Config;
import com.miaxis.btlockcommanderdemo.presenter.LoginPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements LoginContract.View {

    @BindView(R.id.iv_config)
    ImageView ivConfig;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.fl_username)
    FrameLayout flUsername;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.fl_password)
    FrameLayout flPassword;
    @BindView(R.id.cb_remember)
    CheckBox cbRemember;
    @BindView(R.id.ll_option)
    LinearLayout llOption;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.cv_device_info)
    CardView cvDeviceInfo;

    private OnFragmentInteractionListener mListener;
    private LoginContract.Presenter presenter;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initData() {
        presenter = new LoginPresenter(this, this);
    }

    @Override
    protected void initView() {
        Config config = ConfigManager.getInstance().getConfig();
        etUsername.setText(config.getUsername());
        if (TextUtils.isEmpty(config.getPassword())) {
            cbRemember.setChecked(false);
            etPwd.setText("");
        } else {
            cbRemember.setChecked(true);
            etPwd.setText(config.getPassword());
        }
        btnLogin.setOnClickListener(v -> {
            if (checkData()) {
                mListener.showWaitDialog("正在登录，请稍后...");
                presenter.login(etUsername.getText().toString(), etPwd.getText().toString(), cbRemember.isChecked());
            }
        });
        ivConfig.setOnClickListener(v -> mListener.enterAnotherFragment(SettingFragment.newInstance()));
    }

    @Override
    public void loginCallback(boolean result, String message) {
        mListener.dismissWaitDialog();
        if (result) {
            ToastManager.toast(getContext(), "登录成功", ToastManager.SUCCESS);
            mListener.enterAnotherFragment(EnterPagerFragment.newInstance());
        } else {
            ToastManager.toast(getContext(), message, ToastManager.INFO);
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

    private boolean checkData() {
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            ToastManager.toast(getContext(), "请输入用户名", ToastManager.INFO);
            return false;
        } else if (TextUtils.isEmpty(etPwd.getText())) {
            ToastManager.toast(getContext(), "请输入密码", ToastManager.INFO);
            return false;
        }
        return true;
    }
}
