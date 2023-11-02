package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.ashberrysoft.leadertask.R;
import com.v2soft.AndLib.ui.fragments.BaseFragment;


public class GenderDialog extends BaseDialog//
        implements CompoundButton.OnCheckedChangeListener {

    public static final int CODE = R.id.dialog_contact_gender;

    private static final String CLASS_PATH = GenderDialog.class.getSimpleName();
    private static final String EXTRA_GENDER = CLASS_PATH + "EXTRA_GENDER";

    // ADAPTER
    private RadioButton check1;
    private RadioButton check2;
    private RadioButton check3;
    private RadioButton check4;
    private int mChecked;


    public static GenderDialog newInstance(Fragment target, int gender) {
        final GenderDialog d = new GenderDialog();
        d.setTargetFragment(target, CODE);
        final Bundle b = new Bundle();
        b.putSerializable(EXTRA_GENDER, gender);
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final Bundle bundle = getArguments();
        if ( bundle != null) {
            mChecked = (int) bundle.get(EXTRA_GENDER);
        }
        else {
            mChecked = 4;
        }

    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.gender_dialog, null);
        check1 = (RadioButton) v.findViewById((R.id.checkBox1));
        check2 = (RadioButton) v.findViewById((R.id.checkBox2));
        check3 = (RadioButton) v.findViewById((R.id.checkBox3));
        check4 = (RadioButton) v.findViewById((R.id.checkBox4));

        if (mChecked == 1) {
            check1.setChecked(true);
        } else if (mChecked == 2) {
            check2.setChecked(true);
        } else if (mChecked == 3) {
            check3.setChecked(true);
        } else {
            check4.setChecked(true);
        }

        check1.setOnCheckedChangeListener(this);
        check2.setOnCheckedChangeListener(this);
        check3.setOnCheckedChangeListener(this);
        check4.setOnCheckedChangeListener(this);
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);

        return ad.show();
    }

    private void setCode () {
        if (getTargetFragment() instanceof BaseFragment) {
            ((BaseFragment<?, ?>) getTargetFragment()).onFragmentResult(mChecked, CODE);
            getDialog().dismiss();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        check1.setChecked(false);
        check2.setChecked(false);
        check3.setChecked(false);
        check4.setChecked(false);
        buttonView.setChecked(isChecked);

        if (isChecked) {
            if (buttonView.equals(check1)) {
                mChecked = 1;
            } else if (buttonView.equals(check2)) {
                mChecked = 2;
            } else if (buttonView.equals(check3)) {
                mChecked = 3;
            } else {
                mChecked = 4;
            }
            setCode();
        }
    }
}