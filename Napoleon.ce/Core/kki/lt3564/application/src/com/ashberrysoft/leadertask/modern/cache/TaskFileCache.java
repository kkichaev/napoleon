package com.ashberrysoft.leadertask.modern.cache;

import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskFileCache extends BaseSparseCache<List<TaskFile>> {

    // SINGLETON
    private static TaskFileCache sInstance;

    public static TaskFileCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TaskFileCache.class) {
                if (sInstance == null) {
                    sInstance = new TaskFileCache(context);
                }
            }
        }
        return sInstance;
    }

    protected TaskFileCache(Context context) {
        super(context);
    }

    @Override
    public void refreshCache() {
        clear();
    }

    @Override
    public Integer getKey(List<TaskFile> value) {
        return value.get(0).hashCode();
    }
}