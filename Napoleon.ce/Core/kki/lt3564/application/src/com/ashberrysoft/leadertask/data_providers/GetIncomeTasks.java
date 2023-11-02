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
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Выбор задач "входящих"
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 *         «Входящие» - вывести задачи первого уровня + у которых нет связей с категориями (или этих категорий нет в
 *         базе),+ и нет связей с контактами (или этих контактов нет в базе), ????- нет такого поля, пока только
 *         проверка на null и не в проекте (или этого проекта нет в базе), + и исполнитель текущий пользователь, + и
 *         заказчик текущий пользователь, + и у задачи не установлен срок +
 * 
 */
public class GetIncomeTasks extends BaseDatabaseRequest<ArrayList<Task>> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderTaskPerformer_Customer;
    private static PreparedQuery<Task> sPqHideComplTasksForInboxFilter;
    private static PreparedQuery<Task> sPqShowComplTasksForInboxFilter;

    private String mUserName;
    private boolean mShowCompletedTasks;

    /**
     * 
     * @param context
     * @param messages
     *            task messages
     */
    public GetIncomeTasks(Context context, String userName, boolean showCompletedTasks) {
        super(context);
        mUserName = userName;
        mShowCompletedTasks = showCompletedTasks;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_BY_DATE_FINISHED;
    }

    @Override
    protected ArrayList<Task> sendRequest(Void p) throws AbstractDataRequestException {
        try {
            if (sHolderTaskPerformer_Customer == null) {
                // create ArgumentHolder instance for any <?> in "task performer/customer" sql subquery
                sHolderTaskPerformer_Customer = new ArgumentHolder[] {
                        new SelectArg(TaskContract.FIELD_EMAIL_PERFORMER, mUserName),
                        new SelectArg(TaskContract.FIELD_EMAIL_CUSTOMER, mUserName) };
            } else {
                // update values
                sHolderTaskPerformer_Customer[0].setValue(mUserName);
                sHolderTaskPerformer_Customer[1].setValue(mUserName);
            }

            // preparing query for getting inbox tasks
            if (sPqHideComplTasksForInboxFilter == null || sPqShowComplTasksForInboxFilter == null) {
                prepareTaskInputQuery(mDbHelper, mUserName);
            }

            final List<Task> lt1;
            if (mShowCompletedTasks) {
                lt1 = mDbHelper.getTaskDao().query(sPqShowComplTasksForInboxFilter);
            } else {
                lt1 = mDbHelper.getTaskDao().query(sPqHideComplTasksForInboxFilter);
            }

            return new ArrayList<Task>(lt1);
        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
        }
    }

    /**
     * preparing query for getting inbox tasks
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private void prepareTaskInputQuery(DbHelper database, String userName) throws SQLException {
        QueryBuilder<Task, UUID> qb = database.getTaskDao().queryBuilder()
        // .selectColumns(database.getVisibleTaskFields())//
                .orderBy(TaskContract.FIELD_ORDER_CUSTOMER, true).orderBy(TaskContract.FIELD_NAME, true);
        Where<Task, UUID> w1 = qb.where();
        w1.isNull(TaskContract.FIELD_UID_PARENT);
        // TODO: (VOLEY) take into account parent task that have no set term or customer term
        /*
         * w1.raw("NOT EXISTS (SELECT " + Task.FIELD_UID + " FROM tasks t WHERE t." + Task.FIELD_UID + " = tasks." +
         * Task.FIELD_UID_PARENT + " AND (" + Task.FIELD_TERM_BEGIN + " IS NOT NULL OR " + Task.FIELD_TERM_END +
         * " IS NOT NULL OR " + Task.FIELD_TERM_BEGIN_CUSTOMER + " IS NOT NULL OR " + Task.FIELD_TERM_END_CUSTOMER +
         * " IS NOT NULL))");
         */
        w1.raw(TaskContract.FIELD_EMAIL_PERFORMER + " = ? AND " + TaskContract.FIELD_EMAIL_CUSTOMER + " = ?",
                sHolderTaskPerformer_Customer);
        w1.isNull(TaskContract.FIELD_TERM_BEGIN);
        w1.isNull(TaskContract.FIELD_TERM_END);
        w1.isNull(TaskContract.FIELD_CONTACTS);
        w1.isNull(TaskContract.FIELD_UID_PROJECT);
        w1.notIn(TaskContract.FIELD_UID_PROJECT,
                database.getProjectDao().queryBuilder().selectColumns(Project.FIELD_UID));
        w1.or(2);
        w1.and(6);

        // select tasks that don't contain category references
        database.filterTasksWithoutCategory(w1);
        w1.and(2);

        // "show completed tasks" prepared query
        sPqShowComplTasksForInboxFilter = w1.prepare();
        database.filterTasksFinishedFull(w1, qb, userName);
        w1.and(2);
        // "hide completed tasks" prepared query
        sPqHideComplTasksForInboxFilter = w1.prepare();
        // Log.e("Query", mPqShowComplTasksForInboxFilter.getStatement());
    }
}
