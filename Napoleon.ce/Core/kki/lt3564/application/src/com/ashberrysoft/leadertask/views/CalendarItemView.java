package com.ashberrysoft.leadertask.views;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;

import static android.R.attr.padding;
import static android.media.CamcorderProfile.get;
import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.mApp;

public class CalendarItemView extends LinearLayout implements OnClickListener {

    public interface OnCalendarItemListener {
        public void onDateClick(int position);
    }

    // VALUE's
    private int mPosition;

    private boolean mToday;
    private int mColumn;
    private boolean mSetData;
    private boolean mMainMonth;
    private boolean mChosenDate;
    private boolean mHasTasks;

    private int mSelectedColor;
    private int mSelectedColorNoThisMonth;
    private int mSelectedTodayDrawable;
    private int mSelectedDrawable;
    private boolean mInNavigator;
    private LinearLayout.LayoutParams mParams;

    private int mTextColorLightGray;
    private int mTextColorGray;
    private Calendar mCalendar;
    private CalendarItemTextView mMainText;
    private TextView mWeekNumber;
    private View mView;

    private final TimeHelper mTimeHelper;

    // LISTENER
    private OnCalendarItemListener mListener;

    public CalendarItemView(Context context, OnCalendarItemListener listener, boolean inNavigator) {
        super(context);

        setCustomListener(listener);
        initialization(inNavigator);

        mTimeHelper = TimeHelper.getInstance();
    }

    public CalendarItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(true);

        mTimeHelper = TimeHelper.getInstance();
    }

    private void initialization(boolean inNavigator) {
        mInNavigator = inNavigator;
        inflate(getContext(), R.layout.view_lt_calendar_item, this);
        mWeekNumber = (TextView) findViewById(R.id.sub_text);
        mView = (View) findViewById(R.id.divider);


        mSelectedColor = R.drawable.calendar_date_selected;
        mSelectedColorNoThisMonth = R.drawable.calendar_date_selected_no_this_month;
        mSelectedTodayDrawable = R.drawable.calendar_date_today_selected;
        mSelectedDrawable = R.drawable.calendar_date_today;
        mTextColorGray = getContext().getResources().getColor(R.color.navigator_calendar_text_color);
        mTextColorLightGray = getContext().getResources().getColor(R.color.navigator_calendar_text_color2);
        mMainText = new CalendarItemTextView(getContext());
        final int h = getResources().getDimensionPixelSize(R.dimen.week_width5);
        final AbsListView.LayoutParams lp = new AbsListView.LayoutParams(h,h);
        mMainText.setLayoutParams(lp);

        this.addView(mMainText);
    }

    public void setLinearLayoutLayoutParams(LinearLayout.LayoutParams params) {
        mParams = params;
        super.setLayoutParams(params);
    }

    public void setData(int position, Calendar calendar, boolean mainMonth, boolean chosenDate, boolean hasTasks, int column) {
        mSetData = true;
        mToday = mTimeHelper.isToday(calendar.getTimeInMillis());
        mColumn = column;
        mPosition = position;
        mMainMonth = mainMonth;
        mChosenDate = chosenDate;
        mHasTasks = hasTasks;
        mCalendar = calendar;

        mMainText.setColor(mInNavigator ? setItemLookDark(mainMonth, chosenDate, hasTasks)
                : setItemLookLight(mainMonth, chosenDate, hasTasks), mHasTasks);
        mMainText.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

        mMainText.setGravity(Gravity.CENTER);
        mMainText.setOnClickListener(this);

        invalidate();
    }

    private int setItemLookDark(boolean mainMonth, boolean chosenDate, boolean hasTasks) {
        if (mColumn == 0 && LTSettings.getInstance().isShowWeekCountInCalendar()) {
            int width = getResources().getDimensionPixelSize(R.dimen.week_width);
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(lp);
            mWeekNumber.setVisibility(VISIBLE);
            mView.setVisibility(VISIBLE);
            if (mCalendar != null) {
                String text = ""+(mCalendar.get(3) - (LTSettings.getInstance().isWeekCountFromFirstJan() ? 0 : 1));
                mWeekNumber.setText(text);
            }
        } else {
            mWeekNumber.setVisibility(GONE);
            mView.setVisibility(GONE);
        }
        int textColor;
        if (mToday && chosenDate && mainMonth) {
            textColor = getResources().getColor(R.color.navigator_calendar_today_selected_color_dark);
            mMainText.setBackgroundResource(mSelectedTodayDrawable);
        }

        else if (mToday && chosenDate && !mainMonth) {
            textColor = mTextColorLightGray;
            mMainText.setBackgroundResource(mSelectedColorNoThisMonth);
        }

        else if (mToday) {
            textColor = mainMonth ? mTextColorGray : mTextColorLightGray;
            mMainText.setBackgroundResource(mSelectedDrawable);
        }

        else {
            if (chosenDate) {
                if (mainMonth) {
                    textColor = mTextColorGray;
                    mMainText.setBackgroundResource(mSelectedColor);
                    if (mColumn == 5) {
                        textColor = getContext().getResources().getColor(R.color.navigator_calendar_bg_week2);
                    }
                    if (mColumn == 6) {
                        textColor = getContext().getResources().getColor(R.color.navigator_calendar_bg_week3);
                    }
                } else {
                    textColor = mTextColorLightGray;
                    mMainText.setBackgroundResource(mSelectedColorNoThisMonth);
                }
            } else {
                textColor = mainMonth ? mTextColorGray : mTextColorLightGray;
                mMainText.setBackgroundColor(Color.TRANSPARENT);
                if (mainMonth) {
                    if (mColumn == 5) {
                        textColor = getContext().getResources().getColor(R.color.navigator_calendar_bg_week2);
                    }
                    if (mColumn == 6) {
                        textColor = getContext().getResources().getColor(R.color.navigator_calendar_bg_week3);
                    }
                }
            }
        }

        mMainText.setTextColor(textColor);
        return textColor;
        // mMainText.setTypeface(hasTasks ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    private int setItemLookLight(boolean mainMonth, boolean chosenDate, boolean hasTasks) {
        final int textColor;
        if (mToday && chosenDate && mainMonth) {
            textColor = getResources().getColor(R.color.navigator_calendar_today_selected_color_light);
            mMainText.setBackgroundResource(mSelectedTodayDrawable);
        }

        else if (mToday && chosenDate && !mainMonth) {
            textColor = mTextColorGray;
            mMainText.setBackgroundColor(getResources().getColor(R.color.gray_navigator));
        }

        else if (mToday) {
            textColor = mainMonth ? mTextColorLightGray : mTextColorGray;
            mMainText.setBackgroundResource(mSelectedDrawable);
        }

        else {
            if (chosenDate) {
                if (mainMonth) {
                    textColor = Color.WHITE;
                    mMainText.setBackgroundResource(mSelectedColor);
                } else {
                    textColor = mTextColorGray;
                    mMainText.setBackgroundColor(getResources().getColor(R.color.gray_navigator));
                }
            } else {
                textColor = mainMonth ? mTextColorLightGray : mTextColorGray;
                mMainText.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        mMainText.setTextColor(textColor);
        return textColor;
        // mMainText.setTypeface(hasTasks ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    @Override
    public void onClick(View v) {
        mChosenDate = true;
        if (mInNavigator) {
            setItemLookDark(mMainMonth, mChosenDate, mHasTasks);
        } else {
            setItemLookLight(mMainMonth, mChosenDate, mHasTasks);
        }

        if (mListener != null) {
            mListener.onDateClick(mPosition);
        }
    }

    public void setCustomListener(OnCalendarItemListener listener) {
        mListener = listener;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public LinearLayout.LayoutParams getParams() {
        return mParams;
    }
}