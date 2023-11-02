package com.serviko.sales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.dataobjects.ws.ReqCodeParam;
import com.serviko.sales.main_views.ActionDetail;
import com.serviko.sales.main_views.Actions;
import com.serviko.sales.main_views.BaseView;
import com.serviko.sales.main_views.Basket;
import com.serviko.sales.main_views.Catalog;
import com.serviko.sales.main_views.Main;
import com.serviko.sales.main_views.Model;
import com.serviko.sales.main_views.Profile;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    static String appId = "1234";
    static String devId;

    static boolean askedLogin = false;
    static Model activeModel = null;
    static int lastSelectedItem = -1;
    public static String CHANNEL_ID = "";

    Model model;
    boolean reenter = false;

    String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
    };
    private final int REQUEST_CODE_PERMISSIONS = 10;

    public static ReqCodeParam getProgParams() {
        ReqCodeParam ret = new ReqCodeParam();
        ret.appId = appId;
        ret.deviceId = devId;
        return ret;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(this).get(Model.class);
        if(activeModel != null) {
            if(askedLogin && PartnerList.partners().contains(activeModel.getPartner().getValue())) {
                model.setFrom(activeModel);
                reenter = true;
            } else {
                askedLogin = false;
            }
        }
        activeModel = model;

        createNotificationChannel();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.main_activity);

        BottomNavigationView bv = findViewById(R.id.btMenu);
        bv.setOnNavigationItemSelectedListener(this);

        if(!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        model.getBasketQty().observe(this, qty -> {
            updateBasketBadge(qty);
        });
    }

    private void updateBasketBadge(Integer qty) {
        BottomNavigationView bv = findViewById(R.id.btMenu);
        if(bv == null || qty == null)
            return;

        if(qty == 0) {
            BadgeDrawable b = bv.getBadge(R.id.itBasket);
            if(b != null) {
                b.setVisible(false);
                b.clearNumber();
            }
        } else {
            BadgeDrawable b = bv.getOrCreateBadge(R.id.itBasket);
            b.setVisible(true);
            b.setNumber(qty);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        model.bindPicHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        model.unbindPicHandler();
    }

    void loadFragment(BaseView cf) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                )
                .replace(R.id.frmChild, cf, cf.getFragmentTag())
                .commit();
    }

    public void loadFragment(BaseView cf, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                )
                .replace(R.id.frmChild, cf, cf.getFragmentTag());

        if(addToBackStack) {
            ft.addToBackStack(cf.getFragmentTag());
            ft.setReorderingAllowed(true);
        }
        ft.commit();
    }

    public void openAction(ActionDef action) {
        model.currentAction = action;
        loadFragment(new ActionDetail(), true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initAppData();

        if(!askedLogin && allPermissionsGranted()) {
            Login.open(this);
        }

        if(reenter) {
            reenter = false;
            if(model.getPartner().getValue() != null) {
                if(lastSelectedItem == -1)
                    lastSelectedItem = R.id.itHome;
                openItem(lastSelectedItem);
            } else {
                selectCurrentPartner();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Login.REQ_CODE) {
            if(resultCode == RESULT_OK) {
                loadFragment(new Main());
                askedLogin = true;
                selectCurrentPartner();
            } else if(resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        } else if(requestCode == SelectPartner.REQ_CODE) {
            if(resultCode == RESULT_OK) {
                model.setPartner(PartnerList.getCurrent());
            }
        }
    }

    private void createNotificationChannel() {
        CHANNEL_ID = getString(R.string.channel_id);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    void selectCurrentPartner() {
        if(!SelectPartner.open(this, false)) {
            model.setPartner(PartnerList.getCurrent());
        }
        openItem(R.id.itHome);
    }

    private Boolean allPermissionsGranted() {
        for(String p : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void initAppData() {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            task.getException().printStackTrace();
                            return;
                        }
                        appId = task.getResult();
                    }
                });
        fm.subscribeToTopic("news");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            devId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            devId = tm.getDeviceId();
        }
        if(devId == null || BuildConfig.DEBUG) {
            devId = UUID.randomUUID().toString().replace("-", "");
        }
    }

    public void openItem(int id) {
        BottomNavigationView bv = findViewById(R.id.btMenu);
        bv.setSelectedItemId(id);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BaseView cf = null;
        int id = item.getItemId();
        lastSelectedItem = id;

        if(id == R.id.itHome) cf = new Main();
        else if(id == R.id.itCatalog) cf = new Catalog();
        else if(id == R.id.itActions) cf = new Actions();
        else if(id == R.id.itBasket) cf = new Basket();
        else if(id == R.id.itProfile) cf = new Profile();

        if(cf != null) {
            FragmentManager fm = getSupportFragmentManager();
            while(fm.getBackStackEntryCount() > 0)
                fm.popBackStackImmediate();

            loadFragment(cf);
        }
        return true;
    }

    public void logout() {
        askedLogin = false;
        model.logout();

        Login.open(this);
    }
}
