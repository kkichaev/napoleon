package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
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

@DatabaseTable(tableName = UidToDeleteContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = UidToDeleteContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = UidToDeleteContract.TABLE_NAME)
public class UidToDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    private long mId;

    @DatabaseField(columnName = UidToDeleteContract.UID, canBeNull = false)
    @DefaultSortOrder(order = SortOrder.ASC)
    private String mUid;

    @DatabaseField(columnName = UidToDeleteContract.SERVER_CLASS, canBeNull = false)
    private String mServerClass;

    private static int[] sTableIndexes;

    public UidToDelete() {}

    public UidToDelete(BaseLionEntityInterface entity) {
        mUid = String.valueOf(entity.getId());
        mServerClass = entity.getServerClass();
    }

    public UidToDelete(Cursor c) {
        fillFastTable(c);

        mId = c.getLong(sTableIndexes[0]);
        mUid = c.getString(sTableIndexes[1]);
        mServerClass = c.getString(sTableIndexes[2]);
    }

    public ContentValues getContentValues() {
        final ContentValues cv = new ContentValues(2);
        cv.put(UidToDeleteContract.UID, mUid);
        cv.put(UidToDeleteContract.SERVER_CLASS, mServerClass);

        return cv;
    }

    public static ContentValues getContentValues(BaseLionEntityInterface entity) {
        final ContentValues cv = new ContentValues(2);
        cv.put(UidToDeleteContract.UID, String.valueOf(entity.getId()));
        cv.put(UidToDeleteContract.SERVER_CLASS, entity.getServerClass());

        return cv;
    }

    private static void fillFastTable(Cursor c) {
        if (sTableIndexes == null) {
            sTableIndexes = new int[3];
            sTableIndexes[0] = c.getColumnIndex(BaseColumns._ID);
            sTableIndexes[1] = c.getColumnIndex(UidToDeleteContract.UID);
            sTableIndexes[2] = c.getColumnIndex(UidToDeleteContract.SERVER_CLASS);
        }
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getServerClass() {
        return mServerClass;
    }

    public void setServerClass(String serverClass) {
        mServerClass = serverClass;
    }

    public static final void removeUidsFromTable(Context context, List<String> uids, String serverClass) {
        final StringBuilder sb = new StringBuilder();
        SelectionKeeper.equals(sb, UidToDeleteContract.SERVER_CLASS, serverClass);
        sb.append(SharedStrings.AND);
        SelectionKeeper.inToLowerCase(sb, UidToDeleteContract.UID, uids);

        context.getContentResolver().delete(UidToDeleteContract.CONTENT_URI, sb.toString(), null);
    }
}