package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.views.TaskViewNew;
import com.v2soft.AndLib.ui.adapters.CustomViewAdapter;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * 
 * Адаптер для задач
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class TaskAdapter extends CustomViewAdapter<Task> implements OnClickListener {

    public interface OnTaskStatusClickListener {
        void onTaskStatusClick(ImageView iv, Task task);
    }

    private final OnTaskStatusClickListener mListener;

    public TaskAdapter(Context context, final boolean isForNotification, OnTaskStatusClickListener listener) {
        super(context, new CustomViewAdapterFactory<Task, IDataView<Task>>() {
            @Override
            public IDataView<Task> createView(Context context, int res) {
                return new TaskViewNew(context, null);
            }
        });

        mListener = listener;
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final TaskViewNew v = cV != null ? (TaskViewNew) cV : new TaskViewNew(mContext, this);

        v.setData((Task) getItem(position));

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_task_status:
        case R.id.frame:
            mListener.onTaskStatusClick((ImageView) v, (Task) v.getTag());
            break;

        default:
            break;
        }
    }
}