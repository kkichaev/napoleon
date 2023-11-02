package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.views.PerformerListItemViewContactsGroup;
import com.ashberrysoft.leadertask.views.PerformerListItemViewContactsGroup.OnPerformerListItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ContactsGroupPropertiesAdapter extends BaseAdapter {

    // VALUE's
    private Context mContext;
    private List<String> mData = new ArrayList<String>(0);
    List <Employee> employees = new ArrayList<Employee>(0);
    private ContactsGroup mContactsGroup;

    // LISTENER
    private OnPerformerListItemListener mListener;

    public ContactsGroupPropertiesAdapter(Context context, OnPerformerListItemListener listener) {
        mContext = context;
        mListener = listener;
        employees = DbHelper.getInstance(context).getAllPerformers(context);
    }

    public void setData(List<String> data, ContactsGroup contactsGroup) {
        mContactsGroup = contactsGroup;
        mData.clear();
        mData.addAll(data);
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final PerformerListItemViewContactsGroup v = cV != null ? (PerformerListItemViewContactsGroup) cV : new PerformerListItemViewContactsGroup(mContext,
                mListener);

        v.setData(position, getItem(position), employees, mContext, mContactsGroup);

        return v;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<String> getData() {
        return mData;
    }
}