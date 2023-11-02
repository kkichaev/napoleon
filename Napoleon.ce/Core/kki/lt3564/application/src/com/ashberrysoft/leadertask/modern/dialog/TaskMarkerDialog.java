package com.ashberrysoft.leadertask.modern.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.MarkerAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;

public class TaskMarkerDialog extends BaseDialog//
        implements DialogInterface.OnClickListener {

    private static final String EXTRA_MARKER = "EXTRA_MARKER";
    private static final String EXTRA_MARKERS = "EXTRA_MARKERS";
    public static final int CODE = R.id.dialog_task_marker;

    // ADAPTER
    private MarkerAdapter mAdapter;
    private static Fragment mTarget;

    public static TaskMarkerDialog newInstance(Fragment fragment, String markerUid) {
        final Bundle b = new Bundle(1);
        b.putString(EXTRA_MARKER, markerUid);

        final TaskMarkerDialog f = new TaskMarkerDialog();
        f.setTargetFragment(fragment, CODE);
        f.setArguments(b);
        mTarget = fragment;

        return f;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        String selectedMarker;
        final List<Marker> markers;
        final List<Marker> data = new ArrayList<>();

        if (b != null) {
            selectedMarker = b.getString(EXTRA_MARKER);
            markers = (List<Marker>) b.getSerializable(EXTRA_MARKERS);

        } else {
            selectedMarker = getArguments().getString(EXTRA_MARKER);
            markers = MarkerCache.getInstance(getApp()).getAll();
            for (Marker c : markers) {
                if (LTSettings.getInstance().getUserName().equals(c.getCreator()) ) {
                    data.add(c);
                }
            }
        }

        int selectedItem = -1;
        int count = 0;
        if (selectedMarker != null) {
            for (Marker marker : data) {
                if (selectedMarker.toLowerCase().equals(String.valueOf(marker.getId()))) {
                    selectedItem = count;
                    break;
                }
                count++;
            }
        }

        mAdapter = new MarkerAdapter(data, selectedItem);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_status_dialog, null);
        final ListView lv = ((ListView) v.findViewById(R.id.list_status));
        lv.setAdapter(mAdapter);
        //
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.unboarding_dialog_footer, null);
        final TextView textView = (TextView) footer.findViewById(R.id.unbord_diag_text);
        if (mAdapter.isEmpty()) {;
            textView.setText(getResources().getString(R.string.unboarding_dialog_markers));
            textView.setVisibility(View.VISIBLE);
            ad.setNeutralButton(getResources().getString(R.string.add_color), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (getDialog() != null){
                        getDialog().cancel();
                        AddMarkerDialog.newInstance(mTarget).showDialog(getFragmentManager());
                    }
                }
            });
        } else {
            textView.setVisibility(View.GONE);
        }

        lv.addFooterView(footer);
        lv.setFooterDividersEnabled(false);
        ad.setView(v);
        //
        ad.setTitle(R.string.menu_marker);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, this);

        return ad.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        b.putString(EXTRA_MARKER, getMarkerUid());
        b.putSerializable(EXTRA_MARKERS, (Serializable) mAdapter.getData());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                receiveObjects(CODE, getMarkerUid());
                break;
            default:
                break;
        }
    }

    private String getMarkerUid() {
        final Marker marker = mAdapter.getCheckedMarker();
        return marker == null ? "" : String.valueOf(marker.getId()).toUpperCase();
    }
}