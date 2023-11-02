package com.ashberrysoft.leadertask.modern.changer;

import java.util.List;
import java.util.UUID;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryLink;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.utils.Utils;

public class CategoriesChanger extends BaseTaskChanger {

    public CategoriesChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && Utils.equals(taskNew.getCategories(), taskOld.getCategories());
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return !TextUtils.isEmpty(task.getCategories());
    }

    @Override
    public boolean removeLinks(LTask task) {
        final String[] uids = TaskHelper.getCategoriesFromString(task.getCategories());

        for (String uid : uids) {
            /** уменьшаем счетчики связи в соответствии с задачей */
            addRawUpdate(updateTotalLinkCounter(CategoryTotalLinkContract.TABLE_NAME, uid, task, false));

            /** удаляем связь задачи с uid */
            addOperation(ContentProviderOperation.newDelete(CategoryLinkContract.CONTENT_URI).//
                    withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));
        }

        return true;
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        final String[] uids = TaskHelper.getCategoriesFromString(task.getCategories());

        for (String uid : uids) {
            /** уменьшаем счетчики связи в соответствии с задачей */
            if (totalLinkExists(CategoryTotalLinkContract.CONTENT_URI, uid)) {
                /** обновляем если связь существует */
                addRawUpdate(updateTotalLinkCounter(CategoryTotalLinkContract.TABLE_NAME, uid, task, true));

            } else {
                final Category category;
                final int order;
                try {
                    category = DbHelper.getInstance(getContext()).getCategoryByUUId(UUID.fromString(uid));
                    if (category == null) {
                        return false;
                    }

                    final CategoryTotalLink last = TaskHelper.getFirstInOrder(getContext(),//
                            CategoryTotalLink.class, null, CategoryTotalLinkContract.Orders, false);
                    order = last == null ? 1 : last.getOrder() + 1;

                } catch (Exception e) {
                    Utils.toLog(e);
                    return false;
                }

                final CategoryTotalLink totalLink = new CategoryTotalLink();
                totalLink.setUid(uid);
                totalLink.setBelongCurrentUser(true);
                totalLink.setOrder(order);
                totalLink.setName(category.getName());
                totalLink.setVisible(true);
                totalLink.setShowed(category.isShow());
                totalLink.setOpened(category.isExpanded());
                totalLink.setName(category.getName());
                totalLink.setShared(false);

                addOperation(createNewTotalLink(totalLink, task));
            }
        }

        return true;
    }

    @Override
    public boolean createRelatedLinks() {
        final LTask task = getNew();
        final String[] uids = TaskHelper.getCategoriesFromString(task.getCategories());

        for (String uid : uids) {
            /** ищем может ли какая-то задача стать на место этой */
            clearSb();
            SelectionKeeper.like(getSb(), LTaskContract.Categories, uid);

            /** находим задачи относящиеся к связи */
            final List<VerticalDepthTask> verticals = getTasksForLink(toString());

            /** связываем найденные задачи с uid */
            final CategoryLink link = new CategoryLink();
            for (VerticalDepthTask vertical : verticals) {
                link.setUid(uid);
                link.setTaskId(vertical.getTask().getIdTask());
                link.setReaded(vertical.getTask().getReaded());
                link.setStatus(vertical.getTask().getStatus());

                addOperation(Utils.getIncertOperation(link));
            }
        }
        return true;
    }

    @Override
    public void notifyChanges() {
        getContext().getContentResolver().notifyChange(CategoryTotalLinkContract.CONTENT_URI, null);
    }
}