package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("MissingPermission")
public class PotenzialMap extends FragmentActivity implements GoogleMap.OnMarkerDragListener, LocationListener {
    public final static String LOCATION = "location";
    private final static float DEFAULT_MAP_ZOOM = 10.0f;


    private SupportMapFragment mapFragment;
    private Location location;
    protected LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.potenzialmap);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        location = getIntent().getParcelableExtra(LOCATION);

        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (location != null)
            putMarker();
    }

    private void putMarker() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions m = new MarkerOptions();
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                m.position(pos);
                m.title("Вы здесь");
                m.draggable(true);
                googleMap.addMarker(m);
                googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_MAP_ZOOM) );
                googleMap.setOnMarkerDragListener(PotenzialMap.this);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        location.setLatitude(marker.getPosition().latitude);
        location.setLongitude(marker.getPosition().longitude);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra(LOCATION, location);
        setResult(RESULT_OK, i);

        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (location == null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, PotenzialOrg.MIN_TIME, PotenzialOrg.MIN_DISTANCE, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, PotenzialOrg.MIN_TIME, PotenzialOrg.MIN_DISTANCE, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        putMarker();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
