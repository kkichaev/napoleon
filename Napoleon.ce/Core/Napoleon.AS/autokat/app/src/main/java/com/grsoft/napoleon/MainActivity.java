package com.grsoft.napoleon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.MessageNew;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.main.AboutDialog;
import com.grsoft.napoleon.main.ClienEdit;
import com.grsoft.napoleon.main.CoordinateView;
import com.grsoft.napoleon.main.DocumentsView;
import com.grsoft.napoleon.main.MapView;
import com.grsoft.napoleon.main.Model;
import com.grsoft.napoleon.main.Notify;
import com.grsoft.napoleon.main.Schedule;
import com.grsoft.napoleon.main.ScriptWizard;
import com.grsoft.napoleon.main.Settings;
import com.grsoft.napoleon.main.Start;
import com.grsoft.napoleon.price.Price;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ConfigPhotoInitilizer;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String OPEN_SCHEDULE = "open_schedule";
    Toolbar toolBar;
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

        toolBar = findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");

        model = new ViewModelProvider(this).get(Model.class);

        if (model.getCurrentScript() == null)
            openFragment(new Start(), false);

//        if (Model.TESTING) {
//            CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
//            cfg.port=8899;
//            cfg.login = "10";
//            cfg.passw = "182";
//        }

        getSupportFragmentManager().setFragmentResultListener(NeedAgentSignDlg.RESULT_KEY,
                this, (requestKey, result) -> {
                    openSettings(2);
                });

        ((NapoleonAppBase) getApplication()).startMainService();
        checkApplicationPermission();

        if (getIntent() != null && getIntent().getBooleanExtra(OPEN_SCHEDULE, false))
            openStartInRoute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getBooleanExtra(OPEN_SCHEDULE, false)) {
            openStartInRoute();
        }
    }

    private void openStartInRoute() {
        Bundle b = new Bundle();
        b.putBoolean(MainActivity.OPEN_SCHEDULE, true);
        BaseFragment f = new Start();
        f.setArguments(b);
        openFragment(f, false);
    }

    private void checkApplicationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> pms = new ArrayList<>();
            pms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            pms.add(Manifest.permission.CALL_PHONE);
            pms.add(Manifest.permission.CAMERA);
            pms.add(Manifest.permission.READ_PHONE_STATE);

            pms.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (Build.VERSION.SDK_INT >= 29 && Build.VERSION.SDK_INT < 31) {
                pms.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }

            //pms.addAll(ADD_PERMISSIONS);

            for (String p : pms) {
                if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, pms.toArray(new String[]{}), PERMISSION_REQUEST);
                    break;
                }
            }

//			if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//					ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//					ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
//					ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ){
//				ActivityCompat.requestPermissions(this, pms.toArray(new String[]{}), PERMISSION_REQUEST);
//			}
        }
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
        super.onRequestPermissionsResult(rc, permissions, result);
        if (rc == PERMISSION_REQUEST) {
            for (int i = 0; i < result.length; i++)
//				if (result[i] != PackageManager.PERMISSION_GRANTED) {
//					showDialog(R.id.permission_not_set_dialog);
//					break;
//				}else
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

        String title = topFragment.getTitle();
        getSupportActionBar().setTitle(title);
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
        }
        ft.commit();
    }

    public void openSettings() {
        openSettings(0);
    }

    public void openSettings(int pos) {
        BaseFragment f = new Settings();
        Bundle args = new Bundle();
        args.putInt(Settings.TAB_IDX, pos);
        f.setArguments(args);
        openFragment(f, true);
    }

    public void showAbout() {
        AboutDialog dlg = new AboutDialog();
        dlg.show(getSupportFragmentManager(), "");
    }

    public void openSchedule(String id) {
        BaseFragment fmt = new Schedule();
        openFragment(fmt, true);
    }

//    public void openQuest(){
//        OrgEx org = (OrgEx) model.getCurrentOrg().getValue();
//
//        if (org == null)
//            Toast.makeText(this, "выберите организацию", Toast.LENGTH_SHORT).show();
//        else {
//            BaseFragment fmt = new QuestEdit();
//            openFragment(fmt, true);
//        }
//    }

    public void startVisit(OrgEx o) {
        PicStoreImpl pc = new PicStoreImpl();
        AgentPrefix ap = AgentPrefix.get();
        if (ap != null && !pc.read("id", ap.id)) {
            DialogFragment dlg = new NeedAgentSignDlg();
            dlg.show(getSupportFragmentManager(), "");
            return;
        }

        ScriptImpl doc = model.createScriptDoc(this, o);
        if (doc != null) {
            openFragment(new ScriptWizard(), true);
        } else {
            Toast.makeText(this, R.string.no_script_found, Toast.LENGTH_LONG).show();
        }
    }

    public void editClient(boolean editCurrent) {
        OrgEx oe = model.getCurrentOrg().getValue();
        if (!editCurrent || (oe != null && !oe.isPerson()))
            openFragment(new ClienEdit(editCurrent), true);
    }

    public void newClientAdded(OrgEx org) {
        model.setCurrentOrg(org);
    }

    public void openLocation(OrgEx org) {
        if (!checkLocationPermission()) return;

        GpsCoord coord = new GpsCoord(org.latitude, org.longitude, 0);
        CoordinateView frg = new CoordinateView(coord);

        openFragment(frg, true);
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] prms = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            ActivityCompat.requestPermissions(this, prms, 10);
            return false;
        }

        return true;
    }

    public void requestPrice() {
        openFragment(new Price(), true);
    }

    public void showDocuments() {
        openFragment(new DocumentsView(), true);
    }

    public void showNotify() {
        openFragment(new Notify(), true);
    }

    public void signEditor(String fileName, boolean readOnly) {
        BaseFragment f = new SignEditor();
        Bundle b = new Bundle();
        b.putString(SignEditor.FILE_NAME, fileName);
        b.putBoolean(SignEditor.READ_ONLY, readOnly);
        f.setArguments(b);
        openFragment(f, true);
    }

    public void showMap() {
        if (!checkLocationPermission()) return;
        GpsCoord coord = GPSUtilNew.getLastKnownLocation();
        openFragment(new MapView(coord), true);
    }

    private BadgeDrawable notifyBadge;

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem i = menu.findItem(R.id.notify);

        if (i != null) {
            notifyBadge = BadgeDrawable.create(this);
            BadgeUtils.attachBadgeDrawable(notifyBadge, toolBar, R.id.notify);

            int count = getUnreadMessage();

            if (count > 0) {
                notifyBadge.setNumber(count);
                notifyBadge.setVisible(true);
            } else
                notifyBadge.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private int getUnreadMessage() {
        DbWriter.checkDBTable(MessageNew.class);
        return DbReader.fetch(MessageNew.class, "read=0").size();
    }

    public ScriptImpl getIncompleteScript() {
        com.grsoft.napoleon.documents.DocList docList = ScriptDoc.instance().getDirtyDocuments().getDocuments();

        for (Document<?> d : docList) {
            ScriptImpl s = (ScriptImpl) d;

            if (s.getData().created.getTime() > 0 && !s.isComplete()) {
                return s;
            }
        }

        return null;
    }

    public boolean isGPSTurnOn(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
    }
}
