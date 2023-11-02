package com.ashberrysoft.leadertask.adapters;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.views.MarkerListItemView;
import com.ashberrysoft.leadertask.views.MarkerListItemView.OnMarkerListItemListener;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class MarkerAdapter extends BaseAdapter//
        implements OnMarkerListItemListener {

    // VALUE's
    private final List<Marker> mData;
    private int mCheckedItem;

    public MarkerAdapter(List<Marker> markers, int selected) {
        mData = markers;
        mCheckedItem = selected;
    }

    public Marker getCheckedMarker() {
        return mCheckedItem < 0 ? null : getItem(mCheckedItem);
    }

    public List<Marker> getData() {
        return mData;
    }

    @Override
    public View getView(int i, View cV, ViewGroup parent) {
        final MarkerListItemView v = cV == null ? new MarkerListItemView(parent.getContext(), this)
                : (MarkerListItemView) cV;

        v.setData(i, i == mCheckedItem, getItem(i));

        return v;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Marker getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onMarkerListItemClick(int position, boolean isChecked) {
        if(isChecked) {
            if (mCheckedItem != position) {
                mCheckedItem = position;
                this.notifyDataSetChanged();
            }
        }
        else {
            if (mCheckedItem == position) {
                mCheckedItem = -1;
                this.notifyDataSetChanged();
            }
        }
    }
}