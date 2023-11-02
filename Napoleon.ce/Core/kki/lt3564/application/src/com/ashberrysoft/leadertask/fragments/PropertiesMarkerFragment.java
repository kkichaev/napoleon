package com.ashberrysoft.leadertask.fragments;

import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.dialogs.SetColorDialogBuilder;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView.OnFeaturePropertiesHeaderListener;

public class PropertiesMarkerFragment extends BaseFeaturesFragment implements OnFeaturePropertiesHeaderListener, OnAmbilWarnaListener {

    private static final String CLASS_PATH = PropertiesMarkerFragment.class.getName();
    private static final String EXTRA_MARKER = CLASS_PATH + "EXTRA_MARKER";
    private static final String EXTRA_MARKER_NEW = CLASS_PATH + "EXTRA_MARKER_NEW";

    // VIEW
    private PropertiesFeatureHeaderView mHeaderView;

    // VALUE's
    private Marker mMarker;
    private boolean mMarkerNew;
    private boolean mColorText;
    private boolean mShowKeyBoard;

    // ADAPTER
    private ListAdapter mAdapter;

    public static PropertiesMarkerFragment newInstance(Marker marker) {
        final PropertiesMarkerFragment f = new PropertiesMarkerFragment();

        if (marker != null) {
            final Bundle b = new Bundle(1);
            b.putSerializable(EXTRA_MARKER, marker);
            f.setArguments(b);
        }

        return f;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        if (bundle != null) {
            mMarkerNew = bundle.getBoolean(EXTRA_MARKER_NEW, false);
            mMarker = (Marker) bundle.get(EXTRA_MARKER);
            mShowKeyBoard = false;

        } else {
            mMarker = new Marker();
            mMarkerNew = true;
            mShowKeyBoard = true;
        }

        mHeaderView = new PropertiesFeatureHeaderView(getActivity(), this);
        mHeaderView.setMarkerData(mMarker);

        mAdapter = new ArrayAdapter<Integer>(getActivity(), R.layout.simple_text_view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mShowKeyBoard) {
            showKeyboard(mHeaderView.getEditText());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        b.putBoolean(EXTRA_MARKER_NEW, mMarkerNew);
        b.putSerializable(EXTRA_MARKER, mMarker);
    }

    @Override
    public void onFeaturePropertiesChecked(int id, boolean isChecked) {
        switch (id) {
        case R.id.llColorText:
            selectColor(true);
            break;

        case R.id.llColorBack:
            selectColor(false);
            break;

        case R.id.cbClose:
            mMarker.setIsUppercase(isChecked);
            break;

        default:
            break;
        }
    }

    private void selectColor(boolean text) {
        mColorText = text;

        final String markerColor = text ? mMarker.getTextColor() : mMarker.getBackColor();
        final int color;
        if (markerColor == null || Marker.NO_COLOR.equals(markerColor)) {
            color = mSettings.isThemeDark() ? (text ? Color.WHITE : Color.BLACK) : (text ? Color.BLACK : Color.WHITE);

        } else {
            color = Color.parseColor(markerColor);
        }

        new SetColorDialogBuilder(getActivity(), color, this).show();
    }

    @Override
    protected View getListViewHeader() {
        return mHeaderView;
    }

    @Override
    protected ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getActionBarTitle() {
        if (mMarkerNew || mMarker == null || TextUtils.isEmpty(mMarker.getName())) {
            return R.string.marker_new;

        } else {
            return R.string.marker_properties;
        }
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.marker_white;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return false;
    }

    @Override
    protected boolean onAddFeatureClick() {
        return false;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        final String name = mHeaderView.getName().trim();
        inputHide(mHeaderView);
        if (TextUtils.isEmpty(name)) {
            Utils.showToast(getActivity(), R.string.t_error_feature_name);
            return false;

        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveMarker(name);
                }
            }).start();
            return true;
        }
    }

    private void saveMarker(String name) {
        mMarker.setName(name);

        mMarker.setUsn(0);
        mMarker.setUsnName(mMarker.getUsnName() + 1);
        mMarker.setUsnTextColor(mMarker.getUsnTextColor() + 1);
        mMarker.setUsnBackColor(mMarker.getUsnBackColor() + 1);

        if (mMarkerNew) {
            mMarker.setId(UUID.randomUUID());
            mMarker.setCreator(LTSettings.getInstance().getUserName());
            mMarker.setOrder(mSettings.getLastFeatureOrder() + 1);
            mMarker.setUsnOrder(mMarker.getUsnOrder() + 1);

        }

        try {
            if (mMarkerNew) {
                mDbHelper.getMarkerDao().create(mMarker);

            } else {
                mDbHelper.getMarkerDao().update(mMarker);
            }

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        MarkerCache.getInstance(mApp).updateCache(mMarker);
        mApp.getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);

    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) {
        return false;
    }

    @Override
    protected void onDialogPositiveButton() {}

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {}

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        final String hexColor = SetColorDialogBuilder.NO_COLOR == color ? Marker.NO_COLOR : Utils.getColor(color);

        if (mColorText) {
            mMarker.setTextColor(hexColor);
            mHeaderView.setColorText(hexColor);

        } else {
            mMarker.setBackColor(hexColor);
            mHeaderView.setColorBack(hexColor);
        }
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}