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
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PerformerListItemView extends RelativeLayout implements OnClickListener {

    public interface OnPerformerListItemListener {
        public void onPerformerRemove(int position);
    }

    // VIEW
    private TextView mName;
    private ImageView mImage;
    private ImageView mImageCustom;

    // VALUE's
    private int mPosition;

    // LISTENER
    private OnPerformerListItemListener mListener;

    public PerformerListItemView(Context context) {
        super(context);
        initialization();
    }

    public PerformerListItemView(Context context, OnPerformerListItemListener listener) {
        super(context);

        initialization();
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_performer, this);

        mName = (TextView) findViewById(R.id.text_view);
        mImage = (ImageView) findViewById(R.id.image_view);
        mImageCustom = (ImageView) findViewById(R.id.iv_img_custom);
        final ImageView remove = (ImageView) findViewById(R.id.image_view_remove);
        mName.setTextColor(Color.BLACK);
        remove.setImageResource(R.drawable.file_remove_gray);
        remove.setOnClickListener(this);
    }

    public void setData(int position, String performer, List <Employee> employees, Context context) {
        LTApplication mApp = (LTApplication) context.getApplicationContext();
        Employee currentEmployee = new Employee();
        for (Employee employee: employees) {
            if (employee.getEmail().equals(performer)) {
                currentEmployee = employee;
            }
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