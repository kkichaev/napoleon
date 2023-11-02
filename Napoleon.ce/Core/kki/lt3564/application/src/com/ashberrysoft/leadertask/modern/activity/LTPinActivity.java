package com.ashberrysoft.leadertask.modern.activity;

import android.app.Activity;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

import java.util.Arrays;
import java.util.List;

public class LTPinActivity extends AppLockActivity {

    @Override
    public void showForgotDialog() {

    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (mType == AppLock.UNLOCK_PIN) {
//            LTApplication mApp = (LTApplication) getApplication();
//            LTSettings mSettings = mApp.getSettings();
//
//            findViewById(R.id.pin_code_fingerprint_imageview).setVisibility(mSettings.isNeedFingerToStart() ? View.VISIBLE : View.GONE);
//
//            if (mSettings.isNeedFingerToStart()) {
//                TextView tv = findViewById(R.id.pin_code_fingerprint_textview);
//                tv.setText(R.string.touch_sensor);
//            }
//        }
    }

    @Override
    protected boolean isFingerPrintEnable() {
        boolean res = super.isFingerPrintEnable();
        LTApplication mApp = (LTApplication) getApplication();
        LTSettings mSettings = mApp.getSettings();

        return res || mSettings.isNeedFingerToStart();
    }

    @Override
    public void onPinFailure(int attempts) {
        if (mType == AppLock.UNLOCK_PIN && attempts == 3) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onPinSuccess(int attempts) {

    }

    @Override
    public int getPinLength() {
        return super.getPinLength();//you can override this method to change the pin length from the default 4
    }

    @Override
    public List<Integer> getBackableTypes() {
        return Arrays.asList(AppLock.CHANGE_PIN, AppLock.DISABLE_PINLOCK, AppLock.ENABLE_PINLOCK, AppLock.ENABLE_FINGERPRINT);
    }
}
