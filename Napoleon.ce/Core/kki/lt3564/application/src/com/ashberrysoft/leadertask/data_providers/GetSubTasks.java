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
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.service.LeaderTaskService;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;

/**
 * Получение подзадач указанной задачи.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetSubTasks extends AbstractServiceRequest<ArrayList<Task>, Void, List<Task>> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderGetSubtasks;
    private static PreparedQuery<Task> sPqHideComplSubtasks;
    private static PreparedQuery<Task> sPqShowComplSubtasks;

    private Task mParentTask;
    private String mUserName;
    private boolean showCompletedTasks;

    /**
     * 
     * @param context
     * @param task
     *            task data
     * @param messages
     *            task messages
     */
    public GetSubTasks(Context context, Task task, String userName, boolean showCompletedTasks) {
        super(context);

        mParentTask = task;
        mUserName = userName;
        this.showCompletedTasks = showCompletedTasks;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_SUBTASKS_FINISHED;
    }

    @Override
    protected String getServiceAction() {
        return ServiceConstants.RECIVE;
    }

    @Override
    protected Class<?> getServiceClass() {
        return LeaderTaskService.class;
    }

    @Override
    protected ArrayList<Task> parseResult(List<Task> data) throws AbstractDataRequestException {
        return new ArrayList<Task>(data);
    }

    @Override
    protected List<Task> sendRequest(Void p) throws AbstractDataRequestException {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);

        if (sHolderGetSubtasks == null) {
            // create ArgumentHolder instance for any <?> in "get subtasks" sql subquery
            sHolderGetSubtasks = new ArgumentHolder[] { new SelectArg(TaskContract.FIELD_UID_PARENT,
                    mParentTask.getId()) };
        } else {
            // update values
            sHolderGetSubtasks[0].setValue(mParentTask.getId());
        }

        try {
            // preparing query for getting subtasks
            if (sPqHideComplSubtasks == null || sPqShowComplSubtasks == null) {
                prepareGetSubtaskQuery(mUserName, dbHelper);
            }

            if (showCompletedTasks) {
                return dbHelper.getTaskDao().query(sPqHideComplSubtasks);
            } else {
                return dbHelper.getTaskDao().query(sPqShowComplSubtasks);
            }

        } catch (SQLException e) {
            Utils.toLog(e);
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }

    @Override
    protected Void prepareParameters() throws AbstractDataRequestException {
        return null;
    }

    /**
     * preparing query for getting subtasks
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private void prepareGetSubtaskQuery(String userName, DbHelper database) throws SQLException {
        final QueryBuilder<Task, UUID> builder = database.getTaskDao().queryBuilder();
        Where<Task, UUID> w = builder.orderByRaw(//
                "CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER//
                        + " <> ? THEN 0 ELSE 1 END, "//
                        + TaskContract.FIELD_ORDER_CUSTOMER + ", "//
                        + TaskContract.EMP_ORDERS + ", "//
                        + TaskContract.FIELD_NAME//
                , database.prepareOrderByCustomerHolder(userName)).where()
                .raw(TaskContract.FIELD_UID_PARENT + " = ?", sHolderGetSubtasks);

        // "show completed subtasks" prepared query
        sPqShowComplSubtasks = w.prepare();
        database.filterTasksFinishedFullSubtask(w, builder, userName);
        w.and(2);
        // "hide completed subtasks" prepared query
        sPqHideComplSubtasks = w.prepare();
    }
}
