package ru.sobr.app.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SobrProvider extends ContentProvider {

    private static final int PROFILES = 100;
    private static final int PROFILES_ID = 101;

    private static final UriMatcher sUriMatcher;
    private SobrDatabase mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new SobrDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        String groupBy = null;
        String defaultSortOrder;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case PROFILES:
                qb.setTables(SobrDatabase.PROFILES);
                defaultSortOrder = SobrContract.Profiles.DEFAULT_SORT_ORDER;
                break;
            case PROFILES_ID:
                qb.setTables(SobrDatabase.PROFILES);
                defaultSortOrder = SobrContract.Profiles.DEFAULT_SORT_ORDER;
                qb.appendWhere("_id=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = defaultSortOrder;
        } else {
            orderBy = sortOrder;
        }
        Cursor c = qb.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, groupBy,
                null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PROFILES:
                return SobrContract.Profiles.CONTENT_TYPE;
            case PROFILES_ID:
                return SobrContract.Profiles.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri contentUri;
        String tableName;
        switch (sUriMatcher.match(uri)) {
            case PROFILES:
                contentUri = SobrContract.Profiles.CONTENT_URI;
                tableName = SobrDatabase.PROFILES;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long rowId = mOpenHelper.getWritableDatabase().insert(tableName, null, contentValues);
        if (rowId > 0) {
            Uri itemUri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        String tableName;
        switch (sUriMatcher.match(uri)) {
            case PROFILES:
                tableName = SobrDatabase.PROFILES;
                break;
            case PROFILES_ID:
                tableName = SobrDatabase.PROFILES;
                where = "_id=" + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = mOpenHelper.getWritableDatabase().delete(tableName, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        String tableName;
        switch (sUriMatcher.match(uri)) {
            case PROFILES:
                tableName = SobrDatabase.PROFILES;
                break;
            case PROFILES_ID:
                tableName = SobrDatabase.PROFILES;
                where = "_id=" + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = mOpenHelper.getWritableDatabase().update(tableName, contentValues, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(SobrContract.AUTHORITY, "Profiles", PROFILES);
        sUriMatcher.addURI(SobrContract.AUTHORITY, "Profiles/#", PROFILES_ID);

    }

}
