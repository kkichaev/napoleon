package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = TaskTotalLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = TaskTotalLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = TaskTotalLinkContract.TABLE_NAME)
public class TaskTotalLink extends BaseTotalLink {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = TaskTotalLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = TaskTotalLinkContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = TaskTotalLinkContract.Tasks, dataType = DataType.INTEGER)
    private int mTasks;

    @DatabaseField(columnName = TaskTotalLinkContract.TasksUnreaded, dataType = DataType.INTEGER)
    private int mTasksUnreaded;

    @DatabaseField(columnName = TaskTotalLinkContract.TasksUncompleted, dataType = DataType.INTEGER)
    private int mTasksUncompleted;

    @DatabaseField(columnName = TaskTotalLinkContract.TasksUncompletedUnreaded, dataType = DataType.INTEGER)
    private int mTasksUncompletedUnreaded;

    @DatabaseField(columnName = TaskTotalLinkContract.TasksNotes, dataType = DataType.INTEGER)
    private int mTasksNotes;

    @DatabaseField(columnName = TaskTotalLinkContract.TasksFocus, dataType = DataType.INTEGER)
    private int mTasksFocus;

    // @DatabaseField(columnName = TaskTotalLinkContract.ParentId, dataType = DataType.STRING)
    // private String mParenId;

    public TaskTotalLink() {}

    public TaskTotalLink(Cursor cursor) {
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
        return TaskTotalLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return TaskTotalLinkContract.TABLE_NAME;
    }
    
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public MenuItemType getMenuItemType() {
        return null;
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

    //
    // public String getParenId() {
    // return mParenId;
    // }
    //
    // public void setParenId(String parenId) {
    // mParenId = parenId;
    // }
}