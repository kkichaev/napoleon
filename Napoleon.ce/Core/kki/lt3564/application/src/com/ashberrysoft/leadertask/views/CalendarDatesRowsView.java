package com.ashberrysoft.leadertask.views;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.views.CalendarItemView.OnCalendarItemListener;

import static android.R.attr.rowCount;
import static com.ashberrysoft.leadertask.views.LTCalendarView.COLUMN_COUNT;

public class CalendarDatesRowsView extends LinearLayout {


    // VIEW's
    private LinearLayout[] mDatesRows;
    private boolean mInNavigator;
    private int mRowCount;

    // VALUE
    private LTSettings mSettings;
    // private Integer mLTCalendarWidth;

    // LISTENER
    private OnCalendarItemListener mListener;

    public CalendarDatesRowsView(Context context) {
        super(context);
        initialization(true);
    }

    public CalendarDatesRowsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(true);
    }

    public CalendarDatesRowsView(Context context, OnCalendarItemListener listener, boolean inNavigator) {
        super(context);

        setCustomListener(listener);
        initialization(inNavigator);
    }

    private void initialization(boolean inNavigator) {
        /*mInNavigator = inNavigator;
        mSettings = LTSettings.getInstance(getContext());

        this.setOrientation(LinearLayout.VERTICAL);
        {
            final int padding = getResources().getDimensionPixelSize(R.dimen.univ_padding_small_navigator);
            this.setPadding(padding, 0, padding, 0);
        }

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mDatesRows = new LinearLayout[mRowCount];
        for (int i = 0; i < mDatesRows.length; i++) {
            mDatesRows[i] = getDateRow(i, lp);
            this.addView(mDatesRows[i]);
        }*/
    }

    private LinearLayout getDateRow(int i, LayoutParams lp) {
        final LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        return ll;
    }

    public void notifyDataSetChanged(Calendar[] calendars, boolean[] mainMonth, boolean[] chosenDate, boolean[] hasTasks, int rowCount) {
        resetCountRows(rowCount);
        if (mDatesRows[0].getChildCount() == 0) {
            for (int i = 0; i < mRowCount; i++) {
                mDatesRows[i].removeAllViews();
                for (int j = 0; j < LTCalendarView.COLUMN_COUNT; j++) {
                    final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
                    lp.weight = 1;

                    final CalendarItemView v = new CalendarItemView(getContext(), mListener, mInNavigator);
                    v.setLinearLayoutLayoutParams(lp);
                    final int position = i * COLUMN_COUNT + j;
                    setViewData(v, position, calendars[position], mainMonth[position], chosenDate[position],
                            hasTasks[position], j);

                    mDatesRows[i].addView(v);
                }
            }

        } else {
            for (int i = 0; i < mRowCount; i++) {
                for (int j = 0; j < COLUMN_COUNT; j++) {
                    final int position = i * COLUMN_COUNT + j;
                    setViewData((CalendarItemView) mDatesRows[i].getChildAt(j), position, calendars[position],
                            mainMonth[position], chosenDate[position], hasTasks[position], j);
                }
            }
        }
    }

    private void createColums() {
        final LinearLayout.LayoutParams lp;
        if (mSettings.isShowWeekCountInCalendar()) {
            lp = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
        } else {
            final int width;
            final int slidingCustomWidth;
            if (LTSettings.getInstance().isShowWeekCountInCalendar()) {
                slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum_andweek);
            } else {
                slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);
            }

            if (LTSettings.getInstance(getContext()).getLTCalendarWidth() != null) {
                width = slidingCustomWidth / COLUMN_COUNT - getResources().getDimensionPixelSize(R.dimen.divider_big);
            } else {
                width = LayoutParams.WRAP_CONTENT;
            }

            lp = new LinearLayout.LayoutParams(0, width);
            lp.weight = 1;
            //lp.setMargins(1,1,1,1);
        }

        for (int i = 0; i < mRowCount; i++) {
            mDatesRows[i].removeAllViews();
            for (int j = 0; j < COLUMN_COUNT; j++) {
                final CalendarItemView v = new CalendarItemView(getContext(), mListener, mInNavigator);
                v.setLinearLayoutLayoutParams(lp);
                v.setPosition(i * COLUMN_COUNT + j);
                mDatesRows[i].addView(v);
            }
        }
    }

    private void setViewData(CalendarItemView v, int position, Calendar calendars, boolean mainMonth,
            boolean chosenDate, boolean hasTasks, int column) {
        v.setData(position, calendars, mainMonth, chosenDate, hasTasks, column);
    }

    public void setCustomListener(OnCalendarItemListener listener) {
        mListener = listener;
    }

    public void createRows() {
        createColums();
    }

    public void setInNavigator(boolean inNavigator,  int rowCount) {
        mRowCount = rowCount;
        mInNavigator = inNavigator;
        mSettings = LTSettings.getInstance(getContext());

        this.setOrientation(LinearLayout.VERTICAL);
        {
            final int padding = getResources().getDimensionPixelSize(R.dimen.univ_padding_small_navigator);
            this.setPadding(padding, 0, padding, 0);
        }

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mDatesRows = new LinearLayout[mRowCount];
        for (int i = 0; i < mDatesRows.length; i++) {
            mDatesRows[i] = getDateRow(i, lp);
            this.addView(mDatesRows[i]);
        }
    }

    public void resetCountRows(int count) {
        mRowCount = count;
    }
}