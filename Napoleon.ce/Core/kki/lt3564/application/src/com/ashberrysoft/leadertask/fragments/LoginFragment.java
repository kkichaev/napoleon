package com.ashberrysoft.leadertask.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.VerifyUser;
import com.ashberrysoft.leadertask.instance_sync.MyInstanceIDListenerService;
import com.ashberrysoft.leadertask.modern.activity.LoadingScreenActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.dialog.SyncAddressDialog;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;

public class LoginFragment extends LTVisibleBaseFragment implements OnEditorActionListener {

    // VIEW's
    private EditText mLogin;
    private EditText mPassword;
    private ImageView mLogo;
    private Button mCreateAcc;
    private Button mEnter;
    private Button mBack;
    private Button settings;
    private TextView mForgot;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean isEnterClicked = false;

    // VALUE
    private InputMethodManager mIMManager;
    
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
    
    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_login, null);
        mLogin = (EditText) v.findViewById(R.id.l_login);
        mPassword = (EditText) v.findViewById(R.id.l_password);
        mLogo = (ImageView) v.findViewById(R.id.leadertask_logo);


        mLogin.setText(mSettings.getUserProfile().getName());
        mPassword.setText(mSettings.getUserProfile().getPassword());
        mLogo.setImageResource(R.drawable.icon_lt_new_login);

        mCreateAcc = (Button) v.findViewById(R.id.l_create_account);
        mEnter = (Button) v.findViewById(R.id.btn_l_input);
        mBack = (Button) v.findViewById(R.id.back);
        mForgot = (TextView) v.findViewById(R.id.i_forgot_password);
        settings = (Button) v.findViewById(R.id.sync_settings);

        ///createAcc.setText(getActivity().getString(R.string.l_create_account));
        mForgot.setText(getActivity().getString(R.string.forgot_password));

        final ImageView logo = (ImageView) v.findViewById(R.id.logo);
        if (mApp.getSettings().isThemeDark()) {
            v.setBackgroundColor(Color.BLACK);
           
            logo.setImageResource(R.drawable.leadertask_while);
        } else {
            v.setBackgroundColor(Color.WHITE);
           
            logo.setImageResource(R.drawable.leadertask);
        }

        resetEditVisibility();



        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideInput(mLogin);
                Utils.hideInput(mPassword);
                isEnterClicked = false;
                resetEditVisibility();
            }
        });

        mLogin.setOnEditorActionListener(this);
        mPassword.setOnEditorActionListener(this);
        mCreateAcc.setOnClickListener(this);
        mForgot.setOnClickListener(this);
        settings.setOnClickListener(this);
        mEnter.setOnClickListener(this);
        mIMManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        Utils.fixActivityForAnalytics(getActivity(), "Login");

        return v;
    }

    public void resetEditVisibility() {
        if (isEnterClicked) {
            mLogin.setVisibility(View.VISIBLE);
            mPassword.setVisibility(View.VISIBLE);
            mEnter.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_orange));
            mEnter.setTextColor(getResources().getColor(R.color.white));
            mBack.setVisibility(View.VISIBLE);

            if (IPCConstants.BOX) {
                mCreateAcc.setVisibility(View.GONE);
                mForgot.setVisibility(View.GONE);
                settings.setVisibility(View.GONE);
            } else {
                mCreateAcc.setVisibility(View.GONE);
                mForgot.setVisibility(View.VISIBLE);
                settings.setVisibility(View.GONE);
            }
        } else {
            mLogin.setVisibility(View.GONE);
            mPassword.setVisibility(View.GONE);
            mEnter.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_orange2));
            mEnter.setTextColor(getResources().getColor(R.color.black));
            mBack.setVisibility(View.GONE);

            if (IPCConstants.BOX) {
                mCreateAcc.setVisibility(View.GONE);
                mForgot.setVisibility(View.GONE);
                settings.setVisibility(View.VISIBLE);
            } else {
                settings.setVisibility(View.GONE);
                mCreateAcc.setVisibility(View.VISIBLE);
                mForgot.setVisibility(View.GONE);
            }
        }



    }

    @Override
    public void onResume() {
        final IntentFilter filter = new IntentFilter(ServiceConstants.ACTION_LOGIN);
        filter.addAction(ServiceConstants.ACTION_LOGIN_WITHOUT_SYNC);
        filter.addAction(ServiceConstants.ACTION_NOT_SUCCESSFUL_LOGIN);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
        mIMManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        
        if (mSettings.isLoginAfterRegistration() && mLogin.length()>0 && mPassword.length()>0) {
            mSettings.setLoginAfterRegistration(false);
            makeLogin();
        }
        super.onResume();
    }
    
     
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        mIMManager.hideSoftInputFromWindow(mLogin.getWindowToken(), 0);
 
        super.onPause();
    }

    private void makeLogin() {
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            final String login = mLogin.getText().toString();
            final String password = mPassword.getText().toString();

            if (TextUtils.isEmpty(login)) {
                Toast.makeText(mApp, R.string.error_login, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(mApp, R.string.error_password, Toast.LENGTH_SHORT).show();
            } else {
                if (isNetworkAvailable()) {
                    setBlockingProcess(true, null);
                    mSettings.getUserProfile().setNamePassword(login, password);
                    mSettings.setLoginAfterRegistration(true);
                    new VerifyUser(getActivity(), mSettings.getUserProfile()).startAtService();
                } else {
                    Utils.hideInput(mLogin);
                    Utils.hideInput(mPassword);
                    Toast.makeText(mApp, R.string.error_internet_access, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ServiceConstants.ACTION_LOGIN)) {
                // save user name and password
                mSettings.saveUser(mLogin.getText().toString(), mPassword.getText().toString());
                if (isNetworkAvailable()) {
                    synchronize();
                    //Utils.playAudio(getActivity(), 0);
                    //Toast.makeText(mApp, R.string.data_loading, Toast.LENGTH_LONG).show();
                    LTSettings.isNeedToRunLoadingScreen = true;
                    setBlockingProcess(false, null);
                    LTSettings.getInstance().setIsNeedToShowLoadingScreen(true);
                    getActivity().finish();
                    LTSettings.getInstance().setNeedToPutSettings(true);
                    LTSettings.getInstance().setNeedToAddUnboardingTasks(false);
                    getActivity().startActivity(LoadingScreenActivity.newInstance(getActivity())); // ПРОСТО ВХОД
                } else {
                    Toast.makeText(getActivity(), R.string.error_internet_access, Toast.LENGTH_LONG).show();
                    setBlockingProcess(false, null);
                    getActivity().finish();
                    getActivity().startActivity(SlidingActivity.newInstance(getActivity()));
                }
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_LOGIN_WITHOUT_SYNC)) {
                // save user name and password
                mSettings.saveUser(mLogin.getText().toString(), mPassword.getText().toString());
                getActivity().finish();
                getActivity().startActivity(SlidingActivity.newInstance(getActivity()));
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_NOT_SUCCESSFUL_LOGIN)) {
                // hide indeterminate progress bar
                setBlockingProcess(false, null);
                mSettings.getUserProfile().invalidateProfile();

                // get extra as toast content
                final String toastContent = ((VerifyUser) intent
                        .getSerializableExtra(AbstractServiceRequest.EXTRA_TASK)).getResult();
                if (toastContent != null) {
                    // show toast
                    Toast.makeText(getActivity(), toastContent, Toast.LENGTH_LONG).show();
                }

                if (getString(R.string.error_standard_version).equals(toastContent)) {
                    // save user name and password
                    mSettings.saveUser(mLogin.getText().toString(), mPassword.getText().toString());
                    getActivity().finish();
                    getActivity().startActivity(SlidingActivity.newInstance(getActivity()));
                }
            }
        }
    };

    @Override
    public void onFragmentResult(Object object, int requestCode) {
        switch (requestCode) {
            case SyncAddressDialog.CODE:
                mSettings.setSyncNamespace((String) object);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.sync_settings:
            SyncAddressDialog.newInstance(this, mSettings.getSyncNamespaceToEdit()).showDialog(this.getFragmentManager());
            break;
        case R.id.btn_l_input:
            if (isEnterClicked) {
                makeLogin();
            } else {
                isEnterClicked = true;
                resetEditVisibility();
            }
            break;

        case R.id.l_create_account:
            startFragment(RegistrationFragment.newInstance());
            break;

        case R.id.i_forgot_password:
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.link_help)+LTSettings.getInstance().getVerifyUserIdForUri())));
            break;

        default:
            break;
        }
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.help:
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.link_help) + LTSettings.getInstance().getVerifyUserIdForUri())));
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
        case EditorInfo.IME_ACTION_NEXT:
            mPassword.requestFocus();
            return true;

        case EditorInfo.IME_ACTION_DONE:
            makeLogin();
            mIMManager.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
            return true;

        default:
            return false;
        }
    }


}