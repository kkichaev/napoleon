package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils.JsonTaskLabelsUtils;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultSortOrder;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@DatabaseTable(tableName = TaskContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = TaskContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = TaskContract.TABLE_NAME)
public class Task//
        implements Serializable, IEntity, BaseLionEntityInterface, Comparable<Task> {

    private static final long serialVersionUID = 1L;

    private static final String DOT_DOUBLE = "..";
    private static final String DOT_SINGLE = ".";
    public static final SimpleDateFormat SDF = getSimpleDateFormat();

    @DatabaseField(columnName = TaskContract._ID, generatedId = true)
    private int mEmptyId;

    /**
     * UID - уникальный идентификатор элемента (текст)
     */
    @DatabaseField(columnName = TaskContract.FIELD_UID, canBeNull = false, index = true)
    private UUID mId;

    /**
     * UIDParent – уникальный идентификатор родителя (текст, может быть пустой)
     */
    @DatabaseField(columnName = TaskContract.FIELD_UID_PARENT, index = true)
    private UUID mParentId;

    /**
     * Order – порядок элемента в дереве/списке (число, начиная с 1)
     */
    @DefaultSortOrder
    @DatabaseField(columnName = TaskContract.ORDERS, index = true)
    private int mOrder;

    /**
     * Collapsed – свернут ли элемент (0 или 1)
     */
    @DatabaseField(columnName = TaskContract.FIELD_COLLAPSED)
    private boolean mCollapsed;

    /**
     * Name – заголовок элемента (текст)
     */
    @DatabaseField(columnName = TaskContract.FIELD_NAME, index = true)
    private String mName;

    /**
     * Comment – комментарий элемента (текст)
     */
    @DatabaseField(columnName = TaskContract.FIELD_COMMENT)
    private String mComment;

    /**
     * Status – статус задачи (число) 0 - не начиналось (значение по умолчанию) 1 - завершено 3 - заметка 4 - в работе 5
     * - готово к сдаче 6 - отложено 7 - отменено 8 - отклонено 9 - на доработку
     */
    @DatabaseField(columnName = TaskContract.FIELD_STATUS, index = true)
    private int mStatus;

    /**
     * TermBegin – начало срока (дата-время)
     */
    @DatabaseField(columnName = TaskContract.FIELD_TERM_BEGIN, index = true, dataType = DataType.DATE_LONG)
    private Date mTermBegin;

    /**
     * TermEnd – конец срока (дата-время)
     */
    @DatabaseField(columnName = TaskContract.FIELD_TERM_END, index = true, dataType = DataType.DATE_LONG)
    private Date mTermEnd;

    /**
     * Performer - исполнитель задачи (логин исполнителя, если нет исполнителя равен заказчику)
     */
    @DatabaseField(columnName = TaskContract.FIELD_EMAIL_PERFORMER, index = true)
    private String mPerformer;

    /**
     * UIDProject – уникальный идентификатор проекта, к которому привязана задача (текст, может быть пустой)
     */
    @DatabaseField(columnName = TaskContract.FIELD_UID_PROJECT, index = true)
    private UUID mProjectUid;

    /**
     * UIDMarker – уникальный идентификатор маркера, которым помечена задача (текст, по умолчанию default)
     */
    @DatabaseField(columnName = TaskContract.FIELD_UID_MARKER, index = true)
    private UUID mMarkerUid;

    /**
     * IsReaded – прочитана ли задача (0 или 1)
     */
    @DatabaseField(columnName = TaskContract.FIELD_READED, index = true)
    private boolean mReaded;

    /**
     * CustomerOrder – порядок элемента в дереве/списке у заказчика задачи (число, начиная с 1)
     */
    @DatabaseField(columnName = TaskContract.FIELD_ORDER_CUSTOMER, index = true)
    private int mCustomerOrder;

    /**
     * CustomerTermBegin – начало срока заказчика (дата-время)
     */
    @DatabaseField(columnName = TaskContract.FIELD_TERM_BEGIN_CUSTOMER, index = true, dataType = DataType.DATE_LONG)
    private Date mTermBeginCustomer;

    /**
     * CustomerTermEnd – конец срока заказчика (дата-время)
     */
    @DatabaseField(columnName = TaskContract.FIELD_TERM_END_CUSTOMER, index = true, dataType = DataType.DATE_LONG)
    private Date mTermEndCustomer;

    /**
     * Customer – заказчик задачи (логин создателя)
     */
    @DatabaseField(columnName = TaskContract.FIELD_EMAIL_CUSTOMER, index = true)
    private String mCustomer;

    /**
     * Categories – список UID`ов категорий с которыми связана задача (текст)
     */
    @DatabaseField(columnName = TaskContract.FIELD_CATEGORIES, index = true)
    private String mCategories;

    /**
     * Contacts – список UID`ов контактов с которыми связана задача (текст)
     */
    @DatabaseField(columnName = TaskContract.FIELD_CONTACTS, index = true)
    private String mContacts;

    /**
     * USN – номер изменения элемента (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_ENTITY)
    private long mUsn;

    /**
     * USN_ UIDParent – номер изменения родителя (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_UID_PARENT)
    private int mUsnParentUid;

    /**
     * USN_Performer – номер изменения исполнителя задачи (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_EMAIL_PERORMER)
    private int mUsnEmailPerformer;

    /**
     * USN_Name – номер изменения заголовка (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_NAME)
    private int mUsnName;

    /**
     * USN_Comment – номер изменения комментария (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_COMMENT)
    private int mUsnComment;

    /**
     * USN_Status – номер изменения статуса задачи (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_STATUS)
    private int mUsnStatus;

    /**
     * USN_Order – номер изменения порядка (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_ORDER)
    private int mUsnOrder;

    /**
     * USN_UIDProject – номер изменения проекта (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_UID_PROJECT)
    private int mUsnProjectUid;

    /**
     * USN_UIDMarker – номер изменения маркера (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_UID_MARKER)
    private int mUsnMarkerUid;

    /**
     * USN_Term – номер изменения срока задачи (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_TERM)
    private int mUsnTerm;

    /**
     * USN_IsReaded – номер изменения поля прочитано (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_READED)
    private int mUsnReaded;

    /**
     * USN_Collapsed – номер изменения поля свернут (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_COLLAPSED)
    private int mUsnCollapsed;

    /**
     * USN_CustomerOrder – номер изменения порядка заказчика (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_CUSTOMER_ORDER)
    private int mUsnCustomerOrder;

    /**
     * USN_CustomerTerm – номер изменения срока заказчика (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_CUSTOMER_TERM)
    private int mUsnCustomerTerm;

    /**
     * USN_Categories – номер изменения связей с категориями (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_CATEGORIES)
    private int mUsnCategories;

    /**
     * USN_Contacts – номер изменения связей с контактами (число, начиная с 0)
     */
    @DatabaseField(columnName = TaskContract.FIELD_USN_CONTACTS)
    private int mUsnContacts;

    @DatabaseField(columnName = TaskContract.SUBTASKS_SIZE, index = true)
    private int mSubTasksCount;

    @DatabaseField(columnName = TaskContract.SUBTASKS_SIZE_NOT_READ, index = true)
    private int mSubTasksCountNotRead;

    @DatabaseField(columnName = TaskContract.SUBTASKS_SIZE_NOT_MADE, index = true)
    private int mSubTasksCountNotMade;

    @DatabaseField(columnName = TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ, index = true)
    private int mSubTasksCountNotMadeAndNotRead;

    @DatabaseField(columnName = TaskContract.MESSAGES_COUNT, canBeNull = true)
    private Integer mMessagesCount;

    @DatabaseField(columnName = TaskContract.LEFT_POINTER, index = true)
    private int mLeftPointer = 0;

    @DatabaseField(columnName = TaskContract.RIGHT_POINTER, index = true)
    private int mRightPointer = 1;

    @DatabaseField(columnName = TaskContract.WAS_COUNTED)
    private boolean mWasCounted;

    @DatabaseField(columnName = TaskContract.HAS_FILES)
    private boolean mHasFiles;

    @DatabaseField(columnName = TaskContract.LIST_LABELS)
    private String mLabelsString;

    // NEW FIELDS START

    @DatabaseField(columnName = TaskContract.FIELD_CREATE_TIME, dataType = DataType.DATE_LONG)
    private Date mCreationTime;

    @DatabaseField(columnName = TaskContract.FIELD_PERFORM_TIME, dataType = DataType.DATE_LONG)
    private Date mPerformTime;

    @DatabaseField(columnName = TaskContract.FIELD_COMPLETE_TIME, dataType = DataType.DATE_LONG)
    private Date mCompleteTime;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_TYPE)
    private int mSeriesType;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_AFTER_TYPE)
    private int mSeriesAfterType;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_AFTER_COUNT)
    private int mSeriesAfterCount;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_COUNT)
    private int mSeriesWeekCount;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_MON)
    private boolean mSeriesWeekMon;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_TUE)
    private boolean mSeriesWeekTue;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_WED)
    private boolean mSeriesWeekWed;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_THU)
    private boolean mSeriesWeekThu;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_FRI)
    private boolean mSeriesWeekFri;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_SAT)
    private boolean mSeriesWeekSat;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_WEEK_SUN)
    private boolean mSeriesWeekSun;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_MONTH_TYPE)
    private int mSeriesMonthType;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_MONTH_COUNT)
    private int mSeriesMonthCount;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_MONTH_DAY)
    private int mSeriesMonthDay;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_MONTH_WEEKTYPE)
    private int mSeriesMonthWeekType;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_MONTH_DAYOFWEEK)
    private int mSeriesMonthDayOfWeek;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_YEAR_TYPE)
    private int mSeriesYearType;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_YEAR_MONTH)
    private int mSeriesYearMonth;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_YEAR_MONTHDAY)
    private int mSeriesYearMonthDay;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_YEAR_WEEKTYPE)
    private int mSeriesYearWeekType;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_YEAR_DAYOFWEEK)
    private int mSeriesYearDayOfWeek;

    @DatabaseField(columnName = TaskContract.FIELD_SERIES_END, dataType = DataType.DATE_LONG)
    private Date mSeriesEnd;

    @DatabaseField(columnName = TaskContract.FIELD_USN_FIELD_CREATETIME)
    private long mUsnFieldCreateTime;

    @DatabaseField(columnName = TaskContract.FIELD_USN_FIELD_PERFORMTIME)
    private long mUsnFieldPerformTime;

    @DatabaseField(columnName = TaskContract.FIELD_USN_FIELD_COMPLETETIME)
    private long mUsnFieldCompleteTime;

    @DatabaseField(columnName = TaskContract.FIELD_USN_FIELD_SERIES)
    private long mUsnFieldSeries;

    @DatabaseField(columnName = TaskContract.EMP_ORDERS)
    private int mEmpOrders;

    // NEW FIELDS END

    private String mSeriesTypeString;

    private List<Integer> mLabels;

    private static int[] sColums;

    public Task() {}

    public Task(Cursor c) {
        setData(c);
    }

    public void setData(Cursor c) {
        fillFastTable(c);

        mId = UUID.fromString(c.getString(sColums[0]));
        mParentId = getUUIDFromString(c.getString(sColums[1]));
        mOrder = c.getInt(sColums[2]);
        mCollapsed = BaseSOAP.equalsOne(c.getInt(sColums[3]));
        mName = c.getString(sColums[4]);
        mComment = c.getString(sColums[5]);
        mStatus = c.getInt(sColums[6]);
        mTermBegin = getDateFromLong(c.getLong(sColums[7]));
        mTermEnd = getDateFromLong(c.getLong(sColums[8]));
        mPerformer = c.getString(sColums[9]);
        mProjectUid = getUUIDFromString(c.getString(sColums[10]));
        mMarkerUid = getUUIDFromString(c.getString(sColums[11]));
        mReaded = BaseSOAP.equalsOne(c.getInt(sColums[12]));
        mCustomerOrder = c.getInt(sColums[13]);
        mTermBeginCustomer = getDateFromLong(c.getLong(sColums[14]));
        mTermEndCustomer = getDateFromLong(c.getLong(sColums[15]));
        mCustomer = c.getString(sColums[16]);
        mCategories = c.getString(sColums[17]);
        mContacts = c.getString(sColums[18]);

        mUsn = c.getLong(sColums[19]);
        mUsnParentUid = c.getInt(sColums[20]);
        mUsnEmailPerformer = c.getInt(sColums[21]);
        mUsnName = c.getInt(sColums[22]);
        mUsnComment = c.getInt(sColums[23]);
        mUsnStatus = c.getInt(sColums[24]);
        mUsnOrder = c.getInt(sColums[25]);
        mUsnProjectUid = c.getInt(sColums[26]);
        mUsnMarkerUid = c.getInt(sColums[27]);
        mUsnTerm = c.getInt(sColums[28]);
        mUsnReaded = c.getInt(sColums[29]);
        mUsnCollapsed = c.getInt(sColums[30]);
        mUsnCustomerOrder = c.getInt(sColums[31]);
        mUsnCustomerTerm = c.getInt(sColums[32]);
        mUsnCategories = c.getInt(sColums[33]);
        mUsnContacts = c.getInt(sColums[34]);

        mSubTasksCount = c.getInt(sColums[35]);
        mSubTasksCountNotRead = c.getInt(sColums[36]);
        mSubTasksCountNotMade = c.getInt(sColums[37]);
        mSubTasksCountNotMadeAndNotRead = c.getInt(sColums[38]);
        mMessagesCount = c.getInt(sColums[39]);

        mLeftPointer = c.getInt(sColums[40]);
        mRightPointer = c.getInt(sColums[41]);

        mCreationTime = getDateFromLong(c.getLong(sColums[42]));
        mPerformTime = getDateFromLong(c.getLong(sColums[43]));
        mCompleteTime = getDateFromLong(c.getLong(sColums[44]));

        mSeriesType = c.getInt(sColums[45]);
        mSeriesAfterType = c.getInt(sColums[46]);
        mSeriesAfterCount = c.getInt(sColums[47]);

        mSeriesWeekCount = c.getInt(sColums[48]);
        mSeriesWeekMon = BaseSOAP.equalsOne(c.getInt(sColums[49]));
        mSeriesWeekTue = BaseSOAP.equalsOne(c.getInt(sColums[50]));
        mSeriesWeekWed = BaseSOAP.equalsOne(c.getInt(sColums[51]));
        mSeriesWeekThu = BaseSOAP.equalsOne(c.getInt(sColums[52]));
        mSeriesWeekFri = BaseSOAP.equalsOne(c.getInt(sColums[53]));
        mSeriesWeekSat = BaseSOAP.equalsOne(c.getInt(sColums[54]));
        mSeriesWeekSun = BaseSOAP.equalsOne(c.getInt(sColums[55]));
        mSeriesMonthType = c.getInt(sColums[56]);
        mSeriesMonthCount = c.getInt(sColums[57]);
        mSeriesMonthDay = c.getInt(sColums[58]);
        mSeriesMonthWeekType = c.getInt(sColums[59]);
        mSeriesMonthDayOfWeek = c.getInt(sColums[60]);
        mSeriesYearType = c.getInt(sColums[61]);
        mSeriesYearMonth = c.getInt(sColums[62]);
        mSeriesYearMonthDay = c.getInt(sColums[63]);
        mSeriesYearWeekType = c.getInt(sColums[64]);
        mSeriesYearDayOfWeek = c.getInt(sColums[65]);
        mSeriesEnd = getDateFromLong(c.getLong(sColums[66]));

        mUsnFieldCreateTime = c.getLong(sColums[67]);
        mUsnFieldPerformTime = c.getLong(sColums[68]);
        mUsnFieldCompleteTime = c.getLong(sColums[69]);
        mUsnFieldSeries = c.getLong(sColums[70]);
    }

    private static void fillFastTable(Cursor c) {
        if (sColums == null) {
            synchronized (Task.class) {
                if (sColums == null) {
                    sColums = new int[71];

                    sColums[0] = c.getColumnIndex(TaskContract.FIELD_UID);
                    sColums[1] = c.getColumnIndex(TaskContract.FIELD_UID_PARENT);
                    sColums[2] = c.getColumnIndex(TaskContract.ORDERS);
                    sColums[3] = c.getColumnIndex(TaskContract.FIELD_COLLAPSED);
                    sColums[4] = c.getColumnIndex(TaskContract.FIELD_NAME);
                    sColums[5] = c.getColumnIndex(TaskContract.FIELD_COMMENT);
                    sColums[6] = c.getColumnIndex(TaskContract.FIELD_STATUS);
                    sColums[7] = c.getColumnIndex(TaskContract.FIELD_TERM_BEGIN);
                    sColums[8] = c.getColumnIndex(TaskContract.FIELD_TERM_END);
                    sColums[9] = c.getColumnIndex(TaskContract.FIELD_EMAIL_PERFORMER);
                    sColums[10] = c.getColumnIndex(TaskContract.FIELD_UID_PROJECT);
                    sColums[11] = c.getColumnIndex(TaskContract.FIELD_UID_MARKER);
                    sColums[12] = c.getColumnIndex(TaskContract.FIELD_READED);
                    sColums[13] = c.getColumnIndex(TaskContract.FIELD_ORDER_CUSTOMER);
                    sColums[14] = c.getColumnIndex(TaskContract.FIELD_TERM_BEGIN_CUSTOMER);
                    sColums[15] = c.getColumnIndex(TaskContract.FIELD_TERM_END_CUSTOMER);
                    sColums[16] = c.getColumnIndex(TaskContract.FIELD_EMAIL_CUSTOMER);
                    sColums[17] = c.getColumnIndex(TaskContract.FIELD_CATEGORIES);
                    sColums[18] = c.getColumnIndex(TaskContract.FIELD_CONTACTS);

                    sColums[19] = c.getColumnIndex(TaskContract.FIELD_USN_ENTITY);
                    sColums[20] = c.getColumnIndex(TaskContract.FIELD_USN_UID_PARENT);
                    sColums[21] = c.getColumnIndex(TaskContract.FIELD_USN_EMAIL_PERORMER);
                    sColums[22] = c.getColumnIndex(TaskContract.FIELD_USN_NAME);
                    sColums[23] = c.getColumnIndex(TaskContract.FIELD_USN_COMMENT);
                    sColums[24] = c.getColumnIndex(TaskContract.FIELD_USN_STATUS);
                    sColums[25] = c.getColumnIndex(TaskContract.FIELD_USN_ORDER);
                    sColums[26] = c.getColumnIndex(TaskContract.FIELD_USN_UID_PROJECT);
                    sColums[27] = c.getColumnIndex(TaskContract.FIELD_USN_UID_MARKER);
                    sColums[28] = c.getColumnIndex(TaskContract.FIELD_USN_TERM);
                    sColums[29] = c.getColumnIndex(TaskContract.FIELD_USN_READED);
                    sColums[30] = c.getColumnIndex(TaskContract.FIELD_USN_COLLAPSED);
                    sColums[31] = c.getColumnIndex(TaskContract.FIELD_USN_CUSTOMER_ORDER);
                    sColums[32] = c.getColumnIndex(TaskContract.FIELD_USN_CUSTOMER_TERM);
                    sColums[33] = c.getColumnIndex(TaskContract.FIELD_USN_CATEGORIES);
                    sColums[34] = c.getColumnIndex(TaskContract.FIELD_USN_CONTACTS);

                    sColums[35] = c.getColumnIndex(TaskContract.SUBTASKS_SIZE);
                    sColums[36] = c.getColumnIndex(TaskContract.SUBTASKS_SIZE_NOT_READ);
                    sColums[37] = c.getColumnIndex(TaskContract.SUBTASKS_SIZE_NOT_MADE);
                    sColums[38] = c.getColumnIndex(TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ);
                    sColums[39] = c.getColumnIndex(TaskContract.MESSAGES_COUNT);

                    sColums[40] = c.getColumnIndex(TaskContract.LEFT_POINTER);
                    sColums[41] = c.getColumnIndex(TaskContract.RIGHT_POINTER);

                    sColums[42] = c.getColumnIndex(TaskContract.FIELD_CREATE_TIME);
                    sColums[43] = c.getColumnIndex(TaskContract.FIELD_PERFORM_TIME);
                    sColums[44] = c.getColumnIndex(TaskContract.FIELD_COMPLETE_TIME);
                    sColums[45] = c.getColumnIndex(TaskContract.FIELD_SERIES_TYPE);
                    sColums[46] = c.getColumnIndex(TaskContract.FIELD_SERIES_AFTER_TYPE);
                    sColums[47] = c.getColumnIndex(TaskContract.FIELD_SERIES_AFTER_COUNT);
                    sColums[48] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_COUNT);
                    sColums[49] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_MON);
                    sColums[50] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_TUE);
                    sColums[51] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_WED);
                    sColums[52] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_THU);
                    sColums[53] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_FRI);
                    sColums[54] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_SAT);
                    sColums[55] = c.getColumnIndex(TaskContract.FIELD_SERIES_WEEK_SUN);
                    sColums[56] = c.getColumnIndex(TaskContract.FIELD_SERIES_MONTH_TYPE);
                    sColums[57] = c.getColumnIndex(TaskContract.FIELD_SERIES_MONTH_COUNT);
                    sColums[58] = c.getColumnIndex(TaskContract.FIELD_SERIES_MONTH_DAY);
                    sColums[59] = c.getColumnIndex(TaskContract.FIELD_SERIES_MONTH_WEEKTYPE);
                    sColums[60] = c.getColumnIndex(TaskContract.FIELD_SERIES_MONTH_DAYOFWEEK);
                    sColums[61] = c.getColumnIndex(TaskContract.FIELD_SERIES_YEAR_TYPE);
                    sColums[62] = c.getColumnIndex(TaskContract.FIELD_SERIES_YEAR_MONTH);
                    sColums[63] = c.getColumnIndex(TaskContract.FIELD_SERIES_YEAR_MONTHDAY);
                    sColums[64] = c.getColumnIndex(TaskContract.FIELD_SERIES_YEAR_WEEKTYPE);
                    sColums[65] = c.getColumnIndex(TaskContract.FIELD_SERIES_YEAR_DAYOFWEEK);
                    sColums[66] = c.getColumnIndex(TaskContract.FIELD_SERIES_END);

                    sColums[67] = c.getColumnIndex(TaskContract.FIELD_USN_FIELD_CREATETIME);
                    sColums[68] = c.getColumnIndex(TaskContract.FIELD_USN_FIELD_PERFORMTIME);
                    sColums[69] = c.getColumnIndex(TaskContract.FIELD_USN_FIELD_COMPLETETIME);
                    sColums[70] = c.getColumnIndex(TaskContract.FIELD_USN_FIELD_SERIES);
                }
            }
        }
    }

    public void setCategoriesWithSet(Set<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            mCategories = null;
            return;
        }

        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Category category : categories) {
            if (first) {
                first = false;
            } else {
                sb.append(DOT_DOUBLE);
            }
            sb.append(String.valueOf(category.getId()).toUpperCase());
        }

        mCategories = sb.toString();
    }

    public void setCategoriesWithCategory(Category category) {
        if (category == null) {
            mCategories = null;
        } else {
            mCategories = String.valueOf(category.getId()).toUpperCase();
        }
    }

    public Set<Category> getCategoriesInHash() {
        if (TextUtils.isEmpty(mCategories)) {
            return null;
        }

        final Set<Category> categories = new HashSet<Category>();

        int start = 0;
        int end;
        String s = mCategories;

        while (start < s.length()) {
            if (s.contains(DOT_SINGLE)) {
                end = s.indexOf(DOT_SINGLE);

                final Category category = getCategory(s.substring(start, end));
                if (!categories.contains(category)) {
                    categories.add(category);
                }

                s = s.substring(end + 2);
                start = 0;
            } else {
                final Category category = getCategory(s);
                if (!categories.contains(category)) {
                    categories.add(category);
                }
                break;
            }
        }

        return categories;
    }

    private Category getCategory(String s) {
        try {
            return new Category().setId(UUID.fromString(s));
        } catch (Exception e) {
            return null;
        }
    }

    public String getContacts() {
        return mContacts;
    }

    /**
     * get not read subtasks size
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    public int getSubTasksCountNotRead() {
        return mSubTasksCountNotRead;
    }

    /**
     * set not read subtasks size
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    public void setSubTasksCountNotRead(int subTasksSizeNotRead) {
        mSubTasksCountNotRead = subTasksSizeNotRead;
    }

    /**
     * get not made and not read subtasks size
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    public int getSubTasksSizeNotMadeAndNotRead() {
        return mSubTasksCountNotMadeAndNotRead;
    }

    /**
     * set not made and not read subtasks size
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    public void setSubTasksSizeNotMadeAndNotRead(int mSubTasksSizeNotMadeAndNotRead) {
        this.mSubTasksCountNotMadeAndNotRead = mSubTasksSizeNotMadeAndNotRead;
    }

    /**
     * Retun number of messages in this task.
     * 
     * @author V.Shcryabets<vshcryabets@gmail.com>
     * @return number of messages in this task.
     */
    public Integer getMessagesCount() {
        return mMessagesCount;
    }

    /**
     * Set number of messages in this task.
     * 
     * @author V.Shcryabets<vshcryabets@gmail.com>
     */
    public void setMessagesCount(Integer messagesCount) {
        mMessagesCount = messagesCount;
    }

    /**
     * Set this task status type.
     * 
     * @param status
     *            new status for this task.
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     */
    public void setStatusType(TaskStatus status) {
        mStatus = status.getCode();
    }

    /**
     * @author V.Shcryabets<vshcryabets@gmail.com>
     */
    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    /**
     * @author V.Shcryabets<vshcryabets@gmail.com>
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Task) {
            final Task task = (Task) o;
            return mId.equals(task.mId);
        }
        return false;
    }

    /**
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     * @return - integer value that represents task left pointer in tasks hierarchy
     */
    public int getLeftPointer() {
        return mLeftPointer;
    }

    /**
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     * @param depth
     *            - integer value that represents task left pointer in tasks hierarchy
     */
    public void setLeftPointer(int left) {
        mLeftPointer = left;
    }

    /**
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     * @return - integer value that represents task right pointer in tasks hierarchy
     */
    public int getRightPointer() {
        return mRightPointer;
    }

    /**
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     * @param depth
     *            - integer value that represents task right pointer in tasks hierarchy
     */
    public void setRightPointer(int right) {
        mRightPointer = right;
    }

    public Date getTermBegin() {
        return mTermBegin;
    }

    public void setTermBegin(Date termBegin) {
        mTermBegin = termBegin;
    }

    public Date getTermEnd() {
        return mTermEnd;
    }

    public void setTermEnd(Date termEnd) {
        mTermEnd = termEnd;
    }

    public Date getTermCustomerBegin() {
        return mTermBeginCustomer;
    }

    public void setTermCustomerBegin(Date termCustomerBegin) {
        mTermBeginCustomer = termCustomerBegin;
    }

    public Date getTermCustomerEnd() {
        return mTermEndCustomer;
    }

    public void setTermCustomerEnd(Date termCustomerEnd) {
        mTermEndCustomer = termCustomerEnd;
    }

    public String getCustomer() {
        return mCustomer;
    }

    public void setCustomer(String customer) {
        mCustomer = customer;
    }

    public String getPerformer() {
        return mPerformer;
    }

    public void setPerformer(String performer) {
        mPerformer = performer;
    }

    public int getStatus() {
        return mStatus;
    }

    public TaskStatus getStatusType() {
        return TaskStatus.getTaskStatus(mStatus);
    }

    public void setStatus(int status) {
        mStatus = status;
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
        return mUsn;
    }

    public UUID getParentId() {
        return mParentId;
    }

    public void setParentId(UUID parentId) {
        mParentId = parentId;
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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public UUID getProjectUid() {
        return mProjectUid;
    }

    public void setProjectUid(UUID projectUid) {
        mProjectUid = projectUid;
    }

    public UUID getMarkerUid() {
        return mMarkerUid;
    }

    public void setMarkerUid(UUID markerUid) {
        mMarkerUid = markerUid;
    }

    public boolean isReaded() {
        return mReaded;
    }

    public void setReaded(boolean readed) {
        mReaded = readed;
    }

    public int getCustomerOrder() {
        return mCustomerOrder;
    }

    public void setCustomerOrder(int customerOrder) {
        mCustomerOrder = customerOrder;
    }

    public Date getTermBeginCustomer() {
        return mTermBeginCustomer;
    }

    public void setTermBeginCustomer(Date termBeginCustomer) {
        mTermBeginCustomer = termBeginCustomer;
    }

    public Date getTermEndCustomer() {
        return mTermEndCustomer;
    }

    public void setTermEndCustomer(Date termEndCustomer) {
        mTermEndCustomer = termEndCustomer;
    }

    public String getCategories() {
        return mCategories;
    }

    public void setCategories(String categories) {
        mCategories = categories;
    }

    public int getUsnParentUid() {
        return mUsnParentUid;
    }

    public void setUsnParentUid(int usnParentUid) {
        mUsnParentUid = usnParentUid;
    }

    public int getUsnEmailPerformer() {
        return mUsnEmailPerformer;
    }

    public void setUsnEmailPerformer(int usnEmailPerformer) {
        mUsnEmailPerformer = usnEmailPerformer;
    }

    public int getUsnName() {
        return mUsnName;
    }

    public void setUsnName(int usnName) {
        mUsnName = usnName;
    }

    public int getUsnComment() {
        return mUsnComment;
    }

    public void setUsnComment(int usnComment) {
        mUsnComment = usnComment;
    }

    public int getUsnStatus() {
        return mUsnStatus;
    }

    public void setUsnStatus(int usnStatus) {
        mUsnStatus = usnStatus;
    }

    public int getUsnOrder() {
        return mUsnOrder;
    }

    public void setUsnOrder(int usnOrder) {
        mUsnOrder = usnOrder;
    }

    public int getUsnProjectUid() {
        return mUsnProjectUid;
    }

    public void setUsnProjectUid(int usnProjectUid) {
        mUsnProjectUid = usnProjectUid;
    }

    public int getUsnMarkerUid() {
        return mUsnMarkerUid;
    }

    public void setUsnMarkerUid(int usnMarkerUid) {
        mUsnMarkerUid = usnMarkerUid;
    }

    public int getUsnTerm() {
        return mUsnTerm;
    }

    public void setUsnTerm(int usnTerm) {
        mUsnTerm = usnTerm;
    }

    public int getUsnReaded() {
        return mUsnReaded;
    }

    public void setUsnReaded(int usnReaded) {
        mUsnReaded = usnReaded;
    }
    
    public void setUsnReadedPlusOne() {
        mUsnReaded = mUsnReaded+1;
    }

    public int getUsnCollapsed() {
        return mUsnCollapsed;
    }

    public void setUsnCollapsed(int usnCollapsed) {
        mUsnCollapsed = usnCollapsed;
    }

    public int getUsnCustomerOrder() {
        return mUsnCustomerOrder;
    }

    public void setUsnCustomerOrder(int usnCustomerOrder) {
        mUsnCustomerOrder = usnCustomerOrder;
    }

    public int getUsnCustomerTerm() {
        return mUsnCustomerTerm;
    }

    public void setUsnCustomerTerm(int usnCustomerTerm) {
        mUsnCustomerTerm = usnCustomerTerm;
    }

    public int getUsnCategories() {
        return mUsnCategories;
    }

    public void setUsnCategories(int usnCategories) {
        mUsnCategories = usnCategories;
    }

    public int getUsnContacts() {
        return mUsnContacts;
    }

    public void setUsnContacts(int usnContacts) {
        mUsnContacts = usnContacts;
    }

    public int getSubTasksCount() {
        return mSubTasksCount;
    }

    public void setSubTasksCount(int subTasksCount) {
        mSubTasksCount = subTasksCount;
    }

    public int getSubTasksCountNotMade() {
        return mSubTasksCountNotMade;
    }

    public void setSubTasksCountNotMade(int subTasksCountNotMade) {
        mSubTasksCountNotMade = subTasksCountNotMade;
    }

    public int getSubTasksCountNotMadeAndNotRead() {
        return mSubTasksCountNotMadeAndNotRead;
    }

    public void setSubTasksCountNotMadeAndNotRead(int subTasksCountNotMadeAndNotRead) {
        mSubTasksCountNotMadeAndNotRead = subTasksCountNotMadeAndNotRead;
    }

    public List<Integer> getLabels() {
        if (mLabels == null) {
            mLabels = JsonTaskLabelsUtils.convertStringToListLabel(mLabelsString);
        }

        return mLabels;
    }

    public void setLabels(List<Integer> labels) {
        mLabels = labels;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public void setStatus(TaskStatus status) {
        mStatus = status.getCode();
    }

    public void setContacts(String contacts) {
        mContacts = contacts;
    }

    public void setUsn(long usn) {
        mUsn = usn;
    }

    private static SimpleDateFormat getSimpleDateFormat() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeHelper.DEFAULT_TIME_ZONE);

        return sdf;
    }

    public static final String[] VISIBLE_TASK_FIELDS = new String[] {//
    TaskContract.SUBTASKS_SIZE, TaskContract.SUBTASKS_SIZE_NOT_MADE, TaskContract.SUBTASKS_SIZE_NOT_READ, TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ,
            TaskContract.FIELD_UID, TaskContract.FIELD_UID_PARENT, TaskContract.FIELD_NAME, TaskContract.FIELD_COMMENT, TaskContract.FIELD_STATUS,
            TaskContract.FIELD_EMAIL_CUSTOMER, TaskContract.FIELD_EMAIL_PERFORMER, TaskContract.FIELD_TERM_BEGIN, TaskContract.FIELD_TERM_END,
            TaskContract.FIELD_TERM_BEGIN_CUSTOMER, TaskContract.FIELD_TERM_END_CUSTOMER, TaskContract.FIELD_UID_MARKER, TaskContract.FIELD_READED,
            TaskContract.HAS_FILES, TaskContract.MESSAGES_COUNT, TaskContract.LIST_LABELS

    };

    @Override
    public void fillKeyValue(String key, String value) {
        if (TaskContract.FIELD_UID.equalsIgnoreCase(key)) {
            mId = UUID.fromString(value);
        }

        else if (TaskContract.FIELD_UID_PARENT.equalsIgnoreCase(key)) {
            mParentId = UUID.fromString(value);
        }

        else if (TaskContract.FIELD_ORDER.equalsIgnoreCase(key)) {
            mOrder = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_COLLAPSED.equalsIgnoreCase(key)) {
            mCollapsed = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_NAME.equalsIgnoreCase(key)) {
            mName = value;
        }

        else if (TaskContract.FIELD_COMMENT.equalsIgnoreCase(key)) {
            mComment = value;
        }

        else if (TaskContract.FIELD_STATUS.equalsIgnoreCase(key)) {
            mStatus = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_TERM_BEGIN.equalsIgnoreCase(key)) {
            mTermBegin = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_TERM_END.equalsIgnoreCase(key)) {
            mTermEnd = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_EMAIL_PERFORMER.equalsIgnoreCase(key)) {
            mPerformer = value;
        }

        else if (TaskContract.FIELD_UID_PROJECT.equalsIgnoreCase(key)) {
            mProjectUid = UUID.fromString(value);
        }

        else if (TaskContract.FIELD_UID_MARKER.equalsIgnoreCase(key)) {
                mMarkerUid = UUID.fromString(value);
        }

        else if (TaskContract.FIELD_READED.equalsIgnoreCase(key)) {
            mReaded = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_ORDER_CUSTOMER.equalsIgnoreCase(key)) {
            mCustomerOrder = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_TERM_BEGIN_CUSTOMER.equalsIgnoreCase(key)) {
            mTermBeginCustomer = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_TERM_END_CUSTOMER.equalsIgnoreCase(key)) {
            mTermEndCustomer = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_EMAIL_CUSTOMER.equalsIgnoreCase(key)) {
            mCustomer = value;
        }

        else if (TaskContract.FIELD_CATEGORIES.equalsIgnoreCase(key)) {
            mCategories = value;
        }

        else if (TaskContract.FIELD_CONTACTS.equalsIgnoreCase(key)) {
            mContacts = value;
        }

        else if (TaskContract.FIELD_USN_ENTITY.equalsIgnoreCase(key)) {
            mUsn = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_UID_PARENT.equalsIgnoreCase(key)) {
            mUsnParentUid = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_EMAIL_PERORMER.equalsIgnoreCase(key)) {
            mUsnEmailPerformer = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_NAME.equalsIgnoreCase(key)) {
            mUsnName = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_COMMENT.equalsIgnoreCase(key)) {
            mUsnComment = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_STATUS.equalsIgnoreCase(key)) {
            mUsnStatus = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_ORDER.equalsIgnoreCase(key)) {
            mUsnOrder = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_UID_PROJECT.equalsIgnoreCase(key)) {
            mUsnProjectUid = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_UID_MARKER.equalsIgnoreCase(key)) {
            mUsnMarkerUid = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_TERM.equalsIgnoreCase(key)) {
            mUsnTerm = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_READED.equalsIgnoreCase(key)) {
            mUsnReaded = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_COLLAPSED.equalsIgnoreCase(key)) {
            mUsnCollapsed = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_CUSTOMER_ORDER.equalsIgnoreCase(key)) {
            mUsnCustomerOrder = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_CUSTOMER_TERM.equalsIgnoreCase(key)) {
            mUsnCustomerTerm = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_CATEGORIES.equalsIgnoreCase(key)) {
            mUsnCategories = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_USN_CONTACTS.equalsIgnoreCase(key)) {
            mUsnContacts = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_CREATE_TIME.equalsIgnoreCase(key)) {
            mCreationTime = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_PERFORM_TIME.equalsIgnoreCase(key)) {
            mPerformTime = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_COMPLETE_TIME.equalsIgnoreCase(key)) {
            mCompleteTime = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_SERIES_TYPE.equalsIgnoreCase(key)) {
            mSeriesType = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_AFTER_TYPE.equalsIgnoreCase(key)) {
            mSeriesAfterType = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_AFTER_COUNT.equalsIgnoreCase(key)) {
            mSeriesAfterCount = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_COUNT.equalsIgnoreCase(key)) {
            mSeriesWeekCount = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_MON.equalsIgnoreCase(key)) {
            mSeriesWeekMon = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_TUE.equalsIgnoreCase(key)) {
            mSeriesWeekTue = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_WED.equalsIgnoreCase(key)) {
            mSeriesWeekWed = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_THU.equalsIgnoreCase(key)) {
            mSeriesWeekThu = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_FRI.equalsIgnoreCase(key)) {
            mSeriesWeekFri = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_SAT.equalsIgnoreCase(key)) {
            mSeriesWeekSat = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_SERIES_WEEK_SUN.equalsIgnoreCase(key)) {
            mSeriesWeekSun = BaseSOAP.equalsOne(value);
        }

        else if (TaskContract.FIELD_SERIES_MONTH_TYPE.equalsIgnoreCase(key)) {
            mSeriesMonthType = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_MONTH_COUNT.equalsIgnoreCase(key)) {
            mSeriesMonthCount = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_MONTH_DAY.equalsIgnoreCase(key)) {
            mSeriesMonthDay = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_MONTH_WEEKTYPE.equalsIgnoreCase(key)) {
            mSeriesMonthWeekType = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_MONTH_DAYOFWEEK.equalsIgnoreCase(key)) {
            mSeriesMonthDayOfWeek = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_YEAR_TYPE.equalsIgnoreCase(key)) {
            mSeriesYearType = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_YEAR_MONTH.equalsIgnoreCase(key)) {
            mSeriesYearMonth = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_YEAR_MONTHDAY.equalsIgnoreCase(key)) {
            mSeriesYearMonthDay = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_YEAR_WEEKTYPE.equalsIgnoreCase(key)) {
            mSeriesYearWeekType = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_YEAR_DAYOFWEEK.equalsIgnoreCase(key)) {
            mSeriesYearDayOfWeek = Integer.parseInt(value);
        }

        else if (TaskContract.FIELD_SERIES_END.equalsIgnoreCase(key)) {
            mSeriesEnd = BaseSOAP.parseDate(value);
        }

        else if (TaskContract.FIELD_USN_FIELD_CREATETIME.equalsIgnoreCase(key)) {
            mUsnFieldCreateTime = Long.parseLong(value);
        }

        else if (TaskContract.FIELD_USN_FIELD_PERFORMTIME.equalsIgnoreCase(key)) {
            mUsnFieldPerformTime = Long.parseLong(value);
        }

        else if (TaskContract.FIELD_USN_FIELD_COMPLETETIME.equalsIgnoreCase(key)) {
            mUsnFieldCompleteTime = Long.parseLong(value);
        }

        else if (TaskContract.FIELD_USN_FIELD_SERIES.equalsIgnoreCase(key)) {
            mUsnFieldSeries = Long.parseLong(value);
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(TaskContract.SERVER_CLASS));

        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_UID, getId()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_UID_PARENT, getParentId()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_ORDER, getOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_COLLAPSED, isCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_NAME, getName()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_COMMENT, getComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_STATUS, getStatus()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_TERM_BEGIN, getTermBegin(), true));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_TERM_END, getTermEnd(), false));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_EMAIL_PERFORMER,//
                getPerformer() == null ? LTSettings.getInstance(null).getUserName() : getPerformer()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_UID_PROJECT, getProjectUid()));
                sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_READED, isReaded()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_ORDER_CUSTOMER, getCustomerOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_TERM_BEGIN_CUSTOMER, getTermBeginCustomer(), true));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_TERM_END_CUSTOMER, getTermEndCustomer(), false));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_EMAIL_CUSTOMER, getCustomer()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_CATEGORIES, getCategories()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_CONTACTS, getContacts()));

        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_CREATE_TIME, getCreationTime()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_PERFORM_TIME, getPerformTime()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_COMPLETE_TIME, getCompleteTime()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_TYPE, getSeriesType()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_AFTER_TYPE, getSeriesAfterType()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_AFTER_COUNT, getSeriesAfterCount()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_COUNT, getSeriesWeekCount()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_MON, isSeriesWeekMon()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_TUE, isSeriesWeekTue()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_WED, isSeriesWeekWed()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_THU, isSeriesWeekThu()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_FRI, isSeriesWeekFri()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_SAT, isSeriesWeekSat()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_WEEK_SUN, isSeriesWeekSun()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_MONTH_TYPE, getSeriesMonthType()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_MONTH_COUNT, getSeriesMonthCount()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_MONTH_DAY, getSeriesMonthDay()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_MONTH_WEEKTYPE, getSeriesMonthWeekType()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_MONTH_DAYOFWEEK, getSeriesMonthDayOfWeek()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_YEAR_TYPE, getSeriesYearType()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_YEAR_MONTH, getSeriesYearMonth()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_YEAR_MONTHDAY, getSeriesYearMonthDay()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_YEAR_WEEKTYPE, getSeriesYearWeekType()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_YEAR_DAYOFWEEK, getSeriesYearDayOfWeek()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_SERIES_END, getSeriesEnd()));

        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_FIELD_CREATETIME, getUsnFieldCreateTime()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_FIELD_PERFORMTIME, getUsnFieldPerformTime()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_FIELD_COMPLETETIME, getUsnFieldCompleteTime()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_FIELD_SERIES, getUsnFieldSeries()));

        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_ENTITY, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_UID_PARENT, getUsnParentUid()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_EMAIL_PERORMER, getUsnEmailPerformer()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_NAME, getUsnName()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_COMMENT, getUsnComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_STATUS, getUsnStatus()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_ORDER, getUsnOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_UID_PROJECT, getUsnProjectUid()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_UID_MARKER, getUsnMarkerUid()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_TERM, getUsnTerm()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_READED, getUsnReaded()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_COLLAPSED, getUsnCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_CUSTOMER_ORDER, getUsnCustomerOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_CUSTOMER_TERM, getUsnCustomerTerm()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_CATEGORIES, getUsnCategories()));
        sb.append(BaseSOAP.getXmlnsValueLine(TaskContract.FIELD_USN_CONTACTS, getUsnContacts()));

        sb.append(BaseSOAP.getClose(TaskContract.SERVER_CLASS));
    }

    public static UUID getUUIDFromString(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return UUID.fromString(value);
    }

    public static Date getDateFromLong(long value) {
        if (value == 0) {
            return null;
        }
        return new Date(value);
    }

    public boolean isWasCounted() {
        return mWasCounted;
    }

    public void setWasCounted(boolean wasCounted) {
        mWasCounted = wasCounted;
    }

    public boolean isHasFiles() {
        return mHasFiles;
    }

    public void setHasFiles(boolean hasFiles) {
        mHasFiles = hasFiles;
    }

    @Override
    public int compareTo(Task task) {
        if (this.getTermBegin().getTime() > task.getTermBegin().getTime()) {
            return 1;
        }

        else if (this.getTermBegin().getTime() < task.getTermBegin().getTime()) {
            return -1;
        }

        return 0;
    }

    @Override
    public String getServerClass() {
        return TaskContract.SERVER_CLASS;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        if (cv == null) {
            cv = new ContentValues(71);
        } else {
            cv.clear();
        }

        cv.put(TaskContract.FIELD_UID, String.valueOf(getId()));
        cv.put(TaskContract.FIELD_UID_PARENT,//
                getParentId() == null ? null : String.valueOf(getParentId()));
        cv.put(TaskContract.ORDERS, getOrder());
        cv.put(TaskContract.FIELD_COLLAPSED, isCollapsed());
        cv.put(TaskContract.FIELD_NAME, getName());
        cv.put(TaskContract.FIELD_COMMENT, getComment());
        cv.put(TaskContract.FIELD_STATUS, getStatus());
        cv.put(TaskContract.FIELD_TERM_BEGIN,//
                getTermBegin() == null ? null : getTermBegin().getTime());
        cv.put(TaskContract.FIELD_TERM_END,//
                getTermEnd() == null ? null : getTermEnd().getTime());
        cv.put(TaskContract.FIELD_EMAIL_PERFORMER, getPerformer());
        cv.put(TaskContract.FIELD_UID_PROJECT,//
                getProjectUid() == null ? null : String.valueOf(getProjectUid()));
        cv.put(TaskContract.FIELD_UID_MARKER,//
                getMarkerUid() == null ? null : String.valueOf(getMarkerUid()));
        cv.put(TaskContract.FIELD_READED, isReaded());
        cv.put(TaskContract.FIELD_ORDER_CUSTOMER, getCustomerOrder());
        cv.put(TaskContract.FIELD_TERM_BEGIN_CUSTOMER,//
                getTermBeginCustomer() == null ? null : getTermBeginCustomer().getTime());
        cv.put(TaskContract.FIELD_TERM_END_CUSTOMER,//
                getTermEndCustomer() == null ? null : getTermEndCustomer().getTime());
        cv.put(TaskContract.FIELD_EMAIL_CUSTOMER, getCustomer());
        cv.put(TaskContract.FIELD_CATEGORIES, getCategories());
        cv.put(TaskContract.FIELD_CONTACTS, getContacts());

        cv.put(TaskContract.FIELD_USN_ENTITY, getUsn());
        cv.put(TaskContract.FIELD_USN_UID_PARENT, getUsnParentUid());
        cv.put(TaskContract.FIELD_USN_EMAIL_PERORMER, getUsnEmailPerformer());
        cv.put(TaskContract.FIELD_USN_NAME, getUsnName());
        cv.put(TaskContract.FIELD_USN_COMMENT, getUsnComment());
        cv.put(TaskContract.FIELD_USN_STATUS, getUsnStatus());
        cv.put(TaskContract.FIELD_USN_ORDER, getUsnOrder());
        cv.put(TaskContract.FIELD_USN_UID_PROJECT, getUsnProjectUid());
        cv.put(TaskContract.FIELD_USN_UID_MARKER, getUsnMarkerUid());
        cv.put(TaskContract.FIELD_USN_TERM, getUsnTerm());
        cv.put(TaskContract.FIELD_USN_READED, getUsnReaded());
        cv.put(TaskContract.FIELD_USN_COLLAPSED, getUsnCollapsed());
        cv.put(TaskContract.FIELD_USN_CUSTOMER_ORDER, getUsnCustomerOrder());
        cv.put(TaskContract.FIELD_USN_CUSTOMER_TERM, getUsnCustomerTerm());
        cv.put(TaskContract.FIELD_USN_CATEGORIES, getUsnCategories());
        cv.put(TaskContract.FIELD_USN_CONTACTS, getUsnContacts());

        cv.put(TaskContract.SUBTASKS_SIZE, getSubTasksCount());
        cv.put(TaskContract.SUBTASKS_SIZE_NOT_READ, getSubTasksCountNotRead());
        cv.put(TaskContract.SUBTASKS_SIZE_NOT_MADE, getSubTasksCountNotMade());
        cv.put(TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ, getSubTasksCountNotMadeAndNotRead());
        cv.put(TaskContract.MESSAGES_COUNT, getMessagesCount());

        cv.put(TaskContract.LEFT_POINTER, getLeftPointer());
        cv.put(TaskContract.RIGHT_POINTER, getRightPointer());

        cv.put(TaskContract.FIELD_CREATE_TIME, getCreationTime() == null ? null : getCreationTime().getTime());
        cv.put(TaskContract.FIELD_PERFORM_TIME, getPerformTime() == null ? null : getPerformTime().getTime());
        cv.put(TaskContract.FIELD_COMPLETE_TIME, getCompleteTime() == null ? null : getCompleteTime().getTime());
        cv.put(TaskContract.FIELD_SERIES_TYPE, getSeriesType());
        cv.put(TaskContract.FIELD_SERIES_AFTER_TYPE, getSeriesAfterType());
        cv.put(TaskContract.FIELD_SERIES_AFTER_COUNT, getSeriesAfterCount());
        cv.put(TaskContract.FIELD_SERIES_WEEK_COUNT, getSeriesWeekCount());
        cv.put(TaskContract.FIELD_SERIES_WEEK_MON, isSeriesWeekMon());
        cv.put(TaskContract.FIELD_SERIES_WEEK_TUE, isSeriesWeekTue());
        cv.put(TaskContract.FIELD_SERIES_WEEK_WED, isSeriesWeekWed());
        cv.put(TaskContract.FIELD_SERIES_WEEK_THU, isSeriesWeekThu());
        cv.put(TaskContract.FIELD_SERIES_WEEK_FRI, isSeriesWeekFri());
        cv.put(TaskContract.FIELD_SERIES_WEEK_SAT, isSeriesWeekSat());
        cv.put(TaskContract.FIELD_SERIES_WEEK_SUN, isSeriesWeekSun());
        cv.put(TaskContract.FIELD_SERIES_MONTH_TYPE, getSeriesMonthType());
        cv.put(TaskContract.FIELD_SERIES_MONTH_COUNT, getSeriesMonthCount());
        cv.put(TaskContract.FIELD_SERIES_MONTH_DAY, getSeriesMonthDay());
        cv.put(TaskContract.FIELD_SERIES_MONTH_WEEKTYPE, getSeriesMonthWeekType());
        cv.put(TaskContract.FIELD_SERIES_MONTH_DAYOFWEEK, getSeriesMonthDayOfWeek());
        cv.put(TaskContract.FIELD_SERIES_YEAR_TYPE, getSeriesYearType());
        cv.put(TaskContract.FIELD_SERIES_YEAR_MONTH, getSeriesYearMonth());
        cv.put(TaskContract.FIELD_SERIES_YEAR_MONTHDAY, getSeriesYearMonthDay());
        cv.put(TaskContract.FIELD_SERIES_YEAR_WEEKTYPE, getSeriesYearWeekType());
        cv.put(TaskContract.FIELD_SERIES_YEAR_DAYOFWEEK, getSeriesYearDayOfWeek());
        cv.put(TaskContract.FIELD_SERIES_END, getSeriesEnd() == null ? null : getSeriesEnd().getTime());
        cv.put(TaskContract.FIELD_USN_FIELD_CREATETIME, getUsnFieldCreateTime());
        cv.put(TaskContract.FIELD_USN_FIELD_PERFORMTIME, getUsnFieldPerformTime());
        cv.put(TaskContract.FIELD_USN_FIELD_COMPLETETIME, getUsnFieldCompleteTime());
        cv.put(TaskContract.FIELD_USN_FIELD_SERIES, getUsnFieldSeries());

        return cv;
    }

    public Date getCreationTime() {
        return mCreationTime;
    }

    public void setCreationTime(Date creationTime) {
        mCreationTime = creationTime;
    }

    public Date getPerformTime() {
        return mPerformTime;
    }

    public void setPerformTime(Date performTime) {
        mPerformTime = performTime;
    }

    public Date getCompleteTime() {
        return mCompleteTime;
    }

    public void setCompleteTime(Date completeTime) {
        mCompleteTime = completeTime;
    }

    public int getSeriesType() {
        return mSeriesType;
    }

    public void setSeriesType(int seriesType) {
        mSeriesType = seriesType;
    }

    public int getSeriesAfterType() {
        return mSeriesAfterType;
    }

    public void setSeriesAfterType(int seriesAfterType) {
        mSeriesAfterType = seriesAfterType;
    }

    public int getSeriesAfterCount() {
        return mSeriesAfterCount;
    }

    public void setSeriesAfterCount(int seriesAfterCount) {
        mSeriesAfterCount = seriesAfterCount;
    }

    public int getSeriesWeekCount() {
        return mSeriesWeekCount;
    }

    public void setSeriesWeekCount(int seriesWeekCount) {
        mSeriesWeekCount = seriesWeekCount;
    }

    public boolean isSeriesWeekMon() {
        return mSeriesWeekMon;
    }

    public void setSeriesWeekMon(boolean seriesWeekMon) {
        mSeriesWeekMon = seriesWeekMon;
    }

    public boolean isSeriesWeekTue() {
        return mSeriesWeekTue;
    }

    public void setSeriesWeekTue(boolean seriesWeekTue) {
        mSeriesWeekTue = seriesWeekTue;
    }

    public boolean isSeriesWeekWed() {
        return mSeriesWeekWed;
    }

    public void setSeriesWeekWed(boolean seriesWeekWed) {
        mSeriesWeekWed = seriesWeekWed;
    }

    public boolean isSeriesWeekThu() {
        return mSeriesWeekThu;
    }

    public void setSeriesWeekThu(boolean seriesWeekThu) {
        mSeriesWeekThu = seriesWeekThu;
    }

    public boolean isSeriesWeekFri() {
        return mSeriesWeekFri;
    }

    public void setSeriesWeekFri(boolean seriesWeekFri) {
        mSeriesWeekFri = seriesWeekFri;
    }

    public boolean isSeriesWeekSat() {
        return mSeriesWeekSat;
    }

    public void setSeriesWeekSat(boolean seriesWeekSat) {
        mSeriesWeekSat = seriesWeekSat;
    }

    public boolean isSeriesWeekSun() {
        return mSeriesWeekSun;
    }

    public void setSeriesWeekSun(boolean seriesWeekSun) {
        mSeriesWeekSun = seriesWeekSun;
    }

    public int getSeriesMonthType() {
        return mSeriesMonthType;
    }

    public void setSeriesMonthType(int seriesMonthType) {
        mSeriesMonthType = seriesMonthType;
    }

    public int getSeriesMonthCount() {
        return mSeriesMonthCount;
    }

    public void setSeriesMonthCount(int seriesMonthCount) {
        mSeriesMonthCount = seriesMonthCount;
    }

    public int getSeriesMonthDay() {
        return mSeriesMonthDay;
    }

    public void setSeriesMonthDay(int seriesMonthDay) {
        mSeriesMonthDay = seriesMonthDay;
    }

    public int getSeriesMonthWeekType() {
        return mSeriesMonthWeekType;
    }

    public void setSeriesMonthWeekType(int seriesMonthWeekType) {
        mSeriesMonthWeekType = seriesMonthWeekType;
    }

    public int getSeriesMonthDayOfWeek() {
        return mSeriesMonthDayOfWeek;
    }

    public void setSeriesMonthDayOfWeek(int seriesMonthDayOfWeek) {
        mSeriesMonthDayOfWeek = seriesMonthDayOfWeek;
    }

    public int getSeriesYearType() {
        return mSeriesYearType;
    }

    public void setSeriesYearType(int seriesYearType) {
        mSeriesYearType = seriesYearType;
    }

    public int getSeriesYearMonth() {
        return mSeriesYearMonth;
    }

    public void setSeriesYearMonth(int seriesYearMonth) {
        mSeriesYearMonth = seriesYearMonth;
    }

    public int getSeriesYearMonthDay() {
        return mSeriesYearMonthDay;
    }

    public void setSeriesYearMonthDay(int seriesYearMonthDay) {
        mSeriesYearMonthDay = seriesYearMonthDay;
    }

    public int getSeriesYearWeekType() {
        return mSeriesYearWeekType;
    }

    public void setSeriesYearWeekType(int seriesYearWeekType) {
        mSeriesYearWeekType = seriesYearWeekType;
    }

    public int getSeriesYearDayOfWeek() {
        return mSeriesYearDayOfWeek;
    }

    public void setSeriesYearDayOfWeek(int seriesYearDayOfWeek) {
        mSeriesYearDayOfWeek = seriesYearDayOfWeek;
    }

    public Date getSeriesEnd() {
        return mSeriesEnd;
    }

    public void setSeriesEnd(Date seriesEnd) {
        mSeriesEnd = seriesEnd;
    }

    public long getUsnFieldCreateTime() {
        return mUsnFieldCreateTime;
    }

    public void setUsnFieldCreateTime(long usnFieldCreateTime) {
        mUsnFieldCreateTime = usnFieldCreateTime;
    }

    public long getUsnFieldPerformTime() {
        return mUsnFieldPerformTime;
    }

    public void setUsnFieldPerformTime(long usnFieldPerformTime) {
        mUsnFieldPerformTime = usnFieldPerformTime;
    }

    public long getUsnFieldCompleteTime() {
        return mUsnFieldCompleteTime;
    }

    public void setUsnFieldCompleteTime(long usnFieldCompleteTime) {
        mUsnFieldCompleteTime = usnFieldCompleteTime;
    }

    public long getUsnFieldSeries() {
        return mUsnFieldSeries;
    }

    public void setUsnFieldSeries(long usnFieldSeries) {
        mUsnFieldSeries = usnFieldSeries;
    }

    public String getLabelsString() {
        return mLabelsString;
    }

    public void setLabelsString(String labelsString) {
        mLabelsString = labelsString;
    }

    public String getSeriesTypeString() {
        return mSeriesTypeString;
    }

    public void setSeriesTypeString(String seriesTypeString) {
        mSeriesTypeString = seriesTypeString;
    }

    /** Текст периода повторений */
    public static void appendSeriesString(Context context, StringBuilder outSb, Task task) {
        if (task.getSeriesTypeString() != null) {
            outSb.append(SharedStrings.COMMA_C);
            outSb.append(SharedStrings.SPACE_C);
            outSb.append(task.getSeriesTypeString());
            return;
        }

        final SeriesType type = SeriesType.values()[task.getSeriesType()];
        if (type == SeriesType.NONE) {
            return;
        }

        final Resources r = context.getResources();
        final StringBuilder sb = new StringBuilder();

        sb.append(r.getString(type.getMainResId()));
        sb.append(SharedStrings.COLON_C);
        sb.append(SharedStrings.SPACE_C);

        switch (type) {
        case DAILY: {
            sb.append(r.getString(R.string.lang_task_recurrence_in));
            sb.append(SharedStrings.SPACE_C);
            sb.append(task.getSeriesAfterCount());
            sb.append(SharedStrings.SPACE_C);
            sb.append(r.getString(SeriesType.values()[task.getSeriesAfterType()].getAfterResId()));
            break;
        }

        case WEEKLY: {
            sb.append(r.getString(R.string.lang_task_recurrence_every_w));
            sb.append(SharedStrings.SPACE_C);
            sb.append(task.getSeriesWeekCount());
            sb.append(SharedStrings.SPACE_C);
            sb.append(r.getString(R.string.lang_task_recurrence_w_every));
            sb.append(SharedStrings.SPACE_C);

            final boolean[] isDayOfWeek = new boolean[7];
            isDayOfWeek[0] = task.isSeriesWeekMon();
            isDayOfWeek[1] = task.isSeriesWeekTue();
            isDayOfWeek[2] = task.isSeriesWeekWed();
            isDayOfWeek[3] = task.isSeriesWeekThu();
            isDayOfWeek[4] = task.isSeriesWeekFri();
            isDayOfWeek[5] = task.isSeriesWeekSat();
            isDayOfWeek[6] = task.isSeriesWeekSun();
            final String[] dayOfWeekNames = r.getStringArray(R.array.days_of_week_short);

            for (int i = 0; i < isDayOfWeek.length; i++) {
                if (isDayOfWeek[i]) {
                    sb.append(dayOfWeekNames[i]);
                    sb.append(SharedStrings.COMMA_C);
                    sb.append(SharedStrings.SPACE_C);
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            break;
        }

        case MONTHLY: {
            final boolean weeklyMonthType = task.getSeriesMonthType() == SeriesType.WEEKLY.ordinal();
            if (weeklyMonthType) {
                final String[] weekNames = r.getStringArray(R.array.lang_task_recurrence_weeks);
                final String[] dayOfWeekNames = r.getStringArray(R.array.days_of_week_short);
                sb.append(weekNames[task.getSeriesMonthWeekType() - 1]);
                sb.append(SharedStrings.SPACE_C);
                sb.append(dayOfWeekNames[task.getSeriesMonthDayOfWeek() - 1]);
                sb.append(SharedStrings.SPACE_C);
            }

            sb.append(r.getString(R.string.lang_task_recurrence_every_m));
            sb.append(SharedStrings.SPACE_C);
            sb.append(task.getSeriesMonthCount());
            sb.append(SharedStrings.SPACE_C);
            sb.append(r.getString(R.string.lang_task_recurrence_m_every));

            if (!weeklyMonthType) {
                sb.append(SharedStrings.SPACE_C);
                sb.append(task.getSeriesMonthDay());
                sb.append(SharedStrings.SPACE_C);
                sb.append(r.getString(R.string.lang_task_recurrence_m_every_d));
            }
            break;
        }

        case YEARLY: {
            sb.append(r.getString(R.string.lang_task_recurrence_every_y));
            sb.append(SharedStrings.SPACE_C);

            final String[] monthNames = r.getStringArray(R.array.months_full);
            if (task.getSeriesYearType() == SeriesType.WEEKLY.ordinal()) {
                final String[] weekNames = r.getStringArray(R.array.lang_task_recurrence_weeks);
                final String[] dayOfWeekNames = r.getStringArray(R.array.days_of_week_short);

                sb.append(monthNames[task.getSeriesYearMonth() - 1]);
                sb.append(SharedStrings.SPACE_C);
                sb.append(weekNames[task.getSeriesYearWeekType() - 1]);
                sb.append(SharedStrings.SPACE_C);
                sb.append(dayOfWeekNames[task.getSeriesYearDayOfWeek() - 1]);
            } else {
                sb.append(task.getSeriesYearMonthDay());
                sb.append(SharedStrings.SPACE_C);
                sb.append(monthNames[task.getSeriesYearMonth() - 1]);
            }
            break;
        }

        default:
            break;
        }

        task.setSeriesTypeString(sb.toString());

        outSb.append(SharedStrings.COMMA_C);
        outSb.append(SharedStrings.SPACE_C);
        outSb.append(task.getSeriesTypeString());
    }
}