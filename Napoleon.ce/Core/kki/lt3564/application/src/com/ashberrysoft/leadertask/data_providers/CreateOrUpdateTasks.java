package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CreateOrUpdateTasks {

    // VALUE's
    private Context mContext;
    private List<Task> mTasks;
    private boolean mJustCreate;

    private DbHelper mDbHelper;
    private CreateOrRemoveTaskCategories mCategoriesHelper;

    public CreateOrUpdateTasks(Context context, List<Task> tasks, boolean firstSync) {
        mContext = context;
        mTasks = tasks;
        mJustCreate = firstSync;

        mDbHelper = DbHelper.getInstance(mContext);
        mCategoriesHelper = new CreateOrRemoveTaskCategories(mContext, false);
    }

    public void start() throws SQLException {
        try {
            processTasks();

        } catch (SQLException e) {
            Utils.toLog(e);

            throw e;

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private void processTasks() throws Exception {
        removeFromDeletedTask();
        createOrUpdateTasks();
    }

    private void removeFromDeletedTask() throws Exception {
        if (mJustCreate || mTasks.isEmpty()) {
            return;
        }

        final List<UUID> uids = new ArrayList<UUID>(mTasks.size());
        for (Task task : mTasks) {
            uids.add(task.getId());
        }

        removeFromDeletedTask(mDbHelper, uids);
    }

    public static void removeFromDeletedTask(DbHelper dbHelper, List<UUID> uids) throws Exception {
        int location = 0;
        while (location < uids.size()) {
            int nextLocation = location + 900;
            if (nextLocation > uids.size()) {
                nextLocation = uids.size();
            }

            dbHelper.getDeletedTaskDao().deleteIds(uids.subList(location, nextLocation));
            location = nextLocation;
        }
    }

    private void createOrUpdateTasks() throws Exception {
        if (mJustCreate) {
            createTasks(mTasks);
            return;
        }

        final ContentResolver cr = mContext.getContentResolver();
        final List<Task> createTasks = new ArrayList<Task>();
        final ContentValues cv = new ContentValues();
        final Task oldTask = new Task();
        Cursor cursor = null;

        for (Task newTask : mTasks) {
            try {
                cursor = cr.query(TaskContract.CONTENT_URI, null,//
                        TaskContract.selectionFieldUid(String.valueOf(newTask.getId())), null, null);

                if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                    oldTask.setData(cursor);
                    updateTask(oldTask, newTask, cv);
                } else {
                    createTasks.add(newTask);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }

        createTasks(createTasks);
    }

    private void updateTask(Task oldTask, Task newTask, ContentValues cv) throws Exception {
        if (oldTask.getUsn() == newTask.getUsn()) {
            return;
        }

        oldTask.setUsn(newTask.getUsn());
        oldTask.setCustomer(newTask.getCustomer());

        if (oldTask.getUsnParentUid() <= newTask.getUsnParentUid()) {
            oldTask.setParentId(newTask.getParentId());
            oldTask.setUsnParentUid(newTask.getUsnParentUid());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnCollapsed() <= newTask.getUsnCollapsed()) {
            oldTask.setCollapsed(newTask.isCollapsed());
            oldTask.setUsnCollapsed(newTask.getUsnCollapsed());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnOrder() <= newTask.getUsnOrder()) {
            oldTask.setOrder(newTask.getOrder());
            oldTask.setUsnOrder(newTask.getUsnOrder());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnCustomerOrder() <= newTask.getUsnCustomerOrder()) {
            oldTask.setCustomerOrder(newTask.getCustomerOrder());
            oldTask.setUsnCustomerOrder(newTask.getUsnCustomerOrder());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnName() <= newTask.getUsnName()) {
            oldTask.setName(newTask.getName());
            oldTask.setUsnName(newTask.getUsnName());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnComment() <= newTask.getUsnComment()) {
            oldTask.setComment(newTask.getComment());
            oldTask.setUsnComment(newTask.getUsnComment());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnStatus() <= newTask.getUsnStatus()) {
            oldTask.setStatus(newTask.getStatus());
            oldTask.setUsnStatus(newTask.getUsnStatus());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnEmailPerformer() <= newTask.getUsnEmailPerformer()) {
            oldTask.setPerformer(newTask.getPerformer());
            oldTask.setUsnEmailPerformer(newTask.getUsnEmailPerformer());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnTerm() <= newTask.getUsnTerm()) {
            oldTask.setTermBegin(newTask.getTermBegin());
            oldTask.setTermEnd(newTask.getTermEnd());
            oldTask.setUsnTerm(newTask.getUsnTerm());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnCustomerTerm() <= newTask.getUsnCustomerTerm()) {
            oldTask.setTermCustomerBegin(newTask.getTermCustomerBegin());
            oldTask.setTermCustomerEnd(newTask.getTermCustomerEnd());
            oldTask.setUsnCustomerTerm(newTask.getUsnCustomerTerm());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnProjectUid() <= newTask.getUsnProjectUid()) {
            oldTask.setProjectUid(newTask.getProjectUid());
            oldTask.setUsnProjectUid(newTask.getUsnProjectUid());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnMarkerUid() <= newTask.getUsnMarkerUid()) {
            oldTask.setMarkerUid(newTask.getMarkerUid());
            oldTask.setUsnMarkerUid(newTask.getUsnMarkerUid());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnReaded() <= newTask.getUsnReaded()) {
            oldTask.setReaded(newTask.isReaded());
            oldTask.setUsnReaded(newTask.getUsnReaded());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnCategories() <= newTask.getUsnCategories()) {
            oldTask.setUsnCategories(newTask.getUsnCategories());
            oldTask.setCategories(newTask.getCategories());

            updateCategories(newTask);
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnContacts() <= newTask.getUsnContacts()) {
            oldTask.setContacts(newTask.getContacts());
            oldTask.setUsnContacts(newTask.getUsnContacts());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnFieldCreateTime() <= newTask.getUsnFieldCreateTime()) {
            oldTask.setCreationTime(newTask.getCreationTime());
            oldTask.setUsnFieldCreateTime(newTask.getUsnFieldCreateTime());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnFieldPerformTime() <= newTask.getUsnFieldPerformTime()) {
            oldTask.setPerformTime(newTask.getPerformTime());
            oldTask.setUsnFieldPerformTime(newTask.getUsnFieldPerformTime());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnFieldCompleteTime() <= newTask.getUsnFieldCompleteTime()) {
            oldTask.setCompleteTime(newTask.getCompleteTime());
            oldTask.setUsnFieldCompleteTime(newTask.getUsnFieldCompleteTime());
        } else {
            oldTask.setUsn(0);
        }

        if (oldTask.getUsnFieldSeries() <= newTask.getUsnFieldSeries()) {
            oldTask.setUsnFieldSeries(newTask.getUsnFieldSeries());
            oldTask.setSeriesType(newTask.getSeriesType());
            oldTask.setSeriesAfterType(newTask.getSeriesAfterType());
            oldTask.setSeriesAfterCount(newTask.getSeriesAfterCount());
            oldTask.setSeriesWeekCount(newTask.getSeriesWeekCount());
            oldTask.setSeriesWeekMon(newTask.isSeriesWeekMon());
            oldTask.setSeriesWeekTue(newTask.isSeriesWeekTue());
            oldTask.setSeriesWeekWed(newTask.isSeriesWeekWed());
            oldTask.setSeriesWeekThu(newTask.isSeriesWeekThu());
            oldTask.setSeriesWeekFri(newTask.isSeriesWeekFri());
            oldTask.setSeriesWeekSat(newTask.isSeriesWeekSat());
            oldTask.setSeriesWeekSun(newTask.isSeriesWeekSun());
            oldTask.setSeriesMonthType(newTask.getSeriesMonthType());
            oldTask.setSeriesMonthCount(newTask.getSeriesMonthCount());
            oldTask.setSeriesMonthDay(newTask.getSeriesMonthDay());
            oldTask.setSeriesMonthWeekType(newTask.getSeriesMonthWeekType());
            oldTask.setSeriesMonthDayOfWeek(newTask.getSeriesMonthDayOfWeek());
            oldTask.setSeriesYearType(newTask.getSeriesYearType());
            oldTask.setSeriesYearMonth(newTask.getSeriesYearMonth());
            oldTask.setSeriesYearMonthDay(newTask.getSeriesYearMonthDay());
            oldTask.setSeriesYearWeekType(newTask.getSeriesYearWeekType());
            oldTask.setSeriesYearDayOfWeek(newTask.getSeriesYearDayOfWeek());
            oldTask.setSeriesEnd(newTask.getSeriesEnd());

        } else {
            oldTask.setUsn(0);
        }

        mContext.getContentResolver().update(TaskContract.CONTENT_URI,//
                oldTask.getContentValues(cv), TaskContract.selectionFieldUid(String.valueOf(oldTask.getId())), null);
    }

    private void createTasks(final List<Task> tasks) throws Exception {
        if (tasks.isEmpty()) {
            return;
        }

        mDbHelper.getTaskDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Task task : tasks) {
                    mDbHelper.getTaskDao().create(task);
                    updateCategories(task);
                }

                return null;
            }
        });
    }

    private void updateCategories(Task task) throws Exception {
        mCategoriesHelper.setData(task.getCategoriesInHash(), task);
        mCategoriesHelper.run();
    }
}