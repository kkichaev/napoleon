package com.ashberrysoft.leadertask.content_providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.utils.SharedStrings;

public final class LionMetaData implements SharedStrings {

    public static final String TABLE_LION_TASK = "LionTask";

    public static final String TABLE_SEND_UID = "SendUid";
    public static final String TABLE_DELETE_UID = "DeleteUid";

    public static final String TABLE_SET_BLOCKING = "SetBlocking";
    public static final String TABLE_TASK_NOTIFY = "TaskNotify";

    public static final String TABLE_COMPLETED_TASK = "CompletedTask";
    public static final String TABLE_VERTICAL_DEPTH_TASK = "VerticalDepthTask";

    // arkgnapergnparengpaergaerg
    public static final String TABLE_TASK_LINK = "TaskLink";
    public static final String TABLE_CALENDAR_LINK = "CalendarLink";
    public static final String TABLE_INBOX_LINK = "InboxLink";
    public static final String TABLE_UNREAD_LINK = "UnreadLink";
    public static final String TABLE_FOCUS_LINK = "FocusLink";
    public static final String TABLE_READY_LINK = "ReadyLink";
    public static final String TABLE_BY_ME_LINK = "ByMeLink";
    public static final String TABLE_EMP_LINK = "EmpLink";
    public static final String TABLE_FOR_ME_LINK = "ForMeLink";
    public static final String TABLE_PROJECT_LINK = "ProjectLink";
    public static final String TABLE_CATEGORY_LINK = "CategoryLink";
    public static final String TABLE_INWORK_LINK = "InworkLink";
    public static final String TABLE_OVERDUE_LINK = "OverdueLink";
    public static final String TABLE_COLOR_LINK = "ColorLink";

    public static final String TABLE_Task_TOTAL_LINK = "TaskTotalLink";
    public static final String TABLE_CALENDAR_TOTAL_LINK = "CalendarTotalLink";
    public static final String TABLE_INBOX_TOTAL_LINK = "InboxTotalLink";
    public static final String TABLE_UNREAD_TOTAL_LINK = "UnreadTotalLink";
    public static final String TABLE_FOCUS_TOTAL_LINK = "FocusTotalLink";
    public static final String TABLE_READY_TOTAL_LINK = "ReadyTotalLink";
    public static final String TABLE_BY_ME_TOTAL_LINK = "ByMeTotalLink";
    public static final String TABLE_EMP_TOTAL_LINK = "EmpTotalLink";
    public static final String TABLE_FOR_ME_TOTAL_LINK = "ForMeTotalLink";
    public static final String TABLE_PROJECT_TOTAL_LINK = "ProjectTotalLink";
    public static final String TABLE_CATEGORY_TOTAL_LINK = "CategoryTotalLink";
    public static final String TABLE_INWORK_TOTAL_LINK = "InworkTotalLink";
    public static final String TABLE_OVERDUE_TOTAL_LINK = "OverdueTotalLink";
    public static final String TABLE_COLOR_TOTAL_LINK = "ColorTotalLink";

    public abstract static class BaseLionColumns implements BaseColumns {

        public static Uri createContentUri(String tableName) {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(LeaderTaskProviderMetaData.AUTHORITY).appendPath(tableName).build();
        }

        public static final String Uid = "uid";

        public static final String Orders = "orders";

        public static final String UsnEntity = "__usn_entity";
    }

    public static final class LTaskContract extends BaseLionColumns {

        public static final String TABLE_NAME = TABLE_LION_TASK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);

        public static final String UIDParent = "uidparent";
        public static final String Order = "order";
        public static final String Collapsed = "collapsed";
        public static final String Name = "name";
        public static final String Comment = "comment";
        public static final String Status = "status";
        public static final String TermBegin = "termbegin";
        public static final String TermEnd = "termend";
        public static final String Time = "time";
        public static final String Plan = "plan";
        public static final String InWorkTime = "inworktime";
        public static final String EmailPerformer = "emailperformer";
        public static final String UidProject = "uidproject";
        public static final String UidMarker = "uidmarker";
        public static final String Readed = "readed";
        public static final String OrderCustomer = "ordercustomer";
        public static final String TermBeginCustomer = "termbegincustomer";
        public static final String TermEndCustomer = "termendcustomer";
        public static final String EmailCustomer = "emailcustomer";
        public static final String Categories = "categories";
        public static final String Contacts = "contacts";
        public static final String CreateTime = "createtime";
        public static final String PerformTime = "performtime";
        public static final String CompleteTime = "completetime";
        public static final String SeriesType = "seriestype";
        public static final String SeriesAfterType = "seriesaftertype";
        public static final String SeriesAfterCount = "seriesaftercount";
        public static final String SeriesWeekCount = "seriesweekcount";
        public static final String SeriesWeekMon = "seriesweekmon";
        public static final String SeriesWeekTue = "seriesweektue";
        public static final String SeriesWeekWed = "seriesweekwed";
        public static final String SeriesWeekThu = "seriesweekthu";
        public static final String SeriesWeekFri = "seriesweekfri";
        public static final String SeriesWeekSat = "seriesweeksat";
        public static final String SeriesWeekSun = "seriesweeksun";
        public static final String SeriesMonthType = "seriesmonthtype";
        public static final String SeriesMonthCount = "seriesmonthcount";
        public static final String SeriesMonthDay = "seriesmonthday";
        public static final String SeriesMonthWeekType = "seriesmonthweektype";
        public static final String SeriesMonthDayOfWeek = "seriesmonthdayofweek";
        public static final String SeriesYearType = "seriesyeartype";
        public static final String SeriesYearMonth = "seriesyearmonth";
        public static final String SeriesYearMonthDay = "seriesyearmonthday";
        public static final String SeriesYearWeekType = "seriesyearweektype";
        public static final String SeriesYearDayOfWeek = "seriesyeardayofweek";
        public static final String SeriesEnd = "seriesend";

        public static final String UsnFieldUidParent = "__usn_field_uid_parent";
        public static final String UsnFieldEmailPerformer = "__usn_field_email_performer";
        public static final String UsnFieldName = "__usn_field_name";
        public static final String UsnFieldComment = "__usn_field_comment";
        public static final String UsnFieldStatus = "__usn_field_status";
        public static final String UsnFieldOrder = "__usn_field_order";
        public static final String UsnFieldUidProject = "__usn_field_uid_project";
        public static final String UsnFieldUidMarker = "__usn_field_uid_marker";
        public static final String UsnFieldTerm = "__usn_field_term";
        public static final String UsnFieldReaded = "__usn_field_readed";
        public static final String UsnFieldCollapsed = "__usn_field_collapsed";
        public static final String UsnFieldCustomerOrder = "__usn_field_customer_order";
        public static final String UsnFieldCustomerTerm = "__usn_field_customer_term";
        public static final String UsnFieldCategories = "__usn_field_categories";
        public static final String UsnFieldContacts = "__usn_field_contacts";
        public static final String UsnFieldCreatetime = "__usn_field_createtime";
        public static final String UsnFieldPerformtime = "__usn_field_performtime";
        public static final String UsnFieldCompletetime = "__usn_field_completetime";
        public static final String UsnFieldSeries = "__usn_field_series";
        public static final String OrderNew = "ordernew";
        public static final String UsnOrderNew = "__usn_field_order_new";
        public static final String UserOrder = "userorder";
        public static final String MarkerOrder = "markerorder";
        public static final String IsUseTerm = "isuseterm";
        public static final String IsUseTermCustomer = "isusetermcustomer";
        public static final String PerformerReaded = "performerreaded";
        public static final String UsnPerformerReaded = "__usn_field_performerreaded";
        public static final String UsnTime = "__usn_field_time";
        public static final String UsnPlan = "__usn_field_plan";
        public static final String UsnInWorkTime = "__usn_field_inworktime";
        public static final String Focus = "focus";
        public static final String UsnFieldFocus = "__usn_field_focus";
        public static final String Emails = "emails";
        public static final String UsnFieldListMembers = "__usn_field_list_members";
    }

    public abstract static class ProcessUidContract extends BaseLionColumns {

        public static final String LionName = "lionname";

        public static String selectByLionName(String lionName) {
            return SelectionKeeper.equals(null, LionName, lionName);
        }
    }

    public static final class SendUidContract extends ProcessUidContract {

        public static final String TABLE_NAME = TABLE_SEND_UID;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class DeleteUidContract extends ProcessUidContract {

        public static final String TABLE_NAME = TABLE_DELETE_UID;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class SetBlockingContract implements BaseColumns {

        public static final String TABLE_NAME = TABLE_SET_BLOCKING;
        public static final Uri CONTENT_URI = BaseLionColumns.createContentUri(TABLE_NAME);

        public static final String Blocking = "blocking";
    }

    public static final class TaskNotifyContract implements BaseColumns {

        public static final String TABLE_NAME = TABLE_TASK_NOTIFY;
        public static final Uri CONTENT_URI = BaseLionColumns.createContentUri(TABLE_NAME);

        public static final String Time = "time";
    }

    public static final class CompletedTaskContract extends BaseLionColumns {

        public static final String TABLE_NAME = TABLE_COMPLETED_TASK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);

        public static final String ParentCompleted = "parentcompleted";

        public static final String TaskCompleted = "taskcompleted";
    }

    public static interface MenuItemContract {

        public static final String OfCurrentUser = "ofcurrentuser";
        public static final String Name = "name";
        public static final String Level = "level";
        public static final String HasBelow = "hasbelow";
        public static final String Opened = "opened";
        public static final String Visible = "visible";
        public static final String Showed = "showed";
    }

    public static final class VerticalDepthTaskContract implements BaseColumns {

        public static final String TABLE_NAME = TABLE_VERTICAL_DEPTH_TASK;
        public static final Uri CONTENT_URI = BaseLionColumns.createContentUri(TABLE_NAME);

        public static final String Vertical = "vertical";
        public static final String Depth = "depth";
        public static final String ParentId = "parentid";
    }

    // alkrngaerngparnegpaerng

    public static class LinkContract extends BaseLionColumns {

        public static final String TaskId = "taskid";
        public static final String Readed = "readed";
        public static final String Status = "status";
        public static final String Focus = "focus";
    }

    public static final class TaskLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_TASK_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class InboxLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_INBOX_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class UnreadLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_UNREAD_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class FocusLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_FOCUS_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ReadyLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_READY_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class InworkLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_INWORK_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class OverdueLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_OVERDUE_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ColorLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_COLOR_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class CalendarLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_CALENDAR_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ByMeLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_BY_ME_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class EmpLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_EMP_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ForMeLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_FOR_ME_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ProjectLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_PROJECT_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class CategoryLinkContract extends LinkContract {

        public static final String TABLE_NAME = TABLE_CATEGORY_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static class TotalLinkContract extends BaseLionColumns {

        public static final String Tasks = "tasks";
        public static final String TasksUnreaded = "tasksunreaded";
        public static final String TasksUncompleted = "tasksuncompleted";
        public static final String TasksUncompletedUnreaded = "tasksuncompletedunreaded";
        public static final String TasksNotes = "tasksnotes";
        public static final String TasksFocus = "tasksfocus";

        public static final String DEFAULT_ORDER = SelectionKeeper.sort(null, Uid);
    }

    public static final class TaskTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_Task_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);

        public static final String ParentId = "parentid";
    }

    public static final class InboxTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_INBOX_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class UnreadTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_UNREAD_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class FocusTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_FOCUS_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ReadyTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_READY_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class InworkTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_INWORK_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class OverdueTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_OVERDUE_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ColorTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_COLOR_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class CalendarTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_CALENDAR_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ByMeTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_BY_ME_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class EmpTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_EMP_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class ForMeTotalLinkContract extends TotalLinkContract {

        public static final String TABLE_NAME = TABLE_FOR_ME_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static class CollapsibleTotalLinkContract extends TotalLinkContract {

        public static final String Name = "name";
        public static final String BelongCurrentUser = "belongcurrentuser";
        public static final String Level = "level";
        public static final String HasBelow = "hasbelow";
        public static final String Opened = "opened";
        public static final String Visible = "visible";
        public static final String Showed = "showed";
        public static final String Shared = "shared";
    }

    public static final class ProjectTotalLinkContract extends CollapsibleTotalLinkContract {

        public static final String TABLE_NAME = TABLE_PROJECT_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }

    public static final class CategoryTotalLinkContract extends CollapsibleTotalLinkContract {

        public static final String TABLE_NAME = TABLE_CATEGORY_TOTAL_LINK;
        public static final Uri CONTENT_URI = createContentUri(TABLE_NAME);
    }
}