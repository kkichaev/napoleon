package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

import java.util.ArrayList;


public class AddressDialog extends BaseDialog {

    public static final int CODE = R.id.dialog_contact_address;
    public static final int CODE_WORK = R.id.dialog_contact_address_work;

    private static final String CLASS_PATH = AddressDialog.class.getSimpleName();
    private static final String EXTRA_ARRAY_ADDRESS = CLASS_PATH + "EXTRA_ARRAY_ADDRESS";
    private static final String EXTRA_IS_HOME_ADDRESS = CLASS_PATH + "EXTRA_IS_HOME_ADDRESS";

    // ADAPTER
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private ArrayList <String> mAddress;
    private boolean mIsHome;


    public static AddressDialog newInstance(Fragment target, ArrayList <String> list, boolean isHome) {
        final AddressDialog d = new AddressDialog();
        d.setTargetFragment(target, CODE);
        final Bundle b = new Bundle();
        b.putSerializable(EXTRA_ARRAY_ADDRESS, list);
        b.putSerializable(EXTRA_IS_HOME_ADDRESS, isHome);
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final Bundle bundle = getArguments();
        if ( bundle != null) {
            mAddress = (ArrayList) bundle.get(EXTRA_ARRAY_ADDRESS);
            mIsHome = (boolean) bundle.get(EXTRA_IS_HOME_ADDRESS);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.address_dialog, null);
        editText1 = (EditText) v.findViewById((R.id.editText1));
        editText2 = (EditText) v.findViewById((R.id.editText2));
        editText3 = (EditText) v.findViewById((R.id.editText3));
        editText4 = (EditText) v.findViewById((R.id.editText4));
        editText5 = (EditText) v.findViewById((R.id.editText5));

        editText1.setText(mAddress.get(0));
        editText2.setText(mAddress.get(1));
        editText3.setText(mAddress.get(2));
        editText4.setText(mAddress.get(3));
        editText5.setText(mAddress.get(4));

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(mIsHome ? R.string.contact_home_adress : R.string.contact_work_adress);
        ad.setView(v);
        ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getTargetFragment() instanceof BaseFragment) {
                    ArrayList <String> array = new ArrayList<>(0);
                    array.add(editText1.getText().toString());
                    array.add(editText2.getText().toString());
                    array.add(editText3.getText().toString());
                    array.add(editText4.getText().toString());
                    array.add(editText5.getText().toString());
                    ((BaseFragment<?, ?>) getTargetFragment()).onFragmentResult(array, mIsHome ? CODE : CODE_WORK);
                    getDialog().dismiss();
                }
            }
        });
        ad.setNegativeButton(R.string.btn_cancel, null);
        return ad.show();
    }
}