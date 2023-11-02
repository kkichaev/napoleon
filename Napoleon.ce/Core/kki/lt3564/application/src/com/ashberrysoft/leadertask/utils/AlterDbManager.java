package com.ashberrysoft.leadertask.utils;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.DeletedTaskContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.DeleteUidContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.CalendarData;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SimpleNotify;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.DeleteUid;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SendUid;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SetBlocking;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.TaskNotify;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeLink;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryLink;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.FocusLink;
import com.ashberrysoft.leadertask.modern.domains.link.FocusTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ForMeLink;
import com.ashberrysoft.leadertask.modern.domains.link.ForMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.InboxLink;
import com.ashberrysoft.leadertask.modern.domains.link.InboxTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.InworkLink;
import com.ashberrysoft.leadertask.modern.domains.link.InworkTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.OverdueLink;
import com.ashberrysoft.leadertask.modern.domains.link.OverdueTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ReadyLink;
import com.ashberrysoft.leadertask.modern.domains.link.ReadyTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.TaskLink;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.UnreadLink;
import com.ashberrysoft.leadertask.modern.domains.link.UnreadTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.FullTasksResetHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import static android.view.View.Y;

public class AlterDbManager implements SharedStrings {

    private enum SupportedDbVersion {
        CURRENT(DbHelper.DATABASE_VERSION, "13.3.13", 271), //
        A(1309251017, "3.2.1", 27), //
        B(1309251018, "3.3", 28), //
        C(1309251019, "3.4", 30),
        D(1309251020, "3.5", 31),
        F(1309251021, "3.5.1.4", 35),
        G(1309251022, "3.5.4.2", 37),
        H(1309251023, "3.5.6", 39),
        I(1309251024, "3.5.6.2", 39),
        J(1309251025, "3.5.6.3", 39),
        K(1309251026, "3.5.6.4", 39),
        L(1309251027, "3.5.6.5", 39),
        M(1309251028, "3.7", 41),
        N(1309251029, "3.7.0.1", 41),
        O(1309251030, "3.7.0.2", 41),
        P(1309251031, "3.7.1.1", 42),
        Q(1309251032, "4.0.2", 45),
        R(1309251033, "4.0.2", 45),
        S(1309251034, "4.1.0.2", 47),
        T(1309251035, "12.6.25", 135),
        U(1309251036, "12.7.6", 152),
        V(1309251037, "12.7.15", 163),
        W(1309251038,  "12.7.16", 165),
        X(1309251039,  "12.7.23", 171),
        Y(1309251040,  "12.8.10", 190),
        Z(1309251041,  "14.2.1", 316),
        ZA(1309251042,  "14.3", 330);

        private int mDbVersion;

        private SupportedDbVersion(int dbVersion, String appVersionName, int versionCode) {
            mDbVersion = dbVersion;
        }

        public int getDbVersion() {
            return mDbVersion;
        }
    }

    // VALUE's
    private SQLiteDatabase mDb;
    private ConnectionSource mConnectionSource;
    private SupportedDbVersion mVersion;
    private StringBuilder mStringBuilder;
    private boolean mRequiredApiLevel;
    private Context mContext;

    public static AlterDbManager newInstance(Context context, SQLiteDatabase database, ConnectionSource cs,//
            int oldVersion, int newVersion) {
        for (SupportedDbVersion version : SupportedDbVersion.values()) {
            if (version.getDbVersion() == oldVersion) {
                return new AlterDbManager(context, database, cs, oldVersion, newVersion, version);
            }
        }
        return null;
    }

    private AlterDbManager(Context context, SQLiteDatabase database, ConnectionSource cs,//
            int oldVersion, int newVersion, SupportedDbVersion version) {
        mContext = context.getApplicationContext();
        mDb = database;
        mConnectionSource = cs;
        mVersion = version;

        mRequiredApiLevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        mStringBuilder = new StringBuilder();
    }

    public void runAlteration() throws Exception {
        final long start = System.currentTimeMillis();

        while (mVersion != SupportedDbVersion.CURRENT) {
            switch (mVersion) {
            case A:
                blockA();
                break;

            case B:
                blockB();
                break;

            case C:
                blockC();
                break;

            case D:
                blockD();
                break;

            case F:
                blockF();
                break;

            case G:
                blockG();
                break;

            case H:
                blockH();
                break;

            case I:
                blockI();
                break;

            case J:
                blockJ();
                break;

            case K:
                blockK();
                break;

            case L:
                blockL();
                break;

            case M:
                blockM();
                break;

            case N:
                blockN();
                break;

            case O:
                blockO();
                break;

            case P:
                blockP();
                break;

            case Q:
                blockQ();
                break;

            case R:
                blockR();
                break;

            case S:
                blockS();
                break;

            case T:
                blockT();
                break;

            case U:
                blockU();
                break;

            case V:
                blockV();
                break;

            case W:
                blockW();
                break;

            case X:
                blockX();
                break;

            case Y:
                blockY();
                break;

            case Z:
                blockZ();
                break;

            case ZA:
                blockZA();
                break;
            default:
                break;
            }

            nextVersion();
        }

        new FullTasksResetHelper(mContext, true);

        if (LTSettings.getInstance(null) != null) {
            LTSettings.getInstance(null).setRunSyncAfterVersionUpgrade(true);
        }

        Utils.toLog("runAlteration : " + (System.currentTimeMillis() - start));
    }

    private void nextVersion() {
        if (mVersion.ordinal() == 0) {
            return;
        }

        final int next = mVersion.ordinal() + 1;
        if (next < SupportedDbVersion.values().length) {
            mVersion = SupportedDbVersion.values()[next];
        } else {
            mVersion = SupportedDbVersion.CURRENT;
        }
    }

    private void exec(boolean alter, String... strings) {
        if (alter) {
            mStringBuilder.append(ALTER_TABLE);
        }

        for (String string : strings) {
            mStringBuilder.append(string);
        }

        mDb.execSQL(mStringBuilder.toString());
        Utils.clearStringBuilder(mStringBuilder);
    }

    private void set(String tableName, String columnName, Object value) {
        exec(false, UPDATE, tableName, SET, columnName, EQUALS, String.valueOf(value));
    }

    private void set(String tableName, String columnName, Object newValue, Object oldValue) {
        if (oldValue == null) {
            exec(false, UPDATE, tableName, SET, columnName, EQUALS, String.valueOf(newValue), WHERE, columnName, IS_NULL);
        }
    }

    private void set(String toTable, String[] toColumn, String fromTable, String fromColumn[]) {
        set(toTable, toColumn, fromTable, fromColumn, null);
    }

    private void set(String toTable, String[] toColumn, String fromTable, String fromColumn[], String where) {
        final StringBuilder sbToColumn = new StringBuilder();
        final StringBuilder sbFromColumn = new StringBuilder();

        sbToColumn.append(BRACE_OPEN);
        for (int i = 0; i < fromColumn.length; i++) {
            sbToColumn.append(toColumn[i]);
            sbFromColumn.append(fromColumn[i]);
            if (i < fromColumn.length - 1) {
                sbToColumn.append(COMMA_C);
                sbFromColumn.append(COMMA_C);
            }
        }
        sbToColumn.append(BRACE_CLOSE);

        exec(false, INSERT_INTO, toTable, sbToColumn.toString(), SELECT, sbFromColumn.toString(), FROM, fromTable, where == null ? " " : where);
    }

    private void dropIndex(String indexName) {
        exec(false, DROP_INDEX_IF_EXISTS, indexName);
    }

    private void transaction(boolean begin) {
        if (mRequiredApiLevel) {
            exec(false, begin ? BEGIN_TRANSACTION : COMMIT);
        }
    }

    private String upper(String column) {
        StringBuilder sb = new StringBuilder();
        sb.append("upper(").append(column).append(")");
        return sb.toString();
    }

    // ============ Block A ============ //
    private void blockA() throws Exception {
        blockA_dropIndexes();
        blockA_markers();
        blockA_projects();
        blockA_tasks();
        blockA_removeOldTables();
        blockA_createNewTables();
    }

    private void blockA_dropIndexes() {
        transaction(true);

        dropIndex("projects_EmailCreator_idx");
        dropIndex("projects_Name_idx");
        dropIndex("tasklabel_TaskUID_idx");
        dropIndex("tasks_Categories_idx");
        dropIndex("tasks_Contacts_idx");
        dropIndex("tasks_EmailCustomer_idx");
        dropIndex("tasks_EmailPerformer_idx");
        dropIndex("tasks_Name_idx");
        dropIndex("tasks_OrderCustomer_idx");
        dropIndex("tasks_Order_idx");
        dropIndex("tasks_Readed_idx");
        dropIndex("tasks_Status_idx");
        dropIndex("tasks_TermBeginCustomer_idx");
        dropIndex("tasks_TermBegin_idx");
        dropIndex("tasks_TermEndCustomer_idx");
        dropIndex("tasks_TermEnd_idx");
        dropIndex("tasks_UIDParent_idx");
        dropIndex("tasks_UidMarker_idx");
        dropIndex("tasks_UidProject_idx");
        dropIndex("tasks_field_subtasks_size_idx");
        dropIndex("tasks_field_subtasks_size_not_read_idx");
        dropIndex("tasks_field_subtasks_size_not_made_idx");
        dropIndex("tasks_field_subtasks_size_not_made_and_not_read_idx");
        dropIndex("tasks_lft_idx");
        dropIndex("tasks_rgt_idx");

        transaction(false);
    }

    private void blockA_markers() throws SQLException {
        TableUtils.createTable(mConnectionSource, Marker.class);

        final String marker = "marker";
        final String[] markerColumns = { "UID", "mBackColor", "mTextColor", "mName", "mIsUppercase", "mOrder", "__usn_entity", "mUsnBackColor",
                "mUsnIsUppercase", "mUsnName", "mUsnOrder", "mUsnTextColor"};

        transaction(true);
        set(Marker.TABLE_NAME, Marker.COLUMNS, marker, markerColumns);
        exec(false, DROP_TABLE_IF_EXISTS, marker);
        transaction(false);
    }

    private void blockA_projects() throws SQLException {
        final String project = "t_project";

        exec(true, Project.TABLE_NAME, RENAME, TO, project);
        TableUtils.createTable(mConnectionSource, Project.class);

        final String[] projectColumns = { "UIDParent", "mComment", "EmailCreator", "mParent_id", "Name", "UID", "__usn_entity", "\"Order\"", "mFaforite",
                "Collapsed", "mClosed", "Show", "mGroup", "mUsnClosed", "mUsnCollapsed", "mUsnComment", "mUsnFavorite", "mUsnGroup", "mUsnName", "mUsnOrder",
                "mUsnParent", "mUsnSharedUsers", "mUsnShow" };

        transaction(true);
        set(Project.TABLE_NAME, projectColumns, project, projectColumns);
        exec(false, DROP_TABLE_IF_EXISTS, project);
        transaction(false);
    }

    private void blockA_tasks() throws SQLException {
        final String task = "t_tasks";

        exec(true, TaskContract.TABLE_NAME, RENAME, TO, task);
        TableUtils.createTable(mConnectionSource, Task.class);

        final String[] fromTaskColumns = { "UID", "UIDParent", "\"Order\"", "mCollapsed", "Name", "mComment", "Status", "TermBegin", "TermEnd",
                "EmailPerformer", "UidProject", "UidMarker", "Readed", "OrderCustomer", "TermBeginCustomer", "TermEndCustomer", "EmailCustomer", "Categories",
                "Contacts", "__usn_entity", "mUsnParent", "mUsnPerformer", "mUsnName", "mUsnComment", "mUsnStatus", "mUsnOrder", "__usn_field_uid_project",
                "mUsnUIDMarker", "mUsnTerm", "mUsnIsReaded", "mUsnCollapsed", "mUsnCustomerOrder", "mUsnTermCustomer", "mUsnCategories", "mUsnContacts",
                "field_subtasks_size", "field_subtasks_size_not_read", "field_subtasks_size_not_made", "field_subtasks_size_not_made_and_not_read",
                "messagesCount", "lft", "rgt" };

        final String[] toTaskColumns = { TaskContract.FIELD_UID, TaskContract.FIELD_UID_PARENT, TaskContract.ORDERS, TaskContract.FIELD_COLLAPSED,
                TaskContract.FIELD_NAME, TaskContract.FIELD_COMMENT, TaskContract.FIELD_STATUS, TaskContract.FIELD_TERM_BEGIN, TaskContract.FIELD_TERM_END,
                TaskContract.FIELD_EMAIL_PERFORMER, TaskContract.FIELD_UID_PROJECT, TaskContract.FIELD_UID_MARKER, TaskContract.FIELD_READED,
                TaskContract.FIELD_ORDER_CUSTOMER, TaskContract.FIELD_TERM_BEGIN_CUSTOMER, TaskContract.FIELD_TERM_END_CUSTOMER,
                TaskContract.FIELD_EMAIL_CUSTOMER, TaskContract.FIELD_CATEGORIES, TaskContract.FIELD_CONTACTS, TaskContract.FIELD_USN_ENTITY,
                TaskContract.FIELD_USN_UID_PARENT, TaskContract.FIELD_USN_EMAIL_PERORMER, TaskContract.FIELD_USN_NAME, TaskContract.FIELD_USN_COMMENT,
                TaskContract.FIELD_USN_STATUS, TaskContract.FIELD_USN_ORDER, TaskContract.FIELD_USN_UID_PROJECT, TaskContract.FIELD_USN_UID_MARKER,
                TaskContract.FIELD_USN_TERM, TaskContract.FIELD_USN_READED, TaskContract.FIELD_USN_COLLAPSED, TaskContract.FIELD_USN_CUSTOMER_ORDER,
                TaskContract.FIELD_USN_CUSTOMER_TERM, TaskContract.FIELD_USN_CATEGORIES, TaskContract.FIELD_USN_CONTACTS, TaskContract.SUBTASKS_SIZE,
                TaskContract.SUBTASKS_SIZE_NOT_READ, TaskContract.SUBTASKS_SIZE_NOT_MADE, TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ,
                TaskContract.MESSAGES_COUNT, TaskContract.LEFT_POINTER, TaskContract.RIGHT_POINTER,

                TaskContract.WAS_COUNTED, TaskContract.HAS_FILES, TaskContract.LIST_LABELS};

        transaction(true);
        set(TaskContract.TABLE_NAME, toTaskColumns, task, fromTaskColumns);
        exec(false, DROP_TABLE_IF_EXISTS, task);
        transaction(false);
    }

    private void blockA_removeOldTables() {
        exec(false, DROP_TABLE_IF_EXISTS, "notifications");
    }

    private void blockA_createNewTables() throws SQLException {
        TableUtils.createTable(mConnectionSource, CalendarData.class);
        TableUtils.createTable(mConnectionSource, SimpleNotify.class);
        TableUtils.createTable(mConnectionSource, SyncInfo.class);
        TableUtils.createTable(mConnectionSource, TaskFile.class);
        TableUtils.createTable(mConnectionSource, UidToDelete.class);
    }

    // ============ Block B ============ //

    private void blockB() throws SQLException {
        blockB_tasks();
        blockB_createNewTables();
    }

    private void blockB_tasks() throws SQLException {
        final String[] fromTaskColumns = { TaskContract.FIELD_UID, TaskContract.FIELD_UID_PARENT, TaskContract.ORDERS, TaskContract.FIELD_COLLAPSED,
                TaskContract.FIELD_NAME, TaskContract.FIELD_COMMENT, TaskContract.FIELD_STATUS, TaskContract.FIELD_TERM_BEGIN, TaskContract.FIELD_TERM_END,
                TaskContract.FIELD_EMAIL_PERFORMER, TaskContract.FIELD_UID_PROJECT, TaskContract.FIELD_UID_MARKER, TaskContract.FIELD_READED,
                TaskContract.FIELD_ORDER_CUSTOMER, TaskContract.FIELD_TERM_BEGIN_CUSTOMER, TaskContract.FIELD_TERM_END_CUSTOMER,
                TaskContract.FIELD_EMAIL_CUSTOMER, TaskContract.FIELD_CATEGORIES, TaskContract.FIELD_CONTACTS, TaskContract.FIELD_USN_ENTITY,
                TaskContract.FIELD_USN_UID_PARENT, TaskContract.FIELD_USN_EMAIL_PERORMER, TaskContract.FIELD_USN_NAME, TaskContract.FIELD_USN_COMMENT,
                TaskContract.FIELD_USN_STATUS, TaskContract.FIELD_USN_ORDER, TaskContract.FIELD_USN_UID_PROJECT, TaskContract.FIELD_USN_UID_MARKER,
                TaskContract.FIELD_USN_TERM, TaskContract.FIELD_USN_READED, TaskContract.FIELD_USN_COLLAPSED, TaskContract.FIELD_USN_CUSTOMER_ORDER,
                TaskContract.FIELD_USN_CUSTOMER_TERM, TaskContract.FIELD_USN_CATEGORIES, TaskContract.FIELD_USN_CONTACTS, TaskContract.SUBTASKS_SIZE,
                TaskContract.SUBTASKS_SIZE_NOT_READ, TaskContract.SUBTASKS_SIZE_NOT_MADE, TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ,
                TaskContract.MESSAGES_COUNT, TaskContract.LEFT_POINTER, TaskContract.RIGHT_POINTER, TaskContract.WAS_COUNTED, TaskContract.HAS_FILES,
                TaskContract.LIST_LABELS };

        blockB_tasks_dropIndexes(fromTaskColumns);

        final String temporaryTableName = "t_tasks";

        exec(true, TaskContract.TABLE_NAME, RENAME, TO, temporaryTableName);
        TableUtils.createTable(mConnectionSource, Task.class);

        transaction(true);

        set(TaskContract.TABLE_NAME, fromTaskColumns, temporaryTableName, fromTaskColumns);
        exec(false, DROP_TABLE_IF_EXISTS, temporaryTableName);

        set(TaskContract.TABLE_NAME, TaskContract.FIELD_USN_ENTITY, 0);
        set(TaskContract.TABLE_NAME, TaskContract.FIELD_USN_FIELD_SERIES, 0);
        set(TaskContract.TABLE_NAME, TaskContract.FIELD_SERIES_TYPE, 0);

        transaction(false);
    }

    private void blockB_tasks_dropIndexes(String[] columns) {
        final String prefix = "tasks_";
        final String postfix = "_idx";
        final StringBuilder sb = new StringBuilder();
        transaction(true);

        for (String column : columns) {
            Utils.clearStringBuilder(sb);

            sb.append(prefix);
            sb.append(column);
            sb.append(postfix);

            dropIndex(sb.toString());
        }

        transaction(false);
    }

    private void blockB_createNewTables() throws SQLException {
        TableUtils.createTable(mConnectionSource, Emp.class);
        TableUtils.createTable(mConnectionSource, Employee.class);
    }

    // ============ Block C ============ //

    private void blockC() throws SQLException {
        blockC_createTables();
        blockC_moveTasks();
        blockC_moveDeletedTask();
    }

    private void createTables(Class<?>... classes) {
        if (classes != null && classes.length > 0) {
            for (Class<?> c : classes) {
                try {
                    TableUtils.createTableIfNotExists(mConnectionSource, c);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void blockC_createTables() throws SQLException {
        createTables(LTask.class, DeleteUid.class, SendUid.class, CompletedTask.class, VerticalDepthTask.class, SetBlocking.class, TaskNotify.class,
                TaskLink.class, TaskTotalLink.class, CalendarLink.class, CalendarTotalLink.class, InboxLink.class, InboxTotalLink.class, ByMeLink.class,
                ByMeTotalLink.class, ForMeLink.class, ForMeTotalLink.class, ProjectLink.class, ProjectTotalLink.class, CategoryLink.class,
                CategoryTotalLink.class);
    }

    private void blockC_moveTasks() throws SQLException {

        final String[] toLTaskColumns = { LTaskContract.Categories, LTaskContract.Collapsed, LTaskContract.Comment, LTaskContract.CompleteTime,
                LTaskContract.Contacts, LTaskContract.CreateTime, LTaskContract.EmailCustomer, LTaskContract.EmailPerformer, LTaskContract.Name,
                LTaskContract.OrderCustomer, LTaskContract.PerformTime, LTaskContract.Readed, LTaskContract.SeriesAfterCount, LTaskContract.SeriesAfterType,
                LTaskContract.SeriesEnd, LTaskContract.SeriesMonthCount, LTaskContract.SeriesMonthDay, LTaskContract.SeriesMonthDayOfWeek,
                LTaskContract.SeriesMonthType, LTaskContract.SeriesMonthWeekType, LTaskContract.SeriesType, LTaskContract.SeriesWeekCount,
                LTaskContract.SeriesWeekFri, LTaskContract.SeriesWeekMon, LTaskContract.SeriesWeekSat, LTaskContract.SeriesWeekSun,
                LTaskContract.SeriesWeekThu, LTaskContract.SeriesWeekTue, LTaskContract.SeriesWeekWed, LTaskContract.SeriesYearDayOfWeek,
                LTaskContract.SeriesYearMonth, LTaskContract.SeriesYearMonthDay, LTaskContract.SeriesYearType, LTaskContract.SeriesYearWeekType,
                LTaskContract.Status, LTaskContract.TermBegin, LTaskContract.TermBeginCustomer, LTaskContract.TermEnd, LTaskContract.TermEndCustomer,
                LTaskContract.UidMarker, LTaskContract.UIDParent, LTaskContract.UidProject, LTaskContract.UsnFieldCategories, LTaskContract.UsnFieldCollapsed,
                LTaskContract.UsnFieldComment, LTaskContract.UsnFieldCompletetime, LTaskContract.UsnFieldContacts, LTaskContract.UsnFieldCreatetime,
                LTaskContract.UsnFieldCustomerOrder, LTaskContract.UsnFieldCustomerTerm, LTaskContract.UsnFieldEmailPerformer, LTaskContract.UsnFieldName,
                LTaskContract.UsnFieldOrder, LTaskContract.UsnFieldPerformtime, LTaskContract.UsnFieldReaded, LTaskContract.UsnFieldSeries,
                LTaskContract.UsnFieldStatus, LTaskContract.UsnFieldTerm, LTaskContract.UsnFieldUidMarker, LTaskContract.UsnFieldUidParent,
                LTaskContract.UsnFieldUidProject, LTaskContract.Orders, LTaskContract.Uid, LTaskContract.UsnEntity };

        final String[] fromTaskColumns = {

        upper(TaskContract.FIELD_CATEGORIES), TaskContract.FIELD_COLLAPSED, TaskContract.FIELD_COMMENT, TaskContract.FIELD_COMPLETE_TIME,
                TaskContract.FIELD_CONTACTS, TaskContract.FIELD_CREATE_TIME, TaskContract.FIELD_EMAIL_CUSTOMER, TaskContract.FIELD_EMAIL_PERFORMER,
                TaskContract.FIELD_NAME, TaskContract.FIELD_ORDER_CUSTOMER, TaskContract.FIELD_PERFORM_TIME, TaskContract.FIELD_READED,
                TaskContract.FIELD_SERIES_AFTER_COUNT, TaskContract.FIELD_SERIES_AFTER_TYPE, TaskContract.FIELD_SERIES_END,
                TaskContract.FIELD_SERIES_MONTH_COUNT, TaskContract.FIELD_SERIES_MONTH_DAY, TaskContract.FIELD_SERIES_MONTH_DAYOFWEEK,
                TaskContract.FIELD_SERIES_MONTH_TYPE, TaskContract.FIELD_SERIES_MONTH_WEEKTYPE, TaskContract.FIELD_SERIES_TYPE,
                TaskContract.FIELD_SERIES_WEEK_COUNT, TaskContract.FIELD_SERIES_WEEK_FRI, TaskContract.FIELD_SERIES_WEEK_MON,
                TaskContract.FIELD_SERIES_WEEK_SAT, TaskContract.FIELD_SERIES_WEEK_SUN, TaskContract.FIELD_SERIES_WEEK_THU, TaskContract.FIELD_SERIES_WEEK_TUE,
                TaskContract.FIELD_SERIES_WEEK_WED, TaskContract.FIELD_SERIES_YEAR_DAYOFWEEK, TaskContract.FIELD_SERIES_YEAR_MONTH,
                TaskContract.FIELD_SERIES_YEAR_MONTHDAY, TaskContract.FIELD_SERIES_YEAR_TYPE, TaskContract.FIELD_SERIES_YEAR_WEEKTYPE,
                TaskContract.FIELD_STATUS, TaskContract.FIELD_TERM_BEGIN, TaskContract.FIELD_TERM_BEGIN_CUSTOMER, TaskContract.FIELD_TERM_END,
                TaskContract.FIELD_TERM_END_CUSTOMER, upper(TaskContract.FIELD_UID_MARKER), upper(TaskContract.FIELD_UID_PARENT),
                upper(TaskContract.FIELD_UID_PROJECT), TaskContract.FIELD_USN_CATEGORIES, TaskContract.FIELD_USN_COLLAPSED, TaskContract.FIELD_USN_COMMENT,
                TaskContract.FIELD_USN_FIELD_COMPLETETIME, TaskContract.FIELD_USN_CONTACTS, TaskContract.FIELD_USN_FIELD_CREATETIME,
                TaskContract.FIELD_USN_CUSTOMER_ORDER, TaskContract.FIELD_USN_CUSTOMER_TERM, TaskContract.FIELD_USN_EMAIL_PERORMER,
                TaskContract.FIELD_USN_NAME, TaskContract.FIELD_USN_ORDER, TaskContract.FIELD_USN_FIELD_PERFORMTIME, TaskContract.FIELD_USN_READED,
                TaskContract.FIELD_USN_FIELD_SERIES, TaskContract.FIELD_USN_STATUS, TaskContract.FIELD_USN_TERM, TaskContract.FIELD_USN_UID_MARKER,
                TaskContract.FIELD_USN_UID_PARENT, TaskContract.FIELD_USN_UID_PROJECT, TaskContract.ORDERS, upper(TaskContract.FIELD_UID),
                TaskContract.FIELD_USN_ENTITY,};
        set(LTaskContract.TABLE_NAME, toLTaskColumns, TaskContract.TABLE_NAME, fromTaskColumns);

        set(LTaskContract.TABLE_NAME, LTaskContract.TermEndCustomer, 0, null);
        set(LTaskContract.TABLE_NAME, LTaskContract.TermEnd, 0, null);
        set(LTaskContract.TABLE_NAME, LTaskContract.TermBeginCustomer, 0, null);
        set(LTaskContract.TABLE_NAME, LTaskContract.TermBegin, 0, null);
        set(LTaskContract.TABLE_NAME, LTaskContract.SeriesEnd, 0, null);

        TableUtils.clearTable(mConnectionSource, Task.class);
    }

    private void blockC_moveDeletedTask() {
        try {
            final String[] fromColumns = { upper(DeletedTaskContract.Uid) };
            final String[] toColumns = { DeleteUidContract.Uid };
            set(DeleteUidContract.TABLE_NAME, toColumns, DeletedTaskContract.TABLE_NAME, fromColumns, null);

            set(DeleteUidContract.TABLE_NAME, DeleteUidContract.LionName, "'LionTask'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============ Block D ============ //

    private void blockD() throws SQLException {
        blockD_links();
    }

    private void blockD_setTasks(String tableName, String columnName, String columnTaskId)
    {
        // UPDATE ForMeLink SET status = ( SELECT Status FROM LionTask WHERE LionTask._id=ForMeLink.taskid );

        final StringBuilder SelectStatus = new StringBuilder();
        SelectStatus.append(BRACE_OPEN);
        SelectStatus.append(SELECT);
        SelectStatus.append(TaskContract.FIELD_STATUS);
        SelectStatus.append(FROM);
        SelectStatus.append("LionTask");
        SelectStatus.append(WHERE);
        SelectStatus.append("LionTask._id");
        SelectStatus.append(EQUALS);
        SelectStatus.append(tableName);
        SelectStatus.append(DOT_C);
        SelectStatus.append(columnTaskId);
        SelectStatus.append(BRACE_CLOSE);

        set(tableName, columnName, SelectStatus.toString());
    }

    private void blockD_links() throws SQLException {
        exec(true, LionMetaData.ForMeLinkContract.TABLE_NAME, ADD, LionMetaData.ForMeLinkContract.Status, INTEGER);
        exec(true, LionMetaData.CalendarLinkContract.TABLE_NAME, ADD, LionMetaData.CalendarLinkContract.Status, INTEGER);
        exec(true, LionMetaData.InboxLinkContract.TABLE_NAME, ADD, LionMetaData.InboxLinkContract.Status, INTEGER);
        exec(true, LionMetaData.ByMeLinkContract.TABLE_NAME, ADD, LionMetaData.ByMeLinkContract.Status, INTEGER);
        exec(true, LionMetaData.ProjectLinkContract.TABLE_NAME, ADD, LionMetaData.ProjectLinkContract.Status, INTEGER);
        exec(true, LionMetaData.CategoryLinkContract.TABLE_NAME, ADD, LionMetaData.CategoryLinkContract.Status, INTEGER);
        exec(true, LionMetaData.TaskLinkContract.TABLE_NAME, ADD, LionMetaData.TaskLinkContract.Status, INTEGER);

        blockD_setTasks(LionMetaData.ForMeLinkContract.TABLE_NAME, LionMetaData.ForMeLinkContract.Status, LionMetaData.ForMeLinkContract.TaskId);
        blockD_setTasks(LionMetaData.CalendarLinkContract.TABLE_NAME, LionMetaData.CalendarLinkContract.Status, LionMetaData.CalendarLinkContract.TaskId);
        blockD_setTasks(LionMetaData.InboxLinkContract.TABLE_NAME, LionMetaData.InboxLinkContract.Status, LionMetaData.InboxLinkContract.TaskId);
        blockD_setTasks(LionMetaData.ByMeLinkContract.TABLE_NAME, LionMetaData.ByMeLinkContract.Status, LionMetaData.ByMeLinkContract.TaskId);
        blockD_setTasks(LionMetaData.ProjectLinkContract.TABLE_NAME, LionMetaData.ProjectLinkContract.Status, LionMetaData.ProjectLinkContract.TaskId);
        blockD_setTasks(LionMetaData.CategoryLinkContract.TABLE_NAME, LionMetaData.CategoryLinkContract.Status, LionMetaData.CategoryLinkContract.TaskId);
        blockD_setTasks(LionMetaData.TaskLinkContract.TABLE_NAME, LionMetaData.TaskLinkContract.Status, LionMetaData.TaskLinkContract.TaskId);
    }

    // ============ Block F ============ //

    private void blockF() throws SQLException {
        blockF_links();
    }

    private void blockF_setProjectTotalLink(String tableName, String columnName, String totalLinkUid)
    {
        String valueNull = UPDATE + tableName + SET + columnName +" = 0 " + WHERE + columnName + " IS NULL";
        exec(false, String.valueOf(valueNull));

        //UPDATE ProjectTotalLink SET shared = 1 WHERE uid IN ( SELECT UPPER(UID) FROM projects WHERE Emails NOT NULL )
        String valueNew = UPDATE+ tableName + SET + columnName + " = 1 "+ WHERE +  totalLinkUid + IN + BRACE_OPEN + SELECT + "UPPER(UID)" + FROM + "projects" + WHERE + "Emails NOT NULL )";
        exec(false, String.valueOf(valueNew));
    }

    private void blockF_setCategoryTotalLink(String tableName, String columnName, String totalLinkUid)
    {
        String value = UPDATE + tableName + SET + columnName +" = 0 " + WHERE + columnName + " IS NULL";
        exec(false, String.valueOf(value));
    }

    private void blockF_links() throws SQLException {
        exec(true, LionMetaData.ProjectTotalLinkContract.TABLE_NAME, ADD, LionMetaData.CollapsibleTotalLinkContract.Shared, BOOLEAN);
        exec(true, LionMetaData.CategoryTotalLinkContract.TABLE_NAME, ADD, LionMetaData.CollapsibleTotalLinkContract.Shared, BOOLEAN);

        blockF_setProjectTotalLink(LionMetaData.ProjectTotalLinkContract.TABLE_NAME, LionMetaData.CollapsibleTotalLinkContract.Shared, LionMetaData.ProjectTotalLinkContract.Uid);
        blockF_setCategoryTotalLink(LionMetaData.CategoryTotalLinkContract.TABLE_NAME, LionMetaData.CollapsibleTotalLinkContract.Shared, LionMetaData.ProjectTotalLinkContract.Uid);

    }

    // ============ Block G ============ //

    private void blockG() throws SQLException {
        blockG_NewOrder();
    }

    private void blockF_setOrderNew(String tableName, String columnOrderCustomer, String columnOrder)
    {
        String valueNull = UPDATE + tableName + SET + columnOrderCustomer +" = 0 " + WHERE + columnOrderCustomer + " IS NULL";
        exec(false, String.valueOf(valueNull));

        String valueNew = UPDATE+ tableName + SET + columnOrderCustomer + EQUALS + columnOrder;
        exec(false, String.valueOf(valueNew));
    }

    private void blockF_setUsnOrderNew(String tableName, String columnName)
    {
        String valueNull = UPDATE + tableName + SET + columnName +" = 1 " + WHERE + columnName + " IS NULL";
        exec(false, String.valueOf(valueNull));
    }

    private void blockG_NewOrder() throws SQLException {
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.OrderNew, DOUBLE);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UsnOrderNew, INTEGER);

        blockF_setOrderNew(LTaskContract.TABLE_NAME, LTaskContract.OrderNew, LTaskContract.OrderCustomer);
        blockF_setUsnOrderNew(LTaskContract.TABLE_NAME, LTaskContract.UsnOrderNew);
    }

    // ============ Block H ============ //

    private void blockH() throws SQLException {
        blockH_CreateTablesForUnread();
    }

    private void blockH_CreateTablesForUnread()
    {
        try {
            TableUtils.createTableIfNotExists(mConnectionSource, UnreadTotalLink.class);
            TableUtils.createTableIfNotExists(mConnectionSource, UnreadLink.class);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ============ Block I ============ //

    private void blockI() throws SQLException {
        blockI_UserOrder();
    }

    private void blockI_setUserOrder(String tableName, String columnUserOrder)
    {
        String valueNew =  "UPDATE LionTask SET UserOrder = " +
                "( CASE " +
                "WHEN (emailcustomer NOT IN (SELECT Login FROM emps ) )                 THEN ( 1000000000 ) " +
                "WHEN (emailcustomer != '"+LTSettings.getInstance().getUserName()+"' )  THEN (SELECT Orders FROM emps WHERE emps.Login=LionTask.emailcustomer ) " +
                "WHEN (emailcustomer =  '"+LTSettings.getInstance().getUserName()+"' )  THEN ( 1000000001 )" +
                " END ) " +
                "WHERE UserOrder = 0 OR UserOrder IS NULL";
        exec(false, String.valueOf(valueNew));
    }

    private void blockI_UserOrder() throws SQLException {
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UserOrder, INTEGER);

        blockI_setUserOrder(LTaskContract.TABLE_NAME, LTaskContract.UserOrder);
    }

    // ============ Block J ============ //

    private void blockJ() throws SQLException {
        blockJ_MarkerOrder();
    }

    private void blockJ_setMarkerOrder()
    {
        String valueNew =  "UPDATE LionTask SET MarkerOrder = " +
                "( CASE " +
                "WHEN (LOWER (uidmarker) NOT IN (SELECT UID FROM markers ) OR uidmarker =  'default'  ) THEN ( 0 ) " +
                "WHEN (uidmarker != 'default' )  THEN (SELECT Orders FROM markers WHERE markers.UID=LOWER (LionTask.uidmarker )) " +
                "END ) " +
                "WHERE MarkerOrder = 0 OR MarkerOrder IS NULL";
        exec(false, String.valueOf(valueNew));
    }

    private void blockJ_MarkerOrder() throws SQLException {
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.MarkerOrder, INTEGER);

        blockJ_setMarkerOrder();
    }

    // ============ Block K ============ //
    private void blockK () throws SQLException {
        blockK_IsTaskUseTerm(LTaskContract.TermBegin, LTaskContract.IsUseTerm);
        blockK_IsTaskUseTerm(LTaskContract.TermBeginCustomer, LTaskContract.IsUseTermCustomer);
    }

    private void blockK_setIsTaskUseTerm(String termColumnName, String isUseTermColumnName)
    {
        String valueNew =  "UPDATE LionTask SET "+isUseTermColumnName+" =  \n" +
                "                    ( CASE   \n" +
                "                    WHEN ("+termColumnName+" != null OR "+termColumnName+" !=  0  ) THEN ( 1000000000 ) \n" +
                "                    WHEN ("+termColumnName+" = null OR "+termColumnName+" =  0  ) THEN ( 1 ) \n" +
                "                    END ) \n" +
                "                    WHERE "+isUseTermColumnName+" = 0 OR "+isUseTermColumnName+ " IS NULL ";
        exec(false, String.valueOf(valueNew));
    }

    private void blockK_IsTaskUseTerm(String termColumnName, String isUseTermColumnName) throws SQLException {
        exec(true, LTaskContract.TABLE_NAME, ADD, isUseTermColumnName, INTEGER);

        blockK_setIsTaskUseTerm(termColumnName, isUseTermColumnName);
    }

    // ============ Block L ============ //
    private void blockL () throws SQLException {
        blockL_AddColumns();
    }



    private void blockL_AddColumns() throws SQLException {
        exec(true, "category", ADD, "mColor", TEXT);
        exec(true, "category", ADD, "mCreator", TEXT);
        exec(true, "category", ADD, "mUsnColor", INTEGER);

        blockL_setCreator();
    }

    private void blockL_setCreator() {
        String valueCreator = UPDATE + "category" + SET + "mCreator" +" = '"+LTSettings.getInstance().getUserName()+"' " + WHERE + "mCreator" + " IS NULL";
        exec(false, String.valueOf(valueCreator));

        String valueUsnColor= UPDATE + "category" + SET + "mUsnColor" +" = 0 " + WHERE + "mUsnColor" + " IS NULL";
        exec(false, String.valueOf(valueUsnColor));

        String valueUsn= UPDATE + "category" + SET + Category.FIELD_USN + " = 0 ";
        exec(false, String.valueOf(valueUsn));
    }

    // ============ Block M ============ //
    private void blockM () throws SQLException {
        blockM_AddColumns();
    }

    private void blockM_AddColumns() throws SQLException {
        exec(true, Project.TABLE_NAME, ADD, Project.FIELD_QUIET, BOOLEAN);
        exec(true, Project.TABLE_NAME, ADD, Project.FIELD_USN_QUIET, INTEGER);

        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.PerformerReaded, BOOLEAN);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UsnPerformerReaded, INTEGER);

        blockM_setQuiet();
        blockM_setPerformerReaded();

    }

    private void blockM_setQuiet() {
        String valueCreator = UPDATE + Project.TABLE_NAME + SET + Project.FIELD_QUIET +" = 0 " + WHERE + Project.FIELD_QUIET + " IS NULL";
        exec(false, String.valueOf(valueCreator));

        String valueUsnColor= UPDATE + Project.TABLE_NAME + SET + Project.FIELD_USN_QUIET +" = 0 " + WHERE + Project.FIELD_USN_QUIET + " IS NULL";
        exec(false, String.valueOf(valueUsnColor));

        String valueUsn= UPDATE + Project.TABLE_NAME + SET + Project.FIELD_USN + " = 0 ";
        exec(false, String.valueOf(valueUsn));
    }

    private void blockM_setPerformerReaded() {
        String valuePerformerReaded = UPDATE + LTaskContract.TABLE_NAME + SET + LTaskContract.PerformerReaded +" = 1 " + WHERE + LTaskContract.PerformerReaded + " IS NULL";
        exec(false, String.valueOf(valuePerformerReaded));

        String valueUsnPerformerReaded= UPDATE + LTaskContract.TABLE_NAME + SET + LTaskContract.UsnPerformerReaded +" = 0 " + WHERE + LTaskContract.UsnPerformerReaded + " IS NULL";
        exec(false, String.valueOf(valueUsnPerformerReaded));

        String valueUsn= UPDATE + LTaskContract.TABLE_NAME + SET + LTaskContract.UsnEntity + " = 0 ";
        exec(false, String.valueOf(valueUsn));
    }

    // ============ Block N ============ //
    private void blockN () throws SQLException {
        blockN_AddColumns();
    }

    private void blockN_AddColumns() throws SQLException {
        exec(true, Marker.TABLE_NAME, ADD, Marker.FIELD_EMAIL_CREATOR, TEXT);

        blockN_setCreator();

        MarkerCache.getInstance(mContext).refreshCache();
        clearCategoriesAndMarkers();
    }

    private void blockN_setCreator() {
        String valueCreator = UPDATE + Marker.TABLE_NAME + SET + Marker.FIELD_EMAIL_CREATOR +" = '"+LTSettings.getInstance().getUserName()+"' " + WHERE + Marker.FIELD_EMAIL_CREATOR + " IS NULL";
        exec(false, String.valueOf(valueCreator));

        String valueUsn= UPDATE + Marker.TABLE_NAME + SET + Marker.FIELD_USN +" = 0 ";
        exec(false, String.valueOf(valueUsn));

        exec(false, String.valueOf(" DELETE FROM " + Marker.TABLE_NAME + " WHERE " + Marker.FIELD_UID + " = '" + Marker.DEFAULT_MARKER_UUID_STRING_LOWER + "' "));
    }

    // ============ Block O ============ //
    private void blockO () throws SQLException {
        blockO_AddColumns();
    }

    private void blockO_AddColumns() throws SQLException {
        exec(true, EmpContract.TABLE_NAME, ADD, EmpContract.USN_FIELD_FOTO, DOUBLE);

        blockO_setUsnFoto();
    }

    private void blockO_setUsnFoto() {
        String valueUsn= UPDATE + EmpContract.TABLE_NAME + SET + EmpContract.USN_FIELD_FOTO +" = 0 ";
        exec(false, String.valueOf(valueUsn));
    }

    private void clearCategoriesAndMarkers() throws SQLException {
        TableUtils.clearTable(mConnectionSource, Category.class);
        TableUtils.clearTable(mConnectionSource, Marker.class);
    }

    // ============ Block P ============ //
    private void blockP () throws SQLException {
        blockP_AddColumns();
    }

    private void blockP_AddColumns() throws SQLException {
        exec(true, LionMetaData.UnreadTotalLinkContract.TABLE_NAME, ADD, LionMetaData.UnreadTotalLinkContract.TasksNotes, INTEGER);
        exec(true, LionMetaData.TaskTotalLinkContract.TABLE_NAME, ADD, LionMetaData.TaskTotalLinkContract.TasksNotes, INTEGER);
        exec(true, LionMetaData.ProjectTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ProjectTotalLinkContract.TasksNotes, INTEGER);
        exec(true, LionMetaData.InboxTotalLinkContract.TABLE_NAME, ADD, LionMetaData.InboxTotalLinkContract.TasksNotes, INTEGER);
        exec(true, LionMetaData.ForMeTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ForMeTotalLinkContract.TasksNotes, INTEGER);
        exec(true, LionMetaData.CategoryTotalLinkContract.TABLE_NAME, ADD, LionMetaData.CategoryTotalLinkContract.TasksNotes, INTEGER);
        exec(true, LionMetaData.CalendarTotalLinkContract.TABLE_NAME, ADD, LionMetaData.CalendarTotalLinkContract.TasksNotes, INTEGER);
        exec(true, LionMetaData.ByMeTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ByMeTotalLinkContract.TasksNotes, INTEGER);

        blockP_setNotes();
    }

    private void blockP_setNotes() {
        String valueNotes= UPDATE + LionMetaData.UnreadTotalLinkContract.TABLE_NAME + SET + LionMetaData.UnreadTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));

        valueNotes= UPDATE + LionMetaData.TaskTotalLinkContract.TABLE_NAME + SET + LionMetaData.TaskTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));

        valueNotes= UPDATE + LionMetaData.ProjectTotalLinkContract.TABLE_NAME + SET + LionMetaData.ProjectTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));

        valueNotes= UPDATE + LionMetaData.InboxTotalLinkContract.TABLE_NAME + SET + LionMetaData.InboxTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));

        valueNotes= UPDATE + LionMetaData.ForMeTotalLinkContract.TABLE_NAME + SET + LionMetaData.ForMeTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));

        valueNotes= UPDATE + LionMetaData.CategoryTotalLinkContract.TABLE_NAME + SET + LionMetaData.CategoryTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));

        valueNotes= UPDATE + LionMetaData.CalendarTotalLinkContract.TABLE_NAME + SET + LionMetaData.CalendarTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));

        valueNotes= UPDATE + LionMetaData.ByMeTotalLinkContract.TABLE_NAME + SET + LionMetaData.ByMeTotalLinkContract.TasksNotes +" = 0 ";
        exec(false, String.valueOf(valueNotes));
    }

    // ============ Block Q ============ //
    private void blockQ () throws SQLException {
        blockQ_AddTables();
    }

    private void blockQ_AddTables() throws SQLException {
        TableUtils.createTable(mConnectionSource, Contact.class);
        TableUtils.createTable(mConnectionSource, ContactsGroup.class);

    }

    // ============ Block R ============ //
    private void blockR () throws SQLException {
        blockR_AddTables();
    }

    private void blockR_AddTables() throws SQLException {
        TableUtils.createTable(mConnectionSource, ContactFile.class);
    }

    private void blockS() throws SQLException {
        blockS_AddTables();
    }

    private void blockS_AddTables() throws SQLException {
        TableUtils.createTable(mConnectionSource, ReadyTotalLink.class);
        TableUtils.createTable(mConnectionSource, ReadyLink.class);
    }

    private void blockT() throws SQLException {
        blockT_AddTables();
    }

    private void blockT_AddTables() throws SQLException {
        TableUtils.createTable(mConnectionSource, InworkTotalLink.class);
        TableUtils.createTable(mConnectionSource, InworkLink.class);
    }

    private void blockU() throws SQLException {
        TableUtils.createTable(mConnectionSource, OverdueTotalLink.class);
        TableUtils.createTable(mConnectionSource, OverdueLink.class);
    }

    private void blockV() throws SQLException {
        TableUtils.createTable(mConnectionSource, ColorTotalLink.class);
        TableUtils.createTable(mConnectionSource, ColorLink.class);
    }

    private void blockW() throws SQLException {
        TableUtils.createTable(mConnectionSource, EmpTotalLink.class);
        TableUtils.createTable(mConnectionSource, EmpLink.class);
    }

    private void blockX() throws SQLException {
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.Time, INTEGER);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.Plan, INTEGER);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.InWorkTime, LONG);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UsnTime, INTEGER);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UsnPlan, INTEGER);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UsnInWorkTime, INTEGER);


        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.Time);
        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.Plan);
        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.InWorkTime);
        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.UsnTime);
        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.UsnPlan);
        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.UsnInWorkTime);
    }

    private void blockX_set(String tableName, String columnOrderCustomer)
    {
        String valueNull = UPDATE + tableName + SET + columnOrderCustomer +" = 0 " + WHERE + columnOrderCustomer + " IS NULL";
        exec(false, String.valueOf(valueNull));
    }

    private void blockY() throws SQLException {
        exec(true, EmpContract.TABLE_NAME, ADD, EmpContract.PHONE, TEXT);
        exec(true, EmpContract.TABLE_NAME, ADD, EmpContract.USN_FIELD_PHONE, LONG);

        blockY_setUsn();
    }

    private void blockY_setUsn() {
        String valueUsn= UPDATE + EmpContract.TABLE_NAME + SET + EmpContract.USN_FIELD_PHONE +" = 0 ";
        exec(false, String.valueOf(valueUsn));
    }

    private void blockZ() throws SQLException {
        TableUtils.createTable(mConnectionSource, FocusTotalLink.class);
        TableUtils.createTable(mConnectionSource, FocusLink.class);

        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.Focus, BOOLEAN);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UsnFieldFocus, INTEGER);
        exec(true, LionMetaData.ProjectTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ProjectTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.CategoryTotalLinkContract.TABLE_NAME, ADD, LionMetaData.CategoryTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.ByMeTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ByMeTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.TaskLinkContract.TABLE_NAME, ADD, LionMetaData.TaskLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.ForMeTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ForMeTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.ColorTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ColorTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.InworkTotalLinkContract.TABLE_NAME, ADD, LionMetaData.InworkTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.ReadyTotalLinkContract.TABLE_NAME, ADD, LionMetaData.ReadyTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.OverdueTotalLinkContract.TABLE_NAME, ADD, LionMetaData.OverdueTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.EmpTotalLinkContract.TABLE_NAME, ADD, LionMetaData.EmpTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.CalendarTotalLinkContract.TABLE_NAME, ADD, LionMetaData.CalendarTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.InboxTotalLinkContract.TABLE_NAME, ADD, LionMetaData.InboxTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.UnreadTotalLinkContract.TABLE_NAME, ADD, LionMetaData.UnreadTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.CalendarLinkContract.TABLE_NAME, ADD, LionMetaData.CalendarLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.EmpLinkContract.TABLE_NAME, ADD, LionMetaData.EmpLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.OverdueLinkContract.TABLE_NAME, ADD, LionMetaData.OverdueLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.ColorLinkContract.TABLE_NAME, ADD, LionMetaData.ColorLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.CategoryLinkContract.TABLE_NAME, ADD, LionMetaData.CategoryLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.TaskTotalLinkContract.TABLE_NAME, ADD, LionMetaData.TaskTotalLinkContract.TasksFocus, INTEGER);
        exec(true, LionMetaData.InboxLinkContract.TABLE_NAME, ADD, LionMetaData.InboxLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.ForMeLinkContract.TABLE_NAME, ADD, LionMetaData.ForMeLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.UnreadLinkContract.TABLE_NAME, ADD, LionMetaData.UnreadLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.ReadyLinkContract.TABLE_NAME, ADD, LionMetaData.ReadyLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.InworkLinkContract.TABLE_NAME, ADD, LionMetaData.InworkLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.ByMeLinkContract.TABLE_NAME, ADD, LionMetaData.ByMeLinkContract.Focus, INTEGER);
        exec(true, LionMetaData.ProjectLinkContract.TABLE_NAME, ADD, LionMetaData.ProjectLinkContract.Focus, INTEGER);

        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.Focus);
        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.UsnFieldFocus);
        blockX_set(LionMetaData.ProjectTotalLinkContract.TABLE_NAME, LionMetaData.ProjectTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.CategoryTotalLinkContract.TABLE_NAME, LionMetaData.CategoryTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.ByMeTotalLinkContract.TABLE_NAME, LionMetaData.ByMeTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.TaskLinkContract.TABLE_NAME, LionMetaData.TaskLinkContract.Focus);
        blockX_set(LionMetaData.ForMeTotalLinkContract.TABLE_NAME, LionMetaData.ForMeTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.ColorTotalLinkContract.TABLE_NAME, LionMetaData.ColorTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.InworkTotalLinkContract.TABLE_NAME, LionMetaData.InworkTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.ReadyTotalLinkContract.TABLE_NAME, LionMetaData.ReadyTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.OverdueTotalLinkContract.TABLE_NAME, LionMetaData.OverdueTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.EmpTotalLinkContract.TABLE_NAME, LionMetaData.EmpTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.CalendarTotalLinkContract.TABLE_NAME, LionMetaData.CalendarTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.InboxTotalLinkContract.TABLE_NAME, LionMetaData.InboxTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.UnreadTotalLinkContract.TABLE_NAME, LionMetaData.UnreadTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.CalendarLinkContract.TABLE_NAME, LionMetaData.CalendarLinkContract.Focus);
        blockX_set(LionMetaData.EmpLinkContract.TABLE_NAME, LionMetaData.EmpLinkContract.Focus);
        blockX_set(LionMetaData.OverdueLinkContract.TABLE_NAME, LionMetaData.OverdueLinkContract.Focus);
        blockX_set(LionMetaData.ColorLinkContract.TABLE_NAME, LionMetaData.ColorLinkContract.Focus);
        blockX_set(LionMetaData.CategoryLinkContract.TABLE_NAME, LionMetaData.CategoryLinkContract.Focus);
        blockX_set(LionMetaData.TaskTotalLinkContract.TABLE_NAME, LionMetaData.TaskTotalLinkContract.TasksFocus);
        blockX_set(LionMetaData.InboxLinkContract.TABLE_NAME, LionMetaData.InboxLinkContract.Focus);
        blockX_set(LionMetaData.ForMeLinkContract.TABLE_NAME, LionMetaData.ForMeLinkContract.Focus);
        blockX_set(LionMetaData.UnreadLinkContract.TABLE_NAME, LionMetaData.UnreadLinkContract.Focus);
        blockX_set(LionMetaData.ReadyLinkContract.TABLE_NAME, LionMetaData.ReadyLinkContract.Focus);
        blockX_set(LionMetaData.InworkLinkContract.TABLE_NAME, LionMetaData.InworkLinkContract.Focus);
        blockX_set(LionMetaData.ByMeLinkContract.TABLE_NAME, LionMetaData.ByMeLinkContract.Focus);
        blockX_set(LionMetaData.ProjectLinkContract.TABLE_NAME, LionMetaData.ProjectLinkContract.Focus);
    }

    private void blockZA() throws SQLException {
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.Emails, TEXT);
        exec(true, LTaskContract.TABLE_NAME, ADD, LTaskContract.UsnFieldListMembers, INTEGER);

        String valueNull = UPDATE + LTaskContract.TABLE_NAME + SET + LTaskContract.Emails +" = '' " + WHERE + LTaskContract.Emails + " IS NULL";
        exec(false, String.valueOf(valueNull));

        blockX_set(LTaskContract.TABLE_NAME, LTaskContract.UsnFieldListMembers);
    }
}