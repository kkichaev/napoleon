package com.ashberrysoft.leadertask.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.modern.dialog.AddEmpDialog;
import com.ashberrysoft.leadertask.modern.helper.FullTasksResetHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EditEmpFragment extends BaseFeaturesFragment implements ListItemSimpleFeatureView.OnSimpleFeatureViewListener<Emp> {

    // VALUE's
    private Emp mEmpTemporary;
    private Runnable mRemoveEntityRun;
    private Runnable mGoUpEntityRun;
    private Runnable mGoDownEntityRun;

    private static final String CLASS_PATH = EditEmpFragment.class.getName();
    private static final String EXTRA_EMPS = CLASS_PATH + "EXTRA_EMPS";

    // VALUE's
    private MenuInflater mMenuInflater;
    private Emp mTempEmp;
    private int mTempPosition;

    // ADAPTER
    private SimpleFeatureListAdapter<Emp> mAdapter;

    public static EditEmpFragment newInstance() {
        return new EditEmpFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mTempEmp = b != null ? ((Emp) b.getSerializable(EXTRA_EMPS)) : null;
        mMenuInflater = getActivity().getMenuInflater();
        mAdapter = new SimpleFeatureListAdapter<Emp>(getActivity(), FeaturesActivity.FeatureType.EMP, this);
        setActionButtonListener();


        if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
            FeaturesActivity.mActionButton.setVisibility(View.VISIBLE);
        } else {
            FeaturesActivity.mActionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setBackgroundColor(getResources().getColor(R.color.white));
        mAdapter.setData(DbHelper.getListEmps(getActivity()));
        adapterNotifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable(EXTRA_EMPS, mTempEmp);
    }

    @Override
    public void onStop() {
        //mAdapter.clear();
        super.onStop();
    }

    public void updateListAfterDelUser() {
        Emp me = mAdapter.getItem(0);
        final ContentValues cv = new ContentValues();
        cv.put(EmpContract.USN_ENTITY, 0);
        cv.put(EmpContract.USN_FIELD_GENDER, me.getUsnFieldGender() + 1);
        mApp.getContentResolver().update(EmpContract.CONTENT_URI, cv, EmpContract.selectionLogin(LTSettings.getInstance().getUserName()), null);

        mApp.getContentResolver().delete(EmpContract.CONTENT_URI, EmpContract.selectionLogin(mTempEmp.getLogin()), null);
        mApp.getContentResolver().delete(LeaderTaskProviderMetaData.EmployeeContract.CONTENT_URI, LeaderTaskProviderMetaData.EmployeeContract.EMAIL+" = '"+mTempEmp.getLogin()+"'", null);

        mApp.getContentResolver().notifyChange(EmpContract.CONTENT_URI, null);
        mApp.getContentResolver().notifyChange(LeaderTaskProviderMetaData.EmployeeContract.CONTENT_URI, null);

        mAdapter.setData(DbHelper.getListEmps(getActivity()));
        adapterNotifyDataSetChanged();
        Utils.startSync(mApp);
    }

    public void updateListAfterUserAdd(Employee employee) {
        try {
            mAdapter.setData(DbHelper.getListEmps(getActivity()));
            adapterNotifyDataSetChanged();
        } finally {

        }
    }

    @Override
    public void onSimpleFeatureViewClick(Emp data) {
        openEmp((Emp) data);
    }

    @Override
    public void onSimpleFeatureViewLongClick(View v, Emp data, int position, Emp dataPrev, Emp dataPost) {
        mTempEmp = (Emp) data;
        mTempPosition = position;
        getActivity().openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (mTempEmp == null) {
            return;
        }

        mMenuInflater.inflate(R.menu.edit_feature_contextmenu, menu);
        setMenuForRoot(menu, LTSettings.getInstance().getUserName().equals(mTempEmp.getLogin()));
    }

    private void setMenuForRoot(ContextMenu menu, boolean me) {
        setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
        if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector()) && !me) {
            setMenuItemEnabled(menu.findItem(R.id.menu_dell), true);
        } else {
            setMenuItemEnabled(menu.findItem(R.id.menu_dell), false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_properties:
                openEmp(mTempEmp);
                return true;

            case R.id.menu_dell:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.menu_dell)+"?");
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        if (Utils.isNetworkAvailable(mApp)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // Add your data
                                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                                        nameValuePairs.add(new BasicNameValuePair("session", LTSettings.getInstance().getSessionUUID()));
                                        nameValuePairs.add(new BasicNameValuePair("login", LTSettings.getInstance().getUserProfile().getName()));
                                        nameValuePairs.add(new BasicNameValuePair("password", LTSettings.getInstance().getUserProfile().getPassword()));
                                        nameValuePairs.add(new BasicNameValuePair("email", mTempEmp.getLogin()));

                                        String message = OkHttpConnection.postWithParams(nameValuePairs, LTSettings.getInstance().getSyncDelEmp());
                                        message = message.substring(10, message.length()-2);
                                        if (message.equals("0") || message.equals("") || message.isEmpty()) {
                                            // збс
                                            updateListAfterDelUser();
                                        } else {
                                            // ошибка

                                        }

                                        Thread.sleep(3000);
                                        new FullTasksResetHelper(mApp, false);
                                    } catch (Exception e) {

                                    }
                                }
                            }).start();
                        } else {
                            Toast.makeText(mApp, R.string.error_internet_access, Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onDialogPositiveButton() {
        setBlockAtUI(true);

    }

    private void openEmp(Emp emp) {
        if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
            FeaturesActivity.hideActionButton();
        }
        startFragment(PropertiesEmpFragment.newInstance(emp, !isInEmps(emp)));
    }

    private boolean isInEmps(Emp empOpen) {
        Cursor c = null;
        Emp emp;
        try {
            c = mApp.getContentResolver().query(EmpContract.CONTENT_URI, null, null, null, EmpContract.ORDERS);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                emp = new Emp(c);
                if(emp.getLogin().equals(empOpen.getLogin())) {
                    return true;
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    @Override
    protected boolean onAddFeatureClick() {
        Utils.iWantToAddUsers(getActivity(), this);
        return true;
    }

    @Override
    protected View getListViewHeader() {
        return null;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.title_emp;
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.employee;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return true;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        return false;
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) {
        return false;
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    private void setActionButtonListener(){
        FeaturesActivity.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddFeatureClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
            FeaturesActivity.mActionButton.setVisibility(View.VISIBLE);
        } else {
            FeaturesActivity.mActionButton.setVisibility(View.GONE);
        }
    }
}