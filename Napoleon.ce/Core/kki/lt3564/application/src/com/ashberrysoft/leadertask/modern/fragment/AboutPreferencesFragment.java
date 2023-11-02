package com.ashberrysoft.leadertask.modern.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.ZipCompres;
import com.ashberrysoft.leadertask.views.AboutProgramView;

import java.io.File;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by Антон on 21.03.2018.
 */

public class AboutPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String[] PREFERENCE_KEYS = {
            "support", "site"
    };
    public static final String ZIP_FILE_NAME = "log.zip";
    public static final String[] LEADERTASK_SUPPORT = { (IPCConstants.DEBUG ? "anton.sobolev@leadertask.com" : "911@leadertask.com") };
    private Preference[] mPreferences;
    private ProgressDialog mProgress;
    private LTApplication mApp;
    private Handler mHandler;
    private LTSettings mSettings;

    public static AboutPreferencesFragment newInstance() {
        final AboutPreferencesFragment f = new AboutPreferencesFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_about);
        mApp = (LTApplication) getActivity().getApplication();
        mSettings = mApp.getSettings();
        mHandler = new Handler();

        mPreferences = new Preference[PREFERENCE_KEYS.length];
        for (int i = 0; i < mPreferences.length; i++) {
            Preference pref = findPreference(PREFERENCE_KEYS[i]);
            if (pref != null) {
                mPreferences[i] = pref;
                mPreferences[i].setOnPreferenceClickListener(this);
            }
        }

        //addFooterView(new AboutProgramView(mApp, null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.view_about_program, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        TextView mAbout = (TextView) v.findViewById(R.id.version);
        final ImageView leadertask_logo = (ImageView) v.findViewById(R.id.leadertask_logo);

        leadertask_logo.setImageResource(Utils.getLeaderTaskLauncherResource());

        mAbout.setText("v" + mApp.getApplicationBuildVersion());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SettingsActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.settings_about_program));
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();

        // SUPPORT
        if (key.equals(PREFERENCE_KEYS[0])) {
            setBlock(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    toSupport();
                }
            }).start();
            return true;
        }

        // SITE
        if (key.equals(PREFERENCE_KEYS[1])) {
            openSite();
            return true;
        }

        return false;
    }

    private void toSupport() {
        final StringBuilder sb = new StringBuilder();
        try {
            final PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            sb.append("Application:\n\tLeaderTask v");
            sb.append(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {}
        sb.append("\nDevice:\n\t");
        sb.append(android.os.Build.MODEL);
        sb.append("\nAndroid version:\n\t");
        sb.append(android.os.Build.VERSION.RELEASE);
        sb.append("\nTimezone:\n\t");
        sb.append(TimeZone.getDefault());

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.putExtra(Intent.EXTRA_EMAIL, LEADERTASK_SUPPORT);

        final File[] logs = mApp.getAppFolderLogs().listFiles();
        final File zipFile = new File(mApp.getAppFolderLogs(), ZIP_FILE_NAME);
        final File dbFile = IPCConstants.DEBUG ? getActivity().getDatabasePath(DbHelper.DATABASE_NAME) : null;
        new ZipCompres(logs, dbFile, zipFile).toZip();

        if (zipFile != null && zipFile.exists()) {
            for (File log : logs) {
                if (!log.getName().equals(zipFile.getName())) {
                    log.delete();
                }
            }

            intent.setType("application/zip");
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zipFile));
        } else {

            final ArrayList<Uri> uris = new ArrayList<Uri>(logs.length);
            for (File log : logs) {
                uris.add(Uri.fromFile(log));
            }

            intent.setType(SharedStrings.MIME_TYPE_PLAIN);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }

        startActivity(Intent.createChooser(intent, mApp.getString(R.string.what_to_use)));
        setBlock(false);
    }

    private void openSite() {
        final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/main.php?language=russian"));
        startActivity(browser);
    }

    private void setBlock(boolean value) {
        if (value) {
            if (mProgress == null) {
                mProgress = new ProgressDialog(getActivity());
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setMessage(getString(R.string.blocking_process));
            }
            mProgress.show();

        } else {
            mHandler.post(mSetBlockFalse);
        }
    }

    private final Runnable mSetBlockFalse = new Runnable() {
        @Override
        public void run() {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    };
}
