package com.ashberrysoft.leadertask.dialogs;

import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.MarkerAdapter;
import com.ashberrysoft.leadertask.cache.MarkersCacheHolder;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SetMarkerDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String CLASS_PATH = SetMarkerDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_MARKER_UID = CLASS_PATH + "EXTRA_MARKER";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.marker_dialog_request_code;

    // VALUE's
    private UUID mSelectedMarkerUUID;

    // ADAPTER
    private MarkerAdapter mAdapter;

    public static SetMarkerDialog newInstance(Fragment fragment, UUID markerUUID) {
        final Bundle b = new Bundle();
        if (markerUUID != null) {
            b.putString(EXTRA_MARKER_UID, markerUUID.toString());
        }

        final SetMarkerDialog f = new SetMarkerDialog();
        f.setTargetFragment(fragment, REQUEST_CODE);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        if (bundle != null && bundle.containsKey(EXTRA_MARKER_UID)) {
            mSelectedMarkerUUID = UUID.fromString(bundle.getString(EXTRA_MARKER_UID));
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final List<Marker> markers = MarkersCacheHolder.getInstance(getActivity()).getData();

        int selectedItem = -1;
        if (selectedItem < 0 && mSelectedMarkerUUID != null && markers != null) {
            for (int i = 0; i < markers.size(); i++) {
                if (mSelectedMarkerUUID.equals(markers.get(i).getId())) {
                    selectedItem = i;
                    break;
                }
            }
        }

        mAdapter = new MarkerAdapter(markers, selectedItem);
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_status_dialog, null);
        ((ListView) v.findViewById(android.R.id.list)).setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.menu_marker);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.txt_just_no, this);

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

        final Marker marker = mAdapter.getCheckedMarker();
        if (marker != null) {
            b.putString(EXTRA_MARKER_UID, marker.getId().toString());
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (getTargetFragment() != null && which == Dialog.BUTTON_POSITIVE) {
            if (getTargetFragment() instanceof LTBaseFragment) {
                ((LTBaseFragment) getTargetFragment()).onFragmentResult(mAdapter.getCheckedMarker(), REQUEST_CODE);
            }
        }

        dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    public void showDialog(FragmentManager manager) {
        if (manager.findFragmentByTag(DIALOG_TAG) == null) {
            super.show(manager, DIALOG_TAG);
        }
    }

    public static void setTargetFragment(Fragment target, FragmentManager manager) {
        final Fragment fragment = manager.findFragmentByTag(DIALOG_TAG);
        if (fragment != null && fragment instanceof DialogFragment) {
            fragment.setTargetFragment(target, REQUEST_CODE);
        }
    }
}