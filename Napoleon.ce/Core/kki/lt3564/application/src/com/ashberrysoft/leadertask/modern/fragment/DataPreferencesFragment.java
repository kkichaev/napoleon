package com.ashberrysoft.leadertask.modern.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.SwitchPreference;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Антон on 21.03.2018.
 */

public class DataPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String[] PREFERENCE_KEYS = {
            "contacts", "contact_groups", "projects", "categories", "markers", "emps", "del_canceled"
    };
    private Preference[] mPreferences;
    private Preference mContacts;
    private Preference mContactsGroups;
    private LTApplication mApp;
    private LTSettings mSettings;

    public static DataPreferencesFragment newInstance() {
        final DataPreferencesFragment f = new DataPreferencesFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_data);

        mApp = (LTApplication) getActivity().getApplication();
        mSettings = mApp.getSettings();

        mPreferences = new Preference[PREFERENCE_KEYS.length];
        for (int i = 0; i < mPreferences.length; i++) {
            Preference pref = findPreference(PREFERENCE_KEYS[i]);
            if (pref != null) {
                mPreferences[i] = pref;
                mPreferences[i].setOnPreferenceClickListener(this);
            }
        }

        fillPreferenceValues();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.view_list_settings_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SettingsActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.settings_basic_data));
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();

        // OPEN EDIT CONTACTS
        if (key.equals(PREFERENCE_KEYS[0])) {
            if ((mSettings.getLicenseType() == mSettings.LICENSE_TYPE_FREE ||
                    mSettings.getLicenseType() == mSettings.LICENSE_TYPE_NONE)){
                LicenseDialog.newInstance().showDialog(this.getFragmentManager());
            }
            else {
                startActivity(FeaturesActivity.newInstance(getActivity(), FeatureType.CONTACTS));
            }
            return true;
        }

        // OPEN EDIT CONTACT_GROUPS
        if (key.equals(PREFERENCE_KEYS[1])) {
            if ((mSettings.getLicenseType() == mSettings.LICENSE_TYPE_FREE ||
                    mSettings.getLicenseType() == mSettings.LICENSE_TYPE_NONE)){
                LicenseDialog.newInstance().showDialog(this.getFragmentManager());
            }
            else {
                startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.CONTACT_GROUPS));
            }
            return true;
        }

        // OPEN EDIT PROJECTS
        if (key.equals(PREFERENCE_KEYS[2])) {
            startActivity(FeaturesActivity.newInstance(getActivity(), FeatureType.PROJECT));
            return true;
        }

        // OPEN EDIT CATEGORIES
        if (key.equals(PREFERENCE_KEYS[3])) {
            startActivity(FeaturesActivity.newInstance(getActivity(), FeatureType.CATEGORY));
            return true;
        }

        // OPEN EDIT MARKERS
        if (key.equals(PREFERENCE_KEYS[4])) {
            startActivity(FeaturesActivity.newInstance(getActivity(), FeatureType.MARKER));
            return true;
        }

        // OPEN EDIT EMPS
        if (key.equals(PREFERENCE_KEYS[5])) {
            startActivity(FeaturesActivity.newInstance(getActivity(), FeatureType.EMP));
            return true;
        }

        // del tasks cancelled
        if (key.equals(PREFERENCE_KEYS[6])) {
            final int[] selectedItem = {0};
            final String[] items = {getString(R.string.del_canceled2), getString(R.string.del_canceled3)};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.del_canceled_title));
            builder.setCancelable(true);
            builder.setSingleChoiceItems(items, selectedItem[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedItem[0] = which;
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    switch (selectedItem[0]) {
                        case 0:
                            Cursor c = null;
                            ArrayList <LTask> tasks = new ArrayList<LTask>();

                            try {
                                c = getActivity().getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null,//
                                        new TaskSelectionBuilder(new StringBuilder()).getCompletedTasks(null).build(), null, null);
                                if (c != null) {
                                    if (c.getCount() > 0) {
                                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                            tasks.add(new LTask(c));
                                        }
                                    }
                                }

                            } finally {
                                if (c != null) {
                                    c.close();
                                }
                            }
                            for (LTask task : tasks) {
                                new TaskDeleteHelper(mApp, task, false).start();
                            }

                            //Utils.startSync(mApp);
                            break;

                        case 1:
                            Cursor c2 = null;
                            ArrayList <LTask> tasksD = new ArrayList<LTask>();

                            try {
                                c2 = getActivity().getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null,//
                                        new TaskSelectionBuilder(new StringBuilder()).getCompletedTasks(null).build(), null, null);
                                if (c2 != null) {
                                    if (c2.getCount() > 0) {
                                        final int CompleteTime  = c2.getColumnIndex(LionMetaData.LTaskContract.CompleteTime);

                                        for (c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext()) {
                                            if ( TimeHelper.currentTimeMillisWithoutTimeZone() - c2.getLong(CompleteTime) >= new Long("2628000000")) {
                                                tasksD.add(new LTask(c2));
                                            }
                                        }
                                    }
                                }

                            } finally {
                                if (c2 != null) {
                                    c2.close();
                                }
                            }
                            for (LTask task : tasksD) {
                                new TaskDeleteHelper(mApp, task, false).start();
                            }
                            break;

                        default:
                            break;
                    }
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }



        return false;
    }

    private void fillPreferenceValues() {
        resetContactsVisibility();
    }

    private void resetContactsVisibility() {
        if (!mSettings.isContactsEnabled()) {
            Preference mPrefContacts = (Preference) findPreference(PREFERENCE_KEYS[0]);
            Preference mPrefContactsGroup = (Preference) findPreference(PREFERENCE_KEYS[1]);
            PreferenceScreen mScreen = (PreferenceScreen) findPreference("preferenceScreen");
            mScreen.removePreference(mPrefContacts);
            mScreen.removePreference(mPrefContactsGroup);
        }
    }

}
