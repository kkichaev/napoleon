package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.List;

public class PerformerListItemViewContactsGroup extends RelativeLayout implements OnClickListener {

    public interface OnPerformerListItemListener {
        public void onPerformerRemove(int position);
    }

    // VIEW
    private TextView mName;
    private ImageView mImage;
    private ImageView mImageCustom;
    private ImageView mImageDel;

    // VALUE's
    private int mPosition;

    // LISTENER
    private OnPerformerListItemListener mListener;

    public PerformerListItemViewContactsGroup(Context context) {
        super(context);
        initialization();
    }

    public PerformerListItemViewContactsGroup(Context context, OnPerformerListItemListener listener) {
        super(context);

        initialization();
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_performer, this);

        mName = (TextView) findViewById(R.id.text_view);
        mImage = (ImageView) findViewById(R.id.image_view);
        mImageCustom = (ImageView) findViewById(R.id.iv_img_custom);
        mImageDel = (ImageView) findViewById(R.id.image_view_remove);
        mName.setTextColor(Color.BLACK);
        mImageDel.setImageResource(R.drawable.file_remove_gray);
        mImageDel.setOnClickListener(this);
    }

    public void setData(int position, String performer, List <Employee> employees, Context context, ContactsGroup contactsGroup) {
        LTApplication mApp = (LTApplication) context.getApplicationContext();
        Employee currentEmployee = new Employee();
        for (Employee employee: employees) {
            if (employee.getEmail().equals(performer)) {
                currentEmployee = employee;
            }
        }
        if (currentEmployee.getEmail() == null && currentEmployee.getName() == null) {
            currentEmployee.setEmail(performer);
        }
        mPosition = position;
        mImageCustom.setVisibility(INVISIBLE);
        if (currentEmployee.getName() != null) {
            mName.setText(currentEmployee.getName());
        }
        else {
            mName.setText(currentEmployee.getEmail());
        }
        //
        try {
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, currentEmployee.getEmail());
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

        /*if (contactsGroup.getCreator().equals(LTSettings.getInstance().getUserName()) || currentEmployee.getEmail().equals(LTSettings.getInstance().getUserName())){
            mImageDel.setVisibility(VISIBLE);
        } else {
            mImageDel.setVisibility(GONE);
        }*/
        //
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
            case R.id.image_view_remove:
                mListener.onPerformerRemove(mPosition);
            default:
                break;
            }
        }
    }

    public void setCustomListener(OnPerformerListItemListener listener) {
        mListener = listener;
    }
}