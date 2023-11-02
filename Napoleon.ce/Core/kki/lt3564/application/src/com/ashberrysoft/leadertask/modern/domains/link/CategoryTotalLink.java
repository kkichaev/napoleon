package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultSortOrder;

@DatabaseTable(tableName = CategoryTotalLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = CategoryTotalLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = CategoryTotalLinkContract.TABLE_NAME)
public class CategoryTotalLink extends BaseCollapsibleTotalLink<CategoryTotalLink> {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = CategoryTotalLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = CategoryTotalLinkContract.Uid, dataType = DataType.STRING, index = true, unique = true)
    private String mUid;

    @DatabaseField(columnName = CategoryTotalLinkContract.Tasks, dataType = DataType.INTEGER)
    private int mTasks;

    @DatabaseField(columnName = CategoryTotalLinkContract.TasksUnreaded, dataType = DataType.INTEGER)
    private int mTasksUnreaded;

    @DatabaseField(columnName = CategoryTotalLinkContract.TasksUncompleted, dataType = DataType.INTEGER)
    private int mTasksUncompleted;

    @DatabaseField(columnName = CategoryTotalLinkContract.TasksUncompletedUnreaded, dataType = DataType.INTEGER)
    private int mTasksUncompletedUnreaded;

    @DatabaseField(columnName = CategoryTotalLinkContract.Name, dataType = DataType.STRING)
    private String mName;

    @DefaultSortOrder
    @DatabaseField(columnName = CategoryTotalLinkContract.Orders, dataType = DataType.INTEGER)
    private int mOrder;

    @DatabaseField(columnName = CategoryTotalLinkContract.BelongCurrentUser, dataType = DataType.BOOLEAN)
    private boolean mBelongCurrentUser;

    @DatabaseField(columnName = CategoryTotalLinkContract.Level, dataType = DataType.INTEGER)
    private int mLevel;

    @DatabaseField(columnName = CategoryTotalLinkContract.HasBelow, dataType = DataType.BOOLEAN)
    private boolean mHasBelow;

    @DatabaseField(columnName = CategoryTotalLinkContract.Opened, dataType = DataType.BOOLEAN)
    private boolean mOpened;

    @DatabaseField(columnName = CategoryTotalLinkContract.Visible, dataType = DataType.BOOLEAN)
    private boolean mVisible;

    @DatabaseField(columnName = CategoryTotalLinkContract.Showed, dataType = DataType.BOOLEAN)
    private boolean mShowed;

    @DatabaseField(columnName = CategoryTotalLinkContract.Shared, dataType = DataType.BOOLEAN)
    private boolean mShared;

    @DatabaseField(columnName = CategoryTotalLinkContract.TasksNotes, dataType = DataType.INTEGER)
    private int mTasksNotes;

    @DatabaseField(columnName = CategoryTotalLinkContract.TasksFocus, dataType = DataType.INTEGER)
    private int mTasksFocus;


    public CategoryTotalLink() {}

    public CategoryTotalLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Uri getContentUri() {
        return CategoryTotalLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return CategoryTotalLinkContract.TABLE_NAME;
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
        return MenuItemType.CATEGORIES;
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