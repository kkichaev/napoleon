package com.ashberrysoft.leadertask.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.views.CommentListItem;
import com.v2soft.AndLib.ui.adapters.CustomViewAdapter;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * 
 * Адаптер для комментариев
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
public class CommentsAdapter<TaskMessage> extends BaseAdapter {

    // VALUE's
    private List<TaskMessage> mData = new ArrayList<TaskMessage>(0);
    Context mContext;


    public CommentsAdapter(Context context, List<TaskMessage> messagesList) {
        mData = messagesList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void setData(List<TaskMessage> data) {
        mData.clear();
        mData.addAll(data);
    }

    @Override
    public TaskMessage getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<TaskMessage> getData() {
        return mData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentListItem view = convertView != null ? (CommentListItem) convertView : new CommentListItem(mContext) ;
        view.setData((com.ashberrysoft.leadertask.domains.ordinary.TaskMessage) mData.get(position));
        //view.setAdapter(this);
        return view;
    }

}
