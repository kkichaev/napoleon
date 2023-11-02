package com.ashberrysoft.leadertask.modern.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.modern.view.list_item.BaseMenuListItemView;
import com.ashberrysoft.leadertask.modern.view.list_item.HeaderMenuListItemView;
import com.ashberrysoft.leadertask.modern.view.list_item.MenuListItemView;
import com.ashberrysoft.leadertask.modern.view.list_item.BaseMenuListItemView.OnMenuListItemListener;

import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_AVAILABLE_PROJECTS;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_BY_ME;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_CATEGORIES;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_FOR_ME;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_PROJECTS;

public class MenuAdapter extends BaseAdapter {

    // BASE
    private final Context mContext;
    private final OnMenuListItemListener mListener;
    private final Fragment mTarget;

    // VALUE's
    private List<BaseMenuItem> mData;

    public MenuAdapter(Context context, OnMenuListItemListener listener, Fragment target) {
        mContext = context;
        mListener = listener;
        mTarget = target;
    }

    public void setData(List<BaseMenuItem> list) {
        mData = list;
    }

    public List<BaseMenuItem> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int i) {
        final BaseMenuItem item = getItem(i);
        switch (item.getMenuItemType()) {
        case HEADER_BY_ME:
        case HEADER_FOR_ME:
        case HEADER_PROJECTS:
        case HEADER_AVAILABLE_PROJECTS:
        case HEADER_CATEGORIES:
        case HEADER_COLORS:
        case HEADER_EMPS:
            return 0;

        default:
            return item.isVisible() ? 1 : 2;
        }
    }

    @Override
    public BaseMenuItem getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View cV, ViewGroup parent) {
        final BaseMenuItem menuItem = getItem(i);
        final BaseMenuListItemView v;
        
        if (cV == null) {
            switch (menuItem.getMenuItemType()) {
            case HEADER_BY_ME:
            case HEADER_FOR_ME:
            case HEADER_PROJECTS:
            case HEADER_AVAILABLE_PROJECTS:
            case HEADER_CATEGORIES:
            case HEADER_COLORS:
            case HEADER_EMPS:
                v = new HeaderMenuListItemView(mContext, mListener, mTarget);
                break;

            default:
                v = new MenuListItemView(mContext, mListener);
                break;
            }

        } else {
            v = (BaseMenuListItemView) cV;
        }

        v.setData(menuItem, i);
        return v;
    }

    public View getViewByUUID(String uid, View cV, ViewGroup parent) {
        BaseMenuItem menuItem = null;
        int i = 0;
        for (int j = 0; j< mData.size(); j++) {
            BaseMenuItem item = mData.get(j);
            if (uid != null && item.getUid() != null) {
                if (item.getUid().equals(uid)) {
                    menuItem = item;
                    i = j;
                }
            }
        }
        BaseMenuListItemView v = null;

        if (menuItem != null){
            if (cV == null) {
                switch (menuItem.getMenuItemType()) {
                    case HEADER_BY_ME:
                    case HEADER_FOR_ME:
                    case HEADER_PROJECTS:
                    case HEADER_AVAILABLE_PROJECTS:
                    case HEADER_CATEGORIES:
                    case HEADER_COLORS:
                    case HEADER_EMPS:
                        v = new HeaderMenuListItemView(mContext, mListener, mTarget);
                        break;

                    default:
                        v = new MenuListItemView(mContext, mListener);
                        break;
                }

            } else {
                v = (BaseMenuListItemView) cV;
            }

            v.setData(menuItem, i);
        }
        return v;
    }

}