package com.ashberrysoft.leadertask.modern.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CompletedTaskContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.exception.ExceptionReason;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.mApp;
import static com.ashberrysoft.leadertask.modern.domains.lion.LTask.MY_TASK_USER_ORDER;
import static com.ashberrysoft.leadertask.modern.domains.lion.LTask.MY_TASK_USER_ORDER_DESC;
import static com.ashberrysoft.leadertask.utils.SharedStrings.WHERE;

public class FullTasksResetHelper implements Runnable {

    private static final String ORDER_FOR_LAST_ORDERS = getOrderForLast();

    private static String getOrderForLast() {
        final StringBuilder sb = new StringBuilder();

        SelectionKeeper.order(sb, LTaskContract.Orders, false);
        sb.append(SharedStrings.LIMIT);
        sb.append(SharedStrings.ONE);

        return sb.toString();
    }

    // BASE
    private final Context mContext;
    private final boolean mAfterException;

    // VALUE's
    private final ContentResolver mCr;
    private final StringBuilder mSb;
    private final TaskSelectionBuilder mSelectionBuilder;

    public FullTasksResetHelper(Context context, boolean afterException ) {
        mContext = context.getApplicationContext();
        mAfterException = afterException;

        mCr = mContext.getContentResolver();
        mSb = new StringBuilder();
        mSelectionBuilder = new TaskSelectionBuilder(mSb);

        if (afterException) {
            new Thread(this).start();
        } else {
            run();
        }
    }

    @Override
    public void run() {
        Utils.timeChecker("FullTasksResetHelper");
        try {
            setMaximumOrder();
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            setUserOrder();
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            setMarkerOrder();
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            setIsTaskUseTerm(LTaskContract.TermBegin, LTaskContract.IsUseTerm);
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            setIsTaskUseTerm(LTaskContract.TermBeginCustomer, LTaskContract.IsUseTermCustomer);
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            resetUnknownFields();
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            new VerticalDepthHelper(mContext, null);
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            getCompletedTasks();
        } catch (Exception e) {
            toLog(e);
        }
        //
        try {
            Utils.timeChecker("setReadedTasks");
            setReadedTasks();
        } catch (Exception e) {
            toLog(e);
        } finally {
            Utils.timeChecker("setReadedTasks");
        }
        //
        try {
            new TaskLinkReset(mContext).runAll();
        } catch (Exception e) {
            toLog(e);
        } finally {
            Utils.timeChecker("FullTasksResetHelper");
        }
    }

    public static ContentValues[] contentValuesFromList(Collection<ContentValues> values) {
        return values.toArray(new ContentValues[values.size()]);
    }

    private void setReadedTasks() {
        Cursor c = null;
        String selection = "readed = '0' AND uidproject NOTNULL AND LOWER(uidproject) IN (SELECT projects.UID FROM projects WHERE projects.Quiet = '1') AND emailcustomer != '"+LTSettings.getInstance().getUserName()+"' AND emailperformer != '"+LTSettings.getInstance().getUserName()+"'  AND LionTask.uid NOT IN (SELECT UPPER(taskmessage.TaskUID) FROM taskmessage WHERE taskmessage.mCreator = '"+LTSettings.getInstance().getUserName()+"' ) AND status<>1 AND status<>7 AND (emailcustomer<>'"+LTSettings.getInstance().getUserName()+"' AND emailperformer<>'"+LTSettings.getInstance().getUserName()+"'  OR (status<>5 AND status<>8) OR (emailcustomer<>'"+LTSettings.getInstance().getUserName()+"' AND emailperformer<>'"+LTSettings.getInstance().getUserName()+"' AND uidproject IS NOT NULL AND status=5 AND status=8))";
        try {
            c = mCr.query(LTaskContract.CONTENT_URI, null, selection, null, null);

            if (c.getCount() > 0) {
                final List<ContentValues> values = new ArrayList<>(c.getCount());
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    LTask task = new LTask(c);
                    task.setReaded(true);
                    task.setUsnFieldReaded(task.getUsnFieldReaded() + 1);
                    task.setUsnEntity(0);

                    ContentValues cv = task.getContentValues(null);
                    values.add(cv);
                    android.util.Log.v("Tedorius","непрочитанную сделали прочитанной "+task.getUid());
                }
                final ContentValues[] cvs = contentValuesFromList(values);

                mCr.delete(LTaskContract.CONTENT_URI, selection, null);
                mCr.bulkInsert(LTaskContract.CONTENT_URI, cvs);

                mContext.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
                android.util.Log.v("Tedorius","ПРОЧИТАННЫЕ СОХРАНЕНЫ ");
            }

        } catch (Exception e) {
            android.util.Log.v("Tedorius","ОШИБКА В НЕПРОЧИТАННЫХ");
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }


    private void toLog(Exception e) {
        Utils.toLog(e);
        if (!mAfterException) {
            throw LeaderException.create(ExceptionReason.RESET_LINKS, e);
        }
    }

    private void resetUnknownFields() {
        final SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
        Cursor c = null;

        Utils.clearStringBuilder(mSb);
        try {
            c = db.rawQuery(
                    mSelectionBuilder.getRawUpdateUnknownTaskField(LTaskContract.UIDParent, LTaskContract.Uid, LTaskContract.TABLE_NAME, false).build(), null);
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

        Utils.clearStringBuilder(mSb);
        try {
            c = db.rawQuery(mSelectionBuilder.getRawUpdateUnknownTaskField(LTaskContract.UidProject, Project.FIELD_UID, Project.TABLE_NAME, true).build(), null);
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    private static <V extends CursorFiller> ContentValues[] contentValuesFromCollection(Collection<V> values) {
        final ContentValues[] cvs = new ContentValues[values.size()];
        int count = 0;

        for (V value : values) {
            cvs[count++] = value.getContentValues(null);
        }

        return cvs;
    }

    private void setMaximumOrder() {
        int maximum = 0;

        Cursor c = null;
        try {
            c = mCr.query(LTaskContract.CONTENT_URI, null, null, null, ORDER_FOR_LAST_ORDERS);
            if (c.moveToFirst()) {
                maximum = c.getInt(c.getColumnIndex(LTaskContract.Orders));
            }

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (c != null) {
                c.close();
            }
        }

        LTSettings.getInstance(mContext).setMaximumOrder(maximum);
    }

    private void setUserOrder() {
        // тут проход по бд для определения поля UserOrder
        final SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
        Cursor c = null;
        int myOrder = LTSettings.getInstance().isAddingTasksToTop() ? MY_TASK_USER_ORDER_DESC :  MY_TASK_USER_ORDER;


        Utils.clearStringBuilder(mSb);
        try {
            c = db.rawQuery("UPDATE LionTask SET UserOrder = " +
            "( CASE " +
                "WHEN (emailcustomer NOT IN (SELECT Login FROM emps ) )                 THEN ( "+ LTask.EMAIL_TASK_USER_ORDER+" ) " +
                "WHEN (emailcustomer != '"+LTSettings.getInstance().getUserName()+"' )  THEN (SELECT Orders FROM emps WHERE emps.Login=LionTask.emailcustomer ) " +
                "WHEN (emailcustomer = '"+LTSettings.getInstance().getUserName()+"' )  THEN ( "+ myOrder+" )" +
            " END )" +
             " WHERE UserOrder = 0 OR UserOrder IS NULL", null);
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    private void resetUserOrder() {
        // тут проход по бд для определения поля UserOrder
        final SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
        Cursor c = null;

        Utils.clearStringBuilder(mSb);
        try {
            if (LTSettings.getInstance().isAddingTasksToTop()) {
                c = db.rawQuery("UPDATE LionTask SET UserOrder = '"+MY_TASK_USER_ORDER_DESC+"' WHERE UserOrder = '"+MY_TASK_USER_ORDER+"'", null);
            } else {
                c = db.rawQuery("UPDATE LionTask SET UserOrder = '"+MY_TASK_USER_ORDER+"' WHERE UserOrder = '"+MY_TASK_USER_ORDER_DESC+"'", null);
            }
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    private void setMarkerOrder() {
        // тут проход по бд для определения поля MarkerOrder
        final SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
        Cursor c = null;

        Utils.clearStringBuilder(mSb);
        try {
            c = db.rawQuery("UPDATE LionTask SET MarkerOrder = " +
                    "( CASE  " +
                    "WHEN (LOWER (uidmarker) NOT IN (SELECT UID FROM markers ) OR uidmarker =  'default'  ) THEN ( 0 ) " +
                    "WHEN (uidmarker != 'default' )  THEN (SELECT Orders FROM markers WHERE markers.UID=LOWER (LionTask.uidmarker )) " +
                    "END ) " +
                    "WHERE MarkerOrder = 0 OR MarkerOrder IS NULL", null);
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    private void setIsTaskUseTerm(String termColumnName, String isUseTermColumnName) {
        // тут проход по бд для определения поля IsUseTerm и IsUseTermCustomer
        final SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
        Cursor c = null;

        Utils.clearStringBuilder(mSb);
        try {
            c = db.rawQuery("UPDATE LionTask SET "+isUseTermColumnName+" =  \n" +
                    "                    ( CASE   \n" +
                    "                    WHEN ("+termColumnName+" != null OR "+termColumnName+" !=  0  ) THEN ( "+LTask.MY_TASK_IS_USE_TERM_DEFAULT+" ) \n" +
                    "                    WHEN ("+termColumnName+" = null OR "+termColumnName+" =  0  ) THEN ( "+LTask.MY_TASK_NOT_USE_TERM_DEFAULT+" ) \n" +
                    "                    END ) \n" +
                    "                    WHERE "+isUseTermColumnName+" = 0 OR "+isUseTermColumnName+" IS NULL ", null);
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    private void getCompletedTasks() {
        final Map<String, CompletedTask> tasks = new HashMap<>();

        /** get completed tasks without parents */
        getCompletedTasks(tasks, false);

        /** get completed tasks with parents */
        getCompletedTasks(tasks, true);

        /** get completed tasks on already finded */
        String[] uids;
        {
            final Collection<CompletedTask> values = tasks.values();
            uids = new String[values.size()];
            int count = 0;

            for (CompletedTask value : values) {
                uids[count++] = value.getUid();
            }
        }
        while (uids != null) {
            uids = getUidsOfCompletedTasks(tasks, uids);
        }

        final ContentValues[] cvs = contentValuesFromCollection(tasks.values());

        mCr.delete(CompletedTaskContract.CONTENT_URI, null, null);
        mCr.bulkInsert(CompletedTaskContract.CONTENT_URI, cvs);
        CompletedCache.getInstance(mContext).clear();
        CompletedCache.getInstance(mContext).updateCache(tasks.values());
    }

    private void getCompletedTasks(Map<String, CompletedTask> tasks, boolean withParrent) {
        Utils.clearStringBuilder(mSb);
        Cursor c = null;

        try {
            c = mCr.query(LTaskContract.CONTENT_URI, null,//
                    mSelectionBuilder.getCompletedTasksWithParent(withParrent, null).build(), null, null);

            if (c.getCount() > 0) {
                final int columnId = c.getColumnIndex(LTaskContract._ID);
                final int columnUid = c.getColumnIndex(LTaskContract.Uid);

                CompletedTask task;
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    task = new CompletedTask();
                    task.setId(c.getInt(columnId));
                    task.setUid(c.getString(columnUid));
                    task.setTaskCompleted(true);

                    tasks.put(task.getUid(), task);
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private String[] getUidsOfCompletedTasks(Map<String, CompletedTask> tasks, String[] uids) {
        Utils.clearStringBuilder(mSb);
        Cursor c = null;

        SelectionKeeper.in(mSb, LTaskContract.UIDParent, uids);
        mSb.append(SharedStrings.AND);
        mSelectionBuilder.getUncompletedTasks();

        try {
            c = mCr.query(LTaskContract.CONTENT_URI, null, mSb.toString(), null, null);

            if (c.getCount() == 0) {
                return null;
            }

            final int columnId = c.getColumnIndex(LTaskContract._ID);
            final int columnUid = c.getColumnIndex(LTaskContract.Uid);

            CompletedTask task;
            final String[] newUids = new String[c.getCount()];
            int count = 0;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                task = new CompletedTask();
                task.setId(c.getInt(columnId));
                task.setUid(c.getString(columnUid));
                task.setParentCompleted(true);
                task.setTaskCompleted(false);

                tasks.put(task.getUid(), task);
                newUids[count++] = task.getUid();
            }

            return newUids;

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}