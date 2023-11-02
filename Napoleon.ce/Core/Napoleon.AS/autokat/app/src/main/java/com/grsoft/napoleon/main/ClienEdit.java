package com.grsoft.napoleon.main;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.FormatTT;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.AddressErrorDlg;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClienEdit extends BaseFragment {
    static final String TAG = ClienEdit.class.toString();
    static final String ORG_ARG = "org_arg";
    TextView btnLocation;
    boolean editClient;
    static OrgEx oe;
    protected SupportMapFragment map;
    public final static float DEFAULT_MAP_ZOOM = 10.0f;
    List<FormatTT> formats = new ArrayList<>();
    View view;
    View marker;
    private AutoCompleteTextView address;
    private TextView tvGetAddr;

    @Override
    protected int getLayoutID() {
        return R.layout.client_edit;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    public ClienEdit(boolean editCurrent) {
        Bundle args = new Bundle();
        args.putBoolean(ORG_ARG, editCurrent);
        setArguments(args);
        oe = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        marker = view.findViewById(R.id.marker);
        editClient = getArguments().getBoolean(ORG_ARG);

        DbReader.fetch(FormatTT.class).forEach(f->formats.add(f));
        formats.sort(Comparator.comparingInt(x -> x.pos));

        ArrayAdapter<FormatTT> adapter = new ArrayAdapter<>(requireContext(), R.layout.setting_list_item, formats);
        AutoCompleteTextView av = (AutoCompleteTextView) ((TextInputLayout) view.findViewById(R.id.org_format)).getEditText();
        av.setAdapter(adapter);
        av.setOnItemClickListener((parent, tv, position, id) -> {
            FormatTT fmt = formats.get(position);
            fmt.items.sort(Comparator.comparingInt(x -> x.pos));
            ArrayAdapter<FormatTT> a = new ArrayAdapter<>(requireContext(), R.layout.setting_list_item, fmt.items);

            if (this.view != null) {
                AutoCompleteTextView v = (AutoCompleteTextView) ((TextInputLayout) view.findViewById(R.id.org_type)).getEditText();
                v.setAdapter(a);
                v.setText("");
            }
        });

        if (editClient) {
            oe = model.currentOrg.getValue();
            setFields();
        } else if (oe == null) {
            // не переписываем клиента чтобы не потерять введенные поля
            oe = new OrgEx();
            oe.id = Util.genUUID();
        }

        view.findViewById(R.id.btnOK).setOnClickListener((w) -> ok());
        btnLocation = view.findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener((w)->getLocation());

        if (oe.latitude != 0)
            btnLocation.setText(R.string.location_done);

        getParentFragmentManager().setFragmentResultListener(CoordinateView.KEY, getViewLifecycleOwner(), (key, result) -> {
            LatLng location = result.getParcelable(CoordinateView.LOCATION);

            if (location != null) {
                oe.latitude = (int) (location.latitude * Consts.GPS_SCALE);
                oe.longitude = (int) (location.longitude * Consts.GPS_SCALE);
            }
        });

        map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(googleMap -> {
            double lat, lon;
            if (oe.latitude == 0) {
                GpsCoord c = GPSUtilNew.getLastKnownLocation();
                lat = ((double) c.latitude) / Consts.GPS_SCALE;
                lon = ((double) c.longitude) / Consts.GPS_SCALE;
            } else {
                lat = ((double) oe.latitude) / Consts.GPS_SCALE;
                lon = ((double) oe.longitude) / Consts.GPS_SCALE;
            }

            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), DEFAULT_MAP_ZOOM), new GoogleMap.CancelableCallback() {
                @Override public void onCancel() { }
                @Override public void onFinish() {
                    googleMap.setOnCameraMoveListener(()->btnLocation.setText(R.string.get_location));
                }
            });
        });

        tvGetAddr = view.findViewById(R.id.getAddress);
        tvGetAddr.setOnClickListener(this::btnAdrClick);
        
        address = ((AutoCompleteTextView) ((TextInputLayout) view.findViewById(R.id.address)).getEditText());
        address.setAdapter(new AddressAdapter(getContext()));
        address.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                tvGetAddr.setText(R.string.find_the_address);
            }
        });

        ViewGroup vg = view.findViewById(R.id.layout);

        for (int i=0; i < vg.getChildCount(); i++) {
            if (vg.getChildAt(i) instanceof TextInputLayout) {
                TextInputLayout til = (TextInputLayout) vg.getChildAt(i);
                EditText ed = til.getEditText();
                ed.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {
                        Context context = getContext();

                        if (context != null) {
                            int heightDiff = view.getRootView().getHeight() - view.getHeight();
                            if (heightDiff > dpToPx(context, 200)) { // if more than 200 dp, it's probably a keyboard...
                                tvGetAddr.setVisibility(View.GONE);
                                marker.setVisibility(View.GONE);
                            } else {
                                tvGetAddr.setVisibility(View.VISIBLE);
                                marker.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }

        return view;
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void btnAdrClick(View view) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String adrstr = address.getText().toString().trim();

        setPointFromAddress(geocoder, adrstr);
    }

    private void setAddressFromPoint(Geocoder geocoder) {
        map.getMapAsync(googleMap -> {
            LatLng latlng = googleMap.getCameraPosition().target;

            try {
                oe.latitude = (int) (latlng.latitude * Consts.GPS_SCALE);
                oe.longitude = (int) (latlng.longitude * Consts.GPS_SCALE);
                btnLocation.setText(R.string.location_done);
                tvGetAddr.setText(R.string.address_found);

                List<Address> list = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);

                if (list.size() > 0) {
                    Address adr = list.get(0);
                    String str = buildAddress(adr);
                    address.setText(str, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setPointFromAddress(Geocoder geocoder, String adrstr) {
        try {
            List<Address> list = geocoder.getFromLocationName(adrstr, 1);

            if (list.size() > 0) {
                Address adr = list.get(0);

                map.getMapAsync(googleMap -> {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(adr.getLatitude(), adr.getLongitude()), DEFAULT_MAP_ZOOM));
                });

                oe.latitude = (int) (adr.getLatitude() * Consts.GPS_SCALE);
                oe.longitude = (int) (adr.getLongitude() * Consts.GPS_SCALE);
                btnLocation.setText(R.string.location_done);
                tvGetAddr.setText(R.string.address_found);
            }else
                addressErrorDlg();

        }catch (Exception e){
            e.printStackTrace();
            addressErrorDlg();
        }
    }

    private void addressErrorDlg() {
        AddressErrorDlg dlg = new AddressErrorDlg();
        dlg.show(getParentFragmentManager(), "");
    }

    @NonNull
    private String buildAddress(Address adr) {

        String city = adr.getAddressLine(0);
        String street = adr.getAddressLine(1);
        String house = adr.getSubThoroughfare();

        StringBuilder sb = new StringBuilder();

        if (city != null)
            sb.append(city);

        if (sb.length() > 0)
            sb.append(" ");

        if (street != null)
            sb.append(street);

        if (sb.length() > 0)
            sb.append(" ");

        if (house != null)
            sb.append(house);

        return sb.toString();
    }

    void setFields() {
        if (oe == null) {
            return;
        }
        TextInputLayout til = view.findViewById(R.id.name);
        til.getEditText().setText(oe.name);

        til = view.findViewById(R.id.address);
        ((AutoCompleteTextView) til.getEditText()).setText(oe.address, false);

        til = view.findViewById(R.id.remark);
        til.getEditText().setText(oe.remark);

        AutoCompleteTextView av = (AutoCompleteTextView) ((TextInputLayout) view.findViewById(R.id.org_type)).getEditText();
        av.setText(oe.orgType, false);

        av = (AutoCompleteTextView) ((TextInputLayout) view.findViewById(R.id.org_format)).getEditText();
        av.setText(oe.orgFormat, false);
    }


    void setOrg() {
        oe.name = ((TextInputLayout) getView().findViewById(R.id.name)).getEditText().getText().toString().trim();
        oe.address = ((TextInputLayout) getView().findViewById(R.id.address)).getEditText().getText().toString().trim();
        oe.orgType = ((TextInputLayout) getView().findViewById(R.id.org_type)).getEditText().getText().toString().trim();
        oe.orgFormat = ((TextInputLayout) getView().findViewById(R.id.org_format)).getEditText().getText().toString().trim();
        oe.remark = ((TextInputLayout) getView().findViewById(R.id.remark)).getEditText().getText().toString().trim();
    }

    private void getLocation() {
        map.getMapAsync(googleMap -> {
            LatLng latlng = googleMap.getCameraPosition().target;

            try {
                oe.latitude = (int) (latlng.latitude * Consts.GPS_SCALE);
                oe.longitude = (int) (latlng.longitude * Consts.GPS_SCALE);
                btnLocation.setText(R.string.location_done);
                Toast.makeText(getContext(), R.string.location_done, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void ok() {
        if (validateInput()) {
            ((MainActivity) getActivity()).newClientAdded(updateOrg());
            getParentFragmentManager().popBackStack();
        }
    }

    private OrgEx updateOrg() {
        oe.markDirty();
        setOrg();

        DbWriter w = new DbWriter();
        w.insertRecord(oe);
        w.close();

        return oe;
    }

    private boolean validateInput() {
        View v = getView();

        for (int id : new int[]{R.id.name, R.id.address, R.id.org_type, R.id.org_format}) {
            TextInputLayout t = v.findViewById(id);
            t.setError(null);

            if (t.getEditText().getText().toString().trim().length() == 0) {
                t.setError(getString(R.string.value_requirment));
                t.requestFocus();
                return false;
            }
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        setFields();
        if (oe != null && oe.latitude != 0)
            btnLocation.setText(R.string.location_done);

        if(editClient && oe != null && oe.orgFormat.length() == 0) {
            AutoCompleteTextView av = (AutoCompleteTextView) ((TextInputLayout) view.findViewById(R.id.org_format)).getEditText();
            av.requestFocus();
            av.showDropDown();
        }
    }
}
