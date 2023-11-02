package com.ashberrysoft.leadertask.modern.changer;

import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.FocusLink;
import com.ashberrysoft.leadertask.modern.domains.link.FocusTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.utils.Utils;

public class FocusChanger extends BaseTaskChanger {

    public FocusChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null &&
                possiblyHasLink(taskNew) == possiblyHasLink(taskOld) &&
                taskNew.getStatus() == taskOld.getStatus();
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return TaskHelper.isFocusTask(task);
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String uid = "0";

        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(LionMetaData.FocusTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(LionMetaData.FocusLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String uid = "0";

        /** уменьшаем счетчики связи в соответствии с задачей */
        if (totalLinkExists(LionMetaData.FocusTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(LionMetaData.FocusTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final FocusTotalLink totalLink = new FocusTotalLink();
            totalLink.setUid(uid);

            addOperation(createNewTotalLink(totalLink, task));
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        final String uid = "0";

        clearSb();
        final TaskSelectionBuilder sb = new TaskSelectionBuilder(getSb());
        sb.getFocusTasks(null);

        /** находим задачи относящиеся к связи */
        final List<VerticalDepthTask> verticals = getTasksForLink(sb.build());

        /** связываем найденные задачи с uid */
        final FocusLink link = new FocusLink();
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
        getContext().getContentResolver().notifyChange(LionMetaData.FocusTotalLinkContract.CONTENT_URI, null);
    }
}