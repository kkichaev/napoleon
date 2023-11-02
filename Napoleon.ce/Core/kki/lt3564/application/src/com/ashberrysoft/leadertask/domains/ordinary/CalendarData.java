package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.CalendarDataContract;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultSortOrder;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.SortOrder;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@DatabaseTable(tableName = CalendarDataContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = CalendarDataContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = CalendarDataContract.TABLE_NAME)
public class CalendarData implements Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = CalendarDataContract._ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = CalendarDataContract.DATE, unique = true)
    @DefaultSortOrder(order = SortOrder.ASC)
    private long mDate;

    @DatabaseField(columnName = CalendarDataContract.TOTAL_TASKS)
    private int mTotalTasks;

    @DatabaseField(columnName = CalendarDataContract.UNCOMPLETED_TASKS)
    private int mUncompletedTasks;

    private static int[] sTableIndexes;

    public CalendarData() {
    }

    public CalendarData(Cursor c) {
        fillFastTable(c);

        mId = c.getInt(sTableIndexes[0]);
        mDate = c.getLong(sTableIndexes[1]);
        mTotalTasks = c.getInt(sTableIndexes[2]);
        mUncompletedTasks = c.getInt(sTableIndexes[3]);
    }

    public ContentValues getContentValues() {
        final ContentValues cv = new ContentValues(3);
        cv.put(CalendarDataContract.DATE, mDate);
        cv.put(CalendarDataContract.TOTAL_TASKS, mTotalTasks);
        cv.put(CalendarDataContract.UNCOMPLETED_TASKS, mUncompletedTasks);

        return cv;
    }

    private static void fillFastTable(Cursor c) {
        if (sTableIndexes == null) {
            sTableIndexes = new int[4];
            sTableIndexes[0] = c.getColumnIndex(CalendarDataContract._ID);
            sTableIndexes[1] = c.getColumnIndex(CalendarDataContract.DATE);
            sTableIndexes[2] = c.getColumnIndex(CalendarDataContract.TOTAL_TASKS);
            sTableIndexes[3] = c.getColumnIndex(CalendarDataContract.UNCOMPLETED_TASKS);
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public int getTotalTasks() {
        return mTotalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        mTotalTasks = totalTasks;
    }

    public int getUncompletedTasks() {
        return mUncompletedTasks;
    }

    public void setUncompletedTasks(int uncompletedTasks) {
        mUncompletedTasks = uncompletedTasks;
    }
}