package com.ashberrysoft.leadertask.fragments;

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.BaseSlidingActivity;
import com.ashberrysoft.leadertask.activities.BaseSlidingActivity.OnBackClickListener;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.adapters.CustomFragmentStatePagerAdapter;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.CloneTaskHelper;
import com.ashberrysoft.leadertask.data_providers.CreateOrRemoveTaskCategories;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.LazyLoadMethods;
import com.ashberrysoft.leadertask.data_providers.SaveTask;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.interfaces.FragmentsCommunicationInterface;
import com.ashberrysoft.leadertask.modern.builder.TaskBuilder;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.Utils.JsonTaskLabelsUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Свойства задачи, фрагмент с табами
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */

@SuppressWarnings("deprecation")
public class TabViewFragment extends LTVisibleBaseFragment implements OnBackClickListener {

    private static final String CLASS_PATH = TabViewFragment.class.getName();
    private static final String EXTRA_CATEGORY = CLASS_PATH + "EXTRA_CATEGORY";
    private static final String EXTRA_TASK_NEW = CLASS_PATH + "EXTRA_TASK_NEW";

    // INSTANCE
    private static TabViewFragment sInstance;

    // VIEW's
    private PagerTabStrip mTabStrip;
    private ViewPager mViewPager;

    // VALUE's
    private Handler mHandler;

    private Task mTask;
    private Category mCategory;
    private boolean mTaskNew;

    private TaskStatus mOldTaskStatus;
    private String mOldTaskCustomer;
    private String mOldTaskPerformer;
    private DbHelper mDbHelper;
    private boolean mWasResume;

    // ADAPTER
    private CustomFragmentStatePagerAdapter mAdapter;

    // LISTENER
    private FragmentsCommunicationInterface mListener;

    public static TabViewFragment newInstance(Fragment fragment, Task task, Category category) {
        final Bundle b = new Bundle(2);
        b.putSerializable(IPCConstants.EXTRA_TASK, task);
        if (category != null) {
            b.putSerializable(EXTRA_CATEGORY, category);
        }

        final TabViewFragment f = new TabViewFragment();
        f.setTargetFragment(f, 0);
        f.setArguments(b);

        return f;
    }

    public static TabViewFragment newInstance(Fragment fragment, Task task) {
        return newInstance(fragment, task, null);
    }

    public static TabViewFragment newInstance(Fragment fragment, String name, String customer, String performer, Date term, UUID parentId, UUID projectId,
            Category category) {
        final Task task = new TaskBuilder().setDefaults().getTask();

        task.setName(name);
        task.setCustomer(customer);
        task.setPerformer(performer);
        if (term != null) {
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(SharedStrings.GMT));
            calendar.setTime(term);

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);

            task.setTermEnd(calendar.getTime());

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            task.setTermBegin(calendar.getTime());
        }
        task.setParentId(parentId);
        task.setProjectUid(projectId);

        return newInstance(fragment, task, category);
    }

    public static TabViewFragment newInstance(String currentUserName, String textPlain) {
        return newInstance(null, textPlain, currentUserName, currentUserName, null, null, null, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (FragmentsCommunicationInterface) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onFragmentToFragmentCommunication");
        }
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);

        mHandler = new Handler();
        mDbHelper = DbHelper.getInstance(getActivity());

        final Bundle bundle = b == null ? getArguments() : b;
        mTask = (Task) bundle.getSerializable(IPCConstants.EXTRA_TASK);
        mTaskNew = bundle.getBoolean(EXTRA_TASK_NEW, false);
      
        if (bundle.containsKey(EXTRA_CATEGORY)) {
            mCategory = (Category) bundle.getSerializable(EXTRA_CATEGORY);
        }

        sInstance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setName();
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(final View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // Set the pager with an adapter
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mTabStrip = (PagerTabStrip) v.findViewById(R.id.pager_title_strip);

        setBlock(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                {
                    final Task task = mDbHelper.getTaskDao_queryForId(mTask.getId());
                    if (task != null) {
                        mTask = task;

                    } else {
                        mTaskNew = true;

                        CloneTaskHelper.setNewTaskOrder(mApp, mTask);
                        mDbHelper.updateLeftRightPointers(mTask);
                    }
                }

                mAdapter = new CustomFragmentStatePagerAdapter(getChildFragmentManager(), getActivity(), mTask, mCategory, v, mTaskNew);
                mCategory = null;

                // load messages
                LazyLoadMethods.updateTaskMessagesCount(mTask, mDbHelper);
                // get task status before edits
                mOldTaskStatus = mTask.getStatusType();
                // get task customer before edits
                mOldTaskCustomer = mTask.getCustomer();
                // get task performer before edits
                mOldTaskPerformer = mTask.getPerformer();

                if (mTask.isReaded()) {
                    mHandler.post(mUpdateRun);
                    return;
                }

                try {
                    mDbHelper.editsDueToReadedFlagChanged(mApp, mTask);

                } catch (Exception e) {
                    Utils.toLog(e);
                }

                mDbHelper.recalculateVerticalTaskSubtasks(mApp, mSettings.getUserName(), mTask);

                mHandler.post(mUpdateRun);
            }
        }).start();
    }

    private final Runnable mUpdateRun = new Runnable() {
        @Override
        public void run() {
            try {
                if (mWasResume) {
                    mAdapter.setAdapterIsResumed();
                }

                mViewPager.setAdapter(mAdapter);
                mViewPager.setCurrentItem(1);
                mViewPager.setOffscreenPageLimit(mAdapter.getCount());

                setBlock(false);

            } catch (Exception e) {
                Utils.toLog(e);
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(IPCConstants.EXTRA_TASK, mTask);
        outState.putBoolean(EXTRA_TASK_NEW, mTaskNew);
    }

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter filter = new IntentFilter(ServiceConstants.ACTION_TASK_MESSAGE);
        filter.addAction(IPCConstants.ACTION_SAVE_TASK_FINISHED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);

        if (mAdapter != null) {
            mAdapter.setAdapterIsResumed();
            mAdapter.notifyDataSetChanged();
        }
        mWasResume = true;

        setBackground();


        // set flag that screen is not minimized
        SubtasksListFragment.sIsScreenMinimized = false;

        ((BaseSlidingActivity) getActivity()).disableAndSetSlidingMenu(true, true);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        if (mAdapter != null) {
            mAdapter.setAdapterIsPaused();
            mAdapter.notifyDataSetChanged();
        }
        mWasResume = false;

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.edit_fragment_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mViewPager != null && mViewPager.getCurrentItem() == 0) {
            menu.findItem(R.id.add_from_camera).setVisible(false);
            menu.findItem(R.id.add_from_gallery).setVisible(false);
            //menu.findItem(R.id.add_audio).setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            resetTaskFileWithoutSaving();
            return true;

        case R.id.save_task:
            // set is current screen is tasks screen
            Utils.hideInput(mApp, mViewPager);
            setBlockForSave(true);
            // ((BaseSlidingActivity)
            // getActivity()).getSlidingMenu().setIsTasksScreen(true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveChanges();

                    } catch (Exception e) {
                        Utils.toLog(e);
                        mHandler.post(mEndRun);
                    }
                }
            }).start();
            return true;

        case R.id.add_from_camera:
        case R.id.add_from_gallery:
            if (TaskEditFragment.getInstance() != null) {
                return TaskEditFragment.getInstance().onOptionsItemSelected(item);
            }
            return false;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void resetTaskFileWithoutSaving() {
        // TODO removeMessages if new Task

        Utils.hideInput(mApp, mViewPager);
        setBlockForSave(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSettings.isTaskFromNotify()) {
                    mSettings.setTaskFromNotify(false);
                }

                final ContentResolver cr = mApp.getContentResolver();
                final String taskUID = mTask.getId().toString();

                /**
                 * files with weak=true AND delete=false OR weak=true AND delete=true must be set as delete=true
                 */
                final Cursor wd1011 = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionFieldTaskUidAndWeakLink(taskUID, true), null, null);
                if (wd1011.getCount() > 0) {
                    final ContentValues cv = new ContentValues();
                    cv.put(TaskFileContract.DELETE_OBJECT, 1);

                    final int[] colums = new int[2];
                    colums[0] = wd1011.getColumnIndex(TaskFileContract.FIELD_FILENAME);
                    colums[1] = wd1011.getColumnIndex(TaskFileContract.FIELD_FILEUID);
                    for (wd1011.moveToFirst(); !wd1011.isAfterLast(); wd1011.moveToNext()) {
                        new File(mApp.getAppFolder(), wd1011.getString(colums[0])).delete();

                        cr.update(TaskFileContract.CONTENT_URI, cv, TaskFileContract.selectionFieldFileUid(wd1011.getString(colums[1])), null);
                    }
                }
                wd1011.close();

                /**
                 * files with weak=false AND delete=true must be set as delete=false
                 */
                final Cursor wd01 = cr.query(TaskFileContract.CONTENT_URI, null,
                        TaskFileContract.selectionFieldTaskUidAndDeleteObjectAndWeakLink(taskUID, true, false), null, null);
                if (wd01.getCount() > 0) {
                    final ContentValues cv = new ContentValues();
                    cv.put(TaskFileContract.DELETE_OBJECT, 0);

                    final int columUID = wd01.getColumnIndex(TaskFileContract.FIELD_FILEUID);
                    for (wd01.moveToFirst(); !wd01.isAfterLast(); wd01.moveToNext()) {
                        cr.update(TaskFileContract.CONTENT_URI, cv, TaskFileContract.selectionFieldFileUid(wd01.getString(columUID)), null);
                    }
                }
                wd01.close();

                mHandler.post(mEndRun);
            }
        }).start();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ServiceConstants.ACTION_TASK_MESSAGE)) {
                // increase by 1 task messages count
                mTask.setMessagesCount(mTask.getMessagesCount() + 1);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            } else if (action.equals(IPCConstants.ACTION_SAVE_TASK_FINISHED)) {
                setBlockingProcess(false, null);
                getFragmentManager().popBackStack();
            }
        }
    };

    private void saveChanges() throws AbstractDataRequestException {
        final Task task = TaskEditFragment.getInstance().getTask();
        int delta = 0;

        // 3.3 code
        saveTaskFiles(mApp, task.getId().toString());
        final boolean hasTasks = mDbHelper.recalculateFilesInTask(mApp, task);
        task.setHasFiles(hasTasks);
        task.setMessagesCount(mDbHelper.recalculateTaskMessagesInTask(mApp, task));

        // if task is completed before changes was made
        boolean isCompletedBeforeChanges = isCompleted(mOldTaskStatus, mOldTaskCustomer);
        // if task is not completed after changes was made
        boolean isNotCompletedAfterChanges = !isCompleted(task.getStatusType(), task.getCustomer());
        // if task is not completed before changes was made
        boolean isNotCompletedBeforeChanges = !isCompleted(mOldTaskStatus, mOldTaskCustomer);
        // if task is completed after changes was made
        boolean isCompletedAfterChanges = isCompleted(task.getStatusType(), task.getCustomer());

        // task status changed from "completed" to "not completed"
        if (isCompletedBeforeChanges && isNotCompletedAfterChanges) {
            delta = 1;
        }
        // task status changed from "not completed" to "completed"
        else if (isNotCompletedBeforeChanges && isCompletedAfterChanges) {
            delta = -1;
        }

        // if task status changed from "completed" to "not completed" or vice
        // versa, then make some edits to database
        if ((isCompletedBeforeChanges && isNotCompletedAfterChanges) || (isNotCompletedBeforeChanges && isCompletedAfterChanges)) {
            mDbHelper.editsDueToStatusChanged(getActivity(), task.getParentId(), delta, false);
        }
        mDbHelper.editsDueToTermChanged(getActivity());

        /*
         * if we assign task to/from particular user which email doesn't exists in database, then we need to update
         * sliding menu hierarchy
         */
        boolean isOldPerformerExists = false;
        boolean isNewPerformerExists = false;
        final List<String> allEmails = mDbHelper.getAllEmails();
        for (String email : allEmails) {
            if (mOldTaskPerformer.equals(email)) {
                isOldPerformerExists = true;
            }
            if (task.getPerformer().equals(email)) {
                isNewPerformerExists = true;
            }
        }

        if (!isOldPerformerExists || !isNewPerformerExists) {
            // update sliding menu hierarchy
            final Intent intent = new Intent();
            intent.setAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
            LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
        }

        /*
         * current date with set time to 23:59:59.999
         */
        final Calendar today = Calendar.getInstance();
        calendarToFormat(today, true);

        final Calendar dayEnd = Calendar.getInstance();
        dayEnd.setTimeInMillis(mSettings.getFilterSelectedDate());
        calendarToFormat(dayEnd, true);

        final Calendar dayStart = Calendar.getInstance();
        dayStart.setTimeInMillis(dayEnd.getTimeInMillis());
        calendarToFormat(dayStart, false);

        final boolean filterIsToday = today.getTimeInMillis() == dayEnd.getTimeInMillis();
        /*
         * 1. if "show done tasks" filter is activated or if "hide done tasks" filter is activated and current task is
         * not done task 2. if current filter is "Today" and task begin term not equals to null and less or equals to
         * end of current date and task performer email equals to current user email or if current filter is "Inbox" and
         * task begin term equals to null and task performer email equals to current user email and task project UUId
         * equals to null and task categories equal to null or if current filter is "I assigned" and task performer
         * email equals to selected email or if current filter is "I am assigned" or if current filter is "Projects" and
         * task project UUID equals to selected project UUID or if current filter is "Categories" and task categories
         * UUIDs contains selected category or if we select task from subtasks list
         */

        final int taskMode = mApp.getSettings().getTaskMode();
        final boolean termBeginIsNull = task.getTermBegin() == null;
        final boolean isPerformer = mApp.getSettings().getUserName().equals(task.getPerformer());
        final boolean isToday = taskMode == 0;

        if (hasTasks
                || (!mApp.getSettings().isMakeTaskHide() || (mApp.getSettings().isMakeTaskHide() && !mDbHelper.hideTask(task, mApp.getSettings().getUserName())))) {

            if (hasTasks
                    || (isToday && !filterIsToday && !termBeginIsNull && task.getTermBegin().getTime() >= dayStart.getTimeInMillis() && task.getTermBegin()
                            .getTime() <= dayEnd.getTimeInMillis())
                    || (isToday && filterIsToday && task.getTermCustomerBegin() != null && task.getTermCustomerBegin().getTime() <= today.getTimeInMillis() && isPerformer)
                    || (isToday && filterIsToday && !termBeginIsNull && task.getTermBegin().getTime() <= today.getTimeInMillis() && isPerformer)
                    || (taskMode == 1 && termBeginIsNull && isPerformer && task.getProjectUid() == null && task.getCategories() == null)
                    || (taskMode == 2 && mApp.getSettings().getChooseEmail().getName().equals(task.getPerformer()))
                    || (taskMode == 3 && mApp.getSettings().getChooseProject().getId().equals(task.getProjectUid()))
                    || (taskMode == 4 && isContainsCategory(TaskEditFragment.getInstance().getCategories(), mSettings.getChooseCategory().getId()))
                    || taskMode == 5 || getFragmentManager().getBackStackEntryCount() > 1) {
                if (mTaskNew) {
                    mListener.onTaskAdded(task);

                } else {
                    mListener.onTaskChanged(task);
                }

            } else {
                mListener.onTaskDeleted(task, false, false);
            }

        } else {
            mListener.onTaskDeleted(task, false, false);
        }

//        LTCalendarView.clearCalendarData(mApp);
        new CreateOrRemoveTaskCategories(mApp, TaskEditFragment.getInstance().getCategories(), task, true).run();
        new SaveTask(mApp, task).execute(null);
        // Emp.updateTaskEmpSort(mApp, task);

        if (mTaskNew) {
            onNewTask(task);
        }

        if (mSettings.getUserName().equals(task.getCustomer())
                && (task.getStatusType() == TaskStatus.COMPLETED || task.getStatusType() == TaskStatus.CANCELLED)) {
            final TaskSeriesCalculator taskSeriesCalculator = new TaskSeriesCalculator(mApp, task);
            taskSeriesCalculator.createNextSeriesTask();

            final Task newTask = taskSeriesCalculator.getNewTask();
            if (newTask != null) {
                if (mSettings.getTaskMode() == TaskMode.TODAY) {
                    if (newTask.getTermBegin().getTime() <= dayEnd.getTimeInMillis()) {
                        mListener.onTaskAdded(newTask);
                    }
                } else {
                    mListener.onTaskAdded(newTask);
                }
            }
        }

        TaskEditFragment.setInstanceNull();

        mHandler.post(mEndRun);
    }

    private void onNewTask(Task task) {
//        LTCalendarView.clearCalendarData(mApp);
        mDbHelper.recalculateVerticalTaskSubtasks(mApp, mSettings.getUserName(), task);

        {
            final Fragment targetFragment = sInstance == null ? getTargetFragment() : sInstance.getTargetFragment();
            if (targetFragment != null && targetFragment instanceof SubtasksListFragment) {
                SubtasksListFragment.sIncreaseByParentTasksCount = 1;
            }
        }
        {
            boolean isPerformerExists = false;
            final List<String> emails = mDbHelper.getAllEmails();
            for (String email : emails) {
                if (email != null && email.equals(task.getPerformer())) {
                    isPerformerExists = true;
                    break;
                }
            }

            if (!isPerformerExists) {
                final Intent intent = new Intent();
                intent.setAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
                LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
            }
        }
        try {
            mDbHelper.updateNumberTaskAfterDelete_Add(task, true, false, false);

        } catch (AbstractDataRequestException e) {
            Utils.toLog(e);

        } catch (SQLException e) {
            Utils.toLog(e);
        }
    }

    private Runnable mEndRun = new Runnable() {
        @Override
        public void run() {
            try {
                if (sInstance != null) {
                    sInstance.setBlockForSave(false);
                    sInstance.getFragmentManager().popBackStack();
                    sInstance = null;

                } else {
                    setBlockForSave(false);
                    getFragmentManager().popBackStack();
                }
            } catch (Exception e) {
                sInstance = null;
                Utils.toLog(e);
            }
        }
    };

    public static void saveTaskFiles(Context context, String taskUid) {
        final ContentResolver cr = context.getContentResolver();
        context = null;

        /**
         * files with weak=false AND delete=true OR weak=true AND delete=true must be set as weak=true
         */
        final Cursor wd0111 = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionFieldTaskUidAndDeleteObject(taskUid, true), null, null);
        if (wd0111.getCount() > 0) {
            final ContentValues cv = new ContentValues();
            cv.put(TaskFileContract.WEAK_LINK, 1);

            final int columUID = wd0111.getColumnIndex(TaskFileContract.FIELD_FILEUID);
            for (wd0111.moveToFirst(); !wd0111.isAfterLast(); wd0111.moveToNext()) {
                cr.update(TaskFileContract.CONTENT_URI, cv, TaskFileContract.selectionFieldFileUid(wd0111.getString(columUID)), null);
            }
        }
        wd0111.close();

        /**
         * files with weak=true AND delete=false must be set as weak=false
         */
        final Cursor wd10 = cr.query(TaskFileContract.CONTENT_URI, null,
                TaskFileContract.selectionFieldTaskUidAndDeleteObjectAndWeakLink(taskUid, false, true), null, null);
        if (wd10.getCount() > 0) {
            final ContentValues cv = new ContentValues();
            cv.put(TaskFileContract.WEAK_LINK, 0);

            final int columUID = wd10.getColumnIndex(TaskFileContract.FIELD_FILEUID);
            for (wd10.moveToFirst(); !wd10.isAfterLast(); wd10.moveToNext()) {
                cr.update(TaskFileContract.CONTENT_URI, cv, TaskFileContract.selectionFieldFileUid(wd10.getString(columUID)), null);
            }
        }
        wd10.close();
    }

    private void calendarToFormat(Calendar c, boolean toDayEnd) {
        if (toDayEnd) {
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MILLISECOND, 999);
        } else {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
    }

    /**
     * method that defining if task completed or not depending on task status and task customer
     * 
     * @param status
     *            - task status as Status instance
     * @param customer
     *            - task customer
     * @return true - if task is completed false - if task is not completed
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private boolean isCompleted(TaskStatus status, String customer) {
        return (status.getCode() == TaskStatus.COMPLETED.getCode() || status.getCode() == TaskStatus.CANCELLED.getCode())
                || (!customer.equals(mApp.getSettings().getUserName()) && (status.getCode() == TaskStatus.READY.getCode() || status.getCode() == TaskStatus.REJECTED
                        .getCode()));
    }

    /**
     * Checks if particular category UUID contains in set of categories.
     * 
     * @param categories
     *            - set of categories that will be assigned to task
     * @param categoryUUID
     *            - particular category UUID
     * @return: true - if set contains particular category UUID, false - otherwise
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    private boolean isContainsCategory(Set<Category> categories, UUID categoryUUID) {
        if (categories == null || categories.isEmpty()) {
            return false;
        }

        for (Category category : categories) {
            if (category.getId().equals(categoryUUID)) {
                return true;
            }
        }

        return false;
    }

    // /**
    // * return current fragment from fragment state pager adapter
    // *
    // * @author Vadim Oleynik (vadim.welldone@gmail.com)
    // */
    // public Fragment getCurrentFragment() {
    // return mAdapter.getItem(mViewPager.getCurrentItem());
    // }

    @Override
    public boolean showTitleBar() {
        return false;
    }

    private void setName() {

    }

    private void setBackground() {
        if (mApp.getSettings().isThemeDark()) {
            mTabStrip.setTextColor(Color.WHITE);
            mTabStrip.setTabIndicatorColor(Color.WHITE);
            getView().setBackgroundColor(Color.BLACK);
        } else {
            getView().setBackgroundColor(Color.WHITE);
            mTabStrip.setTabIndicatorColor(Color.BLACK);
            mTabStrip.setTextColor(Color.BLACK);
        }

        mApp.setTheme(getActivity());
    }

    @Override
    public boolean onBackClick() {
        resetTaskFileWithoutSaving();
        return false;
    }

    public void setBlockForSave(boolean block) {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(block ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_USER);
        }
        setBlock(block);
    }
}