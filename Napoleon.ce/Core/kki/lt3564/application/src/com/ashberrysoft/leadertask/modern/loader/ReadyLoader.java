package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ReadyTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class ReadyLoader extends BaseLTaskLoader {

    public ReadyLoader(Context context) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getReadyLinkTasks().build(),//
                new TaskSelectionBuilder().getOrderForTasks().build());
    }

    public ReadyLoader(Context context, boolean totalLink) {
        super(context,MenuItemType.READY, ReadyTotalLinkContract.CONTENT_URI, null, null);
    }
}