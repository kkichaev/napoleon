package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.content.Context;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Рассчет значения поля depth (глубина вложенности в иерархии задач) для каждой задачи.
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class TaskDepthCalculation extends BaseDatabaseRequest<Serializable> {
    private static final long serialVersionUID = 1L;
    private List<Task> mAllModifiedTasks;
    private MapWithDupKeys mMapWithDupKeys;
    private Map<UUID, List<Integer>> mTasksMap;
    private List<Task> mAllTasks;
    private int mLeft;
    private int mRight;

    /**
     * parameterized constructor
     * 
     * @param context
     *            - Context instance from which we make constructor call
     */
    public TaskDepthCalculation(Context context) {
        super(context);
    }

    @Override
    public String getResultAction() {
        return IPCConstants.ACTION_TASK_DEPTH_CALCULATION_FINISHED;
    }

    @Override
    protected Serializable sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // create Map for all tasks
            mTasksMap = new HashMap<UUID, List<Integer>>();

            mDbHelper.getTaskDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    // get all tasks
                    mAllTasks = mDbHelper
                            .getTaskDao()
                            .queryBuilder()
                            .selectColumns(TaskContract.FIELD_UID, TaskContract.FIELD_UID_PARENT,
                                    TaskContract.LEFT_POINTER, TaskContract.RIGHT_POINTER).query();
                    return null;
                }
            });

            // create list for all modified tasks
            mAllModifiedTasks = new ArrayList<Task>();
            // create Map with duplicate keys for subtasks of particular task
            mMapWithDupKeys = new MapWithDupKeys();
            for (Task task : mAllTasks) {
                List<Integer> oldValues = new ArrayList<Integer>();
                int left = task.getLeftPointer();
                oldValues.add(left);
                int right = task.getRightPointer();
                oldValues.add(right);
                mTasksMap.put(task.getId(), oldValues);
                if (task.getParentId() != null)
                    mMapWithDupKeys.put(task.getParentId(), task);
            }
            mLeft = 0;
            mRight = 1;
            // iterate over each task
            for (int location = 0; location < mAllTasks.size(); location++) {
                // get particular task
                Task task = mAllTasks.get(location);
                // if particular task has no parent, that is "root" task
                if (task.getParentId() == null) {
                    // set left pointer
                    task.setLeftPointer(mLeft);
                    // set right pointer
                    task.setRightPointer(mRight);
                    // process all subtasks for particular task
                    processSubtasks(task);
                }
            }
            // clear container for all tasks
            mAllTasks = null;
            // clear Map with duplicate keys for subtasks of particular task
            mMapWithDupKeys = null;

            // get Dao instance for task
            // final Dao<Task, UUID> dao = mDatabase.getTaskDao();

            /*
             * UpdateBuilder<Task, UUID> updateBuilder = mDatabase.getTaskDao().updateBuilder();
             * updateBuilder.updateColumnValue(Task.FIELD_LEFT_POINTER, "?");
             * updateBuilder.updateColumnValue(Task.FIELD_RIGHT_POINTER, "?"); updateBuilder.where().raw(Task.FIELD_UID
             * + " = '?'"); final PreparedUpdate<Task> preparedUpdate = updateBuilder.prepare();
             * Log.e("Update statement", preparedUpdate.getStatement());
             */

            mDbHelper.getTaskDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {

                    // update all tasks
                    for (Task task : mAllModifiedTasks) {
                        if (mTasksMap.get(task.getId()).get(0) != task.getLeftPointer()
                                || mTasksMap.get(task.getId()).get(1) != task.getRightPointer()) {
                            mDbHelper.getTaskDao().updateRaw(
                                    "UPDATE tasks SET lft = " + task.getLeftPointer() + ", rgt = "
                                            + task.getRightPointer() + " WHERE UID = '" + task.getId() + "'");
                            /*
                             * preparedUpdate.setArgumentHolderValue(0, task.getLeftPointer());
                             * preparedUpdate.setArgumentHolderValue(1, task.getRightPointer());
                             * preparedUpdate.setArgumentHolderValue(2, task.getIdTask());
                             * mDatabase.getTaskDao().update(preparedUpdate);
                             */
                        }
                    }
                    return null;
                }
            });

            // clear Map for all tasks
            mTasksMap = null;
            // clear Map instance with all tasks
            mAllModifiedTasks = null;
            return null;
        } catch (SQLException e1) {
            throw new LeaderTaskException(ErrorType.SQLITE_ERROR, mContext, 0, e1);
            // e1.printStackTrace();
            // return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param task
     *            - particular task for which need to traverse all subtasks
     */
    private void processSubtasks(Task task) {
        // get all subtasks for particular task
        List<Task> tasks = mMapWithDupKeys.get(task.getId());
        if (tasks != null) {
            for (Task t : tasks) {
                int left = task.getRightPointer();
                t.setLeftPointer(left);
                t.setRightPointer(left + 1);
                // process all subtasks for particular subtask
                processSubtasks(t);
                task.setRightPointer(t.getRightPointer() + 1);
            }
        }
        // add task to result list
        mAllModifiedTasks.add(task);
        // update left and right pointers
        mLeft = task.getRightPointer() + 1;
        mRight = task.getRightPointer() + 2;
    }

    /**
     * Class for saving map entries with duplicate keys.
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    private class MapWithDupKeys {

        // Map instance for entries with duplicate keys
        private Map<UUID, List<Task>> mMap;

        // default constructor
        public MapWithDupKeys() {
            mMap = new HashMap<UUID, List<Task>>();
        }

        // put element to Map instance
        public void put(UUID uuid, Task task) {
            List<Task> tasks = get(uuid);
            if (tasks == null) {
                tasks = new ArrayList<Task>();
                mMap.put(uuid, tasks);
            }
            tasks.add(task);
        }

        // get element by UUID from Map instance
        public List<Task> get(UUID uuid) {
            return (List<Task>) mMap.get(uuid);
        }
    }
}
