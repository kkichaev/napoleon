package com.ashberrysoft.leadertask.modern.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.android.vending.billing.IInAppBillingService;
import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.enums.LeaderTaskLanguage;
import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;
import com.ashberrysoft.leadertask.modern.activity.LTPinActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.dialog.BuyDlg;
import com.ashberrysoft.leadertask.modern.view.CustomSwitchPreference;
import com.ashberrysoft.leadertask.utils.Utils;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.AppLockImpl;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID_CHONO;

/**
 * Created by Антон on 21.03.2018.
 */

public class MainPreferencesFragment extends PreferenceFragment implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final int REQUEST_CODE_ENABLE = 11;
    private static String LANGUAGE_PREF = "language";

    private static final String[] PREFERENCE_KEYS = {
            "autonomy_mode", "express", "sound", "is_need_password", "add_tasks", "calendar_period",
            "show_shrono", "strikethru_tasks", /*"contacts_enabled",*/ LANGUAGE_PREF
    };

    private Preference[] mPreferences;
    private List<Locale> mLocales;
    private LTApplication mApp;
    private LTSettings mSettings;

    private IInAppBillingService mBillingService;
    private ServiceConnection mConnection;

    public static MainPreferencesFragment newInstance() {
        // если надо чет передать во фрагмент
        //final Bundle b = new Bundle();
        //b.putSerializable(EXTRA, extra);

        final MainPreferencesFragment f = new MainPreferencesFragment();
        //f.setArguments(b); // передаем параметры

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.view_list_settings_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_main);

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

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBillingService = IInAppBillingService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBillingService = null;
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

    }


    @Override
    public void onResume() {
        super.onResume();

        ((SettingsActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.settings_basic));
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();

        // ADDING TASKS = TOP|BOTTOM
        if (key.equals(PREFERENCE_KEYS[4])) {
            mSettings.setAddingTasksToTop(!mSettings.isAddingTasksToTop());
            mPreferences[4].setSummary(mSettings.isAddingTasksToTop() ? R.string.add_task_top : R.string.add_task_bot);
            Utils.resetUserOrder(mApp);
            Utils.setSomeSetting("add_task_to_begin", "__usn_field_add_task_to_begin", mSettings.isAddingTasksToTop());
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);
            return true;
        }

        // CALENDAR PERIOD
        if (key.equals(PREFERENCE_KEYS[5])) {
            /*mSettings.setAddingTasksToTop(!mSettings.isAddingTasksToTop());
            mPreferences[4].setSummary(mSettings.isAddingTasksToTop() ? R.string.add_task_top : R.string.add_task_bot);
            Utils.resetUserOrder(mApp);
            mSettings.setNeedToPutSettings(true);
            Utils.startSync(mApp);*/
            final View v = LayoutInflater.from(getActivity()).inflate(R.layout.time_calendar_dialog, null);
            final NumberPicker min = (NumberPicker) v.findViewById(R.id.count_min);
            final NumberPicker max = (NumberPicker) v.findViewById(R.id.count_max);
            min.setMinValue(0);
            max.setMinValue(0);
            min.setMaxValue(24);
            max.setMaxValue(24);
            min.setValue(mSettings.getMinHour());
            max.setValue(mSettings.getMaxHour()); // чтобы менялись

            min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    if (i1 > max.getValue()) {
                        max.setValue(i1);
                    }

                }
            });
            max.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    if (i1 < min.getValue()) {
                        min.setValue(i1);
                    }
                }
            });

            final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setView(v);
            adb.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mSettings.setMinHour(min.getValue());
                    mSettings.setMaxHour(max.getValue());
                    mPreferences[5].setSummary(LTSettings.getInstance().getMinHour()+":00-"+LTSettings.getInstance().getMaxHour()+":00"); // todo

                    Utils.setSomeStringsSetting("cal_work_time", "__usn_field_cal_work_time", LTSettings.getInstance().getMinHour()+":0-"+LTSettings.getInstance().getMaxHour()+":0");
                    mSettings.setNeedToPutSettings(true);
                    Utils.startSync(mApp);
                }
            });
            adb.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            adb.show();

            return true;
        }

        // LANGUAGE
        if (key.equals(LANGUAGE_PREF)) {
            showSelectLanguageDialog();
            return true;
        }



        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        final String key = preference.getKey();

        // AUTONOMY MODE
        if (key.equals(PREFERENCE_KEYS[0])) {
            mSettings.setAutonomyMode(!mSettings.isAutonomyMode());
            ((CustomSwitchPreference) mPreferences[0]).setChecked(mSettings.isAutonomyMode());
            if (!mSettings.isAutonomyMode()) {
                Utils.startSync(mApp);
            }
            return true;
        }

        // SHOW EXPRESS PANEL
        if (key.equals(PREFERENCE_KEYS[1])) {
            mSettings.setShowPanel(!mSettings.isShowPanel());
            ((CustomSwitchPreference) mPreferences[1]).setChecked(mSettings.isShowPanel());
            LeaderTaskSyncService.sendNotif(mApp);
            return true;
        }

        // SOUND
        if (key.equals(PREFERENCE_KEYS[2])) {
            mSettings.setSoundEnabled(!mSettings.isSoundEnabled());
            ((CustomSwitchPreference) mPreferences[2]).setChecked(mSettings.isSoundEnabled());
            return true;
        }

        // show_chrono
        if (key.equals(PREFERENCE_KEYS[6])) {
            if (LTSettings.getInstance().getVerifyKey() == ""){
                new BuyDlg().buyForExtension(getActivity(), mBillingService);
            }else
            if (LTSettings.getInstance().getVerifyAddins().indexOf("b32674b-c991-42bd-b8e6-a0e7a4650c2c") == -1) {
                final View v = LayoutInflater.from(getActivity()).inflate(R.layout.buy_chrono_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);
                builder.setPositiveButton(R.string.contact_details, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final boolean hasCustomLocale = mSettings.getLanguageLocale() != null;
                        final Locale appLocale = hasCustomLocale ?  mSettings.getLanguageLocale() : Locale.getDefault();
                        String url = "https://www.leadertask.ru/apptolink?language=текязык&product=вродеандроидпосмотрикакваналогичныхссылках&action=marketkhronometrazh";
                        final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        getActivity().startActivity(browser);
                    }
                });

                builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();


            } else {
                mSettings.setShowChrono(!mSettings.isShowChrono());
                ((CustomSwitchPreference) mPreferences[6]).setChecked(mSettings.isShowChrono());
                mSettings.setToRebootAfterChanges(true);

                Utils.setSomeSetting("stopwatch", "__usn_field_stopwatch", mSettings.isShowChrono());
                mSettings.setNeedToPutSettings(true);
                Utils.startSync(mApp);
            }
        }

        // STRIKETHRU TASKS
        if (key.equals(PREFERENCE_KEYS[7])) {
            mSettings.setStrikethruTask(!mSettings.isStrikethruTask());
            ((CustomSwitchPreference) mPreferences[7]).setChecked(mSettings.isStrikethruTask());
            mSettings.setToRebootAfterChanges(true);

            return true;
        }
/*
        // CONTACTS ENABLED
        if (key.equals(PREFERENCE_KEYS[8])) {
            mSettings.setContactsEnable(!mSettings.isContactsEnabled());
            ((CustomSwitchPreference) mPreferences[8]).setChecked(mSettings.isContactsEnabled());
            return true;
        }
*/

        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog == null || which != Dialog.BUTTON_POSITIVE) {
            return;
        }

        final Locale locale;
        {
            final int pos = (((AlertDialog) dialog).getListView()).getCheckedItemPosition();
            if (pos == 0) {
                locale = null;

            } else {
                locale = mLocales.get(pos);
            }
        }

        mSettings.setLanguageLocale(locale);

        getActivity().finish();
        startActivity(SlidingActivity.newInstance(mApp));
        startActivity(SettingsActivity.newInstance(mApp));
        mSettings.setToRebootAfterChanges(true);

        Utils.setSomeStringsSetting("language", "__usn_field_language", mApp.getResources().getString(R.string.currlang));
        mSettings.setNeedToPutSettings(true);
        Utils.startSync(mApp);
    }

    private void fillPreferenceValues() {
        ((CustomSwitchPreference) mPreferences[0]).setChecked(mSettings.isAutonomyMode());
        ((CustomSwitchPreference) mPreferences[1]).setChecked(mSettings.isShowPanel());
        ((CustomSwitchPreference) mPreferences[2]).setChecked(mSettings.isSoundEnabled());

         mPreferences[4].setSummary(mSettings.isAddingTasksToTop() ? R.string.add_task_top : R.string.add_task_bot);

         mPreferences[5].setSummary(LTSettings.getInstance().getMinHour()+":00-"+LTSettings.getInstance().getMaxHour()+":00"); // todo

        ((CustomSwitchPreference) mPreferences[6]).setChecked(mSettings.isShowChrono());
        ((CustomSwitchPreference) mPreferences[7]).setChecked(mSettings.isStrikethruTask());

        /*if (LTSettings.getInstance().getVerifyAddins().indexOf("b32674b-c991-42bd-b8e6-a0e7a4650c2c") == -1) {
            // скрыть настройку
            ((CustomSwitchPreference) mPreferences[6]).setEnabled(false);
        }*/

        //((CustomSwitchPreference) mPreferences[8]).setChecked(mSettings.isContactsEnabled());

        if (mSettings.getLanguageLocale() == null) {
            mPreferences[8].setSummary(R.string.lang_system);
        } else {
            mPreferences[8].setSummary(getCuteLanguageName(new StringBuilder(), mSettings.getLanguageLocale(), mSettings.getLanguageLocale()));
        }
    }

    private String getCuteLanguageName(StringBuilder sb, Locale l, Locale appLocale) {
        Utils.clearStringBuilder(sb);

        final String language = l.getDisplayLanguage(appLocale);
        sb.append(language.substring(0, 1).toUpperCase(appLocale));
        sb.append(language.substring(1));

        return sb.toString();
    }

    private void showSelectLanguageDialog() {
        final boolean hasCustomLocale = mSettings.getLanguageLocale() != null;

        mLocales = new ArrayList<>(LeaderTaskLanguage.values().length + 1);

        mLocales.add(Locale.getDefault());
        for (LeaderTaskLanguage l : LeaderTaskLanguage.values()) {
            mLocales.add(l.getLocale());
        }

        final Locale appLocale = hasCustomLocale ? mSettings.getLanguageLocale() : Locale.getDefault();
        final StringBuilder sb = new StringBuilder();
        final String[] strings = new String[mLocales.size()];
        int selectedPosition = -1;
        Locale locale;

        strings[0] = getString(R.string.lang_system);
        for (int i = 1; i < strings.length; i++) {
            locale = mLocales.get(i);
            strings[i] = getCuteLanguageName(sb, locale, appLocale);
            if (appLocale.getLanguage().equals(locale.getLanguage())) {
                selectedPosition = i;
            }
        }

        if (!hasCustomLocale) {
            selectedPosition = 0;
        }

        final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(R.string.settings_language);
        adb.setSingleChoiceItems(strings, selectedPosition, this);
        adb.setPositiveButton(R.string.btn_ok, this);
        adb.show();
    }

    private void openBuy() {
        ArrayList skuList = new ArrayList();
        skuList.add(IN_APP_ID_CHONO);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        Bundle skuDetails;
        try {
            Bundle ownedItems = mBillingService.getPurchases(3, getActivity().getPackageName(), "inapp", null);
            // Check response
            int responseCode = ownedItems.getInt("RESPONSE_CODE");
            if (responseCode != 0) {
            }
            // Get the list of purchased items
            ArrayList<String> purchaseDataList =
                    ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            for (String purchaseData : purchaseDataList) {
                JSONObject o = new JSONObject(purchaseData);
                String purchaseToken = o.optString("token", o.optString("purchaseToken"));
                // Consume purchaseToken, handling any errors
                mBillingService.consumePurchase(3, getActivity().getPackageName(), purchaseToken);
            }
            skuDetails = mBillingService.getSkuDetails(3, getActivity().getPackageName(), "inapp", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    String amount = object.getString("price");
                    StringBuilder sb = new StringBuilder();
                    for (int i=0; i < amount.length(); i++) {
                        char c = amount.charAt(i);
                        if (Character.isDigit(c)) {
                            sb.append(c);
                        } else {
                            if (c == ",".charAt(0)){
                                sb.append(c);
                            } else {
                                if (c == ".".charAt(0)){
                                    sb.append(",");
                                }
                            }
                        }
                    }
                    SettingsActivity.mAmount = sb.toString();
                    SettingsActivity.mAmount = amount;
                    SettingsActivity.mCurrency = object.getString("price_currency_code");
                    if (sku.equals(IN_APP_ID_CHONO)) {
                        Bundle buyIntentBundle = mBillingService.getBuyIntent(3, getActivity().getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                        if ((int) buyIntentBundle.get("RESPONSE_CODE") == 0) { // если можно купить
                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1003, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

}
