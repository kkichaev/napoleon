package com.ashberrysoft.leadertask.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.views.StatusListItem;
import com.ashberrysoft.leadertask.views.StatusListItem.OnStatusListItemListener;
import com.v2soft.AndLib.ui.adapters.CustomViewAdapter;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Адаптер для отображения списка статусов
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class StatusListAdapter extends CustomViewAdapter<TaskStatus> implements OnStatusListItemListener {

    private int mSelected;
    private boolean mSeriesTask;

    public StatusListAdapter(Context context, List<TaskStatus> statuses, int selected, boolean seriesTask) {
        super(context, new CustomViewAdapterFactory<TaskStatus, IDataView<TaskStatus>>() {
            @Override
            public IDataView<TaskStatus> createView(Context context, int res) {
                return new StatusListItem(context);
            }
        });

        setData(statuses);
        mSelected = selected;
        mSeriesTask = seriesTask;
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final StatusListItem v = cV != null ? (StatusListItem) cV : new StatusListItem(mContext, this);

        v.setData((TaskStatus) getItem(position));
        v.setChecked(position, mSelected == position);

        return v;
    }

    public TaskStatus getStatus() {
        return mSelected < 0 ? null : (TaskStatus) getItem(mSelected);
    }

    public void setStatus(int i) {
        mSelected = i;
    }

    @Override
    public void onStatusClick(int position) {
        mSelected = position;
        this.notifyDataSetChanged();
    }

    @Override
    public boolean isSeriesTask() {
        return mSeriesTask;
    }
}