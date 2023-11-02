package com.ashberrysoft.leadertask.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.BaseSlidingActivity;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

/**
 * Базовый фрагмент
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
@SuppressWarnings("deprecation")
public abstract class LTBaseFragment extends BaseFragment<LTApplication, LTSettings> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        // set view background
        setBackground();
        super.onResume();
    }

    private void setBackground() {
        if (mApp.getSettings().isThemeDark()) {
            getView().setBackgroundColor(Color.BLACK);
        } else {
            getView().setBackgroundColor(Color.WHITE);
        }
    }

    protected boolean isLogin() {
        return !"".equals(mSettings.getUserName());
    }

    protected void startFragment(Fragment fragment) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(R.id.main_fragment, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(fragment.getClass().getSimpleName());

        ft.commit();
    }

    /**
     * 
     * @return true - visible bootom panel
     */
    public abstract boolean showTitleBar();

    public boolean isNetworkAvailable() {
        return Utils.isNetworkAvailable(mApp);
    }

    public void showErrorMessage(int resId) {
        showMessage(mApp.getString(resId));
    }

    public void showMessage(String message) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.showErrorMessage(message);
        }
    }

    public void setBlock(boolean setBlock) {
        if (getActivity() != null) {
            ((BaseSlidingActivity) getActivity()).setBlockingProcess(setBlock, null);
        }
    }
}