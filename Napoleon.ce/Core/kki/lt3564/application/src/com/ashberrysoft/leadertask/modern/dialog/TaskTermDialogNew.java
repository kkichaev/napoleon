package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskTermDialogNew extends BaseDialog//
        implements View.OnClickListener, DialogInterface.OnClickListener, ObjectsReceiver {

    private static final SimpleDateFormat SDF_24H = getSimpleDateFormat(true);
    private static final SimpleDateFormat SDF_12H = getSimpleDateFormat(false);

    public static final int CODE = R.id.dialog_task_term_new;
    private static final String EXTRA_TASK = "EXTRA_TASK";
    private static final String EXTRA_END = "EXTRA_END";

    // VIEW's
    private TextView mSelectedDate;
    private TextView mSelectedTime;
    private TextView mSelectedTimeEnd;

    // VALUE's
    private LTask mTask;

    // VIEW
    private LTCalendarView mCalendarView;

    private TimeHelper mTimeHelper;
    private StringBuilder mStringBuilder;
    private StringBuilder mStringBuilder2;
    private Calendar mCalendar;
    private Date mDate;
    private Date mDateEnd;
    private int mDateOffset;
    private static Fragment mFragment;
    private boolean mEndCheck = false;
    private static int halfHour = 30*60*1000;

    public static TaskTermDialogNew newInstance(Fragment fragment, LTask task) {
        final Bundle b = new Bundle(2);
        b.putSerializable(EXTRA_TASK, task.clone());
        b.putBoolean(EXTRA_END, false);
        final TaskTermDialogNew d = new TaskTermDialogNew();

        d.setTargetFragment(fragment, 0);
        d.setArguments(b);
        mFragment = fragment;

        return d;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        mTask = (LTask) bundle.getSerializable(EXTRA_TASK);
        mEndCheck = (boolean) bundle.getBoolean(EXTRA_END, false);

        mTimeHelper = TimeHelper.getInstance();
        mStringBuilder = new StringBuilder();
        mStringBuilder2 = new StringBuilder();
        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        mDate = new Date();
        mDateEnd = new Date();
        //mDateOffset = new Date().getTimezoneOffset()/60;
        mDateOffset = mDate.getTimezoneOffset()/60;

    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(getSettings().getLTCalendarWidth(), ActionBar.LayoutParams.WRAP_CONTENT);
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_term_dialog_new_new, null);

        mSelectedDate = (TextView) v.findViewById(R.id.btn_select_date);
        mSelectedTime = (TextView) v.findViewById(R.id.btn_select_time);
        mSelectedTimeEnd = (TextView) v.findViewById(R.id.btn_select_time_end);
        v.findViewById(R.id.btn_without_term).setOnClickListener(this);
        v.findViewById(R.id.btn_without_time).setOnClickListener(this);
        mSelectedDate.setOnClickListener(this);
        mSelectedTime.setOnClickListener(this);
        mSelectedTimeEnd.setOnClickListener(this);
        v.setLayoutParams(lp2);
        updateViews();

        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(getSettings().getLTCalendarWidth(), ActionBar.LayoutParams.WRAP_CONTENT);
        mCalendarView = new LTCalendarView(getActivity(), new LTCalendarView.OnCalendarDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                if (mTask.getTermBegin() != 0 && !mTimeHelper.isWholeDayTask(mTask, true)) {
                    Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
                    calendar.setTime(new Date(Long.valueOf(mTask.getTermBegin())));

                    mCalendar.setTimeInMillis(date.getTime());
                    mCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    mCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));

                    mTask.setTermBegin(mCalendar.getTimeInMillis());
                    mTask.setTermEnd(mCalendar.getTimeInMillis()/*+halfHour*/);
                }
                else {
                    mTask.setTermBegin(setTimeTo(Long.valueOf(date.getTime()), true));
                    mTask.setTermEnd(setTimeTo(Long.valueOf(date.getTime()), false));
                }

                getSettings().setFilterSelectedDate(mCalendar.getTimeInMillis());
                mCalendarView.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, -1);

                updateViews();
            }

            @Override
            public void fillLostData(List<Calendar> lostData) {

            }

            @Override
            public void restartLoaderCallback() {

            }
        },false);
        mCalendarView.setLayoutParams(lp);
        if(mTask.getTermBegin() != 0) {
            mCalendarView.setDate(0, new Date(Long.valueOf(mTask.getTermBegin())).getMonth(), mCalendar, -1);
            mCalendarView.setChosenDate(new Date(Long.valueOf(mTask.getTermBegin())));
        }
        else {
            mCalendarView.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), null, -1);
        }

        final int slidingCustomWidth;
        if (getSettings().isShowWeekCountInCalendar()) {
            slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum_andweek);
        } else {
            slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);
        }

        FrameLayout.LayoutParams lp23 = new FrameLayout.LayoutParams(slidingCustomWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        mCalendarView.setLayoutParams(lp23);
        //

        final LinearLayout llmain = new LinearLayout(getActivity());
        llmain.setOrientation(LinearLayout.VERTICAL);
        llmain.setGravity(Gravity.CENTER_HORIZONTAL);
        llmain.setBackgroundColor(Color.WHITE);
        llmain.addView(mCalendarView);
        llmain.addView(v);

        final ScrollView sv = new ScrollView(getActivity());
        sv.addView(llmain);

        /*final LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);
        ll.addView(sv);*/


        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(sv);
        ad.setCancelable(true);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);
        return ad.show();
    }
/*
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            ((TasksFragment)mFragment).showKeyboard();
        } catch (Exception e) {

        }
    }
*/
    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable(EXTRA_TASK, mTask);
        mCalendar = mCalendarView.getChosenDate();
    }

    private void updateViews() {
        if (mTask.getTermBegin() == 0 && mTask.getTermEnd() == 0) {
            mSelectedDate.setText(R.string.select_date);
            mSelectedDate.setTextColor(Color.GRAY);

            mSelectedTime.setText(R.string.select_time);
            mSelectedTime.setTextColor(Color.GRAY);
            mSelectedTimeEnd.setText(R.string.select_time);
            mSelectedTimeEnd.setTextColor(Color.GRAY);
        } else {
            mSelectedDate.setTextColor(Color.BLACK);
            Utils.clearStringBuilder(mStringBuilder);
            Utils.clearStringBuilder(mStringBuilder2);
            mDate.setTime(mTask.getTermBegin());
            mDateEnd.setTime(mTask.getTermEnd());

            Date tmpmDate = mDate;
            tmpmDate.setHours(mDate.getHours()+mDateOffset);

            Date tmpmDate2 = mDateEnd;
            tmpmDate2.setHours(mDateEnd.getHours()+mDateOffset);


            mStringBuilder.append(mTimeHelper.getDateForSyncOrSimple2(tmpmDate, Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE), getActivity(), true, false));
            mStringBuilder2.append(mTimeHelper.getDateForSyncOrSimple2(tmpmDate2, Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE), getActivity(), true, false));

            final SimpleDateFormat sdf = DateFormat.is24HourFormat(getApp()) ? SDF_24H : SDF_12H;
            mDate.setTime(mTask.getTermBegin());
            mDateEnd.setTime(mTask.getTermEnd());

            if (mTimeHelper.isWholeDayTask(mTask, true)) {
                mSelectedTime.setTextColor(Color.GRAY);
                mSelectedTime.setText(R.string.select_time);

                mSelectedTimeEnd.setTextColor(Color.GRAY);
                mSelectedTimeEnd.setText(R.string.select_time);

            }
            else {
                mSelectedTime.setTextColor(Color.BLACK);
                mSelectedTime.setText(sdf.format(mDate));

                mSelectedTimeEnd.setTextColor(Color.BLACK);
                mSelectedTimeEnd.setText(sdf.format(mDateEnd));
            }

            mSelectedDate.setText(mStringBuilder);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (getSettings().getUserName().equals(mTask.getEmailCustomer())) {
                mTask.setTermBeginCustomer(mTask.getTermBegin());
                mTask.setTermEndCustomer(mTask.getTermEnd());
                mTask.setUsnFieldCustomerTerm(mTask.getUsnFieldCustomerTerm() + 1);
            }
            mTask.setUsnFieldTerm(mTask.getUsnFieldTerm() + 1);

            receiveObjects(CODE, mTask);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_without_term:
            mTask.setTermBegin(0);
            mTask.setTermEnd(0);
            mCalendarView.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), null, -1);
            break;

        case R.id.btn_without_time:
            if (mTask.getTermBegin() != 0) {
                mTask.setTermBegin(setTimeTo(mTask.getTermBegin(), true));
                mTask.setTermEnd(setTimeTo(mTask.getTermEnd(), false));
            }
            break;

        case R.id.btn_select_date:
            PickDateDialog.newInstance(this, mTask.getTermBegin()).showDialog(getFragmentManager());
            return;

        case R.id.btn_select_time:
            mEndCheck = false;
            if(mTask.getTermBegin() != 0 ) {
                // term is exist
                if (mTimeHelper.isWholeDayTask(mTask, true)) {
                    mCalendar.setTimeInMillis(mTask.getTermBegin());
                    mCalendar.set(Calendar.HOUR_OF_DAY, 18);
                    mCalendar.set(Calendar.MINUTE, 0);
                    PickTimeDialog.newInstance(this, mCalendar.getTimeInMillis()).showDialog(getFragmentManager());
                } else {
                    PickTimeDialog.newInstance(this, mTask.getTermBegin()).showDialog(getFragmentManager());
                }
            }
            else {
                mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                //add1TenMins();
                PickTimeDialog.newInstance(this, mCalendar.getTimeInMillis()).showDialog(getFragmentManager());
            }
            return;

        case R.id.btn_select_time_end:
            mEndCheck = true;
            if(mTask.getTermEnd() != 0 ) {
                // term is exist
                if (mTimeHelper.isWholeDayTask(mTask, true)) {
                    mCalendar.setTimeInMillis(mTask.getTermEnd());
                    mCalendar.set(Calendar.HOUR_OF_DAY, 18);
                    mCalendar.set(Calendar.MINUTE, 0);
                    PickTimeDialog.newInstance(this, mCalendar.getTimeInMillis()).showDialog(getFragmentManager());
                } else {
                    PickTimeDialog.newInstance(this, mTask.getTermEnd()).showDialog(getFragmentManager());
                }
            }
            else {
                mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                //add1TenMins();
                PickTimeDialog.newInstance(this, mCalendar.getTimeInMillis()).showDialog(getFragmentManager());
            }
            return;

        default:
            break;
        }

        updateViews();
    }

    private void add1TenMins() {
        if (mCalendar.getTime().getMinutes() > 49) {
            mCalendar.add(Calendar.HOUR, 1);
            mCalendar.set(Calendar.MINUTE, mCalendar.getTime().getMinutes()+10-60);
        } else {
            mCalendar.add(Calendar.MINUTE, 10);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
    }

    private long setTimeTo(long date, boolean startOfDay) {
        mCalendar.setTimeInMillis(date == 0 ? System.currentTimeMillis() : date);
        return TimeHelper.roundCalendar(mCalendar, startOfDay).getTimeInMillis();
    }

    private static SimpleDateFormat getSimpleDateFormat(boolean is24h) {
        final SimpleDateFormat sdf = new SimpleDateFormat(is24h ? "HH:mm" : "hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeHelper.DEFAULT_TIME_ZONE);

        return sdf;
    }

    @Override
    public void onReceivingObjects(int code, Object... objects) {
        switch (code) {
        case PickDateDialog.CODE: {
            final long date = (long) objects[0];
            if (date == mTask.getTermBegin()) {
                return;
            }

            if (mTask.getTermBegin() != 0 && !mTimeHelper.isWholeDayTask(mTask, true)) {
                mTask.setTermBegin(date);
                mTask.setTermEnd(date/*+halfHour*/);

            } else {
                mTask.setTermBegin(date);
                mTask.setTermEnd(date);
                mTask.setTermBegin(setTimeTo(date, true));
                mTask.setTermEnd(setTimeTo(date, false));
            }
            mCalendarView.setDate(0, new Date(Long.valueOf(mTask.getTermBegin())).getMonth(), mCalendar, -1);
            mCalendarView.setChosenDate(new Date(Long.valueOf(mTask.getTermBegin())));
        }
            break;

        case PickTimeDialog.CODE:
            if (!mEndCheck) {
                long date = (long) objects[0];
                if (date == mTask.getTermBegin() && !mTimeHelper.isWholeDayTask(mTask, true)) {
                    return;
                }
                boolean wasAllDayTask = mTimeHelper.isWholeDayTask(mTask, true);
                mTask.setTermBegin(date);
                //mTask.setTermEnd(date/*+halfHour*/);
                mCalendarView.setDate(0, new Date(Long.valueOf(mTask.getTermBegin())).getMonth(), mCalendar, -1);
                mCalendarView.setChosenDate(new Date(Long.valueOf(mTask.getTermBegin())));

                if ( mTask.getTermEnd() - mTask.getTermBegin() < halfHour) {
                    Date date1 = new Date(mTask.getTermBegin()+halfHour);
                    date1.setHours(date1.getHours()+mDateOffset);
                    Date date2 = new Date(mTask.getTermBegin());
                    date2.setHours(date2.getHours()+mDateOffset);
                    if (date1.getDay() > date2.getDay()) {
                        mCalendar.setTimeInMillis(mTask.getTermBegin());
                        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
                        mCalendar.set(Calendar.MINUTE, 59);

                        mTask.setTermEnd(mCalendar.getTimeInMillis());
                    } else {
                        mTask.setTermEnd(mTask.getTermBegin()+halfHour);
                    }
                } else {
                    if (mTask.getTermEnd() == 0 || wasAllDayTask) {
                        mTask.setTermEnd(mTask.getTermBegin()+halfHour);
                    }
                }

            } else {
                long date = (long) objects[0];
                if (date == mTask.getTermEnd() && !mTimeHelper.isWholeDayTask(mTask, true)) {
                    return;
                }
                boolean wasAllDayTask = mTimeHelper.isWholeDayTask(mTask, true);

               // mTask.setTermBegin(date);
                mTask.setTermEnd(date/*+halfHour*/);
                mCalendarView.setDate(0, new Date(Long.valueOf(mTask.getTermEnd())).getMonth(), mCalendar, -1);
                mCalendarView.setChosenDate(new Date(Long.valueOf(mTask.getTermEnd())));

                if ( mTask.getTermEnd() - mTask.getTermBegin() < halfHour) {
                    //
                    Date date1 = new Date(mTask.getTermEnd()-halfHour);
                    date1.setHours(date1.getHours()+mDateOffset);
                    Date date2 = new Date(mTask.getTermEnd());
                    date2.setHours(date2.getHours()+mDateOffset);
                    if (date1.getDay() < date2.getDay()) {
                        mCalendar.setTimeInMillis(mTask.getTermEnd());
                        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        mCalendar.set(Calendar.MINUTE, 0);

                        mTask.setTermBegin(mCalendar.getTimeInMillis());
                    } else {
                        mTask.setTermBegin(mTask.getTermEnd()-halfHour);
                    }
                } else {
                    if (mTask.getTermBegin() == 0 || wasAllDayTask) {
                        mTask.setTermBegin(mTask.getTermEnd()-halfHour);
                    }
                }
            }
            break;

        default:
            return;
        }

        updateViews();
    }

}