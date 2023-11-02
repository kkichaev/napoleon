package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * 
 * @since 2014-06-20
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class EmployeeListItemView extends LinearLayout implements OnClickListener {

    public interface OnEmployeeListItemViewListener {
        void onEmployeeCheckedChange(boolean isChecked, int position);
    }

    // VIEW's
    private TextView mTvTitle;
    private TextView mTvLogin;
    private CheckBox mCheckBox;
    private LTApplication mApp;
    private ImageView mImage;
    private ImageView mImageCustom;

    // VALUE's
    private int mPosition;

    // LISTENER
    private OnEmployeeListItemViewListener mListener;

    public EmployeeListItemView(Context context) {
        super(context);
        initialization();
        mApp = (LTApplication) getContext().getApplicationContext();
    }

    public EmployeeListItemView(Context context, OnEmployeeListItemViewListener listener) {
        this(context);
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_view_employee, this);
        // this.setPadding(getContext().getResources().getDimensionPixelSize(R.dimen.univ_padding), 0, 0, 0);
        this.setOrientation(HORIZONTAL);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvLogin = (TextView) findViewById(R.id.tv_login);
        mCheckBox = (CheckBox) findViewById(R.id.check_box);
        mImage = (ImageView) findViewById(R.id.image_view);
        mImageCustom = (ImageView) findViewById(R.id.image_view_custom);
       

        mCheckBox.setOnClickListener(this);
        this.setOnClickListener(this);
    }

    public void setData(Employee employee, boolean isChecked, int position, boolean isCustomer) {
        mPosition = position;

        if (TextUtils.isEmpty(employee.getName())//
                || employee.getName().equals(employee.getEmail())) {
            mTvTitle.setText(employee.getEmail());
            mTvLogin.setVisibility(View.GONE);
        } else {
            mTvTitle.setText(employee.getName());
            mTvLogin.setText(employee.getEmail());
            mTvLogin.setVisibility(View.VISIBLE);
        }

        //
        mImageCustom.setVisibility(INVISIBLE);
        try {
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, employee.getEmail());
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

        mCheckBox.setChecked(isChecked);
        if (isCustomer) {
            mCheckBox.setVisibility(VISIBLE);
        } else {
            mCheckBox.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        default:
            mCheckBox.setChecked(!mCheckBox.isChecked());
        case R.id.check_box:
            if (mListener != null) {
                mListener.onEmployeeCheckedChange(mCheckBox.isChecked(), mPosition);
            }
            break;
        }
    }

    public void setCustomListener(OnEmployeeListItemViewListener listener) {
        mListener = listener;
    }
}