package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultSortOrder;

@DatabaseTable(tableName = EmpContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = EmpContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = EmpContract.TABLE_NAME)
public class Emp implements Serializable, IEntity, BaseLionEntityInterface {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_UUID_EMP_S = "62153549-9817-431b-b2ee-981338ad626d";
    public static final UUID DEFAULT_UUID_EMP = UUID.fromString(DEFAULT_UUID_EMP_S);
    public static final String DEFAULT_STRING_EMP = "current_user";

    public static final int MAXIMAL_EMP_ORDERS = 1000000;

    @DatabaseField(columnName = EmpContract._ID, generatedId = true)
    private int mEmptyId;

    @DatabaseField(columnName = EmpContract.UID, canBeNull = false, index = true)
    private UUID mUid;

    @DatabaseField(columnName = EmpContract.LOGIN)
    private String mLogin;

    @DefaultSortOrder
    @DatabaseField(columnName = EmpContract.ORDERS)
    private int mOrder;

    @DatabaseField(columnName = EmpContract.FIRST_NAME)
    private String mFirstName;

    @DatabaseField(columnName = EmpContract.MIDDLE_NAME)
    private String mMiddleName;

    @DatabaseField(columnName = EmpContract.LAST_NAME)
    private String mLastName;

    @DatabaseField(columnName = EmpContract.DETAILS)
    private String mDetails;

    @DatabaseField(columnName = EmpContract.COUNTRY)
    private String mCountry;

    @DatabaseField(columnName = EmpContract.PROVINCE)
    private String mProvince;

    @DatabaseField(columnName = EmpContract.POSTAL_CODE)
    private String mPostalCode;

    @DatabaseField(columnName = EmpContract.CITY)
    private String mCity;

    @DatabaseField(columnName = EmpContract.STREET)
    private String mStreet;

    @DatabaseField(columnName = EmpContract.COMMUNICATION)
    private String mCommunication;

    @DatabaseField(columnName = EmpContract.GENDER)
    private int mGender;

    @DatabaseField(columnName = EmpContract.GROUP_UID)
    private UUID mGroupUid;

    @DatabaseField(columnName = EmpContract.BIRTHDAY, dataType = DataType.DATE_LONG)
    private Date mBirthday;

    @DatabaseField(columnName = EmpContract.TITLE)
    private String mTitle;

    @DatabaseField(columnName = EmpContract.COMMENT)
    private String mComment;

    @DatabaseField(columnName = EmpContract.NOTIFY_BIRTHDAY)
    private boolean mNotifyBirthday;

    @DatabaseField(columnName = EmpContract.FAVORITE)
    private boolean mFavorite;

    @DatabaseField(columnName = EmpContract.SHOW_IN_NAVIGATOR)
    private boolean mShowInNavigator;

    @DatabaseField(columnName = EmpContract.USN_ENTITY)
    private long mUsnEntity;

    @DatabaseField(columnName = EmpContract.USN_FIELD_FIRSTNAME)
    private long mUsnFieldFirstName;

    @DatabaseField(columnName = EmpContract.USN_FIELD_LASTNAME)
    private long mUsnFieldLastName;

    @DatabaseField(columnName = EmpContract.USN_FIELD_MIDDLENAME)
    private long mUsnFieldMiddleName;

    @DatabaseField(columnName = EmpContract.USN_FIELD_DETAILS)
    private long mUsnFieldDetails;

    @DatabaseField(columnName = EmpContract.USN_FIELD_GENDER)
    private long mUsnFieldGender;

    @DatabaseField(columnName = EmpContract.USN_FIELD_COUNTRY)
    private long mUsnFieldCountry;

    @DatabaseField(columnName = EmpContract.USN_FIELD_PROVINCE)
    private long mUsnFieldProvince;

    @DatabaseField(columnName = EmpContract.USN_FIELD_POSTALCODE)
    private long mUsnFieldPostalCode;

    @DatabaseField(columnName = EmpContract.USN_FIELD_CITY)
    private long mUsnFieldCity;

    @DatabaseField(columnName = EmpContract.USN_FIELD_STREET)
    private long mUsnFieldStreet;

    @DatabaseField(columnName = EmpContract.USN_FIELD_UID_GROUP)
    private long mUsnFieldUidGroup;

    @DatabaseField(columnName = EmpContract.USN_FIELD_BIRTHDAY)
    private long mUsnFieldBirthday;

    @DatabaseField(columnName = EmpContract.USN_FIELD_COMMUNICATION)
    private long mUsnFieldCommunication;

    @DatabaseField(columnName = EmpContract.USN_FIELD_ORDER)
    private long mUsnFieldOrder;

    @DatabaseField(columnName = EmpContract.USN_FIELD_TITLE)
    private long mUsnFieldTitle;

    @DatabaseField(columnName = EmpContract.USN_FIELD_COMMENT)
    private long mUsnFieldComment;

    @DatabaseField(columnName = EmpContract.USN_FIELD_NOTIFYBIRTHDAY)
    private long mUsnFieldNotifyBirthday;

    @DatabaseField(columnName = EmpContract.USN_FIELD_FAVORITE)
    private long mUsnFieldFavorite;

    @DatabaseField(columnName = EmpContract.USN_FIELD_SHOWINNAVIGATOR)
    private long mUsnFieldShowInNavigator;

    @DatabaseField(columnName = EmpContract.SEND_ENTITY)
    private boolean mSendEntity;

    @DatabaseField(columnName = EmpContract.USN_FIELD_FOTO)
    private long mUsnFieldFoto;

    @DatabaseField(columnName = EmpContract.PHONE)
    private String mPhone;

    @DatabaseField(columnName = EmpContract.USN_FIELD_PHONE)
    private long mUsnFieldPhone;

    private static int[] sColums;

    public Emp() {}

    public Emp(Cursor c) {
        setData(c);
    }

    public void setData(Cursor c) {
        fillFastTable(c);

        setUid(UUID.fromString(c.getString(sColums[0])));
        setLogin(c.getString(sColums[1]));
        setOrder(c.getInt(sColums[2]));
        setFirstName(c.getString(sColums[3]));
        setMiddleName(c.getString(sColums[4]));
        setLastName(c.getString(sColums[5]));
        setDetails(c.getString(sColums[6]));
        setCountry(c.getString(sColums[7]));
        setProvince(c.getString(sColums[8]));
        setPostalCode(c.getString(sColums[9]));
        setCity(c.getString(sColums[10]));
        setStreet(c.getString(sColums[11]));
        setCommunication(c.getString(sColums[12]));
        setGender(c.getInt(sColums[13]));
        setGroupUid(Task.getUUIDFromString(c.getString(sColums[14])));
        setBirthday(Task.getDateFromLong(c.getLong(sColums[15])));
        setTitle(c.getString(sColums[16]));
        setComment(c.getString(sColums[17]));
        setNotifyBirthday(BaseSOAP.equalsOne(c.getInt(sColums[18])));
        setFavorite(BaseSOAP.equalsOne(c.getInt(sColums[19])));
        setShowInNavigator(BaseSOAP.equalsOne(c.getInt(sColums[20])));

        setUsnEntity(c.getLong(sColums[21]));
        setUsnFieldFirstName(c.getLong(sColums[22]));
        setUsnFieldLastName(c.getLong(sColums[23]));
        setUsnFieldMiddleName(c.getLong(sColums[24]));
        setUsnFieldDetails(c.getLong(sColums[25]));
        setUsnFieldGender(c.getLong(sColums[26]));
        setUsnFieldCountry(c.getLong(sColums[27]));
        setUsnFieldProvince(c.getLong(sColums[28]));
        setUsnFieldPostalCode(c.getLong(sColums[29]));
        setUsnFieldCity(c.getLong(sColums[30]));
        setUsnFieldStreet(c.getLong(sColums[31]));
        setUsnFieldUidGroup(c.getLong(sColums[32]));
        setUsnFieldBirthday(c.getLong(sColums[33]));
        setUsnFieldCommunication(c.getLong(sColums[34]));
        setUsnFieldOrder(c.getLong(sColums[35]));
        setUsnFieldTitle(c.getLong(sColums[36]));
        setUsnFieldComment(c.getLong(sColums[37]));
        setUsnFieldNotifyBirthday(c.getLong(sColums[38]));
        setUsnFieldFavorite(c.getLong(sColums[39]));
        setUsnFieldShowInNavigator(c.getLong(sColums[40]));
        setUsnFieldFoto(c.getLong(sColums[41]));
        setPhone(c.getString(sColums[42]));
        setUsnFieldPhone(c.getLong(sColums[43]));
    }

    private static void fillFastTable(Cursor c) {
        if (sColums == null) {
            sColums = new int[44];

            sColums[0] = c.getColumnIndex(EmpContract.UID);
            sColums[1] = c.getColumnIndex(EmpContract.LOGIN);
            sColums[2] = c.getColumnIndex(EmpContract.ORDERS);
            sColums[3] = c.getColumnIndex(EmpContract.FIRST_NAME);
            sColums[4] = c.getColumnIndex(EmpContract.MIDDLE_NAME);
            sColums[5] = c.getColumnIndex(EmpContract.LAST_NAME);
            sColums[6] = c.getColumnIndex(EmpContract.DETAILS);
            sColums[7] = c.getColumnIndex(EmpContract.COUNTRY);
            sColums[8] = c.getColumnIndex(EmpContract.PROVINCE);
            sColums[9] = c.getColumnIndex(EmpContract.POSTAL_CODE);
            sColums[10] = c.getColumnIndex(EmpContract.CITY);
            sColums[11] = c.getColumnIndex(EmpContract.STREET);
            sColums[12] = c.getColumnIndex(EmpContract.COMMUNICATION);
            sColums[13] = c.getColumnIndex(EmpContract.GENDER);
            sColums[14] = c.getColumnIndex(EmpContract.GROUP_UID);
            sColums[15] = c.getColumnIndex(EmpContract.BIRTHDAY);
            sColums[16] = c.getColumnIndex(EmpContract.TITLE);
            sColums[17] = c.getColumnIndex(EmpContract.COMMENT);
            sColums[18] = c.getColumnIndex(EmpContract.NOTIFY_BIRTHDAY);
            sColums[19] = c.getColumnIndex(EmpContract.FAVORITE);
            sColums[20] = c.getColumnIndex(EmpContract.SHOW_IN_NAVIGATOR);

            sColums[21] = c.getColumnIndex(EmpContract.USN_ENTITY);
            sColums[22] = c.getColumnIndex(EmpContract.USN_FIELD_FIRSTNAME);
            sColums[23] = c.getColumnIndex(EmpContract.USN_FIELD_LASTNAME);
            sColums[24] = c.getColumnIndex(EmpContract.USN_FIELD_MIDDLENAME);
            sColums[25] = c.getColumnIndex(EmpContract.USN_FIELD_DETAILS);
            sColums[26] = c.getColumnIndex(EmpContract.USN_FIELD_GENDER);
            sColums[27] = c.getColumnIndex(EmpContract.USN_FIELD_COUNTRY);
            sColums[28] = c.getColumnIndex(EmpContract.USN_FIELD_PROVINCE);
            sColums[29] = c.getColumnIndex(EmpContract.USN_FIELD_POSTALCODE);
            sColums[30] = c.getColumnIndex(EmpContract.USN_FIELD_CITY);
            sColums[31] = c.getColumnIndex(EmpContract.USN_FIELD_STREET);
            sColums[32] = c.getColumnIndex(EmpContract.USN_FIELD_UID_GROUP);
            sColums[33] = c.getColumnIndex(EmpContract.USN_FIELD_BIRTHDAY);
            sColums[34] = c.getColumnIndex(EmpContract.USN_FIELD_COMMUNICATION);
            sColums[35] = c.getColumnIndex(EmpContract.USN_FIELD_ORDER);
            sColums[36] = c.getColumnIndex(EmpContract.USN_FIELD_TITLE);
            sColums[37] = c.getColumnIndex(EmpContract.USN_FIELD_COMMENT);
            sColums[38] = c.getColumnIndex(EmpContract.USN_FIELD_NOTIFYBIRTHDAY);
            sColums[39] = c.getColumnIndex(EmpContract.USN_FIELD_FAVORITE);
            sColums[40] = c.getColumnIndex(EmpContract.USN_FIELD_SHOWINNAVIGATOR);
            sColums[41] = c.getColumnIndex(EmpContract.USN_FIELD_FOTO);
            sColums[42] = c.getColumnIndex(EmpContract.PHONE);
            sColums[43] = c.getColumnIndex(EmpContract.USN_FIELD_PHONE);
        }
    }

    @Override
    public int hashCode() {
        return mUid.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof Emp) {
            return getId().equals(((Emp) o).getId());
        }

        return false;
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (EmpContract.UID.equalsIgnoreCase(key)) {
            if (value.equalsIgnoreCase(DEFAULT_STRING_EMP)) {
                setUid(DEFAULT_UUID_EMP);
            } else {
                setUid(Task.getUUIDFromString(value));
            }
            return;
        }

        if (EmpContract.LOGIN.equalsIgnoreCase(key)) {
            setLogin(value);
            return;
        }

        if (EmpContract.ORDER.equalsIgnoreCase(key)) {
            setOrder(Integer.parseInt(value));
            return;
        }

        if (EmpContract.FIRST_NAME.equalsIgnoreCase(key)) {
            setFirstName(value);
            return;
        }

        if (EmpContract.MIDDLE_NAME.equalsIgnoreCase(key)) {
            setMiddleName(value);
            return;
        }

        if (EmpContract.LAST_NAME.equalsIgnoreCase(key)) {
            setLastName(value);
            return;
        }

        if (EmpContract.DETAILS.equalsIgnoreCase(key)) {
            setDetails(value);
            return;
        }

        if (EmpContract.COUNTRY.equalsIgnoreCase(key)) {
            setCountry(value);
            return;
        }

        if (EmpContract.PROVINCE.equalsIgnoreCase(key)) {
            setProvince(value);
            return;
        }

        if (EmpContract.POSTAL_CODE.equalsIgnoreCase(key)) {
            setPostalCode(value);
            return;
        }

        if (EmpContract.CITY.equalsIgnoreCase(key)) {
            setCity(value);
            return;
        }

        if (EmpContract.STREET.equalsIgnoreCase(key)) {
            setStreet(value);
            return;
        }

        if (EmpContract.COMMUNICATION.equalsIgnoreCase(key)) {
            setCommunication(value);
            return;
        }

        if (EmpContract.GENDER.equalsIgnoreCase(key)) {
            setGender(Integer.parseInt(value));
            return;
        }

        if (EmpContract.GROUP_UID.equalsIgnoreCase(key)) {
            setGroupUid(Task.getUUIDFromString(value));
            return;
        }

        if (EmpContract.BIRTHDAY.equalsIgnoreCase(key)) {
            setBirthday(BaseSOAP.parseDate(value));
            return;
        }

        if (EmpContract.TITLE.equalsIgnoreCase(key)) {
            setTitle(value);
            return;
        }

        if (EmpContract.COMMENT.equalsIgnoreCase(key)) {
            setComment(value);
            return;
        }

        if (EmpContract.NOTIFY_BIRTHDAY.equalsIgnoreCase(key)) {
            setNotifyBirthday(BaseSOAP.equalsOne(value));
            return;
        }

        if (EmpContract.FAVORITE.equalsIgnoreCase(key)) {
            setFavorite(BaseSOAP.equalsOne(value));
            return;
        }

        if (EmpContract.SHOW_IN_NAVIGATOR.equalsIgnoreCase(key)) {
            setShowInNavigator(BaseSOAP.equalsOne(value));
            return;
        }

        if (EmpContract.USN_ENTITY.equalsIgnoreCase(key)) {
            setUsnEntity(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_FIRSTNAME.equalsIgnoreCase(key)) {
            setUsnFieldFirstName(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_LASTNAME.equalsIgnoreCase(key)) {
            setUsnFieldLastName(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_MIDDLENAME.equalsIgnoreCase(key)) {
            setUsnFieldMiddleName(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_DETAILS.equalsIgnoreCase(key)) {
            setUsnFieldDetails(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_GENDER.equalsIgnoreCase(key)) {
            setUsnFieldGender(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_COUNTRY.equalsIgnoreCase(key)) {
            setUsnFieldCountry(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_PROVINCE.equalsIgnoreCase(key)) {
            setUsnFieldProvince(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_POSTALCODE.equalsIgnoreCase(key)) {
            setUsnFieldPostalCode(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_CITY.equalsIgnoreCase(key)) {
            setUsnFieldCity(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_STREET.equalsIgnoreCase(key)) {
            setUsnFieldStreet(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_UID_GROUP.equalsIgnoreCase(key)) {
            setUsnFieldUidGroup(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_BIRTHDAY.equalsIgnoreCase(key)) {
            setUsnFieldBirthday(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_COMMUNICATION.equalsIgnoreCase(key)) {
            setUsnFieldCommunication(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_ORDER.equalsIgnoreCase(key)) {
            setUsnFieldOrder(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_TITLE.equalsIgnoreCase(key)) {
            setUsnFieldTitle(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_COMMENT.equalsIgnoreCase(key)) {
            setUsnFieldComment(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_NOTIFYBIRTHDAY.equalsIgnoreCase(key)) {
            setUsnFieldNotifyBirthday(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_FAVORITE.equalsIgnoreCase(key)) {
            setUsnFieldFavorite(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_SHOWINNAVIGATOR.equalsIgnoreCase(key)) {
            setUsnFieldShowInNavigator(Long.parseLong(value));
            return;
        }

        if (EmpContract.USN_FIELD_FOTO.equalsIgnoreCase(key)) {
            setUsnFieldFoto(Long.parseLong(value));
            return;
        }

        if (EmpContract.PHONE.equalsIgnoreCase(key)) {
            setPhone(value);
            return;
        }

        if (EmpContract.USN_FIELD_PHONE.equalsIgnoreCase(key)) {
            setUsnFieldPhone(Long.parseLong(value));
            return;
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(EmpContract.SERVER_CLASS));

        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.UID,//
                getUid().equals(DEFAULT_UUID_EMP) ? DEFAULT_STRING_EMP : String.valueOf(getUid())));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.LOGIN, getLogin()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.ORDER, getOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.FIRST_NAME, getFirstName()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.MIDDLE_NAME, getMiddleName()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.LAST_NAME, getLastName()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.DETAILS, getDetails()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.COUNTRY, getCountry()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.PROVINCE, getProvince()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.POSTAL_CODE, getPostalCode()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.CITY, getCity()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.STREET, getStreet()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.COMMUNICATION, getCommunication()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.GENDER, getGender()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.GROUP_UID, getGroupUid()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.BIRTHDAY, getBirthday()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.TITLE, getTitle()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.COMMENT, getComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.NOTIFY_BIRTHDAY, isNotifyBirthday()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.FAVORITE, isFavorite()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.SHOW_IN_NAVIGATOR, isShowInNavigator()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.PHONE, getPhone()));

        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_ENTITY, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_FIRSTNAME, getUsnFieldFirstName()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_LASTNAME, getUsnFieldLastName()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_MIDDLENAME, getUsnFieldMiddleName()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_DETAILS, getUsnFieldDetails()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_GENDER, getUsnFieldGender()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_COUNTRY, getUsnFieldCountry()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_PROVINCE, getUsnFieldProvince()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_POSTALCODE, getUsnFieldPostalCode()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_CITY, getUsnFieldCity()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_STREET, getUsnFieldStreet()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_UID_GROUP, getUsnFieldUidGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_BIRTHDAY, getUsnFieldBirthday()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_COMMUNICATION, getUsnFieldCommunication()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_ORDER, getUsnFieldOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_TITLE, getUsnFieldTitle()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_COMMENT, getUsnFieldComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_NOTIFYBIRTHDAY, getUsnFieldNotifyBirthday()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_FAVORITE, getUsnFieldFavorite()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_SHOWINNAVIGATOR, getUsnFieldShowInNavigator()));
        sb.append(BaseSOAP.getXmlnsValueLine(EmpContract.USN_FIELD_PHONE, getUsnFieldPhone()));
        sb.append(BaseSOAP.getClose(EmpContract.SERVER_CLASS));
    }

    @Override
    public String getServerClass() {
        return EmpContract.SERVER_CLASS;
    }

    public ContentValues getContentValues(ContentValues cv) {
        if (cv == null) {
            cv = new ContentValues();
        } else {
            cv.clear();
        }

        cv.put(EmpContract.UID, String.valueOf(getUid()));
        cv.put(EmpContract.LOGIN, getLogin());
        cv.put(EmpContract.ORDERS, getOrder());
        cv.put(EmpContract.FIRST_NAME, getFirstName());
        cv.put(EmpContract.MIDDLE_NAME, getMiddleName());
        cv.put(EmpContract.LAST_NAME, getLastName());
        cv.put(EmpContract.DETAILS, getDetails());
        cv.put(EmpContract.COUNTRY, getCountry());
        cv.put(EmpContract.PROVINCE, getProvince());
        cv.put(EmpContract.POSTAL_CODE, getPostalCode());
        cv.put(EmpContract.CITY, getCity());
        cv.put(EmpContract.STREET, getStreet());
        cv.put(EmpContract.COMMUNICATION, getCommunication());
        cv.put(EmpContract.GENDER, getGender());
        cv.put(EmpContract.GROUP_UID, getGroupUid() == null ? null : String.valueOf(getGroupUid()));
        cv.put(EmpContract.BIRTHDAY, getBirthday() == null ? null : getBirthday().getTime());
        cv.put(EmpContract.TITLE, getTitle());
        cv.put(EmpContract.COMMENT, getComment());
        cv.put(EmpContract.NOTIFY_BIRTHDAY, isNotifyBirthday());
        cv.put(EmpContract.FAVORITE, isFavorite());
        cv.put(EmpContract.SHOW_IN_NAVIGATOR, isShowInNavigator());
        cv.put(EmpContract.PHONE, getPhone());

        cv.put(EmpContract.USN_ENTITY, getUsn());
        cv.put(EmpContract.USN_FIELD_FIRSTNAME, getUsnFieldFirstName());
        cv.put(EmpContract.USN_FIELD_LASTNAME, getUsnFieldLastName());
        cv.put(EmpContract.USN_FIELD_MIDDLENAME, getUsnFieldMiddleName());
        cv.put(EmpContract.USN_FIELD_DETAILS, getUsnFieldDetails());
        cv.put(EmpContract.USN_FIELD_GENDER, getUsnFieldGender());
        cv.put(EmpContract.USN_FIELD_COUNTRY, getUsnFieldCountry());
        cv.put(EmpContract.USN_FIELD_PROVINCE, getUsnFieldProvince());
        cv.put(EmpContract.USN_FIELD_POSTALCODE, getUsnFieldPostalCode());
        cv.put(EmpContract.USN_FIELD_CITY, getUsnFieldCity());
        cv.put(EmpContract.USN_FIELD_STREET, getUsnFieldStreet());
        cv.put(EmpContract.USN_FIELD_UID_GROUP, getUsnFieldUidGroup());
        cv.put(EmpContract.USN_FIELD_BIRTHDAY, getUsnFieldBirthday());
        cv.put(EmpContract.USN_FIELD_COMMUNICATION, getUsnFieldCommunication());
        cv.put(EmpContract.USN_FIELD_ORDER, getUsnFieldOrder());
        cv.put(EmpContract.USN_FIELD_TITLE, getUsnFieldTitle());
        cv.put(EmpContract.USN_FIELD_COMMENT, getUsnFieldComment());
        cv.put(EmpContract.USN_FIELD_NOTIFYBIRTHDAY, getUsnFieldNotifyBirthday());
        cv.put(EmpContract.USN_FIELD_FAVORITE, getUsnFieldFavorite());
        cv.put(EmpContract.USN_FIELD_SHOWINNAVIGATOR, getUsnFieldShowInNavigator());
        cv.put(EmpContract.USN_FIELD_FOTO, getUsnFieldFoto());
        cv.put(EmpContract.USN_FIELD_PHONE, getUsnFieldPhone());
        return cv;
    }

    @Override
    public UUID getId() {
        return mUid;
    }

    @Override
    public int getIdTask() {
        return 0;
    }

    @Override
    public long getUsn() {
        return mUsnEntity;
    }

    public UUID getUid() {
        return mUid;
    }

    public void setUid(UUID uid) {
        mUid = uid;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public void setMiddleName(String middleName) {
        mMiddleName = middleName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    public void setPostalCode(String postalCode) {
        mPostalCode = postalCode;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getCommunication() {
        return mCommunication;
    }

    public void setCommunication(String communication) {
        mCommunication = communication;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public UUID getGroupUid() {
        return mGroupUid;
    }

    public void setGroupUid(UUID groupUid) {
        mGroupUid = groupUid;
    }

    public Date getBirthday() {
        return mBirthday;
    }

    public void setBirthday(Date birthday) {
        mBirthday = birthday;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public boolean isNotifyBirthday() {
        return mNotifyBirthday;
    }

    public void setNotifyBirthday(boolean notifyBirthday) {
        mNotifyBirthday = notifyBirthday;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public boolean isShowInNavigator() {
        return mShowInNavigator;
    }

    public void setShowInNavigator(boolean showInNavigator) {
        mShowInNavigator = showInNavigator;
    }

    public long getUsnEntity() {
        return mUsnEntity;
    }

    public void setUsnEntity(long usnEntity) {
        mUsnEntity = usnEntity;
    }

    public long getUsnFieldFirstName() {
        return mUsnFieldFirstName;
    }

    public void setUsnFieldFirstName(long usnFieldFirstName) {
        mUsnFieldFirstName = usnFieldFirstName;
    }

    public long getUsnFieldLastName() {
        return mUsnFieldLastName;
    }

    public void setUsnFieldLastName(long usnFieldLastName) {
        mUsnFieldLastName = usnFieldLastName;
    }

    public long getUsnFieldMiddleName() {
        return mUsnFieldMiddleName;
    }

    public void setUsnFieldMiddleName(long usnFieldMiddleName) {
        mUsnFieldMiddleName = usnFieldMiddleName;
    }

    public long getUsnFieldDetails() {
        return mUsnFieldDetails;
    }

    public void setUsnFieldDetails(long usnFieldDetails) {
        mUsnFieldDetails = usnFieldDetails;
    }

    public long getUsnFieldGender() {
        return mUsnFieldGender;
    }

    public void setUsnFieldGender(long usnFieldGender) {
        mUsnFieldGender = usnFieldGender;
    }

    public long getUsnFieldCountry() {
        return mUsnFieldCountry;
    }

    public void setUsnFieldCountry(long usnFieldCountry) {
        mUsnFieldCountry = usnFieldCountry;
    }

    public long getUsnFieldProvince() {
        return mUsnFieldProvince;
    }

    public void setUsnFieldProvince(long usnFieldProvince) {
        mUsnFieldProvince = usnFieldProvince;
    }

    public long getUsnFieldPostalCode() {
        return mUsnFieldPostalCode;
    }

    public void setUsnFieldPostalCode(long usnFieldPostalCode) {
        mUsnFieldPostalCode = usnFieldPostalCode;
    }

    public long getUsnFieldCity() {
        return mUsnFieldCity;
    }

    public void setUsnFieldCity(long usnFieldCity) {
        mUsnFieldCity = usnFieldCity;
    }

    public long getUsnFieldStreet() {
        return mUsnFieldStreet;
    }

    public void setUsnFieldStreet(long usnFieldStreet) {
        mUsnFieldStreet = usnFieldStreet;
    }

    public long getUsnFieldUidGroup() {
        return mUsnFieldUidGroup;
    }

    public void setUsnFieldUidGroup(long usnFieldUidGroup) {
        mUsnFieldUidGroup = usnFieldUidGroup;
    }

    public long getUsnFieldBirthday() {
        return mUsnFieldBirthday;
    }

    public void setUsnFieldBirthday(long usnFieldBirthday) {
        mUsnFieldBirthday = usnFieldBirthday;
    }

    public long getUsnFieldCommunication() {
        return mUsnFieldCommunication;
    }

    public void setUsnFieldCommunication(long usnFieldCommunication) {
        mUsnFieldCommunication = usnFieldCommunication;
    }

    public long getUsnFieldOrder() {
        return mUsnFieldOrder;
    }

    public void setUsnFieldOrder(long usnFieldOrder) {
        mUsnFieldOrder = usnFieldOrder;
    }

    public long getUsnFieldTitle() {
        return mUsnFieldTitle;
    }

    public void setUsnFieldTitle(long usnFieldTitle) {
        mUsnFieldTitle = usnFieldTitle;
    }

    public long getUsnFieldComment() {
        return mUsnFieldComment;
    }

    public void setUsnFieldComment(long usnFieldComment) {
        mUsnFieldComment = usnFieldComment;
    }

    public long getUsnFieldNotifyBirthday() {
        return mUsnFieldNotifyBirthday;
    }

    public void setUsnFieldNotifyBirthday(long usnFieldNotifyBirthday) {
        mUsnFieldNotifyBirthday = usnFieldNotifyBirthday;
    }

    public long getUsnFieldFavorite() {
        return mUsnFieldFavorite;
    }

    public void setUsnFieldFavorite(long usnFieldFavorite) {
        mUsnFieldFavorite = usnFieldFavorite;
    }

    public long getUsnFieldShowInNavigator() {
        return mUsnFieldShowInNavigator;
    }

    public void setUsnFieldShowInNavigator(long usnFieldShowInNavigator) {
        mUsnFieldShowInNavigator = usnFieldShowInNavigator;
    }

    public long getUsnFieldFoto() {
        return mUsnFieldFoto;
    }

    public void setUsnFieldFoto(long usnFieldFoto) {
        mUsnFieldFoto = usnFieldFoto;
    }

    public void setUsnFieldPhone(long usnField) {
        mUsnFieldPhone = usnField;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public long getUsnFieldPhone() {
        return mUsnFieldPhone;
    }

    public String getPhone() {
        return mPhone;
    }

    public boolean isSendEntity() {
        return mSendEntity;
    }

    public void setSendEntity(boolean sendEntity) {
        mSendEntity = sendEntity;
    }

    private static void updateTaskUserOrder(StringBuilder sb, String login, int userOrder, ArrayList<ContentProviderOperation> oprtns) {
        final ContentValues cvTask = new ContentValues();
        Utils.clearStringBuilder(sb);
        final String selectionTask = SelectionKeeper.equals(sb, LionMetaData.LTaskContract.EmailCustomer, login);
        if(login.equals(LTSettings.getInstance().getUserName())) {
            userOrder = LTask.MY_TASK_USER_ORDER;
        }
        cvTask.put(LionMetaData.LTaskContract.UserOrder, userOrder);
        oprtns.add(ContentProviderOperation.newUpdate(LionMetaData.LTaskContract.CONTENT_URI)//
                .withSelection(selectionTask, null)//
                .withValues(cvTask)//
                .build());
    }

    public static void updateTaskUserOrderAfterDelete(Context context, Iterable<String> uids) {
        StringBuilder sb = new StringBuilder();
        String selection = EmpContract.selectionEmpEmailsFromUids(sb, uids);
        Utils.clearStringBuilder(sb);
        final String selectionTask = SelectionKeeper.in(sb, LionMetaData.LTaskContract.EmailCustomer, selection).toString();
        final ContentValues cvTask = new ContentValues();
        cvTask.put(LionMetaData.LTaskContract.UserOrder, LTask.EMAIL_TASK_USER_ORDER);
        context.getContentResolver().update(LionMetaData.LTaskContract.CONTENT_URI, cvTask, selectionTask, null);

    }

    public static boolean addOrUpdateEntity(Context context, List<Emp> entities)//
            throws RemoteException, OperationApplicationException {
        if (entities.isEmpty()) {
            return false;
        }

        final ContentResolver cr = context.getContentResolver();

        final ArrayList<ContentProviderOperation> oprtns = new ArrayList<ContentProviderOperation>(entities.size());
        final StringBuilder sb = new StringBuilder();
        final Emp entityOld = new Emp();

        for (Emp entityNew : entities) {
            Utils.clearStringBuilder(sb);
            if (entityNew.getLogin() != null) {
                if (entityNew.getLogin().equals(LTSettings.getInstance().getUserName())) {
                    entityNew.setUid(Emp.DEFAULT_UUID_EMP);
                }
            final String selection = SelectionKeeper.equals(sb, EmpContract.UID, entityNew.getId());
            final Cursor c = cr.query(EmpContract.CONTENT_URI, null, selection, null, null);

            if (c.getCount() == 1 && c.moveToFirst()) {
                final ContentValues cv = new ContentValues();
                entityOld.setData(c);

                if (entityOld.getUsnEntity() != entityNew.getUsnEntity()) {
                    cv.put(EmpContract.UID, String.valueOf(entityNew.getUid()));
                    cv.put(EmpContract.LOGIN, entityNew.getLogin());
                    cv.put(EmpContract.USN_ENTITY, entityNew.getUsnEntity());
                }

                if (entityOld.getUsnFieldFirstName() <= entityNew.getUsnFieldFirstName()) {
                    cv.put(EmpContract.FIRST_NAME, entityNew.getFirstName());
                    cv.put(EmpContract.USN_FIELD_FIRSTNAME, entityNew.getUsnFieldFirstName());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldLastName() <= entityNew.getUsnFieldLastName()) {
                    cv.put(EmpContract.LAST_NAME, entityNew.getLastName());
                    cv.put(EmpContract.USN_FIELD_LASTNAME, entityNew.getUsnFieldLastName());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldMiddleName() <= entityNew.getUsnFieldMiddleName()) {
                    cv.put(EmpContract.MIDDLE_NAME, entityNew.getMiddleName());
                    cv.put(EmpContract.USN_FIELD_MIDDLENAME, entityNew.getUsnFieldMiddleName());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldDetails() <= entityNew.getUsnFieldDetails()) {
                    cv.put(EmpContract.DETAILS, entityNew.getDetails());
                    cv.put(EmpContract.USN_FIELD_DETAILS, entityNew.getUsnFieldDetails());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldGender() <= entityNew.getUsnFieldGender()) {
                    cv.put(EmpContract.GENDER, entityNew.getGender());
                    cv.put(EmpContract.USN_FIELD_GENDER, entityNew.getUsnFieldGender());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldCountry() <= entityNew.getUsnFieldCountry()) {
                    cv.put(EmpContract.COUNTRY, entityNew.getCountry());
                    cv.put(EmpContract.USN_FIELD_COUNTRY, entityNew.getUsnFieldCountry());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldProvince() <= entityNew.getUsnFieldProvince()) {
                    cv.put(EmpContract.PROVINCE, entityNew.getProvince());
                    cv.put(EmpContract.USN_FIELD_PROVINCE, entityNew.getUsnFieldProvince());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldPostalCode() <= entityNew.getUsnFieldPostalCode()) {
                    cv.put(EmpContract.POSTAL_CODE, entityNew.getPostalCode());
                    cv.put(EmpContract.USN_FIELD_POSTALCODE, entityNew.getUsnFieldPostalCode());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldCity() <= entityNew.getUsnFieldCity()) {
                    cv.put(EmpContract.CITY, entityNew.getCity());
                    cv.put(EmpContract.USN_FIELD_CITY, entityNew.getUsnFieldCity());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldStreet() <= entityNew.getUsnFieldStreet()) {
                    cv.put(EmpContract.STREET, entityNew.getStreet());
                    cv.put(EmpContract.USN_FIELD_STREET, entityNew.getUsnFieldStreet());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldUidGroup() <= entityNew.getUsnFieldUidGroup()) {
                    cv.put(EmpContract.GROUP_UID, entityNew.getGroupUid() == null ? null//
                            : String.valueOf(entityNew.getGroupUid()));
                    cv.put(EmpContract.USN_FIELD_UID_GROUP, entityNew.getUsnFieldUidGroup());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldBirthday() <= entityNew.getUsnFieldBirthday()) {
                    cv.put(EmpContract.BIRTHDAY, entityNew.getBirthday() == null ? null//
                            : entityNew.getBirthday().getTime());
                    cv.put(EmpContract.USN_FIELD_BIRTHDAY, entityNew.getUsnFieldBirthday());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldCommunication() <= entityNew.getUsnFieldCommunication()) {
                    cv.put(EmpContract.COMMUNICATION, entityNew.getCommunication());
                    cv.put(EmpContract.USN_FIELD_COMMUNICATION, entityNew.getUsnFieldCommunication());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldOrder() <= entityNew.getUsnFieldOrder()) {
                    cv.put(EmpContract.ORDERS, entityNew.getOrder());
                    cv.put(EmpContract.USN_FIELD_ORDER, entityNew.getUsnFieldOrder());

                    // Обновление после изменение порядка
                    updateTaskUserOrder(sb, entityNew.getLogin(), entityNew.getOrder(), oprtns);

                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldTitle() <= entityNew.getUsnFieldTitle()) {
                    cv.put(EmpContract.TITLE, entityNew.getTitle());
                    cv.put(EmpContract.USN_FIELD_TITLE, entityNew.getUsnFieldTitle());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldComment() <= entityNew.getUsnFieldComment()) {
                    cv.put(EmpContract.COMMENT, entityNew.getComment());
                    cv.put(EmpContract.USN_FIELD_COMMENT, entityNew.getUsnFieldComment());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldNotifyBirthday() <= entityNew.getUsnFieldNotifyBirthday()) {
                    cv.put(EmpContract.NOTIFY_BIRTHDAY, entityNew.isNotifyBirthday());
                    cv.put(EmpContract.USN_FIELD_NOTIFYBIRTHDAY, entityNew.getUsnFieldNotifyBirthday());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldFavorite() <= entityNew.getUsnFieldFavorite()) {
                    cv.put(EmpContract.FAVORITE, entityNew.isFavorite());
                    cv.put(EmpContract.USN_FIELD_FAVORITE, entityNew.getUsnFieldFavorite());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldShowInNavigator() <= entityNew.getUsnFieldShowInNavigator()) {
                    cv.put(EmpContract.SHOW_IN_NAVIGATOR, entityNew.isShowInNavigator());
                    cv.put(EmpContract.USN_FIELD_SHOWINNAVIGATOR, entityNew.getUsnFieldShowInNavigator());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                if (entityOld.getUsnFieldPhone() <= entityNew.getUsnFieldPhone()) {
                    cv.put(EmpContract.PHONE, entityNew.getPhone());
                    cv.put(EmpContract.USN_FIELD_PHONE, entityNew.getUsnFieldPhone());
                } else {
                    cv.put(EmpContract.USN_ENTITY, 0);
                }

                oprtns.add(ContentProviderOperation.newUpdate(EmpContract.CONTENT_URI)//
                        .withSelection(selection, null)//
                        .withValues(cv)//
                        .build());
            } else {
                oprtns.add(ContentProviderOperation.newInsert(EmpContract.CONTENT_URI)//
                        .withValues(entityNew.getContentValues(null))//
                        .build());
                // Обновление после добавления сотрудника порядка
                updateTaskUserOrder(sb,entityNew.getLogin(), entityNew.getOrder(), oprtns);
            }

            c.close();
        }

        }

        cr.applyBatch(LeaderTaskProviderMetaData.AUTHORITY, oprtns);

        return true;
    }

    public static void reSortEmp(Context context) {
        final ContentResolver cr = context.getContentResolver();

        ArrayList<ContentProviderOperation> operations = null;
        Cursor c = null;


        try {
            c = cr.query(EmpContract.CONTENT_URI, null, null, null, EmpContract.DEFAULT_SORT);

            if (c.getCount() == 0) {
                return;
            }

            operations = new ArrayList<ContentProviderOperation>();

            final int columnOrders = c.getColumnIndex(EmpContract.ORDERS);
            final int columnUsnFieldOrder = c.getColumnIndex(EmpContract.USN_FIELD_ORDER);
            final int columnUid = c.getColumnIndex(EmpContract.UID);
            final int columnLogin = c.getColumnIndex(EmpContract.LOGIN);


            final StringBuilder sb = new StringBuilder();
            int count = 1;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext(), count++) {
                if (count != c.getInt(columnOrders)) {
                    final ContentValues cv = new ContentValues(3);
                    cv.put(EmpContract.USN_ENTITY, 0);
                    cv.put(EmpContract.USN_FIELD_ORDER, c.getLong(columnUsnFieldOrder) + 1);
                    cv.put(EmpContract.ORDERS, count);

                    operations.add(ContentProviderOperation//
                            .newUpdate(EmpContract.CONTENT_URI)//
                            .withValues(cv)//
                            .withSelection(EmpContract.selectionUid(c.getString(columnUid)), null)//
                            .build());

                    updateTaskUserOrder(sb, c.getString(columnLogin), columnOrders, operations);
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        try {
            if (operations != null) {
                cr.applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);
            }
        } catch (Exception e) {}
        //
        // Emp.setEmpSortToTaskCustomer(context);
    }

    public static void checkDefaultEmpCreated(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(EmpContract.CONTENT_URI, null,//
                    EmpContract.selectionUid(Emp.DEFAULT_UUID_EMP_S), null, null);

            if (cursor.getCount() == 0) {
                final Emp emp = new Emp();
                emp.setUid(Emp.DEFAULT_UUID_EMP);
                emp.setLogin(LTSettings.getInstance(context).getUserName());
                emp.setTitle(emp.getLogin());
                emp.setOrder(1);

                context.getContentResolver().insert(EmpContract.CONTENT_URI, emp.getContentValues(null));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}