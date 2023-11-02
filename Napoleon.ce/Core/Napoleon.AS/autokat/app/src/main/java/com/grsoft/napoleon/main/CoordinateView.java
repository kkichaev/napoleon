package com.grsoft.napoleon.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;

import java.util.List;
import java.util.Locale;

public class CoordinateView extends BaseFragment implements GoogleMap.OnMarkerDragListener {
    public static final String TAG = CoordinateView.class.toString();
    public static final String KEY = "coordinateviewkey";
    public static final String LOCATION = "location";
    private LocationManager locman;
    protected SupportMapFragment map;
    public final static float DEFAULT_MAP_ZOOM = 10.0f;
    private TextView tvInfo;
    private TextView tvCurrAcc;
    private TextView tvAcc;
    private TextView tvSatInfo;
    private ProgressBar progress;
    private LatLng location;
    public static int BEST_ACC = 20;
    private int reqAcc = BEST_ACC;
    private final static String ACCURACY = "Точность";
    TextView address;

    public CoordinateView(GpsCoord location) {
        if (location.latitude != 0) {
            Bundle arg = new Bundle();
            LatLng l = new LatLng((double) location.latitude / Consts.GPS_SCALE, (double) location.longitude / Consts.GPS_SCALE);
            arg.putParcelable(CoordinateView.LOCATION, l);
            setArguments(arg);
        }
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


    @Override
    protected int getLayoutID() {
        return R.layout.org_coordinate_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public String getTitle() {
        return getString(R.string.select_point);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        locman = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        initRequireAcc();

        tvAcc = v.findViewById(R.id.tvAcc);
        tvCurrAcc = v.findViewById(R.id.tvCurrAcc);
        progress = v.findViewById(R.id.progress);
        tvInfo = v.findViewById(R.id.tvInfo);

        tvAcc.setText(getString(R.string.request_accurace, reqAcc));
        tvCurrAcc.setVisibility(View.GONE);
        tvInfo.setText(getString(R.string.wait_coord));

        Bundle arg = getArguments();

        if (arg != null) {
            location = arg.getParcelable(LOCATION);
            progress.setVisibility(View.INVISIBLE);
            addMarker(location.latitude, location.longitude);
        }else{
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);

            progress.setVisibility(View.VISIBLE);
        }

        v.findViewById(R.id.btnOK).setOnClickListener(w->getParentFragmentManager().popBackStack());
        return v;
    }

    private  LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            unregLocMan();
            setInfoMessage(location);
            progress.setVisibility(View.GONE);
            addMarker(location.getLatitude(), location.getLongitude());
            updateLocation(location.getLatitude(), location.getLongitude());
        }
    };

    private void updateLocation(double latitude, double longitude) {
        location = (new LatLng(latitude, longitude));
    }

    protected void setAccuracyMsg(int a) {
        tvCurrAcc.setVisibility(View.VISIBLE);
        tvCurrAcc.setText(Html.fromHtml(getString(R.string.cur_acc, a)));
    }

    protected void setInfoMessage(Location location) {
        setInfoMessage(location.getLatitude(), location.getLongitude());
    }

    protected void setInfoMessage(double lat, double lon) {
        tvInfo.setText(getString(R.string.coord_has_got, lat, lon));
    }

    private void updateSatInfo(int timetofix, int satellites) {
        tvSatInfo.setText(Html.fromHtml(getString(R.string.satinfo, satellites)));
    }


    private void addMarker(final double lat, final double lon) {
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions m = new MarkerOptions();
                LatLng pos = new LatLng(lat, lon);
                m.position(pos);
                //m.draggable(true);
                m.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location));
                googleMap.addMarker(m);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_MAP_ZOOM));
                //googleMap.setOnMarkerDragListener(CoordinateView.this);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
    }


    protected void unregLocMan() {
        locman.removeUpdates(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregLocMan();

        Bundle res = new Bundle();
        res.putParcelable(LOCATION, location);
        getParentFragmentManager().setFragmentResult(KEY, res);
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        setInfoMessage(lat, lon);
        updateLocation(lat, lon);
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }
}
