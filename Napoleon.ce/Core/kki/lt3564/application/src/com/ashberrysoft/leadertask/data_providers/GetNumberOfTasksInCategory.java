package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskCategory;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Перечсет поля по количествуву задач для указанной категории.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetNumberOfTasksInCategory extends BaseDatabaseRequest<Serializable> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderTaskStatus;
    private static ArgumentHolder[] sHolderCategoryUUID;
    private Category mCategory;
    private String mUserName;

    /**
     * 
     * @param context
     */
    public GetNumberOfTasksInCategory(Context context, Category category, String userName) {
        super(context.getApplicationContext());//TODO getApplicationContext
        mCategory = category;
        mUserName = userName;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_IN_CATEGORY_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // create ArgumentHolder instance for any <?> in "task status" SQL subquery
            if (sHolderTaskStatus == null)
                sHolderTaskStatus = new ArgumentHolder[] { new SelectArg(TaskContract.FIELD_STATUS,
                        TaskStatus.NOTE.getCode()) };

            // create ArgumentHolder instance for any <?> in "category UUID" SQL subquery
            if (sHolderCategoryUUID == null)
                sHolderCategoryUUID = new ArgumentHolder[] { new SelectArg(TaskCategory.FIELD_CATEGORY_UID,
                        mCategory.getId()) };
            else
                sHolderCategoryUUID[0].setValue(mCategory.getId());

            final QueryBuilder<TaskCategory, Integer> builderTaskCategories = mDbHelper.getTaskCategoryDao()
                    .queryBuilder();
            builderTaskCategories.selectColumns(TaskCategory.FIELD_TASK_UID);
            builderTaskCategories.where().raw(TaskCategory.FIELD_CATEGORY_UID + " = ?", sHolderCategoryUUID);

            QueryBuilder<Task, UUID> builderTasks = mDbHelper.getTaskDao().queryBuilder();
            builderTasks.selectColumns(TaskContract.FIELD_UID, TaskContract.FIELD_UID_PARENT,
                    TaskContract.FIELD_STATUS, TaskContract.FIELD_EMAIL_CUSTOMER);

            // create Where object for retrieving all tasks count and completed tasks count
            Where<Task, UUID> w1 = builderTasks.where().in(TaskContract.FIELD_UID, builderTaskCategories);
            w1.and();
            w1.raw(TaskContract.FIELD_STATUS + " <> ?", sHolderTaskStatus);
            int allTasksCount = (int) w1.countOf();

            // TODO Bug #3473 replaced "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
            mDbHelper.filterTasksFinishedFullSubtask(w1, null, mUserName);
            w1.and(2);
            final int notCompletedTasksCount = (int) w1.countOf();

            // create Where object for retrieving all not read tasks count and completed not read tasks count
            Where<Task, UUID> w2 = builderTasks.where().in(TaskContract.FIELD_UID, builderTaskCategories);
            w2.and();
            w2.raw(TaskContract.FIELD_STATUS + " <> ?", sHolderTaskStatus);
            w2.and();
            w2.ne(TaskContract.FIELD_READED, true);
            int allNotReadTasksCount = (int) w2.countOf();

            // TODO Bug #3473 replaced "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
            mDbHelper.filterTasksFinishedFullSubtask(w2, null, mUserName);
            w2.and(2);
            int notCompletedAndNotReadTasksCount = (int) w2.countOf();

            FilterNumberTask fnt = new FilterNumberTask(TaskMode.CATEGORIES, mCategory.getId().toString(),
                    allTasksCount, notCompletedTasksCount, allNotReadTasksCount, notCompletedAndNotReadTasksCount);
            updateFilterNumberTaskRecord(fnt);
            return null;
        } catch (SQLException e) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }
}