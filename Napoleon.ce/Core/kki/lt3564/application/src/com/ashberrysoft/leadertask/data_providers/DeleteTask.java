package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.DeletedTask;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SimpleNotifications;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Удаление задачи.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class DeleteTask extends BaseDatabaseRequest<Serializable> {

    private static final long serialVersionUID = 1L;
    private final UUID mTaskId;

    /**
     * 
     * @param context
     * @param task
     *            task data
     * @param messages
     *            task messages
     */
    public DeleteTask(Context context, Task task) {
        super(context);
        mTaskId = task.getId();
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_DELETE_TASK_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // get all task fields
            final Task task = DbHelper.getInstance(mContext).getTaskDao_queryForId(mTaskId);
            // left pointer of deleted task
            final int leftPointer = task.getLeftPointer();
            // right pointer of deleted task
            final int rightPointer = task.getRightPointer();
            // difference between right and left pointers
            final int width = task.getRightPointer() - task.getLeftPointer() + 1;
            // calculate deleted date
            final Date deletedDate = new Date(Calendar.getInstance().getTimeInMillis()
                    + Calendar.getInstance().getTimeZone().getRawOffset()
                    + Calendar.getInstance().getTimeZone().getDSTSavings());
            // create empty DeletedTask instance
            final DeletedTask deletedTask = new DeletedTask();
            // set date of task deleting
            deletedTask.setDeleteDate(deletedDate);

            // process tasks by batches
            mDbHelper.getTaskDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    // deleted task and subtasks of deleted task
                    List<Task> tasks = mDbHelper.getTaskDao().queryBuilder().selectColumns(TaskContract.FIELD_UID)
                            .where().between(TaskContract.LEFT_POINTER, leftPointer, rightPointer).query();
                    for (Task task : tasks) {
                        deletedTask.setId(task.getId());
                        // create entry at "deleted_tasks" table
                        mDbHelper.getDeletedTaskDao().create(deletedTask);
                    }

                    // delete task and its subtasks TODO КАКОГО ХЕРА БЕЗ КОНСТАНТ?
                    mDbHelper.getTaskDao().executeRaw(
                            "DELETE FROM tasks WHERE lft BETWEEN " + leftPointer + " AND " + rightPointer);
                    // update left pointer of tasks
                    mDbHelper.getTaskDao().updateRaw(
                            "UPDATE tasks SET lft = lft - " + width + " WHERE lft > " + rightPointer);
                    // update right pointer of tasks
                    mDbHelper.getTaskDao().updateRaw(
                            "UPDATE tasks SET rgt = rgt - " + width + " WHERE rgt > " + rightPointer);
                    // update task number
                    mDbHelper.updateNumberTaskAfterDelete_Add(task, true, false, false);

                    /*
                     * if we assign task for particular user which email doesn't exists in database, then we need to
                     * update sliding menu
                     */
                    boolean isPerformerExists = false;
                    List<String> allEmails = mDbHelper.getAllEmails();
                    for (String email : allEmails) {
                        if (email != null && email.equals(task.getPerformer())) {
                            isPerformerExists = true;
                            break;
                        }
                    }

                    // send broadcast intent in order to update sliding menu "I assigned" section
                    if (!isPerformerExists) {
                        final Intent intent = new Intent();
                        intent.setAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                    return null;
                }
            });

            // update notifications
            // new ProcessNotifications(mContext).deleteNotification(mTask); TODO
            SimpleNotifications.getInstance(mContext).updateOldSimpleNotify(task);
            return null;
        } catch (SQLException e) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
