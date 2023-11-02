package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Status list item view.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class StatusListItem extends RelativeLayout implements IDataView<TaskStatus>,
        OnCheckedChangeListener {

    public interface OnStatusListItemListener {
        void onStatusClick(int position);

        boolean isSeriesTask();
    }

    // VIEW's
    private ImageView mIcon;
    private TextView mTitle;
    private RadioButton mRb;

    // VALUE's
    private TaskStatus mStatus;
    private int mPosition;

    // LISTENER
    private OnStatusListItemListener mListener;

    public StatusListItem(Context context) {
        super(context);
        initialization();
    }

    public StatusListItem(Context context, OnStatusListItemListener listener) {
        this(context);
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_status, this);
        mIcon = (ImageView) findViewById(R.id.status_icon);
        mTitle = (TextView) findViewById(R.id.status_title);
        mRb = (RadioButton) findViewById(R.id.status_radio_button);
        mRb.setOnCheckedChangeListener(this);
    }

    @Override
    public void setData(TaskStatus data) {
        mStatus = data;

        mIcon.setImageResource(mListener.isSeriesTask() ? mStatus.getSeriesResId() : mStatus.getResId());
        mTitle.setText(mStatus.getTextId());

        mRb.setChecked(false);
    }

    @Override
    public TaskStatus getData() {
        return mStatus;
    }

    public void setChecked(int position, boolean checked) {
        mPosition = position;
        if (mRb.isChecked() && checked) {
            return;
        }

        mRb.setChecked(checked);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && mListener != null) {
            mListener.onStatusClick(mPosition);
        }
    }

    public OnStatusListItemListener getCustomListener() {
        return mListener;
    }

    public void setCustomListener(OnStatusListItemListener customListener) {
        mListener = customListener;
    }
}