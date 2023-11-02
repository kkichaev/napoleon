package com.ashberrysoft.leadertask.modern.changer;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ColorLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ColorTotalLinkContract;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.ColorLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.List;

public class ColorChanger extends BaseTaskChanger {

    public ColorChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && Utils.equals(taskNew.getUidMarker(), taskOld.getUidMarker());
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return TaskHelper.isColorTask(getContext(), task);
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String uid = task.getUidMarker();

        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(ColorTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(ColorLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String uid = task.getUidMarker().toUpperCase();

        /** уменьшаем счетчики связи в соответствии с задачей */
        if (totalLinkExists(ColorTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(ColorTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final ColorTotalLink totalLink = new ColorTotalLink();
            totalLink.setUid(uid);

            addOperation(createNewTotalLink(totalLink, task));
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        final LTask task = getNew();
        final String uid = task.getUidMarker();

        /** ищем может ли какая-то задача стать на место этой */
        clearSb();
        //final TaskSelectionBuilder sb = new TaskSelectionBuilder(getSb());
        //sb.getTasksWithColor(null);
        LeaderTaskProviderMetaData.SelectionKeeper.eq(getSb(), LionMetaData.LTaskContract.UidMarker, uid);

        /** находим задачи относящиеся к связи */
        final List<VerticalDepthTask> verticals = getTasksForLink(toString());

        /** связываем найденные задачи с uid */
        final ColorLink link = new ColorLink();
        for (VerticalDepthTask vertical : verticals) {
            link.setUid(uid.toUpperCase());
            link.setTaskId(vertical.getTask().getIdTask());
            link.setReaded(vertical.getTask().getReaded());
            link.setStatus(vertical.getTask().getStatus());

            addOperation(Utils.getIncertOperation(link));
        }

        return true;
    }

    @Override
    public void notifyChanges() {
        getContext().getContentResolver().notifyChange(ColorTotalLinkContract.CONTENT_URI, null);
    }
}