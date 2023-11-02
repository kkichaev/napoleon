package com.ashberrysoft.leadertask.content_providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.CalendarData;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.SimpleNotify;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.DeleteUid;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SendUid;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SetBlocking;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.TaskNotify;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.VerticalDepthTask;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeLink;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryLink;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.FocusLink;
import com.ashberrysoft.leadertask.modern.domains.link.FocusTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ForMeLink;
import com.ashberrysoft.leadertask.modern.domains.link.ForMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.InboxLink;
import com.ashberrysoft.leadertask.modern.domains.link.InboxTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.InworkLink;
import com.ashberrysoft.leadertask.modern.domains.link.InworkTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.OverdueLink;
import com.ashberrysoft.leadertask.modern.domains.link.OverdueTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ReadyLink;
import com.ashberrysoft.leadertask.modern.domains.link.ReadyTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.TaskLink;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.UnreadLink;
import com.ashberrysoft.leadertask.modern.domains.link.UnreadTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MatcherPattern;
import com.tojc.ormlite.android.framework.MimeTypeVnd.SubType;
import com.tojc.ormlite.android.framework.OperationParameters.InsertParameters;
import com.tojc.ormlite.android.framework.Parameter;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class LeaderTaskProvider extends OrmLiteSimpleContentProvider<DbHelper> {

    private static final Class<?>[] CLASSES = { TaskFile.class, CalendarData.class, SyncInfo.class, Task.class, SimpleNotify.class, UidToDelete.class,
            Emp.class, Employee.class,
            // new
            LTask.class, DeleteUid.class, SendUid.class, CompletedTask.class, VerticalDepthTask.class, SetBlocking.class, TaskNotify.class,
            // link
            TaskLink.class, TaskTotalLink.class, CalendarLink.class, CalendarTotalLink.class, InboxLink.class, InboxTotalLink.class, ByMeLink.class,
            ByMeTotalLink.class, ForMeLink.class, ForMeTotalLink.class, ProjectLink.class, ProjectTotalLink.class, CategoryLink.class, CategoryTotalLink.class,
            UnreadLink.class, UnreadTotalLink.class, FocusLink.class, FocusTotalLink.class, Contact.class, ContactFile.class, ReadyLink.class, ReadyTotalLink.class, InworkLink.class, InworkTotalLink.class,
            OverdueLink.class, OverdueTotalLink.class, ColorLink.class, ColorTotalLink.class, EmpLink.class, EmpTotalLink.class
    //
    };

    @Override
    protected Class<DbHelper> getHelperClass() {
        return DbHelper.class;
    }

    @Override
    public boolean onCreate() {
        final MatcherController matcher = new MatcherController();
        int count = 1;

        for (Class<?> cls : CLASSES) {
            matcher.add(cls, SubType.DIRECTORY, SharedStrings.EMPTY, count++);
            matcher.add(cls, SubType.ITEM, SharedStrings.DIEZ, count++);
        }

        setMatcherController(matcher);
        return true;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int result = 0;

        if (!getController().hasPreinitialized()) {
            throw new IllegalStateException("Controller has not been initialized.");
        }

        final int patternCode = getController().getUriMatcher().match(uri);
        final MatcherPattern pattern = getController().findMatcherPattern(patternCode);
        if (pattern == null) {
            throw new IllegalArgumentException("unknown uri : " + uri.toString());
        }

        final SQLiteDatabase db = this.getHelper().getWritableDatabase();
        db.beginTransaction();

        try {
            for (ContentValues value : values) {

                final Uri resultBulkInsert = incertForBulkIncert(this.getHelper(), db, pattern, new Parameter(uri, value));
                if (resultBulkInsert != null) {
                    result++;
                }
            }
            db.setTransactionSuccessful();

            this.getContext().getContentResolver().notifyChange(uri, null);

        } finally {
            db.endTransaction();
        }

        return result;
    }

    private Uri incertForBulkIncert(DbHelper helper, SQLiteDatabase db, MatcherPattern target, InsertParameters parameter) {
        final long id = db.insert(target.getTableInfo().getName(), null, parameter.getValues());
        return ContentUris.withAppendedId(target.getContentUriPattern(), id);
    }

    @Override
    public Uri onInsert(DbHelper helper, SQLiteDatabase db, MatcherPattern target, InsertParameters parameter) {
        Uri result = null;
        try {
            long id = db.insert(target.getTableInfo().getName(), null, parameter.getValues());
            if (id >= 0) {
                result = ContentUris.withAppendedId(target.getContentUriPattern(), id);

            } else {
                final Uri uri = target.getTableInfo().getDefaultContentUriInfo().getContentUri();

                if (ProjectTotalLinkContract.CONTENT_URI.equals(uri) || CategoryTotalLinkContract.CONTENT_URI.equals(uri)) {
                    // Проверка нужна потому что возможна вставка дублей
                    return uri;

                } else {
                    throw new SQLException("Failed to insert row into : " + parameter.getUri().toString());
                }
            }
        } catch (Exception e) {

        }
        return result;
    }
}