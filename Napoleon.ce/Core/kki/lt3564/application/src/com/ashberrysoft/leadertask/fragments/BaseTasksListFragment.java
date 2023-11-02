package com.ashberrysoft.leadertask.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.BaseSlidingActivity;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.activities.HomeActivity.TaskCommunicationInterface;
import com.ashberrysoft.leadertask.adapters.TaskAdapter.OnTaskStatusClickListener;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.DeleteTask;
import com.ashberrysoft.leadertask.dialogs.SetStatusDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.utils.LTPowerManager;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.TaskViewAnimation;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@SuppressWarnings("deprecation")
public abstract class BaseTasksListFragment extends LTVisibleBaseFragment
        //
        implements SwipeRefreshLayout.OnRefreshListener, TaskCommunicationInterface, DialogInterface.OnClickListener,
        OnTaskStatusClickListener {

    // VALUE's
    protected Handler mHandler;
    protected DbHelper mDbHelper;
    private Task mTempTask;
    private ImageView mTempImageView;

    // VIEW's
    protected View mProgressBar;

    protected boolean mAddTask;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMessageNext(msg);
            }
        };
        mDbHelper = DbHelper.getInstance(getActivity());
    }

    protected void handleMessageNext(Message msg) {}

    @Override
    public void onViewCreated(View view, Bundle b) {
        super.onViewCreated(view, b);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((BaseSlidingActivity) getActivity()).disableAndSetSlidingMenu(false, false);

        onPullToRefresh();
        registerReceiver();

        if (mTempImageView == null) {
            final Fragment fragment = getFragmentManager().findFragmentByTag(SetStatusDialog.DIALOG_TAG);
            if (fragment != null && fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).dismiss();
            }
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver();
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(mApp).registerReceiver(mStateChanged,
                new IntentFilter(IPCConstants.ACTION_SYNCHRONIZATION_STATE_CHANGED));
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(mApp).unregisterReceiver(mStateChanged);
    }

    private BroadcastReceiver mStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onPullToRefresh();
        }
    };

    private void onPullToRefresh() {

    }

    protected void setRefreshing(boolean refreshing) {


        mHandler.post(refreshing ? mSetRefreshingTrueRunnable : mSetRefreshCompleteRunnable);
    }

    private Runnable mSetRefreshCompleteRunnable = new Runnable() {
        @Override
        public void run() {
            LTPowerManager.getInstance(mApp).sleepUnlock();
        }
    };

    private Runnable mSetRefreshingTrueRunnable = new Runnable() {
        @Override
        public void run() {
            LTPowerManager.getInstance(mApp).sleepLock();
        }
    };

    @Override
    public boolean showTitleBar() {
        return false;
    }

    protected void closeSlidingMenu() {

    }

    @Override
    public void setBlockingProcess(boolean setBlock, Object tag) {
        mProgressBar.setVisibility(setBlock ? View.VISIBLE : View.GONE);
    }

    protected void showDeleteTaskDialog(Task task) {
        mTempTask = task;
        Utils.getSimpleDialog(getActivity(), this, R.string.confirm_delete_title, R.string.confirm_delete_text);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which != DialogInterface.BUTTON_POSITIVE || mTempTask == null) {
            return;
        }

        setBlock(true);
        new Thread(mDeleteTaskRun).start();
    }

    private final Runnable mDeleteTaskRun = new Runnable() {
        @Override
        public void run() {
//            LTCalendarView.clearCalendarData(getActivity());
            try {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    SubtasksListFragment.sIncreaseByParentTasksCount = -1;
                }
                if (mSettings.getTaskMode() == TaskMode.PROJECTS) {
                    mTempTask.setProjectUid(mSettings.getChooseProject().getId());
                }

                new DeleteTask(getActivity(), mTempTask).execute(null);
                mDbHelper.recalculateVerticalTaskSubtasks(mApp, mSettings.getUserName(), mTempTask);

                removeTask(mTempTask);
                mTempTask = null;

            } catch (AbstractDataRequestException e) {
                Utils.toLog(e);

            } finally {
                mHandler.post(mSetBlockFalseRun);
            }
        }
    };

    private final Runnable mSetBlockFalseRun = new Runnable() {
        @Override
        public void run() {
            setBlock(false);
        }
    };

    @Override
    public void onTaskStatusClick(ImageView iv, Task task) {
        switch (mSettings.getStatusBehavior()) {
        case FINISH:
            TaskViewAnimation.startAnimation(getActivity(), iv, task, null);
            break;

        case SELECT:
            mTempTask = task;
            mTempImageView = iv;

            SetStatusDialog.newInstance(this, mTempTask).showDialog(getFragmentManager());
            break;

        case NONE:
        default:
            break;
        }
    };

    @Override
    public void onFragmentResult(Object data, int requestCode) {
        super.onFragmentResult(data, requestCode);

        if (requestCode == SetStatusDialog.REQUEST_CODE) {
            final TaskStatus status = (TaskStatus) data;
            mTempTask.setStatusType(status);
            TaskViewAnimation.startAnimation(getActivity(), mTempImageView, mTempTask, status);

            mTempTask = null;
            mTempImageView = null;
        }
    }
}