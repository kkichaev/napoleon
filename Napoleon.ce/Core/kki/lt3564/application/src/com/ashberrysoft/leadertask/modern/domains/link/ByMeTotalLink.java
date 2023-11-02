package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = ByMeTotalLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = ByMeTotalLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = ByMeTotalLinkContract.TABLE_NAME)
public class ByMeTotalLink extends BaseTotalLink {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = ByMeTotalLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = ByMeTotalLinkContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = ByMeTotalLinkContract.Tasks, dataType = DataType.INTEGER)
    private int mTasks;

    @DatabaseField(columnName = ByMeTotalLinkContract.TasksUnreaded, dataType = DataType.INTEGER)
    private int mTasksUnreaded;

    @DatabaseField(columnName = ByMeTotalLinkContract.TasksUncompleted, dataType = DataType.INTEGER)
    private int mTasksUncompleted;

    @DatabaseField(columnName = ByMeTotalLinkContract.TasksUncompletedUnreaded, dataType = DataType.INTEGER)
    private int mTasksUncompletedUnreaded;

    @DatabaseField(columnName = ByMeTotalLinkContract.TasksNotes, dataType = DataType.INTEGER)
    private int mTasksNotes;

    @DatabaseField(columnName = ByMeTotalLinkContract.TasksFocus, dataType = DataType.INTEGER)
    private int mTasksFocus;

    public ByMeTotalLink() {}

    public ByMeTotalLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    @Override
    public String getUid() {
        return mUid;
    }

    @Override
    public void setUid(String uid) {
        mUid = uid;
    }

    @Override
    public int getTasks() {
        return mTasks;
    }

    @Override
    public void setTasks(int tasks) {
        mTasks = tasks;
    }

    @Override
    public int getTasksUnreaded() {
        return mTasksUnreaded;
    }

    @Override
    public void setTasksUnreaded(int tasksUnreaded) {
        mTasksUnreaded = tasksUnreaded;
    }

    @Override
    public int getTasksUncompleted() {
        return mTasksUncompleted;
    }

    @Override
    public void setTasksUncompleted(int tasksUncompleted) {
        mTasksUncompleted = tasksUncompleted;
    }

    @Override
    public int getTasksUncompletedUnreaded() {
        return mTasksUncompletedUnreaded;
    }

    @Override
    public void setTasksUncompletedUnreaded(int tasksUncompletedUnreaded) {
        mTasksUncompletedUnreaded = tasksUncompletedUnreaded;
    }

    @Override
    public Uri getContentUri() {
        return ByMeTotalLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return ByMeTotalLinkContract.TABLE_NAME;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public MenuItemType getMenuItemType() {
        return MenuItemType.BY_ME;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean hasBelow() {
        return false;
    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void setOpened(boolean opened) {}

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {}

    @Override
    public int getTasksNotes() {
        return mTasksNotes;
    }

    @Override
    public int getTasksFocus() {
        return mTasksFocus;
    }

    @Override
    public void setTasksNotes(int tasksNotes) {
        mTasksNotes = tasksNotes;
    }

    @Override
    public void setTasksFocus(int tasks) {
        mTasksFocus = tasks;
    }

}