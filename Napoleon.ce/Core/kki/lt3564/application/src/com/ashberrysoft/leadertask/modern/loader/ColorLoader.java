package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.ColorTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class ColorLoader extends BaseCollapsibleTaskLoader {

    public ColorLoader(Context context, OnCollapsibleTaskLoaderListener listener) {
        super(context, listener, MenuItemType.COLOR, ColorTotalLinkContract.CONTENT_URI, //
                null, null);
    }

    public ColorLoader(Context context, String markerId) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getColorTasks(markerId.toUpperCase()).build(), //
                new TaskSelectionBuilder().getOrderForTasks().build());
    }
}