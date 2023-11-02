package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.EmpTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class EmpLoader extends BaseByEmailTaskLoader {

    public EmpLoader(Context context, OnEmailWithEmployeesLoaderListener listener) {
        super(context, listener, MenuItemType.EMP, EmpTotalLinkContract.CONTENT_URI, null,
                EmpTotalLinkContract.DEFAULT_ORDER);
    }

    public EmpLoader(Context context, String user) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getEmpTasks(user).build(), //
                new TaskSelectionBuilder().getOrderForTasks().build());
    }
}