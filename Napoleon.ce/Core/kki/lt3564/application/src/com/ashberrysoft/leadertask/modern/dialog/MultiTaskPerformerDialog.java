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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.EmployeeAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.List;

public class MultiTaskPerformerDialog extends BaseDialog//
        implements OnClickListener{

    private static final String EXTRA_PERFORMER = "EXTRA_PERFORMER";
    public static final int CODE = R.id.multi_dialog_task_performer;
    private static int mCode;

    // VALUE's
    private String mPerformer;
    private List<Employee> mEmployees;
    private static Fragment mTarget;


    // ADAPTER
    private EmployeeAdapter mAdapter;

    public static MultiTaskPerformerDialog newInstance(Fragment fragment) {
        final Bundle b = new Bundle(1);
        b.putString(EXTRA_PERFORMER, "");

        final MultiTaskPerformerDialog d = new MultiTaskPerformerDialog();
        mCode = CODE;
        d.setTargetFragment(fragment, mCode);

        d.setArguments(b);
        mTarget = fragment;

        return d;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mAdapter = new EmployeeAdapter(getActivity());

        final Bundle bundle = b != null ? b : getArguments();
        mPerformer = bundle.getString(EXTRA_PERFORMER);

        mEmployees = DbHelper.getListEmployees(getApp());

        mAdapter.setData(mEmployees, mPerformer);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_performer_dialog, null);

        final ListView lv = (ListView) v.findViewById(R.id.list_performer);
        lv.setAdapter(mAdapter);
        //
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.unboarding_dialog_footer, null);
        final TextView textView = (TextView) footer.findViewById(R.id.unbord_diag_text);
        if (mEmployees.isEmpty()) {;
            textView.setText(getResources().getString(R.string.unboarding_dialog_performer));
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        /*final Button addEmp = (Button) footer.findViewById(R.id.unbord_diag_button);
        addEmp.setText(getResources().getString(R.string.add_emp));
        if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
            addEmp.setVisibility(View.VISIBLE);
        } else {
            addEmp.setVisibility(View.GONE);
        }
        addEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null){
                    getDialog().cancel();
                    Utils.iWantToAddUsers(getActivity(), mTarget);
                }
            }
        });
*/
        lv.addFooterView(footer);
        lv.setFooterDividersEnabled(false);
        ad.setView(v);
        //
        ad.setTitle(R.string.task_instruct);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);

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

        b.putString(EXTRA_PERFORMER, mAdapter.getCheckedPerformer());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            String performer = mAdapter.getCheckedPerformer();
            if (performer == null) {
                performer = getSettings().getUserName();
            }

            receiveObjects(mCode, performer);
        }
    }
}