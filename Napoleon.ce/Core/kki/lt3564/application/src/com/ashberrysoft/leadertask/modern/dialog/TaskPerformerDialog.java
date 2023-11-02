package com.ashberrysoft.leadertask.modern.dialog;

import java.util.List;
import android.annotation.SuppressLint;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.EmployeeAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskPerformerDialog extends BaseDialog//
        implements OnClickListener{

    private static final String EXTRA_PERFORMER = "EXTRA_PERFORMER";
    public static final int CODE = R.id.dialog_task_performer;
    public static final int CODE2 = R.id.dialog_task_performer2;
    private static int mCode;

    // VALUE's
    private String mPerformer;
    private List<Employee> mEmployees;
    private static Fragment mTarget;


    // ADAPTER
    private EmployeeAdapter mAdapter;

    public static TaskPerformerDialog newInstance(Fragment fragment, LTask task, boolean is2) {
        final Bundle b = new Bundle(1);
        b.putString(EXTRA_PERFORMER, task.getEmailPerformer());

        final TaskPerformerDialog d = new TaskPerformerDialog();
        if (is2) {
            mCode =CODE2;
        } else {
            mCode = CODE;
        }
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

        lv.addFooterView(footer);
        lv.setFooterDividersEnabled(false);
        ad.setView(v);
        //
        ad.setTitle(R.string.task_instruct);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);

        ad.setCancelable(true);
        if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
            ad.setNeutralButton(getResources().getString(R.string.btn_add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (getDialog() != null){
                        getDialog().cancel();
                        Utils.iWantToAddUsers(getActivity(), mTarget);
                    }
                }
            });
        }

        return ad.show();
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