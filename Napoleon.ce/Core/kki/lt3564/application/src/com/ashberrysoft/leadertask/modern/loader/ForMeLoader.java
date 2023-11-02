package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class ForMeLoader extends BaseByEmailTaskLoader {

    public ForMeLoader(Context context, OnEmailWithEmployeesLoaderListener listener) {
        super(context, listener, MenuItemType.FOR_ME, ForMeTotalLinkContract.CONTENT_URI, null,
                ForMeTotalLinkContract.DEFAULT_ORDER);
    }

    public ForMeLoader(Context context, String user) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getForMeTasks(user).build(), //
                new TaskSelectionBuilder().getOrderForTasks().build());
    }
}