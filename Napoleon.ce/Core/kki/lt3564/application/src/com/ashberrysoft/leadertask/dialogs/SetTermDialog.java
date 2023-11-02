package com.ashberrysoft.leadertask.dialogs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.fragments.DatePickerFragment;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;
import com.ashberrysoft.leadertask.fragments.TimePickerFragment;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * Диалог для установления срока задачи
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SetTermDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final SimpleDateFormat SDF_24H = getSimpleDateFormat(true);
    private static final SimpleDateFormat SDF_12H = getSimpleDateFormat(false);

    private static final String CLASS_PATH = SetTermDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_TASK = CLASS_PATH + "EXTRA_PERFORMER";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.term_dialog_request_code;

    // VIEW's
    private TextView mSelectedTerm;
    private Button mToday;
    private Button mTomorrow;
    private Button m9Hours;
    private Button m18Hours;

    // VALUE's
    private LTApplication mApp;
    private Task mTask;
    private long mLastClickTime;

    public static SetTermDialog newInstance(Fragment fragment, Task task) {
        final Bundle b = new Bundle();
        b.putSerializable(EXTRA_TASK, task);

        final SetTermDialog d = new SetTermDialog();
        d.setTargetFragment(fragment, REQUEST_CODE);
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);

        mApp = (LTApplication) getActivity().getApplicationContext();

        final Bundle bundle = b != null ? b : getArguments();
        mTask = (Task) bundle.getSerializable(EXTRA_TASK);
    }

    @Override
    public Dialog onCreateDialog(Bundle b) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_term_dialog, null);

        mSelectedTerm = (TextView) v.findViewById(R.id.selected_term);
        mToday = (Button) v.findViewById(R.id.btn_today);
        mTomorrow = (Button) v.findViewById(R.id.btn_tomorrow);
        m9Hours = (Button) v.findViewById(R.id.btn_9_hours);
        m18Hours = (Button) v.findViewById(R.id.btn_18_hours);

        v.findViewById(R.id.btn_without_term).setOnClickListener(this);
        v.findViewById(R.id.btn_without_time).setOnClickListener(this);
        v.findViewById(R.id.btn_select_date).setOnClickListener(this);
        v.findViewById(R.id.btn_select_time).setOnClickListener(this);
        mToday.setOnClickListener(this);
        mTomorrow.setOnClickListener(this);
        m9Hours.setOnClickListener(this);
        m18Hours.setOnClickListener(this);

        updateViews();

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setCancelable(true);
        ad.setTitle(R.string.task_term);
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
        b.putSerializable(EXTRA_TASK, mTask);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (getTargetFragment() != null && which == Dialog.BUTTON_POSITIVE) {
            if (getTargetFragment() instanceof LTBaseFragment) {
                // setBlock(true);
                onResultReturn();
            }
        } else {
            dismiss();
        }
    }

    private void onResultReturn() {
        if (mApp.getSettings().getUserName().equals(mTask.getCustomer())) {
            mTask.setTermCustomerBegin(mTask.getTermBegin());
            mTask.setTermCustomerEnd(mTask.getTermEnd());
        }
        ((LTBaseFragment) getTargetFragment()).onFragmentResult(mTask, REQUEST_CODE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DbHelper.getInstance(getActivity()).editsDueToTermChanged(mApp);
                //
                // mHandler.post(new Runnable() {
                // @Override
                // public void run() {
                // setBlock(false);
                // }
                // });
            }
        }).start();
    }

    // private void setBlock(boolean setBlock) {
    // if (getActivity() != null && getActivity() instanceof BaseSlidingActivity) {
    // ((BaseSlidingActivity) getActivity()).setBlockingProcess(setBlock, null);
    // }
    // }

    // save selected date from DatePickerDialog
    public void dateChanged(Date date) {
        if (mTask.getTermBegin() != null && !Utils.wholeDayTask(mTask, true)) {
            mTask.setTermBegin(date);
            mTask.setTermEnd(date);
        } else {
            mTask.setTermBegin(date);
            mTask.setTermEnd(date);
            mTask.setTermBegin(setMinTime(date));
            mTask.setTermEnd(setMaxTime(date));
        }

        updateViews();
    }

    // save selected time from TimePickerDialog
    public void timeChanged(Date date) {
        mTask.setTermBegin(date);
        mTask.setTermEnd(date);
        updateViews();
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();

        switch (v.getId()) {
        case R.id.btn_without_term:
            // 2. Если в диалоге отмечено "Без срока", то TermBegin = «01.01.1900 00:00:00» и
            // TermEnd = «01.01.9000 23:59:59» (без срока)
            // (vsh) даты не валидны в рамках Явы, поэтому используем null
            mTask.setTermBegin(null);
            mTask.setTermEnd(null);
            break;

        case R.id.btn_without_time:
            // 1. Если в диалоге отмечено "Без времени", то TermBegin = «xx.xx.xxxx 00:00:00» и
            // TermEnd = «xx.xx.xxxx 23:59:59» (где xx.xx.xxxx = выбранная дата)
            if (mTask.getTermBegin() != null) {
                mTask.setTermBegin(setMinTime(mTask.getTermBegin()));
                mTask.setTermEnd(setMaxTime(mTask.getTermEnd()));
            }
            break;

        case R.id.btn_today:
            if (mToday.getText().toString().contains(getString(R.string.btn_today))) {
                mTask.setTermBegin(setMinTime(new Date()));
                mTask.setTermEnd(setMaxTime(new Date()));
                mTask.setTermBegin(setToday(mTask.getTermBegin(), 0));
                mTask.setTermEnd(setToday(mTask.getTermEnd(), 0));
            } else {
                if (Utils.wholeDayTask(mTask, true)) {
                    mTask.setTermBegin(setMinTime(mTask.getTermBegin()));
                    mTask.setTermEnd(setMaxTime(mTask.getTermEnd()));
                }
                scrollDates(Calendar.DAY_OF_YEAR, 1);
            }
            break;

        case R.id.btn_tomorrow:
            if (mTomorrow.getText().toString().contains(getString(R.string.btn_tomorrow))) {
                mTask.setTermBegin(setMinTime(new Date()));
                mTask.setTermEnd(setMaxTime(new Date()));
                mTask.setTermBegin(setToday(mTask.getTermBegin(), 1));
                mTask.setTermEnd(setToday(mTask.getTermEnd(), 1));
            } else {
                if (Utils.wholeDayTask(mTask, true)) {
                    mTask.setTermBegin(setMinTime(mTask.getTermBegin()));
                    mTask.setTermEnd(setMaxTime(mTask.getTermEnd()));
                }
                scrollDates(Calendar.DAY_OF_YEAR, -1);
            }
            break;

        case R.id.btn_9_hours:
            if (m9Hours.getText().toString().contains(getString(R.string.btn_9_hours_using_12_hours_format))
                    || m9Hours.getText().toString().contains(getString(R.string.btn_9_hours_using_24_hours_format))) {
                if (mTask.getTermBegin() == null) {
                    mTask.setTermBegin(setToday(mTask.getTermBegin(), 0));
                    mTask.setTermEnd(setToday(mTask.getTermEnd(), 0));
                }
                setTime(9);
            } else
                scrollTimes(Calendar.MINUTE, true);
            break;

        case R.id.btn_18_hours:
            if (m18Hours.getText().toString().contains(getString(R.string.btn_18_hours_using_12_hours_format))
                    || m18Hours.getText().toString().contains(getString(R.string.btn_18_hours_using_24_hours_format))) {
                if (mTask.getTermBegin() == null) {
                    mTask.setTermBegin(setToday(mTask.getTermBegin(), 0));
                    mTask.setTermEnd(setToday(mTask.getTermEnd(), 0));
                }
                setTime(18);
            } else
                scrollTimes(Calendar.MINUTE, false);
            break;

        case R.id.btn_select_date:
            final DialogFragment dateDialog = DatePickerFragment.newInstance(mTask.getTermBegin());
            dateDialog.setTargetFragment(this, 01);
            dateDialog.show(getFragmentManager(), "date_picker_dialog");
            break;

        case R.id.btn_select_time:
            final DialogFragment timeDialog = TimePickerFragment.newInstance(mTask.getTermBegin());
            timeDialog.setTargetFragment(this, 01);
            timeDialog.show(getFragmentManager(), "time_picker_dialog");
        default:
            break;
        }

        updateViews();
    }

    // set task time to particular time
    private void setTime(int hourOfDay) {
        final Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cl.setTime(mTask.getTermBegin());
        cl.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        mTask.setTermBegin(cl.getTime());
        mTask.setTermEnd(cl.getTime());
    }

    private void scrollDates(int timeunit, int i) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        if (mTask.getTermBegin() == null) {
            mTask.setTermBegin(new Date());
            mTask.setTermEnd(new Date());
        }

        if (timeunit != Calendar.DAY_OF_YEAR) {
            mTask.setTermEnd(new Date(mTask.getTermBegin().getTime()));
        }

        calendar.setTime(mTask.getTermBegin());
        calendar.add(timeunit, i);
        mTask.setTermBegin(calendar.getTime());

        calendar.setTime(mTask.getTermEnd());
        calendar.add(timeunit, i);
        mTask.setTermEnd(calendar.getTime());
    }

    // edit selected time
    private void scrollTimes(int timeunit, boolean isAddMinutes) {
        /*
         * if task has no term then create and initialize begin and end task term as current date/time
         */
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        if (mTask.getTermBegin() == null) {
            mTask.setTermBegin(new Date());
            mTask.setTermEnd(new Date());
        }

        // Utils.wholeDayTask(mTask) &&
        if (timeunit != Calendar.DAY_OF_MONTH) {
            mTask.setTermEnd(new Date(mTask.getTermBegin().getTime()));
        }

        // edit begin term
        calendar.setTime(mTask.getTermBegin());
        calendar = changeMinutes(calendar, isAddMinutes);
        mTask.setTermBegin(calendar.getTime());

        // edit end term
        calendar.setTime(mTask.getTermEnd());
        calendar = changeMinutes(calendar, isAddMinutes);
        mTask.setTermEnd(calendar.getTime());
    }

    // change minutes and hours (as necessary) of selected time
    private Calendar changeMinutes(Calendar calendar, boolean isAddMinutes) {
        if (isAddMinutes) {
            if (calendar.get(Calendar.MINUTE) < 30)
                calendar.set(Calendar.MINUTE, 30);
            else {
                calendar.set(Calendar.MINUTE, 0);
                if (calendar.get(Calendar.HOUR_OF_DAY) == 23)
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                else
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
            }
        } else {
            if (calendar.get(Calendar.MINUTE) <= 30)
                if (calendar.get(Calendar.MINUTE) == 0) {
                    calendar.set(Calendar.MINUTE, 30);
                    if (calendar.get(Calendar.HOUR_OF_DAY) == 0)
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                    else
                        calendar.add(Calendar.HOUR_OF_DAY, -1);
                } else
                    calendar.set(Calendar.MINUTE, 0);
            else
                calendar.set(Calendar.MINUTE, 30);
        }
        return calendar;
    }

    /**
     * Установливает текущую дату не меняя время.
     * 
     * @param date
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @return
     */
    private Date setToday(Date date, int offset) {
        if (date == null) {
            date = new Date();// throw new NullPointerException("Date is null");
        }
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        final Calendar today = Utils.getCalendarDateGMT(new Date());// Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, today.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        if (offset != 0) {
            calendar.add(Calendar.DAY_OF_YEAR, offset);
        }
        return calendar.getTime();
    }

    /**
     * Вывод даты и времени в диалог.
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     */
    private void updateViews() {
        if (mTask.getTermBegin() == null) {
            mSelectedTerm.setText(R.string.empty_term_or_time);
            mToday.setText(getString(R.string.btn_today));
            mTomorrow.setText(getString(R.string.btn_tomorrow));
            setTextFor9And18Btns();
        } else {
            StringBuilder builder = new StringBuilder();
            String day = Utils.getDayOfDate(getActivity(), mTask.getTermBegin());
            builder.append((day != null ? day + " : " : "") + Utils.amputationMonth(mTask.getTermBegin()));
            if (!(Utils.wholeDayTask(mTask, true))) {
                // со временем
                builder.append(", "
                        + (DateFormat.is24HourFormat(getActivity()) ? SDF_24H.format(mTask.getTermBegin()) : SDF_12H
                                .format(mTask.getTermBegin())));
                m9Hours.setText(getString(R.string.btn_plus_30_minutes));
                m18Hours.setText(getString(R.string.btn_minus_30_minutes));
            } else {
                setTextFor9And18Btns();
            }
            mSelectedTerm.setText(builder.toString());
            mToday.setText(getString(R.string.btn_plus_one_day));
            mTomorrow.setText(getString(R.string.btn_minus_one_day));
        }
    }

    // set text for "09:00" and "18:00" buttons depending on time format
    private void setTextFor9And18Btns() {
        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c1.set(Calendar.HOUR_OF_DAY, 9);
        c1.set(Calendar.MINUTE, 0);
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c2.set(Calendar.HOUR_OF_DAY, 18);
        c2.set(Calendar.MINUTE, 0);
        if (DateFormat.is24HourFormat(getActivity())) {
            m9Hours.setText(SDF_24H.format(c1.getTime()));
            m18Hours.setText(SDF_24H.format(c2.getTime()));
        } else {
            m9Hours.setText(SDF_12H.format(c1.getTime()));
            m18Hours.setText(SDF_12H.format(c2.getTime()));
        }
    }

    /**
     * сброс времени в хх.хх.хххх 23:59:59
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param mBeginDate2
     */
    private Date setMaxTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * сброс времени в хх.хх.хххх 00:00
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param mBeginDate2
     */
    private Date setMinTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
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

    private static SimpleDateFormat getSimpleDateFormat(boolean is24h) {
        final SimpleDateFormat sdf = new SimpleDateFormat(is24h ? "HH:mm" : "hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf;
    }
}