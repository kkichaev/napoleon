package com.ashberrysoft.leadertask.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.interfaces.CacheHolder;

@Deprecated
public abstract class BaseCacheHolder<DATA> implements CacheHolder<DATA> {

    // BASE
    private final DbHelper mDbHelper;

    // VALUE's
    private final SparseArrayCompat<DATA> mCache;

    protected BaseCacheHolder(Context context) {
        mDbHelper = DbHelper.getInstance(context.getApplicationContext());
        mCache = new SparseArrayCompat<>();
    }

    @Override
    public void refreshCache() {
        mCache.clear();

        Iterable<DATA> list;
        try {
            list = getListData();

        } catch (Exception e) {
            list = new ArrayList<>(0);
        }

        updateCache(list);
    }

    @Override
    public void updateCache(Iterable<DATA> list) {
        for (DATA d : list) {
            updateCache(d);
        }
    }

    @Override
    public void updateCache(DATA data) {
        mCache.put(getKey(data), data);
    }

    @Override
    public abstract int getKey(DATA data);

    @Override
    public void removeFromCache(int hash) {
        mCache.remove(hash);
    }

    @Override
    public void removeFromCache(DATA data) {
        removeFromCache(getKey(data));
    }

    @Override
    public DATA findData(int hash) {
        return mCache.get(hash);
    }

    @Override
    public List<DATA> findData(List<Integer> hashes) {
        final List<DATA> list = new ArrayList<>();
        for (Integer hash : hashes) {
            final DATA data = findData(hash);
            if (data != null) {
                list.add(data);
            }
        }

        return list;
    }

    @Override
    public List<DATA> getData() {
        final List<DATA> list = new ArrayList<>(mCache.size());
        for (int i = 0; i < mCache.size(); i++) {
            list.add(mCache.valueAt(i));
        }

        return list;
    }

    @Override
    public boolean isEmpty() {
        return mCache.size() == 0;
    }

    protected DbHelper getDbHelper() {
        return mDbHelper;
    }

    protected SparseArrayCompat<DATA> getCache() {
        return mCache;
    }
}