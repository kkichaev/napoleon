package com.ashberrysoft.leadertask.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.fragments.TaskEditFragment;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.activities.IBaseActivity;

public class BaseSlidingActivity extends AppCompatActivity implements IBaseActivity<LTApplication> {

    public interface OnBackClickListener {
        public boolean onBackClick();
    }

    private static final String EXTRA_SHOW_BLOCK = "EXTRA_SHOW_BLOCK";

    // VALUE's
    protected LTApplication mApp;
    private ProgressDialog mProgress;
    private Boolean mIsLandOrientation;
    private Handler mHandler;
    private boolean mShowBlock;

    @Override
    public void onCreate(Bundle b) {
        //getIntent().putExtra(SlidingActivityHelper.EXTRSA_RESOURCE_ID, R.layout.slidingmenumain);
        PreCreateActivityParamsHelper.setActivityParams(this);
        super.onCreate(b);

        mApp = getApplicationObject();
        mHandler = new Handler();

        Utils.changeLocale(getResources(), mApp.getSettings().getLanguageLocale());

        setContentView(R.layout.activity_main);
        //setBehindContentView(R.layout.menu_frame);
        setShowBlock(b);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBlockingProcess(mShowBlock, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putBoolean(EXTRA_SHOW_BLOCK, mShowBlock);
    }

    @Override
    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);
        setShowBlock(b);
    }

    private void setShowBlock(Bundle b) {
        if (b != null && b.containsKey(EXTRA_SHOW_BLOCK)) {
            mShowBlock = b.getBoolean(EXTRA_SHOW_BLOCK);
        } else {
            mShowBlock = false;
        }
    }

    public void enableSlidingMenu() {
        //getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

    public void disableSlidingMenu() {
        //getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    public void disableAndSetSlidingMenu(boolean closeMenu, boolean closeNormal) {
        if (closeMenu) {
            //getSlidingMenu().showContent();
        }
        disableSlidingMenu();
        if (isLandOrientation()) {
            //getSlidingMenu().setMenuCloseNormal(closeNormal);
        }
    }

    @Override
    public void showError(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ErrorDialog.newInstance(message).showDialog(getSupportFragmentManager());
                } catch (Exception e) {
                    Utils.toLog(e);
                }

            }
        });
    }

    @Override
    public void setLoadingProcess(boolean value, Object tag) {}

    @Override
    public void setBlockingProcess(boolean value, Object tag) {
        lockOrientation(value, tag);
        if (value) {
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

        mShowBlock = value;
    }

    private void lockOrientation(boolean lock, Object tag) {
        if (tag != null) {
            setRequestedOrientation(lock ? ActivityInfo.SCREEN_ORIENTATION_LOCKED
                    : ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }

    @SuppressWarnings("deprecation")
    public boolean isLandOrientation() {
        if (mIsLandOrientation == null) {
            final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            final Display display = windowManager.getDefaultDisplay();
            mIsLandOrientation = display.getWidth() > display.getHeight();
        }
        return mIsLandOrientation;
    }

    @Override
    public void showError(int messageResource) {
        showError(mApp.getString(messageResource));
    }

    @Override
    public LTApplication getApplicationObject() {
        return (LTApplication) getApplication();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (TaskEditFragment.getInstance() != null) {
            TaskEditFragment.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }
}