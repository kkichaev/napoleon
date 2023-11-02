package com.ashberrysoft.leadertask.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CursorFeatureAdapter;
import com.ashberrysoft.leadertask.adapters.CursorFeatureAdapter.CursorFeatureAdapterCallback;

/**
 * @since 2014-06-19
 * @author Tregub Artem tregub.artem@gmail.com
 */

public abstract class BaseCursorFeaturesFragment extends BaseFeaturesFragment//
        implements LoaderCallbacks<Cursor>, CursorFeatureAdapterCallback {

    // VALUE's
    protected MenuInflater mMenuInflater;

    // ADAPTER
    protected CursorFeatureAdapter mAdapter;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mAdapter = new CursorFeatureAdapter(mApp, this);
        mMenuInflater = getActivity().getMenuInflater();
        getLoaderManager().initLoader(R.id.lm_cursor_feature, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        setBlock(true);
        return super.onCreateView(inflater, container, b);
    }

    @Override
    protected ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
        case R.id.lm_cursor_feature:
            return getFeatureCursorLoader(mApp);

        default:
            return null;
        }
    }

    protected abstract CursorLoader getFeatureCursorLoader(Context context);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        setBlock(false);

        switch (loader.getId()) {
        case R.id.lm_cursor_feature:
            mAdapter.changeCursor(cursor);
        default:
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setBlock(false);
    }
}