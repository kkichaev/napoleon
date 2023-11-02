package com.ashberrysoft.leadertask.modern.fragment.intro_fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.modern.activity.LTPinActivity;
import com.ashberrysoft.leadertask.modern.view.CustomSwitchPreference;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.AppLockImpl;

import java.util.List;
import java.util.Locale;

public class InputPasFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String IS_NEED_PIN = "is_need_pin";
    private static final String IS_NEED_FINGER = "is_need_finger";
    private static final String IS_NEED_PASSWORD = "is_need_password";

    private static final int REQUEST_CODE_ENABLE = 11;

    private static final String[] PREFERENCE_KEYS = {
            IS_NEED_PASSWORD, IS_NEED_PIN, IS_NEED_FINGER
    };

    private Preference[] mPreferences;
    private List<Locale> mLocales;
    private LTApplication mApp;
    private LTSettings mSettings;

    public static InputPasFragment newInstance() {
        final InputPasFragment f = new InputPasFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.view_list_settings_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_input);

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            getPreferenceScreen().removePreference(findPreference(IS_NEED_FINGER));
        else {
            FingerprintManager m = (FingerprintManager) getActivity().getSystemService("fingerprint");

            if (m != null && !m.isHardwareDetected())
                getPreferenceScreen().removePreference(findPreference(IS_NEED_FINGER));
        }

        CustomSwitchPreference p = (CustomSwitchPreference) findPreference(IS_NEED_PASSWORD);

        if (p != null)
            p.setChecked(mSettings.isNeedPasswordToStart());

        p = (CustomSwitchPreference) findPreference(IS_NEED_PIN);

        if (p != null)
            p.setChecked(mSettings.isNeedPinToStart());

        p = (CustomSwitchPreference) findPreference(IS_NEED_FINGER);

        if (p != null)
            p.setChecked(mSettings.isNeedFingerToStart());
    }


    @Override
    public void onResume() {
        super.onResume();

        ((SettingsActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.pass_at_start));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        final String key = preference.getKey();

        // PASS AT START
        if (key.equals(IS_NEED_PASSWORD)) {
            mSettings.setNeedPasswordToStart(!mSettings.isNeedPasswordToStart());

            if (!mSettings.isNeedPasswordToStart()){
                CustomSwitchPreference p = (CustomSwitchPreference)findPreference(IS_NEED_PIN);
                p.setChecked(false);
                mSettings.setNeedPinToStart(false);
                AppLockImpl impl = AppLockImpl.getInstance(getActivity(), LTPinActivity.class);
                impl.setPasscode(null);

                p = (CustomSwitchPreference)findPreference(IS_NEED_FINGER);

                if (p != null) {
                    p.setChecked(false);
                    mSettings.setNeedFingerToStart(false);
                }
            }
            return true;
        }

        // PIN AT START
        if (key.equals(IS_NEED_PIN)) {
            if (!mSettings.isNeedPinToStart()) {
                Intent intent = new Intent(getActivity(), LTPinActivity.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                startActivityForResult(intent, AppLock.ENABLE_PINLOCK);
            } else {
                mSettings.setNeedPinToStart(!mSettings.isNeedPinToStart());
                ((CustomSwitchPreference) preference).setChecked(mSettings.isNeedPinToStart());
                AppLockImpl impl = AppLockImpl.getInstance(getActivity(), LTPinActivity.class);
                impl.setPasscode(null);


                CustomSwitchPreference p = (CustomSwitchPreference)findPreference(IS_NEED_FINGER);

                if (p != null) {
                    p.setChecked(false);
                    mSettings.setNeedFingerToStart(false);
                }
            }

            return true;
        }

        // FINGER AT START
        if (key.equals(IS_NEED_FINGER)) {
            if (!mSettings.isNeedPinToStart()){
                Intent intent = new Intent(getActivity(), LTPinActivity.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                startActivityForResult(intent, AppLock.ENABLE_PINLOCK_FINGERPRINT);
            }else if (!mSettings.isNeedFingerToStart()) {
                Intent intent = new Intent(getActivity(), LTPinActivity.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_FINGERPRINT);
                startActivityForResult(intent, AppLock.ENABLE_FINGERPRINT);
            } else {
                mSettings.setNeedFingerToStart(!mSettings.isNeedFingerToStart());
            }

            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppLock.ENABLE_PINLOCK || requestCode == AppLock.ENABLE_PINLOCK_FINGERPRINT) {
            if (resultCode == Activity.RESULT_OK) {
                mSettings.setNeedPinToStart(true);
                mSettings.setNeedPasswordToStart(true);
            } else {
                mSettings.setNeedPinToStart(false);

                AppLockImpl impl = AppLockImpl.getInstance(getActivity(), LTPinActivity.class);
                impl.setPasscode(null);
            }

            CustomSwitchPreference p = (CustomSwitchPreference) findPreference(IS_NEED_PIN);
            p.setChecked(mSettings.isNeedPinToStart());

            p = (CustomSwitchPreference) findPreference(IS_NEED_PASSWORD);
            p.setChecked(mSettings.isNeedPasswordToStart());

            if (requestCode == AppLock.ENABLE_PINLOCK_FINGERPRINT){
                Intent intent = new Intent(getActivity(), LTPinActivity.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_FINGERPRINT);
                startActivityForResult(intent, AppLock.ENABLE_FINGERPRINT);
            }
        }

        if (requestCode == AppLock.ENABLE_FINGERPRINT) {
            if (resultCode == Activity.RESULT_OK) {
                mSettings.setNeedFingerToStart(true);
            } else {
                mSettings.setNeedFingerToStart(false);
            }

            CustomSwitchPreference p = (CustomSwitchPreference) findPreference(IS_NEED_FINGER);
            p.setChecked(mSettings.isNeedFingerToStart());
        }

    }
}
