package com.ashberrysoft.leadertask.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.CreateUser;
import com.ashberrysoft.leadertask.domains.simplexml.CreateUserEnvelope;
import com.ashberrysoft.leadertask.modern.activity.PreviewActivity;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.activities.LoginActivity;
import com.ashberrysoft.leadertask.data_providers.network.CreateUser2;
import com.ashberrysoft.leadertask.domains.simplexml.CreateUserEnvelope2;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;


public class RegistrationFragment extends LTBaseFragment {

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }
    
    public interface onSomeEventListener {
        public void Sign_in_google_plus();
		public void Sign_in_facebook();

      }
    
    onSomeEventListener someEventListener;
    
    @Override
    public void onAttach(Activity activity) {
      super.onAttach(activity);
          try {
            someEventListener = (onSomeEventListener) activity;
          } catch (ClassCastException e) {
              throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
          }
    }
    
    
    // VIEW's
    private EditText mEmail;
    private EditText mName;
    /*private EditText mPass;
    private EditText mEditPhone;*/
    private boolean CustomReg;
    private Button facebook_button;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_login, menu);
    }

    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        final View v = inflater.inflate(R.layout.fragment_registration, null);
        CustomReg = false;
        //final ImageView logo = (ImageView) v.findViewById(R.id.logo);
        mName = (EditText) v.findViewById(R.id.reg_name);
        mEmail = (EditText) v.findViewById(R.id.reg_email);
//        mPass = (EditText) v.findViewById(R.id.reg_pass);
//        mEditPhone= (EditText) v.findViewById(R.id.phone);
        final TextView rules = (TextView) v.findViewById(R.id.reg_rules);
        rules.setMovementMethod(LinkMovementMethod.getInstance());
        rules.setFocusable(true);
        rules.setFocusableInTouchMode(true);

        SpannableStringBuilder sb = new SpannableStringBuilder("");
        sb.append(getResources().getString(R.string.reg_rules));
        sb.append(" ");
        sb.append(getResources().getString(R.string.reg_rules1));
        sb.setSpan(new URLSpan("https://www.leadertask.com/offer"), getResources().getString(R.string.reg_rules).length()+1, getResources().getString(R.string.reg_rules).length()+1+getResources().getString(R.string.reg_rules1).length(), 0);

        sb.append(" ");
        sb.append(getResources().getString(R.string.reg_rules2));
        sb.append(" ");
        sb.append(getResources().getString(R.string.reg_rules3));
        sb.setSpan(new URLSpan("https://www.leadertask.com/privacy-policy"), getResources().getString(R.string.reg_rules).length()+1+getResources().getString(R.string.reg_rules1).length()+getResources().getString(R.string.reg_rules2).length()+2, getResources().getString(R.string.reg_rules).length()+getResources().getString(R.string.reg_rules1).length()+1+getResources().getString(R.string.reg_rules2).length()+2+getResources().getString(R.string.reg_rules3).length(), 0);

        rules.setText(sb);

        TextView alt_create = (TextView) v.findViewById(R.id.alt_create);
        Utils.showInput(mName);
        mName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEmail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEmail.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_DONE:
                        makeRegistrationToUser();
                        return true;

                    default:
                        return false;
                }
            }
        });

        final boolean hasCustomLocale = mSettings.getLanguageLocale() != null;
        final Locale appLocale = hasCustomLocale ?  mSettings.getLanguageLocale() : Locale.getDefault();
        /*if (!appLocale.getLanguage().equals("ru")) {
            mEditPhone.setVisibility(View.GONE);
        }
        mEditPhone.setOnEditorActionListener(this);*/
        v.findViewById(R.id.register).setOnClickListener(this);

        Button facebook_button = (Button) v.findViewById(R.id.sign_in_facebook);
        SignInButton google_button = (SignInButton) v.findViewById(R.id.sign_in_google_plus);

        final Button button_back = (Button) v.findViewById(R.id.back_to_login);

        if(LoginActivity.GetDefaultGoogleAcc()!=null)
        {
        	mEmail.setText(LoginActivity.GetDefaultGoogleAcc());
            //mEmail.setSelection(mEmail.length());
        }
        
        if (mSettings.isThemeDark()) {
            //logo.setImageResource(R.drawable.leadertask_while);
            v.setBackgroundColor(Color.BLACK);
        } else {
            //logo.setImageResource(R.drawable.leadertask);
            v.setBackgroundColor(Color.WHITE);
        } 
        
        google_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            1
                    );
                } else {
                    CustomReg = true;
                    someEventListener.Sign_in_google_plus();
                }
            }
          });

        facebook_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            1
                    );
                } else {
                    CustomReg = true;
                    someEventListener.Sign_in_facebook();
                }
            }
        });


        button_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideInput(mEmail);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Utils.fixActivityForAnalytics(getActivity(), "Registration");

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //ФЕЙСБУК



        //ФЕЙСБУК
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return v;
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        }
        catch(JSONException e) {
            Log.d("","Error parsing JSON");
        }
        return null;
    }

    @Override
    public void onResume() {
        final IntentFilter filter = new IntentFilter(ServiceConstants.ACTION_REGISTRATION2);
        filter.addAction(ServiceConstants.ACTION_REGISTRATION_NOT_SUCCESSFUL2);
        filter.addAction(ServiceConstants.ACTION_REGISTRATION_NOT_SUCCESSFUL);
        filter.addAction(ServiceConstants.ACTION_REGISTRATION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            getActivity().getSupportFragmentManager().popBackStack();
            return true;

        case R.id.help:
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.link_help)+LTSettings.getInstance().getVerifyUserIdForUri())));
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.register:
            makeRegistrationToUser();
            break;

        default:
            super.onClick(v);
            break;
        }
    }

    private void AlertDialogConfirmReg()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    	builder.setTitle(R.string.confirm_registration_name)
    			.setMessage(R.string.confirm_registration_text)
    			//.setIcon(R.drawable.ic_android)
    			.setCancelable(false)
    			.setNegativeButton("OK",
    					new DialogInterface.OnClickListener() {
    						public void onClick(DialogInterface dialog, int id)
    						{
    							dialog.cancel();
    							getActivity().finish();
    							mApp.startActivity(LoginActivity.newInstance(mApp));  
    						}
    					});
    	AlertDialog alert = builder.create();
    	alert.show();
    }

    public void makeRegistrationToUserI(String name, String email) {
        if (isNetworkAvailable())
        {
            setBlockingProcess(true, null);
            new CreateUser(mApp, email, name).startAtService();
        }
        else
        {
            Utils.showToast(getActivity(), R.string.error_internet_access);
        }
    }

    public void makeRegistrationToUser() {
        final String name = mName.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();

        if (email.length() == 0) {
            Utils.showToast(getActivity(), R.string.reg_fill_email);
        } else {
            if (isNetworkAvailable()) {
                setBlockingProcess(true, null);
                new CreateUser(mApp, email, name/*, pass, phone*/).startAtService();
            } else {
                Utils.hideInput(mName);
                Utils.hideInput(mEmail);
                Utils.showToast(getActivity(), R.string.error_internet_access);
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ServiceConstants.ACTION_REGISTRATION2.equals(intent.getAction()) || ServiceConstants.ACTION_REGISTRATION_NOT_SUCCESSFUL2.equals(intent.getAction())) {
                final CreateUser2 cu = (CreateUser2) intent.getSerializableExtra(AbstractServiceRequest.EXTRA_TASK);
                final CreateUserEnvelope2 answer = cu.getAnswer();

                setBlockingProcess(false, null);
                if (answer != null && answer.getErrorCode() == CreateUser2.REGISTRATION_COMPLETE)
                {
                    mSettings.saveUser(answer.getLogin(), answer.getPassword());
                    if(CustomReg == true)
                    {
                    	mSettings.setLoginAfterRegistration(true);
                    	CustomReg = false;
                        //Utils.playAudio(getActivity(), 0);
                    	getActivity().finish();
                        mApp.startActivity(LoginActivity.newInstance(mApp));
                    }
                    else
                    {
                    	mSettings.setLoginAfterRegistration(false);
                    	AlertDialogConfirmReg();
                    }
                } 
                else
                {
                	CustomReg = false;
                    showError(cu.getToastMessageId());
                }
            }

            if (ServiceConstants.ACTION_REGISTRATION.equals(intent.getAction()) || ServiceConstants.ACTION_REGISTRATION_NOT_SUCCESSFUL.equals(intent.getAction())) {
                final CreateUser cu = (CreateUser) intent.getSerializableExtra(AbstractServiceRequest.EXTRA_TASK);
                final CreateUserEnvelope answer = cu.getAnswer();

                setBlockingProcess(false, null);
                if (answer != null && answer.getErrorCode() == CreateUser.REGISTRATION_COMPLETE)
                {
                    mSettings.saveUser(answer.getLogin(), answer.getPassword());
                    mSettings.setLoginAfterRegistration(true);
                    LTSettings.getInstance().setIsNeedToShowLoadingScreen(true);
                    //Utils.playAudio(getActivity(), 0);
                    getActivity().finish();
                    mApp.startActivity(LoginActivity.newInstance(mApp));
                }
                else
                {
                    Integer messageId = cu.getToastMessageId();
                    if (messageId != null) {
                        showError(messageId);
                    }
                }
            }
        }
    };

    @Override
    public boolean showTitleBar() {
        return true;
    }
}