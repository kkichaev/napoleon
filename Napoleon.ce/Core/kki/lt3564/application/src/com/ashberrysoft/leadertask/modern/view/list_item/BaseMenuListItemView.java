package com.ashberrysoft.leadertask.modern.view.list_item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;

public abstract class BaseMenuListItemView extends LinearLayout implements OnClickListener, View.OnLongClickListener {

    public interface OnMenuListItemListener {

        void onMenuClick(BaseMenuItem menuItem, int id);

        void onMenuLongClick(View v, BaseMenuItem menuItem, int id);

        void onDropDownClick(BaseMenuItem menuItem, boolean opened);

        void onDropDownClickHeader(BaseMenuItem menuItem, boolean opened);
    }

    private final OnMenuListItemListener mListener;

    public BaseMenuListItemView(Context context, OnMenuListItemListener listener) {
        super(context, (AttributeSet) null);
        mListener = listener;
    }

    public BaseMenuListItemView(Context context, AttributeSet attrs) {
        this(context, (OnMenuListItemListener) null);
    }

    public abstract void setData(BaseMenuItem menuItem, int number);

    protected OnMenuListItemListener getListener() {
        return mListener;
    }
}