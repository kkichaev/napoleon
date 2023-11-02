package com.ashberrysoft.leadertask.modern.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.activity.TodayTasksWidget;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.j256.ormlite.stmt.query.In;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Антон on 25.06.2018.
 */

public class MyFactory implements RemoteViewsService.RemoteViewsFactory {

//    ArrayList<String> data;
    ArrayList <LTask> data;
    Context context;
    SimpleDateFormat sdf;
    int widgetID;

    MyFactory(Context ctx, Intent intent) {
        context = ctx;
        sdf = new SimpleDateFormat("HH:mm:ss");
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
//        data = new ArrayList<String>();
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView = new RemoteViews(context.getPackageName(), R.layout.item);
        rView.setTextViewText(R.id.tvItemText, data.get(position).getName());

        LTask mTask = data.get(position);
        final TaskStatus status = TaskStatus.getTaskStatus(mTask);
        if (mTask.getSeriesType() == TaskSeriesCalculator.SeriesType.NONE.ordinal()) {
            rView.setImageViewResource(R.id.iv_task_status, status.getResId());
        } else {
            rView.setImageViewResource(R.id.iv_task_status, status.getSeriesResId());
        }
/*
        Intent intent = EditTaskActivity.newInstance(context, mTask, false, false);
        PendingIntent pause = PendingIntent.getActivity(context, 0, intent, 0);
        //@SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        //rView.setOnClickPendingIntent(R.id.main_task_info_container, pause);

        rView.setOnClickPendingIntent(R.id.main_task_info_container, getPendingSelfIntent(context, mTask));
*/

        Bundle extras = new Bundle();
        extras.putString(TodayTasksWidget.EXTRA_ITEM, mTask.getUid());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rView.setOnClickFillInIntent(R.id.main_task_info_container, fillInIntent);


        return rView;
    }

    protected PendingIntent getPendingSelfIntent(Context context, LTask mTask) {
        //Intent intent = new Intent(context, getClass());
        //intent.setAction(action);
        Intent intent = EditTaskActivity.newInstance(context, mTask, false, false);
        //PendingIntent pause = PendingIntent.getActivity(context, 0, intent, 0);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        data.clear();

        final boolean[] load = {false};
        final ArrayList<LTask> tasks = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor c = null;

                try {
                    c = context.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, new TaskSelectionBuilder(new StringBuilder()).getCalendarByDay(TimeHelper.currentTimeMillisWithoutTimeZone()).build(), null, null);
                    if (c != null) {
                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                            LTask task = new LTask(c);
                            tasks.add(task);
                        }
                    }

                } finally {
                    if (c != null) {
                        c.close();
                    }
                    load[0] = true;
                }
            }
        }).start();

        while (load[0] == false) {
            if (load[0] == true) {

            }
        }

        data.addAll(tasks);
    }

    @Override
    public void onDestroy() {

    }

    /*
    package com.ashberrysoft.leadertask.modern.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.loader.CalendarDayLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

    public class MyFactory implements RemoteViewsService.RemoteViewsFactory {

        ArrayList <LTask> data;
        Context mContext;
        int widgetID;

        MyFactory(Context ctx, Intent intent) {
            mContext = ctx;
            widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            data = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rView = new RemoteViews(mContext.getPackageName(), R.layout.item);
            rView.setTextViewText(R.id.tvItemText, data.get(position).getName());
            return rView;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDataSetChanged() {
            data.clear();
            data = getTodayTasks(mContext);
        }

        @Override
        public void onDestroy() {

        }

        private ArrayList <LTask> getTodayTasks(Context context) {
            ArrayList <LTask> tasks = new ArrayList<>();
            Cursor c = null;

            try {
                c = context.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null,//
                        new TaskSelectionBuilder(new StringBuilder()).getCalendarByDay(TimeHelper.currentTimeMillisWithoutTimeZone()).build(), null, null);
                if (c != null) {
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        LTask task = new LTask(c);
                        tasks.add(task);
                    }
                }

            } finally {
                if (c != null) {
                    c.close();
                }
                return tasks;
            }
        }

    }

     */

}
