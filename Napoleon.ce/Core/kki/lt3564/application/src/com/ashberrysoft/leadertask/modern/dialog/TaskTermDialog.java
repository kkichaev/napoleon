package com.ashberrysoft.leadertask.modern.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;
import com.ashberrysoft.leadertask.modern.fragment.TasksFragment;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;

public class TaskTermDialog extends BaseDialog//
        implements View.OnClickListener, DialogInterface.OnClickListener, ObjectsReceiver {

    private static final SimpleDateFormat SDF_24H = getSimpleDateFormat(true);
    private static final SimpleDateFormat SDF_12H = getSimpleDateFormat(false);

    public static final int CODE = R.id.dialog_task_term;
    public static final int CODE2 = R.id.dialog_task_term2;
    private static int mCode;
    private static final String EXTRA_TASK = "EXTRA_TASK";

    // VIEW's
    private TextView mSelectedDate;
    private TextView mSelectedTime;

    // VALUE's
    private LTask mTask;

    // VIEW
    private LTCalendarView mCalendarView;

    private TimeHelper mTimeHelper;
    private StringBuilder mStringBuilder;
    private Calendar mCalendar;
    private Date mDate;
    private int mDateOffset;
    private static Fragment mFragment;

    public static TaskTermDialog newInstance(Fragment fragment, LTask task, boolean is2) {
        final Bundle b = new Bundle(1);
        b.putSerializable(EXTRA_TASK, task.clone());
        final TaskTermDialog d = new TaskTermDialog();

        if (is2) {
            mCode =CODE2;
        } else {
            mCode = CODE;
        }
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

        mTimeHelper = TimeHelper.getInstance();
        mStringBuilder = new StringBuilder();
        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        mDate = new Date();
        //mDateOffset = new Date().getTimezoneOffset()/60;
        mDateOffset = mDate.getTimezoneOffset()/60;

    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(getSettings().getLTCalendarWidth(), ActionBar.LayoutParams.WRAP_CONTENT);
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_term_dialog_new, null);

        mSelectedDate = (TextView) v.findViewById(R.id.btn_select_date);
        mSelectedTime = (TextView) v.findViewById(R.id.btn_select_time);
        v.findViewById(R.id.btn_without_term).setOnClickListener(this);
        v.findViewById(R.id.btn_without_time).setOnClickListener(this);
        mSelectedDate.setOnClickListener(this);
        mSelectedTime.setOnClickListener(this);
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
                    mTask.setTermEnd(mCalendar.getTimeInMillis()/*+30*60*1000*/);
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
        //

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



        /*Dialog dialog = ad.create();
        WindowManager.LayoutParams lp0 = new WindowManager.LayoutParams();
        lp0.copyFrom(dialog.getWindow().getAttributes());
        lp0.width = slidingCustomWidth+padding;
        lp0.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp0);*/

        //

        /*final int slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);
        final int padding = getResources().getDimensionPixelSize(R.dimen.slidingmenu_to_small);*/
        AlertDialog dialog = ad.create();
        WindowManager.LayoutParams lp0 = new WindowManager.LayoutParams();
        lp0.copyFrom(dialog.getWindow().getAttributes());
        lp0.width = slidingCustomWidth;
        lp0.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.create();
        //dialog.getWindow().setAttributes(lp0);

        return dialog;
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
        if (mTask.getTermBegin() == 0) {
            mSelectedDate.setText(R.string.select_date);
            mSelectedDate.setTextColor(Color.GRAY);
            mSelectedTime.setText(R.string.select_time);
            mSelectedTime.setTextColor(Color.GRAY);
        } else {
            mSelectedDate.setTextColor(Color.BLACK);
            Utils.clearStringBuilder(mStringBuilder);
            mDate.setTime(mTask.getTermBegin());

            SimpleDateFormat format = new SimpleDateFormat("EEE");
            Date tmpmDate = mDate;
            tmpmDate.setHours(mDate.getHours()+mDateOffset);

            mStringBuilder.append(mTimeHelper.getDateForSyncOrSimple2(tmpmDate, Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE), getActivity(), true, false));

            final SimpleDateFormat sdf = DateFormat.is24HourFormat(getApp()) ? SDF_24H : SDF_12H;
            mDate.setTime(mTask.getTermBegin());
            if (mTimeHelper.isWholeDayTask(mTask, true)) {
                mSelectedTime.setTextColor(Color.GRAY);
                mSelectedTime.setText(R.string.select_time);
            }
            else {
                mSelectedTime.setTextColor(Color.BLACK);
                mSelectedTime.setText(sdf.format(mDate));
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

            receiveObjects(mCode, mTask);
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
                add1TenMins();
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
                mTask.setTermEnd(date/*+30*60*1000*/);

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

        case PickTimeDialog.CODE: {
            long date = (long) objects[0];
            //date = date - new Date().getTimezoneOffset()* 60000;
            if (date == mTask.getTermBegin() && !mTimeHelper.isWholeDayTask(mTask, true)) {
                return;
            }

            mTask.setTermBegin(date);
            mTask.setTermEnd(date/*+30*60*1000*/);
            mCalendarView.setDate(0, new Date(Long.valueOf(mTask.getTermBegin())).getMonth(), mCalendar, -1);
            mCalendarView.setChosenDate(new Date(Long.valueOf(mTask.getTermBegin())));
        }
            break;
        default:
            return;
        }

        updateViews();
    }

}