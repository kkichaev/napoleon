package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.views.SubtaskView;
import com.ashberrysoft.leadertask.views.SubtaskView.OnTaskStatusListener;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CursorTaskAdapter extends CursorAdapter {

    // LISTENER
    private OnTaskStatusListener mListener;

    public CursorTaskAdapter(Context context, Cursor c, OnTaskStatusListener listener) {
        super(context, c, true);
        setCustomListener(listener);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new SubtaskView(context, mListener);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((SubtaskView) view).setData(cursor);
    }

    public void setCustomListener(OnTaskStatusListener listener) {
        mListener = listener;
    }
}