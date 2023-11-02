package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultSortOrder;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@DatabaseTable(tableName = TaskFileContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = TaskFileContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = TaskFileContract.TABLE_NAME)
public class TaskFile//
        implements Serializable, IEntity, BaseLionEntityInterface {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = TaskFileContract._ID, generatedId = true)
    private int mEmptyId;

    @DatabaseField(columnName = TaskFileContract.FIELD_UID, index = true, canBeNull = false)
    private UUID mId;

    @DatabaseField(columnName = TaskFileContract.FIELD_TASKUID, index = true)
    private UUID mTaskId;

    @DatabaseField(columnName = TaskFileContract.FIELD_FILEUID, index = true)
    private UUID mFileId;

    @DatabaseField(columnName = TaskFileContract.FIELD_EMAILCREATOR)
    private String mEmailCreator;

    @DatabaseField(columnName = TaskFileContract.ORDERS)
    @DefaultSortOrder
    private long mOrder;

    @DatabaseField(columnName = TaskFileContract.FIELD_FILENAME)
    private String mFileName;

    @DatabaseField(columnName = TaskFileContract.FIELD_FILESIZE)
    private long mFileSize;

    @DatabaseField(columnName = TaskFileContract.FIELD_FILEVERSION)
    private int mFileVersion;

    @DatabaseField(columnName = TaskFileContract.FIELD_USN_ENTITY)
    private int mUsnEntity;

    @DatabaseField(columnName = TaskFileContract.FIELD_USN_FIELD_ORDER)
    private int mUsnFieldOrder;

    @DatabaseField(columnName = TaskFileContract.FIELD_USN_FIELD_NAME)
    private int mUsnFieldName;

    @DatabaseField(columnName = TaskFileContract.FIELD_USN_FIELD_SIZE)
    private int mUsnFieldSize;

    @DatabaseField(columnName = TaskFileContract.FIELD_USN_FIELD_VERSION)
    private int mUsnFieldVersion;

    @DatabaseField(columnName = TaskFileContract.FILE_EXIST)
    private boolean mFileExist;

    @DatabaseField(columnName = TaskFileContract.DELETE_OBJECT)
    private boolean mDeleteObject;

    @DatabaseField(columnName = TaskFileContract.WEAK_LINK)
    private boolean mWeakLink;

    @DatabaseField(columnName = TaskFileContract.SEND_FILE)
    private boolean mSendFile;

    private int mHashCode;

    private static int[] sTableIndexes;

    public TaskFile() {
        mFileExist = false;
        mDeleteObject = false;
        mWeakLink = false;
        mSendFile = false;
    }

    /** For new files */
    public TaskFile(UUID id, UUID taskId, UUID fileId, String fileName, long fileSize, String emailCreator, long order) {
        mId = id == null ? UUID.randomUUID() : id;
        mTaskId = taskId;
        mFileId = fileId == null ? UUID.randomUUID() : fileId;
        mEmailCreator = emailCreator;
        mOrder = order;
        mFileName = fileName;
        mFileSize = fileSize;
        mFileVersion = 0;
        mUsnEntity = 0;
        mFileExist = true;
        mDeleteObject = false;
        mWeakLink = true;
        mSendFile = false;
    }

    public TaskFile(Cursor c) {
        setData(c);
    }

    public void setData(Cursor c) {
        fillFastTable(c);

        mId = UUID.fromString(c.getString(sTableIndexes[0]));
        mTaskId = UUID.fromString(c.getString(sTableIndexes[1]));
        mFileId = UUID.fromString(c.getString(sTableIndexes[2]));
        mEmailCreator = c.getString(sTableIndexes[3]);
        mOrder = c.getLong(sTableIndexes[4]);
        mFileName = c.getString(sTableIndexes[5]);
        mFileSize = c.getLong(sTableIndexes[6]);
        mFileVersion = c.getInt(sTableIndexes[7]);
        mUsnEntity = c.getInt(sTableIndexes[8]);
        mUsnFieldOrder = c.getInt(sTableIndexes[9]);
        mUsnFieldName = c.getInt(sTableIndexes[10]);
        mUsnFieldSize = c.getInt(sTableIndexes[11]);
        mUsnFieldVersion = c.getInt(sTableIndexes[12]);
        mFileExist = c.getInt(sTableIndexes[13]) == 1;
        mDeleteObject = c.getInt(sTableIndexes[14]) == 1;
        mWeakLink = c.getInt(sTableIndexes[15]) == 1;
        mSendFile = c.getInt(sTableIndexes[16]) == 1;
    }

    public void resetUsnFields() {
        mUsnEntity = 0;
        mUsnFieldOrder = 0;
        mUsnFieldName = 0;
        mUsnFieldSize = 0;
        mUsnFieldVersion = 0;
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (TaskFileContract.FIELD_UID.equals(key)) {
            setId(UUID.fromString(value));
        }

        else if (TaskFileContract.FIELD_TASKUID.equals(key)) {
            setTaskId(UUID.fromString(value));
        }

        else if (TaskFileContract.FIELD_FILEUID.equals(key)) {
            setFileId(UUID.fromString(value));
        }

        else if (TaskFileContract.FIELD_EMAILCREATOR.equals(key)) {
            setEmailCreator(value);
        }

        else if (TaskFileContract.FIELD_ORDER.equals(key)) {
            setOrder(Long.parseLong(value));
        }

        else if (TaskFileContract.FIELD_FILENAME.equals(key)) {
            setFileName(value);
        }

        else if (TaskFileContract.FIELD_FILESIZE.equals(key)) {
            setFileSize(Long.parseLong(value));
        }

        else if (TaskFileContract.FIELD_FILEVERSION.equals(key)) {
            setFileVersion(Integer.parseInt(value));
        }

        else if (TaskFileContract.FIELD_USN_ENTITY.equals(key)) {
            setUsnEntity(Integer.parseInt(value));
        }

        else if (TaskFileContract.FIELD_USN_FIELD_ORDER.equals(key)) {
            setUsnFieldOrder(Integer.parseInt(value));
        }

        else if (TaskFileContract.FIELD_USN_FIELD_NAME.equals(key)) {
            setUsnFieldName(Integer.parseInt(value));
        }

        else if (TaskFileContract.FIELD_USN_FIELD_SIZE.equals(key)) {
            setUsnFieldSize(Integer.parseInt(value));
        }

        else if (TaskFileContract.FIELD_USN_FIELD_VERSION.equals(key)) {
            setUsnFieldVersion(Integer.parseInt(value));
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(TaskFileContract.SERVER_CLASS));

        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_UID, String.valueOf(mId)));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_TASKUID, String.valueOf(mTaskId)));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_FILEUID, String.valueOf(mFileId)));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_EMAILCREATOR, mEmailCreator));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_ORDER, mOrder));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_FILENAME, mFileName));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_FILESIZE, mFileSize));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_FILEVERSION, mFileVersion));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_USN_ENTITY, mUsnEntity));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_USN_FIELD_ORDER, mUsnFieldOrder));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_USN_FIELD_NAME, mUsnFieldName));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_USN_FIELD_SIZE, mUsnFieldSize));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskFileContract.FIELD_USN_FIELD_VERSION, mUsnFieldVersion));

        sb.append(BaseSOAP.getClose(TaskFileContract.SERVER_CLASS));
    }

    private static void fillFastTable(Cursor c) {
        if (sTableIndexes == null) {
            sTableIndexes = new int[17];
            sTableIndexes[0] = c.getColumnIndex(TaskFileContract.FIELD_UID);
            sTableIndexes[1] = c.getColumnIndex(TaskFileContract.FIELD_TASKUID);
            sTableIndexes[2] = c.getColumnIndex(TaskFileContract.FIELD_FILEUID);
            sTableIndexes[3] = c.getColumnIndex(TaskFileContract.FIELD_EMAILCREATOR);
            sTableIndexes[4] = c.getColumnIndex(TaskFileContract.ORDERS);
            sTableIndexes[5] = c.getColumnIndex(TaskFileContract.FIELD_FILENAME);
            sTableIndexes[6] = c.getColumnIndex(TaskFileContract.FIELD_FILESIZE);
            sTableIndexes[7] = c.getColumnIndex(TaskFileContract.FIELD_FILEVERSION);
            sTableIndexes[8] = c.getColumnIndex(TaskFileContract.FIELD_USN_ENTITY);
            sTableIndexes[9] = c.getColumnIndex(TaskFileContract.FIELD_USN_FIELD_ORDER);
            sTableIndexes[10] = c.getColumnIndex(TaskFileContract.FIELD_USN_FIELD_NAME);
            sTableIndexes[11] = c.getColumnIndex(TaskFileContract.FIELD_USN_FIELD_SIZE);
            sTableIndexes[12] = c.getColumnIndex(TaskFileContract.FIELD_USN_FIELD_VERSION);
            sTableIndexes[13] = c.getColumnIndex(TaskFileContract.FILE_EXIST);
            sTableIndexes[14] = c.getColumnIndex(TaskFileContract.DELETE_OBJECT);
            sTableIndexes[15] = c.getColumnIndex(TaskFileContract.WEAK_LINK);
            sTableIndexes[16] = c.getColumnIndex(TaskFileContract.SEND_FILE);
        }
    }

    @Override
    public UUID getId() {
        return mId;
    }

    @Override
    public int getIdTask() {
        return 0;
    }

    @Override
    public long getUsn() {
        return mUsnEntity;
    }

    public UUID getTaskId() {
        return mTaskId;
    }

    public void setTaskId(UUID taskId) {
        mTaskId = taskId;
    }

    public UUID getFileId() {
        return mFileId;
    }

    public void setFileId(UUID fileId) {
        mFileId = fileId;
    }

    public String getEmailCreator() {
        return mEmailCreator;
    }

    public void setEmailCreator(String emailCreator) {
        mEmailCreator = emailCreator;
    }

    public long getOrder() {
        return mOrder;
    }

    public void setOrder(long order) {
        mOrder = order;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public void setFileSize(long fileSize) {
        mFileSize = fileSize;
    }

    public int getFileVersion() {
        return mFileVersion;
    }

    public void setFileVersion(int fileVersion) {
        mFileVersion = fileVersion;
    }

    public int getUsnEntity() {
        return mUsnEntity;
    }

    public void setUsnEntity(int usnEntity) {
        mUsnEntity = usnEntity;
    }

    public int getUsnFieldOrder() {
        return mUsnFieldOrder;
    }

    public void setUsnFieldOrder(int usnFieldOrder) {
        mUsnFieldOrder = usnFieldOrder;
    }

    public int getUsnFieldName() {
        return mUsnFieldName;
    }

    public void setUsnFieldName(int usnFieldName) {
        mUsnFieldName = usnFieldName;
    }

    public int getUsnFieldSize() {
        return mUsnFieldSize;
    }

    public void setUsnFieldSize(int usnFieldSize) {
        mUsnFieldSize = usnFieldSize;
    }

    public int getUsnFieldVersion() {
        return mUsnFieldVersion;
    }

    public void setUsnFieldVersion(int usnFieldVersion) {
        mUsnFieldVersion = usnFieldVersion;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public boolean isFileExist() {
        return mFileExist;
    }

    public void setFileExist(boolean fileExist) {
        mFileExist = fileExist;
    }

    public boolean isDeleteObject() {
        return mDeleteObject;
    }

    public void setDeleteObject(boolean deleteObject) {
        mDeleteObject = deleteObject;
    }

    public boolean isWeakLink() {
        return mWeakLink;
    }

    public void setWeakLink(boolean weakLink) {
        mWeakLink = weakLink;
    }

    public boolean isSendFile() {
        return mSendFile;
    }

    public void setSendFile(boolean sendFile) {
        mSendFile = sendFile;
    }

    @Override
    public String getServerClass() {
        return TaskFileContract.SERVER_CLASS;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        if (cv == null) {
            cv = new ContentValues(17);
        }

        cv.put(TaskFileContract.FIELD_UID, String.valueOf(mId));
        cv.put(TaskFileContract.FIELD_TASKUID, String.valueOf(mTaskId));
        cv.put(TaskFileContract.FIELD_FILEUID, String.valueOf(mFileId));
        cv.put(TaskFileContract.FIELD_EMAILCREATOR, mEmailCreator);
        cv.put(TaskFileContract.ORDERS, mOrder);
        cv.put(TaskFileContract.FIELD_FILENAME, mFileName);
        cv.put(TaskFileContract.FIELD_FILESIZE, mFileSize);
        cv.put(TaskFileContract.FIELD_FILEVERSION, mFileVersion);
        cv.put(TaskFileContract.FIELD_USN_ENTITY, mUsnEntity);
        cv.put(TaskFileContract.FIELD_USN_FIELD_ORDER, mUsnFieldOrder);
        cv.put(TaskFileContract.FIELD_USN_FIELD_NAME, mUsnFieldName);
        cv.put(TaskFileContract.FIELD_USN_FIELD_SIZE, mUsnFieldSize);
        cv.put(TaskFileContract.FIELD_USN_FIELD_VERSION, mUsnFieldVersion);
        cv.put(TaskFileContract.FILE_EXIST, mFileExist);
        cv.put(TaskFileContract.DELETE_OBJECT, mDeleteObject);
        cv.put(TaskFileContract.WEAK_LINK, mWeakLink);
        cv.put(TaskFileContract.SEND_FILE, mSendFile);

        return cv;
    }

    @Override
    public int hashCode() {
        if (mHashCode == 0) {
            mHashCode = TaskHelper.getHashFromUid(getTaskId());
        }
        return mHashCode;
    }

    @Override
    public String toString() {
        return getOrder() + "\t" + getFileName();
    }
}