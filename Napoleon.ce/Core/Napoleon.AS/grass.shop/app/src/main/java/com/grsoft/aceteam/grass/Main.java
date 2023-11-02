package com.grsoft.aceteam.grass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napmobile.BuildConfig;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

import java.util.UUID;

public class Main extends AppCompatActivity {
    Fragment currentFragment;
    BottomNavigationView bottomView;
    int selectedItem;

    Model model;

    String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.CAMERA,
    };
    private final int REQUEST_CODE_PERMISSIONS = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if(!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        bottomView = findViewById(R.id.btMenu);
        bottomView.setOnItemSelectedListener(item -> {
            openTab(item.getItemId());
            return true;
        });

        model = new ViewModelProvider(this).get(Model.class);
        model.onCreate(this);
        openFragment(new OrderFragment(), false);
        selectedItem = R.id.itOrders;
    }

    public void openOrder() {
        View v = bottomView.findViewById(R.id.itOrders);
        if(v != null)
            v.performClick();
    }

    private void openTab(int itemId) {
        if(itemId != selectedItem) {
            selectedItem = itemId;
            model.clearItem();

            BaseFragment cf = (itemId == R.id.itOrders) ? new OrderFragment() : new DocsFragment();
            openFragment(cf, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerDevice();
    }

    private Boolean allPermissionsGranted() {
        for(String p : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    void registerDevice() {
        CfgNpl c = (CfgNpl) ConfigManager.getConfig();
        if(c.uuid.length() == 0) {
            String id = getAppId();
            c.userid = id;
            c.uuid = "";
            ConfigManager.save();
            ServerHelper.register(c, (res, error) -> {
                boolean result = (boolean) res;
                if(!result) {
                    runOnUiThread(() -> {
                        String msg = getString(R.string.reg_error) + "\n" + error;
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        ConfigManager.save();
                    });
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    String getAppId() {
        String devId = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            devId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            devId = tm.getDeviceId();
        }
        if(devId == null || BuildConfig.DEBUG) {
            devId = UUID.randomUUID().toString().replace("-", "");
        }

        return devId;
    }

    public void fragmentResumed(BaseFragment topFragment) {
        currentFragment = topFragment;
//        optionMenu = topFragment.getOptionMenu();
//        invalidateOptionsMenu();

        String title = topFragment.getTitle();
        getSupportActionBar().setTitle(title);
    }

    public void openFragment(BaseFragment fragment, boolean addToBackStack) {
        Log.d("MainActivity", "openFragment:" + fragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment);

        if (addToBackStack) {
            ft.addToBackStack(fragment.TAG());
        }
        ft.commit();
    }

    public void openOrderNumber(OrderImpl order) {
        model.setOrderNumber(order.getData().number);

        View v = bottomView.findViewById(R.id.itDocs);
        if(v != null) v.performClick();
    }
}
