package com.ashberrysoft.leadertask.adapters;

import java.util.List;

import org.apache.http.util.TextUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.views.EmployeeListItemView;
import com.ashberrysoft.leadertask.views.EmployeeListItemView.OnEmployeeListItemViewListener;


public class EmployeeAdapter extends BaseAdapter //
        implements OnEmployeeListItemViewListener {

    // VALUE's
    private Context mContext;
    private List<Employee> mEmployees;
    private int mCheckedEmployeePosition;

    public EmployeeAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<Employee> employees, String employee) {
        mEmployees = employees;
        mCheckedEmployeePosition = NO_SELECTION;

        if (TextUtils.isEmpty(employee)) {
            return;
        }

        int count = 0;
        for (Employee e : mEmployees) {
            if (e.getEmail().equals(employee)) {
                mCheckedEmployeePosition = count;
                break;
            }
            count++;
        }
    }

    public String getCheckedPerformer() {
        return mCheckedEmployeePosition == NO_SELECTION ? null//
                : mEmployees.get(mCheckedEmployeePosition).getEmail();
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final EmployeeListItemView v = cV == null ? new EmployeeListItemView(mContext, this)
                : (EmployeeListItemView) cV;
        v.setData(getItem(position), mCheckedEmployeePosition == position, position, true);
        return v;
    }

    @Override
    public int getCount() {
        return mEmployees == null ? 0 : mEmployees.size();
    }

    @Override
    public Employee getItem(int position) {
        return mEmployees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onEmployeeCheckedChange(boolean isChecked, int position) {
        mCheckedEmployeePosition = isChecked ? position : NO_SELECTION;
        this.notifyDataSetChanged();
    }
}