package com.ashberrysoft.leadertask.modern.domains.auxiliary;

import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.SetBlockingContract;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = SetBlockingContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = SetBlockingContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = SetBlockingContract.TABLE_NAME)
public class SetBlocking//
        implements Serializable, CursorFiller {

    private static final long serialVersionUID = 1L;

    public static final String SELECTION = SelectionKeeper.equals(null, SetBlockingContract._ID, 1);
    public static final int CALLBACK_ID = R.id.callback_set_blocking;

    @DatabaseField(columnName = SetBlockingContract._ID, dataType = DataType.INTEGER, id = true)
    public final int mId = 1;

    @DatabaseField(columnName = SetBlockingContract.Blocking, dataType = DataType.BOOLEAN)
    public boolean mBlocking;

    private static Integer sColumnBlocking;

    public SetBlocking() {}

    public SetBlocking(boolean setBlocking) {
        setBlocking(setBlocking);
    }

    public SetBlocking(Cursor cursor) {
        fillFromCursor(cursor);
    }

    @Override
    public Uri getContentUri() {
        return SetBlockingContract.CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return SetBlockingContract.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        cv = new ContentValues(2);

        cv.put(SetBlockingContract._ID, 1);
        cv.put(SetBlockingContract.Blocking, isBlocking());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        if (sColumnBlocking == null) {
            sColumnBlocking = cursor.getColumnIndex(SetBlockingContract.Blocking);
        }

        setBlocking(cursor.getInt(sColumnBlocking) == 1);
    }

    public boolean isBlocking() {
        return mBlocking;
    }

    public void setBlocking(boolean setBlocking) {
        mBlocking = setBlocking;
    }

    public static void update(Context context, boolean setBlocking) {
        final SetBlocking blocking = new SetBlocking(setBlocking);
        context.getContentResolver().update(blocking.getContentUri(), blocking.getContentValues(null), SELECTION, null);
    }

    public static void create(Context context) {
        final SetBlocking blocking = new SetBlocking();
        context.getContentResolver().insert(blocking.getContentUri(), blocking.getContentValues(null));
    }
}