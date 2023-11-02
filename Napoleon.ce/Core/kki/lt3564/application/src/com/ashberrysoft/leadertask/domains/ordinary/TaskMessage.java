package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;

import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Класс, который представляет собой описание сообщения для задачи.
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class TaskMessage //
        implements Serializable, IEntity, BaseLionEntityInterface {

    private static final long serialVersionUID = 1L;

    public static final String FIELD_UID = "UID";
    public static final String FIELD_CREATOR = "EmailCreator";
    public static final String FIELD_MESSAGE = "Msg";
    public static final String FIELD_IS_DELETED = "Deleted";
    public static final String FIELD_TASK_UID = "TaskUID";
    public static final String FIELD_DATE_CREATE = "DateCreate";
    public static final String FIELD_DATE_MODIFY = "DateModify";
    public static final String FIELD_USN = "__usn_entity";
    public static final String FIELD_USN_MESSAGE = "__usn_field_msg";
    public static final String FIELD_USN_IS_DELETED = "__usn_field_deleted";

    public static final String SERVER_CLASS = "LionTaskMsg";
    /**
     * USN – номер изменения элемента (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN)
    private long mUsn;

    /**
     * UID - уникальный идентификатор элемента (текст)
     */
    @DatabaseField(columnName = FIELD_UID, id = true)
    private UUID mId;

    /**
     * Creator – создатель (логин создателя)
     */
    @DatabaseField
    private String mCreator;

    /**
     * Message – текст сообщения (текст)
     */
    @DatabaseField
    private String mMessage;

    /**
     * USN_Message – номер изменения текста (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnMessage;

    /**
     * IsDeleted – сообщение стерто (0 или 1)
     */
    @DatabaseField
    private boolean mIsDeleted;

    /**
     * USN_IsDeleted – номер изменения поля стерто (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnIsDeleted;

    /**
     * TaskUID – идентификатор задачи (текст)
     */
    @DatabaseField(columnName = FIELD_TASK_UID, index = true)
    private UUID mTaskUID;

    /**
     * DateCreate – дата создания сообщения (дата-время UTC)
     */
    @DatabaseField(columnName = FIELD_DATE_CREATE, index = true, dataType = DataType.DATE_LONG)
    private Date mDateCreate;

    /**
     * DateModify – дата изменения сообщения (дата-время UTC)
     */
    @DatabaseField(columnName = FIELD_DATE_MODIFY, index = true, dataType = DataType.DATE_LONG)
    private Date mDateModify;

    private int mHashCode;

    // default constructor
    public TaskMessage() {}

    // parameterized constructor
    public TaskMessage(Map<String, String> map) {
        setUsn(Integer.parseInt(map.get(FIELD_USN)));
        setId(checkSoapUUID(map.get(FIELD_UID)));
        setCreator(checkSoap(map.get(FIELD_CREATOR)));
        setMessage(checkSoap(map.get(FIELD_MESSAGE)));
        setUsnMessage(Integer.parseInt(map.get(FIELD_USN_MESSAGE)));
        setIsDeleted((map.get(FIELD_IS_DELETED)).equals("1"));
        setUsnIsDeleted(Integer.parseInt(map.get(FIELD_USN_IS_DELETED)));
        setTaskUID(checkSoapUUID(map.get(FIELD_TASK_UID)));
        setDateCreate(checkSoapDate(map.get(FIELD_DATE_CREATE)));
        setDateModify(checkSoapDate(map.get(FIELD_DATE_MODIFY)));
    }

    public TaskMessage(UUID mId, String mCreator, String mMessage, UUID mTaskUID, Date mDateCreate, Date mDateModify,
            boolean mIsDeleted, long mUsn, int mUsnMessage, int mUsnIsDeleted) {
        setId(mId);
        setCreator(mCreator);
        setMessage(mMessage);
        setTaskUID(mTaskUID);
        setDateCreate(mDateCreate);
        setDateModify(mDateModify);
        setIsDeleted(mIsDeleted);
        setUsn(mUsn);
        setUsnMessage(mUsnMessage);
        setUsnIsDeleted(mUsnIsDeleted);
    }

    /*
     * setterts for class fields
     */
    public void setUsn(long mUsn) {
        this.mUsn = mUsn;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public void setCreator(String mCreator) {
        this.mCreator = mCreator;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setUsnMessage(int mUsnMessage) {
        this.mUsnMessage = mUsnMessage;
    }

    public void setIsDeleted(boolean mIsDeleted) {
        this.mIsDeleted = mIsDeleted;
    }

    public void setUsnIsDeleted(int mUsnIsDeleted) {
        this.mUsnIsDeleted = mUsnIsDeleted;
    }

    public void setTaskUID(UUID mTaskUID) {
        this.mTaskUID = mTaskUID;
    }

    public void setDateCreate(Date mDateCreate) {
        this.mDateCreate = mDateCreate;
    }

    public void setDateModify(Date mDateModify) {
        this.mDateModify = mDateModify;
    }

    /*
     * getters for class fields
     */
    public long getUsn() {
        return mUsn;
    }

    public UUID getId() {
        return mId;
    }

    @Override
    public int getIdTask() {
        return 0;
    }

    public String getCreator() {
        return mCreator;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getUsnMessage() {
        return mUsnMessage;
    }

    public boolean isDeleted() {
        return mIsDeleted;
    }

    public int getUsnIsDeleted() {
        return mUsnIsDeleted;
    }

    public UUID getTaskUID() {
        return mTaskUID;
    }

    public Date getDateCreate() {
        return mDateCreate;
    }

    public Date getDateModify() {
        return mDateModify;
    }

    /*
     * check content of the soap object
     */
    private String checkSoap(String str) {
        if (str.equals("anyType{}")) {
            return null;
        }
        return str;
    }

    /*
     * check UUID of the soap object
     */
    private UUID checkSoapUUID(String str) {
        if (str.equals("anyType{}")) {
            return null;
        }
        return UUID.fromString(str);
    }

    /*
     * check Date of the soap object
     */
    private Date checkSoapDate(String str) {
        if (str.equals("anyType{}")) {
            return null;
        }
        try {
            return Task.SDF.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (FIELD_UID.equalsIgnoreCase(key)) {
            mId = UUID.fromString(value);
        }

        else if (FIELD_CREATOR.equalsIgnoreCase(key)) {
            mCreator = value;
        }

        else if (FIELD_MESSAGE.equalsIgnoreCase(key)) {
            mMessage = value;
        }

        else if (FIELD_IS_DELETED.equalsIgnoreCase(key)) {
            mIsDeleted = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_TASK_UID.equalsIgnoreCase(key)) {
            mTaskUID = UUID.fromString(value);
        }

        else if (FIELD_DATE_CREATE.equalsIgnoreCase(key)) {
            mDateCreate = BaseSOAP.parseDate(value);
        }

        else if (FIELD_DATE_MODIFY.equalsIgnoreCase(key)) {
            mDateModify = BaseSOAP.parseDate(value);
        }

        else if (FIELD_USN.equalsIgnoreCase(key)) {
            mUsn = Long.parseLong(value);
        }

        else if (FIELD_USN_MESSAGE.equalsIgnoreCase(key)) {
            mUsnMessage = Integer.parseInt(value);
        }

        else if (FIELD_USN_IS_DELETED.equalsIgnoreCase(key)) {
            mUsnIsDeleted = Integer.parseInt(value);
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(SERVER_CLASS));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_UID, getId()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_CREATOR, getCreator()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_MESSAGE, getMessage()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_IS_DELETED, isDeleted()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_TASK_UID, getTaskUID()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_DATE_CREATE, getDateCreate()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_DATE_MODIFY, getDateModify()));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_MESSAGE, getUsnMessage()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_IS_DELETED, getUsnIsDeleted()));

        sb.append(BaseSOAP.getClose(SERVER_CLASS));
    }

    @Override
    public String getServerClass() {
        return SERVER_CLASS;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        return null;
    }

    @Override
    public int hashCode() {
        if (mHashCode == 0) {
            mHashCode = TaskHelper.getHashFromUid(getTaskUID());
        }
        return mHashCode;
    }
}