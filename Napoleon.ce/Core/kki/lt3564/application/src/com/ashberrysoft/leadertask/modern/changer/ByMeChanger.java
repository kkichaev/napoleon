package com.ashberrysoft.leadertask.modern.changer;

import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeTotalLinkContract;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeLink;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.utils.Utils;

public class ByMeChanger extends BaseTaskChanger {

    public ByMeChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && Utils.equals(taskNew.getEmailCustomer(), taskOld.getEmailCustomer()) && Utils.equals(taskNew.getEmailPerformer(), taskOld.getEmailPerformer());
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        final boolean customer = getSettings().getUserName().equals(task.getEmailCustomer());
        final boolean performer = getSettings().getUserName().equals(task.getEmailPerformer());

        return customer && !performer;
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String uid = task.getEmailPerformer();

        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(ByMeTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(ByMeLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String uid = task.getEmailPerformer();

        /** уменьшаем счетчики связи в соответствии с задачей */
        if (totalLinkExists(ByMeTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(ByMeTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final ByMeTotalLink totalLink = new ByMeTotalLink();
            totalLink.setUid(uid);

            addOperation(createNewTotalLink(totalLink, task));
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        final String uid = getNew().getEmailPerformer();

        /** ищем может ли какая-то задача стать на место этой */
        clearSb();
        final TaskSelectionBuilder sb = new TaskSelectionBuilder(getSb());
        sb.getTaskEqualsCustomerByMe(getSettings().getUserName());

        /** находим задачи относящиеся к связи */
        final List<VerticalDepthTask> verticals = getTasksForLink(sb.build());

        /** связываем найденные задачи с uid */
        final ByMeLink link = new ByMeLink();
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
        getContext().getContentResolver().notifyChange(ByMeTotalLinkContract.CONTENT_URI, null);
    }
}