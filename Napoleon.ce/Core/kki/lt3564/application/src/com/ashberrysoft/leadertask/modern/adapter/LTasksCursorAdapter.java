package com.ashberrysoft.leadertask.modern.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.view.list_item.LTaskItemView;
import com.ashberrysoft.leadertask.modern.view.list_item.LTaskItemView.OnLTaskItemListener;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.ArrayList;

public class LTasksCursorAdapter extends CursorAdapter {

    private final OnLTaskItemListener mListener;

    public LTasksCursorAdapter(Context context, OnLTaskItemListener listener) {
        super(context, null, false);
        mListener = listener;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {
        ((LTaskItemView) v).setData(c);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        final LTaskItemView v = new LTaskItemView(context, mListener);

        try {
            v.setData(c);
        }catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }
}