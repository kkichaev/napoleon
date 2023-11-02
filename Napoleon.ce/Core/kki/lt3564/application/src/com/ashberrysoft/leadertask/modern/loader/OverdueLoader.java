package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class OverdueLoader extends BaseLTaskLoader {

    public OverdueLoader(Context context) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getOverdueLinkTasks().build(),//
                new TaskSelectionBuilder().getOrderForTasks().build());
    }

    public OverdueLoader(Context context, boolean totalLink) {
        super(context,MenuItemType.OVERDUE, LionMetaData.OverdueTotalLinkContract.CONTENT_URI, null, null);
    }
}