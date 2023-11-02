package com.ashberrysoft.leadertask.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.StatusListAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;


public class SetStatusDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public enum UserTaskRole {
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

    private static final String CLASS_PATH = SetStatusDialog.class.getName();
    public static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_TASK = CLASS_PATH + "EXTRA_TASK";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.status_dialog_request_code;

    // VALUE's
    private Task mTask;

    // ADAPTER
    private StatusListAdapter mAdapter;

    public static SetStatusDialog newInstance(Fragment fragment, Task task) {
        final Bundle b = new Bundle();
        b.putSerializable(EXTRA_TASK, task);

        final SetStatusDialog d = new SetStatusDialog();
        d.setTargetFragment(fragment, REQUEST_CODE);
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final Bundle bundle = b != null ? b : getArguments();
        mTask = (Task) bundle.getSerializable(EXTRA_TASK);

        TaskStatus status = mTask.getStatusType();
        if (status == null) {
            status = TaskStatus.NOT_BEGIN;
        }

        final UserTaskRole userRole;
        {
            final String userName = LTSettings.getInstance(getActivity()).getUserName();
            final boolean isCustomer = userName.equals(mTask.getCustomer());
            final boolean isPerformer = userName.equals(mTask.getPerformer());
            userRole = UserTaskRole.whoIsWho(isCustomer, isPerformer);
        }

        final List<TaskStatus> statuses = new ArrayList<TaskStatus>(6);
        statuses.add(TaskStatus.NOT_BEGIN);
        statuses.add(TaskStatus.IN_WORK);
        statuses.add(TaskStatus.PAUSED);

        switch (userRole) {
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
        default:
            break;
        }

        int selected = -1;
        for (int i = 0; i < statuses.size(); i++) {
            if (status.ordinal() == statuses.get(i).ordinal()) {
                selected = i;
                break;
            }
        }

        mAdapter = new StatusListAdapter(getActivity(), statuses, selected,
                mTask.getSeriesType() != SeriesType.NONE.ordinal());

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_status_dialog, null);
        ((ListView) v.findViewById(R.id.list_status)).setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.task_status);
        ad.setNegativeButton(R.string.btn_cancel, this);
        ad.setPositiveButton(R.string.btn_ok, this);

        return ad.show();
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

        mTask.setStatus(mAdapter.getStatus());
        b.putSerializable(EXTRA_TASK, mTask);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (getTargetFragment() != null && which == DialogInterface.BUTTON_POSITIVE) {
            if (getTargetFragment() instanceof LTBaseFragment) {
                ((LTBaseFragment) getTargetFragment()).onFragmentResult(mAdapter.getStatus(), REQUEST_CODE);
            }
        }

        dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    public void showDialog(FragmentManager manager) {
        if (manager.findFragmentByTag(DIALOG_TAG) == null) {
            super.show(manager, DIALOG_TAG);
        }
    }

    public static void setTargetFragment(Fragment target, FragmentManager manager) {
        final Fragment fragment = manager.findFragmentByTag(DIALOG_TAG);
        if (fragment != null && fragment instanceof DialogFragment) {
            fragment.setTargetFragment(target, REQUEST_CODE);
        }
    }
}