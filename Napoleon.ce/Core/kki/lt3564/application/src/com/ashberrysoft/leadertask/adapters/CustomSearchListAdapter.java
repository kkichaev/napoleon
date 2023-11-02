package com.ashberrysoft.leadertask.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.modern.cache.TaskMessageCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSearchView.OnSearchViewListener;
import com.ashberrysoft.leadertask.views.ListItemSearchView;
import com.v2soft.AndLib.dao.ITreePureNode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CustomSearchListAdapter<DATA> extends BaseAdapter {

    public static final int TYPE_TASKS = 0;
    public static final int TYPE_CONTACTS = 1;

    // BASE
    private final Context mContext;

    // VALUE's
    private List<DATA> mData = new ArrayList<DATA>(0);
    private TreeSet mSeparatorsSet = new TreeSet();

    // LISTENER
    private final OnSearchViewListener<DATA> mListener;

    public CustomSearchListAdapter(final Context context, final OnSearchViewListener<DATA> listener) {
        mContext = context;
        mListener = listener;
    }

    public void setData(List<LTask> dataTasks, List<DATA> dataContacts) {
        mData.clear();
        mSeparatorsSet.clear();
        for (LTask data : dataTasks) {
            addSeparatorItem(data);
        }
        for (DATA data : dataContacts) {
            addItem(data);
        }
    }

    public void clear() {
        mData.clear();
    }

    public void updateTask(LTask oldTask, LTask newTask, int position) {
        if (!newTask.equals(oldTask)) {
            mData.set(position,(DATA) newTask);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_TASKS : TYPE_CONTACTS;
    }

    public void addSeparatorItem(final LTask item) {
        mData.add((DATA)item);
        // save separator position
        mSeparatorsSet.add(mData.size() - 1);
    }

    public void addItem(final DATA item) {
        mData.add(item);
    }

    public static List<ITreePureNode> getListContactsAfterSearch(LTSettings settings, DbHelper dbHelper, String search) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();

        List<Contact> contacts = new ArrayList<>();
        contacts = dbHelper.getContactsWithSearch(search);

        for (Contact p : contacts) {
            if (TextUtils.isEmpty(p.getTitle())) {
                continue;
            } else {
                data.add(p);
            }
        }
        return data;
    }

    public static List<LTask> getListTasksAfterSearch(LTSettings settings, DbHelper dbHelper, String search) {
        final List<LTask> data = new ArrayList<LTask>();

        List<LTask> tasks = new ArrayList<>();
        tasks = dbHelper.getTasksWithSearch(search);
        data.addAll(tasks);
        return data;
    }

    public static List<LTask> getListTasksWithParentUID(LTSettings settings, DbHelper dbHelper, String parentUid) {
        final List<LTask> data = new ArrayList<LTask>();

        List<LTask> tasks = new ArrayList<>();
        tasks = dbHelper.getTasksWithParent(parentUid);
        data.addAll(tasks);
        return data;
    }


    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final ListItemSearchView<DATA> v = cV != null ? (ListItemSearchView<DATA>) cV : new ListItemSearchView<DATA>(mContext, mListener);

        v.setData(getItem(position), getItemViewType(position));
        v.setPosition(position);
        return v;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public DATA getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<DATA> getData() {
        return mData;
    }
}