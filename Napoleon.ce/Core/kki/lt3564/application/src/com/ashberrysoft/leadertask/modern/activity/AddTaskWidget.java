package com.ashberrysoft.leadertask.modern.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ashberrysoft.leadertask.R;

public class AddTaskWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //
        // Construct the RemoteViews object
        /*final RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_leadertsk);
        v.setImageViewResource(R.id.image_view, R.drawable.widget_create_task);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);*/

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_leadertsk);
        //v.setOnClickPendingIntent(R.id.image_view, intent);
        views.setImageViewResource(R.id.image_view, R.drawable.widget_create_task);

        Intent firstDialogIntent = new Intent(context, AddNewTaskWidgetActivity.class);
        // Old activities shouldn't be in the history stack
        firstDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent firstDialogPendingIntent = PendingIntent.getActivity(context,
                0,
                firstDialogIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Link the PendingIntent to a Button
        views.setOnClickPendingIntent(R.id.image_view, firstDialogPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


