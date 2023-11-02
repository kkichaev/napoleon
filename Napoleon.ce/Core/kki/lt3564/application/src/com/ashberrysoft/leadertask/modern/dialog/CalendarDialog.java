package com.ashberrysoft.leadertask.modern.dialog;

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
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.views.LTCalendarView;
import com.ashberrysoft.leadertask.views.LTCalendarView.OnCalendarDateSelectedListener;

public class CalendarDialog extends BaseDialog//
        implements DialogInterface.OnClickListener, OnCalendarDateSelectedListener {

    private static final String EXTRA_DATE = "EXTRA_DATE";
    public static final int CODE = R.id.dialog_calendar;

    // VIEW
    private LTCalendarView mCalendarView;

    // VALUE's
    private Calendar mCalendar;

    public static CalendarDialog newInstance(Fragment fragment, Date date) {
        final Bundle b = new Bundle(1);
        if (date != null) {
            b.putLong(EXTRA_DATE, date.getTime());
        }

        final CalendarDialog f = new CalendarDialog();
        f.setTargetFragment(fragment, 0);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTheme(R.style.AppBaseTheme);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        final Bundle bundle = b == null ? getArguments() : b;
        if (bundle.containsKey(EXTRA_DATE)) {
            mCalendar.setTimeInMillis(bundle.getLong(EXTRA_DATE));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle b) {
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(getSettings().getLTCalendarWidth(), LayoutParams.WRAP_CONTENT);

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
        ad.setNegativeButton(R.string.txt_just_no, null);

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
            receiveObjects(CODE, mCalendarView.getChosenDate().getTime());
        }
    }

    @Override
    public void onDateSelected(Date date) {}

    @Override
    public void fillLostData(List<Calendar> lostData) {
        LTCalendarView.fillCalendarData(getApp(), lostData);
    }

    @Override
    public void restartLoaderCallback() {
        getLoaderManager().restartLoader(mCalendarView.getLoaderCallbackId(), null, mCalendarView);
    }
}