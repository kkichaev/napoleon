package com.ashberrysoft.leadertask.modern.dialog;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.StatusListAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.TasksFragment;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskStatusDialog extends BaseDialog//
        implements OnClickListener {

    private enum UserTaskRole {
        NONE, CUSTOMER, PERFORMER, CUSTOMER_PERFORMER;

        public static UserTaskRole whoIsWho(boolean isCustomer, boolean isPerformer) {
            if (isCustomer && isPerformer) {
                return CUSTOMER_PERFORMER;
            }

            else if (isCustomer && !isPerformer) {
                return CUSTOMER;
            }

            else if (!isCustomer && isPerformer) {
                return PERFORMER;
            }

            else {
                return NONE;
            }
        }
    }

    private static final String EXTRA_TASK_STATUS = "EXTRA_TASK_STATUS";
    private static final String EXTRA_TASK_SERIAL = "EXTRA_TASK_SERIAL";
    private static final String EXTRA_NEED_SOUND = "EXTRA_NEED_SOUND";
    private static final String EXTRA_USER_ROLE = "EXTRA_USER_ROLE";
    public static final int CODE = R.id.dialog_task_status;

    // VALUE's
    private int mStatus;
    private boolean mSerial;
    private boolean mNeedSound;
    private UserTaskRole mUserRole;

    // ADAPTER
    private StatusListAdapter mAdapter;

    public static TaskStatusDialog newInstance(Fragment fragment, LTask task, boolean needSound) {
        final Bundle b = new Bundle(3);
        b.putInt(EXTRA_TASK_STATUS, task.getStatus());
        b.putBoolean(EXTRA_TASK_SERIAL, task.getSeriesType() != SeriesType.NONE.ordinal());
        b.putBoolean(EXTRA_NEED_SOUND, needSound);
        {
            final String userName = LTSettings.getInstance().getUserName();
            final boolean isCustomer = userName.equals(task.getEmailCustomer());
            final boolean isPerformer = userName.equals(task.getEmailPerformer());

            b.putInt(EXTRA_USER_ROLE, UserTaskRole.whoIsWho(isCustomer, isPerformer).ordinal());
        }

        final TaskStatusDialog d = new TaskStatusDialog();
        d.setTargetFragment(fragment, CODE);
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b == null ? getArguments() : b;

        mStatus = bundle.getInt(EXTRA_TASK_STATUS, TaskStatus.NOT_BEGIN.getCode());
        mSerial = bundle.getBoolean(EXTRA_TASK_SERIAL);
        mNeedSound = bundle.getBoolean(EXTRA_NEED_SOUND);
        mUserRole = UserTaskRole.values()[bundle.getInt(EXTRA_USER_ROLE)];

        final List<TaskStatus> statuses = new ArrayList<TaskStatus>(6);
        statuses.add(TaskStatus.NOT_BEGIN);
        statuses.add(TaskStatus.IN_WORK);
        statuses.add(TaskStatus.PAUSED);

        switch (mUserRole) {
        case CUSTOMER:
            statuses.add(TaskStatus.CANCELLED);
            statuses.add(TaskStatus.COMPLETED);
            statuses.add(TaskStatus.REFINE);
            break;

        case CUSTOMER_PERFORMER:
            statuses.add(TaskStatus.CANCELLED);
            statuses.add(TaskStatus.COMPLETED);
            statuses.add(TaskStatus.NOTE);
            break;

        case PERFORMER:
            statuses.add(TaskStatus.REJECTED);
            statuses.add(TaskStatus.READY);
            break;

        default:
            break;
        }

        final TaskStatus status = TaskStatus.getTaskStatus(mStatus);
        int selected = -1;

        for (int i = 0; i < statuses.size(); i++) {
            if (status == statuses.get(i)) {
                selected = i;
                break;
            }
        }

        mAdapter = new StatusListAdapter(getActivity(), statuses, selected, mSerial);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setSingleChoiceItems(mAdapter,-1, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAdapter.setStatus(i);
                final TaskStatus status = mAdapter.getStatus();
                if (status.getCode() == Status.TASK_COMPLETED.getStatusCode() && mNeedSound) {
                    Utils.playAudio(getContext(), 1);
                }
                receiveObjects(CODE, status.getCode());
                getDialog().cancel();
            }
        });
        return ad.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (getTargetFragment() == null || !(getTargetFragment() instanceof TasksFragment)) {
            b.putSerializable(EXTRA_TASK_STATUS, mAdapter.getStatus().getCode());
            b.putBoolean(EXTRA_TASK_SERIAL, mSerial);
            b.putBoolean(EXTRA_NEED_SOUND, mNeedSound);
            b.putInt(EXTRA_USER_ROLE, mUserRole.ordinal());
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            final TaskStatus status = mAdapter.getStatus();
            if (status != null) {
                receiveObjects(CODE, status.getCode());

            }
        }
    }
}