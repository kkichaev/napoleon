package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.utils.Utils;

public abstract class BaseLTaskLoader extends CursorLoader {

    // BASE
    private final BaseMenuItem mMenuItem;
    // private final String mSelection;

    // VALUE's
    private final LTSettings mSettings;

    protected BaseLTaskLoader(Context context, Uri contentUri, String selection, String order) {
        this(context, null, contentUri, selection, order);
    }

    protected BaseLTaskLoader(Context context, BaseMenuItem menuItem, Uri contentUri, String selection, String order) {
        super(context.getApplicationContext(), contentUri, null, selection, null, order);

        // mSelection = selection;
        mMenuItem = menuItem;
        mSettings = LTSettings.getInstance(getContext());
    }

    protected BaseMenuItem getMenuItem() {
        return mMenuItem;
    }

    protected LTSettings getSettings() {
        return mSettings;
    }

    @Override
    public Cursor loadInBackground() {
        final Cursor cursor = super.loadInBackground();
        // TODO: тут можно нахуй все удалить, кеш убрать и все вручную проверять. будет быстрее открываться большие списки
        // TODO: 07.11.2017 цвет потеряется
        if (mMenuItem == null) {
            //final long start = System.currentTimeMillis();

            final LTaskCache cache = LTaskCache.getInstance(getContext());
            cache.refreshCache();

            if (cursor.getCount() > 0) {
                final int columnId = cursor.getColumnIndex(LTaskContract._ID);
                final int columnUid = cursor.getColumnIndex(LTaskContract.Uid);
                final int columnMarker = cursor.getColumnIndex(LTaskContract.UidMarker);

                final int[] ids = new int[cursor.getCount()];
                final String[] uids = new String[cursor.getCount()];
                final String[] markers = new String[cursor.getCount()];
                int count = 0;

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    ids[count] = cursor.getInt(columnId);
                    uids[count] = cursor.getString(columnUid).toLowerCase();
                    if(cursor.getString(columnMarker) != null) {
                        markers[count] = cursor.getString(columnMarker).toLowerCase();
                    }
                    else {
                        markers[count] = "";
                    }
                    count++;
                }

                cache.refreshCache(ids, uids, markers);
            }

            //final long difference = (System.currentTimeMillis() - start);
            //android.util.Log.v("Tedorius","~ update LTaskCache time\t" + difference);
        }

        return cursor;
    }
}