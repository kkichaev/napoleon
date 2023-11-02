package com.ashberrysoft.leadertask.modern.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.enums.LeaderTaskLanguage;
import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.helper.TaskLinkReset;
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

public class NavPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String[] PREFERENCE_KEYS = {
            "one_week", "week_number", "week_number_first", "show_tasks_count", "show_overdue_in_nav", "show_categories", "show_colors", "show_emps"
    };
    private Preference[] mPreferences;
    private List<Locale> mLocales;
    private LTApplication mApp;
    private LTSettings mSettings;

    public static NavPreferencesFragment newInstance() {
        final NavPreferencesFragment f = new NavPreferencesFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.view_list_settings_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_nav);

        mApp = (LTApplication) getActivity().getApplication();
        mSettings = mApp.getSettings();

        mPreferences = new Preference[PREFERENCE_KEYS.length];
        for (int i = 0; i < mPreferences.length; i++) {
            Preference pref = findPreference(PREFERENCE_KEYS[i]);
            if (pref != null) {
                mPreferences[i] = pref;
                mPreferences[i].setOnPreferenceChangeListener(this);
            }
        }

        fillPreferenceValues();

    }


    @Override
    public void onResume() {
        super.onResume();
        ((SettingsActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.settings_navigator));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        final String key = preference.getKey();

        // ONE WEEK IN NAV
        if (key.equals(PREFERENCE_KEYS[0])) {
            mSettings.setOneWeekInNav(!mSettings.isOneWeekInNav());
            ((CustomSwitchPreference) mPreferences[0]).setChecked(mSettings.isOneWeekInNav());

            MenuLoader.getInstance(mApp).resetCalendar();
            return true;
        }

        // show_weeks_count
        if (key.equals(PREFERENCE_KEYS[1])) {
            mSettings.setShowWeekCountInCalendar(!mSettings.isShowWeekCountInCalendar());
            ((CustomSwitchPreference) mPreferences[1]).setChecked(mSettings.isShowWeekCountInCalendar());

            final int slidingCustomWidth;
            Integer mSlidingWidth;
            Integer dislay = Utils.getDisplayWidth(mApp);
            /*if (mSettings.isShowWeekCountInCalendar()) {
                slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum_andweek);
            } else {
                slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);
            }*/

            slidingCustomWidth = dislay;

            if (mSettings.setSmallScreen(slidingCustomWidth >= dislay)) {
                mSlidingWidth = dislay - getResources().getDimensionPixelSize(R.dimen.slidingmenu_to_small);

            } else {
                mSlidingWidth = slidingCustomWidth;
            }

            mSettings.setLTCalendarWidth(mSlidingWidth);

            MenuLoader.getInstance(mApp).resetCalendar();
            //
            if (mSlidingMenu != null) {
                //mSlidingMenu.setBehindOffset(dislay - mSettings.getLTCalendarWidth());

                mSlidingMenu.setMode(SlidingMenu.LEFT);
                mSlidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
//                mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
                mSlidingMenu.setShadowDrawable(R.drawable.shadow);
                mSlidingMenu.setFadeDegree(0.35f);
                mSlidingMenu.showContent();
            }
            //
            Utils.setSomeSetting("cal_show_week_number", "__usn_field_cal_show_week_number", mSettings.isShowWeekCountInCalendar());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
            return true;
        }

        // weeks_from_1_jan
        if (key.equals(PREFERENCE_KEYS[2])) {
            mSettings.setWeekCountFromFirstJan(!mSettings.isWeekCountFromFirstJan());
            ((CustomSwitchPreference) mPreferences[2]).setChecked(mSettings.isWeekCountFromFirstJan());

            MenuLoader.getInstance(mApp).resetCalendar();
            Utils.setSomeSetting("cal_number_of_first_week", "__usn_field_cal_number_of_first_week", mSettings.isWeekCountFromFirstJan());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
        }

        // SHOW TASK COUNT IN NAVIGATOR
        if (key.equals(PREFERENCE_KEYS[3])) {
            mSettings.setShowTaskCountInNavigator(!mSettings.showTaskCountInNavigator());
            ((CustomSwitchPreference) mPreferences[3]).setChecked(mSettings.showTaskCountInNavigator());
            MenuLoader.getInstance(mApp).restartLoader();
            Utils.setSomeSetting("nav_show_summary", "__usn_field_nav_show_summary", mSettings.showTaskCountInNavigator());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
            return true;
        }

        // Overdue In Today or not
        if (key.equals(PREFERENCE_KEYS[4])) {
            mSettings.setOverdueInToday(!mSettings.isOverdueInToday());
            ((CustomSwitchPreference) mPreferences[4]).setChecked(!mSettings.isOverdueInToday());

            new TaskLinkReset(mApp).resetTodayTasks(mApp);

            MenuLoader.getInstance(mApp).restartLoader();

            mApp.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);

            Utils.setSomeSetting("nav_show_overdue", "__usn_field_nav_show_overdue", !mSettings.isOverdueInToday());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
            return true;
        }

        // SHOW CATEGORIES IN NAVIGATOR
        if (key.equals(PREFERENCE_KEYS[5])) {
            mSettings.setShowCategoriesInNavigator(!mSettings.showCategoriesInNavigator());
            ((CustomSwitchPreference) mPreferences[5]).setChecked(mSettings.showCategoriesInNavigator());
            MenuLoader.getInstance(mApp).restartLoader();
            Utils.setSomeSetting("nav_show_tags", "__usn_field_nav_show_tags", mSettings.showCategoriesInNavigator());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
            return true;
        }

        // show_colors
        if (key.equals(PREFERENCE_KEYS[6])) {
            mSettings.setShowColorsInNavigator(!mSettings.showColorsInNavigator());
            ((CustomSwitchPreference) mPreferences[6]).setChecked(mSettings.showColorsInNavigator());
            MenuLoader.getInstance(mApp).restartLoader();
            Utils.setSomeSetting("nav_show_markers", "__usn_field_nav_show_markers", mSettings.showColorsInNavigator());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
            return true;
        }

        // show_emps
        if (key.equals(PREFERENCE_KEYS[7])) {
            mSettings.setShowEmpsInNavigator(!mSettings.isEmpsInNavigator());
            ((CustomSwitchPreference) mPreferences[7]).setChecked(mSettings.isEmpsInNavigator());
            MenuLoader.getInstance(mApp).restartLoader();
            Utils.setSomeSetting("nav_show_emps", "__usn_field_nav_show_emps", mSettings.isEmpsInNavigator());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
            return true;
        }

        return false;
    }

    private void fillPreferenceValues() {
        ((CustomSwitchPreference) mPreferences[0]).setChecked(mSettings.isOneWeekInNav());
        ((CustomSwitchPreference) mPreferences[1]).setChecked(mSettings.isShowWeekCountInCalendar());
        ((CustomSwitchPreference) mPreferences[2]).setChecked(mSettings.isWeekCountFromFirstJan());
        ((CustomSwitchPreference) mPreferences[3]).setChecked(mSettings.showTaskCountInNavigator());
        ((CustomSwitchPreference) mPreferences[4]).setChecked(!mSettings.isOverdueInToday());
        ((CustomSwitchPreference) mPreferences[5]).setChecked(mSettings.showCategoriesInNavigator());
        ((CustomSwitchPreference) mPreferences[6]).setChecked(mSettings.showColorsInNavigator());
        ((CustomSwitchPreference) mPreferences[7]).setChecked(mSettings.isEmpsInNavigator());
    }

}
