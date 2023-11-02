package com.ashberrysoft.leadertask.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.CalendarDataContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.CalendarData;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.Utils.TaskUtils;
import com.ashberrysoft.leadertask.views.CalendarItemView.OnCalendarItemListener;

public class LTCalendarView extends LinearLayout//
        implements OnClickListener, OnLongClickListener, OnCalendarItemListener,//
        LoaderCallbacks<Cursor> {

    public interface OnCalendarDateSelectedListener {

        public void onDateSelected(Date date);

        public void fillLostData(List<Calendar> lostData);

        public void restartLoaderCallback();
    }

    public static final int COLUMN_COUNT = 7;
    /*public static int ROW_COUNT = 6;
    public static int mCalendarDaysCount = COLUMN_COUNT * ROW_COUNT;*/
    
    private int mRowCount;
    private int mCalendarDaysCount;

    // VIEW's
    private TextView mDateValue;
    private ViewFlipper mViewFlipper;
    private CalendarDatesRowsView mDatesRows1;
    private CalendarDatesRowsView mDatesRows2;

    // VALUE's
    private Calendar mWorkerCalendar;
    private Calendar mChosenCalendar;
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentWeek;

    private int mCurrentYearOld;
    private int mCurrentMonthOld;
    private int mCurrentWeekOld;

    private boolean mFirstDatesRows;
    private String[] mMonthsFull;
    private boolean mNoSelection;

    private Calendar[] mCalendars;
    private  boolean[] mMainMonth;
    private  boolean[] mChosenDate;
    private  boolean[] mHasTasks;

    private Animation mAnimPrOut;
    private Animation mAnimPrIn;
    private Animation mAnimNeOut;
    private Animation mAnimNeIn;

    private LTSettings mSettings;
    private boolean mInNavigator;
    private boolean isAnotherMonthClicked;
    private boolean mToLeftAnotherMonthClicked;

    // LISTENER
    private OnCalendarDateSelectedListener mListener;

    public LTCalendarView(Context context) {
        super(context);
        initialization(false);
    }

    public LTCalendarView(Context context, OnCalendarDateSelectedListener listener, boolean inNavigator) {
        super(context);

        initialization(inNavigator);
        setCustomListener(listener);
    }

    private void initialization(boolean inNavigator) {
        mInNavigator = inNavigator;
        mSettings = LTSettings.getInstance(getContext());
        if (mSettings.isOneWeekInNav()) {
            mRowCount = 1;
            this.setPadding(0, 0, 0, 20);
        } else {
            mRowCount = 6;
            this.setPadding(0, 0, 0, 0);
        }

        if (!mInNavigator) {
            mRowCount = 6;
            this.setPadding(0, 0, 0, 0);
        }

        mCalendarDaysCount = COLUMN_COUNT * mRowCount;

        inNavigator = true;
        inflate(getContext(), R.layout.view_lt_calendar, this);
        this.setOrientation(LinearLayout.VERTICAL);

        final int slidingCustomWidth;
        /*if (LTSettings.getInstance().isShowWeekCountInCalendar()) {
            slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum_andweek);*/
        //} else {
            slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);
        //}

        RelativeLayout.LayoutParams lp23 = new RelativeLayout.LayoutParams(slidingCustomWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout lvCont = (LinearLayout) findViewById(R.id.container_calendar);

        lvCont.setLayoutParams(lp23);

        mMonthsFull = getResources().getStringArray(R.array.months_full);

        mAnimPrOut = AnimationUtils.loadAnimation(getContext(), R.anim.flipper_prev_out);
        mAnimPrIn = AnimationUtils.loadAnimation(getContext(), R.anim.flipper_prev_in);
        mAnimNeOut = AnimationUtils.loadAnimation(getContext(), R.anim.flipper_next_out);
        mAnimNeIn = AnimationUtils.loadAnimation(getContext(), R.anim.flipper_next_in);

        mDateValue = (TextView) findViewById(R.id.date_value);
        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        mDatesRows1 = (CalendarDatesRowsView) findViewById(R.id.dates_rows_1);
        mDatesRows2 = (CalendarDatesRowsView) findViewById(R.id.dates_rows_2);
        final ImageView toLeft = (ImageView) findViewById(R.id.date_to_left);
        final ImageView toRight = (ImageView) findViewById(R.id.date_to_right);
        if (mSettings.isShowWeekCountInCalendar()) {
            int margin = getResources().getDimensionPixelSize(R.dimen.week_width3);
            int margin2 = getResources().getDimensionPixelSize(R.dimen.week_width4);
            int padding = getResources().getDimensionPixelSize(R.dimen.cDateItemPadding);
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(margin, 0, margin2, 0);
            LinearLayout lv = (LinearLayout) findViewById(R.id.days_of_week);
            lv.setLayoutParams(lp);
            lv.setPadding(padding, padding, padding, padding);
        }

        mFirstDatesRows = true;

        mDateValue.setTextSize(22);
        mDatesRows1.setInNavigator(inNavigator, mRowCount);
        mDatesRows2.setInNavigator(inNavigator, mRowCount);
        mDatesRows1.setCustomListener(this);
        mDatesRows2.setCustomListener(this);
        mDatesRows1.createRows();
        mDatesRows2.createRows();

        toLeft.setOnClickListener(this);
        toLeft.setOnLongClickListener(this);
        toRight.setOnClickListener(this);
        toRight.setOnLongClickListener(this);

        mWorkerCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        mChosenCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);

        toFormat(mWorkerCalendar);


        mCalendars = new Calendar[mCalendarDaysCount];
        mMainMonth = new boolean[mCalendarDaysCount];
        mChosenDate = new boolean[mCalendarDaysCount];
        mHasTasks = new boolean[mCalendarDaysCount];

        for (int i = 0; i < mCalendars.length; i++) {
            mCalendars[i] = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        }
    }

    public void setDate(int year, int month, Calendar selected, int week_of_year) {
        mNoSelection = false;
        setData(year, month, selected, week_of_year);
    }

    private void setData(int year, int month, Calendar selected, int week_of_year) {
        long selectedMillis = 0;
        if (selected != null) {
            toFormat(selected);
            selectedMillis = selected.getTimeInMillis();
        }

        mCurrentYear = year;
        mCurrentMonth = month;
        mCurrentWeek = week_of_year;

        toFirstCustomDate(year, month, mWorkerCalendar, week_of_year);

        mDateValue.setText(getDateValue(mWorkerCalendar));

        int leftMonthDays = 0;
        switch (mWorkerCalendar.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.TUESDAY:
            leftMonthDays = 1;
            break;

        case Calendar.WEDNESDAY:
            leftMonthDays = 2;
            break;

        case Calendar.THURSDAY:
            leftMonthDays = 3;
            break;

        case Calendar.FRIDAY:
            leftMonthDays = 4;
            break;

        case Calendar.SATURDAY:
            leftMonthDays = 5;
            break;

        case Calendar.SUNDAY:
            leftMonthDays = 6;
        default:
            break;
        }

        mWorkerCalendar.add(Calendar.DAY_OF_MONTH, -leftMonthDays);
        for (int i = 0; i < mCalendars.length; i++) {
            mCalendars[i].setTime(mWorkerCalendar.getTime());
            if (week_of_year == -1) {
                mMainMonth[i] = mCalendars[i].get(Calendar.YEAR) == year && mCalendars[i].get(Calendar.MONTH) == month;
            } else {
                mMainMonth[i] = mCalendars[i].get(Calendar.YEAR) == year && mCalendars[i].get(Calendar.MONTH) == month && mCalendars[i].get(Calendar.WEEK_OF_YEAR) == week_of_year;
            }
            mChosenDate[i] = mCalendars[i].getTimeInMillis() == selectedMillis;

            if (mChosenDate[i]) {
                mChosenCalendar.setTimeInMillis(mCalendars[i].getTimeInMillis());
            }

            mWorkerCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        toFirstCustomDate(year, month, mWorkerCalendar, week_of_year);

        if (isAnotherMonthClicked) {
            isAnotherMonthClicked = false;
            if (mCurrentWeek == -1) {
                mWorkerCalendar.add(Calendar.MONTH, mToLeftAnotherMonthClicked ? 1 : -1);
                setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), getChosenDate(), -1);
            } else {
                mWorkerCalendar.add(Calendar.WEEK_OF_YEAR, mToLeftAnotherMonthClicked ? 1 : -1);
                setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), getChosenDate(), mWorkerCalendar.get(Calendar.WEEK_OF_YEAR));
            }
        }

        if (mListener != null) {
            mListener.restartLoaderCallback();
            this.notifyDataSetChanged();

        } else {
            resetBooleanArray(mHasTasks);
            this.notifyDataSetChanged();
        }
    }

    public int getLoaderCallbackId() {
        return R.id.lm_calendar_view;
    }

    public void setControlDate(boolean setData, Date date) {
        if (date == null) {
            mNoSelection = true;
            resetBooleanArray(mChosenDate);

            this.notifyDataSetChanged();

        } else {
            if (setData) {
                mNoSelection = false;

                mWorkerCalendar.setTime(date);
                if (isCurrentMonth(mWorkerCalendar, mCalendars[mCalendarDaysCount / 2])) {
                    setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), mWorkerCalendar, -1);

                } else {
                    mFirstDatesRows = !mFirstDatesRows;
                    setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), mWorkerCalendar, -1);

                    final boolean toLeft = isAfterCurrentMonth(mWorkerCalendar, Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE));
                    setFlipperWithAnimation(toLeft);
                }
            }
        }
    }

    private boolean isCurrentMonth(Calendar main, Calendar compare) {
        return main.get(Calendar.YEAR) == compare.get(Calendar.YEAR) && main.get(Calendar.MONTH) == compare.get(Calendar.MONTH);
    }

    private boolean isAfterCurrentMonth(Calendar main, Calendar compare) {
        return main.get(Calendar.YEAR) < compare.get(Calendar.YEAR) || main.get(Calendar.MONTH) < compare.get(Calendar.MONTH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.date_to_left:
            moveCalendarMonth(true);
            break;

        case R.id.date_to_right:
            moveCalendarMonth(false);
        default:
            break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
        case R.id.date_to_left:
            //moveCalendarYear(true);
            return true;

        case R.id.date_to_right:
            //moveCalendarYear(false);
            return true;

        default:
            return false;
        }
    }

    @Override
    public void onDateClick(int pos) {
        mNoSelection = false;

        if (mMainMonth[pos]) {
            if (mChosenDate[pos]) {
                return;
            }

            resetBooleanArray(mChosenDate);
            mChosenDate[pos] = true;
            mChosenCalendar.setTimeInMillis(mCalendars[pos].getTimeInMillis());

            if (mListener != null) {
                mListener.onDateSelected(mCalendars[pos].getTime());
            }

            this.notifyDataSetChanged();

        } else {
            isAnotherMonthClicked = true;
            boolean left = false;
            int m = 0;

            for (int i =0 ; i < mMainMonth.length; i++ ) {
                if (mMainMonth[i]) {
                    // если текущий месяц то выходим (ещем только до)
                    break;
                } else {
                    // если чужой месяц
                    // то проверяем
                    if (pos == i) {
                        left = true; // если нашли значит нажали на предыдущий меся, если не нашли то на следующий
                    }
                }
            }
            mToLeftAnotherMonthClicked = left;

            mChosenCalendar.setTimeInMillis(mCalendars[pos].getTimeInMillis());

            if (mListener != null) {
                mListener.onDateSelected(mCalendars[pos].getTime());
            }

            /*if (!mInNavigator) {
                moveCalendarMonth(pos < mCalendarDaysCount / 2);
            }*/
        }
    }

    public void moveCalendarMonth(boolean toLeft) {
        mFirstDatesRows = !mFirstDatesRows;

        if (mCurrentWeek == -1) {
            mWorkerCalendar.add(Calendar.MONTH, toLeft ? -1 : 1);
            setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), getChosenDate(), -1);
        } else {
            mWorkerCalendar.add(Calendar.WEEK_OF_YEAR, toLeft ? -1 : 1);
            setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), getChosenDate(), mWorkerCalendar.get(Calendar.WEEK_OF_YEAR));
        }

        setFlipperWithAnimation(toLeft);
    }

    private void setFlipperWithAnimation(boolean toLeft) {
        if (toLeft) {
            mViewFlipper.setOutAnimation(mAnimPrOut);
            mViewFlipper.setInAnimation(mAnimPrIn);
            mViewFlipper.showPrevious();

        } else {
            mViewFlipper.setOutAnimation(mAnimNeOut);
            mViewFlipper.setInAnimation(mAnimNeIn);
            mViewFlipper.showNext();
        }
    }

    public Calendar getChosenDate() {
        return mNoSelection ? null : mChosenCalendar;
    }

    private void resetBooleanArray(boolean[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = false;
        }
    }

    private String getDateValue(Calendar c) {
        return mMonthsFull[c.get(Calendar.MONTH)] + " " + String.valueOf(c.get(Calendar.YEAR));
    }

    private void toFormat(Calendar c) {
        TaskUtils.setCalendarToBaseFormat(c);
    }

    private void toFirstCustomDate(int year, int month, Calendar c, int week_of_year) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        //c.set(Calendar.DAY_OF_MONTH, 1);

        if (week_of_year == -1) {
            c.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            c.set(Calendar.WEEK_OF_YEAR, week_of_year);
        }
    }

    public void setCustomListener(OnCalendarDateSelectedListener listener) {
        mListener = listener;
    }

    public void setChosenDate(Date date) {
        mWorkerCalendar.setTime(date == null ? new Date() : date);
        toFormat(mChosenCalendar);

        if (mWorkerCalendar.getTimeInMillis() == mChosenCalendar.getTimeInMillis()) {
            if (mWorkerCalendar.get(Calendar.YEAR) != mCurrentYear || mWorkerCalendar.get(Calendar.MONTH) != mCurrentMonth) {
                setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), mWorkerCalendar, -1);
            }
        } else {
            if (mWorkerCalendar.get(Calendar.YEAR) != mCurrentYear || mWorkerCalendar.get(Calendar.MONTH) != mCurrentMonth) {
                setData(mWorkerCalendar.get(Calendar.YEAR), mWorkerCalendar.get(Calendar.MONTH), mWorkerCalendar, -1);
            } else {
                final long selectedMillis = mWorkerCalendar.getTimeInMillis();
                for (int i = 0; i < mCalendars.length; i++) {
                    mChosenDate[i] = mCalendars[i].getTimeInMillis() == selectedMillis;

                    if (mChosenDate[i]) {
                        mChosenCalendar.setTimeInMillis(mCalendars[i].getTimeInMillis());
                    }
                }

                toFirstCustomDate(mCurrentYear, mCurrentMonth, mWorkerCalendar, mCurrentWeek);

                this.notifyDataSetChanged();
            }
        }
    }

    public void notifyDataSetChanged() {
        if (mSettings.isOneWeekInNav()) {
            mRowCount = 1;
            this.setPadding(0, 0, 0, 20);
        } else {
            mRowCount = 6;
            this.setPadding(0, 0, 0, 0);
        }

        if (!mInNavigator) {
            mRowCount = 6;
            this.setPadding(0, 0, 0, 0);
        }

        mCalendarDaysCount = COLUMN_COUNT * mRowCount;

        if (mFirstDatesRows) {
            mDatesRows1.notifyDataSetChanged(mCalendars, mMainMonth, mChosenDate, mHasTasks, mRowCount);
        } else {
            mDatesRows2.notifyDataSetChanged(mCalendars, mMainMonth, mChosenDate, mHasTasks, mRowCount);
        }
    }

    public static void fillCalendarData(Context context, List<Calendar> lostData) {
        new FillCalendarThread(context, lostData).start();
    }

    /** Временная мера обновления данных у календаря (без хеширования) */
    public static void clearCalendarData(Context context, LTask... tasks) {
        new RemoveCalendarThread(context, tasks).execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        switch (id) {
        case R.id.lm_calendar_view:
            return new CursorLoader(getContext(), CalendarDataContract.CONTENT_URI, null, CalendarDataContract.selectionDatesIn(mCalendars), null, null);

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (R.id.lm_calendar_view == loader.getId()) {
            setHasTasksArray(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void setHasTasksArray(Cursor cursor) {
        resetBooleanArray(mHasTasks);
        final boolean[] calendarExist = new boolean[mCalendarDaysCount];

        if (cursor.moveToFirst()) {
            final int columnDate = cursor.getColumnIndex(CalendarDataContract.DATE);
            final int columnCount = cursor.getColumnIndex(CalendarDataContract.UNCOMPLETED_TASKS);

            int count = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                for (int i = count; i < mCalendars.length; i++) {
                    if (mCalendars[i].getTimeInMillis() == cursor.getLong(columnDate)) {
                        mHasTasks[i] = cursor.getInt(columnCount) > 0;
                        calendarExist[i] = true;
                        count++;
                        break;
                    }
                }
            }
        }

        final List<Calendar> lostData = new ArrayList<Calendar>(mCalendarDaysCount);
        for (int i = 0; i < calendarExist.length; i++) {
            if (!calendarExist[i]) {
                lostData.add((Calendar) mCalendars[i].clone());
            }
        }

        this.notifyDataSetChanged();

        if (mListener != null && !lostData.isEmpty()) {
            mListener.fillLostData(lostData);
        }
    }

    private static final class FillCalendarThread extends Thread {

        private final Context mContext;
        private final List<Calendar> mCalendars;

        public FillCalendarThread(Context context, List<Calendar> lostData) {
            super(FillCalendarThread.class.getSimpleName());

            mContext = context.getApplicationContext();
            mCalendars = lostData;
        }

        @Override
        public void run() {
            super.run();

            try {
                process();

            } catch (Exception e) {
                Utils.toLog(e);
            }
        }

        public void process() throws Exception {
            final CompletedCache completedCache = CompletedCache.getInstance(mContext);

            final TaskSelectionBuilder sb = new TaskSelectionBuilder();
            final ContentValues[] cvs = new ContentValues[mCalendars.size()];

            int count = 0;
            Cursor c = null;

            Integer columnId = null;

            CalendarData data;

            for (Calendar calendar : mCalendars) {
                final long date = calendar.getTimeInMillis();
                final long from = TimeHelper.roundCalendar(calendar, true).getTimeInMillis();
                final long to = TimeHelper.roundCalendar(calendar, false).getTimeInMillis();

                sb.clear();
                sb.getTasksCreatedFromTo(from, to);

                int tasks = 0;
                int tasksUncompleted = 0;

                try {
                    c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, sb.build(), null, null);
                    if (c.moveToFirst()) {
                        if (columnId == null) {
                            columnId = c.getColumnIndex(LTaskContract._ID);
                        }

                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                            tasks++;
                            if (completedCache.find(c.getInt(columnId)) == null) {
                                tasksUncompleted++;
                            }
                        }
                    }

                } finally {
                    if (c != null) {
                        c.close();
                        c = null;
                    }
                }

                data = new CalendarData();
                data.setDate(date);
                data.setTotalTasks(tasks);
                data.setUncompletedTasks(tasksUncompleted);

                cvs[count++] = data.getContentValues();
                calendar.setTimeInMillis(date);
            }

            mContext.getContentResolver().bulkInsert(CalendarDataContract.CONTENT_URI, cvs);
        }
    }

    private static final class RemoveCalendarThread extends AsyncTask<Void, Void, Void> {

        private final Context mContext;
        private final LTask[] mTasks;

        public RemoveCalendarThread(Context context, LTask... tasks) {

            mContext = context.getApplicationContext();
            mTasks = tasks;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                process();

            } catch (Exception e) {
                Utils.toLog(e);
            }
            return null;
        }

        private void process() throws Exception {
            final String selection;
            if (mTasks == null || mTasks.length == 0) {
                selection = null;

            } else {
                final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
                final StringBuilder sb = new StringBuilder();
                boolean first = true;

                sb.append(CalendarDataContract.DATE);
                sb.append(SharedStrings.IN);
                sb.append(SharedStrings.BRACE_OPEN_C);

                for (LTask task : mTasks) {
                    if (task.getTermBegin() == 0) {
                        continue;
                    }

                    if (first) {
                        first = false;

                    } else {
                        sb.append(SharedStrings.COMMA_C);
                    }

                    calendar.setTimeInMillis(task.getTermBegin());
                    TaskUtils.setCalendarToBaseFormat(calendar);

                    sb.append(calendar.getTimeInMillis());

                }
                sb.append(SharedStrings.BRACE_CLOSE_C);

                selection = sb.toString();
            }
            mContext.getContentResolver().delete(CalendarDataContract.CONTENT_URI, selection, null);
        }
    }
}