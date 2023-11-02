package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultSortOrder;

@DatabaseTable(tableName = ProjectTotalLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = ProjectTotalLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = ProjectTotalLinkContract.TABLE_NAME)
public class ProjectTotalLink extends BaseCollapsibleTotalLink<ProjectTotalLink> {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = ProjectTotalLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = ProjectTotalLinkContract.Uid, dataType = DataType.STRING, index = true, unique = true)
    private String mUid;

    @DatabaseField(columnName = ProjectTotalLinkContract.Tasks, dataType = DataType.INTEGER)
    private int mTasks;

    @DatabaseField(columnName = ProjectTotalLinkContract.TasksUnreaded, dataType = DataType.INTEGER)
    private int mTasksUnreaded;

    @DatabaseField(columnName = ProjectTotalLinkContract.TasksUncompleted, dataType = DataType.INTEGER)
    private int mTasksUncompleted;

    @DatabaseField(columnName = ProjectTotalLinkContract.TasksUncompletedUnreaded, dataType = DataType.INTEGER)
    private int mTasksUncompletedUnreaded;

    @DatabaseField(columnName = ProjectTotalLinkContract.Name, dataType = DataType.STRING)
    private String mName;

    @DefaultSortOrder
    @DatabaseField(columnName = ProjectTotalLinkContract.Orders, dataType = DataType.INTEGER)
    private int mOrder;

    @DatabaseField(columnName = ProjectTotalLinkContract.BelongCurrentUser, dataType = DataType.BOOLEAN)
    private boolean mBelongCurrentUser;

    @DatabaseField(columnName = ProjectTotalLinkContract.Level, dataType = DataType.INTEGER)
    private int mLevel;

    @DatabaseField(columnName = ProjectTotalLinkContract.HasBelow, dataType = DataType.BOOLEAN)
    private boolean mHasBelow;

    @DatabaseField(columnName = ProjectTotalLinkContract.Opened, dataType = DataType.BOOLEAN)
    private boolean mOpened;

    @DatabaseField(columnName = ProjectTotalLinkContract.Visible, dataType = DataType.BOOLEAN)
    private boolean mVisible;

    @DatabaseField(columnName = ProjectTotalLinkContract.Showed, dataType = DataType.BOOLEAN)
    private boolean mShowed;

    @DatabaseField(columnName = ProjectTotalLinkContract.Shared, dataType = DataType.BOOLEAN)
    private boolean mShared;

    @DatabaseField(columnName = ProjectTotalLinkContract.TasksNotes, dataType = DataType.INTEGER)
    private int mTasksNotes;

    @DatabaseField(columnName = ProjectTotalLinkContract.TasksFocus, dataType = DataType.INTEGER)
    private int mTasksFocus;


    public ProjectTotalLink() {}

    public ProjectTotalLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Uri getContentUri() {
        return ProjectTotalLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return ProjectTotalLinkContract.TABLE_NAME;
    }

    @Override
    public String getUid() {
        return mUid;
    }

    @Override
    public int getOrder() {
        return mOrder;
    }

    @Override
    public MenuItemType getMenuItemType() {
        if(isBelongCurrentUser()) {
            if(isShared()) {
                return MenuItemType.PROJECTS_SHARED;
            }
            else {
                return MenuItemType.PROJECTS;
            }
        }
        else {
            return MenuItemType.AVAILABLE_PROJECTS;
        }
    }

    @Override
    public int getLevel() {
        return mLevel;
    }

    @Override
    public boolean hasBelow() {
        return mHasBelow;
    }

    @Override
    public boolean isOpened() {
        return mOpened;
    }

    @Override
    public void setOpened(boolean opened) {
        mOpened = opened;
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    @Override
    public int getTasks() {
        return mTasks;
    }

    @Override
    public int getTasksUnreaded() {
        return mTasksUnreaded;
    }

    @Override
    public int getTasksUncompleted() {
        return mTasksUncompleted;
    }

    @Override
    public int getTasksUncompletedUnreaded() {
        return mTasksUncompletedUnreaded;
    }

    @Override
    public void setOrder(int order) {
        mOrder = order;
    }

    @Override
    public boolean isBelongCurrentUser() {
        return mBelongCurrentUser;
    }

    @Override
    public void setBelongCurrentUser(boolean belongCurrentUser) {
        mBelongCurrentUser = belongCurrentUser;
    }

    @Override
    public void setLevel(int level) {
        mLevel = level;
    }

    @Override
    public void setHasBelow(boolean hasBelow) {
        mHasBelow = hasBelow;
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
    public void setUid(String uid) {
        mUid = uid;
    }

    @Override
    public void setTasks(int tasks) {
        mTasks = tasks;
    }

    @Override
    public void setTasksUnreaded(int tasksUnreaded) {
        mTasksUnreaded = tasksUnreaded;
    }

    @Override
    public void setTasksUncompleted(int tasks) {
        mTasksUncompleted = tasks;
    }

    @Override
    public void setTasksUncompletedUnreaded(int tasksUnreaded) {
        mTasksUncompletedUnreaded = tasksUnreaded;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void setShowed(boolean showed) {
        mShowed = showed;
    }

    @Override
    public boolean isShared() {
        return mShared;
    }

    @Override
    public void setShared(boolean shared) {
        mShared = shared;
    }

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