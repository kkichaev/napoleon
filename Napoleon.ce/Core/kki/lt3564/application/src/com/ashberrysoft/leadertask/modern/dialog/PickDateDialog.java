package com.ashberrysoft.leadertask.modern.dialog;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;

public class PickDateDialog extends BaseDialog//
        implements OnDateSetListener {

    public static final int CODE = R.id.dialog_pick_date;
    private static final String EXTRA_DATE = "EXTRA_DATE";

    // VALUE's
    private Calendar mCalendar;
    boolean mFired;

    public static PickDateDialog newInstance(Fragment target, long date) {
        final Bundle b = new Bundle(1);
        b.putLong(EXTRA_DATE, date == 0 ? System.currentTimeMillis() : date);

        final PickDateDialog d = new PickDateDialog();
        d.setTargetFragment(target, 0);
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        final Bundle b = savedInstanceState == null ? getArguments() : savedInstanceState;
        mCalendar.setTimeInMillis(b.getLong(EXTRA_DATE));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int year = mCalendar.get(Calendar.YEAR);
        final int month = mCalendar.get(Calendar.MONTH);
        final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final int style = getSettings().isThemeDark() ? R.style.leadertask_DateTimeDialog_Dark : R.style.leadertask_DateTimeDialog_Light;

            return new DatePickerDialog(getActivity(), style, this, year, month, dayOfMonth);

        } else {
            return new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putLong(EXTRA_DATE, mCalendar.getTimeInMillis());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mFired) {
            return;

        } else {
            mFired = true;
        }

        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        receiveObjects(CODE, mCalendar.getTimeInMillis());
    }
}