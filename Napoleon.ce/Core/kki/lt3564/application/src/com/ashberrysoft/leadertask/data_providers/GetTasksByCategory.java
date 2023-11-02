package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskCategory;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Получение задач из указанной категории.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetTasksByCategory extends BaseDatabaseRequest<ArrayList<Task>> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderGetCategory;
    private static PreparedQuery<Task> sPqHideComplTasksForCategoryFilter;
    private static PreparedQuery<Task> sPqShowComplTasksForCategoryFilter;

    private String mUserName;
    private boolean showCompletedTasks;
    private Category mCategory;

    /**
     * 
     * @param context
     * @param messages
     *            task messages
     */
    public GetTasksByCategory(Context context, Category category, String userName, boolean showCompletedTasks) {
        super(context);
        mCategory = category;
        mUserName = userName;
        this.showCompletedTasks = showCompletedTasks;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_CATEGORY_TASKS_FINISHED;
    }

    @Override
    protected ArrayList<Task> sendRequest(Void p) throws AbstractDataRequestException {
        try {
            if (sHolderGetCategory == null)
                // create ArgumentHolder instance for any <?> in "get category by UUID" sql subquery
                sHolderGetCategory = new ArgumentHolder[] { new SelectArg(SqlType.STRING, mCategory.getId()) };
            else
                // update values
                sHolderGetCategory[0].setValue(mCategory.getId());

            // preparing query for getting tasks by category
            if (sPqHideComplTasksForCategoryFilter == null || sPqShowComplTasksForCategoryFilter == null) {
                prepareTasksByCategoryQuery(mUserName, mDbHelper);
            }

            List<Task> lt = null;

            if (showCompletedTasks) {
                lt = mDbHelper.getTaskDao().query(sPqShowComplTasksForCategoryFilter);
            } else {
                lt = mDbHelper.getTaskDao().query(sPqHideComplTasksForCategoryFilter);
            }

            lt = mDbHelper.hideSubTasks(lt);

            return new ArrayList<Task>(lt);

        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
        }
    }

    /**
     * preparing query for getting tasks by category
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private void prepareTasksByCategoryQuery(String userName, DbHelper database) throws SQLException {
        QueryBuilder<TaskCategory, Integer> builderTaskCategory = database.getTaskCategoryDao().queryBuilder();
        builderTaskCategory.selectColumns(TaskCategory.FIELD_TASK_UID);
        builderTaskCategory.where().raw(TaskCategory.FIELD_CATEGORY_UID + " = ?", sHolderGetCategory);

        QueryBuilder<Task, UUID> builder = database.getTaskDao().queryBuilder()
        // .selectColumns(database.getVisibleTaskFields())
                .orderByRaw("CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " <> ? THEN 0 ELSE 1 END, "//
                        + TaskContract.FIELD_EMAIL_CUSTOMER + ", "//
                        + TaskContract.EMP_ORDERS + ", "//
                        + TaskContract.FIELD_ORDER_CUSTOMER + ", "//
                        + TaskContract.FIELD_NAME//
                , database.prepareOrderByCustomerHolder(userName));
        Where<Task, UUID> where = builder.where().in(TaskContract.FIELD_UID, builderTaskCategory);
        where.and();

        mDbHelper.filterTasksWithMatchingParentByCategory(where, sHolderGetCategory);
        // "show completed tasks" prepared query
        sPqShowComplTasksForCategoryFilter = where.prepare();

        // TODO Bug #3473 replaced "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
        database.filterTasksFinishedFullSubtask(where, builder, userName);

        // where.raw("NOT EXISTS (SELECT t2.UID FROM tasks t2 WHERE t2.UID = tasks.UIDParent AND EXISTS (SELECT task_category.CategoryUID FROM task_category WHERE task_category.TaskUID = t2.UID AND task_category.CategoryUID = ?))",
        // sHolderGetCategory);
        where.and(2);
        // "hide completed tasks" prepared query
        sPqHideComplTasksForCategoryFilter = where.prepare();
    }
}
