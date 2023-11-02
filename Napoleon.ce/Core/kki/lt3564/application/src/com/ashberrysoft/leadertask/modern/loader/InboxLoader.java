package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.InboxTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class InboxLoader extends BaseLTaskLoader {

    public InboxLoader(Context context) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getInboxLinkTasks().build(),//
                new TaskSelectionBuilder().getOrderForTasks().build());
    }

    public InboxLoader(Context context, boolean totalLink) {
        super(context,MenuItemType.INBOX, InboxTotalLinkContract.CONTENT_URI, null, null);
    }
}