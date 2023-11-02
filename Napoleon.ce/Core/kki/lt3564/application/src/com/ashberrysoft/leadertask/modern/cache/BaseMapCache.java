package com.ashberrysoft.leadertask.modern.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.ashberrysoft.leadertask.application.LTSettings;

public abstract class BaseMapCache<DATA> //
        implements BaseCacheInterface<String, DATA> {

    // BASE
    private final Context mContext;

    // VALUE's
    private final LTSettings mSettings;
    private final Map<String, DATA> mCache;

    protected BaseMapCache(Context context) {
        mContext = context.getApplicationContext();

        mSettings = LTSettings.getInstance(mContext);
        mCache = new HashMap<>();
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
    public boolean remove(String key) {
        return mCache.remove(key) != null;
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
    public DATA find(String key) {
        return mCache.get(key);
    }

    @Override
    public List<DATA> getAll() {
        return new ArrayList<>(mCache.values());
    }

    protected Context getContext() {
        return mContext;
    }

    protected LTSettings getSettings() {
        return mSettings;
    }

    protected Map<String, DATA> getCache() {
        return mCache;
    }
}