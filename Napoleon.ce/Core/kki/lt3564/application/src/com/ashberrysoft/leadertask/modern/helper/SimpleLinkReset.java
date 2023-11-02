package com.ashberrysoft.leadertask.modern.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.VerticalDepthTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.BaseLink;
import com.ashberrysoft.leadertask.modern.domains.link.BaseTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.InboxLink;
import com.ashberrysoft.leadertask.modern.domains.link.TaskLink;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleLinkReset  implements Runnable  {

    private static final String ORDER_LINK_UID = SelectionKeeper.sort(null, LinkContract.Uid);

    // BASE
    private final Context mContext;
    private final StringBuilder mSb;
    private final TaskSelectionBuilder mSelectionBuilder;
    // VALUE's
    private final ContentResolver mCr;
    private boolean mNeedToRecountCalendar;

    public SimpleLinkReset(Context context, boolean afterException, boolean needToRecountCalendar) {
        mContext = context.getApplicationContext();
        mSb = new StringBuilder();
        mCr = mContext.getContentResolver();
        mNeedToRecountCalendar = needToRecountCalendar;
        mSelectionBuilder = new TaskSelectionBuilder(mSb);
        if (afterException) {
            new Thread(this).start();
        } else {
            run();
        }
    }

    @Override
    public void run() {
        if (mNeedToRecountCalendar) {
            mContext.getContentResolver().delete(CalendarLinkContract.CONTENT_URI, null, null);
            runCalendar(TimeHelper.currentTimeMillisWithoutTimeZone(), true);
        }

        //Utils.timeChecker("updateProjectTotalLink");
        if (SlidingActivity.mDelProject != null) {
            try {
                DbHelper.getInstance(mContext).getProjectDao().delete(SlidingActivity.mDelProject);
                final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(mContext);
                linkHelper.deleteTotalLink(mContext, SlidingActivity.mDelProject);
                SlidingActivity.mDelProject = null;
            } catch (SQLException e) {
                Utils.toLog(e);
            }
        }
        UpdateFeatureLinkHelper.updateProjectTotalLink(mContext);
        //Utils.timeChecker("updateProjectTotalLink");

        //Utils.timeChecker("updateCategoryTotalLink");
        UpdateFeatureLinkHelper.updateCategoryTotalLink(mContext);
        //Utils.timeChecker("updateCategoryTotalLink");

        UpdateFeatureLinkHelper.updateEmpTotalLinkNew(mContext);

    }

    public void runCalendar(long date, boolean start) {
        date = CalendarLink.getLongUidFromDate(date);

        Utils.clearStringBuilder(mSb);
        final Thread thread = new RecountLink<>(CalendarLink.class, CalendarTotalLink.class, LTaskContract.TermBegin,//
                CalendarLinkContract.CONTENT_URI, CalendarTotalLinkContract.CONTENT_URI,//
                mSelectionBuilder.getCalendarByDay(date, null).build(), date);

        if (start) {
            thread.start();

        } else {
            thread.run();
        }
    }

    private final class RecountLink<L extends BaseLink, T extends BaseTotalLink> extends Thread {

        // BASE
        private final Class<L> mClassL;
        private final Class<T> mClassT;
        private final String mColumnForUid;
        private final Uri mUriT;
        private final Uri mUriL;
        private final String mSelection;

        // VALUE
        private final String mDate;
        private final boolean mCalendarLink;

        private final StringBuilder mSb;
        private final TaskSelectionBuilder mSelectionBuilder;

        public RecountLink(Class<L> classL, Class<T> classT,//
                           String columnForUid, Uri uriL, Uri uriT, String selection, long... date) {
            super(classL.getSimpleName());

            mClassL = classL;
            mClassT = classT;
            mColumnForUid = columnForUid;
            mUriL = uriL;
            mUriT = uriT;
            mSelection = selection;

            mDate = date != null && date.length == 1 ? String.valueOf(date[0]) : null;
            mCalendarLink = mDate != null;

            mSb = new StringBuilder();
            mSelectionBuilder = new TaskSelectionBuilder(mSb);
        }

        @Override
        public void run() {
            super.run();

            try {
                if (mClassL == CalendarLink.class) {
                    linkOld();
                    totalLinkOld();
                }

                removeNesting();
            } catch (Exception e) {
                Utils.toLog(e);

            } finally {
            }
        }

        /** Создать все возможные базовые связи */
        private void linkOld() throws Exception {
            Cursor c = null;
            try {
                Utils.clearStringBuilder(mSb);
                c = mCr.query(LTaskContract.CONTENT_URI, null, mSelection, null, null);

                if (c.getCount() > 0) {
                    final int columnUidColumn = c.getColumnIndex(mColumnForUid);

                    final int columnId = c.getColumnIndex(LTaskContract._ID);
                    final int columnReaded = c.getColumnIndex(LTaskContract.Readed);
                    final int columnStatus = c.getColumnIndex(LTaskContract.Status);

                    final L link = mClassL.newInstance();
                    final ContentValues[] cvs = new ContentValues[c.getCount()];
                    int count = 0;

                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        if (mCalendarLink) {
                            link.setUid(mDate);

                        } else {
                            link.setUid(c.getString(columnUidColumn));
                        }

                        link.setTaskId(c.getInt(columnId));
                        link.setReaded(c.getInt(columnReaded) == 1);
                        link.setStatus(c.getInt(columnStatus));
                        cvs[count++] = link.getContentValues(null);
                    }

                    final String selection;
                    if (mCalendarLink) {
                        Utils.clearStringBuilder(mSb);
                        selection = SelectionKeeper.equals(mSb, LinkContract.Uid, mDate);

                    } else {
                        selection = null;
                    }

                    mCr.delete(mUriL, selection, null);
                    mCr.bulkInsert(mUriL, cvs);
                } else {
                    if (LTSettings.getInstance().getTasksToDelete().size() > 0) {
                        for (String id : LTSettings.getInstance().getTasksToDelete()) {
                            Utils.clearStringBuilder(mSb);
                            mCr.delete(mUriL, SelectionKeeper.equals(mSb, LinkContract.TaskId, id), null);
                        }
                    }
                }
            } catch (Exception e) {

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

        private void totalLinkOld() throws Exception {
            String selection;

            if (mCalendarLink) {
                Utils.clearStringBuilder(mSb);
                selection = SelectionKeeper.equals(mSb, LinkContract.Uid, mDate);
            } else {
                selection = null;
            }

            Cursor c = null;
            try {
                c = mCr.query(mUriL, null, selection, null, ORDER_LINK_UID);

                if (c.getCount() > 0) {
                    final CompletedCache completed = CompletedCache.getInstance(mContext);
                    final L link = mClassL.newInstance();

                    final Map<String, T> links = new HashMap<>();
                    T totalLink;
                    int isReaded;

                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        link.fillFromCursor(c);

                        totalLink = links.get(link.getUid());
                        if (totalLink == null) {
                            totalLink = mClassT.newInstance();
                            totalLink.setUid(link.getUid());

                            links.put(link.getUid(), totalLink);
                        }

                        isReaded = link.isReaded() ? 0 : 1;

                        totalLink.setTasks(totalLink.getTasks() + 1);
                        totalLink.setTasksUnreaded(totalLink.getTasksUnreaded() + isReaded);
                        if(mClassL == TaskLink.class) {
                            if (completed.find(link.getTaskId()) == null) {
                                totalLink.setTasksUncompleted(totalLink.getTasksUncompleted() + 1);
                                totalLink.setTasksUncompletedUnreaded(totalLink.getTasksUncompletedUnreaded() + isReaded);
                            }
                        }
                        else
                        {
                            if (completed.find(link.getTaskId()) == null ) {
                                if (link.getStatus() != Status.NOTE.getStatusCode()) {
                                    totalLink.setTasksUncompleted(totalLink.getTasksUncompleted() + 1);
                                    totalLink.setTasksUncompletedUnreaded(totalLink.getTasksUncompletedUnreaded() + isReaded);
                                }
                                else {
                                    totalLink.setTasksNotes(totalLink.getTasksNotes() + 1);
                                }
                            }

                        }
                    }

                    final ContentValues[] cvs = contentValuesFromMap(links);

                    mCr.delete(mUriT, selection, null);
                    mCr.bulkInsert(mUriT, cvs);
                    if (mCalendarLink) {
                        MenuLoader.getInstance(mContext).resetTodayItem();
                    }
                }
                else {
                    mCr.delete(mUriT, selection, null);
                    if (mCalendarLink) {
                        MenuLoader.getInstance(mContext).resetTodayItem();
                    }
                    if (mClassL == InboxLink.class) {
                        MenuLoader.getInstance(mContext).resetInboxItem();
                    }
                }

            } catch (Exception e) {

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

        private void removeNesting() throws Exception {
            Cursor c = null;
            try {
                c = mCr.query(mUriT, null, null, null, null);

                if (c.getCount() > 0) {
                    final int columnUid = c.getColumnIndex(TotalLinkContract.Uid);
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        removeNesting(c.getString(columnUid));
                    }
                }

            } finally {
                if (c != null) {
                    c.close();
                    c = null;
                }
            }
        }

        private void removeNesting(String uid) throws Exception {
            Cursor c = null;
            try {
                Utils.clearStringBuilder(mSb);
                c = mCr.query(mUriL, null, SelectionKeeper.equals(mSb, LinkContract.Uid, uid), null, null);
                Utils.clearStringBuilder(mSb);

                if (c.getCount() > 0) {
                    mSb.append(VerticalDepthTaskContract._ID);
                    mSb.append(SharedStrings.IN);
                    mSb.append(SharedStrings.BRACE_OPEN_C);

                    final int columnId = c.getColumnIndex(LinkContract.TaskId);
                    boolean start = true;

                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        if (start) {
                            start = false;

                        } else {
                            mSb.append(SharedStrings.COMMA_C);
                        }
                        mSb.append(c.getString(columnId));
                    }
                    mSb.append(SharedStrings.BRACE_CLOSE_C);
                }

            } finally {
                if (c != null) {
                    c.close();
                    c = null;
                }
            }

            if (mSb.length() > 0) {
                try {
                    c = mCr.query(VerticalDepthTaskContract.CONTENT_URI, null, mSb.toString(), null, null);
                    Utils.clearStringBuilder(mSb);

                    if (c.getCount() > 0) {
                        final SparseArray<VerticalDepthHolder> tasks = new SparseArray<>();
                        VerticalDepthTask taskNew;
                        VerticalDepthHolder taskOld;

                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                            taskNew = new VerticalDepthTask(c);
                            taskOld = tasks.get(taskNew.getVertical());

                            if (taskOld == null || taskOld.getDepth() > taskNew.getDepth()) {
                                tasks.put(taskNew.getVertical(), new VerticalDepthHolder(taskNew));

                            } else if (taskOld.getDepth() == taskNew.getDepth()) {
                                taskOld.add(taskNew);
                            }// TODO: проверять родителей
                        }

                        if (tasks.size() > 0) {
                            final List<VerticalDepthTask> depthTasks = new ArrayList<>();
                            for (int i = 0; i < tasks.size(); i++) {
                                tasks.valueAt(i).addTo(depthTasks);
                            }

                            if (depthTasks.size() > 0) {
                                mSb.append(LinkContract.Uid);
                                mSb.append(SharedStrings.EQUALS);
                                mSb.append(SharedStrings.QUOTE_C);
                                mSb.append(uid);
                                mSb.append(SharedStrings.QUOTE_C);
                                mSb.append(SharedStrings.AND);
                                mSb.append(LinkContract.TaskId);
                                mSb.append(SharedStrings.NOT_IN);
                                mSb.append(SharedStrings.BRACE_OPEN_C);

                                boolean start = true;
                                for (VerticalDepthTask task : depthTasks) {
                                    if (start) {
                                        start = false;

                                    } else {
                                        mSb.append(SharedStrings.COMMA_C);
                                    }
                                    mSb.append(task.getId());
                                }
                                mSb.append(SharedStrings.BRACE_CLOSE_C);

                                mCr.delete(mUriL, mSb.toString(), null);
                            }
                        }
                    }

                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    public static <K, V extends CursorFiller> ContentValues[] contentValuesFromMap(Map<K, V> map) {
        final Collection<V> values = map.values();
        final ContentValues[] cvs = new ContentValues[values.size()];
        int count = 0;

        for (V value : values) {
            cvs[count++] = value.getContentValues(null);
        }

        return cvs;
    }


    private static final class VerticalDepthHolder {

        private final int mDepth;
        private final SparseArray<VerticalDepthTask> mTasks;

        public VerticalDepthHolder(VerticalDepthTask task) {
            mDepth = task.getDepth();

            mTasks = new SparseArray<>(4);
            add(task);
        }

        public int getDepth() {
            return mDepth;
        }

        public void add(VerticalDepthTask task) {
            mTasks.put(task.getId(), task);
        }

        public void addTo(List<VerticalDepthTask> tasks) {
            for (int i = 0; i < mTasks.size(); i++) {
                tasks.add(mTasks.valueAt(i));
            }
        }
    }

}