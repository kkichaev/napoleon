package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskCategory;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Сохранение категорий в которые входит задача после редактирования.
 * 
 * @deprecated
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class SetTaskCategories extends BaseDatabaseRequest<Serializable> {
    private static final long serialVersionUID = 1L;
    private static ArgumentHolder[] sHolderTaskUID;
    private Task mTask;
    private Set<Category> mCategories;
    private boolean mIsForSync;
    private boolean mIsUpdateCategoryUsn;
    private boolean mIsUpdateTasksList;

    /**
     * 
     * @param context
     * @param task
     *            task data
     * @param messages
     *            task messages
     * @param isForSync
     *            - if task category objects need to update within synchronization process even if categories didn't
     *            change
     * @param isUpdateCategoryUsn
     *            - is need to update category usn field
     */
    public SetTaskCategories(Context context, Task task, Set<Category> categories, boolean isForSync,
            boolean isUpdateCategoryUsn, boolean isUpdateTasksList) {
        super(context);

        mTask = task;
        mCategories = categories;
        mIsForSync = isForSync;
        mIsUpdateCategoryUsn = isUpdateCategoryUsn;
        mIsUpdateTasksList = isUpdateTasksList;
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_SAVE_TASK_CATEGORIES_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // create ArgumentHolder instance for any <?> in "task status" SQL subquery
            if (sHolderTaskUID == null) {
                sHolderTaskUID = new ArgumentHolder[] { new SelectArg(TaskCategory.FIELD_TASK_UID, mTask.getId()) };
            }

            // get task categories from database
            final Set<Category> oldCategories = mDbHelper.getCategoriesSetByTask(mTask);

            if (!mIsForSync && oldCategories.equals(mCategories)) {
                // categories was not changed
                return null;
            }
            // get old same task, if exists
            Task oldTask = mDbHelper.getTaskDao_queryForId(mTask.getId());
            if (oldTask == null) {
                oldTask = mTask;
            }

            // update Categories USN
            if (mIsUpdateCategoryUsn) {
                oldTask.setUsnCategories(oldTask.getUsnCategories() + 1);
            }

            if (!mIsForSync) {
                oldTask.setUsn(0);
            }
            // update task categories
            oldTask.setCategoriesWithSet(mCategories);
            // clean old categories
            final Dao<TaskCategory, Integer> dao = mDbHelper.getTaskCategoryDao();
            final DeleteBuilder<TaskCategory, Integer> builder = dao.deleteBuilder();
            builder.where().raw(TaskCategory.FIELD_TASK_UID + " = ?", sHolderTaskUID);
            dao.delete(builder.prepare());

            if (mCategories != null && !mCategories.isEmpty()) {
                // insert new categories
                final TaskCategory taskCategory = new TaskCategory();
                taskCategory.setTaskUID(mTask.getId());

                for (Category category : mCategories) {
                    taskCategory.setCategoryUID(category.getId());

                    try {
                        dao.createIfNotExists(taskCategory);
                    } catch (Exception e) {
                        dao.delete(taskCategory);
                    }
                }
            }

            if (!mIsForSync && !oldCategories.equals(mCategories)) {
                // define set of changed categories
                Set<Category> changedCategories = new HashSet<Category>(oldCategories);
                // get union of old and new categories
                if (mCategories != null) {
                    changedCategories.addAll(mCategories);
                }

                Set<Category> tmp = new HashSet<Category>(oldCategories);

                // get intersection of old and new categories
                if (mCategories != null) {
                    tmp.retainAll(mCategories);
                }
                // get changed categories = union - intersection
                changedCategories.removeAll(tmp);

                // due to task category field changed, make some edits to database
                mDbHelper.editsDueToCategoriesChanged(mContext, changedCategories);
            }

            if (!mIsForSync) {
                mDbHelper.updateTask(oldTask);

                // send broadcast intent in order to update sliding menu and tasks list
                final Intent intent = new Intent();
                if (mIsUpdateTasksList) {
                    intent.setAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
                }
                intent.setAction(ServiceConstants.ACTION_NOTIFY_DATASET_CHANGED_SLIDING_MENU);
                intent.putExtra(ServiceConstants.VALUE_BOOLEAN, true);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }

            return null;
        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
        }
    }
}
