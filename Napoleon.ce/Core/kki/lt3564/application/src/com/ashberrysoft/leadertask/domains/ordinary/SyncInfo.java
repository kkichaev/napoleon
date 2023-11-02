package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@DatabaseTable(tableName = SyncInfoContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = SyncInfoContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = SyncInfoContract.TABLE_NAME)
public class SyncInfo implements Serializable {

    /** возможные состояния статусов во/вне время/ени глобальной синхронизации */
    public enum SyncInfoErrorType {
        /** ничего не происходит */
        NONE, //
        /** в процессе обновления сетевого/адаптера/отображения ошибки */
        IN_PROGRESS, //
        /** в ожидании завершения остальных */
        ENDED, //
        /** нужно отобразить ошибку */
        ERROR
    }

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = SyncInfoContract._ID, generatedId = true)
    private int mId;

    /** состояние сетевой синхронизации */
    @DatabaseField(columnName = SyncInfoContract.SYNC_STATUS, dataType = DataType.ENUM_INTEGER)
    private SyncInfoErrorType mSyncStatus;

    /** состояние обновления адаптера слайдинг меню */
    @DatabaseField(columnName = SyncInfoContract.MENU_STATUS, dataType = DataType.ENUM_INTEGER)
    private SyncInfoErrorType mMenuStatus;

    /** состояние обновления адаптера отображающего задачи */
    @DatabaseField(columnName = SyncInfoContract.LIST_STATUS, dataType = DataType.ENUM_INTEGER)
    private SyncInfoErrorType mListStatus;

    /** состояние отображения ошибки (если возникла во время сетевой синхронизации) */
    @DatabaseField(columnName = SyncInfoContract.ERROR_STATUS, dataType = DataType.ENUM_INTEGER)
    private SyncInfoErrorType mErrorStatus;

    @DatabaseField(columnName = SyncInfoContract.ERROR_CODE)
    private int mErrorCode;

    @DatabaseField(columnName = SyncInfoContract.ERROR_MESSAGE)
    private String mErrorMessage;

    @DatabaseField(columnName = SyncInfoContract.LAST_SYNC_TIME)
    private long mLastSyncTime;

    private static int[] sTableIndexes;

    /**
     * создает запись в таблице<br/>
     * вызывать только при запуске приложения
     */
    public static void initialization(Context context) {
        final ContentResolver cr = context.getContentResolver();
        final Cursor cursor = cr.query(SyncInfoContract.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() != 1) {
            cr.delete(SyncInfoContract.CONTENT_URI, null, null);
            cr.insert(SyncInfoContract.CONTENT_URI, new SyncInfo().getContentValues());
        }
        cursor.close();
    }

    public static void updateSynchronizationInfo(Context context, ContentValues cv) {
        context.getContentResolver().update(SyncInfoContract.CONTENT_URI, cv, null, null);
    }

    public SyncInfo() {
        mSyncStatus = SyncInfoErrorType.NONE;
        mMenuStatus = SyncInfoErrorType.NONE;
        mListStatus = SyncInfoErrorType.NONE;
        mErrorStatus = SyncInfoErrorType.NONE;
    }

    public SyncInfo(Cursor c) {
        fillFastTable(c);

        mId = c.getInt(sTableIndexes[0]);
        mSyncStatus = SyncInfoErrorType.values()[c.getInt(sTableIndexes[1])];
        mMenuStatus = SyncInfoErrorType.values()[c.getInt(sTableIndexes[2])];
        mListStatus = SyncInfoErrorType.values()[c.getInt(sTableIndexes[3])];
        mErrorStatus = SyncInfoErrorType.values()[c.getInt(sTableIndexes[4])];
        mErrorCode = c.getInt(sTableIndexes[5]);
        mErrorMessage = c.getString(sTableIndexes[6]);
        mLastSyncTime = c.getLong(sTableIndexes[7]);
    }

    private static void fillFastTable(Cursor c) {
        if (sTableIndexes == null) {
            sTableIndexes = new int[8];
            sTableIndexes[0] = c.getColumnIndex(SyncInfoContract._ID);
            sTableIndexes[1] = c.getColumnIndex(SyncInfoContract.SYNC_STATUS);
            sTableIndexes[2] = c.getColumnIndex(SyncInfoContract.MENU_STATUS);
            sTableIndexes[3] = c.getColumnIndex(SyncInfoContract.LIST_STATUS);
            sTableIndexes[4] = c.getColumnIndex(SyncInfoContract.ERROR_STATUS);
            sTableIndexes[5] = c.getColumnIndex(SyncInfoContract.ERROR_CODE);
            sTableIndexes[6] = c.getColumnIndex(SyncInfoContract.ERROR_MESSAGE);
            sTableIndexes[7] = c.getColumnIndex(SyncInfoContract.LAST_SYNC_TIME);
        }
    }

    // public void resetValues() {
    // mSyncStatus = SyncInfoErrorType.NONE;
    // mMenuStatus = SyncInfoErrorType.NONE;
    // mListStatus = SyncInfoErrorType.NONE;
    // mErrorStatus = SyncInfoErrorType.NONE;
    // mErrorCode = 0;
    // mErrorMessage = null;
    // }

    public ContentValues getContentValues() {
        final ContentValues cv = new ContentValues(7);
        
        cv.put(SyncInfoContract.SYNC_STATUS, mSyncStatus.ordinal());
        cv.put(SyncInfoContract.MENU_STATUS, mMenuStatus.ordinal());
        cv.put(SyncInfoContract.LIST_STATUS, mListStatus.ordinal());
        cv.put(SyncInfoContract.ERROR_STATUS, mErrorStatus.ordinal());
        cv.put(SyncInfoContract.ERROR_CODE, mErrorCode);
        cv.put(SyncInfoContract.ERROR_MESSAGE, mErrorMessage);
        cv.put(SyncInfoContract.LAST_SYNC_TIME, mLastSyncTime);

        return cv;
    }

    public SyncInfoErrorType getSyncStatus() {
        return mSyncStatus;
    }

    public void setSyncStatus(SyncInfoErrorType syncStatus) {
        mSyncStatus = syncStatus;
    }

    public SyncInfoErrorType getMenuStatus() {
        return mMenuStatus;
    }

    public void setMenuStatus(SyncInfoErrorType menuStatus) {
        mMenuStatus = menuStatus;
    }

    public SyncInfoErrorType getListStatus() {
        return mListStatus;
    }

    public void setListStatus(SyncInfoErrorType listStatus) {
        mListStatus = listStatus;
    }

    public SyncInfoErrorType getErrorStatus() {
        return mErrorStatus;
    }

    public void setErrorStatus(SyncInfoErrorType errorStatus) {
        mErrorStatus = errorStatus;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public long getLastSyncTime() {
        return mLastSyncTime;
    }

    public void setLastSyncTime(long lastSyncTime) {
        mLastSyncTime = lastSyncTime;
    }
}