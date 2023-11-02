package com.ashberrysoft.leadertask.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.fragments.PropertiesEmpFragment;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.dialog.AddressDialog;
import com.ashberrysoft.leadertask.modern.dialog.GenderDialog;
import com.ashberrysoft.leadertask.modern.dialog.PickDateDialog;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @since 2014-06-20
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PropertiesEmpHeaderView extends LinearLayout implements View.OnClickListener {

    // VIEW's
    private final EditText mDisplayAs;
    private final TextView mNameOnServer;
    private ImageView mImage;

    private Dialog mDialogFotoSet;


    private PropertiesEmpFragment mFragment;

    // VALUE
    private final EmployeeCache mCachedEmployee;

    public PropertiesEmpHeaderView(Context context, Emp emp, boolean empNew, PropertiesEmpFragment fragment) {
        this(context, fragment);
        setData(emp, empNew);
    }

    public PropertiesEmpHeaderView(Context context, PropertiesEmpFragment fragment) {
        super(context);
        mFragment = fragment;
        inflate(getContext(), R.layout.view_header_emp_properties, this);
        this.setOrientation(VERTICAL);

        mDisplayAs = (EditText) findViewById(R.id.etDisplayAs);
        mNameOnServer = (TextView) findViewById(R.id.etNameOnServer);

        mImage = ((ImageView) findViewById(R.id.iv_feature_img));


        final int textColor = LTSettings.getInstance(getContext()).isThemeDark() ? Color.WHITE : Color.BLACK;
        mDisplayAs.setTextColor(textColor);
        mNameOnServer.setTextColor(textColor);


        mImage.setOnClickListener(this);

        mCachedEmployee = EmployeeCache.getInstance(getContext());
    }

    public void setData(Emp emp, boolean empNew) {
        if (emp.getLogin() != null && emp.getLogin().equals(LTSettings.getInstance().getUserName())) {
            mDisplayAs.setText(mCachedEmployee.find(emp.getLogin()));
        } else if (TextUtils.isEmpty(emp.getTitle())) {
            mDisplayAs.setText(SharedStrings.EMPTY);
        } else {
            mDisplayAs.setText(emp.getTitle());
        }

        resetFoto(emp.getLogin());

        if (emp.getLogin() != null) {
            mNameOnServer.setText(emp.getLogin());
        } else {
            mNameOnServer.setText(SharedStrings.EMPTY);
        }
        mNameOnServer.setEnabled(empNew);
        //

        if (!emp.getLogin().equals(LTSettings.getInstance().getUserName())) {
            mImage.setClickable(false);
        }
        //
    }

    public void resetFoto(String login) {
        LTApplication mApp = (LTApplication) getContext().getApplicationContext();
        RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, login);
        if(roundedBitmapDrawable != null) {
            mImage.setImageDrawable(roundedBitmapDrawable);
        }
        else {
            mImage.setImageResource(R.drawable.emp_simple);
        }
    }

    public void getData(Emp emp, boolean empNew) {
        emp.setTitle(mDisplayAs.getText().toString().trim());
        if (empNew) {
            emp.setLogin(mNameOnServer.getText().toString().trim().toLowerCase());
        }
    }


    public EditText getEditText() {
        return mDisplayAs;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_feature_img:
            final Activity activity = (Activity) getContext();
            final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
            //
            final String[] mCatsName ={activity.getString(R.string.foto_set), activity.getString(R.string.foto_del)};
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setItems(mCatsName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                        ChooseSupportType(item);
                    }
            });
            builder.setCancelable(true);
            mDialogFotoSet = builder.create();
            mDialogFotoSet.show();
            //
            break;
        default:
            break;
        }
    }

    private void ChooseSupportType(int item)
    {
        switch (item) {
           case 0:
               dismissFotoSetDialog();
               final Intent intent = new Intent(Intent.ACTION_PICK);
               intent.setType(SharedStrings.MIME_TYPE_IMAGE);
               mFragment.startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.title_chooser_image)), 1);
            break;

            case 1:
                dismissFotoSetDialog();
                mFragment.deletePhoto();
            break;
        }
    }

    private void dismissFotoSetDialog() {
        if (mDialogFotoSet != null) {
            mDialogFotoSet.dismiss();
        }
        mDialogFotoSet = null;
    }

}