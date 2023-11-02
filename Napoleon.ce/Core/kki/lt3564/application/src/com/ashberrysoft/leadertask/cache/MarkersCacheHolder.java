package com.ashberrysoft.leadertask.cache;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.domains.ordinary.Marker;

/**
 * Класс с кешированными данными.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
@Deprecated
public class MarkersCacheHolder extends BaseCacheHolder<Marker> {

    // SINGLETON
    private static MarkersCacheHolder sInstance;

    public static MarkersCacheHolder getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MarkersCacheHolder.class) {
                if (sInstance == null) {
                    sInstance = new MarkersCacheHolder(context);
                }
            }
        }
        return sInstance;
    }

    private MarkersCacheHolder(Context context) {
        super(context);
    }

    @Override
    public Iterable<Marker> getListData() throws Exception {
        return getDbHelper().getMarkerDao().queryForAll();
    }

    @Override
    public int getKey(Marker data) {
        return data.getId().hashCode();
    }

    public Marker findData(UUID id) {
        if (id == null) {
            return null;
        }
        return findData(id.hashCode());
    }

    @Override
    public List<Marker> getData() {
        final List<Marker> list = super.getData();
        Collections.sort(list);

        return list;
    }

    public boolean hasCustomMarkers() {
        return getCache().size() > 1;
    }
}