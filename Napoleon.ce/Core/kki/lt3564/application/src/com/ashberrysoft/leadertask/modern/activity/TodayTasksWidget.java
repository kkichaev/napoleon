package com.ashberrysoft.leadertask.modern.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.adapter.LTasksCursorAdapter;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.service.TodayWidgetAdapterService;

import java.util.Date;

public class TodayTasksWidget extends AppWidgetProvider {

    public static final String TODAY_LIST_CLICK = "TODAY_LIST_CLICK";
    public static final String EXTRA_ITEM = "TODAY_ITEM";

    public LTasksCursorAdapter mAdapter;

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Log.d("awd","Se actualiza el widget: " +i);
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_leadertsk);

        Intent adapter = new Intent(context, TodayWidgetAdapterService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        remoteViews.setRemoteAdapter(R.id.list_tasks_widget, adapter);
        remoteViews.setTextViewText(R.id.text_today, TimeHelper.getInstance().getCuteDateTitleS(new Date(System.currentTimeMillis())));

        Intent toastIntent = new Intent(context, TodayTasksWidget.class);
        toastIntent.setAction(TodayTasksWidget.TODAY_LIST_CLICK);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.list_tasks_widget, toastPendingIntent);

        return remoteViews;
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction().equals(TODAY_LIST_CLICK)) {
            String taskUUID =  intent.getStringExtra(EXTRA_ITEM);
            Intent i = EditTaskActivity.newInstance(context, taskUUID);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        super.onReceive(context, intent);
    }

}


