package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultSortOrder;
import com.v2soft.AndLib.dao.TreeDataContainer;

/**
 * 2014-06-18
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */
@DatabaseTable(tableName = ContactContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = ContactContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = ContactContract.TABLE_NAME)
public class Contact extends TreeDataContainer<Contact> implements SlidingMenuTreeDataContainer, Serializable, IEntity,
        Comparable<Contact>, BaseLionEntityInterface {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = ContactContract._ID, generatedId = true)
    private int mEmptyId;

    @DatabaseField(columnName = ContactContract.UID, canBeNull = false, index = true)
    private UUID mUid;

    @DatabaseField(columnName = ContactContract.UID_PARENT)
    private UUID mUidParent;

    @DatabaseField(columnName = ContactContract.EMAIL_CREATOR)
    private String mEmailCreator;

    @DatabaseField(columnName = ContactContract.UID_GROUP)
    private UUID mUidGroup;

    @DatabaseField(columnName = ContactContract.TITLE)
    private String mTitle;

    @DatabaseField(columnName = ContactContract.IS_GROUP)
    private boolean mGroup;

    @DatabaseField(columnName = ContactContract.GENDER)
    private int mGender;

    @DatabaseField(columnName = ContactContract.FIRST_NAME)
    private String mFirstName;

    @DatabaseField(columnName = ContactContract.MIDDLE_NAME)
    private String mMiddleName;

    @DatabaseField(columnName = ContactContract.LAST_NAME)
    private String mLastName;

    @DatabaseField(columnName = ContactContract.COMPANY_NAME)
    private String mCompanyName;

    @DatabaseField(columnName = ContactContract.JOB_TITLE)
    private String mJobTitle;

    @DatabaseField(columnName = ContactContract.DETAILS)
    private String mDetails;

    @DatabaseField(columnName = ContactContract.BIRTHDAY, dataType = DataType.DATE_LONG)
    private Date mBirthday;

    @DatabaseField(columnName = ContactContract.COMMUNICATIONS)
    private String mCommunications;

    @DatabaseField(columnName = ContactContract.HOME_COUNTRY)
    private String mHomeCountry;

    @DatabaseField(columnName = ContactContract.HOME_REGION)
    private String mHomeRegion;

    @DatabaseField(columnName = ContactContract.HOME_INDEX)
    private String mHomeIndex;

    @DatabaseField(columnName = ContactContract.HOME_CITY)
    private String mHomeCity;

    @DatabaseField(columnName = ContactContract.HOME_STREET)
    private String mHomeStreet;

    @DatabaseField(columnName = ContactContract.WORK_COUNTRY)
    private String mWorkCountry;

    @DatabaseField(columnName = ContactContract.WORK_REGION)
    private String mWorkRegion;

    @DatabaseField(columnName = ContactContract.WORK_INDEX)
    private String mWorkIndex;

    @DatabaseField(columnName = ContactContract.WORK_CITY)
    private String mWorkCity;

    @DatabaseField(columnName = ContactContract.WORK_STREET)
    private String mWorkStreet;

    @DefaultSortOrder
    @DatabaseField(columnName = ContactContract.ORDERS)
    private int mOrder;

    @DatabaseField(columnName = ContactContract.COLLAPSED)
    private boolean mCollapsed;

    @DatabaseField(columnName = ContactContract.FAVORITE)
    private boolean mFavorite;

    @DatabaseField(columnName = ContactContract.SHOW_NAVIGATOR)
    private boolean mShowNavigator;

    @DatabaseField(columnName = ContactContract.NOTIFY_BIRTHDAY)
    private boolean mNotifyBirthday;

    @DatabaseField(columnName = ContactContract.USN_ENTITY)
    private long mUsnEntity;

    @DatabaseField(columnName = ContactContract.USN_FIELD_UID_PARENT)
    private long mUsnFieldUidParent;

    @DatabaseField(columnName = ContactContract.USN_FIELD_UID_GROUP)
    private long mUsnFieldUidGroup;

    @DatabaseField(columnName = ContactContract.USN_FIELD_TITLE)
    private long mUsnFieldTitle;

    @DatabaseField(columnName = ContactContract.USN_FIELD_ISGROUP)
    private long mUsnFieldIsGroup;

    @DatabaseField(columnName = ContactContract.USN_FIELD_GENDER)
    private long mUsnFieldGender;

    @DatabaseField(columnName = ContactContract.USN_FIELD_FIRSTNAME)
    private long mUsnFieldFirstName;

    @DatabaseField(columnName = ContactContract.USN_FIELD_MIDDLENAME)
    private long mUsnFieldMiddleName;

    @DatabaseField(columnName = ContactContract.USN_FIELD_LASTNAME)
    private long mUsnFieldLastName;

    @DatabaseField(columnName = ContactContract.USN_FIELD_COMPANY_NAME)
    private long mUsnFieldCompanyName;

    @DatabaseField(columnName = ContactContract.USN_FIELD_JOB_TITLE)
    private long mUsnFieldJobTitle;

    @DatabaseField(columnName = ContactContract.USN_FIELD_DETAILS)
    private long mUsnFieldDetails;

    @DatabaseField(columnName = ContactContract.USN_FIELD_BIRTHDAY)
    private long mUsnFieldBirthday;

    @DatabaseField(columnName = ContactContract.USN_FIELD_COMMUNICATIONS)
    private long mUsnFieldCommunications;

    @DatabaseField(columnName = ContactContract.USN_FIELD_HOME_CITY)
    private long mUsnFieldHomeCity;

    @DatabaseField(columnName = ContactContract.USN_FIELD_HOME_COUNTRY)
    private long mUsnFieldHomeCountry;

    @DatabaseField(columnName = ContactContract.USN_FIELD_HOME_REGION)
    private long mUsnFieldHomeRegion;

    @DatabaseField(columnName = ContactContract.USN_FIELD_HOME_INDEX)
    private long mUsnFieldHomeIndex;

    @DatabaseField(columnName = ContactContract.USN_FIELD_HOME_STREET)
    private long mUsnFieldHomeStreet;

    @DatabaseField(columnName = ContactContract.USN_FIELD_WORK_CITY)
    private long mUsnFieldWorkCity;

    @DatabaseField(columnName = ContactContract.USN_FIELD_WORK_COUNTRY)
    private long mUsnFieldWorkCountry;

    @DatabaseField(columnName = ContactContract.USN_FIELD_WORK_REGION)
    private long mUsnFieldWorkRegion;

    @DatabaseField(columnName = ContactContract.USN_FIELD_WORK_INDEX)
    private long mUsnFieldWorkIndex;

    @DatabaseField(columnName = ContactContract.USN_FIELD_WORK_STREET)
    private long mUsnFieldWorkStreet;

    @DatabaseField(columnName = ContactContract.USN_FIELD_ORDER)
    private long mUsnFieldOrder;

    @DatabaseField(columnName = ContactContract.USN_FIELD_COLLAPSED)
    private long mUsnFieldCollapsed;

    @DatabaseField(columnName = ContactContract.USN_FIELD_FAVORITE)
    private long mUsnFieldFavorite;

    @DatabaseField(columnName = ContactContract.USN_FIELD_SHOW_NAVIGATOR)
    private long mUsnFieldShowNavigator;

    @DatabaseField(columnName = ContactContract.USN_FIELD_NOTIFY_BIRTHDAY)
    private long mUsnFieldNotifyBirthday;

    @DatabaseField(foreign = true)
    private Contact mParent;

    @DatabaseField(columnName = ContactContract.USN_FIELD_FOTO)
    private long mUsnFieldFoto;

    private int mLevel;


    private static int[] sColums;

    public Contact() {
        mChilds = new ArrayList<Contact>(0);
    }

    public Contact(Cursor c) {
        setContact(c);
    }

    public Contact getParent() {
        return mParent;
    }

    public void setParent(Contact parent) {
        mParent = parent;
    }

    public void addChild(Contact contact) {
        mChilds.add(contact);
        contact.setParent(this);
        contact.setIndent(mLevel + 1);
    }

    public static final Comparator<Contact> COMPARATOR = new Comparator<Contact>() {
        @Override
        public int compare(Contact lhs, Contact rhs) {
            return lhs.getId().compareTo(rhs.getId());
        }
    };

    public void setContact(Cursor c) {
        fillFastTable(c);

        setUid(UUID.fromString(c.getString(sColums[0])));
        setUidParent(Task.getUUIDFromString(c.getString(sColums[1])));
        setEmailCreator(c.getString(sColums[2]));
        setUidGroup(Task.getUUIDFromString(c.getString(sColums[3])));
        setTitle(c.getString(sColums[4]));
        setGroup(BaseSOAP.equalsOne(c.getInt(sColums[5])));
        setGender(c.getInt(sColums[6]));
        setFirstName(c.getString(sColums[7]));
        setMiddleName(c.getString(sColums[8]));
        setLastName(c.getString(sColums[9]));
        setCompanyName(c.getString(sColums[10]));
        setJobTitle(c.getString(sColums[11]));
        setDetails(c.getString(sColums[12]));
        setBirthday(new Date(c.getLong(sColums[13])));
        setCommunications(c.getString(sColums[14]));
        setHomeCountry(c.getString(sColums[15]));
        setHomeRegion(c.getString(sColums[16]));
        setHomeIndex(c.getString(sColums[17]));
        setHomeCity(c.getString(sColums[18]));
        setHomeStreet(c.getString(sColums[19]));
        setWorkCountry(c.getString(sColums[20]));
        setWorkRegion(c.getString(sColums[21]));
        setWorkIndex(c.getString(sColums[22]));
        setWorkCity(c.getString(sColums[23]));
        setWorkStreet(c.getString(sColums[24]));
        setOrder(c.getInt(sColums[25]));
        setCollapsed(BaseSOAP.equalsOne(c.getInt(sColums[26])));
        setFavorite(BaseSOAP.equalsOne(c.getInt(sColums[27])));
        setShowNavigator(BaseSOAP.equalsOne(c.getInt(sColums[28])));
        setNotifyBirthday(BaseSOAP.equalsOne(c.getInt(sColums[29])));

        setUsnEntity(c.getLong(sColums[30]));
        setUsnFieldUidParent(c.getLong(sColums[31]));
        setUsnFieldUidGroup(c.getLong(sColums[32]));
        setUsnFieldTitle(c.getLong(sColums[33]));
        setUsnFieldIsGroup(c.getLong(sColums[34]));
        setUsnFieldGender(c.getLong(sColums[35]));
        setUsnFieldFirstName(c.getLong(sColums[36]));
        setUsnFieldMiddleName(c.getLong(sColums[37]));
        setUsnFieldLastName(c.getLong(sColums[38]));
        setUsnFieldCompanyName(c.getLong(sColums[39]));
        setUsnFieldJobTitle(c.getLong(sColums[40]));
        setUsnFieldDetails(c.getLong(sColums[41]));
        setUsnFieldBirthday(c.getLong(sColums[42]));
        setUsnFieldCommunications(c.getLong(sColums[43]));
        setUsnFieldHomeCity(c.getLong(sColums[44]));
        setUsnFieldHomeCountry(c.getLong(sColums[45]));
        setUsnFieldHomeRegion(c.getLong(sColums[46]));
        setUsnFieldHomeIndex(c.getLong(sColums[47]));
        setUsnFieldHomeStreet(c.getLong(sColums[48]));
        setUsnFieldWorkCity(c.getLong(sColums[49]));
        setUsnFieldWorkCountry(c.getLong(sColums[50]));
        setUsnFieldWorkRegion(c.getLong(sColums[51]));
        setUsnFieldWorkIndex(c.getLong(sColums[52]));
        setUsnFieldWorkStreet(c.getLong(sColums[53]));
        setUsnFieldOrder(c.getLong(sColums[54]));
        setUsnFieldCollapsed(c.getLong(sColums[55]));
        setUsnFieldFavorite(c.getLong(sColums[56]));
        setUsnFieldShowNavigator(c.getLong(sColums[57]));
        setUsnFieldNotifyBirthday(c.getLong(sColums[58]));
    }

    private static void fillFastTable(Cursor c) {
        if (sColums == null) {
            sColums = new int[59];

            sColums[0] = c.getColumnIndex(ContactContract.UID);
            sColums[1] = c.getColumnIndex(ContactContract.UID_PARENT);
            sColums[2] = c.getColumnIndex(ContactContract.EMAIL_CREATOR);
            sColums[3] = c.getColumnIndex(ContactContract.UID_GROUP);
            sColums[4] = c.getColumnIndex(ContactContract.TITLE);
            sColums[5] = c.getColumnIndex(ContactContract.IS_GROUP);
            sColums[6] = c.getColumnIndex(ContactContract.GENDER);
            sColums[7] = c.getColumnIndex(ContactContract.FIRST_NAME);
            sColums[8] = c.getColumnIndex(ContactContract.MIDDLE_NAME);
            sColums[9] = c.getColumnIndex(ContactContract.LAST_NAME);
            sColums[10] = c.getColumnIndex(ContactContract.COMPANY_NAME);
            sColums[11] = c.getColumnIndex(ContactContract.JOB_TITLE);
            sColums[12] = c.getColumnIndex(ContactContract.DETAILS);
            sColums[13] = c.getColumnIndex(ContactContract.BIRTHDAY);
            sColums[14] = c.getColumnIndex(ContactContract.COMMUNICATIONS);
            sColums[15] = c.getColumnIndex(ContactContract.HOME_COUNTRY);
            sColums[16] = c.getColumnIndex(ContactContract.HOME_REGION);
            sColums[17] = c.getColumnIndex(ContactContract.HOME_INDEX);
            sColums[18] = c.getColumnIndex(ContactContract.HOME_CITY);
            sColums[19] = c.getColumnIndex(ContactContract.HOME_STREET);
            sColums[20] = c.getColumnIndex(ContactContract.WORK_COUNTRY);
            sColums[21] = c.getColumnIndex(ContactContract.WORK_REGION);
            sColums[22] = c.getColumnIndex(ContactContract.WORK_INDEX);
            sColums[23] = c.getColumnIndex(ContactContract.WORK_CITY);
            sColums[24] = c.getColumnIndex(ContactContract.WORK_STREET);
            sColums[25] = c.getColumnIndex(ContactContract.ORDERS);
            sColums[26] = c.getColumnIndex(ContactContract.COLLAPSED);
            sColums[27] = c.getColumnIndex(ContactContract.FAVORITE);
            sColums[28] = c.getColumnIndex(ContactContract.SHOW_NAVIGATOR);
            sColums[29] = c.getColumnIndex(ContactContract.NOTIFY_BIRTHDAY);

            sColums[30] = c.getColumnIndex(ContactContract.USN_ENTITY);
            sColums[31] = c.getColumnIndex(ContactContract.USN_FIELD_UID_PARENT);
            sColums[32] = c.getColumnIndex(ContactContract.USN_FIELD_UID_GROUP);
            sColums[33] = c.getColumnIndex(ContactContract.USN_FIELD_TITLE);
            sColums[34] = c.getColumnIndex(ContactContract.USN_FIELD_ISGROUP);
            sColums[35] = c.getColumnIndex(ContactContract.USN_FIELD_GENDER);
            sColums[36] = c.getColumnIndex(ContactContract.USN_FIELD_FIRSTNAME);
            sColums[37] = c.getColumnIndex(ContactContract.USN_FIELD_MIDDLENAME);
            sColums[38] = c.getColumnIndex(ContactContract.USN_FIELD_LASTNAME);
            sColums[39] = c.getColumnIndex(ContactContract.USN_FIELD_COMPANY_NAME);
            sColums[40] = c.getColumnIndex(ContactContract.USN_FIELD_JOB_TITLE);
            sColums[41] = c.getColumnIndex(ContactContract.USN_FIELD_DETAILS);
            sColums[42] = c.getColumnIndex(ContactContract.USN_FIELD_BIRTHDAY);
            sColums[43] = c.getColumnIndex(ContactContract.USN_FIELD_COMMUNICATIONS);
            sColums[44] = c.getColumnIndex(ContactContract.USN_FIELD_HOME_CITY);
            sColums[45] = c.getColumnIndex(ContactContract.USN_FIELD_HOME_COUNTRY);
            sColums[46] = c.getColumnIndex(ContactContract.USN_FIELD_HOME_REGION);
            sColums[47] = c.getColumnIndex(ContactContract.USN_FIELD_HOME_INDEX);
            sColums[48] = c.getColumnIndex(ContactContract.USN_FIELD_HOME_STREET);
            sColums[49] = c.getColumnIndex(ContactContract.USN_FIELD_WORK_CITY);
            sColums[50] = c.getColumnIndex(ContactContract.USN_FIELD_WORK_COUNTRY);
            sColums[51] = c.getColumnIndex(ContactContract.USN_FIELD_WORK_REGION);
            sColums[52] = c.getColumnIndex(ContactContract.USN_FIELD_WORK_INDEX);
            sColums[53] = c.getColumnIndex(ContactContract.USN_FIELD_WORK_STREET);
            sColums[54] = c.getColumnIndex(ContactContract.USN_FIELD_ORDER);
            sColums[55] = c.getColumnIndex(ContactContract.USN_FIELD_COLLAPSED);
            sColums[56] = c.getColumnIndex(ContactContract.USN_FIELD_FAVORITE);
            sColums[57] = c.getColumnIndex(ContactContract.USN_FIELD_SHOW_NAVIGATOR);
            sColums[58] = c.getColumnIndex(ContactContract.USN_FIELD_NOTIFY_BIRTHDAY);
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

        if (o instanceof Contact) {
            return getId().equals(((Contact) o).getId());
        }

        return false;
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (ContactContract.UID.equalsIgnoreCase(key)) {
            mUid = UUID.fromString(value);
            return;
        }

        if (ContactContract.UID_PARENT.equalsIgnoreCase(key)) {
            mUidParent = UUID.fromString(value);
            return;
        }

        if (ContactContract.EMAIL_CREATOR.equalsIgnoreCase(key)) {
            mEmailCreator = value;
            return;
        }

        if (ContactContract.UID_GROUP.equalsIgnoreCase(key)) {
            mUidGroup = UUID.fromString(value);
            return;
        }

        if (ContactContract.TITLE.equalsIgnoreCase(key)) {
            mTitle = value;
            return;
        }

        if (ContactContract.IS_GROUP.equalsIgnoreCase(key)) {
            mGroup = BaseSOAP.equalsOne(value);
            return;
        }

        if (ContactContract.GENDER.equalsIgnoreCase(key)) {
            mGender = Integer.parseInt(value);
            return;
        }

        if (ContactContract.FIRST_NAME.equalsIgnoreCase(key)) {
            mFirstName = value;
            return;
        }

        if (ContactContract.MIDDLE_NAME.equalsIgnoreCase(key)) {
            mMiddleName = value;
            return;
        }

        if (ContactContract.LAST_NAME.equalsIgnoreCase(key)) {
            mLastName = value;
            return;
        }

        if (ContactContract.COMPANY_NAME.equalsIgnoreCase(key)) {
            mCompanyName = value;
            return;
        }

        if (ContactContract.JOB_TITLE.equalsIgnoreCase(key)) {
            mJobTitle = value;
            return;
        }

        if (ContactContract.DETAILS.equalsIgnoreCase(key)) {
            mDetails = value;
            return;
        }

        if (ContactContract.BIRTHDAY.equalsIgnoreCase(key)) {
            mBirthday = BaseSOAP.parseDate(value);
            return;
        }

        if (ContactContract.COMMUNICATIONS.equalsIgnoreCase(key)) {
            mCommunications = value;
            return;
        }

        if (ContactContract.HOME_COUNTRY.equalsIgnoreCase(key)) {
            mHomeCountry = value;
            return;
        }

        if (ContactContract.HOME_REGION.equalsIgnoreCase(key)) {
            mHomeRegion = value;
            return;
        }

        if (ContactContract.HOME_INDEX.equalsIgnoreCase(key)) {
            mHomeIndex = value;
            return;
        }

        if (ContactContract.HOME_CITY.equalsIgnoreCase(key)) {
            mHomeCity = value;
            return;
        }

        if (ContactContract.HOME_STREET.equalsIgnoreCase(key)) {
            mHomeStreet = value;
            return;
        }

        if (ContactContract.WORK_COUNTRY.equalsIgnoreCase(key)) {
            mWorkCountry = value;
            return;
        }

        if (ContactContract.WORK_REGION.equalsIgnoreCase(key)) {
            mWorkRegion = value;
            return;
        }

        if (ContactContract.WORK_INDEX.equalsIgnoreCase(key)) {
            mWorkIndex = value;
            return;
        }

        if (ContactContract.WORK_CITY.equalsIgnoreCase(key)) {
            mWorkCity = value;
            return;
        }

        if (ContactContract.WORK_STREET.equalsIgnoreCase(key)) {
            mWorkStreet = value;
            return;
        }

        if (ContactContract.ORDER.equalsIgnoreCase(key)) {
            mOrder = Integer.parseInt(value);
            return;
        }

        if (ContactContract.COLLAPSED.equalsIgnoreCase(key)) {
            mCollapsed = BaseSOAP.equalsOne(value);
            return;
        }

        if (ContactContract.FAVORITE.equalsIgnoreCase(key)) {
            mFavorite = BaseSOAP.equalsOne(value);
            return;
        }

        if (ContactContract.SHOW_NAVIGATOR.equalsIgnoreCase(key)) {
            mShowNavigator = BaseSOAP.equalsOne(value);
            return;
        }

        if (ContactContract.NOTIFY_BIRTHDAY.equalsIgnoreCase(key)) {
            mNotifyBirthday = BaseSOAP.equalsOne(value);
            return;
        }

        if (ContactContract.USN_ENTITY.equalsIgnoreCase(key)) {
            mUsnEntity = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_UID_PARENT.equalsIgnoreCase(key)) {
            mUsnFieldUidParent = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_UID_GROUP.equalsIgnoreCase(key)) {
            mUsnFieldUidGroup = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_TITLE.equalsIgnoreCase(key)) {
            mUsnFieldTitle = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_ISGROUP.equalsIgnoreCase(key)) {
            mUsnFieldIsGroup = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_GENDER.equalsIgnoreCase(key)) {
            mUsnFieldGender = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_FIRSTNAME.equalsIgnoreCase(key)) {
            mUsnFieldFirstName = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_MIDDLENAME.equalsIgnoreCase(key)) {
            mUsnFieldMiddleName = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_LASTNAME.equalsIgnoreCase(key)) {
            mUsnFieldLastName = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_COMPANY_NAME.equalsIgnoreCase(key)) {
            mUsnFieldCompanyName = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_JOB_TITLE.equalsIgnoreCase(key)) {
            mUsnFieldJobTitle = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_DETAILS.equalsIgnoreCase(key)) {
            mUsnFieldDetails = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_BIRTHDAY.equalsIgnoreCase(key)) {
            mUsnFieldBirthday = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_COMMUNICATIONS.equalsIgnoreCase(key)) {
            mUsnFieldCommunications = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_HOME_CITY.equalsIgnoreCase(key)) {
            mUsnFieldHomeCity = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_HOME_COUNTRY.equalsIgnoreCase(key)) {
            mUsnFieldHomeCountry = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_HOME_REGION.equalsIgnoreCase(key)) {
            mUsnFieldHomeRegion = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_HOME_INDEX.equalsIgnoreCase(key)) {
            mUsnFieldHomeIndex = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_HOME_STREET.equalsIgnoreCase(key)) {
            mUsnFieldHomeStreet = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_WORK_CITY.equalsIgnoreCase(key)) {
            mUsnFieldWorkCity = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_WORK_COUNTRY.equalsIgnoreCase(key)) {
            mUsnFieldWorkCountry = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_WORK_REGION.equalsIgnoreCase(key)) {
            mUsnFieldWorkRegion = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_WORK_INDEX.equalsIgnoreCase(key)) {
            mUsnFieldWorkIndex = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_WORK_STREET.equalsIgnoreCase(key)) {
            mUsnFieldWorkStreet = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_ORDER.equalsIgnoreCase(key)) {
            mUsnFieldOrder = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_COLLAPSED.equalsIgnoreCase(key)) {
            mUsnFieldCollapsed = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_FAVORITE.equalsIgnoreCase(key)) {
            mUsnFieldFavorite = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_SHOW_NAVIGATOR.equalsIgnoreCase(key)) {
            mUsnFieldShowNavigator = Long.parseLong(value);
            return;
        }

        if (ContactContract.USN_FIELD_NOTIFY_BIRTHDAY.equalsIgnoreCase(key)) {
            mUsnFieldNotifyBirthday = Long.parseLong(value);
            return;
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(ContactContract.SERVER_CLASS));

        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.UID, getUid()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.UID_PARENT, getUidParent()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.EMAIL_CREATOR, getEmailCreator()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.UID_GROUP, getUidGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.TITLE, getTitle()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.IS_GROUP, isGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.GENDER, getGender()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.FIRST_NAME, getFirstName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.MIDDLE_NAME, getMiddleName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.LAST_NAME, getLastName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.COMPANY_NAME, getCompanyName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.JOB_TITLE, getJobTitle()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.DETAILS, getDetails()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.BIRTHDAY, getBirthday()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.COMMUNICATIONS, getCommunications()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.HOME_COUNTRY, getHomeCountry()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.HOME_REGION, getHomeRegion()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.HOME_INDEX, getHomeIndex()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.HOME_CITY, getHomeCity()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.HOME_STREET, getHomeStreet()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.WORK_COUNTRY, getWorkCountry()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.WORK_REGION, getWorkRegion()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.WORK_INDEX, getWorkIndex()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.WORK_CITY, getWorkCity()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.WORK_STREET, getWorkStreet()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.ORDER, getOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.COLLAPSED, isCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.FAVORITE, isFavorite()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.SHOW_NAVIGATOR, isShowNavigator()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.NOTIFY_BIRTHDAY, isNotifyBirthday()));

        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_ENTITY, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_UID_PARENT, getUsnFieldUidParent()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_UID_GROUP, getUsnFieldUidGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_TITLE, getUsnFieldTitle()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_ISGROUP, getUsnFieldIsGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_GENDER, getUsnFieldGender()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_FIRSTNAME, getUsnFieldFirstName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_MIDDLENAME, getUsnFieldMiddleName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_LASTNAME, getUsnFieldLastName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_COMPANY_NAME, getUsnFieldCompanyName()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_JOB_TITLE, getUsnFieldJobTitle()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_DETAILS, getUsnFieldDetails()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_BIRTHDAY, getUsnFieldBirthday()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_COMMUNICATIONS, getUsnFieldCommunications()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_HOME_CITY, getUsnFieldHomeCity()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_HOME_COUNTRY, getUsnFieldHomeCountry()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_HOME_REGION, getUsnFieldHomeRegion()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_HOME_INDEX, getUsnFieldHomeIndex()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_HOME_STREET, getUsnFieldHomeStreet()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_WORK_CITY, getUsnFieldWorkCity()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_WORK_COUNTRY, getUsnFieldWorkCountry()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_WORK_REGION, getUsnFieldWorkRegion()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_WORK_INDEX, getUsnFieldWorkIndex()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_WORK_STREET, getUsnFieldWorkStreet()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_ORDER, getUsnFieldOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_COLLAPSED, getUsnFieldCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_FAVORITE, getUsnFieldFavorite()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_SHOW_NAVIGATOR, getUsnFieldShowNavigator()));
        sb.append(BaseSOAP.getXmlnsValueLine(ContactContract.USN_FIELD_NOTIFY_BIRTHDAY, getUsnFieldNotifyBirthday()));

        sb.append(BaseSOAP.getClose(ContactContract.SERVER_CLASS));
    }

    @Override
    public String getServerClass() {
        return ContactContract.SERVER_CLASS;
    }

    public ContentValues getContentValues(ContentValues cv) {
        if (cv == null) {
            cv = new ContentValues();
        } else {
            cv.clear();
        }

        cv.put(ContactContract.UID, String.valueOf(getUid()));
        cv.put(ContactContract.UID_PARENT, getUidParent() == null ? null : String.valueOf(getUidParent()));
        cv.put(ContactContract.EMAIL_CREATOR, getEmailCreator());
        cv.put(ContactContract.UID_GROUP, getUidGroup() == null ? null : String.valueOf(getUidGroup()));
        cv.put(ContactContract.TITLE, getTitle());
        cv.put(ContactContract.IS_GROUP, isGroup());
        cv.put(ContactContract.GENDER, getGender());
        cv.put(ContactContract.FIRST_NAME, getFirstName());
        cv.put(ContactContract.MIDDLE_NAME, getMiddleName());
        cv.put(ContactContract.LAST_NAME, getLastName());
        cv.put(ContactContract.COMPANY_NAME, getCompanyName());
        cv.put(ContactContract.JOB_TITLE, getJobTitle());
        cv.put(ContactContract.DETAILS, getDetails());
        cv.put(ContactContract.BIRTHDAY, getBirthday().getTime());
        cv.put(ContactContract.COMMUNICATIONS, getCommunications());
        cv.put(ContactContract.HOME_COUNTRY, getHomeCountry());
        cv.put(ContactContract.HOME_REGION, getHomeRegion());
        cv.put(ContactContract.HOME_INDEX, getHomeIndex());
        cv.put(ContactContract.HOME_CITY, getHomeCity());
        cv.put(ContactContract.HOME_STREET, getHomeStreet());
        cv.put(ContactContract.WORK_COUNTRY, getWorkCountry());
        cv.put(ContactContract.WORK_REGION, getWorkRegion());
        cv.put(ContactContract.WORK_INDEX, getWorkIndex());
        cv.put(ContactContract.WORK_CITY, getWorkCity());
        cv.put(ContactContract.WORK_STREET, getWorkStreet());
        cv.put(ContactContract.ORDER, getOrder());
        cv.put(ContactContract.COLLAPSED, isCollapsed());
        cv.put(ContactContract.FAVORITE, isFavorite());
        cv.put(ContactContract.SHOW_NAVIGATOR, isShowNavigator());
        cv.put(ContactContract.NOTIFY_BIRTHDAY, isNotifyBirthday());

        cv.put(ContactContract.USN_ENTITY, getUsn());
        cv.put(ContactContract.USN_FIELD_UID_PARENT, getUsnFieldUidParent());
        cv.put(ContactContract.USN_FIELD_UID_GROUP, getUsnFieldUidGroup());
        cv.put(ContactContract.USN_FIELD_TITLE, getUsnFieldTitle());
        cv.put(ContactContract.USN_FIELD_ISGROUP, getUsnFieldIsGroup());
        cv.put(ContactContract.USN_FIELD_GENDER, getUsnFieldGender());
        cv.put(ContactContract.USN_FIELD_FIRSTNAME, getUsnFieldFirstName());
        cv.put(ContactContract.USN_FIELD_MIDDLENAME, getUsnFieldMiddleName());
        cv.put(ContactContract.USN_FIELD_LASTNAME, getUsnFieldLastName());
        cv.put(ContactContract.USN_FIELD_COMPANY_NAME, getUsnFieldCompanyName());
        cv.put(ContactContract.USN_FIELD_JOB_TITLE, getUsnFieldJobTitle());
        cv.put(ContactContract.USN_FIELD_DETAILS, getUsnFieldDetails());
        cv.put(ContactContract.USN_FIELD_BIRTHDAY, getUsnFieldBirthday());
        cv.put(ContactContract.USN_FIELD_COMMUNICATIONS, getUsnFieldCommunications());
        cv.put(ContactContract.USN_FIELD_HOME_CITY, getUsnFieldHomeCity());
        cv.put(ContactContract.USN_FIELD_HOME_COUNTRY, getUsnFieldHomeCountry());
        cv.put(ContactContract.USN_FIELD_HOME_REGION, getUsnFieldHomeRegion());
        cv.put(ContactContract.USN_FIELD_HOME_INDEX, getUsnFieldHomeIndex());
        cv.put(ContactContract.USN_FIELD_HOME_STREET, getUsnFieldHomeStreet());
        cv.put(ContactContract.USN_FIELD_WORK_CITY, getUsnFieldWorkCity());
        cv.put(ContactContract.USN_FIELD_WORK_COUNTRY, getUsnFieldWorkCountry());
        cv.put(ContactContract.USN_FIELD_WORK_REGION, getUsnFieldWorkRegion());
        cv.put(ContactContract.USN_FIELD_WORK_INDEX, getUsnFieldWorkIndex());
        cv.put(ContactContract.USN_FIELD_WORK_STREET, getUsnFieldWorkStreet());
        cv.put(ContactContract.USN_FIELD_ORDER, getUsnFieldOrder());
        cv.put(ContactContract.USN_FIELD_COLLAPSED, getUsnFieldCollapsed());
        cv.put(ContactContract.USN_FIELD_FAVORITE, getUsnFieldFavorite());
        cv.put(ContactContract.USN_FIELD_SHOW_NAVIGATOR, getUsnFieldShowNavigator());
        cv.put(ContactContract.USN_FIELD_NOTIFY_BIRTHDAY, getUsnFieldNotifyBirthday());

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

    public UUID getUidParent() {
        return mUidParent;
    }

    public void setUidParent(UUID uidParent) {
        mUidParent = uidParent;
    }

    public String getEmailCreator() {
        return mEmailCreator;
    }

    public void setEmailCreator(String emailCreator) {
        mEmailCreator = emailCreator;
    }

    public UUID getUidGroup() {
        return mUidGroup;
    }

    public void setUidGroup(UUID uidGroup) {
        mUidGroup = uidGroup;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isGroup() {
        return mGroup;
    }

    public void setGroup(boolean group) {
        mGroup = group;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
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

    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        mCompanyName = companyName;
    }

    public String getJobTitle() {
        return mJobTitle;
    }

    public void setJobTitle(String jobTitle) {
        mJobTitle = jobTitle;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public Date getBirthday() {
        return mBirthday;
    }

    public void setBirthday(Date birthday) {
        mBirthday = birthday;
    }

    public String getCommunications() {
        return mCommunications;
    }

    public void setCommunications(String communications) {
        mCommunications = communications;
    }

    public String getHomeCountry() {
        return mHomeCountry;
    }

    public void setHomeCountry(String homeCountry) {
        mHomeCountry = homeCountry;
    }

    public String getHomeRegion() {
        return mHomeRegion;
    }

    public void setHomeRegion(String homeRegion) {
        mHomeRegion = homeRegion;
    }

    public String getHomeIndex() {
        return mHomeIndex;
    }

    public void setHomeIndex(String homeIndex) {
        mHomeIndex = homeIndex;
    }

    public String getHomeCity() {
        return mHomeCity;
    }

    public void setHomeCity(String homeCity) {
        mHomeCity = homeCity;
    }

    public String getHomeStreet() {
        return mHomeStreet;
    }

    public void setHomeStreet(String homeStreet) {
        mHomeStreet = homeStreet;
    }

    public String getWorkCountry() {
        return mWorkCountry;
    }

    public void setWorkCountry(String workCountry) {
        mWorkCountry = workCountry;
    }

    public String getWorkRegion() {
        return mWorkRegion;
    }

    public void setWorkRegion(String workRegion) {
        mWorkRegion = workRegion;
    }

    public String getWorkIndex() {
        return mWorkIndex;
    }

    public void setWorkIndex(String workIndex) {
        mWorkIndex = workIndex;
    }

    public String getWorkCity() {
        return mWorkCity;
    }

    public void setWorkCity(String workCity) {
        mWorkCity = workCity;
    }

    public String getWorkStreet() {
        return mWorkStreet;
    }

    public void setWorkStreet(String workStreet) {
        mWorkStreet = workStreet;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }

    public boolean isCollapsed() {
        return mCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        mCollapsed = collapsed;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public boolean isShowNavigator() {
        return mShowNavigator;
    }

    public void setShowNavigator(boolean showNavigator) {
        mShowNavigator = showNavigator;
    }

    public boolean isNotifyBirthday() {
        return mNotifyBirthday;
    }

    public void setNotifyBirthday(boolean notifyBirthday) {
        mNotifyBirthday = notifyBirthday;
    }

    public long getUsnEntity() {
        return mUsnEntity;
    }

    public void setUsnEntity(long usnEntity) {
        mUsnEntity = usnEntity;
    }

    public void setUsnPlusPlus() {
        mUsnEntity++;
    }

    public long getUsnFieldUidParent() {
        return mUsnFieldUidParent;
    }

    public void setUsnFieldUidParent(long usnFieldUidParent) {
        mUsnFieldUidParent = usnFieldUidParent;
    }

    public long getUsnFieldUidGroup() {
        return mUsnFieldUidGroup;
    }

    public void setUsnFieldUidGroup(long usnFieldUidGroup) {
        mUsnFieldUidGroup = usnFieldUidGroup;
    }

    public long getUsnFieldTitle() {
        return mUsnFieldTitle;
    }

    public void setUsnFieldTitle(long usnFieldTitle) {
        mUsnFieldTitle = usnFieldTitle;
    }

    public long getUsnFieldIsGroup() {
        return mUsnFieldIsGroup;
    }

    public void setUsnFieldIsGroup(long usnFieldIsGroup) {
        mUsnFieldIsGroup = usnFieldIsGroup;
    }

    public long getUsnFieldGender() {
        return mUsnFieldGender;
    }

    public void setUsnFieldGender(long usnFieldGender) {
        mUsnFieldGender = usnFieldGender;
    }

    public long getUsnFieldFirstName() {
        return mUsnFieldFirstName;
    }

    public void setUsnFieldFirstName(long usnFieldFirstName) {
        mUsnFieldFirstName = usnFieldFirstName;
    }

    public long getUsnFieldMiddleName() {
        return mUsnFieldMiddleName;
    }

    public void setUsnFieldMiddleName(long usnFieldMiddleName) {
        mUsnFieldMiddleName = usnFieldMiddleName;
    }

    public long getUsnFieldLastName() {
        return mUsnFieldLastName;
    }

    public void setUsnFieldLastName(long usnFieldLastName) {
        mUsnFieldLastName = usnFieldLastName;
    }

    public long getUsnFieldCompanyName() {
        return mUsnFieldCompanyName;
    }

    public void setUsnFieldCompanyName(long usnFieldCompanyName) {
        mUsnFieldCompanyName = usnFieldCompanyName;
    }

    public long getUsnFieldJobTitle() {
        return mUsnFieldJobTitle;
    }

    public void setUsnFieldJobTitle(long usnFieldJobTitle) {
        mUsnFieldJobTitle = usnFieldJobTitle;
    }

    public long getUsnFieldDetails() {
        return mUsnFieldDetails;
    }

    public void setUsnFieldDetails(long usnFieldDetails) {
        mUsnFieldDetails = usnFieldDetails;
    }

    public long getUsnFieldBirthday() {
        return mUsnFieldBirthday;
    }

    public void setUsnFieldBirthday(long usnFieldBirthday) {
        mUsnFieldBirthday = usnFieldBirthday;
    }

    public long getUsnFieldCommunications() {
        return mUsnFieldCommunications;
    }

    public void setUsnFieldCommunications(long usnFieldCommunications) {
        mUsnFieldCommunications = usnFieldCommunications;
    }

    public long getUsnFieldHomeCity() {
        return mUsnFieldHomeCity;
    }

    public void setUsnFieldHomeCity(long usnFieldHomeCity) {
        mUsnFieldHomeCity = usnFieldHomeCity;
    }

    public long getUsnFieldHomeCountry() {
        return mUsnFieldHomeCountry;
    }

    public void setUsnFieldHomeCountry(long usnFieldHomeCountry) {
        mUsnFieldHomeCountry = usnFieldHomeCountry;
    }

    public long getUsnFieldHomeRegion() {
        return mUsnFieldHomeRegion;
    }

    public void setUsnFieldHomeRegion(long usnFieldHomeRegion) {
        mUsnFieldHomeRegion = usnFieldHomeRegion;
    }

    public long getUsnFieldHomeIndex() {
        return mUsnFieldHomeIndex;
    }

    public void setUsnFieldHomeIndex(long usnFieldHomeIndex) {
        mUsnFieldHomeIndex = usnFieldHomeIndex;
    }

    public long getUsnFieldHomeStreet() {
        return mUsnFieldHomeStreet;
    }

    public void setUsnFieldHomeStreet(long usnFieldHomeStreet) {
        mUsnFieldHomeStreet = usnFieldHomeStreet;
    }

    public long getUsnFieldWorkCity() {
        return mUsnFieldWorkCity;
    }

    public void setUsnFieldWorkCity(long usnFieldWorkCity) {
        mUsnFieldWorkCity = usnFieldWorkCity;
    }

    public long getUsnFieldWorkCountry() {
        return mUsnFieldWorkCountry;
    }

    public void setUsnFieldWorkCountry(long usnFieldWorkCountry) {
        mUsnFieldWorkCountry = usnFieldWorkCountry;
    }

    public long getUsnFieldWorkRegion() {
        return mUsnFieldWorkRegion;
    }

    public void setUsnFieldWorkRegion(long usnFieldWorkRegion) {
        mUsnFieldWorkRegion = usnFieldWorkRegion;
    }

    public long getUsnFieldWorkIndex() {
        return mUsnFieldWorkIndex;
    }

    public void setUsnFieldWorkIndex(long usnFieldWorkIndex) {
        mUsnFieldWorkIndex = usnFieldWorkIndex;
    }

    public long getUsnFieldWorkStreet() {
        return mUsnFieldWorkStreet;
    }

    public void setUsnFieldWorkStreet(long usnFieldWorkStreet) {
        mUsnFieldWorkStreet = usnFieldWorkStreet;
    }

    public long getUsnFieldOrder() {
        return mUsnFieldOrder;
    }

    public void setUsnFieldOrder(long usnFieldOrder) {
        mUsnFieldOrder = usnFieldOrder;
    }

    public long getUsnFieldCollapsed() {
        return mUsnFieldCollapsed;
    }

    public void setUsnFieldCollapsed(long usnFieldCollapsed) {
        mUsnFieldCollapsed = usnFieldCollapsed;
    }

    public long getUsnFieldFavorite() {
        return mUsnFieldFavorite;
    }

    public void setUsnFieldFavorite(long usnFieldFavorite) {
        mUsnFieldFavorite = usnFieldFavorite;
    }

    public long getUsnFieldShowNavigator() {
        return mUsnFieldShowNavigator;
    }

    public void setUsnFieldShowNavigator(long usnFieldShowNavigator) {
        mUsnFieldShowNavigator = usnFieldShowNavigator;
    }

    public long getUsnFieldNotifyBirthday() {
        return mUsnFieldNotifyBirthday;
    }

    public void setUsnFieldNotifyBirthday(long usnFieldNotifyBirthday) {
        mUsnFieldNotifyBirthday = usnFieldNotifyBirthday;
    }

    public long getUsnFieldFoto() {
        return mUsnFieldFoto;
    }

    public void setUsnFieldFoto(long usnFieldFoto) {
        mUsnFieldFoto = usnFieldFoto;
    }

    @Override
    public int compareTo(Contact another) {
        return 0;
    }

    @Override
    public int getNodeLevel() {
        return 0;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getFilterId() {
        return null;
    }

    @Override
    public int getIndent() {
        return mLevel;
    }

    public void setIndent(int level) {
        mLevel = level;
    }
}