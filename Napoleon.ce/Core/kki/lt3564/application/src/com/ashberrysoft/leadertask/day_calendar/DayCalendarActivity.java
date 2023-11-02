package com.ashberrysoft.leadertask.day_calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.day_calendar.custom_calendar.WeekView;
import com.ashberrysoft.leadertask.day_calendar.custom_calendar.WeekViewEvent;
import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.activity.AddNewTaskWidgetActivity;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache.LTaskCacheHolder;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.ToastController;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DayCalendarActivity extends BaseCalendarActivity implements EditTaskActivity.OnTaskWasAddedListener, WeekView.EventClickListener,  WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    ArrayList <LTask> mTasks = new ArrayList<>();
    List<WeekViewEvent> mEvents = new ArrayList<WeekViewEvent>();
    public ActionButton mActionButton;
    private List <Marker> mAllMarkers;
    private TimeHelper th;
    private Calendar startTime;
    private Calendar endTime;

    public static Intent newInstance(Context context) {
        final Intent intent = new Intent(context, DayCalendarActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAllMarkers = DbHelper.getInstance(this).getAllMarkersNew();

        th = TimeHelper.getInstance();
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();


        Utils.timeChecker("select");
        select(getApp());
        Utils.timeChecker("select");

        mActionButton = (ActionButton) findViewById(R.id.add_task_button);

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setAlreadyHasAnyTasks();
                addNewTaskForTerm(mWeekView.getCurDate().getTimeInMillis());
            }
        });


        getWeekView().setOnEventClickListener(this);
        getWeekView().setEventLongPressListener(this);
        getWeekView().setEmptyViewLongPressListener(this);
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        if (event.getTask() != null) {
            startActivity(EditTaskActivity.newInstance(this, event.getTask(), false, true, this));
        }
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        //Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        //Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
        if (time.getTime().getMinutes() <= 30) {
            Date tempDate = (Date) time.getTime().clone();
            tempDate.setMinutes(0);
            time.setTime(tempDate);
        } else {
            Date tempDate = (Date) time.getTime().clone();
            tempDate.setMinutes(0);
            tempDate.setHours(tempDate.getHours() + 1 );
            time.setTime(tempDate);
        }
        addNewTaskForTerm(time.getTimeInMillis());
    }

    private void addNewTaskForTerm(long term) {
        term = term + LTSettings.getInstance().getMinHour() * 60 * 60 * 1000;
        String performer = null;
        term = TimeHelper.getInstance().dellTimeZone(term);
        LTask task = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), performer, term, null, null, null, null);
        task.setTermBegin(term);
        task.setTermBeginCustomer(term);
        task.setTermEnd(term+60*60*1000);
        task.setTermEndCustomer(term+60*60*1000);
        startActivity(EditTaskActivity.newInstance(this, task, true, true, this));
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return mEvents;
    }

    @Override
    public int getContainerId() {
        return 0;
    }

    private void select(Context context) {
        Cursor c = null;
        TaskSelectionBuilder mSelectionBuilder = new TaskSelectionBuilder(new StringBuilder());

        ContentResolver mCr = context.getContentResolver();
        //String mSelection = mSelectionBuilder.getUncompletedTasks().toString()+" AND "+ LTaskContract._ID+" IN ( SELECT CalendarLink.taskid FROM CalendarLink )";
        Calendar calendarTemp = Calendar.getInstance();
        calendarTemp.setTimeInMillis(System.currentTimeMillis());
        calendarTemp.add(Calendar.YEAR, 1);
        String mSelection = ""+mSelectionBuilder.getCalendarLinkByDayForCalendarDay(calendarTemp.getTimeInMillis(), null);

        try {

            c = mCr.query(LTaskContract.CONTENT_URI, null, mSelection, null, null);
            if (c.getCount() > 0) {
                int i = 0;
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    LTask task = new LTask(c);
                    mTasks.add(task);
                }
            }

        } catch (Exception e) {

        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (!mTasks.isEmpty()) {
            int i = 0;
            for (LTask task : mTasks) {
                convertTaskToEvent(task);
            }
        }
    }

    private boolean convertTaskToEvent(LTask task) {
        boolean isNewTaskAdded = true;
        long termBegin = 0;
        long termEnd = 0;
        boolean isMyTerm;
        if (task.getTermBegin() != 0) {
            isMyTerm = true;
            termBegin = TimeHelper.getInstance().addTimeZone(task.getTermBegin());
            termEnd = TimeHelper.getInstance().addTimeZone(task.getTermEnd());
        } else {
            isMyTerm = false;
            termBegin = TimeHelper.getInstance().addTimeZone(task.getTermBeginCustomer());
            termEnd = TimeHelper.getInstance().addTimeZone(task.getTermEndCustomer());
        }

        // удалить если задача есть в событиях
        for (WeekViewEvent event : mEvents) {
            if (event.getId().equals(task.getUid())) {
                mEvents.remove(event);
                isNewTaskAdded = false;
                break;
            }
        }
        //
        if (task.getStatus() != TaskStatus.COMPLETED.getCode() && task.getStatus() != TaskStatus.CANCELLED.getCode()) {
            if ((termBegin != 0 && !(!task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) && task.getEmailCustomer().equals(LTSettings.getInstance().getUserName()))) ||
                    (termBegin != 0 && (task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) && task.getEmailCustomer().equals(LTSettings.getInstance().getUserName())))) {
                Marker marker = null;
                if (mAllMarkers != null && task.getUidMarker() != null && !task.getUidMarker().isEmpty()) {
                    try {
                        for (Marker tmp : mAllMarkers) {
                            if (tmp.getId().toString().toLowerCase().equals(task.getUidMarker().toLowerCase())) {
                                marker = tmp;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        marker = null;
                    }
                }

                if (th.isWholeDayTask(task, isMyTerm ? task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) : !task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()))) {
                    // ЗАДАЧИ НА 1 ДЕНЬ
                    //AllDay event
                /*startTime = Calendar.getInstance();
                startTime.setTime(new Date(termBegin));
                startTime.set(Calendar.HOUR_OF_DAY, 0);
                startTime.set(Calendar.MINUTE, 0);
                endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR_OF_DAY, 23);
                WeekViewEvent event = new WeekViewEvent(task.getIdTask(), task.getName(), null, startTime, endTime, true);
                setMarker(marker, event);
                mEvents.add(event);*/
                } else {
                    if (th.isSomeDaysTask(task, isMyTerm ? task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) : !task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()))) {
                        // ЗАДАЧИ НА НЕСКОЛЬКО ДНЕЙ
                    /*startTime = Calendar.getInstance();
                    startTime.setTime(new Date(termBegin));
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, 23);
                    WeekViewEvent event = new WeekViewEvent(task.getIdTask(), task.getName(), null, startTime, endTime, true);
                    setMarker(marker, event);
                    mEvents.add(event);*/
                    } else {
                        // если старая задача и создавалась на андройде
                        // то там время конца и начала одинаковое
                        // надо сделать разницу в час
                        if (termBegin == termEnd) {
                            startTime = Calendar.getInstance();
                            startTime.setTime(new Date(termBegin));

                            endTime = Calendar.getInstance();
                            endTime.setTime(new Date(termEnd));

                            if (endTime.getTime().getHours() == 23 && endTime.getTime().getMinutes() >= 30) {  // TODO
                                //int mins = endTime.getTime().getMinutes();

                                endTime.set(Calendar.MINUTE, 59);
                                WeekViewEvent event = new WeekViewEvent(task.getUid(), task.getName(), startTime, endTime, task);
                                setMarker(marker, event);
                                mEvents.add(event);
                                //
                        /*endTime = Calendar.getInstance();
                        endTime.add(Calendar.DAY_OF_WEEK, 1);
                        endTime.set(Calendar.HOUR_OF_DAY, 0);
                        startTime = (Calendar) endTime.clone();
                        startTime.set(Calendar.MINUTE, 0);
                        endTime.set(Calendar.MINUTE, 60-mins);
                        event = new WeekViewEvent(task.getIdTask(), task.getName(), startTime, endTime);
                        setMarker(marker, event);
                        mEvents.add(event);*/
                            } else {
                                endTime.add(Calendar.MINUTE, 30);
                                WeekViewEvent event = new WeekViewEvent(task.getUid(), task.getName(), startTime, endTime, task);
                                setMarker(marker, event);
                                mEvents.add(event);
                            }
                        } else {
                            startTime = Calendar.getInstance();
                            startTime.setTime(new Date(termBegin));

                            endTime = Calendar.getInstance();
                            endTime.setTime(new Date(termEnd));

                            WeekViewEvent event = new WeekViewEvent(task.getUid(), task.getName(), startTime, endTime, task);
                            setMarker(marker, event);
                            mEvents.add(event);
                        }
                    }
                }
            } else {
                //android.util.Log.v("Tedorius", "мимо " + task.getName());
            }
        }
        return isNewTaskAdded;
    }

    @SuppressWarnings("deprecation")
    private void setMarker(Marker marker, WeekViewEvent event) {
        if (marker == null) {
            setBackgroundColorDefault(event);
            setTextColorDefault(event);

        } else {
            if (marker.getBackColor() == null || Marker.NO_COLOR.equals(marker.getBackColor())) {
                setBackgroundColorDefault(event);
            } else {
                event.setColor(parseColor(marker.getBackColor()));
            }

            if (marker.getTextColor() == null || Marker.NO_COLOR.equals(marker.getTextColor())) {
                setTextColorDefault(event);
            } else {
                final int colorText = parseColor(marker.getTextColor());
                event.setTextColor(colorText);
            }
        }
    }

    private void setBackgroundColorDefault(WeekViewEvent event) {
        final int color = Color.WHITE;
        event.setColor(color);
    }

    private void setTextColorDefault(WeekViewEvent event) {
        final int color = Color.BLACK;
        event.setTextColor(color);
    }

    private int parseColor(String color) {
        int clr = -1;
        try {
            clr = Color.parseColor(color);
        } catch (Exception e) {
            clr = Color.BLACK;
        } finally {
            return clr;
        }
    }

    @Override
    public void onTaskSaved(LTask task) {
        // если сохранять повторяющуюся задачу то после выхода
        if (convertTaskToEvent(task)) {
            //ToastController.getInstance(DayCalendarActivity.this).showToast(getString(R.string.task_added));
        }

        long date = task.getTermBegin();

        if (date > 0) {
            Calendar day = Calendar.getInstance();
            day.setTimeInMillis(date);
            mWeekView.goToDateAfterSave(day);
        }
    }
}
