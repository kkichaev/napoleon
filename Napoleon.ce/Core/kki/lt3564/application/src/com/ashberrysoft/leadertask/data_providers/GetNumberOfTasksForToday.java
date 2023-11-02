package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.GetTasksByDate.AuxiliaryTaskFilter;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * обновление поля по кол-ву задач для слайдинг меню для раздела Сегодня.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetNumberOfTasksForToday extends BaseDatabaseRequest<Serializable> {
    private static final long serialVersionUID = 1L;
    private String mUserName;

    /**
     * 
     * @param context
     */
    public GetNumberOfTasksForToday(Context context, String userName) {
        super(context.getApplicationContext());//TODO getApplicationContext
        mUserName = userName;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_BY_EMAIL_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            final Calendar calendar = Utils.getCalendarDateGMT(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            final Date beginDate = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            final Date endDate = calendar.getTime();

            final QueryBuilder<Task, UUID> builder = mDbHelper.getTaskDao().queryBuilder();
            builder.selectColumns(TaskContract.FIELD_UID, TaskContract.FIELD_STATUS, TaskContract.FIELD_EMAIL_CUSTOMER);

            Where<Task, UUID> where = GetTasksByDate.filterTasksToday(mDbHelper, builder.where(), mUserName, beginDate,
                    endDate, beginDate, builder, false);
            where.and();
            where.ne(TaskContract.FIELD_STATUS, TaskStatus.NOTE.getCode());
            final int totalCount = (int) where.countOf();

            // mDatabase.filterTasksFinishedFull(where, builder, mUserName);
            // where.and(2);
            // final int unfinishedCount = (int) where.countOf();

            // <COMPLETED> TODO Bug #3460
            final QueryBuilder<Task, UUID> builderCompl = mDbHelper.getTaskDao().queryBuilder();
            builderCompl.selectColumns(mDbHelper.getVisibleTaskFields());
            builderCompl.orderByRaw("CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " <> '" + mUserName
                    + "' THEN 0 ELSE 1 END, " + TaskContract.FIELD_EMAIL_CUSTOMER + ", "
                    + TaskContract.FIELD_ORDER_CUSTOMER + ", " + TaskContract.FIELD_NAME);

            where = GetTasksByDate.filterTasksToday(mDbHelper, builderCompl.where(), mUserName, beginDate, endDate,
                    beginDate, builderCompl, false);

            mDbHelper.filterTasksWithMatchingParentByDate(where, mUserName, beginDate, endDate, beginDate);
            where.and(2);

            // TODO Bug #3471 "filterTasksFinishedFull" was changet to "filterTasksFinishedFullSubtask"
            mDbHelper.filterTasksFinishedFullSubtask(where, builderCompl, mUserName);
            where.and(2);

            int unfinishedCount = 0; // (int)where.countOf();
            try {
                final List<Task> list = AuxiliaryTaskFilter.auxiliaryFilter(where.query(), mDbHelper, mUserName);
                if (list != null) {
                    unfinishedCount = list.size();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // </COMPLETED>

            where = GetTasksByDate.filterTasksToday(mDbHelper, builder.where(), mUserName, beginDate, endDate,
                    beginDate, builder, false);
            where.and().ne(TaskContract.FIELD_READED, true);
            final int unreadCount = (int) where.countOf();

            mDbHelper.filterTasksFinishedFull(where, builder, mUserName);
            where.and(2);
            final int unreadUnFinishedTasks = (int) where.countOf();

            updateFilterNumberTaskRecord(new FilterNumberTask(0, FilterNumberTask.RECORD_TODAY, totalCount,
                    unfinishedCount, unreadCount, unreadUnFinishedTasks));

            return null;
        } catch (SQLException e) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }
}
