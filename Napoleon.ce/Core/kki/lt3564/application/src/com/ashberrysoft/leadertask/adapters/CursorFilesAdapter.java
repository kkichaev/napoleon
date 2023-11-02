package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.views.CursorFileListItemView;
import com.ashberrysoft.leadertask.views.CursorFileListItemView.OnCursorFileListItemListener;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CursorFilesAdapter extends CursorAdapter {

    // LISTENER
    private OnCursorFileListItemListener mListener;

    public CursorFilesAdapter(Context context, Cursor c, OnCursorFileListItemListener listener) {
        super(context, c, true);
        setCustomListener(listener);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new CursorFileListItemView(context, mListener);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((CursorFileListItemView) view).setData(cursor);
    }

    public void setCustomListener(OnCursorFileListItemListener listener) {
        mListener = listener;
    }
}