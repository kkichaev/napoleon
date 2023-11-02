package com.ashberrysoft.leadertask.modern.domains.auxiliary;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskNotifyContract;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = TaskNotifyContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = TaskNotifyContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = TaskNotifyContract.TABLE_NAME)
public class TaskNotify implements CursorFiller, Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = TaskNotifyContract._ID, dataType = DataType.INTEGER, id = true)
    private int mId;

    @DatabaseField(columnName = TaskNotifyContract.Time, dataType = DataType.LONG)
    private long mTime;

    private static int[] sColumns;

    public TaskNotify() {}

    public TaskNotify(Cursor c) {
        fillFromCursor(c);
    }

    @Override
    public Uri getContentUri() {
        return TaskNotifyContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return TaskNotifyContract.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        cv = new ContentValues(2);
        cv.put(TaskNotifyContract._ID, getId());
        cv.put(TaskNotifyContract.Time, getTime());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        if (sColumns == null) {
            sColumns = new int[2];
            sColumns[0] = cursor.getColumnIndex(TaskNotifyContract._ID);
            sColumns[1] = cursor.getColumnIndex(TaskNotifyContract.Time);
        }

        setId(cursor.getInt(sColumns[0]));
        setTime(cursor.getLong(sColumns[1]));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }
}