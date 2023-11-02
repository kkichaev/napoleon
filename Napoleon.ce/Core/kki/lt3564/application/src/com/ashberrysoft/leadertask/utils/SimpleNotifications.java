package com.ashberrysoft.leadertask.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SimpleNotifyContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.SimpleNotify;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.Utils.TaskUtils;
import com.j256.ormlite.stmt.Where;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@Deprecated
public class SimpleNotifications {

    private static final String CLASS_PATH = "com.ashberrysoft.leadertask.utils.SimpleNotifications.";
    public static final String EXTRA_TASK_UUID = CLASS_PATH + "EXTRA_TASK_UUID";
    public static final String EXTRA_TASK = CLASS_PATH + "EXTRA_TASK";
    public static final String ACTION_SHOW_NOTIFICATION = CLASS_PATH + "ACTION_SHOW_NOTIFICATION";
    public static final String ACTION_OPEN_TASK = CLASS_PATH + "ACTION_OPEN_TASK";

    // INSTANCE
    private static SimpleNotifications sInstance;

    // VALUE's
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private final AlarmManager mAlarmManager;
    private final LTSettings mSettings;
    private final DbHelper mDbHelper;
    private final Uri mNotifySound;

    private Calendar mPrimary;
    private Calendar mSecondary;
    private int mLastId;

    public static SimpleNotifications getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SimpleNotifications.class) {
                if (sInstance == null) {
                    sInstance = new SimpleNotifications(context);
                }
            }
        }
        return sInstance;
    }

    private SimpleNotifications(Context context) {
        mContext = context.getApplicationContext();

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mSettings = LTSettings.getInstance(mContext);
        mDbHelper = DbHelper.getInstance(mContext);

        mNotifySound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE //
                + "://" + mContext.getPackageName() + "/raw/reminder");

        mSecondary = Calendar.getInstance();
        final String time = String.valueOf(System.currentTimeMillis());
        mLastId = Integer.parseInt(time.substring(time.length() / 2));
    }

    /** Метод для первой прогонки задач в уведомления */
    public void convertAllTasksToSimpleNotifies() {
        final List<Task> tasks;
        try {
            mSecondary.setTimeInMillis(System.currentTimeMillis());
            convertToGMT(true);

            final Where<Task, UUID> where = mDbHelper.getTaskDao().queryBuilder().where();
            where.isNotNull(TaskContract.FIELD_TERM_BEGIN);
            where.and().gt(TaskContract.FIELD_TERM_BEGIN, mPrimary.getTime());
            tasks = where.query();

        } catch (SQLException e) {
            Utils.toLog(e);
            return;
        }

        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        clearAllSimpleNotifies(false);

        final List<SimpleNotify> notifies = new ArrayList<SimpleNotify>();
        for (Task task : tasks) {
            final SimpleNotify notify = convertTaskToSimpleNotify(task);
            if (notify != null) {
                notifies.add(notify);
            }
        }

        if (notifies.isEmpty()) {
            return;
        }

        final ContentValues[] cvs = new ContentValues[notifies.size()];
        for (int i = 0; i < cvs.length; i++) {
            cvs[i] = notifies.get(i).getContentValues();
        }

        mContext.getContentResolver().bulkInsert(SimpleNotifyContract.CONTENT_URI, cvs);
        connectAllSimpleNotifiesToTrigger();
    }

    /** Метод для перегонки новой задачи в уведомление */
    public void convertNewTaskToSimpleNotify(Task task) {
        final SimpleNotify notify = convertTaskToSimpleNotify(task);
        if (notify == null) {
            return;
        }

        final Uri uri = mContext.getContentResolver()//
                .insert(SimpleNotifyContract.CONTENT_URI, notify.getContentValues());
        notify.setId(ContentUris.parseId(uri));

        connectSimpleNotifyToTrigger(notify);
    }

    /** Метод для перегонки старой задачи в уведомление */
    public void updateOldSimpleNotify(Task task) { // TODO
        final String taskId = String.valueOf(task.getId());
        final SimpleNotify oldNotify = getSimpleNotifyByTaskId(taskId);
        if (oldNotify == null) {
            convertNewTaskToSimpleNotify(task);
            return;
        }

        final SimpleNotify newNotify = convertTaskToSimpleNotify(task);
        if (newNotify == null) {
            disconnectSimpleNotifyFromTrigger(oldNotify);
            deleteSimpleNotifyByTaskId(taskId);
            return;
        }

        newNotify.setId(oldNotify.getId());

        mContext.getContentResolver()//
                .update(SimpleNotifyContract.CONTENT_URI, newNotify.getContentValues(), SimpleNotifyContract.selectionId(oldNotify.getId()), null);

        connectSimpleNotifyToTrigger(newNotify);
    }

    /** Метод соединяющий все уведомления из базы с триггером времени */
    public void connectAllSimpleNotifiesToTrigger() {
        if (!mSettings.isReminder()) {
            return;
        }

        final Cursor c = mContext.getContentResolver().query(SimpleNotifyContract.CONTENT_URI, null, null, null, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            connectSimpleNotifyToTrigger(new SimpleNotify(c));
        }
        c.close();
    }

    /** Метод отсоединяющий все уведомления из базы от триггера времени */
    public void disconnectAllSimpleNotifiesFromTrigger() {
        final Cursor c = mContext.getContentResolver().query(SimpleNotifyContract.CONTENT_URI, null, null, null, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            disconnectSimpleNotifyFromTrigger(new SimpleNotify(c));
        }
        c.close();
    }

    /** Метод отсоединяющий все уведомления от триггера и удаляющий все записи из базы */
    public void clearAllSimpleNotifies(boolean cancelNotifications) {
        if (cancelNotifications) {
            mNotificationManager.cancelAll();
        }
        disconnectAllSimpleNotifiesFromTrigger();
        deleteAllSimpleNotifies();
    }

    /** Метод запускающий уведомление */
    public void showNotification(String taskId) {
        synchronized (SimpleNotifications.class) {
            final Cursor c = mContext.getContentResolver()//
                    .query(TaskContract.CONTENT_URI, null, TaskContract.selectionFieldUid(taskId), null, null);

            Task task = null;
            if (c.getCount() == 1 && c.moveToFirst()) {
                task = new Task(c);
            }
            c.close();

            if (task == null) {
                return;
            }

            deleteSimpleNotifyByTaskId(taskId);

            final Intent intent = HomeActivity.newInstance(mContext);
            intent.setAction(ACTION_OPEN_TASK);
            intent.putExtra(EXTRA_TASK, task);

            final PendingIntent pending = PendingIntent.getActivity(mContext, mLastId++, intent, 0);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setContentTitle(mContext.getString(R.string.notification_title));
            builder.setTicker(mContext.getString(R.string.app_name));
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setWhen(System.currentTimeMillis());
            builder.setContentIntent(pending);
            builder.setContentText(task.getName());
            builder.setAutoCancel(true);

            builder.setSound(mNotifySound);
            builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

            mNotificationManager.notify(mLastId++, builder.build());
        }
    }

    private SimpleNotify getSimpleNotifyByTaskId(String taskId) {
        final Cursor c = mContext.getContentResolver().query//
                (SimpleNotifyContract.CONTENT_URI, null, SimpleNotifyContract.selectionTaskId(taskId), null, null);

        SimpleNotify notify = null;
        if (c.getCount() == 1 && c.moveToFirst()) {
            notify = new SimpleNotify(c);
        } else if (c.getCount() > 1) {
            deleteSimpleNotifyByTaskId(taskId);
        }
        c.close();

        return notify;
    }

    private void disconnectSimpleNotifyFromTrigger(SimpleNotify notify) {
        deleteOldSimpleNotify(notify);
        mAlarmManager.cancel(getOperationForAlarmManager(notify));
    }

    private void connectSimpleNotifyToTrigger(SimpleNotify notify) {
        if (!mSettings.isReminder() || deleteOldSimpleNotify(notify) != null) {
            return;
        }

        Utils.toLog("connectSimpleNotifyToTrigger = " + new Date(notify.getNotifyTime()) + //
                "\t millis = " + notify.getNotifyTime());

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, notify.getNotifyTime(), getOperationForAlarmManager(notify));
    }

    private Integer deleteOldSimpleNotify(SimpleNotify notify) {
        if (notify.getNotifyTime() < System.currentTimeMillis()) {
            return mContext.getContentResolver()//
                    .delete(SimpleNotifyContract.CONTENT_URI, SimpleNotifyContract.selectionId(notify.getId()), null);
        }
        return null;
    }

    private PendingIntent getOperationForAlarmManager(SimpleNotify notify) {
        final Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(EXTRA_TASK_UUID, notify.getTaskId());

        return PendingIntent.getBroadcast(mContext, notify.getIntId(), intent, 0);
    }

    private SimpleNotify convertTaskToSimpleNotify(Task task) {
        if (task.getTermBegin() == null || oneDayTask(task)) {
            return null;
        }

        mSecondary.setTimeInMillis(task.getTermBegin().getTime());
        convertToGMT(false);

        final boolean mainCondition = mPrimary.getTimeInMillis() > System.currentTimeMillis()//
                && workWithTask(task);

        if (mainCondition) {
            final SimpleNotify notify = new SimpleNotify();
            notify.setTaskId(String.valueOf(task.getId()));
            notify.setNotifyTime(mPrimary.getTimeInMillis());

            return notify;
        }

        return null;
    }

    /** Условие пригодности задачи для конвертации */
    private boolean workWithTask(Task task) {
        return !TaskUtils.isCompleted(task, mSettings.getUserName()) && !(!mSettings.getUserName().equals(task.getCustomer())//
                && !mSettings.getUserName().equals(task.getPerformer()));
    }

    /** Условие однодневности задачи */
    private boolean oneDayTask(Task task) {
        return task.getTermEnd() != null && Utils.wholeDayTask(task, true);
    }

    private void deleteSimpleNotifyByTaskId(String taskId) {
        mContext.getContentResolver()//
                .delete(SimpleNotifyContract.CONTENT_URI, SimpleNotifyContract.selectionTaskId(taskId), null);
    }

    private void deleteAllSimpleNotifies() {
        mContext.getContentResolver().delete(SimpleNotifyContract.CONTENT_URI, null, null);
    }

    private void convertToGMT(boolean add) {
        mSecondary.set(Calendar.SECOND, 0);
        mSecondary.set(Calendar.MILLISECOND, 0);

        final Date date = mSecondary.getTime();
        final TimeZone timeZone = mSecondary.getTimeZone();
        final long timeInMilliseconds = date.getTime();
        final int offsetFromUTC = timeZone.getOffset(timeInMilliseconds);

        if (mPrimary == null) {
            mPrimary = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        }

        mPrimary.setTime(date);
        mPrimary.add(Calendar.MILLISECOND, (add ? 1 : -1) * offsetFromUTC);
    }
}