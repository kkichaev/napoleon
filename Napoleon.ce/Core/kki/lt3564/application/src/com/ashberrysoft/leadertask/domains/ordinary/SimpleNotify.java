package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SimpleNotifyContract;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@DatabaseTable(tableName = SimpleNotifyContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = SimpleNotifyContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = SimpleNotifyContract.TABLE_NAME)
public class SimpleNotify implements Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    private long mId;

    @DatabaseField(columnName = SimpleNotifyContract.TASK_UUID)
    private String mTaskId;

    @DatabaseField(columnName = SimpleNotifyContract.NOTIFY_TIME)
    private long mNotifyTime;

    private static int[] sTableIndexes;

    public SimpleNotify() {
    }

    public SimpleNotify(Cursor c) {
        fillFastTable(c);

        mId = c.getInt(sTableIndexes[0]);
        mTaskId = c.getString(sTableIndexes[1]);
        mNotifyTime = c.getLong(sTableIndexes[2]);
    }

    private static void fillFastTable(Cursor c) {
        if (sTableIndexes == null) {
            sTableIndexes = new int[3];
            sTableIndexes[0] = c.getColumnIndex(SimpleNotifyContract._ID);
            sTableIndexes[1] = c.getColumnIndex(SimpleNotifyContract.TASK_UUID);
            sTableIndexes[2] = c.getColumnIndex(SimpleNotifyContract.NOTIFY_TIME);
        }
    }

    public ContentValues getContentValues() {
        final ContentValues cv = new ContentValues();
        cv.put(SimpleNotifyContract.TASK_UUID, mTaskId);
        cv.put(SimpleNotifyContract.NOTIFY_TIME, mNotifyTime);

        return cv;
    }

    public long getId() {
        return mId;
    }

    public int getIntId() {
        final int result = (int) mId;
        if (result == -1) {
            return intFromLong(mId);
        }

        return result;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getTaskId() {
        return mTaskId;
    }

    public void setTaskId(String taskId) {
        mTaskId = taskId;
    }

    public long getNotifyTime() {
        return mNotifyTime;
    }

    public void setNotifyTime(long notifyTime) {
        mNotifyTime = notifyTime;
    }

    public static int intFromLong(long l) {
        final boolean negative = l < 0;
        final String si = String.valueOf(Integer.MAX_VALUE);
        final String sl = String.valueOf(l);

        if (sl.length() < si.length()) {
            return Integer.parseInt(sl);
        }
        return (negative ? -1 : 1) * Integer.parseInt(sl.substring(sl.length() - si.length() + 1));
    }
}