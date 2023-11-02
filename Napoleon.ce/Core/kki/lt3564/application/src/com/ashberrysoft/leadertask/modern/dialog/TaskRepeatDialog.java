package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskSeriesHelper;

public class TaskRepeatDialog extends BaseDialog//
        implements OnClickListener {

    public static final int CODE = R.id.repeat_dialog_request_code;

    private ArrayAdapter<CharSequence> mAdapterMainChooser;
    private ArrayAdapter<CharSequence> mAdapterChooserDayRight;
    private ArrayAdapter<CharSequence> mAdapterChooserDayRightMonth;
    private ArrayAdapter<CharSequence> mAdapterChooserDayRightMonthWeeks;
    private ArrayAdapter<CharSequence> mAdapterChooserDayRightYearMonth;
    private LTask mTask;
    private RelativeLayout mViewTypeDay;
    private LinearLayout mViewTypeWeek;
    private RelativeLayout mViewTypeMonth;
    private RelativeLayout mViewTypeYear;
    private LinearLayout mViewAfterCountMonthDaysContainer;
    private LinearLayout mViewAfterCountMonthDaysContainerCustom;
    private LinearLayout mViewAfterCountYearDaysContainer;
    private LinearLayout mViewAfterCountYearDaysContainerCustom;
    private Spinner mMainChooser;
    private Spinner mMainRightChooserDay;
    private Spinner mMainRightChooserMonth;
    private Spinner mMainRightChooserMonthWeeks;
    private Spinner mMainRightChooserYear;
    private Spinner mMainRightChooserYearWeeks;
    private NumberPicker mViewAfterCount;
    private NumberPicker mViewAfterCountWeeks;
    private NumberPicker mViewAfterCountMonth;
    private NumberPicker mViewAfterCountMonthDays;
    private NumberPicker mViewAfterCountYearDays;
    private Spinner mViewAfterCountYear;
    private CheckBox mWeekMo;
    private CheckBox mWeekTu;
    private CheckBox mWeekWe;
    private CheckBox mWeekTh;
    private CheckBox mWeekFr;
    private CheckBox mWeekSa;
    private CheckBox mWeekSu;
    private static final String EXTRA_TASK = "EXTRA_TASK";

    public static TaskRepeatDialog newInstance(Fragment target, LTask task) {
        final Bundle b = new Bundle(1);
        b.putSerializable(EXTRA_TASK, task);

        final TaskRepeatDialog d = new TaskRepeatDialog();
        d.setTargetFragment(target, CODE);
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        mTask = (LTask) bundle.getSerializable(EXTRA_TASK);

        mAdapterMainChooser = ArrayAdapter.createFromResource(getApp(),
                R.array.repeat_main_chooser, R.layout.spinner_item);
        mAdapterMainChooser.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mAdapterChooserDayRight = ArrayAdapter.createFromResource(getApp(),
                R.array.repeat_day_year_chooser, R.layout.spinner_item);
        mAdapterChooserDayRight.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mAdapterChooserDayRightMonth = ArrayAdapter.createFromResource(getApp(),
                R.array.repeat_month_chooser, R.layout.spinner_item);
        mAdapterChooserDayRightMonth.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mAdapterChooserDayRightMonthWeeks = ArrayAdapter.createFromResource(getApp(),
                R.array.days_of_week_short, R.layout.spinner_item);
        mAdapterChooserDayRightMonthWeeks.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mAdapterChooserDayRightYearMonth = ArrayAdapter.createFromResource(getApp(),
                R.array.months_full, R.layout.spinner_item);
        mAdapterChooserDayRightYearMonth.setDropDownViewResource(R.layout.spinner_dropdown_item);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_repeat_dialog, null);
        //
        mMainChooser = (Spinner) v.findViewById(R.id.repeat_chooser_type);
        mMainRightChooserDay = (Spinner) v.findViewById(R.id.repeat_chooser_right);
        mMainRightChooserMonth = (Spinner) v.findViewById(R.id.repeat_chooser_right_month);
        mMainRightChooserYear = (Spinner) v.findViewById(R.id.repeat_chooser_right_year);
        mMainRightChooserMonthWeeks = (Spinner) v.findViewById(R.id.repeat_chooser_right_month_day_of_week);
        mMainRightChooserYearWeeks = (Spinner) v.findViewById(R.id.repeat_chooser_right_year_day_of_week);
        mViewAfterCount = (NumberPicker) v.findViewById(R.id.repeat_chooser_left);
        mViewAfterCountWeeks = (NumberPicker) v.findViewById(R.id.repeat_chooser_right_week);
        mViewAfterCountMonth = (NumberPicker) v.findViewById(R.id.repeat_chooser_left_month);
        mViewAfterCountMonthDays = (NumberPicker) v.findViewById(R.id.repeat_chooser_left_month_day);
        mViewAfterCountYear = (Spinner) v.findViewById(R.id.repeat_chooser_left_year);
        mViewAfterCountYearDays = (NumberPicker) v.findViewById(R.id.repeat_chooser_left_year_day);
        mViewTypeDay = (RelativeLayout) v.findViewById(R.id.container_type_repeat_1);
        mViewTypeWeek = (LinearLayout) v.findViewById(R.id.container_type_repeat_2);
        mViewTypeMonth = (RelativeLayout) v.findViewById(R.id.container_type_repeat_3);
        mViewTypeYear = (RelativeLayout) v.findViewById(R.id.container_type_repeat_4);
        mViewAfterCountMonthDaysContainer = (LinearLayout) v.findViewById(R.id.repeat_chooser_left_month_day_container);
        mViewAfterCountMonthDaysContainerCustom = (LinearLayout) v.findViewById(R.id.repeat_chooser_left_month_day_container_custom);
        mViewAfterCountYearDaysContainer = (LinearLayout) v.findViewById(R.id.repeat_chooser_left_year_day_container);
        mViewAfterCountYearDaysContainerCustom = (LinearLayout) v.findViewById(R.id.repeat_chooser_left_year_day_container_custom);
        mWeekMo = (CheckBox) v.findViewById(R.id.checkbox1);
        mWeekTu = (CheckBox) v.findViewById(R.id.checkbox2);
        mWeekWe = (CheckBox) v.findViewById(R.id.checkbox3);
        mWeekTh = (CheckBox) v.findViewById(R.id.checkbox4);
        mWeekFr = (CheckBox) v.findViewById(R.id.checkbox5);
        mWeekSa = (CheckBox) v.findViewById(R.id.checkbox6);
        mWeekSu = (CheckBox) v.findViewById(R.id.checkbox7);

        mViewAfterCount.setMaxValue(365);
        mViewAfterCount.setMinValue(1);
        mViewAfterCountWeeks.setMaxValue(365);
        mViewAfterCountWeeks.setMinValue(1);
        mViewAfterCountMonth.setMaxValue(365);
        mViewAfterCountMonth.setMinValue(1);
        mViewAfterCountMonthDays.setMaxValue(31);
        mViewAfterCountMonthDays.setMinValue(1);
        mViewAfterCountYearDays.setMaxValue(31);
        mViewAfterCountYearDays.setMinValue(1);

//        //
//        mViewAfterCount.setValue(1);
//        mViewAfterCountWeeks.setValue(1);
//        mViewAfterCountMonth.setValue(1);
//        mViewAfterCountMonthDays.setValue(1);
//        mViewAfterCountYearDays.setValue(1);
        //

        mMainRightChooserDay.setAdapter(mAdapterChooserDayRight);
        mMainRightChooserMonth.setAdapter(mAdapterChooserDayRightMonth);
        mMainRightChooserMonthWeeks.setAdapter(mAdapterChooserDayRightMonthWeeks);
        mMainRightChooserYear.setAdapter(mAdapterChooserDayRightMonth);
        mMainRightChooserYearWeeks.setAdapter(mAdapterChooserDayRightMonthWeeks);
        mViewAfterCountYear.setAdapter(mAdapterChooserDayRightYearMonth);
        mMainRightChooserMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    mViewAfterCountMonthDaysContainer.setVisibility(View.GONE);
                    mViewAfterCountMonthDaysContainerCustom.setVisibility(View.VISIBLE);
                } else {
                    mViewAfterCountMonthDaysContainer.setVisibility(View.VISIBLE);
                    mViewAfterCountMonthDaysContainerCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMainRightChooserYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    mViewAfterCountYearDaysContainer.setVisibility(View.GONE);
                    mViewAfterCountYearDaysContainerCustom.setVisibility(View.VISIBLE);
                } else {
                    mViewAfterCountYearDaysContainer.setVisibility(View.VISIBLE);
                    mViewAfterCountYearDaysContainerCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMainChooser.setAdapter(mAdapterMainChooser);
        mMainChooser.setSelection(mTask.getSeriesType());
        mMainChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetViewsAfterChangeRepeatType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        resetViewsAfterChangeRepeatType(mTask.getSeriesType());



        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.term_repeat);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);

        Dialog dialog = ad.show();


        return dialog;
    }

    private void resetViewsAfterChangeRepeatType(int type) {
        mTask.setSeriesType(type);
        try {
            resetValues();
        } finally {
            switch (type) {
                case 1:
                    mViewTypeDay.setVisibility(View.VISIBLE);
                    mViewTypeWeek.setVisibility(View.GONE);
                    mViewTypeMonth.setVisibility(View.GONE);
                    mViewTypeYear.setVisibility(View.GONE);
                    break;
                case 2:
                    mViewTypeDay.setVisibility(View.GONE);
                    mViewTypeWeek.setVisibility(View.VISIBLE);
                    mViewTypeMonth.setVisibility(View.GONE);
                    mViewTypeYear.setVisibility(View.GONE);
                    break;
                case 3:
                    mViewTypeDay.setVisibility(View.GONE);
                    mViewTypeWeek.setVisibility(View.GONE);
                    mViewTypeMonth.setVisibility(View.VISIBLE);
                    mViewTypeYear.setVisibility(View.GONE);
                    break;
                case 4:
                    mViewTypeDay.setVisibility(View.GONE);
                    mViewTypeWeek.setVisibility(View.GONE);
                    mViewTypeMonth.setVisibility(View.GONE);
                    mViewTypeYear.setVisibility(View.VISIBLE);
                    break;
                case 0:
                default:
                    //скрыть все
                    mViewTypeDay.setVisibility(View.GONE);
                    mViewTypeWeek.setVisibility(View.GONE);
                    mViewTypeMonth.setVisibility(View.GONE);
                    mViewTypeYear.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void resetValues() {
        switch (mTask.getSeriesType()) {
            case 1:
                mMainRightChooserDay.setSelection(mTask.getSeriesAfterType() - 1);
                if (mTask.getSeriesAfterCount() == 0) {
                    mViewAfterCount.setValue(1);
                } else {
                    mViewAfterCount.setValue(mTask.getSeriesAfterCount());
                }
                break;
            case 2:
                if (mTask.getSeriesWeekCount() == 0) {
                    mViewAfterCountWeeks.setValue(1);
                } else {
                    mViewAfterCountWeeks.setValue(mTask.getSeriesWeekCount());
                }
                mWeekMo.setChecked(mTask.getSeriesWeekMon());
                mWeekTu.setChecked(mTask.getSeriesWeekTue());
                mWeekWe.setChecked(mTask.getSeriesWeekWed());
                mWeekTh.setChecked(mTask.getSeriesWeekThu());
                mWeekFr.setChecked(mTask.getSeriesWeekFri());
                mWeekSa.setChecked(mTask.getSeriesWeekSat());
                mWeekSu.setChecked(mTask.getSeriesWeekSun());
                break;
            case 3:
                try {
                    if (mTask.getSeriesMonthType() != 1) {
                        mViewAfterCountMonthDaysContainer.setVisibility(View.GONE);
                        mViewAfterCountMonthDaysContainerCustom.setVisibility(View.VISIBLE);
                    } else {
                        mViewAfterCountMonthDaysContainer.setVisibility(View.VISIBLE);
                        mViewAfterCountMonthDaysContainerCustom.setVisibility(View.GONE);
                    }
                } finally {
                    if (mTask.getSeriesMonthType() == 1) {
                        mMainRightChooserMonth.setSelection(mTask.getSeriesMonthType() - 1);
                        if (mTask.getSeriesMonthCount() == 0) {
                            mViewAfterCountMonth.setValue(1);
                        } else {
                            mViewAfterCountMonth.setValue(mTask.getSeriesMonthCount());
                        }
                        if (mTask.getSeriesMonthDay() == 0) {
                            mViewAfterCountMonthDays.setValue(1);
                        } else {
                            mViewAfterCountMonthDays.setValue(mTask.getSeriesMonthDay());
                        }
                    } else {
                        mMainRightChooserMonth.setSelection(mTask.getSeriesMonthWeekType());
                        if (mTask.getSeriesMonthCount() == 0) {
                            mViewAfterCountMonth.setValue(1);
                        } else {
                            mViewAfterCountMonth.setValue(mTask.getSeriesMonthCount());
                        }
                        mMainRightChooserMonthWeeks.setSelection(mTask.getSeriesMonthDayOfWeek() - 1);
                    }
                }
                break;
            case 4:
                try {
                    if (mTask.getSeriesYearType() != 1) {
                        mViewAfterCountYearDaysContainer.setVisibility(View.GONE);
                        mViewAfterCountYearDaysContainerCustom.setVisibility(View.VISIBLE);
                    } else {
                        mViewAfterCountYearDaysContainer.setVisibility(View.VISIBLE);
                        mViewAfterCountYearDaysContainerCustom.setVisibility(View.GONE);
                    }
                } finally {
                    if (mTask.getSeriesYearType() == 1) {
                        mMainRightChooserYear.setSelection(mTask.getSeriesYearType() - 1);
                        mViewAfterCountYear.setSelection(mTask.getSeriesYearMonth() - 1);
                        if (mTask.getSeriesYearMonthDay() == 0) {
                            mViewAfterCountYearDays.setValue(1);
                        } else {
                            mViewAfterCountYearDays.setValue(mTask.getSeriesYearMonthDay());
                        }
                    } else {
                        mMainRightChooserYear.setSelection(mTask.getSeriesYearWeekType());
                        mViewAfterCountYear.setSelection(mTask.getSeriesYearMonth() - 1);
                        mMainRightChooserYearWeeks.setSelection(mTask.getSeriesYearDayOfWeek() - 1);
                    }
                }
                break;
            case 0:
            default:

                break;
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
            switch (mTask.getSeriesType()) {
                case 1:
                    mTask.setSeriesAfterType(mMainRightChooserDay.getSelectedItemPosition() + 1);
                    mTask.setSeriesAfterCount(mViewAfterCount.getValue());
                    break;
                case 2:
                    mTask.setSeriesWeekCount(mViewAfterCountWeeks.getValue());
                    mTask.setSeriesWeekMon(mWeekMo.isChecked());
                    mTask.setSeriesWeekTue(mWeekTu.isChecked());
                    mTask.setSeriesWeekWed(mWeekWe.isChecked());
                    mTask.setSeriesWeekThu(mWeekTh.isChecked());
                    mTask.setSeriesWeekFri(mWeekFr.isChecked());
                    mTask.setSeriesWeekSat(mWeekSa.isChecked());
                    mTask.setSeriesWeekSun(mWeekSu.isChecked());
                    break;
                case 3:
                    if (mViewAfterCountMonthDaysContainer.getVisibility() == View.VISIBLE) {
                        mTask.setSeriesMonthType(1);
                        mTask.setSeriesMonthCount(mViewAfterCountMonth.getValue());
                        mTask.setSeriesMonthDay(mViewAfterCountMonthDays.getValue());
                    } else {
                        mTask.setSeriesMonthType(2);
                        mTask.setSeriesMonthWeekType(mMainRightChooserMonth.getSelectedItemPosition());
                        mTask.setSeriesMonthCount(mViewAfterCountMonth.getValue());
                        mTask.setSeriesMonthDayOfWeek(mMainRightChooserMonthWeeks.getSelectedItemPosition() + 1);
                    }
                    break;
                case 4:
                    if (mViewAfterCountYearDaysContainer.getVisibility() == View.VISIBLE) {
                        mTask.setSeriesYearType(1);
                        mTask.setSeriesYearMonth(mViewAfterCountYear.getSelectedItemPosition() + 1);
                        mTask.setSeriesYearMonthDay(mViewAfterCountYearDays.getValue());
                    } else {
                        mTask.setSeriesYearType(2);
                        mTask.setSeriesYearWeekType(mMainRightChooserYear.getSelectedItemPosition());
                        mTask.setSeriesYearMonth(mViewAfterCountYear.getSelectedItemPosition() + 1);
                        mTask.setSeriesYearDayOfWeek(mMainRightChooserYearWeeks.getSelectedItemPosition() + 1);
                    }
                    break;
                case 0:
                default:
                    TaskSeriesHelper.resetTaskSeries(mTask, true);
                    break;
            }
            mTask.setUsnEntity(0);
            mTask.setUsnFieldSeries(mTask.getUsnFieldSeries() + 1);
            receiveObjects(CODE, mTask);
        }
    }
}