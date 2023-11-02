package com.ashberrysoft.leadertask.modern.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;

public abstract class BaseSyncStatusFragment extends BaseFragment//
        implements LoaderCallbacks<Cursor> {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        getLoaderManager().restartLoader(R.id.lm_sync_info, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case R.id.lm_sync_info:
            return new CursorLoader(getApp(), SyncInfoContract.CONTENT_URI, null, null, null, null);

        default:
            return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
        case R.id.lm_sync_info:
            if (cursor.moveToFirst()) {
                onSyncStatusChange(cursor);
            }
            break;

        default:
            break;
        }
    }

    private void onSyncStatusChange(Cursor cursor) {
        final SyncInfo si = new SyncInfo(cursor);

        switch (si.getSyncStatus()) {
        case ENDED:
        case ERROR:
            onSyncStatusChange(si);
            break;

        default:
            break;
        }
    }

    /**
     * Выполнение действий во фрагменте после изменения состояния синхронизации
     * 
     * @param si
     */
    public abstract void onSyncStatusChange(SyncInfo si);

    public void onFragmentResult(Object data, int requestCode) {

    }
}