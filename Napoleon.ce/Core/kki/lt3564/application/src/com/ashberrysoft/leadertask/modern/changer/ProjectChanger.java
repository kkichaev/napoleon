package com.ashberrysoft.leadertask.modern.changer;

import java.util.List;
import java.util.UUID;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.utils.Utils;

public class ProjectChanger extends BaseTaskChanger {

    public ProjectChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && Utils.equals(taskNew.getUidProject(), taskOld.getUidProject());
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return !TextUtils.isEmpty(task.getUidProject());
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String uid = task.getUidProject();

        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(ProjectTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(ProjectLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String uid = task.getUidProject();

        /** уменьшаем счетчики связи в соответствии с задачей */
        if (totalLinkExists(ProjectTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(ProjectTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final Project project;
            final int order;
            try {
                project = DbHelper.getInstance(getContext()).getProjectByUUId(UUID.fromString(uid));
                if (project == null) {
                    return false;
                }

                final ProjectTotalLink last = TaskHelper.getFirstInOrder(getContext(),//
                        ProjectTotalLink.class, null, ProjectTotalLinkContract.Orders, false);
                order = last == null ? 1 : last.getOrder() + 1;

            } catch (Exception e) {
                Utils.toLog(e);
                return false;
            }

            final ProjectTotalLink totalLink = new ProjectTotalLink();
            totalLink.setUid(uid);
            totalLink.setBelongCurrentUser(true);
            totalLink.setOrder(order);
            totalLink.setVisible(true);
            totalLink.setShowed(project.isShow());
            totalLink.setOpened(project.isExpanded());
            totalLink.setName(project.getName());
            totalLink.setShared(project.getSharedUsers()!=null);

            addOperation(createNewTotalLink(totalLink, task));
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        final String uid = getNew().getUidProject();

        /** ищем может ли какая-то задача стать на место этой */
        clearSb();
        SelectionKeeper.eq(getSb(), LTaskContract.UidProject, uid);

        /** находим задачи относящиеся к связи */
        final List<VerticalDepthTask> verticals = getTasksForLink(toString());

        /** связываем найденные задачи с uid */
        final ProjectLink link = new ProjectLink();
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
        getContext().getContentResolver().notifyChange(ProjectTotalLinkContract.CONTENT_URI, null);
    }
}