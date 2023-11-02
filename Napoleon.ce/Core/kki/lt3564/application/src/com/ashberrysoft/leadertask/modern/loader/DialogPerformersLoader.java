package com.ashberrysoft.leadertask.modern.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;

public class DialogPerformersLoader extends BaseDialogLoader<Employee> {

    public DialogPerformersLoader(Context context, OnDialogLoadListener<Employee> listener) {
        super(context, EmpContract.CONTENT_URI, null,//
                SelectionKeeper.notEquals(null, EmpContract.UID, Emp.DEFAULT_UUID_EMP_S), null, EmpContract.DEFAULT_SORT, listener);
    }

    @Override
    public Cursor loadInBackground() {
        final Cursor cursor = super.loadInBackground();
        setResultValues(DbHelper.getInstance(getContext()).getListEmployees(getContext()));

        return cursor;
    }
}