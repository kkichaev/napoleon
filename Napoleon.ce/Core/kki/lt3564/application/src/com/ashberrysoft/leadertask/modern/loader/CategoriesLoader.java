package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class CategoriesLoader extends BaseCollapsibleTaskLoader {

    public CategoriesLoader(Context context, OnCollapsibleTaskLoaderListener listener) {
        super(context, listener, MenuItemType.CATEGORIES, CategoryTotalLinkContract.CONTENT_URI, //
                null, null);
    }

    public CategoriesLoader(Context context, String projectId) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getCategoryTasks(projectId).build(), //
                new TaskSelectionBuilder().getOrderForTasks().build());
    }
}