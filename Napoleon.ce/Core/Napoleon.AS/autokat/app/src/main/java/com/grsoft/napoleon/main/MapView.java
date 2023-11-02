package com.grsoft.napoleon.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;

public class MapView extends CoordinateView {

    private OnBackPressedCallback onBackPressedCallback;

    public MapView() {
        super(GPSUtilNew.getLastKnownLocation());
    }

    public MapView(GpsCoord loc) {
        super(loc);
    }

    TextView startVisit;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        startVisit = v.findViewById(R.id.btnOK);
        startVisit.setText(R.string.ok);
        startVisit.setOnClickListener(w->startVisit());

        map.getMapAsync(m->{settingMap(m);});

        DbReader.fetch(OrgEx.class, "latitude > 0").forEach(o->addOrgMarker(o));

        model.setCurrentOrg(null);
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                model.setCurrentOrg(null);
                setEnabled(false);

                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        return v;
    }

    private void startVisit() {
        onBackPressedCallback.remove();
        OrgEx org = model.getCurrentOrg().getValue();
        getParentFragmentManager().popBackStack();
        if (org != null) ((MainActivity) getActivity()).startVisit(org);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    private void addOrgMarker(OrgEx o) {
        map.getMapAsync(googleMap -> {
            MarkerOptions m = new MarkerOptions();
            LatLng pos = new LatLng((double) o.latitude / Consts.GPS_SCALE, (double) o.longitude / Consts.GPS_SCALE);
            m.position(pos);
            m.draggable(true);
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.org_location));
            Marker r = googleMap.addMarker(m);
            r.setTag(o);
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_MAP_ZOOM));
            googleMap.setOnMarkerDragListener(MapView.this);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        });
    }

    private void settingMap(GoogleMap m) {
        m.setOnMarkerClickListener(mr->onMarkerClicked(mr));
        m.setOnMapClickListener(mm->onMapClicked());
        m.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                return null;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                OrgEx org = (OrgEx) marker.getTag();

                if (org == null) return  null;

                View v = getLayoutInflater().inflate(R.layout.map_ballon, null);
                ((TextView)v.findViewById(R.id.address)).setText(org.address);
                ((TextView)v.findViewById(R.id.name)).setText(org.name);

                return v;
            }
        });
    }

    private void onMapClicked() {
        startVisit.setText(R.string.ok);
        model.setCurrentOrg(null);
    }

    private boolean onMarkerClicked(Marker mr) {
        OrgEx org = (OrgEx) mr.getTag();
        startVisit.setText(R.string.start_visit);
        model.setCurrentOrg(org);

        return false;
    }
}
