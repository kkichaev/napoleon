package com.grsoft.napoleon;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.highlight.Highlight;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CheckInvoice;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Outcome;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.IncomeDoc;
import com.grsoft.napoleon.documents.OutcomeDoc;
import com.grsoft.network.DocExportListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StartActivity extends FragmentActivity {
    private static final int PERMISSION_REQUEST = 100;
    private static final int REQUEST_SETTING_CODE = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sync(); }
        });

        findViewById(R.id.btnIncome).setOnClickListener(new View.OnClickListener(){ @Override public void onClick(View view) { IncomeList.open(StartActivity.this); } });
        findViewById(R.id.btnDelivery).setOnClickListener(new View.OnClickListener(){ @Override public void onClick(View view) { OutcomeList.open(StartActivity.this); } });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itSetting){
            Setting.open(StartActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sync() {
        Hitching[] rcv = new Hitching[] {
                new RcvNewHitching(PriceEx.class, "Price"),
                new RcvNewHitching(Org.class, "Org"),
                new RcvNewHitching(Delivery.class, "Delivery"),
                new RcvNewHitching(CheckInvoice.class, "CheckInvoice"),
        };

        // remove old docs
        Calendar c= Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -14);
        Date d = c.getTime();
        String where = "where created < " + Long.toString(d.getTime());
        String stmt;
        String tableName = (new Outcome()).getTableName();
        if(DbWriter.isTableExists(tableName)) {
            stmt = "delete from " + tableName + where;
            try {
                DataBaseManager.getDataBase().execSQL(stmt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tableName = (new Income()).getTableName();
        if(DbWriter.isTableExists(tableName)) {
            stmt = "delete from " + tableName + where;
            try {
                DataBaseManager.getDataBase().execSQL(stmt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<DocExportListener> send = new ArrayList<>();
        DocExportListener docs;
        docs = IncomeDoc.instance().getDirtyDocuments();
        if(docs.getDocuments().getCount() > 0)
            send.add(docs);
        docs = OutcomeDoc.instance().getDirtyDocuments();
        if(docs.getDocuments().getCount() > 0)
            send.add(docs);
        new SyncProcess(this, new ArrayList<>(Arrays.asList(rcv)), send).execute((Void[])null);
    }

    class PermissionNotSetDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder ab = new AlertDialog.Builder(StartActivity.this);
            ab.setTitle(R.string.need_set_permission);
            ab.setMessage(R.string.set_permissions_in_settings);
            ab.setCancelable(false);
            ab.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", StartActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_SETTING_CODE);
                }
            });
            return ab.create();
        }
    }

    boolean checkPermissions() {
        if(Build.VERSION.SDK_INT >= 23) {
            String[] prms = new String[] {
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };

            for(String p : prms) {
                if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED ){
                    ActivityCompat.requestPermissions(this, prms, PERMISSION_REQUEST);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTING_CODE)
            checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
        if(rc == PERMISSION_REQUEST) {
            for(int i = 0; i < result.length; i++)
                if (result[i] != PackageManager.PERMISSION_GRANTED) {
                    PermissionNotSetDialog dlg = new PermissionNotSetDialog();
                    dlg.show(getSupportFragmentManager(), "");
                    break;
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }
}
