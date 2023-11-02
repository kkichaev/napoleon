package com.ashberrysoft.leadertask.modern.dialog;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.widget.TimePicker;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;

import static android.R.attr.offset;

public class PickTimeDialog extends BaseDialog //
        implements OnTimeSetListener {

    public static final int CODE = R.id.dialog_pick_time;
    private static final String EXTRA_DATE = "EXTRA_DATE";

    // VALUE's
    private Calendar mCalendar;
    boolean mFired;

    public static PickTimeDialog newInstance(Fragment target, long date) {
        final Bundle b = new Bundle(1);
        b.putLong(EXTRA_DATE, date == 0 ? System.currentTimeMillis() : date);

        final PickTimeDialog d = new PickTimeDialog();
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
        final int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = mCalendar.get(Calendar.MINUTE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final int style = getSettings().isThemeDark() ? R.style.leadertask_DateTimeDialog_Dark : R.style.leadertask_DateTimeDialog_Light;

            return new TimePickerDialog(getActivity(), style, this, hourOfDay, minute, DateFormat.is24HourFormat(getApp()));

        } else {
            return new TimePickerDialog(getActivity(), this, hourOfDay, minute, DateFormat.is24HourFormat(getApp()));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putLong(EXTRA_DATE, mCalendar.getTimeInMillis());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mFired) {
            return;

        } else {
            mFired = true;
        }

        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        Date date = new Date(mCalendar.getTimeInMillis());
        int offset = date.getTimezoneOffset()/60;
        date.setHours(date.getHours());

        receiveObjects(CODE, mCalendar.getTimeInMillis());
    }
}