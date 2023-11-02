package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.ProjectMembersAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ProjectMembersDialog extends BaseDialog//
        implements OnClickListener {

    public static final int CODE = R.id.project_dialog_request_code;
    private static final String EXTRA_EMPS = "EXTRA_EMPS";

    private static final String SPLIT_SYMBOL = "\\.\\.";

    // VALUE's
    private String mSharedUsers;
    private static boolean mIsCustomer;
    private ArrayList<String> mPerformers;
    private static Fragment mTarget;
    private static Project mProject;

    // ADAPTER
    private ProjectMembersAdapter mAdapter;
    private List<Employee> mEmployees;

    public static ProjectMembersDialog newInstance(Fragment target, Project project, boolean isCustomer) {
        final Bundle b = new Bundle(1);
        mIsCustomer = isCustomer;
        if (project.getSharedUsers() != null) {
            b.putString(EXTRA_EMPS, project.getSharedUsers());
        }

        final ProjectMembersDialog d = new ProjectMembersDialog();
        d.setTargetFragment(target, CODE);
        d.setArguments(b);
        mTarget = target;
        mProject = project;
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        mSharedUsers = bundle.getString(EXTRA_EMPS);
        mPerformers = new ArrayList<String>();
        if (mSharedUsers != null) {
            final String[] users = mSharedUsers.split(SPLIT_SYMBOL);
            for (String u : users) {
                if (mIsCustomer) { // генерим список без себя, если мы можем менять
                    if (!u.equals(LTSettings.getInstance().getUserName())) {
                        mPerformers.add(u);
                    }
                } else { // генерим список с собой, если мы ничего не можем менять
                    mPerformers.add(u);
                }
            }
        }
        // делаем адаптер с сотрудниками
        mAdapter = new ProjectMembersAdapter(getActivity());

        mEmployees = DbHelper.getListEmployees(getActivity());

        if (!mIsCustomer) {
            EmployeeCache mEmployeeCache = EmployeeCache.getInstance(getActivity());
            mEmployeeCache.refreshCache();
            Employee me = new Employee();
            me.setEmail(LTSettings.getInstance().getUserName());
            me.setName(""+mEmployeeCache.find(getSettings().getUserName()));
            mEmployees.add(me);
        }

        mAdapter.setData(mEmployees, mPerformers, mIsCustomer);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_category_dialog, null);
        final ListView lv = (ListView) v.findViewById(R.id.list_categories);
        //
        final View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_members_dialog, null);
        //header.setOrientation(HORIZONTAL);
        TextView mTvTitle = (TextView) header.findViewById(R.id.title);
        TextView mTvEmail = (TextView) header.findViewById(R.id.email);
        ImageView mImage = (ImageView) header.findViewById(R.id.image_view);
        ImageView mImageCustom = (ImageView) header.findViewById(R.id.image_view_custom);
        String emailCreator = mProject.getCreator();
        String nameCreator = "";
        List <Employee> allEmps = DbHelper.getListEmployees(getActivity());
        for (Employee emp : allEmps) {
            if (emp.getEmail().equals(emailCreator)) {
                nameCreator = emp.getName();
            }
        }
        if (emailCreator.equals(LTSettings.getInstance().getUserName())) {
            mTvTitle.setText(nameCreator);
            EmployeeCache mEmployeeCache = EmployeeCache.getInstance(getActivity());
            mEmployeeCache.refreshCache();
            mTvTitle.setText(mEmployeeCache.find(getSettings().getUserName()));
        } else {
            mTvTitle.setText(nameCreator);
        }
        mTvEmail.setText(emailCreator);

        //
        mImageCustom.setVisibility(INVISIBLE);
        try {
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(getApp(), emailCreator);
            if (roundedBitmapDrawable != null) {
                mImage.setImageDrawable(roundedBitmapDrawable);
                mImageCustom.setVisibility(VISIBLE);
                mImageCustom.setImageResource(R.drawable.emp_circle_simple);
            } else {
                mImage.setImageResource(R.drawable.emp_simple);
            }
        }
        catch (Exception e) {
            mImage.setImageResource(R.drawable.emp_simple);
        }

        //

        lv.addHeaderView(header);
        lv.setCacheColorHint(0);
        lv.setAdapter(mAdapter);
        if (mEmployees.isEmpty() && LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
            View footer = LayoutInflater.from(getActivity()).inflate(R.layout.unboarding_dialog_footer, null);

            /*final Button addEmp = (Button) footer.findViewById(R.id.unbord_diag_button);
            final TextView textView = (TextView) footer.findViewById(R.id.unbord_diag_text);
            textView.setText(getResources().getString(R.string.unboarding_dialog_members));
            addEmp.setText(getResources().getString(R.string.add_emp));
            if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
                addEmp.setVisibility(VISIBLE);
            } else {
                addEmp.setVisibility(View.GONE);
            }
            addEmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDialog() != null){
                        getDialog().cancel();
                        AddEmpDialog.newInstance(mTarget).showDialog(getFragmentManager());
                    }
                }
            });*/
            ad.setView(footer);
        } else {
            ad.setView(v);
        }

        ad.setTitle(R.string.project_access);
        ad.setCancelable(true);
        if (mIsCustomer) {
            ad.setPositiveButton(R.string.btn_ok, this);
        }
        ad.setNegativeButton(R.string.btn_cancel, null);

        return ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(EXTRA_EMPS, getMembers());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            receiveObjects(CODE, getMembers());
        }
    }

    private String getMembers() {
        return mAdapter.getPerformers();
    }

}