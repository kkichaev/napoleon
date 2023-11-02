package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class TaskChildsLoader extends BaseLTaskLoader {

    public TaskChildsLoader(Context context, int parentId) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getTaskChilds(parentId).build(), //
                new TaskSelectionBuilder().getOrderForTasks().build());
    }
}