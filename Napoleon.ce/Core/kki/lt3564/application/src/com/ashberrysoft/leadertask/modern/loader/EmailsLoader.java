package com.ashberrysoft.leadertask.modern.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.view.MenuItem;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;

public class EmailsLoader extends  BaseLTaskLoader {
    public EmailsLoader(Context context, BaseMenuItem mi) {
        super(context, mi,  LionMetaData.LTaskContract.CONTENT_URI,
                new TaskSelectionBuilder().getEmailsTasks().build(),
                new TaskSelectionBuilder().getOrderForTasks().build());
    }

    @Override
    protected Cursor onLoadInBackground() {
        Cursor res = super.onLoadInBackground();
        BaseMenuItem mi = getMenuItem();

        if (res != null &&  mi != null && mi instanceof EmailsMenuItem)
            ((EmailsMenuItem) mi).countItems = res.getCount();

        return res;
    }
}
