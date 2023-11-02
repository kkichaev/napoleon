package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuTreeDataContainer;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.views.IDataView;


public class ContactsGroupListItem extends LinearLayout implements IDataView<ITreePureNode>, OnClickListener,
        OnCheckedChangeListener {

    // TODO Bug #3519 added interface and everething that wis it
    public interface OnContactsGroupListItemListener {
        public void onContactsGroupChecked(ContactsGroup contactsGroup, boolean isChecked);

        public void onContactsGroupOpen(ContactsGroup contactsGroup, boolean isCollapsed);
    }

    // VIEW's
    private ImageView mIcon;
    private TextView mTitle;
    private ImageView mDropDownView;
    private CheckBox mCheckbox;

    // VALUE's
    protected ContactsGroup mContactsGroup;
    private boolean mIsChecked;

    // LISTENER
    private OnContactsGroupListItemListener mListener;

    public ContactsGroupListItem(Context context) {
        super(context);
        initialization();
    }

    public ContactsGroupListItem(Context context, OnContactsGroupListItemListener listener) {
        super(context);

        setCustomListener(listener);
        initialization();
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_contacts_group_dialog, this);

        mDropDownView = (ImageView) findViewById(R.id.img_drop_down);
        mIcon = (ImageView) findViewById(R.id.icon_contacts_grop);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mCheckbox = (CheckBox) findViewById(R.id.checkbox);

        mDropDownView.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mCheckbox.setOnClickListener(this);
        mCheckbox.setOnCheckedChangeListener(this);
    }

    public void setChecked(ContactsGroup currentContactsGroup) {
        if (currentContactsGroup != null) {
            if (currentContactsGroup.getId() != null) {
                mIsChecked = currentContactsGroup != null && currentContactsGroup.getId().equals(mContactsGroup.getId());
                mCheckbox.setChecked(mIsChecked);
            }
        }
    }

    @Override
    public void setData(ITreePureNode data) {
        mContactsGroup = (ContactsGroup) data;
        if (mContactsGroup.getCreator().equals(LTSettings.getInstance().getUserName())) {
            if (mContactsGroup.getSharedUsers() != null) {
                mIcon.setImageResource(R.drawable.cg_shared);
            }
            else {
                mIcon.setImageResource(R.drawable.cg_my);
            }
        }
        else {
            mIcon.setImageResource(R.drawable.cg_avalibale);
        }
        final SlidingMenuTreeDataContainer container = (SlidingMenuTreeDataContainer) data;
        mTitle.setText(container.getName());
        if (!data.isExpandable()) {
            mDropDownView.setVisibility(INVISIBLE);
        } else {
            mDropDownView.setVisibility(VISIBLE);
            if (mContactsGroup.isExpanded()) {
                mDropDownView.setImageResource(R.drawable.arrow_down);
            } else {
                mDropDownView.setImageResource(R.drawable.arrow_right);
            }
        }
        final int paddingLeft = Utils.convertDipToPixels(getContext(), 15 * container.getIndent());
        setPadding(paddingLeft, 0, 0, 0);
    }

    public void setChecked(boolean isChecked) {
        mCheckbox.setChecked(isChecked);
    }

    @Override
    public ITreePureNode getData() {
        return mContactsGroup;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mCheckbox.setOnTouchListener(listener);
        mTitle.setOnTouchListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_drop_down:
        case R.id.icon:
            if (mListener != null) {
                mListener.onContactsGroupOpen(mContactsGroup, mContactsGroup.isCollapsed());
            }
            break;

        case R.id.txt_title:
            mCheckbox.setChecked(!mCheckbox.isChecked());
        default:
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        if (mListener != null) {
            if (mIsChecked) {
                mListener.onContactsGroupChecked(isChecked ? mContactsGroup : null, mIsChecked);
            } else {
                mListener.onContactsGroupChecked(isChecked ? mContactsGroup : null, isChecked);
            }
        }
    }

    public void setCustomListener(OnContactsGroupListItemListener listener) {
        mListener = listener;
    }
}