package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * get particular tasks to display in the notification screen
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class GetTasksNotifications extends BaseDatabaseRequest<ArrayList<Task>> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] mHolderGetTasksByTerm;
    private static PreparedQuery<Task> sPqHideComplTasksForNotifs;
    private static PreparedQuery<Task> sPqShowComplTasksForNotifs;
    private String mUserName;
    private boolean showCompletedTasks;

    /**
     * 
     * @param context
     * @param messages
     *            task messages
     */
    public GetTasksNotifications(Context context, String userName, boolean showCompletedTasks) {
        super(context);
        mUserName = userName;
        this.showCompletedTasks = showCompletedTasks;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_NOTIFICATION_TASKS_FINISHED;
    }

    @Override
    protected ArrayList<Task> sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // define begin and current time for getting notifications
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            Date currentTime = new Date(Calendar.getInstance().getTimeInMillis()
                    + Calendar.getInstance().getTimeZone().getRawOffset()
            /* + Calendar.getInstance().getTimeZone().getDSTSavings() */);
            Date beginTime = new Date(startTime.getTimeInMillis() + Calendar.getInstance().getTimeZone().getRawOffset()
            /* + Calendar.getInstance().getTimeZone().getDSTSavings() */);

            if (mHolderGetTasksByTerm == null)
                // create ArgumentHolder instance for any <?> in "get tasks by term" sql subquery
                mHolderGetTasksByTerm = new ArgumentHolder[] {
                        new SelectArg(TaskContract.FIELD_EMAIL_CUSTOMER, mUserName),
                        new SelectArg(TaskContract.FIELD_EMAIL_PERFORMER, mUserName),
                        new SelectArg(SqlType.LONG, beginTime.getTime()),
                        new SelectArg(SqlType.LONG, currentTime.getTime()) };
            else {
                mHolderGetTasksByTerm[0].setValue(mUserName);
                mHolderGetTasksByTerm[1].setValue(mUserName);
                mHolderGetTasksByTerm[2].setValue(beginTime.getTime());
                mHolderGetTasksByTerm[3].setValue(currentTime.getTime());
            }

            // preparing query for getting tasks for notification
            if (sPqHideComplTasksForNotifs == null || sPqShowComplTasksForNotifs == null)
                prepareGetTasksForNotifsQuery(mUserName);

            List<Task> tasks = new ArrayList<Task>();
            if (!showCompletedTasks)
                tasks = mDbHelper.getTaskDao().query(sPqHideComplTasksForNotifs);
            else
                tasks = mDbHelper.getTaskDao().query(sPqShowComplTasksForNotifs);

            tasks = mDbHelper.hideSubTasks(tasks);

            return new ArrayList<Task>(tasks);
        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
        }
    }

    /**
     * preparing query for getting tasks for notification
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private void prepareGetTasksForNotifsQuery(String userName) throws SQLException {
        final QueryBuilder<Task, UUID> builder = mDbHelper.getTaskDao().queryBuilder();
        Where<Task, UUID> w = builder
        // .selectColumns(mDbHelper.getVisibleTaskFields())
                .orderByRaw(
                        "CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " <> ? THEN 0 ELSE 1 END, "
                                + TaskContract.FIELD_EMAIL_CUSTOMER + ", " + TaskContract.FIELD_ORDER_CUSTOMER + ", "
                                + TaskContract.FIELD_NAME, mDbHelper.prepareOrderByCustomerHolder(userName)).where();

        w.raw("(" + TaskContract.FIELD_EMAIL_CUSTOMER + " = ? OR " + TaskContract.FIELD_EMAIL_PERFORMER + " = ?) AND "
                + TaskContract.FIELD_TERM_BEGIN + " BETWEEN ? AND ? ", mHolderGetTasksByTerm);

        // "show completed tasks" prepared query
        sPqShowComplTasksForNotifs = w.prepare();
        mDbHelper.filterTasksFinishedFull(w, builder, userName);
        w.and(2);
        // "hide completed tasks" prepared query
        sPqHideComplTasksForNotifs = w.prepare();
    }
}
