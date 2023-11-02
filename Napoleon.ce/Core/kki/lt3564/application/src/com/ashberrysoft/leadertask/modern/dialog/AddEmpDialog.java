package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.fragments.EditEmpFragment;
import com.ashberrysoft.leadertask.modern.fragment.EditTaskFragment;
import com.ashberrysoft.leadertask.modern.helper.FullTasksResetHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.ashberrysoft.leadertask.modern.fragment.TasksFragment.mAdapter;

public class AddEmpDialog extends BaseDialog {

    public static final int CODE = R.id.dialog_add_emp;

    private static final String CLASS_PATH = AddEmpDialog.class.getSimpleName();

    // ADAPTER
    private EditText editText0;
    private EditText editText1;
    private static Context mContext;
    private static FragmentActivity mActivity;
    EditText edPassw;


    public static AddEmpDialog newInstance(Fragment target) {
        final AddEmpDialog d = new AddEmpDialog();
        mContext = target.getActivity().getApplicationContext();
        mActivity = target.getActivity();
        d.setTargetFragment(target, CODE);
        final Bundle b = new Bundle();
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_emp_dialog, null);
        editText1 = (EditText) v.findViewById((R.id.editText1));
        editText0 = (EditText) v.findViewById((R.id.editText0));
        edPassw = (EditText) v.findViewById((R.id.edPassw));

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(R.string.emp_new);
        ad.setView(v);
        ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (Utils.isNetworkAvailable(getApp())) {
                    final String mEmail = editText1.getText().toString().trim();
                    final String name = editText0.getText().toString().trim();
                    final String pwd= edPassw.getText().toString().trim();

                    if (pwd.length() < 8)
                        Toast.makeText(getApp(), R.string.error_format_password, Toast.LENGTH_SHORT).show();
                    if (mEmail.isEmpty()) {
                        Toast.makeText(getApp(), R.string.error_format_email, Toast.LENGTH_SHORT).show();
                    } else {
                        if (name.isEmpty()) {
                            Toast.makeText(getApp(), R.string.preview_slide3_edit_hint2, Toast.LENGTH_SHORT).show();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Employee employee = new Employee();
                                    try {
                                        // Add your data
                                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                                        nameValuePairs.add(new BasicNameValuePair("session", LTSettings.getInstance().getSessionUUID()));
                                        nameValuePairs.add(new BasicNameValuePair("login", getSettings().getUserProfile().getName()));
                                        nameValuePairs.add(new BasicNameValuePair("password", getSettings().getUserProfile().getPassword()));
                                        nameValuePairs.add(new BasicNameValuePair("email", mEmail));
                                        nameValuePairs.add(new BasicNameValuePair("name", name));
                                        nameValuePairs.add(new BasicNameValuePair("userpassword ", pwd));

                                        String message = OkHttpConnection.postWithParams(nameValuePairs, LTSettings.getInstance().getSyncAddEmp());
                                        message = message.substring(10, message.length() - 2);
                                        if (message.equals("0") || message.equals("") || message.isEmpty()) {
                                            // збс
                                            employee = new Employee();
                                            employee.setEmail(mEmail);
                                            employee.setName(name);

                                            LTSettings.allInvitedUsersWas.add(employee);
                                            LTSettings.needToShowAddMessage = true;

                                            if (LTSettings.needToShowToastAfterAddUser) {
                                                LTSettings.needToShowToastAfterAddUser = false;
                                                Utils.showUnbordingToasts(mActivity, 2);
                                            }

                                            ErrorDialog.newInstance(
                                                    mActivity.getResources().getString(R.string.add_new_user_message3, mEmail, pwd))
                                                    .showDialog(mActivity.getSupportFragmentManager());

                                            new FullTasksResetHelper(getApp(), false);

                                            //receiveObjects(TaskPerformerDialog.CODE, mEmail);

                                            Intent i = new Intent(EditTaskFragment.UPDATE_UI_ACTION);
                                            i.putExtra(EditTaskFragment.EMAIL, mEmail);

                                            mActivity.sendBroadcast(i);
                                        } else {
                                            // ошибка
                                            if (message.equals("Invalid email format")) {
                                                message = mActivity.getResources().getString(R.string.error_format_email);
                                            }
                                            if (
                                                    message.equals("The employee is already present in other organization")
                                                            || message.equals("The employee is already present in this organization")
                                                            || message.equals("The employee is the director of the organization")
                                                            || message.equals("in user's org present employees")) {
                                                message = mActivity.getResources().getString(R.string.error_addemp_org);
                                            }

                                            ErrorDialog.newInstance(message).showDialog(mActivity.getSupportFragmentManager());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            getDialog().dismiss();
                        }
                    }
                } else {
                    Toast.makeText(getApp(), R.string.error_internet_access, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad.setNegativeButton(R.string.btn_cancel, null);

        Dialog d = ad.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
        return d;
    }

    public static void updateListAfterUserAdd(Employee employee, Context context) {
        try {
            Emp emp = new Emp();
            emp.setLogin(employee.getEmail());
            emp.setUid(UUID.randomUUID());
            emp.setTitle(employee.getName());
            emp.setOrder(getMaxPosition(context)+1);
            emp.setUsnEntity(0);
            context.getContentResolver().insert(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, emp.getContentValues(null));
            //context.getContentResolver().insert(LeaderTaskProviderMetaData.EmployeeContract.CONTENT_URI, employee.getContentValues(null));

            context.getContentResolver().notifyChange(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null);
            //context.getContentResolver().notifyChange(LeaderTaskProviderMetaData.EmployeeContract.CONTENT_URI, null);
            //

        } finally {

        }
    }

    private static int getMaxPosition(Context context) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null, null, null, null);
            return c.getCount();
        } finally {
            if (c != null) {
                c.close();
            }
        }
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
    
}