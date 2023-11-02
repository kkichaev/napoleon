package com.ashberrysoft.leadertask.interfaces;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface CursorFiller {

    Uri getContentUri();

    String getTableName();

    ContentValues getContentValues(ContentValues cv);

    void fillFromCursor(Cursor cursor);
}