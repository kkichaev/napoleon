package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CategoryAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.CategoriesRootTreeItem;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SetCommunicationDialog extends BaseDialog//
        implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final int CODE = R.id.communication_dialog_request_code;


    // ADAPTER
    private RadioButton check1;
    private RadioButton check2;
    private RadioButton check3;
    private RadioButton check4;
    private EditText editTextValue;
    private EditText editTextName;


    public static SetCommunicationDialog newInstance(Fragment target) {
        final SetCommunicationDialog d = new SetCommunicationDialog();
        d.setTargetFragment(target, CODE);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);


    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.communication_dialog, null);
        check1 = (RadioButton) v.findViewById((R.id.checkBox1));
        check1.setChecked(true);
        check2 = (RadioButton) v.findViewById((R.id.checkBox2));
        check3 = (RadioButton) v.findViewById((R.id.checkBox3));
        check4 = (RadioButton) v.findViewById((R.id.checkBox4));
        editTextValue = (EditText) v.findViewById((R.id.value));
        editTextName = (EditText) v.findViewById((R.id.name));
        check1.setOnCheckedChangeListener(this);
        check2.setOnCheckedChangeListener(this);
        check3.setOnCheckedChangeListener(this);
        check4.setOnCheckedChangeListener(this);
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.contact_connection);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);

        return ad.show();
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (editTextValue.getText().toString().trim().length() > 0) {
                if (getTargetFragment() instanceof BaseFragment) {
                    String comm = getTypeComm() + "\t" + editTextValue.getText().toString().trim() + "\t" + editTextName.getText().toString().trim();
                    ((BaseFragment<?, ?>) getTargetFragment()).onFragmentResult(comm, CODE);
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.t_error_not_all_information), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getTypeComm() {
        String type = "";
        if (check1.isChecked()) {
            type = "tel";
        } else {
            if (check2.isChecked()) {
                type = "eml";
            } else {
                if (check3.isChecked()) {
                    type = "msg";
                } else {
                    type = "www";
                }
            }
        }
        return type;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        check1.setChecked(false);
        check2.setChecked(false);
        check3.setChecked(false);
        check4.setChecked(false);
        buttonView.setChecked(isChecked);
    }
}