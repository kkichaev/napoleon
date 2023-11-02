package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;

public class EditTaskFeatureHeaderView extends LinearLayout {

    // VIEW's
    private final ImageView mImageView;
    private final TextView mTextView;

    public EditTaskFeatureHeaderView(Context context) {
        super(context);

        inflate(getContext(), R.layout.view_edit_task_header, this);
        this.setOrientation(LinearLayout.VERTICAL);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mTextView = (TextView) findViewById(R.id.text_view);
        final View divider = findViewById(R.id.divider);

        if (LTSettings.getInstance(getContext()).isThemeDark()) {
            mTextView.setTextColor(Color.WHITE);
            divider.setBackgroundColor(Color.WHITE);

        } else {
            mTextView.setTextColor(Color.BLACK);
            divider.setBackgroundResource(R.color.divider_gray);
        }
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setImageResource(int resId) {
        getImageView().setImageResource(resId);
    }

    public void setText(CharSequence string) {
        getTextView().setText(string);
    }

    public void setText(int stringId) {
        setText(getContext().getString(stringId));
    }

    public void setHint(int stringId) {
        getTextView().setHint(stringId);
    }
}