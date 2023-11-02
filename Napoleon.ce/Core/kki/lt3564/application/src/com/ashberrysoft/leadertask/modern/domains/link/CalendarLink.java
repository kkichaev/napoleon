package com.ashberrysoft.leadertask.modern.domains.link;

import java.util.Calendar;

import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarLinkContract;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = CalendarLinkContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = CalendarLinkContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = CalendarLinkContract.TABLE_NAME)
public class CalendarLink extends BaseLink {

    private final static Calendar CALENDAR = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);

    @DatabaseField(columnName = CalendarLinkContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = CalendarLinkContract.Uid, dataType = DataType.STRING, index = true)
    private String mUid;

    @DatabaseField(columnName = CalendarLinkContract.TaskId, dataType = DataType.INTEGER, index = true)
    private int mTaskId;

    @DatabaseField(columnName = CalendarLinkContract.Readed, dataType = DataType.BOOLEAN)
    private boolean mReaded;

    @DatabaseField(columnName = CalendarLinkContract.Status, dataType = DataType.INTEGER)
    private int mStatus;

    @DatabaseField(columnName = CalendarLinkContract.Focus, dataType = DataType.BOOLEAN)
    private boolean mFocus;

    public CalendarLink() {}

    public CalendarLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Uri getContentUri() {
        return CalendarLinkContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return CalendarLinkContract.TABLE_NAME;
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

    private static long getUidFromDate(long date) {
        synchronized (CALENDAR) {
            CALENDAR.setTimeInMillis(date);
            
            final long answer = TimeHelper.roundCalendar(CALENDAR, true).getTimeInMillis();
            return answer;
        }
    }

    public static long getLongUidFromDate(long date) {
        return getUidFromDate(date);
    }

    public static String getStringUidFromDate(long date) {
        return String.valueOf(getUidFromDate(date));
    }
}