package com.ashberrysoft.leadertask.modern.changer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CompletedTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskFootstepHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSeriesHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;

public class TaskStatusChanger extends BaseTaskChanger {

    // BASE
    private final boolean mStatusToCompleted;
    private final boolean mStatusFromCompleted;

    // VALUE's
    private final CompletedCache mCompletedCache;
    private final TaskFootstepHelper mFootstepHelper;
    private final String mCurrentUser;
    private final Map<String, Boolean> mCompletedParents;
    private final Context mContext;

    private List<LTask> mAllFamilyTasks;

    public TaskStatusChanger(Context context, LTask taskNew, LTask taskOld) {
        super(context, taskNew, taskOld, null);
        mContext = context;
        mStatusToCompleted = TaskHelper.isCompleted(getNew().getStatus(),//
                getSettings().getUserName(), getNew().getEmailCustomer());
        mStatusFromCompleted = getOld() != null && TaskHelper.isCompleted(getOld().getStatus(),//
                getSettings().getUserName(), getOld().getEmailCustomer());

        mCompletedCache = CompletedCache.getInstance(getContext());
        mFootstepHelper = new TaskFootstepHelper(getContext());
        mCurrentUser = LTSettings.getInstance().getUserName();
        mCompletedParents = new HashMap<>();
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        return true;// у всех задач есть статусы
    }

    @Override
    public boolean removeLinks(LTask task) {
        return mStatusToCompleted != mStatusFromCompleted; // если статусы не равны то они будут меняться
    }

    private void hideNotify(LTask task) {
        int status = task.getStatus();
        if (status ==  1 || status == 7 || status == 5 || status == 8) {
            TaskNotifyHelper.getInstance(mContext.getApplicationContext()).cancelNotify(task);
        }
    }

    @Override
    public boolean increaseLinksCounter() {
        if (getOld() == null || mStatusToCompleted == mStatusFromCompleted) {
            if (getOld() != null)
            {
                mAllFamilyTasks = getAllTaskFamilyChilds(getNew()); // взять все нижние задачи и их задачи
                if (getOld().getStatus() != getNew().getStatus() && getOld().getStatus() == Status.NOTE.getStatusCode()) {
                    mFootstepHelper.changeTotalAndApply(mAllFamilyTasks, false, true);// добавляем к счетчикам
                    mFootstepHelper.changeTotalNotes(mAllFamilyTasks, false, false);
                } else {
                    if (getOld().getStatus() != getNew().getStatus() && getNew().getStatus() == Status.NOTE.getStatusCode()) {
                        mFootstepHelper.changeTotalAndApply(mAllFamilyTasks, false, false);// отнимаем у счетчиков
                        mFootstepHelper.changeTotalNotes(mAllFamilyTasks, false, true);
                    }
                }
                LTCalendarView.clearCalendarData(getContext(), getOld());
                LTCalendarView.clearCalendarData(getContext(), mAllFamilyTasks.toArray(new LTask[mAllFamilyTasks.size()]));
            }
            return false;// если новая задача или если равны то ничего не делать
        }


        mAllFamilyTasks = getAllTaskFamilyChilds(getNew()); // взять все нижние задачи и их задачи

        LTCalendarView.clearCalendarData(getContext(), getOld());
        LTCalendarView.clearCalendarData(getContext(), mAllFamilyTasks.toArray(new LTask[mAllFamilyTasks.size()]));

        if (mStatusToCompleted) {// статус будет меняться на завершенный
            final List<ContentValues> cvs = new ArrayList<>(mAllFamilyTasks.size());
            CompletedTask completedTask;
            LTask task;


            if (getNew().getSeriesType() != TaskSeriesHelper.SeriesType.NONE.ordinal() && getNew().getEmailCustomer().equals(LTSettings.getInstance().getUserName())) {
                // если повторяющуяся
                new TaskSeriesHelper(getContext(), getNew()).run();
            } else {
                // если обычная
                for (Iterator<LTask> iterator = mAllFamilyTasks.iterator(); iterator.hasNext();) {
                    task = iterator.next();

                    if (mCompletedCache.find(task.getIdTask()) != null) {
                        iterator.remove();

                    } else {
                        completedTask = new CompletedTask(task, mCurrentUser);
                        completedTask.setParentCompleted(true);

                        mCompletedCache.updateCache(completedTask);// добавляем всех детей в завершенные задачи
                        cvs.add(completedTask.getContentValues(null));
                    }
                }
            }



            if (mAllFamilyTasks.size() > 0 && getOld().getStatus() != Status.NOTE.getStatusCode()) {
                mFootstepHelper.changeTotalAndApply(mAllFamilyTasks, false, false);// отнимаем у счетчиков
            }
            if (mAllFamilyTasks.size() > 0 && getOld().getStatus() == Status.NOTE.getStatusCode()) {
                mFootstepHelper.changeTotalNotes(mAllFamilyTasks, false, false);
            }
            if (cvs.size() > 0) {
                getContext().getContentResolver().bulkInsert(CompletedTaskContract.CONTENT_URI, Utils.getArray(cvs));
            }

            hideNotify(getNew());
        } else {
            if (getNew().getUIDParent() != null) {// если есть родитель
                if (mCompletedCache.find(getNew().getUIDParent()) != null) {// и родитель завершен
                    return false;// то ничего не меняется
                }
                mCompletedParents.put(getNew().getUIDParent(), false);// иначе родитель не завершен
            }
            mCompletedParents.put(getNew().getUid(), false);// ну и сама изменяемая задача не завершена

            final List<Integer> ids = new ArrayList<>(mAllFamilyTasks.size());
            boolean completedTask;
            LTask task;

            for (Iterator<LTask> iterator = mAllFamilyTasks.iterator(); iterator.hasNext();) {
                task = iterator.next();

                completedTask = isParentCompleted(task.getUIDParent())// если родитель завершен то задача завершена
                        || TaskHelper.isCompleted(task.getStatus(), mCurrentUser, task.getEmailCustomer());
                mCompletedParents.put(task.getUid(), completedTask);

                if (completedTask) {
                    iterator.remove();

                } else {// если задача не завершена
                    mCompletedCache.remove(task.getIdTask());// то убираем из завершенных
                    ids.add(task.getIdTask());
                }
            }

            if (mAllFamilyTasks.size() > 0 && getNew().getStatus() != Status.NOTE.getStatusCode()) {
                mFootstepHelper.changeTotalAndApply(mAllFamilyTasks, false, true);// добавляем к счетчикам
            }
            if (mAllFamilyTasks.size() > 0 && getNew().getStatus() == Status.NOTE.getStatusCode()) {
                mFootstepHelper.changeTotalNotes(mAllFamilyTasks, false, true);
            }
            if (ids.size() > 0) {
                clearSb();
                getContext().getContentResolver().delete(CompletedTaskContract.CONTENT_URI, SelectionKeeper.in(getSb(), CompletedTaskContract._ID, ids), null);
            }
        }

        return true;
    }

    private boolean isParentCompleted(String uidParent) {
        if (uidParent == null) {
            return false;
        }

        Boolean parentCompleted = mCompletedParents.get(uidParent);
        if (parentCompleted != null) {
            return parentCompleted;
        }

        for (LTask task : mAllFamilyTasks) {
            if (uidParent.equals(task.getUid())) {
                parentCompleted = TaskHelper.isCompleted(task.getStatus(), mCurrentUser, task.getEmailCustomer());
                break;
            }
        }

        if (parentCompleted == null) {
            parentCompleted = false;
        }
        mCompletedParents.put(uidParent, parentCompleted);

        return parentCompleted;
    }

    @Override
    public boolean createRelatedLinks() {
        return false;
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return false;
    }

    @Override
    public void notifyChanges() {}
}