package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskCategory;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.stmt.DeleteBuilder;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CreateOrRemoveTaskCategories {

    // VALUE's
    private Context mContext;
    private Set<Category> mCategories;
    private Task mTask;
    private boolean mUpdateNumberOfTasks;

    private DbHelper mDbHelper;

    public CreateOrRemoveTaskCategories(//
            Context context, Set<Category> categories, Task task, boolean updateNumberOfTasks) {
        mContext = context;
        mCategories = categories;
        mTask = task;
        mUpdateNumberOfTasks = updateNumberOfTasks;

        mDbHelper = DbHelper.getInstance(mContext);
    }

    public CreateOrRemoveTaskCategories(//
            Context context, Category category, Task task, boolean updateNumberOfTasks) {
        mContext = context;
        if (category != null) {
            mCategories = new HashSet<Category>(1);
            mCategories.add(category);
        }
        mTask = task;
        mUpdateNumberOfTasks = updateNumberOfTasks;

        mDbHelper = DbHelper.getInstance(mContext);
    }

    public CreateOrRemoveTaskCategories(Context context, boolean updateNumberOfTasks) {
        mContext = context;
        mUpdateNumberOfTasks = updateNumberOfTasks;

        mDbHelper = DbHelper.getInstance(mContext);
    }

    public CreateOrRemoveTaskCategories setData(Set<Category> categories, Task task) {
        mCategories = categories;
        mTask = task;

        return this;
    }

    public void run() throws LeaderTaskException {
        try {
            process();

        } catch (Exception e) {
            Utils.toLog(e);
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e);
        }
    }

    private void process() throws Exception {
        final boolean noNewCategories = mCategories == null || mCategories.isEmpty();

        if (mUpdateNumberOfTasks) {
            withUpdateNumberOfTasks(noNewCategories);
        } else {
            createTaskCategories(noNewCategories);
        }
    }

    private void withUpdateNumberOfTasks(boolean noNewCategories) throws Exception {
        final Set<Category> oldCategories = mDbHelper.getCategoriesSetByTask(mTask);
        removeOldTaskCategories();

        createTaskCategories(noNewCategories);

        updateNumberOfTasks(oldCategories, noNewCategories);
        sendBroadcast();
    }

    private void updateNumberOfTasks(Set<Category> oldCategories, boolean noNewCategories) throws SQLException {
        final Set<Category> allCategories = new HashSet<Category>();
        allCategories.addAll(oldCategories);
        if (!noNewCategories) {
            allCategories.addAll(mCategories);
        }

        mDbHelper.editsDueToCategoriesChanged(mContext, allCategories);
    }

    private void removeOldTaskCategories() throws SQLException {
        final DeleteBuilder<TaskCategory, Integer> builder = mDbHelper.getTaskCategoryDao().deleteBuilder();
        builder.where().eq(TaskCategory.FIELD_TASK_UID, mTask.getId());

        mDbHelper.getTaskCategoryDao().delete(builder.prepare());
    }

    private void sendBroadcast() {
        final Intent intent = new Intent();
        intent.setAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
        intent.setAction(ServiceConstants.ACTION_NOTIFY_DATASET_CHANGED_SLIDING_MENU);
        intent.putExtra(ServiceConstants.VALUE_BOOLEAN, true);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void createTaskCategories(boolean noNewCategories) throws Exception {
        if (noNewCategories || mCategories == null || mCategories.isEmpty()) {
            return;
        }

        final TaskCategory taskCategory = new TaskCategory();
        taskCategory.setTaskUID(mTask.getId());

        mDbHelper.getTaskCategoryDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Category category : mCategories) {
                    taskCategory.setCategoryUID(category.getId());

                    try {
                        mDbHelper.getTaskCategoryDao().create(taskCategory);

                    } catch (Exception e) {}
                }

                return null;
            }
        });
    }
}