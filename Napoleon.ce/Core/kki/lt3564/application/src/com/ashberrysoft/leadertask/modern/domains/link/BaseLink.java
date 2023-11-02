package com.ashberrysoft.leadertask.modern.domains.link;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.LinkContract;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;

public abstract class BaseLink//
        implements CursorFiller {

    private static final Map<Uri, Integer[]> COLUMNS = new HashMap<>(5);

    public BaseLink() {}

    public BaseLink(Cursor cursor) {
        fillFromCursor(cursor);
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        cv = new ContentValues(4);

        cv.put(LinkContract.Uid, getUid());
        cv.put(LinkContract.TaskId, getTaskId());
        cv.put(LinkContract.Readed, isReaded());
        cv.put(LinkContract.Status, getStatus());
        cv.put(LinkContract.Focus, getFocus());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        Integer[] columns = COLUMNS.get(getContentUri());
        if (columns == null) {
            columns = new Integer[6];

            columns[0] = cursor.getColumnIndex(LinkContract._ID);
            columns[1] = cursor.getColumnIndex(LinkContract.Uid);
            columns[2] = cursor.getColumnIndex(LinkContract.TaskId);
            columns[3] = cursor.getColumnIndex(LinkContract.Readed);
            columns[4] = cursor.getColumnIndex(LinkContract.Status);
            columns[5] = cursor.getColumnIndex(LinkContract.Focus);

            COLUMNS.put(getContentUri(), columns);
        }

        setId(cursor.getInt(columns[0]));
        setUid(cursor.getString(columns[1]));
        setTaskId(cursor.getInt(columns[2]));
        setReaded(cursor.getInt(columns[3]) == 1);
        setStatus(cursor.getInt(columns[4]));
        setFocus(cursor.getInt(columns[5]) == 1);
    }

    public abstract int getId();

    public abstract void setId(int id);

    public abstract String getUid();

    public abstract void setUid(String uid);

    public abstract int getTaskId();

    public abstract void setTaskId(int taskId);

    public abstract boolean isReaded();

    public abstract void setReaded(boolean readed);

    public abstract int getStatus();

    public abstract void setStatus(int status);

    public abstract void setFocus(boolean focus);

    public abstract boolean getFocus();
    @Override
    public String toString() {
        return getId() + " " + getUid() + " " + getTaskId();
    }
}