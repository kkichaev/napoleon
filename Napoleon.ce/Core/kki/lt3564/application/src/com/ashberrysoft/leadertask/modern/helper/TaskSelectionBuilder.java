package com.ashberrysoft.leadertask.modern.helper;

import java.util.Calendar;

import android.text.TextUtils;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.UnreadLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CompletedTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.InboxLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.TaskTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.VerticalDepthTaskContract;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import static com.ashberrysoft.leadertask.R.id.date;
import static com.ashberrysoft.leadertask.domains.ordinary.Marker.DEFAULT_MARKER_STRING;

public final class TaskSelectionBuilder implements SharedStrings {

    private static final String PRE1 = "t1";
    private static final String PRE2 = "t2";

    // BASE
    private final LTSettings mSettings;

    // VALUE
    private final StringBuilder mSb;

    public TaskSelectionBuilder(StringBuilder sb) {
        mSettings = LTSettings.getInstance();
        mSb = sb != null ? sb : new StringBuilder();
    }

    public TaskSelectionBuilder() {
        this(null);
    }

    public void clear() {
        Utils.clearStringBuilder(mSb);
    }

    @Override
    public String toString() {
        return build();
    }

    public String build() {
        return getSelection(mSb);
    }

    private static String getSelection(StringBuilder sb) {
        final String selection = sb.toString();
        // Utils.toLog(selection);

        return selection;
    }

    public TaskSelectionBuilder getCalendarTotalLinksFromTo(long from, long to) {
        dateIsDate(CalendarTotalLinkContract.Uid, MathSymb.MORE_EQ, from);
        and();
        dateIsDate(CalendarTotalLinkContract.Uid, MathSymb.LESS_EQ, to);

        return this;
    }

    public TaskSelectionBuilder getTasksCreatedFromTo(long from, long to) {
        dateIsDate(LTaskContract.TermBegin, MathSymb.MORE_EQ, from);
        and();
        dateIsDate(LTaskContract.TermBegin, MathSymb.LESS_EQ, to);
        and();
        statusIs(false, TaskStatus.NOTE);
        and();
        currentUserIs(true, LTaskContract.EmailPerformer);

        return this;
    }

    public TaskSelectionBuilder getTaskEqualsCustomerAndPerformer(String customer, String performer) {
        columnEquals(LTaskContract.EmailCustomer, customer);
        and();
        columnEquals(LTaskContract.EmailPerformer, performer);

        return this;
    }

    public TaskSelectionBuilder getTaskEqualsCustomer(String customer) {
        columnEquals(LTaskContract.EmailCustomer, customer);
        return this;
    }

    public TaskSelectionBuilder getTaskEqualsPerformerForMe(String customer) {
        columnNotEquals(LTaskContract.EmailCustomer, customer);
        and();
        columnEquals(LTaskContract.EmailPerformer, customer);

        return this;
    }

    public TaskSelectionBuilder getTaskEqualsCustomerByMe(String customer) {
        columnEquals(LTaskContract.EmailCustomer, customer);
        and();
        columnNotEquals(LTaskContract.EmailPerformer, customer);

        return this;
    }

    public TaskSelectionBuilder getTaskEqualsCustomerEmp(String performer) {
        columnEquals(LTaskContract.EmailPerformer, performer);

        return this;
    }

    public TaskSelectionBuilder getTasksFromVertical(int vertical, String selection) {
        {
            braceOpen();
            mSb.append(VerticalDepthTaskContract._ID);
            in(true);
            {
                braceOpen();

                select();
                secCol(PRE1, VerticalDepthTaskContract._ID);
                fromSecTable(VerticalDepthTaskContract.TABLE_NAME, PRE1);
                where();
                columnEquals(PRE1, VerticalDepthTaskContract.Vertical, vertical);

                braceClose();
            }
            braceClose();
        }
        if (!TextUtils.isEmpty(selection)) {
            and();
            mSb.append(selection);
        }

        return this;
    }

    public TaskSelectionBuilder getTasksFromVertical(String taskLinkTableName, String taskLinkUid, int vertical) {
        mSb.append(VerticalDepthTaskContract._ID);
        in(true);
        {
            braceOpen();

            select();
            secCol(PRE2, TaskLinkContract.TaskId);
            fromSecTable(taskLinkTableName, PRE2);
            where();
            columnEquals(PRE2, TaskLinkContract.Uid, taskLinkUid);
            and();
            secCol(PRE2, TaskLinkContract.TaskId);
            in(true);
            {
                braceOpen();

                select();
                secCol(PRE1, VerticalDepthTaskContract._ID);
                fromSecTable(VerticalDepthTaskContract.TABLE_NAME, PRE1);
                where();
                columnEquals(PRE1, VerticalDepthTaskContract.Vertical, vertical);

                braceClose();
            }
            braceClose();
        }
        return this;
    }

    public TaskSelectionBuilder getParentId(String childId) {
        mSb.append(LTaskContract.Uid);
        equals(true);
        {
            braceOpen();

            select();
            secCol(PRE1, LTaskContract.UIDParent);
            fromSecTable(LTaskContract.TABLE_NAME, PRE1);
            where();
            secCol(PRE1, LTaskContract._ID);
            equals(true);
            mSb.append(childId);

            braceClose();
        }

        return this;
    }

    public TaskSelectionBuilder getTasksWithTerm(long date) {
        braceOpen();
        columnIsNull(LTaskContract.TermBegin, false);
        and();
        columnMore(LTaskContract.TermBegin, true, date);
        braceClose();
        or();
        braceOpen();
        columnIsNull(LTaskContract.TermBeginCustomer, false);
        and();
        columnMore(LTaskContract.TermBeginCustomer, true, date);
        braceClose();

        return this;
    }

    public TaskSelectionBuilder getTasksWithPlan() {
        columnIsNull(LTaskContract.Plan, false);
        and();
        statusIs(true, TaskStatus.IN_WORK);
        and();
        currentUserIs(true, LTaskContract.EmailPerformer);

        return this;
    }

    public TaskSelectionBuilder getInboxLinkTasks() {
        selectTaskIdFromManyMany(InboxLinkContract.TABLE_NAME, null);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getUnreadLinkTasks() {
        selectTaskIdFromManyMany(UnreadLinkContract.TABLE_NAME, null);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getFocusLinkTasks() {
        selectTaskIdFromManyMany(LionMetaData.FocusLinkContract.TABLE_NAME, null);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getEmailsTasks(){
        mSb.append("emails like '");
        mSb.append(mSettings.getUserName());
        mSb.append("' ");
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getReadyLinkTasks() {
        selectTaskIdFromManyMany(LionMetaData.ReadyLinkContract.TABLE_NAME, null);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getInworkLinkTasks() {
        selectTaskIdFromManyMany(LionMetaData.InworkLinkContract.TABLE_NAME, null);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getOverdueLinkTasks() {
        selectTaskIdFromManyMany(LionMetaData.OverdueLinkContract.TABLE_NAME, null);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getColorLinkTasks() {
        selectTaskIdFromManyMany(LionMetaData.ColorLinkContract.TABLE_NAME, null);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getProjectTasks(String projectId) {
        selectTaskIdFromManyMany(ProjectLinkContract.TABLE_NAME, projectId);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getCategoryTasks(String categoryId) {
        selectTaskIdFromManyMany(CategoryLinkContract.TABLE_NAME, categoryId);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getColorTasks(String markerId) {
        selectTaskIdFromManyMany(LionMetaData.ColorLinkContract.TABLE_NAME, markerId);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getByMeTasks(String user) {
        selectTaskIdFromManyMany(ByMeLinkContract.TABLE_NAME, user);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getForMeTasks(String user) {
        //selectTaskIdFromManyMany(ForMeLinkContract.TABLE_NAME, user);
        mSb.append("LionTask.uid NOT IN ( SELECT t0.uid FROM LionTask t0  WHERE t0.uidparent  IN ( SELECT t1.uidparent FROM LionTask t1 WHERE t1.emailcustomer='"+user+"' AND t1.emailperformer='"+LTSettings.getInstance().getUserName()+"' AND t1.uidparent IN ( SELECT t2.uid FROM LionTask t2 WHERE t2.emailcustomer='"+user+"' AND t2.emailperformer='"+LTSettings.getInstance().getUserName()+"' ) ) /*AND (t0._id NOT IN (SELECT t1._id FROM CompletedTask t1))*/ AND t0.emailcustomer='"+user+"'  AND t0.emailperformer='"+LTSettings.getInstance().getUserName()+"' ) AND LionTask.emailcustomer='"+user+"' AND LionTask.emailperformer='"+LTSettings.getInstance().getUserName()+"' ");
        hideCompletedTasks(false);
        return this;
    }

    public TaskSelectionBuilder getForMeTasksAll(String user) {
        //selectTaskIdFromManyMany(ForMeLinkContract.TABLE_NAME, user);
        mSb.append("LionTask.uid NOT IN ( SELECT t0.uid FROM LionTask t0  WHERE t0.uidparent  IN ( SELECT t1.uidparent FROM LionTask t1 WHERE t1.emailcustomer<>'"+user+"' AND t1.emailperformer='"+LTSettings.getInstance().getUserName()+"' AND t1.uidparent IN ( SELECT t2.uid FROM LionTask t2 WHERE t2.emailcustomer<>'"+user+"' AND t2.emailperformer='"+LTSettings.getInstance().getUserName()+"' ) ) /*AND (t0._id NOT IN (SELECT t1._id FROM CompletedTask t1))*/ AND t0.emailcustomer<>'"+user+"'  AND t0.emailperformer='"+LTSettings.getInstance().getUserName()+"' ) AND LionTask.emailcustomer<>'"+user+"' AND LionTask.emailperformer='"+LTSettings.getInstance().getUserName()+"' ");
        hideCompletedTasks(false);
        return this;
    }

    public TaskSelectionBuilder getTaskChilds(int parentId) {
        selectTaskIdFromManyMany(TaskLinkContract.TABLE_NAME, String.valueOf(parentId));
        hideCompletedTasks(false);

        return this;
    }

    private void selectTaskIdFromManyMany(String tableName, String value) {
        mSb.append(LTaskContract._ID);
        in(true);

        {
            braceOpen();

            select();
            secCol(PRE1, LinkContract.TaskId);
            fromSecTable(tableName, PRE1);
            if (value != null) {
                where();
                columnEquals(PRE1, LinkContract.Uid, value);
            }
            braceClose();
        }
    }

    public TaskSelectionBuilder getInboxTasks(String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }
        columnIsNull(LTaskContract.UIDParent, true);
        and();
        dateIsNull(LTaskContract.TermBegin, true);
        and();
        dateIsNull(LTaskContract.TermEnd, true);
        and();
        columnIsNull(LTaskContract.UidProject, true);
        and();
        currentUserIs(true, LTaskContract.EmailCustomer);
        and();
        currentUserIs(true, LTaskContract.EmailPerformer);
        return this;
    }

    public TaskSelectionBuilder getTasksWithUser(boolean byMe, String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }
        columnIsNull(LTaskContract.EmailCustomer, false);
        and();
        columnIsNull(LTaskContract.EmailPerformer, false);
        and();
        currentUserIs(byMe, LTaskContract.EmailCustomer);
        and();
        currentUserIs(!byMe, LTaskContract.EmailPerformer);

        return this;
    }

    public TaskSelectionBuilder getTasksWithPerformer(String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }
        columnIsNull(LTaskContract.EmailCustomer, false);
        //columnEquals(LTaskContract.EmailPerformer, email);

        return this;
    }

    public TaskSelectionBuilder getEmpTasks(String email) {
        selectTaskIdFromManyMany(LionMetaData.EmpLinkContract.TABLE_NAME, email);
        hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getVerticalTasksWithLinks() {
        columnMore(VerticalDepthTaskContract.TABLE_NAME, VerticalDepthTaskContract.Depth, true, 1);
        and();
        exists(true);
        {
            braceOpen();

            select();
            secCol(PRE1, TaskTotalLinkContract._ID);
            fromSecTable(TaskTotalLinkContract.TABLE_NAME, PRE1);
            where();
            secCol(PRE1, TaskTotalLinkContract.Uid);
            equals(true);
            secCol(VerticalDepthTaskContract.TABLE_NAME, VerticalDepthTaskContract._ID);
            and();
            {
                braceOpen();

                columnMore(PRE1, TaskTotalLinkContract.TasksUnreaded, true, 0);
                or();
                columnMore(PRE1, TaskTotalLinkContract.TasksUncompletedUnreaded, true, 0);

                braceClose();
            }

            braceClose();
        }

        return this;
    }

    public TaskSelectionBuilder getRawUpdateTaskLinkUidToId(String id) {
        mSb.append(UPDATE);
        mSb.append(TaskLinkContract.TABLE_NAME);
        mSb.append(SET);
        mSb.append(TaskLinkContract.Uid);
        equals(true);
        {
            braceOpen();

            select();
            secCol(PRE1, LTaskContract._ID);
            fromSecTable(LTaskContract.TABLE_NAME, PRE1);
            where();
            secCol(PRE1, LinkContract.Uid);
            equals(true);
            secCol(TaskLinkContract.TABLE_NAME, TaskLinkContract.Uid);
            if(id != null) {
                and();
                secCol(PRE1, LinkContract.Uid);
                equals(true);
                quotes(id);
            }

            braceClose();
        }

        return this;
    }

    public TaskSelectionBuilder getNewRawUpdateTaskLinkUidToId(String id) {
        mSb.append(UPDATE);
        mSb.append(TaskLinkContract.TABLE_NAME);
        mSb.append(SET);
        mSb.append(TaskLinkContract.Uid);
        equals(true);
        {
            braceOpen();

            select();
            secCol(PRE1, LTaskContract._ID);
            fromSecTable(LTaskContract.TABLE_NAME, PRE1);
            where();
            secCol(PRE1, LinkContract.Uid);
            equals(true);
            secCol(TaskLinkContract.TABLE_NAME, TaskLinkContract.Uid);
            braceClose();
        }

        where();
        secCol(TaskLinkContract.TABLE_NAME, TaskLinkContract.TaskId);
        equals(true);
        quotes(id);

        return this;
    }

    public TaskSelectionBuilder getRawUpdateVerticalParentUidToId() {
        mSb.append(UPDATE);
        mSb.append(VerticalDepthTaskContract.TABLE_NAME);
        mSb.append(SET);
        mSb.append(VerticalDepthTaskContract.ParentId);
        equals(true);
        {
            braceOpen();

            select();
            secCol(PRE1, LTaskContract._ID);
            fromSecTable(LTaskContract.TABLE_NAME, PRE1);
            where();
            secCol(VerticalDepthTaskContract.TABLE_NAME, VerticalDepthTaskContract.ParentId);
            equals(true);
            secCol(PRE1, LTaskContract.Uid);

            braceClose();
        }

        return this;
    }

    public TaskSelectionBuilder getRawUpdateUnknownTaskField(String field, String foreignColumn, String foreignTable, boolean noCase) {
        mSb.append(UPDATE);
        mSb.append(LTaskContract.TABLE_NAME);
        mSb.append(SET);
        mSb.append(field);
        equals(true);
        // quotes(EMPTY);
        mSb.append(NULL);

        where();
        secCol(LTaskContract.TABLE_NAME, field);
        isNull(false);
        and();
        exists(false);
        {
            braceOpen();

            select();
            secCol(PRE1, foreignColumn);
            fromSecTable(foreignTable, PRE1);
            where();
            secCol(LTaskContract.TABLE_NAME, field);
            equals(true);
            secCol(PRE1, foreignColumn);
            if (noCase) {
                mSb.append(COLLATE_NOCASE);
            }

            braceClose();
        }

        return this;
    }

    public TaskSelectionBuilder getTasksWith(String columnName, String uid) {
        columnIsNull(columnName, false);
        if(uid != null) {
            and();
            columnEquals(LTaskContract.Uid, uid);
        }

        return this;
    }

    public TaskSelectionBuilder getTasksWithColorNew(String columnName, String uid) {
        mSb.append(" LionTask.uidmarker IS NOT NULL AND LionTask.uidmarker<>'"+DEFAULT_MARKER_STRING+"' AND  LionTask.uidmarker IN ( SELECT  UPPER (t0.uid) FROM Markers t0 WHERE t0.EmailCreator =='"+LTSettings.getInstance().getUserName()+"' ) " );
        /*columnIsNull(columnName, false);
        and();
        columnNotEquals(columnName, DEFAULT_MARKER_STRING);
        if(uid != null) {
            and();
            columnEquals(LTaskContract.Uid, uid);
        }*/

        return this;
    }

    public TaskSelectionBuilder getTasksWithColor(String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }

        mSb.append(" LionTask.uidmarker IS NOT NULL AND LionTask.uidmarker<>'"+DEFAULT_MARKER_STRING+"' AND  LionTask.uidmarker IN ( SELECT  UPPER (t0.uid) FROM Markers t0 WHERE t0.EmailCreator =='"+LTSettings.getInstance().getUserName()+"' ) " );
        /*columnIsNull(columnName, false);
        and();
        columnNotEquals(columnName, DEFAULT_MARKER_STRING);*/


        return this;
    }

    public TaskSelectionBuilder getTasksWithParam(String columnName, String uid) {
        columnEquals(columnName, uid);
        return this;
    }

    public TaskSelectionBuilder getCompletedTasksWithParent(boolean withParent, String uid) {
        {
            braceOpen();
            columnIsNull(LTaskContract.UIDParent, !withParent);
            if (withParent) {
                and();
            } else {
                or();
            }
            exists(withParent);
            {
                braceOpen();
                select();
                secCol(PRE1, LTaskContract.Uid);
                fromSecTable(LTaskContract.TABLE_NAME, PRE1);
                where();
                secCol(PRE1, LTaskContract.Uid);
                equals(true);
                secCol(LTaskContract.TABLE_NAME, LTaskContract.UIDParent);
                braceClose();
            }
            braceClose();
        }
        and();

        getCompletedTasks(uid);

        return this;
    }

    public TaskSelectionBuilder getOverdueLinkTasks(String uid) {
        final long dayStart;
        final long dayEnd;
        final long date = TimeHelper.currentTimeMillisWithoutTimeZone();
        {
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
            calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

            TimeHelper.roundCalendar(calendar, false);

            calendar.setTimeInMillis(date);

            TimeHelper.roundCalendar(calendar, true);
            dayStart = calendar.getTimeInMillis();

            TimeHelper.roundCalendar(calendar, false);
            dayEnd = calendar.getTimeInMillis();

        }
        if (uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }

        mSb.append("((LionTask.termbegin<>0 AND LionTask.termend<>0) OR (LionTask.TermBeginCustomer<>0 AND LionTask.TermEndCustomer<>0 AND LionTask.emailperformer='"+LTSettings.getInstance().getUserName()+"') ) AND  LionTask.status<>1 AND LionTask.status<>7 AND (" +
                "(LionTask.termend<'"+dayStart+"' AND LionTask.termbegin<'"+dayEnd+"' AND LionTask.termendcustomer<'"+dayStart+"' AND LionTask.termbegincustomer<'"+dayEnd+"' AND (LionTask.emailperformer='"+LTSettings.getInstance().getUserName()+"' OR "+ "LionTask.emailcustomer<>'"+LTSettings.getInstance().getUserName()+"')) "+
        "OR " +
                "(LionTask.termend<'"+dayStart+"' AND LionTask.termendcustomer<'"+dayStart+"' AND LionTask.status<>1 AND LionTask.status<>7 AND (LionTask.emailcustomer='"+LTSettings.getInstance().getUserName()+"' OR (LionTask.status<>5 AND LionTask.status<>8) "+ "OR (LionTask.emailcustomer<>'"+LTSettings.getInstance().getUserName()+"' AND LionTask.emailperformer<>'"+LTSettings.getInstance().getUserName()+"' AND LionTask.uidproject IS NOT NULL  AND LionTask.status=5 AND LionTask.status=8)))) ");
        //braceClose();
        return this;
    }

    public TaskSelectionBuilder getCompletedTasks(String uid) {
        if(uid !=null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }
        statusIs(true, TaskStatus.COMPLETED);
        or();
        statusIs(true, TaskStatus.CANCELLED);
        if(uid !=null) {
            and();
            columnEquals(LTaskContract.Uid, uid);

        }
        or();
        {
            braceOpen();
            currentUserIs(true, LTaskContract.EmailPerformer);
            and();
            currentUserIs(false, LTaskContract.EmailCustomer);
            and();
            {
                braceOpen();
                statusIs(true, TaskStatus.READY);
                or();
                statusIs(true, TaskStatus.REJECTED);
                braceClose();
                if(uid !=null) {
                    and();
                    columnEquals(LTaskContract.Uid, uid);
                }
            }
            braceClose();
        }

        return this;
    }

    public TaskSelectionBuilder getUnreadTasks(String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }

        mSb.append(" readed = '0' ");
        hideCompletedTasks(false);
        return this;
    }

    public TaskSelectionBuilder getFocusTasks(String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }

        mSb.append(" focus = '1' ");
        hideCompletedTasks(false);
        return this;
    }

    public TaskSelectionBuilder getReadyTasks(String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }
        mSb.append(" status=5 AND emailcustomer='"+LTSettings.getInstance().getUserName()+"' ");
        return this;
    }

    public TaskSelectionBuilder getInworkTasks(String uid) {
        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }
        mSb.append(" status=4 AND emailperformer='"+LTSettings.getInstance().getUserName()+"' ");
        return this;
    }

    public TaskSelectionBuilder getUncompletedTasks() {
        statusIs(false, TaskStatus.COMPLETED);
        and();
        statusIs(false, TaskStatus.CANCELLED);

        and();
        {
            braceOpen();
            currentUserIs(true, LTaskContract.EmailCustomer); // создатель
            or();
            {
                braceOpen();
                statusIs(false, TaskStatus.READY);
                and();
                statusIs(false, TaskStatus.REJECTED);
                braceClose();
            }
            or();
            {
                braceOpen();
                currentUserIs(false, LTaskContract.EmailCustomer);
                and();
                currentUserIs(false, LTaskContract.EmailPerformer);
                and();
                columnIsNull(LTaskContract.UidProject, false);
                and();
                statusIs(true, TaskStatus.READY);
                and();
                statusIs(true, TaskStatus.REJECTED);
                braceClose();
            }
            braceClose();
        }
        return this;
    }

    public TaskSelectionBuilder getOrderForTasks() {
        switch (LTSettings.getInstance().getTasksOrder())
        {
            case 1:
                getOrderByTerm();
            break;

            case 2:
                getOrderByMarkers();
            break;

            case 3:
                getOrderByName();
            break;

            case 0:
            default:
                if (LTSettings.getInstance().getUserName().equals("tedorius@yandex.ru")){
                    getOrderByTerm();
                } else {
                    getOrderDefault();
                }
                break;
        }

        return this;
    }

    public void getOrderDefault() {
        mSb.append(SPACE_C);
        mSb.append(LTaskContract.UserOrder);
        nextOrder(LTaskContract.EmailCustomer);
        nextOrder(LTaskContract.OrderNew);
        nextOrder(LTaskContract.Name);
    }

    public void getOrderByTerm() {
        mSb.append(SPACE_C);
        mSb.append(LTaskContract.IsUseTerm+SharedStrings.DESC);
        nextOrder(LTaskContract.TermEnd);
        nextOrder(LTaskContract.IsUseTermCustomer+SharedStrings.DESC);
        nextOrder(LTaskContract.TermEndCustomer);
        nextOrder(LTaskContract.UserOrder);
        nextOrder(LTaskContract.EmailCustomer);
        nextOrder(LTaskContract.OrderNew);
        nextOrder(LTaskContract.Name);
    }

    public void getOrderByMarkers() {
        mSb.append(SPACE_C);
        mSb.append(LTaskContract.MarkerOrder + SharedStrings.DESC);
        nextOrder(LTaskContract.UserOrder);
        nextOrder(LTaskContract.EmailCustomer);
        nextOrder(LTaskContract.OrderNew);
        nextOrder(LTaskContract.Name);
    }

    public void getOrderByName() {
        mSb.append(SPACE_C);
        mSb.append(LTaskContract.Name);
    }

    private void hideCompetedTasksAndReadyNotForMeTasks() {
        //mSb.append(" status<>1 AND status<>7 AND status<>5 AND status<>8 AND ");  // не видно готовых к сдаче
        if (mSettings.isMakeTaskHide()) {
            braceOpen();
            getUncompletedTasks();
            braceClose();
            and();
        }
        braceOpen();

    }

    public TaskSelectionBuilder checkTodayTasks(long date) {
        final boolean beforeOrToday;
        final long dayStart;
        final long dayEnd;
        {
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
            calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

            TimeHelper.roundCalendar(calendar, false);
            beforeOrToday = false;

            calendar.setTimeInMillis(date);

            TimeHelper.roundCalendar(calendar, true);
            dayStart = calendar.getTimeInMillis();

            TimeHelper.roundCalendar(calendar, false);
            dayEnd = calendar.getTimeInMillis();
        }

        hideCompetedTasksAndReadyNotForMeTasks();

        dateIsNull(LTaskContract.TermBegin, false);
        and();
        dateIsNull(LTaskContract.TermEnd, false);
        and();
        {
            braceOpen();
            {
                braceOpen();
                dateIsDate(LTaskContract.TermEnd, MathSymb.MORE_EQ, dayStart);
                and();
                dateIsDate(LTaskContract.TermBegin, MathSymb.LESS_EQ, dayEnd);
                and();
                {
                    braceOpen();
                    currentUserIs(true, LTaskContract.EmailPerformer);
                    or();
                    currentUserIs(false, LTaskContract.EmailCustomer);
                    braceClose();
                }
                braceClose();
            }
            if (beforeOrToday) {
                or();
                {
                    braceOpen();
                    dateIsDate(LTaskContract.TermEnd, MathSymb.LESS, dayStart);
                    and();
                    getUncompletedTasks();
                    braceClose();
                }
            }
            braceClose();
        }
        or();
        {
            braceOpen();
            dateIsNull(LTaskContract.TermBegin, true);
            and();
            dateIsNull(LTaskContract.TermEnd, true);
            and();
            {
                braceOpen();
                currentUserIs(true, LTaskContract.EmailPerformer);
                and();
                dateIsNull(LTaskContract.TermBeginCustomer, false);
                and();
                dateIsNull(LTaskContract.TermEndCustomer, false);
                and();
                {
                    braceOpen();
                    {
                        braceOpen();
                        dateIsDate(LTaskContract.TermEndCustomer, MathSymb.MORE_EQ, dayStart);
                        and();
                        dateIsDate(LTaskContract.TermBeginCustomer, MathSymb.LESS_EQ, dayEnd);
                        braceClose();
                    }
                    if (beforeOrToday) {
                        or();
                        {
                            braceOpen();
                            dateIsDate(LTaskContract.TermEndCustomer, MathSymb.LESS, dayStart);
                            and();
                            getUncompletedTasks();
                            braceClose();
                        }
                    }
                    braceClose();
                }
                braceClose();
            }
            braceClose();
        }
        braceClose();


        return this;
    }

    public TaskSelectionBuilder getCalendarByDay(long date, String uid) {
        if (!LTSettings.getInstance().isOverdueInToday()) {
            final boolean beforeOrToday;
            final long dayStart;
            final long dayEnd;
            {
                final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
                calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

                TimeHelper.roundCalendar(calendar, false);
                beforeOrToday = false;

                calendar.setTimeInMillis(date);

                TimeHelper.roundCalendar(calendar, true);
                dayStart = calendar.getTimeInMillis();

                TimeHelper.roundCalendar(calendar, false);
                dayEnd = calendar.getTimeInMillis();
            }

            if (uid != null) {
                columnEquals(LTaskContract.Uid, uid);
                and();
            }

            hideCompetedTasksAndReadyNotForMeTasks();

            dateIsNull(LTaskContract.TermBegin, false);
            and();
            dateIsNull(LTaskContract.TermEnd, false);
            and();
            {
                braceOpen();
                {
                    braceOpen();
                    dateIsDate(LTaskContract.TermEnd, MathSymb.MORE_EQ, dayStart);
                    and();
                    dateIsDate(LTaskContract.TermBegin, MathSymb.LESS_EQ, dayEnd);
                    and();
                    {
                        braceOpen();
                        currentUserIs(true, LTaskContract.EmailPerformer);
                        or();
                        currentUserIs(false, LTaskContract.EmailCustomer);
                        braceClose();
                    }
                    braceClose();
                }
                if (beforeOrToday) {
                    or();
                    {
                        braceOpen();
                        dateIsDate(LTaskContract.TermEnd, MathSymb.LESS, dayStart);
                        and();
                        getUncompletedTasks();
                        braceClose();
                    }
                }
                braceClose();
            }
            or();
            {
                braceOpen();
                dateIsNull(LTaskContract.TermBegin, true);
                and();
                dateIsNull(LTaskContract.TermEnd, true);
                and();
                {
                    braceOpen();
                    currentUserIs(true, LTaskContract.EmailPerformer);
                    and();
                    dateIsNull(LTaskContract.TermBeginCustomer, false);
                    and();
                    dateIsNull(LTaskContract.TermEndCustomer, false);
                    and();
                    {
                        braceOpen();
                        {
                            braceOpen();
                            dateIsDate(LTaskContract.TermEndCustomer, MathSymb.MORE_EQ, dayStart);
                            and();
                            dateIsDate(LTaskContract.TermBeginCustomer, MathSymb.LESS_EQ, dayEnd);
                            braceClose();
                        }
                        if (beforeOrToday) {
                            or();
                            {
                                braceOpen();
                                dateIsDate(LTaskContract.TermEndCustomer, MathSymb.LESS, dayStart);
                                and();
                                getUncompletedTasks();
                                braceClose();
                            }
                        }
                        braceClose();
                    }
                    braceClose();
                }
                braceClose();
            }
            braceClose();


            return this;
        } else {
            return getCalendarLinkByDay(date, uid);
        }
    }

    public TaskSelectionBuilder getCalendarLinkByDay(long day, String uid) {
        final boolean beforeOrToday;
        final long dayStart;
        final long dayEnd;
        {
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
            calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

            TimeHelper.roundCalendar(calendar, false);
            beforeOrToday = day <= calendar.getTimeInMillis();

            calendar.setTimeInMillis(day);

            TimeHelper.roundCalendar(calendar, true);
            dayStart = calendar.getTimeInMillis();

            TimeHelper.roundCalendar(calendar, false);
            dayEnd = calendar.getTimeInMillis();
        }

        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }

        dateIsNull(LTaskContract.TermBegin, false);
        and();
        dateIsNull(LTaskContract.TermEnd, false);
        and();
        {
            braceOpen();
            {
                braceOpen();
                dateIsDate(LTaskContract.TermEnd, MathSymb.MORE_EQ, dayStart);
                and();
                dateIsDate(LTaskContract.TermBegin, MathSymb.LESS_EQ, dayEnd);
                and();
                {
                    braceOpen();
                    currentUserIs(true, LTaskContract.EmailPerformer);
                    or();
                    currentUserIs(false, LTaskContract.EmailCustomer);
                    braceClose();
                }
                braceClose();
            }
            if (beforeOrToday) {
                or();
                {
                    braceOpen();
                    dateIsDate(LTaskContract.TermEnd, MathSymb.LESS, dayStart);
                    and();
                    getUncompletedTasks();
                    braceClose();
                }
            }
            braceClose();
        }
        or();
        {
            braceOpen();
            dateIsNull(LTaskContract.TermBegin, true);
            and();
            dateIsNull(LTaskContract.TermEnd, true);
            and();
            {
                braceOpen();
                currentUserIs(true, LTaskContract.EmailPerformer);
                and();
                dateIsNull(LTaskContract.TermBeginCustomer, false);
                and();
                dateIsNull(LTaskContract.TermEndCustomer, false);
                and();
                {
                    braceOpen();
                    {
                        braceOpen();
                        dateIsDate(LTaskContract.TermEndCustomer, MathSymb.MORE_EQ, dayStart);
                        and();
                        dateIsDate(LTaskContract.TermBeginCustomer, MathSymb.LESS_EQ, dayEnd);
                        braceClose();
                    }
                    if (beforeOrToday) {
                        or();
                        {
                            braceOpen();
                            dateIsDate(LTaskContract.TermEndCustomer, MathSymb.LESS, dayStart);
                            and();
                            getUncompletedTasks();
                            braceClose();
                        }
                    }
                    braceClose();
                }
                braceClose();
            }
            braceClose();
        }

        return this;
    }

    public TaskSelectionBuilder getCalendarByDay(long date) {
        final String uid = CalendarLink.getStringUidFromDate(date);

        selectTaskIdFromManyMany(CalendarLinkContract.TABLE_NAME, uid);
        hideCompletedTasks(false);

        // {
        // braceOpen();
        // getCalendarLinkByDay(day);
        // braceClose();
        // } TODO:
        // hideCompletedTasks(false);

        return this;
    }

    public TaskSelectionBuilder getCalendarLinkByDayForCalendarDay(long day, String uid) {
        final boolean beforeOrToday;
        final long dayStart;
        final long dayEnd;
        {
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
            calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

            TimeHelper.roundCalendar(calendar, false);
            beforeOrToday = true;

            calendar.setTimeInMillis(day);

            TimeHelper.roundCalendar(calendar, true);
            dayStart = calendar.getTimeInMillis();

            TimeHelper.roundCalendar(calendar, false);
            dayEnd = calendar.getTimeInMillis();
        }

        if(uid != null) {
            columnEquals(LTaskContract.Uid, uid);
            and();
        }

        dateIsNull(LTaskContract.TermBegin, false);
        and();
        dateIsNull(LTaskContract.TermEnd, false);
        and();
        {
            braceOpen();
            {
                braceOpen();
                dateIsDate(LTaskContract.TermEnd, MathSymb.MORE_EQ, dayStart);
                and();
                dateIsDate(LTaskContract.TermBegin, MathSymb.LESS_EQ, dayEnd);
                and();
                {
                    braceOpen();
                    currentUserIs(true, LTaskContract.EmailPerformer);
                    or();
                    currentUserIs(false, LTaskContract.EmailCustomer);
                    or();
                    mSb.append("emails like '");
                    mSb.append(mSettings.getUserName());
                    mSb.append("' ");
                    braceClose();
                }
                braceClose();
            }
            if (beforeOrToday) {
                or();
                {
                    braceOpen();
                    dateIsDate(LTaskContract.TermEnd, MathSymb.LESS, dayStart);
                    and();
                    getUncompletedTasks();
                    braceClose();
                }
            }
            braceClose();
        }
        or();
        {
            braceOpen();
            dateIsNull(LTaskContract.TermBegin, true);
            and();
            dateIsNull(LTaskContract.TermEnd, true);
            and();
            {
                braceOpen();
                braceOpen();
                    currentUserIs(true, LTaskContract.EmailPerformer);
                    or();
                    mSb.append("emails like '");
                    mSb.append(mSettings.getUserName());
                    mSb.append("' ");
                braceClose();
                and();
                dateIsNull(LTaskContract.TermBeginCustomer, false);
                and();
                dateIsNull(LTaskContract.TermEndCustomer, false);
                and();
                {
                    braceOpen();
                    {
                        braceOpen();
                        dateIsDate(LTaskContract.TermEndCustomer, MathSymb.MORE_EQ, dayStart);
                        and();
                        dateIsDate(LTaskContract.TermBeginCustomer, MathSymb.LESS_EQ, dayEnd);
                        braceClose();
                    }
                    if (beforeOrToday) {
                        or();
                        {
                            braceOpen();
                            dateIsDate(LTaskContract.TermEndCustomer, MathSymb.LESS, dayStart);
                            and();
                            getUncompletedTasks();
                            braceClose();
                        }
                    }
                    braceClose();
                }
                braceClose();
            }
            braceClose();
        }

        return this;
    }

    private void hideCompletedTasks(boolean andAfter) {
        if (mSettings.isMakeTaskHide()) {
            if (!andAfter) {
                and();
            }
            braceOpen();
            secCol(LTaskContract.TABLE_NAME, LTaskContract._ID);
            in(false);
            {
                braceOpen();
                select();
                secCol(PRE1, CompletedTaskContract._ID);
                fromSecTable(CompletedTaskContract.TABLE_NAME, PRE1);
                braceClose();
            }
            braceClose();
            if (andAfter) {
                and(); 
            }
        }
    }

    private void in(boolean in) {
        mSb.append(in ? IN : NOT_IN);
    }

    private void fromSecTable(String tableName, String pre) {
        mSb.append(FROM);
        mSb.append(tableName);
        mSb.append(SharedStrings.SPACE_C);
        mSb.append(pre);
    }

    private void secCol(String pre, String columnName) {
        mSb.append(pre);
        mSb.append(DOT_C);
        mSb.append(columnName);
    }

    private void exists(boolean exists) {
        if (!exists) {
            mSb.append(NOT);
        }
        mSb.append(EXISTS);
    }

    private void dateIsNull(String columnName, boolean isNull) {
        mSb.append(columnName);
        equals(isNull);
        mSb.append(0);
    }

    private void dateIsDate(String columnDateName, MathSymb is, long date) {
        mSb.append(columnDateName);
        mSb.append(is);
        mSb.append(date);
    }

    private void nextOrder(String columnName) {
        mSb.append(COMMA_C);
        mSb.append(columnName);
    }

    private void caseWhenCurrentUserThenElse(String columnUserName, boolean equals) {
        mSb.append(CASE_WHEN);
        currentUserIs(equals, columnUserName);
        mSb.append(THEN_ELSE);
    }

    private void caseWhenColumnEqValueThenElse(String columnName, String value, boolean equals) {
        mSb.append(CASE_WHEN);
        mSb.append(columnName);
        equals(equals);
        mSb.append(value);
        mSb.append(THEN_ELSE);
    }

    @SuppressWarnings("unused")
    private void orderBy() {
        mSb.append(ORDER_BY);
    }

    private void where() {
        mSb.append(WHERE);
    }

    private void select() {
        mSb.append(SELECT);
    }

    @SuppressWarnings("unused")
    private void allFrom(String tableName) {
        mSb.append(START_C);
        mSb.append(FROM);
        mSb.append(tableName);
    }

    private void statusIs(boolean equals, TaskStatus status) {
        mSb.append(LTaskContract.Status);
        equals(equals);
        mSb.append(status.getCode());
    }

    private void equals(boolean equals) {
        mSb.append(equals ? EQUALS : NOT_EQUALS);
    }

    private void and() {
        mSb.append(AND);
    }

    private void braceOpen() {
        mSb.append(BRACE_OPEN_C);
    }

    private void braceClose() {
        mSb.append(BRACE_CLOSE_C);
    }

    private void currentUserIs(boolean equals, String columnUserName) {
        mSb.append(columnUserName);
        equals(equals);
        currentUser();
    }

    private void currentUser() {
        quotes(mSettings.getUserName());
    }

    private void quotes(String string) {
        mSb.append(QUOTE_C);
        mSb.append(string);
        mSb.append(QUOTE_C);
    }

    private void or() {
        mSb.append(OR);
    }

    public void columnIsNull(String columnName, boolean isNull) {
        mSb.append(columnName);
        isNull(isNull);
    }

    private void columnEquals(String pre, String columnName, String value) {
        secCol(pre, columnName);
        equals(true);
        quotes(value);
    }

    private void columnEquals(String pre, String columnName, int value) {
        secCol(pre, columnName);
        equals(true);
        mSb.append(value);
    }

    private void columnEquals(String columnName, String value) {
        mSb.append(columnName);
        equals(true);
        quotes(value);
    }

    private void columnNotEquals(String columnName, String value) {
        mSb.append(columnName);
        equals(false);
        quotes(value);
    }

    private void isNull(boolean isNull) {
        mSb.append(isNull ? IS_NULL : IS_NOT_NULL);
    }

    private static enum MathSymb {
        EQ("="), NOT_EQ("<>"), MORE(">"), MORE_EQ(">="), LESS("<"), LESS_EQ("<=");

        final String mMathSymb;

        MathSymb(String mathSymb) {
            mMathSymb = mathSymb;
        }

        @Override
        public String toString() {
            return mMathSymb;
        }
    }

    private void columnMore(String columnName, boolean more, long value) {
        mSb.append(columnName);
        mSb.append(more ? MathSymb.MORE : MathSymb.LESS);
        mSb.append(value);
    }

    private void columnMore(String pre, String columnName, boolean more, long value) {
        mSb.append(pre);
        mSb.append(DOT_C);
        columnMore(columnName, more, value);
    }
}