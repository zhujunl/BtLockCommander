package com.miaxis.btlockcommanderdemo.view.fragment;

import android.os.Bundle;

import java.util.Map;

import androidx.fragment.app.Fragment;

public interface OnFragmentInteractionListener {
    void clearBackStack();
    void enterAnotherFragment(BaseFragment baseFragment);
    void backToStack(Class<? extends Fragment> fragment);
    void showWaitDialog(String message);
    void dismissWaitDialog();
}
