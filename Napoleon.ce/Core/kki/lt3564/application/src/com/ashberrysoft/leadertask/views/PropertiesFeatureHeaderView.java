package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PropertiesFeatureHeaderView extends LinearLayout//
        implements OnClickListener, OnCheckedChangeListener {

    public interface OnFeaturePropertiesHeaderListener {

        public void onFeaturePropertiesChecked(int id, boolean isChecked);
    }

    // VIEW's
    private final EditText mEtName;

    private final EditText mEtComment;
    private final View mDivComment;

    private final View mLlColorText;
    private final TextView mTvColorText;
    private final View mColorText;
    private final View mDivColorText;

    private final View mLlColorBack;
    private final TextView mTvColorBack;
    private final View mColorBack;
    private final View mDivColorBack;

    private final View mLlClose;
    private final TextView mTvClose;
    private final CheckBox mCbClose;

    private final View mLlProjectTasks;
    private final TextView mTvProjectTasks;
    private final CheckBox mCbProjectTasks;
    private final View mDivProjectTasks;

    private final View mDivClose;

    // VALUE's
    private final LTSettings mSettings;

    // LISTENER
    private OnFeaturePropertiesHeaderListener mListener;

    public PropertiesFeatureHeaderView(Context context, OnFeaturePropertiesHeaderListener listener) {
        this(context);
        setCustomListener(listener);
    }

    public PropertiesFeatureHeaderView(Context context) {
        super(context);

        inflate(getContext(), R.layout.view_header_dao_properties, this);
        this.setOrientation(VERTICAL);

        mEtName = (EditText) findViewById(R.id.etName);

        mEtComment = (EditText) findViewById(R.id.etComment);
        mDivComment = findViewById(R.id.divComment);

        mLlColorText = findViewById(R.id.llColorText);
        mTvColorText = (TextView) findViewById(R.id.tvColorText);
        mColorText = findViewById(R.id.vColorText);
        mDivColorText = findViewById(R.id.divColorText);

        mLlColorBack = findViewById(R.id.llColorBack);
        mTvColorBack = (TextView) findViewById(R.id.tvColorBack);
        mColorBack = findViewById(R.id.vColorBack);
        mDivColorBack = findViewById(R.id.divColorBack);

        mLlClose = findViewById(R.id.llClose);
        mTvClose = (TextView) findViewById(R.id.tvClose);
        mCbClose = (CheckBox) findViewById(R.id.cbClose);
        mLlProjectTasks = findViewById(R.id.checkProjectTasks);
        mTvProjectTasks = (TextView) findViewById(R.id.tvProjectTasks);
        mCbProjectTasks = (CheckBox) findViewById(R.id.cbProjectTasks);

        mDivClose = findViewById(R.id.divClose);
        mDivProjectTasks = findViewById(R.id.divProjectTasks);

        mSettings = LTSettings.getInstance(getContext());
        {
            final int textColor = mSettings.isThemeDark() ? Color.WHITE : Color.BLACK;

            mEtName.setTextColor(textColor);
            mEtComment.setTextColor(textColor);
            mTvClose.setTextColor(textColor);
            mTvColorText.setTextColor(textColor);
            mTvColorBack.setTextColor(textColor);
        }

        mCbClose.setOnCheckedChangeListener(this);
        mCbProjectTasks.setOnCheckedChangeListener(this);

        mLlColorText.setOnClickListener(this);
        mLlColorBack.setOnClickListener(this);
        mLlClose.setOnClickListener(this);
        mLlProjectTasks.setOnClickListener(this);
    }

    public void setProjectData(Project project) {
        setName(project.getName());
        if (!TextUtils.isEmpty(project.getComment())) {
            mEtComment.setText(project.getComment());
        }
        mLlClose.setVisibility(View.VISIBLE);
        mEtComment.setVisibility(View.GONE);
        mDivComment.setVisibility(View.GONE);
        mCbClose.setChecked(project.isQuiet());
        mCbProjectTasks.setChecked(project.isGroup());
    }

    public void setContactsGroupData(ContactsGroup contactsGroup) {
        setName(contactsGroup.getName());
        if (!TextUtils.isEmpty(contactsGroup.getComment())) {
            mEtComment.setText(contactsGroup.getComment());
        }
        mLlClose.setVisibility(View.GONE);
        mDivClose.setVisibility(View.GONE);
    }

    public void setCategoryData(Category category) {
        mLlColorBack.setVisibility(View.VISIBLE);
        mDivColorBack.setVisibility(View.VISIBLE);
        mEtComment.setVisibility(View.GONE);
        mDivComment.setVisibility(View.GONE);

        mLlClose.setVisibility(View.GONE);
        mDivClose.setVisibility(View.GONE);


        setName(category.getName());
        setColorBack(category.getColor());
        if (!TextUtils.isEmpty(category.getComment())) {
            mEtComment.setText(category.getComment());
        }
    }

    public void setMarkerData(Marker marker) {
        mLlColorText.setVisibility(View.VISIBLE);
        mDivColorText.setVisibility(View.VISIBLE);
        mLlColorBack.setVisibility(View.VISIBLE);
        mDivColorBack.setVisibility(View.VISIBLE);

        mEtComment.setVisibility(View.GONE);
        mDivComment.setVisibility(View.GONE);


        setName(marker.getName());

        mTvClose.setText(R.string.marker_all_caps);
        mCbClose.setChecked(marker.isUppercase());

        setColorText(marker.getTextColor());
        setColorBack(marker.getBackColor());
    }

    private void setName(String name) {
        mEtName.requestFocus();
        if (!TextUtils.isEmpty(name)) {
            mEtName.setText(name);
            mEtName.setSelection(name.length());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.llClose:
            mCbClose.setChecked(!mCbClose.isChecked());
            break;
        case R.id.checkProjectTasks:
            mCbProjectTasks.setChecked(!mCbProjectTasks.isChecked());
            break;

        case R.id.llColorText:
        case R.id.llColorBack:
            if (mListener != null) {
                mListener.onFeaturePropertiesChecked(v.getId(), false);
            }
            break;

        default:
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        if (mListener != null) {
            mListener.onFeaturePropertiesChecked(v.getId(), isChecked);
        }
    }

    public void setCustomListener(OnFeaturePropertiesHeaderListener listener) {
        mListener = listener;
    }

    public String getName() {
        return mEtName.getText().toString();
    }

    public String getComment() {
        return mEtComment.getText().toString();
    }

    public void setColorText(String color) {
        setColorProperties(mColorText, color, true);
    }

    public void setColorBack(String color) {
        setColorProperties(mColorBack, color, false);
    }

    private void setColorProperties(View v, String color, boolean text) {
        try {
            if (color == null || Marker.NO_COLOR.equals(color)) {
                v.setBackgroundColor(Color.TRANSPARENT);

            } else {
                String colorStr = color;
                if (colorStr != null) {
                    if (!colorStr.contains("#")) {
                        colorStr = "#" + colorStr;
                    }
                }

                v.setBackgroundColor(Color.parseColor(colorStr));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public EditText getEditText() {
        return mEtName;
    }
}