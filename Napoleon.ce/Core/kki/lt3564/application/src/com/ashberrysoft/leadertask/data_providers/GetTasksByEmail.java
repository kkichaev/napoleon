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
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.Email.OrderInstruct;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * получение списка задач по указанному е-мейл
 * 
 * @param settings
 * @param email
 * @param isForNotReadAndNotCompleted
 * @return
 * @throws SQLException
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class GetTasksByEmail extends BaseDatabaseRequest<ArrayList<Task>> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderTaskPerformer_Customer;
    private static PreparedQuery<Task> sPqHideComplTasksForEmailFilter;
    private static PreparedQuery<Task> sPqShowComplTasksForEmailFilter;

    private String mUserName;
    private boolean showCompletedTasks;
    private Email mEmail;

    /**
     * 
     * @param context
     * @param messages
     *            task messages
     */
    public GetTasksByEmail(Context context, Email email, String userName, boolean showCompletedTasks) {
        super(context);
        mEmail = email;
        mUserName = userName;
        this.showCompletedTasks = showCompletedTasks;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_BY_EMAIL_FINISHED;
    }

    @Override
    protected ArrayList<Task> sendRequest(Void p) throws AbstractDataRequestException {
        try {
            if (sHolderTaskPerformer_Customer == null)
                // create ArgumentHolder instance for any <?> in "task performer/customer" sql subquery
                if (mEmail.getOrderInstruct() == OrderInstruct.INSTRUCTI)
                    sHolderTaskPerformer_Customer = new ArgumentHolder[] {
                            new SelectArg(TaskContract.FIELD_EMAIL_PERFORMER, mEmail.getName()),
                            new SelectArg(TaskContract.FIELD_EMAIL_CUSTOMER, mUserName) };
                else
                    sHolderTaskPerformer_Customer = new ArgumentHolder[] {
                            new SelectArg(TaskContract.FIELD_EMAIL_PERFORMER, mUserName),
                            new SelectArg(TaskContract.FIELD_EMAIL_CUSTOMER, mEmail.getName()) };
            else {
                // update values
                if (mEmail.getOrderInstruct() == OrderInstruct.INSTRUCTI) {
                    sHolderTaskPerformer_Customer[0].setValue(mEmail.getName());
                    sHolderTaskPerformer_Customer[1].setValue(mUserName);
                } else {
                    sHolderTaskPerformer_Customer[0].setValue(mUserName);
                    sHolderTaskPerformer_Customer[1].setValue(mEmail.getName());
                }
            }

            // preparing query for getting tasks by email
            if (sPqHideComplTasksForEmailFilter == null || sPqShowComplTasksForEmailFilter == null) {
                prepareGetTasksByEmailQuery(mDbHelper, mUserName);
            }

            List<Task> lt = new ArrayList<Task>();
            if (!showCompletedTasks) {
                lt = mDbHelper.getTaskDao().query(sPqHideComplTasksForEmailFilter);
                // Log.e("Query", mPqHideComplTasksForEmailFilter.getStatement());
            } else {
                lt = mDbHelper.getTaskDao().query(sPqShowComplTasksForEmailFilter);
                // Log.e("Query", mPqShowComplTasksForEmailFilter.getStatement());
            }

            lt = mDbHelper.hideSubTasks(lt);

            return new ArrayList<Task>(lt);
        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
        }
    }

    /**
     * preparing query for getting tasks by email
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private void prepareGetTasksByEmailQuery(DbHelper database, String userName) throws SQLException {
        QueryBuilder<Task, UUID> builder = database.getTaskDao().queryBuilder()
        // .selectColumns(database.getVisibleTaskFields())
                .orderByRaw("CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " <> ? THEN 0 ELSE 1 END, "//
                        + TaskContract.EMP_ORDERS + ", "//
                        + TaskContract.FIELD_EMAIL_CUSTOMER + ", "//
                        + TaskContract.FIELD_ORDER_CUSTOMER + ", "//
                        + TaskContract.FIELD_NAME//
                , database.prepareOrderByCustomerHolder(userName));
        
        Where<Task, UUID> filter = builder.where().raw(
                TaskContract.FIELD_EMAIL_PERFORMER + " = ? AND " + TaskContract.FIELD_EMAIL_CUSTOMER + " = ?",
                sHolderTaskPerformer_Customer);
        filter.and();
        database.filterTasksWithMatchingParentByEmail(filter, userName, mEmail);
        // "show completed tasks" prepared query
        sPqShowComplTasksForEmailFilter = filter.prepare();

        // TODO Bug #3472 replaced "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
        database.filterTasksFinishedFullSubtask(filter, builder, userName);
        filter.and(2);
        // "hide completed tasks" prepared query
        sPqHideComplTasksForEmailFilter = filter.prepare();
    }
}
