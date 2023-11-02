package com.ashberrysoft.leadertask.modern.cache;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.CompletedTaskContract;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.utils.Utils;

public class CompletedCache extends BaseSparseCache<CompletedTask> {

    // SINGLETON
    private static CompletedCache sInstance;

    public static CompletedCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CompletedCache.class) {
                if (sInstance == null) {
                    sInstance = new CompletedCache(context);
                }
            }
        }
        return sInstance;
    }

    protected CompletedCache(Context context) {
        super(context);
    }

    @Override
    public void refreshCache() {
        clear();

        Cursor c = null;
        try {
            c = getContext().getContentResolver().query(CompletedTaskContract.CONTENT_URI, null, null, null, null);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                updateCache(new CompletedTask(c));
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    public Integer getKey(CompletedTask value) {
        return value.getIdTask();
    }

    public CompletedTask find(String uid) {
        CompletedTask task = null;
        for (int i = 0; i < getCache().size(); i++) {
            task = getCache().valueAt(i);
            if (uid.equals(task.getUid())) {
                return task;
            }
        }
        return null;
    }
}