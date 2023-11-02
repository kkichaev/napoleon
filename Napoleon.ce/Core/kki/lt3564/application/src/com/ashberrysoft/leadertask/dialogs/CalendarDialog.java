package com.ashberrysoft.leadertask.dialogs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.views.LTCalendarView;
import com.ashberrysoft.leadertask.views.LTCalendarView.OnCalendarDateSelectedListener;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CalendarDialog extends DialogFragment implements DialogInterface.OnClickListener, OnCalendarDateSelectedListener {

    private static final String CLASS_PATH = CalendarDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_DATE = CLASS_PATH + "EXTRA_DATE";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.calendar_dialog_request_code;

    // VIEW
    private LTCalendarView mCalendarView;

    // VALUE's
    private LTApplication mApp;
    private Calendar mCalendar;

    public static CalendarDialog newInstance(Fragment fragment, Date date) {
        final Bundle b = new Bundle();
        if (date != null) {
            b.putLong(EXTRA_DATE, date.getTime());
        }

        final CalendarDialog f = new CalendarDialog();
        f.setTargetFragment(fragment, REQUEST_CODE);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mApp = (LTApplication) getActivity().getApplicationContext();

        mCalendar = Calendar.getInstance();
        final Bundle bundle = b == null ? getArguments() : b;
        if (bundle.containsKey(EXTRA_DATE)) {
            mCalendar.setTimeInMillis(bundle.getLong(EXTRA_DATE));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle b) {
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mApp.getSettings().getLTCalendarWidth(), LayoutParams.WRAP_CONTENT);

        mCalendarView = new LTCalendarView(getActivity());
        mCalendarView.setLayoutParams(lp);
        mCalendarView.setCustomListener(this);
        mCalendarView.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, -1);

        final ScrollView sv = new ScrollView(getActivity());
        sv.addView(mCalendarView);

        final LinearLayout ll = new LinearLayout(getActivity());
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setBackgroundColor(Color.WHITE);
        ll.addView(sv);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(ll);
        ad.setPositiveButton(R.string.txt_just_yes, this);
        ad.setNegativeButton(R.string.txt_just_no, this);

        final Dialog d = ad.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return d;
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        mCalendar = mCalendarView.getChosenDate();
        if (mCalendar != null) {
            b.putLong(EXTRA_DATE, mCalendar.getTimeInMillis());
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE && getTargetFragment() != null) {
            if (getTargetFragment() instanceof BaseFragment) {
                ((BaseFragment<?, ?>) getTargetFragment())//
                        .onFragmentResult(mCalendarView.getChosenDate().getTime(), REQUEST_CODE);
            }
        }
    }

    @Override
    public void onDateSelected(Date date) {}

    @Override
    public void fillLostData(List<Calendar> lostData) {
        LTCalendarView.fillCalendarData(mApp, lostData);
    }

    @Override
    public void restartLoaderCallback() {
        getLoaderManager().restartLoader(mCalendarView.getLoaderCallbackId(), null, mCalendarView);
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
}