package com.novotek.sales;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.novotek.dataobjects.LoginResult;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.ws.ErrResult;
import com.novotek.sales.login_views.AckCode;
import com.novotek.sales.login_views.CheckCode;
import com.novotek.sales.login_views.Model;
import com.novotek.sales.login_views.LoadData;
import com.novotek.utils.Updater;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    static final String REQ_TAG = "reqcode_str";
    static final String TAG = "Login";
    private static final int UPDATE_REQUEST_CODE = 10;
    public static final int REQ_CODE = R.id.req_login;

    Model model;

    Fragment curFragment;

    public static LoginResult loginResult = new LoginResult();

    public static void open(Activity context) {
        Intent i = new Intent(context, Login.class);
        context.startActivityForResult(i, REQ_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(this).get(Model.class);
        model.load(this);

        model.getPhone().observe(this, phone -> {
            model.save(Login.this);
        });

        model.getRequestError().observe(this, err -> {
//            if(err.error.equals(ErrResult.OLD_VERSION)) {
//                updateVersion();
//            } else {
                runOnUiThread(() -> {
                    loadFragment(new AckCode());
                    Toast.makeText(Login.this, err.error, Toast.LENGTH_LONG).show();
                });
//            }
        });

        model.getRequestResult().observe(this, result -> {
            if(result != null && result.error != 0) {
                View v = LayoutInflater.from(this).inflate(R.layout.login_error, null);
                PopupWindow pw = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                Pattern phoneP = Pattern.compile("<phone>(.+)</phone>");
                Matcher m = phoneP.matcher(result.message);

                final String phone = m.find() ? m.group(0) : "";

                String text = result.message.replace("phone>", "u>");
                TextView tv = (TextView)v.findViewById(R.id.text);
                tv.setText(Html.fromHtml(text));

                tv.setOnClickListener(view -> {
                    pw.dismiss();
                    if(phone.length() > 0) {
                        model.updateLastConnect(this, 0);
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Model.toPhoneNumber(phone))));
                        setResult(RESULT_CANCELED, new Intent());
                        finish();
                    }
                });

                pw.showAtLocation(findViewById(R.id.frmChild), Gravity.CENTER, 0, 0);
                findViewById(R.id.wait).setVisibility(View.VISIBLE);

                pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        findViewById(R.id.wait).setVisibility(View.GONE);
                    }
                });

//                Toast.makeText(Login.this, result.message, Toast.LENGTH_LONG).show();
            } else {
                if(!(curFragment instanceof CheckCode)) {
                    loadFragment(new CheckCode());
                }
            }
        });

        model.getDataLoaded().observe(this, result -> {
            if(result) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);

        if(Model.TESTING) {
//            loadFragment(new CheckCode());
            loadFragment(new LoadData());
        } else {
            if(model.getWaitInterval() > 0)
                loadFragment(new CheckCode());
            else
                loadFragment(new AckCode());
        }

        getSupportFragmentManager().setFragmentResultListener(CheckCode.RESULT_CODE, this, (reqCode, bundle) -> {
            loadFragment(new LoadData());
        });
//        updateVersion();
    }

    void loadFragment(Fragment cf) {
        curFragment = cf;
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                )
                .replace(R.id.frmChild, cf)
                .commit();
    }

    boolean startGooglePlayUpdate() {
        return false;
//        boolean ret = true;
//        try {
//            AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
//            // Returns an intent object that you use to check for an update.
//            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//            // Checks whether the platform allows the specified type of update,
//            // and checks the update priority.
//            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                    try {
//                        model.setRequest(false);
//                        appUpdateManager.startUpdateFlowForResult(
//                                appUpdateInfo,
//                                AppUpdateType.IMMEDIATE,
//                                this,
//                                UPDATE_REQUEST_CODE);
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                        startLocalUpdate();
//                    }
//                }
//            });
//            appUpdateInfoTask.addOnFailureListener(runnable -> {
//                startLocalUpdate();
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            ret = false;
//        }
//        return ret;
    }

    void checkUpdateInProgress() {
        try {
            AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this,
                                UPDATE_REQUEST_CODE);
                    } catch(Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startLocalUpdate() {
        // download & install by hand
        Updater.update(this, new Updater.Handler() {
            @Override
            public void requestDone() { model.setRequest(false); }

            @Override
            public void progress(Updater.Progress progress) { model.setLoadProgress(progress); }
        });
//        Toast.makeText(this, R.string.update_text, Toast.LENGTH_LONG).show();
    }

    void updateVersion() {
        model.setRequest(true);
        if(!startGooglePlayUpdate()) {
            startLocalUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUpdateInProgress();
    }

    @Override
    public void onBackPressed() {
        if(curFragment instanceof CheckCode && model.getWaitInterval() == 0) {
            loadFragment(new AckCode());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == UPDATE_REQUEST_CODE) {
            if(resultCode != RESULT_OK) {
                if(!startGooglePlayUpdate()) {
                    startLocalUpdate();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}