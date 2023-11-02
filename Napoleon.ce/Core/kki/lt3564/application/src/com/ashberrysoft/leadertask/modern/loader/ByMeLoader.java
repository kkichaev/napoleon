package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class ByMeLoader extends BaseByEmailTaskLoader {

    public ByMeLoader(Context context, OnEmailWithEmployeesLoaderListener listener) {
        super(context, listener, MenuItemType.BY_ME, ByMeTotalLinkContract.CONTENT_URI, null,
                ByMeTotalLinkContract.DEFAULT_ORDER);
    }

    public ByMeLoader(Context context, String user) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getByMeTasks(user).build(), //
                new TaskSelectionBuilder().getOrderForTasks().build());
    }
}