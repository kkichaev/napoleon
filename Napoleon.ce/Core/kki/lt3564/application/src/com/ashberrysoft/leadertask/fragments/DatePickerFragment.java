package com.ashberrysoft.leadertask.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.dialogs.SetTermDialog;

/**
 * Класс для установки дат через DatePicker.
 * 
 */
public class DatePickerFragment extends DialogFragment {
    private static final String KEY_DATE = "date";
    private Date mDate;

    public static DatePickerFragment newInstance(Date date) {
        final Bundle b = new Bundle();
        b.putLong(KEY_DATE, date == null ? System.currentTimeMillis() : date.getTime());

        final DatePickerFragment d = new DatePickerFragment();
        d.setArguments(b);

        return d;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = new Date(getArguments().getLong(KEY_DATE));
        final Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cl.setTime(mDate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            int style = 0;
            if (((LTApplication) getActivity().getApplicationContext()).getSettings().isThemeDark())
                style = R.style.leadertask_DateTimeDialog_Dark;
            else
                style = R.style.leadertask_DateTimeDialog_Light;
            return new DatePickerDialog(getActivity(), style, mDateSetListener, cl.get(Calendar.YEAR),
                    cl.get(Calendar.MONTH), cl.get(Calendar.DAY_OF_MONTH));
        } else
            return new DatePickerDialog(getActivity(), mDateSetListener, cl.get(Calendar.YEAR), cl.get(Calendar.MONTH),
                    cl.get(Calendar.DAY_OF_MONTH));

    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        /*
         * for Android 4.1 devices onDateSet() method called twice therefore we add flag to resolve this behavior
         */
        boolean fired = false;

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (fired == true)
                return;
            else
                fired = true;
            final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            c.setTime(mDate);
            final Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cl.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
            cl.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
            cl.set(Calendar.SECOND, c.get(Calendar.SECOND));
            cl.set(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND));
            cl.set(Calendar.YEAR, year);
            cl.set(Calendar.MONTH, monthOfYear);
            cl.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mDate = cl.getTime();
            if (getTargetFragment() != null) {
                try {
                    ((LTBaseFragment) getTargetFragment()).onFragmentResult(mDate, getTargetRequestCode());
                } catch (ClassCastException e) {
                    ((SetTermDialog) getTargetFragment()).dateChanged(mDate);
                }
            }
        }
    };
}