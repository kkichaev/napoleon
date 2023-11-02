package ru.sobr.app.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import ru.sobr.app.telephony.SobrGsm;
import ru.sobr.app.ui.holo.HoloAlertDialogBuilder;
import ru.sobr.app.utils.Constants;
import ru.sobr.app.utils.DefaultProfiles;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileDetailActivity extends SherlockActivity implements
        OnClickListener, OnCheckedChangeListener, OnItemSelectedListener {

    public static final String TAG = "ProfileDetailActivity";
    public static final boolean DEBUG = false;

    public static final int CUSTOM_SETTINGS_ID = 2;
    public static final String KEY_PROFILE_EDITMODE = "profileeditmode_key";
    public static final String KEY_PROFILE_SYSTYPE = "systemtype_key";
    public static final String KEY_PROFILE_SYSNUMB = "systemnumber_key";
    public static final String KEY_PROFILE_PASSWORD = "password_key";
    public static final String KEY_PROFILE_PINCODE = "pincode_key";
    public static final String DISABLE_COMMAND = "disable_value";

    boolean onPause = false;

    private String mTempSystemType = "";

    private Intent mResultIntent = new Intent();

    private Button mChangePass;
    private Spinner mSystemType, mPhoneStatus;

    private EditText mName;
    private EditText mSysNumber;
    private EditText mPassword;
    private EditText mUnlockCode;
    private EditText mPinCode;
    private EditText mBasePhoneNumber, mSecondPhoneNumber, mThirdPhoneNumber;
    private EditText mBalanceThreshold, mBalanceQueryCode;
    private EditText mSobrAssistLogin, mSobrAssistPassword;

    private CheckBox mPinCodeOnBoot;

    private InnerSmsStatusReceiver mSmsStatusReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setResult(Activity.RESULT_CANCELED);
        ActionBar ab = getSupportActionBar();

        if (getIntent() == null || getIntent().getAction() == null) {
            this.finish();
            return;
        }
        if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
            if (getIntent().getData() == null) {
                this.finish();
                return;
            }
        } else if (getIntent().getAction().equals(Intent.ACTION_INSERT)) {
        } else {
            this.finish();
            return;
        }

        mSmsStatusReceiver = new InnerSmsStatusReceiver();

        mSystemType = (Spinner) findViewById(R.id.profile_systemtype_spinner);
        mSystemType.setOnItemSelectedListener(this);
        mPhoneStatus = (Spinner) findViewById(R.id.phone_status_spinner);
        mPhoneStatus.setOnItemSelectedListener(this);

        mSobrAssistLogin = (EditText) findViewById(R.id.sobr_assist_login);
        mSobrAssistPassword = (EditText) findViewById(R.id.sobr_assist_password);

        findViewById(R.id.sobr_assist_registration).setOnClickListener(this);

        mName = (EditText) findViewById(R.id.profile_name_text);
        mSysNumber = (EditText) findViewById(R.id.profile_phone_number_text);
        mPassword = (EditText) findViewById(R.id.profile_password_text);
        mUnlockCode = (EditText) findViewById(R.id.profile_unlock_code_text);
        mPinCode = (EditText) findViewById(R.id.profile_pin_code_text);
        mBasePhoneNumber = (EditText) findViewById(R.id.profile_base_phone_number_text);
        mSecondPhoneNumber = (EditText) findViewById(R.id.profile_second_phone_number_text);
        mThirdPhoneNumber = (EditText) findViewById(R.id.profile_third_phone_number_text);
        mBalanceThreshold = (EditText) findViewById(R.id.profile_max_balance_text);
        mBalanceQueryCode = (EditText) findViewById(R.id.profile_balance_query_code_text);

        mPinCodeOnBoot = (CheckBox) findViewById(R.id.profile_pin_code_on_start);
        ((CompoundButton) findViewById(R.id.profile_password_show_text))
                .setOnCheckedChangeListener(this);
        ((CompoundButton) findViewById(R.id.profile_unlock_code_show_text))
                .setOnCheckedChangeListener(this);
        ((CompoundButton) findViewById(R.id.profile_pin_code_show_text))
                .setOnCheckedChangeListener(this);
        ((CompoundButton) findViewById(R.id.sobr_assist_show_text))
                .setOnCheckedChangeListener(this);

        mChangePass = (Button) findViewById(R.id.profile_change_password_btn);
        mChangePass.setOnClickListener(this);
        findViewById(R.id.profile_unlock_code_btn).setOnClickListener(this);
        findViewById(R.id.profile_pin_code_btn).setOnClickListener(this);
        findViewById(R.id.profile_base_phone_number_btn).setOnClickListener(
                this);
        findViewById(R.id.profile_second_phone_number_btn).setOnClickListener(
                this);
        findViewById(R.id.profile_third_phone_number_btn).setOnClickListener(
                this);
        findViewById(R.id.profile_max_balance_btn).setOnClickListener(this);
        findViewById(R.id.profile_balance_query_code_btn).setOnClickListener(
                this);

        findViewById(R.id.profile_get_settings_info).setOnClickListener(this);
        findViewById(R.id.profile_detail_custom).setOnClickListener(this);

        if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
            try {
                Cursor cursor = getContentResolver().query(
                        getIntent().getData(), null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int columnName = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.NAME);
                        int columnSysType = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_TYPE);
                        int columnPhoneStatys = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.PHONE_STATUS);
                        int columnPhoneNumber = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                        int columnPassword = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.PASSWORD);
                        int columnUnlockCode = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.UNLOCK_CODE);
                        int columnPinCode = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.PIN_CODE);
                        int columnPinCodeOnBoot = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.PIN_CODE_ON_BOOT);
                        int columnBasePhoneNumber = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.BASE_PHONE_NUMBER);
                        int columnSecondPhoneNumber = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.SECOND_PHONE_NUMBER);
                        int columnThirdPhoneNumber = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.THIRD_PHONE_NUMBER);
                        int columnBalanceThreshold = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.BALANCE_THRESHOLD);
                        int columnBalanceQueryCode = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.BALANCE_QUERY_CODE);
                        int sobrAssistLogin = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.SOBR_ASSIST_LOGIN);
                        int sobrAssistPassword = cursor
                                .getColumnIndexOrThrow(SobrContract.Profiles.SOBR_ASSIST_PASSWORD);

                        ab.setTitle(cursor.getString(columnName));

                        mSystemType.setSelection(getValuePosition(
                                cursor.getString(columnSysType),
                                R.array.entryvalues_system_type));
                        mPhoneStatus.setSelection(getValuePosition(
                                cursor.getString(columnPhoneStatys),
                                R.array.entryvalues_phone_status));

                        mSobrAssistLogin.setText(cursor.getString(sobrAssistLogin));
                        mSobrAssistPassword.setText(cursor.getString(sobrAssistPassword));

                        mName.setText(cursor.getString(columnName));
                        mSysNumber.setText(cursor.getString(columnPhoneNumber));
                        mPassword.setText(cursor.getString(columnPassword));
                        mUnlockCode.setText(cursor.getString(columnUnlockCode));
                        mPinCode.setText(cursor.getString(columnPinCode));
                        boolean pinCodeOnBoot = cursor.getString(
                                columnPinCodeOnBoot).equals("true");
                        mPinCodeOnBoot.setChecked(pinCodeOnBoot);
                        mBasePhoneNumber.setText(cursor
                                .getString(columnBasePhoneNumber));
                        mSecondPhoneNumber.setText(cursor
                                .getString(columnSecondPhoneNumber));
                        mThirdPhoneNumber.setText(cursor
                                .getString(columnThirdPhoneNumber));
                        mBalanceThreshold.setText(cursor
                                .getString(columnBalanceThreshold));
                        mBalanceQueryCode.setText(cursor
                                .getString(columnBalanceQueryCode));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {
            }
        }

        mTempSystemType = getSelectedValue(mSystemType, null,
                R.array.entryvalues_system_type);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
            menu.add(R.string.profile_delete)
                    .setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (getIntent().getAction().equals(
                                    Intent.ACTION_EDIT)) {
                                deleteProfileDialog();
                            }
                            return true;
                        }

                    })
                    .setShowAsAction(
                            MenuItem.SHOW_AS_ACTION_IF_ROOM
                                    | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }

        menu.add(R.string.profile_save)
                .setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String typeValue = getSelectedValue(mSystemType, null,
                                R.array.entryvalues_system_type);
                        if (typeValue.equals(Constants.SOBR_CHIP0103)
                                || typeValue.equals(Constants.SOBR_G0103)
                                || typeValue.equals(Constants.SOBR_CHIP111213)) {
                            if (profileNameValid() && sysNumberValid()
                                    && pinCodeValid() && basePhoneNumbValid()) {
                                saveProfile();
                                ProfileDetailActivity.this.finish();
                            }
                        } else if (typeValue.equals(Constants.SOBR_DOMONLINE)) {
                            if (profileNameValid() && sysNumberValid()
                                    && passwordValid() && pinCodeValid()
                                    && basePhoneNumbValid()) {
                                saveProfile();
                                ProfileDetailActivity.this.finish();
                            }

                        } else if (typeValue.equals(Constants.SOBR_GSM) || 
                        		typeValue.equals(Constants.SOBR_GSM510) ) {
                            if (profileNameValid() && sysNumberValid()
                                    && passwordValid() && unlockCodeValid()
                                    && pinCodeValid() && basePhoneNumbValid()) {
                                saveProfile();
                                ProfileDetailActivity.this.finish();
                            }

                        }
                        return true;
                    }

                })
                .setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM
                                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableButtons();
        registerReceiver(mSmsStatusReceiver, new IntentFilter(
                SobrGsm.ACTION_SMS_SENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSmsStatusReceiver);
        onPause = true;
        
        if(MainActivity.isApplicationSentToBackground(this)){
        	System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        String systemType = getSelectedValue(mSystemType, null,
                R.array.entryvalues_system_type);

        String systemNumb = mSysNumber.getText().toString();
        String password = mPassword.getText().toString();
        String pinCode = mPinCode.getText().toString();

        switch (v.getId()) {
            case R.id.sobr_assist_registration:
                openRegistrationScreen();
                break;
            case R.id.profile_change_password_btn:
                showChangePassDialog();
                break;

            case R.id.profile_get_settings_info:
                if (systemType.equals(Constants.SOBR_CHIP0103)
                        || systemType.equals(Constants.SOBR_G0103)
                        || systemType.equals(Constants.SOBR_CHIP111213)) {
                    if (sysNumberValid() && pinCodeValid()) {
                        SobrGsm.profileGetSettingsType3(this, systemNumb, pinCode);
                        v.setEnabled(false);
                    }
                } else {
                    if (sysNumberValid() && passwordValid()) {
                        SobrGsm.profileGetSettings(this, systemNumb, password);
                        v.setEnabled(false);
                    }
                }
                break;

            case R.id.profile_unlock_code_btn:
                if (sysNumberValid() && passwordValid() && unlockCodeValid()) {
                    SobrGsm.profileUnlockEngine(this, systemNumb, password,
                            mUnlockCode.getText().toString());
                    v.setEnabled(false);
                }
                break;

            case R.id.profile_pin_code_btn:

                if (systemType.equals(Constants.SOBR_CHIP0103)
                        || systemType.equals(Constants.SOBR_G0103)
                        || systemType.equals(Constants.SOBR_CHIP111213)) {

                    if (sysNumberValid())
                        showChangePinCodeDialog();

                } else {
                    if (sysNumberValid() && passwordValid()
                            && !TextUtils.isEmpty(mPinCode.getText())) {
                        SobrGsm.profilePinCode(this, systemNumb, password, pinCode);
                        v.setEnabled(false);
                    }
                }
                break;

            case R.id.profile_base_phone_number_btn:
                String basePhone = clearPhoneNumber(mBasePhoneNumber.getText()
                        .toString());

                if (systemType.equals(Constants.SOBR_CHIP0103)
                        || systemType.equals(Constants.SOBR_G0103)
                        || systemType.equals(Constants.SOBR_CHIP111213)) {
                    if (sysNumberValid() && pinCodeValid()
                            && !TextUtils.isEmpty(mBasePhoneNumber.getText())) {
                        SobrGsm.profileBasePhoneNumbType3(this, systemNumb,
                                pinCode, basePhone);
                        v.setEnabled(false);
                    }
                } else {
                    if (sysNumberValid() && passwordValid()
                            && !TextUtils.isEmpty(mBasePhoneNumber.getText())) {
                        SobrGsm.profileBasePhoneNumb(this, systemNumb, password,
                                basePhone);
                        v.setEnabled(false);
                    }
                }
                break;

            case R.id.profile_second_phone_number_btn:
                if (sysNumberValid() && passwordValid()
                        && !TextUtils.isEmpty(mSecondPhoneNumber.getText())) {
                    SobrGsm.profileSecondPhoneNumb(this, systemNumb, password,
                            clearPhoneNumber(mSecondPhoneNumber.getText()
                                    .toString()));
                    v.setEnabled(false);
                }
                break;

            case R.id.profile_third_phone_number_btn:
                if (sysNumberValid() && passwordValid()
                        && !TextUtils.isEmpty(mThirdPhoneNumber.getText())) {
                    SobrGsm.profileThirdPhoneNumb(
                            this,
                            systemNumb,
                            password,
                            clearPhoneNumber(mThirdPhoneNumber.getText().toString()));
                    v.setEnabled(false);
                }
                break;

            case R.id.profile_max_balance_btn:
                if (systemType.equals(Constants.SOBR_CHIP0103)
                        || systemType.equals(Constants.SOBR_G0103)
                        || systemType.equals(Constants.SOBR_CHIP111213)) {
                    if (sysNumberValid() && pinCodeValid()
                            && !TextUtils.isEmpty(mBalanceThreshold.getText())) {
                        SobrGsm.profileMaxBalanceType3(this, systemNumb, pinCode,
                                mBalanceThreshold.getText().toString());
                        v.setEnabled(false);
                    }
                } else {
                    if (sysNumberValid() && passwordValid()
                            && !TextUtils.isEmpty(mBalanceThreshold.getText())) {
                        SobrGsm.profileMaxBalance(this, systemNumb, password,
                                mBalanceThreshold.getText().toString());
                        v.setEnabled(false);
                    }
                }
                break;

            case R.id.profile_balance_query_code_btn:
                if (systemType.equals(Constants.SOBR_CHIP0103)
                        || systemType.equals(Constants.SOBR_G0103)
                        || systemType.equals(Constants.SOBR_CHIP111213)) {
                    if (sysNumberValid() && pinCodeValid()
                            && !TextUtils.isEmpty(mBalanceQueryCode.getText())) {
                        SobrGsm.profileGetBalanceType3(this, systemNumb, pinCode,
                                mBalanceQueryCode.getText().toString());
                        v.setEnabled(false);
                    }
                } else {
                    if (sysNumberValid() && passwordValid()
                            && !TextUtils.isEmpty(mBalanceQueryCode.getText())) {
                        SobrGsm.profileGetBalance(this, systemNumb, password,
                                mBalanceQueryCode.getText().toString());
                        v.setEnabled(false);
                    }
                }
                break;

            case R.id.profile_detail_custom:

                Intent intent;
                if (mResultIntent.getExtras() != null) {
                    intent = mResultIntent;
                    intent.setClass(this, CustomProfileDetailActivity.class);
                    intent.putExtra(KEY_PROFILE_EDITMODE, true);
                } else {
                    if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
                        intent = getCustomProfileDataFormDB();
                        intent.setClass(this, CustomProfileDetailActivity.class);
                        intent.putExtra(KEY_PROFILE_EDITMODE, true);
                    } else {
                        intent = new Intent();
                        intent.setClass(this, CustomProfileDetailActivity.class);
                        intent.putExtra(KEY_PROFILE_EDITMODE, false);
                    }
                }

                intent.putExtra(
                        KEY_PROFILE_SYSTYPE,
                        getSelectedValue(mSystemType, null,
                                R.array.entryvalues_system_type));
                intent.putExtra(KEY_PROFILE_SYSNUMB, systemNumb);
                intent.putExtra(KEY_PROFILE_PASSWORD, password);
                intent.putExtra(KEY_PROFILE_PINCODE, pinCode);

                if (systemType.equals(Constants.SOBR_CHIP0103)
                        || systemType.equals(Constants.SOBR_G0103) 
                        || systemType.equals(Constants.SOBR_CHIP111213)) {
                    if (sysNumberValid()) {
                        startActivityForResult(intent, CUSTOM_SETTINGS_ID);
                    }
                } else {
                    if (sysNumberValid() && passwordValid()) {
                        startActivityForResult(intent, CUSTOM_SETTINGS_ID);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void openRegistrationScreen() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.URL, "http://location.sobr-a.ru/client-portal/?action=register");
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // boolean checked = ((CheckBox) v).isChecked();
        switch (buttonView.getId()) {
            case R.id.sobr_assist_show_text:
                if (isChecked) {
                    mSobrAssistPassword.setTransformationMethod(null);
                } else
                    mSobrAssistPassword
                            .setTransformationMethod(new PasswordTransformationMethod());
                break;
            case R.id.profile_password_show_text:
                if (isChecked) {
                    mPassword.setTransformationMethod(null);
                } else
                    mPassword
                            .setTransformationMethod(new PasswordTransformationMethod());
                break;
            case R.id.profile_unlock_code_show_text:
                if (isChecked) {
                    mUnlockCode.setTransformationMethod(null);
                } else
                    mUnlockCode
                            .setTransformationMethod(new PasswordTransformationMethod());
                break;
            case R.id.profile_pin_code_show_text:
                if (isChecked) {
                    mPinCode.setTransformationMethod(null);
                } else
                    mPinCode.setTransformationMethod(new PasswordTransformationMethod());
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        switch (parent.getId()) {
            case R.id.phone_status_spinner:

                break;

            case R.id.profile_systemtype_spinner:
                String typeValue = getSelectedValue(mSystemType, null,
                        R.array.entryvalues_system_type);

                if (typeValue.equals(Constants.SOBR_CHIP0103)
                        || typeValue.equals(Constants.SOBR_G0103)
                        || typeValue.equals(Constants.SOBR_CHIP111213)) {

                    mPassword.setText("");
                    mPassword.setVisibility(View.GONE);
                    findViewById(R.id.profile_password_title).setVisibility(
                            View.GONE);
                    findViewById(R.id.profile_password_show_text).setVisibility(
                            View.GONE);

                    mChangePass.setVisibility(View.GONE);

                    mSecondPhoneNumber.setText("");
                    findViewById(R.id.profile_second_phone_number_title)
                            .setVisibility(View.GONE);
                    findViewById(R.id.profile_second_phone_number_layout)
                            .setVisibility(View.GONE);

                    mThirdPhoneNumber.setText("");
                    findViewById(R.id.profile_third_phone_number_title)
                            .setVisibility(View.GONE);
                    findViewById(R.id.profile_third_phone_number_layout)
                            .setVisibility(View.GONE);
                    
                    mPhoneStatus.setVisibility(View.GONE);
                    findViewById(R.id.tvPhoneStatusTitle).setVisibility(View.GONE);

                } else {
                    mPassword.setVisibility(View.VISIBLE);
                    findViewById(R.id.profile_password_title).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.profile_password_show_text).setVisibility(
                            View.VISIBLE);

                    mChangePass.setVisibility(View.VISIBLE);

                    findViewById(R.id.profile_second_phone_number_title)
                            .setVisibility(View.VISIBLE);
                    findViewById(R.id.profile_second_phone_number_layout)
                            .setVisibility(View.VISIBLE);

                    findViewById(R.id.profile_third_phone_number_title)
                            .setVisibility(View.VISIBLE);
                    findViewById(R.id.profile_third_phone_number_layout)
                            .setVisibility(View.VISIBLE);
                    
                    mPhoneStatus.setVisibility(View.VISIBLE);
                    findViewById(R.id.tvPhoneStatusTitle).setVisibility(View.VISIBLE);
                }

                if (!(typeValue.equals(Constants.SOBR_GSM) || typeValue.equals(Constants.SOBR_GSM510))) {
                    mUnlockCode.setText("");
                    findViewById(R.id.profile_unlock_code_title).setVisibility(
                            View.GONE);
                    findViewById(R.id.profile_unlock_code_layout).setVisibility(
                            View.GONE);
                    findViewById(R.id.profile_unlock_code_show_text).setVisibility(
                            View.GONE);
                    findViewById(R.id.sobr_assist_title).setVisibility(
                            View.GONE);
                    findViewById(R.id.sobr_assist_small_text).setVisibility(
                            View.GONE);
                    findViewById(R.id.sobr_assist_login).setVisibility(
                            View.GONE);
                    findViewById(R.id.sobr_assist_password).setVisibility(
                            View.GONE);
                    findViewById(R.id.sobr_assist_show_text).setVisibility(
                            View.GONE);
                    findViewById(R.id.sobr_assist_registration).setVisibility(
                            View.GONE);
                } else {
                    findViewById(R.id.profile_unlock_code_title).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.profile_unlock_code_layout).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.profile_unlock_code_show_text).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.sobr_assist_title).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.sobr_assist_small_text).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.sobr_assist_login).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.sobr_assist_password).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.sobr_assist_show_text).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.sobr_assist_registration).setVisibility(
                            View.VISIBLE);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CUSTOM_SETTINGS_ID) {
            // if (DEBUG)Log.d(TAG, "onActivityResult: RESULT_OK");
            mResultIntent = data;

        }
    }

    private void saveProfile() {
        ContentValues contentValues = new ContentValues();
        String systemType = getSelectedValue(mSystemType, null,
                R.array.entryvalues_system_type);

        // Profile settings
        contentValues.put(SobrContract.Profiles.NAME, capitalize(mName
                .getText().toString()));
        contentValues.put(SobrContract.Profiles.SYSTEM_PHONE_NUMBER, mSysNumber
                .getText().toString());
        contentValues.put(SobrContract.Profiles.PASSWORD, mPassword.getText()
                .toString());
        contentValues.put(SobrContract.Profiles.UNLOCK_CODE, mUnlockCode
                .getText().toString());
        contentValues.put(SobrContract.Profiles.PIN_CODE, mPinCode.getText()
                .toString());
        contentValues.put(SobrContract.Profiles.PIN_CODE_ON_BOOT, ""
                + mPinCodeOnBoot.isChecked());
        contentValues.put(SobrContract.Profiles.BASE_PHONE_NUMBER,
                mBasePhoneNumber.getText().toString());
        contentValues.put(SobrContract.Profiles.SECOND_PHONE_NUMBER,
                mSecondPhoneNumber.getText().toString());
        contentValues.put(SobrContract.Profiles.THIRD_PHONE_NUMBER,
                mThirdPhoneNumber.getText().toString());
        contentValues.put(SobrContract.Profiles.BALANCE_THRESHOLD,
                mBalanceThreshold.getText().toString());
        contentValues.put(SobrContract.Profiles.BALANCE_QUERY_CODE,
                mBalanceQueryCode.getText().toString());
        contentValues.put(SobrContract.Profiles.SOBR_ASSIST_LOGIN,
                mSobrAssistLogin.getText().toString());
        contentValues.put(SobrContract.Profiles.SOBR_ASSIST_PASSWORD,
                mSobrAssistPassword.getText().toString());

        contentValues.put(SobrContract.Profiles.SYSTEM_TYPE, systemType);
        contentValues.put(
                SobrContract.Profiles.PHONE_STATUS,
                getSelectedValue(mPhoneStatus, null,
                        R.array.entryvalues_phone_status));

        { // By default
            if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
                if (!mTempSystemType.equals(systemType)) {

                    if (systemType.equals(Constants.SOBR_GSM)) {
                        contentValues = DefaultProfiles.sobrGsm(contentValues);

                    } else if (systemType.equals(Constants.SOBR_DOMONLINE)) {
                        contentValues = DefaultProfiles
                                .sobrDomonline(contentValues);

                    } else if (systemType.equals(Constants.SOBR_CHIP0103)) {
                        contentValues = DefaultProfiles
                                .sobrChip0103(contentValues);

                    } else if (systemType.equals(Constants.SOBR_G0103) ||
                    		systemType.equals(Constants.SOBR_CHIP111213)) {
                        contentValues = DefaultProfiles
                                .sobrG0103(contentValues);

                    } else if (systemType.equals(Constants.SOBR_GSM510)) {
                        contentValues = DefaultProfiles
                                .sobrGsm510(contentValues);

                    } else {
                        contentValues = DefaultProfiles.sobrGsm(contentValues);
                    }

                }
            } else {
                // if (DEBUG)Log.d(TAG, "Custom settings fields - Default");

                if (systemType.equals(Constants.SOBR_GSM)) {
                    contentValues = DefaultProfiles.sobrGsm(contentValues);

                } else if (systemType.equals(Constants.SOBR_DOMONLINE)) {
                    contentValues = DefaultProfiles
                            .sobrDomonline(contentValues);

                } else if (systemType.equals(Constants.SOBR_CHIP0103)) {
                    contentValues = DefaultProfiles.sobrChip0103(contentValues);

                } else if (systemType.equals(Constants.SOBR_G0103) ||
                		systemType.equals(Constants.SOBR_CHIP111213)) {
                    contentValues = DefaultProfiles.sobrG0103(contentValues);
                } else if (systemType.equals(Constants.SOBR_GSM510)) {
                    contentValues = DefaultProfiles.sobrGsm510(contentValues);

                } else {
                    contentValues = DefaultProfiles.sobrGsm(contentValues);
                }

            }
        }

        // Custom profile settings
        if (mResultIntent.getExtras() != null) {
            // if (DEBUG)Log.d(TAG, "Custom settings fields - SAVE NEW");

            contentValues.put(SobrContract.Profiles.COMMAND_123, mResultIntent
                    .getExtras().getString("command123"));
            contentValues.put(SobrContract.Profiles.COMMAND_456, mResultIntent
                    .getExtras().getString("command456"));
            contentValues.put(SobrContract.Profiles.COMMAND_789, mResultIntent
                    .getExtras().getString("command789"));
            contentValues.put(SobrContract.Profiles.COMMAND_666, mResultIntent
                    .getExtras().getString("command666"));
            contentValues.put(SobrContract.Profiles.COMMAND_777, mResultIntent
                    .getExtras().getString("command777"));
            contentValues.put(SobrContract.Profiles.COMMAND_999, mResultIntent
                    .getExtras().getString("command999"));
            contentValues.put(SobrContract.Profiles.COMMAND_09, mResultIntent
                    .getExtras().getString("command09"));
            contentValues.put(SobrContract.Profiles.COMMAND_911, mResultIntent
                    .getExtras().getString("command911"));
            if (mResultIntent.getExtras().getString("command123Title") != null) {
                contentValues.put(SobrContract.Profiles.COMMAND_123_TITLE, mResultIntent
                        .getExtras().getString("command123Title"));
            }
            if (mResultIntent.getExtras().getString("command456Title") != null) {
                contentValues.put(SobrContract.Profiles.COMMAND_456_TITLE, mResultIntent
                        .getExtras().getString("command456Title"));
            }
            if (mResultIntent.getExtras().getString("command789Title") != null) {
                contentValues.put(SobrContract.Profiles.COMMAND_789_TITLE, mResultIntent
                        .getExtras().getString("command789Title"));
            }
            if (mResultIntent.getExtras().getString("command777Title") != null) {
                contentValues.put(SobrContract.Profiles.COMMAND_777_TITLE, mResultIntent
                        .getExtras().getString("command777Title"));
            }
            if (mResultIntent.getExtras().getString("command999Title") != null) {
                contentValues.put(SobrContract.Profiles.COMMAND_999_TITLE, mResultIntent
                        .getExtras().getString("command999Title"));
            }
            if (mResultIntent.getExtras().getString("command911Title") != null) {
                contentValues.put(SobrContract.Profiles.COMMAND_911_TITLE, mResultIntent
                        .getExtras().getString("command911Title"));
            }

            contentValues.put(SobrContract.Profiles.GPS_RECEIVER, mResultIntent
                    .getExtras().getString("gps_receiver"));
            contentValues.put(SobrContract.Profiles.REPORT_ON_MOVE,
                    mResultIntent.getExtras().getString("report_on_move"));
            contentValues.put(SobrContract.Profiles.SHOCK_SENSOR, mResultIntent
                    .getExtras().getString("shock_sensor"));
            contentValues.put(SobrContract.Profiles.IMMOBILIZER, mResultIntent
                    .getExtras().getString("immobilizer"));
            contentValues.put(SobrContract.Profiles.FIFTH_PHONE_NUMBER, 
            		mResultIntent.getExtras().getString(SobrContract.Profiles.FIFTH_PHONE_NUMBER));
            contentValues.put(SobrContract.Profiles.GSM510_WORK_MODE, 
            		mResultIntent.getExtras().getString(SobrContract.Profiles.GSM510_WORK_MODE));
            contentValues.put(SobrContract.Profiles.PREHEATER, Boolean.toString(
            		mResultIntent.getExtras().getBoolean(SobrContract.Profiles.PREHEATER)));
            contentValues.put(SobrContract.Profiles.CHANELS, 
            		mResultIntent.getExtras().getInt(SobrContract.Profiles.CHANELS));
            contentValues.put(SobrContract.Profiles.CMD1, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.CMD1));
            contentValues.put(SobrContract.Profiles.KEY1, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.KEY1));
            contentValues.put(SobrContract.Profiles.CMD2, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.CMD2));
            contentValues.put(SobrContract.Profiles.KEY2, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.KEY2));
            contentValues.put(SobrContract.Profiles.CMD3, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.CMD3));
            contentValues.put(SobrContract.Profiles.KEY3, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.KEY3));
            contentValues.put(SobrContract.Profiles.CMD4, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.CMD4));
            contentValues.put(SobrContract.Profiles.KEY4, mResultIntent
                    .getExtras().getString(SobrContract.Profiles.KEY4));
            contentValues.put(SobrContract.Profiles.ALARM, Boolean.toString(
            		mResultIntent.getExtras().getBoolean(SobrContract.Profiles.ALARM)));
        }


        try {
            if (getIntent().getAction().equals(Intent.ACTION_INSERT)) {
                getContentResolver().insert(getIntent().getData(),
                        contentValues);
            } else if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
                getContentResolver().update(getIntent().getData(),
                        contentValues, null, null);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }

    private void deleteProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.profile_delete);
        builder.setMessage(getString(R.string.profile_delete_message));

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(getIntent().getData(),
                                null, null);
                        ProfileDetailActivity.this.finish();
                    }
                });

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private Intent getCustomProfileDataFormDB() {
        Intent data = new Intent();
        try {
            Cursor cursor = getContentResolver().query(getIntent().getData(),
                    null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_123_TITLE))) {
                        data.putExtra(
                                "command123Title",
                                cursor.getString(cursor
                                        .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_123_TITLE)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_456_TITLE))) {
                        data.putExtra(
                                "command456Title",
                                cursor.getString(cursor
                                        .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_456_TITLE)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_789_TITLE))) {
                        data.putExtra(
                                "command789Title",
                                cursor.getString(cursor
                                        .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_789_TITLE)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_777_TITLE))) {
                        data.putExtra(
                                "command777Title",
                                cursor.getString(cursor
                                        .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_777_TITLE)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_999_TITLE))) {
                        data.putExtra(
                                "command999Title",
                                cursor.getString(cursor
                                        .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_999_TITLE)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_911_TITLE))) {
                        data.putExtra(
                                "command911Title",
                                cursor.getString(cursor
                                        .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_911_TITLE)));
                    }

                    data.putExtra(
                            "command123",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_123)));

                    data.putExtra(
                            "command456",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_456)));

                    data.putExtra(
                            "command789",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_789)));

                    data.putExtra(
                            "command666",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_666)));

                    data.putExtra(
                            "command777",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_777)));

                    data.putExtra(
                            "command999",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_999)));

                    data.putExtra(
                            "command09",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_09)));

                    data.putExtra(
                            "command911",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.COMMAND_911)));

                    data.putExtra(
                            "gps_receiver",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.GPS_RECEIVER)));

                    data.putExtra(
                            "report_on_move",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.REPORT_ON_MOVE)));

                    data.putExtra(
                            "shock_sensor",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.SHOCK_SENSOR)));

                    data.putExtra(
                            "immobilizer",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.IMMOBILIZER)));
                    
                    data.putExtra(SobrContract.Profiles.GSM510_WORK_MODE, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.GSM510_WORK_MODE)));
                    
                    data.putExtra(SobrContract.Profiles.FIFTH_PHONE_NUMBER, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.FIFTH_PHONE_NUMBER)));
                    data.putExtra(SobrContract.Profiles.PREHEATER, 
                    		Boolean.parseBoolean(
                    				cursor.getString(cursor
                                            .getColumnIndexOrThrow(SobrContract.Profiles.PREHEATER))));
                    data.putExtra(SobrContract.Profiles.CHANELS, 
                    		cursor.getInt(cursor.getColumnIndexOrThrow(SobrContract.Profiles.CHANELS)));
                    data.putExtra(SobrContract.Profiles.CMD1, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.CMD1)));
                    data.putExtra(SobrContract.Profiles.KEY1, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.KEY1)));
                    data.putExtra(SobrContract.Profiles.CMD2, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.CMD2)));
                    data.putExtra(SobrContract.Profiles.KEY2, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.KEY2)));
                    data.putExtra(SobrContract.Profiles.CMD3, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.CMD3)));
                    data.putExtra(SobrContract.Profiles.KEY3, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.KEY3)));
                    data.putExtra(SobrContract.Profiles.CMD4, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.CMD4)));
                    data.putExtra(SobrContract.Profiles.KEY4, 
                    		cursor.getString(cursor
                                    .getColumnIndexOrThrow(SobrContract.Profiles.KEY4)));
                    data.putExtra(SobrContract.Profiles.ALARM, 
                    		Boolean.parseBoolean(
                    				cursor.getString(cursor
                                            .getColumnIndexOrThrow(SobrContract.Profiles.ALARM))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
        }

        return data;
    }

    protected void enableButtons() {
        findViewById(R.id.profile_change_password_btn).setEnabled(true);
        findViewById(R.id.profile_unlock_code_btn).setEnabled(true);
        findViewById(R.id.profile_pin_code_btn).setEnabled(true);
        findViewById(R.id.profile_base_phone_number_btn).setEnabled(true);
        findViewById(R.id.profile_second_phone_number_btn).setEnabled(true);
        findViewById(R.id.profile_third_phone_number_btn).setEnabled(true);
        findViewById(R.id.profile_max_balance_btn).setEnabled(true);
        findViewById(R.id.profile_balance_query_code_btn).setEnabled(true);
        findViewById(R.id.profile_get_settings_info).setEnabled(true);
        findViewById(R.id.profile_detail_custom).setEnabled(true);
    }

    private void showChangePassDialog() {
        AlertDialog dialog;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(
                R.layout.dialog_profile_password, null, false);

        final EditText pass = (EditText) layout
                .findViewById(R.id.dialog_password_text);
        pass.setText(mPassword.getText().toString());
        final EditText newPass = (EditText) layout
                .findViewById(R.id.dialog_new_password_text);
        newPass.requestFocus();

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = new AlertDialog.Builder(this);
        } else {
            ContextThemeWrapper ctw = new ContextThemeWrapper(this,
                    R.style.MyTheme);
            builder = new HoloAlertDialogBuilder(ctw);
        }
        builder.setTitle(R.string.profile_change_password);
        builder.setView(layout);

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Проверка старого пароля
                        // EditText pass = (EditText)
                        // layout.findViewById(R.id.dialog_password_text);
                        // if(pass.getText().toString().equals(mPassword.getText().toString())){

                        EditText newPassConf = (EditText) layout
                                .findViewById(R.id.dialog_new_password_confirm_text);
                        // Проверка полей нового пароля
                        if (!TextUtils.isEmpty(newPass.getText())
                                && !TextUtils.isEmpty(newPassConf.getText())
                                && !newPass.getText().toString()
                                .equals(pass.getText().toString())
                                && newPass
                                .getText()
                                .toString()
                                .equals(newPassConf.getText()
                                        .toString())) {

                            // Отправка SMS
                            SobrGsm.profileChangePass(
                                    ProfileDetailActivity.this, mSysNumber
                                    .getText().toString(), pass
                                    .getText().toString(), newPass
                                    .getText().toString());
                            // Диалог с предупреждением
                            AlertDialog.Builder adb = new AlertDialog.Builder(
                                    ProfileDetailActivity.this);
                            adb.setMessage(
                                    R.string.profile_dialog_chenge_pass_warning_text)
                                    .setTitle(
                                            R.string.profile_dialog_chenge_pass_warning_title);
                            adb.setPositiveButton(android.R.string.ok, null);
                            AlertDialog warning = adb.create();
                            warning.show();
                            mPassword.setText(newPass.getText().toString());

                        } else {

                            Toast.makeText(ProfileDetailActivity.this,
                                    R.string.profile_dialog_new_password_error,
                                    Toast.LENGTH_LONG).show();
                            showChangePassDialog();

                        }

                    }
                });

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialog = builder.create();
        dialog.show();

    }

    private void showChangePinCodeDialog() {
        AlertDialog dialog;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(
                R.layout.dialog_profile_pincode, null, false);

        final EditText pincode = (EditText) layout
                .findViewById(R.id.dialog_pincode_text);
        pincode.setText(mPinCode.getText().toString());
        final EditText newPin = (EditText) layout
                .findViewById(R.id.dialog_new_pincode_text);
        newPin.requestFocus();

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = new AlertDialog.Builder(this);
        } else {
            ContextThemeWrapper ctw = new ContextThemeWrapper(this,
                    R.style.MyTheme);
            builder = new HoloAlertDialogBuilder(ctw);
        }
        builder.setTitle(R.string.profile_pin_code);
        builder.setView(layout);

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Проверка старого PIN-кода
                        // EditText pincode = (EditText)
                        // layout.findViewById(R.id.dialog_pincode_text);
                        // if(pass.getText().toString().equals(mPassword.getText().toString())){

                        EditText newPinConf = (EditText) layout
                                .findViewById(R.id.dialog_new_pincode_confirm_text);

                        // Проверка полей нового PIN-кода
                        if (!TextUtils.isEmpty(newPin.getText())
                                && !TextUtils.isEmpty(newPinConf.getText())
                                && !newPin.getText().toString()
                                .equals(pincode.getText().toString())
                                && newPin
                                .getText()
                                .toString()
                                .equals(newPinConf.getText().toString())) {

                            // Отправка SMS
                            SobrGsm.profilePinCodeType3(
                                    ProfileDetailActivity.this, mSysNumber
                                    .getText().toString(), pincode
                                    .getText().toString(), newPin
                                    .getText().toString());

                            mPinCode.setText(newPin.getText().toString());

                        } else {

                            Toast.makeText(ProfileDetailActivity.this,
                                    R.string.profile_dialog_new_pincode_error,
                                    Toast.LENGTH_LONG).show();
                            showChangePinCodeDialog();

                        }

                    }
                });

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private class InnerSmsStatusReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null
                    && intent.getAction().equals(SobrGsm.ACTION_SMS_SENT)) {
                enableButtons();
            }
        }
    }

    protected String getSelectedValue(Spinner command, EditText customCommand,
                                      int commandEntryvalues) {
        String itemData = "";
        if ((command.getSelectedItemPosition() + 1) != command.getCount()) {
            String commandValues[] = getResources().getStringArray(
                    commandEntryvalues);
            itemData = commandValues[command.getSelectedItemPosition()];
        } else {
            if (customCommand != null) {
                itemData = customCommand.getText().toString();
            } else {
                String command123Values[] = getResources().getStringArray(
                        commandEntryvalues);
                itemData = command123Values[command.getSelectedItemPosition()];
            }
        }
        // if (DEBUG)Log.d(TAG, "itemData - " + itemData);
        return itemData;
    }

    protected int getValuePosition(String commandValue, int commandEntryvalues) {
        String commandValues[] = getResources().getStringArray(
                commandEntryvalues);
        int length = commandValues.length;
        int itemNumb = length - 1;
        for (int i = 0; i < length; i++) {
            if (commandValues[i].equals(commandValue)) {
                itemNumb = i;
                break;
            }
        }
        // if (DEBUG)Log.d(TAG, "itemNumb " + itemNumb);
        return itemNumb;
    }

    public String toUpperCase(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public String capitalize(String str) {
        String[] brokenString = str.split(" ");
        String newString = "";

        for (String s : brokenString) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            newString = newString + new String(chars) + " ";
        }

        return newString.trim();
    }

    private boolean profileNameValid() {
        boolean valid = !TextUtils.isEmpty(mName.getText());

        if (valid) {
            ((TextView) findViewById(R.id.profile_name_title))
                    .setTextColor(Color.BLACK);
        } else {
            ((TextView) findViewById(R.id.profile_name_title))
                    .setTextColor(Color.RED);
            Toast.makeText(this, R.string.profile_toast_profilename_error,
                    Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private boolean basePhoneNumbValid() {
        boolean valid = !TextUtils.isEmpty(mBasePhoneNumber.getText());

        if (valid) {
            ((TextView) findViewById(R.id.profile_base_phone_number_title))
                    .setTextColor(Color.BLACK);
        } else {
            ((TextView) findViewById(R.id.profile_base_phone_number_title))
                    .setTextColor(Color.RED);
            Toast.makeText(this, R.string.profile_toast_basephonenumb_error,
                    Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private boolean pinCodeValid() {
        boolean valid = !TextUtils.isEmpty(mPinCode.getText())
                && mPinCode.getText().toString().length() == 4;

        if (valid) {
            ((TextView) findViewById(R.id.profile_pin_code_title))
                    .setTextColor(Color.BLACK);
        } else {
            ((TextView) findViewById(R.id.profile_pin_code_title))
                    .setTextColor(Color.RED);
            Toast.makeText(this, R.string.profile_toast_pincode_error,
                    Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private boolean unlockCodeValid() {
        boolean valid = !TextUtils.isEmpty(mUnlockCode.getText())
                && mUnlockCode.getText().toString().length() == 3;

        if (valid) {
            ((TextView) findViewById(R.id.profile_unlock_code_title))
                    .setTextColor(Color.BLACK);
        } else {
            ((TextView) findViewById(R.id.profile_unlock_code_title))
                    .setTextColor(Color.RED);
            Toast.makeText(this, R.string.profile_toast_unlockcode_error,
                    Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private boolean sysNumberValid() {

        boolean valid = !TextUtils.isEmpty(mSysNumber.getText());

        if (valid) {
            ((TextView) findViewById(R.id.profile_phone_number_title))
                    .setTextColor(Color.BLACK);
        } else {
            ((TextView) findViewById(R.id.profile_phone_number_title))
                    .setTextColor(Color.RED);
            Toast.makeText(this, R.string.profile_toast_sysnumber_error,
                    Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private boolean passwordValid() {
        boolean valid = !TextUtils.isEmpty(mPassword.getText())
                && mPassword.getText().toString().length() == 5;

        if (valid) {
            ((TextView) findViewById(R.id.profile_password_title))
                    .setTextColor(Color.BLACK);
        } else {
            ((TextView) findViewById(R.id.profile_password_title))
                    .setTextColor(Color.RED);
            Toast.makeText(this, R.string.profile_toast_password_error,
                    Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private String clearPhoneNumber(String number) {
        String cleanNumb = number;
        cleanNumb = cleanNumb.replaceAll("[^A-Za-z0-9]", "");
        // if (DEBUG) Log.d(TAG, "String - "+ cleanNumb);
        return cleanNumb;
    }

}