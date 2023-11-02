package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CursorFeatureAdapter.CFADataView;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.utils.SharedStrings;

/**
 * 
 * @since 2014-06-19
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class EmpListItemView extends CFADataView {

    // VIEW's
    private TextView mTvTitle;
    private TextView mTvLogin;

    // VALUE
    private EmployeeCache mEmployeeCache;

    public EmpListItemView(Context context) {
        super(context);
        initialization();
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_view_emp, this);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvLogin = (TextView) findViewById(R.id.tv_login);

        ((ImageView) findViewById(R.id.iv_feature_img))//
                .setImageResource(LTSettings.getInstance(getContext())//
                        .isThemeDark() ? R.drawable.employee_white : R.drawable.employee);

        if (LTSettings.getInstance(getContext()).isThemeDark()) {
            mTvTitle.setTextColor(Color.WHITE);
            mTvLogin.setTextColor(Color.WHITE);
        } else {
            mTvTitle.setTextColor(Color.BLACK);
            mTvLogin.setTextColor(Color.BLACK);
        }

        mEmployeeCache = EmployeeCache.getInstance(getContext());
    }

    @Override
    public void setData(Cursor c) {
        final Emp emp = new Emp(c);
        this.setTag(emp);

        if (emp.getLogin().equals(emp.getTitle())) {
            mTvTitle.setText(mEmployeeCache.find(emp.getLogin()));
        } else if (TextUtils.isEmpty(emp.getTitle())) {
            mTvTitle.setText(SharedStrings.EMPTY);
        } else {
            mTvTitle.setText(emp.getTitle());
        }

        mTvLogin.setText(emp.getLogin());
    }
}