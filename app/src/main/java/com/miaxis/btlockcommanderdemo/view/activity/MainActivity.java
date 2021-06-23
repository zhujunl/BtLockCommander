package com.miaxis.btlockcommanderdemo.view.activity;

import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.app.BtLockCommanderApp;
import com.miaxis.btlockcommanderdemo.app.GlideApp;
import com.miaxis.btlockcommanderdemo.event.BluetoothEvent;
import com.miaxis.btlockcommanderdemo.event.UncaughtExceptionEvent;
import com.miaxis.btlockcommanderdemo.manager.BluetoothManager;
import com.miaxis.btlockcommanderdemo.manager.ToastManager;
import com.miaxis.btlockcommanderdemo.service.PassThroughService;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;
import com.miaxis.btlockcommanderdemo.view.fragment.BaseFragment;
import com.miaxis.btlockcommanderdemo.view.fragment.ConnectFragment;
import com.miaxis.btlockcommanderdemo.view.fragment.EnterFragment;
import com.miaxis.btlockcommanderdemo.view.fragment.EnterPagerFragment;
import com.miaxis.btlockcommanderdemo.view.fragment.LoginFragment;
import com.miaxis.btlockcommanderdemo.view.fragment.OnFragmentInteractionListener;
import com.miaxis.btlockcommanderdemo.view.fragment.ScanFragment;
import com.miaxis.btlockcommanderdemo.view.fragment.SettingFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements OnFragmentInteractionListener {

    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.fl_main)
    FrameLayout flMain;

    private MaterialDialog waitDialog;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        initDialog();
        Disposable subscribe = new RxPermissions(this)
                .request(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    if (success) {
                        new Thread(() -> BtLockCommanderApp.getInstance().initApplicationAsync(MainActivity.this::showFirstPage)).start();
                    } else {
                        Toast.makeText(MainActivity.this, "拒绝权限将无法正常使用", Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> Log.e("asd", "权限获取出错"));
    }

    @Override
    protected void initView() {
        GlideApp.with(this).load(R.raw.loading).into(ivLoading);
    }

    @Override
    public void clearBackStack() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void enterAnotherFragment(BaseFragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        String backStateName = fragment.getClass().getName();
        manager.popBackStackImmediate(backStateName, 0);
        manager.beginTransaction()
                .replace(R.id.fl_main, fragment)
                .addToBackStack(backStateName)
                .commit();
    }

    @Override
    public void backToStack(Class<? extends Fragment> fragment) {
//        getSupportFragmentManager().popBackStack();
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            if (fragment != null) {
                getSupportFragmentManager().popBackStack(fragment.getName(), 0);
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            finish();
        }
    }

    @Override
    public void showWaitDialog(String message) {
        waitDialog.getContentView().setText(message);
        waitDialog.show();
    }

    @Override
    public void dismissWaitDialog() {
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        switch (event.getType()) {
            case BluetoothEvent.ON_STATE_OFF:
                ToastManager.toast(this, "蓝牙已关闭", ToastManager.INFO);
                if (!isMainPage()) {
                    enterAnotherFragment(EnterPagerFragment.newInstance());
                }
                break;
            case BluetoothEvent.ON_STATE_TURNING_OFF:
                ToastManager.toast(this, "蓝牙关闭中", ToastManager.INFO);
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUncaughtExceptionEvent(UncaughtExceptionEvent event) {
        ToastManager.toast(this, "发生未捕获异常，已断开连接", ToastManager.ERROR);
        BluetoothManager.getInstance().disConnected();
        enterAnotherFragment(EnterPagerFragment.newInstance());
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getVisibleFragment();
        if (fragment instanceof ScanFragment || fragment instanceof SettingFragment) {
            backToStack(null);
        } else {
            new MaterialDialog.Builder(this)
                    .title("确认退出？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        BluetoothManager.getInstance().disConnected();
                        finish();
                        System.exit(0);
                    })
                    .negativeText("取消")
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        System.exit(0);
    }

    private boolean isMainPage() {
        Fragment fragment = getVisibleFragment();
        return fragment instanceof EnterPagerFragment
                || fragment instanceof EnterFragment
                || fragment instanceof ScanFragment
                || fragment instanceof ConnectFragment;
    }

    private void showFirstPage(Boolean result) {
        runOnUiThread(() -> {
            if (result) {
                ivLoading.setVisibility(View.GONE);
                if (ValueUtil.APP_VERSION) {
                    enterAnotherFragment(LoginFragment.newInstance());
                } else {
                    enterAnotherFragment(EnterPagerFragment.newInstance());
                }
            } else {
                ToastManager.toast(MainActivity.this, "未找到蓝牙设备", ToastManager.ERROR);
            }
        });
        PassThroughService.startActionResumePassThrough(this);
    }

    private void initDialog() {
        waitDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content("请稍后")
                .cancelable(false)
                .autoDismiss(false)
                .build();
    }

}
