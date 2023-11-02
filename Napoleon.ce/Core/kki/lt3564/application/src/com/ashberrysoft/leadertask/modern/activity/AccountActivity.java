package com.ashberrysoft.leadertask.modern.activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Entity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.Config;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.data_providers.network.ChangeUserPassword;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.enums.LeaderTaskLanguage;
import com.ashberrysoft.leadertask.fragments.EditEmpFragment;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.ashberrysoft.leadertask.application.Config.IN_APP_DAYS;
import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID;
import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID_UUID;
import static com.ashberrysoft.leadertask.application.Config.NETWROK_BUY_LEADERTASK;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener, OnEditorActionListener {

    private Toolbar mToolbar;
    private TextView mTitleToolbar;
    private LinearLayout accIProfile;
    private Button accItemChangeUser;
    private LinearLayout main_container;
    private LinearLayout bg_container;
    private LinearLayout profile_container;
    private Button accLicenseButton;
    ImageView mImagePremium;
    private Button profileSave;
    private TextView accLicenseText;
    private TextView accTitleContLicense;
    private TextView accCountLicense;
    private TextView acc_license_main_user;
    private TextView acc_license_org;
    private TextView acc_license_server_size;
    private TextView accLicenseText_title;
    private TextView acc_license_main_user_title;
    private TextView acc_license_org_title;
    private TextView acc_license_server_size_title;
    private TextView profile_name;
    private EditText profile_name_edit;
    private EditText profile_phone;
    private EditText account_old_password;
    private EditText account_new_password;
    private EditText account_new_password_again;
    private TextView profile_email;
    private TextView profileChangePassword;
    private boolean inProfile = false;
    ImageView mImage;
    LTSettings mSettings;
    private File mTempFile;
    private boolean fotoChanges = false;
    private Emp mEmp;
    private IInAppBillingService mBillingService;
    private ServiceConnection mConnection;

    private String mAmount = "";
    private String mCurrency = "";
    private String mPackageName = "";
    private String mProductId = "";
    private String mPurchaseToken = "";
    private String mSignature = "";
    private String mPurchaseData = "";

    public static Intent newInstance(Context context) {
        final Intent intent = new Intent(context, AccountActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PreCreateActivityParamsHelper.setActivityParams(this);
        super.onCreate(savedInstanceState);
        mSettings = LTSettings.getInstance();
        setContentView(R.layout.account_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleToolbar = (TextView) findViewById(R.id.toolbar_text_name);
        mTitleToolbar.setText(R.string.account);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        accIProfile = (LinearLayout) findViewById(R.id.profile);
        accItemChangeUser = (Button) findViewById(R.id.acc_item_change_user);
        main_container = (LinearLayout) findViewById(R.id.main_container);
        bg_container = (LinearLayout) findViewById(R.id.bg_account);
        profile_container = (LinearLayout) findViewById(R.id.profile_container);
        accLicenseButton = (Button) findViewById(R.id.acc_license_button);
        accLicenseButton.setVisibility(mSettings.getSyncNamespace().equals(Config.SOAP_NAMESPACE_DEFAULT) ? View.VISIBLE : View.GONE);
        profileSave = (Button) findViewById(R.id.profile_save);

        accLicenseText = (TextView) findViewById(R.id.acc_license_text);
        accLicenseText_title = (TextView) findViewById(R.id.acc_license_text_title);
        accCountLicense = (TextView) findViewById(R.id.acc_cont_emps);
        accTitleContLicense = (TextView) findViewById(R.id.acc_text_cont_emps_title);
        acc_license_main_user = (TextView) findViewById(R.id.acc_license_main_user);
        acc_license_main_user_title = (TextView) findViewById(R.id.acc_license_main_user_title);
        acc_license_org = (TextView) findViewById(R.id.acc_license_org);
        acc_license_org_title = (TextView) findViewById(R.id.acc_license_org_title);
        acc_license_server_size = (TextView) findViewById(R.id.acc_license_server_size);
        acc_license_server_size_title = (TextView) findViewById(R.id.acc_license_server_size_title);

        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_name_edit = (EditText) findViewById(R.id.profile_name_edit);
        profile_phone = (EditText) findViewById(R.id.profile_phone);
        account_old_password = (EditText) findViewById(R.id.account_old_password);
        account_new_password = (EditText) findViewById(R.id.account_new_password);
        account_new_password_again = (EditText) findViewById(R.id.account_new_password_again);
        profile_email = (TextView) findViewById(R.id.profile_email);
        profileChangePassword = (TextView) findViewById(R.id.profile_change_password);
        mImage = (ImageView) findViewById(R.id.image_view);
        mImagePremium = (ImageView) findViewById(R.id.premium);

        acc_license_main_user.setText(EmployeeCache.getInstance(this).find(mSettings.getUserName()));
        profile_name.setText(EmployeeCache.getInstance(this).find(mSettings.getUserName()));
        profile_name_edit.setText(EmployeeCache.getInstance(this).find(mSettings.getUserName()));

        profile_email.setText(mSettings.getUserName());

        resetFoto(mSettings.getUserName());

        setLicenseParams();

        //
        if (mSettings.getVerifyKey() == "") {
            accLicenseButton.setText(getString(R.string.to_premium));
            bg_container.setBackgroundColor(getResources().getColor(R.color.login_background));
            profileChangePassword.setBackground(getResources().getDrawable(R.drawable.selector_menu_item));
            mImagePremium.setVisibility(View.GONE);
        } else {
            accLicenseButton.setText(getString(R.string.extend));
            bg_container.setBackgroundColor(getResources().getColor(R.color.premium_color));
            profileChangePassword.setBackgroundColor(getResources().getColor(R.color.premium_color));
            mImagePremium.setVisibility(View.VISIBLE);
        }
        accLicenseButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_bg_btn_orange));
        //

        accIProfile.setOnClickListener(this);
        accItemChangeUser.setOnClickListener(this);
        accLicenseButton.setOnClickListener(this);
        profileSave.setOnClickListener(this);
        profileChangePassword.setOnClickListener(this);

        profile_name_edit.setOnEditorActionListener(this);
        profile_phone.setOnEditorActionListener(this);
        account_new_password_again.setOnEditorActionListener(this);

        List<Emp> emps = DbHelper.getListEmps(this);
        for (Emp temp: emps) {
            if (temp.getLogin().equals(mSettings.getUserName())) {
                mEmp = temp;
                break;
            }
        }

        if (mEmp != null && mEmp.getPhone() != null) {
            int index = mEmp.getPhone().indexOf(" (TimeZone");
            String phone;
            if (index == -1) {
                phone = mEmp.getPhone();
            } else {
                phone = mEmp.getPhone().substring(0, index);
            }
            profile_phone.setText(phone);
        }

        final boolean hasCustomLocale = mSettings.getLanguageLocale() != null;
        final Locale appLocale = hasCustomLocale ?  mSettings.getLanguageLocale() : Locale.getDefault();
        if (appLocale.getLanguage().equals("ru")) {
            profile_phone.setVisibility(View.VISIBLE);
        } else {
            profile_phone.setVisibility(View.GONE);
        }

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
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void resetFoto(String fileName) {
        RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFullFotoBitmapFromFolder((LTApplication) this.getApplicationContext(), fileName);
        if (roundedBitmapDrawable != null) {
            mImage.setImageDrawable(roundedBitmapDrawable);
        } else {
            if (fileName.equals(Utils.TMP_FOTO_FILE_NAME)) {
                roundedBitmapDrawable = Utils.getFullFotoBitmapFromFolder((LTApplication) this.getApplicationContext(), mSettings.getUserName());
                if (roundedBitmapDrawable != null) {
                    mImage.setImageDrawable(roundedBitmapDrawable);
                } else {
                    mImage.setImageResource(R.drawable.emp_simple);
                }
            } else {
                mImage.setImageResource(R.drawable.emp_simple);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!inProfile) {
            super.onBackPressed();
        } else {
            openProfile(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1001) {
            mPurchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    //
                    try {
                        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                        mSignature = dataSignature;
                    } catch (Exception e) {

                    }
                    //
                    JSONObject jo = new JSONObject(mPurchaseData);
                    mPackageName = jo.getString("packageName");
                    mProductId = jo.getString("productId");
                    mPurchaseToken = jo.getString("purchaseToken");

                    buyInWebreg();

                } catch (JSONException e) {
                    android.util.Log.v("Tedorius",e.getMessage());
                }
            }
        } else {
            if (data == null && mTempFile != null) {
                data = new Intent();
                data.setData(Uri.parse(SharedStrings.CONTENT_FILE + mTempFile.getAbsolutePath()));
                mTempFile = null;
            }

            if (data != null && data.getData() != null) {
                // скопировать с темп именем
                boolean error = false;

                try {
                    final String path = Utils.getRealPathFromURI((LTApplication) getApplicationContext(), data.getData());
                    if (path == null) {
                        return;
                    }

                    Utils.exifRotate(path);

                    final File file = Utils.FileWorker.copyEmpFotoFile(path, ((LTApplication) getApplicationContext()).getAppFolder());

                } catch (Exception e) {
                    error = true;
                } finally {
                    if (error) {
                        resetFoto(mSettings.getUserName());
                    } else {
                        resetFoto(Utils.TMP_FOTO_FILE_NAME); // и брать ресур фотки с него
                        fotoChanges = true;
                    }
                }
            }
        }
    }

    private void buyInWebreg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("email", LTSettings.getInstance().getUserName()));
                    nameValuePairs.add(new BasicNameValuePair("packagename", mPackageName));
                    nameValuePairs.add(new BasicNameValuePair("productid", IN_APP_ID_UUID));  // сюда пихать уиды хронометраж или покупка ЛТ
                    nameValuePairs.add(new BasicNameValuePair("token", mPurchaseToken));
                    nameValuePairs.add(new BasicNameValuePair("language", Locale.getDefault().getLanguage()));
                    nameValuePairs.add(new BasicNameValuePair("currency", mCurrency));
                    nameValuePairs.add(new BasicNameValuePair("amount", mAmount));
                    long endDate = mSettings.getVerifyEndDateInLong();
                    if (endDate < 0 || endDate < TimeHelper.currentTimeMillisWithoutTimeZone()) {
                        endDate = TimeHelper.currentTimeMillisWithoutTimeZone();
                    }
                    nameValuePairs.add(new BasicNameValuePair("days", TimeHelper.getInstance().getIntDifferencesDateInDays(endDate, TimeHelper.currentTimeMillisWithoutTimeZone())+IN_APP_DAYS+""));
                    nameValuePairs.add(new BasicNameValuePair("signature", mSignature));
                    nameValuePairs.add(new BasicNameValuePair("full_purchase", mPurchaseData));

                    String message = OkHttpConnection.postWithParams(nameValuePairs, NETWROK_BUY_LEADERTASK);

                    if (message.equals("OK")) {
                        AccountActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Utils.showToast(AccountActivity.this, "Покупка прошла успешно!");
                            }
                        });
                        Utils.startSync(((LTApplication) getApplicationContext()));
                    } else {
                        // ошибка
                        AccountActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Utils.showToast(AccountActivity.this, getResources().getString(R.string.exception_unknown));
                            }
                        });
                    }
                } catch (Exception e) {
                    AccountActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Utils.showToast(AccountActivity.this, getResources().getString(R.string.exception_unknown));
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile:
                // редактировать профиль
                if (!inProfile) {
                    openProfile(true);
                } else {
                    final AlertDialog.Builder ad = new AlertDialog.Builder(AccountActivity.this);
                    ad.setCancelable(true);
                    ad.setMessage(R.string.d_title_pick_an_image_source);
                    ad.setNegativeButton(R.string.m_add_from_camera, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imageCapture();
                            dialog.cancel();
                        }
                    });
                    ad.setPositiveButton(R.string.choose_new_file, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            final Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(SharedStrings.MIME_TYPE_IMAGE);
                            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.title_chooser_image)), 1);
                        }
                    });

                    ad.show();
                }
                break;

            case R.id.acc_item_change_user:
                // сменить пользователя диалог
                final AlertDialog.Builder ad = new AlertDialog.Builder(AccountActivity.this);
                ad.setCancelable(true);
                ad.setTitle(R.string.settings_confirmation);
                ad.setMessage(R.string.settings_logout);
                ad.setPositiveButton(R.string.btn_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetData(true);
                        mSettings.setLoginAfterRegistration(false);
                    }
                });
                ad.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = ad.create();
                alert.show();
                break;

            case R.id.acc_license_button:
                // купить/продлить лицензию
                openBuy();
                break;

            case R.id.profile_change_password:
                profile_name_edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                profileChangePassword.setVisibility(View.GONE);
                account_old_password.setVisibility(View.VISIBLE);
                account_new_password.setVisibility(View.VISIBLE);
                account_new_password_again.setVisibility(View.VISIBLE);

                profile_name_edit.clearFocus();
                Utils.showInput(account_old_password);
                account_old_password.requestFocus();
                break;

            case R.id.profile_save:
                saveAccount();
                break;
            default:
                break;
        }
    }

    private void saveAccount() {
        //Проверка на изменение пароля
        if (profileChangePassword.getVisibility() == View.GONE) {
            //есди до этого мы нажали кнопку с изменением пароля
            String old_password = account_old_password.getText().toString().trim();
            final String new_password = account_new_password.getText().toString().trim();
            String new_password_again = account_new_password_again.getText().toString().trim();

            if (!new_password.isEmpty() && !new_password_again.isEmpty()) {
                if (!old_password.equals(mSettings.getUserProfile().getPassword())) {
                    Toast.makeText(AccountActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!new_password.equals(new_password_again)) {
                        Toast.makeText(AccountActivity.this, "Новый пароль не совпадает", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        //все совпадает
                        if (new_password.length() < 8) {
                            Toast.makeText(AccountActivity.this, "Пароль должен быть не менее 8 символов", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            if (old_password.equals(new_password)) {
                                Toast.makeText(AccountActivity.this, "Новый пароль должен отличаться от старого", Toast.LENGTH_SHORT).show();
                            } else {
                                // все збс
                                final LeaderTaskUser user = mSettings.getUserProfile();
                                Thread thread = new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            new ChangeUserPassword(getApplicationContext(), user, new_password).execute(null);
                                        } catch (Exception e) {
                                            //Toast.makeText(AccountActivity.this, "Произошла ошибка при смене пароля", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                thread.start();
                            }
                            //
                        }
                    }
                }
            }
        }

        //
        if (mEmp != null) {
            String newUserName = profile_name_edit.getText().toString().trim();
            String newPhone = profile_phone.getText().toString().trim();
            profile_name.setText(newUserName);
            if (mSettings.getVerifyEmailDirector().equals(mSettings.getUserName())) {
                if (newUserName.length() == 0) {
                    acc_license_main_user.setText(EmployeeCache.getInstance(this).find(mSettings.getUserName()));
                } else {
                    acc_license_main_user.setText(newUserName+" ("+mSettings.getVerifyEmailDirector()+")");
                }

            }

            final ContentValues cv = new ContentValues();
            cv.put(EmpContract.USN_ENTITY, 0);
            cv.put(EmpContract.TITLE, profile_name_edit.getText().toString().trim());
            cv.put(EmpContract.USN_FIELD_TITLE, mEmp.getUsnFieldTitle() + 1);

            if (!newPhone.isEmpty()) {
                int zoneInt = TimeZone.getDefault().getRawOffset() / 60 / 60 / 1000;
                String zone = "" + (zoneInt > 0 ? "+" + zoneInt : "" + zoneInt);
                if (newPhone != null && !newPhone.isEmpty()) {
                    newPhone = newPhone + " (TimeZone: " + zone + ")";
                }
                cv.put(EmpContract.PHONE, newPhone);
                cv.put(EmpContract.USN_FIELD_PHONE, mEmp.getUsnFieldPhone() + 1);
            }
            if (fotoChanges) {
                //save new foto
                final File src = new File(((LTApplication) getApplicationContext()).getAppFolder(), mSettings.getUserName());
                final File dst = new File(((LTApplication) getApplicationContext()).getAppFolder(), Utils.TMP_FOTO_FILE_NAME);

                if (dst != null && dst.exists()) {
                    try {
                        Utils.FileWorker.copyFile(dst, src);
                        cv.put(LeaderTaskProviderMetaData.EmpContract.USN_FIELD_FOTO, mEmp.getUsnFieldFoto() + 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        clearCacheFoto(mSettings.getUserName());
                        clearCacheFoto(dst.getName());
                        dst.delete();
                    }
                }
                //
                MenuLoader.getInstance(this).resetMyFoto();
            }
            ((LTApplication) getApplicationContext()).getContentResolver()//
                    .update(EmpContract.CONTENT_URI, cv, EmpContract.selectionUid(mEmp.getUid()), null);
        }
        EmployeeCache.getInstance(((LTApplication)getApplicationContext())).refreshCache();
        ((LTApplication)getApplicationContext()).getContentResolver().notifyChange(LionMetaData.ByMeTotalLinkContract.CONTENT_URI, null);
        ((LTApplication)getApplicationContext()).getContentResolver().notifyChange(LionMetaData.ForMeTotalLinkContract.CONTENT_URI, null);
        ((LTApplication)getApplicationContext()).getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
        Utils.hideInput(((LTApplication)getApplicationContext()), profile_name_edit);
        Utils.hideInput(((LTApplication)getApplicationContext()), profile_phone);
        openProfile(false);
        if (profileChangePassword.getVisibility() == View.GONE) {
            profileChangePassword.setVisibility(View.VISIBLE);
            account_old_password.setText("");
            account_new_password.setText("");
            account_new_password_again.setText("");
            account_old_password.setVisibility(View.GONE);
            account_new_password.setVisibility(View.GONE);
            account_new_password_again.setVisibility(View.GONE);
        }
        Utils.startSync(((LTApplication) getApplicationContext()));
    }

    private void imageCapture() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Utils.showToast(AccountActivity.this, R.string.t_error_external_storage);
            return;
        }

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(AccountActivity.this.getPackageManager()) == null) {
            Utils.showToast(AccountActivity.this, R.string.t_error_camera);
            return;
        }

        mTempFile = new File(((LTApplication) getApplicationContext()).getAppFolder(), Utils.FileWorker.getNewCurrentPictureFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));

        startActivityForResult(intent, 2);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (intent == null) {
            intent = new Intent();
        }
        super.startActivityForResult(intent, requestCode);
    }

    private void openProfile(boolean open) {
        inProfile = open;
        if (inProfile) {
            fotoChanges = false;
            profile_name.setVisibility(View.GONE);
            profile_email.setText(getResources().getString(R.string.account_change));;
            main_container.setVisibility(View.GONE);
            profile_container.setVisibility(View.VISIBLE);
            mTitleToolbar.setText(R.string.account_profile);
            accItemChangeUser.setVisibility(View.GONE);
            //accIProfile.setBackground(getResources().getDrawable(R.drawable.selector_menu_item));
        } else {
            profile_name.setVisibility(View.VISIBLE);
            profile_email.setText(mSettings.getUserName());
            main_container.setVisibility(View.VISIBLE);
            profile_container.setVisibility(View.GONE);
            mTitleToolbar.setText(R.string.account);
            accItemChangeUser.setVisibility(View.VISIBLE);
            //accIProfile.setBackground(getResources().getDrawable(R.drawable.selector_menu_item_white));
            resetFoto(mSettings.getUserName());
        }
    }

    private void openBuy() {
        final View v = LayoutInflater.from(this).inflate(R.layout.premium_dialog, null);
        final AlertDialog.Builder ad = new AlertDialog.Builder(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View button = (View) v.findViewById(R.id.want_to_buy);
        View buttonB = (View) v.findViewById(R.id.back);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSettings.iCanBuyLeadertask) {
                    ArrayList skuList = new ArrayList();
                    skuList.add(IN_APP_ID);
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                    Bundle skuDetails;
                    try {
                        Bundle ownedItems = mBillingService.getPurchases(3, getPackageName(), "inapp", null);
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
                            mBillingService.consumePurchase(3, getPackageName(), purchaseToken);
                        }
                        skuDetails = mBillingService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
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
                                mAmount = sb.toString();
                                mAmount = amount;
                                mCurrency = object.getString("price_currency_code");
                                if (sku.equals(IN_APP_ID)) {
                                    Bundle buyIntentBundle = mBillingService.getBuyIntent(3, getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                    if ((int) buyIntentBundle.get("RESPONSE_CODE") == 0) { // если можно купить
                                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                        startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                } else {
                    Utils.openBrowserToBuy(mSettings, AccountActivity.this);
                }
            }
        });
        ad.setView(v);
        ad.setCancelable(true);
        final AlertDialog dialog = ad.create();

        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void resetData(boolean logOut) {
        startBlockProgressDialog();
        new Utils.ResetDataThread(this, logOut).start();
    }


    private void startBlockProgressDialog() {
        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setMessage(getString(R.string.blocking_process));
        mProgress.show();
    }

    private void setLicenseParams() {
        try {
            long verifyEndDate = mSettings.getVerifyEndDateInLong();
            String main_user = "" + EmployeeCache.getInstance(this).find(mSettings.getVerifyEmailDirector());
            double available = Float.valueOf(mSettings.getVerifyAvailableBytes()) / 1024 / 1024;
            double unavailable = Float.valueOf(mSettings.getVerifyBytes()) / 1024 / 1024;

            double percent = ((unavailable * 100) / available);

            switch (mSettings.getLicenseType()) {
                case LTSettings.LICENSE_TYPE_NONE:
                    accLicenseButton.setVisibility(View.INVISIBLE);
                    acc_license_main_user.setVisibility(View.GONE);
                    acc_license_org.setVisibility(View.GONE);
                    acc_license_server_size.setVisibility(View.GONE);
                    accLicenseText.setVisibility(View.GONE);
                    accCountLicense.setVisibility(View.GONE);
                    acc_license_main_user_title.setVisibility(View.GONE);
                    acc_license_org_title.setVisibility(View.GONE);
                    acc_license_server_size_title.setVisibility(View.GONE);
                    accLicenseText_title.setVisibility(View.GONE);
                    accTitleContLicense.setVisibility(View.GONE);
                    break;

                case LTSettings.LICENSE_TYPE_FREE:

                    acc_license_main_user.setVisibility(View.GONE);
                    acc_license_main_user_title.setVisibility(View.GONE);
                    acc_license_org.setVisibility(View.GONE);
                    acc_license_org_title.setVisibility(View.GONE);
                    accLicenseText.setText(getString(R.string.license) + ": " + getString(R.string.free));
                    accCountLicense.setVisibility(View.GONE);
                    accTitleContLicense.setVisibility(View.GONE);
                    acc_license_server_size.setText("" + new BigDecimal(unavailable).setScale(1, RoundingMode.HALF_UP).floatValue() + " MB (" + new BigDecimal(percent).setScale(1, RoundingMode.HALF_UP).floatValue() + "%)");

                    break;

                case LTSettings.LICENSE_TYPE_PREMIUM:
                case LTSettings.LICENSE_TYPE_BUSINESS:


                    acc_license_main_user.setText(EmployeeCache.getInstance(this).find(mSettings.getVerifyNameDirector())+" ("+mSettings.getVerifyEmailDirector()+")");
                    if (mSettings.getVerifyCount() == null || mSettings.getVerifyCount().isEmpty()) {
                        accCountLicense.setVisibility(View.GONE);
                        accTitleContLicense.setVisibility(View.GONE);
                    } else {
                        accCountLicense.setText(mSettings.getVerifyCount());
                    }
                    acc_license_org.setText(mSettings.getVerifyOrgName()); //
                    accLicenseText.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(verifyEndDate))
                            + TimeHelper.getInstance().getDifferencesDateInDays(verifyEndDate, TimeHelper.currentTimeMillisWithoutTimeZone()));
                    acc_license_server_size.setText("" + new BigDecimal(unavailable).setScale(1, RoundingMode.HALF_UP).floatValue() + " MB (" + new BigDecimal(percent).setScale(1, RoundingMode.HALF_UP).floatValue() + "%)");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            accLicenseButton.setVisibility(View.INVISIBLE);
            acc_license_main_user.setVisibility(View.GONE);
            acc_license_org.setVisibility(View.GONE);
            acc_license_server_size.setVisibility(View.GONE);
            accLicenseText.setVisibility(View.GONE);
            acc_license_main_user_title.setVisibility(View.GONE);
            acc_license_org_title.setVisibility(View.GONE);
            acc_license_server_size_title.setVisibility(View.GONE);
            accLicenseText_title.setVisibility(View.GONE);
            accTitleContLicense.setVisibility(View.GONE);
            accCountLicense.setVisibility(View.GONE);
        }
    }

    private void clearCacheFoto(String fileName) {
        try {
            File cacheImgFile = new File(((LTApplication)getApplicationContext()).getAppFolder() + "/cache_" + fileName);
            if (cacheImgFile.exists()) {
                cacheImgFile.delete();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                if (v.getId() == profile_phone.getId()) {
                    saveAccount();
                } else if (v.getId() == account_new_password_again.getId()) {
                    saveAccount();
                }
                return true;

            default:
                return false;
        }
    }
}
