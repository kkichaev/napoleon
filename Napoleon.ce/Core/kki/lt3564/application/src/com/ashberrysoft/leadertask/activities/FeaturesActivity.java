package com.ashberrysoft.leadertask.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.fragments.EditCategoriesFragment;
import com.ashberrysoft.leadertask.fragments.EditContactGroupsFragment;
import com.ashberrysoft.leadertask.fragments.EditContactsFragment;
import com.ashberrysoft.leadertask.fragments.EditEmpFragment;
import com.ashberrysoft.leadertask.fragments.EditMarkersFragment;
import com.ashberrysoft.leadertask.fragments.EditProjectsFragment;
import com.ashberrysoft.leadertask.fragments.PropertiesCategoryFragment;
import com.ashberrysoft.leadertask.fragments.PropertiesEmpFragment;
import com.ashberrysoft.leadertask.fragments.PropertiesMarkerFragment;
import com.ashberrysoft.leadertask.fragments.PropertiesProjectFragment;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.software.shell.fab.ActionButton;
import com.v2soft.AndLib.ui.activities.IBaseActivity;

import java.util.List;
import java.util.UUID;

import static android.R.attr.process;
import static com.ashberrysoft.leadertask.R.id.categories;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class FeaturesActivity extends AppCompatActivity//
        implements IBaseActivity<LTApplication> {

    public enum FeatureType {
        NOTHING, PROJECT, CATEGORY, EMP, MARKER, CONTACT_GROUPS, CONTACTS;
    }

    private static final String CLASS_PATH = FeaturesActivity.class.getName();
    private static final String EXTRA_FEATURE_TYPE = CLASS_PATH + "EXTRA_FEATURE_TYPE";
    private static final String EXTRA_FEATURE_TYPE_NEW = CLASS_PATH + "EXTRA_FEATURE_TYPE_NEW";
    private static final String EXTRA_UID = CLASS_PATH + "EXTRA_UID";
    public static final int FRAGMENT_CONTAINER = R.id.frame_layout;

    // VALUE's
    private ProgressDialog mProgress;
    private LTApplication mApp;
    public static ActionButton mActionButton;
    public static String EDIT_CONTACTS_FRAGMENT_TAG = "EDIT_CONTACTS_FRAGMENT_TAG";

    public static Intent newInstance(Context context, FeatureType type) {
        final Intent intent = new Intent(context, FeaturesActivity.class);
        intent.putExtra(EXTRA_FEATURE_TYPE, type.ordinal());

        return intent;
    }

    public static Intent newInstance(Context context, FeatureType type, String uid) {
        final Intent intent = new Intent(context, FeaturesActivity.class);
        intent.putExtra(EXTRA_FEATURE_TYPE_NEW, type.ordinal());
        intent.putExtra(EXTRA_UID, uid);

        return intent;
    }

    @Override
    protected void onCreate(Bundle b) {
        PreCreateActivityParamsHelper.setActivityParams(this);
        super.onCreate(b);
        mApp = (LTApplication) getApplicationContext();
        setContentView(R.layout.activity_edit_features);
        setActionBar();
        mActionButton = (ActionButton) findViewById(R.id.action_button);

        Utils.changeLocale(getResources(), mApp.getSettings().getLanguageLocale());

        Fragment f = null;
        if (b == null && getIntent() != null) {
            switch (FeatureType.values()[getIntent().getIntExtra(EXTRA_FEATURE_TYPE, 0)]) {
            case PROJECT:
                f = EditProjectsFragment.newInstance();
                break;

            case CATEGORY:
                f = EditCategoriesFragment.newInstance();
                break;

            case EMP:
                f = EditEmpFragment.newInstance();
                break;

            case MARKER:
                f = EditMarkersFragment.newInstance();
                break;

            case CONTACTS:
                f = EditContactsFragment.newInstance();
            break;

            case CONTACT_GROUPS:
                f = EditContactGroupsFragment.newInstance();
            break;

            default:
                break;
            }
            if (f != null) {
                if (FeatureType.values()[getIntent().getIntExtra(EXTRA_FEATURE_TYPE, 0)].equals(FeatureType.CONTACTS)) {
                    startFragmentWithTag(f, EDIT_CONTACTS_FRAGMENT_TAG);
                } else {
                    startFragment(f);
                }
            } else {
                if (b == null && getIntent() != null) {
                    mActionButton.setVisibility(View.GONE);
                    String uid = getIntent().getStringExtra(EXTRA_UID);
                    switch (FeatureType.values()[getIntent().getIntExtra(EXTRA_FEATURE_TYPE_NEW, 0)]) {
                        case PROJECT:
                            List<Project> projects = null;
                            try {
                                projects = DbHelper.getInstance(this).getProjectDao().queryBuilder().where().eq(Project.FIELD_UID, UUID.fromString(uid)).query();
                            } catch (Exception e) {

                            }
                            if (projects != null && !projects.isEmpty()) {
                                f = PropertiesProjectFragment.newInstance(projects.get(0));
                            } else {
                                f = EditProjectsFragment.newInstance();
                            }
                            break;

                        case CATEGORY:
                            List<Category> categories = null;
                            try {
                                categories = DbHelper.getInstance(this).getCategoryDao().queryBuilder().where().eq(Category.FIELD_UID, UUID.fromString(uid)).query();
                            } catch (Exception e) {

                            }
                            if (categories != null && !categories.isEmpty()) {
                                f = PropertiesCategoryFragment.newInstance(categories.get(0));
                            } else {
                                f = EditCategoriesFragment.newInstance();
                            }
                            break;

                        case EMP:
                            List<Emp> emps = null;
                            try {
                                emps = DbHelper.getInstance(this).getEmpDao().queryBuilder().where().eq(LeaderTaskProviderMetaData.EmpContract.LOGIN, uid).query();
                            } catch (Exception e) {

                            }
                            if (emps != null && !emps.isEmpty()) {
                                f = PropertiesEmpFragment.newInstance(emps.get(0), false);
                            } else {
                                f = EditEmpFragment.newInstance();
                            }
                            break;

                        case MARKER:
                            List<Marker> colors = null;
                            try {
                                colors = DbHelper.getInstance(this).getMarkerDao().queryBuilder().where().eq(Marker.FIELD_UID, UUID.fromString(uid)).query();
                            } catch (Exception e) {

                            }
                            if (colors != null && !colors.isEmpty()) {
                                f = PropertiesMarkerFragment.newInstance(colors.get(0));
                            } else {
                                f = EditMarkersFragment.newInstance();
                            }
                            break;

                        default:
                            break;
                    }
                    startFragment(f);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    public void setActionBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        //mApp.setTheme(this);
    }

    public void startFragment(Fragment f) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(FRAGMENT_CONTAINER, f);
        ft.commit();
    }

    public void startFragmentWithTag(Fragment f, String TAG) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(FRAGMENT_CONTAINER, f, TAG);
        ft.commit();
    }

    @Override
    public void showError(String message) {
        ErrorDialog.newInstance(message).showDialog(getSupportFragmentManager());
    }

    public static void hideActionButton() {
        if (mActionButton != null) {
            mActionButton.hide();
        }
    }

    public static void showActionButton() {
        if (mActionButton != null) {
            mActionButton.show();
        }
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
        if (getSupportFragmentManager().findFragmentByTag(EDIT_CONTACTS_FRAGMENT_TAG) != null) {
            EditContactsFragment fragment = (EditContactsFragment) getSupportFragmentManager().findFragmentByTag(EDIT_CONTACTS_FRAGMENT_TAG);
            if (fragment.isSearchShowing()) {
                fragment.closeSearchView();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}