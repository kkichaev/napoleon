package com.ashberrysoft.leadertask.modern.cache;

import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.utils.Utils;

public class MarkerCache extends BaseSparseCache<Marker> {

    // SINGLETON
    private static MarkerCache sInstance;

    public static MarkerCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MarkerCache.class) {
                if (sInstance == null) {
                    sInstance = new MarkerCache(context);
                }
            }
        }
        return sInstance;
    }

    protected MarkerCache(Context context) {
        super(context);
    }

    @Override
    public void refreshCache() {
        try {
            updateCache(DbHelper.getInstance(getContext()).getMarkerDao().queryForAll());

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    @Override
    protected void reorderGetAllArray(List<Marker> all) {
        Collections.sort(all);
    }

    @Override
    public Integer getKey(Marker value) {
        return String.valueOf(value.getId()).hashCode();
    }

    public boolean hasCustomMarkers() {
        if (getCache().size() > 0) {
            return true;
        }

        return false;
    }

    public int getOrderForMarker(String markerUidLower) {
        if (getCache().size() == 0) {
            return 0;
        }
        else {
            for (int i = 0; i < getCache().size(); i++) {
                if (markerUidLower.equals(getCache().valueAt(i).getId().toString())) {
                    return getCache().valueAt(i).getOrder();
                }
            }
            return 0;
        }
    }
}