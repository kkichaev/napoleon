package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.GetTasksByDate.AuxiliaryTaskFilter;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Перечсет поля по количествуву задач для у указанного проекта.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetNumberOfTasksInProject extends BaseDatabaseRequest<Serializable> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderTaskStatus;
    private static ArgumentHolder[] sHolderProjectUUID;
    private Project mProject;
    private String mUserName;

    /**
     * 
     * @param context
     */
    public GetNumberOfTasksInProject(Context context, Project project, String userName) {
        super(context.getApplicationContext());// TODO getApplicationContext
        mProject = project;
        mUserName = userName;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_NUMBER_IN_PROJECT_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // create ArgumentHolder instance for any <?> in "task status" SQL subquery
            if (sHolderTaskStatus == null)
                sHolderTaskStatus = new ArgumentHolder[] { new SelectArg(TaskContract.FIELD_STATUS,
                        TaskStatus.NOTE.getCode()) };

            // create ArgumentHolder instance for any <?> in "project UUID" SQL subquery
            if (sHolderProjectUUID == null)
                sHolderProjectUUID = new ArgumentHolder[] { new SelectArg(TaskContract.FIELD_UID_PROJECT,
                        mProject.getId()) };
            else
                sHolderProjectUUID[0].setValue(mProject.getId());

            QueryBuilder<Task, UUID> qb = mDbHelper
                    .getTaskDao()
                    .queryBuilder()
                    .selectColumns(TaskContract.FIELD_UID_PARENT, TaskContract.FIELD_STATUS,
                            TaskContract.FIELD_EMAIL_CUSTOMER);

            // create Where object for retrieving all tasks count and completed tasks count
            final Where<Task, UUID> w1 = qb.where().raw(TaskContract.FIELD_UID_PROJECT + " = ?", sHolderProjectUUID);
            w1.and();
            w1.raw(TaskContract.FIELD_STATUS + " <> ?", sHolderTaskStatus);
            int allTasksCount = (int) w1.countOf();

            // <COMPLETED>
            // TODO Bug #3472 from "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
            // if (mProject.getOrder())

            mDbHelper.filterTasksFinishedFullSubtask(w1, null, mUserName);
            w1.and(2);
            int notCompletedTasksCount = (int) w1.countOf();

            if (mProject.getCreator() != null && !mUserName.equals(mProject.getCreator())) {
                final PreparedQuery<Task> query = GetTasksByProject.prepareTasksByProjectQuery(mDbHelper, mUserName,
                        false, mProject.getId());
                final List<Task> tasksQuery = mDbHelper.getTaskDao().query(query);
                final List<Task> tasksHided = mDbHelper.hideSubTasks(tasksQuery);

                AuxiliaryTaskFilter.auxiliaryFilter(tasksHided, mDbHelper, mUserName);
                notCompletedTasksCount -= AuxiliaryTaskFilter.sDeletedItemsCount;
            }

            // final PreparedQuery<Task> query = GetTasksByProject.prepareTasksByProjectQuery(mUserName, mDatabase,
            // false,
            // mProject.getIdTask());
            // final List<Task> queryTasks = mDatabase.getTaskDao().query(query);
            // final List<Task> hidedTasks = mDatabase.hideSubTasks(queryTasks);
            //
            // int notCompletedTasksCount = GetTasksByDate.auxiliaryFilter(hidedTasks, mDatabase, mUserName).size();
            // </COMPLETED>

            // create Where object for retrieving all not read tasks count and completed not read tasks count
            Where<Task, UUID> w2 = qb.where().raw(TaskContract.FIELD_UID_PROJECT + " = ?", sHolderProjectUUID);
            w2.and();
            w2.raw(TaskContract.FIELD_STATUS + " <> ?", sHolderTaskStatus);
            w2.and();
            w2.ne(TaskContract.FIELD_READED, true);
            int allNotReadTasksCount = (int) w2.countOf();

            // TODO Bug #3472 from "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
            mDbHelper.filterTasksFinishedFullSubtask(w2, null, mUserName);
            w2.and(2);
            int notCompletedAndNotReadTasksCount = (int) w2.countOf();

            FilterNumberTask fnt = new FilterNumberTask(3, mProject.getId().toString(), allTasksCount,
                    notCompletedTasksCount, allNotReadTasksCount, notCompletedAndNotReadTasksCount);
            updateFilterNumberTaskRecord(fnt);
            return null;
        } catch (SQLException e) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }
}
