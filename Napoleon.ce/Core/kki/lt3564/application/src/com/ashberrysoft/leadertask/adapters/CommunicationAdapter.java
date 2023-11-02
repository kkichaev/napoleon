package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.views.CommunicationListItemView;
import com.ashberrysoft.leadertask.views.CommunicationListItemView.OnCommunicationListItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CommunicationAdapter extends BaseAdapter {

    // VALUE's
    private Context mContext;
    private List<String> mData = new ArrayList <String>(0);
    private boolean canDelete;

    // LISTENER
    private OnCommunicationListItemListener mListener;

    public CommunicationAdapter(Context context, OnCommunicationListItemListener listener, boolean del) {
        mContext = context;
        mListener = listener;
        canDelete = del;
    }

    public void setData(List<String> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final CommunicationListItemView v = cV != null ? (CommunicationListItemView) cV : new CommunicationListItemView(mContext,
                mListener);

        v.setData(position, getItem(position), canDelete, mContext);

        return v;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<String> getData() {
        return mData;
    }
}