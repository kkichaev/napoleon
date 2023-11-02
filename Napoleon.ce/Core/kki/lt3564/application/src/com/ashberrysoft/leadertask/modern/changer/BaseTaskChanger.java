package com.ashberrysoft.leadertask.modern.changer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.VerticalDepthTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.BaseTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskFootstepHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public abstract class BaseTaskChanger extends Thread {

    // BASE
    private final Context mContext;
    private final LTask mTaskNew;
    private final LTask mTaskOld;
    private final VerticalDepthTask mVerticalDepth;

    // VALUE's
    private final LTSettings mSettings;
    private final CompletedCache mCompletedCache;
    private final StringBuilder mStringBuilder;

    private final ArrayList<ContentProviderOperation> mOperations;
    private final List<String> mRawUpdate;

    protected BaseTaskChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super();

        mContext = context.getApplicationContext();
        mTaskNew = taskNew;
        mTaskOld = taskOld;
        mVerticalDepth = verticalDepth;

        mSettings = LTSettings.getInstance();
        mCompletedCache = CompletedCache.getInstance(getContext());

        mStringBuilder = new StringBuilder();
        mOperations = new ArrayList<>();
        mRawUpdate = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();

        try {
            if (!equalsTasks(getNew(), getOld())) {
                makeChanges();
                applyBatch();
                if(getNew().getStatus() != Status.NOTE.getStatusCode() || getOld().getStatus() != Status.NOTE.getStatusCode()) {
                    applyRawUpdate();
                }
                notifyChanges();
            }

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private void makeChanges() throws Exception {
        if (getOld() != null && possiblyHasLink(getOld())) {
            removeLinks(getOld());
        }

        if (possiblyHasLink(getNew())) {
            increaseLinksCounter();
        }
        createRelatedLinks();
    }

    private void applyBatch() throws Exception {
        if (getOperations().size() > 0) {
            getContext().getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, getOperations());
        }
    }

    private void applyRawUpdate() throws Exception {
        if (getRawUpdate().size() > 0) {
            final SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
            try {
                db.beginTransaction();

                Cursor c = null;
                for (String update : getRawUpdate()) {
                    try {
                        c = db.rawQuery(update, null);
                        c.moveToFirst();

                    } catch (Exception e) {
                        Utils.toLog(e);

                    } finally {
                        if (c != null) {
                            c.close();
                            c = null;
                        }
                    }
                }
                db.setTransactionSuccessful();

            } finally {
                db.endTransaction();
            }
        }
    }

    /** Проверить */
    public abstract boolean equalsTasks(LTask taskNew, LTask taskOld);

    /** Узнать может ли иметь задача соответственные связи */
    public abstract boolean possiblyHasLink(LTask task);

    /** Удалить соответственные связи и уменьшить счетчики в соответствии с задачей */
    public abstract boolean removeLinks(LTask task);

    /** Увеличить счетчики связи в соответствии с новой задачей */
    public abstract boolean increaseLinksCounter();

    /** Создать новые связи в соответствии с вертикалью задачи */
    public abstract boolean createRelatedLinks();

    /** Уведомить меню про изменение значений */
    public abstract void notifyChanges();

    protected boolean isCompleted(LTask task) {
        return mCompletedCache.find(task.getIdTask()) != null || //
                (task.getUIDParent() != null && mCompletedCache.find(task.getUIDParent()) != null) || //
                TaskHelper.isCompleted(task.getStatus(), getSettings().getUserName(), task.getEmailCustomer());
    }

    protected List<LTask> getAllTaskFamilyChilds(LTask task) {
        final List<LTask> list = new ArrayList<>(1);
        list.add(task);

        LTask[] uids = new LTask[] { task };
        while (uids.length > 0) {
            uids = getChildsByUids(uids);
            list.addAll(Arrays.asList(uids));
        }
        return list;
    }

    private LTask[] getChildsByUids(LTask... uids) {
        clearSb();
        SelectionKeeper.in(getSb(), LTaskContract.UIDParent, uids);

        Cursor c = null;
        try {
            c = getContext().getContentResolver().query(LTaskContract.CONTENT_URI, null, toString(), null, null);

            if (c.getCount() > 0) {
                uids = new LTask[c.getCount()];
                int count = 0;

                final int columnId = c.getColumnIndex(LTaskContract._ID);
                final int columnUid = c.getColumnIndex(LTaskContract.Uid);
                final int columnStatus = c.getColumnIndex(LTaskContract.Status);
                final int columnTermEnd = c.getColumnIndex(LTaskContract.TermEnd);
                final int columnTermBegin = c.getColumnIndex(LTaskContract.TermBegin);
                final int columnUidParent = c.getColumnIndex(LTaskContract.UIDParent);
                final int columnCategories = c.getColumnIndex(LTaskContract.Categories);
                final int columnUidProject = c.getColumnIndex(LTaskContract.UidProject);
                final int columnCustomer = c.getColumnIndex(LTaskContract.EmailCustomer);
                final int columnPerformer = c.getColumnIndex(LTaskContract.EmailPerformer);
                final int columnReaded = c.getColumnIndex(LTaskContract.Readed);
                final int columnPerformerReaded = c.getColumnIndex(LTaskContract.PerformerReaded);
                final int columnFocus = c.getColumnIndex(LTaskContract.Focus);

                LTask task;
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    task = new LTask();

                    task.setId(c.getInt(columnId));
                    task.setUid(c.getString(columnUid));
                    task.setStatus(c.getInt(columnStatus));
                    task.setTermEnd(c.getLong(columnTermEnd));
                    task.setTermBegin(c.getLong(columnTermBegin));
                    task.setUIDParent(c.getString(columnUidParent));
                    task.setCategories(c.getString(columnCategories));
                    task.setUidProject(c.getString(columnUidProject));
                    task.setEmailCustomer(c.getString(columnCustomer));
                    task.setEmailPerformer(c.getString(columnPerformer));
                    task.setReaded(c.getInt(columnReaded) != 0);
                    task.setPerformerReaded(c.getInt(columnPerformerReaded) != 0);
                    task.setFocus(c.getInt(columnFocus) != 0);
                    uids[count++] = task;
                }
                return uids;
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return new LTask[0];
    }

    protected String selectUidAndTaskId(String uid, int taskId) {
        clearSb();

        mStringBuilder.append(LinkContract.Uid);
        mStringBuilder.append(SharedStrings.EQUALS_C);
        mStringBuilder.append(SharedStrings.QUOTE_C);
        mStringBuilder.append(uid);
        mStringBuilder.append(SharedStrings.QUOTE_C);

        mStringBuilder.append(SharedStrings.AND);

        mStringBuilder.append(LinkContract.TaskId);
        mStringBuilder.append(SharedStrings.EQUALS_C);
        mStringBuilder.append(taskId);

        return toString();
    }

    protected String updateTotalLinkCounter(String tableName, String uid, LTask task, boolean increment) {
        /** создаем сырой запрос к бд для изменения соотв ссылки */
        TaskFootstepHelper.getRawQueryChangeTotal(getSb(), task,//
                tableName, uid, isNew(), isCompleted(task), increment, false, false, task.getFocus());

        return toString();
    }

    protected ContentProviderOperation.Builder createNewTotalLink(BaseTotalLink totalLink, LTask task) {
        totalLink.setTasks(1);
        if (!task.getReaded()) {
            totalLink.setTasksUnreaded(1);
        }
        if (task.getStatus() == Status.NOTE.getStatusCode()) {
            totalLink.setTasksNotes(1);
        }
        if (!isCompleted(task)) {
            totalLink.setTasksUncompleted(1);
            if (!task.getReaded()) {
                totalLink.setTasksUncompletedUnreaded(1);
            }
        }
        if(task.getFocus())
            totalLink.setTasksFocus(1);

        return Utils.getIncertOperation(totalLink);
    }

    private List<LTask> getAllTasksFromVerticalAndSelection(String selection) {
        clearSb();
        final TaskSelectionBuilder sb = new TaskSelectionBuilder(getSb());
        sb.getTasksFromVertical(getVerticalDepth().getVertical(), selection);

        List<LTask> tasks = null;
        Cursor c = null;

        try {
            c = getContext().getContentResolver().//
                    query(LTaskContract.CONTENT_URI, null, sb.build(), null, LTaskContract._ID);
            if (c.getCount() > 0) {
                tasks = new ArrayList<>(c.getCount());
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    tasks.add(new LTask(c));
                }
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return Utils.returnNotNull(tasks);
    }

    private List<VerticalDepthTask> getAllVerticalsFromTasks(List<LTask> tasks) {
        List<VerticalDepthTask> verticals = null;
        Cursor c = null;

        clearSb();
        SelectionKeeper.inTaskIds(getSb(), VerticalDepthTaskContract._ID, tasks);

        try {
            c = getContext().getContentResolver().//
                    query(VerticalDepthTaskContract.CONTENT_URI, null, toString(), null, VerticalDepthTaskContract._ID);
            if (c.getCount() > 0) {
                verticals = new ArrayList<>(c.getCount());
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    verticals.add(new VerticalDepthTask(c));
                }
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return Utils.returnNotNull(verticals);
    }

    protected List<VerticalDepthTask> getTasksForLink(String tasksSelection) {
        /** находим все задачи в вертикали которые соответствуют условию */
        final List<LTask> tasks = getAllTasksFromVerticalAndSelection(tasksSelection);
        /** находим все вертикали ранее найденых задач */
        final List<VerticalDepthTask> verticals = getAllVerticalsFromTasks(tasks);

        /** устанавливаем соответствия между вертикалями и задачами */
        for (VerticalDepthTask vertical : verticals) {
            if (tasks.size() == 0) {
                break;
            }

            LTask task;
            for (Iterator<LTask> iterator = tasks.iterator(); iterator.hasNext();) {
                task = iterator.next();
                if (vertical.getId() == task.getIdTask()) {
                    vertical.setTask(task);
                    iterator.remove();
                    break;
                }
            }
        }

        /** сортируем по глубине - вверху с наименьшей глубиной */
        Collections.sort(verticals);

        /** ищем минимальную глубину */
        int min = Integer.MAX_VALUE;
        for (VerticalDepthTask vertical : verticals) {
            if (vertical.getDepth() < min) {
                min = vertical.getDepth();
            }
        }

        /** удаляем все несоответствующие минимальной глубине вертикали */
        for (Iterator<VerticalDepthTask> iterator = verticals.iterator(); iterator.hasNext();) {
            if (iterator.next().getDepth() != min) {
                iterator.remove();
            }
        }
        return verticals;
    }

    protected boolean totalLinkExists(Uri uri, String uid) {
        clearSb();
        final String selection = SelectionKeeper.equals(getSb(), TotalLinkContract.Uid, uid);

        clearSb();
        final String order = SelectionKeeper.orderLimitOne(getSb(), TotalLinkContract.Uid, true).toString();

        boolean answer = false;
        Cursor c = null;

        try {
            c = getContext().getContentResolver().query(uri, null, selection, null, order);
            answer = c.moveToFirst();

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return answer;
    }

    protected Context getContext() {
        return mContext;
    }

    protected LTSettings getSettings() {
        return mSettings;
    }

    protected void clearSb() {
        Utils.clearStringBuilder(mStringBuilder);
    }

    protected LTask getNew() {
        return mTaskNew;
    }

    protected LTask getOld() {
        return mTaskOld;
    }

    protected VerticalDepthTask getVerticalDepth() {
        return mVerticalDepth;
    }

    protected ArrayList<ContentProviderOperation> getOperations() {
        return mOperations;
    }

    protected void addOperation(ContentProviderOperation.Builder operation) {
        getOperations().add(operation.build());
    }

    private List<String> getRawUpdate() {
        return mRawUpdate;
    }

    protected void addRawUpdate(String uid) {
        mRawUpdate.add(uid);
    }

    protected boolean isNew() {
        return getOld() == null;
    }

    protected StringBuilder getSb() {
        return mStringBuilder;
    }

    @Override
    public String toString() {
        return getSb().toString();
    }
}