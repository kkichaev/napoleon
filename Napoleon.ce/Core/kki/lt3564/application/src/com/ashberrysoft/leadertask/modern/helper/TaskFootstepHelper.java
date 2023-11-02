package com.ashberrysoft.leadertask.modern.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ReadyTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.InworkTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.InboxTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.modern.domains.link.ForMeLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import static com.ashberrysoft.leadertask.R.string.task;
import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.mApp;

/** Класс для уменьшения или увеличения */
public class TaskFootstepHelper//
        implements SharedStrings {

    // BASE
    private final Context mContext;

    // VALUE's
    private final String mCurrentUser;
    private final StringBuilder mSb;
    private final CompletedCache mCompletedCache;
    private final TaskSelectionBuilder mSelectionBuilder;

    private final List<String> mRawQueries;
    private final Map<String, Uri> mNotifyUries;
    private final Map<String, String> mParentUid;

    public TaskFootstepHelper(Context context) {
        mContext = context.getApplicationContext();

        mCurrentUser = LTSettings.getInstance().getUserName();
        mSb = new StringBuilder();
        mCompletedCache = CompletedCache.getInstance(mContext);
        mSelectionBuilder = new TaskSelectionBuilder();

        mRawQueries = new ArrayList<>();
        mNotifyUries = new HashMap<>();
        mParentUid = new HashMap<>();
    }

    public void changeTotalAndApply(List<LTask> tasks, boolean newTask, boolean increment) {
        for (LTask task : tasks) {
            changeTotal(task, newTask, increment, false, false, false);
        }
        apply();
    }

    public void changeTotalUnreadedAndApply(LTask task, boolean newTask, boolean increment) {
        changeTotal(task, newTask, increment, true, false, false);
        apply();
    }

    public void changeTotalNotes(List<LTask> tasks, boolean newTask, boolean increment) {
        for (LTask task : tasks) {
            changeTotal(task, newTask, increment, false, true, false);
        }
        apply();
    }

    public void changeTotalNoteTask(LTask task, boolean newTask, boolean increment) {
        changeTotal(task, newTask, increment, false, true, false);
        apply();
    }

    /**
     * Если выбран newTask то значит, что создается или удаляется задача, иначе изменился только статус Если выбран
     * increment,то увеличить иначе уменьшить
     */
    private void changeTotal(LTask task, boolean newTask, boolean increment, boolean justUnreaded, boolean justNotes, boolean focus) {
        // задача-родитель
        if (task.getUIDParent() != null) {
            String parentId = getParentUidOfParent(String.valueOf(task.getIdTask()));
            mRawQueries.add(getRawQueryChangeTotal(task, TaskTotalLinkContract.TABLE_NAME, parentId,//
                    newTask, increment, justUnreaded, justNotes, focus));

            if (!task.getReaded()) {
                while (true) {
                    parentId = getParentUidOfParent(parentId);
                    if (parentId == null) {
                        break;
                    }
                    mRawQueries.add(getRawQueryChangeTotal(task, TaskTotalLinkContract.TABLE_NAME, parentId,//
                            newTask, increment, true, justNotes, focus));
                }
            }

            mRawQueries.add(updateLink(LionMetaData.TaskLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        final boolean customer = mCurrentUser.equals(task.getEmailCustomer());
        final boolean performer = mCurrentUser.equals(task.getEmailPerformer());

        // сегодня
        final long today = CalendarLink.getLongUidFromDate(TimeHelper.currentTimeMillisWithoutTimeZone());
        if (TaskHelper.belongsToCalendarDate(today, task)) {
            if (TaskHelper.isOverdueTask(task) && LTSettings.getInstance().isOverdueInToday()) {
                mRawQueries.add(getRawQueryChangeTotal(task, CalendarTotalLinkContract.TABLE_NAME, String.valueOf(today),//
                        newTask, increment, justUnreaded, justNotes, focus));
                mRawQueries.add(updateLink(LionMetaData.CalendarLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
            } else {
                if (!LTSettings.getInstance().isOverdueInToday() && TaskHelper.isOverdueTask(task)) {
                    mRawQueries.add(getRawQueryChangeTotal(task, LionMetaData.OverdueTotalLinkContract.TABLE_NAME, "0",//
                            newTask, increment, justUnreaded, justNotes, focus));
                    mRawQueries.add(updateLink(LionMetaData.OverdueTotalLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
                } else {
                    mRawQueries.add(getRawQueryChangeTotal(task, CalendarTotalLinkContract.TABLE_NAME, String.valueOf(today),//
                            newTask, increment, justUnreaded, justNotes, focus));
                    mRawQueries.add(updateLink(LionMetaData.CalendarLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
                }
            }
        }

        // входящим
        if (TaskHelper.isInboxTask(mContext, task)) {
            mRawQueries.add(getRawQueryChangeTotal(task, InboxTotalLinkContract.TABLE_NAME, mCurrentUser,//
                    newTask, increment, justUnreaded, justNotes, focus));
            mRawQueries.add(updateLink(LionMetaData.InboxLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        // готово к сдаче
        if (TaskHelper.isReadyTask(task)) {
            mRawQueries.add(getRawQueryChangeTotal(task, ReadyTotalLinkContract.TABLE_NAME, mCurrentUser,//
                    newTask, increment, justUnreaded, justNotes, focus));
            mRawQueries.add(updateLink(LionMetaData.ReadyLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        // в работе
        if (TaskHelper.isInworkTask(task)) {
            mRawQueries.add(getRawQueryChangeTotal(task, LionMetaData.InworkTotalLinkContract.TABLE_NAME, mCurrentUser,//
                    newTask, increment, justUnreaded, justNotes, focus));
            mRawQueries.add(updateLink(LionMetaData.InworkLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        // поручено мной
        if (customer && !performer) {
            mRawQueries.add(getRawQueryChangeTotal(task, ByMeTotalLinkContract.TABLE_NAME, task.getEmailPerformer(),//
                    newTask, increment, justUnreaded, justNotes, focus));
            mRawQueries.add(updateLink(LionMetaData.ByMeLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        // поручено мне
        if (!customer && performer) {
            mRawQueries.add(getRawQueryChangeTotal(task, ForMeTotalLinkContract.TABLE_NAME, task.getEmailCustomer(),//
                    newTask, increment, justUnreaded, justNotes, focus));
            mRawQueries.add(updateLink(ForMeLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        // проекты
        if (task.getUidProject() != null) {
            mRawQueries.add(getRawQueryChangeTotal(task, ProjectTotalLinkContract.TABLE_NAME, task.getUidProject(),//
                    newTask, increment, justUnreaded, justNotes, focus));
            mRawQueries.add(updateLink(LionMetaData.ProjectLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        // категории
        if (task.getCategories() != null) {
            for (String uid : TaskHelper.getCategoriesFromString(task.getCategories())) {
                mRawQueries.add(getRawQueryChangeTotal(task, CategoryTotalLinkContract.TABLE_NAME, uid,//
                        newTask, increment, justUnreaded, justNotes, focus));
            }
            mRawQueries.add(updateLink(LionMetaData.CategoryLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        // цвета
        if (task.getUidMarker() != null) {
            mRawQueries.add(getRawQueryChangeTotal(task, LionMetaData.ColorTotalLinkContract.TABLE_NAME, task.getUidMarker().toUpperCase(),//
                    newTask, increment, justUnreaded, justNotes,focus));
            mRawQueries.add(updateLink(LionMetaData.ColorTotalLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

        if (justUnreaded ) {
            //сотрудники
            mRawQueries.add(getRawQueryChangeTotal(task, LionMetaData.EmpTotalLinkContract.TABLE_NAME, task.getEmailPerformer(),//
                    newTask, increment, justUnreaded, justNotes,focus));
            mRawQueries.add(updateLink(LionMetaData.EmpTotalLinkContract.TABLE_NAME, task.getIdTask(), task.getReaded()));
        }

    }

    private String updateLink(String link, int taskId, boolean isReaded) {
        int reader = isReaded ? 1 : 0;
        return " UPDATE "+link+" SET readed = '"+reader+"' WHERE "+link+".taskid = '"+taskId+"' ";

    }

    private String getRawQueryChangeTotal(LTask task, String tableName, String uid,//
            boolean newTask, boolean increment, boolean justUnreaded, boolean justNotes, boolean focus) {
        getRawQueryChangeTotal(mSb, task, tableName, uid,//
                newTask, mCompletedCache.find(task.getIdTask()) != null, increment, justUnreaded, justNotes, focus);
        mNotifyUries.put(tableName, getUriByTableName(tableName));

        return mSb.toString();
    }

    public static void getRawQueryChangeTotal(StringBuilder sb, LTask task, String tableName, String uid,//
            boolean newTask, boolean completedTask, boolean increment, boolean justUnreaded, boolean justNotes, boolean focus) {
        final char sign = increment ? PLUS_C : MINUS_C;

        Utils.clearStringBuilder(sb);
        sb.append(UPDATE);
        sb.append(tableName);
        sb.append(SET);


        if (justNotes) {
            getFootstep(sb, TotalLinkContract.TasksNotes, sign);
        }
        else {
            if (focus) {
                getFootstep(sb, TotalLinkContract.TasksFocus, sign);
                if (!completedTask) {
                    sb.append(COMMA_C);
                    getFootstep(sb, TotalLinkContract.TasksUncompletedUnreaded, sign);
                }

            }else if (justUnreaded) {
                getFootstep(sb, TotalLinkContract.TasksUnreaded, sign);
                if (!completedTask) {
                    sb.append(COMMA_C);
                    getFootstep(sb, TotalLinkContract.TasksUncompletedUnreaded, sign);
                }

            } else {
                if (newTask) {
                    getFootstep(sb, TotalLinkContract.Tasks, sign);
                    if (!task.getReaded()) {
                        sb.append(COMMA_C);
                        getFootstep(sb, TotalLinkContract.TasksUnreaded, sign);
                    }
                }

                if (!newTask || !completedTask) {
                    if (newTask) {
                        sb.append(COMMA_C);
                    }

                    getFootstep(sb, TotalLinkContract.TasksUncompleted, sign);
                    if (!task.getReaded()) {
                        sb.append(COMMA_C);
                        getFootstep(sb, TotalLinkContract.TasksUncompletedUnreaded, sign);
                    }
                }
            }
        }
        sb.append(WHERE);
        SelectionKeeper.eq(sb, TotalLinkContract.Uid, uid);
    }

    private Uri getUriByTableName(String tableName) {
        switch (tableName) {
        case TaskTotalLinkContract.TABLE_NAME:
            return TaskTotalLinkContract.CONTENT_URI;

        case CalendarTotalLinkContract.TABLE_NAME:
            return CalendarTotalLinkContract.CONTENT_URI;

        case InboxTotalLinkContract.TABLE_NAME:
            return InboxTotalLinkContract.CONTENT_URI;

        case UnreadTotalLinkContract.TABLE_NAME:
           return UnreadTotalLinkContract.CONTENT_URI;

        case ByMeTotalLinkContract.TABLE_NAME:
            return ByMeTotalLinkContract.CONTENT_URI;

        case ForMeTotalLinkContract.TABLE_NAME:
            return ForMeTotalLinkContract.CONTENT_URI;

        case ProjectTotalLinkContract.TABLE_NAME:
            return ProjectTotalLinkContract.CONTENT_URI;

        case CategoryTotalLinkContract.TABLE_NAME:
            return CategoryTotalLinkContract.CONTENT_URI;

        case LionMetaData.ColorTotalLinkContract.TABLE_NAME:
            return LionMetaData.ColorTotalLinkContract.CONTENT_URI;

        case LionMetaData.EmpTotalLinkContract.TABLE_NAME:
            return LionMetaData.EmpTotalLinkContract.CONTENT_URI;

        case LionMetaData.OverdueTotalLinkContract.TABLE_NAME:
            return LionMetaData.OverdueTotalLinkContract.CONTENT_URI;

        case ReadyTotalLinkContract.TABLE_NAME:
            return ReadyTotalLinkContract.CONTENT_URI;

        case InworkTotalLinkContract.TABLE_NAME:
            return InworkTotalLinkContract.CONTENT_URI;

        default:
            return null;
        }
    }

    private String getParentUidOfParent(String childId) {
        String id = mParentUid.get(childId);
        if (id != null) {
            return id.length() == 0 ? null : id;
        }

        Cursor c = null;
        try {
            mSelectionBuilder.clear();
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI,//
                    null, mSelectionBuilder.getParentId(childId).build(), null, null);

            if (c.moveToFirst()) {
                id = c.getString(c.getColumnIndex(LTaskContract._ID));
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        mParentUid.put(childId, TextUtils.isEmpty(id) ? EMPTY : id);
        return id;
    }

    private static void getFootstep(StringBuilder sb, String columnName, char sign) {
        sb.append(columnName);
        sb.append(EQUALS_C);
        sb.append(columnName);
        sb.append(sign);
        sb.append(1);
    }

    private boolean apply() {
        final SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
        try {
            db.beginTransaction();

            Cursor c = null;
            for (String query : mRawQueries) {
                if (query != null) {
                    try {
                        c = db.rawQuery(query, null);
                        c.moveToFirst();

                    } catch (Exception e) {
                        Utils.toLog(e);

                    } finally {
                        if (c != null) {
                            c.close();
                        }
                        c = null;
                    }
                }
            }
            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Utils.toLog(e);
            return false;

        } finally {
            db.endTransaction();

            for (Uri uri : mNotifyUries.values()) {
                mContext.getContentResolver().notifyChange(uri, null);
            }

            mRawQueries.clear();
            mNotifyUries.clear();
        }
    }
}