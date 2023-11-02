package com.ashberrysoft.leadertask.modern.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.DeleteUidContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.cache.TaskMessageCache;
import com.ashberrysoft.leadertask.modern.changer.CalendarChanger;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.DeleteUid;
import com.ashberrysoft.leadertask.modern.domains.link.BaseLink;
import com.ashberrysoft.leadertask.modern.domains.link.BaseTotalLink;
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
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;

import static com.ashberrysoft.leadertask.enums.MenuItemType.INWORK;

public class TaskDeleteHelper extends Thread {

    // BASE
    private final Context mContext;
    private final LTask mTask;
    private final boolean mNeedToSync;

    // VALUE's
    private final String mCurrentUser;
    private final StringBuilder mSb;
    private final List<LTask> mTasks;
    private final ArrayList<ContentProviderOperation> mOperations;
    public static boolean deletingTask = false;

    public TaskDeleteHelper(Context context, LTask task, boolean needToSync) {
        super(TaskDeleteHelper.class.getSimpleName());

        mContext = context.getApplicationContext();
        mTask = task;
        mNeedToSync = needToSync;

        mCurrentUser = LTSettings.getInstance(mContext).getUserName();
        mSb = new StringBuilder();
        mTasks = new ArrayList<>();
        mOperations = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();

        try {
            addToList(mTask.getUid());
            removeMainTask();
            removeChildTasks();
            if (mNeedToSync) {
                Utils.startSync((LTApplication) mContext);
            }
        } catch (Exception e) {
            Utils.toLog(e);
        } finally {
            deletingTask = false;
        }
    }

    /** Рекурсивное добавление детей по родителям */
    private void addToList(String... uids) {
        Utils.clearStringBuilder(mSb);
        final String[] newUids;
        Cursor c = null;

        try {
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, SelectionKeeper.in(mSb, LTaskContract.UIDParent, uids).toString(), null,
                    null);

            if (c.getCount() == 0) {
                return;
            }

            newUids = new String[c.getCount()];
            int count = 0;
            LTask task;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                task = new LTask(c);

                newUids[count++] = task.getUid();
                mTasks.add(task);
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        addToList(newUids);
    }

    private void removeMainTask() {
        Utils.clearStringBuilder(mSb);
        mOperations.add(ContentProviderOperation.newDelete(TaskTotalLinkContract.CONTENT_URI)
                .withSelection(SelectionKeeper.equals(mSb, TaskTotalLinkContract.Uid, mTask.getIdTask()), null).build());

        if (mTask.getUIDParent() != null) {
            final TaskLink link = new TaskLink();
            final TaskTotalLink totalLink = new TaskTotalLink();
            final String uid;
            {
                final LTask t = TaskHelper.getTask(mContext, mTask.getUIDParent());
                uid = String.valueOf(t.getIdTask());
            }

            // TODO: Взять текущую задачу-тотал-линк и вычесть её содержимое у родительской задачи-тотал-линк or NOT
            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, mTask, uid);
        }

        removeTask(mTask);
        LTCalendarView.clearCalendarData(mContext, mTask);
    }

    private void removeChildTasks() {
        for (LTask task : mTasks) {
            mOperations.clear();
            removeTask(task);
        }
        LTCalendarView.clearCalendarData(mContext, mTasks.toArray(new LTask[mTasks.size()]));
    }

    private void removeTask(LTask task) {
        mOperations.add(ContentProviderOperation.newInsert(DeleteUidContract.CONTENT_URI).withValues(DeleteUid.getContentValues(task)).build());

        Utils.clearStringBuilder(mSb);
        mOperations.add(ContentProviderOperation.newDelete(LTaskContract.CONTENT_URI)
                .withSelection(SelectionKeeper.equals(mSb, LTaskContract._ID, task.getIdTask()), null).build());

        boolean inboxTask = true;
        boolean unreadTask = true;
        boolean inworkTask = false;
        boolean readyTask = false;
        boolean focusedTask = task.getFocus();

        TaskNotifyHelper.getInstance(mContext).deleteOldTaskNotify(task);

        if (task.getReaded() == true) {
            unreadTask = false;
        }

        if (task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) && task.getStatus() == TaskStatus.IN_WORK.getCode()) {
            inworkTask = true;
        }

        if (task.getEmailCustomer().equals(LTSettings.getInstance().getUserName()) && task.getStatus() == TaskStatus.READY.getCode()) {
            readyTask = true;
        }

        if (task.getUIDParent() != null) {
            inboxTask = false;
        }

        if (task.getTermBegin() != 0) {
            inboxTask = false;

            final CalendarLink link = new CalendarLink();
            final CalendarTotalLink totalLink = new CalendarTotalLink();

            final long today = CalendarLink.getLongUidFromDate(TimeHelper.currentTimeMillisWithoutTimeZone());
            final long date = CalendarLink.getLongUidFromDate(task.getTermBegin());

            if (date <= today) {
                final List<CalendarTotalLink> totalLinks = CalendarChanger.getTotalLinks(mContext, mSb, date, today);
                if (totalLinks != null) {
                    for (CalendarTotalLink tLink : totalLinks) {
                        addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, tLink.getUid());
                    }
                }

            } else {
                addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, String.valueOf(date));
            }
        }

        if (!mCurrentUser.equals(task.getEmailPerformer())) {
            inboxTask = false;

            final ByMeLink link = new ByMeLink();
            final ByMeTotalLink totalLink = new ByMeTotalLink();
            final String uid = task.getEmailPerformer();

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (task.getUidProject() != null) {
            inboxTask = false;

            final ProjectLink link = new ProjectLink();
            final ProjectTotalLink totalLink = new ProjectTotalLink();
            final String uid = task.getUidProject();

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (task.getCategories() != null) {

            final CategoryLink link = new CategoryLink();
            final CategoryTotalLink totalLink = new CategoryTotalLink();
            final String[] categories = TaskHelper.getCategoriesFromString(task.getCategories());

            for (String uid : categories) {
                addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
            }
        }

        if (inboxTask) {
            final InboxLink link = new InboxLink();
            final InboxTotalLink totalLink = new InboxTotalLink();
            final String uid = mCurrentUser;

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (unreadTask) {
            final UnreadLink link = new UnreadLink();
            final UnreadTotalLink totalLink = new UnreadTotalLink();
            final String uid = "0";

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (focusedTask) {
            final FocusLink link = new FocusLink();
            final FocusTotalLink totalLink = new FocusTotalLink();
            final String uid = "0";

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (inworkTask) {
            final InworkLink link = new InworkLink();
            final InworkTotalLink totalLink = new InworkTotalLink();
            final String uid = mCurrentUser;

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (task.getEmailPerformer() != null) {
            final EmpLink link = new EmpLink();
            final EmpTotalLink totalLink = new EmpTotalLink();

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, task.getEmailPerformer());
        }

        if (readyTask) {
            final ReadyLink link = new ReadyLink();
            final ReadyTotalLink totalLink = new ReadyTotalLink();
            final String uid = mCurrentUser;

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (TaskHelper.isColorTask(mContext, task)) {
            final ColorLink link = new ColorLink();
            final ColorTotalLink totalLink = new ColorTotalLink();
            final String uid = task.getUidMarker();

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (TaskHelper.isOverdueTask(task)) {
            final OverdueLink link = new OverdueLink();
            final OverdueTotalLink totalLink = new OverdueTotalLink();
            final String uid = "0";

            addLinkToOperations(mContext, mOperations, mSb, link, totalLink, task, uid);
        }

        if (mOperations.size() > 0) {
            applyBatch();
        }

        final int uidHash = TaskHelper.getHashFromUid(mTask.getUid().toLowerCase());
        {
            final TaskMessageCache messageCache = TaskMessageCache.getInstance(mContext);
            final List<TaskMessage> messages = messageCache.find(uidHash);

            if (messages != null) {
                try {
                    DbHelper.getInstance(mContext).getTaskMessageDao().delete(messages);

                } catch (SQLException e) {
                    Utils.toLog(e);
                }

                messageCache.remove(uidHash);
            }
        }
        {
            final TaskFileCache fileCache = TaskFileCache.getInstance(mContext);
            final List<TaskFile> files = fileCache.find(uidHash);

            if (files != null) {
                mOperations.clear();

                deleteTaskFiles(files, mOperations, mSb);

                applyBatch();

                fileCache.remove(uidHash);
            }
        }
    }

    public static void deleteTaskFiles(List<TaskFile> files, ArrayList<ContentProviderOperation> operations, StringBuilder sb) {
        final ContentValues cv = new ContentValues(2);
        cv.put(TaskFileContract.DELETE_OBJECT, true);
        cv.put(TaskFileContract.WEAK_LINK, true);

        for (TaskFile file : files) {
            Utils.clearStringBuilder(sb);
            operations.add(ContentProviderOperation.newUpdate(TaskFileContract.CONTENT_URI).withValues(cv)
                    .withSelection(SelectionKeeper.equals(sb, TaskFileContract.FIELD_FILEUID, String.valueOf(file.getFileId())), null).build());
        }
    }

    private void applyBatch() {
        try {
            mContext.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, mOperations);

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private static <L extends BaseLink, T extends BaseTotalLink> void addLinkToOperations(Context context, ArrayList<ContentProviderOperation> operations,
            StringBuilder sb,//
            L link, T totalLink, LTask task, String uid) {
        /** Удаляем прямую ссылку */
        Utils.clearStringBuilder(sb);
        operations.add(ContentProviderOperation.newDelete(link.getContentUri())
                .withSelection(SelectionKeeper.equals(sb, LinkContract.TaskId, task.getIdTask()), null).build());

        /** Уменьшаем счетчики */
        Utils.clearStringBuilder(sb);
        final String selection = SelectionKeeper.equals(sb, TotalLinkContract.Uid, uid);

        if (TaskHelper.fillSingleItem(context, totalLink, selection)) {
            if (task.getStatus()!= Status.NOTE.getStatusCode()) {
                totalLink.decrementTasks();
                if (!task.getReaded()) {
                    totalLink.decrementTasksUnreaded();
                }

                if (task.getFocus())
                    totalLink.decrementTasksFocused();

                final CompletedTask t = CompletedCache.getInstance(context).find(task.getIdTask());
                if (t == null || !t.isParentCompleted() && !t.isTaskCompleted()) {
                    totalLink.decrementTasksUncompleted();
                    if (!task.getReaded()) {
                        totalLink.decrementTasksUncompletedUnreaded();
                    }
                }

                operations.add(ContentProviderOperation.newUpdate(totalLink.getContentUri()).withSelection(selection, null)
                        .withValues(totalLink.getContentValues(null)).build());
            }
            else {
                totalLink.decrementTasksNotes();
                operations.add(ContentProviderOperation.newUpdate(totalLink.getContentUri()).withSelection(selection, null)
                        .withValues(totalLink.getContentValues(null)).build());
            }
        }

    }
}