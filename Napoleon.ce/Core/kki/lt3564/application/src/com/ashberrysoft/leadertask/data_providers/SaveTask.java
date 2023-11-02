package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.SimpleNotifications;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Сохранение задачи после редактирования.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class SaveTask extends BaseDatabaseRequest<Serializable> {
    private static final long serialVersionUID = 1L;
    private Task mTask;

    /**
     * 
     * @param context
     * @param task
     *            task data
     */
    public SaveTask(Context context, Task task) {
        super(context);
        mTask = task;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_SAVE_TASK_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // update subtasks
            updateProjectInTaskRecursive(mTask.getId(), mTask.getProjectUid(), mDbHelper, false);
            // save changes in task
            mDbHelper.updateTask(mTask, true, true, false);
            SimpleNotifications.getInstance(mContext).updateOldSimpleNotify(mTask);

            return null;
        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
        }
    }

    private void updateProjectInTaskRecursive(UUID taskUID, UUID uidProject, DbHelper database, boolean isUpdateTask)
            throws SQLException {
        // update task project
        if (isUpdateTask) {
            Task oldTask = database.getTaskDao_queryForId(taskUID);
            UUID oldProjectUID = oldTask.getProjectUid();
            if (((uidProject != null) && (oldProjectUID != null) && (!uidProject.equals(oldProjectUID)))
                    || ((uidProject != null) && (oldProjectUID == null))
                    || ((uidProject == null) && (oldProjectUID != null))) {
                UpdateBuilder<Task, UUID> update = database.getTaskDao().updateBuilder();
                update.updateColumnValue(TaskContract.FIELD_UID_PROJECT, uidProject);
                update.updateColumnValue(TaskContract.FIELD_USN_ENTITY, 0);
                update.updateColumnExpression(TaskContract.FIELD_USN_UID_PROJECT, TaskContract.FIELD_USN_UID_PROJECT
                        + "+1");
                update.where().eq(TaskContract.FIELD_UID, taskUID);
                database.getTaskDao().update(update.prepare());
            }
        }
        isUpdateTask = true;
        // go through child
        QueryBuilder<Task, UUID> builder = database.getTaskDao().queryBuilder();
        builder.selectColumns(TaskContract.FIELD_UID);
        builder.where().eq(TaskContract.FIELD_UID_PARENT, taskUID);
        final List<Task> subTasks = builder.query();
        for (Task task : subTasks) {
            updateProjectInTaskRecursive(task.getId(), uidProject, database, isUpdateTask);
        }
    }
}
