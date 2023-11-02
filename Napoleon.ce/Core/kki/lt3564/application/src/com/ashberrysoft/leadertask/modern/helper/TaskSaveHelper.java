package com.ashberrysoft.leadertask.modern.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.VerticalDepthTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.cache.TaskMessageCache;
import com.ashberrysoft.leadertask.modern.changer.BaseTaskChanger;
import com.ashberrysoft.leadertask.modern.changer.ByMeChanger;
import com.ashberrysoft.leadertask.modern.changer.CalendarChanger;
import com.ashberrysoft.leadertask.modern.changer.CategoriesChanger;
import com.ashberrysoft.leadertask.modern.changer.ColorChanger;
import com.ashberrysoft.leadertask.modern.changer.EmpChanger;
import com.ashberrysoft.leadertask.modern.changer.FocusChanger;
import com.ashberrysoft.leadertask.modern.changer.ForMeChanger;
import com.ashberrysoft.leadertask.modern.changer.InboxChanger;
import com.ashberrysoft.leadertask.modern.changer.InworkChanger;
import com.ashberrysoft.leadertask.modern.changer.OverdueChanger;
import com.ashberrysoft.leadertask.modern.changer.ParentChanger;
import com.ashberrysoft.leadertask.modern.changer.ProjectChanger;
import com.ashberrysoft.leadertask.modern.changer.ReadyChanger;
import com.ashberrysoft.leadertask.modern.changer.TaskStatusChanger;
import com.ashberrysoft.leadertask.modern.changer.UnreadChanger;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.ChronoHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.dao.Dao;

import static com.ashberrysoft.leadertask.modern.domains.lion.LTask.MY_TASK_USER_ORDER;
import static com.ashberrysoft.leadertask.modern.domains.lion.LTask.MY_TASK_USER_ORDER_DESC;

public class TaskSaveHelper extends Thread {

    public static final String ACTION_SCROLL_TO_NEW_TASK = "ACTION_SCROLL_TO_NEW_TASK";
    private static final Intent INTENT_SCROLL_TO_NEW_TASK = new Intent(ACTION_SCROLL_TO_NEW_TASK);
    // BASE
    private final Context mContext;

    private final LTask mTask;
    private final boolean mNewTask;
    private final boolean mPaste;
    private final boolean mIsSeriesTask;
    private final List<TaskMessage> mTaskMessages;

    private final LTask mTaskOld;
    private final int mTaskMessagesSaveFrom;

    private final List<TaskFile> mTaskFiles;
    private final List<TaskFile> mTaskFilesDeleted;

    // VALUE's
    private final ContentResolver mCr;
    private final LTSettings mSettings;
    private final DbHelper mDbHelper;
    private final StringBuilder mStringBuilder;
    public static boolean savingTask = false;

    private LTask mTaskParent;

    private UUID mTaskUuid;

    public TaskSaveHelper(boolean isPaste, Context context,//
            LTask task, boolean taskNew, List<TaskMessage> taskMessages, //
            LTask taskOld, int taskMessagesSaveFrom,//
            List<TaskFile> taskFiles, List<TaskFile> taskFilesDeleted, boolean isSeriesTask) {
        super(TaskSaveHelper.class.getSimpleName());

        mContext = context.getApplicationContext();

        mTask = task;
        mNewTask = taskNew;
        mPaste = isPaste;
        mIsSeriesTask = isSeriesTask;
        mTaskMessages = taskMessages;

        mTaskOld = taskOld;
        mTaskMessagesSaveFrom = taskMessagesSaveFrom;

        mTaskFiles = taskFiles;
        mTaskFilesDeleted = taskFilesDeleted;

        mCr = mContext.getContentResolver();
        mSettings = LTSettings.getInstance();
        mDbHelper = DbHelper.getInstance(mContext);
        mStringBuilder = new StringBuilder();
    }

    @Override
    public void run() {
        super.run();

        final long start = System.currentTimeMillis();
        try {
            process();

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            notifyDataSetChanged(start);
            savingTask = false;
        }
    }

    private void process() throws Exception {
        mTask.setUsnEntity(0);
        if (/*mNewTask &&*/ TextUtils.isEmpty(mTask.getUid())) {
            mTaskUuid = UUID.randomUUID();
            mTask.setUid(String.valueOf(mTaskUuid).toUpperCase());

        } else {
            mTaskUuid = UUID.fromString(mTask.getUid());
        }

        new SaveFilesThread().start();
        new SaveMessagesThread().start();

        final String uidParent = mTask.getUIDParent();
        if (uidParent != null) {
            mTaskParent = TaskHelper.getTask(mContext, uidParent);
        }

        final VerticalDepthTask verticalDepth;
        if (mNewTask) {
            changeTaskNew();
            if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                // если стало в работе а был другой
                mTask.setInWorkTime(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());
                mTask.setUsnInWorkTime(mTask.getUsnInWorkTime() + 1);

                if (mTask.getPlan() != 0) {
                    TaskNotifyHelper.getInstance(mContext).updateTaskNotifyChrono(mTask, TaskNotifyHelper.ChonoCode);
                }
            }
            verticalDepth = getVertical();

        } else {
            if (mTaskOld.getStatus() != mTask.getStatus()) {
                // если статусы не равны и было готово к сдаче и статус поменялся -
                //mFootstepHelper.delReadyTotalIfNeed(mTaskOld);

                if (mTaskOld.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                    // если был в работе и изменился
                    if (mTask.getPlan() != 0) {
                        if (mTask.getEmailPerformer().equals(mSettings.getUserName())) {
                            TaskNotifyHelper.getInstance(mContext).deleteOldTaskNotifyChrono(mTask, TaskNotifyHelper.ChonoCode);
                        }
                    }
                    //int wasInWork = (int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone()-mTask.getInWorkTime())/1000);
                    int wasInWork = (int) ChronoHelper.instance.getFactTiming(mTask.getTime(), mTask.getInWorkTime());
                    mTask.setTime(wasInWork);
                    mTask.setUsnTime(mTask.getUsnTime() + 1);
                } else {
                    if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                        // если стало в работе а был другой
                            if (mTaskOld.getUsnInWorkTime() == mTask.getUsnInWorkTime()) {
                                mTask.setInWorkTime(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());
                                mTask.setUsnInWorkTime(mTask.getUsnInWorkTime() + 1);
                            }

                        if (mTask.getPlan() != 0) {
                            TaskNotifyHelper.getInstance(mContext).updateTaskNotifyChrono(mTask, TaskNotifyHelper.ChonoCode);
                        }
                    }
                }
            }
            changeTaskOld();

            Utils.clearStringBuilder(mStringBuilder);
            final VerticalDepthTask vd = TaskHelper.getSingleItem(mContext, VerticalDepthTask.class, VerticalDepthTaskContract.CONTENT_URI,
                    SelectionKeeper.equals(mStringBuilder, VerticalDepthTaskContract._ID, mTask.getIdTask()));
            verticalDepth = vd != null ? vd : getVertical();
        }

        final LTask taskOld = mNewTask ? null : mTaskOld;
        new TaskStatusChanger(mContext, mTask, taskOld).run();

        MenuLoader.getInstance(mContext).resetCount();
        final BaseTaskChanger[] changers = new BaseTaskChanger[14];
        changers[0] = new ParentChanger(mContext, mTask, taskOld, verticalDepth);
        changers[1] = new CalendarChanger(mContext, mTask, taskOld, verticalDepth);
        changers[2] = new InboxChanger(mContext, mTask, taskOld, verticalDepth);
        changers[3] = new ByMeChanger(mContext, mTask, taskOld, verticalDepth);
        changers[4] = new ForMeChanger(mContext, mTask, taskOld, verticalDepth);
        changers[5] = new ProjectChanger(mContext, mTask, taskOld, verticalDepth);
        changers[6] = new CategoriesChanger(mContext, mTask, taskOld, verticalDepth);
        changers[7] = new UnreadChanger(mContext, mTask, taskOld, verticalDepth);
        changers[8] = new ReadyChanger(mContext, mTask, taskOld, verticalDepth);
        changers[9] = new InworkChanger(mContext, mTask, taskOld, verticalDepth);
        changers[10] = new OverdueChanger(mContext, mTask, taskOld, verticalDepth);
        changers[11] = new ColorChanger(mContext, mTask, taskOld, verticalDepth);
        changers[12] = new EmpChanger(mContext, mTask, taskOld, verticalDepth);
        changers[13] = new FocusChanger(mContext, mTask, taskOld, verticalDepth);

        for (BaseTaskChanger changer : changers) {
            changer.run();
        }

        hideNotify();

        // mTaskOld
        // mTask

        if (mNewTask) {
            Utils.startSync((LTApplication) mContext.getApplicationContext());
        } else {
            if (mTask.getDifferencesInTasks(taskOld).size() > 0) {
                Utils.startSync((LTApplication) mContext.getApplicationContext());
            }
        }

        Utils.updateTodayWidget(mContext);
    }

    private void hideNotify() {
        int status = mTask.getStatus();
        if (status ==  1 || status == 7 || status == 5 || status == 8) {
            TaskNotifyHelper.getInstance(mContext.getApplicationContext()).cancelNotify(mTask);
        }
    }

    private void notifyDataSetChanged(long start) {
        /*final long difference = System.currentTimeMillis() - start;
        Utils.toLog(" ~ TaskSaveHelper work time = " + difference);

        if (difference < SLEEP_TIME) {
            try {
                Thread.sleep(SLEEP_TIME - difference);

            } catch (InterruptedException e) {
                Utils.toLog(e);
            }
        }*/

        mCr.notifyChange(LTaskContract.CONTENT_URI, null);
        if (mNewTask && !mIsSeriesTask) {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(INTENT_SCROLL_TO_NEW_TASK);
        }
    }

    private void changeTaskNew() {
        if (LTSettings.needToShowToastAfterAddTask) {
            LTSettings.needToShowToastAfterAddTask = false;
            Utils.showUnbordingToasts(mContext, 0);
        }

        final boolean customer = mSettings.getUserName().equals(mTask.getEmailCustomer());
        final boolean performer = mSettings.getUserName().equals(mTask.getEmailPerformer());

        if (customer && !performer) {
            if (LTSettings.needToShowToastAfterAssign) {
                LTSettings.needToShowToastAfterAssign = false;
                Utils.showUnbordingToasts(mContext, 3);
            }
        }
        //
        mTask.setReaded(true);
        if (mTask.getTermBegin() != 0 && mTask.getTermBeginCustomer() == 0) {
            mTask.setTermBeginCustomer(mTask.getTermBegin());
            mTask.setTermEndCustomer(mTask.getTermEnd());
            mTask.setUsnFieldCustomerTerm(mTask.getUsnFieldCustomerTerm() + 1);
        }
        {
            //final long currentTime = System.currentTimeMillis();
            final long currentTime = TimeHelper.currentTimeMillisWithoutTimeZone();

            mTask.setCreateTime(currentTime);
            mTask.setPerformTime(currentTime);
            mTask.setCompleteTime(currentTime);
        }
        {
            final int latestOrder;
            final double latestOrderNew;
            if (mPaste) {
                latestOrder = mSettings.getMaximumOrder() + 1;
                latestOrderNew = getOrderNewFromManyParentsBot(mTask, mSettings.getUserName()) + 1;
            } else {
                if (LTSettings.getInstance().isAddingTasksToTop()) {
                    latestOrder = mSettings.getMaximumOrder() + 1;
                    latestOrderNew = getOrderNewFromManyParents(mTask, mSettings.getUserName()) - 1;
                } else {
                    latestOrder = mSettings.getMaximumOrder() + 1;
                    latestOrderNew = getOrderNewFromManyParentsBot(mTask, mSettings.getUserName()) + 1;
                }
            }

            //При добавлении новой задачи OrderNew ставить равным максимальный OrderNew в данной родительской задаче
            //среди задач у который заказчик равен текущему пользователю + 1.0
            mSettings.setMaximumOrder(latestOrder);

            mTask.setOrder(latestOrder);
            mTask.setOrderCustomer(latestOrder);
            mTask.setOrderNew(latestOrderNew);
            int myOrder = LTSettings.getInstance().isAddingTasksToTop() ? MY_TASK_USER_ORDER_DESC :  MY_TASK_USER_ORDER;
            mTask.setUserOrder(myOrder);
            if(mTask.getUidMarker() != null) {
                mTask.setUidMarker(mTask.getUidMarker().toUpperCase());
                mTask.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(mContext, mTask.getUidMarker().toUpperCase()));
            }
            mTask.setIsUseTerm(mTask.getTermBegin() == 0 ? LTask.MY_TASK_NOT_USE_TERM_DEFAULT : LTask.MY_TASK_IS_USE_TERM_DEFAULT);
            mTask.setIsUseTermCustomer(mTask.getTermBeginCustomer() == 0 ? LTask.MY_TASK_NOT_USE_TERM_DEFAULT : LTask.MY_TASK_IS_USE_TERM_DEFAULT);
        }

        {
            final Uri uri = mCr.insert(LTaskContract.CONTENT_URI, mTask.getContentValues(null));
            final int id = (int) ContentUris.parseId(uri);
            mTask.setId(id);
        }
        TaskNotifyHelper.getInstance(mContext).updateTaskNotify(mTask);

        final CompletedCache completedCache = CompletedCache.getInstance(mContext);
        final boolean completedParent = mTaskParent != null && completedCache.find(mTaskParent.getIdTask()) != null;
        final boolean completedTask = TaskHelper.isCompleted(mTask.getStatus(), mSettings.getUserName(), mTask.getEmailCustomer());

        if (completedParent || completedTask) {
            final CompletedTask taskCompleted = new CompletedTask();
            taskCompleted.setId(mTask.getIdTask());
            taskCompleted.setUid(mTask.getUid());
            taskCompleted.setParentCompleted(completedParent);
            taskCompleted.setTaskCompleted(completedTask);

            completedCache.updateCache(taskCompleted);
            mContext.getContentResolver().insert(taskCompleted.getContentUri(), taskCompleted.getContentValues(null));
        }
    }

    private void changeTaskOld() {
        final boolean customer = mSettings.getUserName().equals(mTask.getEmailCustomer());
        final boolean performer = mSettings.getUserName().equals(mTask.getEmailPerformer());

        if (customer && !performer) {
            if (LTSettings.needToShowToastAfterAssign) {
                LTSettings.needToShowToastAfterAssign = false;
                Utils.showUnbordingToasts(mContext, 4);
            }
        }

        final ContentValues cv = mTaskOld.getDifference(mTask);
        if (cv.containsKey(LTaskContract.UsnFieldStatus)) {
            if ((customer && mTask.getStatus() == TaskStatus.COMPLETED.getCode()) || (performer && mTask.getStatus() == TaskStatus.READY.getCode())) {
                mTask.setCompleteTime(TimeHelper.currentTimeMillisWithoutTimeZone());
                mTask.setUsnFieldCompletetime(mTask.getUsnFieldCompletetime() + 1);
            }
        }

        if (cv.size() == 2) {
            return;// Nothing was changed
        }

        Utils.clearStringBuilder(mStringBuilder);
        mCr.update(LTaskContract.CONTENT_URI, cv, SelectionKeeper.equals(mStringBuilder, LTaskContract._ID, mTask.getIdTask()), null);

        TaskNotifyHelper.getInstance(mContext).updateTaskNotify(mTask);
    }

    private VerticalDepthTask getVertical() {
        final VerticalDepthTask vertical = new VerticalDepthTask();
        vertical.setId(mTask.getIdTask());

        if (mTaskParent == null) {
            vertical.setVertical(mSettings.getMaximumVertical() + 1);
            vertical.setDepth(1);
            mSettings.setMaximumVertical(vertical.getVertical());

        } else {
            Cursor c = null;
            try {
                c = mContext.getContentResolver().query(VerticalDepthTaskContract.CONTENT_URI, null,
                        SelectionKeeper.equals(null, VerticalDepthTaskContract._ID, mTaskParent.getIdTask()), null, null);
                if (c.moveToFirst()) {
                    vertical.setVertical(c.getInt(c.getColumnIndex(VerticalDepthTaskContract.Vertical)));
                    vertical.setDepth(c.getInt(c.getColumnIndex(VerticalDepthTaskContract.Depth)) + 1);

                } else {
                    vertical.setVertical(mSettings.getMaximumVertical() + 1);
                    vertical.setDepth(1);
                    mSettings.setMaximumVertical(vertical.getVertical());
                }

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

        mContext.getContentResolver().insert(VerticalDepthTaskContract.CONTENT_URI, vertical.getContentValues(null));
        return vertical;
    }

    private final class SaveMessagesThread extends Thread {
        @Override
        public void run() {
            super.run();

            if (mTaskMessages != null) {
                for (TaskMessage message : mTaskMessages) {
                    if (message.getTaskUID() == null) {
                        message.setTaskUID(UUID.fromString(mTask.getUid()));
                    }
                }
            }

            if (mTaskMessages != null && mTaskMessages.size() != mTaskMessagesSaveFrom) {
                if (mTaskMessages.size() > 0) {
                    TaskMessageCache.getInstance(mContext).updateCache(mTaskMessages);
                }

            } else {
                return;
            }

            final Dao<TaskMessage, UUID> dao = mDbHelper.getTaskMessageDao();
            try {
                dao.callBatchTasks(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        for (int i = mTaskMessagesSaveFrom; i < mTaskMessages.size(); i++) {
                            try {
                                dao.create(mTaskMessages.get(i));

                            } catch (SQLException e) {
                                Utils.toLog(e);
                            }
                        }
                        return null;
                    }
                });

            } catch (Exception e) {
                Utils.toLog(e);
            }
        }
    }

    private final class SaveFilesThread extends Thread {
        @Override
        public void run() {
            super.run();

            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

            if (mTaskFiles.size() == 0) {
                TaskFileCache.getInstance(mContext).remove(TaskHelper.getHashFromUid(mTaskUuid));

            } else {
                ContentProviderOperation operation;
                ContentValues cv;
                String selection;
                int count = 1;

                for (TaskFile file : mTaskFiles) {
                    if (file.isWeakLink()) {
                        file.setOrder(count);
                        file.setWeakLink(false);
                        file.setTaskId(mTaskUuid);

                        operation = ContentProviderOperation.newInsert(TaskFileContract.CONTENT_URI).withValues(file.getContentValues(null)).build();

                    } else {
                        if (file.getOrder() != count) {
                            Utils.clearStringBuilder(mStringBuilder);
                            selection = SelectionKeeper.equals(mStringBuilder, TaskFileContract.FIELD_FILEUID, String.valueOf(file.getFileId()));

                            file.setOrder(count);
                            file.setUsnFieldOrder(file.getUsnFieldOrder() + 1);
                            file.setUsnEntity(0);

                            cv = new ContentValues(3);
                            cv.put(TaskFileContract.ORDERS, file.getOrder());
                            cv.put(TaskFileContract.FIELD_USN_ENTITY, file.getUsnEntity());
                            cv.put(TaskFileContract.FIELD_USN_FIELD_ORDER, file.getUsnFieldOrder());

                            operation = ContentProviderOperation.newUpdate(TaskFileContract.CONTENT_URI).//
                                    withValues(cv).withSelection(selection, null).build();

                        } else {
                            operation = null;
                        }
                    }

                    if (operation != null) {
                        operations.add(operation);
                    }
                    count++;
                }
                TaskFileCache.getInstance(mContext).updateCache(mTaskFiles);
            }
            if (mTaskFilesDeleted.size() > 0) {
                TaskDeleteHelper.deleteTaskFiles(mTaskFilesDeleted, operations, mStringBuilder);
            }

            if (operations.size() > 0) {
                try {
                    mContext.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);

                } catch (Exception e) {
                    Utils.toLog(e);
                }
            }
        }
    }

    private double getOrderNewFromManyParents(LTask task, String currentUser)
    {
        // SELECT MAX(ordernew) FROM LionTask WHERE emailcustomer='anton.sobolev@leadertask.com' AND uidparent IS NULL
        double newOrder = 0;
        String parent = task.getUIDParent()!=null ? SharedStrings.EQUALS+SharedStrings.QUOTE+task.getUIDParent()+SharedStrings.QUOTE : SharedStrings.IS_NULL;
        String select = LTaskContract.EmailCustomer+SharedStrings.EQUALS+SharedStrings.QUOTE+currentUser+SharedStrings.QUOTE+SharedStrings.AND+LTaskContract.UIDParent+parent;
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, select, null, getOrderForLast());
            if (c.moveToFirst()) {
                newOrder = c.getDouble(c.getColumnIndex(LTaskContract.OrderNew));
            }
        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return newOrder;
    }

    private static String getOrderForLast() {
        final StringBuilder sb = new StringBuilder();

        SelectionKeeper.order(sb, LTaskContract.OrderNew, true);
        sb.append(SharedStrings.LIMIT);
        sb.append(SharedStrings.ONE);

        return sb.toString();
    }

    private double getOrderNewFromManyParentsBot(LTask task, String currentUser)
    {
        // SELECT MAX(ordernew) FROM LionTask WHERE emailcustomer='anton.sobolev@leadertask.com' AND uidparent IS NULL
        double newOrder = 0;
        String parent = task.getUIDParent()!=null ? SharedStrings.EQUALS+SharedStrings.QUOTE+task.getUIDParent()+SharedStrings.QUOTE : SharedStrings.IS_NULL;
        String select = LTaskContract.EmailCustomer+SharedStrings.EQUALS+SharedStrings.QUOTE+currentUser+SharedStrings.QUOTE+SharedStrings.AND+LTaskContract.UIDParent+parent;
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, select, null, getOrderForLastBot());
            if (c.moveToFirst()) {
                newOrder = c.getDouble(c.getColumnIndex(LTaskContract.OrderNew));
            }
        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return newOrder;
    }


    private static String getOrderForLastBot() {
        final StringBuilder sb = new StringBuilder();

        SelectionKeeper.order(sb, LTaskContract.OrderNew, false);
        sb.append(SharedStrings.LIMIT);
        sb.append(SharedStrings.ONE);

        return sb.toString();
    }

}