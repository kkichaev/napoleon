package com.ashberrysoft.leadertask.content_providers;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.SharedStrings;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public final class LeaderTaskProviderMetaData implements SharedStrings {

    public static final String AUTHORITY = "com.ashberrysoft.leadertask";
    public static final String AUTHORITY_PROVIDER = AUTHORITY + ".content_providers";

    private static final String TASKFILE_TABLE_NAME = "task_files";
    private static final String CONTACTSFILE_TABLE_NAME = "contacts_files";
    private static final String CALENDAR_DATA_TABLE_NAME = "calendar_data";
    private static final String SIMPLE_NOTIFY = "simple_notify";
    private static final String SYNCHRONIZATION_INFO_TABLE_NAME = "synchronization_info";
    public static final String TASK_TABLE_NAME = "tasks";
    private static final String UID_TO_DELETE_TABLE_NAME = "uid_to_delete";
    public static final String CONTACT_TABLE_NAME = "contacts";
    public static final String EMP_TABLE_NAME = "emps";
    public static final String EMPLOYEES_TABLE_NAME = "employees";
    public static final String TABLE_DELETED_TASK = "deleted_tasks";

    private static final String EQUALS = "=";
    private static final String OR = " OR ";
    private static final String QUOTE = "\"";


    public static final class TaskFileContract implements BaseColumns {

        public static final String TABLE_NAME = TASKFILE_TABLE_NAME;

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        public static final String SERVER_CLASS = "LionTaskFile";

        // SERVER FIELDS
        /** UUID (String) */
        public static final String FIELD_UID = "Uid";
        /** UUID (String) */
        public static final String FIELD_TASKUID = "TaskUID";
        /** UUID (String) */
        public static final String FIELD_FILEUID = "FileUID";
        /** String */
        public static final String FIELD_EMAILCREATOR = "EmailCreator";
        /** Long */
        public static final String FIELD_ORDER = "Order";
        /** String */
        public static final String FIELD_FILENAME = "FileName";
        /** Long */
        public static final String FIELD_FILESIZE = "FileSize";
        /** Long */
        public static final String FIELD_FILEVERSION = "FileVersion";
        /** Int */
        public static final String FIELD_USN_ENTITY = "__usn_entity";
        /** Int */
        public static final String FIELD_USN_FIELD_ORDER = "__usn_field_order";
        /** Int */
        public static final String FIELD_USN_FIELD_NAME = "__usn_field_name";
        /** Int */
        public static final String FIELD_USN_FIELD_SIZE = "__usn_field_size";
        /** Int */
        public static final String FIELD_USN_FIELD_VERSION = "__usn_field_version";

        // LOCAL FIELDS
        /**
         * <b>Use FIELD_ORDER for LionTaskFile</b> <br/>
         * 
         * @see TaskFileContract.FIELD_ORDER
         */
        public static final String ORDERS = "Orders";
        /**
         * Int (Boolean) <br/>
         * 1 = exist, 0 = not exist
         */
        public static final String FILE_EXIST = "FileExist";
        /**
         * Int (Boolean) <br/>
         * 1 = to delete, 0 = normal state
         */
        public static final String DELETE_OBJECT = "DeleteObject";

        /**
         * Int (Boolean) <br/>
         * 1 = weak, 0 = normal state
         */
        public static final String WEAK_LINK = "WeakLink";

        /**
         * Int (Boolean) <br/>
         * 1 = send, 0 = normal state
         */
        public static final String SEND_FILE = "SendFile";

        // FORMAT SELECTION
        private static final String SELECTION_FILE_EXIST = FILE_EXIST + "=%s";
        private static final String SELECTION_DELETE_OBJECT = DELETE_OBJECT + "=%s";
        private static final String SELECTION_FIELD_UID = FIELD_UID + "=\"%s\"";
        private static final String SELECTION_FIELD_FILEUID = FIELD_FILEUID + "=\"%s\"";
        private static final String SELECTION_WEAK_LINK = WEAK_LINK + "=%s";
        private static final String SELECTION_FIELD_TASKUID = FIELD_TASKUID + "=\"%s\"";

        public static String selectionFileExist(boolean exist) {
            return String.format(SELECTION_FILE_EXIST, exist ? 1 : 0);
        }

        public static String selectionDeleteObject(boolean toDelete) {
            return String.format(SELECTION_DELETE_OBJECT, toDelete ? 1 : 0);
        }

        public static String selectionDeleteObjectFileExist(boolean toDelete, boolean exist) {
            return selectionDeleteObject(toDelete) + AND + selectionFileExist(exist);
        }

        public static String selectionFieldUid(String uid) {
            return String.format(SELECTION_FIELD_UID, uid.toLowerCase());
        }

        public static String selectionFieldFileUid(String uid) {
            return String.format(SELECTION_FIELD_FILEUID, uid.toLowerCase());
        }

        public static String selectionWeakLink(boolean weakLink) {
            return String.format(SELECTION_WEAK_LINK, weakLink ? 1 : 0);
        }

        public static String selectionFieldTaskUid(String uid) {
            return String.format(SELECTION_FIELD_TASKUID, uid.toLowerCase());
        }

        public static String selectionFieldTaskUidAndWeakLink(String uid, boolean weakLink) {
            return selectionFieldTaskUid(uid) + AND + selectionWeakLink(weakLink);
        }

        public static String selectionFieldTaskUidAndDeleteObject(String uid, boolean toDelete) {
            return selectionFieldTaskUid(uid) + AND + selectionDeleteObject(toDelete);
        }

        public static String selectionFieldTaskUidAndDeleteObjectAndWeakLink(String uid,//
                boolean toDelete, boolean weakLink) {
            final StringBuilder sb = new StringBuilder();
            SelectionKeeper.equals(sb, FIELD_TASKUID, uid);
            sb.append(AND);
            SelectionKeeper.equals(sb, DELETE_OBJECT, toDelete);
            sb.append(AND);
            SelectionKeeper.equals(sb, WEAK_LINK, weakLink);

            return sb.toString();
        }

        public static String makeOR(String... ss) {
            final StringBuilder sb = new StringBuilder();

            for (int i = 0; i < ss.length; i++) {
                sb.append(ss[i]);
                if (i < ss.length - 1) {
                    sb.append(OR);
                }
            }

            return sb.toString();
        }

        public static String selectionSendFile(boolean sendFile) {
            return SelectionKeeper.equals(null, SEND_FILE, sendFile);
        }

        public static String defaultSort() {
            return SelectionKeeper.sort(null, ORDERS);
        }

        /** StringBuilder must be not null */
        public static String inWeakLinkAndTaskUids(StringBuilder sb, boolean weakLink, String... uids) {
            SelectionKeeper.equals(sb, WEAK_LINK, weakLink);
            sb.append(AND);
            SelectionKeeper.in(sb, FIELD_TASKUID, uids);

            return sb.toString();
        }
    }


    public static final class ContactsFileContract implements BaseColumns {

        public static final String TABLE_NAME = CONTACTSFILE_TABLE_NAME;

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        public static final String SERVER_CLASS = "LionContactFile";

        // SERVER FIELDS
        /** UUID (String) */
        public static final String FIELD_UID = "Uid";
        /** UUID (String) */
        public static final String FIELD_CONTACTUID = "ContactUID";
        /** UUID (String) */
        public static final String FIELD_FILEUID = "FileUID";
        /** String */
        public static final String FIELD_EMAILCREATOR = "EmailCreator";
        /** Long */
        public static final String FIELD_ORDER = "Order";
        /** String */
        public static final String FIELD_FILENAME = "FileName";
        /** Long */
        public static final String FIELD_FILESIZE = "FileSize";
        /** Long */
        public static final String FIELD_FILEVERSION = "FileVersion";
        /** Int */
        public static final String FIELD_USN_ENTITY = "__usn_entity";
        /** Int */
        public static final String FIELD_USN_FIELD_ORDER = "__usn_field_order";
        /** Int */
        public static final String FIELD_USN_FIELD_NAME = "__usn_field_name";
        /** Int */
        public static final String FIELD_USN_FIELD_SIZE = "__usn_field_size";
        /** Int */
        public static final String FIELD_USN_FIELD_VERSION = "__usn_field_version";

        // LOCAL FIELDS
        /**
         * <b>Use FIELD_ORDER for LionContactsFile</b> <br/>
         *
         * @see ContactsFileContract.FIELD_ORDER
         */
        public static final String ORDERS = "Orders";
        /**
         * Int (Boolean) <br/>
         * 1 = exist, 0 = not exist
         */
        public static final String FILE_EXIST = "FileExist";
        /**
         * Int (Boolean) <br/>
         * 1 = to delete, 0 = normal state
         */
        public static final String DELETE_OBJECT = "DeleteObject";

        /**
         * Int (Boolean) <br/>
         * 1 = weak, 0 = normal state
         */
        public static final String WEAK_LINK = "WeakLink";

        /**
         * Int (Boolean) <br/>
         * 1 = send, 0 = normal state
         */
        public static final String SEND_FILE = "SendFile";

        // FORMAT SELECTION
        private static final String SELECTION_FILE_EXIST = FILE_EXIST + "=%s";
        private static final String SELECTION_DELETE_OBJECT = DELETE_OBJECT + "=%s";
        private static final String SELECTION_FIELD_UID = FIELD_UID + "=\"%s\"";
        private static final String SELECTION_FIELD_FILEUID = FIELD_FILEUID + "=\"%s\"";
        private static final String SELECTION_WEAK_LINK = WEAK_LINK + "=%s";
        private static final String SELECTION_FIELD_CONTACTSUID = FIELD_CONTACTUID + "=\"%s\"";

        public static String selectionFileExist(boolean exist) {
            return String.format(SELECTION_FILE_EXIST, exist ? 1 : 0);
        }

        public static String selectionDeleteObject(boolean toDelete) {
            return String.format(SELECTION_DELETE_OBJECT, toDelete ? 1 : 0);
        }

        public static String selectionDeleteObjectFileExist(boolean toDelete, boolean exist) {
            return selectionDeleteObject(toDelete) + AND + selectionFileExist(exist);
        }

        public static String selectionFieldUid(String uid) {
            return String.format(SELECTION_FIELD_UID, uid.toLowerCase());
        }

        public static String selectionFieldFileUid(String uid) {
            return String.format(SELECTION_FIELD_FILEUID, uid.toLowerCase());
        }

        public static String selectionWeakLink(boolean weakLink) {
            return String.format(SELECTION_WEAK_LINK, weakLink ? 1 : 0);
        }

        public static String selectionFieldContactsUid(String uid) {
            return String.format(SELECTION_FIELD_CONTACTSUID, uid.toLowerCase());
        }

        public static String selectionFieldContactsUidAndWeakLink(String uid, boolean weakLink) {
            return selectionFieldContactsUid(uid) + AND + selectionWeakLink(weakLink);
        }

        public static String selectionFieldContactsUidAndDeleteObject(String uid, boolean toDelete) {
            return selectionFieldContactsUid(uid) + AND + selectionDeleteObject(toDelete);
        }

        public static String selectionFieldContactsUidAndDeleteObjectAndWeakLink(String uid,//
                                                                             boolean toDelete, boolean weakLink) {
            final StringBuilder sb = new StringBuilder();
            SelectionKeeper.equals(sb, FIELD_CONTACTUID, uid);
            sb.append(AND);
            SelectionKeeper.equals(sb, DELETE_OBJECT, toDelete);
            sb.append(AND);
            SelectionKeeper.equals(sb, WEAK_LINK, weakLink);

            return sb.toString();
        }

        public static String makeOR(String... ss) {
            final StringBuilder sb = new StringBuilder();

            for (int i = 0; i < ss.length; i++) {
                sb.append(ss[i]);
                if (i < ss.length - 1) {
                    sb.append(OR);
                }
            }

            return sb.toString();
        }

        public static String selectionSendFile(boolean sendFile) {
            return SelectionKeeper.equals(null, SEND_FILE, sendFile);
        }

        public static String defaultSort() {
            return SelectionKeeper.sort(null, ORDERS);
        }

        /** StringBuilder must be not null */
        public static String inWeakLinkAndContactsUids(StringBuilder sb, boolean weakLink, String... uids) {
            SelectionKeeper.equals(sb, WEAK_LINK, weakLink);
            sb.append(AND);
            SelectionKeeper.in(sb, FIELD_CONTACTUID, uids);

            return sb.toString();
        }
    }


    /**
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class CalendarDataContract implements BaseColumns {

        public static final String TABLE_NAME = CALENDAR_DATA_TABLE_NAME;

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        /** Long */
        public static final String DATE = "date";
        /** Int */
        public static final String TOTAL_TASKS = "total_tasks";
        /** Int */
        public static final String UNCOMPLETED_TASKS = "uncompleted_tasks";

        public static String selectionDatesIn(Calendar... cs) {
            final StringBuilder sb = new StringBuilder();
            sb.append(DATE);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (Calendar c : cs) {
                if (start) {
                    start = false;
                } else {
                    sb.append(COMMA_C);
                }

                sb.append(c.getTimeInMillis());
            }
            sb.append(BRACE_CLOSE_C);

            return sb.toString();
        }
    }

    /**
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class SyncInfoContract implements BaseColumns {

        public static final String TABLE_NAME = SYNCHRONIZATION_INFO_TABLE_NAME;

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        /** Int */
        public static final String SYNC_STATUS = "sync_status";
        /** Int */
        public static final String MENU_STATUS = "menu_status";
        /** Int */
        public static final String LIST_STATUS = "list_status";
        /** Int */
        public static final String ERROR_STATUS = "error_status";
        /** Int */
        public static final String ERROR_CODE = "error_code";
        /** String */
        public static final String ERROR_MESSAGE = "error_message";
        /** Long */
        public static final String LAST_SYNC_TIME = "last_sync_time";
    }

    /**
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class TaskContract implements BaseColumns {

        public static final String TABLE_NAME = TASK_TABLE_NAME;
        public static final String SERVER_CLASS = "LionTask";

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        // SERVER FIELDS
        /** String */
        public static final String FIELD_UID = "UID";
        /** String */
        public static final String FIELD_UID_PARENT = "UIDParent";
        /** Int */
        public static final String FIELD_ORDER = "Order";
        /**
         * Int (Boolean) <br/>
         * 1 = exist, 0 = not exist
         */
        public static final String FIELD_COLLAPSED = "Collapsed";
        /** String */
        public static final String FIELD_NAME = "Name";
        /** String */
        public static final String FIELD_COMMENT = "Comment";
        /** Int */
        public static final String FIELD_STATUS = "Status";
        /** Long */
        public static final String FIELD_TERM_BEGIN = "TermBegin";
        /** Long */
        public static final String FIELD_TERM_END = "TermEnd";
        /** String */
        public static final String FIELD_EMAIL_PERFORMER = "EmailPerformer";
        /** String */
        public static final String FIELD_UID_PROJECT = "UidProject";
        /** String */
        public static final String FIELD_UID_MARKER = "UidMarker";
        /**
         * Int (Boolean) <br/>
         * 1 = exist, 0 = not exist
         */
        public static final String FIELD_READED = "Readed";

        public static final String FIELD_PERFORMER_READED = "PerformerReaded";

        public static final String FIELD_USN_PERFORMER_READED = "__usn_field_performerreaded";
        /** Int */
        public static final String FIELD_ORDER_CUSTOMER = "OrderCustomer";
        /** Long */
        public static final String FIELD_TERM_BEGIN_CUSTOMER = "TermBeginCustomer";
        /** Long */
        public static final String FIELD_TERM_END_CUSTOMER = "TermEndCustomer";
        /** String */
        public static final String FIELD_EMAIL_CUSTOMER = "EmailCustomer";
        /** String */
        public static final String FIELD_CATEGORIES = "Categories";
        /** String */
        public static final String FIELD_CONTACTS = "Contacts";
        // there are more main fields
        /** Long */
        public static final String FIELD_USN_ENTITY = "__usn_entity";
        /** Int */
        public static final String FIELD_USN_UID_PARENT = "__usn_field_uid_parent";
        /** Int */
        public static final String FIELD_USN_EMAIL_PERORMER = "__usn_field_email_performer";
        /** Int */
        public static final String FIELD_USN_NAME = "__usn_field_name";
        /** Int */
        public static final String FIELD_USN_COMMENT = "__usn_field_comment";
        /** Int */
        public static final String FIELD_USN_STATUS = "__usn_field_status";
        /** Int */
        public static final String FIELD_USN_ORDER = "__usn_field_order";
        /** Int */
        public static final String FIELD_USN_UID_PROJECT = "__usn_field_uid_project";
        /** Int */
        public static final String FIELD_USN_UID_MARKER = "__usn_field_uid_marker";
        /** Int */
        public static final String FIELD_USN_TERM = "__usn_field_term";
        /** Int */
        public static final String FIELD_USN_READED = "__usn_field_readed";
        /** Int */
        public static final String FIELD_USN_COLLAPSED = "__usn_field_collapsed";
        /** Int */
        public static final String FIELD_USN_CUSTOMER_ORDER = "__usn_field_customer_order";
        /** Int */
        public static final String FIELD_USN_CUSTOMER_TERM = "__usn_field_customer_term";
        /** Int */
        public static final String FIELD_USN_CATEGORIES = "__usn_field_categories";
        /** Int */
        public static final String FIELD_USN_CONTACTS = "__usn_field_contacts";

        public static final String FIELD_CREATE_TIME = "CreateTime";
        public static final String FIELD_PERFORM_TIME = "PerformTime";
        public static final String FIELD_COMPLETE_TIME = "CompleteTime";

        public static final String FIELD_SERIES_TYPE = "SeriesType";
        public static final String FIELD_SERIES_AFTER_TYPE = "SeriesAfterType";
        public static final String FIELD_SERIES_AFTER_COUNT = "SeriesAfterCount";
        public static final String FIELD_SERIES_WEEK_COUNT = "SeriesWeekCount";
        public static final String FIELD_SERIES_WEEK_MON = "SeriesWeekMon";
        public static final String FIELD_SERIES_WEEK_TUE = "SeriesWeekTue";
        public static final String FIELD_SERIES_WEEK_WED = "SeriesWeekWed";
        public static final String FIELD_SERIES_WEEK_THU = "SeriesWeekThu";
        public static final String FIELD_SERIES_WEEK_FRI = "SeriesWeekFri";
        public static final String FIELD_SERIES_WEEK_SAT = "SeriesWeekSat";
        public static final String FIELD_SERIES_WEEK_SUN = "SeriesWeekSun";
        public static final String FIELD_SERIES_MONTH_TYPE = "SeriesMonthType";
        public static final String FIELD_SERIES_MONTH_COUNT = "SeriesMonthCount";
        public static final String FIELD_SERIES_MONTH_DAY = "SeriesMonthDay";
        public static final String FIELD_SERIES_MONTH_WEEKTYPE = "SeriesMonthWeekType";
        public static final String FIELD_SERIES_MONTH_DAYOFWEEK = "SeriesMonthDayOfWeek";
        public static final String FIELD_SERIES_YEAR_TYPE = "SeriesYearType";
        public static final String FIELD_SERIES_YEAR_MONTH = "SeriesYearMonth";
        public static final String FIELD_SERIES_YEAR_MONTHDAY = "SeriesYearMonthDay";
        public static final String FIELD_SERIES_YEAR_WEEKTYPE = "SeriesYearWeekType";
        public static final String FIELD_SERIES_YEAR_DAYOFWEEK = "SeriesYearDayOfWeek";
        public static final String FIELD_SERIES_END = "SeriesEnd";

        public static final String FIELD_USN_FIELD_CREATETIME = "__usn_field_createtime";
        public static final String FIELD_USN_FIELD_PERFORMTIME = "__usn_field_performtime";
        public static final String FIELD_USN_FIELD_COMPLETETIME = "__usn_field_completetime";
        public static final String FIELD_USN_FIELD_SERIES = "__usn_field_series";

        // LOCAL FIELDS
        /** Int */
        public static final String SUBTASKS_SIZE = "field_subtasks_size";
        /** Int */
        public static final String SUBTASKS_SIZE_NOT_READ = "field_subtasks_size_not_read";
        /** Int */
        public static final String SUBTASKS_SIZE_NOT_MADE = "field_subtasks_size_not_made";
        /** Int */
        public static final String SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ = "field_subtasks_size_not_made_and_not_read";
        /** Int */
        public static final String MESSAGES_COUNT = "messagesCount";
        /** Int */
        public static final String LEFT_POINTER = "lft";
        /** Int */
        public static final String RIGHT_POINTER = "rgt";
        /**
         * <b>Use FIELD_ORDER for LionTaskFile</b> <br/>
         * 
         * @see TaskContract.FIELD_ORDER
         */
        public static final String ORDERS = "Orders";
        /**
         * Int (Boolean) <br/>
         * 1 = was, 0 = not
         */
        public static final String WAS_COUNTED = "was_counted";
        /**
         * Int (Boolean) <br/>
         * 1 = has, 0 = not
         */
        public static final String HAS_FILES = "has_files";
        /** String (Json) */
        public static final String LIST_LABELS = "list_labels";
        /** Extra sorting field */
        public static final String EMP_ORDERS = "EmpOrders";

        public static String selectionFieldUid(String uid) {
            return SelectionKeeper.equals(null, FIELD_UID, uid.toLowerCase());
        }

        public static String selectionFieldUidParent(String uid) {
            return SelectionKeeper.equals(null, FIELD_UID_PARENT, uid.toLowerCase());
        }

        public static String selectionFieldUidInList(List<String> uids) {
            return SelectionKeeper.in(null, FIELD_UID, uids);
        }

        public static String selectionFieldEmailCustomer(String emailCustomer) {
            return SelectionKeeper.equals(null, FIELD_EMAIL_CUSTOMER, emailCustomer);
        }
    }

    /**
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class SimpleNotifyContract implements BaseColumns {

        public static final String TABLE_NAME = SIMPLE_NOTIFY;

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        /** String */
        public static final String TASK_UUID = "task_uuid";
        /** Long */
        public static final String NOTIFY_TIME = "date";

        // SELECTIONS
        public static String selectionId(long id) {
            return _ID + EQUALS + id;
        }

        public static String selectionTaskId(String taskId) {
            return TASK_UUID + EQUALS + QUOTE + taskId + QUOTE;
        }
    }

    public static String selectionMarkerUids(StringBuilder sb ,Iterable<String> uids ) {
        sb.append(SharedStrings.SELECT);
        sb.append(" UPPER ( UID ) ");
        sb.append(SharedStrings.FROM);
        sb.append(" markers ");
        sb.append(SharedStrings.WHERE);

        sb.append(" UID ");
        sb.append(SharedStrings.IN);
        sb.append(SharedStrings.BRACE_OPEN_C);

        boolean start = true;
        for (String value : uids) {
            if (start) {
                start = false;
            } else {
                sb.append(SharedStrings.COMMA_C);
            }

            sb.append(SharedStrings.QUOTE_C);
            sb.append(value.toLowerCase());
            sb.append(SharedStrings.QUOTE_C);
        }
        sb.append(SharedStrings.BRACE_CLOSE_C);
        return sb.toString();
    }
    
    /**
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class DeletedTaskContract implements BaseColumns {

        public static final String TABLE_NAME = TABLE_DELETED_TASK;

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        /** String */
        public static final String Uid = "mId";
        /** Long */
        public static final String DeleteDate = "mDeleteDate";

        // SELECTIONS
        public static String selectionId(long id) {
            return _ID + EQUALS + id;
        }

//        public static String selectionTaskId(String taskId) {
//            return TASK_UUID + EQUALS + QUOTE + taskId + QUOTE;
//        }
    }

    /**
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class UidToDeleteContract implements BaseColumns {

        public static final String TABLE_NAME = UID_TO_DELETE_TABLE_NAME;

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        /** String */
        public static final String UID = "uid";
        /** String */
        public static final String SERVER_CLASS = "server_class";

        // SELECTIONS
        public static String selectionServerClass(String serverClass) {
            return SelectionKeeper.equals(null, SERVER_CLASS, serverClass);
        }
    }

    /**
     * 2014-06-18
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class ContactContract implements BaseColumns {

        public static final String TABLE_NAME = CONTACT_TABLE_NAME;
        public static final String SERVER_CLASS = "LionContact";

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        public static final String UID = "Uid";
        public static final String UID_PARENT = "UidParent";
        public static final String EMAIL_CREATOR = "EmailCreator";
        public static final String UID_GROUP = "UidGroup";
        public static final String TITLE = "Title";
        public static final String IS_GROUP = "IsGroup";
        public static final String GENDER = "Gender";
        public static final String FIRST_NAME = "FirstName";
        public static final String MIDDLE_NAME = "MiddleName";
        public static final String LAST_NAME = "LastName";
        public static final String COMPANY_NAME = "CompanyName";
        public static final String JOB_TITLE = "JobTitle";
        public static final String DETAILS = "Details";
        public static final String BIRTHDAY = "Birthday";
        public static final String COMMUNICATIONS = "Communications";
        public static final String HOME_COUNTRY = "HomeCountry";
        public static final String HOME_REGION = "HomeRegion";
        public static final String HOME_INDEX = "HomeIndex";
        public static final String HOME_CITY = "HomeCity";
        public static final String HOME_STREET = "HomeStreet";
        public static final String WORK_COUNTRY = "WorkCountry";
        public static final String WORK_REGION = "WorkRegion";
        public static final String WORK_INDEX = "WorkIndex";
        public static final String WORK_CITY = "WorkCity";
        public static final String WORK_STREET = "WorkStreet";
        public static final String ORDER = "Order";
        public static final String COLLAPSED = "Collapsed";
        public static final String FAVORITE = "Favorite";
        public static final String SHOW_NAVIGATOR = "ShowNavigator";
        public static final String NOTIFY_BIRTHDAY = "NotifyBirthday";

        public static final String USN_ENTITY = "__usn_entity";
        public static final String USN_FIELD_UID_PARENT = "__usn_field_uid_parent";
        public static final String USN_FIELD_UID_GROUP = "__usn_field_uid_group";
        public static final String USN_FIELD_TITLE = "__usn_field_title";
        public static final String USN_FIELD_ISGROUP = "__usn_field_isgroup";
        public static final String USN_FIELD_GENDER = "__usn_field_gender";
        public static final String USN_FIELD_FIRSTNAME = "__usn_field_firstname";
        public static final String USN_FIELD_MIDDLENAME = "__usn_field_middlename";
        public static final String USN_FIELD_LASTNAME = "__usn_field_lastname";
        public static final String USN_FIELD_COMPANY_NAME = "__usn_field_company_name";
        public static final String USN_FIELD_JOB_TITLE = "__usn_field_job_title";
        public static final String USN_FIELD_DETAILS = "__usn_field_details";
        public static final String USN_FIELD_BIRTHDAY = "__usn_field_birthday";
        public static final String USN_FIELD_COMMUNICATIONS = "__usn_field_communications";
        public static final String USN_FIELD_HOME_CITY = "__usn_field_home_city";
        public static final String USN_FIELD_HOME_COUNTRY = "__usn_field_home_country";
        public static final String USN_FIELD_HOME_REGION = "__usn_field_home_region";
        public static final String USN_FIELD_HOME_INDEX = "__usn_field_home_index";
        public static final String USN_FIELD_HOME_STREET = "__usn_field_home_street";
        public static final String USN_FIELD_WORK_CITY = "__usn_field_work_city";
        public static final String USN_FIELD_WORK_COUNTRY = "__usn_field_work_country";
        public static final String USN_FIELD_WORK_REGION = "__usn_field_work_region";
        public static final String USN_FIELD_WORK_INDEX = "__usn_field_work_index";
        public static final String USN_FIELD_WORK_STREET = "__usn_field_work_street";
        public static final String USN_FIELD_ORDER = "__usn_field_order";
        public static final String USN_FIELD_COLLAPSED = "__usn_field_collapsed";
        public static final String USN_FIELD_FAVORITE = "__usn_field_favorite";
        public static final String USN_FIELD_SHOW_NAVIGATOR = "__usn_field_show_navigator";
        public static final String USN_FIELD_NOTIFY_BIRTHDAY = "__usn_field_notify_birthday";
        public static final String USN_FIELD_FOTO = "__usn_foto";


        public static final String ORDERS = "Orders";

        public static String selectionUid(String uid) {
            return SelectionKeeper.equals(null, UID, uid);
        }
    }

    /**
     * 2014-06-18
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class EmpContract implements BaseColumns {

        public static final String TABLE_NAME = EMP_TABLE_NAME;
        public static final String SERVER_CLASS = "LionEmp";

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        public static final String UID = "Uid";
        public static final String LOGIN = "Login";
        public static final String ORDER = "Order";
        public static final String FIRST_NAME = "FirstName";
        public static final String MIDDLE_NAME = "MiddleName";
        public static final String LAST_NAME = "LastName";
        public static final String DETAILS = "Details";
        public static final String COUNTRY = "Country";
        public static final String PROVINCE = "Province";
        public static final String POSTAL_CODE = "PostalCode";
        public static final String CITY = "City";
        public static final String STREET = "Street";
        public static final String COMMUNICATION = "Communication";
        public static final String GENDER = "Gender";
        public static final String GROUP_UID = "GroupUID";
        public static final String BIRTHDAY = "Birthday";
        public static final String TITLE = "Title";
        public static final String COMMENT = "Comment";
        public static final String NOTIFY_BIRTHDAY = "NotifyBirthday";
        public static final String FAVORITE = "Favorite";
        public static final String SHOW_IN_NAVIGATOR = "ShowInNavigator";
        public static final String PHONE = "Phone";

        public static final String USN_ENTITY = "__usn_entity";
        public static final String USN_FIELD_FIRSTNAME = "__usn_field_firstname";
        public static final String USN_FIELD_LASTNAME = "__usn_field_lastname";
        public static final String USN_FIELD_MIDDLENAME = "__usn_field_middlename";
        public static final String USN_FIELD_DETAILS = "__usn_field_details";
        public static final String USN_FIELD_GENDER = "__usn_field_gender";
        public static final String USN_FIELD_COUNTRY = "__usn_field_country";
        public static final String USN_FIELD_PROVINCE = "__usn_field_province";
        public static final String USN_FIELD_POSTALCODE = "__usn_field_postalcode";
        public static final String USN_FIELD_CITY = "__usn_field_city";
        public static final String USN_FIELD_STREET = "__usn_field_street";
        public static final String USN_FIELD_UID_GROUP = "__usn_field_uid_group";
        public static final String USN_FIELD_BIRTHDAY = "__usn_field_birthday";
        public static final String USN_FIELD_COMMUNICATION = "__usn_field_communication";
        public static final String USN_FIELD_ORDER = "__usn_field_order";
        public static final String USN_FIELD_TITLE = "__usn_field_title";
        public static final String USN_FIELD_COMMENT = "__usn_field_comment";
        public static final String USN_FIELD_NOTIFYBIRTHDAY = "__usn_field_notifybirthday";
        public static final String USN_FIELD_FAVORITE = "__usn_field_favorite";
        public static final String USN_FIELD_SHOWINNAVIGATOR = "__usn_field_showinnavigator";
        public static final String USN_FIELD_FOTO = "__usn_foto";
        public static final String USN_FIELD_PHONE = "__usn_field_phone";

        public static final String ORDERS = "Orders";
        public static final String SEND_ENTITY = "SendEntity";

        public static String selectionUid(UUID uid) {
            return selectionUid(String.valueOf(uid));
        }

        public static String selectionLogin(String login) {
            return SelectionKeeper.equals(null, LOGIN, login);
        }

        public static String selectionUid(String uid) {
            return SelectionKeeper.equals(null, UID, uid);
        }

        public static String selectionSendEntity(boolean sendEntity) {
            return SelectionKeeper.equals(null, SEND_ENTITY, sendEntity);
        }

        public static String selectionEmpEmailsFromUids(StringBuilder sb ,Iterable<String> uids ) {
            sb.append(SharedStrings.SELECT);
            sb.append(EmpContract.LOGIN);
            sb.append(SharedStrings.FROM);
            sb.append(EmpContract.TABLE_NAME);
            sb.append(SharedStrings.WHERE);

            sb.append(EmpContract.UID);
            sb.append(SharedStrings.IN);
            sb.append(SharedStrings.BRACE_OPEN_C);

            boolean start = true;
            for (String value : uids) {
                if (start) {
                    start = false;
                } else {
                    sb.append(SharedStrings.COMMA_C);
                }

                sb.append(SharedStrings.QUOTE_C);
                sb.append(value);
                sb.append(SharedStrings.QUOTE_C);
            }
            sb.append(SharedStrings.BRACE_CLOSE_C);
            return sb.toString();
        }

        public static final String DEFAULT_SORT = defaultSort();

        private static String defaultSort() {
            final StringBuilder sb = new StringBuilder();

            SelectionKeeper.sortCaseWhen(sb, UID, EQUALS, Emp.DEFAULT_UUID_EMP_S);
            sb.append(COMMA_C);
            SelectionKeeper.sort(sb, ORDERS, LOGIN);

            return sb.toString();
        }

        public static String selectionOrders(int order) {
            return SelectionKeeper.equals(null, ORDERS, order);
        }

        public static String selectionNotDefaultEmp() {
            return SelectionKeeper.notEquals(null, UID, Emp.DEFAULT_UUID_EMP_S);
        }
    }

    /**
     * 2014-06-20
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class EmployeeContract implements BaseColumns {

        public static final String TABLE_NAME = EMPLOYEES_TABLE_NAME;
        public static final String SERVER_CLASS = "VUEmployee";

        public static final Uri CONTENT_URI = new Uri.Builder()//
                .scheme(ContentResolver.SCHEME_CONTENT)//
                .authority(AUTHORITY)//
                .appendPath(TABLE_NAME).build();

        public static final String NAME = "name";
        public static final String EMAIL = "email";

        public static final String DEFAULT_SORT = SelectionKeeper.sort(null, EMAIL);
    }

    /**
     * 
     * @since 2014-06-19
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static class SelectionKeeper {

        public static String equalsUUID(StringBuilder sb, String columnName, UUID uuid) {
            return equals(sb, columnName, String.valueOf(uuid).toUpperCase());
        }

        public static String equals(StringBuilder sb, String columnName, Object obj) {
            return equals(sb, columnName, String.valueOf(obj));
        }

        public static String notEquals(StringBuilder sb, String columnName, String value) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(NOT_EQUALS);
            sb.append(QUOTE_C);
            sb.append(value);
            sb.append(QUOTE_C);

            return sb.toString();
        }

        public static String equals(StringBuilder sb, String columnName, String value) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(EQUALS_C);
            sb.append(QUOTE_C);
            sb.append(value);
            sb.append(QUOTE_C);

            return sb.toString();
        }

        public static String equals(StringBuilder sb, String columnName, boolean value) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(EQUALS_C);
            sb.append(value ? ONE : ZERO);

            return sb.toString();
        }

        public static String equals(StringBuilder sb, String columnName, long value) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(EQUALS_C);
            sb.append(value);

            return sb.toString();
        }

        public static String in(StringBuilder sb, String columnName, Iterable<String> values) {
            return in(sb, columnName, values, true);
        }

        public static String in(StringBuilder sb, String columnName, Iterable<String> values, boolean in) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(in ? IN : NOT_IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (String value : values) {
                if (start) {
                    start = false;
                } else {
                    sb.append(COMMA_C);
                }

                sb.append(QUOTE_C);
                sb.append(value);
                sb.append(QUOTE_C);
            }
            sb.append(BRACE_CLOSE_C);

            return sb.toString();
        }

        /** if toLowerCase = false than value.toUpperCase() */
        public static String inToLowerCase(StringBuilder sb, String columnName, List<String> values) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (String value : values) {
                if (start) {
                    start = false;
                } else {
                    sb.append(COMMA_C);
                    sb.append(SPACE_C);
                }

                sb.append(QUOTE_C);
                sb.append(value.toLowerCase());
                sb.append(QUOTE_C);
            }
            sb.append(BRACE_CLOSE_C);

            return sb.toString();
        }

        public static String sort(StringBuilder sb, String... columnNames) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            boolean start = true;
            for (String column : columnNames) {
                if (start) {
                    start = false;
                } else {
                    sb.append(COMMA_C);
                }

                sb.append(column);
            }

            return sb.toString();
        }

        public static String sortCaseWhen(StringBuilder sb, String columnName, String sign, String value) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(CASE_WHEN);
            sb.append(columnName);
            sb.append(sign);
            sb.append(QUOTE_C);
            sb.append(value);
            sb.append(QUOTE_C);
            sb.append(THEN_ELSE);

            return sb.toString();
        }

        public static StringBuilder isNull(StringBuilder sb, String columnName, boolean isNull) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(isNull ? IS_NULL : IS_NOT_NULL);

            return sb;
        }

        public static StringBuilder in(StringBuilder sb, String columnName, int... ids) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (int id : ids) {
                if (start) {
                    start = false;
                } else {
                    sb.append(COMMA_C);
                }

                sb.append(id);
            }
            sb.append(BRACE_CLOSE_C);

            return sb;
        }

        public static StringBuilder in(StringBuilder sb, String columnName, String... ids) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (String id : ids) {
                if (start) {
                    start = false;
                } else {
                    sb.append(COMMA_C);
                }

                sb.append(QUOTE_C);
                sb.append(id);
                sb.append(QUOTE_C);
            }
            sb.append(BRACE_CLOSE_C);

            return sb;
        }

        public static StringBuilder eq(StringBuilder sb, String columnName, String value) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(EQUALS_C);
            sb.append(QUOTE_C);
            sb.append(value);
            sb.append(QUOTE_C);

            return sb;
        }

        public static StringBuilder eq(StringBuilder sb, String columnName, int value) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(columnName);
            sb.append(EQUALS_C);
            sb.append(value);

            return sb;
        }

        public static StringBuilder order(StringBuilder sb, String column, boolean asc) {
            if (sb == null) {
                sb = new StringBuilder();
            }

            sb.append(column);
            sb.append(asc ? ASC : DESC);

            return sb;
        }

        /** Sb must be not NULL */
        public static StringBuilder in(StringBuilder sb, String columnName, LTask... uids) {
            sb.append(columnName);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (LTask uid : uids) {
                if (start) {
                    start = false;

                } else {
                    sb.append(COMMA_C);
                }

                sb.append(QUOTE_C);
                sb.append(uid.getUid());
                sb.append(QUOTE_C);
            }
            sb.append(BRACE_CLOSE_C);

            return sb;
        }

        /** Sb must be not NULL */
        public static StringBuilder in(StringBuilder sb, String columnName, String selection) {
            sb.append(columnName);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);
            sb.append(selection);
            sb.append(BRACE_CLOSE_C);

            return sb;
        }

        /** StringBuilder != null */
        public static StringBuilder orderLimitOne(StringBuilder sb, String column, boolean asc) {
            sb.append(column);
            sb.append(asc ? ASC : DESC);
            sb.append(LIMIT);
            sb.append(ONE);

            return sb;
        }

        /** StringBuilder != null */
        public static String in(StringBuilder sb, String columnName, List<Integer> values) {
            sb.append(columnName);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (Integer value : values) {
                if (start) {
                    start = false;

                } else {
                    sb.append(COMMA_C);
                }
                sb.append(value);
            }
            sb.append(BRACE_CLOSE_C);

            return sb.toString();
        }

        /** Sb must be not NULL */
        public static StringBuilder inTaskIds(StringBuilder sb, String columnName, List<LTask> ids) {
            sb.append(columnName);
            sb.append(IN);
            sb.append(BRACE_OPEN_C);

            boolean start = true;
            for (LTask id : ids) {
                if (start) {
                    start = false;

                } else {
                    sb.append(COMMA_C);
                }

                sb.append(id.getIdTask());
            }
            sb.append(BRACE_CLOSE_C);

            return sb;
        }

        /** Sb must be not NULL */
        public static StringBuilder like(StringBuilder sb, String columnName, String uid) {
            sb.append(columnName);
            sb.append(LIKE);
            
            sb.append(QUOTE_C);
            sb.append(PERCENT_C);
            sb.append(uid);
            sb.append(PERCENT_C);
            sb.append(QUOTE_C);

            return sb;
        }
    }
}