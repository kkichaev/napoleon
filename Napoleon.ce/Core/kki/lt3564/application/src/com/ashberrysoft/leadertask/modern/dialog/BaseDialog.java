package com.ashberrysoft.leadertask.modern.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SetBlocking;

/**
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 30.07.14
 */
public class BaseDialog extends DialogFragment {

    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";

    private final String mDialogTag = getDialogTag(((Object) this).getClass());

    // BASE
    private LTApplication mApp;
    private LTSettings mSettings;
    private LocalBroadcastManager mBroadcastManager;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (LTApplication) getActivity().getApplicationContext();
        mSettings = LTSettings.getInstance(mApp);
        mBroadcastManager = LocalBroadcastManager.getInstance(mApp);
    }

    @Override
    public void onResume() {
        super.onResume();

        {
            final IntentFilter filter = getIntentFilter();
            if (filter != null) {
                mReceiver = getBroadcastReceiver();
                mBroadcastManager.registerReceiver(mReceiver, filter);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
        }
    }

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

    public LTApplication getApp() {
        return mApp;
    }

    public LTSettings getSettings() {
        return mSettings;
    }

    protected void sendLocalBroadcast(Intent intent) {
        mBroadcastManager.sendBroadcast(intent);
    }

    public void setBlocking(boolean block) {
        SetBlocking.update(getApp(), block);
    }

    protected void receiveObjects(int code, Object... objects) {
        if (getTargetFragment() != null) {
            if (getTargetFragment() instanceof ObjectsReceiver) {
                ((ObjectsReceiver) getTargetFragment()).onReceivingObjects(code, objects);
            }

        } else if (getActivity() != null) {
            if (getActivity() instanceof ObjectsReceiver) {
                ((ObjectsReceiver) getActivity()).onReceivingObjects(code, objects);
            }
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    public void showDialog(FragmentManager manager) {
        if (manager.findFragmentByTag(mDialogTag) == null) {
            super.show(manager, mDialogTag);
        }
    }

    public static String getDialogTag(Class<?> cls) {
        return cls.getName() + "DIALOG_TAG";
    }

    public static void dismissFragmentIfExist(FragmentManager manager, String dialogTag) {
        final Fragment fragment = manager.findFragmentByTag(dialogTag);
        if (fragment != null && fragment instanceof DialogFragment) {
            ((DialogFragment) fragment).dismiss();
        }
    }


}