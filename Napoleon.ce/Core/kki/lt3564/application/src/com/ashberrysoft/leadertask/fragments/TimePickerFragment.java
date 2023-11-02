package com.ashberrysoft.leadertask.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.dialogs.SetTermDialog;

/**
 * Класс для установки времени через TimePicker
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class TimePickerFragment extends DialogFragment {

    private static final String KEY_DATE = "date";

    // VALUE's
    private Date mDate;

    public static TimePickerFragment newInstance(Date date) {
        final Bundle b = new Bundle();
        b.putLong(KEY_DATE, date == null ? convertToGMT(System.currentTimeMillis()) : date.getTime());

        final TimePickerFragment d = new TimePickerFragment();
        d.setArguments(b);

        return d;
    }

    @SuppressLint("InlinedApi")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final long timeDate = getArguments().getLong(KEY_DATE, System.currentTimeMillis());

        mDate = new Date(timeDate);
        final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeDate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            int style = 0;
            if (LTSettings.getInstance(getActivity()).isThemeDark()) {
                style = R.style.leadertask_DateTimeDialog_Dark;
            } else {
                style = R.style.leadertask_DateTimeDialog_Light;
            }

            return new TimePickerDialog(getActivity(), style, mTimeSetListener,//
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        } else {
            return new TimePickerDialog(getActivity(), mTimeSetListener,//
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        }

    }

    private static long convertToGMT(long time) {
        final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

        final TimeZone timeZone = Calendar.getInstance().getTimeZone();
        final int offsetFromUTC = timeZone.getOffset(time);

        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MILLISECOND, offsetFromUTC);

        return calendar.getTimeInMillis();
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        /*
         * for Android 4.1 devices onTimeSet() method called twice therefore we
         * add flag to resolve this behavior
         */
        private boolean mFired = false;

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (mFired == true) {
                return;
            } else {
                mFired = true;
            }

            final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            c.setTime(mDate);

            final Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cl.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cl.set(Calendar.MINUTE, minute);
            cl.set(Calendar.SECOND, c.get(Calendar.SECOND));
            cl.set(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND));
            cl.set(Calendar.YEAR, c.get(Calendar.YEAR));
            cl.set(Calendar.MONTH, c.get(Calendar.MONTH));
            cl.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
            mDate = cl.getTime();

            if (getTargetFragment() != null) {
                ((SetTermDialog) getTargetFragment()).timeChanged(mDate);
            }
        }
    };
}
