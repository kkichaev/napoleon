package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class InworkLoader extends BaseLTaskLoader {

    public InworkLoader(Context context) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getInworkLinkTasks().build(),//
                new TaskSelectionBuilder().getOrderForTasks().build());
    }

    public InworkLoader(Context context, boolean totalLink) {
        super(context,MenuItemType.INWORK, LionMetaData.InworkTotalLinkContract.CONTENT_URI, null, null);
    }
}