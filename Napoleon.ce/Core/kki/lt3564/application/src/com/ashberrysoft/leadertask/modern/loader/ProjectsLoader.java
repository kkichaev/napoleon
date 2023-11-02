package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class ProjectsLoader extends BaseCollapsibleTaskLoader {

    public ProjectsLoader(Context context, OnCollapsibleTaskLoaderListener listener, MenuItemType type) {
        super(context, listener, type, ProjectTotalLinkContract.CONTENT_URI, SelectionKeeper.equals(null,
                ProjectTotalLinkContract.BelongCurrentUser, type == MenuItemType.PROJECTS), null);
    }

    public ProjectsLoader(Context context, String projectId) {
        super(context, LTaskContract.CONTENT_URI,//
                new TaskSelectionBuilder().getProjectTasks(projectId).build(), //
                new TaskSelectionBuilder().getOrderForTasks().build());
    }
}