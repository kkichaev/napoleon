package com.ashberrysoft.leadertask.modern.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.fragments.PropertiesContactFragment;
import com.ashberrysoft.leadertask.modern.view.list_item.ContactFileListItemView;
import com.ashberrysoft.leadertask.modern.view.list_item.ContactFileListItemView.OnContactFileListener;

import java.util.List;

public class EditContactFilesAdapter extends BaseAdapter {

    // BASE
    private final List<ContactFile> mData;
    private final OnContactFileListener mListener;

    public EditContactFilesAdapter(List<ContactFile> data, OnContactFileListener listener) {
        mData = data;
        mListener = listener;
    }

    public List<ContactFile> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ContactFile getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View cV, ViewGroup vG) {
        final ContactFileListItemView v = cV != null ? (ContactFileListItemView) cV : new ContactFileListItemView(
                vG.getContext(), mListener);

        v.setData(getItem(i));

        return v;
    }
}