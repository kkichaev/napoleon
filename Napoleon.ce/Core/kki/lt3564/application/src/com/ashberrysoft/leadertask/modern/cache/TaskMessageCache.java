package com.ashberrysoft.leadertask.modern.cache;

import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskMessageCache extends BaseSparseCache<List<TaskMessage>> {

    // SINGLETON
    private static TaskMessageCache sInstance;

    public static TaskMessageCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TaskMessageCache.class) {
                if (sInstance == null) {
                    sInstance = new TaskMessageCache(context);
                }
            }
        }
        return sInstance;
    }

    protected TaskMessageCache(Context context) {
        super(context);
    }

    @Override
    public void refreshCache() {
    }

    @Override
    public Integer getKey(List<TaskMessage> value) {
        return value.get(0).hashCode();
    }
}