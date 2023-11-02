package com.ashberrysoft.leadertask.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.VerifyUser;
import com.ashberrysoft.leadertask.modern.activity.PreviewActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;

public class LoginFragmentExtra extends LTVisibleBaseFragment {

    // VIEW's
    private EditText mLogin;
    private EditText mPassword;
    private ImageView mLogo;
    
    // VALUE
    private InputMethodManager mIMManager;
    
    public static LoginFragmentExtra newInstance() {
        return new LoginFragmentExtra();
    }
    
    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_login_extra, null);
        mLogin = (EditText) v.findViewById(R.id.l_login);
        mPassword = (EditText) v.findViewById(R.id.l_password);
        mLogo = (ImageView) v.findViewById(R.id.leadertask_logo);

        mLogin.setText(mSettings.getUserProfile().getName());
        mLogo.setImageResource(Utils.getLeaderTaskLauncherResource());


        final TextView change_user = (TextView) v.findViewById(R.id.change_user);
        change_user.setText(getActivity().getString(R.string.settings_change_user_title));

        final ImageView logo = (ImageView) v.findViewById(R.id.logo);
        if (mApp.getSettings().isThemeDark()) {
            v.setBackgroundColor(Color.BLACK);
           
            logo.setImageResource(R.drawable.leadertask_while);
        } else {
            v.setBackgroundColor(Color.WHITE);
           
            logo.setImageResource(R.drawable.leadertask);
        }

        change_user.setOnClickListener(this);
        v.findViewById(R.id.btn_l_input).setOnClickListener(this);
        mIMManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        Utils.fixActivityForAnalytics(getActivity(), "Login");

        return v;
    }

    private void makeLogin() {
        final String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(password) || !password.equals(mSettings.getUserProfile().getPassword())) {
            if (password.length() > 0) {
                Toast.makeText(mApp, R.string.error_wrong_auth, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mApp, R.string.error_password, Toast.LENGTH_SHORT).show();
            }
        } else {
            getActivity().finish();
            getActivity().startActivity(SlidingActivity.newInstance(getActivity()));
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_l_input:
            makeLogin();
            break;

        case R.id.change_user:
            // сменить пользователя диалог
            final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setCancelable(true);
            ad.setTitle(R.string.settings_confirmation);
            ad.setMessage(R.string.settings_logout);
            ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetData(true);
                    LTSettings.getInstance().setLoginAfterRegistration(false);
                }
            });
            ad.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = ad.create();
            alert.show();
            break;
        default:
            break;
        }
    }

    private void resetData(boolean logOut) {
        startBlockProgressDialog();
        new Utils.ResetDataThread(getActivity(), logOut).start();
    }


    private void startBlockProgressDialog() {
        ProgressDialog mProgress = new ProgressDialog(getActivity());
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setMessage(getString(R.string.blocking_process));
        mProgress.show();
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }
}