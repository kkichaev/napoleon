package com.ashberrysoft.leadertask.modern.changer;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarTotalLinkContract;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskLinkReset;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;

import static com.ashberrysoft.leadertask.R.id.date;

public class CalendarChanger extends BaseTaskChanger {

    public CalendarChanger(Context context, LTask taskNew, LTask taskOld, VerticalDepthTask verticalDepth) {
        super(context, taskNew, taskOld, verticalDepth);
    }

    @Override
    public boolean equalsTasks(LTask taskNew, LTask taskOld) {
        return taskOld != null && //
                Utils.equals(taskNew.getTermBegin(), taskOld.getTermBegin()) && //
                Utils.equals(taskNew.getTermEnd(), taskOld.getTermEnd()) && //
                Utils.equals(taskNew.getEmailCustomer(), taskOld.getEmailCustomer()) && //
                Utils.equals(taskNew.getEmailPerformer(), taskOld.getEmailPerformer());
    }

    @Override
    public boolean possiblyHasLink(LTask task) {
        final long termBegin = task.getTermBegin();
        final long termEnd = task.getTermEnd();
        final long termBeginCustomer = task.getTermBeginCustomer();
        final long termEndCustomer = task.getTermEndCustomer();

        return termBegin != 0 && termEnd != 0 || termBeginCustomer != 0 && termEndCustomer != 0;
    }

    @Override
    public boolean removeLinks(LTask task) {
        LTCalendarView.clearCalendarData(getContext(), task);

        final long today = CalendarLink.getLongUidFromDate(TimeHelper.currentTimeMillisWithoutTimeZone());
        final long datePerformer = CalendarLink.getLongUidFromDate(task.getTermBegin());
        final long dateCustomer = CalendarLink.getLongUidFromDate(task.getTermBeginCustomer());
        final long date = datePerformer !=0 ? datePerformer : dateCustomer;

        if (date < today) {
            if (getSettings().isOverdueInToday()) {
                final List<CalendarTotalLink> totalLinks = getTotalLinks(getContext(), getSb(), date, today);
                for (CalendarTotalLink totalLink : totalLinks) {
                    removeLinks(totalLink.getUid(), task);
                }
            } else {
                removeLinks(String.valueOf(date), task);
            }
        } else {
            removeLinks(String.valueOf(date), task);
        }

        return true;
    }

    private void removeLinks(String uid, LTask task) {
        /** уменьшаем счетчики связи в соответствии с задачей */
        addRawUpdate(updateTotalLinkCounter(CalendarTotalLinkContract.TABLE_NAME, uid, task, false));

        /** удаляем связь задачи с uid */
        addOperation(ContentProviderOperation.newDelete(CalendarLinkContract.CONTENT_URI).//
                withSelection(selectUidAndTaskId(uid, task.getIdTask()), null));
    }

    @Override
    public boolean increaseLinksCounter() {
        final LTask task = getNew();
        LTCalendarView.clearCalendarData(getContext(), task);

        final long today = CalendarLink.getLongUidFromDate(TimeHelper.currentTimeMillisWithoutTimeZone());
        final long datePerformer = CalendarLink.getLongUidFromDate(task.getTermBegin());
        final long dateCustomer = CalendarLink.getLongUidFromDate(task.getTermBeginCustomer());
        final long date = datePerformer !=0 ? datePerformer : dateCustomer;

        if (date < today) {
            if (getSettings().isOverdueInToday()) {
                final List<CalendarTotalLink> totalLinks = getTotalLinks(getContext(), getSb(), date, today);
                for (CalendarTotalLink totalLink : totalLinks) {
                    increaseLinksCounter(totalLink.getUid(), task, false);
                }
            }

        } else {
            increaseLinksCounter(String.valueOf(date), task, true);
        }

        return true;
    }

    private void increaseLinksCounter(String uid, LTask task, boolean createIfNotExists) {
        /** увеличиваем счетчики связи в соответствии с задачей */
        if (!createIfNotExists || totalLinkExists(CalendarTotalLinkContract.CONTENT_URI, uid)) {
            /** обновляем если связь существует */
            addRawUpdate(updateTotalLinkCounter(CalendarTotalLinkContract.TABLE_NAME, uid, task, true));

        } else {
            final CalendarTotalLink totalLink = new CalendarTotalLink();
            totalLink.setUid(uid);

            addOperation(createNewTotalLink(totalLink, task));
        }
    }

    @Override
    public boolean createRelatedLinks() {
        final LTask task = getNew();

        final long today = CalendarLink.getLongUidFromDate(TimeHelper.currentTimeMillisWithoutTimeZone());
        final long datePerformer = CalendarLink.getLongUidFromDate(task.getTermBegin());
        final long dateCustomer = CalendarLink.getLongUidFromDate(task.getTermBeginCustomer());
        final long date = datePerformer !=0 ? datePerformer : dateCustomer;

        if (date > 0 && date < today) {
            if (getSettings().isOverdueInToday()) {
                final List<CalendarTotalLink> totalLinks = getTotalLinks(getContext(), getSb(), date, today);
                for (CalendarTotalLink totalLink : totalLinks) {
                    createRelatedLinks(totalLink.getUid(), totalLink.getUniqueId());
                }
            } else {
                createRelatedLinks(String.valueOf(date), date);
            }
        } else {
            createRelatedLinks(String.valueOf(date), date);
        }

        return true;
    }

    private void createRelatedLinks(String uid, long date) {
        /** ищем может ли какая-то задача стать на место этой */
        clearSb();
        TaskSelectionBuilder sb = new TaskSelectionBuilder(getSb());
        sb.getCalendarByDay(date, null);

        /** находим задачи относящиеся к связи */
        List<VerticalDepthTask> verticals = getTasksForLink(sb.build());

        /** связываем найденные задачи с uid */
        final CalendarLink link = new CalendarLink();
        for (VerticalDepthTask vertical : verticals) {
            link.setUid(uid);
            link.setTaskId(vertical.getTask().getIdTask());
            link.setReaded(vertical.getTask().getReaded());
            link.setStatus(vertical.getTask().getStatus());

            addOperation(Utils.getIncertOperation(link));
        }
    }

    @Override
    public void notifyChanges() {
        getContext().getContentResolver().notifyChange(CalendarTotalLinkContract.CONTENT_URI, null);
    }

    public static List<CalendarTotalLink> getTotalLinks(Context context, StringBuilder sBuilder, long from, long to) {
        Utils.clearStringBuilder(sBuilder);
        final TaskSelectionBuilder sb = new TaskSelectionBuilder(sBuilder);
        sb.getCalendarTotalLinksFromTo(from, to);

        List<CalendarTotalLink> totalLinks = null;
        Cursor c = null;

        try {
            c = context.getContentResolver().query(CalendarTotalLinkContract.CONTENT_URI, null, sb.build(), null, null);
            if (c.getCount() > 0) {
                totalLinks = new ArrayList<>(c.getCount());
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    totalLinks.add(new CalendarTotalLink(c));
                }
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return totalLinks;
    }
}