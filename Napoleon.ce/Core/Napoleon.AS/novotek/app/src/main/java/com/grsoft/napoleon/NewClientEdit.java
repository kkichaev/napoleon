package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.grsoft.dataobjects.NewClient;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.NewClientImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class NewClientEdit extends FragmentActivity implements SendResultListener {

    public static void open(Context context, long rowid) {
        Intent i = new Intent(context, NewClientEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(i);
    }


    AutoCompleteTextView edAddress;
    Spinner spSalesChannel;
    Spinner spProfile;
    Spinner spTypeTT;
    EditText edPhone;
    EditText edRemark;
    EditText edINN;
    EditText edName;
    EditText edMinIGStart;
    EditText edHourIGStart;
    EditText edMinIGFinish;
    EditText edHourIGFinish;
    EditText edMinLunchStart;
    EditText edHourLunchStart;
    EditText edMinLunchFinish;
    EditText edHourLunchFinish;
    EditText edFIO;

    View marker;
    SupportMapFragment mapFragment;
    NewClientImpl doc;
    GoogleMap map;

    public final static float DEFAULT_MAP_ZOOM = 17.0f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_client_edit);

        edINN = findViewById(R.id.edINN);
        edName = findViewById(R.id.edName);
        edAddress = findViewById(R.id.edAddress);
        marker = findViewById(R.id.marker);
        edPhone = findViewById(R.id.edPhone);
        spSalesChannel = findViewById(R.id.spSalesChannel);
        spProfile = findViewById(R.id.spProfile);
        spTypeTT = findViewById(R.id.spTypeTT);
        edMinIGStart = findViewById(R.id.edMinIGStart);
        edHourIGStart = findViewById(R.id.edHourIGStart);
        edMinIGFinish = findViewById(R.id.edMinIGFinish);
        edHourIGFinish = findViewById(R.id.edHourIGFinish);
        edMinLunchStart = findViewById(R.id.edMinLunchStart);
        edHourLunchStart = findViewById(R.id.edHourLunchStart);
        edMinLunchFinish = findViewById(R.id.edMinLunchFinish);
        edHourLunchFinish = findViewById(R.id.edHourLunchFinish);
        edRemark = findViewById(R.id.edRemark);
        edFIO = findViewById(R.id.edFIO);

        edAddress.setAdapter(new AddressAdapter(this));

        findViewById(R.id.btnRequest).setOnClickListener((v)->DaData.getNameOrg(edINN.getText().toString().trim(), new DaData.Action() {
            @Override
            public void run(String val) {
                edINN.post(()->innCompleted(val));
            }
        }));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        ScrollView scroll = findViewById(R.id.scroll);
        ImageView iv = findViewById(R.id.skin);

        iv.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    scroll.requestDisallowInterceptTouchEvent(true);
                    return false;

                case MotionEvent.ACTION_UP:
                    scroll.requestDisallowInterceptTouchEvent(false);
                    return true;

                default:
                    return true;
            }
        });

        findViewById(R.id.tvFindAddress).setOnClickListener((v)->findAddress());

        long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);

        if (rowid != ExtrasConst.INVALID_ROWID){
            doc = new NewClientImpl();
            doc.read(rowid);
            doc.close();

            NewClient client = doc.getData();
            edINN.setText(client.inn);
            edName.setText(client.name);
            edAddress.setText(client.address);
            edPhone.setText(client.phone);
            edFIO.setText(client.fio);
            edRemark.setText(client.remark);
        }

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            double lat = 0, lon = 0;
            if (doc == null || doc.getData().latitude == 0) {
                GpsCoord c = GPSUtilNew.getLastKnownLocation();
                lat = ((double) c.latitude) / Consts.GPS_SCALE;
                lon = ((double) c.longitude) / Consts.GPS_SCALE;
            } else {
                lat = ((double) doc.getData().latitude) / Consts.GPS_SCALE;
                lon = ((double) doc.getData().longitude) / Consts.GPS_SCALE;
            }

            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), DEFAULT_MAP_ZOOM), new GoogleMap.CancelableCallback() {
                @Override public void onCancel() { }
                @Override public void onFinish() { }
            });
        });

        findViewById(R.id.btnOK).setOnClickListener((v)->doOK());
        findViewById(R.id.btnCancel).setOnClickListener((v)->finish());

        if (edPhone.getText().toString().trim().length() == 0)
            edPhone.setText("+7");

        ConfigImpl config = new ConfigImpl();
        String sel = "";

        if (doc != null)
            sel = doc.getData().salesChannel;

        DialogHelper.loadSpinnerFromConfig(config, "КаналПродаж", new ArrayList<>(), spSalesChannel, sel, false);

        if (doc != null)
            sel = doc.getData().profile;

        DialogHelper.loadSpinnerFromConfig(config, "ПрофильТТ", new ArrayList<>(), spProfile, sel, false);

        if (doc != null)
            sel = doc.getData().typeTT;

        DialogHelper.loadSpinnerFromConfig(config, "ТипТТ", new ArrayList<>(), spTypeTT, sel, false);

        edHourIGStart.setText("");
        edMinIGStart.setText("");
        edHourIGFinish.setText("");
        edMinIGFinish.setText("");
        edHourLunchStart.setText("");
        edMinLunchStart.setText("");
        edHourLunchFinish.setText("");
        edMinLunchFinish.setText("");

        if (doc != null){
            String time = doc.getData().time1;
            String[] times = time.split("-");

            if (times.length == 2){
                String[] dig = times[0].split(":");

                if (dig.length == 2){
                    edHourIGStart.setText(dig[0]);
                    edMinIGStart.setText(dig[1]);
                }

                dig = times[1].split(":");

                if (dig.length == 2){
                    edHourIGFinish.setText(dig[0]);
                    edMinIGFinish.setText(dig[1]);
                }
            }

            time = doc.getData().time2;
            times = time.split("-");

            if (times.length == 2){
                String[] dig = times[0].split(":");

                if (dig.length == 2){
                    edHourLunchStart.setText(dig[0]);
                    edMinLunchStart.setText(dig[1]);
                }

                dig = times[1].split(":");

                if (dig.length == 2){
                    edHourLunchFinish.setText(dig[0]);
                    edMinLunchFinish.setText(dig[1]);
                }
            }
        }

        edHourIGStart.addTextChangedListener(new TextWatcher() {
            boolean oneLetter = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oneLetter = s.length() == 1;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 && oneLetter){
                    edMinIGStart.post(()->{
                        edMinIGStart.requestFocus();
                    });
                }
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        edHourIGFinish.addTextChangedListener(new TextWatcher() {
            boolean oneLetter = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oneLetter = s.length() == 1;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 && oneLetter){
                    edMinIGFinish.post(()->{
                        edMinIGFinish.requestFocus();
                    });
                }
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        edHourLunchStart.addTextChangedListener(new TextWatcher() {
            boolean oneLetter = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oneLetter = s.length() == 1;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 && oneLetter){
                    edMinLunchStart.post(()->{
                        edMinLunchStart.requestFocus();
                    });
                }
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        edHourLunchFinish.addTextChangedListener(new TextWatcher() {
            boolean oneLetter = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oneLetter = s.length() == 1;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 && oneLetter){
                    edMinLunchFinish.post(()->{
                        edMinLunchFinish.requestFocus();
                    });
                }
            }

            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void doOK() {
        if (!checkFieldValues())
            return;

        LatLng latlng = map.getCameraPosition().target;
        if (doc == null){
            doc = new NewClientImpl();
            GpsCoord coord = new GpsCoord((int) (latlng.latitude *  Consts.GPS_SCALE), (int) (latlng.longitude * Consts.GPS_SCALE), 0);
            doc.init(this, UUID.randomUUID().toString().replace("-",""),  coord);
        }

        NewClient client = doc.getData();
        client.inn = edINN.getText().toString().trim();
        client.name = edName.getText().toString().trim();
        client.address = edAddress.getText().toString().trim();
        client.latitude = (int) (latlng.latitude * Consts.GPS_SCALE);
        client.longitude = (int) (latlng.longitude * Consts.GPS_SCALE);
        client.phone = edPhone.getText().toString().trim();
        client.fio = edFIO.getText().toString().trim();
        client.time1 = compileTime(edHourIGStart, edMinIGStart, edHourIGFinish, edMinIGFinish);
        client.time2 = compileTime(edHourLunchStart, edMinLunchStart, edHourLunchFinish, edMinLunchFinish);
        client.salesChannel = spSalesChannel.getSelectedItem().toString().trim();
        client.profile = spProfile.getSelectedItem().toString().trim();
        client.typeTT = spTypeTT.getSelectedItem().toString().trim();
        client.remark = edRemark.getText().toString().trim();

        doc.write();
        doc.close();

        new DocumentSender(this, findViewById(R.id.btnOK),
                NewClientDoc.instance().getObjectName(), doc,
                doc.getRowid(), this).execute((Void[])null);
    }

    private String compileTime(EditText edHourStart, EditText edMinStart, EditText edHourFinish, EditText edMinFinish){
        return String.format("%s:%s-%s:%s", edHourStart.getText().toString().trim(), edMinStart.getText().toString().trim(),
                edHourFinish.getText().toString().trim(), edMinFinish.getText().toString().trim());
    }

    private boolean checkFieldValues() {
        if (edINN.getText().toString().trim().length() == 0){
            Toast.makeText(this, R.string.input_inn, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edName.getText().toString().trim().length() == 0){
            Toast.makeText(this, R.string.input_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edAddress.getText().toString().trim().length() == 0){
            Toast.makeText(this, R.string.input_address, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edFIO.getText().toString().trim().length() == 0){
            Toast.makeText(this, R.string.input_fio, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edMinIGStart.getText().toString().trim().length() == 0 || edHourIGStart.getText().toString().trim().length() == 0 ||
                edMinIGFinish.getText().toString().trim().length() == 0 || edHourIGFinish.getText().toString().trim().length() == 0){
            Toast.makeText(this, R.string.input_time1, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edMinLunchStart.getText().toString().trim().length() == 0 || edHourLunchStart.getText().toString().trim().length() == 0 ||
                edMinLunchFinish.getText().toString().trim().length() == 0 || edHourLunchFinish.getText().toString().trim().length() == 0){
            Toast.makeText(this, R.string.input_time2, Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (spSalesChannel.getSelectedItemPosition() == 0){
//            Toast.makeText(this, R.string.select_sales_channel, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (spProfile.getSelectedItemPosition() == 0){
//            Toast.makeText(this, R.string.select_profile, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (spTypeTT.getSelectedItemPosition() == 0){
//            Toast.makeText(this, R.string.select_type_tt, Toast.LENGTH_SHORT).show();
//            return false;
//        }

        String phone = edPhone.getText().toString().trim();

        if (phone.length() != 12 || !phone.startsWith("+7")){
            Toast.makeText(this, R.string.incorrect_phone_number, Toast.LENGTH_SHORT).show();
            return false;
        }

        try{
            checkValueRange(new EditText[]{edMinIGStart, edMinIGFinish, edMinLunchStart, edMinLunchFinish}, 59);
            checkValueRange(new EditText[]{edHourIGStart, edHourIGFinish, edHourLunchStart, edHourLunchFinish}, 23);
        }catch (Exception e){
            Toast.makeText(this, R.string.incorrect_time_format, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkValueRange(EditText[] input, int limit) throws Exception {
        for(EditText ed : input){
            int val = Integer.parseInt(ed.getText().toString().trim());

            if (val > limit)
                throw new Exception();
        }
    }

    private void findAddress(){
        String addr = edAddress.getText().toString().trim();

        if (addr.length() == 0)
            return;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        setPointFromAddress(geocoder, addr);
    }

    private void innCompleted(String name) {
        edName.setText(name);
    }

    private void setPointFromAddress(Geocoder geocoder, String adrstr) {
        try {
            List<Address> list = geocoder.getFromLocationName(adrstr, 1);

            if (list.size() > 0) {
                Address adr = list.get(0);

                mapFragment.getMapAsync(googleMap -> {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(adr.getLatitude(), adr.getLongitude()), DEFAULT_MAP_ZOOM));
                });
            }else
                addressErrorDlg();

        }catch (Exception e){
            e.printStackTrace();
            addressErrorDlg();
        }
    }

    private void addressErrorDlg() {
        AddressErrorDlg dlg = new AddressErrorDlg();
        dlg.show(getSupportFragmentManager(), "");
    }

    @Override
    public void postSendExecute(boolean result) {
        finish();
    }
}
