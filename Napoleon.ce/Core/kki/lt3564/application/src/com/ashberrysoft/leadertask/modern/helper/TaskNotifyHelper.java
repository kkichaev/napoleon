package com.ashberrysoft.leadertask.modern.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskNotifyContract;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.modern.activity.CloseTaskReceiver;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.activity.TomorrowTaskReceiver;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.TaskNotify;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.receivers.AlarmBroadcastReceiver;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskNotifyHelper {

    public static final String ACTION_SHOW_NOTIFICATION = "com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper.ACTION_SHOW_NOTIFICATION";
    public static final String ACTION_SHOW_NOTIFICATION_NOW = "com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper.ACTION_SHOW_NOTIFICATION_NOW ";
    public static final String EXTRA_TASK_ID = "com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper.EXTRA_TASK_ID";

    private final Object LOCK = new Object();

    // INSTANCE
    private static TaskNotifyHelper sInstance;
    public static int ChonoCode = 5467824;
    public static String CHANNEL_ID = "lt_channel";// The id of the channel.

    // VALUE's
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private final AlarmManager mAlarmManager;
    private final LTSettings mSettings;
    private final TimeHelper mTimeHelper;
    private final TaskSelectionBuilder mSelectionBuilder;

    private final Calendar mCalendar;

    public static TaskNotifyHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TaskNotifyHelper.class) {
                if (sInstance == null) {
                    sInstance = new TaskNotifyHelper(context);
                }
            }
        }
        return sInstance;
    }

    private TaskNotifyHelper(Context context) {
        mContext = context.getApplicationContext();

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mSettings = LTSettings.getInstance();
        mTimeHelper = TimeHelper.getInstance();
        mSelectionBuilder = new TaskSelectionBuilder();

        //mNotifySound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/reminder");

        mCalendar = Calendar.getInstance();
    }

    public void initChannels(Context context) {
        // Sets an ID for the notification, so it can be updated.
        try {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "LeaderTask", importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }

        } catch (Exception e) {

        }
    }

    public void convertTasksToNotify() {
        OperationAllWithTrigger(false, true);

        List<LTask> tasks = getTasksWithTerm();
        clearAllTaskNotifies(false);
        if (!tasks.isEmpty()) {

            for (LTask task : tasks) {
                updateTaskNotify(task);
            }

        }

        tasks = getTasksWithPlan();
        if (!tasks.isEmpty()) {

            for (LTask task : tasks) {
                updateTaskNotifyChrono(task, ChonoCode);
            }
        }
    }

    private List<LTask> getTasksWithTerm() {
        final long date = TimeHelper.currentTimeMillisWithoutTimeZone();
        // Utils.toLog("\n > getTasksWithTerm date = " + date);

        Cursor c = null;

        try {
            mSelectionBuilder.clear();
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, mSelectionBuilder.getTasksWithTerm(date).build(), null, null);

            if (c.getCount() > 0) {
                final List<LTask> tasks = new ArrayList<>(c.getCount());
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    tasks.add(new LTask(c));
                }
                return tasks;
            }
            return new ArrayList<>(0);

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private List<LTask> getTasksWithPlan() {

        Cursor c = null;

        try {
            mSelectionBuilder.clear();
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, mSelectionBuilder.getTasksWithPlan().build(), null, null);

            if (c.getCount() > 0) {
                final List<LTask> tasks = new ArrayList<>(c.getCount());
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    tasks.add(new LTask(c));
                }
                return tasks;
            }
            return new ArrayList<>(0);

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void cancelNotify(LTask task) {
        deleteTaskNotifyById(task.getIdTask());
        mNotificationManager.cancel(task.getIdTask());
    }

    public void cancelNotifyChrono(LTask task, int code) {
        deleteTaskNotifyById(task.getIdTask() + code);
        mNotificationManager.cancel(task.getIdTask() + code);
    }

    public void cancelNotifyByID(int taskID) {
        mNotificationManager.cancel(taskID);
    }

    public void updateTaskNotify(LTask task) {
        deleteOldTaskNotify(task);

        final TaskNotify newNotify = convertTaskToNotify(task);
        if (newNotify == null) {
            return;
        }

        mContext.getContentResolver().insert(TaskNotifyContract.CONTENT_URI, newNotify.getContentValues(null));

        connectTaskNotifyToTrigger(newNotify);
    }

    public void updateTaskNotifyChrono(LTask task, int code) {
        deleteOldTaskNotifyChrono(task, code);

        final TaskNotify newNotify = convertTaskToNotifyChrono(task, code);
        if (newNotify == null) {
            return;
        }

        mContext.getContentResolver().insert(TaskNotifyContract.CONTENT_URI, newNotify.getContentValues(null));

        connectTaskNotifyToTrigger(newNotify);
    }

    public void showTaskNotifyNewAssignmentToMe(LTask task, boolean needSound) {
        new ShowNotify(task.getIdTask(), SelectionKeeper.equals(new StringBuilder(), LTaskContract._ID, task.getIdTask()), 1, needSound, false).start();
    }

    public void showTaskNotifyCancelAssignmentFromMe(LTask task, boolean needSound) {
        new ShowNotify(task.getIdTask(), SelectionKeeper.equals(new StringBuilder(), LTaskContract._ID, task.getIdTask()), 2, needSound, false).start();
    }

    public void showTaskNotifyNewComment(LTask task, boolean needSound, boolean manyComments) {
        new ShowNotify(task.getIdTask(), SelectionKeeper.equals(new StringBuilder(), LTaskContract._ID, task.getIdTask()), 3, needSound, manyComments).start();
    }

    public TaskNotify deleteOldTaskNotify(LTask task) {
        final TaskNotify oldNotify = getTaskNotifyByTaskId(task.getIdTask());
        if (oldNotify == null) {
            return null;
        }

        deleteTaskNotifyById(oldNotify.getId());
        disconnectTaskNotifyFromTrigger(oldNotify);

        return oldNotify;
    }

    public TaskNotify deleteOldTaskNotifyChrono(LTask task, int code) {
        final TaskNotify oldNotify = getTaskNotifyByTaskId(task.getIdTask()+code);
        if (oldNotify == null) {
            return null;
        }

        deleteTaskNotifyById(oldNotify.getId()+code);
        disconnectTaskNotifyFromTrigger(oldNotify);

        return oldNotify;
    }

    public void connectAllTaskNotifiesToTrigger() {
        if (mSettings.isReminder()) {
            OperationAllWithTrigger(true, false);
        }
    }

    public void disconnectAllTaskNotifiesFromTrigger() {
        OperationAllWithTrigger(false, false);
    }


    public void clearAllTaskNotifies(boolean cancelNotifications) {
        if (cancelNotifications) {
            mNotificationManager.cancelAll();
        }
        try {
            OperationAllWithTrigger(false, true);
        }
        catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private TaskNotify getTaskNotifyByTaskId(int taskId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(TaskNotifyContract.CONTENT_URI, null,//
                    getSelectionById(taskId), null, null);

            if (c.moveToFirst()) {
                return new TaskNotify(c);
            }
            return null;

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void disconnectTaskNotifyFromTrigger(TaskNotify notify) {
        mAlarmManager.cancel(getOperationForAlarmManager(notify));
    }

    private void connectTaskNotifyToTrigger(TaskNotify notify) {
        if (mSettings.isReminder()) {
            final long date = TimeHelper.addTimeZone(notify.getTime());
            //final long date = notify.getTime();
            //mAlarmManager.set(AlarmManager.RTC_WAKEUP, date, getOperationForAlarmManager(notify));

            PendingIntent pi = getOperationForAlarmManager(notify);
            int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                mAlarmManager.setExactAndAllowWhileIdle(ALARM_TYPE, date, pi);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                mAlarmManager.setExact(ALARM_TYPE, date, pi);
            else
                mAlarmManager.set(ALARM_TYPE, date, pi);
        }
    }

    private PendingIntent getOperationForAlarmManager(TaskNotify notify) {
        /*Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        // create an explicit intent by defining a class
        intent.setClass(mContext, AlarmBroadcastReceiver.class);
        intent.putExtra(EXTRA_TASK_ID, notify.getId());

        return PendingIntent.getBroadcast(mContext, notify.getId(), intent, 0);*/
        return startAlarmBroadcastReceiver(mContext, 0 , notify);
    }

    // ТАК РАБОТАЕТ НА КСИАОМИ
   public PendingIntent startAlarmBroadcastReceiver(Context context, long delay, TaskNotify notify) {
        Intent _intent = new Intent(context, AlarmBroadcastReceiver.class);
        _intent.setClass(context, AlarmBroadcastReceiver.class);
        _intent.setAction(ACTION_SHOW_NOTIFICATION);
        _intent.putExtra(EXTRA_TASK_ID, notify.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notify.getId(), _intent, 0);

        return pendingIntent;
    }


    /*public PendingIntent startAlarmBroadcastReceiver(Context context, long delay, TaskNotify notify) {
        Intent _intent = new Intent(context, AlarmBroadcastReceiver.class);
        _intent.setClass(context, AlarmBroadcastReceiver.class);
        _intent.setAction(ACTION_SHOW_NOTIFICATION);
        _intent.putExtra(EXTRA_TASK_ID, notify.getId());
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, _intent, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notify.getId(), _intent, 0);


        return pendingIntent;
    }*/

    private PendingIntent getOperationForAlarmManagerNow(TaskNotify notify) {
        final Intent intent = new Intent(ACTION_SHOW_NOTIFICATION_NOW);
        intent.putExtra(EXTRA_TASK_ID, notify.getId());

        return PendingIntent.getBroadcast(mContext, notify.getId(), intent, 0);
    }

    private TaskNotify convertTaskToNotify(LTask task) {
        long time = 0;
        if ((mSettings.getUserName().equals(task.getEmailCustomer()) && (mSettings.getUserName().equals(task.getEmailPerformer())))) {
            time = task.getTermBegin();
        } else {
            if (mSettings.getUserName().equals(task.getEmailPerformer())) {
                time = task.getTermBeginCustomer();
            } else {
                time = task.getTermBegin();
            }
        }

        if (time == 0 || oneDayTask(task)) {
            return null;
        }

        mCalendar.setTimeInMillis(time);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        if (mSettings.getNotifyPreTime() == 60) {
            mCalendar.add(Calendar.HOUR, -1);
        } else {
            mCalendar.add(Calendar.MINUTE, -mSettings.getNotifyPreTime());
        }

        final long notifyTime = mCalendar.getTimeInMillis();
        final long currentTime = TimeHelper.currentTimeMillisWithoutTimeZone();

        final boolean mainCondition = notifyTime > currentTime && workWithTask(task);
        if (mainCondition) {
            final TaskNotify notify = new TaskNotify();
            notify.setId(task.getIdTask());
            notify.setTime(notifyTime);

            return notify;
        }
        return null;
    }

    public TaskNotify convertTaskToNotifyChrono(LTask task, int code) {
        int wasInWork = task.getTime()+(int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone()-task.getInWorkTime())/1000);
        if (task.getPlan() == 0 && task.getStatus() == Status.TASK_IN_WORK.getStatusCode() && task.getEmailPerformer().equals(mSettings.getUserName()) && task.getPlan() > wasInWork) {
            return null;
        }

        mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
//        mCalendar.setTimeInMillis(task.getInWorkTime());
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.add(Calendar.SECOND, task.getPlan()-wasInWork);


        final long notifyTime = mCalendar.getTimeInMillis();
        final long currentTime = TimeHelper.currentTimeMillisWithoutTimeZone();
        //final long currentTime = System.currentTimeMillis();

        final boolean mainCondition = notifyTime > currentTime && workWithTask(task);
        if (mainCondition) {
            final TaskNotify notify = new TaskNotify();
            notify.setId(task.getIdTask() + code);
            notify.setTime(notifyTime);
            return notify;
        }
        return null;
    }



    private boolean workWithTask(LTask task) {
        return TaskHelper.isUncompleted(task.getStatus(), mSettings.getUserName(), task.getEmailCustomer())
            && (((mSettings.getUserName().equals(task.getEmailCustomer()) && !(mSettings.getUserName().equals(task.getEmailPerformer()))) || (mSettings.getUserName().equals(task.getEmailPerformer()))));
    }


    private boolean oneDayTask(LTask task) {
        return task.getTermEnd() != 0 && mTimeHelper.isWholeDayTask(task, true);
    }

    private void deleteTaskNotifyById(int taskId) {
        mContext.getContentResolver().delete(TaskNotifyContract.CONTENT_URI, getSelectionById(taskId), null);
    }


    public void showNotification(int taskId) {
        new ShowNotify(taskId, SelectionKeeper.equals(new StringBuilder(), LTaskContract._ID, taskId), 0, true, false).start();
    }

    /** Показывает уведомление, если задача есть в БД */
    private final class ShowNotify extends Thread {

        private int mTaskId;
        private final String mSelection;
        private final int mTypeNotify;
        private final boolean mIsNeedSound;
        private final boolean mManyComments;

        public ShowNotify(int taskId, String selection, int type, boolean needSound, boolean manyComments) {
            super(ShowNotify.class.getSimpleName());

            mTaskId = taskId;
            mSelection = selection;
            mTypeNotify = type;
            mIsNeedSound = needSound;
            mManyComments = manyComments;
        }

        @Override
        public void run() {
            super.run();

            boolean found = false;

            deleteTaskNotifyById(mTaskId);
            mNotificationManager.cancel(mTaskId);

            LTask task = null;
            Cursor c = null;

            try {
                c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, mSelection, null, null);

                if (c.moveToFirst()) {
                    task = new LTask(c);
                    found = true;

                }

            } finally {
                if (c != null) {
                    c.close();
                }
            }
            if (found) {
                final Intent intent = SlidingActivity.newInstance(mContext, task);
                final PendingIntent pending = PendingIntent.getActivity(mContext, mTaskId, intent, 0);

                Notification.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder = new Notification.Builder(mContext, CHANNEL_ID);
                } else {
                    builder = new Notification.Builder(mContext);
                }

                if (mTypeNotify == 1) {
                    builder.setContentTitle(mContext.getString(R.string.new_assignment));
                } else {
                    if (mTypeNotify == 0) {
                        builder.setContentTitle(mContext.getString(R.string.notification_title));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String usr = mSettings.getUserName();

                            if (task.getEmailCustomer().equals(usr) || task.getEmailPerformer().equals(usr)) {
                                Intent i = new Intent(mContext, CloseTaskReceiver.class);
                                i.putExtra(SlidingActivity.EXTRA_TASK, task);
                                PendingIntent cp = PendingIntent.getBroadcast(mContext, mTaskId, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                Notification.Action action = new Notification.Action(-1, mContext.getString(R.string.task_close_noty), cp);
                                builder.addAction(action);
                            }

                            Intent i2 = new Intent(mContext, TomorrowTaskReceiver.class);
                            i2.putExtra(SlidingActivity.EXTRA_TASK, task);
                            PendingIntent cp = PendingIntent.getBroadcast(mContext, mTaskId, i2, PendingIntent.FLAG_UPDATE_CURRENT);

                            Notification.Action action = new Notification.Action(-1, mContext.getString(R.string.task_tomorrow_noty), cp);
                            builder.addAction(action);
                        }
                    } else {
                        if (mTypeNotify == 2) {
                            builder.setContentTitle(mContext.getString(R.string.notification_task_done));
                        } else {
                            if (mTypeNotify == 3) {
                                if (mManyComments) {
                                    builder.setContentTitle(mContext.getString(R.string.notification_new_comments));
                                } else {
                                    builder.setContentTitle(mContext.getString(R.string.notification_new_comment));
                                }
                            }
                        }
                    }
                }
                builder.setTicker(mContext.getString(R.string.app_name));
                builder.setSmallIcon(R.drawable.notification_icon);
                builder.setWhen(System.currentTimeMillis());
                builder.setContentIntent(pending);

                builder.setContentText(task.getName());
                builder.setAutoCancel(true);
                if (mIsNeedSound) {
                    setSound(mContext, builder);
                }

                initChannels(mContext);
                mNotificationManager.notify(mTaskId, builder.build());
            } else {
                if (mSettings.isShowChrono()) {
                    mTaskId = mTaskId - ChonoCode;
                    deleteTaskNotifyById(mTaskId);
                    mNotificationManager.cancel(mTaskId);

                    final LTask task2;
                    Cursor c2 = null;

                    try {
                        c2 = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, SelectionKeeper.equals(new StringBuilder(), LTaskContract._ID, mTaskId), null, null);

                        if (c2.moveToFirst()) {
                            task2 = new LTask(c2);

                        } else {
                            return;
                        }

                    } finally {
                        if (c2 != null) {
                            c2.close();
                        }
                    }

                    final Intent intent2 = SlidingActivity.newInstance(mContext, task2);
                    final PendingIntent pending2 = PendingIntent.getActivity(mContext, mTaskId, intent2, 0);

                    Notification.Builder builder2;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder2 = new Notification.Builder(mContext, CHANNEL_ID);
                    } else {
                        builder2 = new Notification.Builder(mContext);
                    }

                    builder2.setContentTitle(mContext.getString(R.string.chronometry_notify));
                    builder2.setTicker(mContext.getString(R.string.app_name));
                    builder2.setSmallIcon(R.drawable.notification_icon);
                    builder2.setWhen(System.currentTimeMillis());
                    builder2.setContentIntent(pending2);


                    builder2.setContentText(task2.getName());
                    builder2.setAutoCancel(true);
                    if (mIsNeedSound) {
                        setSound(mContext, builder2);
                    }
                    initChannels(mContext);
                    mNotificationManager.notify(mTaskId, builder2.build());
                }
            }
        }
    }

    public static void setSound(Context context, Notification.Builder builder) {
        if ((LTSettings.getInstance().getUserName().equals("anton.sobolev@leadertask.com") || LTSettings.getInstance().getUserName().equals("ivlievser@gmail.com") || LTSettings.getInstance().getUserName().equals("sergey.lukyanenko@leadertask.com"))) {
            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/temp1"));
        } else {
            builder.setSound(LTSettings.getInstance().isNotifyStandartSound() ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) : Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/reminder"));
        }

        builder.setDefaults(LTSettings.getInstance().isNotifyVibration() ? Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE : Notification.DEFAULT_LIGHTS);
    }

    public void OperationAllWithTrigger(boolean connect, boolean deleteAllOnEnd) {
        Cursor c = null;

        try {
            c = mContext.getContentResolver().query(TaskNotifyContract.CONTENT_URI,//
                    null, null, null, null);

            TaskNotify notify;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                notify = new TaskNotify(c);

                if (connect) {
                    connectTaskNotifyToTrigger(notify);

                } else {
                    disconnectTaskNotifyFromTrigger(notify);
                }
            }

        } catch (Exception e) {

        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (deleteAllOnEnd) {
            mContext.getContentResolver().delete(TaskNotifyContract.CONTENT_URI, null, null);
        }
    }

    private String getSelectionById(int taskId) {
        return SelectionKeeper.equals(new StringBuilder(), TaskNotifyContract._ID, taskId);
    }
}