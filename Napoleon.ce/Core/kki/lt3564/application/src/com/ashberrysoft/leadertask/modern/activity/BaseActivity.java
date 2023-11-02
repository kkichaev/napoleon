package com.ashberrysoft.leadertask.modern.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.SetBlockingContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.day_calendar.DayCalendarActivity;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.interfaces.OnActivityBackPressedListener;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SetBlocking;
import com.ashberrysoft.leadertask.modern.fragment.BaseFragment;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity //
        implements ObjectsReceiver, LoaderCallbacks<Cursor> {

    // BASE
    private LTApplication mApp;
    private LocalBroadcastManager mBroadcastManager;
    private DbHelper mDbHelper;
    private Integer mDisplayWidth;


    private Boolean mLandOrientation;

    public ProgressDialog mProgress;

    private BroadcastReceiver mReceiver;
    private LTSettings mSettings;

    // VALUE's
    private Integer mSlidingWidth;

    public abstract int getContainerId();

    @Override
    public void onBackPressed() {
        final boolean pressBack;

        final Fragment fragment = findFragmentInContainer();
        if (fragment != null && fragment instanceof OnActivityBackPressedListener) {
            pressBack = ((OnActivityBackPressedListener) fragment).onBackPress();

        } else {
            pressBack = true;
        }

        if (pressBack) {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreate(Bundle b) {
        if (!this.getClass().equals(AddNewTaskWidgetActivity.class)) {
            if (this.getClass().equals(EditTaskActivity.class) || this.getClass().equals(DayCalendarActivity.class)) {
                PreCreateActivityParamsHelper.setActivityParamsGray(this);
            } else {
                PreCreateActivityParamsHelper.setActivityParams(this);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
            }
        }
        super.onCreate(b);
        mApp = (LTApplication) getApplicationContext();
        mSettings = LTSettings.getInstance(mApp);
        mBroadcastManager = LocalBroadcastManager.getInstance(mApp);
        mDbHelper = DbHelper.getInstance(mApp);

        Utils.changeLocale(getResources(), mSettings.getLanguageLocale());
        initSlidingMenu();

        getSupportLoaderManager().initLoader(SetBlocking.CALLBACK_ID, null, this);

        LTApplication.mBackStackActivities.put(getComponentName().getClassName(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LTApplication.mBackStackActivities.remove(getComponentName().getClassName());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        switch (id) {
        case SetBlocking.CALLBACK_ID:
            return new CursorLoader(this, SetBlockingContract.CONTENT_URI, null, SetBlocking.SELECTION, null, null);

        default:
            return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> l) {}

    @Override
    public void onLoadFinished(Loader<Cursor> l, Cursor c) {
        switch (l.getId()) {
        case SetBlocking.CALLBACK_ID:
            if (c.moveToFirst() && new SetBlocking(c).isBlocking()) {
                if (mProgress == null) {
                    mProgress = new ProgressDialog(this);
                    mProgress.setMessage(getString(R.string.blocking_process));
                    mProgress.setCanceledOnTouchOutside(false);
                }
                mProgress.show();

            } else {
                if (mProgress != null) {
                    mProgress.dismiss();
                    mProgress = null;
                }
            }
            break;

        default:
            break;
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
    public void onReceivingObjects(int code, Object... objects) {}

    @Override
    public void onResume() {
        super.onResume();

        mApp.setTheme(this);
        {
            final IntentFilter filter = getIntentFilter();
            if (filter != null) {
                mReceiver = getBroadcastReceiver();
                mBroadcastManager.registerReceiver(mReceiver, filter);
            }
        }
    }

    public void setBlocking(boolean setBlock) {
        SetBlocking.update(getApp(), setBlock);
     }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBroadcastReceive(context, intent);
            }
        };
    }

    private void initSlidingMenu() {
        final int slidingCustomWidth;
        Integer dislay = Utils.getDisplayWidth(getApp());
        /*if (mSettings.isShowWeekCountInCalendar()) {
            slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum_andweek);
        } else {
            slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);
        }*/
        slidingCustomWidth = dislay;

        if (getSettings().setSmallScreen(slidingCustomWidth >= getDisplayWidth())) {
            mSlidingWidth = getDisplayWidth() - getResources().getDimensionPixelSize(R.dimen.slidingmenu_to_small);

        } else {
            mSlidingWidth = slidingCustomWidth;
        }

        getSettings().setLTCalendarWidth(mSlidingWidth);
    }

    protected Fragment findFragmentInContainer() {
        return getSupportFragmentManager().findFragmentById(getContainerId());
    }

    protected LTApplication getApp() {
        return mApp;
    }

    protected DbHelper getDbHelper() {
        return mDbHelper;
    }

    protected int getDisplayWidth() {
        if (mDisplayWidth == null) {
            mDisplayWidth = Utils.getDisplayWidth(getApp());
        }
        return mDisplayWidth;
    }

    protected IntentFilter getIntentFilter() {
        return null;
    }

    protected LTSettings getSettings() {
        return mSettings;
    }

    protected boolean isLandOrientation() {
        if (mLandOrientation == null) {
            mLandOrientation = Utils.isLandOrientation(getApp());
        }
        return mLandOrientation;
    }

    protected void onBroadcastReceive(Context context, Intent intent) {}

    @Override
    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    protected void sendLocalBroadcast(Intent intent) {
        mBroadcastManager.sendBroadcast(intent);
  }



    protected void startFragment(BaseFragment fragment, boolean toBackStack) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(getContainerId(), fragment);
        if (toBackStack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }

        ft.commit();
    }
}