package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Обновление поля по кол-ву задач для слайдинг меню по входящим.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class GetNumberOfIncomeTasks extends BaseDatabaseRequest<Serializable> {
    private static final long serialVersionUID = 1L;

    private String mUserName;

    /**
     * 
     * @param context
     */
    public GetNumberOfIncomeTasks(Context context, String userName) {
        super(context.getApplicationContext());// TODO getApplicationContext
        mUserName = userName;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_BY_EMAIL_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            QueryBuilder<Task, UUID> builder = mDbHelper.getTaskDao().queryBuilder();

            Where<Task, UUID> where = builder.where();
            filterTasksIncome(where, mUserName);
            int totalCount = (int) where.countOf();

            mDbHelper.filterTasksFinishedFull(where, builder, mUserName);
            where.and(2);
            int unfinishedCount = (int) where.countOf();

            where = builder.where();
            filterTasksIncome(where, mUserName);
            where.and().ne(TaskContract.FIELD_READED, true);
            int unreadCount = (int) where.countOf();

            mDbHelper.filterTasksFinishedFull(where, builder, mUserName);
            where.and(2);
            int unreadUnFinishedTasks = (int) where.countOf();

            updateFilterNumberTaskRecord(new FilterNumberTask(1, FilterNumberTask.RECORD_INCOME, totalCount,
                    unfinishedCount, unreadCount, unreadUnFinishedTasks));
            return null;
        } catch (SQLException e) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }

    /**
     * Построение фильтра который выбирает задачи для "входящих"
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param where
     * @throws SQLException
     */
    private void filterTasksIncome(Where<Task, UUID> where, String userName) throws SQLException {
        // 1
        where.ne(TaskContract.FIELD_STATUS, TaskStatus.NOTE.getCode());
        // 2
        where.isNull(TaskContract.FIELD_UID_PARENT);
        // TODO: (VOLEY) take into account parent task that have no set term or customer term
        /*
         * where.raw("NOT EXISTS (SELECT " + Task.FIELD_UID + " FROM tasks t WHERE t." + Task.FIELD_UID + " = tasks." +
         * Task.FIELD_UID_PARENT + " AND (" + Task.FIELD_TERM_BEGIN + " IS NOT NULL OR " + Task.FIELD_TERM_END +
         * " IS NOT NULL OR " + Task.FIELD_TERM_BEGIN_CUSTOMER + " IS NOT NULL OR " + Task.FIELD_TERM_END_CUSTOMER +
         * " IS NOT NULL))");
         */
        where.and();
        where.eq(TaskContract.FIELD_EMAIL_PERFORMER, userName);
        where.and();
        where.eq(TaskContract.FIELD_EMAIL_CUSTOMER, userName);
        where.and();
        where.isNull(TaskContract.FIELD_TERM_BEGIN);
        where.and();
        where.isNull(TaskContract.FIELD_TERM_END);
        where.and();
        where.isNull(TaskContract.FIELD_CONTACTS);
        // 3
        { 
            where.isNull(TaskContract.FIELD_UID_PROJECT);
            filterTasksWithoutProject(where);
            where.or(2);
        }
        // 4
        mDbHelper.filterTasksWithoutCategory(where);

        where.and(4);
    }

    /**
     * Добавление фильтра который оставляет только задачи у которых нет проекта или проект не существует в локальной БД.
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param where
     */
    private void filterTasksWithoutProject(Where<Task, UUID> where) {
        where.raw("NOT EXISTS (SELECT " + Project.FIELD_UID + " FROM projects WHERE projects." + Project.FIELD_UID
                + "=tasks." + TaskContract.FIELD_UID_PROJECT + ")");
    }
}
