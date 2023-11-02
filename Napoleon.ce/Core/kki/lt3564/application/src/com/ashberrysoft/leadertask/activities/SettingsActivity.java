package com.ashberrysoft.leadertask.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.fragment.PreferencesFragment;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ashberrysoft.leadertask.application.Config.IN_APP_DAYS;
import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID_CHONO_UUID;
import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID_UUID;
import static com.ashberrysoft.leadertask.application.Config.NETWROK_BUY_LEADERTASK;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {


    public static final int FRAGMENT_CONTAINER = R.id.main_fragment_properties;

    // VALUE's
    private LTApplication mApp;
    private LTSettings mSettings;
    private Toolbar mToolbar;
    private TextView mTitleToolbar;


    public static String mAmount = "";
    public static String mCurrency = "";
    public static String mPackageName = "";
    public static String mProductId = "";
    public static String mPurchaseToken = "";
    public static String mSignature = "";
    public static String mPurchaseData = "";

    public static Intent newInstance(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    public void onCreate(Bundle b) {
        PreCreateActivityParamsHelper.setActivityParams(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(b);

        mApp = (LTApplication) getApplication();
        mApp.setTheme(this);
        mSettings = mApp.getSettings();

        Utils.changeLocale(getResources(), mSettings.getLanguageLocale());

        setContentView(R.layout.activity_settings);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleToolbar = (TextView) findViewById(R.id.toolbar_text_name);
        startFragment(PreferencesFragment.newInstance(), false);
    }

    private void startFragment(android.app.Fragment fragment, boolean toBackStack) {
        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(SettingsActivity.FRAGMENT_CONTAINER, fragment);
        // добавлять или нет в стек.
        if (toBackStack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }

        ft.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final Drawable upArrow = getResources().getDrawable(R.drawable.baseline_arrow_back_white_24);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(this);
    }

    public void setToolbarTitle(String title) {
        mTitleToolbar.setText(title);
    }

    @Override
    public int getContainerId() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        onClick(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.fixActivityForAnalytics(mApp, "Settings");
    }

    @Override
    public void onClick(View v) {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1003) {
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

                    buyInWebRegChrono();

                } catch (JSONException e) {
                    android.util.Log.v("Tedorius", e.getMessage());
                }
            }
        }
    }

    private void buyInWebRegChrono() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("email", LTSettings.getInstance().getUserName()));
                    nameValuePairs.add(new BasicNameValuePair("packagename", mPackageName));
                    nameValuePairs.add(new BasicNameValuePair("productid", IN_APP_ID_CHONO_UUID));
                    nameValuePairs.add(new BasicNameValuePair("token", mPurchaseToken));
                    nameValuePairs.add(new BasicNameValuePair("language", Locale.getDefault().getLanguage()));
                    nameValuePairs.add(new BasicNameValuePair("currency", mCurrency));
                    nameValuePairs.add(new BasicNameValuePair("amount", mAmount));
                    long endDate = getSettings().getVerifyEndDateInLong();
                    if (endDate < 0 || endDate < TimeHelper.currentTimeMillisWithoutTimeZone()) {
                        endDate = TimeHelper.currentTimeMillisWithoutTimeZone();
                    }
                    nameValuePairs.add(new BasicNameValuePair("days", TimeHelper.getInstance().getIntDifferencesDateInDays(endDate, TimeHelper.currentTimeMillisWithoutTimeZone())+""));
                    nameValuePairs.add(new BasicNameValuePair("signature", mSignature));
                    nameValuePairs.add(new BasicNameValuePair("full_purchase", mPurchaseData));

                    String message = OkHttpConnection.postWithParams(nameValuePairs, NETWROK_BUY_LEADERTASK);

                    if (message.equals("OK")) {
                        SettingsActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Utils.showToast(SettingsActivity.this, "Покупка прошла успешно!");
                            }
                        });
                        Utils.startSync(((LTApplication) getApplicationContext()));
                    } else {
                        // ошибка
                        SettingsActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Utils.showToast(SettingsActivity.this, getResources().getString(R.string.exception_unknown));
                            }
                        });
                    }
                } catch (Exception e) {
                    SettingsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Utils.showToast(SettingsActivity.this, getResources().getString(R.string.exception_unknown));
                        }
                    });
                }
            }
        }).start();
    }
}