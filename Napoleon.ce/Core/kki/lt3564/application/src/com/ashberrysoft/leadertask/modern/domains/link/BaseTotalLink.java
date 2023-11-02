package com.ashberrysoft.leadertask.modern.domains.link;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public abstract class BaseTotalLink //
        implements CursorFiller, BaseMenuItem {

    private static final long serialVersionUID = 1L;
    private static final Map<Uri, Integer[]> COLUMNS = new HashMap<>(5);

    private String mName;

    @Override
    public String getUid() {
        return null;
    }

    public BaseTotalLink() {}

    public BaseTotalLink(Cursor cursor) {
        fillFromCursor(cursor);
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        cv = new ContentValues(6);

        cv.put(TotalLinkContract.Uid, getUid());
        cv.put(TotalLinkContract.Tasks, getTasks());
        cv.put(TotalLinkContract.TasksUnreaded, getTasksUnreaded());
        cv.put(TotalLinkContract.TasksUncompleted, getTasksUncompleted());
        cv.put(TotalLinkContract.TasksUncompletedUnreaded, getTasksUncompletedUnreaded());
        cv.put(TotalLinkContract.TasksNotes, getTasksNotes());
        cv.put(TotalLinkContract.TasksFocus, getTasksFocus());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        Integer[] columns = COLUMNS.get(getContentUri());
        if (columns == null) {
            columns = new Integer[8];

            columns[0] = cursor.getColumnIndex(TotalLinkContract._ID);
            columns[1] = cursor.getColumnIndex(TotalLinkContract.Uid);
            columns[2] = cursor.getColumnIndex(TotalLinkContract.Tasks);
            columns[3] = cursor.getColumnIndex(TotalLinkContract.TasksUnreaded);
            columns[4] = cursor.getColumnIndex(TotalLinkContract.TasksUncompleted);
            columns[5] = cursor.getColumnIndex(TotalLinkContract.TasksUncompletedUnreaded);
            columns[6] = cursor.getColumnIndex(TotalLinkContract.TasksNotes);
            columns[7] = cursor.getColumnIndex(TotalLinkContract.TasksFocus);

            COLUMNS.put(getContentUri(), columns);
        }

        setId(cursor.getInt(columns[0]));
        setUid(cursor.getString(columns[1]));
        setTasks(cursor.getInt(columns[2]));
        setTasksUnreaded(cursor.getInt(columns[3]));
        setTasksUncompleted(cursor.getInt(columns[4]));
        setTasksUncompletedUnreaded(cursor.getInt(columns[5]));
        setTasksNotes(cursor.getInt(columns[6]));
        setTasksFocus(cursor.getInt(columns[7]));
    }

    public abstract int getId();

    public abstract void setId(int id);

    @Override
    public long getUniqueId() {
        return getId();
    }

    public abstract void setUid(String uid);

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public abstract void setTasks(int tasks);

    public abstract void setTasksUnreaded(int tasksUnreaded);

    public abstract void setTasksUncompleted(int tasks);

    public abstract void setTasksUncompletedUnreaded(int tasksUnreaded);

    public abstract void setTasksNotes(int tasksNotes);

    public void incrementTasks() {
        sumToTasks(1);
    }

    public void decrementTasks() {
        sumToTasks(-1);
    }

    public void decrementTasksNotes() {
        sumToTasksNotes(-1);
    }

    public void incrementTasksUnreaded() {
        sumToTasksUnreaded(1);
    }

    public void decrementTasksUnreaded() {
        sumToTasksUnreaded(-1);
    }

    public void decrementTasksFocused() {
        setTasksFocus(getTasksFocus() - 1);
    }

    public void incrementTasksUncompleted() {
        sumToTasksUncompleted(1);
    }

    public void decrementTasksUncompleted() {
        sumToTasksUncompleted(-1);
    }

    public void incrementTasksUncompletedUnreaded() {
        sumToTasksUncompletedUnreaded(1);
    }

    public void decrementTasksUncompletedUnreaded() {
        sumToTasksUncompletedUnreaded(-1);
    }

    public void sumToTasks(int i) {
        setTasks(getTasks() + i);
    }

    public void sumToTasksNotes(int i) {
        setTasksNotes(getTasksNotes() + i);
    }

    public void sumToTasksUnreaded(int i) {
        setTasksUnreaded(getTasksUnreaded() + i);
    }

    public void sumToTasksUncompleted(int i) {
        setTasksUncompleted(getTasksUncompleted() + i);
    }

    public void sumToTasksUncompletedUnreaded(int i) {
        setTasksUncompletedUnreaded(getTasksUncompletedUnreaded() + i);
    }

    public abstract void setTasksFocus(int tasks);

    private final StringBuilder mSb = new StringBuilder();

    @Override
    public String toString() {
        Utils.clearStringBuilder(mSb);

        mSb.append(getClass().getSimpleName());
        mSb.append(SharedStrings.TAB_C);
        mSb.append(getUid());
        mSb.append(SharedStrings.TAB_C);
        mSb.append(getTasks());
        mSb.append(SharedStrings.TAB_C);
        mSb.append(getTasksUnreaded());
        mSb.append(SharedStrings.TAB_C);
        mSb.append(getTasksUncompleted());
        mSb.append(SharedStrings.TAB_C);
        mSb.append(getTasksUncompletedUnreaded());
        mSb.append(SharedStrings.TAB_C);
        mSb.append(getTasksNotes());
        mSb.append(getTasksFocus());

        return mSb.toString();
    }

    public void resetValues() {
        setTasks(0);
        setTasksUnreaded(0);
        setTasksUncompleted(0);
        setTasksUncompletedUnreaded(0);
        setTasksNotes(0);
        setTasksFocus(0);
    }
}