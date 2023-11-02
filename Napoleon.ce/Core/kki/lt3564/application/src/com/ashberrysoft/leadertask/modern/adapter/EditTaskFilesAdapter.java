package com.ashberrysoft.leadertask.modern.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.modern.view.list_item.TaskFileListItemView;
import com.ashberrysoft.leadertask.modern.view.list_item.TaskFileListItemView.OnTaskFileListener;

public class EditTaskFilesAdapter extends BaseAdapter {

    // BASE
    private final List<TaskFile> mData;
    private final OnTaskFileListener mListener;

    public EditTaskFilesAdapter(List<TaskFile> data, OnTaskFileListener listener) {
        mData = data;
        mListener = listener;
    }

    public List<TaskFile> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public TaskFile getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View cV, ViewGroup vG) {
        final TaskFileListItemView v = cV != null ? (TaskFileListItemView) cV : new TaskFileListItemView(
                vG.getContext(), mListener);
        v.setData(getItem(i));

        return v;
    }
}