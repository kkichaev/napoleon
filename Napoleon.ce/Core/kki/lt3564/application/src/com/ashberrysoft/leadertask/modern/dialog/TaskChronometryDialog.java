package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.ChronoHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ashberrysoft.leadertask.R.id.count_days;
import static com.ashberrysoft.leadertask.R.id.count_hour;
import static com.ashberrysoft.leadertask.R.id.count_min;
import static com.ashberrysoft.leadertask.R.id.editText;
import static com.ashberrysoft.leadertask.modern.helper.TimeHelper.SDF_12H;
import static com.ashberrysoft.leadertask.modern.helper.TimeHelper.SDF_24H;

public class TaskChronometryDialog extends BaseDialog//
        implements DialogInterface.OnClickListener {

    public static final int CODE = R.id.dialog_task_chronometry;
    private static final String EXTRA_TASK = "EXTRA_TASK";

    // VIEW's
    private NumberPicker mViewAfterCountMin;
    private NumberPicker mViewAfterCountHour;
    private NumberPicker mViewAfterCountDays;
    private TextView mComment;
    private TextView mFact;
    private FrameLayout mDelFact;
    private TextView mFactTitle;
    private Context mContext;
    private boolean isAfterReset = false;

    // VALUE's
    private LTask mTask;

    public static TaskChronometryDialog newInstance(Fragment fragment, LTask task) {
        final Bundle b = new Bundle(2);
        b.putSerializable(EXTRA_TASK, task.clone());
        final TaskChronometryDialog d = new TaskChronometryDialog();
        d.setTargetFragment(fragment, 0);
        d.setArguments(b);

        return d;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        mTask = (LTask) bundle.getSerializable(EXTRA_TASK);
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int min = mTask.getPlan();
            if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                // добавить тайм тикер чтобы обновлять
                int wasInWork = mTask.getTime()+(int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone() - mTask.getInWorkTime()) / 1000); // сек
                if (getContext() == null) {

                    timerHandler.removeCallbacks(timerRunnable);
                    return;
                }
                mFact.setText(Utils.inWorkDialog(getContext(), wasInWork));
            }

            timerHandler.postDelayed(this, 1000);
        }
    };

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(getSettings().getLTCalendarWidth(), ActionBar.LayoutParams.WRAP_CONTENT);
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_chrono_dialog, null);


        mViewAfterCountMin = (NumberPicker) v.findViewById(count_min);
        mViewAfterCountHour = (NumberPicker) v.findViewById(count_hour);
        mViewAfterCountDays = (NumberPicker) v.findViewById(count_days);
        mComment = (TextView) v.findViewById(R.id.comment);
        mFact = (TextView) v.findViewById(R.id.fact);
        mDelFact = (FrameLayout) v.findViewById(R.id.btn_without_fact);
        mDelFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // сбросить хронометраж
                mTask.setTime(0);
                mTask.setInWorkTime(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());

                resetChrono();
                isAfterReset = true;
            }
        });
        mFactTitle = (TextView) v.findViewById(R.id.factTitle);

        resetChrono();

        mViewAfterCountMin.setMaxValue(59);
        mViewAfterCountMin.setMinValue(0);

        mViewAfterCountHour.setMaxValue(7);
        mViewAfterCountHour.setMinValue(0);

        mViewAfterCountDays.setMaxValue(365);
        mViewAfterCountDays.setMinValue(0);

        if (mTask.getPlan() != 0) {
            int ost = mTask.getPlan();
            while (ost != 0) {
                if (ost / 60 < 60) { // если меньше часа
                    //устанавливаем минуты
                    int value = ost / 60;
                    mViewAfterCountMin.setValue(value);
                    ost = ost - value * 60;
                } else if (ost / 60 / 60  < 8) { // если меньше чем 8 часов но больше чем час
                    // устанавливаем часы
                    int value = ost / 60 / 60 ;
                    mViewAfterCountHour.setValue(value);
                    ost = ost - value * 60 * 60;
                } else {
                    // устанавливаем дни
                    int value = ost / 60 / 60 / 8 ;
                    mViewAfterCountDays.setValue(value);
                    ost = ost - value * 60 * 60 * 8;
                }
            }
        }

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setCancelable(true);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);
        ad.setNeutralButton(R.string.chronometry_del, this);

        Dialog dialog = ad.create();

        return dialog;
    }

    private void resetChrono() {
        int wasInWork = 0;
        if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
            wasInWork = mTask.getTime() + (int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone() - mTask.getInWorkTime()) / 1000); // сек
            if (mTask.getInWorkTime() == 0) {
                wasInWork = 0;
            }
        } else {
            wasInWork = mTask.getTime();
        }

        timerHandler.removeCallbacks(timerRunnable);
        if (wasInWork == 0) {
            mComment.setVisibility(View.VISIBLE);
            mFact.setVisibility(View.GONE);
            mDelFact.setVisibility(View.GONE);
            mFactTitle.setVisibility(View.GONE);
        } else {
            mComment.setVisibility(View.GONE);
            mFact.setVisibility(View.VISIBLE);
            if (mTask.getEmailCustomer().equals(LTSettings.getInstance().getUserName())) {
                mDelFact.setVisibility(View.VISIBLE);
            } else {
                mDelFact.setVisibility(View.GONE);
            }
            mFactTitle.setVisibility(View.VISIBLE);
            mFact.setText(Utils.inWorkDialog(getContext(), wasInWork));
            timerHandler.postDelayed(timerRunnable, 1000);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable(EXTRA_TASK, mTask);
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (isAfterReset) {
                mTask.setTime(0);
                mTask.setInWorkTime(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());

                mTask.setUsnTime(mTask.getUsnTime() + 1);
                mTask.setUsnInWorkTime(mTask.getUsnInWorkTime() + 1);
                resetChrono();
                isAfterReset = false;
            }
            int count = mViewAfterCountMin.getValue()* 60 + mViewAfterCountHour.getValue()* 60 * 60 + mViewAfterCountDays.getValue() * 60 * 60 * 8;
            mTask.setPlan(count);
            mTask.setUsnPlan(mTask.getUsnPlan() + 1);

            //int wasInWork = (int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone()-mTask.getInWorkTime())/1000);
            //int wasInWork = (int) ChronoHelper.instance.getFactTiming(mTask.getTime(), mTask.getInWorkTime());
            //mTask.setTime(wasInWork);
            //mTask.setUsnTime(mTask.getUsnTime() + 1);

            receiveObjects(CODE, mTask);
        }
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            mTask.setPlan(0);
            mTask.setUsnPlan(mTask.getUsnPlan() + 1);

            //int wasInWork = (int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone()-mTask.getInWorkTime())/1000);
            //int wasInWork = (int)ChronoHelper.instance.getFactTiming(mTask.getTime(), mTask.getInWorkTime());
            //mTask.setTime(wasInWork);
            //mTask.setUsnTime(mTask.getUsnTime() + 1);

            receiveObjects(CODE, mTask);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
    }

}