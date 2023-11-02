package com.grsoft.napoleon;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.grsoft.napoleon.main.AboutDialog;
import com.grsoft.napoleon.main.Model;
import com.grsoft.napoleon.main.ResponseSender;
import com.grsoft.napoleon.main.Settings;
import com.grsoft.napoleon.main.StartView;
import com.grsoft.napoleon.main.ViewDocs;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ConfigPhotoInitilizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String OPEN_SCHEDULE = "open_schedule";
//    Toolbar toolBar;
    Model model;
    int optionMenu = 0;
    BaseFragment currentFragment;
    private static final int PERMISSION_REQUEST = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        setContentView(R.layout.main);
        getWindow().setStatusBarColor(getColor(R.color.primary));

//        toolBar = findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolBar);
//        getSupportActionBar().setTitle("");

        model = new ViewModelProvider(this).get(Model.class);

//        if (Model.TESTING) {
//        }

//        ((NapoleonAppBase) getApplication()).startMainService();
        checkApplicationPermission();

        openFragment(new StartView(), false);

//        File dir = getExternalFilesDir(null);
//        File signF = new File(dir, "sign.png");
//        String signFile = signF.getAbsolutePath();
//        signEditor(signFile, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void checkApplicationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> pms = new ArrayList<>();
            pms.add(Manifest.permission.INTERNET);
            pms.add(Manifest.permission.ACCESS_NETWORK_STATE);
            pms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            for (String p : pms) {
                if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, pms.toArray(new String[]{}), PERMISSION_REQUEST);
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
        super.onRequestPermissionsResult(rc, permissions, result);
        if (rc == PERMISSION_REQUEST) {
            for (int i = 0; i < result.length; i++)
                if (permissions[i].equals(Manifest.permission.CAMERA)) {
                    Config cfg = ConfigManager.getConfig();

                    if (cfg.cameraHeight == 0) {
                        new ConfigPhotoInitilizer().init(cfg);
                        ConfigManager.save();
                    }
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionMenu != 0)
            getMenuInflater().inflate(optionMenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (currentFragment != null) {
            currentFragment.onOptionsItemSelected(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fragmentResumed(BaseFragment topFragment) {
        currentFragment = topFragment;
        optionMenu = topFragment.getOptionMenu();
        invalidateOptionsMenu();

//        String title = topFragment.getTitle();
//        getSupportActionBar().setTitle(title);
    }

    public void setOptionMenu(int menuRes) {
        optionMenu = menuRes;
        invalidateOptionsMenu();
    }

    public void openFragment(BaseFragment fragment, boolean addToBackStack) {
        Log.d("MainActivity", "openFragment:" + fragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment);

        if (addToBackStack) {
            ft.addToBackStack(fragment.TAG());
        } else {
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        }
        ft.commit();
    }

    public void openSettings() {
        BaseFragment f = new Settings();
        openFragment(f, true);
    }

    public void showAbout() {
        AboutDialog dlg = new AboutDialog();
        dlg.show(getSupportFragmentManager(), "");
    }

    public void signEditor(String fileName, boolean readOnly) {
        BaseFragment f = new SignEditor();
        Bundle b = new Bundle();
        b.putString(SignEditor.FILE_NAME, fileName);
        b.putBoolean(SignEditor.READ_ONLY, readOnly);
        f.setArguments(b);
        openFragment(f, true);
    }

    public void showSignResponse() {
        openFragment(new ResponseSender(), true);
    }

    public void openStart() {
        openFragment(new StartView(), false);
    }
}
