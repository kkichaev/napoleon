package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.GetTasksByDate.AuxiliaryTaskFilter;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Получение задач из указанного проекта.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetTasksByProject extends BaseDatabaseRequest<ArrayList<Task>> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderGetProject;
    private static PreparedQuery<Task> sPqHideComplTasksForProjectFilter;
    private static PreparedQuery<Task> sPqShowComplTasksForProjectFilter;

    private String mUserName;
    private boolean showCompletedTasks;
    private Project mProject;

    /**
     * 
     * @param context
     * @param messages
     *            task messages
     */
    public GetTasksByProject(Context context, Project project, String userName, boolean showCompletedTasks) {
        super(context);
        mProject = project;
        mUserName = userName;
        this.showCompletedTasks = showCompletedTasks;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_PROJECT_TASKS_FINISHED;
    }

    @Override
    protected ArrayList<Task> sendRequest(Void p) throws AbstractDataRequestException {
        try {
            if (sHolderGetProject == null) {
                // create ArgumentHolder instance for any <?> in "get project by UUID" sql subquery
                sHolderGetProject = new ArgumentHolder[] { new SelectArg(TaskContract.FIELD_UID_PROJECT,
                        mProject.getId()) };
            } else {
                // update values
                sHolderGetProject[0].setValue(mProject.getId());
            }

            // preparing query for getting tasks by project
            if (sPqHideComplTasksForProjectFilter == null || sPqShowComplTasksForProjectFilter == null) {
                prepareTasksByProjectQuery(mUserName);
            }

            List<Task> tasks = mDbHelper.getTaskDao().query(
                    showCompletedTasks ? sPqShowComplTasksForProjectFilter : sPqHideComplTasksForProjectFilter);

            tasks = mDbHelper.hideSubTasks(tasks);

            if (showCompletedTasks) {
                return new ArrayList<Task>(tasks);
            }

            return AuxiliaryTaskFilter.auxiliaryFilter(tasks, mDbHelper, mUserName);

        } catch (SQLException e) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }

    /**
     * preparing query for getting tasks by project
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private void prepareTasksByProjectQuery(String userName) throws SQLException {
        final QueryBuilder<Task, UUID> qb = mDbHelper.getTaskDao().queryBuilder();
        // qb.selectColumns(mDbHelper.getVisibleTaskFields());
        qb.orderByRaw("CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " <> ? THEN 0 ELSE 1 END, "//
                + TaskContract.EMP_ORDERS + ", "//
                + TaskContract.FIELD_EMAIL_CUSTOMER + ", "//
                + TaskContract.FIELD_ORDER_CUSTOMER + ", "//
                + TaskContract.FIELD_NAME//
        , mDbHelper.prepareOrderByCustomerHolder(userName));

        final Where<Task, UUID> w1 = qb.where().raw(TaskContract.FIELD_UID_PROJECT + " = ?", sHolderGetProject);
        w1.and();

        mDbHelper.filterTasksWithMatchingParentByProject(w1, sHolderGetProject);
        // "show completed tasks" prepared query
        sPqShowComplTasksForProjectFilter = w1.prepare();

        // TODO Bug #3472 from "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
        mDbHelper.filterTasksFinishedFullSubtask(w1, qb, userName);
        w1.and(2);
        // "hide completed tasks" prepared query
        sPqHideComplTasksForProjectFilter = w1.prepare();
    }

    public static PreparedQuery<Task> prepareTasksByProjectQuery(DbHelper db, String userName, boolean toShow, UUID id)
            throws SQLException {
        final QueryBuilder<Task, UUID> qb = db.getTaskDao().queryBuilder();
        // qb.selectColumns(db.getVisibleTaskFields());
        qb.orderByRaw("CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " <> ? THEN 0 ELSE 1 END, "
                + TaskContract.FIELD_EMAIL_CUSTOMER + ", " + TaskContract.FIELD_ORDER_CUSTOMER + ", "
                + TaskContract.FIELD_NAME, db.prepareOrderByCustomerHolder(userName));

        if (sHolderGetProject == null) {
            sHolderGetProject = new ArgumentHolder[] { new SelectArg(TaskContract.FIELD_UID_PROJECT, id) };
        } else {
            sHolderGetProject[0].setValue(id);
        }

        final Where<Task, UUID> w1 = qb.where().raw(TaskContract.FIELD_UID_PROJECT + " = ?", sHolderGetProject);
        w1.and();

        db.filterTasksWithMatchingParentByProject(w1, sHolderGetProject);

        if (toShow) {
            return sPqShowComplTasksForProjectFilter = w1.prepare();
        }

        db.filterTasksFinishedFullSubtask(w1, qb, userName);
        w1.and(2);
        return sPqHideComplTasksForProjectFilter = w1.prepare();
    }
}