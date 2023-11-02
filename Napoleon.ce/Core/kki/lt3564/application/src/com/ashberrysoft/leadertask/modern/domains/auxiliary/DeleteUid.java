package com.ashberrysoft.leadertask.modern.domains.auxiliary;

import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.DeleteUidContract;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@DatabaseTable(tableName = DeleteUidContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = DeleteUidContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = DeleteUidContract.TABLE_NAME)
public final class DeleteUid extends BaseProcessUid {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = DeleteUidContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = DeleteUidContract.Uid, dataType = DataType.STRING)
    private String mUid;

    @DatabaseField(columnName = DeleteUidContract.LionName, dataType = DataType.STRING)
    private String mLionName;

    public DeleteUid() {}

    public DeleteUid(String uid, String lionName) {
        setUid(uid);
        setLionName(lionName);
    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public int getIdTask() {
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
    public String getLionName() {
        return mLionName;
    }

    @Override
    public void setLionName(String lionName) {
        mLionName = lionName;
    }

    public static void addUids(Context context, List<String> uids, String lionName) {
        addUids(DeleteUidContract.CONTENT_URI, context, uids, lionName);
    }

    public static void removeUids(Context context, List<String> uids, String lionName) {
        removeUids(DeleteUidContract.CONTENT_URI, context, uids, lionName);
    }
}