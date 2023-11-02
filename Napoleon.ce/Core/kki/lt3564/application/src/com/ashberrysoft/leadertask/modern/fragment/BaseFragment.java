package com.ashberrysoft.leadertask.modern.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SetBlocking;
import com.ashberrysoft.leadertask.utils.Utils;

public abstract class BaseFragment extends Fragment//
        implements ObjectsReceiver {

    // BASE
    private LTApplication mApp;
    private LTSettings mSettings;
    private LocalBroadcastManager mBroadcastManager;
    private DbHelper mDbHelper;
    private int mContainerId;
    private BroadcastReceiver mReceiver;
    private ActionBar mActionBar;

    // VALUE's
    private Boolean mLandOrientation;
    private Integer mDisplayWidth;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mApp = (LTApplication) activity.getApplicationContext();
        mSettings = LTSettings.getInstance(mApp);
        mBroadcastManager = LocalBroadcastManager.getInstance(mApp);
        mDbHelper = DbHelper.getInstance(mApp);
        mContainerId = ((BaseActivity) activity).getContainerId();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        super.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return super.onCreateView(inflater, container, b);
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null && mActionBar != null) {
            final BaseActivity activity = (BaseActivity) getActivity();

            mActionBar = activity.getSupportActionBar();
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowCustomEnabled(true);

        }
    }

    protected abstract Boolean showSlidingMenu();

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter filter = getIntentFilter();
        if (filter != null) {
            mReceiver = getBroadcastReceiver();
            mBroadcastManager.registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    @Override
    public void onReceivingObjects(int code, Object... objects) {}

    protected IntentFilter getIntentFilter() {
        return null;
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBroadcastReceive(context, intent);
            }
        };
    }

    protected void onBroadcastReceive(Context context, Intent intent) {}

    protected void startFragmentInstead(BaseFragment replaceable, BaseFragment fragment, boolean toBackStack) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.remove(replaceable);
        ft.replace(mContainerId, fragment);
        if (toBackStack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }

        ft.commit();
    }

    protected void startFragment(BaseFragment fragment, boolean toBackStack) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(mContainerId, fragment);
        if (toBackStack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }

        ft.commit();
    }

    protected void startFragment(BaseFragment fragment) {
        startFragment(fragment, true);
    }

    protected LTApplication getApp() {
        return mApp;
    }

    protected LTSettings getSettings() {
        return mSettings;
    }

    protected void sendLocalBroadcast(Intent intent) {
        mBroadcastManager.sendBroadcast(intent);
    }

    protected DbHelper getDbHelper() {
        return mDbHelper;
    }

    protected ActionBar getActionBar() {
        return mActionBar;
    }

    protected void setActionBarTitle(String title) {
        mActionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
    }

    protected Fragment findFragmentInContainer() {
        return getFragmentManager().findFragmentById(mContainerId);
    }

    protected boolean isLandOrientation() {
        if (mLandOrientation == null) {
            mLandOrientation = Utils.isLandOrientation(getApp());
        }
        return mLandOrientation;
    }

    protected int getDisplayWidth() {
        if (mDisplayWidth == null) {
            mDisplayWidth = Utils.getDisplayWidth(getApp());
        }
        return mDisplayWidth;
    }

    protected void setBlocking(boolean setBlock) {
        SetBlocking.update(getApp(), setBlock);
    }
}