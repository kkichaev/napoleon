package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = CalendarTotalLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = CalendarTotalLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = CalendarTotalLinkContract.TABLE_NAME)
public class CalendarTotalLink extends BaseTotalLink {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = CalendarTotalLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = CalendarTotalLinkContract.Uid, dataType = DataType.STRING, index = true, unique = true)
    private String mUid;

    @DatabaseField(columnName = CalendarTotalLinkContract.Tasks, dataType = DataType.INTEGER)
    private int mTasks;

    @DatabaseField(columnName = CalendarTotalLinkContract.TasksUnreaded, dataType = DataType.INTEGER)
    private int mTasksUnreaded;

    @DatabaseField(columnName = CalendarTotalLinkContract.TasksUncompleted, dataType = DataType.INTEGER)
    private int mTasksUncompleted;

    @DatabaseField(columnName = CalendarTotalLinkContract.TasksUncompletedUnreaded, dataType = DataType.INTEGER)
    private int mTasksUncompletedUnreaded;

    @DatabaseField(columnName = CalendarTotalLinkContract.TasksNotes, dataType = DataType.INTEGER)
    private int mTasksNotes;

    @DatabaseField(columnName = CalendarTotalLinkContract.TasksFocus, dataType = DataType.INTEGER)
    private int mTasksFocus;

    private long mUniqueId;
    private MenuItemType mItemType;

    public CalendarTotalLink() {
        mItemType = MenuItemType.TODAY;
    }

    public CalendarTotalLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public long getUniqueId() {
        return mUniqueId;
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

        try {
            mUniqueId = Long.parseLong(uid);
            mItemType = TimeHelper.getInstance().isToday(getUniqueId()) ? MenuItemType.TODAY : MenuItemType.CALENDAR_DAY;

        } catch (Exception e) {}
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
        return CalendarTotalLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return CalendarTotalLinkContract.TABLE_NAME;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public MenuItemType getMenuItemType() {
        return mItemType;
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