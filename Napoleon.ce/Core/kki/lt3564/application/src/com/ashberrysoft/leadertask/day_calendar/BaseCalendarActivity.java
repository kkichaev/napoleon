package com.ashberrysoft.leadertask.day_calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.day_calendar.custom_calendar.DateTimeInterpreter;
import com.ashberrysoft.leadertask.day_calendar.custom_calendar.MonthLoader;
import com.ashberrysoft.leadertask.day_calendar.custom_calendar.WeekView;
import com.ashberrysoft.leadertask.day_calendar.custom_calendar.WeekViewEvent;
import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.fragment.TasksFragment;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.software.shell.fab.ActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public abstract class BaseCalendarActivity extends BaseActivity implements MonthLoader.MonthChangeListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    public WeekView mWeekView;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter();

        // НОВОЕ
        mWeekViewType = TYPE_DAY_VIEW;
        mWeekView.setNumberOfVisibleDays(1);

        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        //
        if(LTSettings.getInstance().getMenuItem().getMenuItemType() == MenuItemType.TODAY || LTSettings.getInstance().getMenuItem().getMenuItemType() == MenuItemType.CALENDAR_DAY) {
            Calendar today = Calendar.getInstance();

            long time = LTSettings.getInstance().getMenuItem().getUniqueId();
            if (time != -1)
                today.setTimeInMillis(time);

            mWeekView.goToDate(today);
        }

        setActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            /*case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDateTimeInterpreter() {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                boolean isOneDayMode = false;
                if (mWeekViewType == TYPE_DAY_VIEW) {
                    isOneDayMode = true;
                } else {
                    isOneDayMode = false;
                }
                String nearestDay = TimeHelper.getInstance().getCuteDateTitleForCalendar(date.getTime(), isOneDayMode);
                if (nearestDay == null) {
                    nearestDay = "";
                }
                return nearestDay;
            }

            @Override
            public String interpretTime(int hour) {

                if (DateFormat.is24HourFormat(getApplicationContext())) {
                    return hour > 11 ? (hour) + ":00" : (hour == 0 ? /*"00:00"*/ "" : hour + ":00"); // TODO
                } else {
                    return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");// TODO
                }
            }
        });
    }


    public WeekView getWeekView() {
        return mWeekView;
    }

    private void setActionBar()
    {
        // Set a toolbar which will replace the action bar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar_day_calendar);

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);

        final Drawable upArrow = getResources().getDrawable(R.drawable.baseline_arrow_back_white_24);
        upArrow.setColorFilter(getResources().getColor(R.color.toolbar_prop_color_text), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
        actionBar.setTitle("");
        //actionBar.setLogo(TaskStatus.getTaskStatus(mTaskOld.getStatus()).getResIdWhite());
    }
}
