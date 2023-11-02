package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class UnreadLoader extends BaseLTaskLoader {

    public UnreadLoader(Context context) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getUnreadLinkTasks().build(),//
                new TaskSelectionBuilder().getOrderForTasks().build());
    }

    public UnreadLoader(Context context, boolean totalLink) {
        super(context,MenuItemType.UNREAD, UnreadTotalLinkContract.CONTENT_URI, null, null);
    }
}