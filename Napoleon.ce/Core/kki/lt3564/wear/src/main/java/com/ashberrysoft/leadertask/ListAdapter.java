package com.ashberrysoft.leadertask;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;

import java.util.List;


public class ListAdapter extends WearableListView.Adapter {

    // BASE
    private final Context mContext;

    // VALUE's
    private List<String> mData;
    private boolean mIsTodayList;
    private ListItem.OnClickTaskItemListener mListener;

    public ListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<String> list, boolean isToday,  ListItem.OnClickTaskItemListener listener) {
        mData = list;
        mIsTodayList = isToday;
        mListener = listener;
    }

    public void setDataAfterCancelTask(int id) {
        mData.remove(id);
    }

    public List<String> getData() {
        return mData;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WearableListView.ViewHolder(new ListItem(mContext, mListener));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, final int position) {
        ListItem ItemView = (ListItem) viewHolder.itemView;
        ItemView.setData(mData.get(position), position, mIsTodayList);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
