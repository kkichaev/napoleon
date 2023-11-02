package com.ashberrysoft.leadertask.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.LoginActivity;
import com.ashberrysoft.leadertask.activities.WidgetActivity;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public abstract class BaseWidget extends AppWidgetProvider {

    public static final String ACTION_WIDGET_CLICK_AUDIO = "com.ashberrysoft.leadertask.widget.ACTION_WIDGET_CLICK_AUDIO";
    public static final String ACTION_WIDGET_CLICK_PHOTO = "com.ashberrysoft.leadertask.widget.ACTION_WIDGET_CLICK_PHOTO";
    public static final String ACTION_WIDGET_CLICK_TASK = "com.ashberrysoft.leadertask.widget.ACTION_WIDGET_CLICK_TASK";

    public static final String EXTRA_WIDGET_TYPE = "com.ashberrysoft.leadertask.widget.EXTRA_WIDGET_TYPE";

    private static final long PAUSE_BETWEEN_MESSAGES = 5000;

    public enum WidgetType {
        AUDIO(R.drawable.widget_audio_record, ACTION_WIDGET_CLICK_AUDIO), PHOTO(R.drawable.widget_photo, ACTION_WIDGET_CLICK_PHOTO), TASK(R.drawable.widget_create_task, ACTION_WIDGET_CLICK_TASK);

        final int mImageRes;
        final Intent mIntent;

        WidgetType(int imageResource, String action) {
            mImageRes = imageResource;

            mIntent = new Intent(action);
            mIntent.putExtra(EXTRA_WIDGET_TYPE, ordinal());
        }

        public int getImageRes() {
            return mImageRes;
        }

        public Intent getIntent() {
            return mIntent;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final Intent intent = new Intent(context, WidgetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BaseWidget.EXTRA_WIDGET_TYPE, WidgetType.PHOTO.ordinal());

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_leadertsk);
        v.setOnClickPendingIntent(R.id.image_view, pendingIntent);
        v.setImageViewResource(R.id.image_view, getWidgetType().getImageRes());

        appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), v);
    }

    protected abstract WidgetType getWidgetType();

    private static long sLastErrorTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        switch (intent.getAction()) {
        case ACTION_WIDGET_CLICK_AUDIO:
            if (LTSettings.getInstance().getUserProfile().isValid()) {
                final Intent intentNew = new Intent(context, SlidingActivity.class);
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentNew);
                //
                final WidgetType type = WidgetType.values()[intent.getIntExtra(EXTRA_WIDGET_TYPE, 0)];
                WidgetActivity.startActivity(context, type);

            } else if (System.currentTimeMillis() - sLastErrorTime > PAUSE_BETWEEN_MESSAGES) {
                sLastErrorTime = System.currentTimeMillis();
                Utils.showToast(context, R.string.t_error_no_auth);
            }
            break;
        case ACTION_WIDGET_CLICK_PHOTO:
            if (LTSettings.getInstance().getUserProfile().isValid()) {
                /*final Intent intentNew = new Intent(context, SlidingActivity.class);
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentNew);*/
                //
                final WidgetType type = WidgetType.values()[intent.getIntExtra(EXTRA_WIDGET_TYPE, 0)];
                WidgetActivity.startActivity(context, type);

            } else if (System.currentTimeMillis() - sLastErrorTime > PAUSE_BETWEEN_MESSAGES) {
                sLastErrorTime = System.currentTimeMillis();
                Utils.showToast(context, R.string.t_error_no_auth);
            }
            break;

        case ACTION_WIDGET_CLICK_TASK:
            if (LTSettings.getInstance().getUserProfile().isValid()) {
                final Intent intentNew = new Intent(context, SlidingActivity.class);
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentNew);
                //
                final WidgetType type = WidgetType.values()[intent.getIntExtra(EXTRA_WIDGET_TYPE, 0)];
                WidgetActivity.startActivity(context, type);

            } else if (System.currentTimeMillis() - sLastErrorTime > PAUSE_BETWEEN_MESSAGES) {
                sLastErrorTime = System.currentTimeMillis();
                Utils.showToast(context, R.string.t_error_no_auth);
            }
            break;
        default:
            break;
        }
    }
}