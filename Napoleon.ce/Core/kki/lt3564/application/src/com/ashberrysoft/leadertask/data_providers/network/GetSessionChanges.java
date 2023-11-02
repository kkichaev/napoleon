package com.ashberrysoft.leadertask.data_providers.network;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
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
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequest;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.DeleteUid;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.exception.ExceptionReason;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandlerProcessAll;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GetSessionChanges extends BaseTimeSOAPall<Serializable> implements ProcessSOAPRequest {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "GetSessionChanges";

    private List<Contact> mListContactsFotos = new ArrayList<>();
    private ArrayList<Emp> mListEmpsFotos = new ArrayList<>();
    private Context mContext;

    private List<UUID> mListSendCategory;
    private List<UUID> mListSendProject;
    private List<UUID> mListSendContacts;
    private List<UUID> mListSendContactGroups;
    private List<UUID> mListSendMarkers;
    private List<String> mListSendTaks;
    private List<UUID> mListSendTaskMessages;
    private String mSessionOrder;
    private String mErrorCode;
    private List<String> mTasksForMeNew;
    private List<String> mTasksByMeCanceled;
    private List<String> mTasksWhereMessages;

    public GetSessionChanges(Context context,LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
        mContext = context;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {

    }

    @Override
    protected void writeClearSessionChanges(OutputStreamWriter writer) throws Exception {

    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);
        LTApplication mApp = (LTApplication) mContext.getApplicationContext();
        final ContentResolver cr = mContext.getContentResolver();
        final LTApplication app = (LTApplication) mContext.getApplicationContext();

        try {
            //Парсим ProcessResponse
            final SwitchParseHandlerProcessAll handler = SwitchParseHandlerProcessAll.newInstance(inputStream);
            mSessionOrder = handler.getSessionOrder();
            mErrorCode = handler.getErrorCode();
            // Category ////////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<Category> entityCategory = handler.getDataCategory();

            if (!entityCategory.getListDelete().isEmpty()) {
                // обновить тотал
                StringBuilder selection = new StringBuilder();
                for (String uid: entityCategory.getListDelete()) {
                    if (!selection.toString().equals("")) {
                        selection.append(" AND ");
                    }
                    selection.append("uid = '"+uid+"'");
                }
                mContext.getContentResolver().delete(LionMetaData.CategoryTotalLinkContract.CONTENT_URI, selection.toString(), null);
                mContext.getContentResolver().delete(LionMetaData.CategoryLinkContract.CONTENT_URI, selection.toString(), null);
                dbHelper.deleteCategories(convertStringsToUUIDs(entityCategory.getListDelete()));
            }

            mListSendCategory = convertStringsToUUIDs(entityCategory.getListSend());

            if (!entityCategory.getListProcess().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityCategory.getListProcess(), Category.SERVER_CLASS);
                dbHelper.deleteCategories(convertStringsToUUIDs(entityCategory.getListProcess()));
            }

            if (!entityCategory.getListAdd().isEmpty()) {
                dbHelper.updateCategories(entityCategory.getListAdd());
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            ///Project//////////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<Project> entityProject = handler.getDataProjects();

            if (!entityProject.getListDelete().isEmpty()) {
                // обновить тотал
                StringBuilder selection = new StringBuilder();
                for (String uid: entityProject.getListDelete()) {
                    if (!selection.toString().equals("")) {
                        selection.append(" AND ");
                    }
                    selection.append("uid = '"+uid+"'");
                }
                mContext.getContentResolver().delete(LionMetaData.ProjectTotalLinkContract.CONTENT_URI, selection.toString(), null);
                mContext.getContentResolver().delete(LionMetaData.ProjectLinkContract.CONTENT_URI, selection.toString(), null);
                dbHelper.deleteProjects(convertStringsToUUIDs(entityProject.getListDelete()));

            }

            mListSendProject = convertStringsToUUIDs(entityProject.getListSend());

            if (!entityProject.getListProcess().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityProject.getListProcess(), Project.SERVER_CLASS);
                dbHelper.deleteProjects(convertStringsToUUIDs(entityProject.getListProcess()));
            }

            if (!entityProject.getListAdd().isEmpty()) {
                dbHelper.updateProjects(entityProject.getListAdd());
            }

            ////////////////////////////////////////////////////////////////////////////////////////

            ////ContactGroups///////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<ContactsGroup> entityContactGroups = handler.getDataContactGroups();

            if (!entityContactGroups.getListDelete().isEmpty()) {
                dbHelper.deleteContactsGroups(convertStringsToUUIDs(entityContactGroups.getListDelete()));
            }

            mListSendContactGroups = convertStringsToUUIDs(entityContactGroups.getListSend());

            if (!entityContactGroups.getListProcess().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityContactGroups.getListProcess(), ContactsGroup.SERVER_CLASS);
                dbHelper.deleteContactsGroups(convertStringsToUUIDs(entityContactGroups.getListProcess()));
            }

            if (!entityContactGroups.getListAdd().isEmpty()) {
                dbHelper.updateContactsGroups(entityContactGroups.getListAdd());
            }

            ////////////////////////////////////////////////////////////////////////////////////////

            ////Contacts////////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<Contact> entityContacts = handler.getDataContacts();

            if (!entityContacts.getListDelete().isEmpty()) {
                mContext.getContentResolver().delete(LeaderTaskProviderMetaData.ContactContract.CONTENT_URI,
                        SelectionKeeper.inToLowerCase(null, LeaderTaskProviderMetaData.ContactContract.UID, entityContacts.getListDelete()), null);
                mContext.getContentResolver().notifyChange(LeaderTaskProviderMetaData.ContactContract.CONTENT_URI, null);

            }

            mListSendContacts = convertStringsToUUIDs(entityContacts.getListSend());

            if (!entityContacts.getListProcess().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityContacts.getListProcess(), LeaderTaskProviderMetaData.ContactContract.SERVER_CLASS);
                dbHelper.deleteContacts(convertStringsToUUIDs(entityContacts.getListProcess()));
            }

            if (!entityContacts.getListAdd().isEmpty()) {
                dbHelper.updateContacts(entityContacts.getListAdd());
            }

            ////////////////////////////////////////////////////////////////////////////////////////

            ////ContactFiles////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<ContactFile> entityContactFiles = handler.getDataContactFiles();

            for (String uid : entityContactFiles.getListDelete()) {
                deleteContactFileAndFile(app, cr, uid);
            }

            for (String uid : entityContactFiles.getListProcess()) {
                deleteContactFileAndFile(app, cr, uid);
            }

            if (!entityContactFiles.getListSend().isEmpty()) {
                final ContentValues cv = new ContentValues(1);
                cv.put(ContactsFileContract.SEND_FILE, 1);

                cr.update(ContactsFileContract.CONTENT_URI, cv,//
                        SelectionKeeper.in(null, ContactsFileContract.FIELD_UID, entityContactFiles.getListSend()), null);
            }

            addOrUpdateContactFiles(app.getAppFolder(), cr, entityContactFiles.getListAdd());
            ////////////////////////////////////////////////////////////////////////////////////////

            ////////////////////////////////////////////////////////////////////////////////////////

            ///Markers//////////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<Marker> entityMarkers = handler.getDataMarkers();
            List<Marker> markers = new ArrayList<>();
            for (Marker marker : entityMarkers.getListAdd()) {
                if (!Marker.DEFAULT_MARKER_UUID.equals(marker.getId())) {
                    markers.add(marker);
                }
            }
            entityMarkers.getListAdd().clear();
            entityMarkers.setListAdd(markers);

            if (!entityMarkers.getListDelete().isEmpty()) {
                for (String uidToDelete : entityMarkers.getListDelete()) {
                    // обновляем маркера и порядки задач с удаляемым маркером на дефотный
                    Marker.updateTaskMarkerOrder(uidToDelete.toUpperCase(), 0, mContext);
                    Marker tempMarker = dbHelper.getMarkerDao().queryForId(UUID.fromString(uidToDelete)); //ищем в кеше
                    MarkerCache.getInstance(mContext).remove(tempMarker); // удаляем с кеша
                }
                dbHelper.deleteMarkers(convertStringsToUUIDs(entityMarkers.getListDelete()));
            }

            mListSendMarkers = convertStringsToUUIDs(entityMarkers.getListSend());

            if (!entityMarkers.getListProcess().isEmpty()) {
                dbHelper.deleteMarkers(convertStringsToUUIDs(entityMarkers.getListProcess()));
            }

            if (!entityMarkers.getListAdd().isEmpty()) {
                dbHelper.updateMarkers(entityMarkers.getListAdd());
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            ////Tasks///////////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<LTask> entityTasks = handler.getDataTasks();

            if(!LTSettings.getInstance().isHasAnyTask()) {
                // если не было задач и они прилетели
                if (!entityTasks.getListAdd().isEmpty() || !entityTasks.getListProcess().isEmpty()) {
                    LTSettings.getInstance().setAlreadyHasAnyTasks();
                }
            }

            if (!entityTasks.getListDelete().isEmpty()) {
                deleteEntities(entityTasks.getListDelete(), mApp);
            }

            mListSendTaks = entityTasks.getListSend();

            if (!entityTasks.getListProcess().isEmpty()) {
                processEntities(entityTasks.getListProcess(), mApp);
            }

            if (!LTSettings.getInstance().isNeedToShowLoadingScreen()) {
                mTasksForMeNew = Utils.showNewAssignedTaskForMe(mApp, entityTasks.getListAdd());
                mTasksByMeCanceled = Utils.showCancelledTaskByMe(mApp, entityTasks.getListAdd());
            }

            if (!entityTasks.getListAdd().isEmpty()) {
                updateEntities(entityTasks.getListAdd(), mApp);
            }

            ////////////////////////////////////////////////////////////////////////////////////////

            ////TaskMessages////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<TaskMessage> entityTaskMessages = handler.getDataTaskMessages();

            boolean calculate = false;

            if (!entityTaskMessages.getListDelete().isEmpty()) {
                dbHelper.deleteTaskMessages(convertStringsToUUIDs(entityTaskMessages.getListDelete()));
                calculate = true;
            }

            mListSendTaskMessages = convertStringsToUUIDs(entityTaskMessages.getListSend());

            if (!entityTaskMessages.getListProcess().isEmpty()) {
                dbHelper.deleteTaskMessages(convertStringsToUUIDs(entityTaskMessages.getListProcess()));
                calculate = true;
            }

            if (!LTSettings.getInstance().isNeedToShowLoadingScreen()) {
                mTasksWhereMessages = Utils.showNewCommentForTask(mApp, entityTaskMessages.getListAdd());
            }

            if (!entityTaskMessages.getListAdd().isEmpty()) {
                dbHelper.updateTaskMessages(entityTaskMessages.getListAdd());
                calculate = true;
            }

            if (calculate) {
                dbHelper.calculateTaskMessagesInTask(mContext);
            }

            ////////////////////////////////////////////////////////////////////////////////////////

            ///TaskFiles////////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<TaskFile> entityTaskFiles = handler.getDataTaskFiles();

            boolean wasChange = false;
            for (String uid : entityTaskFiles.getListDelete()) {
                deleteTaskFileAndFile(app, cr, uid);
                wasChange = true;
            }

            for (String uid : entityTaskFiles.getListProcess()) {
                deleteTaskFileAndFile(app, cr, uid);

                wasChange = true;
            }

            if (!entityTaskFiles.getListSend().isEmpty()) {
                final ContentValues cvTaskFiles = new ContentValues(1);
                cvTaskFiles.put(TaskFileContract.SEND_FILE, 1);

                cr.update(TaskFileContract.CONTENT_URI, cvTaskFiles,//
                        SelectionKeeper.in(null, TaskFileContract.FIELD_UID, entityTaskFiles.getListSend()), null);
                wasChange = true;
            }

            if (addOrUpdateTaskFiles(app.getAppFolder(), cr, entityTaskFiles.getListAdd())) {
                wasChange = true;
            }

            if (wasChange) {
                DbHelper.calculateFilesInTask(app); // ne rabotaet
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            ////Emps////////////////////////////////////////////////////////////////////////////////
            final BaseLionProcessEntity<Emp> entityEmps = handler.getDataEmps();

            boolean wasChanged = false;

            if (!entityEmps.getListDelete().isEmpty()) {
                mContext.getContentResolver().delete(EmpContract.CONTENT_URI,
                        SelectionKeeper.inToLowerCase(null, EmpContract.UID, entityEmps.getListDelete()), null);

                Emp.updateTaskUserOrderAfterDelete(mContext, entityEmps.getListDelete());
                wasChanged = true;
            }

            if (!entityEmps.getListProcess().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entityEmps.getListProcess(), EmpContract.SERVER_CLASS);
                wasChanged = true;
            }

            if (Emp.addOrUpdateEntity(mContext, entityEmps.getListAdd())) {
                wasChanged = true;
            }

            if (!entityEmps.getListSend().isEmpty()) {
                final ContentValues cvEmps = new ContentValues(1);
                cvEmps.put(EmpContract.SEND_ENTITY, 1);

                for (int i = 0; i < entityEmps.getListSend().size(); i++) {
                    if (Emp.DEFAULT_STRING_EMP.equals(entityEmps.getListSend().get(i).toLowerCase())) {
                        entityEmps.getListSend().set(i, Emp.DEFAULT_UUID_EMP_S);
                        break;
                    }
                }

                mContext.getContentResolver().update(EmpContract.CONTENT_URI, cvEmps,//
                        SelectionKeeper.in(null, EmpContract.UID, entityEmps.getListSend()), null);

                wasChanged = false;
            }

            if (wasChanged) {
                Emp.reSortEmp(mContext);
            }
            ////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            int m = 0;
            m++;
        }

        finally {
            return null;
        }
    }

    public List<UUID> getListSendCategories() {
        return mListSendCategory;
    }

    public List<UUID> getListSendProjects() {
        return mListSendProject;
    }

    public List<UUID> getListSendContacts() {
        return mListSendContacts;
    }

    public List<UUID> getListSendContactGroups() {
        return mListSendContactGroups;
    }

    public List<UUID> getListSendMarkers() {
        return mListSendMarkers;
    }

    public List<String> getListSendTasks() {
        return mListSendTaks;
    }

    public List<UUID> getListSendTaskMessages() {
        return mListSendTaskMessages;
    }

    public String getSessionOrder() {
        return mSessionOrder;
    }

    public String getError() {
        return mErrorCode;
    }

    public List <String> getTasksForMeNew() {
        return mTasksForMeNew;
    }

    public List <String> getTasksByMeCanceled() {
        return mTasksByMeCanceled;
    }

    public List <String> getTasksWhereMessages() {
        return mTasksWhereMessages;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PROCESS_PROJECTS;
    }

    private void deleteContactFileAndFile(LTApplication app, ContentResolver cr, String uid) {
        final Cursor c = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionFieldUid(uid), null, null);

        try {
            while (c.moveToNext()) {
                if (c.isFirst()) {
                    new File(app.getAppFolder(), c.getString(c.getColumnIndex(ContactsFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(ContactsFileContract.CONTENT_URI, ContactsFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, ContactsFileContract.selectionFieldUid(uid));
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
        ops.add(ContentProviderOperation.newDelete(ContactsFileContract.CONTENT_URI)
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

    public static boolean addOrUpdateContactFiles(File appFolder, ContentResolver cr, List<ContactFile> files)//
            throws RemoteException, OperationApplicationException {
        if (files.isEmpty()) {
            return false;
        }

        final ArrayList<ContentProviderOperation> oprtns = new ArrayList<ContentProviderOperation>(files.size());
        final StringBuilder sb = new StringBuilder();
        final ContactFile fileOld = new ContactFile();

        for (ContactFile fileNew : files) {
            Utils.clearStringBuilder(sb);
            final String selection = SelectionKeeper.equals(sb, ContactsFileContract.FIELD_UID, fileNew.getId());
            final Cursor c = cr.query(ContactsFileContract.CONTENT_URI, null, selection, null, null);

            if (c.getCount() == 1 && c.moveToFirst()) {
                fileOld.setData(c);

                final ContentValues cv = new ContentValues();
                cv.put(ContactsFileContract.DELETE_OBJECT, 0);

                if (fileOld.getUsnEntity() != fileNew.getUsnEntity()) {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, fileNew.getUsnEntity());
                    cv.put(ContactsFileContract.FIELD_UID, fileNew.getId().toString());
                    cv.put(ContactsFileContract.FIELD_CONTACTUID, fileNew.getContactId().toString());
                    cv.put(ContactsFileContract.FIELD_FILEUID, fileNew.getFileId().toString());
                    cv.put(ContactsFileContract.FIELD_EMAILCREATOR, fileNew.getEmailCreator());
                }

                if (fileOld.getUsnFieldOrder() <= fileNew.getUsnFieldOrder()) {
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_ORDER, fileNew.getUsnFieldOrder());
                    cv.put(ContactsFileContract.ORDERS, fileNew.getOrder());
                } else {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldName() <= fileNew.getUsnFieldName()) {
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_NAME, fileNew.getUsnFieldName());
                    cv.put(ContactsFileContract.FIELD_FILENAME, fileNew.getFileName());
                } else {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldSize() <= fileNew.getUsnFieldSize()) {
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_SIZE, fileNew.getUsnFieldSize());
                    cv.put(ContactsFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                } else {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldVersion() != fileNew.getUsnFieldVersion()) {
                    cv.put(ContactsFileContract.FIELD_FILEVERSION, fileNew.getFileVersion());
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_VERSION, fileNew.getUsnFieldVersion());
                    cv.put(ContactsFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                    cv.put(ContactsFileContract.FILE_EXIST, 0);
                    new File(appFolder, fileOld.getFileName()).delete();
                }

                oprtns.add(ContentProviderOperation.newUpdate(ContactsFileContract.CONTENT_URI)//
                        .withSelection(selection, null)//
                        .withValues(cv)//
                        .build());
            } else {
                oprtns.add(ContentProviderOperation.newInsert(ContactsFileContract.CONTENT_URI)//
                        .withValues(fileNew.getContentValues(null))//
                        .build());
            }
            c.close();
        }

        cr.applyBatch(LeaderTaskProviderMetaData.AUTHORITY, oprtns);

        return true;
    }

    private boolean deleteEntities(List<String> uids, LTApplication mApp) {
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
                        c = mApp.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, SelectionKeeper.equals(mSb, LionMetaData.LTaskContract.Uid, uid), null, null);
                        if (c.getCount() > 0) {
                            final int columnId = c.getColumnIndex(LionMetaData.LTaskContract._ID);
                            final int columnUid = c.getColumnIndex(LionMetaData.LTaskContract.Uid);
                            c.moveToFirst();
                            LTSettings.getInstance().getTasksToDelete().add(""+c.getInt(columnId));
                            LTSettings.getInstance().getTasksToUpdate().add(""+c.getInt(columnUid));
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }//
                }
                Utils.clearStringBuilder(mSb);
                mApp.getContentResolver().delete(task.getContentUri(),
                        SelectionKeeper.in(mSb, LionMetaData.BaseLionColumns.Uid, uids), null);

                mSb.append(SharedStrings.AND);
                mApp.getContentResolver().delete(LionMetaData.DeleteUidContract.CONTENT_URI,
                        SelectionKeeper.equals(mSb, LionMetaData.DeleteUidContract.LionName, task.getLionName()), null);

            }

            return deleteEntities;

        } catch (Exception e) {
            throw LeaderException.create(ExceptionReason.SQLITE, e);
        }
    }

    private boolean processEntities(List<String> uids, LTApplication mApp) {
        try {
            LTask task = new LTask();
            final boolean processEntities = uids.size() > 0;

            if (processEntities) {
                DeleteUid.removeUids(mApp, uids, task.getLionName());
            }

            return processEntities;

        } catch (Exception e) {
            throw LeaderException.create(ExceptionReason.SQLITE, e);
        }
    }

    protected boolean updateEntities(List<LTask> entities, LTApplication mApp) {
        if (entities.size() == 0) {
            return false;
        }

        boolean updateEntities = false;
        Cursor c = null;
        LTask task = new LTask();
        StringBuilder mSb = new StringBuilder();

        try {
            Utils.clearStringBuilder(mSb);
            c = mApp.getContentResolver().query(task.getContentUri(), null, in(mSb, LionMetaData.BaseLionColumns.Uid, entities), null, null);

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
                                if (entity.getStatus() == 1 || entity.getStatus() == 7) {
                                    SynchronizationTask.isHasCanceledTaskInUpdate = true;
                                }
                                update.add(ContentProviderOperation.newUpdate(task.getContentUri()).withValues(task.getDifference(entity))
                                        .withSelection(SelectionKeeper.equals(mSb, LionMetaData.BaseLionColumns.Uid, uid), null).build());
                                LTSettings.getInstance().getTasksToUpdate().add(entity.getUid());
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
                LTSettings.getInstance().getTasksToUpdate().add(entity.getUid());
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

    protected boolean addEntities(List<LTask> entities, LTApplication mApp) {
        try {
            final boolean addEntities = entities.size() > 0;
            LTask task = new LTask();

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

    private void deleteTaskFileAndFile(LTApplication app, ContentResolver cr, String uid) {
        final Cursor c = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionFieldFileUid(uid),
                null, null);

        try {
            while (c.moveToNext()) {
                if (c.isFirst()) {
                    new File(app.getAppFolder(), c.getString(c.getColumnIndex(TaskFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(TaskFileContract.CONTENT_URI, TaskFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, TaskFileContract.selectionFieldUid(uid));
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

        final Cursor cursor = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionFieldFileUid(uid),
                null, null);

        try {
            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    new File(app.getAppFolder(), cursor.getString(cursor.getColumnIndex(ContactsFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(TaskFileContract.CONTENT_URI, TaskFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, ContactsFileContract.selectionFieldUid(uid));
                }
            }
        }
        catch (Exception e)
        {
            Utils.toLog(e);
        }
        finally {
            if(cursor!= null)
            {
                cursor.close();
            }
        }
    }

    public static boolean addOrUpdateTaskFiles(File appFolder, ContentResolver cr, List<TaskFile> files)//
            throws RemoteException, OperationApplicationException {
        if (files.isEmpty()) {
            return false;
        }

        final ArrayList<ContentProviderOperation> oprtns = new ArrayList<ContentProviderOperation>(files.size());
        final StringBuilder sb = new StringBuilder();
        final TaskFile fileOld = new TaskFile();

        for (TaskFile fileNew : files) {
            Utils.clearStringBuilder(sb);
            final String selection = SelectionKeeper.equals(sb, TaskFileContract.FIELD_UID, fileNew.getId());
            final Cursor c = cr.query(TaskFileContract.CONTENT_URI, null, selection, null, null);

            if (c.getCount() == 1 && c.moveToFirst()) {
                fileOld.setData(c);

                final ContentValues cv = new ContentValues();
                cv.put(TaskFileContract.DELETE_OBJECT, 0);

                if (fileOld.getUsnEntity() != fileNew.getUsnEntity()) {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, fileNew.getUsnEntity());
                    cv.put(TaskFileContract.FIELD_UID, fileNew.getId().toString());
                    cv.put(TaskFileContract.FIELD_TASKUID, fileNew.getTaskId().toString());
                    cv.put(TaskFileContract.FIELD_FILEUID, fileNew.getFileId().toString());
                    cv.put(TaskFileContract.FIELD_EMAILCREATOR, fileNew.getEmailCreator());
                }

                if (fileOld.getUsnFieldOrder() <= fileNew.getUsnFieldOrder()) {
                    cv.put(TaskFileContract.FIELD_USN_FIELD_ORDER, fileNew.getUsnFieldOrder());
                    cv.put(TaskFileContract.ORDERS, fileNew.getOrder());
                } else {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldName() <= fileNew.getUsnFieldName()) {
                    cv.put(TaskFileContract.FIELD_USN_FIELD_NAME, fileNew.getUsnFieldName());
                    cv.put(TaskFileContract.FIELD_FILENAME, fileNew.getFileName());
                } else {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldSize() <= fileNew.getUsnFieldSize()) {
                    cv.put(TaskFileContract.FIELD_USN_FIELD_SIZE, fileNew.getUsnFieldSize());
                    cv.put(TaskFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                } else {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldVersion() != fileNew.getUsnFieldVersion()) {
                    cv.put(TaskFileContract.FIELD_FILEVERSION, fileNew.getFileVersion());
                    cv.put(TaskFileContract.FIELD_USN_FIELD_VERSION, fileNew.getUsnFieldVersion());
                    cv.put(TaskFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                    cv.put(TaskFileContract.FILE_EXIST, 0);
                    new File(appFolder, fileOld.getFileName()).delete();
                }

                oprtns.add(ContentProviderOperation.newUpdate(TaskFileContract.CONTENT_URI)//
                        .withSelection(selection, null)//
                        .withValues(cv)//
                        .build());
            } else {
                oprtns.add(ContentProviderOperation.newInsert(TaskFileContract.CONTENT_URI)//
                        .withValues(fileNew.getContentValues(null))//
                        .build());
            }
            c.close();
        }

        cr.applyBatch(LeaderTaskProviderMetaData.AUTHORITY, oprtns);

        return true;
    }
}