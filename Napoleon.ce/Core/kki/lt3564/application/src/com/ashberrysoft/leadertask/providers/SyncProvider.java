package com.ashberrysoft.leadertask.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Sample data provider.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class SyncProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.ashberrysoft.leadertask.providers.SyncProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/items");
    public static final Uri CONTENT_INSERT_URI = Uri.parse("content://" + PROVIDER_NAME + "/insert10Items");
    private UriMatcher mUriMatcher;
    private static final int FEED_ITEMS = 1;
    private static final int FEED_INSERT = 2;
    public static final String METHOD_CLEAN_DB = "clean";

    // The account name
    public static final String ACCOUNT_NAME = "leadertaskaccount";

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
        // return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
        case FEED_ITEMS:
            return "vnd.android.cursor.dir/com.v2soft.V2AndLib.demoapp.providers.items";
        case FEED_INSERT:
            return "vnd.android.cursor.item/com.v2soft.V2AndLib.demoapp.providers.action";
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override
    public boolean onCreate() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(PROVIDER_NAME, "items", FEED_ITEMS);
        mUriMatcher.addURI(PROVIDER_NAME, "insert10Items", FEED_INSERT);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // prepare query
        switch (mUriMatcher.match(uri)) {
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = mUriMatcher.match(uri);
        switch (uriType) {
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int uriType = mUriMatcher.match(uri);
        switch (uriType) {
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
