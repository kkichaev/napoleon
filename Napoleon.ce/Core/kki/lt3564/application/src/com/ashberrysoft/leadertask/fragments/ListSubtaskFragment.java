package com.ashberrysoft.leadertask.fragments;

import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CursorTaskAdapter;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.views.BackListView;
import com.ashberrysoft.leadertask.views.SubtaskView.OnTaskStatusListener;

public class ListSubtaskFragment extends BaseTasksListFragment implements LoaderCallbacks<Cursor>, OnTaskStatusListener {

    private static final String CLASS_PATH = ListSubtaskFragment.class.getName();
    private static final String EXTRA_PARENT_UUID = CLASS_PATH + "EXTRA_PARENT_UUID";

    // VIEW's
    private BackListView mListView;

    // VALUE's
    private String mParentUUID;

    // ADDAPTER
    private CursorTaskAdapter mAdapter;

    public static ListSubtaskFragment newInstance(UUID parent) {
        final Bundle b = new Bundle();
        b.putString(EXTRA_PARENT_UUID, String.valueOf(parent));

        final ListSubtaskFragment f = new ListSubtaskFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mParentUUID = b != null ? b.getString(EXTRA_PARENT_UUID) : getArguments().getString(EXTRA_PARENT_UUID);
        mAdapter = new CursorTaskAdapter(getActivity(), null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return null;
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        getLoaderManager().restartLoader(R.id.lm_sync_info, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(EXTRA_PARENT_UUID, mParentUUID);
    }

    @Override
    public void onTaskStatusClick(Task task) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTaskViewClick(Task task) {
        final boolean goSub;
        if (mSettings.isMakeTaskHide()) {
            goSub = task.getSubTasksCount() > 0;
        } else {
            goSub = task.getSubTasksCountNotMade() > 0;
        }

        if (goSub) {
            startFragment(ListSubtaskFragment.newInstance(task.getId()));

        } else {
            startFragment(TabViewFragment.newInstance(null, task));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        switch (id) {
        case R.id.lm_sync_info:
            return new CursorLoader(mApp, SyncInfoContract.CONTENT_URI, null, null, null, null);

        case R.id.lm_subtask_adapter:
            return new CursorLoader(mApp, TaskContract.CONTENT_URI, null,
                    TaskContract.selectionFieldUidParent(mParentUUID), null, null);

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
        case R.id.lm_sync_info:
            onSyncStatusChange(cursor);
            break;

        case R.id.lm_subtask_adapter:
            mAdapter.swapCursor(cursor);
            break;

        default:
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {}

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

            getLoaderManager().restartLoader(R.id.lm_subtask_adapter, null, this);
            // new PerformLogic(true).run(); // TODO
        default:
            break;
        }
    }

    @Override
    public void removeTask(Task task) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTask(Task task) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changeTask(Task task) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleMessageNext(Message msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRefresh() {

    }
}