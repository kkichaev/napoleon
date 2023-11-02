package com.ashberrysoft.leadertask.modern.loader;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class BaseDialogLoader<DATA> extends CursorLoader {

    public interface OnDialogLoadListener<DATA> {
        void deliverResult(List<DATA> data);
    }

    private final OnDialogLoadListener<DATA> mListener;
    private List<DATA> mResultValues;

    public BaseDialogLoader(Context context, Uri uri, String[] projection, //
            String selection, String[] selectionArgs, String sortOrder, OnDialogLoadListener<DATA> listener) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        mListener = listener;
    }

    @Override
    public void deliverResult(Cursor cursor) {
        if (mListener != null) {
            mListener.deliverResult(mResultValues);
        }

        super.deliverResult(cursor);
    }

    protected List<DATA> getResultValues() {
        return mResultValues;
    }

    protected void setResultValues(List<DATA> data) {
        mResultValues = data;
    }
}