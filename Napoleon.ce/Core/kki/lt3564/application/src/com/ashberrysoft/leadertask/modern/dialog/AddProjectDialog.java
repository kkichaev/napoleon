package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.fragment.BaseSyncStatusFragment;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ashberrysoft.leadertask.R.id.editText2;


public class AddProjectDialog extends BaseDialog {

    public static final int CODE = R.id.dialog_add_project;

    private static final String CLASS_PATH = AddProjectDialog.class.getSimpleName();

    // ADAPTER
    private EditText editText1;
    private static Context mContext;


    public static AddProjectDialog newInstance(Fragment target) {
        final AddProjectDialog d = new AddProjectDialog();
        mContext = target.getActivity().getApplicationContext();
        d.setTargetFragment(target, CODE);
        final Bundle b = new Bundle();
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final Bundle bundle = getArguments();
        if ( bundle != null) {

        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_project_dialog, null);
        editText1 = (EditText) v.findViewById((R.id.editText1));

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText1.getText().toString().trim().length() == 0) {
                    Utils.showToast(getActivity(), R.string.t_error_feature_name);
                } else {
                    if (LTSettings.needToShowToastAfterAddProject) {
                        LTSettings.needToShowToastAfterAddProject = false;
                        Utils.showUnbordingToasts(mContext, 1);
                    }
                    Project project = new Project();
                    setProjectParams(project);
                    project.setName(editText1.getText().toString());
                    saveProject(project);
                    if (getTargetFragment() instanceof BaseSyncStatusFragment) {
                        ((BaseSyncStatusFragment) getTargetFragment()).onFragmentResult(project, CODE);
                    }
                    getDialog().dismiss();
                }
            }
        });
        ad.setNegativeButton(R.string.btn_cancel, null);

        Dialog d = ad.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
        return d;
    }
    
    
    private void setProjectParams(Project project) {
        project.setSharedUsers(null);

        project.setUsn(0);
        project.setUsnName(project.getUsnName() + 1);
        project.setUsnSharedUsers(project.getUsnSharedUsers() + 1);
        project.setUsnComment(project.getUsnComment() + 1);

        project.setId(UUID.randomUUID());
        project.setCreator(LTSettings.getInstance().getUserName());

        project.setOrder(getOrder(mContext) + 1);
        project.setUsnOrder(project.getUsnOrder() + 1);
    }

    private int getOrder(Context context) {
        int order = 0;

        final List<Project> projects;
        try {
            projects = DbHelper.getInstance(context).getProjectDao().queryForAll();
        } catch (SQLException e) {
            return order;
        }
        Collections.sort(projects);

        for (Project p : projects) {
            if (TextUtils.isEmpty(p.getName()) || !LTSettings.getInstance().getUserName().equals(p.getCreator())) {
                continue;
            } else {
                if (p.getParentId() == null) {
                    if (order < p.getOrder()) {
                        order = p.getOrder();
                    }
                }
            }
        }
        return order;
    }

    private void saveProject(Project project) {
        try {
            DbHelper.getInstance(getApp()).getProjectDao().create(project);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(getApp());
        linkHelper.createTotalLink(project);

        Utils.startSync(getApp());
    }

}