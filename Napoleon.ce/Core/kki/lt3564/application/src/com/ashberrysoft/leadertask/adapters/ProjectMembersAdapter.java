package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.views.EmployeeListItemView;
import com.ashberrysoft.leadertask.views.EmployeeListItemView.OnEmployeeListItemViewListener;

import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class ProjectMembersAdapter extends BaseAdapter //
        implements OnEmployeeListItemViewListener {

    // VALUE's
    private Context mContext;
    private List<Employee> mEmployees;
    private boolean [] mCheckedEmployees;
    private static final String DOUBLE_DOTS = "..";
    private boolean mIsCustomer;

    public ProjectMembersAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<Employee> employees, ArrayList<String> checkedPerformers, boolean isCustomer) {
        mEmployees = employees;
        mIsCustomer = isCustomer;
        mCheckedEmployees = new boolean[mEmployees.size()];

        int count = 0;
        for (Employee e : mEmployees) {
            for (String checkedUser: checkedPerformers) {
                if (e.getEmail().equals(checkedUser)) {
                    mCheckedEmployees[count] = true;
                    break;
                } else {
                    if (mIsCustomer) {
                        mCheckedEmployees[count] = false;
                    }
                }
            }
            count++;
        }

        int n = 0;
        if (!mIsCustomer) {
            for (int i=0; i < mCheckedEmployees.length; i++) {
                if (!mCheckedEmployees[i]) {
                    mEmployees.remove(i-n);
                    n++;
                }
            }
        }
    }

    public String getPerformers() {
        ArrayList<String> checkedPerformers = new ArrayList<>();
        int count = 0;
        for (boolean isChecked : mCheckedEmployees) {
            if (isChecked) {
                checkedPerformers.add(mEmployees.get(count).getEmail());
            }
            count++;
        }
        //
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < checkedPerformers.size(); i++) {
            stringBuilder.append(checkedPerformers.get(i));
            if (i < checkedPerformers.size() - 1) {
                stringBuilder.append(DOUBLE_DOTS);
            }
        }
        return stringBuilder.toString();

    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final EmployeeListItemView v = cV == null ? new EmployeeListItemView(mContext, this) : (EmployeeListItemView) cV;
        v.setData(getItem(position), mCheckedEmployees[position], position, mIsCustomer);
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
        mCheckedEmployees[position] = !mCheckedEmployees[position];
    }
}