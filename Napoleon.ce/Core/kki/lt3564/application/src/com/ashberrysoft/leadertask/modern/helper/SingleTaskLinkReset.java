package com.ashberrysoft.leadertask.modern.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
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
import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadTotalLinkContract;
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
import com.ashberrysoft.leadertask.modern.helper.VerticalDepthHelper.SumAllAtTop;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleTaskLinkReset {

    private static final String ORDER_LINK_UID = SelectionKeeper.sort(null, LinkContract.Uid);

    // BASE
    private final Context mContext;

    // VALUE's
    private final ContentResolver mCr;

    private final StringBuilder mSb;
    private final TaskSelectionBuilder mSelectionBuilder;
    private final String mTaskUUID;
    private LTask mTaskSingle;
    private boolean isSubtask;
    private boolean isNewSubTask;
    private boolean isOldSubTask;
    private BaseLink oldSubTaskLink = null;
    private BaseLink oldSubTaskLinkDel = null;
    private String  parentSubString = null;

    /** Ко-во запускаемых потоков */
    private volatile int mRecountLinkCount = 12;

    public SingleTaskLinkReset(Context context, String taskUid) {
        mContext = context.getApplicationContext();
        mTaskSingle = null;
        mTaskUUID = taskUid;
        isSubtask = false;
        mCr = mContext.getContentResolver();
        mSb = new StringBuilder();
        mSelectionBuilder = new TaskSelectionBuilder(mSb);
    }

    public void runAll() {
        ///
        try {
            Cursor c = null;
            try {
                Utils.clearStringBuilder(mSb);
                c = mCr.query(LTaskContract.CONTENT_URI, null, SelectionKeeper.equals(new StringBuilder(), LTaskContract.Uid, mTaskUUID), null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    LTask task = new LTask();
                    task.fillFromCursor(c);
                    mTaskSingle = task;
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        } finally {}
        ///
        Utils.clearStringBuilder(mSb);
        try {
            //Utils.timeChecker("TaskLink");
            new RecountLink<>(TaskLink.class, TaskTotalLink.class, LTaskContract.UIDParent, TaskLinkContract.CONTENT_URI, TaskTotalLinkContract.CONTENT_URI, mSelectionBuilder.getTasksWith(LTaskContract.UIDParent, mTaskUUID).build()).start();
        } finally {
            try {
                while (mRecountLinkCount > 11) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                runCalendar(TimeHelper.currentTimeMillisWithoutTimeZone(), true);
                //Utils.timeChecker("CalendarLink");
                //Utils.timeChecker("InboxLink");
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(InboxLink.class, InboxTotalLink.class, LTaskContract.EmailCustomer,//
                        InboxLinkContract.CONTENT_URI, InboxTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getInboxTasks(mTaskUUID).build()).start();
        } finally {
            try {
                while (mRecountLinkCount > 10) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                //Utils.timeChecker("InboxLink");
                //Utils.timeChecker("UnreadLink");
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(UnreadLink.class, UnreadTotalLink.class, LTaskContract.Readed,//
                        UnreadLinkContract.CONTENT_URI, UnreadTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getUnreadTasks(mTaskUUID).build()).start();
            } finally {
            try {
                //Utils.timeChecker("UnreadLink");
                //Utils.timeChecker("ByMeLink");
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(ByMeLink.class, ByMeTotalLink.class, LTaskContract.EmailPerformer, ByMeLinkContract.CONTENT_URI, ByMeTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWithUser(true, mTaskUUID).build()).start();
            } finally {
            try {while (mRecountLinkCount > 9) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    Utils.toLog(e);
                }
            }
                //Utils.timeChecker("ByMeLink");
                //Utils.timeChecker("ForMeLink");
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(ForMeLink.class, ForMeTotalLink.class, LTaskContract.EmailCustomer, ForMeLinkContract.CONTENT_URI,
                        ForMeTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWithUser(false, mTaskUUID).build()).start();
            } finally {
            try {
                while (mRecountLinkCount > 8) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                //Utils.timeChecker("ForMeLink");
                //Utils.timeChecker("ProjectLink");
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(ProjectLink.class, ProjectTotalLink.class, LTaskContract.UidProject, ProjectLinkContract.CONTENT_URI,
                        ProjectTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWith(LTaskContract.UidProject, mTaskUUID).build()).start();
            } finally {
            try {
                while (mRecountLinkCount > 7) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                //Utils.timeChecker("ProjectLink");
                //Utils.timeChecker("CategoryLink");
                Utils.clearStringBuilder(mSb);
                new RecountLink<>(CategoryLink.class, CategoryTotalLink.class, LTaskContract.Categories, CategoryLinkContract.CONTENT_URI,
                        CategoryTotalLinkContract.CONTENT_URI,//
                        mSelectionBuilder.getTasksWith(LTaskContract.Categories, mTaskUUID).build()).start();
            } finally {
                try {
                while (mRecountLinkCount > 6) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Utils.toLog(e);
                    }
                }
                //Utils.timeChecker("CategoryLink");
                } finally {
                    RecountReadyLink();
                }
            }
        } } } } } }

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
                    mSelectionBuilder.getReadyTasks(mTaskUUID).build()).start();
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
                        mSelectionBuilder.getInworkTasks(mTaskUUID).build()).start();
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
                            mSelectionBuilder.getOverdueLinkTasks(mTaskUUID).build()).start();
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
                        new RecountLink<>(ColorLink.class, ColorTotalLink.class,  LTaskContract.UidMarker,//
                                LionMetaData.ColorLinkContract.CONTENT_URI, LionMetaData.ColorTotalLinkContract.CONTENT_URI,//
                                mSelectionBuilder.getTasksWithColor(mTaskUUID).build()).start();
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
                                    mSelectionBuilder.getTasksWithPerformer(mTaskUUID).build()).start();
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
                } else {
                    link();
                }

                /*if (mClassL == TaskLink.class) {
                    setTaskLinkUidToId();
                }*/

                if (mClassL == TaskLink.class) {
                        new SumAllAtTop(mContext).start();
                } else if (mClassL != InboxLink.class) {
                    removeNesting();
                }
            } catch (Exception e) {
                Utils.toLog(e);

            } finally {
                threadEnded();
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

                    /*if (mClassL == CategoryLink.class) {
                        forCategories(c, columnUidColumn, columnId, columnReaded, columnStatus);
                        return;
                    }*/

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
                            if (mClassL == UnreadLink.class || mClassL == OverdueLink.class) {
                                link.setUid("0");
                            } else {
                                link.setUid(c.getString(columnUidColumn));
                            }
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
                    mCr.delete(mUriL, null, null);
                    /*if (LTSettings.getInstance().getTasksToDelete().size() > 0) {
                        for (String id : LTSettings.getInstance().getTasksToDelete()) {
                            Utils.clearStringBuilder(mSb);
                            mCr.delete(mUriL, SelectionKeeper.equals(mSb, LinkContract.TaskId, id), null);
                        }
                    }*/
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
                        /*if (mClassL == ColorLink.class) {
                            try {
                                if (mColorTotalLinks.size() > 0) {
                                    mColorTotalLinks.remove(totalLink);
                                }
                                mColorTotalLinks.add((ColorTotalLink) totalLink);
                            } catch (Exception e) {

                            }
                        }*/
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

        /** Создать все возможные базовые связи */
        private void link() throws Exception {
            Cursor c = null;
            Cursor cursor = null;

            try {
                Utils.clearStringBuilder(mSb);
                c = mCr.query(LTaskContract.CONTENT_URI, null, mSelection, null, null);

                if (c.getCount() > 0) {
                    final int columnUidColumn = c.getColumnIndex(mColumnForUid);

                    final int columnId = c.getColumnIndex(LTaskContract._ID);
                    final int columnReaded = c.getColumnIndex(LTaskContract.Readed);
                    final int columnStatus = c.getColumnIndex(LTaskContract.Status);

                    final L tempLink = mClassL.newInstance();
                    final List <L> links = new ArrayList<>();
                    final List <L> oldLinks = new ArrayList<>();
                    String[] uidsForCategories;
                    int count = 0;

                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        if (mCalendarLink) {
                            tempLink.setUid(mDate);
                        } else {
                            if (mClassL == TaskLink.class) {
                                isSubtask = true;
                                String uidParent = c.getString(columnUidColumn);
                                Cursor tempCursor = null;
                                try {
                                    tempCursor = mCr.query(LTaskContract.CONTENT_URI, null, LTaskContract.Uid + " = '" + uidParent +"'", null, null);
                                    if (tempCursor.getCount() > 0) {
                                        final int id = c.getColumnIndex(LTaskContract._ID);
                                        tempCursor.moveToFirst();
                                        tempLink.setUid(""+tempCursor.getInt(id));
                                    }
                                } finally {
                                    if (tempCursor != null) {
                                        tempCursor.close();
                                    }
                                }
                            } else {
                                if (mClassL == UnreadLink.class || mClassL == OverdueLink.class) {
                                    tempLink.setUid("0");
                                } else {
                                    tempLink.setUid(c.getString(columnUidColumn));
                                }
                            }
                        }
                        tempLink.setTaskId(c.getInt(columnId));
                        tempLink.setReaded(c.getInt(columnReaded) == 1);
                        tempLink.setStatus(c.getInt(columnStatus));

                        if (mClassL == CategoryLink.class) {
                            uidsForCategories = TaskHelper.getCategoriesFromString(c.getString(columnUidColumn));
                            for (String uid : uidsForCategories) {
                                L link = mClassL.newInstance();
                                link.setTaskId(c.getInt(columnId));
                                link.setReaded(c.getInt(columnReaded) == 1);
                                link.setStatus(c.getInt(columnStatus));
                                link.setUid(uid);
                                links.add(link);
                                link = null;
                            }
                        } else {
                            links.add(tempLink);
                        }

                        for (L link : links) {
                            final String selection = "taskid='" + link.getTaskId() + "'";
                            ///////////////////////////////
                            L oldLink = mClassL.newInstance();
                            try {
                                cursor = mCr.query(mUriL, null, selection, null, null);

                                if (cursor.getCount() > 0) {
                                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                        oldLink.fillFromCursor(cursor);
                                        if (mClassL == TaskLink.class) {
                                            if (oldSubTaskLink == null) {
                                                oldSubTaskLink = oldLink;
                                            }
                                        }
                                    }
                                }
                            } finally {
                                cursor.close();
                            }

                            boolean isUpdateNewTaskTotal = false;
                            boolean isUpdateOldTaskTotal = false;
                            link.setId(oldLink.getId());
                            if (isSubtask) {
                                if (oldSubTaskLink != null) {
                                    if (!mCalendarLink) {
                                        oldLink = (L) oldSubTaskLink;
                                        oldLink.setUid(link.getUid());
                                    }
                                }
                            }
                            if (!link.getUid().equals(oldLink.getUid())) {
                                if (oldLink.getUid() == null) {
                                    //  НОВАЯ ЗАДАЧА
                                    android.util.Log.v("Tedorius", mClassL + " НОВАЯ ЗАДАЧА ");
                                    isUpdateNewTaskTotal = true;
                                } else {
                                    // изменили с линка на линк
                                    if (mClassL == ForMeLink.class) {
                                        // все равно завершилась или нет, если перешла в другой раздел то - скрыть уведомление
                                        TaskNotifyHelper.getInstance(mContext.getApplicationContext()).cancelNotify(mTaskSingle);
                                    }
                                    updateTotal(oldLink, link, true, true, null);
                                }
                            } else {
                                // если поменялся статус или прочитано
                                if (link.getStatus() != oldLink.getStatus() || link.isReaded() != oldLink.isReaded()) {
                                    android.util.Log.v("Tedorius", mClassL + " ИЗМЕНЕННАЯ ЗАДАЧА ");

                                    if (mClassL == ForMeLink.class) {
                                        // если завершилась задача - скрыть уведомление
                                        if (link.getStatus() ==  1 || link.getStatus() == 7 || link.getStatus() == 5 || link.getStatus() == 8) {
                                            TaskNotifyHelper.getInstance(mContext.getApplicationContext()).cancelNotify(mTaskSingle);
                                        }
                                    }

                                    /*if (mClassL == TaskLink.class) {
                                        isOldSubTask = true;
                                    }*/

                                    if (link.getStatus() != oldLink.getStatus()) {
                                        android.util.Log.v("Tedorius", mClassL + " статус был " + oldLink.getStatus() + " изменился на " + link.getStatus());
                                    }
                                    if (link.isReaded() != oldLink.isReaded()) {
                                        android.util.Log.v("Tedorius", mClassL + (oldLink.isReaded() ? " было прочитано " : " было непрочитано ") + "изменился на " +
                                                (link.isReaded() ? " прочитано " : " непрочитано "));
                                    }

                                    isUpdateOldTaskTotal = true;
                                }
                            }
                            ///////////////////////////////
                            if (isUpdateNewTaskTotal) {
                                updateTotal(oldLink, link, true, false, null);
                            } else {
                                if (isUpdateOldTaskTotal) {
                                    updateTotal(oldLink, link, false, false, null);
                                    ///////////////////////////////////////////////////////////////////////////////////////////////
                                    if (mClassL == TaskLink.class) {
                                        L linkParent = link;

                                        while (linkParent.getUid() != null) {
                                            final String selection2 = "taskid='" + linkParent.getUid() + "'";
                                            //
                                            L oldLinkParent = mClassL.newInstance();
                                            Cursor cursor2 = null;
                                            try {
                                                cursor2 = mCr.query(mUriL, null, selection2, null, null);

                                                if (cursor2.getCount() > 0) {
                                                    for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext()) {
                                                        oldLinkParent.fillFromCursor(cursor2);
                                                        //
                                                        android.util.Log.v("Tedorius", "эту нужно обновить "+oldLinkParent.getTaskId());
                                                        updateTotal(oldLink, link, false, false, ""+oldLinkParent.getTaskId());
                                                        //
                                                        linkParent = oldLinkParent;
                                                    }
                                                } else {
                                                    android.util.Log.v("Tedorius", "эту нужно обновить "+linkParent.getUid());
                                                    updateTotal(oldLink, link, false, false, linkParent.getUid());
                                                    linkParent = oldLinkParent;
                                                }
                                            } finally {
                                                cursor2.close();
                                            }
                                        }

                                    }
                                    ///////////////////////////////////////////////////////////////////////////////////////////////////
                                }
                            }
                            mCr.delete(mUriL, "taskid='" + link.getTaskId()+"'", null);
                            ContentValues cv = link.getContentValues(null);
                            mCr.insert(mUriL, cv);

                            //TaskNotifyHelper.getInstance(mContext).updateTaskNotify(mTaskSingle);
                        }
                    }

                } else {
                    delLinkAndUpdateTotal(""+mTaskSingle.getIdTask());
                    /*if (mClassL == CalendarLink.class) {
                        TaskNotifyHelper.getInstance(mContext).deleteOldTaskNotify(mTaskSingle);
                    }*/
                }
            } catch (Exception e) {

            } finally {
                if (c != null) {
                    c.close();
                }
                deleteLinksOfDeletedTasks();
            }
        }

        private void delTotal (L linkOld) {
            final Map<String, T> links = new HashMap<>();
            String selection;
            T totalOld;

            Utils.clearStringBuilder(mSb);
            selection = SelectionKeeper.equals(mSb, TotalLinkContract.Uid, linkOld.getUid());

            Cursor c = null;
            try {
                totalOld = mClassT.newInstance();
                int isReaded = linkOld.isReaded() ? 0 : 1;

                totalOld.setTasks(totalOld.getTasks() + 1);
                totalOld.setTasksUnreaded(totalOld.getTasksUnreaded() + isReaded);
                if (mClassL == TaskLink.class) {
                    if (linkOld.getStatus() !=  1 && linkOld.getStatus() != 7) {
                        totalOld.setTasksUncompleted(totalOld.getTasksUncompleted() + 1);
                        totalOld.setTasksUncompletedUnreaded(totalOld.getTasksUncompletedUnreaded() + isReaded);
                    }
                } else {
                    if (mClassL == ForMeLink.class) {
                        if (linkOld.getStatus() !=  1 && linkOld.getStatus() != 7 && linkOld.getStatus() != 5) {
                            if (linkOld.getStatus() != Status.NOTE.getStatusCode()) {
                                totalOld.setTasksUncompleted(totalOld.getTasksUncompleted() + 1);
                                totalOld.setTasksUncompletedUnreaded(totalOld.getTasksUncompletedUnreaded() + isReaded);
                            } else {
                                totalOld.setTasksNotes(totalOld.getTasksNotes() + 1);
                            }
                        }
                    } else {
                        if (linkOld.getStatus() !=  1 && linkOld.getStatus() != 7) {
                            if (linkOld.getStatus() != Status.NOTE.getStatusCode()) {
                                totalOld.setTasksUncompleted(totalOld.getTasksUncompleted() + 1);
                                totalOld.setTasksUncompletedUnreaded(totalOld.getTasksUncompletedUnreaded() + isReaded);
                            } else {
                                totalOld.setTasksNotes(totalOld.getTasksNotes() + 1);
                            }
                        }
                    }
                }

                ///////////////////////////////////////////////////////////////////////
                c = mCr.query(mUriT, null, selection, null, ORDER_LINK_UID);
                T fullTotal = mClassT.newInstance();
                fullTotal.setUid(linkOld.getUid());
                if (c.getCount() > 0) {
                    // получаем прошлый тотал
                    c.moveToFirst();
                    fullTotal.fillFromCursor(c);
                }

                fullTotal.setTasks(fullTotal.getTasks() - totalOld.getTasks());
                fullTotal.setTasksUnreaded(fullTotal.getTasksUnreaded() - totalOld.getTasksUnreaded());
                fullTotal.setTasksUncompleted(fullTotal.getTasksUncompleted() - totalOld.getTasksUncompleted());
                fullTotal.setTasksUncompletedUnreaded(fullTotal.getTasksUncompletedUnreaded() - totalOld.getTasksUncompletedUnreaded());
                fullTotal.setTasksNotes(fullTotal.getTasksNotes() - totalOld.getTasksNotes());

                links.put(linkOld.getUid(), fullTotal);
                final ContentValues[] cvs = contentValuesFromMap(links);

                mCr.delete(mUriT, selection, null);
                mCr.bulkInsert(mUriT, cvs);

                mCr.delete(mUriL, "taskid='" + linkOld.getTaskId()+"'", null);
                ContentValues cv = linkOld.getContentValues(null);

            } catch (Exception e) {

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

        private void updateTotal(L linkOld, L linkNew, boolean newTask, boolean changeUid, String needUpdate) {
            final Map<String, T> links = new HashMap<>();
            String selectionNew;
            String selectionOld;
            T totalOld;
            T totalNew;
            int Tasks = 0;
            int TasksUnreaded = 0;
            int TasksUncompleted = 0;
            int TasksUncompletedUnreaded = 0;
            int TasksNotes = 0;

            Utils.clearStringBuilder(mSb);
            if (needUpdate != null && !needUpdate.isEmpty()) {
                selectionNew = SelectionKeeper.equals(mSb, TotalLinkContract.Uid, needUpdate);
            } else {
                selectionNew = SelectionKeeper.equals(mSb, TotalLinkContract.Uid, linkNew.getUid());
            }
            Utils.clearStringBuilder(mSb);
            selectionOld = SelectionKeeper.equals(mSb, TotalLinkContract.Uid, linkOld.getUid());

            Cursor c = null;
            Cursor cursor = null;
            try {
                totalOld = mClassT.newInstance();
                int isReaded = linkOld.isReaded() ? 0 : 1;
                totalOld.setUid(linkOld.getUid());
                totalOld.setTasks(totalOld.getTasks() + 1);
                totalOld.setTasksUnreaded(totalOld.getTasksUnreaded() + isReaded);
                if (mClassL == TaskLink.class) {
                    if (linkOld.getStatus() !=  1 && linkOld.getStatus() != 7) {
                        totalOld.setTasksUncompleted(totalOld.getTasksUncompleted() + 1);
                        totalOld.setTasksUncompletedUnreaded(totalOld.getTasksUncompletedUnreaded() + isReaded);
                    }
                } else {
                    if (mClassL == ForMeLink.class) {
                        if (linkOld.getStatus() !=  1 && linkOld.getStatus() != 7 && linkOld.getStatus() != 5  && linkOld.getStatus() != 8) {
                            if (linkOld.getStatus() != Status.NOTE.getStatusCode()) {
                                totalOld.setTasksUncompleted(totalOld.getTasksUncompleted() + 1);
                                totalOld.setTasksUncompletedUnreaded(totalOld.getTasksUncompletedUnreaded() + isReaded);
                            } else {
                                totalOld.setTasksNotes(totalOld.getTasksNotes() + 1);
                            }
                        }
                    } else {
                        if (linkOld.getStatus() !=  1 && linkOld.getStatus() != 7) {
                            if (linkOld.getStatus() != Status.NOTE.getStatusCode()) {
                                totalOld.setTasksUncompleted(totalOld.getTasksUncompleted() + 1);
                                totalOld.setTasksUncompletedUnreaded(totalOld.getTasksUncompletedUnreaded() + isReaded);
                            } else {
                                totalOld.setTasksNotes(totalOld.getTasksNotes() + 1);
                            }
                        }
                    }


                }
                totalNew = mClassT.newInstance();
                int isReadedNew = linkNew.isReaded() ? 0 : 1;
                totalNew.setUid(linkNew.getUid());

                totalNew.setTasks(totalNew.getTasks() + 1);
                totalNew.setTasksUnreaded(totalNew.getTasksUnreaded() + isReadedNew);
                if (mClassL == TaskLink.class) {
                    if (linkNew.getStatus() !=  1 && linkNew.getStatus() != 7) {
                        totalNew.setTasksUncompleted(totalNew.getTasksUncompleted() + 1);
                        totalNew.setTasksUncompletedUnreaded(totalNew.getTasksUncompletedUnreaded() + isReadedNew);
                    }
                } else {
                    if (mClassL == ForMeLink.class) {
                        if (linkNew.getStatus() !=  1 && linkNew.getStatus() != 7 && linkNew.getStatus() != 5 && linkNew.getStatus() != 8) {
                            if (linkNew.getStatus() != Status.NOTE.getStatusCode()) {
                                totalNew.setTasksUncompleted(totalNew.getTasksUncompleted() + 1);
                                totalNew.setTasksUncompletedUnreaded(totalNew.getTasksUncompletedUnreaded() + isReadedNew);
                            } else {
                                totalNew.setTasksNotes(totalNew.getTasksNotes() + 1);
                            }
                        }
                    } else {
                        if (linkNew.getStatus() !=  1 && linkNew.getStatus() != 7) {
                            if (linkNew.getStatus() != Status.NOTE.getStatusCode()) {
                                totalNew.setTasksUncompleted(totalNew.getTasksUncompleted() + 1);
                                totalNew.setTasksUncompletedUnreaded(totalNew.getTasksUncompletedUnreaded() + isReadedNew);
                            } else {
                                totalNew.setTasksNotes(totalNew.getTasksNotes() + 1);
                            }
                        }
                    }
                }

                ///////////////////////////////////////////////////////////////////////
                c = mCr.query(mUriT, null, selectionNew, null, ORDER_LINK_UID);
                T fullTotalNew = mClassT.newInstance();
                fullTotalNew.setUid(linkNew.getUid());
                if (c.getCount() > 0) {
                    // получаем прошлый тотал
                    c.moveToFirst();
                    fullTotalNew.fillFromCursor(c);
                }

                if (!newTask) {
                    if (totalNew.getTasks() > totalOld.getTasks()) {
                        Tasks = 1;
                    } else {
                        if (totalNew.getTasks() < totalOld.getTasks()) {
                            Tasks = -1;
                        }
                    }
                    if (totalNew.getTasksUnreaded() > totalOld.getTasksUnreaded()) {
                        TasksUnreaded = 1;
                    } else {
                        if (totalNew.getTasksUnreaded() < totalOld.getTasksUnreaded()) {
                            TasksUnreaded = -1;
                        }
                    }
                    if (totalNew.getTasksUncompleted() > totalOld.getTasksUncompleted()) {
                        TasksUncompleted = 1;
                    } else {
                        if (totalNew.getTasksUncompleted() < totalOld.getTasksUncompleted()) {
                            TasksUncompleted = -1;
                        }
                    }
                    if (totalNew.getTasksUncompletedUnreaded() > totalOld.getTasksUncompletedUnreaded()) {
                        TasksUncompletedUnreaded = 1;
                    } else {
                        if (totalNew.getTasksUncompletedUnreaded() < totalOld.getTasksUncompletedUnreaded()) {
                            TasksUncompletedUnreaded = -1;
                        }
                    }
                    if (totalNew.getTasksNotes() > totalOld.getTasksNotes()) {
                        TasksNotes = 1;
                    } else {
                        if (totalNew.getTasksNotes() < totalOld.getTasksNotes()) {
                            TasksNotes = -1;
                        }
                    }


                }

                fullTotalNew.setTasks(fullTotalNew.getTasks()  + (newTask ? totalNew.getTasks() : Tasks));
                fullTotalNew.setTasksUnreaded(fullTotalNew.getTasksUnreaded()  + (newTask ? totalNew.getTasksUnreaded() : TasksUnreaded));
                fullTotalNew.setTasksUncompleted(fullTotalNew.getTasksUncompleted() + (newTask ? totalNew.getTasksUncompleted() :  TasksUncompleted));
                fullTotalNew.setTasksUncompletedUnreaded(fullTotalNew.getTasksUncompletedUnreaded() +  (newTask ? totalNew.getTasksUncompletedUnreaded() : TasksUncompletedUnreaded));
                fullTotalNew.setTasksNotes(fullTotalNew.getTasksNotes() + (newTask ? totalNew.getTasksNotes() : TasksNotes));


                links.put(linkNew.getUid(), fullTotalNew);
                ContentValues[] cvs = contentValuesFromMap(links);

                mCr.delete(mUriT, selectionNew, null);
                mCr.bulkInsert(mUriT, cvs);

                if (changeUid) {
                    if (linkOld.getUid() != null) {
                        Map<String, T> linksChange = new HashMap<>();
                        cursor = mCr.query(mUriT, null, selectionOld, null, ORDER_LINK_UID);
                        T fullTotalOld = mClassT.newInstance();
                        fullTotalOld.setUid(linkOld.getUid());
                        if (cursor.getCount() > 0) {
                            // получаем прошлый тотал
                            cursor.moveToFirst();
                            fullTotalOld.fillFromCursor(cursor);
                        }

                        fullTotalOld.setTasks(fullTotalOld.getTasks() - totalOld.getTasks());
                        fullTotalOld.setTasksUnreaded(fullTotalOld.getTasksUnreaded() - totalOld.getTasksUnreaded());
                        fullTotalOld.setTasksUncompleted(fullTotalOld.getTasksUncompleted() - totalOld.getTasksUncompleted());
                        fullTotalOld.setTasksUncompletedUnreaded(fullTotalOld.getTasksUncompletedUnreaded() - totalOld.getTasksUncompletedUnreaded());
                        fullTotalOld.setTasksNotes(fullTotalOld.getTasksNotes() - totalOld.getTasksNotes());


                        linksChange.put(linkOld.getUid(), fullTotalOld);
                        cvs = contentValuesFromMap(linksChange);

                        mCr.delete(mUriT, selectionOld, null);
                        mCr.bulkInsert(mUriT, cvs);
                    }
                }
            } catch (Exception e) {

            } finally {
                if (c != null) {
                    c.close();
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        private void delLinkAndUpdateTotal(String taskId) {
            Cursor c1 = null;
            Cursor c2 = null;
            try {
                Utils.clearStringBuilder(mSb);
                L oldLink = mClassL.newInstance();
                c1 = mCr.query(mUriL, null, SelectionKeeper.equals(mSb, LinkContract.TaskId, taskId), null, null);
                if (c1.getCount() > 0) {
                    for (c1.moveToFirst(); !c1.isAfterLast(); c1.moveToNext()) {
                        oldLink.fillFromCursor(c1);
                        delTotal(oldLink);
                        if (mClassL == TaskLink.class) {
                            isSubtask = true;
                            oldSubTaskLink = oldLink;
                            //если это подзадача
                            if (oldSubTaskLinkDel == null) {
                                oldSubTaskLinkDel = oldLink;
                            }
                        }
                    }
                } else {
                    //если линка такого нет
                    if (oldSubTaskLinkDel != null) {
                        //но это подзадача
                        //то ищем самого главного его родителя в таблице TaskLink и запоминаем TaskId
                        if (parentSubString == null) {
                            parentSubString = oldSubTaskLinkDel.getUid();
                            findMainParent("" + oldSubTaskLinkDel.getUid());
                        }
                        // потом смотрим есть ли в mUriL такой TaskId и какой у него UID
                        if (parentSubString != null) {
                            Utils.clearStringBuilder(mSb);
                            c2 = mCr.query(mUriL, null, SelectionKeeper.equals(mSb, LinkContract.TaskId, parentSubString), null, null);
                            if (c2.getCount() > 0) {
                                c2.moveToFirst();
                                L tempLink = mClassL.newInstance();
                                tempLink.fillFromCursor(c2);
                                tempLink.setReaded(oldSubTaskLinkDel.isReaded());
                                tempLink.setStatus(oldSubTaskLinkDel.getStatus());
                                tempLink.setTaskId(oldSubTaskLinkDel.getTaskId());
                                //по UID удаляем
                                delTotal(tempLink);
                            }
                        }
                    }
                }
            } catch (Exception e) {

            } finally {
                boolean hasLinks = false;
                if (c1 != null) {
                    if (c1.getCount() > 0) {
                        hasLinks = true;
                    }
                    c1.close();
                    c1 = null;
                }
                if (c2 != null) {
                    if (c2.getCount() > 0) {
                        hasLinks = true;
                    }
                    c2.close();
                    c2 = null;
                }
                if (hasLinks) {
                    Utils.clearStringBuilder(mSb);
                    mCr.delete(mUriL, SelectionKeeper.equals(mSb, LinkContract.TaskId, taskId), null);
                }
            }
        }

        private void findMainParent(String taskId) {
            Cursor c = null;
            try {
                Utils.clearStringBuilder(mSb);
                c = mCr.query(TaskLinkContract.CONTENT_URI, null, SelectionKeeper.equals(mSb, LinkContract.TaskId, taskId), null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    L link = mClassL.newInstance();
                    link.fillFromCursor(c);
                    //ищем родилея у родителя
                    parentSubString = link.getUid();
                    findMainParent(link.getUid());
                }
            } catch (Exception e) {

            }
            finally {
                if (c != null) {
                    c.close();
                }
            }
        }

        private void deleteLinksOfDeletedTasks() {
            for (String id : LTSettings.getInstance().getTasksToDelete()) {
                delLinkAndUpdateTotal(id);
                if (mClassL == ForMeLink.class) {
                    TaskNotifyHelper.getInstance(mContext).cancelNotifyByID(Integer.parseInt(id));
                }
            }
            if (mClassL == CategoryLink.class) {
                LTSettings.getInstance().getTasksToDelete().clear();
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



        private void setTaskLinkUidToId() throws Exception {
            Utils.clearStringBuilder(mSb);
            Cursor c = null;

            try {
                c = DbHelper.getInstance(mContext).getWritableDatabase().rawQuery(mSelectionBuilder.getNewRawUpdateTaskLinkUidToId(""+mTaskSingle.getIdTask()).build(), null);
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