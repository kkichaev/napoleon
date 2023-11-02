package com.ashberrysoft.leadertask.modern.cache;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache.LTaskCacheHolder;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class LTaskCache extends BaseSparseCache<LTaskCacheHolder> {

    private static final Object OBJECT = new Object();

    // SINGLETON
    private static LTaskCache sInstance;

    // VALUE's
    private final StringBuilder mSb;

    private final EmployeeCache mEmployee;
    private final MarkerCache mMarker;
    private final CompletedCache mCompleted;

    private final TaskLinkCache mTaskLink;
    private final TaskFileCache mTaskFile;
    private final TaskMessageCache mTaskMessage;

    private boolean mSimpleCacheRefreshed;

    public static LTaskCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LTaskCache.class) {
                if (sInstance == null) {
                    sInstance = new LTaskCache(context);
                }
            }
        }
        return sInstance;
    }

    private LTaskCache(Context context) {
        super(context);

        mSb = new StringBuilder();

        mEmployee = EmployeeCache.getInstance(getContext());
        mMarker = MarkerCache.getInstance(getContext());
        mCompleted = CompletedCache.getInstance(getContext());

        mTaskLink = TaskLinkCache.getInstance(getContext());
        mTaskFile = TaskFileCache.getInstance(getContext());
        mTaskMessage = TaskMessageCache.getInstance(getContext());
    }

    @Override
    public void refreshCache() {
        if (!mSimpleCacheRefreshed) {
            mSimpleCacheRefreshed = true;

            mEmployee.refreshCache();
            mMarker.refreshCache();
            mCompleted.refreshCache();
        }
    }

    public void resetSimpleCache() {
        mSimpleCacheRefreshed = false;
        refreshCache();
    }

    @Override
    public Integer getKey(LTaskCacheHolder value) {
        return value.getId();
    }

    public void refreshCache(LTask task) {
        refreshCache(new int[] { task.getIdTask() }, new String[] { task.getUid() }, new String[] { task.getUidMarker() });
    }

    public void refreshCache(int[] ids, String[] uids, String[] markers) {
        synchronized (OBJECT) {
            refreshCache();

            if (ids.length > 0) {
                updateTaskLink(ids);
                updateTaskFile(uids);
                updateTaskMessage(uids);

                update(ids, uids, markers);
            }
        }
    }

    private void updateTaskLink(int[] ids) {
        mTaskLink.clear();
        Utils.clearStringBuilder(mSb);
        Cursor c = null;

        try {
            c = getContext().getContentResolver().query(TaskTotalLinkContract.CONTENT_URI, null,//
                    SelectionKeeper.in(mSb, TaskTotalLinkContract.Uid, ids).toString(), null, null);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                mTaskLink.updateCache(new TaskTotalLink(c));
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void updateTaskFile(String[] uids) {
        Utils.clearStringBuilder(mSb);
        Cursor c = null;

        try {
            c = getContext().getContentResolver().query(TaskFileContract.CONTENT_URI, null,//
                    TaskFileContract.inWeakLinkAndTaskUids(mSb, false, uids), null, null);

            if (c.getCount() > 0) {
                final TaskFile[] files = new TaskFile[c.getCount()];
                int count = 0;

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    files[count++] = new TaskFile(c);
                }

                final Map<UUID, List<TaskFile>> map = new HashMap<>();
                List<TaskFile> list;

                for (TaskFile file : files) {
                    list = map.get(file.getTaskId());
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(file.getTaskId(), list);
                    }
                    list.add(file);
                }

                mTaskFile.updateCache(map.values());
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void updateTaskMessage(String[] uids) {
        try {
            final List<UUID> uuids = new ArrayList<>(uids.length);
            /*
            Ревизия 236
            Было:
            //////////////////////////////////////////////////
            for (String uid : uids) {
                uuids.add(UUID.fromString(uid));
            }
            //////////////////////////////////////////////////
            Изменили, потому что не могло достать UUID и вылетало с ошибкой IllegalArgumentException: Invalid UUID: 2
            http://stackoverflow.com/questions/18871980/uuid-fromstring-returns-an-invalid-uuid
            */
            for (String uid : uids) {
                String temp = uid.replace("-", "");
                UUID uuid = new UUID(
                        new BigInteger(temp.substring(0, 16), 16).longValue(),
                        new BigInteger(temp.substring(16), 16).longValue());
                uuids.add(uuid);
            }

            final Dao<TaskMessage, UUID> dao = DbHelper.getInstance(getContext()).getTaskMessageDao();
            final QueryBuilder<TaskMessage, UUID> query = dao.queryBuilder().orderBy(TaskMessage.FIELD_TASK_UID, true);
            query.setWhere(dao.queryBuilder().where().in(TaskMessage.FIELD_TASK_UID, uuids));
            final List<TaskMessage> messages = dao.query(query.prepare());

            if (messages.size() > 0) {
                final Map<UUID, List<TaskMessage>> map = new HashMap<>();
                List<TaskMessage> list;

                for (TaskMessage message : messages) {
                    list = map.get(message.getTaskUID());
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(message.getTaskUID(), list);
                    }
                    list.add(message);
                }

                mTaskMessage.updateCache(map.values());
            }

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private void update(int[] ids, String[] uids, String[] markers) {

        LTaskCacheHolder holder;
        String uid;
        String marker;
        int uidHash;
        int count = 0;
        Marker holderMarker;

        for (int id : ids) {
            holder = new LTaskCacheHolder(id);
            uid = uids[count];
            marker = markers[count];
            uidHash = uid.hashCode();

            if (!Marker.DEFAULT_MARKER_STRING.equals(marker)) {
                try {
                    holderMarker = mMarker.find(marker.hashCode());
                    holder.setMarker(holderMarker);
                }
                catch (Exception e) {}
            }
            holder.setTaskTotal(mTaskLink.find(id));
            holder.setCompletedTask(mCompleted.find(id));
            holder.setHasFiles(mTaskFile.find(uidHash) != null);
            holder.setHasMessages(mTaskMessage.find(uidHash) != null);

            updateCache(holder);
            count++;
        }
    }

    public static final class LTaskCacheHolder//
            implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int mId;
        private Marker mMarker;
        private List<Category> mCategories;
        private TaskTotalLink mTaskTotal;
        private CompletedTask mCompletedTask;
        private boolean mHasFiles;
        private boolean mHasMessages;

        public LTaskCacheHolder(int taskId) {
            mId = taskId;
        }

        public int getId() {
            return mId;
        }

        public Marker getMarker() {
            return mMarker;
        }

        public void setMarker(Marker marker) {
            mMarker = marker;
        }

        public List<Category> getCategories() {
            return mCategories;
        }

        public void setCategories(List<Category> categories) {
            mCategories = categories;
        }

        public TaskTotalLink getTaskTotal() {
            return mTaskTotal;
        }

        public void setTaskTotal(TaskTotalLink taskTotal) {
            mTaskTotal = taskTotal;
        }

        public CompletedTask getCompletedTask() {
            return mCompletedTask;
        }

        public void setCompletedTask(CompletedTask completedTask) {
            mCompletedTask = completedTask;
        }

        public boolean isHasFiles() {
            return mHasFiles;
        }

        public void setHasFiles(boolean hasFiles) {
            mHasFiles = hasFiles;
        }

        public boolean isHasMessages() {
            return mHasMessages;
        }

        public void setHasMessages(boolean hasMessages) {
            mHasMessages = hasMessages;
        }

        private final StringBuilder mSb = new StringBuilder();

        @Override
        public String toString() {
            Utils.clearStringBuilder(mSb);

            mSb.append("id = ");
            mSb.append(getId());
            mSb.append(SharedStrings.TAB_C);
            mSb.append(getTaskTotal());

            return mSb.toString();
        }
    }
}