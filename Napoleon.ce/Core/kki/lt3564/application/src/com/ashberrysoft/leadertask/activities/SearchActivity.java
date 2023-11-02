package com.ashberrysoft.leadertask.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.fragments.EditCategoriesFragment;
import com.ashberrysoft.leadertask.fragments.EditContactGroupsFragment;
import com.ashberrysoft.leadertask.fragments.EditContactsFragment;
import com.ashberrysoft.leadertask.fragments.EditEmpFragment;
import com.ashberrysoft.leadertask.fragments.EditMarkersFragment;
import com.ashberrysoft.leadertask.fragments.EditProjectsFragment;
import com.ashberrysoft.leadertask.fragments.SearchFragment;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.software.shell.fab.ActionButton;
import com.v2soft.AndLib.ui.activities.IBaseActivity;

import static android.R.attr.fragment;
import static com.ashberrysoft.leadertask.modern.fragment.TasksFragment.mTempTask;


public class SearchActivity extends AppCompatActivity//
        implements IBaseActivity<LTApplication> {


    private static final String CLASS_PATH = SearchActivity.class.getName();
    public static final int FRAGMENT_CONTAINER = R.id.frame_layout;

    // VALUE's
    private ProgressDialog mProgress;
    private LTApplication mApp;
    public static String SEARCH_FRAGMENT_TAG = "SEARCH_FRAGMENT_TAG";

    public static Intent newInstance(Context context) {
        final Intent intent = new Intent(context, SearchActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle b) {
        PreCreateActivityParamsHelper.setActivityParams(this);
        super.onCreate(b);
        mApp = (LTApplication) getApplicationContext();
        setContentView(R.layout.activity_search);
        setActionBar();
        Utils.changeLocale(getResources(), mApp.getSettings().getLanguageLocale());

        if (b == null ) {
            Fragment f = SearchFragment.newInstance(null);
            startFragmentWithTag(f, SEARCH_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    public void setActionBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApp.setTheme(this);
    }

    public void startFragmentWithTag(Fragment f, String TAG) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(FRAGMENT_CONTAINER, f, TAG);
        ft.addToBackStack(f.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public void showError(String message) {
        ErrorDialog.newInstance(message).showDialog(getSupportFragmentManager());
    }

    @Override
    public void showError(int messageResource) {
        showError(getString(messageResource));
    }

    @Override
    public void setBlockingProcess(boolean value, Object tag) {
        lockOrientation(value);

        if (value) {
            if (mProgress == null) {
                mProgress = new ProgressDialog(this);
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setMessage(getString(R.string.blocking_process));
            }
            mProgress.show();
        } else {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

    private void lockOrientation(boolean lock) {
        setRequestedOrientation(lock ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    @Override
    public LTApplication getApplicationObject() {
        return mApp;
    }

    @Override
    public void setLoadingProcess(boolean value, Object tag) {}

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}