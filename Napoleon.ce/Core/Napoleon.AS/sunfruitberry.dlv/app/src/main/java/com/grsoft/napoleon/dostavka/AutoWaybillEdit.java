package com.grsoft.napoleon.dostavka;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AutoInfo;
import com.grsoft.dataobjects.AutoWaybill;
import com.grsoft.dataobjects.DVisit;
import com.grsoft.dataobjects.impl.AutoInfoImpl;
import com.grsoft.dataobjects.impl.AutoWaybillImpl;
import com.grsoft.dataobjects.impl.DVisitImpl;
import com.grsoft.dataobjects.impl.FuelImpl;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesItemsAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.io.File;

public class AutoWaybillEdit extends Activity {
    public static final String DEL_PHOTO_ACTION = "com.grsoft.napoleon.dostavka.TaskEdit.DEL_PHOTO_ACTION";
    AutoWaybillImpl document = new AutoWaybillImpl();
    ImagesItemsAdapter iadapter;
    DVisitImpl vis;

    public static void open(Context context, long rowid){
        Intent i = new Intent(context, AutoWaybillEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_waybill_edit);

        findViewById(R.id.btnPhoto).setOnClickListener((v)->photoClick());

        document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));

        AutoInfoImpl autoInfo = new AutoInfoImpl();
        autoInfo.read("id", document.getId());

        String fuel1 = "";
        String fuel2 = "";

        FuelImpl fuel = new FuelImpl();
        fuel.read("id", autoInfo.getData().fuel1Id);
        fuel1 = fuel.getData().name;

        if (autoInfo.getData().fuel2Id.length() > 0){
            fuel.read("id", autoInfo.getData().fuel2Id);
            fuel2 = fuel.getData().name;
        }

        AutoInfo info = new AutoInfo();
        info.id = document.getId();

        DbReader reader = new DbReader();
        reader.read(info, info.getTableName());
        reader.close();

        TextView tv = findViewById(R.id.tvNumber);
        tv.setText(info.number);
        tv.setTextColor(info.color == 0 ? Color.BLACK : info.color);

        tv = findViewById(R.id.tvDate);
        tv.setText(Util.simpleDateFormat.format(document.getDate()));

        tv = findViewById(R.id.tvFuelName1Start);
        tv.setText(fuel1);

        tv = findViewById(R.id.tvFuelName2Start);
        tv.setText(fuel2);

        tv = findViewById(R.id.tvFuelName1Finish);
        tv.setText(fuel1);

        tv = findViewById(R.id.tvFuelName2Finish);
        tv.setText(fuel2);

        tv = findViewById(R.id.tvFuelName1Input);
        tv.setText("Заправка " + fuel1);
        tv = findViewById(R.id.tvFuelName2Input);
        tv.setText("Заправка " + fuel2);

        AutoWaybill wb = document.getData();

        EditText ed = findViewById(R.id.edStartKM);
        ed.setText(wb.startKM == 0 ? "" : Util.IntToScaleStr(wb.startKM, Consts.SUM_SCALE));
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateDistance();
            }
        });

        ed = findViewById(R.id.edFinishKM);
        ed.setText(wb.finishKM == 0 ? "" : Util.IntToScaleStr(wb.finishKM, Consts.SUM_SCALE));

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateDistance();
            }
        });

        ed = findViewById(R.id.edFuel1Start);
        ed.setText(wb.fuel1Start == 0 ? "" : Util.IntToScaleStr(wb.fuel1Start, Consts.QTY_SCALE));

        ed = findViewById(R.id.edFuel2Start);
        ed.setText(wb.fuel2Start == 0 ? "" : Util.IntToScaleStr(wb.fuel2Start, Consts.QTY_SCALE));

        ed = findViewById(R.id.edFuel1Finish);
        ed.setText(wb.fuel1Finish == 0 ? "" : Util.IntToScaleStr(wb.fuel1Finish, Consts.QTY_SCALE));

        ed = findViewById(R.id.edFuel2Finish);
        ed.setText(wb.fuel2Start == 0 ? "" : Util.IntToScaleStr(wb.fuel2Finish, Consts.QTY_SCALE));

        ed = findViewById(R.id.edFuel1Input);
        ed.setText(wb.fuel1Input == 0 ? "" : Util.IntToScaleStr(wb.fuel1Input, Consts.QTY_SCALE));

        ed = findViewById(R.id.edFuel2Input);
        ed.setText(wb.fuel2Input == 0 ? "" : Util.IntToScaleStr(wb.fuel2Input, Consts.QTY_SCALE));

        findViewById(R.id.btnClose).setOnClickListener((v)->closeDoc());

        updateDistance();
    }

    private void updateDistance(){
        EditText ed = findViewById(R.id.edStartKM);
        int startKM = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

        ed = findViewById(R.id.edFinishKM);
        int finishKM = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

        int val = finishKM - startKM;

        TextView tv = findViewById(R.id.tvDistance);
        tv.setText(Util.IntToScaleStr(val, Consts.SUM_SCALE));
    }

    private void closeDoc() {
        document.getData().closed = 1;
        finish();
    }

//    private void photoClick() {
//        DVisit data = vis.getData();
//        data.created = document.getData().visit;
//
//        if (!vis.read()) {
//            if (vis.init(this, "", GPSUtilNew.getLastKnownLocation())){
//                document.getData().visit = data.created;
//                document.write();
//                document.close();
//            }
//        }
//
//        DocType.setCurDoc(DVisitDoc.instance());
//        vis.open(this);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK
                && storePath.trim().length() > 0) {

            vis.addPhoto(storePath.getBytes());
            vis.write();
            vis.close();
            storePath = "";
            updateImages();
        }
    }

    public void onResume() {
        super.onResume();

        vis = new DVisitImpl();
        DVisit dvisit = vis.getData();
        dvisit.created = document.getData().visit;
        if(vis.read()) {
            updateImages();
        }
        vis.close();
    }

    private void updateImages() {
        DVisit dvisit = vis.getData();
        int sz = (int) getResources().getDimension(R.dimen.preview_sz);
        int pd = (int)getResources().getDimension(R.dimen.preview_padding);

        iadapter = new ImagesItemsAdapter(this, dvisit.items, sz, sz, pd, false);
        HorizontalListView g = (HorizontalListView)findViewById(R.id.gvItems);
        if(g != null) {
            g.setAdapter(iadapter);
        }
        g.setOnItemLongClickListener((p,v,x,i)->managePhoto(x));
    }

    private boolean managePhoto(int x) {
        ManagePhotoDlg dlg = new ManagePhotoDlg();
        Bundle args = new Bundle();
        args.putInt(ManagePhotoDlg.PIC_ID, x);
        args.putLong(ManagePhotoDlg.VIS_ID, vis.getData().created.getTime());
        dlg.setArguments(args);
        dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        AutoWaybill wb = document.getData();

        EditText ed = findViewById(R.id.edStartKM);
        wb.startKM = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

        ed = findViewById(R.id.edFinishKM);
        wb.finishKM = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

        ed = findViewById(R.id.edFuel1Start);
        wb.fuel1Start = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

        ed = findViewById(R.id.edFuel2Start);
        wb.fuel2Start = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

        ed = findViewById(R.id.edFuel1Finish);
        wb.fuel1Finish = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

        ed = findViewById(R.id.edFuel2Finish);
        wb.fuel2Finish = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

        ed = findViewById(R.id.edFuel1Input);
        wb.fuel1Input = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

        ed = findViewById(R.id.edFuel2Input);
        wb.fuel2Input = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

        document.write();
        document.close();
    }

    private static final String COUNTER = "counter_str";
    private String storePath = new String();
    protected static final int CAMERA_ACTIVITY = 1;

    private void photoClick() {
        DVisit data = vis.getData();
        data.created = document.getData().visit;

        if (!vis.read()) {
            if (vis.init(this, "", GPSUtilNew.getLastKnownLocation())){
                document.getData().visit = data.created;
                document.write();
                document.close();
            }
        }

        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                int cnt = pref.getInt(COUNTER, 1);
                File file = new File(getExternalFilesDir(null), Integer.toString(cnt) + ".jpg");
                storePath = file.getAbsolutePath();
                SharedPreferences.Editor ed = pref.edit();
                ed.putInt(COUNTER, ++cnt);
                ed.commit();

                Uri uri = null;

                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(AutoWaybillEdit.this, getString(R.string.fileprovider_authorities), file);
                }else
                    uri = Uri.fromFile(file);


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(intent, CAMERA_ACTIVITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(delphoto, new IntentFilter(DEL_PHOTO_ACTION));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(delphoto);
    }

    BroadcastReceiver delphoto = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            int idx = intent.getIntExtra(ManagePhotoDlg.PIC_ID, -1);

            if (idx != -1){
                vis.getData().items.remove(idx);
                vis.write();
                vis.close();
            }

            updateImages();
        }
    };
}
