package com.ashberrysoft.leadertask.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.BaseSlidingActivity;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.adapters.TaskAdapter;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.cache.CachedEmployee;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.data_providers.BaseDatabaseRequest;
import com.ashberrysoft.leadertask.data_providers.GetIncomeTasks;
import com.ashberrysoft.leadertask.data_providers.GetTasksByCategory;
import com.ashberrysoft.leadertask.data_providers.GetTasksByDate;
import com.ashberrysoft.leadertask.data_providers.GetTasksByEmail;
import com.ashberrysoft.leadertask.data_providers.GetTasksByProject;
import com.ashberrysoft.leadertask.data_providers.GetTasksNotifications;
import com.ashberrysoft.leadertask.dialogs.CalendarDialog;
import com.ashberrysoft.leadertask.dialogs.SetPerformerDialog;
import com.ashberrysoft.leadertask.dialogs.SetTermDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.Email.OrderInstruct;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.SimpleNotifications;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView.OnCalendarDateSelectedListener;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;


/**
 * Фрагмент для отображения списка задач
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
@Deprecated
public class TasksListFragment extends BaseTasksListFragment implements //
        OnItemClickListener, OnItemLongClickListener, OnCalendarDateSelectedListener, LoaderCallbacks<Cursor> {

    @Override
    public void onRefresh() {

    }

    public interface OnCalendarControlListener {
        public void setControlDate(boolean setData, Date date);
    }

    public static final String IS_HAS_PARENT = "is_has_parent";
    public static final String IS_FOR_NOTIFICATION = "is_for_notification";
    public static final String DATE = "date";
    private static final String FIRST_VISIBLE_POSITION = "first_visible_position";
    private static final String FIRST_VISIBLE_ITEM_OFFSET = "first_visible_item_offset";

    // VIEW's
    private Menu mMenu;
    private TextView mCustomTitle;
    private View mTitleImage;
    private ListView mListView;

    // VALUE's
    /**
     * режим отображения задач: 0 - сегодня; 1 - входящие; 2 - поручено; 3 - проекты и доступные мне; 4 - категории
     */
    private int mTaskMode;
    private Date mDate;
    public long mStart;
    public int mFirstVisiblePosition;// first visible position of ListView
    public int mOffset;// first visible item offset from parent top (in pixels)
    private boolean mIsHasParent;
    public static int sPosition;// adapter item position context menu will be
                                // displayed for
    private static List<Task> sData;// current tasks list
    private static boolean sIsForNotification;
    private CachedEmployee mCachedEmployee;

    // ADAPTER
    private TaskAdapter mAdapter;

    // LISTENER
    private OnCalendarControlListener mListener;

    public static TasksListFragment newInstance(boolean isHasParent, boolean isForNotification) {
        final Bundle b = new Bundle();
        b.putBoolean(IS_HAS_PARENT, isHasParent);
        b.putBoolean(IS_FOR_NOTIFICATION, isForNotification);

        final TasksListFragment f = new TasksListFragment();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mIsHasParent = getArguments().getBoolean(IS_HAS_PARENT);
        sIsForNotification = getArguments().getBoolean(IS_FOR_NOTIFICATION);
        mDate = (Date) getArguments().getSerializable(DATE);

        if (mDate == null) {
            if (mSettings.getFilterSelectedDate() != 0) {
                mDate = new Date(mSettings.getFilterSelectedDate());
            } else {
                mDate = new Date(System.currentTimeMillis() + Calendar.getInstance().getTimeZone().getRawOffset()
                        + Calendar.getInstance().getTimeZone().getDSTSavings());
            }
        }
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        if (b != null) {
            mFirstVisiblePosition = b.getInt(FIRST_VISIBLE_POSITION, 0);
            mOffset = b.getInt(FIRST_VISIBLE_ITEM_OFFSET, 0);
        }

        mAdapter = new TaskAdapter(getActivity(), sIsForNotification, this);

        // get fragment arguments from bundle
        if (b != null) {
            if (b.containsKey(IS_HAS_PARENT)) {
                mIsHasParent = b.getBoolean(IS_HAS_PARENT);
            }
            if (b.containsKey(IS_FOR_NOTIFICATION)) {
                sIsForNotification = b.getBoolean(IS_FOR_NOTIFICATION);
            }
            if (b.containsKey(DATE)) {
                mDate = (Date) b.getSerializable(DATE);
            }
        }

        if (mDate == null) {
            if (mSettings.getFilterSelectedDate() != 0) {
                mDate = new Date(mSettings.getFilterSelectedDate());
            } else {
                mDate = new Date(System.currentTimeMillis() + Calendar.getInstance().getTimeZone().getRawOffset()
                        + Calendar.getInstance().getTimeZone().getDSTSavings());
            }
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mListView.setAdapter(mAdapter);

        registerForContextMenu(mListView);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        // set custom title for Action Bar
        setCustomTitle();

        mCachedEmployee = CachedEmployee.getInstance(getActivity());

        return null;
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        getLoaderManager().restartLoader(R.id.lm_sync_info, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();

        final Fragment f = getFragmentManager().findFragmentById(HomeActivity.SLIDING_CONTAINER);
        if (f != null && f instanceof SlidingMenuFragment) {
            mListener = (OnCalendarControlListener) f;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mConnectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        final IntentFilter filter = new IntentFilter(ServiceConstants.ACTION_TASKS_UPDATE);
        filter.addAction(ServiceConstants.ACTION_TASKS_TODAY);
        filter.addAction(ServiceConstants.ACTION_TASKS_INPUT);
        filter.addAction(ServiceConstants.ACTION_TASK_INSTRUCT);
        filter.addAction(ServiceConstants.ACTION_TASK_PROJECT);
        filter.addAction(ServiceConstants.ACTION_TASK_CATEGORY);
        filter.addAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);

        // get current task mode
        if (sIsForNotification) {
            mApp.getSettings().setTaskMode(-1);
            mTaskMode = -1;
        } else {
            mTaskMode = mApp.getSettings().getTaskMode();
        }

        // update tasks list and action bar content
        if (!isDataEmpty()) {
            mAdapter.setData(getData());
            if (mTaskMode == 0 || mTaskMode == -1) {
                // final String titleDate = Utils.getDayOfDate(mApp, mDate);
                // mCustomTitle.setText((titleDate != null ? titleDate + ": " : "") + Utils.amputationMonth(mDate));
                mCustomTitle.setText(TimeHelper.getInstance().getCuteDateTitle(mDate));

                if (mTaskMode == -1) {
                    setBlockingProcess(true, this);

                    setTaskForDate();

                    if (!mIsHasParent) {
                        mCustomTitle.setText(R.string.notifications);
                    }
                } else {
                    mTitleImage.setVisibility(View.VISIBLE);
                }

                if (!mIsHasParent) {
                    mCustomTitle.setText(R.string.notifications);
                }
            } else {
                if (mTaskMode == 1) {
                    setLogo(R.drawable.inbox, getString(R.string.task_inbox));
                }
                if (mTaskMode == 2 || mTaskMode == 5) {
                    if (mApp.getSettings().getChooseEmail().getOrderInstruct().equals(OrderInstruct.INSTRUCTI)) {
                    } else {
                    }
                    mCustomTitle.setText(mCachedEmployee.getName(mApp.getSettings().getChooseEmail().getName()));
                    mTitleImage.setVisibility(View.GONE);
                }
                if (mTaskMode == 3) {
                    setLogo(R.drawable.project, mApp.getSettings().getChooseProject().getName());
                }
                if (mTaskMode == 4) {
                    setLogo(R.drawable.category_white_big, mApp.getSettings().getChooseCategory().getName());
                }
                if (!mIsHasParent) {
                    mCustomTitle.setText(R.string.notifications);
                    mTitleImage.setVisibility(View.GONE);
                }
            }
        }

        else {
            setBlockingProcess(true, this);
            if (mTaskMode == 0 || mTaskMode == -1) {

                setTaskForDate();

                if (!mIsHasParent) {
                    mCustomTitle.setText(R.string.notifications);
                }
            } else {
                if (mTaskMode == 1) {
                    setTaskInput(false);
                }
                if (mTaskMode == 2 || mTaskMode == 5) {
                    setTaskEmail(false, mApp.getSettings().getChooseEmail());
                }
                if (mTaskMode == 3) {
                    setTaskProject(false, mApp.getSettings().getChooseProject());
                }
                if (mTaskMode == 4) {
                    try {
                        setTaskCategory(false, mApp.getSettings().getChooseCategory());
                    } catch (AbstractDataRequestException e) {
                        e.printStackTrace();
                    }
                }
                if (!mIsHasParent) {
                    mCustomTitle.setText(R.string.notifications);
                    mTitleImage.setVisibility(View.GONE);
                }
                mAdapter.clear();

                new UpdateTask(false, TaskAction.NONE, null).execute();
            }
        }

        if (SubtasksListFragment.sIncreaseByParentTasksCount == -1) {
            Task task = null;
            try {
                task = getData().get(sPosition);
            } catch (NullPointerException e) {
                Utils.toLog(e);
            }

            if (task != null) {
                task.setSubTasksCount(task.getSubTasksCount() - 1);
                if (!mDbHelper.hideTask(SubtasksListFragment.sCurrentTask, mSettings.getUserName())) {
                    task.setSubTasksCountNotMade(task.getSubTasksCountNotMade() - 1);
                    if (!SubtasksListFragment.sCurrentTask.isReaded()) {
                        task.setSubTasksSizeNotMadeAndNotRead(task.getSubTasksSizeNotMadeAndNotRead() - 1);
                        task.setSubTasksCountNotRead(task.getSubTasksCountNotRead() - 1);
                    }
                } else if (!SubtasksListFragment.sCurrentTask.isReaded()) {
                    task.setSubTasksSizeNotMadeAndNotRead(task.getSubTasksSizeNotMadeAndNotRead() - 1);
                    task.setSubTasksCountNotRead(task.getSubTasksCountNotRead() - 1);
                }

                getData().set(sPosition, task);
            }
            SubtasksListFragment.sIncreaseByParentTasksCount = 0;
        }
        // clear list of subtasks lists
        SubtasksListFragment.clearData();
        // set flag that screen is not minimized
        SubtasksListFragment.sIsScreenMinimized = false;
        // update adapter in order to apply new theme
        mAdapter.notifyDataSetChanged();
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelectionFromTop(mFirstVisiblePosition, mOffset);
            }
        });

        ((BaseSlidingActivity) getActivity()).enableSlidingMenu();

        if (mApp.getTextPlainSend() != null) {
            startFragment(TabViewFragment.newInstance(mSettings.getUserName(), mApp.getTextPlainSend()));
            mApp.setTextPlainSend(null);
        }

        if (mSettings.isRunSyncAfterVersionUpgrade()) {
            mSettings.setRunSyncAfterVersionUpgrade(false);

        }

//        if (mSettings.isListFragmentNotifyDataSetChanges()) {
//            mSettings.setListFragmentNotifyDataSetChanges(false);
//            onListUpdate(true);
//        }

        if (mAddTask) {
            mAddTask = false;
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // stopRefresh();
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_HAS_PARENT, mIsHasParent);
        outState.putBoolean(IS_FOR_NOTIFICATION, sIsForNotification);
        outState.putSerializable(DATE, mDate);
        if (mListView != null) {
            outState.putInt(FIRST_VISIBLE_POSITION, mListView.getFirstVisiblePosition());
            outState.putInt(FIRST_VISIBLE_ITEM_OFFSET, mListView.getChildAt(0) == null ? 0 : mListView.getChildAt(0).getTop());
        } else {
            outState.putInt(FIRST_VISIBLE_POSITION, mFirstVisiblePosition);
            outState.putInt(FIRST_VISIBLE_ITEM_OFFSET, mOffset);
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mConnectionChangeReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        mFirstVisiblePosition = mListView.getFirstVisiblePosition();
        mOffset = mListView.getChildAt(0) == null ? 0 : mListView.getChildAt(0).getTop();
        super.onPause();
    }

    @Override
    public void onStop() {
        mAdapter.setData(new ArrayList<Task>(0));
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
         * if while destroying this Fragment we located in the notification screen and didn't click on any of filters
         * then change mTaskMode to "0" in order to properly process this Fragment while next loading (especially, in
         * order to have full context menu and correct Action Bar icon)
         */
        if (mTaskMode == -1)
            mApp.getSettings().setTaskMode(0);
    }

    /**
     * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
     */
    @SuppressLint("InflateParams")
    private void setCustomTitle() {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_title, null);
        mCustomTitle = (TextView) v.findViewById(R.id.custom_title);
        mTitleImage = v.findViewById(R.id.triangle);
        v.setOnClickListener(mOnTitleActionBarClickListener);

    }

    private void setTaskForDate() {
        // final String titleDate = Utils.getDayOfDate(mApp, mDate);
        // mCustomTitle.setText((titleDate != null ? titleDate + ": " : SharedStrings.EMPTY)
        // + Utils.amputationMonth(mDate));
        mCustomTitle.setText(TimeHelper.getInstance().getCuteDateTitle(mDate));

        if (mTaskMode == -1) {
            mTitleImage.setVisibility(View.GONE);
        } else {
            mTitleImage.setVisibility(View.VISIBLE);
        }

        getTask(false);
        mAdapter.clear();
        new UpdateTask(false, TaskAction.NONE, null).execute();
    }

    private void getTask(boolean logo) {
        if (logo) {
            if (mIsHasParent) {
                setAdapterDataWithDelay(new GetTasksByDate(getActivity(), mDate, mApp.getSettings().getUserName(), !mSettings.isMakeTaskHide()));
            } else {
                setAdapterDataWithDelay(new GetTasksNotifications(getActivity(), mSettings.getUserName(), !mSettings.isMakeTaskHide()));
            }
        } else {
            if (mTaskMode != -1) {
                mHandler.post(mSetLogoRun);
            }
            updateMenuState(mMenu, sIsForNotification);
            // if (mTaskMode != -1)
            // mActionBar.setLogo(R.drawable.calendar_today);
        }
    }

    private Runnable mSetLogoRun = new Runnable() {
        @Override
        public void run() {
        }
    };

    private void setTaskInput(boolean logo) {
        mHandler.post(mCalendarRunnable);

        if (!logo) {
            setLogo(R.drawable.inbox, getString(R.string.task_inbox));

        } else {
            setAdapterDataWithDelay(new GetIncomeTasks(getActivity(), mSettings.getUserName(), !mSettings.isMakeTaskHide()));
        }
    }

    private Runnable mCalendarRunnable = new Runnable() {
        @Override
        public void run() {
            setControlDate(true, null);
        }
    };

    /**
     * Disable menu (except show/hide finished tasks.
     * 
     * @author V.Shcryabets<vshcryabets@gmail.com>
     * @param menu
     */
    private void disableMenu(Menu menu) {
        //menu.findItem(R.id.add_task).setVisible(false);
        //menu.findItem(R.id.add_task).setEnabled(false);
        // menu.findItem(R.id.synchronize).setVisible(false);
        // menu.findItem(R.id.synchronize).setEnabled(false);
        // menu.findItem(R.id.settings).setVisible(false);
        // menu.findItem(R.id.settings).setEnabled(false);
        menu.findItem(R.id.show_hide_make_task).setVisible(true);
        menu.findItem(R.id.show_hide_make_task).setEnabled(true);
    }

    /**
     * Enable all menu items.
     * 
     * @author V.Shcryabets<vshcryabets@gmail.com>
     * @param menu
     */
    private void enableMenu(Menu menu) {
        if (mMenu == null) {
            return;
        }

        // final com.actionbarsherlock.view.MenuItem sync =
        // mMenu.findItem(R.id.synchronize);
        // if (sync != null) {
        // sync.setVisible(true);
        // }

        final MenuItem makeTask = mMenu.findItem(R.id.show_hide_make_task);
        if (makeTask != null) {
            makeTask.setVisible(true);
        }
    }

    private void setTaskEmail(boolean logo, Email email) {
        mHandler.post(mCalendarRunnable);

        if (email.getName() != null) {
            if (!logo) {
                if (email.getOrderInstruct().equals(OrderInstruct.INSTRUCTI)) {

                } else {

                }
                mCustomTitle.setText(mCachedEmployee.getName(email.getName()));
                mTitleImage.setVisibility(View.GONE);
                if (!sIsForNotification) {
                    if (mMenu != null) {
                        enableMenu(mMenu);
                    }
                } else {
                    disableMenu(mMenu);
                }
            } else {
                setAdapterDataWithDelay(new GetTasksByEmail(getActivity(), email, mSettings.getUserName(), !mSettings.isMakeTaskHide()));
            }
        }
    }

    private void setTaskProject(boolean logo, Project project) {
        mHandler.post(mCalendarRunnable);

        if (project.getId() != null) {
            if (!logo) {
                setLogo(R.drawable.project, project.getName());
            } else {
                setAdapterDataWithDelay(new GetTasksByProject(getActivity(), project, mSettings.getUserName(), !mSettings.isMakeTaskHide()));
            }
        }
    }

    private void setTaskCategory(boolean logo, Category category) throws AbstractDataRequestException {
        mHandler.post(mCalendarRunnable);

        if (category.getId() != null) {
            if (!logo) {
                setLogo(R.drawable.category_white_big, category.getName());
            } else {
                setAdapterDataWithDelay(new GetTasksByCategory(getActivity(), category, mSettings.getUserName(), !mSettings.isMakeTaskHide()));
            }
        }
    }

    private void setLogo(int logo, String title) {
        mCustomTitle.setText(title);
        mTitleImage.setVisibility(View.GONE);
        updateMenuState(mMenu, sIsForNotification);
    }

    private void updateMenuState(Menu menu, boolean isForNotification) {
        if (menu != null) {
            if (!isForNotification) {
                enableMenu(menu);
            } else {
                disableMenu(menu);
            }
        }
    }

    private void setAdapterDataWithDelay(BaseDatabaseRequest<ArrayList<Task>> request) {
        try {
            sData = request.execute(null).getResult();
            final long end = System.currentTimeMillis() - mStart;


            if (getData() != null) {
                mAdapter.setData(getData());
            }
        } catch (AbstractDataRequestException e1) {
            Utils.toLog(e1);
        }
    }

    @Override
    public boolean showTitleBar() {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (mTaskMode != -1) {
            sPosition = arg2;
            showSubtasks((Task) mAdapter.getItem(arg2), false);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.task_item_menu, menu);

        final MenuItem itemSbtask = menu.findItem(R.id.menu_assign);
        final MenuItem itemAssign = menu.findItem(R.id.menu_assign);
        final MenuItem itemDelete = menu.findItem(R.id.menu_dell);
        final MenuItem itemProper = menu.findItem(R.id.menu_dell);

        if (mTaskMode == -1) {
            showMenuItem(itemSbtask, false);
            showMenuItem(itemAssign, false);
            showMenuItem(itemDelete, false);
            showMenuItem(itemProper, false);
        } else {
            showMenuItem(itemSbtask, true);
            showMenuItem(itemProper, true);
            // display "assign" menu item
            // mPosition = ((AdapterContextMenuInfo) menuInfo).position;
            String customer = null;
            try {
                customer = ((Task) mAdapter.getItem(sPosition)).getCustomer();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            final boolean show = mSettings.getUserName().equals(customer);
            showMenuItem(itemAssign, show);
            showMenuItem(itemDelete, show);
        }
    }

    private void showMenuItem(MenuItem item, boolean show) {
        item.setVisible(show);
        // item.setEnabled(show);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        try {
            sPosition = info.position;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        switch (item.getItemId()) {
        case R.id.menu_properties:
            showTabViewFragmant((Task) mAdapter.getItem(sPosition));
            return true;

        case R.id.menu_subtasks:
            showSubtasks((Task) mAdapter.getItem(sPosition), true);
            return true;

        case R.id.menu_term:
            SetTermDialog.newInstance(this, (Task) mAdapter.getItem(sPosition)).showDialog(getFragmentManager());
            return true;

        case R.id.menu_assign:
            final String performer = ((Task) mAdapter.getItem(sPosition)).getPerformer();
            SetPerformerDialog.newInstance(this, performer).showDialog(getFragmentManager());
            return true;

        case R.id.menu_dell:
            showDeleteTaskDialog((Task) mAdapter.getItem(sPosition));
            return true;

        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        inflater.inflate(R.menu.actionbar_menu_task_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mApp.getSettings().isMakeTaskHide())
            mMenu.findItem(R.id.show_hide_make_task).setTitle(R.string.menu_show_make_task);
        else
            mMenu.findItem(R.id.show_hide_make_task).setTitle(R.string.menu_hide_make_task);
        if (sIsForNotification) {
            disableMenu(mMenu);
        } else {
            enableMenu(mMenu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case android.R.id.home:
            // toggle sliding menu
            break;

        case R.id.new_activity:
            break;

        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewTask(Date date, Email mail, UUID projectUUID, Category category, UUID parentUUID) {
        if (mTaskMode == 0) {
            date = mDate;
        }
        if (mTaskMode == 2) {
            if (mApp.getSettings().getChooseEmail().getOrderInstruct().equals(OrderInstruct.INSTRUCTI)) {
                mail = mApp.getSettings().getChooseEmail();
            }
        }
        if (mTaskMode == 3) {
            projectUUID = mApp.getSettings().getChooseProject().getId();
        }
        if (mTaskMode == 4) {
            category = mApp.getSettings().getChooseCategory();
        }

        final String performer = mail != null ? mail.getName() : mSettings.getUserName();
        startFragment(TabViewFragment.newInstance(this, null, mSettings.getUserName(), performer, date, parentUUID, projectUUID, category));
        // startFragment(AddTaskFragment.newInstance(this, date, mail, projectUUID, category, parentUUID));
    }

    @Override
    public void onFragmentResult(Object object, int type) {
        super.onFragmentResult(object, type);

        switch (type) {
        case SetTermDialog.REQUEST_CODE:
            setTermToTask(object);
            break;

        case SetPerformerDialog.REQUEST_CODE:
            setPerformerToTask(object);
            break;

        case CalendarDialog.REQUEST_CODE:
            final Date date = (Date) object;
            onDateSelected(date);
            setControlDate(true, date);
            break;

        default:
            break;
        }
    }

    @Override
    public void onDateSelected(Date date) {
        mTaskMode = 0;
        mSettings.setTaskMode(mTaskMode);

        setBlockingProcess(true, this);
        mDate = date;
        setTaskForDate();
        // save filter selected date
        mApp.getSettings().setFilterSelectedDate(mDate.getTime());
    }

    private void setControlDate(boolean setData, Date date) {
        mSettings.setFilterSelectedDate(date != null ? date.getTime() : 0);

        if (mListener != null) {
            mListener.setControlDate(setData, date);
        }
    }

    private void updateAdaperAtUI(final boolean stopBlock) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                if (stopBlock) {
                    setBlock(false);
                }
            }
        });
    }

    private void setTermToTask(final Object obj) {
        setBlock(true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                final Task task = (Task) mAdapter.getItem(sPosition);
                if (task == null) {
                    return;
                }

                task.setTermBegin(((Task) obj).getTermBegin());
                task.setTermEnd(((Task) obj).getTermEnd());
                task.setTermCustomerBegin(((Task) obj).getTermCustomerBegin());
                task.setTermCustomerEnd(((Task) obj).getTermCustomerEnd());

                try {
                    mDbHelper.updateTask(task, false, sIsForNotification, false);
                } catch (AbstractDataRequestException e) {
                    e.printStackTrace();
                }

                /*
                 * current date with set time to 23:59:59.999
                 */
                final Calendar todayEnd = Calendar.getInstance();
                todayEnd.set(Calendar.HOUR_OF_DAY, 23);
                todayEnd.set(Calendar.MINUTE, 59);
                todayEnd.set(Calendar.SECOND, 59);
                todayEnd.set(Calendar.MILLISECOND, 999);
                /*
                 * today with set time to 00:00:00.000
                 */
                final Calendar todayBegin = Calendar.getInstance();
                todayBegin.set(Calendar.HOUR_OF_DAY, 0);
                todayBegin.set(Calendar.MINUTE, 0);
                todayBegin.set(Calendar.SECOND, 0);
                todayBegin.set(Calendar.MILLISECOND, 0);
                /*
                 * if current filter is "Today" and task begin term not equals to null and lower or equals to end of
                 * current date or if current filter is "Inbox" and task begin term equals to null or if current filter
                 * is one of "I assigned", "I am assigned", "Projects", "Categories", then update tasks list
                 */

                final boolean currentIsCustomer = mApp.getSettings().getUserName().equals(task.getCustomer());
                final boolean currentIsPerformer = mApp.getSettings().getUserName().equals(task.getPerformer());

                final boolean isTermBeginNull = task.getTermBegin() == null;
                final int rawOffset = Calendar.getInstance().getTimeZone().getRawOffset();

                final boolean isTimeInRange = !isTermBeginNull && (task.getTermBegin().getTime() - rawOffset) >= todayBegin.getTimeInMillis()
                        && (task.getTermBegin().getTime() - rawOffset) <= System.currentTimeMillis();

                final boolean isToday = mApp.getSettings().getTaskMode() == 0 && !isTermBeginNull
                        && task.getTermBegin().getTime() <= todayEnd.getTimeInMillis();

                // TODO Bug #3516 was added this 5 lines
                if (mApp.getSettings().getTaskMode() == 0 && currentIsCustomer && !currentIsPerformer && task.getTermCustomerEnd() != null
                        && (task.getTermCustomerEnd().getTime() - rawOffset) <= todayEnd.getTimeInMillis()) {
                    getData().remove(task);
                    mAdapter.removeItem(task);

                } else {
                    if ((mApp.getSettings().getTaskMode() == -1 && (currentIsCustomer || currentIsPerformer) && isTimeInRange) || isToday
                            || (mApp.getSettings().getTaskMode() == 1 && isTermBeginNull) || mApp.getSettings().getTaskMode() == 2
                            || mApp.getSettings().getTaskMode() == 3 || mApp.getSettings().getTaskMode() == 4 || mApp.getSettings().getTaskMode() == 5) {
                        try {
                            setTaskToData(sPosition, task); // TODO
                        } catch (IndexOutOfBoundsException e) {
                            Utils.toLog(e);
                        }
                        updateAdaperAtUI(false);
                    } else {
                        getData().remove(task);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.removeItem(task);
                            }
                        });
                    }
                }

                // update notifications
                // new
                // ProcessNotifications(getActivity()).updateNotification(task);
                // TODO
                SimpleNotifications.getInstance(mApp).updateOldSimpleNotify(task);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setBlock(false);
                    }
                });
            }
        }).start();
    }

    private void setPerformerToTask(final Object obj) {
        setBlock(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Task task = (Task) mAdapter.getItem(sPosition);
                String oldPerformer = task.getPerformer();
                task.setPerformer((String) obj);

                try {
                    mDbHelper.updateTask(task, false, false, false);
                } catch (AbstractDataRequestException e) {
                    e.printStackTrace();
                }

                /*
                 * if we assign task to/from particular user which email doesn't exists in database, then we need to
                 * update sliding menu hierarchy
                 */
                boolean isOldPerformerExists = false;
                boolean isNewPerformerExists = false;

                final List<String> allEmails = mDbHelper.getAllEmails();
                for (String email : allEmails) {
                    if (oldPerformer.equals(email)) {
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
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }

                /*
                 * if current filter is one of "Today", "Inbox" and task performer email equals to current user email or
                 * if current filter is "I assigned" and selected email equals to new task performer email or if current
                 * filter one of "Projects", "Categories", then update tasks list
                 */
                if (((mApp.getSettings().getTaskMode() == 0 || mApp.getSettings().getTaskMode() == 1) && mApp.getSettings().getUserName()
                        .equals(task.getPerformer()))
                        || (mApp.getSettings().getTaskMode() == 2 && mApp.getSettings().getChooseEmail().getName().equals(task.getPerformer()))
                        || mApp.getSettings().getTaskMode() == 3 || mApp.getSettings().getTaskMode() == 4) {
                    getData().set(sPosition, task);
                    updateAdaperAtUI(false);
                } else {
                    getData().remove(task);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.removeItem(task);
                        }
                    });
                }

                // update notifications
                // new
                // ProcessNotifications(getActivity()).updateNotification(task);
                // TODO
                SimpleNotifications.getInstance(mApp).updateOldSimpleNotify(task);
                // Emp.updateTaskEmpSort(mApp, task);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setBlock(false);
                    }
                });
            }
        }).start();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sIsForNotification = false;
            mIsHasParent = true;

            if ((intent.getAction().equals(ServiceConstants.ACTION_TASKS_UPDATE)) || (intent.getAction().equals(ServiceConstants.ACTION_NOTIFYDATASETCHANGED))) {
                onListUpdate(intent.getBooleanExtra(ServiceConstants.VALUE_BOOLEAN, true));
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASKS_INPUT)) {
                // clear tasks list
                clearData();
                mAdapter.clear();
                mTaskMode = 1;
                mApp.getSettings().setTaskMode(mTaskMode);
                setTaskInput(false);
                new UpdateTask(false, TaskAction.NONE, null).execute();
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASKS_TODAY)) {
                // TODO Bug #3460
                // clear tasks list
                clearData();
                mDate = new Date();// Utils.getCurrentTimeWithSavings()

                // Calendar date1 = Calendar.getInstance();
                // date1.setTimeZone(TimeZone.getTimeZone("GMT"));
                // date1.setTime(date);
                // Calendar date2 = Calendar.getInstance();
                // date2.setTimeZone(TimeZone.getTimeZone("GMT"));
                // date2.setTime(mDate);
                // if ((!Utils.compareDays(date1, date2)) || (mTaskMode != 0)) {
                mTaskMode = 0;
                mApp.getSettings().setTaskMode(mTaskMode);
                mAdapter.clear();
                // mDate = date;
                mCustomTitle.setText(TimeHelper.getInstance().getCuteDateTitle(mDate));
                mTitleImage.setVisibility(View.VISIBLE);
                getTask(false);

                setControlDate(true, mDate);

                new UpdateTask(false, TaskAction.NONE, null).execute();
                // }
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASK_INSTRUCT)) {
                // clear tasks list
                clearData();
                mAdapter.clear();
                final Email email = (Email) intent.getExtras().getSerializable(ServiceConstants.VALUE_EMAIL);
                mTaskMode = (email.getOrderInstruct() == OrderInstruct.INSTRUCTI ? TaskMode.ASSIGNED_BY_ME : TaskMode.ASSIGNED_TO_ME);
                mApp.getSettings().setTaskMode(mTaskMode);
                mApp.getSettings().setChooseEmail(email);
                setTaskEmail(false, email);
                new UpdateTask(false, TaskAction.NONE, null).execute();
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASK_PROJECT)) {
                // clear tasks list
                clearData();
                mAdapter.clear();
                final Project pr = (Project) intent.getExtras().getSerializable(ServiceConstants.VALUE_PROJECT);
                mTaskMode = 3;
                mApp.getSettings().setTaskMode(mTaskMode);
                mApp.getSettings().setChooseProject(pr);
                setTaskProject(false, pr);
                new UpdateTask(false, TaskAction.NONE, null).execute();
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASK_CATEGORY)) {
                // clear tasks list
                clearData();
                mAdapter.clear();
                final Category category = (Category) intent.getExtras().getSerializable(ServiceConstants.VALUE_CATEGORY);
                mTaskMode = 4;
                mApp.getSettings().setTaskMode(mTaskMode);
                mApp.getSettings().setChooseCategory(category);
                try {
                    setTaskCategory(false, category);
                } catch (AbstractDataRequestException e) {
                    Utils.toLog(e);
                }
                new UpdateTask(false, TaskAction.NONE, null).execute();
            }
        }
    };

    private void onListUpdate(boolean value) {
        mTaskMode = mSettings.getTaskMode();
        if (value) {
            mAdapter.clear();
            setBlockingProcess(true, this);
            switch (mTaskMode) {
            case -1:
                sIsForNotification = true;
                mIsHasParent = false;
                getTask(false);
                break;

            case 0:
                getTask(false);
                break;

            case 1:
                setTaskInput(false);
                break;

            case 2:
            case 5:
                setTaskEmail(false, mApp.getSettings().getChooseEmail());
                break;
            case 3:
                setTaskProject(false, mApp.getSettings().getChooseProject());
                break;
            case 4:
                try {
                    setTaskCategory(false, mApp.getSettings().getChooseCategory());
                } catch (AbstractDataRequestException e) {}
            default:
                break;
            }

            new UpdateTask(false, TaskAction.NONE, null).execute();
        }
    }

    /**
     * 
     * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
     * 
     */
    private BroadcastReceiver mConnectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {} else {
                mApp.cancelSynchronize();
                onListUpdateEnd(mApp);
            }
        }
    };

    private void showSubtasks(Task task, boolean isStillDisplay) {
        if (!isStillDisplay) {
            if ((mSettings.isMakeTaskHide() && task.getSubTasksCountNotMade() < 1) || (!mSettings.isMakeTaskHide() && task.getSubTasksCount() < 1)) {
                showTabViewFragmant(task);
                return;
            }
        }
        startFragment(SubtasksListFragment.newInstance(task, sPosition));
    }

    private void showTabViewFragmant(Task task) {
        if (task != null && !task.isReaded()) {
            task.setReaded(true);
            getData().set(sPosition, task);

        }
        startFragment(TabViewFragment.newInstance(null, task));
    }

    // remove task from the adapter
    public void removeTask(Task task) {
        // if (afterSaveTaskFromNotify()) {
        // return;
        // }

        if (getData() != null) {
            getData().remove(task);
            mAdapter.removeItem(task);
        } else {
            new UpdateTask(true, TaskAction.REMOVE, task).execute();
        }
    }

    // add task to the adapter
    public void addTask(Task task) {
        mAddTask = true;

        if (getData() != null) {
            getData().add(task);
            mAdapter.addItem(task);

        } else {
            new UpdateTask(true, TaskAction.ADD, task).execute();
        }
    }

    // change task in the adapter
    public void changeTask(Task task) {
        // if (afterSaveTaskFromNotify()) {
        // return;
        // }

        if (!isDataEmpty()) {
            try {
                getData().set(sPosition, task);
            } catch (IndexOutOfBoundsException e) {
                Utils.toLog(e);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            new UpdateTask(true, TaskAction.CHANGE, task).execute();
        }
    }

    // private boolean afterSaveTaskFromNotify() {
    // if (mSettings.isTaskFromNotify()) {
    // mSettings.setTaskFromNotify(false);
    // clearData();
    // return true;
    // }
    // return false;
    // }

    private OnClickListener mOnTitleActionBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTaskMode == -1) {
                getActivity().finish();
            } else if (mTaskMode == 0) {
                CalendarDialog.newInstance(TasksListFragment.this, mDate).showDialog(getFragmentManager());
            }
        }
    };

    private class UpdateTask extends AsyncTask<String, Void, Void> {

        private boolean mIsUpdateAdapterData;
        private TaskAction mAction;
        private Task mTask;

        public UpdateTask(boolean isUpdateAdapterData, TaskAction action, Task task) {
            super();

            mIsUpdateAdapterData = isUpdateAdapterData;
            mAction = action;
            mTask = task;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            setBlockingProcess(true, this);
            mStart = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(String... params) {
            switch (mTaskMode) {
            case 0:
            case -1:
                getTask(true);
                break;

            case 1:
                setTaskInput(true);
                break;

            case 2:
            case 5:
                setTaskEmail(true, mApp.getSettings().getChooseEmail());
                break;

            case 3:
                setTaskProject(true, mApp.getSettings().getChooseProject());
                break;

            case 4:
                try {
                    setTaskCategory(true, mApp.getSettings().getChooseCategory());
                } catch (AbstractDataRequestException e) {
                    Utils.toLog(e);
                }
            default:
                break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            setBlockingProcess(false, this);
            onListUpdateEnd(mApp);

            if (!mIsUpdateAdapterData) {
                mAdapter.notifyDataSetChanged();
            } else {
                if (mAction.equals(TaskAction.REMOVE)) {
                    getData().remove(mTask);
                    mAdapter.removeItem(mTask);
                    // } else if (mAction.equals(TaskAction.ADD)) {
                    // mData.add(mTask);
                    // mAdapter.addItem(mTask);
                } else if (mAction.equals(TaskAction.CHANGE)) {
                    getData().set(sPosition, mTask);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public static void onListUpdateEnd(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(SyncInfoContract.CONTENT_URI, null, null, null, null);
            cursor.moveToFirst();

            final SyncInfo si = new SyncInfo(cursor);
            switch (si.getSyncStatus()) {
            case ENDED:
            case ERROR:
                if (si.getListStatus() == SyncInfoErrorType.IN_PROGRESS) {
                    final ContentValues cv = new ContentValues();
                    cv.put(SyncInfoContract.LIST_STATUS, SyncInfoErrorType.ENDED.ordinal());
                    SyncInfo.updateSynchronizationInfo(context, cv);
                }
            default:
                break;
            }
        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        sPosition = position;
        getActivity().openContextMenu(mListView);

        return true;
    }

    @Override
    public void fillLostData(List<Calendar> lostData) {}

    @Override
    public void restartLoaderCallback() {}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        switch (id) {
        case R.id.lm_sync_info:
            return new CursorLoader(mApp, SyncInfoContract.CONTENT_URI, null, null, null, null);

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
        case R.id.lm_sync_info:
            onSyncStatusChange(cursor);
        default:
            break;
        }
    }

    private void onSyncStatusChange(Cursor cursor) {
        cursor.moveToFirst();
        final SyncInfo si = new SyncInfo(cursor);

        switch (si.getSyncStatus()) {
        case ENDED:
        case ERROR:
            onListStatusChange(si);
        default:
            break;
        }
    }

    private void onListStatusChange(SyncInfo si) {
        switch (si.getListStatus()) {
        case NONE:
            final ContentValues cv = new ContentValues();
            cv.put(SyncInfoContract.LIST_STATUS, SyncInfoErrorType.IN_PROGRESS.ordinal());
            SyncInfo.updateSynchronizationInfo(mApp, cv);

            onListUpdate(true);
        default:
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public static synchronized void clearData() {
        if (sData != null) {
            sData.clear();
        }
    }

    public static synchronized boolean isDataEmpty() {
        return sData == null || sData.isEmpty();
    }

    public static void setTaskToData(int position, Task task) {
        getData().set(position, task);
    }

    public static synchronized Task getData(int position) {
        if (sData != null && sData.size() > position) {
            return sData.get(position);
        }
        return null;
    }

    public static synchronized List<Task> getData() {
        return sData;
    }

    @Override
    protected void handleMessageNext(Message msg) {
        mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
    }
}