package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = ForMeTotalLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = ForMeTotalLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = ForMeTotalLinkContract.TABLE_NAME)
public class ForMeTotalLink extends BaseTotalLink {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = ForMeTotalLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = ForMeTotalLinkContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = ForMeTotalLinkContract.Tasks, dataType = DataType.INTEGER)
    private int mTasks;

    @DatabaseField(columnName = ForMeTotalLinkContract.TasksUnreaded, dataType = DataType.INTEGER)
    private int mTasksUnreaded;

    @DatabaseField(columnName = ForMeTotalLinkContract.TasksUncompleted, dataType = DataType.INTEGER)
    private int mTasksUncompleted;

    @DatabaseField(columnName = ForMeTotalLinkContract.TasksUncompletedUnreaded, dataType = DataType.INTEGER)
    private int mTasksUncompletedUnreaded;

    @DatabaseField(columnName = ForMeTotalLinkContract.TasksNotes, dataType = DataType.INTEGER)
    private int mTasksNotes;

    @DatabaseField(columnName = ForMeTotalLinkContract.TasksFocus, dataType = DataType.INTEGER)
    private int mTasksFocus;


    public ForMeTotalLink() {}

    public ForMeTotalLink(Cursor cursor) {
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
        return ForMeTotalLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return ForMeTotalLinkContract.TABLE_NAME;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public MenuItemType getMenuItemType() {
        return MenuItemType.FOR_ME;
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