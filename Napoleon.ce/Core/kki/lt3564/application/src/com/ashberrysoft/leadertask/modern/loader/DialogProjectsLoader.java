package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectLinkContract;
import com.ashberrysoft.leadertask.domains.ordinary.Project;

public class DialogProjectsLoader extends BaseDialogLoader<Project> {

    public DialogProjectsLoader(Context context, OnDialogLoadListener<Project> listener) {
        super(context, ProjectLinkContract.CONTENT_URI, null, SelectionKeeper.equals(null, ProjectLinkContract._ID, 0),
                null, null, listener);
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }
}