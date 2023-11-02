package com.ashberrysoft.leadertask.data_providers.network;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.modern.builder.XmlSoap;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.exception.ExceptionReason;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandlerPutAll;
import com.google.firebase.iid.FirebaseInstanceId;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutAll extends BaseTimeSOAPall<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutAll";
    private static final String TAGS = "tags";
    private static final String PROJECTS = "prjs";
    private static final String CONTACT_GROUPS = "contacts_groups";
    private static final String CONTACT_FILES = "contact_files";
    private static final String MARKERS = "markers";
    private static final String TASKS = "tasks";
    private static final String CONTACTS = "contacts";
    private static final String TASK_MESSAGES = "task_msgs";
    private static final String EMPS = "emps";
    private static final String TASK_FIILES = "task_files";
    public static final String OBJECTS_TO_VERIFY = "objects_to_verify";
    public static final String OBJ_CLIENT_TO_VERIFY = "ObjClient_ToVerify";
    public static final String _STR_UID = "_str_uid";
    public static final String _USN_ENTITY = "_usn_entity";
    public static final String OBJECTS_TO_REMOVE = "objects_to_remove";
    public static final String OBJ_CLIENT_TO_REMOVE = "ObjClient_ToRemove";
    public static final String REM_TAGS = "tags_to_remove";
    public static final String REM_PROJECTS = "projects_to_remove";
    public static final String REM_CONTACTS = "contacts_to_remove";
    public static final String REM_CONTACTS_GROUPS = "contacts_groups_to_remove";
    public static final String REM_CONTACT_FILES = "contact_files_to_remove";
    public static final String REM_MARKERS = "markers_to_remove";
    public static final String REM_TASKS = "tasks_to_remove";
    public static final String REM_TASK_FILES = "task_files_to_remove";
    public static final String REM_EMPS = "emps_to_remove";

    private List<Category> mEntitiesCategories;
    private List<Project> mEntitiesProjects;
    private List<Contact> mEntitiesContacts;
    private List<ContactsGroup> mEntitiesContactsGroups;
    private List<Marker> mEntitiesMarkers;
    private List<TaskMessage> mEntitiesTaskMessages;
    private List<LTask> mEntitiesTasks;
    private List<TaskFile> mEntitiesTaskFiles;
    private boolean mIsPutAfterGetSessionChanges = false;

    public PutAll(Context context, LeaderTaskUser user, List<Category> entitiesCategory, List<Project> entitiesProject, List<ContactsGroup> entitiesContactsGroup, List<Contact> entitiesContact, List<Marker> entitiesMarker,
                  List<TaskMessage> entitiesTaskMessages,  List<LTask> entitiesTasks, List <TaskFile> entitiesTaskFiles, boolean isPutAfterGetSessionChanges) {
        super(context, METHOD_NAME, user);
        mEntitiesCategories = entitiesCategory;
        mEntitiesProjects = entitiesProject;
        mEntitiesContacts = entitiesContact;
        mEntitiesContactsGroups = entitiesContactsGroup;
        mEntitiesMarkers = entitiesMarker;
        mEntitiesTaskMessages = entitiesTaskMessages;
        mEntitiesTasks = entitiesTasks;
        mEntitiesTaskFiles = entitiesTaskFiles;
        mIsPutAfterGetSessionChanges = isPutAfterGetSessionChanges;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        //
        writer.write(getOpen(TAGS));
        if (mEntitiesCategories != null && !mEntitiesCategories.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Category entity : mEntitiesCategories) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(TAGS));
        //
        writer.write(getOpen(PROJECTS));
        if (mEntitiesProjects != null && !mEntitiesProjects.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Project entity : mEntitiesProjects) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(PROJECTS));
        //
        writer.write(getOpen(CONTACTS));
        if (mEntitiesContacts != null && !mEntitiesContacts.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Contact entity : mEntitiesContacts) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(CONTACTS));
        //
        writer.write(getOpen(CONTACT_GROUPS));
        if (mEntitiesContactsGroups != null && !mEntitiesContactsGroups.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (ContactsGroup entity : mEntitiesContactsGroups) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(CONTACT_GROUPS));
        //
        final Cursor s = mContext.getContentResolver().query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.ContactsFileContract.selectionSendFile(true), null, null);

        writer.write(getOpen(CONTACT_FILES));
        if (s.getCount() > 0) {
            final StringBuilder sb = new StringBuilder();
            final ContactFile ContactFile = new ContactFile();

            for (s.moveToFirst(); !s.isAfterLast(); s.moveToNext()) {
                ContactFile.setData(s);
                Utils.clearStringBuilder(sb);
                ContactFile.getLionEntity(sb);

                writer.write(sb.toString());
            }
        }
        writer.write(getClose(CONTACT_FILES));
        s.close();

        {
            final ContentValues cv = new ContentValues(1);
            cv.put(LeaderTaskProviderMetaData.ContactsFileContract.SEND_FILE, 0);
            mContext.getContentResolver().update(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, cv,
                    LeaderTaskProviderMetaData.ContactsFileContract.selectionSendFile(true), null);
        }
        //
        writer.write(getOpen(MARKERS));
        if (mEntitiesMarkers != null && !mEntitiesMarkers.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Marker entity : mEntitiesMarkers) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(MARKERS));
        //
        writer.write(getOpen(TASKS));
        if (mEntitiesTasks != null && !mEntitiesTasks.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (LTask entity : mEntitiesTasks) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(TASKS));
        //
        writer.write(getOpen(TASK_MESSAGES));
        if (mEntitiesTaskMessages != null && !mEntitiesTaskMessages.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (TaskMessage entity : mEntitiesTaskMessages) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(TASK_MESSAGES));
        //
        writer.write(getOpen(TASK_FIILES));
        if (mEntitiesTaskFiles != null && !mEntitiesTaskFiles.isEmpty()) {
            //
            final StringBuilder sb = new StringBuilder();
            for (TaskFile entity : mEntitiesTaskFiles) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(TASK_FIILES));

        {
            final ContentValues cv = new ContentValues(1);
            cv.put(LeaderTaskProviderMetaData.TaskFileContract.SEND_FILE, 0);
            mContext.getContentResolver().update(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, cv,
                    LeaderTaskProviderMetaData.TaskFileContract.selectionSendFile(true), null);
        }
        //
        final Cursor r = mContext.getContentResolver().query(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.EmpContract.USN_ENTITY +"=0", null, null);

        writer.write(getOpen(EMPS));
        if (r.getCount() > 0) {

            final StringBuilder sb = new StringBuilder();
            final Emp emp = new Emp();
            for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                emp.setData(r);
                Utils.clearStringBuilder(sb);
                emp.getLionEntity(sb);

                writer.write(sb.toString());
            }
        }
        writer.write(getClose(EMPS));
        r.close();

        {
            final ContentValues cv = new ContentValues(1);
            cv.put(LeaderTaskProviderMetaData.EmpContract.SEND_ENTITY, 0);
            mContext.getContentResolver().update(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, cv,//
                    LeaderTaskProviderMetaData.EmpContract.selectionSendEntity(true), null);
        }

        if (mIsPutAfterGetSessionChanges) {
            // добавить удаленные
            generateCategory(writer);
            generateProjects(writer);
            generateContacts(writer);
            generateContactGroups(writer);
            generateContactFiles(writer);
            generateMarkers(writer);
            generateTasks(writer);
            generateTaskFiles(writer);
            generateEmps(writer);
        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        writer.write(getOpen("push_token"));
        writer.write(refreshedToken == null ? "" : refreshedToken);
        writer.write(getClose("push_token"));


        JSONObject object = null;
        try {
            object = new JSONObject(LTSettings.getInstance().getSettingsJson());

            if (object != null) {
                writer.write(getOpen("settings"));

                try {
                    String value = object.getString("add_task_to_begin");
                    writer.write(getOpen("add_task_to_begin"));
                    writer.write(value);
                    writer.write(getClose("add_task_to_begin"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_add_task_to_begin");
                    writer.write(getOpen("__usn_field_add_task_to_begin"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_add_task_to_begin"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("cal_number_of_first_week");
                    writer.write(getOpen("cal_number_of_first_week"));
                    writer.write(value);
                    writer.write(getClose("cal_number_of_first_week"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_cal_number_of_first_week");
                    writer.write(getOpen("__usn_field_cal_number_of_first_week"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_cal_number_of_first_week"));
                } catch (Exception e) {}
                //
                try {
                    String value = object.getString("cal_show_week_number");
                    writer.write(getOpen("cal_show_week_number"));
                    writer.write(value);
                    writer.write(getClose("cal_show_week_number"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_cal_show_week_number");
                    writer.write(getOpen("__usn_field_cal_show_week_number"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_cal_show_week_number"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("nav_show_tags");
                    writer.write(getOpen("nav_show_tags"));
                    writer.write(value);
                    writer.write(getClose("nav_show_tags"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_nav_show_tags");
                    writer.write(getOpen("__usn_field_nav_show_tags"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_nav_show_tags"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("nav_show_overdue");
                    writer.write(getOpen("nav_show_overdue"));
                    writer.write(value);
                    writer.write(getClose("nav_show_overdue"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_nav_show_overdue");
                    writer.write(getOpen("__usn_field_nav_show_overdue"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_nav_show_overdue"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("nav_show_summary");
                    writer.write(getOpen("nav_show_summary"));
                    writer.write(value);
                    writer.write(getClose("nav_show_summary"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_nav_show_summary");
                    writer.write(getOpen("__usn_field_nav_show_summary"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_nav_show_summary"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("nav_show_emps");
                    writer.write(getOpen("nav_show_emps"));
                    writer.write(value);
                    writer.write(getClose("nav_show_emps"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_nav_show_emps");
                    writer.write(getOpen("__usn_field_nav_show_emps"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_nav_show_emps"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("nav_show_markers");
                    writer.write(getOpen("nav_show_markers"));
                    writer.write(value);
                    writer.write(getClose("nav_show_markers"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_nav_show_markers");
                    writer.write(getOpen("__usn_field_nav_show_markers"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_nav_show_markers"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("language");
                    writer.write(getOpen("language"));
                    writer.write(value);
                    writer.write(getClose("language"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_language");
                    writer.write(getOpen("__usn_field_language"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_language"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("stopwatch");
                    writer.write(getOpen("stopwatch"));
                    writer.write(value);
                    writer.write(getClose("stopwatch"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_stopwatch");
                    writer.write(getOpen("__usn_field_stopwatch"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_stopwatch"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("reminders_in_n_minutes");
                    writer.write(getOpen("reminders_in_n_minutes"));
                    writer.write(value);
                    writer.write(getClose("reminders_in_n_minutes"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_reminders_in_n_minutes");
                    writer.write(getOpen("__usn_field_reminders_in_n_minutes"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_reminders_in_n_minutes"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("cal_work_time");
                    writer.write(getOpen("cal_work_time"));
                    writer.write(value);
                    writer.write(getClose("cal_work_time"));
                } catch (Exception e) {}

                try {
                    String value = object.getString("__usn_field_cal_work_time");
                    writer.write(getOpen("__usn_field_cal_work_time"));
                    writer.write(value);
                    writer.write(getClose("__usn_field_cal_work_time"));
                } catch (Exception e) {}

                writer.write(getClose("settings"));

            }
        } catch (Exception e) {

        } finally {

            LTSettings.getInstance().setNeedToPutSettings(false);
        }

        //
    }

    @Override
    protected void writeClearSessionChanges(OutputStreamWriter writer) throws Exception {

    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);
        /*final Dao<Category, UUID> dao = dbHelper.getCategoryDao();
        final DatabaseConnection connection = dao.startThreadConnection();
        dao.setAutoCommit(connection, false);*/
        LTApplication mApp = (LTApplication) mContext.getApplicationContext();

        try {
            //Парсим ProcessResponse
            final SwitchParseHandlerPutAll handler = SwitchParseHandlerPutAll.newInstance(inputStream);
            // Category ////////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<Category> entityCategory = handler.getDataCategory();

            if (!entityCategory.getListDelete().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityCategory.getListDelete(), Category.SERVER_CLASS);
                dbHelper.deleteListCategory(convertStringsToUUIDs(entityCategory.getListDelete()));
            }

            if (!entityCategory.getListChange().isEmpty()) {
                dbHelper.updateCategories(entityCategory.getListChange());
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            ///Project//////////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<Project> entityProject = handler.getDataProjects();

            if (!entityProject.getListDelete().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityProject.getListDelete(), Project.SERVER_CLASS);
                dbHelper.deleteProjects(convertStringsToUUIDs(entityProject.getListDelete()));
            }

            if (!entityProject.getListChange().isEmpty()) {
                dbHelper.updateProjects(entityProject.getListChange());
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            /////Contacts///////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<Contact> entityContact = handler.getDataContacts();

            if (!entityContact.getListDelete().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityContact.getListDelete(), LeaderTaskProviderMetaData.ContactContract.SERVER_CLASS);
                dbHelper.deleteContacts(convertStringsToUUIDs(entityContact.getListDelete()));
            }

            if (!entityContact.getListChange().isEmpty()) {
                dbHelper.updateContacts(entityContact.getListChange());
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            ///ContactGroups////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<ContactsGroup> entityContactGroups = handler.getDataContactGroups();

            if (!entityContactGroups.getListDelete().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityContactGroups.getListDelete(), ContactsGroup.SERVER_CLASS);
                dbHelper.deleteContactsGroups(convertStringsToUUIDs(entityContactGroups.getListDelete()));
            }

            if (!entityContactGroups.getListChange().isEmpty()) {
                dbHelper.updateContactsGroups(entityContactGroups.getListChange());
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            //////ContactFiles////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<ContactFile> entityContactFile = handler.getDataContactFiles();
            final ContentResolver cr = mContext.getContentResolver();
            final LTApplication app = (LTApplication) mContext.getApplicationContext();

            boolean wasChange = false;
            for (String uid : entityContactFile.getListDelete()) {
                deleteContactFileAndFile(app, cr, uid);
                wasChange = true;
            }

            if (ProcessContactFiles.addOrUpdateContactFiles(app.getAppFolder(), cr, entityContactFile.getListChange())) {
                wasChange = true;
            }

            if (wasChange) {
                //DbHelper.calculateFilesInContact(app);
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            /////Markers////////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<Marker> entityMarker = handler.getDataMarkers();

            if (!entityMarker.getListDelete().isEmpty()) {
                dbHelper.deleteMarkers(convertStringsToUUIDs(entityMarker.getListDelete()));
            }

            if (!entityMarker.getListChange().isEmpty()) {
                dbHelper.updateMarkers(entityMarker.getListChange());
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            ////Tasks/////////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity entityTasks = handler.getDataTasks();

            if (!entityTasks.getListDelete().isEmpty()) {
                deleteTasks(entityTasks.getListDelete(),mApp);
            }

            if (!entityTasks.getListChange().isEmpty()) {
                updateTasks(entityTasks.getListChange(), mApp);
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            /////TaskMessages//////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<TaskMessage> entityTaskMessage = handler.getDataTaskMessages();
            boolean calculate = false;

            if (!entityTaskMessage.getListDelete().isEmpty()) {
                dbHelper.deleteTaskMessages(convertStringsToUUIDs(entityTaskMessage.getListDelete()));
                calculate = true;
            }

            if (!entityTaskMessage.getListChange().isEmpty()) {
                dbHelper.updateTaskMessages(entityTaskMessage.getListChange());
                calculate = true;
            }

            if (calculate) {
                dbHelper.calculateTaskMessagesInTask(mContext);
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            ////TaskFiles///////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<TaskFile> entityTaskFile = handler.getDataTaskFiles();

            boolean wasChangeTaskFile = false;
            for (String uid : entityTaskFile.getListDelete()) {
                deleteTaskFileAndFile(app, cr, uid);
                wasChangeTaskFile = true;
            }

            if (ProcessTaskFiles.addOrUpdateTaskFiles(app.getAppFolder(), cr, entityTaskFile.getListChange())) {
                wasChangeTaskFile = true;
            }

            if (wasChangeTaskFile) {
                DbHelper.calculateFilesInTask(app); // ne rabotaet

            }
            ////////////////////////////////////////////////////////////////////////////////////////

            /////Emps///////////////////////////////////////////////////////////////////////////////
            final BaseLionPutEntity<Emp> entityEmp = handler.getDataEmps();

            if (!entityEmp.getListDelete().isEmpty()) {
                mContext.getContentResolver().delete(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI,
                        LeaderTaskProviderMetaData.SelectionKeeper.in(null, LeaderTaskProviderMetaData.EmpContract.UID, entityEmp.getListDelete()), null);

                Emp.updateTaskUserOrderAfterDelete(mContext, entityEmp.getListDelete());
            }

            Emp.addOrUpdateEntity(mContext, entityEmp.getListChange());
            ////////////////////////////////////////////////////////////////////////////////////////

        } finally {

            return null;
        }
    }

    private void deleteTaskFileAndFile(LTApplication app, ContentResolver cr, String uid) {
        final Cursor c = cr.query(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.TaskFileContract.selectionFieldUid(uid), null, null);

        try {
            while (c.moveToNext()) {
                if (c.isFirst()) {
                    new File(app.getAppFolder(), c.getString(c.getColumnIndex(LeaderTaskProviderMetaData.TaskFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(TaskFileContract.CONTENT_URI, TaskFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, LeaderTaskProviderMetaData.TaskFileContract.selectionFieldUid(uid));
                }
            }
        }
        catch (Exception e)
        {
            Utils.toLog(e);
        }
        finally {
            if(c!= null)
            {
                c.close();
            }
        }
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_CATEGORIES;
    }

    private void deleteContactFileAndFile(LTApplication app, ContentResolver cr, String uid) {
        final Cursor c = cr.query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.ContactsFileContract.selectionFieldUid(uid), null, null);

        try {
            while (c.moveToNext()) {
                if (c.isFirst()) {
                    new File(app.getAppFolder(), c.getString(c.getColumnIndex(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(ContactsFileContract.CONTENT_URI, ContactsFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, LeaderTaskProviderMetaData.ContactsFileContract.selectionFieldUid(uid));
                }
            }
        }
        catch (Exception e)
        {
            Utils.toLog(e);
        }
        finally {
            if(c!= null)
            {
                c.close();
            }
        }
    }

    private void deleteContact(ContentResolver cr, String uid) {

        String where = uid;
        String[] params = new String[]{};

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI)
                .withSelection(where, params)
                .build());
        try {
            cr.applyBatch(LeaderTaskProviderMetaData.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean updateTasks(List<LTask> entities, LTApplication mApp) {
        if (entities.size() == 0) {
            return false;
        }
        StringBuilder mSb = new StringBuilder();
        LTask task = new LTask();

        boolean updateEntities = false;
        Cursor c = null;

        try {
            Utils.clearStringBuilder(mSb);
            c =mApp.getContentResolver().query(task.getContentUri(), null, in(mSb, LionMetaData.BaseLionColumns.Uid, entities), null, null);

            if (c.moveToFirst()) {
                final int columnUid = c.getColumnIndex(LionMetaData.BaseLionColumns.Uid);
                final int columnUsnEntity = c.getColumnIndex(LionMetaData.BaseLionColumns.UsnEntity);

                String uid;
                LTask entity;

                final ArrayList<ContentProviderOperation> update = new ArrayList<>(c.getCount());
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    uid = c.getString(columnUid);

                    for (Iterator<LTask> iterator = entities.iterator(); iterator.hasNext();) {
                        entity = iterator.next();
                        if (uid.equals(entity.getUid())) {
                            iterator.remove();

                            if (c.getLong(columnUsnEntity) != entity.getUsnEntity()) {
                                task.fillFromCursor(c);

                                Utils.clearStringBuilder(mSb);
                                update.add(ContentProviderOperation.newUpdate(task.getContentUri()).withValues(task.getDifference(entity))
                                        .withSelection(LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.BaseLionColumns.Uid, uid), null).build());
                                //LTSettings.getInstance().getTasksToUpdate().add(entity.getUid());
                            }

                            break;
                        }
                    }
                }

                if (update.size() > 0) {
                    try {
                        mApp.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, update);
                        updateEntities = true;

                    } catch (Exception e) {
                        Utils.toLog(e);
                    }
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (addEntities(entities, mApp)) {
            for (LTask entity : entities) {
                //LTSettings.getInstance().getTasksToUpdate().add(entity.getUid());
            }
            updateEntities = true;
        }

        return updateEntities;
    }

    private String in(StringBuilder sb, String columnName, List<LTask> entities) {
        sb.append(columnName);
        sb.append(SharedStrings.IN);
        sb.append(SharedStrings.BRACE_OPEN_C);

        boolean start = true;
        for (LTask entity : entities) {
            if (start) {
                start = false;

            } else {
                sb.append(SharedStrings.COMMA_C);
            }

            sb.append(SharedStrings.QUOTE_C);
            sb.append(entity.getUid());
            sb.append(SharedStrings.QUOTE_C);
        }
        sb.append(SharedStrings.BRACE_CLOSE_C);

        return sb.toString();
    }

    private boolean deleteTasks(List<String> uids, LTApplication mApp) {
        try {
            StringBuilder mSb = new StringBuilder();
            LTask task = new LTask();
            final boolean deleteEntities = uids.size() > 0;

            if (deleteEntities) {
                for (int i=0; i < uids.size()-1; i++) {
                    String uid = uids.get(i);
                    //
                    Cursor c = null;
                    try {
                        Utils.clearStringBuilder(mSb);
                        c = mApp.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.LTaskContract.Uid, uid), null, null);
                        if (c.getCount() > 0) {
                            final int columnId = c.getColumnIndex(LionMetaData.LTaskContract._ID);
                            final int columnUid = c.getColumnIndex(LionMetaData.LTaskContract.Uid);
                            c.moveToFirst();
                            //LTSettings.getInstance().getTasksToDelete().add(""+c.getInt(columnId));
                            //LTSettings.getInstance().getTasksToUpdate().add(""+c.getInt(columnUid));
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }//
                }
                Utils.clearStringBuilder(mSb);
                mApp.getContentResolver().delete(task.getContentUri(),
                        LeaderTaskProviderMetaData.SelectionKeeper.in(mSb, LionMetaData.BaseLionColumns.Uid, uids), null);

                mSb.append(SharedStrings.AND);
                mApp.getContentResolver().delete(LionMetaData.DeleteUidContract.CONTENT_URI,
                        LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.DeleteUidContract.LionName, task.getLionName()), null);

            }

            return deleteEntities;

        } catch (Exception e) {
            throw LeaderException.create(ExceptionReason.SQLITE, e);
        }
    }

    protected boolean addEntities(List<LTask> entities, LTApplication mApp) {
        try {

            StringBuilder mSb = new StringBuilder();
            LTask task = new LTask();
            final boolean addEntities = entities.size() > 0;

            if (entities.size() > 0) {
                final ContentValues[] cvs = new ContentValues[entities.size()];
                int count = 0;
                for (LTask entity : entities) {
                    cvs[count] = entity.getContentValues(null);
                    if(!cvs[count].get(LionMetaData.LTaskContract.EmailPerformer).toString().equals(LTSettings.getInstance().getUserName()) &&
                            !cvs[count].get(LionMetaData.LTaskContract.EmailCustomer).toString().equals(LTSettings.getInstance().getUserName()) &&
                            !cvs[count].getAsBoolean(LionMetaData.LTaskContract.Readed)){

                        if(hasProjectWithQuite(cvs[count].get(LionMetaData.LTaskContract.UidProject).toString())) {
                            //меняем тут всё
                            int mUsnFieldReaded = cvs[count].getAsInteger(LionMetaData.LTaskContract.UsnFieldReaded).intValue() + 1;
                            cvs[count].remove(LionMetaData.LTaskContract.UsnFieldReaded);
                            cvs[count].remove(LionMetaData.LTaskContract.UsnEntity);
                            cvs[count].remove(LionMetaData.LTaskContract.Readed);

                            cvs[count].put(LionMetaData.LTaskContract.UsnFieldReaded, mUsnFieldReaded);
                            cvs[count].put(LionMetaData.LTaskContract.UsnEntity, 0);
                            cvs[count].put(LionMetaData.LTaskContract.Readed, true);
                        }
                    }
                    count++;
                }

                mApp.getContentResolver().bulkInsert(task.getContentUri(), cvs);
            }

            return addEntities;

        } catch (Exception e) {
            throw LeaderException.create(ExceptionReason.SQLITE, e);
        }
    }

    private boolean hasProjectWithQuite(String uid) throws SQLException,
            AbstractDataRequestException {
        if (uid != null) {
            Cursor c = null;
            try {
                c = DbHelper.getInstance(mContext).getWritableDatabase().rawQuery("SELECT * FROM projects WHERE projects.UID = '"+uid.toLowerCase()+"' AND projects.Quiet = '1'", null);
            } finally {
                if (c != null) {
                    if(c.getCount()>0) {
                        c.close();
                        return true;
                    }
                    c.close();
                    return false;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    private void generateCategory(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(REM_TAGS));
            final Cursor r = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                    LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(Category.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(LeaderTaskProviderMetaData.UidToDeleteContract.UID);
                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(columnUid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            writer.write(getClose(REM_TAGS));
        } catch (IOException e) {

        }
    }

    private void generateContactGroups(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(REM_CONTACTS_GROUPS));
            //
            final Cursor r = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                    LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(ContactsGroup.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(LeaderTaskProviderMetaData.UidToDeleteContract.UID);
                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(columnUid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            //
            writer.write(getClose(REM_CONTACTS_GROUPS));
        } catch (IOException e) {

        }
    }

    private void generateContacts(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(REM_CONTACTS));
            //
            final Cursor r = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                    LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(LeaderTaskProviderMetaData.ContactContract.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(LeaderTaskProviderMetaData.UidToDeleteContract.UID);
                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(columnUid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            //
            writer.write(getClose(REM_CONTACTS));
        } catch (IOException e) {

        }
    }

    private void generateContactFiles(OutputStreamWriter writer) {
        try {
            final ContentResolver cr = mContext.getContentResolver();
            writer.write(getOpen(REM_CONTACT_FILES));
            //
            final Cursor r = cr.query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.ContactsFileContract.selectionDeleteObject(true),
                    null, null);
            if (r.getCount() > 0) {
                final int uid = r.getColumnIndex(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_UID);

                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(uid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            //
            writer.write(getClose(REM_CONTACT_FILES));
        } catch (IOException e) {

        }
    }

    private void generateMarkers(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(REM_MARKERS));
            //
            final Cursor r = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                    LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(Marker.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(LeaderTaskProviderMetaData.UidToDeleteContract.UID);
                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(columnUid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            //
            writer.write(getClose(REM_MARKERS));
        } catch (IOException e) {

        }
    }

    private void generateTasks(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(REM_TASKS));
            //
            Cursor remove = null;

            try {
                remove = mContext.getContentResolver().query(LionMetaData.DeleteUidContract.CONTENT_URI, null,
                        LeaderTaskProviderMetaData.SelectionKeeper.equals(null, LionMetaData.DeleteUidContract.LionName,  LionMetaData.LTaskContract.TABLE_NAME), null, null);
                if (remove.getCount() > 0) {
                    final int columnUid = remove.getColumnIndex(LionMetaData.DeleteUidContract.Uid);

                    for (remove.moveToFirst(); !remove.isAfterLast(); remove.moveToNext()) {
                        writer.write(XmlSoap.getOpen(OBJ_CLIENT_TO_REMOVE));

                        writer.write(XmlSoap.getOpen(_STR_UID));
                        writer.write(remove.getString(columnUid));
                        writer.write(XmlSoap.getClose(_STR_UID));

                        writer.write(XmlSoap.getClose(OBJ_CLIENT_TO_REMOVE));
                    }
                }

            } finally {
                if (remove != null) {
                    remove.close();
                }
            }
            //
            writer.write(getClose(REM_TASKS));
        } catch (IOException e) {

        }
    }

    private void generateTaskFiles(OutputStreamWriter writer) {
        try {
            final ContentResolver cr = mContext.getContentResolver();
            writer.write(getOpen(REM_TASK_FILES));
            //
            final Cursor r = cr.query(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.TaskFileContract.selectionDeleteObject(true),
                    null, null);
            if (r.getCount() > 0) {
                final int uid = r.getColumnIndex(LeaderTaskProviderMetaData.TaskFileContract.FIELD_UID);

                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(uid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            //
            writer.write(getClose(REM_TASK_FILES));
        } catch (IOException e) {

        }
    }

    private void generateEmps(OutputStreamWriter writer) {
        try {
            Emp.checkDefaultEmpCreated(mContext);

            final ContentResolver cr = mContext.getContentResolver();
            writer.write(getOpen(REM_EMPS));
            //
            final Cursor r = cr.query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                    LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(LeaderTaskProviderMetaData.EmpContract.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int uid = r.getColumnIndex(LeaderTaskProviderMetaData.UidToDeleteContract.UID);

                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(uid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            //
            writer.write(getClose(REM_EMPS));
        } catch (IOException e) {

        }
    }

    private void generateProjects(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(REM_PROJECTS));
            final Cursor r = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                    LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(Project.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(LeaderTaskProviderMetaData.UidToDeleteContract.UID);
                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(columnUid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            writer.write(getClose(REM_PROJECTS));
        } catch (IOException e) {

        }
    }
}