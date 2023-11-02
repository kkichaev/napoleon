package com.ashberrysoft.leadertask.modern.domains.auxiliary;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.VerticalDepthTaskContract;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = VerticalDepthTaskContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = VerticalDepthTaskContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = VerticalDepthTaskContract.TABLE_NAME)
public class VerticalDepthTask//
        implements CursorFiller, Comparable<VerticalDepthTask> {

    @DatabaseField(columnName = VerticalDepthTaskContract._ID, dataType = DataType.INTEGER, id = true)
    private int mId;

    @DatabaseField(columnName = VerticalDepthTaskContract.Vertical, dataType = DataType.INTEGER)
    private int mVertical;

    @DatabaseField(columnName = VerticalDepthTaskContract.Depth, dataType = DataType.INTEGER)
    private int mDepth;

    @DatabaseField(columnName = VerticalDepthTaskContract.ParentId, dataType = DataType.STRING)
    private String mParentId;

    private boolean mPassed;
    private int mParent_Id;
    private LTask mTask;

    private static int[] sColumns;

    public VerticalDepthTask() {}

    public VerticalDepthTask(Cursor cursor) {
        fillFromCursor(cursor);
    }

    @Override
    public Uri getContentUri() {
        return VerticalDepthTaskContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return VerticalDepthTaskContract.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        cv = new ContentValues(4);

        cv.put(VerticalDepthTaskContract._ID, getId());
        cv.put(VerticalDepthTaskContract.Vertical, getVertical());
        cv.put(VerticalDepthTaskContract.Depth, getDepth());
        cv.put(VerticalDepthTaskContract.ParentId, getParentId());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        if (sColumns == null) {
            sColumns = new int[4];

            sColumns[0] = cursor.getColumnIndex(VerticalDepthTaskContract._ID);
            sColumns[1] = cursor.getColumnIndex(VerticalDepthTaskContract.Vertical);
            sColumns[2] = cursor.getColumnIndex(VerticalDepthTaskContract.Depth);
            sColumns[3] = cursor.getColumnIndex(VerticalDepthTaskContract.ParentId);
        }

        setId(cursor.getInt(sColumns[0]));
        setVertical(cursor.getInt(sColumns[1]));
        setDepth(cursor.getInt(sColumns[2]));
        setParentId(cursor.getString(sColumns[3]));

        setParent_Id(getParentId() == null ? 0 : Integer.parseInt(getParentId()));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getVertical() {
        return mVertical;
    }

    public void setVertical(int vertical) {
        mVertical = vertical;
    }

    public int getDepth() {
        return mDepth;
    }

    public void setDepth(int depth) {
        mDepth = depth;
    }

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        mParentId = parentId;
    }

    public boolean isPassed() {
        return mPassed;
    }

    public void setPassed(boolean passed) {
        mPassed = passed;
    }

    public int getParent_Id() {
        return mParent_Id;
    }

    public void setParent_Id(int parent_Id) {
        mParent_Id = parent_Id;
    }

    public LTask getTask() {
        return mTask;
    }

    public void setTask(LTask task) {
        mTask = task;
    }

    @Override
    public int compareTo(VerticalDepthTask another) {
        if (another.getDepth() == getDepth()) {
            return 0;
        }
        return another.getDepth() > getDepth() ? 1 : -1;
    }
}