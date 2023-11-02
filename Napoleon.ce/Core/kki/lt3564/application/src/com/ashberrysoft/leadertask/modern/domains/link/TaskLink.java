package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskLinkContract;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = TaskLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = TaskLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = TaskLinkContract.TABLE_NAME)
public class TaskLink extends BaseLink {

    @DatabaseField(columnName = TaskLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = TaskLinkContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = TaskLinkContract.TaskId, dataType = DataType.INTEGER, index = true)
    private int mTaskId;

    @DatabaseField(columnName = TaskLinkContract.Readed, dataType = DataType.BOOLEAN)
    private boolean mReaded;

    @DatabaseField(columnName = TaskLinkContract.Status, dataType = DataType.INTEGER)
    private int mStatus;

    @DatabaseField(columnName = TaskLinkContract.Focus, dataType = DataType.BOOLEAN)
    private boolean mFocus;

    public TaskLink() {}

    public TaskLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Uri getContentUri() {
        return TaskLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return TaskLinkContract.TABLE_NAME;
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
    public int getTaskId() {
        return mTaskId;
    }

    @Override
    public void setTaskId(int taskId) {
        mTaskId = taskId;
    }

    @Override
    public boolean isReaded() {
        return mReaded;
    }

    @Override
    public void setReaded(boolean readed) {
        mReaded = readed;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public void setStatus(int status) {
        mStatus = status;
    }

    @Override
    public void setFocus(boolean focus) {
        mFocus = focus;
    }

    @Override
    public boolean getFocus() {
        return mFocus;
    }
}