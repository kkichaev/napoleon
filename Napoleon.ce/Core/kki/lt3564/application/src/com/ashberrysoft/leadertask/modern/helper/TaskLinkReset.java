package com.ashberrysoft.leadertask.modern.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.SparseArray;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.InboxLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.InboxTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.VerticalDepthTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
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
import com.ashberrysoft.leadertask.modern.helper.VerticalDepthHelper.SumAllAtTop;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.google.android.gms.wearable.Node;

import static com.ashberrysoft.leadertask.R.id.date;

public class TaskLinkReset {

    private static final String ORDER_LINK_UID = SelectionKeeper.sort(null, LinkContract.Uid);

    // BASE
    private final Context mContext;

    // VALUE's
    private final ContentResolver mCr;

    private final StringBuilder mSb;
    private final TaskSelectionBuilder mSelectionBuilder;
    private ArrayList <ColorTotalLink> mColorTotalLinks;

    /** Ко-во запускаемых потоков */
    private volatile int mRecountLinkCount = 12;

    public TaskLinkReset(Context context) {
        mContext = context.getApplicationContext();
        mCr = mContext.getContentResolver();
        mSb = new StringBuilder();
        mSelectionBuilder = new TaskSelectionBuilder(mSb);
        mColorTotalLinks = new ArrayList<>();
    }

    public void resetTodayTasks(final Context context) {
        new Task(context).execute();
    }

    class Task  extends AsyncTask<Node, Void, Void> {
        Context mContext;
        public Task(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Node... nodes) {
            mContext.getContentResolver().delete(CalendarLinkContract.CONTENT_URI, null, null);
            runCalendar(TimeHelper.currentTimeMillisWithoutTimeZone(), true);
            return null;
        }
    }

    public void runAll() {
        Utils.clearStringBuilder(mSb);
        try {
            new RecountLink<>(TaskLink.class, TaskTotalLink.class, LTaskContract.UIDParent, TaskLinkContract.CONTENT_URI, TaskTotalLinkContract.CONTENT_URI, mSelectionBuilder.getTasksWith(LTaskContract.UIDParent, null).build()).start();
        } finally {
            try {
                while (mRecountLinkCount > 11) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                mContext.getContentResolver().delete(CalendarLinkContract.CONTENT_URI, null, null);
                runCalendar(TimeHelper.currentTimeMillisWithoutTimeZone(), true);
        } finally {
            try {
                while (mRecountLinkCount > 10) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(InboxLink.class, InboxTotalLink.class, LTaskContract.EmailCustomer,//
                        InboxLinkContract.CONTENT_URI, InboxTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getInboxTasks(null).build()).start();
        } finally {
            try {
                while (mRecountLinkCount > 9) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(UnreadLink.class, UnreadTotalLink.class, LTaskContract.Readed,//
                        UnreadLinkContract.CONTENT_URI, UnreadTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getUnreadTasks(null).build()).start();

                Utils.clearStringBuilder(mSb);
                new RecountLink<>(FocusLink.class, FocusTotalLink.class, LTaskContract.Focus,//
                        LionMetaData.FocusLinkContract.CONTENT_URI,
                        LionMetaData.FocusTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getFocusTasks(null).build()).start();
            } finally {
            try {
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(ByMeLink.class, ByMeTotalLink.class, LTaskContract.EmailPerformer, ByMeLinkContract.CONTENT_URI, ByMeTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWithUser(true, null).build()).start();
            } finally {
            try {
                while (mRecountLinkCount > 8) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    Utils.toLog(e);
                }
            }
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(ForMeLink.class, ForMeTotalLink.class, LTaskContract.EmailCustomer, ForMeLinkContract.CONTENT_URI,
                        ForMeTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWithUser(false, null).build()).start();
            } finally {
            try {
                while (mRecountLinkCount > 7) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(ProjectLink.class, ProjectTotalLink.class, LTaskContract.UidProject, ProjectLinkContract.CONTENT_URI,
                        ProjectTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWith(LTaskContract.UidProject, null).build()).start();
            } finally {
            try {
                while (mRecountLinkCount > 6) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(CategoryLink.class, CategoryTotalLink.class, LTaskContract.Categories, CategoryLinkContract.CONTENT_URI,
                        CategoryTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWith(LTaskContract.Categories, null).build()).start();
            } finally {
                RecountReadyLink();
            }
        } } } } } } }

    }

    private void RecountReadyLink() {
        try {
            while (mRecountLinkCount > 5) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    Utils.toLog(e);
                }
            }
            Utils.clearStringBuilder(mSb);
            new RecountLink<>(ReadyLink.class, ReadyTotalLink.class, LTaskContract.EmailCustomer,//
                    LionMetaData.ReadyLinkContract.CONTENT_URI, LionMetaData.ReadyTotalLinkContract.CONTENT_URI,//
                    mSelectionBuilder.getReadyTasks(null).build()).start();
        } finally {
            try {
                while (mRecountLinkCount > 4) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(InworkLink.class, InworkTotalLink.class, LTaskContract.EmailPerformer,//
                        LionMetaData.InworkLinkContract.CONTENT_URI, LionMetaData.InworkTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getInworkTasks(null).build()).start();
            } finally {
                try {
                    while (mRecountLinkCount > 3) {
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            Utils.toLog(e);
                        }
                    }
                    Utils.clearStringBuilder(mSb);
                    new RecountLink<>(OverdueLink.class, OverdueTotalLink.class, LTaskContract.EmailCustomer,//
                            LionMetaData.OverdueLinkContract.CONTENT_URI, LionMetaData.OverdueTotalLinkContract.CONTENT_URI,//
                            mSelectionBuilder.getOverdueLinkTasks(null).build()).start();
                } finally {
                    try {
                        while (mRecountLinkCount > 2) {
                            try {
                                Thread.sleep(0);
                            } catch (InterruptedException e) {
                                Utils.toLog(e);
                            }
                        }
                        Utils.clearStringBuilder(mSb);
                        new RecountLink<>(ColorLink.class, ColorTotalLink.class, LTaskContract.UidMarker,//
                                LionMetaData.ColorLinkContract.CONTENT_URI, LionMetaData.ColorTotalLinkContract.CONTENT_URI,//
                                mSelectionBuilder.getTasksWithColor(null).build()).start();
                    } finally {
                        try {
                            while (mRecountLinkCount > 1) {
                                try {
                                    Thread.sleep(0);
                                } catch (InterruptedException e) {
                                    Utils.toLog(e);
                                }
                            }
                            Utils.clearStringBuilder(mSb);
                            new RecountLink<>(EmpLink.class, EmpTotalLink.class, LTaskContract.EmailPerformer,//
                                    LionMetaData.EmpLinkContract.CONTENT_URI, LionMetaData.EmpTotalLinkContract.CONTENT_URI,//
                                    mSelectionBuilder.getTasksWithPerformer(null).build()).start();
                        } finally {
                            //сюда новый
                        }
                    }
                }
            }
        }
    }

    private synchronized void threadEnded() {
        mRecountLinkCount--;
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
        private final Uri mUriL;
        private final Uri mUriT;
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
                link();
                if (mClassL == TaskLink.class) {
                    setTaskLinkUidToId();
                }

                totalLink();

                if (mClassL == TaskLink.class) {
                        new SumAllAtTop(mContext).start();
                } else if (mClassL != InboxLink.class && mClassL != UnreadLink.class) {
                    removeNesting();
                }

                if (mClassL == ProjectLink.class) {
                    //Utils.timeChecker("updateProjectTotalLink");
                    UpdateFeatureLinkHelper.updateProjectTotalLink(mContext);
                    //Utils.timeChecker("updateProjectTotalLink");

                } else if (mClassL == CategoryLink.class) {
                    //Utils.timeChecker("updateCategoryTotalLink");
                    UpdateFeatureLinkHelper.updateCategoryTotalLink(mContext);
                    //Utils.timeChecker("updateCategoryTotalLink");
                } else if (mClassL == ColorLink.class) {
                    UpdateFeatureLinkHelper.updateColorTotalLink(mContext, mColorTotalLinks);
                } else if (mClassL == EmpLink.class) {
                    //UpdateFeatureLinkHelper.updateEmpTotalLink(mContext, mEmpTotalLinks);

                    UpdateFeatureLinkHelper.updateEmpTotalLinkNew(mContext);
                }

            } catch (Exception e) {
                Utils.toLog(e);

            } finally {
                threadEnded();
            }
        }

        /** Создать все возможные базовые связи */
        private void link() throws Exception {
            Cursor c = null;
            try {
                Utils.clearStringBuilder(mSb);
                c = mCr.query(LTaskContract.CONTENT_URI, null, mSelection, null, null);

                if (c.getCount() > 0) {
                    final int columnUidColumn = c.getColumnIndex(mColumnForUid);

                    final int columnId = c.getColumnIndex(LTaskContract._ID);
                    final int columnReaded = c.getColumnIndex(LTaskContract.Readed);
                    final int columnStatus = c.getColumnIndex(LTaskContract.Status);
                    final int columnFocus = c.getColumnIndex(LTaskContract.Focus);

                    if (mClassL == CategoryLink.class) {
                        forCategories(c, columnUidColumn, columnId, columnReaded, columnStatus);
                        return;
                    }

                    /*if (mClassL == ColorLink.class) {
                        forColors(c, columnUidColumn, columnId, columnReaded, columnStatus);
                        return;
                    }*/

                    final L link = mClassL.newInstance();
                    final ContentValues[] cvs = new ContentValues[c.getCount()];
                    int count = 0;

                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        if (mCalendarLink) {
                            link.setUid(mDate);

                        } else {
                            if (mClassL == UnreadLink.class || mClassL == OverdueLink.class || mClassL == FocusLink.class) {
                                link.setUid("0");
                            } else {
                                link.setUid(c.getString(columnUidColumn));
                            }
                        }

                        link.setTaskId(c.getInt(columnId));
                        link.setReaded(c.getInt(columnReaded) == 1);
                        link.setStatus(c.getInt(columnStatus));
                        link.setFocus(c.getInt(columnFocus) == 1);
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
                    mCr.delete(mUriL, null, null);
                    /*if (LTSettings.getInstance().getTasksToDelete().size() > 0) {
                        for (String id : LTSettings.getInstance().getTasksToDelete()) {
                            Utils.clearStringBuilder(mSb);
                            mCr.delete(mUriL, SelectionKeeper.equals(mSb, LinkContract.TaskId, id), null);
                        }
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

        private void totalLink() throws Exception {
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

                        if (link.getFocus())
                            totalLink.setTasksFocus(totalLink.getTasksFocus() + 1);

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
                        if (mClassL == ColorLink.class) {
                            try {
                                if (mColorTotalLinks.size() > 0) {
                                    mColorTotalLinks.remove(totalLink);
                                }
                                mColorTotalLinks.add((ColorTotalLink) totalLink);
                            } catch (Exception e) {

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

        private void forCategories(Cursor c, int columnUidColumn, int columnId, int columnReaded, int columnStatus) throws Exception {
            final L link = mClassL.newInstance();
            final List<ContentValues> values = new ArrayList<>(c.getColumnCount());
            String[] uids;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                link.setTaskId(c.getInt(columnId));
                link.setReaded(c.getInt(columnReaded) == 1);
                link.setStatus(c.getInt(columnStatus));

                uids = TaskHelper.getCategoriesFromString(c.getString(columnUidColumn));
                for (String uid : uids) {
                    link.setUid(uid);
                    values.add(link.getContentValues(null));
                }
            }

            final ContentValues[] cvs = contentValuesFromList(values);

            mCr.delete(mUriL, null, null);
            mCr.bulkInsert(mUriL, cvs);
        }

        private void forColors(Cursor c, int columnUidColumn, int columnId, int columnReaded, int columnStatus) throws Exception {
            final L link = mClassL.newInstance();
            final List<ContentValues> values = new ArrayList<>(c.getColumnCount());
            //String[] uids;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                link.setTaskId(c.getInt(columnId));
                link.setReaded(c.getInt(columnReaded) == 1);
                link.setStatus(c.getInt(columnStatus));

                //uids = TaskHelper.getCategoriesFromString(c.getString(columnUidColumn));
                //for (String uid : uids) {
                    link.setUid(c.getString(columnUidColumn));
                    values.add(link.getContentValues(null));
                //}
            }

            final ContentValues[] cvs = contentValuesFromList(values);

            mCr.delete(mUriL, null, null);
            mCr.bulkInsert(mUriL, cvs);
        }

        private void setTaskLinkUidToId() throws Exception {
            Utils.clearStringBuilder(mSb);
            Cursor c = null;

            try {
                c = DbHelper.getInstance(mContext).getWritableDatabase().rawQuery(mSelectionBuilder.getRawUpdateTaskLinkUidToId( null).build(), null);
                c.moveToFirst();

            } finally {
                if (c != null) {
                    c.close();
                    c = null;
                }
            }
        }
    }

    public static ContentValues[] contentValuesFromList(Collection<ContentValues> values) {
        return values.toArray(new ContentValues[values.size()]);
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