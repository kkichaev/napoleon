package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.CalendarData;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.DeletedTask;
import com.ashberrysoft.leadertask.domains.ordinary.DeletedTaskMessage;
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.Email.OrderInstruct;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SimpleNotify;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskCategory;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.fragments.SubtasksListFragment;
import com.ashberrysoft.leadertask.fragments.TasksListFragment;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.cache.TaskMessageCache;
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
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.AlterDbManager;
import com.ashberrysoft.leadertask.utils.CursorySyncLogger;
import com.ashberrysoft.leadertask.utils.SimpleNotifications;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.Utils.TaskUtils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

import static com.ashberrysoft.leadertask.R.drawable.cursor;
import static com.ashberrysoft.leadertask.R.drawable.employee;
import static com.ashberrysoft.leadertask.R.id.comment;
import static com.ashberrysoft.leadertask.R.id.status;

@SuppressWarnings("deprecation")
public class DbHelper extends OrmLiteSqliteOpenHelper {

    public static final int DATABASE_VERSION = 1309251043; // 1309251040; // 1309251039; // 1309251038;  // 1309251037; // 1309251036; // 1309251035; // 1309251034; // 1309251033; // 1309251032; // 1309251031; // 1309251030; //1309251029; //1309251028; //1309251027; // 1309251026; // 1309251025; // 1309251024; //1309251023; // 1309251022; 1309251021; //1309251020; //1309251019; // 1309251018

    public static final String DATABASE_NAME = "leader_task.db";

    private static final Class<?>[] CLASSES = { DeletedTask.class, Project.class, Category.class, Task.class, Marker.class, TaskMessage.class,
            TaskCategory.class, FilterNumberTask.class, DeletedTaskMessage.class, TaskFile.class, CalendarData.class, SyncInfo.class,
            SimpleNotify.class, UidToDelete.class, Emp.class, Employee.class,
            // new
            LTask.class, DeleteUid.class, SendUid.class, CompletedTask.class, VerticalDepthTask.class, SetBlocking.class, TaskNotify.class,
            // link
            TaskLink.class, TaskTotalLink.class, CalendarLink.class, CalendarTotalLink.class, InboxLink.class, InboxTotalLink.class, ByMeLink.class,
            ByMeTotalLink.class, ForMeLink.class, ForMeTotalLink.class, ProjectLink.class, ProjectTotalLink.class, CategoryLink.class, CategoryTotalLink.class,
            UnreadLink.class, UnreadTotalLink.class, FocusLink.class, FocusTotalLink.class,
            //
            //superNew
            ContactsGroup.class, Contact.class, ContactFile.class, ReadyLink.class, ReadyTotalLink.class, InworkLink.class, InworkTotalLink.class, OverdueLink.class, OverdueTotalLink.class,
            ColorLink.class, ColorTotalLink.class, EmpLink.class, EmpTotalLink.class

    };

    // INSTANCE
    private static DbHelper sInstance;

    // VALUE
    private Context mContext;
    private LTSettings mSettings;

    // DAO's
    private Dao<DeletedTask, UUID> mDeletedTaskDao;
    private Dao<Project, UUID> mProjectDao;
    private Dao<ContactsGroup, UUID> mContactsGroupDao;
    private Dao<Contact, UUID> mContactsDao;
    private Dao<LTask, UUID> mLTaskDao;
    private Dao<TaskFile, UUID> mTaskFilesDao;
    private Dao<Emp, UUID> mEmpDao;
    private Dao<Category, UUID> mCategoryDao;
    private Dao<Task, UUID> mTaskDao;
    private Dao<Marker, UUID> mMarkerDao;
    private Dao<TaskMessage, UUID> mTaskMessageDao;
    private Dao<TaskCategory, Integer> mTaskCategoryDao;
    private Dao<FilterNumberTask, Integer> mFilterNumberTaskDao;
    private Dao<DeletedTaskMessage, UUID> mDeletedTaskMessageDao;

    // HOLDER's
    private PreparedQuery<Category> mPqTaskCategories;
    private ArgumentHolder[] mHolderTaskNotCompleted;
    private ArgumentHolder[] mHolderOrderByCustomerEmail;
    private ArgumentHolder[] mHolderGetCategoriesByTask;

    public static DbHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DbHelper.class) {
                if (sInstance == null) {
                    sInstance = OpenHelperManager.getHelper(context, DbHelper.class);
                }
            }
        }
        return sInstance;
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context.getApplicationContext();
        mSettings = LTSettings.getInstance(mContext);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource cs) {
        try {
            for (Class<?> c : CLASSES) {
                TableUtils.createTable(cs, c);
            }
        } catch (SQLException e) {
            Utils.toLog(e);
        }
    }

    public void cleanDatabase() {
        final ConnectionSource cs = getConnectionSource();
        try {
            TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
                public Void call() throws SQLException {
                    for (Class<?> c : CLASSES) {
                        TableUtils.clearTable(cs, c);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            Utils.toLog(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource cs, int oldVersion, int newVersion) {
        try {
            final AlterDbManager alterDbManager = AlterDbManager.newInstance(mContext, database, cs, oldVersion, newVersion);
            if (alterDbManager != null) {
                alterDbManager.runAlteration();
                return;
            }
        } catch (Exception e) {
            Utils.toLog(e);
        }

        try {
            for (Class<?> c : CLASSES) {
                TableUtils.dropTable(cs, c, true);
            }
            onCreate(database);
        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    public Dao<DeletedTask, UUID> getDeletedTaskDao() {
        if (mDeletedTaskDao == null) {
            synchronized (this) {
                if (mDeletedTaskDao == null) {
                    try {
                        mDeletedTaskDao = getDao(DeletedTask.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return mDeletedTaskDao;
    }

    public Dao<DeletedTaskMessage, UUID> getDeletedTaskMessageDao() {
        if (mDeletedTaskMessageDao == null) {
            synchronized (this) {
                if (mDeletedTaskMessageDao == null) {
                    try {
                        mDeletedTaskMessageDao = getDao(DeletedTaskMessage.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mDeletedTaskMessageDao;
    }

    public Dao<Project, UUID> getProjectDao() {
        if (mProjectDao == null) {
            synchronized (this) {
                if (mProjectDao == null) {
                    try {
                        mProjectDao = getDao(Project.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mProjectDao;
    }

    public Dao<ContactsGroup, UUID> getContactsGroupDao() {
        if (mContactsGroupDao == null) {
            synchronized (this) {
                if (mContactsGroupDao == null) {
                    try {
                        mContactsGroupDao = getDao(ContactsGroup.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mContactsGroupDao;
    }

    public Dao<Contact, UUID> getContactDao() {
        if (mContactsDao == null) {
            synchronized (this) {
                if (mContactsDao == null) {
                    try {
                        mContactsDao = getDao(Contact.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mContactsDao;
    }

    public Dao<LTask, UUID> getLTaskDao() {
        if (mLTaskDao == null) {
            synchronized (this) {
                if (mLTaskDao == null) {
                    try {
                        mLTaskDao = getDao(LTask.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mLTaskDao;
    }

    public Dao<TaskFile, UUID> getTaskFiles() {
        if (mTaskFilesDao == null) {
            synchronized (this) {
                if (mTaskFilesDao == null) {
                    try {
                        mTaskFilesDao = getDao(TaskFile.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mTaskFilesDao;
    }

    public Dao<Emp, UUID> getEmpDao() {
        if (mEmpDao == null) {
            synchronized (this) {
                if (mEmpDao == null) {
                    try {
                        mEmpDao = getDao(Emp.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mEmpDao;
    }

    public Dao<Category, UUID> getCategoryDao() {
        if (mCategoryDao == null) {
            synchronized (this) {
                if (mCategoryDao == null) {
                    try {
                        mCategoryDao = getDao(Category.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mCategoryDao;
    }

    public Dao<Task, UUID> getTaskDao() {
        // public Dao<Task, UUID> getDaoForTask() {
        if (mTaskDao == null) {
            synchronized (this) {
                if (mTaskDao == null) {
                    try {
                        mTaskDao = getDao(Task.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return mTaskDao;
    }

    public Task getTaskDao_queryForId(UUID uuid) {
        return getTaskDao_queryForId(getTaskDao(), uuid);
    }

    public static Task getTaskDao_queryForId(Dao<Task, UUID> dao, UUID uuid) {
        try {
            return dao.queryBuilder().where().eq(TaskContract.FIELD_UID, uuid).queryForFirst();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public int getTaskDao_countAll() {
        try {
            return (int) getTaskDao().countOf();
        } catch (Exception e) {
            Utils.toLog(e);
            return 0;
        }
    }

    public List<Task> getTaskDao_queryForIds(List<UUID> uuids) {
        return getTaskDao_queryForIds(getTaskDao(), uuids);
    }

    public static List<Task> getTaskDao_queryForIds(Dao<Task, UUID> dao, List<UUID> uuids) {
        try {
            return dao.queryBuilder().where().in(TaskContract.FIELD_UID, uuids).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return new ArrayList<Task>(0);
        }
    }

    public void updateTaskWithCalendar(Context context, Task task) {

    }

    public void updateTask(Task task) throws SQLException {
        getTaskDao().update(task);
    }

    public Dao<TaskCategory, Integer> getTaskCategoryDao() {
        if (mTaskCategoryDao == null) {
            synchronized (this) {
                if (mTaskCategoryDao == null) {
                    try {
                        mTaskCategoryDao = getDao(TaskCategory.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mTaskCategoryDao;
    }

    public Dao<FilterNumberTask, Integer> getFilterNumberTaskDao() {
        if (mFilterNumberTaskDao == null) {
            synchronized (this) {
                if (mFilterNumberTaskDao == null) {
                    try {
                        mFilterNumberTaskDao = getDao(FilterNumberTask.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mFilterNumberTaskDao;
    }

    public Dao<Marker, UUID> getMarkerDao() {
        if (mMarkerDao == null) {
            synchronized (this) {
                if (mMarkerDao == null) {
                    try {
                        mMarkerDao = getDao(Marker.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mMarkerDao;
    }

    public Dao<TaskMessage, UUID> getTaskMessageDao() {
        if (mTaskMessageDao == null) {
            synchronized (this) {
                if (mTaskMessageDao == null) {
                    try {
                        mTaskMessageDao = getDao(TaskMessage.class);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mTaskMessageDao;
    }

    /**
     * preparing holder for "order by customer" subquery
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    protected ArgumentHolder[] prepareOrderByCustomerHolder(String userName) {
        if (mHolderOrderByCustomerEmail == null)
            // create ArgumentHolder instance for any <?> in
            // "order by customer email" sql subquery
            mHolderOrderByCustomerEmail = new ArgumentHolder[] { new SelectArg(SqlType.STRING, userName) };
        else
            mHolderOrderByCustomerEmail[0].setValue(userName);
        return mHolderOrderByCustomerEmail;
    }

    public List<Project> getProjects(final Context context, boolean updateNumber) throws SQLException, AbstractDataRequestException {
        final String userName = LTSettings.getInstance(context).getUserName();
        final QueryBuilder<Project, UUID> builder = getProjectDao().queryBuilder();
        builder.orderBy(Project.FIELD_ORDER, true);
        builder.orderBy(Project.FIELD_NAME, true);
        builder.selectColumns(Project.FIELD_UID, Project.FIELD_NAME, Project.FIELD_COLLAPSED, Project.FIELD_UID_PARENT, Project.FIELD_ORDER);
        builder.where().eq(Project.FIELD_CREATOR, userName);

        final List<Project> result = builder.query();
        if (updateNumber) {
            for (final Project project : result) {
                if (project != null) {
                    new GetNumberOfTasksInProject(context, project, userName).execute(null);
                }
            }
        }
        return result;
    }

    public List<Project> getAllProjectsForSynchronization() {
        try {
            return getProjectDao().queryBuilder().selectColumns(Project.FIELD_UID, Project.FIELD_USN).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Project> getAllProjectsForPutAll() {
        try {
            return getProjectDao().queryBuilder().where().eq(Project.FIELD_USN, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Category> getAllCategoriesForPutAll() {
        try {
            return getCategoryDao().queryBuilder().where().eq(Category.FIELD_USN, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<ContactsGroup> getAllContactsGroupsForPutAll() {
        try {
            return getContactsGroupDao().queryBuilder().where().eq(ContactsGroup.FIELD_USN, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Contact> getAllContactForPutAll() {
        try {
            return getContactDao().queryBuilder().where().eq(ContactContract.USN_ENTITY, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Marker> getAllMarkersForPutAll() {
        try {
            return getMarkerDao().queryBuilder().where().eq(Marker.FIELD_USN, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<TaskMessage> getAllTaskMessagesForPutAll() {
        try {
            return getTaskMessageDao().queryBuilder().where().eq(TaskMessage.FIELD_USN, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<LTask> getAllLTaskForPutAll() {
        try {
            return getLTaskDao().queryBuilder().where().eq(LTaskContract.UsnEntity, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Emp> getAllEmpsForPutAll() {
        try {
            return getEmpDao().queryBuilder().where().eq(EmpContract.USN_ENTITY, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<TaskFile> getAllTaskFilesForPutAll() {
        try {
            return getTaskFiles().queryBuilder().where().eq(LTaskContract.UsnEntity, "0").query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<ContactsGroup> getAllContactsGroupsForSynchronization() {
        try {
            return getContactsGroupDao().queryBuilder().selectColumns(ContactsGroup.FIELD_UID, ContactsGroup.FIELD_USN).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Contact> getAllContactsForSynchronization() {
        try {
            return getContactDao().queryBuilder().selectColumns(ContactContract.UID, ContactContract.USN_ENTITY).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Contact> getAllContacts() {
        try {
            //
            List <Contact> listContact = new ArrayList<>();
            List <Contact> tempListContact = new ArrayList<>();
                tempListContact = getContactDao().queryBuilder().orderBy(ContactContract.ORDERS, true).orderBy(ContactContract.TITLE, true).query();
                for (Contact contact: tempListContact) {
                    List<ContactsGroup> contactsGroup = new ArrayList<>();
                    if (contact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                        listContact.add(contact);
                    } else {
                        if(contact.getUidGroup() != null) {
                            contactsGroup = getContactsGroupDao().queryBuilder().where().in(ContactsGroup.FIELD_UID, contact.getUidGroup()).query();
                            if (contactsGroup.size() > 0) {
                                if (contactsGroup.get(0).getSharedUsers().indexOf(LTSettings.getInstance().getUserName()) != -1) {
                                    listContact.add(contact);
                                }
                            }
                        }
                    }
                }

            return listContact;
        }
        catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Contact> getAllContactsForView() {
        try {
            //
            List <Contact> listContact = new ArrayList<>();
            List <Contact> tempListContact = new ArrayList<>();
                tempListContact = getContactDao().queryBuilder().orderBy(ContactContract.ORDERS, true).orderBy(ContactContract.TITLE, true).query();
                for (Contact contact: tempListContact) {
                    List<ContactsGroup> contactsGroup = new ArrayList<>();
                    //if (contact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                        listContact.add(contact);
                    /*} else {
                        if(contact.getUidGroup() != null) {
                            contactsGroup = getContactsGroupDao().queryBuilder().where().in(ContactsGroup.FIELD_UID, contact.getUidGroup()).query();
                            if (contactsGroup.size() > 0) {
                                if (contactsGroup.get(0).getSharedUsers().indexOf(LTSettings.getInstance().getUserName()) != -1) {
                                    listContact.add(contact);
                                }
                            }
                        }
                    }*/
                }

            return listContact;
        }
        catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<LTask> getTasksWithSearch(String search) {
        List <LTask> searchTasks = new ArrayList<>();
        List <LTask> searchTasksUncompleted = new ArrayList<>();
        List <LTask> searchTasksCompleted = new ArrayList<>();
        final ContentResolver cr = mContext.getContentResolver();
        final Cursor v = cr.query(LTaskContract.CONTENT_URI, null, null, null, LTaskContract.CreateTime+" DESC");
        if (v.getCount() > 0) {
            final int uid = v.getColumnIndex(LTaskContract.Uid);
            final int name = v.getColumnIndex(LTaskContract.Name);
            final int comment = v.getColumnIndex(LTaskContract.Comment);
            final int status = v.getColumnIndex(LTaskContract.Status);
            //boolean taskIsAlreadyFounded = false;
            for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {

                //Tasks
                if (v.getString(name) != null &&
                        v.getString(name).toUpperCase().contains(search.toUpperCase()) ||
                        (v.getString(comment) != null &&
                                v.getString(comment).toUpperCase().contains(search.toUpperCase()))) {
                    LTask tempTask = new LTask(v);
                    if (v.getString(status).equals("1") || v.getString(status).equals("7")) {
                        searchTasksCompleted.add(tempTask);
                    } else {
                        searchTasksUncompleted.add(tempTask);
                    }
                } /*else {
                    //Comments
                    final int uidHash = TaskHelper.getHashFromUid(v.getString(uid).toLowerCase());
                    final int uidHashUpper = TaskHelper.getHashFromUid(v.getString(uid).toUpperCase());
                    final TaskMessageCache messageCache = TaskMessageCache.getInstance(mContext);
                    final List<TaskMessage> messages = messageCache.find(uidHash);
                    if (messages != null && messages.size() > 0) {
                        for (TaskMessage taskMessage : messages) {
                            if (taskMessage.getIdTask().toString().equals(v.getString(uid).toLowerCase())) {
                                if (!taskIsAlreadyFounded && taskMessage.getMessage() != null &&
                                        taskMessage.getMessage().toUpperCase().contains(search.toUpperCase())) {
                                    taskIsAlreadyFounded = true;
                                    LTask tempTask = new LTask(v);
                                    searchTasks.add(tempTask);
                                }
                            }
                        }
                    }
                }*/
            }
        }
        v.close();
        searchTasks.addAll(searchTasksUncompleted);
        searchTasks.addAll(searchTasksCompleted);
        return searchTasks;
        //
        /*search.toUpperCase();
        List <LTask> searchTasks = new ArrayList<>();
        List <LTask> tempListTasks = new ArrayList<>();
        try {
            tempListTasks = getLTaskDao().queryBuilder().orderBy(LTaskContract.OrderNew, true).query();
            for (LTask tempTask: tempListTasks) {
                if ((tempTask.getName() != null &&
                    tempTask.getName().toUpperCase().contains(search.toUpperCase())) ||
                    (tempTask.getComment() != null &&
                    tempTask.getComment().toUpperCase().contains(search.toUpperCase()))) {
                        searchTasks.add(tempTask);
                }
            }
        }
        catch (Exception e) {
            Utils.toLog(e);
            return null;
        } finally {
            tempListTasks.clear();
            return searchTasks;
        }*/
    }

    public List<LTask> getTasksWithParent(String uid) {
        List <LTask> searchTasks = new ArrayList<>();
        List <LTask> searchTasksUncompleted = new ArrayList<>();
        List <LTask> searchTasksCompleted = new ArrayList<>();
        final ContentResolver cr = mContext.getContentResolver();
        //
        final Cursor v = cr.query(LTaskContract.CONTENT_URI, null, LTaskContract.UIDParent+" =='"+uid+"'", null, LTaskContract.UserOrder+" DESC");
        if (v.getCount() > 0) {
            final int uidParent = v.getColumnIndex(LTaskContract.UIDParent);
            final int status = v.getColumnIndex(LTaskContract.Status);
            for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                //Tasks
                LTask tempTask = new LTask(v);
                if (v.getString(status).equals("1") || v.getString(status).equals("7")) {
                    searchTasksCompleted.add(tempTask);
                } else {
                    searchTasksUncompleted.add(tempTask);
                }
            }
        }
        v.close();
        searchTasks.addAll(searchTasksUncompleted);
        searchTasks.addAll(searchTasksCompleted);
        return searchTasks;
    }

    public List<Contact> getContactsWithSearch(String search) {
        search.toUpperCase();
        List <Contact> listContact = new ArrayList<>();
        try {
            List <Contact> tempListContact = new ArrayList<>();
            List <Contact> searchContact = new ArrayList<>();
            tempListContact = getContactDao().queryBuilder().orderBy(ContactContract.ORDERS, true).query();
            for (Contact tempContact: tempListContact) {
                if ((tempContact.getTitle() != null &&
                    tempContact.getTitle().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getFirstName() != null &&
                    tempContact.getFirstName().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getLastName() != null &&
                    tempContact.getLastName().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getMiddleName() != null &&
                    tempContact.getMiddleName().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getCompanyName() != null &&
                    tempContact.getCompanyName().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getHomeIndex() != null &&
                    tempContact.getHomeIndex().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getHomeCity() != null &&
                    tempContact.getHomeCity().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getHomeCountry() != null &&
                    tempContact.getHomeCountry().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getHomeRegion() != null &&
                    tempContact.getHomeRegion().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getHomeStreet() != null &&
                    tempContact.getHomeStreet().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getWorkIndex() != null &&
                    tempContact.getWorkIndex().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getWorkCity() != null &&
                    tempContact.getWorkCity().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getWorkCountry() != null &&
                    tempContact.getWorkCountry().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getWorkRegion() != null &&
                    tempContact.getWorkRegion().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getWorkStreet() != null &&
                    tempContact.getWorkStreet().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getDetails() != null &&
                    tempContact.getDetails().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getJobTitle() != null &&
                    tempContact.getJobTitle().toUpperCase().contains(search.toUpperCase())) ||
                    (tempContact.getCommunications() != null &&
                    Utils.getUTF8stringFromBase64(tempContact.getCommunications()).toUpperCase().contains(search.toUpperCase()))) {
                        searchContact.add(tempContact);
                }
            }

            for (Contact contact: searchContact) {
                List<ContactsGroup> contactsGroup = new ArrayList<>();
                if (contact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                    listContact.add(contact);
                } else {
                    if(contact.getUidGroup() != null) {
                        contactsGroup = getContactsGroupDao().queryBuilder().where().in(ContactsGroup.FIELD_UID, contact.getUidGroup()).query();
                        if (contactsGroup.size() > 0) {
                            if (contactsGroup.get(0).getSharedUsers().indexOf(LTSettings.getInstance().getUserName()) != -1) {
                                listContact.add(contact);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
        return listContact;
    }

    public List<Contact> getAllContactsWithOrder(int order) {
        String currentOrder;
        switch (order) {
            case 1:
                currentOrder = ContactContract.TITLE;
                break;
            default:
                return getAllContacts();
        }
        try {
            //
            List <Contact> listContact = new ArrayList<>();
            List <Contact> tempListContact = new ArrayList<>();
                tempListContact = getContactDao().queryBuilder().orderByRaw("LOWER("+ContactContract.TITLE +") ASC").query();
                for (Contact contact: tempListContact) {
                    List<ContactsGroup> contactsGroup = new ArrayList<>();
                    if (contact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                        listContact.add(contact);
                    } else {
                        if(contact.getUidGroup() != null) {
                            contactsGroup = getContactsGroupDao().queryBuilder().where().in(ContactsGroup.FIELD_UID, contact.getUidGroup()).query();
                            if (contactsGroup.size() > 0) {
                                if (contactsGroup.get(0).getSharedUsers().indexOf(LTSettings.getInstance().getUserName()) != -1) {
                                    listContact.add(contact);
                                }
                            }
                        }
                    }
                }

            return listContact;
        }
        catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<ContactsGroup> getAllContactsGroups() {
        try {
            return getContactsGroupDao().queryBuilder().orderBy(ContactsGroup.FIELD_ORDER, true).orderBy(ContactsGroup.FIELD_NAME, true).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<ContactsGroup> getMyContactsGroups() {
        try {
            return getContactsGroupDao().queryBuilder().orderBy(ContactsGroup.FIELD_ORDER, true).orderBy(ContactsGroup.FIELD_NAME, true).where().eq(ContactsGroup.FIELD_CREATOR, mSettings.getUserName()).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<ContactsGroup> getSharedContactsGroups() {
        try {
            List<ContactsGroup> currentList = new ArrayList<>();
            List<ContactsGroup> list = getContactsGroupDao().queryBuilder().orderBy(ContactsGroup.FIELD_ORDER, true).orderBy(ContactsGroup.FIELD_NAME, true).where().notIn(ContactsGroup.FIELD_CREATOR, mSettings.getUserName()).query();
            for (ContactsGroup contactsGroup : list) {
                if (contactsGroup.getSharedUsers() != null) {
                    if (contactsGroup.getSharedUsers().contains(mSettings.getUserName())) {
                        currentList.add(contactsGroup);
                    }
                }
            }
            return currentList;
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Project> getAvailableProject(Context context, boolean updateNumber) throws SQLException, AbstractDataRequestException {
        final String userName = LTSettings.getInstance(context).getUserName();
        final Where<Project, UUID> where = getProjectDao()
                .queryBuilder()
                .orderBy(Project.FIELD_ORDER, true)
                .orderBy(Project.FIELD_NAME, true)
                .selectColumns(Project.FIELD_UID, Project.FIELD_NAME, Project.FIELD_COLLAPSED, Project.FIELD_UID_PARENT, Project.FIELD_ORDER,
                        Project.FIELD_CREATOR).where();

        final List<Project> lp = where.ne(Project.FIELD_CREATOR, userName).query();

        if (updateNumber) {
            for (Project project : lp) {
                new GetNumberOfTasksInProject(context, project, userName).execute(null);
            }
        }

        return lp;
    }
    public List<Category> getCategories(Context context, boolean updateNumber) throws SQLException, AbstractDataRequestException {
        final String userName = LTSettings.getInstance(context).getUserName();

        final List<Category> lc = getCategoryDao().queryBuilder().orderBy(Category.FIELD_ORDER, true).orderBy(Category.FIELD_NAME, true).where().eq("mCreator", userName).query();

        if (updateNumber) {
            for (Category category : lc) {
                new GetNumberOfTasksInCategory(context, category, userName).execute(null);
            }
        }

        return lc;
    }

    public List<Marker> getMarkers(Context context) throws SQLException, AbstractDataRequestException {
        final String userName = LTSettings.getInstance(context).getUserName();

        final List<Marker> lc = getMarkerDao().queryBuilder().orderBy(Marker.FIELD_NAME, true).where().eq("EmailCreator", userName).query();


        return lc;
    }

    /**
     * РџРѕР»СѓС‡РµРЅРёРµ РїСЂРѕРµРєС‚Р° РїРѕ UUID
     * 
     * @author Tetiana Diachuk (diacht@gmail.com)
     * 
     * @param uuid
     * @return
     * @throws SQLException
     */
    public Project getProjectByUUId(UUID uuid) throws SQLException {
        return getProjectDao().queryBuilder().where().eq(Project.FIELD_UID, uuid).queryForFirst();
    }

    public Category getCategoryByUUId(UUID uuid) throws SQLException {
        return getCategoryDao().queryBuilder().where().eq(Category.FIELD_UID, uuid).queryForFirst();
    }

    public Marker getMarkerByUUId(UUID uuid) throws SQLException {
        return getMarkerDao().queryBuilder().where().eq(Marker.FIELD_UID, uuid).queryForFirst();
    }

    public Emp getEmpByLogin(String login) throws SQLException {
        return getEmpDao().queryBuilder().where().eq(EmpContract.LOGIN, login).queryForFirst();
    }


    public List<Category> getAllCategories() {
        try {
            return getCategoryDao().queryBuilder().orderBy(Category.FIELD_ORDER, true).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Category> getAllMyCategories() {
        try {
            final QueryBuilder<Category, UUID> builder = getCategoryDao().queryBuilder();
            builder.orderBy(Category.FIELD_ORDER, true);
            builder.where().eq("mCreator", LTSettings.getInstance().getUserName());

            return builder.query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Marker> getAllMarkers() {
        try {
            return getMarkerDao().queryBuilder().orderBy(Marker.ORDERS, true).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public List<Marker> getAllMarkersNew() {
        try {
            return getMarkerDao().queryBuilder().orderBy(Marker.ORDERS, false).query();
        } catch (Exception e) {
            Utils.toLog(e);
            return null;
        }
    }

    public void updateMarkers(final List<Marker> markers) {
        try {
            getMarkerDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    // create list of markers uuid
                    final List<UUID> uuids = new ArrayList<UUID>(markers.size());
                    // inflate list
                    for (Marker marker : markers) {
                        uuids.add(marker.getId());
                    }

                    // get markers from database with particular uuid
                    final List<Marker> oldMarkers = getMarkerDao().queryBuilder().where().in(Marker.FIELD_UID, uuids).query();
                    uuids.clear();

                    // transfer particular markers from List to HashMap
                    final Map<UUID, Marker> oldMarkersInMap = new HashMap<UUID, Marker>(oldMarkers.size());
                    for (Marker marker : oldMarkers) {
                        oldMarkersInMap.put(marker.getId(), marker);
                    }
                    oldMarkers.clear();

                    for (Marker newMarker : markers) {
                        final Marker oldMarker = oldMarkersInMap.get(newMarker.getId());
                        if (oldMarker == null) {
                            getMarkerDao().createOrUpdate(newMarker);
                            final DeletedTask object = getDeletedTaskDao().queryForId(newMarker.getId());
                            if (object != null) {
                                getDeletedTaskDao().delete(object);
                            }

                        } else if (newMarker.getUsn() != oldMarker.getUsn()) {
                            oldMarker.setUsn(newMarker.getUsn());
                            oldMarker.setId(newMarker.getId());
                            oldMarker.setCreator(newMarker.getCreator());

                            if (oldMarker.getUsnOrder() <= newMarker.getUsnOrder()) {
                                oldMarker.setOrder(newMarker.getOrder());
                                oldMarker.setUsnOrder(newMarker.getUsnOrder());

                                // Обновление после изменение порядка
                                Marker.updateTaskMarkerOrder(newMarker.getId().toString().toUpperCase(), newMarker.getOrder(), mContext);

                            } else
                                oldMarker.setUsn(0);

                            if (oldMarker.getUsnName() <= newMarker.getUsnName()) {
                                oldMarker.setName(newMarker.getName());
                                oldMarker.setUsnName(newMarker.getUsnName());
                            } else
                                oldMarker.setUsn(0);

                            if (oldMarker.getUsnIsUppercase() <= newMarker.getUsnIsUppercase()) {
                                oldMarker.setIsUppercase(newMarker.isUppercase());
                                oldMarker.setUsnIsUppercase(newMarker.getUsnIsUppercase());
                            } else
                                oldMarker.setUsn(0);

                            if (oldMarker.getUsnTextColor() <= newMarker.getUsnTextColor()) {
                                oldMarker.setTextColor(newMarker.getTextColor());
                                oldMarker.setUsnTextColor(newMarker.getUsnTextColor());
                            } else
                                oldMarker.setUsn(0);

                            if (oldMarker.getUsnBackColor() <= newMarker.getUsnBackColor()) {
                                oldMarker.setBackColor(newMarker.getBackColor());
                                oldMarker.setUsnBackColor(newMarker.getUsnBackColor());
                            } else
                                oldMarker.setUsn(0);
                            getMarkerDao().update(oldMarker);
                        }
                    }

                    oldMarkersInMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * СѓРґР°Р»РµРЅРёРµ РјР°СЂРєРµСЂРѕРІ
     * 
     * @param markers
     */
    public void deleteMarkers(final List<UUID> markers) {
        try {
            getMarkerDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    getMarkerDao().deleteIds(markers);
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update projects that received as synchronization result
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public void updateProjects(final List<Project> projects) {
        try {
            getProjectDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {

                    // create list of projects uuid
                    final List<UUID> uuids = new ArrayList<UUID>();
                    // inflate list
                    for (Project project : projects) {
                        uuids.add(project.getId());
                    }

                    // get projects from database with particular uuid
                    final List<Project> oldProjects = getProjectDao()//
                            .queryBuilder().where().in(Project.FIELD_UID, uuids).query();
                    uuids.clear();
                    // transfer particular projects from List to HashMap
                    final Map<UUID, Project> oldProjectsInMap = new HashMap<UUID, Project>();
                    for (Project project : oldProjects) {
                        oldProjectsInMap.put(project.getId(), project);
                    }
                    oldProjects.clear();

                    for (Project newProject : projects) {
                        final Project oldProject = oldProjectsInMap.get(newProject.getId());
                        if (oldProject == null) {
                            getProjectDao().create(newProject);
                        }

                        else {
                            if (newProject.getUsn() == oldProject.getUsn()) {
                                continue;
                            }

                            oldProject.setUsn(newProject.getUsn());
                            oldProject.setCreator(newProject.getCreator());

                            if (oldProject.getUsnParent() <= newProject.getUsnParent()) {
                                oldProject.setParentId(newProject.getParentId());
                                oldProject.setUsnParent(newProject.getUsnParent());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnCollapsed() <= newProject.getUsnCollapsed()) {
                                oldProject.setCollapsed(newProject.isCollapsed());
                                oldProject.setUsnCollapsed(newProject.getUsnCollapsed());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnOrder() <= newProject.getUsnOrder()) {
                                oldProject.setOrder(newProject.getOrder());
                                oldProject.setUsnOrder(newProject.getUsnOrder());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnName() <= newProject.getUsnName()) {
                                oldProject.setName(newProject.getName());
                                oldProject.setUsnName(newProject.getUsnName());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnComment() <= newProject.getUsnComment()) {
                                oldProject.setComment(newProject.getComment());
                                oldProject.setUsnComment(newProject.getUsnComment());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnFavorite() <= newProject.getUsnFavorite()) {
                                oldProject.setFavorite(newProject.isFavorite());
                                oldProject.setUsnFavorite(newProject.getUsnFavorite());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnGroup() <= newProject.getUsnGroup()) {
                                oldProject.setGroup(newProject.isGroup());
                                oldProject.setUsnGroup(newProject.getUsnGroup());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnShow() <= newProject.getUsnShow()) {
                                oldProject.setShow(newProject.isShow());
                                oldProject.setUsnShow(newProject.getUsnShow());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnClosed() <= newProject.getUsnClosed()) {
                                oldProject.setClosed(newProject.isClosed());
                                oldProject.setUsnClosed(newProject.getUsnClosed());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnQuiet() <= newProject.getUsnQuiet()) {
                                oldProject.setQuiet(newProject.isQuiet());
                                oldProject.setUsnQuiet(newProject.getUsnQuiet());
                            } else {
                                oldProject.setUsn(0);
                            }

                            if (oldProject.getUsnSharedUsers() <= newProject.getUsnSharedUsers()) {
                                oldProject.setSharedUsers(newProject.getSharedUsers());
                                oldProject.setUsnSharedUsers(newProject.getUsnSharedUsers());
                            } else {
                                oldProject.setUsn(0);
                            }

                            getProjectDao().update(oldProject);
                        }
                    }

                    oldProjectsInMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateContactsGroups(final List<ContactsGroup> contactsGroups) {
        try {
            getContactsGroupDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {

                    // create list of projects uuid
                    final List<UUID> uuids = new ArrayList<UUID>();
                    // inflate list
                    for (ContactsGroup contactsGroup : contactsGroups) {
                        uuids.add(contactsGroup.getId());
                    }

                    // get projects from database with particular uuid
                    final List<ContactsGroup> oldContactsGroups = getContactsGroupDao()//
                            .queryBuilder().where().in(ContactsGroup.FIELD_UID, uuids).query();
                    uuids.clear();
                    // transfer particular ContactsGroups from List to HashMap
                    final Map<UUID, ContactsGroup> oldContactsGroupsInMap = new HashMap<UUID, ContactsGroup>();
                    for (ContactsGroup ContactsGroup : oldContactsGroups) {
                        oldContactsGroupsInMap.put(ContactsGroup.getId(), ContactsGroup);
                    }
                    oldContactsGroups.clear();

                    for (ContactsGroup newContactsGroup : contactsGroups) {
                        final ContactsGroup oldContactsGroup = oldContactsGroupsInMap.get(newContactsGroup.getId());
                        if (oldContactsGroup == null) {
                            getContactsGroupDao().create(newContactsGroup);
                        }

                        else {
                            if (newContactsGroup.getUsn() == oldContactsGroup.getUsn()) {
                                continue;
                            }

                            oldContactsGroup.setUsn(newContactsGroup.getUsn());
                            oldContactsGroup.setCreator(newContactsGroup.getCreator());

                            if (oldContactsGroup.getUsnParent() <= newContactsGroup.getUsnParent()) {
                                oldContactsGroup.setParentId(newContactsGroup.getParentId());
                                oldContactsGroup.setUsnParent(newContactsGroup.getUsnParent());
                            } else {
                                oldContactsGroup.setUsn(0);
                            }

                            if (oldContactsGroup.getUsnCollapsed() <= newContactsGroup.getUsnCollapsed()) {
                                oldContactsGroup.setCollapsed(newContactsGroup.isCollapsed());
                                oldContactsGroup.setUsnCollapsed(newContactsGroup.getUsnCollapsed());
                            } else {
                                oldContactsGroup.setUsn(0);
                            }

                            if (oldContactsGroup.getUsnOrder() <= newContactsGroup.getUsnOrder()) {
                                oldContactsGroup.setOrder(newContactsGroup.getOrder());
                                oldContactsGroup.setUsnOrder(newContactsGroup.getUsnOrder());
                            } else {
                                oldContactsGroup.setUsn(0);
                            }

                            if (oldContactsGroup.getUsnName() <= newContactsGroup.getUsnName()) {
                                oldContactsGroup.setName(newContactsGroup.getName());
                                oldContactsGroup.setUsnName(newContactsGroup.getUsnName());
                            } else {
                                oldContactsGroup.setUsn(0);
                            }

                            if (oldContactsGroup.getUsnComment() <= newContactsGroup.getUsnComment()) {
                                oldContactsGroup.setComment(newContactsGroup.getComment());
                                oldContactsGroup.setUsnComment(newContactsGroup.getUsnComment());
                            } else {
                                oldContactsGroup.setUsn(0);
                            }

                            if (oldContactsGroup.getUsnSharedUsers() <= newContactsGroup.getUsnSharedUsers()) {
                                oldContactsGroup.setSharedUsers(newContactsGroup.getSharedUsers());
                                oldContactsGroup.setUsnSharedUsers(newContactsGroup.getUsnSharedUsers());
                            } else {
                                oldContactsGroup.setUsn(0);
                            }

                            getContactsGroupDao().update(oldContactsGroup);
                        }
                    }

                    oldContactsGroupsInMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateContacts (final List<Contact> contacts) {
        try {
            getContactDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {

                    // create list of projects uuid
                    final List<UUID> uuids = new ArrayList<UUID>();
                    // inflate list
                    for (Contact contactsGroup : contacts) {
                        uuids.add(contactsGroup.getId());
                    }

                    // get projects from database with particular uuid
                    final List<Contact> oldContacts = getContactDao()//
                            .queryBuilder().where().in(ContactContract.UID, uuids).query();
                    uuids.clear();
                    // transfer particular Contacts from List to HashMap
                    final Map<UUID, Contact> oldContactsInMap = new HashMap<UUID, Contact>();
                    for (Contact Contact : oldContacts) {
                        oldContactsInMap.put(Contact.getId(), Contact);
                    }
                    oldContacts.clear();

                    for (Contact newContact : contacts) {
                        final Contact oldContact = oldContactsInMap.get(newContact.getId());
                        if (oldContact == null) {
                            getContactDao().create(newContact);
                        } else {
                            if (newContact.getUsn() == oldContact.getUsn()) {
                                continue;
                            }

                            oldContact.setUsnEntity(newContact.getUsnEntity());
                            oldContact.setEmailCreator(newContact.getEmailCreator());
                            oldContact.setUsnFieldFoto(newContact.getUsnFieldFoto());

                            if (oldContact.getUsnFieldOrder() <= newContact.getUsnFieldOrder()) {
                                oldContact.setOrder(newContact.getOrder());
                                oldContact.setUsnFieldOrder(newContact.getUsnFieldOrder());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldUidParent() <= newContact.getUsnFieldUidParent()) {
                                oldContact.setUidParent(newContact.getUidParent());
                                oldContact.setUsnFieldUidParent(newContact.getUsnFieldUidParent());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldUidGroup() <= newContact.getUsnFieldUidGroup()) {
                                oldContact.setUidGroup(newContact.getUidGroup());
                                oldContact.setUsnFieldUidGroup(newContact.getUsnFieldUidGroup());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldTitle() <= newContact.getUsnFieldTitle()) {
                                oldContact.setTitle(newContact.getTitle());
                                oldContact.setUsnFieldTitle(newContact.getUsnFieldTitle());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldIsGroup() <= newContact.getUsnFieldIsGroup()) {
                                oldContact.setGroup(newContact.isGroup());
                                oldContact.setUsnFieldIsGroup(newContact.getUsnFieldIsGroup());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldGender() <= newContact.getUsnFieldGender()) {
                                oldContact.setGender(newContact.getGender());
                                oldContact.setUsnFieldGender(newContact.getUsnFieldGender());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldFirstName() <= newContact.getUsnFieldFirstName()) {
                                oldContact.setFirstName(newContact.getFirstName());
                                oldContact.setUsnFieldFirstName(newContact.getUsnFieldFirstName());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldMiddleName() <= newContact.getUsnFieldMiddleName()) {
                                oldContact.setMiddleName(newContact.getMiddleName());
                                oldContact.setUsnFieldMiddleName(newContact.getUsnFieldMiddleName());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldLastName() <= newContact.getUsnFieldLastName()) {
                                oldContact.setLastName(newContact.getLastName());
                                oldContact.setUsnFieldLastName(newContact.getUsnFieldLastName());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldCompanyName() <= newContact.getUsnFieldCompanyName()) {
                                oldContact.setCompanyName(newContact.getCompanyName());
                                oldContact.setUsnFieldCompanyName(newContact.getUsnFieldCompanyName());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldJobTitle() <= newContact.getUsnFieldJobTitle()) {
                                oldContact.setJobTitle(newContact.getJobTitle());
                                oldContact.setUsnFieldJobTitle(newContact.getUsnFieldJobTitle());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldDetails() <= newContact.getUsnFieldDetails()) {
                                oldContact.setDetails(newContact.getDetails());
                                oldContact.setUsnFieldDetails(newContact.getUsnFieldDetails());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldDetails() <= newContact.getUsnFieldDetails()) {
                                oldContact.setDetails(newContact.getDetails());
                                oldContact.setUsnFieldDetails(newContact.getUsnFieldDetails());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldBirthday() <= newContact.getUsnFieldBirthday()) {
                                oldContact.setBirthday(newContact.getBirthday());
                                oldContact.setUsnFieldBirthday(newContact.getUsnFieldBirthday());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldCommunications() <= newContact.getUsnFieldCommunications()) {
                                oldContact.setCommunications(newContact.getCommunications());
                                oldContact.setUsnFieldCommunications(newContact.getUsnFieldCommunications());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldHomeCity() <= newContact.getUsnFieldHomeCity()) {
                                oldContact.setHomeCity(newContact.getHomeCity());
                                oldContact.setUsnFieldHomeCity(newContact.getUsnFieldHomeCity());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldHomeCountry() <= newContact.getUsnFieldHomeCountry()) {
                                oldContact.setHomeCountry(newContact.getHomeCountry());
                                oldContact.setUsnFieldHomeCountry(newContact.getUsnFieldHomeCountry());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldHomeRegion() <= newContact.getUsnFieldHomeRegion()) {
                                oldContact.setHomeRegion(newContact.getHomeRegion());
                                oldContact.setUsnFieldHomeRegion(newContact.getUsnFieldHomeRegion());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldHomeIndex() <= newContact.getUsnFieldHomeIndex()) {
                                oldContact.setHomeIndex(newContact.getHomeIndex());
                                oldContact.setUsnFieldHomeIndex(newContact.getUsnFieldHomeIndex());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldHomeStreet() <= newContact.getUsnFieldHomeStreet()) {
                                oldContact.setHomeStreet(newContact.getHomeStreet());
                                oldContact.setUsnFieldHomeStreet(newContact.getUsnFieldHomeStreet());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldWorkCity() <= newContact.getUsnFieldWorkCity()) {
                                oldContact.setWorkCity(newContact.getWorkCity());
                                oldContact.setUsnFieldWorkCity(newContact.getUsnFieldWorkCity());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldWorkCountry() <= newContact.getUsnFieldWorkCountry()) {
                                oldContact.setWorkCountry(newContact.getWorkCountry());
                                oldContact.setUsnFieldWorkCountry(newContact.getUsnFieldWorkCountry());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldWorkRegion() <= newContact.getUsnFieldWorkRegion()) {
                                oldContact.setWorkRegion(newContact.getWorkRegion());
                                oldContact.setUsnFieldWorkRegion(newContact.getUsnFieldWorkRegion());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldWorkIndex() <= newContact.getUsnFieldWorkIndex()) {
                                oldContact.setWorkIndex(newContact.getWorkIndex());
                                oldContact.setUsnFieldWorkIndex(newContact.getUsnFieldWorkIndex());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldWorkStreet() <= newContact.getUsnFieldWorkStreet()) {
                                oldContact.setWorkStreet(newContact.getWorkStreet());
                                oldContact.setUsnFieldWorkStreet(newContact.getUsnFieldWorkStreet());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldCollapsed() <= newContact.getUsnFieldCollapsed()) {
                                oldContact.setCollapsed(newContact.isCollapsed());
                                oldContact.setUsnFieldCollapsed(newContact.getUsnFieldCollapsed());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldFavorite() <= newContact.getUsnFieldFavorite()) {
                                oldContact.setFavorite(newContact.isFavorite());
                                oldContact.setUsnFieldFavorite(newContact.getUsnFieldFavorite());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldShowNavigator() <= newContact.getUsnFieldShowNavigator()) {
                                oldContact.setShowNavigator(newContact.isShowNavigator());
                                oldContact.setUsnFieldShowNavigator(newContact.getUsnFieldShowNavigator());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            if (oldContact.getUsnFieldNotifyBirthday() <= newContact.getUsnFieldNotifyBirthday()) {
                                oldContact.setNotifyBirthday(newContact.isNotifyBirthday());
                                oldContact.setUsnFieldNotifyBirthday(newContact.getUsnFieldNotifyBirthday());
                            } else {
                                oldContact.setUsnEntity(0);
                            }

                            getContactDao().update(oldContact);
                        }
                    }

                    oldContactsInMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateContactsNew (final List<Contact> contacts) {
        try {
            getContactDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {

                    // create list of projects uuid
                    final List<UUID> uuids = new ArrayList<UUID>();
                    // inflate list
                    for (Contact contactsGroup : contacts) {
                        uuids.add(contactsGroup.getId());
                    }

                    // get projects from database with particular uuid
                    final List<Contact> oldContacts = getContactDao()//
                            .queryBuilder().where().in(ContactContract.UID, uuids).query();
                    uuids.clear();
                    // transfer particular Contacts from List to HashMap
                    final Map<UUID, Contact> oldContactsInMap = new HashMap<UUID, Contact>();
                    for (Contact Contact : oldContacts) {
                        oldContactsInMap.put(Contact.getId(), Contact);
                    }
                    oldContacts.clear();

                    for (Contact newContact : contacts) {
                        final Contact oldContact = oldContactsInMap.get(newContact.getId());
                        if (oldContact == null) {
                            getContactDao().create(newContact);
                        } else {
                            if (newContact.getUsn() == oldContact.getUsn()) {
                                continue;
                            }

                            oldContact.setUsnEntity(newContact.getUsnEntity());
                            oldContact.setEmailCreator(newContact.getEmailCreator());
                            oldContact.setUsnFieldFoto(newContact.getUsnFieldFoto());


                                oldContact.setOrder(newContact.getOrder());
                                oldContact.setUsnFieldOrder(newContact.getUsnFieldOrder());

                                oldContact.setUidParent(newContact.getUidParent());
                                oldContact.setUsnFieldUidParent(newContact.getUsnFieldUidParent());


                                oldContact.setUidGroup(newContact.getUidGroup());
                                oldContact.setUsnFieldUidGroup(newContact.getUsnFieldUidGroup());


                                oldContact.setTitle(newContact.getTitle());
                                oldContact.setUsnFieldTitle(newContact.getUsnFieldTitle());


                                oldContact.setGroup(newContact.isGroup());
                                oldContact.setUsnFieldIsGroup(newContact.getUsnFieldIsGroup());


                                oldContact.setGender(newContact.getGender());
                                oldContact.setUsnFieldGender(newContact.getUsnFieldGender());


                                oldContact.setFirstName(newContact.getFirstName());
                                oldContact.setUsnFieldFirstName(newContact.getUsnFieldFirstName());


                                oldContact.setMiddleName(newContact.getMiddleName());
                                oldContact.setUsnFieldMiddleName(newContact.getUsnFieldMiddleName());


                                oldContact.setLastName(newContact.getLastName());
                                oldContact.setUsnFieldLastName(newContact.getUsnFieldLastName());

                                oldContact.setCompanyName(newContact.getCompanyName());
                                oldContact.setUsnFieldCompanyName(newContact.getUsnFieldCompanyName());

                                oldContact.setJobTitle(newContact.getJobTitle());
                                oldContact.setUsnFieldJobTitle(newContact.getUsnFieldJobTitle());


                                oldContact.setDetails(newContact.getDetails());
                                oldContact.setUsnFieldDetails(newContact.getUsnFieldDetails());


                                oldContact.setDetails(newContact.getDetails());
                                oldContact.setUsnFieldDetails(newContact.getUsnFieldDetails());


                                oldContact.setBirthday(newContact.getBirthday());
                                oldContact.setUsnFieldBirthday(newContact.getUsnFieldBirthday());


                                oldContact.setCommunications(newContact.getCommunications());
                                oldContact.setUsnFieldCommunications(newContact.getUsnFieldCommunications());

                                oldContact.setHomeCity(newContact.getHomeCity());
                                oldContact.setUsnFieldHomeCity(newContact.getUsnFieldHomeCity());


                                oldContact.setHomeCountry(newContact.getHomeCountry());
                                oldContact.setUsnFieldHomeCountry(newContact.getUsnFieldHomeCountry());

                                oldContact.setHomeRegion(newContact.getHomeRegion());
                                oldContact.setUsnFieldHomeRegion(newContact.getUsnFieldHomeRegion());


                                oldContact.setHomeIndex(newContact.getHomeIndex());
                                oldContact.setUsnFieldHomeIndex(newContact.getUsnFieldHomeIndex());


                                oldContact.setHomeStreet(newContact.getHomeStreet());
                                oldContact.setUsnFieldHomeStreet(newContact.getUsnFieldHomeStreet());


                                oldContact.setWorkCity(newContact.getWorkCity());
                                oldContact.setUsnFieldWorkCity(newContact.getUsnFieldWorkCity());


                                oldContact.setWorkCountry(newContact.getWorkCountry());
                                oldContact.setUsnFieldWorkCountry(newContact.getUsnFieldWorkCountry());

                                oldContact.setWorkRegion(newContact.getWorkRegion());
                                oldContact.setUsnFieldWorkRegion(newContact.getUsnFieldWorkRegion());

                                oldContact.setWorkIndex(newContact.getWorkIndex());
                                oldContact.setUsnFieldWorkIndex(newContact.getUsnFieldWorkIndex());


                                oldContact.setWorkStreet(newContact.getWorkStreet());
                                oldContact.setUsnFieldWorkStreet(newContact.getUsnFieldWorkStreet());


                                oldContact.setCollapsed(newContact.isCollapsed());
                                oldContact.setUsnFieldCollapsed(newContact.getUsnFieldCollapsed());


                                oldContact.setFavorite(newContact.isFavorite());
                                oldContact.setUsnFieldFavorite(newContact.getUsnFieldFavorite());


                                oldContact.setShowNavigator(newContact.isShowNavigator());
                                oldContact.setUsnFieldShowNavigator(newContact.getUsnFieldShowNavigator());

                                oldContact.setNotifyBirthday(newContact.isNotifyBirthday());
                                oldContact.setUsnFieldNotifyBirthday(newContact.getUsnFieldNotifyBirthday());

                            getContactDao().update(oldContact);
                        }
                    }

                    oldContactsInMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateContactsUsnFoto (final List<Contact> contacts) {
        try {
            getContactDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {

                    // create list of projects uuid
                    final List<UUID> uuids = new ArrayList<UUID>();
                    // inflate list
                    for (Contact contactsGroup : contacts) {
                        uuids.add(contactsGroup.getId());
                    }

                    // get projects from database with particular uuid
                    final List<Contact> oldContacts = getContactDao()//
                            .queryBuilder().where().in(ContactContract.UID, uuids).query();
                    uuids.clear();
                    // transfer particular Contacts from List to HashMap
                    final Map<UUID, Contact> oldContactsInMap = new HashMap<UUID, Contact>();
                    for (Contact Contact : oldContacts) {
                        oldContactsInMap.put(Contact.getId(), Contact);
                    }
                    oldContacts.clear();

                    for (Contact newContact : contacts) {
                        final Contact oldContact = oldContactsInMap.get(newContact.getId());
                        if (oldContact == null) {
                            getContactDao().create(newContact);
                        }
                        else {
                            oldContact.setUsnFieldFoto(newContact.getUsnFieldFoto());

                            getContactDao().update(oldContact);
                        }
                    }

                    oldContactsInMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * СѓРґР°Р»РµРЅРёРµ РїСЂРѕРµРєС‚РѕРІ
     * 
     * @param projects
     */
    public void deleteProjects(final List<UUID> projects) {
        try {
            getProjectDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    getProjectDao().deleteIds(projects);
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteContactsGroups(final List<UUID> contactsGroups) {
        try {
            getContactsGroupDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    getContactsGroupDao().deleteIds(contactsGroups);
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteContacts(final List<UUID> contacts) {
        try {
            getContactDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    getContactDao().deleteIds(contacts);
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Р”РѕР±Р°РІР»РµРЅРёРµ Р·Р°РґР°С‡Рё РІ Р±Р°Р·Сѓ РґР°РЅРЅС‹С…
     * 
     * @param context
     *            - РєРѕРЅС‚РµРєСЃС‚, РёР· РєРѕС‚РѕСЂРѕРіРѕ РїСЂРѕРёСЃС…РѕРґРёС‚ РІС‹Р·РѕРІ РґР°РЅРЅРѕРіРѕ РјРµС‚РѕРґР°
     * @param task
     *            - Р·Р°РґР°С‡Р°, РєРѕС‚РѕСЂСѓСЋ РЅРµРѕР±С…РѕРґРёРјРѕ РґРѕР±Р°РІРёС‚СЊ РІ Р±Р°Р·Сѓ РґР°РЅРЅС‹С…
     * @param category
     *            - СѓРЅРёРєР°Р»СЊРЅС‹Р№ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ (UUID) РєР°С‚РµРіРѕСЂРёРё Р·Р°РґР°С‡Рё
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * @author V.Shcryabets<vshcryabets@gmail.com>
     */
    public void addTask(Context context, Task task, Category category, boolean isUpdateTasksList) {
        if (TextUtils.isEmpty(task.getPerformer())) {
            throw new IllegalAccessError("Not specified performer for task");
        }

        // properly set Order and CustomerOrder fields of task instance
        /*
         * if current task has a parent then retrieve max value of Order field using descendants of parent task
         */
        String currentUser = LTSettings.getInstance(context).getUserName();
        if (task.getParentId() != null) {
            task = setOrderFromSubtaskMax(task);

        } else {
            /*
             * else retrieve max value of Order field using all tasks
             */
            task = setOrderFromTaskMax(task);
        }

        task.setUsn(1);
        task.setUsnParentUid(0);
        task.setCollapsed(true);
        task.setUsnCollapsed(0);
        task.setUsnOrder(0);
        task.setUsnCustomerOrder(0);
        task.setUsnName(1);
        task.setUsnComment(0);
        task.setUsnStatus(0);
        task.setCustomer(currentUser);
        if (!currentUser.equals(task.getPerformer()))
            task.setUsnEmailPerformer(1);
        else
            task.setUsnEmailPerformer(0);
        task.setUsnTerm(0);
        task.setUsnCustomerTerm(0);
        task.setUsnProjectUid(0);
        task.setMarkerUid(Marker.DEFAULT_MARKER_UUID);
        task.setUsnMarkerUid(0);
        task.setUsnReaded(0);
        task.setUsnCategories(0);
        task.setUsnContacts(0);

        updateLeftRightPointers(task);

        try {
            task.setCategoriesWithCategory(category);
            getTaskDao().create(task);
            updateNumberTaskAfterDelete_Add(task, false, false, isUpdateTasksList);

            new CreateOrRemoveTaskCategories(context, category, task, false).run();

        } catch (SQLException e) {
            Utils.toLog(e);
        } catch (AbstractDataRequestException e) {
            Utils.toLog(e);
        }

        SimpleNotifications.getInstance(context).convertNewTaskToSimpleNotify(task);
    }

    public void updateLeftRightPointers(Task task) {
        if (task.getParentId() == null) {
            Task lastTask = null;
            try {
                lastTask = getTaskDao().queryBuilder().selectColumns(TaskContract.RIGHT_POINTER).orderBy(TaskContract.RIGHT_POINTER, false).queryForFirst();

            } catch (SQLException e) {
                Utils.toLog(e);
            }

            if (lastTask == null) {
                task.setLeftPointer(0);
                task.setRightPointer(1);

            } else {
                task.setLeftPointer(lastTask.getRightPointer() + 1);
                task.setRightPointer(lastTask.getRightPointer() + 2);
            }

        } else {
            final Task parentTask = getTaskDao_queryForId(task.getParentId());

            if (parentTask != null) {
                final int leftPointer = parentTask.getRightPointer();
                task.setLeftPointer(leftPointer);
                task.setRightPointer(leftPointer + 1);

                try {
                    // update left pointer of tasks
                    getTaskDao().updateRaw("UPDATE tasks SET lft = lft + 2 WHERE lft > " + (task.getRightPointer() - 2));
                    // update right pointer of tasks
                    getTaskDao().updateRaw("UPDATE tasks SET rgt = rgt + 2 WHERE rgt > " + (task.getRightPointer() - 2));

                } catch (SQLException e) {
                    Utils.toLog(e);
                }
            }
        }
    }

    /**
     * set value of task Order field using parent's descendants max value
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public Task setOrderFromSubtaskMax(Task task) {
        Task t = null;
        try {
            t = getTaskDao().queryBuilder().selectColumns(TaskContract.ORDERS).orderBy(TaskContract.ORDERS, false).where()
                    .eq(TaskContract.FIELD_UID_PARENT, task.getParentId()).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
         * if database doesn't contain any subtasks then set value of Order field using parent task
         */
        if (t == null) {
            try {
                t = getTaskDao().queryBuilder().selectColumns(TaskContract.ORDERS).orderBy(TaskContract.ORDERS, false).where()
                        .eq(TaskContract.FIELD_UID, task.getParentId()).queryForFirst();
                task.setOrder(t.getOrder());
                task.setCustomerOrder(t.getOrder());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        /*
         * else set next value in series after max value
         */
        else {
            task.setOrder(t.getOrder() + 1);
            task.setCustomerOrder(t.getOrder() + 1);
        }

        return task;
    }

    /**
     * set value of task Order field using all tasks max value
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public Task setOrderFromTaskMax(Task task) {
        Task t = null;
        try {
            t = getTaskDao().queryBuilder().selectColumns(TaskContract.ORDERS).orderBy(TaskContract.ORDERS, false).queryForFirst();

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        if (t == null) {
            task.setOrder(1);
            task.setCustomerOrder(1);

        } else {
            task.setOrder(t.getOrder() + 1);
            task.setCustomerOrder(t.getOrder() + 1);
        }

        return task;
    }

    /**
     * update task in database task - task that will be updated context - current application context updateTasks -
     * necessary or unnecessary to update tasks list. This field depricated and currently not used. isForNotification:
     * true - indicates that task term was changed from notification tasks list and unnecessary to update sliding menu
     * false - some task property was changed and necessary to update sliding menu
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * @throws AbstractDataRequestException
     * 
     */
    public void updateTask(Task task, boolean updateTasks, boolean isForNotification, boolean isUpdateTasksList) throws AbstractDataRequestException {// Was
                                                                                                                                                      // Removed
                                                                                                                                                      // context
                                                                                                                                                      // parameter
        LTSettings settings = LTSettings.getInstance(mContext);
        boolean upPr = false;
        boolean updatePerformer = false;
        boolean updateCustomer = false;
        boolean updateStatus = false;
        // get task that need to update
        try {
            Task oldTask = getTaskDao_queryForId(task.getId());
            if (oldTask != null) {
                // update subtasks count
                // oldTask.setSubTasksSize(task.getSubTasksSize());

                // update made subtasks count
                // oldTask.setSubTasksSizeMade(task.getSubTasksSizeMade());

                // update not read subtasks count
                // oldTask.setSubTasksSizeNotRead(task.getSubTasksSize());

                // UPDATE Marker
                if (!oldTask.getMarkerUid().equals(task.getMarkerUid())) {
                    oldTask.setMarkerUid(task.getMarkerUid());
                    oldTask.setUsnMarkerUid(oldTask.getUsnMarkerUid() + 1);
                }

                // update task title
                if (!task.getName().equals(oldTask.getName())) {
                    oldTask.setName(task.getName());
                    oldTask.setUsnName(oldTask.getUsnName() + 1);
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task "IsReaded" option
                if (task.isReaded() != oldTask.isReaded()) {
                    // get parent task
                    if (oldTask.getParentId() != null) {
                        Task parentTask = getTaskDao_queryForId(oldTask.getParentId());
                        if (parentTask != null) {
                            // update not read subtasks count for parent task
                            if (task.isReaded())
                                parentTask.setSubTasksCountNotRead(parentTask.getSubTasksCountNotRead() - 1);
                            else
                                parentTask.setSubTasksCountNotRead(parentTask.getSubTasksCountNotRead() + 1);
                            // update parent task
                            updateTask(parentTask);
                        }
                    }
                    oldTask.setReaded(task.isReaded());
                    oldTask.setUsnReaded(oldTask.getUsnReaded() + 1);
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task comment
                String p1 = task.getComment();
                String p2 = oldTask.getComment();

                if (((p1 != null) && (p2 != null) && (!p1.equals(p2))) || ((p1 != null) && (p2 == null)) || ((p1 == null) && (p2 != null))) {
                    oldTask.setComment(task.getComment());
                    oldTask.setUsnComment(oldTask.getUsnComment() + 1);
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task status
                if (task.getStatus() != oldTask.getStatus()) {
                    {// @since 2014-06-26
                        final boolean performer = mSettings.getUserName().equals(task.getPerformer());
                        final boolean customer = mSettings.getUserName().equals(task.getCustomer());
                        if ((performer && !customer && task.getStatus() == TaskStatus.READY.ordinal()) || //
                                (customer && task.getStatus() == TaskStatus.COMPLETED.ordinal())) {
                            oldTask.setCompleteTime(new Date(Utils.getCurrentTimeWithSavings()));
                            oldTask.setUsnFieldCompleteTime(oldTask.getUsnFieldCompleteTime() + 1);
                        }
                    }

                    if (oldTask.getParentId() != null) {
                        if (hideTask(task, settings.getUserName())) {
                            if (!hideTask(oldTask, settings.getUserName())) {
                                // get parent task
                                if (oldTask.getParentId() != null) {
                                    Task parentTask = getTaskDao_queryForId(oldTask.getParentId());
                                    if (parentTask != null) {
                                        parentTask.setSubTasksCountNotMade(parentTask.getSubTasksCountNotMade() - 1);
                                    }
                                    // update not made and not read subtasks
                                    // count for parent task
                                    if (!oldTask.isReaded())
                                        parentTask.setSubTasksSizeNotMadeAndNotRead(parentTask.getSubTasksSizeNotMadeAndNotRead() - 1);
                                    // update parent task
                                    updateTask(parentTask);
                                }
                            }
                        } else {
                            if (hideTask(oldTask, settings.getUserName())) {
                                // get parent task
                                if (oldTask.getParentId() != null) {
                                    Task parentTask = getTaskDao_queryForId(oldTask.getParentId());
                                    parentTask.setSubTasksCountNotMade(parentTask.getSubTasksCountNotMade() + 1);
                                    // update not made and not read subtasks
                                    // count for parent task
                                    if (!oldTask.isReaded())
                                        parentTask.setSubTasksSizeNotMadeAndNotRead(parentTask.getSubTasksSizeNotMadeAndNotRead() + 1);
                                    // update parent task
                                    updateTask(parentTask);
                                }
                            }
                        }
                    }
                    if (hideTask(task, settings.getUserName()) != hideTask(oldTask, settings.getUserName())) {
                        updateStatus = true;
                        if ((task.getCustomer() != null) && (task.getCustomer() != null) && (task.getCustomer().equals(settings.getUserName()))
                                && (!task.getPerformer().equals(settings.getUserName()))) {
                            updatePerformer = true;
                        }
                        if ((task.getPerformer() != null) && (task.getCustomer() != null) && (task.getPerformer().equals(settings.getUserName()))
                                && (!task.getCustomer().equals(settings.getUserName()))) {
                            updateCustomer = true;
                        }
                    }

                    oldTask.setStatus(task.getStatus());
                    oldTask.setUsnStatus(oldTask.getUsnStatus() + 1);
                    if (oldTask.getUsn() != 0) {
                        oldTask.setUsn(0);
                    }
                }

                // update task begin customer term
                Date d1 = task.getTermCustomerBegin();
                Date d2 = oldTask.getTermCustomerBegin();
                boolean termCust = false;
                if (((d1 != null) && (d2 != null) && (!d1.equals(d2))) || ((d1 != null) && (d2 == null)) || ((d1 == null) && (d2 != null))) {
                    oldTask.setTermCustomerBegin(task.getTermCustomerBegin());
                    oldTask.setUsnCustomerTerm(oldTask.getUsnCustomerTerm() + 1);
                    termCust = true;
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task end customer term
                d1 = task.getTermCustomerEnd();
                d2 = oldTask.getTermCustomerEnd();

                if (((d1 != null) && (d2 != null) && (!d1.equals(d2))) || ((d1 != null) && (d2 == null)) || ((d1 == null) && (d2 != null))) {
                    oldTask.setTermCustomerEnd(task.getTermCustomerEnd());
                    if (!termCust) {
                        oldTask.setUsnCustomerTerm(oldTask.getUsnCustomerTerm() + 1);
                    }
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                boolean term = false;
                // update task begin term
                d1 = task.getTermBegin();
                d2 = oldTask.getTermBegin();

                if (((d1 != null) && (d2 != null) && (!d1.equals(d2))) || ((d1 != null) && (d2 == null)) || ((d1 == null) && (d2 != null))) {
                    oldTask.setTermBegin(task.getTermBegin());
                    oldTask.setUsnTerm(oldTask.getUsnTerm() + 1);
                    term = true;
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task end customer term
                d1 = task.getTermEnd();
                d2 = oldTask.getTermEnd();

                if (((d1 != null) && (d2 != null) && (!d1.equals(d2))) || ((d1 != null) && (d2 == null)) || ((d1 == null) && (d2 != null))) {
                    oldTask.setTermEnd(task.getTermEnd());
                    if (!term) {
                        oldTask.setUsnTerm(oldTask.getUsnTerm() + 1);
                    }
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task performer
                String per1 = task.getPerformer();
                String per2 = oldTask.getPerformer();
                if (((per1 != null) && (per2 != null) && (!per1.equals(per2))) || ((per1 != null) && (per2 == null)) || ((per1 == null) && (per2 != null))) {

                    if (oldTask.getCustomer().equals(settings.getUserName())) {
                        updatePerformer = true;
                        updateStatus = false;
                    }

                    oldTask.setPerformTime(new Date(Utils.getCurrentTimeWithSavings()));
                    oldTask.setUsnFieldPerformTime(oldTask.getUsnFieldPerformTime() + 1);

                    oldTask.setPerformer(task.getPerformer());
                    oldTask.setUsnEmailPerformer(oldTask.getUsnEmailPerformer() + 1);
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task project
                UUID newProjectUID = task.getProjectUid();
                UUID oldProjectUID = oldTask.getProjectUid();
                if ((newProjectUID != null && oldProjectUID != null && !newProjectUID.equals(oldProjectUID))
                        || (newProjectUID != null && oldProjectUID == null) || (newProjectUID == null && oldProjectUID != null)) {

                    upPr = true;
                    updateStatus = false;
                    oldTask.setProjectUid(task.getProjectUid());
                    oldTask.setUsnProjectUid(oldTask.getUsnProjectUid() + 1);
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                // update task contacts
                p1 = task.getContacts();
                p2 = oldTask.getContacts();

                if (((p1 != null) && (p2 != null) && (!p1.equals(p2))) || ((p1 != null) && (p2 == null)) || ((p1 == null) && (p2 != null))) {
                    oldTask.setContacts(task.getContacts());
                    oldTask.setUsnContacts(oldTask.getUsnContacts() + 1);
                    if (oldTask.getUsn() != 0)
                        oldTask.setUsn(0);
                }

                p1 = task.getCategories();
                p2 = oldTask.getCategories();

                if (!(p1 != null && p1.equals(p2)) && !(p2 != null && p2.equals(p1))) {
                    oldTask.setCategories(p1);
                    oldTask.setUsnCategories(oldTask.getUsnCategories() + 1);
                    oldTask.setUsn(0);
                }

                oldTask.setLabelsString(task.getLabelsString());

                updateTask(oldTask);

                updateTaskAuxiliaryData(settings, upPr, newProjectUID, updateStatus, oldProjectUID, updatePerformer, per1, per2, updateCustomer, oldTask);

            } else {
                // TODO look like work!
                getTaskDao().create(task);

                updateTaskAuxiliaryData(settings, upPr, task.getProjectUid(), updateStatus, null, updatePerformer, task.getPerformer(), null, updateCustomer,
                        oldTask);
            }
            // update tasks count for sliding menu filters
            updateNumberTaskAfterDelete_Add(oldTask, false, true, isUpdateTasksList);

        } catch (SQLException e) {
            Utils.toLog(e);

        } catch (ClassCastException e) {
            Utils.toLog(e);
        }
    }

    private void updateTaskAuxiliaryData(LTSettings settings, boolean upPr, UUID newProjectUID, boolean updateStatus, UUID oldProjectUID,
            boolean updatePerformer, String per1, String per2, boolean updateCustomer, Task oldTask) throws AbstractDataRequestException, SQLException {
        if (upPr) {
            if (newProjectUID != null) {
                new GetNumberOfTasksInProject(mContext, getProjectByUUId(newProjectUID), settings.getUserName()).execute(null);
            }
            if (!updateStatus) {
                if (oldProjectUID != null) {
                    Project project = getProjectByUUId(oldProjectUID);
                    if (project != null)
                        new GetNumberOfTasksInProject(mContext, project, settings.getUserName()).execute(null);
                }
            }
        }

        if (updatePerformer) {
            if (!settings.getUserName().equals(per1))
                new GetNumberOfTasksByEmail(mContext, new Email(per1, OrderInstruct.INSTRUCTI), settings.getUserName()).execute(null);
            if (!updateStatus && !settings.getUserName().equals(per2)) {
                new GetNumberOfTasksByEmail(mContext, new Email(per2, OrderInstruct.INSTRUCTI), settings.getUserName()).execute(null);
            }
        }

        if (updateCustomer) {
            new GetNumberOfTasksByEmail(mContext, new Email(oldTask.getCustomer(), OrderInstruct.INSTRUCTME), settings.getUserName()).execute(null);
        }
    }

    /**
     * get all tasks from database
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public List<Task> getAllTasks() {
        try {
            return getTaskDao().queryBuilder().selectColumns(TaskContract.FIELD_UID, TaskContract.FIELD_USN_ENTITY).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get all deleted tasks
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public List<DeletedTask> getAllDeletedTasks() {
        List<DeletedTask> tasks = new ArrayList<DeletedTask>();
        try {
            tasks = getDeletedTaskDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * РћР±РЅРѕРІР»СЏРµС‚ РїРѕР»Рµ Collapsed РІ Project in the database.
     * 
     * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
     * @author Tetiana Diachuk (diacht@gmail.com)
     * @param project
     * 
     *            РѕР±РЅРѕРІР»РµРЅРёРµ 3С… РїРѕР»РµР№: Collapsed вЂ“ СЃРІРµСЂРЅСѓС‚ Р»Рё СЌР»РµРјРµРЅС‚ (0 РёР»Рё 1) USN_Collapsed вЂ“ РЅРѕРјРµСЂ РёР·РјРµРЅРµРЅРёСЏ РїРѕР»СЏ
     *            СЃРІРµСЂРЅСѓС‚ (С‡РёСЃР»Рѕ, РЅР°С‡РёРЅР°СЏ СЃ 0) USN вЂ“ РЅРѕРјРµСЂ РёР·РјРµРЅРµРЅРёСЏ СЌР»РµРјРµРЅС‚Р° (С‡РёСЃР»Рѕ, РЅР°С‡РёРЅР°СЏ СЃ 0)
     */
    public void updateProject(Project project) {
        try {
            Project oldProject = getProjectDao().queryBuilder().where().eq(Project.FIELD_UID, project.getId()).queryForFirst();
            if (oldProject != null) {

                // update task status
                if (project.isCollapsed() != oldProject.isCollapsed()) {
                    oldProject.setCollapsed(project.isCollapsed());
                    oldProject.setUsnCollapsed(oldProject.getUsnCollapsed() + 1);
                    if (oldProject.getUsn() != 0)
                        oldProject.setUsn(0);
                    getProjectDao().update(oldProject);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void updateContactsGroup(ContactsGroup project) {
        try {
            ContactsGroup oldContactsGroup = getContactsGroupDao().queryBuilder().where().eq(ContactsGroup.FIELD_UID, project.getId()).queryForFirst();
            if (oldContactsGroup != null) {

                // update task status
                if (project.isCollapsed() != oldContactsGroup.isCollapsed()) {
                    oldContactsGroup.setCollapsed(project.isCollapsed());
                    oldContactsGroup.setUsnCollapsed(oldContactsGroup.getUsnCollapsed() + 1);
                    if (oldContactsGroup.getUsn() != 0)
                        oldContactsGroup.setUsn(0);
                    getContactsGroupDao().update(oldContactsGroup);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * update Category in the database.
     * 
     * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
     * @author Tetiana Diachuk (diacht@gmail.com)
     * @param category
     * 
     *            РѕР±РЅРѕРІР»РµРЅРёРµ 3С… РїРѕР»РµР№: Collapsed вЂ“ СЃРІРµСЂРЅСѓС‚ Р»Рё СЌР»РµРјРµРЅС‚ (0 РёР»Рё 1) USN_Collapsed вЂ“ РЅРѕРјРµСЂ РёР·РјРµРЅРµРЅРёСЏ РїРѕР»СЏ
     *            СЃРІРµСЂРЅСѓС‚ (С‡РёСЃР»Рѕ, РЅР°С‡РёРЅР°СЏ СЃ 0) USN вЂ“ РЅРѕРјРµСЂ РёР·РјРµРЅРµРЅРёСЏ СЌР»РµРјРµРЅС‚Р° (С‡РёСЃР»Рѕ, РЅР°С‡РёРЅР°СЏ СЃ 0)
     */
    public void setCategoryCollapsed(Category category, boolean collapsed) {
        try {
            category.setCollapsed(collapsed);
            Category oldCategory = getCategoryDao().queryForId(category.getId());
            if (oldCategory != null) {
                // update task status
                if (category.isCollapsed() != oldCategory.isCollapsed()) {
                    oldCategory.setCollapsed(category.isCollapsed());
                    oldCategory.setUsnCollapsed(oldCategory.getUsnCollapsed() + 1);
                    if (oldCategory.getUsn() != 0)
                        oldCategory.setUsn(0);
                    getCategoryDao().update(oldCategory);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void setContactCollapsed(Contact contact, boolean collapsed) {
        try {
            contact.setCollapsed(collapsed);
            Contact oldContact = getContactDao().queryForId(contact.getId());
            if (oldContact != null) {
                // update task status
                if (contact.isCollapsed() != oldContact.isCollapsed()) {
                    oldContact.setCollapsed(contact.isCollapsed());
                    oldContact.setUsnFieldCollapsed(oldContact.getUsnFieldCollapsed() + 1);
                    if (oldContact.getUsn() != 0)
                        oldContact.setUsnEntity(0);
                    getContactDao().update(oldContact);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void setContactsGroupCollapsed(ContactsGroup contactContract, boolean collapsed) {
        try {
            contactContract.setCollapsed(collapsed);
            ContactsGroup oldContactsGroup = getContactsGroupDao().queryForId(contactContract.getId());
            if (oldContactsGroup != null) {
                if (contactContract.isCollapsed() != oldContactsGroup.isCollapsed()) {
                    oldContactsGroup.setCollapsed(contactContract.isCollapsed());
                    oldContactsGroup.setUsnCollapsed(oldContactsGroup.getUsnCollapsed() + 1);
                    if (oldContactsGroup.getUsn() != 0)
                        oldContactsGroup.setUsn(0);
                    getContactsGroupDao().update(oldContactsGroup);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /***
     * Р’ СЃРїРёСЃРєРµ В«РЇ РїРѕСЂСѓС‡РёР»В» РѕС‚РѕР±СЂР°Р¶Р°СЋС‚СЃСЏ РІСЃРµ РµРјР°Р№Р»С‹ РёСЃРїРѕР»РЅРёС‚РµР»РµР№, РєРѕС‚РѕСЂС‹Рј С‚РµРєСѓС‰РёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ РїРѕСЂСѓС‡РёР» Р·Р°РґР°С‡Рё Рё СЌС‚Рё
     * Р·Р°РґР°С‡Рё РЅРµ Р·Р°РІРµСЂС€РµРЅС‹/РѕС‚РјРµРЅРµРЅС‹. Р•РјР°Р№Р»С‹ РІ СЃРїРёСЃРєРµ РѕС‚СЃРѕСЂС‚РёСЂРѕРІР°РЅС‹ РїРѕ Р°Р»С„Р°РІРёС‚Сѓ. Р¤РёР»СЊС‚СЂ В«РЇ РїРѕСЂСѓС‡РёР»В» - РѕС‚С„РёР»СЊС‚СЂРѕРІР°С‚СЊ
     * Р·Р°РґР°С‡Рё Сѓ РєРѕС‚РѕСЂС‹С… Р·Р°РєР°Р·С‡РёРє С‚РµРєСѓС‰РёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ, Р° РёСЃРїРѕР»РЅРёС‚РµР»СЊ РЅРµ С‚РµРєСѓС‰РёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ
     * 
     * @param userName
     * @return
     * @throws SQLException
     * @throws AbstractDataRequestException
     */
    public List<Email> getEmailsInstructI(Context context, String userName, boolean updateNumber) throws SQLException, AbstractDataRequestException {
        Where<Task, UUID> where = getTaskDao().queryBuilder() //
                .distinct().groupBy(TaskContract.FIELD_EMAIL_PERFORMER) //
                .orderBy(TaskContract.FIELD_EMAIL_PERFORMER, true)//
                .selectColumns(TaskContract.FIELD_EMAIL_PERFORMER).where();

        where = where.eq(TaskContract.FIELD_EMAIL_CUSTOMER, userName) //
                .and().ne(TaskContract.FIELD_EMAIL_PERFORMER, userName)//
                .and().ne(TaskContract.FIELD_STATUS, 1)//
                .and().ne(TaskContract.FIELD_STATUS, 7);

        if (LTSettings.getInstance(context).isMakeTaskHide() && !updateNumber) {
            filterTasksFinishedFull(where, null, userName);
            where.and(2);
        }

        final List<Task> tasks = where.query();
        final List<Email> emails = new ArrayList<Email>();

        for (Task task : tasks) {
            final Email email = new Email(task.getPerformer(), OrderInstruct.INSTRUCTI);

            if (updateNumber) {
                final int notDoneTasksCount = new GetNumberOfTasksByEmail(context, email, userName)//
                        .execute(null).getResult();
                if ((mSettings.isMakeTaskHide() && notDoneTasksCount > 0) || !mSettings.isMakeTaskHide()) {
                    emails.add(email);
                }
            } else {
                emails.add(email);
            }
        }

        filterEmployees(emails);

        return emails;
    }

    /**
     * 
     * @since 2014-06-23
     * @author Tregub Artem tregub.artem@gmail.com
     */
    private void filterEmployees(List<Email> emails) {
        if (emails.isEmpty()) {
            return;
        }

        {
            final List<Employee> employees = new ArrayList<Employee>();
            Cursor c = null;
            try {
                c = mContext.getContentResolver().query(EmployeeContract.CONTENT_URI,//
                        null, null, null, EmployeeContract.DEFAULT_SORT);

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    employees.add(new Employee(c));
                }

            } finally {
                if (c != null) {
                    c.close();
                }
            }

            for (Employee e : employees) {
                for (Email em : emails) {
                    if (e.getEmail().equals(em.getName())) {
                        em.setTitle(e.getName());
                        break;
                    }
                }
            }
            employees.clear();
        }

        final List<Employee> emps = new ArrayList<Employee>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(EmpContract.CONTENT_URI, null, null, null, EmpContract.DEFAULT_SORT);
            final int columnLogin = c.getColumnIndex(EmpContract.LOGIN);
            final int columnTitle = c.getColumnIndex(EmpContract.TITLE);
            final int columnOrders = c.getColumnIndex(EmpContract.ORDERS);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                final String email = c.getString(columnLogin);
                final String name = c.getString(columnTitle);

                if (!TextUtils.isEmpty(name) && !name.equals(email)) {
                    final Employee employee = new Employee();
                    employee.setName(name);
                    employee.setEmail(email);
                    employee.setId(c.getInt(columnOrders));

                    emps.add(employee);
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        final List<Email> newEmails = new ArrayList<Email>();
        for (Employee e : emps) {
            for (int i = 0; i < emails.size(); i++) {
                final Email email = emails.get(i);

                if (e.getEmail().equals(email.getName())) {
                    email.setTitle(e.getName());
                    email.setOrders(e.getTableId());

                    newEmails.add(email);
                    emails.remove(i);
                    break;
                }
            }
        }
        emps.clear();

        if (newEmails.isEmpty()) {
            return;
        }

        Collections.sort(newEmails);
        emails.addAll(0, newEmails);
        newEmails.clear();
    }


    public static List<Emp> getListEmps(Context context) {
        final List<Employee> verifyList = new ArrayList<Employee>();
        final List<Emp> empsListFinal = new ArrayList<Emp>();
        LTSettings.getInstance().setLastFeatureOrder(0);
        Emp emp;
        Employee employee;
        boolean isMain;
        Cursor cursor = context.getContentResolver().query(EmpContract.CONTENT_URI, null, null,
                null, EmpContract.ORDERS);
        Cursor cursorEmployees = context.getContentResolver().query(EmployeeContract.CONTENT_URI, null, null,
                null, null);
        try {
            for (cursorEmployees.moveToFirst(); !cursorEmployees.isAfterLast(); cursorEmployees.moveToNext()) {
                employee = new Employee(cursorEmployees);
                isMain = false;
                cursor.moveToFirst();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    emp = new Emp(cursor);
                    if(emp.getLogin().equals(employee.getEmail())) {
                        verifyList.add(employee);
                        isMain = true;
                        cursor.moveToLast();
                    }
                }
                /*if(!isMain) {
                    emp = new Emp();
                    emp.setLogin(employee.getEmail());
                    emp.setTitle(Emp.DEFAULT_STRING_EMP);
                    emailList.add(emp);
                }*/
            }
            //
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                emp = new Emp(cursor);
                for (Employee empVerify : verifyList)
                {
                    if(emp.getLogin().equals(empVerify.getEmail())) {
                        try {
                            if (emp.getLogin().equals(LTSettings.getInstance().getUserName()) && emp.getTitle() == null) {
                                emp.setTitle(emp.getFirstName() + " " + emp.getLastName());
                            }
                        } finally {
                            empsListFinal.add(emp);
                        }
                    }
                }
            }
        }
        finally {
            if (cursor != null)
            {
                cursor.close();
                cursorEmployees.close();
                cursor = null;
                cursorEmployees = null;
            }
        }

        return empsListFinal;
    }

    public static List<Employee> getAllPerformers(Context context) {
        final List<Employee> employees = new ArrayList<Employee>();

        try {
            final HashSet<String> uniqueEmployees = new HashSet<String>();
            final String userName = LTSettings.getInstance(context).getUserName();

            Cursor c = null;
            try {
                c = context.getContentResolver().query(EmpContract.CONTENT_URI, null, null, null,
                        EmpContract.DEFAULT_SORT);
                final int columnLogin = c.getColumnIndex(EmpContract.LOGIN);
                final int columnTitle = c.getColumnIndex(EmpContract.TITLE);

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    if (userName.equals(c.getString(columnLogin))) {
                        continue;
                    }

                    final Employee employee = new Employee();
                    employee.setEmail(c.getString(columnLogin));
                    employee.setName(c.getString(columnTitle));

                    if (uniqueEmployees.add(employee.getEmail())) {
                        employees.add(employee);
                    }
                }

            } finally {
                if (c != null) {
                    c.close();
                    c = null;
                }
            }

            {
                final ArrayList<String> emails = DbHelper.getInstance(context).getAllEmailsSorted();
                for (String email : emails) {
                    if (TextUtils.isEmpty(email) || userName.equals(email)) {
                        continue;
                    }

                    final Employee employee = new Employee();
                    employee.setEmail(email);

                    if (uniqueEmployees.add(employee.getEmail())) {
                        employees.add(employee);
                    }
                }
            }

            try {
                c = context.getContentResolver().query(EmployeeContract.CONTENT_URI,//
                        null, null, null, EmployeeContract.DEFAULT_SORT);
                final int columnEmail = c.getColumnIndex(EmployeeContract.EMAIL);
                final int columnName = c.getColumnIndex(EmployeeContract.NAME);

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    if (userName.equals(c.getString(columnEmail))) {
                        continue;
                    }

                    final Employee employee = new Employee();
                    employee.setEmail(c.getString(columnEmail));
                    employee.setName(c.getString(columnName));

                    if (uniqueEmployees.add(employee.getEmail())) {
                        employees.add(employee);
                    }
                }

            } finally {
                if (c != null) {
                    c.close();
                    c = null;
                }
            }

        } finally {
            return employees;
        }
    }

    public static List<Employee> getListEmployeesWithoutMe(Context context) {
        final List<Employee> verifyList = new ArrayList<Employee>();
        LTSettings.getInstance().setLastFeatureOrder(0);
        Employee employee;
        Cursor cursorEmployees = context.getContentResolver().query(EmployeeContract.CONTENT_URI, null, null,
                null, null);

        try {
            for (cursorEmployees.moveToFirst(); !cursorEmployees.isAfterLast(); cursorEmployees.moveToNext()) {
                employee = new Employee(cursorEmployees);
                if (employee.getEmail() != null && !employee.getEmail().equals(LTSettings.getInstance().getUserName()) ) {
                    verifyList.add(employee);
                }
            }
        } finally {
            if (cursorEmployees != null)            {
                cursorEmployees.close();
                cursorEmployees = null;
            }
        }


        return verifyList;
    }

    public static String getEmployeeName(Context context, String empEmail) {
        Employee verifyList = null;
        Employee employee;
        Cursor cursorEmployees = context.getContentResolver().query(EmployeeContract.CONTENT_URI, null, null,
                null, null);
        try {
            for (cursorEmployees.moveToFirst(); !cursorEmployees.isAfterLast(); cursorEmployees.moveToNext()) {
                employee = new Employee(cursorEmployees);
                if (employee.getEmail().equals(empEmail) ) {
                    verifyList = employee;
                }
            }
        } finally {
            if (cursorEmployees != null)            {
                cursorEmployees.close();
                cursorEmployees = null;
            }
        }

        if (verifyList == null) {
            return "";
        } else {
            return verifyList.getName();
        }
    }

    public static List<Employee> getListEmployees(Context context) {
        /*final List<Employee> emailList = new ArrayList<Employee>();
        final List<Employee> verifyList = new ArrayList<Employee>();
        final List<Employee> empsListFinal = new ArrayList<Employee>();
        LTSettings.getInstance().setLastFeatureOrder(0);
        Emp emp;
        Employee employee;
        boolean isMain;
        Cursor cursor = context.getContentResolver().query(EmpContract.CONTENT_URI, null, null,
                null, EmpContract.ORDERS);
        Cursor cursorEmployees = context.getContentResolver().query(EmployeeContract.CONTENT_URI, null, null,
                null, null);
        try {
            for (cursorEmployees.moveToFirst(); !cursorEmployees.isAfterLast(); cursorEmployees.moveToNext()) {
                employee = new Employee(cursorEmployees);
                isMain = false;
                cursor.moveToFirst();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    emp = new Emp(cursor);
                    if (emp.getLogin() != null) {
                        if (emp.getLogin().equals(employee.getEmail()) && !emp.getLogin().equals(LTSettings.getInstance().getUserName())) {
                            verifyList.add(employee);
                            isMain = true;
                            cursor.moveToLast();
                        }
                    }
                }
                if(!isMain && !employee.getEmail().equals(LTSettings.getInstance().getUserName())) {
                    employee.setName(Emp.DEFAULT_STRING_EMP);
                    emailList.add(employee);
                }
            }
            //
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                emp = new Emp(cursor);
                for (Employee empVerify : verifyList)
                {
                    if(emp.getLogin().equals(empVerify.getEmail())) {
                        empVerify.setName(emp.getTitle());
                        empsListFinal.add(empVerify);
                    }
                }
            }
        }
        finally {
            if (cursor != null)
            {
                cursor.close();
                cursorEmployees.close();
                cursor = null;
                cursorEmployees = null;
            }
        }

        empsListFinal.addAll(emailList);

        return empsListFinal;*/
        return getListEmployeesWithoutMe(context);
    }

    public static List<Employee> getListEmployeesForNav(Context context) {
        final List<Employee> emailList = new ArrayList<Employee>();
        final List<Employee> verifyList = new ArrayList<Employee>();
        final List<Employee> empsListFinal = new ArrayList<Employee>();
        LTSettings.getInstance().setLastFeatureOrder(0);
        Emp emp;
        Employee employee;
        boolean isMain;
        Cursor cursor = context.getContentResolver().query(EmpContract.CONTENT_URI, null, null,
                null, EmpContract.ORDERS);
        Cursor cursorEmployees = context.getContentResolver().query(EmployeeContract.CONTENT_URI, null, null,
                null, null);
        try {
            for (cursorEmployees.moveToFirst(); !cursorEmployees.isAfterLast(); cursorEmployees.moveToNext()) {
                employee = new Employee(cursorEmployees);
                isMain = false;
                cursor.moveToFirst();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    emp = new Emp(cursor);
                    if (emp.getLogin() != null) {
                        if (emp.getLogin().equals(employee.getEmail()) /*&& !emp.getLogin().equals(LTSettings.getInstance().getUserName())*/) {
                            verifyList.add(employee);
                            isMain = true;
                            cursor.moveToLast();
                        }
                    }
                }
                if(!isMain /*&& !employee.getEmail().equals(LTSettings.getInstance().getUserName())*/) {
                    employee.setName(Emp.DEFAULT_STRING_EMP);
                    emailList.add(employee);
                }
            }
            //
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                emp = new Emp(cursor);
                for (Employee empVerify : verifyList)
                {
                    if (emp != null && empVerify != null) {
                        if (emp.getLogin().equals(empVerify.getEmail())) {
                            empVerify.setName(emp.getTitle());
                            empsListFinal.add(empVerify);
                        }
                    }
                }
            }
        }
        finally {
            if (cursor != null)
            {
                cursor.close();
                cursorEmployees.close();
                cursor = null;
                cursorEmployees = null;
            }
        }

        empsListFinal.addAll(emailList);

        return empsListFinal;
    }

    public static List<Employee> getListEmployeesForNavNew(Context context) {
        final List<Employee> verifyList = new ArrayList<Employee>();
        LTSettings.getInstance().setLastFeatureOrder(0);
        Employee employee;
        Cursor cursorEmployees = context.getContentResolver().query(EmployeeContract.CONTENT_URI, null, null,
                null, null);

        try {
            for (cursorEmployees.moveToFirst(); !cursorEmployees.isAfterLast(); cursorEmployees.moveToNext()) {
                employee = new Employee(cursorEmployees);
                if (employee.getEmail() != null) {
                    verifyList.add(employee);
                }
            }
        } finally {
            if (cursorEmployees != null)            {
                cursorEmployees.close();
                cursorEmployees = null;
            }
        }


        return verifyList;
    }

    public static List<String> getListEmpsForWear(Context context) {
        //
        final List<String> strings = new ArrayList<String>();

        Emp emp;
        Cursor cursor = context.getContentResolver().query(EmpContract.CONTENT_URI, null, null,
                null, EmpContract.ORDERS);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            emp = new Emp(cursor);
            strings.add(emp.getLogin());
        }

        return strings;
    }

    public static String getUserNameFromEmail(Context context)
    {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(EmpContract.CONTENT_URI, null, null, null, EmpContract.DEFAULT_SORT);
            final int columnLogin = c.getColumnIndex(EmpContract.LOGIN);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                final String email = c.getString(columnLogin);

                if (LTSettings.getInstance().getUserName().equals(email)) {
                    c.close();
                    return ""+EmployeeCache.getInstance(context).find(email);
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return LTSettings.getInstance().getUserName();
    }

    public List<Email> getEmailsInstructMe(Context context, boolean updateNumber) throws SQLException, AbstractDataRequestException {
        final String userName = LTSettings.getInstance(context).getUserName();
        Where<Task, UUID> w = getTaskDao().queryBuilder().distinct().groupBy(TaskContract.FIELD_EMAIL_CUSTOMER)
                .orderBy(TaskContract.FIELD_EMAIL_CUSTOMER, true).selectColumns(TaskContract.FIELD_EMAIL_CUSTOMER).where();
        Where<Task, UUID> w1 = w.ne(TaskContract.FIELD_EMAIL_CUSTOMER, userName).and().eq(TaskContract.FIELD_EMAIL_PERFORMER, userName).and()
                .ne(TaskContract.FIELD_STATUS, 1).and().ne(TaskContract.FIELD_STATUS, 7).and().ne(TaskContract.FIELD_STATUS, 5).and()
                .ne(TaskContract.FIELD_STATUS, 8);

        if (mSettings.isMakeTaskHide() && !updateNumber) {
            filterTasksFinishedFull(w1, null, userName);
            w1.and(2);
        }
        List<Task> lt = w1.query();

        List<Email> emails = new ArrayList<Email>();
        for (int i = 0; i < lt.size(); i++) {
            Email email = new Email(lt.get(i).getCustomer(), OrderInstruct.INSTRUCTME);
            if (updateNumber) {
                int notDoneTasksCount = new GetNumberOfTasksByEmail(context, email, userName).execute(null).getResult();
                if ((mSettings.isMakeTaskHide() && notDoneTasksCount > 0) || !mSettings.isMakeTaskHide())
                    emails.add(email);
            } else
                emails.add(email);
        }

        filterEmployees(emails);

        return emails;
    }

    public void setSubtasksSize(final Map<UUID, Integer> map, final Map<UUID, Integer> mapNotMade, final Map<UUID, Integer> mapNotRead,
            final Map<UUID, Integer> mapNotMadeAndNotRead) {
        try {
            /*
             * getTaskDao().callBatchTasks(new Callable<Void>() { public Void call() throws Exception, SQLException {
             */
            for (Map.Entry<UUID, Integer> entry : map.entrySet()) {
                final Task task = getTaskDao_queryForId(entry.getKey());
                if (task != null) {
                    // TODO BUG #3421 if increased
                    // "task.getSubTasksSize....() + "
                    task.setSubTasksCount(task.getSubTasksCount() + entry.getValue());
                    if (mapNotMade.containsKey(task.getId())) {
                        task.setSubTasksCountNotMade(task.getSubTasksCountNotMade() + mapNotMade.get(task.getId()));
                    }
                    if (mapNotRead.containsKey(task.getId())) {
                        task.setSubTasksCountNotRead(task.getSubTasksCountNotRead() + mapNotRead.get(task.getId()));
                    }
                    if (mapNotMadeAndNotRead.containsKey(task.getId())) {
                        task.setSubTasksSizeNotMadeAndNotRead(task.getSubTasksSizeNotMadeAndNotRead() + mapNotMadeAndNotRead.get(task.getId()));
                    }
                    updateTask(task);
                }
            }
            /*
             * return null; } });
             */
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * РёР· РѕС‚С„РёР»СЊС‚СЂРѕРІР°РЅРЅРѕРіРѕ СЃРїРёСЃРєР° СЃРєСЂС‹РІР°СЋС‚СЃСЏ Р·Р°РґР°С‡Рё, Сѓ РєРѕС‚РѕСЂС‹С… СѓСЃС‚Р°РЅРѕРІР»РµРЅ СЃС‚Р°С‚СѓСЃ (РёР»Рё Сѓ СЂРѕРґРёС‚РµР»СЊСЃРєРѕР№ Р·Р°РґР°С‡Рё СѓСЃС‚Р°РЅРѕРІР»РµРЅ
     * СЃС‚Р°С‚СѓСЃ): 1. Р—Р°РІРµСЂС€РµРЅРѕ РёР»Рё РѕС‚РјРµРЅРµРЅРѕ 2. Р�Р»Рё РµСЃР»Рё Р·Р°РєР°Р·С‡РёРє Р·Р°РґР°С‡Рё РЅРµ С‚РµРєСѓС‰РёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ Рё СѓСЃС‚Р°РЅРѕРІР»РµРЅ СЃС‚Р°С‚СѓСЃ РіРѕС‚РѕРІРѕ
     * Рє СЃРґР°С‡Рµ РёР»Рё РѕС‚РєР»РѕРЅРµРЅРѕ
     * 
     * lt - tasks list that need to process settings - LTSettings instance that contains all application settings
     * 
     * @throws SQLException
     * 
     */
    protected List<Task> hideMakeTasks(List<Task> lt, String username) throws SQLException {
        List<Task> result = new ArrayList<Task>();
        // filter by task status
        for (Task t : lt) {
            if (!(t.getStatusType() == TaskStatus.COMPLETED || t.getStatusType() == TaskStatus.CANCELLED || (!t.getCustomer().equals(username) && (t
                    .getStatusType() == TaskStatus.READY || t.getStatusType() == TaskStatus.REJECTED)))) {
                result.add(t);
            }
        }

        lt = result;
        result = new ArrayList<Task>();
        for (int i = 0; i < lt.size(); i++) {
            result = processAllParents(lt.get(i), result, username);
        }
        return result;
    }

    /**
     * Process all parents of particular task in order to find completed parent
     * 
     * @param task
     *            - particular Task instance
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    private List<Task> processAllParents(Task task, List<Task> results, String userName) throws SQLException {
        if (task.getParentId() != null) {
            final Task parentTask = getTaskDao_queryForId(task.getParentId());

            if (parentTask != null) {
                if (!(parentTask.getStatusType() == TaskStatus.COMPLETED || parentTask.getStatusType() == TaskStatus.CANCELLED || (!parentTask.getCustomer()
                        .equals(userName) && (parentTask.getStatusType() == TaskStatus.READY || parentTask.getStatusType() == TaskStatus.REJECTED))))
                    // result.add(task);
                    processAllParents(parentTask, results, userName);
            } else
                results.add(task);
        } else
            results.add(task);
        return results;
    }

    /**
     * СЃРїСЂСЏС‚Р°С‚СЊ РїРѕРґР·Р°РґР°С‡Рё РёР· СЃРїРёСЃРєР° Р·Р°РґР°С‡
     * 
     * @param tasks
     * @return
     * @throws SQLException
     * @author Tetiana Diachuk (diacht@gmail.com)
     */
    public List<Task> hideSubTasks(List<Task> tasks) throws SQLException {
        final Set<UUID> uuids = new HashSet<UUID>();
        final List<Task> result = new ArrayList<Task>();

        for (Task task : tasks) {
            uuids.add(task.getId());
        }

        for (Task task : tasks) {
            if (!uuids.contains(task.getParentId())) {
                result.add(task);
            }
        }

        return result;
    }

    protected ArgumentHolder[] prepareTaskNotCompletedHolder(String userName) {
        if (mHolderTaskNotCompleted == null) {
            // create ArgumentHolder instance for any <?> in
            // "task not completed" sql subquery
            mHolderTaskNotCompleted = new ArgumentHolder[] { new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.COMPLETED.getCode()),
                    new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.CANCELLED.getCode()), new SelectArg(TaskContract.FIELD_EMAIL_CUSTOMER, userName),
                    new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.READY.getCode()),
                    new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.REJECTED.getCode()),
                    new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.COMPLETED.getCode()),
                    new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.CANCELLED.getCode()), new SelectArg(TaskContract.FIELD_EMAIL_CUSTOMER, userName),
                    new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.READY.getCode()),
                    new SelectArg(TaskContract.FIELD_STATUS, TaskStatus.REJECTED.getCode()) };
        } else {
            // update values
            mHolderTaskNotCompleted[2].setValue(userName);
            mHolderTaskNotCompleted[7].setValue(userName);
        }
        return mHolderTaskNotCompleted;
    }

    /**
     * delete particular tasks
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com) TODO CHECK THIS
     */
    public void deleteTasks(final List<UUID> tasks) {
        try {
            getTaskDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    final DeleteBuilder<Task, UUID> delete = getTaskDao().deleteBuilder();
                    getTaskDao_deleteIds(tasks, delete);
                    // getTaskDao().deleteIds(tasks);
                    return null;
                }
            });
        } catch (Exception e) {
            Utils.toLog(e);
            e.printStackTrace();
        }
    }

    private void getTaskDao_deleteIds(List<UUID> tasks, DeleteBuilder<Task, UUID> delete) throws SQLException {
        delete.setWhere(getTaskDao().queryBuilder().where().in(TaskContract.FIELD_UID, tasks));
        delete.delete();
    }

    /**
     * delete particular tasks from deleted table
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public void deleteTasksFromDeletedTaskTable(final List<UUID> tasks) {
        try {
            /*
             * getDeletedTaskDao().callBatchTasks(new Callable<Void>() { public Void call() throws Exception {
             */
            getDeletedTaskDao().deleteIds(tasks);
            /*
             * return null; } });
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update task category
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public void updateTaskCategory(final Set<Category> taskCategories, final Task task) {
        final TaskCategory taskCategory = new TaskCategory();
        taskCategory.setTaskUID(task.getId());

        try {
            /*
             * getTaskCategoryDao().callBatchTasks(new Callable<Void>() { public Void call() throws Exception {
             */

            for (Category category : taskCategories) {
                taskCategory.setCategoryUID(category.getId());

                getTaskCategoryDao().createOrUpdate(taskCategory);
            }
            /*
             * return null; } });
             */
        } catch (Exception e) {}
    }

    public void updateTaskCategory(final List<Category> categories, final UUID taskId) {
        if (categories.isEmpty()) {
            return;
        }

        final TaskCategory taskCategory = new TaskCategory();
        taskCategory.setTaskUID(taskId);

        try {
            for (Category category : categories) {
                taskCategory.setCategoryUID(category.getId());
                getTaskCategoryDao().createOrUpdate(taskCategory);
            }
        } catch (Exception e) {}
    }

    /**
     * update categories that received as synchronization result
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public void updateCategories(final List<Category> categories) {
        try {
            getCategoryDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {

                    // create list of categories uuid
                    final List<UUID> uuids = new ArrayList<UUID>();
                    // inflate list
                    for (Category category : categories) {
                        uuids.add(category.getId());
                    }

                    // get categories from database with particular uuid
                    final List<Category> oldCategories = getCategoryDao().queryBuilder().where().in(Category.FIELD_UID, uuids).query();
                    uuids.clear();

                    // transfer particular categories from List to HashMap
                    final Map<UUID, Category> oldCategoriesMap = new HashMap<UUID, Category>();
                    for (Category category : oldCategories) {
                        oldCategoriesMap.put(category.getId(), category);
                    }
                    oldCategories.clear();

                    for (Category newCategory : categories) {
                        final Category oldCategory = oldCategoriesMap.get(newCategory.getId());
                        if (oldCategory == null) {
                            getCategoryDao().create(newCategory);
                        }

                        else {
                            if (newCategory.getUsn() == oldCategory.getUsn()) {
                                continue;
                            }
                            oldCategory.setCreator(newCategory.getCreator());
                            oldCategory.setUsn(newCategory.getUsn());
                            if (oldCategory.getUsnParent() <= newCategory.getUsnParent()) {
                                oldCategory.setParentId(newCategory.getParentId());
                                oldCategory.setUsnParent(newCategory.getUsnParent());
                            } else {
                                oldCategory.setUsn(0);
                            }

                            if (oldCategory.getUsnCollapsed() <= newCategory.getUsnCollapsed()) {
                                oldCategory.setCollapsed(newCategory.isCollapsed());
                                oldCategory.setUsnCollapsed(newCategory.getUsnCollapsed());
                            } else {
                                oldCategory.setUsn(0);
                            }

                            if (oldCategory.getUsnOrder() <= newCategory.getUsnOrder()) {
                                oldCategory.setOrder(newCategory.getOrder());
                                oldCategory.setUsnOrder(newCategory.getUsnOrder());
                            } else {
                                oldCategory.setUsn(0);
                            }

                            if (oldCategory.getUsnName() <= newCategory.getUsnName()) {
                                oldCategory.setName(newCategory.getName());
                                oldCategory.setUsnName(newCategory.getUsnName());
                            } else {
                                oldCategory.setUsn(0);
                            }

                            if (oldCategory.getUsnComment() <= newCategory.getUsnComment()) {
                                oldCategory.setComment(newCategory.getComment());
                                oldCategory.setUsnComment(newCategory.getUsnComment());
                            } else {
                                oldCategory.setUsn(0);
                            }

                            if (oldCategory.getUsnFavorite() <= newCategory.getUsnFavorite()) {
                                oldCategory.setFaforite(newCategory.isFaforite());
                                oldCategory.setUsnFavorite(newCategory.getUsnFavorite());
                            } else {
                                oldCategory.setUsn(0);
                            }

                            if (oldCategory.getUsnGroup() <= newCategory.getUsnGroup()) {
                                oldCategory.setGroup(newCategory.isGroup());
                                oldCategory.setUsnGroup(newCategory.getUsnGroup());
                            } else {
                                oldCategory.setUsn(0);
                            }

                            if (oldCategory.getUsnShow() <= newCategory.getUsnShow()) {
                                oldCategory.setShow(newCategory.isShow());
                                oldCategory.setUsnShow(newCategory.getUsnShow());
                            } else {
                                oldCategory.setUsn(0);
                            }
                            if (oldCategory.getUsnColor() <= newCategory.getUsnColor()) {
                                oldCategory.setColor(newCategory.getColor());
                                oldCategory.setUsnColor(newCategory.getUsnColor());
                            } else
                                oldCategory.setUsn(0);
                            getCategoryDao().update(oldCategory);
                        }
                    }

                    oldCategoriesMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            Utils.toLog(e);
            e.printStackTrace();
        }
    }

    /**
     * delete particular categories
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public void deleteCategories(final List<UUID> categories) {
        try {
            getCategoryDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    getCategoryDao().deleteIds(categories);
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO CHECK THIS OUT
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public void deleteListCategory(final List<UUID> categories) {
        try {
            getCategoryDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    getCategoryDao_deleteIds(categories);
                    return null;
                }
            });
        } catch (Exception e) {
            Utils.toLog(e);
            e.printStackTrace();
        }
    }

    private void getCategoryDao_deleteIds(List<UUID> categories) throws SQLException {
        final DeleteBuilder<Category, UUID> delete = getCategoryDao().deleteBuilder();
        delete.setWhere(getCategoryDao().queryBuilder().where().in(Category.FIELD_UID, categories));
        delete.delete();
    }

    /**
     * delete particular task messages
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public void deleteTaskMessages(final List<UUID> taskUUID) {
        try {
            getTaskMessageDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    getTaskMessageDao().deleteIds(taskUUID);
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update task messages that received as synchronization result
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public void updateTaskMessages(final List<TaskMessage> taskMessages) {
        try {
            // TODO (VSH) РЅСѓР¶РЅРѕ СЃРѕС…СЂР°РЅРёС‚СЊ С‡РёСЃР»Рѕ СЃРѕРѕР±С‰РµРЅРёР№ Сѓ С‚Р°СЃРєРѕРІ (РїРѕР»Рµ
            // Task.FIELD_MESSAGES_COUNT)
            getTaskMessageDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    // create list of task messages uuid
                    List<UUID> uuids = new ArrayList<UUID>();
                    // inflate list
                    for (TaskMessage taskMessage : taskMessages)
                        uuids.add(taskMessage.getId());
                    // get task messages from database with particular uuid
                    List<TaskMessage> oldTaskMessages = getTaskMessageDao().queryBuilder().where().in(TaskMessage.FIELD_UID, uuids).query();
                    uuids.clear();
                    // transfer particular task messages from List to HashMap
                    Map<UUID, TaskMessage> oldTaskMessagesInMap = new HashMap<UUID, TaskMessage>();
                    for (TaskMessage taskMessage : oldTaskMessages)
                        oldTaskMessagesInMap.put(taskMessage.getId(), taskMessage);
                    oldTaskMessages.clear();
                    for (TaskMessage newTaskMessage : taskMessages) {
                        TaskMessage oldTaskMessage = oldTaskMessagesInMap.get(newTaskMessage.getId());
                        if (oldTaskMessage == null) {
                            getTaskMessageDao().create(newTaskMessage);
                        } else {
                            if (newTaskMessage.getUsn() != oldTaskMessage.getUsn()) {
                                oldTaskMessage.setUsn(newTaskMessage.getUsn());
                                oldTaskMessage.setId(newTaskMessage.getId());
                                oldTaskMessage.setCreator(newTaskMessage.getCreator());
                                oldTaskMessage.setTaskUID(newTaskMessage.getTaskUID());
                                oldTaskMessage.setDateCreate(newTaskMessage.getDateCreate());
                                oldTaskMessage.setDateModify(newTaskMessage.getDateModify());
                                if (oldTaskMessage.getUsnMessage() <= newTaskMessage.getUsnMessage()) {
                                    oldTaskMessage.setMessage(newTaskMessage.getMessage());
                                    oldTaskMessage.setUsnMessage(newTaskMessage.getUsnMessage());
                                } else
                                    oldTaskMessage.setUsn(0);
                                if (oldTaskMessage.getUsnIsDeleted() <= newTaskMessage.getUsnIsDeleted()) {
                                    oldTaskMessage.setIsDeleted(newTaskMessage.isDeleted());
                                    oldTaskMessage.setUsnIsDeleted(newTaskMessage.getUsnIsDeleted());
                                } else
                                    oldTaskMessage.setUsn(0);
                                getTaskMessageDao().update(oldTaskMessage);
                            }
                        }
                    }
                    oldTaskMessagesInMap.clear();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get all tasks messages
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * 
     */
    public List<TaskMessage> getAllTasksMessages() {
        try {
            return getTaskMessageDao().queryBuilder().selectColumns(TaskMessage.FIELD_UID, TaskMessage.FIELD_USN).query();
        } catch (SQLException e) {
            return new ArrayList<TaskMessage>(0);
        }
    }

    // TODO (VSH) WTF???
    public boolean hideTask(Task task, String userName) {
        if ((task.getStatus() == 1) || (task.getStatus() == 7)) {
            return true;
        } else {
            if (userName.equals(task.getCustomer()) || (task.getStatus() != 5 && task.getStatus() != 8)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * РїoР»СѓС‡РµРЅРёРµ РєРѕР»-РІР° Р·Р°РґР°С‡ РґР»СЏ РїРѕР»РµР№ СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ
     * 
     * @param name
     * @param mode
     * @param settings
     * @return FilterNumberTask instance
     * @throws SQLException
     */
    public FilterNumberTask getFilterNumberTask(String name, int mode) throws SQLException {
        return getFilterNumberTaskDao().queryBuilder().where().eq(FilterNumberTask.FIELD_NAME, name).and().eq(FilterNumberTask.FIELD_TASK_MODE, mode)
                .queryForFirst();
    }

    /**
     * РџРѕСЃС‚СЂРѕРµРЅРёРµ РїРѕР»РЅРѕРіРѕ(СЃ СѓС‡РµС‚РѕРј СЃРѕСЃС‚РѕСЏРЅРёСЏ СЂРѕРґРёС‚РµР»СЊСЃРєРѕР№ Р·Р°РґР°С‡Рё) С„РёР»СЊС‚СЂР° РєРѕС‚РѕСЂС‹Р№ РѕС‚СЃРµРёРІР°РµС‚ Р·Р°РІРµСЂС€РµРЅРЅС‹Рµ Р·Р°РґР°С‡Рё.
     * 
     * @category Р”Р›РЇ Р’РЎР•РҐ РљР РћРњР• РџРћР”Р—РђР”РђР§
     * @param where
     * @param holder
     * @throws SQLException
     */
    protected Where<Task, UUID> filterTasksFinishedFull(Where<Task, UUID> where, QueryBuilder<Task, UUID> builder, String userName) throws SQLException {
        /*
         * where.raw(Task.FIELD_STATUS + " <> ? AND " + Task.FIELD_STATUS + " <> ? AND " + "(" +
         * Task.FIELD_EMAIL_CUSTOMER + " = ? OR (" + Task.FIELD_STATUS + " <> ? AND " + Task.FIELD_STATUS + " <> ?)) " +
         * "AND " + "(" + Task.FIELD_UID_PARENT + " IS NULL OR NOT EXISTS (SELECT " + Task.FIELD_UID +
         * " FROM tasks WHERE UID = tasks." + Task.FIELD_UID_PARENT + ") OR EXISTS (SELECT " + Task.FIELD_UID +
         * " FROM tasks t2 WHERE t2.UID = tasks." + Task.FIELD_UID_PARENT + " AND " + Task.FIELD_STATUS + " <> ? AND " +
         * Task.FIELD_STATUS + " <> ? AND (" + Task.FIELD_EMAIL_CUSTOMER + " = ? OR (" + Task.FIELD_STATUS +
         * " <> ? AND " + Task.FIELD_STATUS + " <> ?))))", prepareTaskNotCompletedHolder(userName));
         */
        where.raw(TaskContract.FIELD_STATUS + " <> ? AND " + TaskContract.FIELD_STATUS + " <> ? AND " + "(" + TaskContract.FIELD_EMAIL_CUSTOMER + " = ? OR ("
                + TaskContract.FIELD_STATUS + " <> ? AND " + TaskContract.FIELD_STATUS + " <> ?)) " + "AND" + "(" + TaskContract.FIELD_UID_PARENT
                + " IS NULL OR NOT EXISTS (SELECT t1." + TaskContract.FIELD_UID
                + " FROM tasks t1 WHERE t1.UID = tasks."
                + TaskContract.FIELD_UID_PARENT
                + ") OR EXISTS (SELECT t2."// TODO Bug #3460
                + TaskContract.FIELD_UID + " FROM tasks t2 WHERE t2.lft < tasks.lft AND t2.rgt > tasks.rgt" + " AND (t2." + TaskContract.FIELD_STATUS
                + " = ? OR t2." + TaskContract.FIELD_STATUS + " = ? OR (t2." + TaskContract.FIELD_EMAIL_CUSTOMER + " <> ? AND (t2." + TaskContract.FIELD_STATUS
                + " = ? OR t2." + TaskContract.FIELD_STATUS + " = ?)))))", prepareTaskNotCompletedHolder(userName));
        return where;
    }


    protected Where<Task, UUID> filterTasksFinishedFullSubtask(Where<Task, UUID> where, QueryBuilder<Task, UUID> builder, String userName) throws SQLException {
        /*
         * where.raw(Task.FIELD_STATUS + " <> ? AND " + Task.FIELD_STATUS + " <> ? AND " + "(" +
         * Task.FIELD_EMAIL_CUSTOMER + " = ? OR (" + Task.FIELD_STATUS + " <> ? AND " + Task.FIELD_STATUS + " <> ?)) " +
         * "AND " + "(" + Task.FIELD_UID_PARENT + " IS NULL OR NOT EXISTS (SELECT " + Task.FIELD_UID +
         * " FROM tasks WHERE UID = tasks." + Task.FIELD_UID_PARENT + ") OR EXISTS (SELECT " + Task.FIELD_UID +
         * " FROM tasks t2 WHERE t2.UID = tasks." + Task.FIELD_UID_PARENT + " AND " + Task.FIELD_STATUS + " <> ? AND " +
         * Task.FIELD_STATUS + " <> ? AND (" + Task.FIELD_EMAIL_CUSTOMER + " = ? OR (" + Task.FIELD_STATUS +
         * " <> ? AND " + Task.FIELD_STATUS + " <> ?))))", prepareTaskNotCompletedHolder(userName));
         */
        where.raw(TaskContract.FIELD_STATUS + " <> ? AND " + TaskContract.FIELD_STATUS + " <> ? AND " + "(" + TaskContract.FIELD_EMAIL_CUSTOMER + " = ? OR ("
                + TaskContract.FIELD_STATUS + " <> ? AND " + TaskContract.FIELD_STATUS + " <> ?)) " + "AND" + "(" + TaskContract.FIELD_UID_PARENT
                + " IS NULL OR NOT EXISTS (SELECT t1." + TaskContract.FIELD_UID + " FROM tasks t1 WHERE t1.UID = tasks." + TaskContract.FIELD_UID_PARENT
                + ") OR NOT EXISTS (SELECT t2." + TaskContract.FIELD_UID + " FROM tasks t2 WHERE t2.lft < tasks.lft AND t2.rgt > tasks.rgt" + " AND (t2."
                + TaskContract.FIELD_STATUS + " = ? OR t2." + TaskContract.FIELD_STATUS + " = ? OR (t2." + TaskContract.FIELD_EMAIL_CUSTOMER + " <> ? AND (t2."
                + TaskContract.FIELD_STATUS + " = ? OR t2." + TaskContract.FIELD_STATUS + " = ?)))))", prepareTaskNotCompletedHolder(userName));
        return where;
    }


    protected Where<Task, UUID> filterTasksFinished(Where<Task, UUID> where, String userName) throws SQLException {
        where.raw(TaskContract.FIELD_STATUS + " <> 1 AND " + TaskContract.FIELD_STATUS + " <> 7 AND " + "(" + TaskContract.FIELD_EMAIL_CUSTOMER + " = '"
                + userName + "' OR (" + TaskContract.FIELD_STATUS + " <> 5 AND " + TaskContract.FIELD_STATUS + " <> 8))");
        return where;
    }

    /**
     * Р”РѕР±Р°РІР»РµРЅРёРµ С„РёР»СЊС‚СЂР° РєРѕС‚РѕСЂС‹Р№ РѕСЃС‚Р°РІР»СЏРµС‚ С‚РѕР»СЊРєРѕ Р·Р°РґР°С‡Рё Сѓ РєРѕС‚РѕСЂС‹С… РЅРµС‚ РєР°С‚РµРіРѕСЂРёР№ РёР»Рё РєР°С‚РµРіРѕСЂРёСЏ РЅРµ СЃСѓС‰РµСЃС‚РІСѓРµС‚ РІ
     * Р»РѕРєР°Р»СЊРЅРѕР№ Р‘Р”.
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param where
     */
    protected void filterTasksWithoutCategory(Where<Task, UUID> where) {
        where.raw("NOT EXISTS (SELECT rowid FROM task_category WHERE tasks.UID = task_category.TaskUID AND "
                + "EXISTS (SELECT * FROM category WHERE task_category.CategoryUid = category.UID))");
    }

    /**
     * РћС‚СЃРµРёРІР°РЅРёРµ Р·Р°РґР°С‡, РєРѕС‚РѕСЂС‹Рµ РёРјРµСЋС‚ СЂРѕРґРёС‚РµР»СЏ, СѓРґРѕРІР»РµС‚РІРѕСЂСЏСЋС‰РµРіРѕ СѓСЃР»РѕРІРёСЏРј С„РёР»СЊС‚СЂР° РїРѕ РѕРїСЂРµРґРµР»РµРЅРЅРѕРјСѓ email.
     * 
     * @param where
     *            - Where instance
     * @param userName
     *            - user login
     * @param email
     *            - Email instance for task customer/performer
     * @throws SQLException
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    protected Where<Task, UUID> filterTasksWithMatchingParentByEmail(Where<Task, UUID> where, String userName, Email email) throws SQLException {
        ArgumentHolder[] holder = new ArgumentHolder[] { new SelectArg(SqlType.STRING, email.getName()), new SelectArg(SqlType.STRING, userName) };
        String query = null;
        if (email.getOrderInstruct() == OrderInstruct.INSTRUCTI) {
            query = "NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND t1.EmailPerformer = ? AND t1.EmailCustomer = ?)";
        } else
            query = "NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND t1.EmailCustomer = ? AND t1.EmailPerformer = ?)";
        where.raw(query, holder);
        return where;
    }

    /**
     * РћС‚СЃРµРёРІР°РЅРёРµ РєРѕР»РёС‡РµСЃС‚РІР° Р·Р°РґР°С‡, РєРѕС‚РѕСЂС‹Рµ РёРјРµСЋС‚ СЂРѕРґРёС‚РµР»СЏ, СѓРґРѕРІР»РµС‚РІРѕСЂСЏСЋС‰РµРіРѕ СѓСЃР»РѕРІРёСЏРј С„РёР»СЊС‚СЂР° РїРѕ РѕРїСЂРµРґРµР»РµРЅРЅРѕРјСѓ email.
     * 
     * @param where
     *            - Where instance
     * @param userName
     *            - user login
     * @param email
     *            - Email instance for task customer/performer
     * @throws SQLException
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    protected Where<Task, UUID> filterTasksCountWithMatchingParentByEmail(Where<Task, UUID> where, String userName, Email email) throws SQLException {
        ArgumentHolder[] holder = new ArgumentHolder[] { new SelectArg(SqlType.STRING, email.getName()), new SelectArg(SqlType.STRING, userName) };
        String query = null;
        if (email.getOrderInstruct() == OrderInstruct.INSTRUCTI) {
            query = "NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND t1.EmailPerformer = ? AND t1.EmailCustomer = ?)";
        } else
            query = "NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND t1.EmailCustomer = ? AND t1.EmailPerformer = ?)";
        where.raw(query, holder);
        return where;
    }

    /**
     * РћС‚СЃРµРёРІР°РЅРёРµ Р·Р°РґР°С‡, РєРѕС‚РѕСЂС‹Рµ РёРјРµСЋС‚ СЂРѕРґРёС‚РµР»СЏ, СѓРґРѕРІР»РµС‚РІРѕСЂСЏСЋС‰РµРіРѕ СѓСЃР»РѕРІРёСЏРј С„РёР»СЊС‚СЂР° РїРѕ РѕРїСЂРµРґРµР»РµРЅРЅРѕРјСѓ РїСЂРѕРµРєС‚Сѓ.
     * 
     * @param where
     *            - Where instance
     * @param userName
     *            - user login
     * @param email
     *            - Email instance for task customer/performer
     * @throws SQLException
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    protected Where<Task, UUID> filterTasksWithMatchingParentByProject(Where<Task, UUID> where, ArgumentHolder[] holder) throws SQLException {
        where.raw("NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND t1.UidProject = ?)", holder);
        return where;
    }

    /**
     * РћС‚СЃРµРёРІР°РЅРёРµ Р·Р°РґР°С‡, РєРѕС‚РѕСЂС‹Рµ РёРјРµСЋС‚ СЂРѕРґРёС‚РµР»СЏ, СѓРґРѕРІР»РµС‚РІРѕСЂСЏСЋС‰РµРіРѕ СѓСЃР»РѕРІРёСЏРј С„РёР»СЊС‚СЂР° РїРѕ РѕРїСЂРµРґРµР»РµРЅРЅРѕР№ РєР°С‚РµРіРѕСЂРёРё.
     * 
     * @param where
     *            - Where instance
     * @param userName
     *            - user login
     * @param email
     *            - Email instance for task customer/performer
     * @throws SQLException
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    protected Where<Task, UUID> filterTasksWithMatchingParentByCategory(Where<Task, UUID> where, ArgumentHolder[] holder) throws SQLException {
        where.raw(
                "NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND EXISTS (SELECT task_category.TaskUID FROM task_category WHERE task_category.CategoryUID = ? AND task_category.TaskUID = t1.UID))",
                holder);
        return where;
    }

    /**
     * РћС‚СЃРµРёРІР°РЅРёРµ Р·Р°РґР°С‡, РєРѕС‚РѕСЂС‹Рµ РёРјРµСЋС‚ СЂРѕРґРёС‚РµР»СЏ, СѓРґРѕРІР»РµС‚РІРѕСЂСЏСЋС‰РµРіРѕ СѓСЃР»РѕРІРёСЏРј С„РёР»СЊС‚СЂР° РїРѕ РѕРїСЂРµРґРµР»РµРЅРЅРѕР№ РґР°С‚Рµ.
     * 
     * @param where
     *            - Where instance
     * @param userName
     *            - user login
     * @param email
     *            - Email instance for task customer/performer
     * @throws SQLException
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     */
    protected Where<Task, UUID> filterTasksWithMatchingParentByDate(Where<Task, UUID> where, String userName, Date dateBegin, Date dateEnd, Date dateToday)
            throws SQLException {
        /*
         * where.raw(
         * "NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND EXISTS (" +
         * query + "))");
         */
        where.raw("NOT EXISTS (SELECT t1.UID FROM tasks AS t1 WHERE tasks.lft > t1.lft AND tasks.rgt < t1.rgt AND "
        // TODO Bug #3460 РЎР®Р”Рђ Р‘Р« Р’РЎРўРђР’Р�РўР¬ Р Р•РљРЈР РЎР�Р’РќРЈР® РџР РћР’Р•Р РљРЈ Р РћР”Р�РўР•Р›Р•Р™
                + "((t1.TermEnd IS NOT NULL AND t1.TermBegin IS NOT NULL AND " + "((t1.TermEnd >= " + dateBegin.getTime() + " AND t1.TermBegin <= "
                + dateEnd.getTime() + " AND (t1.EmailCustomer <> '" + userName + "' OR " + "t1.EmailPerformer = '" + userName + "' ) ) OR ("
                + dateBegin.getTime() + " <= " + dateToday.getTime() + " AND " + "t1.TermEnd < " + dateBegin.getTime()
                + " AND t1.Status <> 1 AND t1.Status <> 7 AND " + "(t1.EmailCustomer = '" + userName + "' OR (t1.Status <> 5 AND t1.Status <> 8)) ) ) ) "
                + "OR (t1.TermEnd IS NULL AND t1.TermBegin IS NULL AND (t1.EmailPerformer = '" + userName + "' AND t1.EmailCustomer <> '" + userName + "' "
                + "AND t1.TermEndCustomer IS NOT NULL AND t1.TermBeginCustomer IS NOT NULL AND ((t1.TermEndCustomer >= " + dateBegin.getTime() + " AND "
                + "t1.TermBeginCustomer <= " + dateEnd.getTime() + " ) OR (" + dateBegin.getTime() + " <= " + dateToday.getTime() + " AND "
                + "t1.TermEndCustomer < " + dateBegin.getTime() + " AND t1.Status <> 1 AND t1.Status <> 7 AND " + "(t1.EmailCustomer = '" + userName
                + "' OR (t1.Status <> 5 AND t1.Status <> 8))))))))");
        return where;
    }

    /**
     * update tasks number after delete / add particular task
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * @param delete
     *            - is task will be deleted: true - task will be deleted, false - task not will be deleted
     * @param isNotUpdateSubtaskscount
     *            : true - not update, false - update
     * @throws AbstractDataRequestException
     * 
     */
    public void updateNumberTaskAfterDelete_Add(final Task task, boolean delete, boolean isNotUpdateSubtasksCount, boolean isUpdateTasksList)
            throws SQLException, AbstractDataRequestException {
        final Context context = mContext.getApplicationContext();

        final String userName = LTSettings.getInstance(context).getUserName();
        if (!isNotUpdateSubtasksCount) {
            if (task.getParentId() != null) {
                Task parentTask = getTaskDao_queryForId(task.getParentId());
                if (parentTask != null) {
                    if (delete) {
                        parentTask.setSubTasksCount(parentTask.getSubTasksCount() - 1);
                    } else {
                        parentTask.setSubTasksCount(parentTask.getSubTasksCount() + 1);
                    }

                    // change not made subtasks count for parent task
                    if (!hideTask(task, userName)) {
                        if (delete) {
                            parentTask.setSubTasksCountNotMade(parentTask.getSubTasksCountNotMade() - 1);
                        } else {
                            parentTask.setSubTasksCountNotMade(parentTask.getSubTasksCountNotMade() + 1);
                        }
                        // change not made and not read subtasks count for
                        // parent task
                        if (!task.isReaded()) {
                            if (delete) {
                                parentTask.setSubTasksSizeNotMadeAndNotRead(parentTask.getSubTasksSizeNotMadeAndNotRead() - 1);
                            } else {
                                parentTask.setSubTasksSizeNotMadeAndNotRead(parentTask.getSubTasksSizeNotMadeAndNotRead() + 1);
                            }
                        }
                    }

                    // change not read subtasks count for parent task
                    if (!task.isReaded()) {
                        if (delete) {
                            parentTask.setSubTasksCountNotRead(parentTask.getSubTasksCountNotRead() - 1);
                        } else {
                            parentTask.setSubTasksCountNotRead(parentTask.getSubTasksCountNotRead() + 1);
                        }
                    }
                    updateTask(parentTask);
                }
            }
        }

        // update tasks count for sliding menu in another thread
        /*
         * Thread thread = new Thread(new Runnable() { public void run() {
         */try {
            // update task count for "Today" filter
            new GetNumberOfTasksForToday(context, userName).execute(null);

            // update task count for "Input" filter
            new GetNumberOfIncomeTasks(context, userName).execute(null);

            if (task.getProjectUid() != null) {
                Project project = getProjectByUUId(task.getProjectUid());
                if (project != null)
                    new GetNumberOfTasksInProject(context, project, userName).execute(null);
            }

            if ((task.getPerformer() != null) && (!task.getPerformer().equals(userName))
                    && ((task.getCustomer() != null) || (task.getCustomer().equals(userName)))) {
                new GetNumberOfTasksByEmail(context, new Email(task.getPerformer(), OrderInstruct.INSTRUCTI), userName).execute(null);
            }

            if ((task.getCustomer() != null) && (!task.getCustomer().equals(userName))
                    && ((task.getPerformer() != null) || (task.getPerformer().equals(userName)))) {
                new GetNumberOfTasksByEmail(context, new Email(task.getCustomer(), OrderInstruct.INSTRUCTME), userName).execute(null);
            }

            Set<Category> cat = getCategoriesSetByTask(task);
            if (cat != null) {
                for (Category category : cat) {
                    new GetNumberOfTasksInCategory(context, category, userName).execute(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * if we assign task for particular user which email doesn't exists in database, then we need to update sliding
         * menu
         */
        /*
         * boolean isPerformerExists = false; List<String> allPerformers = getAllPerformerEmails(); for (String email :
         * allPerformers) { if (email.equals(task.getPerformer())) { isPerformerExists = true; break; } }
         */

        /*
         * if we delete last task that assigned from particular user, then we need to update sliding menu
         */
        /*
         * boolean isCustomerExists = false; List<String> allCustomers = getAllCustomerEmails(); for (String email :
         * allCustomers) { if (email.equals(task.getCustomer())) { isCustomerExists = true; break; } }
         */

        // send broadcast intent in order to update sliding menu "I assigned"
        // section
        // if (!isPerformerExists || !isCustomerExists) {
        /* final */Intent intent = new Intent();
        if (isUpdateTasksList) {
            intent.setAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
        // }

        /* final Intent */intent = new Intent();
        intent.setAction(ServiceConstants.ACTION_NOTIFY_DATASET_CHANGED_SLIDING_MENU);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        /*
         * } }); thread.start();
         */

        if (isUpdateTasksList) {
            intent = new Intent();
            intent.setAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    /**
     * Retrieve all tasks emails
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @throws SQLException
     * 
     */
    public ArrayList<String> getAllEmails() {
        try {
            final GenericRawResults<String[]> results = getTaskDao()//
                    .queryRaw("SELECT DISTINCT EmailCustomer FROM tasks UNION SELECT EmailPerformer FROM tasks");

            final ArrayList<String> emails = new ArrayList<String>();

            final Iterator<String[]> iterator = results.iterator();
            while (iterator.hasNext()) {
                final String email = iterator.next()[0];
                if (email != null) {
                    emails.add(email);
                }
            }
            results.close();

            return emails;

        } catch (SQLException e) {
            return new ArrayList<String>(0);
        }
    }

    public ArrayList<String> getAllEmailsSorted() {
        final ArrayList<String> emails = getAllEmails();
        if (!emails.isEmpty()) {
            Collections.sort(emails);
        }
        return emails;
    }

    /**
     * Retrieve all tasks performer emails
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * @throws SQLException
     * 
     */
    public ArrayList<String> getAllPerformerEmails() {
        try {
            GenericRawResults<String[]> results = getTaskDao().queryRaw("SELECT DISTINCT EmailPerformer FROM tasks");
            ArrayList<String> emails = new ArrayList<String>();
            Iterator<String[]> iterator = results.iterator();
            while (iterator.hasNext()) {
                String[] row = iterator.next();
                emails.add(row[0]);
            }
            results.close();
            return emails;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    /**
     * Retrieve all tasks customer emails
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     * @throws SQLException
     * 
     */
    public ArrayList<String> getAllCustomerEmails() {
        try {
            GenericRawResults<String[]> results = getTaskDao().queryRaw("SELECT DISTINCT EmailCustomer FROM tasks");
            ArrayList<String> emails = new ArrayList<String>();
            Iterator<String[]> iterator = results.iterator();
            while (iterator.hasNext()) {
                String[] row = iterator.next();
                emails.add(row[0]);
            }
            results.close();
            return emails;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    protected String[] getVisibleTaskFields() {
        return Task.VISIBLE_TASK_FIELDS;
    }

    /**
     * Retun number of messages in this task.
     * 
     * @param data
     * @author V.Shcryabets<vshcryabets@gmail.com>
     * @return number of messages in this task.
     * @throws SQLException
     */
    public Integer getMessagesCount(Task task) throws SQLException {
        if (task.getId() != null) {
            return (int) getTaskMessageDao().queryBuilder().where()//
                    .eq(TaskMessage.FIELD_TASK_UID, task.getId()).countOf();
        }
        return null;
    }

    /**
     * Return all categories for specified task
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @throws SQLException
     */
    public List<Category> getCategoriesListByTask(UUID taskId) throws SQLException {
        if (mPqTaskCategories == null) {
            mHolderGetCategoriesByTask = new ArgumentHolder[] { new SelectArg(TaskCategory.FIELD_TASK_UID, taskId) };

            final QueryBuilder<Category, UUID> builder = getCategoryDao().queryBuilder();
            final QueryBuilder<TaskCategory, Integer> subQueryBuilder = getTaskCategoryDao().queryBuilder();
            subQueryBuilder.selectColumns(TaskCategory.FIELD_CATEGORY_UID);
            subQueryBuilder.where().raw(TaskCategory.FIELD_TASK_UID + " = ?", mHolderGetCategoriesByTask);

            mPqTaskCategories = builder.where().in(Category.FIELD_UID, subQueryBuilder).prepare();
        } else {
            mHolderGetCategoriesByTask[0].setValue(taskId);
        }

        return getCategoryDao().query(mPqTaskCategories);
    }

    public Set<Category> getCategoriesSetByTask(Task task) throws SQLException {
        return new HashSet<Category>(getCategoriesListByTask(task.getId()));
    }

    public Set<Category> getCategoriesThatInTask(Task task) throws SQLException {
        final List<TaskCategory> taskCategories = getTaskCategoryDao()//
                .queryBuilder().where().eq(TaskCategory.FIELD_TASK_UID, task.getId()).query();

        if (taskCategories.isEmpty()) {
            return new HashSet<Category>(0);
        }

        final List<UUID> uuids = new ArrayList<UUID>(taskCategories.size());
        for (TaskCategory taskCategory : taskCategories) {
            uuids.add(taskCategory.getCategoryUID());
        }

        return new HashSet<Category>(getCategoryDao().queryBuilder().where().in(Category.FIELD_UID, uuids).query());
    }

    /**
     * Р�Р·РјРµРЅРµРЅРёРµ РїРѕР»РµР№ РѕР±СЉРµРєС‚Р° FilterNumberTask РґР»СЏ С„РёР»СЊС‚СЂР° СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ "РЎРµРіРѕРґРЅСЏ".
     * 
     * @param context
     *            - РєРѕРЅС‚РµРєСЃС‚, РёР· РєРѕС‚РѕСЂРѕРіРѕ РїСЂРѕРёСЃС…РѕРґРёС‚ РІС‹Р·РѕРІ РґР°РЅРЅРѕРіРѕ РјРµС‚РѕРґР°
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     * @throws AbstractDataRequestException
     */
    public void editsDueToTermChanged(Context context) {
        try {
            synchronized (this) {
                final LTSettings settings = LTSettings.getInstance(context);

                // get current task mode
                int taskMode = settings.getTaskMode();
                // update task count for "Today" filter
                new GetNumberOfTasksForToday(context, settings.getUserName()).execute(null);
                // if current filter is "Today" or "Inbox"
                if (taskMode == 0 || taskMode == 1) {
                    // update task count for "Income" filter
                    new GetNumberOfIncomeTasks(context, settings.getUserName()).execute(null);
                }
                // send broadcast intent in order to update sliding menu and
                // tasks list
                final Intent intent = new Intent();
                intent.setAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
                intent.setAction(ServiceConstants.ACTION_NOTIFY_DATASET_CHANGED_SLIDING_MENU);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        } catch (AbstractDataRequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. Р�Р·РјРµРЅРµРЅРёРµ РєРѕР»РёС‡РµСЃС‚РІР° СЃРґРµР»Р°РЅРЅС‹С… Р·Р°РґР°С‡ РґР»СЏ СЂРѕРґРёС‚РµР»СЊСЃРєРѕР№ Р·Р°РґР°С‡Рё РІ Р·Р°РІРёСЃРёРјРѕСЃС‚Рё РѕС‚ РЅРѕРІРѕРіРѕ СЃС‚Р°С‚СѓСЃР° РґРѕС‡РµСЂРЅРµР№ Р·Р°РґР°С‡Рё.
     * 2. Р�Р·РјРµРЅРµРЅРёРµ РєРѕР»РёС‡РµСЃС‚РІР° РЅРµ СЃРґРµР»Р°РЅРЅС‹С… Р·Р°РґР°С‡ РґР»СЏ СЌР»РµРјРµРЅС‚Р° СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ.
     * 
     * @param context
     *            - РєРѕРЅС‚РµРєСЃС‚, РёР· РєРѕС‚РѕСЂРѕРіРѕ РїСЂРѕРёСЃС…РѕРґРёС‚ РІС‹Р·РѕРІ РґР°РЅРЅРѕРіРѕ РјРµС‚РѕРґР°
     * @param parentUUID
     *            - UUID СЂРѕРґРёС‚РµР»СЊСЃРєРѕР№ Р·Р°РґР°С‡Рё
     * @param delta
     *            - Р·РЅР°С‡РµРЅРёРµ, РЅР° РєРѕС‚РѕСЂРѕРµ РЅРµРѕР±С…РѕРґРёРјРѕ РёР·РјРµРЅРёС‚СЊ РєРѕР»РёС‡РµСЃС‚РІРѕ СЃРґРµР»Р°РЅРЅС‹С… Р·Р°РґР°С‡ РґР»СЏ СЂРѕРґРёС‚РµР»СЊСЃРєРѕР№ Р·Р°РґР°С‡Рё: 1 -
     *            РµСЃР»Рё Р·Р°РґР°С‡Р° РїРѕРјРµС‡РµРЅР° РєР°Рє Р·Р°РІРµСЂС€РµРЅРЅР°СЏ; -1 - РµСЃР»Рё Р·Р°РґР°С‡Р° РїРѕРјРµС‡РµРЅР° РєР°Рє РЅРµ Р·Р°РІРµСЂС€РµРЅРЅР°СЏ
     * @param isForNotification
     *            - С„Р»Р°Рі, РєРѕС‚РѕСЂС‹Р№ РїРµСЂРµРґР°РµС‚СЃСЏ РІ РјРµС‚РѕРґ updateTask: true - РїРѕСЃР»Рµ РёР·РјРµРЅРµРЅРёСЏ Р·Р°РґР°С‡Рё РЅРµ РЅСѓР¶РЅРѕ РѕР±РЅРѕРІР»СЏС‚СЊ
     *            СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ, false - РїРѕСЃР»Рµ РёР·РјРµРЅРµРЅРёСЏ Р·Р°РґР°С‡Рё РЅСѓР¶РЅРѕ РѕР±РЅРѕРІР»СЏС‚СЊ СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     * @throws AbstractDataRequestException
     */
    public void editsDueToStatusChanged(Context context, UUID parentUUID, int delta, boolean isForNotification) {
        /*
         * change parent task's subtasks count in static lists
         */
        final FragmentManager fm = ((HomeActivity) context).getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            final Task task = TasksListFragment.getData(TasksListFragment.sPosition);
            if (task != null) {
                task.setSubTasksCountNotMade(task.getSubTasksCountNotMade() + delta);
                if (!task.isReaded()) {
                    task.setSubTasksSizeNotMadeAndNotRead(task.getSubTasksSizeNotMadeAndNotRead() + delta);
                }

                TasksListFragment.setTaskToData(TasksListFragment.sPosition, task);
            }
        }

        else if (fm.getBackStackEntryCount() > 1) {
            if (fm.findFragmentByTag(HomeActivity.TASK_LIST_TAG) != null && SubtasksListFragment.getDataSize() == 1) {
                final Task task = TasksListFragment.getData(TasksListFragment.sPosition);
                if (task != null) {
                    task.setSubTasksCountNotMade(task.getSubTasksCountNotMade() + delta);
                    if (!task.isReaded()) {
                        task.setSubTasksSizeNotMadeAndNotRead(task.getSubTasksSizeNotMadeAndNotRead() + delta);
                    }

                    TasksListFragment.setTaskToData(TasksListFragment.sPosition, task);
                }
            }

            else {
                final Task task = SubtasksListFragment.getListOfData//
                        (SubtasksListFragment.getDataSize() - 2).get(fm.findFragmentByTag(HomeActivity.TASK_LIST_TAG) == null//
                        ? SubtasksListFragment.sPosition
                                : SubtasksListFragment.sParentTaskPosition);

                task.setSubTasksCountNotMade(task.getSubTasksCountNotMade() + delta);

                if (!task.isReaded()) {
                    task.setSubTasksSizeNotMadeAndNotRead(task.getSubTasksSizeNotMadeAndNotRead() + delta);
                }

                SubtasksListFragment.getListOfData//
                        (SubtasksListFragment.getDataSize() - 2).set(fm.findFragmentByTag(HomeActivity.TASK_LIST_TAG) == null//
                        ? SubtasksListFragment.sPosition
                                : SubtasksListFragment.sParentTaskPosition, task);
            }
        }

        /*
         * change unfinished tasks count for FilterNumberTask instance
         */
        // define sliding menu item title
        final LTSettings settings = LTSettings.getInstance(context);
        final String name;
        switch (settings.getTaskMode()) {
        case 0:
            name = FilterNumberTask.RECORD_TODAY;
            break;

        case 1:
            name = FilterNumberTask.RECORD_INCOME;
            break;

        case 2:
        case 5:
            name = settings.getChooseEmail().getName();
            break;

        case 3:
            name = settings.getChooseProject().getId().toString();
            break;

        case 4:
            name = settings.getChooseCategory().getId().toString();
            break;

        default:
            name = "";
        }

        try {
            FilterNumberTask fnt = getFilterNumberTask(name, settings.getTaskMode());
            // update FilterNumberTask instance
            if (fnt != null) {
                fnt.setTaskNotDone(fnt.getTaskNotDone() - delta);
                getFilterNumberTaskDao().update(fnt);
            }
        } catch (SQLException e) {
            Utils.toLog(e);
        }
    }

    /**
     * 1. Р�Р·РјРµРЅРµРЅРёРµ С„Р»Р°РіР° "РїСЂРѕС‡РёС‚Р°РЅРѕ" С‚РµРєСѓС‰РµР№ Р·Р°РґР°С‡Рё (СѓСЃС‚Р°РЅРѕРІРєР° Р·РЅР°С‡РµРЅРёСЏ РІ true). 2. Р�Р·РјРµРЅРµРЅРёРµ РєРѕР»РёС‡РµСЃС‚РІР° РЅРµРїСЂРѕС‡РёС‚Р°РЅРЅС‹С…
     * Р·Р°РґР°С‡ РґР»СЏ СЂРѕРґРёС‚РµР»СЊСЃРєРѕР№ Р·Р°РґР°С‡Рё РёР·-Р·Р° С‚РѕРіРѕ, С‡С‚Рѕ Р·Р°РґР°С‡Р° СЃС‚Р°Р»Р° РїСЂРѕС‡РёС‚Р°РЅРЅРѕР№. 3. Р�Р·РјРµРЅРµРЅРёРµ РєРѕР»РёС‡РµСЃС‚РІР° РЅРµРїСЂРѕС‡РёС‚Р°РЅРЅС‹С…
     * Р·Р°РґР°С‡ РґР»СЏ СЌР»РµРјРµРЅС‚Р° СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ.
     * 
     * @param context
     *            - РєРѕРЅС‚РµРєСЃС‚, РёР· РєРѕС‚РѕСЂРѕРіРѕ РїСЂРѕРёСЃС…РѕРґРёС‚ РІС‹Р·РѕРІ РґР°РЅРЅРѕРіРѕ РјРµС‚РѕРґР°
     * @param task
     *            - Р·Р°РґР°С‡Р°, Сѓ РєРѕС‚РѕСЂРѕР№ РёР·РјРµРЅРµРЅ С„Р»Р°Рі "РїСЂРѕС‡РёС‚Р°РЅРѕ"
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     * @throws AbstractDataRequestException
     */
    public void editsDueToReadedFlagChanged(Context context, Task task) throws AbstractDataRequestException {
        // get LTSettings instance
        final LTSettings settings = LTSettings.getInstance(context);
        // define current filter name
        String name;
        switch (settings.getTaskMode()) {
        case 0:
            name = FilterNumberTask.RECORD_TODAY;
            break;

        case 1:
            name = FilterNumberTask.RECORD_INCOME;
            break;

        case 2:
        case 5:
            name = settings.getChooseEmail().getName();
            break;

        case 3:
            name = settings.getChooseProject().getId().toString();
            break;

        case 4:
            name = settings.getChooseCategory().getId().toString();
            break;

        default:
            name = "";
            break;
        }

        try {
            // get FilterNumberTask instance
            FilterNumberTask fnt = getFilterNumberTask(name, settings.getTaskMode());
            // update FilterNumberTask instance
            fnt.setTaskNotReadForAll(fnt.getTaskNotReadForAll() - 1);
            final List<Task> tasks = new ArrayList<Task>(1);
            tasks.add(task);

            if (hideMakeTasks(tasks, settings.getUserName()).size() == 1) {
                fnt.setTaskNotReadForNotDone(fnt.getTaskNotReadForNotDone() - 1);
            }
            getFilterNumberTaskDao().update(fnt);

            // set "is read" flag to true
            task.setReaded(true);

            // decrease by 1 count of not read subtasks for parent task
            // if (task.getParentId() != null) {
            // // get parent task
            // Task parentTask = getTaskDao_queryForId(task.getParentId());
            // if (parentTask != null) {
            // // update not made and not read subtasks count for parent task
            // if (!hideTask(task, settings.getUserName()))
            // parentTask.setSubTasksSizeNotMadeAndNotRead(parentTask.getSubTasksSizeNotMadeAndNotRead()
            // - 1);
            // // update parent task
            // updateTask(parentTask);
            // }
            // }

            // update currently selected task
            updateTask(task, true, false, true);

        } catch (SQLException e) {
            Utils.toLog(e);
        }
    }

    /**
     * Р�Р·РјРµРЅРµРЅРёРµ РїРѕР»РµР№ РѕР±СЉРµРєС‚РѕРІ FilterNumberTask РґР»СЏ С„РёР»СЊС‚СЂРѕРІ "РљР°С‚РµРіРѕСЂРёРё" СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ.
     * 
     * @param context
     *            - РєРѕРЅС‚РµРєСЃС‚, РёР· РєРѕС‚РѕСЂРѕРіРѕ РїСЂРѕРёСЃС…РѕРґРёС‚ РІС‹Р·РѕРІ РґР°РЅРЅРѕРіРѕ РјРµС‚РѕРґР°
     * @param categories
     *            - СЃРёРјРјРµС‚СЂРёС‡РµСЃРєР°СЏ СЂР°Р·РЅРѕСЃС‚СЊ РјРµР¶РґСѓ РЅРѕРІС‹РјРё Рё СЃС‚Р°СЂС‹РјРё РєР°С‚РµРіРѕСЂРёСЏРјРё (РєР°С‚РµРіРѕСЂРёРё, РЅРµ РїСЂРёРЅР°РґР»РµР¶Р°С‰РёРµ РѕРґРЅРѕРІСЂРµРјРµРЅРЅРѕ
     *            Рё Рє СЃС‚Р°СЂС‹Рј РєР°С‚РµРіРѕСЂРёСЏРј, Рё Рє РЅРѕРІС‹Рј РєР°С‚РµРіРѕСЂРёСЏРј)
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     * @throws AbstractDataRequestException
     */
    public void editsDueToCategoriesChanged(Context context, Set<Category> categories) {
        try {
            // update task count for particular category filter
            for (Category category : categories) {
                new GetNumberOfTasksInCategory(context, category,//
                        LTSettings.getInstance(context).getUserName()).execute(null);
            }
        } catch (AbstractDataRequestException e) {
            Utils.toLog(e);
        }
    }

    /**
     * Р�Р·РјРµРЅРµРЅРёРµ РїРѕР»РµР№ РѕР±СЉРµРєС‚РѕРІ FilterNumberTask РґР»СЏ С„РёР»СЊС‚СЂРѕРІ "РџСЂРѕРµРєС‚С‹" СЃР»Р°Р№РґРёРЅРі РјРµРЅСЋ.
     * 
     * @param context
     *            - РєРѕРЅС‚РµРєСЃС‚, РёР· РєРѕС‚РѕСЂРѕРіРѕ РїСЂРѕРёСЃС…РѕРґРёС‚ РІС‹Р·РѕРІ РґР°РЅРЅРѕРіРѕ РјРµС‚РѕРґР°
     * @param projects
     *            - РЅР°Р±РѕСЂ, СЃРѕРґРµСЂР¶Р°С‰РёР№ СЃС‚Р°СЂС‹Р№ Рё РЅРѕРІС‹Р№ РїСЂРѕРµРєС‚ Р·Р°РґР°С‡Рё
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     * @throws AbstractDataRequestException
     */
    // @Deprecated
    // public void editsDueToProjectChanged(Context context, Set<Project>
    // projects) {
    // try {
    // // update task count for particular project filter
    // for (Project project : projects)
    // new GetNumberOfTasksInProject(context, project,
    // ((LeaderTaskApplication)
    // context.getApplicationContext()).getSettings().getUserName())
    // .execute(null);
    // // send broadcast intent in order to update sliding menu and tasks list
    // final Intent intent = new Intent();
    // intent.setAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
    // intent.setAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGEDSLIDINGMENU);
    // LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    // } catch (AbstractDataRequestException e) {
    // e.printStackTrace();
    // }
    // }

    /* ============== @author Tregub Artem tregub.artem@gmail.com ============== */

    /**
     * РџСЂРѕС†РµРґСѓСЂР° РїРѕРґСЃС‡РµС‚Р° РІСЃРµС… РїРѕРґР·Р°РґР°С‡ Р·Р°РґР°С‡ РїСЂРё РїРѕРјРѕС‰Рё СЃРїРёСЃРєР°
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public void calculateFirstTasksSubtasks(Context context, List<Task> tasks) {
        final String currentUser = LTSettings.getInstance(context).getUserName();

        for (Task task : tasks) {
            calculateFirstTaskSubtasks(context, currentUser, task, tasks);
        }
    }

    /**
     * РћСЃРЅРѕРІРЅР°СЏ СЂРµРєСѓСЂСЃРёРІРЅР°СЏ С„СѓРЅРєС†РёСЏ. РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє СЃРІРѕРёС… РїРѕРґР·Р°РґР°С‡ Рё РґР°Р»СЊС€Рµ РїРµСЂРµРґР°РµС‚ РёРј СѓРїСЂР°РІР»РµРЅРёРµ. РџРѕСЃР»РµРґРЅСЏСЏ Р·Р°РґР°С‡Р°
     * Р±РµР· РїРѕРґР·Р°РґР°С‡ РїСЂРѕСЃС‡РёС‚С‹РІР°РµС‚ СЃРµР±СЏ, Р»РѕР¶РёС‚ РІ Р±Р°Р·Сѓ Р·РЅР°С‡РµРЅРёСЏ Рё С‚Р°Рє РґРѕ СЃР°РјРѕР№ РІРµСЂС…РЅРµР№ Рё РїРѕСЃР»РµРґРЅРµР№ Р·Р°РґР°С‡Рё.
     */
    private FourIntValues calculateFirstTaskSubtasks(Context context, String currentUser,//
            Task parentTask, List<Task> tasks) {
        final UUID parentParentUUID = parentTask.getParentId();
        final boolean isParentComplete = TaskUtils.isCompleted(parentTask, currentUser);
        final FourIntValues v = new FourIntValues();

        try {
            final List<Task> childTasks = filterForParentTask(parentTask, tasks);
            for (Task task : childTasks) {
                if (task.isWasCounted()) {
                    addSubtaskFourIntValues(isParentComplete, v, task, currentUser);
                } else {
                    if (!task.getId().equals(parentParentUUID)) {
                        v.add(calculateFirstTaskSubtasks(context, currentUser, task, tasks));
                    }
                }
            }
        } catch (StackOverflowError e) {
            CursorySyncLogger.getInstance(context).toLog(e);
        }

        parentTask.setSubTasksCount(v.All);
        parentTask.setSubTasksCountNotRead(v.NotReaded);
        parentTask.setSubTasksCountNotMade(v.NotCompleted);
        parentTask.setSubTasksCountNotMadeAndNotRead(v.NotCompletedNotRead);
        parentTask.setWasCounted(true);

        return getTaskFourIntValues(isParentComplete, v, parentTask, currentUser);
    }

    /** Р¤СѓРЅРєС†РёСЏ РїСЂРѕСЃС‡РµС‚Р° Р·Р°РґР°С‡Рё РґР»СЏ РµС‘ СЂРѕРґРёС‚РµР»СЏ (РµСЃР»Рё РѕРЅР° РµС‰Рµ РЅРµ РёРјРµРµС‚СЃСЏ РІ Р±Р°Р·Рµ) */
    private static FourIntValues getTaskFourIntValues(boolean isParentComplete, FourIntValues v,//
            Task task, String currentUser) {
        v.All = 1;
        v.NotCompleted = isParentComplete ? 0 : 1;

        return mainCalculateLogic(isParentComplete, isParentComplete, v,//
                !task.isReaded() ? 1 : 0, task.getSubTasksCountNotRead(), task.getSubTasksCountNotMadeAndNotRead());
    }

    /** Р¤СѓРЅРєС†РёСЏ РїСЂРѕСЃС‡РµС‚Р° Р·Р°РґР°С‡Рё РґР»СЏ РµС‘ СЂРѕРґРёС‚РµР»СЏ (РµСЃР»Рё РѕРЅР° СѓР¶Рµ РёРјРµРµС‚СЃСЏ РІ Р±Р°Р·Рµ) */
    private static void addSubtaskFourIntValues(boolean isParentComplete, FourIntValues v, Task task, String currentUser) {
        final boolean isCompleted = TaskUtils.isCompleted(task, currentUser);

        v.All++;
        v.NotCompleted += isCompleted ? 0 : 1;

        mainCalculateLogic(isCompleted, isParentComplete, v,//
                !task.isReaded() ? 1 : 0, task.getSubTasksCountNotRead(), task.getSubTasksCountNotMadeAndNotRead());
    }

    /** Р¤РёР»СЊС‚СЂР°С†РёСЏ Р·Р°РґР°С‡ РёР· СЃРїРёСЃРєР° РїРѕ СЃРѕРѕС‚РІРµС‚СЃС‚РІРёСЋ РїРѕР»СЏ getParentId */
    private List<Task> filterForParentTask(Task parentTask, List<Task> tasks) {
        final List<Task> childTasks = new ArrayList<Task>();

        for (Task task : tasks) {
            if (parentTask.getId().equals(task.getParentId())) {
                childTasks.add(task);
            }
        }

        return childTasks;
    }

    // ---------------------------CURSOR---------------------------

    /**
     * РџСЂРѕС†РµРґСѓСЂР° РїРѕРґСЃС‡РµС‚Р° РІСЃРµС… РїРѕРґР·Р°РґР°С‡ Р·Р°РґР°С‡ РїСЂРё РїРѕРјРѕС‰Рё РєСѓСЂСЃРѕСЂР°
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public void recalculateVerticalTaskSubtasks(Context context, String currentUser, Task task) {
        final List<String> uuids = new ArrayList<String>(1);
        uuids.add(String.valueOf(task.getId()));

        calculateVerticalTasksSubtasks(context, currentUser, uuids);
    }

    /**
     * РџСЂРѕС†РµРґСѓСЂР° РїРѕРґСЃС‡РµС‚Р° РІСЃРµС… РїРѕРґР·Р°РґР°С‡ Р·Р°РґР°С‡ РїСЂРё РїРѕРјРѕС‰Рё РєСѓСЂСЃРѕСЂР°
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public void calculateVerticalTasksSubtasks(Context context, String currentUser, List<String> uuids) {
        int[] colums = null;
        final ContentResolver cr = context.getContentResolver();

        final ContentValues cv = new ContentValues();
        cv.put(TaskContract.WAS_COUNTED, 0);
        cr.update(TaskContract.CONTENT_URI, cv, null, null);

        while (!uuids.isEmpty()) {
            uuids = getVerticalTasksSubtasks(currentUser, uuids, cr, cv, colums);
        }
    }

    private List<String> getVerticalTasksSubtasks(String currentUser, List<String> uuids,//
            ContentResolver cr, ContentValues cv, int[] colums) {
        final Cursor c = cr.query(TaskContract.CONTENT_URI, null,//
                TaskContract.selectionFieldUidInList(uuids), null, null);
        final int columUid = c.getColumnIndex(TaskContract.FIELD_UID);
        final int columParentUid = c.getColumnIndex(TaskContract.FIELD_UID_PARENT);

        uuids.clear();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            calculateTaskSubtasks(c, currentUser, c.getString(columUid), colums, cr, cv);

            final String parentUUID = c.getString(columParentUid);
            if (!TextUtils.isEmpty(parentUUID)) {
                uuids.add(parentUUID);
            }
        }
        c.close();

        return uuids;
    }

    /**
     * РџСЂРѕС†РµРґСѓСЂР° РїРѕРґСЃС‡РµС‚Р° РІСЃРµС… РїРѕРґР·Р°РґР°С‡ Р·Р°РґР°С‡ РїСЂРё РїРѕРјРѕС‰Рё РєСѓСЂСЃРѕСЂР°
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public void calculateTasksSubtasks(Context context, String currentUser) {
        final ContentResolver cr = context.getContentResolver();
        final ContentValues cv = new ContentValues();
        int[] colums = null;

        final Cursor c = cr.query(TaskContract.CONTENT_URI, null, null, null, null);
        final int columUid = c.getColumnIndex(TaskContract.FIELD_UID);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            calculateTaskSubtasks(c, currentUser, c.getString(columUid), colums, cr, cv);
        }
        c.close();
    }

    /**
     * РћСЃРЅРѕРІРЅР°СЏ СЂРµРєСѓСЂСЃРёРІРЅР°СЏ С„СѓРЅРєС†РёСЏ. РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє СЃРІРѕРёС… РїРѕРґР·Р°РґР°С‡ Рё РґР°Р»СЊС€Рµ РїРµСЂРµРґР°РµС‚ РёРј СѓРїСЂР°РІР»РµРЅРёРµ. РџРѕСЃР»РµРґРЅСЏСЏ Р·Р°РґР°С‡Р°
     * Р±РµР· РїРѕРґР·Р°РґР°С‡ РїСЂРѕСЃС‡РёС‚С‹РІР°РµС‚ СЃРµР±СЏ, Р»РѕР¶РёС‚ РІ Р±Р°Р·Сѓ Р·РЅР°С‡РµРЅРёСЏ Рё С‚Р°Рє РґРѕ СЃР°РјРѕР№ РІРµСЂС…РЅРµР№ Рё РїРѕСЃР»РµРґРЅРµР№ Р·Р°РґР°С‡Рё.
     */
    private static final FourIntValues calculateTaskSubtasks(Cursor current, String currentUser, String uid, int[] colums, ContentResolver cr, ContentValues cv) {
        final Cursor child = cr.query(TaskContract.CONTENT_URI, null, TaskContract.selectionFieldUidParent(uid), null, null);
        if (colums == null) {
            colums = new int[8];
            colums[0] = current.getColumnIndex(TaskContract.FIELD_UID);
            colums[1] = current.getColumnIndex(TaskContract.FIELD_UID_PARENT);
            colums[2] = current.getColumnIndex(TaskContract.FIELD_READED);
            colums[3] = current.getColumnIndex(TaskContract.FIELD_STATUS);
            colums[4] = current.getColumnIndex(TaskContract.FIELD_EMAIL_CUSTOMER);
            colums[5] = current.getColumnIndex(TaskContract.WAS_COUNTED);
            colums[6] = current.getColumnIndex(TaskContract.SUBTASKS_SIZE_NOT_READ);
            colums[7] = current.getColumnIndex(TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ);
        }

        final String parentParentUID = current.getString(colums[1]);
        final boolean isParentComplete = TaskUtils.isCompleted//
                (current.getInt(colums[3]), current.getString(colums[4]), currentUser);
        final FourIntValues v = new FourIntValues();

        try {
            if (child.getCount() > 0) {
                for (child.moveToFirst(); !child.isAfterLast(); child.moveToNext()) {
                    if (child.getInt(colums[5]) == 1) {
                        addSubtaskFourIntValues(isParentComplete, v, child, colums, currentUser);
                    } else {
                        final String childUid = child.getString(colums[0]);
                        if (!childUid.equals(parentParentUID)) {
                            v.add(calculateTaskSubtasks(child, currentUser, childUid, colums, cr, cv));
                        }
                    }
                }
            }
        } catch (StackOverflowError e) {
            final CursorySyncLogger logger = CursorySyncLogger.getInstance(null);
            if (logger != null) {
                logger.toLog(e);
            }
        } finally {
            child.close();
        }

        cv.clear();
        cv.put(TaskContract.SUBTASKS_SIZE, v.All);
        cv.put(TaskContract.SUBTASKS_SIZE_NOT_READ, v.NotReaded);
        cv.put(TaskContract.SUBTASKS_SIZE_NOT_MADE, v.NotCompleted);
        cv.put(TaskContract.SUBTASKS_SIZE_NOT_MADE_AND_NOT_READ, v.NotCompletedNotRead);
        cv.put(TaskContract.WAS_COUNTED, 1);
        cr.update(TaskContract.CONTENT_URI, cv, TaskContract.selectionFieldUid(uid), null);

        getTaskFourIntValues(isParentComplete, v, current, colums, currentUser);

        return v;
    }

    /** Р¤СѓРЅРєС†РёСЏ РїСЂРѕСЃС‡РµС‚Р° Р·Р°РґР°С‡Рё РґР»СЏ РµС‘ СЂРѕРґРёС‚РµР»СЏ (РµСЃР»Рё РѕРЅР° РµС‰Рµ РЅРµ РёРјРµРµС‚СЃСЏ РІ Р±Р°Р·Рµ) */
    private static FourIntValues getTaskFourIntValues(boolean isParentComplete, FourIntValues v, Cursor c, int[] colums, String currentUser) {
        v.All = 1;
        v.NotCompleted = isParentComplete ? 0 : 1;

        return mainCalculateLogic(isParentComplete, isParentComplete, v,//
                c.getInt(colums[2]) == 0 ? 1 : 0, c.getInt(colums[6]), c.getInt(colums[7]));
    }

    /** Р¤СѓРЅРєС†РёСЏ РїСЂРѕСЃС‡РµС‚Р° Р·Р°РґР°С‡Рё РґР»СЏ РµС‘ СЂРѕРґРёС‚РµР»СЏ (РµСЃР»Рё РѕРЅР° СѓР¶Рµ РёРјРµРµС‚СЃСЏ РІ Р±Р°Р·Рµ) */
    private static void addSubtaskFourIntValues(boolean isParentComplete, FourIntValues v, Cursor c, int[] colums, String currentUser) {
        final boolean isCompleted = TaskUtils.isCompleted//
                (c.getInt(colums[3]), c.getString(colums[4]), currentUser);
        v.All++;
        v.NotCompleted += isCompleted ? 0 : 1;

        mainCalculateLogic(isCompleted, isParentComplete, v,//
                c.getInt(colums[2]) == 0 ? 1 : 0, c.getInt(colums[6]), c.getInt(colums[7]));
    }

    private static FourIntValues mainCalculateLogic(boolean isCompleted, boolean isParentComplete,//
            FourIntValues v, int notReaded, int notReadedCount, int notCompletedNotReadCont) {
        v.NotReaded += isParentComplete || isCompleted ? 0 : notReaded;
        v.NotCompletedNotRead += isCompleted ? 0 : notReaded;

        v.NotReaded += notReadedCount;
        v.NotCompletedNotRead += notCompletedNotReadCont;

        return v;
    }

    /** РџСЂРѕСЃС‚РѕР№ РєР»Р°СЃСЃ РґР»СЏ С„СѓРЅРєС†РёРё "calculateCountForTask" */
    public static class FourIntValues {
        public int All;
        public int NotReaded;
        public int NotCompleted;
        public int NotCompletedNotRead;

        public void add(FourIntValues v) {
            All += v.All;
            NotReaded += v.NotReaded;
            NotCompleted += v.NotCompleted;
            NotCompletedNotRead += v.NotCompletedNotRead;
        }
    }

    /** РњРµС‚РѕРґ СѓСЃС‚Р°РЅР°РІР»РёРІР°СЋС‰РёР№ РЅР°Р»РёС‡РёРµ Сѓ Р·Р°РґР°С‡Рё С„Р°Р№Р»РѕРІ */
    public static void calculateFilesInTask(Context context) {
        final ContentResolver cr = context.getContentResolver();
        final Cursor files = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionDeleteObject(false), null, null);

        final int columnTaskUUID = files.getColumnIndex(TaskFileContract.FIELD_TASKUID);
        final HashSet<String> tasks = new HashSet<String>();
        for (files.moveToFirst(); !files.isAfterLast(); files.moveToNext()) {
            tasks.add(files.getString(columnTaskUUID));
        }
        files.close();

        final ContentValues cv = new ContentValues();
        cv.put(TaskContract.HAS_FILES, 0);

        cr.update(TaskContract.CONTENT_URI, cv, null, null);

        if (tasks.size() > 0) {
            cv.clear();
            cv.put(TaskContract.HAS_FILES, 1);

            final Iterator<String> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                final String uuid = iterator.next();
                cr.update(TaskContract.CONTENT_URI, cv, TaskContract.selectionFieldUid(uuid), null);
            }
        }
    }

    /** РњРµС‚РѕРґ РїРµСЂРµСЃС‡РёС‚С‹РІР°СЋС‰РёР№ РЅР°Р»РёС‡РёРµ Сѓ Р·Р°РґР°С‡Рё С„Р°Р№Р»РѕРІ */
    public boolean recalculateFilesInTask(Context context, Task task) {
        final ContentResolver cr = context.getContentResolver();
        final Cursor files = cr.query(TaskFileContract.CONTENT_URI, null,
                TaskFileContract.selectionFieldTaskUidAndDeleteObject(task.getId().toString(), false), null, null);

        final boolean hasFiles = files.getCount() > 0;
        files.close();

        final ContentValues cv = new ContentValues();
        cv.put(TaskContract.HAS_FILES, hasFiles ? 1 : 0);

        cr.update(TaskContract.CONTENT_URI, cv, TaskContract.selectionFieldUid(task.getId().toString()), null);

        return hasFiles;
    }

    /** РњРµС‚РѕРґ СѓСЃС‚Р°РЅР°РІР»РёРІР°СЋС‰РёР№ РєРѕР»РёС‡РµСЃС‚РІРѕ СЃРѕРѕР±С‰РµРЅРёР№ Сѓ Р·Р°РґР°С‡ */
    public void calculateTaskMessagesInTask(Context context) {
        final List<TaskMessage> taskMessages;
        try {
            taskMessages = getTaskMessageDao().queryForAll();
        } catch (SQLException e) {
            Utils.toLog(e);
            return;
        }

        if (taskMessages.isEmpty()) {
            return;
        }

        final HashMap<String, Integer> msgsInTsk = new HashMap<String, Integer>();
        for (TaskMessage taskMessage : taskMessages) {
            final String uuid = String.valueOf(taskMessage.getTaskUID());
            if (msgsInTsk.containsKey(uuid)) {
                msgsInTsk.put(uuid, msgsInTsk.get(uuid) + 1);
            } else {
                msgsInTsk.put(uuid, 1);
            }
        }

        final ContentValues cv = new ContentValues();
        cv.put(TaskContract.MESSAGES_COUNT, 0);

        final ContentResolver cr = context.getContentResolver();
        cr.update(TaskContract.CONTENT_URI, cv, null, null);

        if (msgsInTsk.isEmpty()) {
            return;
        }

        final Iterator<Entry<String, Integer>> iterator = msgsInTsk.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, Integer> entry = iterator.next();

            cv.clear();
            cv.put(TaskContract.MESSAGES_COUNT, entry.getValue());

            cr.update(TaskContract.CONTENT_URI, cv, TaskContract.selectionFieldUid(entry.getKey()), null);
        }
    }

    /** РњРµС‚РѕРґ РїРµСЂРµСЃС‡РёС‚С‹РІР°СЋС‰РёР№ РєРѕР»РёС‡РµСЃС‚РІРѕ СЃРѕРѕР±С‰РµРЅРёР№ Сѓ Р·Р°РґР°С‡ */
    public int recalculateTaskMessagesInTask(Context context, Task task) {
        int messagesCount;
        try {
            messagesCount = (int) getTaskMessageDao().queryBuilder().where()//
                    .eq(TaskMessage.FIELD_TASK_UID, task.getId()).countOf();
        } catch (SQLException e) {
            Utils.toLog(e);
            messagesCount = 0;
        }

        final ContentValues cv = new ContentValues();
        cv.put(TaskContract.MESSAGES_COUNT, messagesCount);

        context.getContentResolver().update(TaskContract.CONTENT_URI, cv, TaskContract.selectionFieldUid(task.getId().toString()), null);

        return messagesCount;
    }

}