package com.ashberrysoft.leadertask.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView.OnSimpleFeatureViewListener;
import com.software.shell.fab.ActionButton;

public class EditMarkersFragment extends BaseFeaturesFragment implements OnSimpleFeatureViewListener<Marker> {

    private static final String CLASS_PATH = EditMarkersFragment.class.getName();
    private static final String EXTRA_MARKER = CLASS_PATH + "EXTRA_MARKER";

    // VALUE's
    private MenuInflater mMenuInflater;
    private Marker mTempMarker;
    private MarkerCache mMarkersCache;

    // ADAPTER
    private SimpleFeatureListAdapter<Marker> mAdapter;

    public static EditMarkersFragment newInstance() {
        return new EditMarkersFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mMenuInflater = getActivity().getMenuInflater();
        mMarkersCache = MarkerCache.getInstance(getActivity());

        mTempMarker = b == null ? null : (Marker) b.getSerializable(EXTRA_MARKER);
        mAdapter = new SimpleFeatureListAdapter<Marker>(getActivity(), FeatureType.MARKER, this);
        setActionButtonListener();
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        setData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_MARKER, mTempMarker);
    }

    @Override
    public void onStop() {
        //mAdapter.clear();
        super.onStop();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (mTempMarker == null) {
            return;
        }

        mMenuInflater.inflate(R.menu.edit_feature_contextmenu, menu);
        setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
        setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);


        if (mTempMarker.getOrder() >= mAdapter.getCount()) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
        }

        if (mTempMarker.getOrder() <= 1) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
        }
    }

    private void setData() {
        try {
            final List<Marker> list = mDbHelper.getMarkerDao().queryForAll();
            final List<Marker> data = new ArrayList<>();
            if (list != null) {
                try {
                    Collections.sort(list);
                } catch (Exception e) {
                }
                for (Marker c : list) {
                    if (LTSettings.getInstance().getUserName().equals(c.getCreator())) {
                        data.add(c);
                    }
                }
                mAdapter.setData(data);
                try {
                    recalculate();

                } catch (Exception e) {
                    Utils.toLog(e);
                }
            }
        } catch (SQLException e) {
            Utils.toLog(e);
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_properties:
            openMarker(mTempMarker);
            return true;

        case R.id.m_go_up:
            getOperationHolder().runThread(Operation.UP);
            return true;

        case R.id.m_go_down:
            getOperationHolder().runThread(Operation.DOWN);
            return true;

        case R.id.menu_dell:
            showSimpleDialog(R.string.d_title_marker_remove, R.string.d_message_marker_remove);
            return true;

        default:
            return super.onContextItemSelected(item);
        }
    }

    private void openMarker(Marker marker) {
        FeaturesActivity.hideActionButton();
        mSettings.setLastFeatureOrder(mAdapter.getCount());
        startFragment(PropertiesMarkerFragment.newInstance(marker));
    }

    @Override
    protected View getListViewHeader() {
        return null;
    }

    @Override
    protected ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.settings_Markers;
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.marker_white;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return true;
    }

    @Override
    protected boolean onAddFeatureClick() {
        openMarker(null);
        return true;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        return false;
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) {
        return false;
    }

    @Override
    protected void onDialogPositiveButton() {
        getOperationHolder().runThread(Operation.DELETE);
    }

    private void changeOrder(int newOrder) {
        final List<Marker> list = mAdapter.getData();

        final Marker changeMarker = list.get(list.size() - newOrder);
        final int oldOrder = mTempMarker.getOrder();

        changeMarker.setOrder(oldOrder);
        mTempMarker.setOrder(newOrder);

        list.set(list.size() - oldOrder, changeMarker);
        list.set(list.size() - newOrder, mTempMarker);

        changeMarker.setUsn(changeMarker.getUsn() + 1);
        changeMarker.setUsnOrder(changeMarker.getUsnOrder() + 1);
        mTempMarker.setUsn(mTempMarker.getUsn() + 1);
        mTempMarker.setUsnOrder(mTempMarker.getUsnOrder() + 1);

        final List<Marker> update = new ArrayList<>(2);
        update.add(changeMarker);
        update.add(mTempMarker);

        mMarkersCache.updateCache(update);
        mDbHelper.updateMarkers(update);


        MarkerCache.getInstance(mApp).updateCache(update);
        mApp.getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);
        Marker.updateTaskMarkerOrder(changeMarker.getId().toString().toUpperCase(), changeMarker.getOrder(), mApp);
        Marker.updateTaskMarkerOrder(mTempMarker.getId().toString().toUpperCase(), mTempMarker.getOrder(), mApp);
        mTempMarker = null;
    }


    private void recalculate() throws Exception {
        final List<Marker> list = mAdapter.getData();
        if (list.isEmpty()) {
            return;
        }

        int order = list.size();

        boolean update = false;
        for (Marker m : list) {
            if (m.getOrder() != order) {
                update = true;
                m.setUsn(m.getUsn() + 1);
                m.setUsnOrder(m.getUsnOrder() + 1);
            }
            m.setOrder(order--);
            Marker.updateTaskMarkerOrder(m.getId().toString().toUpperCase(), m.getOrder(), mApp);
        }

        if (update) {
            mMarkersCache.updateCache(list);
            mDbHelper.updateMarkers(list);
        }
    }

    @Override
    public void onSimpleFeatureViewClick(Marker data) {
        openMarker(data);
    }

    @Override
    public void onSimpleFeatureViewLongClick(View v, Marker data, int position, Marker dataPrev, Marker dataPost) {
        mTempMarker = data;
        getActivity().openContextMenu(v);
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        switch (operation) {

        case UP:
            changeOrder(mTempMarker.getOrder() + 1);
            return true;

        case DOWN:
            changeOrder(mTempMarker.getOrder() - 1);
            return true;

        case DELETE:
            mApp.getContentResolver().insert(UidToDeleteContract.CONTENT_URI, UidToDelete.getContentValues(mTempMarker));

            try {
                mDbHelper.getMarkerDao().deleteById(mTempMarker.getId());

                mMarkersCache.remove(mTempMarker);
                mAdapter.getData().remove(mTempMarker);
                Marker.updateTaskMarkerOrder(mTempMarker.getId().toString().toUpperCase(), 0, mApp);
                recalculate();

            } catch (Exception e) {
                Utils.toLog(e);
            }

            MarkerCache.getInstance(mApp).remove(mTempMarker);
            mApp.getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);
            mTempMarker = null;

            return true;

        default:
            return false;
        }
    }

    private void setActionButtonListener(){
        FeaturesActivity.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddFeatureClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        FeaturesActivity.showActionButton();
    }
}