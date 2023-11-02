package com.ashberrysoft.leadertask.modern.cache;

import android.content.Context;

import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;

public class TaskLinkCache extends BaseSparseCache<TaskTotalLink> {

    // SINGLETON
    private static TaskLinkCache sInstance;

    public static TaskLinkCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TaskLinkCache.class) {
                if (sInstance == null) {
                    sInstance = new TaskLinkCache(context);
                }
            }
        }
        return sInstance;
    }

    protected TaskLinkCache(Context context) {
        super(context);
    }

    @Override
    public void refreshCache() {}

    @Override
    public Integer getKey(TaskTotalLink value) {
        return Integer.parseInt(value.getUid());
    }
}