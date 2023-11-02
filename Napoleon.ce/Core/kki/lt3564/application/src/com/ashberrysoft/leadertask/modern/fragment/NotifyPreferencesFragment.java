package com.ashberrysoft.leadertask.modern.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.modern.helper.TaskLinkReset;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.modern.view.CustomSwitchPreference;
import com.ashberrysoft.leadertask.utils.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ashberrysoft.leadertask.modern.activity.SlidingActivity.mSlidingMenu;

/**
 * Created by Антон on 21.03.2018.
 */

public class NotifyPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String[] PREFERENCE_KEYS = {
            "reminder", "reminder_for_me", "reminder_by_me", "reminder_comments", "reminder_overdue", "reminder_today"
    };
    private Preference[] mPreferences;
    private List<Locale> mLocales;
    private LTApplication mApp;
    private LTSettings mSettings;

    public static NotifyPreferencesFragment newInstance() {
        final NotifyPreferencesFragment f = new NotifyPreferencesFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.view_list_settings_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_notify);

        mApp = (LTApplication) getActivity().getApplication();
        mSettings = mApp.getSettings();

        mPreferences = new Preference[PREFERENCE_KEYS.length];
        for (int i = 0; i < mPreferences.length; i++) {
            Preference pref = findPreference(PREFERENCE_KEYS[i]);
            if (pref != null) {
                mPreferences[i] = pref;
                mPreferences[i].setOnPreferenceChangeListener(this);
                mPreferences[i].setOnPreferenceClickListener(this);
            }
        }

        fillPreferenceValues();

    }


    @Override
    public void onResume() {
        super.onResume();
        ((SettingsActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.notify_settings));
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();

        // NOTIFICATIONS
        if (key.equals(PREFERENCE_KEYS[0])) {
            final String[] items = {getString(R.string.settings_reminder_title), getString(R.string.settings_vibration), getString(R.string.settings_standart_sound)};
            final boolean[] selectedItems = new boolean[items.length];
            selectedItems[0] = mSettings.isReminder() ? true : false;
            selectedItems[1] = mSettings.isNotifyVibration() ? true : false;
            selectedItems[2] = mSettings.isNotifyStandartSound() ? true : false;
            final ArrayList seletedItems=new ArrayList();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.settings_reminder_title));
            FrameLayout preTerm = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.pre_notify_spinner, null);
            Spinner spinnerPreTime = (Spinner) preTerm.findViewById(R.id.sp_pre_notify);
            ArrayAdapter<CharSequence> mAdapterChooser = ArrayAdapter.createFromResource(mApp, R.array.pre_time, R.layout.spinner_item);
            mAdapterChooser.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerPreTime.setAdapter(mAdapterChooser);
            int selection = 0;
            switch (mSettings.getNotifyPreTime()) {
                case 0:
                    selection = 0;
                    break;
                case 5:
                    selection = 1;
                    break;
                case 10:
                    selection = 2;
                    break;
                case 15:
                    selection = 3;
                    break;
                case 30:
                    selection = 4;
                    break;
                case 60:
                    selection = 5;
                    break;
            }
            spinnerPreTime.setSelection(selection);
            spinnerPreTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            mSettings.setNotifyPreTime(0);
                            break;
                        case 1:
                            mSettings.setNotifyPreTime(5);
                            break;
                        case 2:
                            mSettings.setNotifyPreTime(10);
                            break;
                        case 3:
                            mSettings.setNotifyPreTime(15);
                            break;
                        case 4:
                            mSettings.setNotifyPreTime(30);
                            break;
                        case 5:
                            mSettings.setNotifyPreTime(60);
                            break;
                    }

                    TaskNotifyHelper.getInstance(mApp).convertTasksToNotify();
                    Utils.setSomeStringsSetting("reminders_in_n_minutes", "__usn_field_reminders_in_n_minutes", ""+mSettings.getNotifyPreTime());
                    mSettings.setNeedToPutSettings(true);
                    Utils.startSync(mApp);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            builder.setView(preTerm);
            builder.setMultiChoiceItems(items, selectedItems,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected,boolean isChecked) {
                            // тут меняем чекбоксы
                            if (isChecked) {
                                seletedItems.add(indexSelected);
                            } else if (seletedItems.contains(indexSelected)) {
                                seletedItems.remove(Integer.valueOf(indexSelected));
                            }
                            // тут меняем настройки ЛТ
                            switch (indexSelected)
                            {
                                case 0:
                                    mSettings.setReminder(!mSettings.isReminder());
                                    final TaskNotifyHelper notifyHelper = TaskNotifyHelper.getInstance(mApp);

                                    if (mSettings.isReminder()) {
                                        //mPreferences[2].setSummary(R.string.settings_reminder_on);
                                        notifyHelper.connectAllTaskNotifiesToTrigger();

                                    } else {
                                        //mPreferences[2].setSummary(R.string.settings_reminder_off);
                                        notifyHelper.disconnectAllTaskNotifiesFromTrigger();
                                    }
                                    break;
                                case 1:
                                    mSettings.setNotifyVibration(!mSettings.isNotifyVibration());
                                    break;
                                case 2:
                                    mSettings.setNotifyStandartSound(!mSettings.isNotifyStandartSound());
                                    break;
                            }
                            //
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
            return true;
        }
        return false;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        final String key = preference.getKey();

        // settings_notification_for_me
        if (key.equals(PREFERENCE_KEYS[1])) {
            mSettings.setNotifyForMe(!mSettings.isNotifyForMe());
            ((CustomSwitchPreference) mPreferences[1]).setChecked(mSettings.isNotifyForMe());

            return true;
        }

        // settings_notification_by_me
        if (key.equals(PREFERENCE_KEYS[2])) {
            mSettings.setNotifyByMyCanceled(!mSettings.isNotifyByMeCanceled());
            ((CustomSwitchPreference) mPreferences[2]).setChecked(mSettings.isNotifyByMeCanceled());

            return true;
        }

        // settings_notification_comments
        if (key.equals(PREFERENCE_KEYS[3])) {
            mSettings.setNotifyComments(!mSettings.isNotifyComments());
            ((CustomSwitchPreference) mPreferences[3]).setChecked(mSettings.isNotifyComments());

            return true;
        }

        // settings_reminder_overdue
        if (key.equals(PREFERENCE_KEYS[4])) {
            mSettings.setNotifyOverdue(!mSettings.isNotifyOverdue());
            ((CustomSwitchPreference) mPreferences[4]).setChecked(mSettings.isNotifyOverdue());

            return true;
        }

        // settings_reminder_today
        if (key.equals(PREFERENCE_KEYS[5])) {
            mSettings.setNotifyToday(!mSettings.isNotifyToday());
            ((CustomSwitchPreference) mPreferences[5]).setChecked(mSettings.isNotifyToday());

            return true;
        }

        return false;
    }

    private void fillPreferenceValues() {
        ((CustomSwitchPreference) mPreferences[1]).setChecked(mSettings.isNotifyForMe());
        ((CustomSwitchPreference) mPreferences[2]).setChecked(mSettings.isNotifyByMeCanceled());
        ((CustomSwitchPreference) mPreferences[3]).setChecked(mSettings.isNotifyComments());
        ((CustomSwitchPreference) mPreferences[4]).setChecked(mSettings.isNotifyOverdue());
        ((CustomSwitchPreference) mPreferences[5]).setChecked(mSettings.isNotifyToday());
    }

}
