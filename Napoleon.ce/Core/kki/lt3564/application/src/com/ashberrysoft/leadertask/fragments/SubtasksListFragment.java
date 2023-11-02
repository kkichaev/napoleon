package com.ashberrysoft.leadertask.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.TaskAdapter;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.data_providers.GetSubTasks;
import com.ashberrysoft.leadertask.dialogs.SetPerformerDialog;
import com.ashberrysoft.leadertask.dialogs.SetTermDialog;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SimpleNotifications;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.BackListView;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;


/**
 * Фрагмент для отображения списка подзадач конкретной задачи
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
@Deprecated
public class SubtasksListFragment extends BaseTasksListFragment implements OnItemClickListener, LoaderCallbacks<Cursor> {

    private static final String FIRST_VISIBLE_POSITION = "first_visible_position";
    private static final String FIRST_VISIBLE_ITEM_OFFSET = "first_visible_item_offset";

    // VIEW's
    private BackListView mListView;

    // VALUE's
    private Task mParentTask;
    public int mFirstVisiblePosition;// first visible position of ListView
    public int mOffset;// first visible item offset from parent top (in pixels)
    private static List<List<Task>> sData;
    public static Task sCurrentTask;
    public static int sParentTaskPosition;
    public static int sPosition;// adapter item position context menu will be
                                // displayed for
    private static int sBackStackEntryCount = 0;
    private static int sOrientation = 0;
    private static boolean sIsAddOrEditScreenActivated;
    public static int sIncreaseByParentTasksCount;// value at which we must
                                                  // increase / decrease tasks
                                                  // count of parent
    private static boolean sIsScreenOff;
    public static boolean sIsScreenMinimized;

    // ADAPTER
    private TaskAdapter mAdapter;


    public static SubtasksListFragment newInstance(Task parentTask, int parentTaskPosition) {
        final Bundle b = new Bundle();
        b.putSerializable(IPCConstants.EXTRA_TASK, parentTask);
        b.putInt(IPCConstants.EXTRA_TASK_POSITION, parentTaskPosition);

        final SubtasksListFragment f = new SubtasksListFragment();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        if (b != null) {
            mFirstVisiblePosition = b.getInt(FIRST_VISIBLE_POSITION, 0);
            mOffset = b.getInt(FIRST_VISIBLE_ITEM_OFFSET, 0);
        }

        mParentTask = (Task) getArguments().getSerializable(IPCConstants.EXTRA_TASK);
        sParentTaskPosition = getArguments().getInt(IPCConstants.EXTRA_TASK_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mListView.setOnItemClickListener(this);
        mListView.setFragmentManager(getActivity().getSupportFragmentManager());
        registerForContextMenu(mListView);

        mAdapter = new TaskAdapter(getActivity(), false, this);
        mListView.setAdapter(mAdapter);

        setLogo(mParentTask);

        return null;
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        getLoaderManager().restartLoader(R.id.lm_sync_info, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        setBlockingProcess(true, this);
        new Thread(new PerformLogic(false)).start();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        mFirstVisiblePosition = mListView.getFirstVisiblePosition();
        mOffset = mListView.getChildAt(0) == null ? 0 : mListView.getChildAt(0).getTop();

        super.onPause();
    }

    @Override
    public void onStop() {
        mAdapter.clear();// setData(new ArrayList<Task>(0));
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(mScreenOffReceiver);
        } catch (IllegalArgumentException e) {}
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        b.putSerializable(IPCConstants.EXTRA_TASK, mParentTask);
        // b.putInt(IPCConstants.EXTRA_TASK_CURRENT_POSITION, sPosition);

        if (mListView != null) {
            b.putInt(FIRST_VISIBLE_POSITION, mListView.getFirstVisiblePosition());
            b.putInt(FIRST_VISIBLE_ITEM_OFFSET, mListView.getChildAt(0) == null ? 0 : mListView.getChildAt(0).getTop());
        } else {
            b.putInt(FIRST_VISIBLE_POSITION, mFirstVisiblePosition);
            b.putInt(FIRST_VISIBLE_ITEM_OFFSET, mOffset);
        }
    }

    private void setLogo(Task task) {

    }

    @Override
    public boolean showTitleBar() {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        /*
         * if current gesture is not move gesture then perform corresponding processing of click event, otherwise - do
         * nothing
         */
        if (!mListView.isMoveGesture()) {
            // mCurrentTask = mData.get(mData.size() - 1).get(arg2);
            sPosition = arg2;
            showSubtasks(mAdapter.getData().get(arg2));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        /*
         * if current gesture is not move gesture then perform corresponding processing of long click event, otherwise -
         * do nothing
         */
        if (!mListView.isMoveGesture()) {
            super.onCreateContextMenu(menu, v, menuInfo);
            final MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.task_item_menu, menu);
            // display "assign" menu item
            final int position = ((AdapterContextMenuInfo) menuInfo).position;
            final String customer = ((Task) mAdapter.getItem(position)).getCustomer();

            final MenuItem itemAssign = menu.findItem(R.id.menu_assign);
            final MenuItem itemDelete = menu.findItem(R.id.menu_dell);
            final boolean show = mApp.getSettings().getUserName().equals(customer);
            itemAssign.setVisible(show);
            itemDelete.setVisible(show);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        try {
            sCurrentTask = getLastListOfData().get(info.position);
        } catch (IndexOutOfBoundsException e) {
            new PerformLogic(true).run();
            return false;
        }

        switch (item.getItemId()) {
        case R.id.menu_properties:
            sPosition = info.position;
            sIsAddOrEditScreenActivated = true;
            // if (!sCurrentTask.isReaded()) {
            // sCurrentTask.setReaded(true);
            // getData().get(getData().size() - 1).set(sPosition, sCurrentTask);
            // }
            // startFragmentAtDetails(TabViewFragment.newInstance(sCurrentTask),
            // "task_tab_fragment", false);

            showTabViewFragmant(sCurrentTask);
            return true;

        case R.id.menu_subtasks:
            sPosition = info.position;
            startFragment(SubtasksListFragment.newInstance(getLastListOfData()//
                    .get(info.position), info.position));
            return true;

        case R.id.menu_term:
            sPosition = info.position;
            SetTermDialog.newInstance(this, sCurrentTask).showDialog(getFragmentManager());
            return true;

        case R.id.menu_assign:
            sPosition = info.position;
            final String performer = (getLastListOfData()).get(info.position).getPerformer();
            SetPerformerDialog.newInstance(this, performer).showDialog(getFragmentManager());
            return true;

        case R.id.menu_dell:
            showDeleteTaskDialog(getLastListOfData().get(info.position));
            return true;

        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu_task_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mApp.getSettings().isMakeTaskHide()) {
            menu.findItem(R.id.show_hide_make_task).setTitle(R.string.menu_show_make_task);
        } else {
            menu.findItem(R.id.show_hide_make_task).setTitle(R.string.menu_hide_make_task);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case android.R.id.home:
            getFragmentManager().popBackStack();
            break;

        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ServiceConstants.ACTION_NOTIFYDATASETCHANGED)) {
                if (intent.getBooleanExtra(ServiceConstants.VALUE_BOOLEAN, true)) {
                    mAdapter.clear();
                    new UpdateTask(false, TaskAction.NONE, null).execute();
                }
            } else if (intent.getAction().equals(ServiceConstants.ACTION_TASKS_UPDATE)) {
                new PerformLogic(true).run();
            }
        }
    };

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                sIsScreenOff = true;
            }
        }
    };

    private void showSubtasks(Task task) {
        // if (!task.isReaded() && task.getSubTasksCount() == 0) {
        // task.setReaded(true);
        // getData().get(getData().size() - 1).set(sPosition, task);
        // }

        if ((mSettings.isMakeTaskHide() && task.getSubTasksCountNotMade() < 1) || (!mSettings.isMakeTaskHide() && task.getSubTasksCount() < 1)) {
            showTabViewFragmant(task);
            sIsAddOrEditScreenActivated = true;
        } else {
            startFragment(SubtasksListFragment.newInstance(task, sPosition));
        }
    }

    private void showTabViewFragmant(Task task) {
        if (task != null && !task.isReaded()) {
            task.setReaded(true);
            getLastListOfData().set(sPosition, task);

        }
        startFragment(TabViewFragment.newInstance(null, task));
    }

    @Override
    public void removeTask(Task task) {
        if (afterSaveTaskFromNotify()) {
            return;
        }

        if (getData() != null) {
            getLastListOfData().remove(task);
            mAdapter.removeItem(task);
        } else {
            new UpdateTask(true, TaskAction.REMOVE, task).execute();
        }
    }

    @Override
    public void addTask(Task task) {
        mAddTask = true;

        if (getData() != null) {
            getLastListOfData().add(task);
            if (mAdapter == null) {
                mAdapter = new TaskAdapter(getActivity(), false, this);
                mAdapter.setData(getLastListOfData());

            } else {
                mAdapter.addItem(task);
            }

        } else {
            new UpdateTask(true, TaskAction.ADD, task).execute();
        }
    }

    @Override
    public void changeTask(Task task) {
        if (afterSaveTaskFromNotify()) {
            return;
        }

        if (getData() != null) {
            getLastListOfData().set(sPosition, task);
            if (mAdapter == null) {
                mAdapter = new TaskAdapter(getActivity(), false, this);
                mAdapter.setData(getLastListOfData());
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            new UpdateTask(true, TaskAction.CHANGE, task).execute();
        }
    }

    private boolean afterSaveTaskFromNotify() {
        if (mSettings.isTaskFromNotify()) {
            mSettings.setTaskFromNotify(false);
            clearData();
            TasksListFragment.clearData();
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentResult(Object object, int type) {
        super.onFragmentResult(object, type);

        switch (type) {
        case SetTermDialog.REQUEST_CODE:
            sCurrentTask = (Task) object;
            if (sCurrentTask == null) {
                return;
            }

            setBlock(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mDbHelper.updateTask(sCurrentTask, false, false, false);
                    } catch (AbstractDataRequestException e) {
                        Utils.toLog(e);
                    }

                    // new
                    // ProcessNotifications(getActivity()).updateNotification(sCurrentTask);
                    // TODO
                    SimpleNotifications.getInstance(mApp).updateOldSimpleNotify(sCurrentTask);

                    updateCurrentTaskAndAdapter();
                }
            }).start();
            break;

        case SetPerformerDialog.REQUEST_CODE:
            setPerformer(object);
            break;

        default:
            updateCurrentTaskAndAdapter();
            break;
        }
    }

    private void setPerformer(final Object object) {
        setBlock(true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String oldPerformer = sCurrentTask.getPerformer();
                sCurrentTask.setPerformer((String) object);

                try {
                    mDbHelper.updateTask(sCurrentTask, true, false, false);
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
                    if (sCurrentTask.getPerformer().equals(email)) {
                        isNewPerformerExists = true;
                    }
                }

                if (!isOldPerformerExists || !isNewPerformerExists) {
                    // update sliding menu hierarchy
                    final Intent intent = new Intent();
                    intent.setAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }

                // update notifications
                // new
                // ProcessNotifications(getActivity()).updateNotification(sCurrentTask);
                // TODO
                SimpleNotifications.getInstance(mApp).updateOldSimpleNotify(sCurrentTask);
                // Emp.updateTaskEmpSort(mApp, sCurrentTask);

                updateCurrentTaskAndAdapter();
            }
        }).start();
    }

    private void updateCurrentTaskAndAdapter() {
        final List<Task> tasks = getLastListOfData();
        if (sPosition < tasks.size()) {
            tasks.set(sPosition, sCurrentTask);
        }
        mHandler.post(mUpdateAdapterRun);
    }

    private Runnable mUpdateAdapterRun = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
            setBlock(false);
        }
    };

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

            new PerformLogic(true).run();
        default:
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onRefresh() {

    }

    private class PerformLogic implements Runnable {

        private boolean mIsAfterSync;

        public PerformLogic(boolean isAfterSync) {
            mIsAfterSync = isAfterSync;
        }

        @Override
        public void run() {
            if (getActivity() == null) {
                return;
            }

            boolean runUpdateTask = false;

            // mActionBar.setDisplayShowCustomEnabled(false);

            if (mIsAfterSync) {
                mAdapter.clear();
                new UpdateTask(false, TaskAction.NONE, null).execute();
                runUpdateTask = true;
            }

            else if (getData() != null) {
                if (sOrientation != getResources().getConfiguration().orientation) {
                    mAdapter.setData(getLastListOfData()); // TODO CHECKOUT 613
                    sOrientation = getResources().getConfiguration().orientation;
                }

                else {
                    if (!sIsAddOrEditScreenActivated) {
                        if (sBackStackEntryCount < getFragmentManager().getBackStackEntryCount()) {
                            mAdapter.clear();
                            new UpdateTask(false, TaskAction.NONE, null).execute();
                            runUpdateTask = true;
                        }

                        else {
                            if (sBackStackEntryCount > getFragmentManager().getBackStackEntryCount()) {
                                removeData(getData().size() - 1);
                            }

                            if (getData().size() != 0) {
                                mAdapter.setData(getLastListOfData());
                            }

                            else {
                                mAdapter.clear();
                                new UpdateTask(false, TaskAction.NONE, null).execute();
                                runUpdateTask = true;
                            }
                        }
                    }

                    else {
                        mAdapter.setData(getLastListOfData());
                    }
                }
            }

            else {
                if (sIsScreenMinimized) {
                    try { // TODO Bug #3504 added try{}catch{}
                        final int position = getData().size() - 1;
                        if (position >= 0 && position < getData().size()) {
                            mAdapter.setData(getData().get(position));
                        }
                    } catch (Exception e) {
                        Utils.toLog(e);
                    }

                    sIsScreenMinimized = false;
                }

                else if (sOrientation == 0 || //
                        sOrientation == getResources().getConfiguration().orientation && !sIsScreenOff) {
                    mAdapter.clear();
                    new UpdateTask(false, TaskAction.NONE, null).execute();
                    runUpdateTask = true;
                }

                else if (sOrientation != getResources().getConfiguration().orientation) {
                    if (getData() != null) {
                        mAdapter.setData(getLastListOfData());
                    }

                    else {
                        mAdapter.clear();
                        new UpdateTask(false, TaskAction.NONE, null).execute();
                        runUpdateTask = true;
                    }
                }
            }

            if (sIncreaseByParentTasksCount != 0) {
                if (sIncreaseByParentTasksCount == 1) {
                    mParentTask.setSubTasksCountNotMade(mParentTask.getSubTasksCountNotMade() + 1);
                    mParentTask.setSubTasksCount(mParentTask.getSubTasksCount() + 1);

                    if (sData != null) {
                        synchronized (sData) {
                            if (sData != null && sData.size() > 1) {
                                try {
                                    final int position = sData.size() - 2;
                                    final List<Task> listTasks = sData.get(position);
                                    listTasks.set(sParentTaskPosition, mParentTask);
                                } catch (IndexOutOfBoundsException e) {
                                    Utils.toLog(e);
                                }
                            }

                            else if (!TasksListFragment.isDataEmpty()) {
                                TasksListFragment.setTaskToData(sParentTaskPosition, mParentTask);
                            }
                        }
                    }

                    // if (getData()!=null) {
                    // if (getData().size() > 1) {
                    // getData().get(getData().size() - 2).set(sParentTaskPosition, mParentTask);
                    // }
                    //
                    // else if (!TasksListFragment.isDataEmpty()) {
                    // TasksListFragment.setTaskToData(sParentTaskPosition, mParentTask);
                    // }
                    // }
                }

                else if (sIncreaseByParentTasksCount == -1) {
                    if (!getData().isEmpty() && getData().size() >= 1) {
                        final Task cTask = getLastListOfData().get(sPosition);
                        cTask.setSubTasksCount(cTask.getSubTasksCount() - 1);
                        if (!mDbHelper.hideTask(cTask, mSettings.getUserName())) {
                            cTask.setSubTasksCountNotMade(cTask.getSubTasksCountNotMade() - 1);
                            if (!cTask.isReaded()) {
                                cTask.setSubTasksSizeNotMadeAndNotRead(cTask.getSubTasksSizeNotMadeAndNotRead() - 1);
                                cTask.setSubTasksCountNotRead(cTask.getSubTasksCountNotRead() - 1);
                            }
                        }

                        else if (!cTask.isReaded()) {
                            cTask.setSubTasksSizeNotMadeAndNotRead(cTask.getSubTasksSizeNotMadeAndNotRead() - 1);
                            cTask.setSubTasksCountNotRead(cTask.getSubTasksCountNotRead() - 1);
                        }
                        getLastListOfData().set(sPosition, cTask);
                    }
                }
                sIncreaseByParentTasksCount = 0;
            }

            sBackStackEntryCount = getFragmentManager().getBackStackEntryCount();
            sIsAddOrEditScreenActivated = false;
            sOrientation = getResources().getConfiguration().orientation;
            sIsScreenOff = false;

            mListView.post(mLvSelectionRun);

            final IntentFilter filter = new IntentFilter(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
            filter.addAction(ServiceConstants.ACTION_TASKS_UPDATE);
            LocalBroadcastManager.getInstance(mApp).registerReceiver(mReceiver, filter);
            mApp.registerReceiver(mScreenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

            TasksListFragment.onListUpdateEnd(mApp);

            if (!runUpdateTask) {
                mHandler.post(mSetBlockFalseRun);
            }
        }
    }

    private Runnable mSetBlockFalseRun = new Runnable() {
        @Override
        public void run() {
            setBlockingProcess(false, this);

            if (mAddTask) {
                mAddTask = false;
                mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
            }
        }
    };

    private Runnable mLvSelectionRun = new Runnable() {
        @Override
        public void run() {
            mListView.setSelectionFromTop(mFirstVisiblePosition, mOffset);
        }
    };

    private class UpdateTask extends AsyncTask<Void, Void, Void> {

        private boolean mIsUpdateAdapterData;
        private TaskAction mAction;
        private Task mTask;
        private long mStart;

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
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mStart = System.currentTimeMillis();
                if (getData() == null) {
                    sData = new ArrayList<List<Task>>();
                }

                getData().add(new GetSubTasks//
                        (mApp, mParentTask, mSettings.getUserName(), mSettings.isMakeTaskHide())//
                                .execute(null).getResult());

                final long end = System.currentTimeMillis() - mStart;
                if (end < 500) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }

                // TODO Bug #3504 added try{}catch{}
                try {
                    final int position = getData().size() - 1;
                    if (position < 0) {
                        return null;
                    }

                    final List<Task> tasks = getData().get(position);
                    mAdapter.setData(tasks);
                } catch (Exception e) {
                    Utils.toLog(e);
                }
            } catch (AbstractDataRequestException e1) {
                Utils.toLog(e1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mIsUpdateAdapterData) {
                switch (mAction) {
                case REMOVE:
                    getData().remove(mTask);
                    mAdapter.removeItem(mTask);
                    break;

                case ADD:
                    getLastListOfData().add(mTask);
                    mAdapter.addItem(mTask);
                    break;

                case CHANGE:
                    getLastListOfData().set(sPosition, mTask);
                default:
                    break;
                }
                mAdapter.notifyDataSetChanged();
            }
            setBlockingProcess(false, this);
        }
    }

    public static synchronized void clearData() {
        if (sData != null) {
            sData.clear();
        }
    }

    public static synchronized boolean isDataEmpty() {
        return sData == null || sData.isEmpty();
    }

    public static synchronized int getDataSize() {
        return sData == null || sData.isEmpty() ? 0 : sData.size();
    }

    public static synchronized List<Task> getLastListOfData() {
        int last = sData == null || sData.isEmpty() ? 0 : sData.size();
        if (last > 0) {
            last--;
        }

        return sData.get(last);
    }

    public static synchronized void removeData(int position) {
        if (position >= 0) {
            sData.remove(position);
        }
    }

    public static synchronized List<Task> getListOfData(int position) {
        return sData.get(position);
    }

    public static synchronized void addListToDataAtFirst(List<Task> list) {
        sData.add(0, list);
    }

    public static synchronized List<List<Task>> getData() {
        return sData;
    }
}