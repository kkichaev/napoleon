package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class FocusLoader extends BaseLTaskLoader {

    public FocusLoader(Context context) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getFocusLinkTasks().build(),//
                new TaskSelectionBuilder().getOrderForTasks().build());
    }

    public FocusLoader(Context context, boolean totalLink) {
        super(context,MenuItemType.FOCUS, LionMetaData.FocusTotalLinkContract.CONTENT_URI, null, null);
    }
}