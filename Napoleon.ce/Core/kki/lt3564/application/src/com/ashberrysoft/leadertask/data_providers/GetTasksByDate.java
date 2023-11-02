package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Получение задач по указанной дате.
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 */
public class GetTasksByDate extends BaseDatabaseRequest<ArrayList<Task>> {
    private static final long serialVersionUID = 1L;
    private String mUserName;
    private boolean showCompletedTasks;
    private Date mDate;

    /**
     * 
     * @param context
     * @param messages
     *            task messages
     */
    public GetTasksByDate(Context context, Date date, String userName, boolean showCompletedTasks) {
        super(context);
        mDate = date;
        mUserName = userName;
        this.showCompletedTasks = showCompletedTasks;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_GET_TASKS_BY_DATE_FINISHED;
    }

    @Override
    protected ArrayList<Task> sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // Date parameters
            final TimeZone tz = TimeZone.getTimeZone(SharedStrings.GMT);
            Calendar fEnd = Calendar.getInstance(tz);
            Calendar fBegin = Calendar.getInstance(tz);
            final Date currentDate = new Date(Utils.getCurrentTimeWithSavings());

            fEnd.setTime(mDate);
            fEnd.set(Calendar.HOUR_OF_DAY, 23);
            fEnd.set(Calendar.MINUTE, 59);
            fEnd.set(Calendar.SECOND, 59);
            fEnd.set(Calendar.MILLISECOND, 59);

            fBegin.setTime(mDate);
            fBegin.set(Calendar.HOUR_OF_DAY, 0);
            fBegin.set(Calendar.MINUTE, 0);
            fBegin.set(Calendar.SECOND, 0);
            fBegin.set(Calendar.MILLISECOND, 1);

            /*
             * building query get Where instance and set result ordering by "customer order"
             */
            final QueryBuilder<Task, UUID> builder = mDbHelper.getTaskDao().queryBuilder();
            // builder.selectColumns(mDbHelper.getVisibleTaskFields());

            builder.orderByRaw("CASE WHEN " + TaskContract.FIELD_EMAIL_CUSTOMER + " <> '" + mUserName
                    + "' THEN 0 ELSE 1 END, "//
                    + TaskContract.EMP_ORDERS + ", "//
                    + TaskContract.FIELD_EMAIL_CUSTOMER + ", "//
                    + TaskContract.FIELD_ORDER_CUSTOMER + ", "//
                    + TaskContract.FIELD_NAME);

            // builder.join(joinedQueryBuilder)

            final Where<Task, UUID> where = filterTasksToday(mDbHelper, builder.where(), mUserName, fBegin.getTime(),
                    fEnd.getTime(), currentDate, builder, false);

            mDbHelper.filterTasksWithMatchingParentByDate(where, mUserName, fBegin.getTime(), fEnd.getTime(),
                    currentDate);
            where.and(2);

            // TODO Bug #3471 "filterTasksFinishedFull" was changet to "filterTasksFinishedFullSubtask"
            if (!showCompletedTasks) {
                mDbHelper.filterTasksFinishedFullSubtask(where, builder, mUserName);
                where.and(2);
            }

            // Log.e("Query", builder.prepareStatementString());
            final List<Task> list = where.query();

            if (showCompletedTasks) {
                return new ArrayList<Task>(list);
            }

            // TODO Bug #3460 was created function auxiliaryFilter
            return AuxiliaryTaskFilter.auxiliaryFilter(list, mDbHelper, mUserName);
        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
        }
    }

    public static class AuxiliaryTaskFilter {

        public static int sDeletedItemsCount;

        /**
         * Функция фильтрующая отфильтрованные задачи по условию
         * 
         * @author Tregub Artem tregub.artem@gmail.com
         */
        public static ArrayList<Task> auxiliaryFilter(List<Task> list, DbHelper dbHelper, String user) {
            sDeletedItemsCount = 0;

            if (list == null || dbHelper == null) {
                return null;
            }

            final Dao<Task, UUID> dao = dbHelper.getTaskDao();
            for (int i = list.size() - 1; i >= 0; i--) {
                if (isTaskCompleted(dao, list.get(i), user)) {
                    sDeletedItemsCount++;
                    list.remove(i);
                }
            }

            return new ArrayList<Task>(list);
        }

        /**
         * Рекурсивная функция проверки задачи или родителя задачи на условие выполнения
         * 
         * @author Tregub Artem tregub.artem@gmail.com
         */
        private static boolean isTaskCompleted(Dao<Task, UUID> dao, Task child, String user) {
            if (child == null) {
                return false;
            }

            if (isTaskCompleted(child.getStatus(), user, child.getCustomer())) {
                return true;
            }

            final UUID parentUUID = child.getParentId();
            if (parentUUID == null) {
                return false;
            }

            try {
                return (isTaskCompleted(dao, DbHelper.getTaskDao_queryForId(dao, parentUUID), user));
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Функция проверки статуса задачи на выполнение условий<br>
         * <i>Status – статус задачи (число)</i><br>
         * 0 - не начиналось (значение по умолчанию)<br>
         * 1 - завершено<br>
         * 3 - заметка<br>
         * 4 - в работе<br>
         * 5 - готово к сдаче<br>
         * 6 - отложено<br>
         * 7 - отменено<br>
         * 8 - отклонено<br>
         * 9 - на доработку
         * 
         * @author Tregub Artem tregub.artem@gmail.com
         */
        private static boolean isTaskCompleted(int status, String user, String customer) {
            switch (status) {
            case 1:
            case 7:
                return true;

            case 5:
            case 8:
                return !user.equals(customer);

            default:
                return false;
            }
        }
    }

    /**
     * Построение фильтра который выбирает задачи для "Сегодня"
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param where
     * @param dateToday
     * @param userName
     * @throws SQLException
     */
    protected static Where<Task, UUID> filterTasksToday(DbHelper database, Where<Task, UUID> where, String userName,
            Date dateBegin, Date dateEnd, Date dateToday, QueryBuilder<Task, UUID> builder, boolean removeSubtasks)
            throws SQLException {
        prepareCounterWhere(database, where, userName, dateBegin, dateEnd, dateToday, builder);

        // 4
        if (removeSubtasks) { // remove subtasks
            QueryBuilder<Task, UUID> subbuilder = database.getTaskDao().queryBuilder();
            Where<Task, UUID> subWhere = subbuilder.where();
            prepareCounterWhere(database, subWhere, userName, dateBegin, dateEnd, dateToday, subbuilder);
            subbuilder.selectColumns(TaskContract.FIELD_UID);

            where.isNull(TaskContract.FIELD_UID_PARENT);
            where.notIn(TaskContract.FIELD_UID_PARENT, subbuilder);
            where.or(2);
            where.and(2);
        }
        return where;
    }

    /**
     * Формирование запроса для фильтра по дате.
     * 
     * @param where
     * @param userName
     *            имя текущего пользователя
     * @param dateToday
     *            текущая дата
     * @throws SQLException
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     */
    private static void prepareCounterWhere(DbHelper database, Where<Task, UUID> where, String userName,
            Date dateBegin, Date dateEnd, Date today, QueryBuilder<Task, UUID> builder) throws SQLException {

        // Логика для фильтра по дате (сегодня):
        // tDataBegin = выбранный для фильтрации день, время 00:00:00
        // tDataEnd = выбранный для фильтрации день, время 23:59:59
        //
        // Задача входит в выборку по сроку
        // Если у задачи установлен срок исполнителя {
        // 2
        {
            where.isNotNull(TaskContract.FIELD_TERM_END);
            where.isNotNull(TaskContract.FIELD_TERM_BEGIN);
            {
                // - если (TermEnd >= tDataBegin) и (TermBegin <= tDataEnd) и (Заказчик != Текущий пользователь или
                // Исполнитель == Текущий пользователь) то в выборку попадает // если срок попадает в текущий день, то
                // выводим;
                where.ge(TaskContract.FIELD_TERM_END, dateBegin);
                where.le(TaskContract.FIELD_TERM_BEGIN, dateEnd);
                {
                    where.ne(TaskContract.FIELD_EMAIL_CUSTOMER, userName);
                    where.eq(TaskContract.FIELD_EMAIL_PERFORMER, userName);
                    where.or(2);
                }
                where.and(3);

                // - если (tDataBegin <= Текущий день, время 00:00:00) и (TermEnd < tDataBegin) и задача не завершена то
                // выборку попадает // в сегодняшнем и предыдущем днях выводим незавершенные задачи к этой дате;
                if (dateBegin.compareTo(today) < 1) {
                    where.lt(TaskContract.FIELD_TERM_END, dateBegin);
                    database.filterTasksFinished(where, userName);
                    where.and(2);
                    where.or(2);
                }
            }
            where.and(3);
            //
            // - иначе в выборку не попадает
            //
        }
        // } иначе если у задачи не установлен срок исполнителя {
        // 3
        {
            where.isNull(TaskContract.FIELD_TERM_END);
            where.isNull(TaskContract.FIELD_TERM_BEGIN);
            {
                // - если у задачи иcполнитель текущий пользователь и заказчик не текущий пользователь и у задачи
                // установлен срок заказчика {
                where.eq(TaskContract.FIELD_EMAIL_PERFORMER, userName);
                where.ne(TaskContract.FIELD_EMAIL_CUSTOMER, userName);
                where.isNotNull(TaskContract.FIELD_TERM_END_CUSTOMER);
                where.isNotNull(TaskContract.FIELD_TERM_BEGIN_CUSTOMER);
                {
                    {
                        // - если (CustomerTermEnd >= tDataBegin) и (CustomerTermBegin <= tDataEnd)
                        // то в выборку попадает // если срок попадает в текущий день, то выводим
                        where.ge(TaskContract.FIELD_TERM_END_CUSTOMER, dateBegin);
                        where.le(TaskContract.FIELD_TERM_BEGIN_CUSTOMER, dateEnd);
                        where.and(2);
                    }
                    {
                        // - если (tDataBegin <= Текущий день, время 00:00:00) и (CustomerTermEnd < tDataBegin) и
                        // задача не завершена то выборку попадает // в сегодняшнем и предыдущем днях выводим
                        // незавершенные задачи к этой дате
                        if (dateBegin.compareTo(today) < 1) {
                            where.lt(TaskContract.FIELD_TERM_END_CUSTOMER, dateBegin);
                            database.filterTasksFinished(where, userName);
                            where.and(2);
                            where.or(2);
                        }
                    }
                }
                // - иначе в выборку не попадает
                where.and(5);
            }
            where.and(3);
        }
        // - иначе в выборку не попадает
        where.or(2);
    }
}
