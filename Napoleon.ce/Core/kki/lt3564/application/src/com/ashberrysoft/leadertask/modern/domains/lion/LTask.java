package com.ashberrysoft.leadertask.modern.domains.lion;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.IEntity;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuTreeDataContainer;
import com.ashberrysoft.leadertask.modern.builder.XmlSoap;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

import java.io.Serializable;
import java.util.UUID;

@DatabaseTable(tableName = LTaskContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = LTaskContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = LTaskContract.TABLE_NAME)
public final class LTask extends BaseLion<LTask> implements SlidingMenuTreeDataContainer, Serializable,
        IEntity, Comparable<LTask>, BaseLionEntityInterface {

    private static final long serialVersionUID = 1290227681L;
    public static final int MY_TASK_USER_ORDER = 1000000001;
    public static final int MY_TASK_USER_ORDER_DESC = -1000000001;
    public static final int EMAIL_TASK_USER_ORDER = 1000000000;
    public static final int MY_TASK_IS_USE_TERM_DEFAULT = 1000000000;
    public static final int MY_TASK_NOT_USE_TERM_DEFAULT = 1;

    @DatabaseField(columnName = LTaskContract._ID, dataType = DataType.INTEGER, generatedId = true)
    private int mId;

    @DatabaseField(columnName = LTaskContract.Orders, dataType = DataType.INTEGER)
    private int mOrder;

    @DatabaseField(columnName = LTaskContract.Uid, dataType = DataType.STRING, index = true, unique = true)
    private String mUid;

    @DatabaseField(columnName = LTaskContract.UIDParent, dataType = DataType.STRING, index = true)
    private String mUIDParent;

    @DatabaseField(columnName = LTaskContract.Collapsed, dataType = DataType.BOOLEAN)
    private boolean mCollapsed;

    @DatabaseField(columnName = LTaskContract.Name, dataType = DataType.STRING)
    private String mName;

    @DatabaseField(columnName = LTaskContract.Comment, dataType = DataType.STRING)
    private String mComment;

    @DatabaseField(columnName = LTaskContract.Status, dataType = DataType.INTEGER)
    private int mStatus;

    @DatabaseField(columnName = LTaskContract.TermBegin, dataType = DataType.LONG)
    private long mTermBegin;

    @DatabaseField(columnName = LTaskContract.TermEnd, dataType = DataType.LONG)
    private long mTermEnd;

    @DatabaseField(columnName = LTaskContract.EmailPerformer, dataType = DataType.STRING)
    private String mEmailPerformer;

    @DatabaseField(columnName = LTaskContract.UidProject, dataType = DataType.STRING)
    private String mUidProject;

    @DatabaseField(columnName = LTaskContract.UidMarker, dataType = DataType.STRING)
    private String mUidMarker;

    @DatabaseField(columnName = LTaskContract.Readed, dataType = DataType.BOOLEAN)
    private boolean mReaded;

    @DatabaseField(columnName = LTaskContract.OrderCustomer, dataType = DataType.INTEGER)
    private int mOrderCustomer;

    @DatabaseField(columnName = LTaskContract.TermBeginCustomer, dataType = DataType.LONG)
    private long mTermBeginCustomer;

    @DatabaseField(columnName = LTaskContract.TermEndCustomer, dataType = DataType.LONG)
    private long mTermEndCustomer;

    @DatabaseField(columnName = LTaskContract.EmailCustomer, dataType = DataType.STRING)
    private String mEmailCustomer;

    @DatabaseField(columnName = LTaskContract.Categories, dataType = DataType.STRING)
    private String mCategories;

    @DatabaseField(columnName = LTaskContract.Contacts, dataType = DataType.STRING)
    private String mContacts;

    @DatabaseField(columnName = LTaskContract.CreateTime, dataType = DataType.LONG)
    private long mCreateTime;

    @DatabaseField(columnName = LTaskContract.PerformTime, dataType = DataType.LONG)
    private long mPerformTime;

    @DatabaseField(columnName = LTaskContract.CompleteTime, dataType = DataType.LONG)
    private long mCompleteTime;

    @DatabaseField(columnName = LTaskContract.SeriesType, dataType = DataType.INTEGER)
    private int mSeriesType;

    @DatabaseField(columnName = LTaskContract.SeriesAfterType, dataType = DataType.INTEGER)
    private int mSeriesAfterType;

    @DatabaseField(columnName = LTaskContract.SeriesAfterCount, dataType = DataType.INTEGER)
    private int mSeriesAfterCount;

    @DatabaseField(columnName = LTaskContract.SeriesWeekCount, dataType = DataType.INTEGER)
    private int mSeriesWeekCount;

    @DatabaseField(columnName = LTaskContract.SeriesWeekMon, dataType = DataType.BOOLEAN)
    private boolean mSeriesWeekMon;

    @DatabaseField(columnName = LTaskContract.SeriesWeekTue, dataType = DataType.BOOLEAN)
    private boolean mSeriesWeekTue;

    @DatabaseField(columnName = LTaskContract.SeriesWeekWed, dataType = DataType.BOOLEAN)
    private boolean mSeriesWeekWed;

    @DatabaseField(columnName = LTaskContract.SeriesWeekThu, dataType = DataType.BOOLEAN)
    private boolean mSeriesWeekThu;

    @DatabaseField(columnName = LTaskContract.SeriesWeekFri, dataType = DataType.BOOLEAN)
    private boolean mSeriesWeekFri;

    @DatabaseField(columnName = LTaskContract.SeriesWeekSat, dataType = DataType.BOOLEAN)
    private boolean mSeriesWeekSat;

    @DatabaseField(columnName = LTaskContract.SeriesWeekSun, dataType = DataType.BOOLEAN)
    private boolean mSeriesWeekSun;

    @DatabaseField(columnName = LTaskContract.SeriesMonthType, dataType = DataType.INTEGER)
    private int mSeriesMonthType;

    @DatabaseField(columnName = LTaskContract.SeriesMonthCount, dataType = DataType.INTEGER)
    private int mSeriesMonthCount;

    @DatabaseField(columnName = LTaskContract.SeriesMonthDay, dataType = DataType.INTEGER)
    private int mSeriesMonthDay;

    @DatabaseField(columnName = LTaskContract.SeriesMonthWeekType, dataType = DataType.INTEGER)
    private int mSeriesMonthWeekType;

    @DatabaseField(columnName = LTaskContract.SeriesMonthDayOfWeek, dataType = DataType.INTEGER)
    private int mSeriesMonthDayOfWeek;

    @DatabaseField(columnName = LTaskContract.SeriesYearType, dataType = DataType.INTEGER)
    private int mSeriesYearType;

    @DatabaseField(columnName = LTaskContract.SeriesYearMonth, dataType = DataType.INTEGER)
    private int mSeriesYearMonth;

    @DatabaseField(columnName = LTaskContract.SeriesYearMonthDay, dataType = DataType.INTEGER)
    private int mSeriesYearMonthDay;

    @DatabaseField(columnName = LTaskContract.SeriesYearWeekType, dataType = DataType.INTEGER)
    private int mSeriesYearWeekType;

    @DatabaseField(columnName = LTaskContract.SeriesYearDayOfWeek, dataType = DataType.INTEGER)
    private int mSeriesYearDayOfWeek;

    @DatabaseField(columnName = LTaskContract.SeriesEnd, dataType = DataType.LONG)
    private long mSeriesEnd;

    @DatabaseField(columnName = LTaskContract.UsnEntity, dataType = DataType.INTEGER)
    private int mUsnEntity;

    @DatabaseField(columnName = LTaskContract.UsnFieldUidParent, dataType = DataType.INTEGER)
    private int mUsnFieldUidParent;

    @DatabaseField(columnName = LTaskContract.UsnFieldEmailPerformer, dataType = DataType.INTEGER)
    private int mUsnFieldEmailPerformer;

    @DatabaseField(columnName = LTaskContract.UsnFieldName, dataType = DataType.INTEGER)
    private int mUsnFieldName;

    @DatabaseField(columnName = LTaskContract.UsnFieldComment, dataType = DataType.INTEGER)
    private int mUsnFieldComment;

    @DatabaseField(columnName = LTaskContract.UsnFieldStatus, dataType = DataType.INTEGER)
    private int mUsnFieldStatus;

    @DatabaseField(columnName = LTaskContract.UsnFieldOrder, dataType = DataType.INTEGER)
    private int mUsnFieldOrder;

    @DatabaseField(columnName = LTaskContract.UsnFieldUidProject, dataType = DataType.INTEGER)
    private int mUsnFieldUidProject;

    @DatabaseField(columnName = LTaskContract.UsnFieldUidMarker, dataType = DataType.INTEGER)
    private int mUsnFieldUidMarker;

    @DatabaseField(columnName = LTaskContract.UsnFieldTerm, dataType = DataType.INTEGER)
    private int mUsnFieldTerm;

    @DatabaseField(columnName = LTaskContract.UsnFieldReaded, dataType = DataType.INTEGER)
    private int mUsnFieldReaded;

    @DatabaseField(columnName = LTaskContract.UsnFieldCollapsed, dataType = DataType.INTEGER)
    private int mUsnFieldCollapsed;

    @DatabaseField(columnName = LTaskContract.UsnFieldCustomerOrder, dataType = DataType.INTEGER)
    private int mUsnFieldCustomerOrder;

    @DatabaseField(columnName = LTaskContract.UsnFieldCustomerTerm, dataType = DataType.INTEGER)
    private int mUsnFieldCustomerTerm;

    @DatabaseField(columnName = LTaskContract.UsnFieldCategories, dataType = DataType.INTEGER)
    private int mUsnFieldCategories;

    @DatabaseField(columnName = LTaskContract.UsnFieldContacts, dataType = DataType.INTEGER)
    private int mUsnFieldContacts;

    @DatabaseField(columnName = LTaskContract.UsnFieldCreatetime, dataType = DataType.INTEGER)
    private int mUsnFieldCreatetime;

    @DatabaseField(columnName = LTaskContract.UsnFieldPerformtime, dataType = DataType.INTEGER)
    private int mUsnFieldPerformtime;

    @DatabaseField(columnName = LTaskContract.UsnFieldCompletetime, dataType = DataType.INTEGER)
    private int mUsnFieldCompletetime;

    @DatabaseField(columnName = LTaskContract.UsnFieldSeries, dataType = DataType.INTEGER)
    private int mUsnFieldSeries;

    @DatabaseField(columnName = LTaskContract.PerformerReaded, dataType = DataType.BOOLEAN)
    private boolean mPerformerReaded;

    @DatabaseField(columnName = LTaskContract.UsnPerformerReaded, dataType = DataType.INTEGER)
    private int mUsnFieldPerformerReaded;

    @DatabaseField(columnName = LTaskContract.OrderNew, dataType = DataType.DOUBLE)
    private double mOrderNew;

    @DatabaseField(columnName = LTaskContract.UsnOrderNew, dataType = DataType.INTEGER)
    private int mUsnOrderNew;

    @DatabaseField(columnName = LTaskContract.UserOrder, dataType = DataType.INTEGER)
    private int mUserOrder;

    @DatabaseField(columnName = LTaskContract.MarkerOrder, dataType = DataType.INTEGER)
    private int mMarkerOrder;

    @DatabaseField(columnName = LTaskContract.IsUseTerm, dataType = DataType.INTEGER)
    private int mIsUseTerm;

    @DatabaseField(columnName = LTaskContract.IsUseTermCustomer, dataType = DataType.INTEGER)
    private int mIsUseTermCustomer;

    @DatabaseField(columnName = LTaskContract.Time, dataType = DataType.INTEGER)
    private int mTime;

    @DatabaseField(columnName = LTaskContract.Plan, dataType = DataType.INTEGER)
    private int mPlan;

    @DatabaseField(columnName = LTaskContract.InWorkTime, dataType = DataType.LONG)
    private long mInWorkTime;

    @DatabaseField(columnName = LTaskContract.UsnTime, dataType = DataType.INTEGER)
    private int mUsnTime;

    @DatabaseField(columnName = LTaskContract.UsnPlan, dataType = DataType.INTEGER)
    private int mUsnPlan;

    @DatabaseField(columnName = LTaskContract.UsnInWorkTime, dataType = DataType.INTEGER)
    private int mUsnInWorkTime;

    @DatabaseField(columnName = LTaskContract.Focus, dataType = DataType.BOOLEAN)
    private boolean mFocus;

    @DatabaseField(columnName = LTaskContract.UsnFieldFocus, dataType = DataType.INTEGER)
    private int mUsnFieldFocus;

    @DatabaseField(columnName = LTaskContract.Emails, dataType = DataType.STRING)
    private String mEmails;

    @DatabaseField(columnName = LTaskContract.UsnFieldListMembers, dataType = DataType.INTEGER)
    private int mUsnFieldListMembers;

    private static int[] sColumns;

    public LTask() {}

    public LTask(Cursor cursor) {
        fillFromCursor(cursor);
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
    public long getUsn() {
        return 0;
    }

    @Override
    public String getUid() {
        return mUid;
    }

    @Override
    public int getUsnEntity() {
        return mUsnEntity;
    }

    @Override
    public String getLionName() {
        return LTaskContract.TABLE_NAME;
    }

    @Override
    public Uri getContentUri() {
        return LTaskContract.CONTENT_URI;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        if (cv == null) {
            cv = new ContentValues(78);

        } else {
            cv.clear();
        }

        cv.put(LTaskContract.Orders, getOrder());
        cv.put(LTaskContract.Time, getTime());
        cv.put(LTaskContract.Plan, getPlan());
        cv.put(LTaskContract.InWorkTime, getInWorkTime());
        cv.put(LTaskContract.UsnTime, getUsnTime());
        cv.put(LTaskContract.UsnPlan, getUsnPlan());
        cv.put(LTaskContract.UsnInWorkTime, getUsnInWorkTime());
        cv.put(LTaskContract.Uid, getUid());
        cv.put(LTaskContract.UIDParent, getUIDParent());
        cv.put(LTaskContract.Collapsed, getCollapsed());
        cv.put(LTaskContract.Name, getName());
        cv.put(LTaskContract.Comment, getComment());
        cv.put(LTaskContract.Status, getStatus());
        cv.put(LTaskContract.TermBegin, getTermBegin());
        cv.put(LTaskContract.TermEnd, getTermEnd());
        cv.put(LTaskContract.EmailPerformer, getEmailPerformer());
        cv.put(LTaskContract.UidProject, getUidProject());
        cv.put(LTaskContract.UidMarker, getUidMarker());
        cv.put(LTaskContract.Readed, getReaded());
        cv.put(LTaskContract.OrderCustomer, getOrderCustomer());
        cv.put(LTaskContract.OrderNew, getOrderNew());
        cv.put(LTaskContract.UserOrder, getUserOrder());
        cv.put(LTaskContract.MarkerOrder, getMarkerOrder());
        cv.put(LTaskContract.IsUseTerm, getIsUseTerm());
        cv.put(LTaskContract.IsUseTermCustomer, getIsUseTermCustomer());
        cv.put(LTaskContract.TermBeginCustomer, getTermBeginCustomer());
        cv.put(LTaskContract.TermEndCustomer, getTermEndCustomer());
        cv.put(LTaskContract.PerformerReaded, getPerformerReaded());
        cv.put(LTaskContract.EmailCustomer, getEmailCustomer());
        cv.put(LTaskContract.Categories, getCategories());
        cv.put(LTaskContract.Contacts, getContacts());
        cv.put(LTaskContract.CreateTime, getCreateTime());
        cv.put(LTaskContract.PerformTime, getPerformTime());
        cv.put(LTaskContract.CompleteTime, getCompleteTime());
        cv.put(LTaskContract.SeriesType, getSeriesType());
        cv.put(LTaskContract.SeriesAfterType, getSeriesAfterType());
        cv.put(LTaskContract.SeriesAfterCount, getSeriesAfterCount());
        cv.put(LTaskContract.SeriesWeekCount, getSeriesWeekCount());
        cv.put(LTaskContract.SeriesWeekMon, getSeriesWeekMon());
        cv.put(LTaskContract.SeriesWeekTue, getSeriesWeekTue());
        cv.put(LTaskContract.SeriesWeekWed, getSeriesWeekWed());
        cv.put(LTaskContract.SeriesWeekThu, getSeriesWeekThu());
        cv.put(LTaskContract.SeriesWeekFri, getSeriesWeekFri());
        cv.put(LTaskContract.SeriesWeekSat, getSeriesWeekSat());
        cv.put(LTaskContract.SeriesWeekSun, getSeriesWeekSun());
        cv.put(LTaskContract.SeriesMonthType, getSeriesMonthType());
        cv.put(LTaskContract.SeriesMonthCount, getSeriesMonthCount());
        cv.put(LTaskContract.SeriesMonthDay, getSeriesMonthDay());
        cv.put(LTaskContract.SeriesMonthWeekType, getSeriesMonthWeekType());
        cv.put(LTaskContract.SeriesMonthDayOfWeek, getSeriesMonthDayOfWeek());
        cv.put(LTaskContract.SeriesYearType, getSeriesYearType());
        cv.put(LTaskContract.SeriesYearMonth, getSeriesYearMonth());
        cv.put(LTaskContract.SeriesYearMonthDay, getSeriesYearMonthDay());
        cv.put(LTaskContract.SeriesYearWeekType, getSeriesYearWeekType());
        cv.put(LTaskContract.SeriesYearDayOfWeek, getSeriesYearDayOfWeek());
        cv.put(LTaskContract.SeriesEnd, getSeriesEnd());
        cv.put(LTaskContract.UsnPerformerReaded, getUsnFieldPerformerReaded());
        cv.put(LTaskContract.UsnEntity, getUsnEntity());
        cv.put(LTaskContract.UsnFieldUidParent, getUsnFieldUidParent());
        cv.put(LTaskContract.UsnFieldEmailPerformer, getUsnFieldEmailPerformer());
        cv.put(LTaskContract.UsnFieldName, getUsnFieldName());
        cv.put(LTaskContract.UsnFieldComment, getUsnFieldComment());
        cv.put(LTaskContract.UsnFieldStatus, getUsnFieldStatus());
        cv.put(LTaskContract.UsnFieldOrder, getUsnFieldOrder());
        cv.put(LTaskContract.UsnFieldUidProject, getUsnFieldUidProject());
        cv.put(LTaskContract.UsnFieldUidMarker, getUsnFieldUidMarker());
        cv.put(LTaskContract.UsnFieldTerm, getUsnFieldTerm());
        cv.put(LTaskContract.UsnFieldReaded, getUsnFieldReaded());
        cv.put(LTaskContract.UsnFieldCollapsed, getUsnFieldCollapsed());
        cv.put(LTaskContract.UsnFieldCustomerOrder, getUsnFieldCustomerOrder());
        cv.put(LTaskContract.UsnOrderNew, getUsnOrderNew());
        cv.put(LTaskContract.UsnFieldCustomerTerm, getUsnFieldCustomerTerm());
        cv.put(LTaskContract.UsnFieldCategories, getUsnFieldCategories());
        cv.put(LTaskContract.UsnFieldContacts, getUsnFieldContacts());
        cv.put(LTaskContract.UsnFieldCreatetime, getUsnFieldCreatetime());
        cv.put(LTaskContract.UsnFieldPerformtime, getUsnFieldPerformtime());
        cv.put(LTaskContract.UsnFieldCompletetime, getUsnFieldCompletetime());
        cv.put(LTaskContract.UsnFieldSeries, getUsnFieldSeries());
        cv.put(LTaskContract.Focus, getFocus());
        cv.put(LTaskContract.UsnFieldFocus, getUsnFieldFocus());
        cv.put(LTaskContract.Emails, getEmails());
        cv.put(LTaskContract.UsnFieldListMembers, getUsnFieldListMembers());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        int count = 0;

        if (sColumns == null) {
            sColumns = new int[cursor.getColumnCount()];
            for (String s : cursor.getColumnNames()) {
                sColumns[count++] = cursor.getColumnIndex(s);
            }
        }

        count = 0;
        for (String key : cursor.getColumnNames()) {
            fillKeyValue(key, cursor.getString(sColumns[count++]));
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        final XmlSoap.Builder soap = new XmlSoap.Builder(sb);
        soap.addStart(getLionName());

        soap.addString(LTaskContract.Uid, getUid());
        soap.addString(LTaskContract.UIDParent, getUIDParent());
        soap.addNumeric(LTaskContract.Order, getOrder());
        soap.addBoolean(LTaskContract.Collapsed, getCollapsed());
        soap.addString(LTaskContract.Name, getName());
        soap.addString(LTaskContract.Comment, getComment());
        soap.addNumeric(LTaskContract.Status, getStatus());
        soap.addDate(LTaskContract.TermBegin, getTermBegin(), true);
        soap.addDate(LTaskContract.TermEnd, getTermEnd(), false);
        soap.addString(LTaskContract.EmailPerformer, getEmailPerformer());
        soap.addString(LTaskContract.UidProject, getUidProject());
        soap.addString(LTaskContract.UidMarker, getUidMarker());
        soap.addBoolean(LTaskContract.Readed, getReaded());
        soap.addNumeric(LTaskContract.OrderCustomer, getOrderCustomer());
        soap.addDate(LTaskContract.TermBeginCustomer, getTermBeginCustomer(), true);
        soap.addDate(LTaskContract.TermEndCustomer, getTermEndCustomer(), false);
        soap.addString(LTaskContract.EmailCustomer, getEmailCustomer());
        soap.addString(LTaskContract.Categories, getCategories());
        soap.addString(LTaskContract.Contacts, getContacts());
        soap.addDate(LTaskContract.CreateTime, getCreateTime());
        soap.addDate(LTaskContract.PerformTime, getPerformTime());
        soap.addDate(LTaskContract.CompleteTime, getCompleteTime());
        soap.addNumeric(LTaskContract.SeriesType, getSeriesType());
        soap.addNumeric(LTaskContract.SeriesAfterType, getSeriesAfterType());
        soap.addNumeric(LTaskContract.SeriesAfterCount, getSeriesAfterCount());
        soap.addNumeric(LTaskContract.SeriesWeekCount, getSeriesWeekCount());
        soap.addBoolean(LTaskContract.SeriesWeekMon, getSeriesWeekMon());
        soap.addBoolean(LTaskContract.SeriesWeekTue, getSeriesWeekTue());
        soap.addBoolean(LTaskContract.SeriesWeekWed, getSeriesWeekWed());
        soap.addBoolean(LTaskContract.SeriesWeekThu, getSeriesWeekThu());
        soap.addBoolean(LTaskContract.SeriesWeekFri, getSeriesWeekFri());
        soap.addBoolean(LTaskContract.SeriesWeekSat, getSeriesWeekSat());
        soap.addBoolean(LTaskContract.SeriesWeekSun, getSeriesWeekSun());
        soap.addNumeric(LTaskContract.SeriesMonthType, getSeriesMonthType());
        soap.addNumeric(LTaskContract.SeriesMonthCount, getSeriesMonthCount());
        soap.addNumeric(LTaskContract.SeriesMonthDay, getSeriesMonthDay());
        soap.addNumeric(LTaskContract.SeriesMonthWeekType, getSeriesMonthWeekType());
        soap.addNumeric(LTaskContract.SeriesMonthDayOfWeek, getSeriesMonthDayOfWeek());
        soap.addNumeric(LTaskContract.SeriesYearType, getSeriesYearType());
        soap.addNumeric(LTaskContract.SeriesYearMonth, getSeriesYearMonth());
        soap.addNumeric(LTaskContract.SeriesYearMonthDay, getSeriesYearMonthDay());
        soap.addNumeric(LTaskContract.SeriesYearWeekType, getSeriesYearWeekType());
        soap.addNumeric(LTaskContract.SeriesYearDayOfWeek, getSeriesYearDayOfWeek());
        soap.addDate(LTaskContract.SeriesEnd, getSeriesEnd());
        soap.addNumeric(LTaskContract.UsnEntity, getUsnEntity());
        soap.addNumeric(LTaskContract.UsnFieldUidParent, getUsnFieldUidParent());
        soap.addNumeric(LTaskContract.UsnFieldEmailPerformer, getUsnFieldEmailPerformer());
        soap.addNumeric(LTaskContract.UsnFieldName, getUsnFieldName());
        soap.addNumeric(LTaskContract.UsnFieldComment, getUsnFieldComment());
        soap.addNumeric(LTaskContract.UsnFieldStatus, getUsnFieldStatus());
        soap.addNumeric(LTaskContract.UsnFieldOrder, getUsnFieldOrder());
        soap.addNumeric(LTaskContract.UsnFieldUidProject, getUsnFieldUidProject());
        soap.addNumeric(LTaskContract.UsnFieldUidMarker, getUsnFieldUidMarker());
        soap.addNumeric(LTaskContract.UsnFieldTerm, getUsnFieldTerm());
        soap.addNumeric(LTaskContract.UsnFieldReaded, getUsnFieldReaded());
        soap.addNumeric(LTaskContract.UsnFieldCollapsed, getUsnFieldCollapsed());
        soap.addNumeric(LTaskContract.UsnFieldCustomerOrder, getUsnFieldCustomerOrder());
        soap.addNumeric(LTaskContract.UsnFieldCustomerTerm, getUsnFieldCustomerTerm());
        soap.addNumeric(LTaskContract.UsnFieldCategories, getUsnFieldCategories());
        soap.addNumeric(LTaskContract.UsnFieldContacts, getUsnFieldContacts());
        soap.addNumeric(LTaskContract.UsnFieldCreatetime, getUsnFieldCreatetime());
        soap.addNumeric(LTaskContract.UsnFieldPerformtime, getUsnFieldPerformtime());
        soap.addNumeric(LTaskContract.UsnFieldCompletetime, getUsnFieldCompletetime());
        soap.addNumeric(LTaskContract.UsnFieldSeries, getUsnFieldSeries());

        soap.addNumeric(LTaskContract.OrderNew, getOrderNew());
        soap.addNumeric(LTaskContract.UsnOrderNew, getUsnOrderNew());

        soap.addBoolean(LTaskContract.PerformerReaded, getPerformerReaded());
        soap.addNumeric(LTaskContract.UsnPerformerReaded, getUsnFieldPerformerReaded());

        soap.addNumeric(LTaskContract.Time, getTime());
        soap.addNumeric(LTaskContract.Plan, getPlan());
        soap.addDate(LTaskContract.InWorkTime, getInWorkTime());

        soap.addNumeric(LTaskContract.UsnTime, getUsnTime());
        soap.addNumeric(LTaskContract.UsnPlan, getUsnPlan());
        soap.addNumeric(LTaskContract.UsnInWorkTime, getUsnInWorkTime());
        soap.addBoolean(LTaskContract.Focus, getFocus());
        soap.addNumeric(LTaskContract.UsnFieldFocus, getUsnFieldFocus());
        soap.addString(LTaskContract.Emails, getEmails());
        soap.addNumeric(LTaskContract.UsnFieldListMembers, getUsnFieldListMembers());

        soap.addEnd(getLionName());
        sb = soap.build();
        //return soap.build();
    }

    @Override
    public String getServerClass() {
        return null;
    }

    @Override
    public void fillKeyValue(String key, String value) {
        switch (key) {
        case LTaskContract._ID:
            setId(Integer.parseInt(value));
            return;

        case LTaskContract.Order:
        case LTaskContract.Orders:
            setOrder(Integer.parseInt(value));
            return;

        case LTaskContract.Uid:
            setUid(value.toUpperCase());
            return;

        case LTaskContract.UIDParent:
            setUIDParent(value);
            return;

        case LTaskContract.Collapsed:
            setCollapsed(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.Name:
            setName(value);
            return;

        case LTaskContract.Comment:
            setComment(value);
            return;

        case LTaskContract.Status:
            setStatus(Integer.parseInt(value));
            return;

        case LTaskContract.TermBegin:
            setTermBegin(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.TermEnd:
            setTermEnd(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.EmailPerformer:
            setEmailPerformer(value);
            return;

        case LTaskContract.UidProject:
            setUidProject(value);
            return;

        case LTaskContract.UidMarker:
            setUidMarker(value);
            return;

        case LTaskContract.Readed:
            setReaded(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.PerformerReaded:
            setPerformerReaded(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.OrderCustomer:
            setOrderCustomer(Integer.parseInt(value));
            return;

        case LTaskContract.OrderNew:
            setOrderNew(Double.parseDouble(value));
            return;

        case LTaskContract.UserOrder:
            setUserOrder(Integer.parseInt(value));
            return;

        case LTaskContract.MarkerOrder:
            setMarkerOrder(parseWithDefault(value, 0));
            return;

        case LTaskContract.IsUseTerm:
            setIsUseTerm(Integer.parseInt(value));
            return;

        case LTaskContract.IsUseTermCustomer:
            setIsUseTermCustomer(Integer.parseInt(value));
            return;

        case LTaskContract.UsnOrderNew:
            setUsnOrderNew(Integer.parseInt(value));
            return;

        case LTaskContract.TermBeginCustomer:
            setTermBeginCustomer(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.TermEndCustomer:
            setTermEndCustomer(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.EmailCustomer:
            setEmailCustomer(value);
            return;

        case LTaskContract.Categories:
            setCategories(value);
            return;

        case LTaskContract.Contacts:
            setContacts(value);
            return;

        case LTaskContract.CreateTime:
            setCreateTime(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.PerformTime:
            setPerformTime(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.CompleteTime:
            setCompleteTime(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.SeriesType:
            setSeriesType(parseWithDefault(value, 0));
            return;

        case LTaskContract.SeriesAfterType:
            setSeriesAfterType(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesAfterCount:
            setSeriesAfterCount(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesWeekCount:
            setSeriesWeekCount(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesWeekMon:
            setSeriesWeekMon(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.SeriesWeekTue:
            setSeriesWeekTue(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.SeriesWeekWed:
            setSeriesWeekWed(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.SeriesWeekThu:
            setSeriesWeekThu(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.SeriesWeekFri:
            setSeriesWeekFri(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.SeriesWeekSat:
            setSeriesWeekSat(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.SeriesWeekSun:
            setSeriesWeekSun(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.SeriesMonthType:
            setSeriesMonthType(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesMonthCount:
            setSeriesMonthCount(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesMonthDay:
            setSeriesMonthDay(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesMonthWeekType:
            setSeriesMonthWeekType(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesMonthDayOfWeek:
            setSeriesMonthDayOfWeek(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesYearType:
            setSeriesYearType(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesYearMonth:
            setSeriesYearMonth(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesYearMonthDay:
            setSeriesYearMonthDay(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesYearWeekType:
            setSeriesYearWeekType(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesYearDayOfWeek:
            setSeriesYearDayOfWeek(parseWithDefault(value, 1));
            return;

        case LTaskContract.SeriesEnd:
            setSeriesEnd(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.UsnEntity:
            setUsnEntity(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldUidParent:
            setUsnFieldUidParent(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldEmailPerformer:
            setUsnFieldEmailPerformer(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldName:
            setUsnFieldName(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldComment:
            setUsnFieldComment(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldStatus:
            setUsnFieldStatus(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldOrder:
            setUsnFieldOrder(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldUidProject:
            setUsnFieldUidProject(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldUidMarker:
            setUsnFieldUidMarker(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldTerm:
            setUsnFieldTerm(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldReaded:
            setUsnFieldReaded(Integer.parseInt(value));
            return;

        case LTaskContract.UsnPerformerReaded:
            setUsnFieldPerformerReaded(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldCollapsed:
            setUsnFieldCollapsed(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldCustomerOrder:
            setUsnFieldCustomerOrder(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldCustomerTerm:
            setUsnFieldCustomerTerm(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldCategories:
            setUsnFieldCategories(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldContacts:
            setUsnFieldContacts(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldCreatetime:
            setUsnFieldCreatetime(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldPerformtime:
            setUsnFieldPerformtime(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldCompletetime:
            setUsnFieldCompletetime(Integer.parseInt(value));
            return;

        case LTaskContract.UsnFieldSeries:
            setUsnFieldSeries(Integer.parseInt(value));
            return;

        case LTaskContract.UsnTime:
            setUsnTime(Integer.parseInt(value));
            return;

        case LTaskContract.UsnPlan:
            setUsnPlan(Integer.parseInt(value));
            return;

        case LTaskContract.UsnInWorkTime:
            setUsnInWorkTime(Integer.parseInt(value));
            return;

        case LTaskContract.Time:
            setTime(Integer.parseInt(value));
            return;

        case LTaskContract.Plan:
            setPlan(Integer.parseInt(value));
            return;


        case LTaskContract.InWorkTime:
            setInWorkTime(XmlSoap.parseDateMillis(value));
            return;

        case LTaskContract.Focus:
            setFocus(XmlSoap.equalsOne(value));
            return;

        case LTaskContract.UsnFieldFocus:
            setUsnFieldFocus(Integer.parseInt(value));
            return;

        case LTaskContract.Emails:
            setEmails(value);
            return;

        case LTaskContract.UsnFieldListMembers:
            setUsnFieldListMembers(Integer.parseInt(value));
            return;

        default:
            return;
        }
    }

    @Override
    public ContentValues getDifference(LTask entity) {
        final ContentValues cv = new ContentValues();

        cv.put(LTaskContract.UsnEntity, entity.getUsnEntity());
        cv.put(LTaskContract.EmailCustomer, entity.getEmailCustomer());

        if (getUsnFieldUidParent() < entity.getUsnFieldUidParent()) {
            cv.put(LTaskContract.UsnFieldUidParent, entity.getUsnFieldUidParent());
            cv.put(LTaskContract.UIDParent, entity.getUIDParent());

        } else if (getUsnFieldUidParent() > entity.getUsnFieldUidParent()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldEmailPerformer() < entity.getUsnFieldEmailPerformer()) {
            cv.put(LTaskContract.UsnFieldEmailPerformer, entity.getUsnFieldEmailPerformer());
            cv.put(LTaskContract.EmailPerformer, entity.getEmailPerformer());

        } else if (getUsnFieldEmailPerformer() > entity.getUsnFieldEmailPerformer()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldName() < entity.getUsnFieldName()) {
            cv.put(LTaskContract.UsnFieldName, entity.getUsnFieldName());
            cv.put(LTaskContract.Name, entity.getName());

        } else if (getUsnFieldName() > entity.getUsnFieldName()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldComment() < entity.getUsnFieldComment()) {
            cv.put(LTaskContract.UsnFieldComment, entity.getUsnFieldComment());
            cv.put(LTaskContract.Comment, entity.getComment());

        } else if (getUsnFieldComment() > entity.getUsnFieldComment()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldStatus() < entity.getUsnFieldStatus()) {
            cv.put(LTaskContract.UsnFieldStatus, entity.getUsnFieldStatus());
            cv.put(LTaskContract.Status, entity.getStatus());

        } else if (getUsnFieldStatus() > entity.getUsnFieldStatus()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldOrder() < entity.getUsnFieldOrder()) {
            cv.put(LTaskContract.UsnFieldOrder, entity.getUsnFieldOrder());
            cv.put(LTaskContract.Orders, entity.getOrder());

        } else if (getUsnFieldOrder() > entity.getUsnFieldOrder()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldUidProject() < entity.getUsnFieldUidProject()) {
            cv.put(LTaskContract.UsnFieldUidProject, entity.getUsnFieldUidProject());
            cv.put(LTaskContract.UidProject, entity.getUidProject());

        } else if (getUsnFieldUidProject() > entity.getUsnFieldUidProject()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldUidMarker() < entity.getUsnFieldUidMarker()) {
            Application apk = new Application();
            cv.put(LTaskContract.UsnFieldUidMarker, entity.getUsnFieldUidMarker());
            cv.put(LTaskContract.UidMarker, entity.getUidMarker());
            cv.put(LTaskContract.MarkerOrder, Marker.getMarkerOrderFromLowerUid(apk, entity.getUidMarker().toLowerCase()));

        } else if (getUsnFieldUidMarker() > entity.getUsnFieldUidMarker()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldTerm() < entity.getUsnFieldTerm()) {
            cv.put(LTaskContract.UsnFieldTerm, entity.getUsnFieldTerm());
            cv.put(LTaskContract.TermBegin, entity.getTermBegin());
            cv.put(LTaskContract.TermEnd, entity.getTermEnd());
            cv.put(LTaskContract.IsUseTerm, entity.getTermBegin() == 0 ? LTask.MY_TASK_NOT_USE_TERM_DEFAULT : LTask.MY_TASK_IS_USE_TERM_DEFAULT);

        } else if (getUsnFieldTerm() > entity.getUsnFieldTerm()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldReaded() < entity.getUsnFieldReaded()) {
            cv.put(LTaskContract.UsnFieldReaded, entity.getUsnFieldReaded());
            cv.put(LTaskContract.Readed, entity.getReaded());

        } else if (getUsnFieldReaded() > entity.getUsnFieldReaded()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldPerformerReaded() < entity.getUsnFieldPerformerReaded()) {
            cv.put(LTaskContract.UsnPerformerReaded, entity.getUsnFieldPerformerReaded());
            cv.put(LTaskContract.PerformerReaded, entity.getPerformerReaded());

        } else if (getUsnFieldPerformerReaded() > entity.getUsnFieldPerformerReaded()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCollapsed() < entity.getUsnFieldCollapsed()) {
            cv.put(LTaskContract.UsnFieldCollapsed, entity.getUsnFieldCollapsed());
            cv.put(LTaskContract.Collapsed, entity.getCollapsed());

        } else if (getUsnFieldCollapsed() > entity.getUsnFieldCollapsed()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCustomerOrder() < entity.getUsnFieldCustomerOrder()) {
            cv.put(LTaskContract.UsnFieldCustomerOrder, entity.getUsnFieldCustomerOrder());
            cv.put(LTaskContract.OrderCustomer, entity.getOrderCustomer());

        } else if (getUsnFieldCustomerOrder() > entity.getUsnFieldCustomerOrder()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnOrderNew() < entity.getUsnOrderNew()) {
            cv.put(LTaskContract.UsnOrderNew, entity.getUsnOrderNew());
            cv.put(LTaskContract.OrderNew, entity.getOrderNew());

        } else if (getUsnOrderNew() > entity.getUsnOrderNew()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCustomerTerm() < entity.getUsnFieldCustomerTerm()) {
            cv.put(LTaskContract.UsnFieldCustomerTerm, entity.getUsnFieldCustomerTerm());
            cv.put(LTaskContract.TermBeginCustomer, entity.getTermBeginCustomer());
            cv.put(LTaskContract.TermEndCustomer, entity.getTermEndCustomer());
            cv.put(LTaskContract.IsUseTermCustomer, entity.getTermBeginCustomer() == 0 ?LTask.MY_TASK_NOT_USE_TERM_DEFAULT : LTask.MY_TASK_IS_USE_TERM_DEFAULT);

        } else if (getUsnFieldCustomerTerm() > entity.getUsnFieldCustomerTerm()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCategories() < entity.getUsnFieldCategories()) {
            cv.put(LTaskContract.UsnFieldCategories, entity.getUsnFieldCategories());
            cv.put(LTaskContract.Categories, entity.getCategories());

        } else if (getUsnFieldCategories() > entity.getUsnFieldCategories()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldContacts() < entity.getUsnFieldContacts()) {
            cv.put(LTaskContract.UsnFieldContacts, entity.getUsnFieldContacts());
            cv.put(LTaskContract.Contacts, entity.getContacts());

        } else if (getUsnFieldContacts() > entity.getUsnFieldContacts()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCreatetime() < entity.getUsnFieldCreatetime()) {
            cv.put(LTaskContract.UsnFieldCreatetime, entity.getUsnFieldCreatetime());
            cv.put(LTaskContract.CreateTime, entity.getCreateTime());

        } else if (getUsnFieldCreatetime() > entity.getUsnFieldCreatetime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldPerformtime() < entity.getUsnFieldPerformtime()) {
            cv.put(LTaskContract.UsnFieldPerformtime, entity.getUsnFieldPerformtime());
            cv.put(LTaskContract.PerformTime, entity.getPerformTime());

        } else if (getUsnFieldPerformtime() > entity.getUsnFieldPerformtime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCompletetime() < entity.getUsnFieldCompletetime()) {
            cv.put(LTaskContract.UsnFieldCompletetime, entity.getUsnFieldCompletetime());
            cv.put(LTaskContract.CompleteTime, entity.getCompleteTime());

        } else if (getUsnFieldCompletetime() > entity.getUsnFieldCompletetime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnTime() < entity.getUsnTime()) {
            cv.put(LTaskContract.UsnTime, entity.getUsnTime());
            cv.put(LTaskContract.Time, entity.getTime());

        } else if (getUsnTime() > entity.getUsnTime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnPlan() < entity.getUsnPlan()) {
            cv.put(LTaskContract.UsnPlan, entity.getUsnPlan());
            cv.put(LTaskContract.Plan, entity.getPlan());

        } else if (getUsnPlan() > entity.getUsnPlan()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnInWorkTime() < entity.getUsnInWorkTime()) {
            cv.put(LTaskContract.UsnInWorkTime, entity.getUsnInWorkTime());
            cv.put(LTaskContract.InWorkTime, entity.getInWorkTime());

        } else if (getUsnInWorkTime() > entity.getUsnInWorkTime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldFocus() < entity.getUsnFieldFocus()) {
            cv.put(LTaskContract.UsnFieldFocus, entity.getUsnFieldFocus());
            cv.put(LTaskContract.Focus, entity.getFocus());

        } else if (getUsnFieldFocus() > entity.getUsnFieldFocus()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if(getUsnFieldListMembers() < entity.getUsnFieldListMembers()){
            cv.put(LTaskContract.UsnFieldListMembers, entity.getUsnFieldListMembers());
            cv.put(LTaskContract.Emails, entity.getEmails());
        }else if(getUsnFieldListMembers() > entity.getUsnFieldListMembers())
            cv.put(LTaskContract.UsnEntity, 0);

        if (getUsnFieldSeries() < entity.getUsnFieldSeries()) {
            cv.put(LTaskContract.UsnFieldSeries, entity.getUsnFieldSeries());
            cv.put(LTaskContract.SeriesType, entity.getSeriesType());
            cv.put(LTaskContract.SeriesAfterType, entity.getSeriesAfterType());
            cv.put(LTaskContract.SeriesAfterCount, entity.getSeriesAfterCount());
            cv.put(LTaskContract.SeriesWeekCount, entity.getSeriesWeekCount());
            cv.put(LTaskContract.SeriesWeekMon, entity.getSeriesWeekMon());
            cv.put(LTaskContract.SeriesWeekTue, entity.getSeriesWeekTue());
            cv.put(LTaskContract.SeriesWeekWed, entity.getSeriesWeekWed());
            cv.put(LTaskContract.SeriesWeekThu, entity.getSeriesWeekThu());
            cv.put(LTaskContract.SeriesWeekFri, entity.getSeriesWeekFri());
            cv.put(LTaskContract.SeriesWeekSat, entity.getSeriesWeekSat());
            cv.put(LTaskContract.SeriesWeekSun, entity.getSeriesWeekSun());
            cv.put(LTaskContract.SeriesMonthType, entity.getSeriesMonthType());
            cv.put(LTaskContract.SeriesMonthCount, entity.getSeriesMonthCount());
            cv.put(LTaskContract.SeriesMonthDay, entity.getSeriesMonthDay());
            cv.put(LTaskContract.SeriesMonthWeekType, entity.getSeriesMonthWeekType());
            cv.put(LTaskContract.SeriesMonthDayOfWeek, entity.getSeriesMonthDayOfWeek());
            cv.put(LTaskContract.SeriesYearType, entity.getSeriesYearType());
            cv.put(LTaskContract.SeriesYearMonth, entity.getSeriesYearMonth());
            cv.put(LTaskContract.SeriesYearMonthDay, entity.getSeriesYearMonthDay());
            cv.put(LTaskContract.SeriesYearWeekType, entity.getSeriesYearWeekType());
            cv.put(LTaskContract.SeriesYearDayOfWeek, entity.getSeriesYearDayOfWeek());
            cv.put(LTaskContract.SeriesEnd, entity.getSeriesEnd());

        } else if (getUsnFieldSeries() > entity.getUsnFieldSeries()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        return cv;
    }

    public ContentValues getDifferencesInTasks(LTask entity) {
        final ContentValues cv = new ContentValues();

        //cv.put(LTaskContract.UsnEntity, entity.getUsnEntity());
        //cv.put(LTaskContract.EmailCustomer, entity.getEmailCustomer());

        if (getUsnFieldUidParent() < entity.getUsnFieldUidParent()) {
            cv.put(LTaskContract.UsnFieldUidParent, entity.getUsnFieldUidParent());
            cv.put(LTaskContract.UIDParent, entity.getUIDParent());

        } else if (getUsnFieldUidParent() > entity.getUsnFieldUidParent()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldEmailPerformer() < entity.getUsnFieldEmailPerformer()) {
            cv.put(LTaskContract.UsnFieldEmailPerformer, entity.getUsnFieldEmailPerformer());
            cv.put(LTaskContract.EmailPerformer, entity.getEmailPerformer());

        } else if (getUsnFieldEmailPerformer() > entity.getUsnFieldEmailPerformer()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldName() < entity.getUsnFieldName()) {
            cv.put(LTaskContract.UsnFieldName, entity.getUsnFieldName());
            cv.put(LTaskContract.Name, entity.getName());

        } else if (getUsnFieldName() > entity.getUsnFieldName()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldComment() < entity.getUsnFieldComment()) {
            cv.put(LTaskContract.UsnFieldComment, entity.getUsnFieldComment());
            cv.put(LTaskContract.Comment, entity.getComment());

        } else if (getUsnFieldComment() > entity.getUsnFieldComment()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldStatus() < entity.getUsnFieldStatus()) {
            cv.put(LTaskContract.UsnFieldStatus, entity.getUsnFieldStatus());
            cv.put(LTaskContract.Status, entity.getStatus());

        } else if (getUsnFieldStatus() > entity.getUsnFieldStatus()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldOrder() < entity.getUsnFieldOrder()) {
            cv.put(LTaskContract.UsnFieldOrder, entity.getUsnFieldOrder());
            cv.put(LTaskContract.Orders, entity.getOrder());

        } else if (getUsnFieldOrder() > entity.getUsnFieldOrder()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldUidProject() < entity.getUsnFieldUidProject()) {
            cv.put(LTaskContract.UsnFieldUidProject, entity.getUsnFieldUidProject());
            cv.put(LTaskContract.UidProject, entity.getUidProject());

        } else if (getUsnFieldUidProject() > entity.getUsnFieldUidProject()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldUidMarker() < entity.getUsnFieldUidMarker()) {
            Application apk = new Application();
            cv.put(LTaskContract.UsnFieldUidMarker, entity.getUsnFieldUidMarker());
            cv.put(LTaskContract.UidMarker, entity.getUidMarker());
            cv.put(LTaskContract.MarkerOrder, Marker.getMarkerOrderFromLowerUid(apk, entity.getUidMarker().toLowerCase()));

        } else if (getUsnFieldUidMarker() > entity.getUsnFieldUidMarker()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldTerm() < entity.getUsnFieldTerm()) {
            cv.put(LTaskContract.UsnFieldTerm, entity.getUsnFieldTerm());
            cv.put(LTaskContract.TermBegin, entity.getTermBegin());
            cv.put(LTaskContract.TermEnd, entity.getTermEnd());
            cv.put(LTaskContract.IsUseTerm, entity.getTermBegin() == 0 ? LTask.MY_TASK_NOT_USE_TERM_DEFAULT : LTask.MY_TASK_IS_USE_TERM_DEFAULT);

        } else if (getUsnFieldTerm() > entity.getUsnFieldTerm()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldReaded() < entity.getUsnFieldReaded()) {
            cv.put(LTaskContract.UsnFieldReaded, entity.getUsnFieldReaded());
            cv.put(LTaskContract.Readed, entity.getReaded());

        } else if (getUsnFieldReaded() > entity.getUsnFieldReaded()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldPerformerReaded() < entity.getUsnFieldPerformerReaded()) {
            cv.put(LTaskContract.UsnPerformerReaded, entity.getUsnFieldPerformerReaded());
            cv.put(LTaskContract.PerformerReaded, entity.getPerformerReaded());

        } else if (getUsnFieldPerformerReaded() > entity.getUsnFieldPerformerReaded()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCollapsed() < entity.getUsnFieldCollapsed()) {
            cv.put(LTaskContract.UsnFieldCollapsed, entity.getUsnFieldCollapsed());
            cv.put(LTaskContract.Collapsed, entity.getCollapsed());

        } else if (getUsnFieldCollapsed() > entity.getUsnFieldCollapsed()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCustomerOrder() < entity.getUsnFieldCustomerOrder()) {
            cv.put(LTaskContract.UsnFieldCustomerOrder, entity.getUsnFieldCustomerOrder());
            cv.put(LTaskContract.OrderCustomer, entity.getOrderCustomer());

        } else if (getUsnFieldCustomerOrder() > entity.getUsnFieldCustomerOrder()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnOrderNew() < entity.getUsnOrderNew()) {
            cv.put(LTaskContract.UsnOrderNew, entity.getUsnOrderNew());
            cv.put(LTaskContract.OrderNew, entity.getOrderNew());

        } else if (getUsnOrderNew() > entity.getUsnOrderNew()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCustomerTerm() < entity.getUsnFieldCustomerTerm()) {
            cv.put(LTaskContract.UsnFieldCustomerTerm, entity.getUsnFieldCustomerTerm());
            cv.put(LTaskContract.TermBeginCustomer, entity.getTermBeginCustomer());
            cv.put(LTaskContract.TermEndCustomer, entity.getTermEndCustomer());
            cv.put(LTaskContract.IsUseTermCustomer, entity.getTermBeginCustomer() == 0 ?LTask.MY_TASK_NOT_USE_TERM_DEFAULT : LTask.MY_TASK_IS_USE_TERM_DEFAULT);

        } else if (getUsnFieldCustomerTerm() > entity.getUsnFieldCustomerTerm()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCategories() < entity.getUsnFieldCategories()) {
            cv.put(LTaskContract.UsnFieldCategories, entity.getUsnFieldCategories());
            cv.put(LTaskContract.Categories, entity.getCategories());

        } else if (getUsnFieldCategories() > entity.getUsnFieldCategories()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldContacts() < entity.getUsnFieldContacts()) {
            cv.put(LTaskContract.UsnFieldContacts, entity.getUsnFieldContacts());
            cv.put(LTaskContract.Contacts, entity.getContacts());

        } else if (getUsnFieldContacts() > entity.getUsnFieldContacts()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCreatetime() < entity.getUsnFieldCreatetime()) {
            cv.put(LTaskContract.UsnFieldCreatetime, entity.getUsnFieldCreatetime());
            cv.put(LTaskContract.CreateTime, entity.getCreateTime());

        } else if (getUsnFieldCreatetime() > entity.getUsnFieldCreatetime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldPerformtime() < entity.getUsnFieldPerformtime()) {
            cv.put(LTaskContract.UsnFieldPerformtime, entity.getUsnFieldPerformtime());
            cv.put(LTaskContract.PerformTime, entity.getPerformTime());

        } else if (getUsnFieldPerformtime() > entity.getUsnFieldPerformtime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldCompletetime() < entity.getUsnFieldCompletetime()) {
            cv.put(LTaskContract.UsnFieldCompletetime, entity.getUsnFieldCompletetime());
            cv.put(LTaskContract.CompleteTime, entity.getCompleteTime());

        } else if (getUsnFieldCompletetime() > entity.getUsnFieldCompletetime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnTime() < entity.getUsnTime()) {
            cv.put(LTaskContract.UsnTime, entity.getUsnTime());
            cv.put(LTaskContract.Time, entity.getTime());

        } else if (getUsnTime() > entity.getUsnTime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnPlan() < entity.getUsnPlan()) {
            cv.put(LTaskContract.UsnPlan, entity.getUsnPlan());
            cv.put(LTaskContract.Plan, entity.getPlan());

        } else if (getUsnPlan() > entity.getUsnPlan()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnInWorkTime() < entity.getUsnInWorkTime()) {
            cv.put(LTaskContract.UsnInWorkTime, entity.getUsnInWorkTime());
            cv.put(LTaskContract.InWorkTime, entity.getInWorkTime());

        } else if (getUsnInWorkTime() > entity.getUsnInWorkTime()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldFocus() < entity.getUsnFieldFocus()) {
            cv.put(LTaskContract.UsnFieldFocus, entity.getUsnFieldFocus());
            cv.put(LTaskContract.Focus, entity.getFocus());

        } else if (getUsnFieldFocus() > entity.getUsnFieldFocus()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldListMembers() < entity.getUsnFieldListMembers()) {
            cv.put(LTaskContract.UsnFieldListMembers, entity.getUsnFieldListMembers());
            cv.put(LTaskContract.Emails, entity.getEmails());

        } else if (getUsnFieldListMembers() > entity.getUsnFieldListMembers()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        if (getUsnFieldSeries() < entity.getUsnFieldSeries()) {
            cv.put(LTaskContract.UsnFieldSeries, entity.getUsnFieldSeries());
            cv.put(LTaskContract.SeriesType, entity.getSeriesType());
            cv.put(LTaskContract.SeriesAfterType, entity.getSeriesAfterType());
            cv.put(LTaskContract.SeriesAfterCount, entity.getSeriesAfterCount());
            cv.put(LTaskContract.SeriesWeekCount, entity.getSeriesWeekCount());
            cv.put(LTaskContract.SeriesWeekMon, entity.getSeriesWeekMon());
            cv.put(LTaskContract.SeriesWeekTue, entity.getSeriesWeekTue());
            cv.put(LTaskContract.SeriesWeekWed, entity.getSeriesWeekWed());
            cv.put(LTaskContract.SeriesWeekThu, entity.getSeriesWeekThu());
            cv.put(LTaskContract.SeriesWeekFri, entity.getSeriesWeekFri());
            cv.put(LTaskContract.SeriesWeekSat, entity.getSeriesWeekSat());
            cv.put(LTaskContract.SeriesWeekSun, entity.getSeriesWeekSun());
            cv.put(LTaskContract.SeriesMonthType, entity.getSeriesMonthType());
            cv.put(LTaskContract.SeriesMonthCount, entity.getSeriesMonthCount());
            cv.put(LTaskContract.SeriesMonthDay, entity.getSeriesMonthDay());
            cv.put(LTaskContract.SeriesMonthWeekType, entity.getSeriesMonthWeekType());
            cv.put(LTaskContract.SeriesMonthDayOfWeek, entity.getSeriesMonthDayOfWeek());
            cv.put(LTaskContract.SeriesYearType, entity.getSeriesYearType());
            cv.put(LTaskContract.SeriesYearMonth, entity.getSeriesYearMonth());
            cv.put(LTaskContract.SeriesYearMonthDay, entity.getSeriesYearMonthDay());
            cv.put(LTaskContract.SeriesYearWeekType, entity.getSeriesYearWeekType());
            cv.put(LTaskContract.SeriesYearDayOfWeek, entity.getSeriesYearDayOfWeek());
            cv.put(LTaskContract.SeriesEnd, entity.getSeriesEnd());

        } else if (getUsnFieldSeries() > entity.getUsnFieldSeries()) {
            cv.put(LTaskContract.UsnEntity, 0);
        }

        return cv;
    }

    @Override
    public void setId(int value) {
        mId = value;
    }

    public void setOrder(int value) {
        mOrder = value;
    }

    public int getOrder() {
        return mOrder;
    }

    @Override
    public void setUid(String value) {
        mUid = value;
    }

    public void setUIDParent(String value) {
        mUIDParent = value;
    }

    public String getUIDParent() {
        return mUIDParent;
    }

    public void setCollapsed(boolean value) {
        mCollapsed = value;
    }

    public boolean getCollapsed() {
        return mCollapsed;
    }

    public void setName(String value) {
        mName = value;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String getFilterId() {
        return null;
    }

    @Override
    public int getIndent() {
        return 0;
    }

    public void setComment(String value) {
        mComment = value;
    }

    public String getComment() {
        return mComment;
    }

    public void setStatus(int value) {
        mStatus = value;
}

    public int getStatus() {
        return mStatus;
    }

    public int getTime() {
        return mTime;
    }
    public int getPlan() {
        return mPlan;
    }
    public long getInWorkTime() {
        return mInWorkTime;
    }

    public void setTime(int value) {
        mTime = value;
    }
    public  void setPlan(int value) {
        mPlan = value;
    }
    public  void setInWorkTime(long value) {
        mInWorkTime = value;
    }

    public int getUsnTime() {
        return mUsnTime;
    }
    public int getUsnPlan() {
        return mUsnPlan;
    }
    public int getUsnInWorkTime() {
        return mUsnInWorkTime;
    }

    public void setUsnTime(int value) {
        mUsnTime = value;
    }
    public void setUsnPlan(int value) {
        mUsnPlan = value;
    }
    public void setUsnInWorkTime(int value) {
        mUsnInWorkTime = value;
    }

    public void setTermBegin(long value) {
        mTermBegin = value;
    }

    public long getTermBegin() {
        return mTermBegin;
    }

    public void setTermEnd(long value) {
        mTermEnd = value;
    }

    public long getTermEnd() {
        return mTermEnd;
    }

    public void setEmailPerformer(String value) {
        mEmailPerformer = value;
    }

    public String getEmailPerformer() {
        return mEmailPerformer;
    }

    public void setUidProject(String value) {
        mUidProject = value;
    }

    public String getUidProject() {
        return mUidProject;
    }

    public void setUidMarker(String value) {
        mUidMarker = value;
    }

    public String getUidMarker() {
        return mUidMarker;
    }

    public void setReaded(boolean value) {
        mReaded = value;
    }

    public boolean getReaded() {
        return mReaded;
    }

    public void setPerformerReaded(boolean value) {
        mPerformerReaded = value;
    }

    public boolean getPerformerReaded() {
        return mPerformerReaded;
    }

    public void setOrderCustomer(int value) {
        mOrderCustomer = value;
    }

    public void setMarkerOrder(int value) { mMarkerOrder = value; }

    public int getMarkerOrder() {
        return mMarkerOrder;
    }

    public void setIsUseTerm(int value) { mIsUseTerm = value; }

    public int getIsUseTerm() {
        return mIsUseTerm;
    }

    public void setIsUseTermCustomer(int value) { mIsUseTermCustomer = value; }

    public int getIsUseTermCustomer() {
        return mIsUseTermCustomer;
    }

    public void setUserOrder(int value) { mUserOrder = value; }

    public int getUserOrder() {
        return mUserOrder;
    }

    public void setOrderNew(double value) { mOrderNew = value; }

    public int getOrderCustomer() {
        return mOrderCustomer;
    }

    public double getOrderNew() {
        return mOrderNew;
    }

    public void setTermBeginCustomer(long value) {
        mTermBeginCustomer = value;
    }

    public long getTermBeginCustomer() {
        return mTermBeginCustomer;
    }

    public void setTermEndCustomer(long value) {
        mTermEndCustomer = value;
    }

    public long getTermEndCustomer() {
        return mTermEndCustomer;
    }

    public void setEmailCustomer(String value) {
        mEmailCustomer = value;
    }

    public String getEmailCustomer() {
        return mEmailCustomer;
    }

    public void setCategories(String value) {
        mCategories = value;
    }

    public String getCategories() {
        return mCategories;
    }

    public void setContacts(String value) {
        mContacts = value;
    }

    public String getContacts() {
        return mContacts;
    }

    public void setCreateTime(long value) {
        mCreateTime = value;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setPerformTime(long value) {
        mPerformTime = value;
    }

    public long getPerformTime() {
        return mPerformTime;
    }

    public void setCompleteTime(long value) {
        mCompleteTime = value;
    }

    public long getCompleteTime() {
        return mCompleteTime;
    }

    public void setSeriesType(int value) {
        mSeriesType = value;
    }

    public int getSeriesType() {
        return mSeriesType;
    }

    public void setSeriesAfterType(int value) {
        mSeriesAfterType = value;
    }

    public int getSeriesAfterType() {
        return mSeriesAfterType;
    }

    public void setSeriesAfterCount(int value) {
        mSeriesAfterCount = value;
    }

    public int getSeriesAfterCount() {
        return mSeriesAfterCount;
    }

    public void setSeriesWeekCount(int value) {
        mSeriesWeekCount = value;
    }

    public int getSeriesWeekCount() {
        return mSeriesWeekCount;
    }

    public void setSeriesWeekMon(boolean value) {
        mSeriesWeekMon = value;
    }

    public boolean getSeriesWeekMon() {
        return mSeriesWeekMon;
    }

    public void setSeriesWeekTue(boolean value) {
        mSeriesWeekTue = value;
    }

    public boolean getSeriesWeekTue() {
        return mSeriesWeekTue;
    }

    public void setSeriesWeekWed(boolean value) {
        mSeriesWeekWed = value;
    }

    public boolean getSeriesWeekWed() {
        return mSeriesWeekWed;
    }

    public void setSeriesWeekThu(boolean value) {
        mSeriesWeekThu = value;
    }

    public boolean getSeriesWeekThu() {
        return mSeriesWeekThu;
    }

    public void setSeriesWeekFri(boolean value) {
        mSeriesWeekFri = value;
    }

    public boolean getSeriesWeekFri() {
        return mSeriesWeekFri;
    }

    public void setSeriesWeekSat(boolean value) {
        mSeriesWeekSat = value;
    }

    public boolean getSeriesWeekSat() {
        return mSeriesWeekSat;
    }

    public void setSeriesWeekSun(boolean value) {
        mSeriesWeekSun = value;
    }

    public boolean getSeriesWeekSun() {
        return mSeriesWeekSun;
    }

    public void setSeriesMonthType(int value) {
        mSeriesMonthType = value;
    }

    public int getSeriesMonthType() {
        return mSeriesMonthType;
    }

    public void setSeriesMonthCount(int value) {
        mSeriesMonthCount = value;
    }

    public int getSeriesMonthCount() {
        return mSeriesMonthCount;
    }

    public void setSeriesMonthDay(int value) {
        mSeriesMonthDay = value;
    }

    public int getSeriesMonthDay() {
        return mSeriesMonthDay;
    }

    public void setSeriesMonthWeekType(int value) {
        mSeriesMonthWeekType = value;
    }

    public int getSeriesMonthWeekType() {
        return mSeriesMonthWeekType <= 0 ? 1 : mSeriesMonthWeekType;
    }

    public void setSeriesMonthDayOfWeek(int value) {
        mSeriesMonthDayOfWeek = value;
    }

    public int getSeriesMonthDayOfWeek() {
        return mSeriesMonthDayOfWeek;
    }

    public void setSeriesYearType(int value) {
        mSeriesYearType = value;
    }

    public int getSeriesYearType() {
        return mSeriesYearType;
    }

    public void setSeriesYearMonth(int value) {
        mSeriesYearMonth = value;
    }

    public int getSeriesYearMonth() {
        return mSeriesYearMonth;
    }

    public void setSeriesYearMonthDay(int value) {
        mSeriesYearMonthDay = value;
    }

    public int getSeriesYearMonthDay() {
        return mSeriesYearMonthDay;
    }

    public void setSeriesYearWeekType(int value) {
        mSeriesYearWeekType = value;
    }

    public int getSeriesYearWeekType() {
        return mSeriesYearWeekType;
    }

    public void setSeriesYearDayOfWeek(int value) {
        mSeriesYearDayOfWeek = value;
    }

    public int getSeriesYearDayOfWeek() {
        return mSeriesYearDayOfWeek;
    }

    public void setSeriesEnd(long value) {
        mSeriesEnd = value;
    }

    public long getSeriesEnd() {
        return mSeriesEnd;
    }

    public void setUsnEntity(int value) {
        mUsnEntity = value;
    }

    public void setUsnFieldUidParent(int value) {
        mUsnFieldUidParent = value;
    }

    public int getUsnFieldUidParent() {
        return mUsnFieldUidParent;
    }

    public void setUsnFieldEmailPerformer(int value) {
        mUsnFieldEmailPerformer = value;
    }

    public int getUsnFieldEmailPerformer() {
        return mUsnFieldEmailPerformer;
    }

    public void setUsnFieldName(int value) {
        mUsnFieldName = value;
    }

    public int getUsnFieldName() {
        return mUsnFieldName;
    }

    public void setUsnFieldComment(int value) {
        mUsnFieldComment = value;
    }

    public int getUsnFieldComment() {
        return mUsnFieldComment;
    }

    public void setUsnFieldStatus(int value) {
        mUsnFieldStatus = value;
    }

    public int getUsnFieldStatus() {
        return mUsnFieldStatus;
    }

    public void setUsnFieldOrder(int value) {
        mUsnFieldOrder = value;
    }

    public void setUsnOrderNew(int value) {
        mUsnOrderNew = value;
    }

    public int getUsnFieldOrder() {
        return mUsnFieldOrder;
    }

    public void setUsnFieldUidProject(int value) {
        mUsnFieldUidProject = value;
    }

    public int getUsnFieldUidProject() {
        return mUsnFieldUidProject;
    }

    public void setUsnFieldUidMarker(int value) {
        mUsnFieldUidMarker = value;
    }

    public int getUsnFieldUidMarker() {
        return mUsnFieldUidMarker;
    }

    public void setUsnFieldTerm(int value) {
        mUsnFieldTerm = value;
    }

    public int getUsnFieldTerm() {
        return mUsnFieldTerm;
    }

    public void setUsnFieldReaded(int value) {
        mUsnFieldReaded = value;
    }

    public int getUsnFieldReaded() {
        return mUsnFieldReaded;
    }

    public void setUsnFieldPerformerReaded(int value) {
        mUsnFieldPerformerReaded = value;
    }

    public int getUsnFieldPerformerReaded() {
        return mUsnFieldPerformerReaded;
    }

    public void setUsnFieldCollapsed(int value) {
        mUsnFieldCollapsed = value;
    }

    public int getUsnFieldCollapsed() {
        return mUsnFieldCollapsed;
    }

    public void setUsnFieldCustomerOrder(int value) {
        mUsnFieldCustomerOrder = value;
    }

    public int getUsnFieldCustomerOrder() {
        return mUsnFieldCustomerOrder;
    }

    public int getUsnOrderNew() {
        return mUsnOrderNew;
    }

    public void setUsnFieldCustomerTerm(int value) {
        mUsnFieldCustomerTerm = value;
    }

    public int getUsnFieldCustomerTerm() {
        return mUsnFieldCustomerTerm;
    }

    public void setUsnFieldCategories(int value) {
        mUsnFieldCategories = value;
    }

    public int getUsnFieldCategories() {
        return mUsnFieldCategories;
    }

    public void setUsnFieldContacts(int value) {
        mUsnFieldContacts = value;
    }

    public int getUsnFieldContacts() {
        return mUsnFieldContacts;
    }

    public void setUsnFieldCreatetime(int value) {
        mUsnFieldCreatetime = value;
    }

    public int getUsnFieldCreatetime() {
        return mUsnFieldCreatetime;
    }

    public void setUsnFieldPerformtime(int value) {
        mUsnFieldPerformtime = value;
    }

    public int getUsnFieldPerformtime() {
        return mUsnFieldPerformtime;
    }

    public void setUsnFieldCompletetime(int value) {
        mUsnFieldCompletetime = value;
    }

    public int getUsnFieldCompletetime() {
        return mUsnFieldCompletetime;
    }

    public void setUsnFieldSeries(int value) {
        mUsnFieldSeries = value;
    }

    public int getUsnFieldSeries() {
        return mUsnFieldSeries;
    }

    public boolean getFocus(){
        return mFocus;
    }

    public void setFocus(boolean val){
        mFocus = val;
    }

    public int getUsnFieldFocus(){
        return mUsnFieldFocus ;
    }

    public void setUsnFieldFocus(int val){
        mUsnFieldFocus = val;
    }

    public LTask clone() {
        final LTask task = new LTask();

        task.setId(getIdTask());
        task.setOrder(getOrder());
        task.setUid(getUid());
        task.setUIDParent(getUIDParent());
        task.setCollapsed(getCollapsed());
        task.setName(getName());
        task.setComment(getComment());
        task.setStatus(getStatus());
        task.setTermBegin(getTermBegin());
        task.setTermEnd(getTermEnd());
        task.setEmailPerformer(getEmailPerformer());
        task.setUidProject(getUidProject());
        task.setUidMarker(getUidMarker());
        task.setReaded(getReaded());
        task.setPerformerReaded(getPerformerReaded());
        task.setOrderCustomer(getOrderCustomer());
        task.setOrderNew(getOrderNew());
        task.setTermBeginCustomer(getTermBeginCustomer());
        task.setTermEndCustomer(getTermEndCustomer());
        task.setEmailCustomer(getEmailCustomer());
        task.setCategories(getCategories());
        task.setContacts(getContacts());
        task.setCreateTime(getCreateTime());
        task.setPerformTime(getPerformTime());
        task.setCompleteTime(getCompleteTime());
        task.setSeriesType(getSeriesType());
        task.setSeriesAfterType(getSeriesAfterType());
        task.setSeriesAfterCount(getSeriesAfterCount());
        task.setSeriesWeekCount(getSeriesWeekCount());
        task.setSeriesWeekMon(getSeriesWeekMon());
        task.setSeriesWeekTue(getSeriesWeekTue());
        task.setSeriesWeekWed(getSeriesWeekWed());
        task.setSeriesWeekThu(getSeriesWeekThu());
        task.setSeriesWeekFri(getSeriesWeekFri());
        task.setSeriesWeekSat(getSeriesWeekSat());
        task.setSeriesWeekSun(getSeriesWeekSun());
        task.setSeriesMonthType(getSeriesMonthType());
        task.setSeriesMonthCount(getSeriesMonthCount());
        task.setSeriesMonthDay(getSeriesMonthDay());
        task.setSeriesMonthWeekType(getSeriesMonthWeekType());
        task.setSeriesMonthDayOfWeek(getSeriesMonthDayOfWeek());
        task.setSeriesYearType(getSeriesYearType());
        task.setSeriesYearMonth(getSeriesYearMonth());
        task.setSeriesYearMonthDay(getSeriesYearMonthDay());
        task.setSeriesYearWeekType(getSeriesYearWeekType());
        task.setSeriesYearDayOfWeek(getSeriesYearDayOfWeek());
        task.setSeriesEnd(getSeriesEnd());
        task.setUsnEntity(getUsnEntity());
        task.setUsnFieldUidParent(getUsnFieldUidParent());
        task.setUsnFieldEmailPerformer(getUsnFieldEmailPerformer());
        task.setUsnFieldName(getUsnFieldName());
        task.setUsnFieldComment(getUsnFieldComment());
        task.setUsnFieldStatus(getUsnFieldStatus());
        task.setUsnFieldOrder(getUsnFieldOrder());
        task.setUsnFieldUidProject(getUsnFieldUidProject());
        task.setUsnFieldUidMarker(getUsnFieldUidMarker());
        task.setUsnFieldTerm(getUsnFieldTerm());
        task.setUsnFieldReaded(getUsnFieldReaded());
        task.setUsnFieldCollapsed(getUsnFieldCollapsed());
        task.setUsnFieldCustomerOrder(getUsnFieldCustomerOrder());
        task.setUsnOrderNew(getUsnOrderNew());
        task.setUsnFieldCustomerTerm(getUsnFieldCustomerTerm());
        task.setUsnFieldCategories(getUsnFieldCategories());
        task.setUsnFieldContacts(getUsnFieldContacts());
        task.setUsnFieldCreatetime(getUsnFieldCreatetime());
        task.setUsnFieldPerformtime(getUsnFieldPerformtime());
        task.setUsnFieldCompletetime(getUsnFieldCompletetime());
        task.setUsnFieldPerformerReaded(getUsnFieldPerformerReaded());
        task.setUsnFieldSeries(getUsnFieldSeries());
        task.setUserOrder(getUserOrder());
        task.setMarkerOrder(getMarkerOrder());
        task.setIsUseTerm(getIsUseTerm());
        task.setIsUseTermCustomer(getIsUseTermCustomer());
        task.setTime(getTime());
        task.setPlan(getPlan());
        task.setInWorkTime(getInWorkTime());
        task.setUsnTime(getUsnTime());
        task.setUsnPlan(getUsnPlan());
        task.setUsnInWorkTime(getUsnInWorkTime());
        task.setFocus(getFocus());
        task.setUsnFieldFocus(getUsnFieldFocus());
        task.setEmails(getEmails());
        task.setUsnFieldListMembers(getUsnFieldListMembers());
        return task;
    }

    private final StringBuilder sb = new StringBuilder();

    @Override
    public String toString() {
        Utils.clearStringBuilder(sb);
        sb.append(getIdTask());
        sb.append(SharedStrings.TAB_C);
        sb.append(getName());

        return sb.toString();
    }

    @Override
    public String getTableName() {
        return LTaskContract.TABLE_NAME;
    }

    public static int parseWithDefault(String number, int defaultVal) {
        try {
            if(number == null) {
                return defaultVal;
            }
            else {
                return Integer.parseInt(number);
            }
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    @Override
    public int compareTo(LTask another) {
        return 0;
    }

    public String getEmails() {
        return mEmails;
    }

    public void setEmails(String val){
        mEmails = val;
    }

    public int getUsnFieldListMembers(){
        return  mUsnFieldListMembers;
    }

    public void setUsnFieldListMembers(int val){
        mUsnFieldListMembers = val;
    }
}