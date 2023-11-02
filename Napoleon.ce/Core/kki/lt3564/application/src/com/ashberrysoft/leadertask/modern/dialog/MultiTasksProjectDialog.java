package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.ProjectsTreeAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.UUID;

public class MultiTasksProjectDialog extends BaseDialog//
        implements OnClickListener {
    // , LoaderCallbacks<Cursor>, OnDialogLoadListener<Project> {

    public static final int CODE = R.id.multi_dialog_task_project;
    private static final String EXTRA_PROJECT_UID = "EXTRA_PROJECT_UID";
    // private static final String EXTRA_PROJECTS = "EXTRA_PROJECTS";

    // VALUE's
    private String mUidProjeect;
    // private List<Project> mProjects;

    // ADAPTER
    private ProjectsTreeAdapter mAdapter;

    public static MultiTasksProjectDialog newInstance(Fragment target) {
        final Bundle b = new Bundle(1);

        final MultiTasksProjectDialog d = new MultiTasksProjectDialog();
        d.setTargetFragment(target, CODE);
        d.setArguments(b);

        return d;
    }

    // @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b == null ? getArguments() : b;
        mUidProjeect = bundle.getString(EXTRA_PROJECT_UID);
        // if (bundle.containsKey(EXTRA_PROJECTS)) {
        // mProjects = (List<Project>) bundle.getSerializable(EXTRA_PROJECTS);
        //
        // } else {
        //
        // }

        Project project = null;
        if (mUidProjeect != null) {
            try {
                project = DbHelper.getInstance(getApp()).getProjectDao().queryForId(UUID.fromString(mUidProjeect));

            } catch (Exception e) {
                Utils.toLog(e);
            }
        }

        mAdapter = new ProjectsTreeAdapter(getActivity(), project);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_project_dialog, null);

        final ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setCacheColorHint(0);
        lv.setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.default_project);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);

        return ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        final Project project = mAdapter.getSelectedProject();
        if (project != null) {
            b.putString(EXTRA_PROJECT_UID, String.valueOf(project.getId()));
        }
        // if (mProjects != null) {
        // b.putSerializable(EXTRA_PROJECTS, (Serializable) mProjects);
        // }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            receiveObjects(CODE, mAdapter.getSelectedProject());
        }
    }
    //
    // @Override
    // public void deliverResult(List<Project> data) {
    // mAdapter.setData(data);
    // }
    //
    // @Override
    // public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    // setBlocking(true);
    // return new DialogProjectsLoader(getActivity(), this);
    // }
    //
    // @Override
    // public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
    // setBlocking(false);
    // }
    //
    // @Override
    // public void onLoaderReset(Loader<Cursor> arg0) {
    // setBlocking(false);
    // }
}