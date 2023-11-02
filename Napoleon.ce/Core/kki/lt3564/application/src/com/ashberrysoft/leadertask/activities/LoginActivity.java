package com.ashberrysoft.leadertask.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.fragments.LoginFragment;
import com.ashberrysoft.leadertask.fragments.LoginFragmentExtra;
import com.ashberrysoft.leadertask.fragments.RegistrationFragment;
import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;
import com.ashberrysoft.leadertask.instance_sync.MyInstanceIDListenerService;
import com.ashberrysoft.leadertask.modern.activity.LTPinActivity;
import com.ashberrysoft.leadertask.modern.activity.LoadingScreenActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
/*import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;*/
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import google_plus.GAuthHelper;

import static com.ashberrysoft.leadertask.utils.Utils.isMyServiceRunning;

public class LoginActivity extends BaseSlidingActivity implements RegistrationFragment.onSomeEventListener{

    private static final String TAG  = "RegistrationActivity";

    private GAuthHelper gah;
    private static final String PREF_TOKEN = "token";
    public static String first_google_acc;
    private static final int RC_SIGN_IN = 9001;
    private static final int UNLOCK_PIN = 9002;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;


    public static Intent newInstance(Context context) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }catch(Throwable e){
            e.printStackTrace();
        }

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("Tedorius", response.toString());

                                        String name ="";
                                        String email = "";
                                        String url = "";
                                        try {
                                            name = object.get("name").toString();
                                            email =  object.get("email").toString();
                                            url = ((JSONObject)((JSONObject)object.get("picture")).get("data")).get("url").toString();

                                        } catch (Exception e) {

                                        } finally {
                                            Registration_by_google_or_facebook(email, name);

                                            if (url != null && !url.isEmpty()) {
                                                LTSettings.getInstance().setNeedToDownloadPhotoGoogleFacebook(true);
                                                LTSettings.getInstance().setDownloadUriGoogleFacebook(url);
                                            }

                                        }
                                        Log.v("Tedorius", name);
                                        Log.v("Tedorius", email);
                                        Log.v("Tedorius", url);
                                    }
                                });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "name,email,picture");
                                request.setParameters(parameters);
                                request.executeAsync();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });

        if (!isMyServiceRunning(LeaderTaskSyncService.class, LoginActivity.this)) {
            Intent intent = new Intent(this, LeaderTaskSyncService.class);
            startService(intent);
        } else {
            LeaderTaskSyncService.sendNotif(this);
        }

        if (mApp.getSettings().isLoginAfterRegistration() && mApp.getSettings().getUserProfile().isValid()) {
            if (!mApp.getSettings().isNeedPasswordToStart()) {
                if (LTSettings.getInstance().isNeedToShowLoadingScreen()) {
                    LTSettings.isNeedToRunLoadingScreen = true;
                    Utils.startSyncAlways((LTApplication) getApplicationContext());

                    LTSettings.needToShowToastAfterAddTask = true;
                    LTSettings.needToShowToastAfterAddProject = true;
                    LTSettings.needToShowToastAfterAddUser = true;
                    LTSettings.needToShowToastAfterAssign = true;
                    LTSettings.getInstance().setNeedToPutSettings(true);
                    LTSettings.getInstance().setNeedToAddUnboardingTasks(true);
                    startActivity(LoadingScreenActivity.newInstance(getApplicationContext())); // РЕГИСТРАЦИЯ
                    finish();
                } else {
                    startActivity(SlidingActivity.newInstance(getApplicationContext()));
                    finish();
                }
                return;
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main_login);
        disableSlidingMenu();

        if (savedInstanceState == null) {
            final FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (mApp.getSettings().isLoginAfterRegistration() && mApp.getSettings().getUserProfile().isValid() && mApp.getSettings().isNeedPasswordToStart()) {
                mFragmentTransaction.replace(R.id.main_fragment, LoginFragmentExtra.newInstance());
            } else {
                mFragmentTransaction.replace(R.id.main_fragment, LoginFragment.newInstance());
            }
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }

        // set is current screen is tasks screen
        gah = new GAuthHelper(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String authToken = prefs.getString(PREF_TOKEN, "");
        if (authToken.length()==0)
        {
            // token not found, need authorization
            final String[] accn = gah.getAccNames();
            if (accn.length >=1) {
                first_google_acc = accn[0];
            }
            else {
                first_google_acc = null;
            }
        }

        if (mApp.getSettings().isNeedPasswordToStart() && mApp.getSettings().isNeedPinToStart()){
            Intent intent = new Intent(this, LTPinActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            startActivityForResult(intent, UNLOCK_PIN);
        }

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1
            );
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void Sign_in_google_plus() {
        if (Utils.isNetworkAvailable(mApp)) {
            signOut();
            signIn();
        }
        else {
            Utils.showToast(LoginActivity.this, R.string.error_internet_access);
        }
    }


    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else if (requestCode == UNLOCK_PIN && resultCode == Activity.RESULT_OK){
            startActivity(SlidingActivity.newInstance(this));
            finish();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);


            Registration_by_google_or_facebook(account.getEmail(), account.getDisplayName());

            if (account.getPhotoUrl() != null) {
                LTSettings.getInstance().setNeedToDownloadPhotoGoogleFacebook(true);
                LTSettings.getInstance().setDownloadUriGoogleFacebook(account.getPhotoUrl().toString());
            }

        } catch (ApiException e) {

        }
    }

    private void signIn() {
        if (mGoogleSignInClient != null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void signOut() {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }
    }


    /*private void logout(){
        Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }*/

    @Override
    public void Sign_in_facebook() {
        if (Utils.isNetworkAvailable(mApp)) {
            LoginManager.getInstance().logOut();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList( "public_profile", "email"));

        } else {
            Utils.showToast(LoginActivity.this, R.string.error_internet_access);
        }
    }

    @Override
    public void onResume() {
        mApp.setTheme(this);
        final IntentFilter filter = new IntentFilter(ServiceConstants.ACTION_SERVICE_ERROR);
        filter.addAction(ServiceConstants.ACTION_SSL_HANDSHAKE_ERROR);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ServiceConstants.ACTION_SERVICE_ERROR)) {
                setBlockingProcess(false, null);
                String ex = ((LeaderTaskException) intent.getExtras().getSerializable(
                        AbstractServiceRequest.EXTRA_EXCEPTION)).toString();
                showError(ex);
            } else if (intent.getAction().equals(ServiceConstants.ACTION_SSL_HANDSHAKE_ERROR)) {
                setBlockingProcess(false, null);
                Toast.makeText(LoginActivity.this, com.ashberrysoft.leadertask.R.string.error_ssl_handshake,
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        final Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (f != null && f instanceof LoginFragment)
        {
            LoginFragment loginFragment = (LoginFragment) f;
            if (LoginFragment.isEnterClicked) {
                LoginFragment.isEnterClicked = false;
                loginFragment.resetEditVisibility();
            } else {
                super.finish();
            }
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private void Registration_by_google_or_facebook(String email, String UserName)
    {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("RegistrationFragment");

        ((RegistrationFragment) fragment).makeRegistrationToUserI(UserName, email);
    }

    public static String GetDefaultGoogleAcc()
    {
        return first_google_acc;
    }
}