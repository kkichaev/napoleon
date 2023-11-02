package com.ashberrysoft.leadertask.modern.domains.auxiliary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProcessUidContract;
import com.ashberrysoft.leadertask.interfaces.IdentifierEntity;
import com.ashberrysoft.leadertask.interfaces.LionEntity;
import com.ashberrysoft.leadertask.utils.SharedStrings;

public abstract class BaseProcessUid implements Serializable, IdentifierEntity {

    private static final long serialVersionUID = 1L;

    private static Map<Class<?>, Integer[]> sColumns = new HashMap<>(2);

    public BaseProcessUid() {}

    public <DATA extends LionEntity<DATA>> BaseProcessUid(DATA entity) {
        setUid(entity.getUid());
        setLionName(entity.getLionName());
    }

    public BaseProcessUid(Cursor c) {
        Integer[] columns = sColumns.get(getClass());
        if (columns == null) {
            columns = new Integer[3];

            columns[0] = c.getColumnIndex(ProcessUidContract._ID);
            columns[1] = c.getColumnIndex(ProcessUidContract.Uid);
            columns[2] = c.getColumnIndex(ProcessUidContract.LionName);

            sColumns.put(getClass(), columns);
        }

        setId(c.getInt(columns[0]));
        setUid(c.getString(columns[1]));
        setLionName(c.getString(columns[2]));
    }

    public ContentValues getContentValues() {
        final ContentValues cv = new ContentValues(2);
        cv.put(ProcessUidContract.Uid, getUid());
        cv.put(ProcessUidContract.LionName, getLionName());

        return cv;
    }

    public abstract String getLionName();

    public abstract void setLionName(String lionName);

    public static <DATA extends LionEntity<DATA>> ContentValues getContentValues(DATA entity) {
        final ContentValues cv = new ContentValues(2);
        cv.put(ProcessUidContract.Uid, entity.getUid());
        cv.put(ProcessUidContract.LionName, entity.getLionName());

        return cv;
    }

    protected static final void addUids(Uri contentUri, Context context, List<String> uids, String lionName) {
        final ContentValues[] cvs = new ContentValues[uids.size()];
        int count = 0;

        for (String uid : uids) {
            final ContentValues cv = new ContentValues(2);
            cv.put(ProcessUidContract.Uid, uid);
            cv.put(ProcessUidContract.LionName, lionName);

            cvs[count++] = cv;
        }

        context.getContentResolver().bulkInsert(contentUri, cvs);
    }

    protected static final void removeUids(Uri contentUri, Context context, List<String> uids, String lionName) {
        final StringBuilder sb = new StringBuilder();

        SelectionKeeper.equals(sb, ProcessUidContract.LionName, lionName);
        if (uids != null && uids.size() > 0) {
            sb.append(SharedStrings.AND);
            SelectionKeeper.inToLowerCase(sb, ProcessUidContract._ID, uids);
        }

        context.getContentResolver().delete(contentUri, sb.toString(), null);
    }
}