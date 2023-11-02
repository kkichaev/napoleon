package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
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
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuTreeDataContainer;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.views.IDataView;


public class ContactListItem extends LinearLayout implements OnClickListener,
        OnCheckedChangeListener {

    // TODO Bug #3519 added interface and everething that wis it
    public interface OnContactListItemListener {
        public void onContactChecked(Contact contact, boolean isChecked);

        public void onContactOpen(Contact contact);
    }

    // VIEW's
    private ImageView mIcon;
    private TextView mTitle;
    private ImageView mDropDownView;
    private CheckBox mCheckbox;

    // VALUE's
    protected Contact mContact;

    // LISTENER
    private OnContactListItemListener mListener;

    public ContactListItem(Context context) {
        super(context);
        initialization();
    }

    public ContactListItem(Context context, OnContactListItemListener listener) {
        super(context);

        setCustomListener(listener);
        initialization();
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_sliding_menu_for_contact_dialog, this);

        //mDropDownView = (ImageView) findViewById(R.id.img_drop_down);
        mIcon = (ImageView) findViewById(R.id.icon_contact);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mCheckbox = (CheckBox) findViewById(R.id.checkbox);

        //mDropDownView.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mCheckbox.setOnClickListener(this);
        mCheckbox.setOnCheckedChangeListener(this);
    }

    public void setData(Contact data) {
        mContact = data;
        mTitle.setText(mContact.getTitle());
        //
        LTApplication mApp = (LTApplication) getContext().getApplicationContext();
        RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, mContact.getId().toString());
        if(roundedBitmapDrawable != null) {
            mIcon.setImageDrawable(roundedBitmapDrawable);
        }
        else {

            if (mContact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                switch (mContact.getGender()) {
                    case 1:
                        mIcon.setImageResource(R.drawable.c_men);
                        break;
                    case 2:
                        mIcon.setImageResource(R.drawable.c_women);
                        break;
                    case 3:
                        mIcon.setImageResource(R.drawable.c_org);
                        break;
                    default:
                        mIcon.setImageResource(R.drawable.c_nobody);
                        break;
                }
            }
            else {
                switch (mContact.getGender()) {
                    case 1:
                        mIcon.setImageResource(R.drawable.c_men_avaleble);
                        break;
                    case 2:
                        mIcon.setImageResource(R.drawable.c_women_avaleble);
                        break;
                    case 3:
                        mIcon.setImageResource(R.drawable.c_org_avaleble);
                        break;
                    default:
                        mIcon.setImageResource(R.drawable.c_nobody_avaleble);
                        break;
                }
            }
        }
        //
    }

    public void setChecked(boolean isChecked) {
        mCheckbox.setChecked(isChecked);
    }


    public Contact getData() {
        return mContact;
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
                    mListener.onContactOpen(mContact);
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
            mListener.onContactChecked(mContact, isChecked);
        }
    }

    public void setCustomListener(OnContactListItemListener listener) {
        mListener = listener;
    }
}