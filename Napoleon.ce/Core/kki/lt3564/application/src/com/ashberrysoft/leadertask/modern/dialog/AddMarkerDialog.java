package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.fragment.BaseSyncStatusFragment;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class AddMarkerDialog extends BaseDialog {

    public static final int CODE = R.id.dialog_add_marker;
    private static final String CLASS_PATH = AddMarkerDialog.class.getSimpleName();

    // ADAPTER
    private EditText editText1;
    private View mBg;
    private String mDefaultColor;
    private static Context mContext;


    public static AddMarkerDialog newInstance(Fragment target) {
        final AddMarkerDialog d = new AddMarkerDialog();
        mContext = target.getActivity().getApplicationContext();
        d.setTargetFragment(target, CODE);
        final Bundle b = new Bundle();
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final Bundle bundle = getArguments();
        if ( bundle != null) {

        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_marker_category, null);
        editText1 = (EditText) v.findViewById((R.id.editText1));
        mDefaultColor = getDefaultColor();
        mBg = (View) v.findViewById(R.id.backColor);
        mBg.setBackgroundColor(Color.parseColor(mDefaultColor));
        mBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefaultColor = getDefaultColor();
                mBg.setBackgroundColor(Color.parseColor(mDefaultColor));
            }
        });

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = editText1.getText().toString().trim();

                if(s.length() > 0) {
                    Marker marker = new Marker();
                    setMarkerParams(marker);
                    marker.setName(s);
                    saveMarker(marker);
                    if (getTargetFragment() instanceof BaseSyncStatusFragment) {
                        ((BaseSyncStatusFragment) getTargetFragment()).onFragmentResult(marker, CODE);
                    }
                    getDialog().dismiss();
                }
            }
        });
        ad.setNegativeButton(R.string.btn_cancel, null);

        Dialog d = ad.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
        return d;
    }
    
    
    private void setMarkerParams(Marker marker) {
        marker.setUsn(0);
        marker.setUsnName(marker.getUsnName() + 1);

        String defaultColor = getDefaultColor();

        marker.setBackColor(mDefaultColor);
        marker.setTextColor("#fff");

        marker.setId(UUID.randomUUID());
        marker.setCreator(LTSettings.getInstance().getUserName());

        marker.setOrder(getOrder(mContext) + 1);
        marker.setUsnOrder(marker.getUsnOrder() + 1);
        //
        MarkerCache.getInstance(getApp()).updateCache(marker);
        getApp().getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
    }

    private int getOrder(Context context) {
        int order = 0;

        final List<Marker> markers;
        try {
            markers = DbHelper.getInstance(context).getMarkerDao().queryForAll();
        } catch (SQLException e) {
            return order;
        }

        order = markers.size();
        return order;
    }

    private void saveMarker(Marker marker) {
        try {
            DbHelper.getInstance(getApp()).getMarkerDao().create(marker);
            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(mContext);
            linkHelper.createTotalLink(marker);

            mContext.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
        } catch (SQLException e) {
            Utils.toLog(e);
        }

        Utils.startSync(getApp());
    }

    private String getDefaultColor() {
        String defaultColors[]={"#DF0C0C","#FF8C68","#CD5F00","#965500","#FFEB00","#878700","#73D246","#008C8C","#5ACDFF","#0F5FFF","#5F32BE","#A05AB9","#FF4BC8","#5A0046","#BEBEBE","#465069"};
        return defaultColors[new Random().nextInt(defaultColors.length)];
    }

}