package com.ashberrysoft.leadertask.data_providers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.dao.Dao;

/**
 * @since 2014-06-24
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CloneTaskHelper {

    // VALUE's
    private final Context mContext;
    private final Task mTask;

    private Task mNewTask;

    public CloneTaskHelper(Context context, Task task) {
        mContext = context;
        mTask = task;
    }

    public CloneTaskHelper createCloneOfSeriesTask(Date begin, Date end) {
        mNewTask = new Task();
        copyFieldsOfSeriesTask(mNewTask, begin, end);

        return this;
    }

    public void create() {
        try {
            DbHelper.getInstance(mContext).getTaskDao().create(mNewTask);

        } catch (SQLException e) {
            return;
        }

        updateTaskCategoryLink(mNewTask);
        copyFiles(mNewTask);
        try {
            DbHelper.getInstance(mContext).updateNumberTaskAfterDelete_Add(mNewTask, false, false, false);

        } catch (Exception e) {}
        // LTCalendarView.clearCalendarData(mContext);
    }

    private void copyFieldsOfSeriesTask(Task task, Date begin, Date end) {
        task.setId(UUID.randomUUID());
        task.setParentId(mTask.getParentId());

        task.setName(mTask.getName());
        task.setComment(mTask.getComment());

        task.setPerformer(mTask.getPerformer());
        task.setCustomer(LTSettings.getInstance(mContext).getUserName());

        task.setTermBegin(begin);
        task.setTermEnd(end);
        task.setTermBeginCustomer(begin);
        task.setTermEndCustomer(end);

        task.setCategories(mTask.getCategories());
        task.setContacts(mTask.getContacts());
        task.setProjectUid(mTask.getProjectUid());
        task.setMarkerUid(mTask.getMarkerUid());
        task.setLabelsString(mTask.getLabelsString());
        task.setStatus(TaskStatus.NOT_BEGIN.getCode());

        task.setHasFiles(mTask.isHasFiles());

        setNewTaskOrder(mContext, task);
        task.setReaded(true);

        final Date date = new Date(Utils.getCurrentTimeWithSavings());
        task.setCreationTime(date);
        task.setPerformTime(date);
        task.setCompleteTime(date);

        task.setSeriesType(mTask.getSeriesType());
        task.setSeriesAfterType(mTask.getSeriesAfterType());
        task.setSeriesAfterCount(mTask.getSeriesAfterCount());
        task.setSeriesWeekCount(mTask.getSeriesWeekCount());
        task.setSeriesWeekMon(mTask.isSeriesWeekMon());
        task.setSeriesWeekTue(mTask.isSeriesWeekTue());
        task.setSeriesWeekWed(mTask.isSeriesWeekWed());
        task.setSeriesWeekThu(mTask.isSeriesWeekThu());
        task.setSeriesWeekFri(mTask.isSeriesWeekFri());
        task.setSeriesWeekSat(mTask.isSeriesWeekSat());
        task.setSeriesWeekSun(mTask.isSeriesWeekSun());
        task.setSeriesMonthType(mTask.getSeriesMonthType());
        task.setSeriesMonthCount(mTask.getSeriesMonthCount());
        task.setSeriesMonthDay(mTask.getSeriesMonthDay());
        task.setSeriesMonthWeekType(mTask.getSeriesMonthWeekType());
        task.setSeriesMonthDayOfWeek(mTask.getSeriesMonthDayOfWeek());
        task.setSeriesYearType(mTask.getSeriesYearType());
        task.setSeriesYearMonth(mTask.getSeriesYearMonth());
        task.setSeriesYearMonthDay(mTask.getSeriesYearMonthDay());
        task.setSeriesYearWeekType(mTask.getSeriesYearWeekType());
        task.setSeriesYearDayOfWeek(mTask.getSeriesYearDayOfWeek());
        task.setSeriesEnd(mTask.getSeriesEnd());

        DbHelper.getInstance(mContext).updateLeftRightPointers(task);
    }

    private void updateTaskCategoryLink(Task task) {
        try {
            new CreateOrRemoveTaskCategories(mContext, false).setData(task.getCategoriesInHash(), task).run();
        } catch (LeaderTaskException e) {}
    }

    private void copyFiles(Task task) {
        Cursor c = null;

        try {
            c = mContext.getContentResolver().query(TaskFileContract.CONTENT_URI, null, TaskFileContract//
                    .selectionFieldTaskUidAndDeleteObjectAndWeakLink(String.valueOf(mTask.getId()), false, false),//
                    null, TaskFileContract.defaultSort());

            if (c.getCount() == 0) {
                return;
            }

            final File appFolder = ((LTApplication) mContext).getAppFolder();
            final ContentValues[] cvs = new ContentValues[c.getCount()];
            int order = 1;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext(), order++) {
                final TaskFile f = new TaskFile(c);

                if (f.isFileExist()) {
                    try {
                        Utils.FileWorker.copyFile(f.getFileName(), appFolder);
                    } catch (IOException e) {
                        Utils.toLog(e);
                    }
                }

                f.setId(UUID.randomUUID());
                f.setFileId(UUID.randomUUID());
                f.setTaskId(task.getId());
                f.setOrder(order);
                f.resetUsnFields();

                cvs[order - 1] = f.getContentValues(null);
            }

            mContext.getContentResolver().bulkInsert(TaskFileContract.CONTENT_URI, cvs);

        } catch (Exception e) {
            Utils.toLog(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static void setNewTaskOrder(Context context, Task task) {
        final DbHelper dbHelper = DbHelper.getInstance(context);

        if (task.getParentId() != null) {
            dbHelper.setOrderFromSubtaskMax(task);

        } else {
            dbHelper.setOrderFromTaskMax(task);
        }
    }

    public Task getNewTask() {
        return mNewTask;
    }

    public void setNewTask(Task newTask) {
        mNewTask = newTask;
    }
}