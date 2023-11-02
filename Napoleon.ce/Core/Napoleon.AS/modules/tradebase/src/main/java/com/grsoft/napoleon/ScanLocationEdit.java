package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grsoft.dataobjects.ScanLocation;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ScanLocationDoc;
import com.grsoft.network.BaseSimpleActivity;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;


public class ScanLocationEdit extends BaseSimpleActivity implements GpsStatus.Listener, GoogleMap.OnMarkerDragListener, ScriptActivity {
    public static Class<? extends Activity> activity = ScanLocationEdit.class;
    private final static String ACCURACY = "Точность";
    public static int BEST_ACC = 20;
    private int reqAcc = BEST_ACC;
    private CreatableDocument<ScanLocation> document = createDocument();
    private LocationManager locman;
    private TextView tvAcc;
    private TextView tvInfo;
    private TextView tvCurrAcc;
    private TextView tvSatInfo;
    private View btnSend;
    private ProgressBar progress;
    private OrgImpl org = new OrgImpl();
    private TextView tvOrgInfo;
    private SupportMapFragment mapFragment;
    private final static float DEFAULT_MAP_ZOOM = 10.0f;

    public static void open(Context context, long rowid) {
        Intent i = new Intent(context, activity);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(i);
    }

    @Override
    protected void inflateView() {
        tvAcc = (TextView) findViewById(R.id.tvAcc);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvCurrAcc = (TextView) findViewById(R.id.tvCurrAcc);
        tvSatInfo = (TextView) findViewById(R.id.tvSatInfo);
        btnSend = findViewById(R.id.btnSend);
        progress = (ProgressBar) findViewById(R.id.progress);
        tvOrgInfo = (TextView) findViewById(R.id.tvOrg);
    }

    protected void init() {
        initRequireAcc();
        initDoc();
        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void initView() {
        tvSatInfo.setText("");
        tvAcc.setText(getString(R.string.request_accurace, reqAcc));
        tvCurrAcc.setVisibility(View.GONE);
        setOrgInfo(document.getId());
        ScanLocation c = document.getData();
        btnSend.setEnabled(false);
        btnSend.setOnClickListener(sendClick());
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (c.latitude == 0 || c.longitude == 0)
            tvInfo.setText(getString(R.string.wait_coord));
        else {
            updateLocationText(c);
            btnSend.setEnabled(true);
            progress.setVisibility(View.GONE);
            tvSatInfo.setVisibility(View.GONE);
            setAccuracyMsg(c.accuracy);
            addMarker((float) c.latitude / Consts.GPS_SCALE, (float) c.longitude / Consts.GPS_SCALE);
        }

        ScriptHelper.initView(this, DocType.getCurDoc().getObjectName(), document.getData().created, document.getId());
    }

    private void updateLocationText(ScanLocation c) {
        tvInfo.setText(getString(R.string.coord_has_got, (float) c.latitude / Consts.GPS_SCALE, (float) c.longitude / Consts.GPS_SCALE));
    }

    private CreatableDocument<ScanLocation> createDocument() {
        return (CreatableDocument<ScanLocation>) DocType.getCurDoc().create();
    }

    private OnClickListener sendClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        };
    }

    public void send() {
        new DocumentSender(this, btnSend, DocType.getCurDoc().getObjectName(), document, document.getRowid()).execute((Void[]) null);
    }

    protected void initDoc() {
        Intent i = getIntent();

        if (i != null) {
            long rowid = i.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);

            if (rowid != ExtrasConst.INVALID_ROWID)
                document.read(rowid);
        }
    }

    private void setOrgInfo(String id) {
        org.read("id", id);
        tvOrgInfo.setText(org.getData().name);

    }

    protected void initRequireAcc() {
        ConfigImpl cfg = new ConfigImpl();
        StringBuilder sb = new StringBuilder();
        if (cfg.getValue(sb, ACCURACY)) {
            try {
                reqAcc = Integer.parseInt(sb.toString());
            } catch (Exception e) {
            }
        }
    }

    ;

    LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(final Location location) {
            if (location.getAccuracy() <= reqAcc) {
                setDocCoordGood(location);
                unregLocMan();
                setInfoMessage(location);
                btnSend.setEnabled(true);
                progress.setVisibility(View.GONE);

                addMarker(location.getLatitude(), location.getLongitude());
            }

            setAccuracyMsg((int) location.getAccuracy());
        }
    };

    private void addMarker(final double lat, final double lon) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions m = new MarkerOptions();
                LatLng pos = new LatLng(lat, lon);
                m.position(pos);
                m.title(org.getData().name);
                m.draggable(true);
                googleMap.addMarker(m);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_MAP_ZOOM));
                googleMap.setOnMarkerDragListener(ScanLocationEdit.this);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.gpsgatheredit;
    }

    protected void setAccuracyMsg(int a) {
        tvCurrAcc.setVisibility(View.VISIBLE);
        tvCurrAcc.setText(Html.fromHtml(getString(R.string.cur_acc, a)));
    }

    protected void setInfoMessage(Location location) {
        tvInfo.setText(getString(R.string.coord_has_got, location.getLatitude(), location.getLongitude()));
    }

    protected void setDocCoordGood(Location loc) {
        ScanLocation c = document.getData();
        c.latitude = (int) (loc.getLatitude() * Consts.GPS_SCALE);
        c.longitude = (int) (loc.getLongitude() * Consts.GPS_SCALE);
        c.accuracy = (int) loc.getAccuracy();
        document.write();

        ScanLocationDoc.instance().refreshDocSum(document.getId());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locman.getProvider(LocationManager.GPS_PROVIDER) != null && locman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ScanLocation c = document.getData();
            if (c.latitude == 0 || c.longitude == 0) {
                if (Build.VERSION.SDK_INT < 30)
                    locman.addGpsStatusListener(this);
                else
                    locman.registerGnssStatusCallback(new GnssStatus.Callback() {
                        @Override
                        public void onStarted() {
                            super.onStarted();
                        }
                    });
                locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                progress.setVisibility(View.VISIBLE);
            }
        } else
            showDialog(R.id.gsp_setting_dlg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregLocMan();

        if (isFinishing()) {
            ScanLocation c = document.getData();
            if (c.latitude == 0 || c.longitude == 0) {
                document.delete();
                document.close();
            }
        }
    }

    protected void unregLocMan() {
        locman.removeUpdates(listener);

        if (Build.VERSION.SDK_INT < 30)
            locman.removeGpsStatusListener(this);
    }

    @SuppressWarnings("unused")
    @Override
    public void onGpsStatusChanged(int event) {
        int satellites = 0;
        int timetofix = locman.getGpsStatus(null).getTimeToFirstFix();

        for (GpsSatellite sat : locman.getGpsStatus(null).getSatellites())
            satellites++;

        updateSatInfo(timetofix, satellites);
    }

    private void updateSatInfo(int timetofix, int satellites) {
        tvSatInfo.setText(Html.fromHtml(getString(R.string.satinfo, satellites)));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.gsp_setting_dlg)
            return createAskOpenGpsDialog();
        return super.onCreateDialog(id);
    }

    private Dialog createAskOpenGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gpsOffTitle);
        builder.setMessage(R.string.gpsOffMessage);
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        return builder.create();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        document.getData().latitude = (int) (marker.getPosition().latitude * Consts.GPS_SCALE);
        document.getData().longitude = (int) (marker.getPosition().longitude * Consts.GPS_SCALE);
        updateLocationText(document.getData());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        document.getData().latitude = (int) (marker.getPosition().latitude * Consts.GPS_SCALE);
        document.getData().longitude = (int) (marker.getPosition().longitude * Consts.GPS_SCALE);
        document.write();
        document.close();

        ScanLocationDoc.instance().refreshDocSum(document.getId());
        updateLocationText(document.getData());
    }

    @Override
    public boolean closeDocument() {
        return true;
    }

//	private String msToHuman(int ms) {
//		int x = ms / 1000;
//	    int seconds = x % 60;
//		x /= 60;
//		int	minutes = x % 60;
//		x /= 60;
//		int hours = x % 24;
//		x /= 24;
//        int days = x;
//				
//        StringBuilder sb  = new StringBuilder();
//        
//        if(days > 0)
//        	sb.append(days).append( "дн. ");
//        if(hours > 0)
//        	sb.append(hours).append( "ч. ");
//        if(minutes > 0)
//        	sb.append(minutes).append( "мин. ");        
//        if(seconds > 0)
//        	sb.append(seconds).append( "сек. ");
//        
//        if(sb.length() == 0)
//        	sb.append(ms / 1000).append(" мс.");
//        
//		return sb.toString();
//	}
}
