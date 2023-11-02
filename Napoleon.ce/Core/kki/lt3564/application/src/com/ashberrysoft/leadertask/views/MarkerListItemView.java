package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class MarkerListItemView extends RelativeLayout implements OnClickListener, OnCheckedChangeListener {

    public interface OnMarkerListItemListener {
        public void onMarkerListItemClick(int position, boolean isChecked);
    }

    // VIEW's
    private TextView mTextView;
    private CheckBox mCheckBox;
    private RelativeLayout mStatusLayout;

    // VALUE
    private int mPosition;

    // LISTENER
    private OnMarkerListItemListener mListener;

    public MarkerListItemView(Context context) {
        super(context);
        initialization();
    }

    public MarkerListItemView(Context context, OnMarkerListItemListener listener) {
        super(context);

        initialization();
        setCustomListener(listener);
    }

    public MarkerListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialization();
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_marker, this);
        this.setOnClickListener(this);

        mTextView = (TextView) findViewById(R.id.status_title);
        mCheckBox = (CheckBox) findViewById(R.id.status_radio_button);
        mStatusLayout = (RelativeLayout) findViewById(R.id.status_layout);
        mCheckBox.setOnCheckedChangeListener(this);
    }

    public void setData(int position, boolean isChecked, Marker marker) {
        mPosition = position;
        mCheckBox.setChecked(isChecked);


        mTextView.setText(marker.isUppercase() ? marker.getName().toUpperCase() : marker.getName());
        try {
            if (marker.getBackColor() == null || Marker.NO_COLOR.equals(marker.getBackColor())) {
                mStatusLayout.setBackgroundColor(Color.TRANSPARENT);
            } else {
                String colorStr = marker.getBackColor();
                if (!colorStr.contains("#")) {
                    colorStr = "#"+colorStr;
                }
                final int color = Color.parseColor(colorStr);
                mStatusLayout.setBackgroundColor(color);
            }

            if (marker.getTextColor() == null || Marker.NO_COLOR.equals(marker.getTextColor())) {
                mTextView.setTextColor(Color.BLACK);

            } else {
                final int color = Color.parseColor(marker.getTextColor());
                mTextView.setTextColor(color);
            }
        } catch (Exception e) {

        }
        finally {

        }
    }

    @Override
    public void onClick(View v) {
        mCheckBox.setChecked(!mCheckBox.isChecked());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mListener != null) {
            mListener.onMarkerListItemClick(mPosition, isChecked);
        }
    }

    public void setCustomListener(OnMarkerListItemListener listener) {
        mListener = listener;
    }
}