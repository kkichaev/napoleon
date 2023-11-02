package com.ashberrysoft.leadertask.modern.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.VerticalDepthTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public class VerticalDepthHelper {

    // BASE
    private final Context mContext;

    // VALUE's
    private final ContentValues mRemoveUidParent;
    private final StringBuilder mSb;
    private Map<String, VerticalDepthTask> mMap;
    private Map<String, VerticalDepthTask> mMapAll;
    private final String mTaskUUID;

    public VerticalDepthHelper(Context context, String taskUUID) {
        mContext = context.getApplicationContext();
        mTaskUUID = taskUUID;
        mRemoveUidParent = new ContentValues(1);
        mRemoveUidParent.put(LTaskContract.UIDParent, (String) null);
        mSb = new StringBuilder();

        run();
    }

    private void run() {
        try {
            if (mTaskUUID !=null) {
                fillAllMap();
            }
            fillMap();
            setVertical();
            setDepth();
            saveVertical();
            updateParentUidToId();

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private void fillAllMap() {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, null, null, null);

            final int columnId = c.getColumnIndex(LTaskContract._ID);
            final int columnUid = c.getColumnIndex(LTaskContract.Uid);
            final int columnUidParent = c.getColumnIndex(LTaskContract.UIDParent);

            mMapAll = new HashMap<String, VerticalDepthTask>(c.getCount());
            VerticalDepthTask task;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                task = new VerticalDepthTask();
                task.setId(c.getInt(columnId));
                task.setParentId(c.getString(columnUidParent));

                mMapAll.put(c.getString(columnUid), task);
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void fillMap() {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(LTaskContract.CONTENT_URI, null, mTaskUUID == null ? null : SelectionKeeper.equals(mSb, LTaskContract.Uid, mTaskUUID), null, null);

            final int columnId = c.getColumnIndex(LTaskContract._ID);
            final int columnUid = c.getColumnIndex(LTaskContract.Uid);
            final int columnUidParent = c.getColumnIndex(LTaskContract.UIDParent);

            mMap = new HashMap<String, VerticalDepthTask>(c.getCount());
            VerticalDepthTask task;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                task = new VerticalDepthTask();
                task.setId(c.getInt(columnId));
                task.setParentId(c.getString(columnUidParent));

                mMap.put(c.getString(columnUid), task);
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void setVertical() {
        int vertical = 1;

        for (VerticalDepthTask task : mMap.values()) {
            if (task.getVertical() == 0) {
                getVertical(task, vertical++);
            }
        }
    }

    private int getVertical(VerticalDepthTask task, int vertical) {
        if (task.getVertical() != 0) {
            return task.getVertical();
        }

        if (task.getParentId() == null) {
            task.setVertical(vertical);
            return vertical;

        } else {
            if (task.isPassed()) {
                removeUidParent(task.getId());

                task.setParentId(null);
                task.setVertical(vertical);

                return vertical;
            }

            task.setPassed(true);
            mMap.put(task.getParentId(), getTask(task.getParentId()));
            vertical = getVertical(getTask(task.getParentId()), vertical);

            task.setVertical(vertical);

            return vertical;
        }
    }

    private void removeUidParent(int id) {
        // TODO: usn uidParent +1
        Utils.clearStringBuilder(mSb);
        mContext.getContentResolver().update(LTaskContract.CONTENT_URI, mRemoveUidParent, SelectionKeeper.eq(mSb, LTaskContract._ID, id).toString(), null);
    }

    private void setDepth() {
        for (VerticalDepthTask task : mMap.values()) {
            getDepth(task);
        }
    }

    private int getDepth(VerticalDepthTask task) {
        if (task.getDepth() != 0) {
            return task.getDepth();
        }

        if (task.getParentId() == null) {
            task.setDepth(1);
            return 1;

        } else {
            final int depth = getDepth(getTask(task.getParentId())) + 1;

            task.setDepth(depth);

            return depth;
        }
    }

    private VerticalDepthTask getTask(String uid) {
        if (mTaskUUID !=null) {
            return mMapAll.get(uid);
        } else {
            return mMap.get(uid);
        }
    }

    private void saveVertical() {
        final ContentValues[] cvs = new ContentValues[mMap.size()];
        int count = 0;

        for (VerticalDepthTask task : mMap.values()) {
            cvs[count++] = task.getContentValues(null);
        }

        if (mTaskUUID == null) {
            mContext.getContentResolver().delete(VerticalDepthTaskContract.CONTENT_URI, null, null);

        } else {
            for (VerticalDepthTask task : mMap.values()) {
                Utils.clearStringBuilder(mSb);
                mContext.getContentResolver().delete(VerticalDepthTaskContract.CONTENT_URI, SelectionKeeper.equals(mSb, VerticalDepthTaskContract._ID, task.getId()), null);
            }
        }

        mContext.getContentResolver().bulkInsert(VerticalDepthTaskContract.CONTENT_URI, cvs);

        Cursor c = null;
        Utils.clearStringBuilder(mSb);

        try {
            c = mContext.getContentResolver().query(VerticalDepthTaskContract.CONTENT_URI, null, null, null,
                    SelectionKeeper.sort(mSb, VerticalDepthTaskContract.Vertical));

            final int maxVertical = c.moveToLast() ? c.getInt(c.getColumnIndex(VerticalDepthTaskContract.Vertical)) : 1;
            LTSettings.getInstance(mContext).setMaximumVertical(maxVertical);

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void updateParentUidToId() {
        Utils.clearStringBuilder(mSb);
        Cursor c = null;

        try {
            c = DbHelper.getInstance(mContext).getWritableDatabase().rawQuery(new TaskSelectionBuilder(mSb).getRawUpdateVerticalParentUidToId().build(), null);
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    public static final class SumAllAtTop extends Thread {

        private final Context mContext;

        public SumAllAtTop(Context context) {
            super(SumAllAtTop.class.getSimpleName());
            mContext = context.getApplicationContext();
        }

        @Override
        public void run() {
            super.run();

            final StringBuilder sb = new StringBuilder();
            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

            Cursor c = null;

            try {
                SelectionKeeper.order(sb, VerticalDepthTaskContract.Depth, false);
                sb.append(SharedStrings.COMMA_C);
                SelectionKeeper.order(sb, VerticalDepthTaskContract.ParentId, true);
                final String order = sb.toString();

                Utils.clearStringBuilder(sb);
                c = mContext.getContentResolver().query(VerticalDepthTaskContract.CONTENT_URI, null,
                        new TaskSelectionBuilder(sb).getVerticalTasksWithLinks().build(), null, order);

                final int columnId = c.getColumnIndex(VerticalDepthTaskContract._ID);
                final int columnParentId = c.getColumnIndex(VerticalDepthTaskContract.ParentId);
                final int columnDepth = c.getColumnIndex(VerticalDepthTaskContract.Depth);

                int depth;
                int lastDepth = -1;

                final SparseArray<TaskTotalLink> totalLinks = new SparseArray<>();

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    depth = c.getInt(columnDepth);
                    if (lastDepth < 0) {
                        lastDepth = depth;
                    }

                    addLowerToTopper(mContext, operations, totalLinks, sb, c.getInt(columnId), c.getString(columnParentId));

                    if (depth < lastDepth) {
                        applyBatch(operations);
                        lastDepth = depth;
                    }
                }
                applyBatch(operations);

            } finally {
                if (c != null) {
                    c.close();
                }
                mContext.getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);
            }
        }

        private void applyBatch(ArrayList<ContentProviderOperation> operations) {
            if (operations.size() > 0) {
                try {
                    mContext.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);

                } catch (Exception e) {
                    Utils.toLog(e);
                }

                operations.clear();
            }
        }

        private static void addLowerToTopper(Context context, ArrayList<ContentProviderOperation> operations, SparseArray<TaskTotalLink> totalLinks,
                StringBuilder sb, int childId, String parentId) {
            TaskTotalLink childLink = totalLinks.get(childId);
            if (childLink == null) {
                childLink = TaskHelper.getTaskTotalLink(context, sb, childId);
                if (childLink == null || parentId == null) {
                    return;
                }

                totalLinks.put(childId, childLink);
            }

            final int pId = Integer.parseInt(parentId);
            TaskTotalLink parentLink = totalLinks.get(pId);
            if (parentLink == null) {
                parentLink = TaskHelper.getTaskTotalLink(context, sb, pId);
                if (parentLink == null) {
                    return;
                }

                totalLinks.put(pId, parentLink);
            }

            parentLink.sumToTasksUnreaded(childLink.getTasksUnreaded());
            parentLink.sumToTasksUncompletedUnreaded(childLink.getTasksUncompletedUnreaded());

            final ContentValues cv = new ContentValues(2);
            cv.put(TaskTotalLinkContract.TasksUnreaded, parentLink.getTasksUnreaded());
            cv.put(TaskTotalLinkContract.TasksUncompletedUnreaded, parentLink.getTasksUncompletedUnreaded());

            operations.add(ContentProviderOperation.newUpdate(TaskTotalLinkContract.CONTENT_URI).withValues(cv).withSelection(sb.toString(), null).build());
        }
    }
}