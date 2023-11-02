package com.ashberrysoft.leadertask.modern.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;

import com.ashberrysoft.leadertask.application.LTSettings;

public abstract class BaseSparseCache<DATA> //
        implements BaseCacheInterface<Integer, DATA> {

    // BASE
    private final Context mContext;

    // VALUE's
    private final LTSettings mSettings;
    private final SparseArray<DATA> mCache;

    protected BaseSparseCache(Context context) {
        mContext = context.getApplicationContext();

        mSettings = LTSettings.getInstance(mContext);
        mCache = new SparseArray<>();
    }

    @Override
    public void updateCache(Collection<DATA> values) {
        for (DATA value : values) {
            updateCache(value);
        }
    }

    @Override
    public void updateCache(DATA value) {
        mCache.put(getKey(value), value);
    }

    @Override
    public boolean remove(Integer key) {
        mCache.remove(key);

        return true;
    }

    @Override
    public boolean remove(DATA data) {
        return data != null && remove(getKey(data));
    }

    @Override
    public void clear() {
        mCache.clear();
    }

    @Override
    public DATA find(Integer key) {
        return mCache.get(key);
    }

    @Override
    public List<DATA> getAll() {
        final List<DATA> all = new ArrayList<>(mCache.size());

        for (int i = 0; i < mCache.size(); i++) {
            all.add(mCache.valueAt(i));
        }
        reorderGetAllArray(all);

        return all;
    }

    protected void reorderGetAllArray(List<DATA> all) {}

    protected Context getContext() {
        return mContext;
    }

    protected LTSettings getSettings() {
        return mSettings;
    }

    protected SparseArray<DATA> getCache() {
        return mCache;
    }
}