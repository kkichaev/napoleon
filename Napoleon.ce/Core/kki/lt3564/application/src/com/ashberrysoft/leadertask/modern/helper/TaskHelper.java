package com.ashberrysoft.leadertask.modern.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CompletedTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import static com.ashberrysoft.leadertask.R.string.task;
import static com.ashberrysoft.leadertask.utils.SharedStrings.AND;

public class TaskHelper {

    private static final SimpleDateFormat SDF = getSimpleDateFormat();

    private static SimpleDateFormat getSimpleDateFormat() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeHelper.DEFAULT_TIME_ZONE);

        return sdf;
    }

    public static SimpleDateFormat getTaskFormat() {
        if (SDF.getTimeZone() == TimeHelper.DEFAULT_TIME_ZONE) {
            return SDF;
        }

        Utils.toLog("$ $ $ $ getTaskFormat timeZone not DEFAULT");
        SDF.setTimeZone(TimeHelper.DEFAULT_TIME_ZONE);
        return SDF;
    }

    private static final int COMPLETED = TaskStatus.COMPLETED.getCode();
    private static final int CANCELLED = TaskStatus.CANCELLED.getCode();
    private static final int READY = TaskStatus.READY.getCode();
    private static final int INWORK = TaskStatus.IN_WORK.getCode();
    private static final int REJECTED = TaskStatus.REJECTED.getCode();

    public static boolean isCompleted(int status, String currentUser, String customerUser) {
        return status == COMPLETED || status == CANCELLED || (!currentUser.equals(customerUser) && (status == READY || status == REJECTED));
    }

    public static boolean isUncompleted(int status, String currentUser, String customerUser) {
        return status != COMPLETED && status != CANCELLED && (currentUser.equals(customerUser) || (status != READY && status != REJECTED));
    }

    @Deprecated
    public static Calendar calenderTo(Calendar calendar, boolean toBegin) {
        return TimeHelper.roundCalendar(calendar, toBegin);
    }

    public static String[] getCategoriesFromString(String categories) {
        if (TextUtils.isEmpty(categories)) {
            return Utils.EMPTY_STRING_ARRAY;
        }
        return categories.split(SharedStrings.SPLIT_DOT_DOBLE);
    }

    public static String[] getContactsFromString(String contacts) {
        if (TextUtils.isEmpty(contacts)) {
            return Utils.EMPTY_STRING_ARRAY;
        }
        return contacts.split(SharedStrings.SPLIT_DOT_DOBLE);
    }

    public static void appendSeriesString(Context context, StringBuilder outSb, LTask task, boolean withComma) {
        final SeriesType type = SeriesType.values()[task.getSeriesType()];
        if (type == SeriesType.NONE) {
            return;
        }

        final Resources r = context.getResources();
        final StringBuilder sb = new StringBuilder();

        sb.append(r.getString(type.getMainResId()));
        sb.append(SharedStrings.COLON_C);
        sb.append(SharedStrings.SPACE_C);
        try {
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
                    isDayOfWeek[0] = task.getSeriesWeekMon();
                    isDayOfWeek[1] = task.getSeriesWeekTue();
                    isDayOfWeek[2] = task.getSeriesWeekWed();
                    isDayOfWeek[3] = task.getSeriesWeekThu();
                    isDayOfWeek[4] = task.getSeriesWeekFri();
                    isDayOfWeek[5] = task.getSeriesWeekSat();
                    isDayOfWeek[6] = task.getSeriesWeekSun();
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
                        try {
                            sb.append(dayOfWeekNames[task.getSeriesMonthDayOfWeek() - 1]);
                        } catch (Exception e) {

                        }
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
        } finally {
            if (withComma) {
                outSb.append(SharedStrings.COMMA_C);
                outSb.append(SharedStrings.SPACE_C);
            }
            outSb.append(sb);
        }
    }

    public static LTask createNewTaskWithParams(String currentUser, String performer, long term, String parentId, String projectId, String categoryId, String colorId) {
        final LTask task = new LTask();
        UUID taskUuid = UUID.randomUUID();
        task.setUid(String.valueOf(taskUuid).toUpperCase());

        task.setEmailCustomer(currentUser);
        if (performer != null) {
            task.setPerformTime(System.currentTimeMillis());
            task.setEmailPerformer(performer);
            task.setUsnFieldEmailPerformer(1);

            task.setPerformerReaded(false);
            task.setUsnFieldPerformerReaded(1);
        } else {
            task.setEmailPerformer(currentUser);
            task.setUsnFieldEmailPerformer(1);

            task.setPerformerReaded(true);
            task.setUsnFieldPerformerReaded(1);
        }
        if (term != 0) {
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
            calendar.setTimeInMillis(term);

            TimeHelper.roundCalendar(calendar, false);
            task.setTermEnd(calendar.getTimeInMillis());

            TimeHelper.roundCalendar(calendar, true);
            task.setTermBegin(calendar.getTimeInMillis());

            task.setUsnFieldTerm(1);
        }
        if (parentId != null) {
            task.setUIDParent(parentId.toUpperCase());
            task.setUsnFieldUidParent(1);
        }
        if (projectId != null) {
            task.setUidProject(projectId);
            task.setUsnFieldUidProject(1);
        }
        if (categoryId != null) {
            task.setCategories(categoryId);
            task.setUsnFieldCategories(1);
        }

        if (colorId != null) {
            task.setUidMarker(colorId);
            task.setUsnFieldUidMarker(1);
        }
        return task;
    }

    public static String getStringFromCategories(List<Category> values) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Category value : values) {
            if (first) {
                first = false;

            } else {
                sb.append(SharedStrings.DOT_C);
                sb.append(SharedStrings.DOT_C);
            }
            sb.append(String.valueOf(value.getId()).toUpperCase());
        }

        return sb.toString();
    }

    public static String getStringFromContacts(List<Contact> values) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Contact value : values) {
            if (first) {
                first = false;

            } else {
                sb.append(SharedStrings.DOT_C);
                sb.append(SharedStrings.DOT_C);
            }
            sb.append(String.valueOf(value.getId()).toUpperCase());
        }

        return sb.toString();
    }

    public static boolean isCompletedTask(Context context, int taskId) {
        return getCountItems(context, CompletedTaskContract.CONTENT_URI, SelectionKeeper.equals(null, CompletedTaskContract._ID, taskId)) == 1;
    }

    public static boolean isCompletedTask(Context context, String taskUid) {
        return getCountItems(context, CompletedTaskContract.CONTENT_URI, SelectionKeeper.equals(null, CompletedTaskContract.Uid, taskUid)) == 1;
    }

    public static LTask getTask(Context context, String taskUid) {
        return getSingleItem(context, LTask.class, LTaskContract.CONTENT_URI, SelectionKeeper.equals(null, LTaskContract.Uid, taskUid));
    }

    public static TaskTotalLink getTaskTotalLink(Context context, String uid) {
        final LTask task = getTask(context, uid);
        return task == null ? null : getTaskTotalLink(context, task.getIdTask());
    }

    public static TaskTotalLink getTaskTotalLink(Context context, int id) {
        return getSingleItem(context, TaskTotalLink.class, TaskTotalLinkContract.CONTENT_URI, SelectionKeeper.equals(null, TaskTotalLinkContract.Uid, id));
    }

    public static TaskTotalLink getTaskTotalLink(Context context, StringBuilder sb, int id) {
        Utils.clearStringBuilder(sb);
        return getSingleItem(context, TaskTotalLink.class, TaskTotalLinkContract.CONTENT_URI, SelectionKeeper.equals(sb, TaskTotalLinkContract.Uid, id));
    }

    public static <T extends CursorFiller> T getSingleItem(Context context, Class<T> cls, Uri uri, String selection) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(uri, null, selection, null, null);
            if (c.moveToFirst()) {
                final T t = cls.newInstance();
                t.fillFromCursor(c);

                return t;
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static <T extends CursorFiller> boolean fillSingleItem(Context context, T t, String selection) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(t.getContentUri(), null, selection, null, null);
            if (c.getCount() == 1 && c.moveToFirst()) {
                t.fillFromCursor(c);
                return true;
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    public static int getCountItems(Context context, Uri uri, String selection) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(uri, null, selection, null, null);
            return c.getCount();

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return 0;
    }

    public static int getHashFromUid(UUID uid) {
        return getHashFromUid(String.valueOf(uid));
    }

    /** Only if lower case and not null!!! */
    public static int getHashFromUid(String uid) {
        return uid.hashCode();
    }

    public static boolean isInboxTask(Context context, LTask task) {
        if (task.getUIDParent() != null) {
            return false;
        }

        if (task.getTermBegin() != 0 || task.getTermEnd() != 0) {
            return false;
        }

        final String currentUser = LTSettings.getInstance(context).getUserName();
        if (!currentUser.equals(task.getEmailCustomer()) || !currentUser.equals(task.getEmailPerformer())) {
            return false;
        }

        final DbHelper dbHelper = DbHelper.getInstance(context);
        try {
            if (task.getUidProject() != null && dbHelper.getProjectDao().queryForId(UUID.fromString(task.getUidProject())) != null) {
                return false;
            }

        } catch (Exception e) {
            Utils.toLog(e);
            return false;
        }
        return true;
    }

    public static boolean isUnreadTask(LTask task) {
        if (task.getReaded() == false) {
            return true;
        }
        return false;
    }

    public static boolean isReadyTask( LTask task) {
        if (task.getEmailCustomer().equals(LTSettings.getInstance().getUserName()) && task.getStatus() == READY) {
            return true;
        }
        return false;
    }

    public static boolean isFocusTask(LTask task) {
        return task.getFocus() && task.getStatus() != COMPLETED;
    }

    public static boolean isColorTask(Context context,LTask task) {
        if (task.getUidMarker() != null && task.getUidMarker() != Marker.DEFAULT_MARKER_STRING) {
            // если есть маркер
            Marker marker = null;
            try {
                marker = DbHelper.getInstance(context).getMarkerByUUId(UUID.fromString(task.getUidMarker()));
                if (marker != null) {
                    if (marker.getCreator().equals(LTSettings.getInstance().getUserName())){
                        // если я создатель маркера
                        return true;
                    }
                }

            } catch (Exception e) {

            }

        }
        return false;
    }

    public static boolean isOverdueTask( LTask task) {
        final long dayStart;
        final long dayEnd;
        final long date = TimeHelper.currentTimeMillisWithoutTimeZone();
        {
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
            calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

            TimeHelper.roundCalendar(calendar, false);

            calendar.setTimeInMillis(date);

            TimeHelper.roundCalendar(calendar, true);
            dayStart = calendar.getTimeInMillis();

            TimeHelper.roundCalendar(calendar, false);
            dayEnd = calendar.getTimeInMillis();

        }

        if ( ((task.getTermBegin() != 0 && task.getTermEnd() != 0) || (task.getTermBeginCustomer() != 0 && task.getTermEndCustomer() != 0 && task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()))) && task.getStatus() != 1 && task.getStatus() != 7 && ((task.getTermEnd() < dayStart && task.getTermBegin() < dayEnd && task.getTermEndCustomer() < dayStart && task.getTermBeginCustomer() < dayEnd && ( task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) || !task.getEmailCustomer().equals(LTSettings.getInstance().getUserName()))) || ( task.getTermEnd() < dayStart && task.getTermEndCustomer() < dayStart && task.getStatus() != 1 && task.getStatus() != 7 &&
                (task.getEmailCustomer().equals(LTSettings.getInstance().getUserName()) || (task.getStatus() != 5 && task.getStatus() != 8) || (!task.getEmailCustomer().equals(LTSettings.getInstance().getUserName()) && !task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) && task.getUidProject() != null && task.getStatus() == 5 && task.getStatus() == 8) )
        ))) {
            return true;
        }
        return false;
    }

    public static boolean isInworkTask( LTask task) {
        if (task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) && task.getStatus() == INWORK) {
            return true;
        }
        return false;
    }

    /** Выборка должна быть простой (без сортировки и прочего) */
    public static <T extends CursorFiller> T getFirstInOrder(Context context,//
            Class<T> cls, String selection, String columnOrder, boolean ascOrder) {
        Cursor c = null;

        try {
            final T t = cls.newInstance();

            c = context.getContentResolver().query(t.getContentUri(), null, selection, null, //
                    columnOrder + (ascOrder ? SharedStrings.ASC : SharedStrings.DESC)//
                            + SharedStrings.LIMIT + SharedStrings.ONE);
            if (c.moveToFirst()) {
                t.fillFromCursor(c);

                return t;
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static boolean belongsToCalendarDate(long day, LTask task) {
        boolean answer = false;

        final String currentUser = LTSettings.getInstance().getUserName();
        final boolean beforeOrToday;
        final long dayStart;
        final long dayEnd;
        {
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);

            TimeHelper.roundCalendar(calendar, false);
            beforeOrToday = day <= calendar.getTimeInMillis();

            calendar.setTimeInMillis(day);

            TimeHelper.roundCalendar(calendar, true);
            dayStart = calendar.getTimeInMillis();

            TimeHelper.roundCalendar(calendar, false);
            dayEnd = calendar.getTimeInMillis();
        }

        final boolean termBeginExists = task.getTermBegin() != 0;
        final boolean termEndExists = task.getTermEnd() != 0;

        final boolean termBeginMoreEqDayStart = task.getTermBegin() >= dayStart;
        final boolean termEndLessEqDayEnd = task.getTermEnd() <= dayEnd;

        final boolean customer = currentUser.equals(task.getEmailCustomer());
        final boolean performer = currentUser.equals(task.getEmailPerformer());

        final boolean termEndLessDayStart = task.getTermEnd() < dayStart;

        if (termBeginExists && termEndExists && //
                ((termBeginMoreEqDayStart && termEndLessEqDayEnd && (performer || !customer)) || //
                (beforeOrToday && termEndLessDayStart))) {
            // && isUncompleted(task.getStatus(), currentUser, task.getEmailCustomer())
            answer = true;

        } else if (!termBeginExists && !termEndExists && performer && !customer) {
            final boolean termBeginCustomerExists = task.getTermBeginCustomer() != 0;
            final boolean termEndCustomerExists = task.getTermEndCustomer() != 0;

            if (termBeginCustomerExists && termEndCustomerExists) {
                final boolean termBeginCustomerMoreEqDayStart = task.getTermBeginCustomer() >= dayStart;
                final boolean termEndCustomerLessEqDayEnd = task.getTermEndCustomer() <= dayEnd;

                if (termBeginCustomerMoreEqDayStart && termEndCustomerLessEqDayEnd) {
                    answer = true;

                } else if (beforeOrToday) {
                    final boolean termEndCustomerLessDayStart = task.getTermEndCustomer() < dayStart;

                    if (termEndCustomerLessDayStart) {
                        // && isUncompleted(task.getStatus(), currentUser, task.getEmailCustomer())
                        answer = true;
                    }
                }
            }
        }

        return answer;
    }
}