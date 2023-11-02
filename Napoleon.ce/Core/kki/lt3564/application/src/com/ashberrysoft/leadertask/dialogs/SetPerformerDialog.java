package com.ashberrysoft.leadertask.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.EmployeeAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.v2soft.AndLib.ui.activities.IBaseActivity;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

/**
 * 
 * @since 2014-06-20
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SetPerformerDialog extends DialogFragment//
        implements DialogInterface.OnClickListener, OnEditorActionListener {

    private static final String CLASS_PATH = SetPerformerDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_PERFORMER = CLASS_PATH + "EXTRA_PERFORMER";
    private static final String EXTRA_TITLE = CLASS_PATH + "EXTRA_TITLE";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.performer_dialog_request_code;

    // VALUE's
    private Handler mHandler;
    private String mPerformer;
    private Thread mThread;
    private List<Employee> mEmployees;

    // ADAPTER
    private EmployeeAdapter mAdapter;

    public static SetPerformerDialog newInstance(Fragment fragment, String performer) {
        final Bundle b = new Bundle();
        if (!TextUtils.isEmpty(performer)) {
            b.putString(EXTRA_PERFORMER, performer);
        }

        final SetPerformerDialog d = new SetPerformerDialog();
        d.setTargetFragment(fragment, REQUEST_CODE);
        d.setArguments(b);

        return d;
    }

    public static SetPerformerDialog newInstanceCustomTitle(Fragment fragment, int titleId) {
        final Bundle b = new Bundle();
        b.putInt(EXTRA_TITLE, titleId);

        final SetPerformerDialog d = new SetPerformerDialog();
        d.setTargetFragment(fragment, REQUEST_CODE);
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mHandler = new Handler();
        final Bundle bundle = b != null ? b : getArguments();
        if (bundle.containsKey(EXTRA_PERFORMER)) {
            mPerformer = bundle.getString(EXTRA_PERFORMER);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle b) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_project_dialog, null);

        final Bundle bundle = b != null ? b : getArguments();
        final int titleId = bundle.getInt(EXTRA_TITLE, R.string.task_instruct);

        mAdapter = new EmployeeAdapter(getActivity());

        mEmployees = DbHelper.getListEmployees(getActivity());

        mAdapter.setData(mEmployees, mPerformer);

        final ListView listView = (ListView) v.findViewById(android.R.id.list);

        listView.setCacheColorHint(0);
        listView.setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(titleId);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, this);

        return ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }

        final String performer = mAdapter.getCheckedPerformer();
        if (!TextUtils.isEmpty(performer)) {
            b.putString(EXTRA_PERFORMER, performer);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (getTargetFragment() != null && which == Dialog.BUTTON_POSITIVE) {
            final String performer;
            if (mAdapter.getCheckedPerformer() == null) {
                performer = LTSettings.getInstance(getActivity()).getUserName();
            } else {
                performer = mAdapter.getCheckedPerformer();
            }

            if (getTargetFragment() instanceof BaseFragment) {
                ((BaseFragment<?, ?>) getTargetFragment()).onFragmentResult(performer, REQUEST_CODE);
            }
        }

        dismiss();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            onClick(null, Dialog.BUTTON_POSITIVE);
            return true;
        }

        return false;
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