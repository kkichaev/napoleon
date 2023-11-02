package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.Email.OrderInstruct;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Перечсет поля по количествуву задач для у указанного email.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetNumberOfTasksByEmail extends BaseDatabaseRequest<Integer> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderTaskPerformer_Customer;

    private Email mEmail;
    private String mUserName;

    /**
     * 
     * @param context
     */
    public GetNumberOfTasksByEmail(Context context, Email email, String userName) {
        super(context.getApplicationContext());// TODO getApplicationContext
        mEmail = email;
        mUserName = userName;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_BY_EMAIL_FINISHED;
    }

    @Override
    protected Integer sendRequest(Void p) throws AbstractDataRequestException {
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

            QueryBuilder<Task, UUID> builder = mDbHelper
                    .getTaskDao()
                    .queryBuilder()
                    .selectColumns(mDbHelper.getVisibleTaskFields())
                    .orderByRaw(
                            "CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " = ? THEN 0 ELSE 1 END, "
                                    + TaskContract.FIELD_EMAIL_CUSTOMER + ", " + TaskContract.FIELD_ORDER_CUSTOMER
                                    + ", " + TaskContract.FIELD_NAME, mDbHelper.prepareOrderByCustomerHolder(mUserName));

            // create Where object for retrieving all tasks count and completed tasks count
            Where<Task, UUID> filter1 = builder.where().raw(
                    TaskContract.FIELD_EMAIL_PERFORMER + " = ? AND " + TaskContract.FIELD_EMAIL_CUSTOMER + " = ?",
                    sHolderTaskPerformer_Customer);
            filter1.ne(TaskContract.FIELD_STATUS, TaskStatus.NOTE.getCode());
            filter1.and(2);
            int allTasksCount = (int) filter1.countOf();

            // TODO Bug #3472 replaced "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
            mDbHelper.filterTasksFinishedFullSubtask(filter1, null, mUserName);
            filter1.and(2);
            int notCompletedTasksCount = (int) filter1.countOf();

            // create Where object for retrieving all not read tasks count and completed not read tasks count
            Where<Task, UUID> filter2 = builder.where().raw(
                    TaskContract.FIELD_EMAIL_PERFORMER + " = ? AND " + TaskContract.FIELD_EMAIL_CUSTOMER + " = ?",
                    sHolderTaskPerformer_Customer);
            filter2.ne(TaskContract.FIELD_STATUS, TaskStatus.NOTE.getCode());
            filter2.and(2);
            filter2.and();
            filter2.ne(TaskContract.FIELD_READED, true);
            int allNotReadTasksCount = (int) filter2.countOf();

            // TODO Bug #3472 replaced "filterTasksFinishedFull" to "filterTasksFinishedFullSubtask"
            mDbHelper.filterTasksFinishedFullSubtask(filter2, null, mUserName);
            filter2.and(2);
            int notCompletedAndNotReadTasksCount = (int) filter2.countOf();

            FilterNumberTask fnt = new FilterNumberTask(
                    (mEmail.getOrderInstruct() == OrderInstruct.INSTRUCTI ? TaskMode.ASSIGNED_BY_ME
                            : TaskMode.ASSIGNED_TO_ME), mEmail.getName(), allTasksCount, notCompletedTasksCount,
                    allNotReadTasksCount, notCompletedAndNotReadTasksCount);
            updateFilterNumberTaskRecord(fnt);
            return notCompletedTasksCount;
        } catch (SQLException e) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }
}
