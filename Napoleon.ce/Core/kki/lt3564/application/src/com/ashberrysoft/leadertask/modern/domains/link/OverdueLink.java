package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.OverdueLinkContract;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = OverdueLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = OverdueLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = OverdueLinkContract.TABLE_NAME)
public class OverdueLink extends BaseLink {

    @DatabaseField(columnName = OverdueLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = OverdueLinkContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = OverdueLinkContract.TaskId, dataType = DataType.INTEGER, index = true)
    private int mTaskId;

    @DatabaseField(columnName = OverdueLinkContract.Readed, dataType = DataType.BOOLEAN)
    private boolean mReaded;

    @DatabaseField(columnName = OverdueLinkContract.Status, dataType = DataType.INTEGER)
    private int mStatus;

    @DatabaseField(columnName = OverdueLinkContract.Focus, dataType = DataType.BOOLEAN)
    private boolean mFocus;

    public OverdueLink() {}

    public OverdueLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Uri getContentUri() {
        return OverdueLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return OverdueLinkContract.TABLE_NAME;
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