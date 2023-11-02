package com.ashberrysoft.leadertask.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.data_providers.DeleteTask;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.fragments.SubtasksListFragment;
import com.ashberrysoft.leadertask.interfaces.FragmentsCommunicationInterface;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Диалог с двумя кнопками - ОК и Отмена - для удаления задачи
 * 
 * @deprecated
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class DeleteTaskDialog extends DialogFragment implements OnClickListener {

    private Task mTask;
    private FragmentsCommunicationInterface mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FragmentsCommunicationInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onFragmentToFragmentCommunication");
        }
    }

    /**
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param task
     * @param userName
     * @return
     */
    public static DeleteTaskDialog newInstance(Task task) {
        DeleteTaskDialog dialog = new DeleteTaskDialog();
        Bundle args = new Bundle();
        args.putSerializable(IPCConstants.EXTRA_TASK, task);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mTask = (Task) getArguments().getSerializable(IPCConstants.EXTRA_TASK);

        return Utils.getSimpleDialog(getActivity(), this, R.string.confirm_delete_title, R.string.confirm_delete_text);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            // TODO CalendarData update task TermBegin
//            LTCalendarView.clearCalendarData(getActivity());
            // if (mTask.getTermBegin() != null) {
            // getActivity().getContentResolver().delete(CalendarDataContract.CONTENT_URI,
            // CalendarDataContract.selectionDate(mTask.getTermBegin()), null);
            // }

            try {
                /*
                 * if we delete task from subtasks screen, then set flag to particular value
                 */
                if (getFragmentManager().getBackStackEntryCount() > 0)
                    SubtasksListFragment.sIncreaseByParentTasksCount = -1;
                LTSettings settings = LTSettings.getInstance(getActivity());
                if (settings.getTaskMode() == TaskMode.PROJECTS) {
                    mTask.setProjectUid(settings.getChooseProject().getId());
                }

                new DeleteTask(getActivity(), mTask).execute(null);
                mCallback.onTaskDeleted(mTask, true, false);
            } catch (AbstractDataRequestException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
