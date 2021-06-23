package com.miaxis.btlockcommanderdemo.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle3.components.support.RxAppCompatDialogFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 一非 on 2018/4/24.
 */

public abstract class BaseDialogFragment extends RxAppCompatDialogFragment {

    protected Context context;
    private Unbinder bind;

    public BaseDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(setContentView(), container, false);
        bind = ButterKnife.bind(this, view);
        initData();
        initView();
        return view;
    }

    protected abstract int setContentView();

    protected abstract void initData();

    protected abstract void initView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }

}
