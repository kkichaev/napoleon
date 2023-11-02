package com.ashberrysoft.leadertask.modern.dialog;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.ProjectsTreeAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskProjectDialog extends BaseDialog//
        implements OnClickListener {
    // , LoaderCallbacks<Cursor>, OnDialogLoadListener<Project> {

    public static final int CODE = R.id.dialog_task_project;
    public static final int CODE2 = R.id.dialog_task_project;

    private static int mCode;
    private static final String EXTRA_PROJECT_UID = "EXTRA_PROJECT_UID";
    // private static final String EXTRA_PROJECTS = "EXTRA_PROJECTS";

    // VALUE's
    private String mUidProjeect;
    private static Fragment mTarget;
    // private List<Project> mProjects;

    // ADAPTER
    private ProjectsTreeAdapter mAdapter;

    public static TaskProjectDialog newInstance(Fragment target, LTask task, boolean is2) {
        final Bundle b = new Bundle(1);
        if (task.getUidProject() != null) {
            b.putString(EXTRA_PROJECT_UID, task.getUidProject());
        }

        final TaskProjectDialog d = new TaskProjectDialog();
        if (is2) {
            mCode =CODE2;
        } else {
            mCode = CODE;
        }
        d.setTargetFragment(target, 0);
        d.setArguments(b);
        mTarget = target;

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
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_project_dialog, null);

        final ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setCacheColorHint(0);
        lv.setAdapter(mAdapter);
        //
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.unboarding_dialog_footer, null);
        final TextView textView = (TextView) footer.findViewById(R.id.unbord_diag_text);
        if (mAdapter.isEmpty()) {;
            textView.setText(getResources().getString(R.string.unboarding_dialog_projects));
            textView.setVisibility(View.VISIBLE);

        } else {
            textView.setVisibility(View.GONE);
        }

        lv.addFooterView(footer);
        lv.setFooterDividersEnabled(false);
        ad.setView(v);
        //
        ad.setTitle(R.string.default_project);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);
        ad.setNeutralButton(getResources().getString(R.string.btn_add), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (getDialog() != null){
                    getDialog().cancel();
                    AddProjectDialog.newInstance(mTarget).showDialog(getFragmentManager());
                }
            }
        });

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
            receiveObjects(mCode, mAdapter.getSelectedProject());
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