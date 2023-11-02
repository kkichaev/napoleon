package com.ashberrysoft.leadertask.modern.domains.link;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.FocusLinkContract;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = LionMetaData.FocusLinkContract.TABLE_NAME)

@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = FocusLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = FocusLinkContract.TABLE_NAME)
public class FocusLink extends BaseLink {

    @DatabaseField(columnName = FocusLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FocusLinkContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = FocusLinkContract.TaskId, dataType = DataType.INTEGER, index = true)
    private int mTaskId;

    @DatabaseField(columnName = FocusLinkContract.Readed, dataType = DataType.BOOLEAN)
    private boolean mReaded;

    @DatabaseField(columnName = FocusLinkContract.Status, dataType = DataType.INTEGER)
    private int mStatus;

    @DatabaseField(columnName = FocusLinkContract.Focus, dataType = DataType.BOOLEAN)
    private boolean mFocus;

    public FocusLink() {}

    public FocusLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Uri getContentUri() {
        return FocusLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return FocusLinkContract.TABLE_NAME;
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