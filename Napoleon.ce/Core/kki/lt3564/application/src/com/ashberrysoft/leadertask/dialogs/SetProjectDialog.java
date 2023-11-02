package com.ashberrysoft.leadertask.dialogs;

import java.util.UUID;

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
import com.ashberrysoft.leadertask.adapters.ProjectsTreeAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;

/**
 * Диалог для установления проекта задачи
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SetProjectDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String CLASS_PATH = SetProjectDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_PROJECT_UUID = CLASS_PATH + "EXTRA_PROJECT_UUID";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.project_dialog_request_code;

    // VIEW
    private ListView mProjectList;

    // VALUE
    private String mProjectUUID;

    // ADAPTER
    private ProjectsTreeAdapter mAdapter;

    public static SetProjectDialog newInstance(Fragment fragment, UUID projectUUID) {
        final Bundle b = new Bundle();
        if (projectUUID != null) {
            b.putString(EXTRA_PROJECT_UUID, projectUUID.toString());
        }

        final SetProjectDialog d = new SetProjectDialog();
        d.setTargetFragment(fragment, REQUEST_CODE);
        d.setArguments(b);

        return d;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_project_dialog, null);

        Project project = null;
        UUID projectUUID = null;

        final Bundle bundle = b != null ? b : getArguments();
        if (bundle.containsKey(EXTRA_PROJECT_UUID)) {
            mProjectUUID = bundle.getString(EXTRA_PROJECT_UUID);
            projectUUID = UUID.fromString(mProjectUUID);
        }

        try {
            final DbHelper db = DbHelper.getInstance(getActivity());
            project = db.getProjectDao().queryForId(projectUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAdapter = new ProjectsTreeAdapter(getActivity(), project);

        mProjectList = (ListView) v.findViewById(android.R.id.list);
        mProjectList.setCacheColorHint(0);
        mProjectList.setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.default_project);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, this);

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

        final Project project = mAdapter.getSelectedProject();
        if (project != null) {
            b.putString(EXTRA_PROJECT_UUID, project.getId().toString());
        }

        else if (mProjectUUID != null) {
            b.putString(EXTRA_PROJECT_UUID, mProjectUUID);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (getTargetFragment() != null && which == Dialog.BUTTON_POSITIVE) {
            if (getTargetFragment() instanceof LTBaseFragment) {
                ((LTBaseFragment) getTargetFragment()).onFragmentResult(mAdapter.getSelectedProject(), REQUEST_CODE);
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