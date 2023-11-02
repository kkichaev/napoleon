package com.serviko.sales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.serviko.dataobjects.Basket;
import com.serviko.dataobjects.BasketItem;
import com.serviko.dataobjects.LoginResult;
import com.serviko.dataobjects.OrderSend;
import com.serviko.dataobjects.OrderSendItem;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.ws.ErrResult;
import com.serviko.dataobjects.ws.SendBasketParam;
import com.serviko.dataobjects.xml.FieldWriter;
import com.serviko.sales.login_views.AckCode;
import com.serviko.sales.login_views.CheckCode;
import com.serviko.sales.login_views.Model;
import com.serviko.sales.login_views.LoadData;
import com.serviko.utils.Updater;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Date;
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

        model.getSmsMode().observe(this, aBoolean -> {
            model.save(Login.this);
        });

        model.getPhone().observe(this, phone -> {
            model.save(Login.this);
        });

        model.getRequestError().observe(this, err -> {
            if(err.error.equals(ErrResult.OLD_VERSION)) {
                updateVersion();
            } else {
                runOnUiThread(() -> {
                    loadFragment(new AckCode());
                    Toast.makeText(Login.this, err.error, Toast.LENGTH_LONG).show();
                });
            }
        });

        model.getRequestResult().observe(this, result -> {
            if(result.result == false) {
                if(result.error.equals(ErrResult.OLD_VERSION)) {
                    updateVersion();
                } else {
                    Toast.makeText(Login.this, result.error, Toast.LENGTH_LONG).show();
                }
            } else {
                if (result.code == -1) {
                    loadFragment(new LoadData());
                } else {
                    loadFragment(new CheckCode());
                }
            }
        });

        model.getDataLoaded().observe(this, result -> {
            if(result) {
                setResult(RESULT_OK);
                finish();
            }
        });

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);

//        test();

        if(Model.TESTING) {
//            loadFragment(new AckCode());
            loadFragment(new LoadData());
        } else {
            loadFragment(new AckCode());
        }

        getSupportFragmentManager().setFragmentResultListener(CheckCode.RESULT_CODE, this, (reqCode, bundle) -> {
            loadFragment(new LoadData());
            model.loadData(Login.this);
        });
//        updateVersion();
    }

//    String test() {
//        String body = "";
//        try {
//            StringWriter sw = new StringWriter();
//            XmlSerializer s = Xml.newSerializer();
//            s.setOutput(sw);
//            s.startTag(null, "test");
//            s.flush();
//            sw.write("testinf<check>ttt</check>");
//            s.endTag(null, "test");
//            s.flush();
//
//            body = sw.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return body;
//    }

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
        if(curFragment instanceof CheckCode) {
            loadFragment(new AckCode());
            return;
        }
        super.onBackPressed();
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