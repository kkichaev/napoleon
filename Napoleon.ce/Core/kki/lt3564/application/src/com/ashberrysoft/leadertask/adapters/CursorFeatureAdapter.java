package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 
 * @since 2014-06-19
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CursorFeatureAdapter extends CursorAdapter {

    public interface CursorFeatureAdapterCallback {
        CFADataView getNewView(Context context);
    }

    public abstract static class CFADataView extends LinearLayout {

        public CFADataView(Context context) {
            super(context);
        }

        public abstract void setData(Cursor c);
    }

    // CALLBACK
    private CursorFeatureAdapterCallback mCallback;

    public CursorFeatureAdapter(Context context, CursorFeatureAdapterCallback callback) {
        super(context, null, false);

        mCallback = callback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View v = mCallback.getNewView(context);
        return v == null ? new View(context) : v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((CFADataView) view).setData(cursor);
    }
}