package com.ashberrysoft.leadertask.modern.changer;

import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.InboxLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.InboxTotalLinkContract;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.InboxLink;
import com.ashberrysoft.leadertask.modern.domains.link.InboxTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.utils.Utils;

public class InboxChanger extends BaseTaskChanger {

    public InboxChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && (possiblyHasLink(taskNew) == possiblyHasLink(taskOld));
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return TaskHelper.isInboxTask(getContext(), task);
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String uid = getSettings().getUserName();

        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(InboxTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(InboxLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String uid = getSettings().getUserName();

        /** уменьшаем счетчики связи в соответствии с задачей */
        if (totalLinkExists(InboxTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(InboxTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final InboxTotalLink totalLink = new InboxTotalLink();
            totalLink.setUid(uid);

            addOperation(createNewTotalLink(totalLink, task));
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        final String uid = getSettings().getUserName();

        /** ищем может ли какая-то задача стать на место этой */
        clearSb();
        final TaskSelectionBuilder sb = new TaskSelectionBuilder(getSb());
        sb.getInboxTasks(null);

        /** находим задачи относящиеся к связи */
        final List<VerticalDepthTask> verticals = getTasksForLink(sb.build());

        /** связываем найденные задачи с uid */
        final InboxLink link = new InboxLink();
        for (VerticalDepthTask vertical : verticals) {
            link.setUid(uid);
            link.setTaskId(vertical.getTask().getIdTask());
            link.setReaded(vertical.getTask().getReaded());
            link.setStatus(vertical.getTask().getStatus());

            addOperation(Utils.getIncertOperation(link));
        }

        return true;
    }

    @Override
    public void notifyChanges() {
        getContext().getContentResolver().notifyChange(InboxTotalLinkContract.CONTENT_URI, null);
    }
}