package com.ashberrysoft.leadertask.modern.changer;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.TaskLink;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;

public class ParentChanger extends BaseTaskChanger {

    public ParentChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && Utils.equals(taskNew.getUIDParent(), taskOld.getUIDParent());
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return !TextUtils.isEmpty(task.getUIDParent());
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String uid = getParentId(task.getUIDParent());

        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(TaskTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(TaskLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String uid = getParentId(task.getUIDParent());

        /** уменьшаем счетчики связи в соответствии с задачей */
        if (totalLinkExists(TaskTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(TaskTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final TaskTotalLink totalLink = new TaskTotalLink();
            totalLink.setUid(uid);

            addOperation(createNewTotalLink(totalLink, task));
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        final LTask task = getNew();
        final String uid = getParentId(task.getUIDParent());

        final TaskLink link = new TaskLink();
        link.setUid(uid);
        link.setTaskId(task.getIdTask());
        link.setReaded(task.getReaded());
        link.setStatus(task.getStatus());

        addOperation(Utils.getIncertOperation(link));

        return true;
    }

    @Override
    public void notifyChanges() {
        getContext().getContentResolver().notifyChange(TaskTotalLinkContract.CONTENT_URI, null);
    }

    private String getParentId(String uid) {
        clearSb();
        SelectionKeeper.eq(getSb(), LTaskContract.Uid, uid);

        String id = uid;
        Cursor c = null;

        try {
            c = getContext().getContentResolver().query(LTaskContract.CONTENT_URI, null, toString(), null, null);
            if (c.moveToFirst()) {
                id = c.getString(c.getColumnIndex(LTaskContract._ID));
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return id;
    }
}