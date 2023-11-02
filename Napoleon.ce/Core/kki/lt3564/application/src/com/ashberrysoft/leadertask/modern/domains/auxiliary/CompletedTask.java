package com.ashberrysoft.leadertask.modern.domains.auxiliary;

import java.io.Serializable;
import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CompletedTaskContract;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.interfaces.IdentifierEntity;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = CompletedTaskContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = CompletedTaskContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = CompletedTaskContract.TABLE_NAME)
public final class CompletedTask implements CursorFiller, IdentifierEntity, Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = CompletedTaskContract._ID, dataType = DataType.INTEGER, id = true)
    private int mId;

    @DatabaseField(columnName = CompletedTaskContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = CompletedTaskContract.ParentCompleted, dataType = DataType.BOOLEAN)
    private boolean mParentCompleted;

    @DatabaseField(columnName = CompletedTaskContract.TaskCompleted, dataType = DataType.BOOLEAN)
    private boolean mTaskCompleted;

    private int mHashCode;
    private static int[] sColumns;

    public CompletedTask() {}

    public CompletedTask(LTask task, String currentUser) {
        setId(task.getIdTask());
        setUid(task.getUid());
        setTaskCompleted(TaskHelper.isCompleted(task.getStatus(),//
                currentUser, task.getEmailCustomer()));
    }

    public CompletedTask(int id, String uid) {
        mId = id;
        mUid = uid;
    }

    public CompletedTask(Cursor c) {
        fillFromCursor(c);
    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public int getIdTask() {
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

    public boolean isParentCompleted() {
        return mParentCompleted;
    }

    public void setParentCompleted(boolean parentCompleted) {
        mParentCompleted = parentCompleted;
    }

    public boolean isTaskCompleted() {
        return mTaskCompleted;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        mTaskCompleted = taskCompleted;
    }

    @Override
    public Uri getContentUri() {
        return CompletedTaskContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return CompletedTaskContract.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        cv = new ContentValues(4);

        cv.put(CompletedTaskContract._ID, getIdTask());
        cv.put(CompletedTaskContract.Uid, getUid());
        cv.put(CompletedTaskContract.ParentCompleted, isParentCompleted());
        cv.put(CompletedTaskContract.TaskCompleted, isTaskCompleted());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        if (sColumns == null) {
            synchronized (CompletedTask.class) {
                if (sColumns == null) {
                    sColumns = new int[4];

                    sColumns[0] = cursor.getColumnIndex(CompletedTaskContract._ID);
                    sColumns[1] = cursor.getColumnIndex(CompletedTaskContract.Uid);
                    sColumns[2] = cursor.getColumnIndex(CompletedTaskContract.ParentCompleted);
                    sColumns[3] = cursor.getColumnIndex(CompletedTaskContract.TaskCompleted);
                }
            }
        }

        setId(cursor.getInt(sColumns[0]));
        setUid(cursor.getString(sColumns[1]));
        setParentCompleted(cursor.getInt(sColumns[2]) == 1);
        setTaskCompleted(cursor.getInt(sColumns[3]) == 1);
    }

    @Override
    public int hashCode() {
        if (mHashCode == 0) {
            mHashCode = (getId() + getUid()).hashCode();
        }
        return mHashCode;
    }
}