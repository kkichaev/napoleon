package com.ashberrysoft.leadertask.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.adapters.SlidingMenuAdapter;
import com.ashberrysoft.leadertask.application.BroadcastAction;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.cache.CachedEmployee;
import com.ashberrysoft.leadertask.cache.MarkersCacheHolder;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.data_providers.GetNumberOfIncomeTasks;
import com.ashberrysoft.leadertask.data_providers.GetNumberOfTasksForToday;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuRootTreeItem;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.fragments.TasksListFragment.OnCalendarControlListener;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;
import com.ashberrysoft.leadertask.views.LTCalendarView.OnCalendarDateSelectedListener;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.ui.activities.IBaseActivity;

/**
 * Списки фильтрации
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Tregub Artem tregub.artem@gmail.com
 */

@Deprecated
public class SlidingMenuFragment extends Fragment implements OnItemClickListener, OnClickListener, OnCalendarDateSelectedListener, OnCalendarControlListener,
        LoaderCallbacks<Cursor> {

    public static final Calendar CALENDAR = Calendar.getInstance();

    // VIEW's
    private View mMainView;
    private ListView mListView;
    private LTCalendarView mLTCalendar;
    private View mProgressBar;

    // VALUE's
    private LTSettings mSettings;
    private static SlidingMenuRootTreeItem sSlidingMenuRootTreeItem;
    private Handler mHandler;
    private LTApplication mApp;

    // ADAPTER
    private SlidingMenuAdapter mAdapter;

    // LISTENER
    private OnCalendarDateSelectedListener mListener;

    public static SlidingMenuFragment newInstance() {
        return new SlidingMenuFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mApp = (LTApplication) getActivity().getApplicationContext();
        mSettings = LTSettings.getInstance(getActivity());
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_sliding_menu, container, false);

        mListView = (ListView) mMainView.findViewById(R.id.slm_list_view);
//        mProgressBar = mMainView.findViewById(R.id.slm_progress_bar);

        mListView.setOnItemClickListener(this);
        //mMainView.findViewById(R.id.slm_settings).setOnClickListener(this);

        if (mSettings.isCalendarInNavigator()) {
            mLTCalendar = new LTCalendarView(getActivity(), this, true);

            final long time = mSettings.getFilterSelectedDate();
            if (time != 0) {
                CALENDAR.setTimeInMillis(time);
                mLTCalendar.setDate(CALENDAR.get(Calendar.YEAR), CALENDAR.get(Calendar.MONTH), CALENDAR, -1);
            } else {
                mLTCalendar.setDate(CALENDAR.get(Calendar.YEAR), CALENDAR.get(Calendar.MONTH), CALENDAR, -1);
                mLTCalendar.setControlDate(true, null);
            }

            mListView.addHeaderView(mLTCalendar, null, false);
        }

        if (sSlidingMenuRootTreeItem == null) {
            new UpdateSLMenu().execute();
        } else {
            mAdapter = new SlidingMenuAdapter(getActivity(), sSlidingMenuRootTreeItem);
            mListView.setAdapter(mAdapter);
        }

        return mMainView;
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);

        getLoaderManager().restartLoader(R.id.lm_sync_info, null, this);
        if (getActivity() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() != null) {
            final Fragment f = getFragmentManager().findFragmentById(HomeActivity.FRAGMENT_CONTAINER);
            if (f != null && f instanceof TasksListFragment) {
                mListener = (OnCalendarDateSelectedListener) f;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(ServiceConstants.ACTION_TASKS_UPDATE);
            filter.addAction(ServiceConstants.ACTION_NOTIFY_DATASET_CHANGED_SLIDING_MENU);
            filter.addAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
            filter.addAction(IPCConstants.ACTION_GET_TASKS_NUMBER_IN_PROJECT_FINISHED);
            filter.addAction(BroadcastAction.MIDNIGHT_NOTIFY);

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
        }

//        if (mSettings.isToRebootSlidingMenu()) {
//            mSettings.setToRebootSlidingMenu(false);
//            new UpdateSLMenu().execute();
//        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.clear();
        }

        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int position, long id) {
        if (mSettings.isCalendarInNavigator()) {
            position--;
        }

        mAdapter.onItemClicked(position, (ITreeData<?>) mAdapter.getItem(position));
    }

    @Override
    public void onDateSelected(Date date) {
        CALENDAR.setTime(date);
        if (mListener != null) {
            mListener.onDateSelected(date);
        }
    }

    @Override
    public void setControlDate(boolean setData, Date date) {
        if (mLTCalendar != null) {
            mLTCalendar.setControlDate(setData, date);
        }
    }


    @Override
    public void fillLostData(List<Calendar> lostData) {
        LTCalendarView.fillCalendarData(mApp, lostData);
    }

    @Override
    public void restartLoaderCallback() {
        if (mLTCalendar != null) {
            getLoaderManager().restartLoader(mLTCalendar.getLoaderCallbackId(), null, mLTCalendar);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ServiceConstants.ACTION_TASKS_UPDATE.equals(intent.getAction())) {
                onMenuUpdate();
            }

            else if (ServiceConstants.ACTION_NOTIFY_DATASET_CHANGED_SLIDING_MENU.equals(intent.getAction()) && mAdapter != null) {
                if (!mApp.isSync()) {
                    mAdapter.notifyDataSetChanged();
                }
            }

            else if (ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION.equals(intent.getAction()) && mAdapter != null) {
                // update sliding menu, especially, "I assigned" filter
                new UpdateSLMenu().execute();
                restartLoaderCallback();

            } else if (BroadcastAction.MIDNIGHT_NOTIFY.equals(intent.getAction())) {
                final Date date = new Date();

                CALENDAR.setTime(date);
                if (mLTCalendar != null) {
                    mLTCalendar.setControlDate(true, date);
                    if (mListener != null) {
                        mListener.onDateSelected(date);
                    }
                }
            }
        }
    };

    private void onMenuUpdate() {
        if (getActivity() == null || sSlidingMenuRootTreeItem == null) {
            return;
        }

        final Thread t = new Thread(mTaskUpdateRunnable);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    private Runnable mTaskUpdateRunnable = new Runnable() {
        public void run() {
            synchronized (sSlidingMenuRootTreeItem) {
                try {
                    new GetNumberOfTasksForToday(mApp, mSettings.getUserName()).execute(null);
                    new GetNumberOfIncomeTasks(mApp, mSettings.getUserName()).execute(null);

                    if (sSlidingMenuRootTreeItem != null) {
                        sSlidingMenuRootTreeItem.updateInstructI(mApp, mSettings.getUserName(), true, mSettings.getInstructI());
                    }

                    if (sSlidingMenuRootTreeItem != null) {
                        sSlidingMenuRootTreeItem.updateAssignedToMe(mApp, mSettings.getUserName(), true, mSettings.getInstructMe());
                    }

                    if (sSlidingMenuRootTreeItem != null) {
                        sSlidingMenuRootTreeItem.updateProjects(mApp, true);
                    }

                    if (sSlidingMenuRootTreeItem != null) {
                        sSlidingMenuRootTreeItem.updateAvailableProjects(mApp, true);
                    }

                    if (sSlidingMenuRootTreeItem != null) {
                        sSlidingMenuRootTreeItem.updateCategories(mApp, true, mSettings.getUserName());
                    }

                    if (sSlidingMenuRootTreeItem != null) {
                        sSlidingMenuRootTreeItem.expandedHeader();
                    }

                    updateAdapterInUI();
                } catch (AbstractDataRequestException e) {
                    Utils.toLog(e);
                }
            }
        }
    };

    private void updateAdapterInUI() {
//        LTCalendarView.clearCalendarData(mApp);

        final ContentValues cv = new ContentValues();
        cv.put(SyncInfoContract.MENU_STATUS, SyncInfoErrorType.ENDED.ordinal());
        SyncInfo.updateSynchronizationInfo(mApp, cv);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // restartLoaderCallback();
                mAdapter.notifyDataSetChanged();
                setBlockWithPb(false);
            }
        });

    }

    private void setBlockWithPb(final boolean setBlock) {
        if (setBlock) {
            mListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void showError(String message) {
        if (getActivity() != null && getActivity() instanceof IBaseActivity) {
            final IBaseActivity<?> activity = (IBaseActivity<?>) getActivity();
            activity.showError(message);
        }
    }

    @Override
    public void onClick(View v) {
        /*if (v.getIdTask() == R.id.slm_settings) {
            startActivity(SettingsActivity.newInstance(getActivity()));
        }*/
    }

    public static void setSlidingMenuRootTreeItemNull() {
        sSlidingMenuRootTreeItem = null;
    }

    /**
     * Class for creating sliding menu content in work thread and updating sliding menu in UI thread.
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    private class UpdateSLMenu extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setBlockWithPb(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            MarkersCacheHolder.getInstance(mApp).refreshCache();
            CachedEmployee.getInstance(mApp).update();

            if (getActivity() != null) {
                sSlidingMenuRootTreeItem = new SlidingMenuRootTreeItem(getActivity(),//
                        LTSettings.getInstance(mApp).getUserName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (getActivity() != null && sSlidingMenuRootTreeItem != null) {
                mAdapter = new SlidingMenuAdapter(getActivity(), sSlidingMenuRootTreeItem);
                mListView.setAdapter(mAdapter);
            }
            setBlockWithPb(false);
        }
    }

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
            onMenuStatusChange(si);
        default:
            break;
        }
    }

    private void onMenuStatusChange(SyncInfo si) {
        switch (si.getMenuStatus()) {
        case NONE:
            final ContentValues cv = new ContentValues();
            cv.put(SyncInfoContract.MENU_STATUS, SyncInfoErrorType.IN_PROGRESS.ordinal());
            SyncInfo.updateSynchronizationInfo(mApp, cv);

            onMenuUpdate();
        default:
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public static void clearSlidingMenuRootTreeItem() {
        sSlidingMenuRootTreeItem = null;
    }
}