package com.ashberrysoft.leadertask.modern.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.dialog.TaskPerformerDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskTermDialog;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TaskDrawerFragment extends BaseSyncStatusFragment implements  View.OnClickListener {

    private static boolean mIsPerformer;
    private ImageView mSubtasksIcon;
    private ImageView mPropertiesIcon;
    private ImageView mAssignIcon;
    private ImageView mTermIcon;
    private ImageView mDeleteIcon;

    public static Fragment newInstance(LTask task) {
        mIsPerformer = isPerformer(task);
        return new TaskDrawerFragment();
    }

    private static boolean isPerformer(LTask task) {
        return LTSettings.getInstance().getUserName().equals(task.getEmailCustomer());
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

    }

    @Override
    public void onSyncStatusChange(SyncInfo si) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        if(getResources().getConfiguration().orientation== 1) {
            return mIsPerformer ? inflater.inflate(R.layout.fragment_task_properties, container, false) : inflater.inflate(R.layout.fragment_task_properties_simple, container, false);
        }
        else {
            return mIsPerformer ? inflater.inflate(R.layout.fragment_task_properties_land, container, false) : inflater.inflate(R.layout.fragment_task_properties_simple_land, container, false);
        }
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);

        if(mIsPerformer) {  // 5
            mSubtasksIcon = (ImageView) v.findViewById(R.id.menu_subtasks_icon);
            mPropertiesIcon = (ImageView) v.findViewById(R.id.menu_properties_icon);
            mAssignIcon = (ImageView) v.findViewById(R.id.menu_assign_icon);
            mTermIcon = (ImageView) v.findViewById(R.id.menu_term_icon);
            mDeleteIcon = (ImageView) v.findViewById(R.id.menu_delete_icon);

            mSubtasksIcon.setOnClickListener(this);
            mPropertiesIcon.setOnClickListener(this);
            mAssignIcon.setOnClickListener(this);
            mTermIcon.setOnClickListener(this);
            mDeleteIcon.setOnClickListener(this);
        }
        else {  // 3
            mSubtasksIcon = (ImageView) v.findViewById(R.id.menu_subtasks_icon);
            mPropertiesIcon = (ImageView) v.findViewById(R.id.menu_properties_icon);
            mTermIcon = (ImageView) v.findViewById(R.id.menu_term_icon);

            mSubtasksIcon.setOnClickListener(this);
            mPropertiesIcon.setOnClickListener(this);
            mTermIcon.setOnClickListener(this);
        }
    }

    @Override
    protected Boolean showSlidingMenu() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_subtasks_icon:
                SlidingActivity.mTaskDrawerLayout.closeDrawer(Gravity.RIGHT);
                startFragment(TasksFragment.newInstance(TasksFragment.mMenuItem, TasksFragment.mTempTask));
                break;
            case R.id.menu_properties_icon:
                SlidingActivity.mTaskDrawerLayout.closeDrawer(Gravity.RIGHT);
                startActivity(EditTaskActivity.newInstance(getActivity(), TasksFragment.mTempTask, false, false));
                break;
            case R.id.menu_assign_icon:
                TaskPerformerDialog.newInstance(this, TasksFragment.mTempTask, false).showDialog(getFragmentManager());
                SlidingActivity.mTaskDrawerLayout.closeDrawer(Gravity.RIGHT);
                break;
            case R.id.menu_term_icon:
                TaskTermDialog.newInstance(this, TasksFragment.mTempTask, false).showDialog(getFragmentManager());
                SlidingActivity.mTaskDrawerLayout.closeDrawer(Gravity.RIGHT);
                break;
            case R.id.menu_delete_icon:
                SlidingActivity.mTaskDrawerLayout.closeDrawer(Gravity.RIGHT);
                Utils.getSimpleDialog(getActivity(), getDeleteDialogListener(), R.string.confirm_delete_title, R.string.confirm_delete_text);
                break;
            default:
                break;
        }
    }

    private DialogInterface.OnClickListener getDeleteDialogListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    new TaskDeleteHelper(getApp(), TasksFragment.mTempTask, true).start();
                    TasksFragment.mTempTask = null;
                }
            }
        };
    }

    @Override
    public void onReceivingObjects(int code, Object... objects) {
        switch (code) {
            case TaskTermDialog.CODE:
                if (TasksFragment.mTempTask != null) {
                    final LTask task = (LTask) objects[0];
                    if (!TimeHelper.termsEquals(task, TasksFragment.mTempTask)) {
                        final LTask taskOld = TasksFragment.mTempTask.clone();

                        TasksFragment.mTempTask.setTermBegin(task.getTermBegin());
                        TasksFragment.mTempTask.setTermEnd(task.getTermEnd());

                        TasksFragment.mTempTask.setTermBeginCustomer(task.getTermBeginCustomer());
                        TasksFragment.mTempTask.setTermEndCustomer(task.getTermEndCustomer());

                        TasksFragment.mTempTask.setUsnFieldTerm(task.getUsnFieldTerm() + 1);
                        TasksFragment.mTempTask.setUsnFieldCustomerTerm(task.getUsnFieldCustomerTerm() + 1);

                        saveTask(TasksFragment.mTempTask, taskOld);
                    }
                    TasksFragment.mTempTask = null;
                }
                break;

            case TaskPerformerDialog.CODE:
                if (TasksFragment.mTempTask != null) {
                    final String performer = (String) objects[0];
                    if (!performer.equalsIgnoreCase(TasksFragment.mTempTask.getEmailPerformer())) {
                        final LTask taskOld = TasksFragment.mTempTask.clone();

                        TasksFragment.mTempTask.setEmailPerformer(performer.toLowerCase());
                        TasksFragment.mTempTask.setUsnFieldEmailPerformer(TasksFragment.mTempTask.getUsnFieldEmailPerformer() + 1);

                        TasksFragment.mTempTask.setPerformTime(System.currentTimeMillis());
                        TasksFragment.mTempTask.setUsnFieldPerformtime(TasksFragment.mTempTask.getUsnFieldPerformtime() + 1);

                        saveTask(TasksFragment.mTempTask, taskOld);
                    }
                    TasksFragment.mTempTask = null;
                }
                break;

            default:
                super.onReceivingObjects(code, objects);
                break;
        }
    }

    private void saveTask(LTask taskNew, LTask taskOld) {
        new TaskSaveHelper(false, getApp(), taskNew, false, null, taskOld, 0,//
                new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
    }
}
