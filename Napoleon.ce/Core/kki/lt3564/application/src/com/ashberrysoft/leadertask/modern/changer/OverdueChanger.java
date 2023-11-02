package com.ashberrysoft.leadertask.modern.changer;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.OverdueLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.OverdueTotalLinkContract;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.OverdueLink;
import com.ashberrysoft.leadertask.modern.domains.link.OverdueTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.List;

import static com.ashberrysoft.leadertask.R.string.task;

public class OverdueChanger extends BaseTaskChanger {

    public OverdueChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && (possiblyHasLink(taskNew) == possiblyHasLink(taskOld));
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return TaskHelper.isOverdueTask(task);
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String uid = "0";

        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(OverdueTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(OverdueLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String uid = "0";

        /** уменьшаем счетчики связи в соответствии с задачей */
        if (totalLinkExists(OverdueTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(OverdueTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final OverdueTotalLink totalLink = new OverdueTotalLink();
            totalLink.setUid(uid);

            addOperation(createNewTotalLink(totalLink, task));
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        LTask task = getNew();
        final String uid = "0";

        /** ищем может ли какая-то задача стать на место этой */
        clearSb();
        final TaskSelectionBuilder sb = new TaskSelectionBuilder(getSb());
        sb.getOverdueLinkTasks(null);

        /** находим задачи относящиеся к связи */
        final List<VerticalDepthTask> verticals = getTasksForLink(sb.build());

        /** связываем найденные задачи с uid */
        final OverdueLink link = new OverdueLink();
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
        getContext().getContentResolver().notifyChange(OverdueTotalLinkContract.CONTENT_URI, null);
    }
}