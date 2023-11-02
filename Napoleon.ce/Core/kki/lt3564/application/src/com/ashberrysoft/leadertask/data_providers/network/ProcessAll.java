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
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
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
import com.ashberrysoft.leadertask.modern.builder.XmlSoap;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.DeleteUid;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.exception.ExceptionReason;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactFotoHandler;
import com.ashberrysoft.leadertask.xml_handlers.SimpleProcessHandler.SimpleProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpFotoHandler;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;

import java.io.ByteArrayInputStream;
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

public class ProcessAll extends BaseTimeSOAPall<Serializable> implements ProcessSOAPRequest {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessAll";

    private static List<Contact> mListContacts = new ArrayList<>();

    private List<Project> mListVerifyProject;
    private List<Category> mListVerifyCategory;
    private List<Contact> mListVerifyContacts;
    private List<ContactsGroup> mListVerifyContactsGroup;
    private List<Marker> mListVerifyMarker;
    private List<TaskMessage> mListVerifyTaskMessage;
    private List<Contact> mListContactsFotos = new ArrayList<>();
    private ArrayList<Emp> mListEmpsFotos = new ArrayList<>();
    private Context mContext;

    private List<UUID> mListSendCategory;
    private List<UUID> mListSendProject;
    private List<UUID> mListSendEmps;
    private List<UUID> mListSendTaskFiles;
    private List<UUID> mListSendContacts;
    private List<UUID> mListSendContactGroups;
    private List<UUID> mListSendMarkers;
    private List<String> mListSendTaks;
    private List<UUID> mListSendTaskMessages = new ArrayList<UUID>();
    private boolean mOnlyFilesAndFotos;
    private String mErrorCode;

    private List<String> tasksForMeNew;
    private List<String> tasksByMeCanceled;
    private List<String> tasksWhereMessages;

    public ProcessAll(Context context,
                      LeaderTaskUser user,
                      List<Project> verifyProject,
                      List<Category> verifyCategory,
                      List<Contact> verifyContact,
                      List<ContactsGroup> verifyContactGroups,
                      List<Marker> verifyMarker,
                      List<TaskMessage> verifyTaskMessage,
                      boolean onlyFilesAndFotos) {

        super(context, METHOD_NAME, user);
        mListVerifyProject = verifyProject;
        mListVerifyCategory = verifyCategory;
        mListVerifyContacts = verifyContact;
        mListVerifyContactsGroup = verifyContactGroups;
        mListVerifyMarker = verifyMarker;
        mListVerifyTaskMessage = verifyTaskMessage;
        mOnlyFilesAndFotos = onlyFilesAndFotos;
        mContext = context;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        Utils.timeChecker("writeRequestSubXML");
        if (!mOnlyFilesAndFotos) {
            generateCategory(writer);
            generateProjects(writer);
            generateContacts(writer);
            generateContactGroups(writer);
            generateContactFiles(writer);
        }
        generateContactFotos(writer);
        if (!mOnlyFilesAndFotos) {
            generateMarkers(writer);
            generateTasks(writer);
            generateTaskMessages(writer);
            generateTaskFiles(writer);
        }
        generateFiles(writer);
        if (!mOnlyFilesAndFotos) {
            generateEmps(writer);
        }
        generateEmpsFotos(writer);
        Utils.timeChecker("writeRequestSubXML");
    }

    @Override
    protected void writeClearSessionChanges(OutputStreamWriter writer) throws Exception {
        if(SynchronizationTask.isBigSync) {
            writer.write(getOpen("clear_session_changes"));
            writer.write(getOpen("str_order"));
            writer.write("1000000000");
            writer.write(getClose("str_order"));
            writer.write(getClose("clear_session_changes"));
        }
    }


    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);
        LTApplication mApp = (LTApplication) mContext.getApplicationContext();
        final ContentResolver cr = mContext.getContentResolver();
        final LTApplication app = (LTApplication) mContext.getApplicationContext();

        try {
            //Парсим ProcessResponse
            Utils.timeChecker("SwitchParseHandlerProcessAll");
            final SwitchParseHandlerProcessAll handler = SwitchParseHandlerProcessAll.newInstance(inputStream);
            Utils.timeChecker("SwitchParseHandlerProcessAll");
            mErrorCode = handler.getErrorCode();
            if (!mOnlyFilesAndFotos) {
                // Category ////////////////////////////////////////////////////////////////////////////
                final BaseLionProcessEntity<Category> entityCategory = handler.getDataCategory();

                if (!entityCategory.getListDelete().isEmpty()) {
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
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.SEND_FILE, 1);

                    cr.update(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, cv,//
                            SelectionKeeper.in(null, LeaderTaskProviderMetaData.ContactsFileContract.FIELD_UID, entityContactFiles.getListSend()), null);
                }

                addOrUpdateContactFiles(app.getAppFolder(), cr, entityContactFiles.getListAdd());
                ////////////////////////////////////////////////////////////////////////////////////////
            }
            ////ContactsFoto/////////////////////////////////////////////////////////////////////////////
            //
            /*try {
                mListContacts = DbHelper.getInstance(mContext).getContactDao().queryBuilder().orderBy(LeaderTaskProviderMetaData.ContactContract.ORDERS, true).query();
            } catch (SQLException e) {
            }
            //

            final ProcessContactFotoHandler.SimpleProcessEntity entityContactFotos = handler.getDataContactFotos();

            if (!entityContactFotos.getListDownload().isEmpty()) {
                //качать фотки
                for (int i=0; i< entityContactFotos.getListDownload().size()-1; i++) {
                    for (Contact contact : mListContacts) {
                        if (contact.getUid().toString().equals(entityContactFotos.getListDownload().get(i).equals(Emp.DEFAULT_STRING_EMP) ? Emp.DEFAULT_UUID_EMP_S : entityContactFotos.getListDownload().get(i))) {
                            try {
                                try {
                                    File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + contact.getUid().toString());
                                    if (cacheImgFile.exists()) {
                                        cacheImgFile.delete();
                                    }
                                } catch (Exception e) {

                                }
                                new DownloadFile(mContext, entityContactFotos.getListDownload().get(i), contact.getUid().toString(), LTSettings.getInstance().getUserProfile(), mApp.getAppFolder(), 2).downloadFile();

                                android.util.Log.v("Tedorius","File downloaded ContactFotos: "+contact.getUid().toString());
                            }
                            catch (Exception e) {
                                int m = 0;
                            }
                            finally {
                                break;
                            }
                        }
                    }
                }
            }*/
            ////////////////////////////////////////////////////////////////////////////////////////
            if (!mOnlyFilesAndFotos) {
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

                Utils.timeChecker("Tasks");
                ////Tasks///////////////////////////////////////////////////////////////////////////////

                Utils.timeChecker("Tasks1");
                final BaseLionProcessEntity<LTask> entityTasks = handler.getDataTasks();
                if(!LTSettings.getInstance().isHasAnyTask()) {
                    // если не было задач и они прилетели
                    if (!entityTasks.getListAdd().isEmpty() || !entityTasks.getListProcess().isEmpty()) {
                        LTSettings.getInstance().setAlreadyHasAnyTasks();
                    }
                }

                Utils.timeChecker("Tasks1");

                Utils.timeChecker("Tasks2");
                if (!entityTasks.getListDelete().isEmpty()) {
                    deleteEntities(entityTasks.getListDelete(), mApp);
                }
                Utils.timeChecker("Tasks2");

                Utils.timeChecker("Tasks3");
                mListSendTaks = entityTasks.getListSend();

                Utils.timeChecker("Tasks3");

                Utils.timeChecker("Tasks4");
                if (!entityTasks.getListProcess().isEmpty()) {
                    processEntities(entityTasks.getListProcess(), mApp);
                }
                Utils.timeChecker("Tasks4");

                Utils.timeChecker("Tasks5");
                if (!LTSettings.getInstance().isNeedToShowLoadingScreen()) {
                    tasksForMeNew = Utils.showNewAssignedTaskForMe(mApp, entityTasks.getListAdd());
                    tasksByMeCanceled = Utils.showCancelledTaskByMe(mApp, entityTasks.getListAdd());
                }
                Utils.timeChecker("Tasks5");

                Utils.timeChecker("Tasks6");
                if (!entityTasks.getListAdd().isEmpty()) {
                    updateEntities(entityTasks.getListAdd(), mApp);
                }
                Utils.timeChecker("Tasks6");
                ////////////////////////////////////////////////////////////////////////////////////////

                Utils.timeChecker("Tasks");
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
                    tasksWhereMessages = Utils.showNewCommentForTask(mApp, entityTaskMessages.getListAdd());
                }

                if (!entityTaskMessages.getListAdd().isEmpty()) {
                    dbHelper.updateTaskMessages(entityTaskMessages.getListAdd());
                    calculate = true;
                }

                if (calculate) {
                    dbHelper.calculateTaskMessagesInTask(mContext);
                }

                ////////////////////////////////////////////////////////////////////////////////////////
            }

            ////Files///////////////////////////////////////////////////////////////////////////////
            final SimpleProcessEntity entityFiles = handler.getDataFiles();

            for (String uid : entityFiles.getListDelete()) {
                deleteTaskFileAndFile(app, cr, uid);
            }

            final ContentValues cv = new ContentValues();
            final StringBuilder sb = new StringBuilder();
            final StringBuilder sbContacts = new StringBuilder();
            for (String uid : entityFiles.getListSend()) {
                final String where = TaskFileContract.selectionFieldFileUid(uid);
                final Cursor c = cr.query(TaskFileContract.CONTENT_URI, null, where, null, null);
                c.moveToFirst();
                if (c != null && c.getCount() > 0) {
                    TaskFile taskFile = new TaskFile(c);
                    UploadFile uploadFile = new UploadFile(mContext, app.getSettings().getUserProfile(), app.getAppFolder(), sb, taskFile.getFileName(), taskFile.getFileId().toString(), taskFile.getEmailCreator(), taskFile.getFileVersion(), 0);
                    final Integer usn = uploadFile.uploadFile();
                    sb.append(SharedStrings.NEW_LINE_C);

                    if (usn != null) {
                        cv.clear();
                        cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                        cv.put(TaskFileContract.FIELD_FILEVERSION, usn);
                        cr.update(TaskFileContract.CONTENT_URI, cv, where, null);
                    }
                }
                c.close();
            }
            saveResponse(mContext, new ByteArrayInputStream(sb.toString().getBytes()), UploadFile.METHOD_TYPE);

            for (String uid : entityFiles.getListSend()) {
                final String where = ContactsFileContract.selectionFieldFileUid(uid);
                final Cursor cursor = cr.query(ContactsFileContract.CONTENT_URI, null, where, null, null);
                cursor.moveToFirst();
                if (cursor != null && cursor.getCount() > 0) {
                    ContactFile taskFile = new ContactFile(cursor);
                    UploadFile uploadFile = new UploadFile(mContext, app.getSettings().getUserProfile(), app.getAppFolder(), sbContacts, taskFile.getFileName(), taskFile.getFileId().toString(), taskFile.getEmailCreator(), taskFile.getFileVersion(), 0);
                    final Integer usn = uploadFile.uploadFile();
                    sbContacts.append(SharedStrings.NEW_LINE_C);

                    if (usn != null) {
                        cv.clear();
                        cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                        cv.put(ContactsFileContract.FIELD_FILEVERSION, usn);
                        cr.update(ContactsFileContract.CONTENT_URI, cv, where, null);
                    }
                }
                cursor.close();
            }
            saveResponse(mContext, new ByteArrayInputStream(sbContacts.toString().getBytes()), UploadFile.METHOD_TYPE);
            ////////////////////////////////////////////////////////////////////////////////////////
            if (!mOnlyFilesAndFotos) {
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
                    /*List<String> getListSendUpper = new ArrayList<>();
                    for (int i = 0; i < entityTaskFiles.getListSend().size(); i++) {
                        getListSendUpper.add(entityTaskFiles.getListSend().get(i).toUpperCase());
                    }*/
                    /*final ContentValues cvTaskFiles = new ContentValues(1);
                    cvTaskFiles.put(TaskFileContract.SEND_FILE, 1);

                    cr.update(TaskFileContract.CONTENT_URI, cvTaskFiles,//
                            SelectionKeeper.in(null, TaskFileContract.FIELD_UID, getListSendUpper), null);*/

                    mListSendTaskFiles = convertStringsToUUIDs(entityTaskFiles.getListSend());
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


                mListSendEmps = convertStringsToUUIDs(entityEmps.getListSend());

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

                    mContext.getContentResolver().update(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, cvEmps,//
                            SelectionKeeper.in(null, EmpContract.UID, entityEmps.getListSend()), null);

                    wasChanged = false;
                }

                if (wasChanged) {
                    Emp.reSortEmp(mContext);
                }
                ////////////////////////////////////////////////////////////////////////////////////////
            }
            ////EmpsFoto////////////////////////////////////////////////////////////////////////////
            final ProcessEmpFotoHandler.SimpleProcessEntity entityEmpsFotos = handler.getDataEmpsFotos();


            if (!entityEmpsFotos.getListDownload().isEmpty()) {
                //качать фотки
                for (int i=0; i < entityEmpsFotos.getListDownload().size()-1; i++) {
                    for (Emp emp : mListEmpsFotos) {
                        if (emp.getUid().toString().equals(entityEmpsFotos.getListDownload().get(i).equals(Emp.DEFAULT_STRING_EMP) ? Emp.DEFAULT_UUID_EMP_S : entityEmpsFotos.getListDownload().get(i))) {
                            try {
                                try {
                                    File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + emp.getLogin());
                                    if (cacheImgFile.exists()) {
                                        cacheImgFile.delete();
                                    }
                                } catch (Exception e) {

                                }
                                try {
                                    new DownloadFile(mContext, entityEmpsFotos.getListDownload().get(i), emp.getLogin(), LTSettings.getInstance().getUserProfile(), mApp.getAppFolder(), 1).downloadFile();
                                    android.util.Log.v("Tedorius","File downloaded EmpsFotos: "+emp.getLogin());
                                } finally {
                                    try {
                                        if (i == entityEmpsFotos.getListDownload().size()-2) {
                                            LTSettings.isNeedDownLoadEmpFotos = false;
                                        }
                                    } finally {
                                        //LTSettings.isNeedDownLoadEmpFotos = false;
                                    }
                                }
                                //


                                //
                            }
                            catch (Exception e) {

                            }
                            finally {
                                break;
                            }
                        }
                    }
                }
            } else {
                LTSettings.isNeedDownLoadEmpFotos = false;
            }

            if (!entityEmpsFotos.getListNew().isEmpty()) {

            }

            if (!entityEmpsFotos.getListSend().isEmpty()) {
                UploadFile uploadFile = new UploadFile(mContext, mApp.getSettings().getUserProfile(), mApp.getAppFolder(), new StringBuilder(), "/cache_"+LTSettings.getInstance().getUserName(), entityEmpsFotos.getListSend().get(0), LTSettings.getInstance().getUserName(), 0, 1);
                final Integer usn = uploadFile.uploadFile();

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

    public List<UUID> getListSendEmps() {
        return mListSendEmps;
    }

    public List<UUID> getListSendTaskFiles() {
        return mListSendTaskFiles;
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

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PROCESS_PROJECTS;
    }

    public List <String> getTasksForMeNew() {
        return tasksForMeNew;
    }

    public List <String> getTasksByMeCanceled() {
        return tasksByMeCanceled;
    }

    public List <String> getTasksWhereMessages() {
        return tasksWhereMessages;
    }

    private void generateProjects(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_PROJECTS));
            if (mListVerifyProject != null && !mListVerifyProject.isEmpty()) {
                for (Project entity : mListVerifyProject) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(entity.getId().toString());
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(String.valueOf(entity.getUsn()));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            writer.write(getClose(VER_PROJECTS));


            writer.write(getOpen(REM_PROJECTS));
            final Cursor r = mContext.getContentResolver().query(UidToDeleteContract.CONTENT_URI, null,
                    UidToDeleteContract.selectionServerClass(Project.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(UidToDeleteContract.UID);
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

    private void generateCategory(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_TAGS));
            if (mListVerifyCategory != null && !mListVerifyCategory.isEmpty()) {
                for (Category entity : mListVerifyCategory) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(entity.getId().toString());
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(String.valueOf(entity.getUsn()));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            writer.write(getClose(VER_TAGS));

            writer.write(getOpen(REM_TAGS));
            final Cursor r = mContext.getContentResolver().query(UidToDeleteContract.CONTENT_URI, null,
                    UidToDeleteContract.selectionServerClass(Category.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(UidToDeleteContract.UID);
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
            writer.write(getOpen(VER_CONTACTS_GROUPS));
            //
            if (mListVerifyContactsGroup != null && !mListVerifyContactsGroup.isEmpty()) {
                for (ContactsGroup entity : mListVerifyContactsGroup) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(entity.getId().toString());
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(String.valueOf(entity.getUsn()));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            //
            writer.write(getClose(VER_CONTACTS_GROUPS));
            writer.write(getOpen(REM_CONTACTS_GROUPS));
            //
            final Cursor r = mContext.getContentResolver().query(UidToDeleteContract.CONTENT_URI, null,
                    UidToDeleteContract.selectionServerClass(ContactsGroup.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(UidToDeleteContract.UID);
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
            writer.write(getOpen(VER_CONTACTS));
            //
            if (mListVerifyContacts != null && !mListVerifyContacts.isEmpty()) {
                for (Contact entity : mListVerifyContacts) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(entity.getId().toString());
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(String.valueOf(entity.getUsn()));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            //
            writer.write(getClose(VER_CONTACTS));
            writer.write(getOpen(REM_CONTACTS));
            //
            final Cursor r = mContext.getContentResolver().query(UidToDeleteContract.CONTENT_URI, null,
                    UidToDeleteContract.selectionServerClass(LeaderTaskProviderMetaData.ContactContract.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(UidToDeleteContract.UID);
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
            writer.write(getOpen(VER_CONTACT_FILES));
            //
            final ContentResolver cr = mContext.getContentResolver();

            final Cursor v = cr.query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.ContactsFileContract.selectionDeleteObject(false),
                    null, null);
            if (v.getCount() > 0) {
                final int uid = v.getColumnIndex(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_UID);
                final int usn = v.getColumnIndex(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_ENTITY);

                for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(v.getString(uid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(v.getString(usn));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            v.close();
            //
            writer.write(getClose(VER_CONTACT_FILES));
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

    private void generateContactFotos(OutputStreamWriter writer) {
        //
        try {
            mListContactsFotos = DbHelper.getInstance(mContext).getContactDao().queryBuilder().orderBy(LeaderTaskProviderMetaData.ContactContract.ORDERS, true).query();
        } catch (SQLException e) {
        }
        //
        try {
            writer.write(getOpen(VER_CONTACT_FOTOS));
            //
            DbHelper.getInstance(mContext).getContactDao();
            if (mListContactsFotos.size() > 0) {
                for (Contact contact: mListContactsFotos) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(contact.getUid().toString());
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(String.valueOf(contact.getUsnFieldFoto()));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            //
            writer.write(getClose(VER_CONTACT_FOTOS));
            writer.write(getOpen(REM_CONTACT_FOTOS));
            //
            writer.write(getClose(REM_CONTACT_FOTOS));
        } catch (IOException e) {

        }
    }

    private void generateMarkers(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_CONTACT_MARKERS));
            //
            if (mListVerifyMarker != null && !mListVerifyMarker.isEmpty()) {
                for (Marker marker : mListVerifyMarker) {
                    if (Marker.DEFAULT_MARKER_UUID.equals(marker.getId())) {
                        continue;
                    }

                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(marker.getId().toString());
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(String.valueOf(marker.getUsn()));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            //
            writer.write(getClose(VER_CONTACT_MARKERS));
            writer.write(getOpen(REM_CONTACT_MARKERS));
            //
            final Cursor r = mContext.getContentResolver().query(UidToDeleteContract.CONTENT_URI, null,
                    UidToDeleteContract.selectionServerClass(Marker.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int columnUid = r.getColumnIndex(UidToDeleteContract.UID);
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
            writer.write(getClose(REM_CONTACT_MARKERS));
        } catch (IOException e) {

        }
    }

    private void generateTasks(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_TASKS));
            //
            Cursor verify = null;

            try {
                verify = mContext.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, null, null, null);
                if (verify.getCount() > 0) {
                    final int columnUid = verify.getColumnIndex(LionMetaData.BaseLionColumns.Uid);
                    final int columnUsnEntity = verify.getColumnIndex(LionMetaData.BaseLionColumns.UsnEntity);

                    for (verify.moveToFirst(); !verify.isAfterLast(); verify.moveToNext()) {
                        writer.write(XmlSoap.getOpen(OBJ_CLIENT_TO_VERIFY));

                        writer.write(XmlSoap.getOpen(_STR_UID));
                        writer.write(verify.getString(columnUid));
                        writer.write(XmlSoap.getClose(_STR_UID));

                        writer.write(XmlSoap.getOpen(_USN_ENTITY));
                        writer.write(verify.getString(columnUsnEntity));
                        writer.write(XmlSoap.getClose(_USN_ENTITY));

                        writer.write(XmlSoap.getClose(OBJ_CLIENT_TO_VERIFY));
                    }
                }

            } finally {
                if (verify != null) {
                    verify.close();
                }
            };
            //
            writer.write(getClose(VER_TASKS));
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

    private void generateTaskMessages(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_TASK_MSG));
            //
            if (mListVerifyTaskMessage != null && !mListVerifyTaskMessage.isEmpty()) {
                for (TaskMessage messages : mListVerifyTaskMessage) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(String.valueOf(messages.getId()));
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(String.valueOf(messages.getUsn()));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            //
            writer.write(getClose(VER_TASK_MSG));
            writer.write(getOpen(REM_TASK_MSG));
            //
            writer.write(getClose(REM_TASK_MSG));
        } catch (IOException e) {

        }
    }

    private void generateTaskFiles(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_TASK_FILES));
            //
            final ContentResolver cr = mContext.getContentResolver();

            final Cursor v = cr.query(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.TaskFileContract.selectionDeleteObject(false),
                    null, null);
            if (v.getCount() > 0) {
                final int uid = v.getColumnIndex(LeaderTaskProviderMetaData.TaskFileContract.FIELD_UID);
                final int usn = v.getColumnIndex(LeaderTaskProviderMetaData.TaskFileContract.FIELD_USN_ENTITY);

                for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    writer.write(v.getString(uid));
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(v.getString(usn));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            v.close();
            //
            writer.write(getClose(VER_TASK_FILES));
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

    private void generateFiles(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_FILES));
            //
            final ContentResolver cr = mContext.getContentResolver();

            final Cursor v = cr.query(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, null,
                    null, null, null);
            final Cursor c = cr.query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null,
                    null, null, null);
            if (v.getCount() > 0) {
                final int uid = v.getColumnIndex(LeaderTaskProviderMetaData.TaskFileContract.FIELD_FILEUID);
                final int usn = v.getColumnIndex(LeaderTaskProviderMetaData.TaskFileContract.FIELD_FILEVERSION);

                for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));
                    writer.write(getOpen(_STR_UID));
                    writer.write(v.getString(uid));
                    writer.write(getClose(_STR_UID));
                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(v.getString(usn));
                    writer.write(getClose(_USN_ENTITY));
                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }

            if (c.getCount() > 0) {
                final int uid = c.getColumnIndex(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILEUID);
                final int usn = c.getColumnIndex(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILEVERSION);

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));
                    writer.write(getOpen(_STR_UID));
                    writer.write(c.getString(uid));
                    writer.write(getClose(_STR_UID));
                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(c.getString(usn));
                    writer.write(getClose(_USN_ENTITY));
                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            c.close();
            v.close();
            //
            writer.write(getClose(VER_FILES));
            writer.write(getOpen(REM_FILES));
            //
            final Cursor r = cr.query(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.TaskFileContract.selectionDeleteObject(true),
                    null, null);
            final Cursor cursor = cr.query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.ContactsFileContract.selectionDeleteObject(true),
                    null, null);
            if (r.getCount() > 0) {
                final int uid = r.getColumnIndex(LeaderTaskProviderMetaData.TaskFileContract.FIELD_FILEUID);

                for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));
                    writer.write(getOpen(_STR_UID));
                    writer.write(r.getString(uid));
                    writer.write(getClose(_STR_UID));
                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }

            if (cursor.getCount() > 0) {
                final int uid = cursor.getColumnIndex(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILEUID);

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));
                    writer.write(getOpen(_STR_UID));
                    writer.write(cursor.getString(uid));
                    writer.write(getClose(_STR_UID));
                    writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
                }
            }
            r.close();
            cursor.close();
            //
            writer.write(getClose(REM_FILES));
        } catch (IOException e) {

        }
    }

    private void generateEmps(OutputStreamWriter writer) {
        try {
            writer.write(getOpen(VER_EMPS));
            // потом добавить
            Emp.checkDefaultEmpCreated(mContext);

            final ContentResolver cr = mContext.getContentResolver();

            final Cursor v = cr.query(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null, null, null, null);
            if (v.getCount() > 0) {
                final int uid = v.getColumnIndex(LeaderTaskProviderMetaData.EmpContract.UID);
                final int usn = v.getColumnIndex(LeaderTaskProviderMetaData.EmpContract.USN_ENTITY);

                for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    final String stringUUID = v.getString(uid);
                    writer.write(stringUUID.equals(Emp.DEFAULT_UUID_EMP_S) ? Emp.DEFAULT_STRING_EMP : stringUUID);
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(v.getString(usn));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            v.close();
            //
            writer.write(getClose(VER_EMPS));
            writer.write(getOpen(REM_EMPS));
            //
            final Cursor r = cr.query(UidToDeleteContract.CONTENT_URI, null,
                    UidToDeleteContract.selectionServerClass(LeaderTaskProviderMetaData.EmpContract.SERVER_CLASS), null, null);
            if (r.getCount() > 0) {
                final int uid = r.getColumnIndex(UidToDeleteContract.UID);

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

    private void generateEmpsFotos(OutputStreamWriter writer) {
        try {
            //потом убрать
            Emp.checkDefaultEmpCreated(mContext);

            writer.write(getOpen(VER_EMPS_FOTOS));
            //
            final ContentResolver cr = mContext.getContentResolver();

            final Cursor v = cr.query(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null, null, null, null);
            if (v.getCount() > 0) {
                final int uid = v.getColumnIndex(LeaderTaskProviderMetaData.EmpContract.UID);
                final int usnFoto = v.getColumnIndex(LeaderTaskProviderMetaData.EmpContract.USN_FIELD_FOTO);

                for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                    Emp emp = new Emp(v);
                    mListEmpsFotos.add(emp);
                    writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                    writer.write(getOpen(_STR_UID));
                    final String stringUUID = v.getString(uid);
                    writer.write(stringUUID.equals(Emp.DEFAULT_UUID_EMP_S) ? Emp.DEFAULT_STRING_EMP : stringUUID);
                    writer.write(getClose(_STR_UID));

                    writer.write(getOpen(_USN_ENTITY));
                    writer.write(v.getString(usnFoto));
                    writer.write(getClose(_USN_ENTITY));

                    writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
                }
            }
            v.close();
            //
            writer.write(getClose(VER_EMPS_FOTOS));
            writer.write(getOpen(REM_EMPS_FOTOS));
            //
            writer.write(getClose(REM_EMPS_FOTOS));
        } catch (IOException e) {

        }
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
            final String selection = LeaderTaskProviderMetaData.SelectionKeeper.equals(sb, LeaderTaskProviderMetaData.ContactsFileContract.FIELD_UID, fileNew.getId());
            final Cursor c = cr.query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null, selection, null, null);

            if (c.getCount() == 1 && c.moveToFirst()) {
                fileOld.setData(c);

                final ContentValues cv = new ContentValues();
                cv.put(LeaderTaskProviderMetaData.ContactsFileContract.DELETE_OBJECT, 0);

                if (fileOld.getUsnEntity() != fileNew.getUsnEntity()) {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_ENTITY, fileNew.getUsnEntity());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_UID, fileNew.getId().toString());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_CONTACTUID, fileNew.getContactId().toString());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILEUID, fileNew.getFileId().toString());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_EMAILCREATOR, fileNew.getEmailCreator());
                }

                if (fileOld.getUsnFieldOrder() <= fileNew.getUsnFieldOrder()) {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_FIELD_ORDER, fileNew.getUsnFieldOrder());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.ORDERS, fileNew.getOrder());
                } else {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldName() <= fileNew.getUsnFieldName()) {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_FIELD_NAME, fileNew.getUsnFieldName());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILENAME, fileNew.getFileName());
                } else {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldSize() <= fileNew.getUsnFieldSize()) {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_FIELD_SIZE, fileNew.getUsnFieldSize());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                } else {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldVersion() != fileNew.getUsnFieldVersion()) {
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILEVERSION, fileNew.getFileVersion());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_USN_FIELD_VERSION, fileNew.getUsnFieldVersion());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                    cv.put(LeaderTaskProviderMetaData.ContactsFileContract.FILE_EXIST, 0);
                    new File(appFolder, fileOld.getFileName()).delete();
                }

                oprtns.add(ContentProviderOperation.newUpdate(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI)//
                        .withSelection(selection, null)//
                        .withValues(cv)//
                        .build());
            } else {
                oprtns.add(ContentProviderOperation.newInsert(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI)//
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
                        c = mApp.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.LTaskContract.Uid, uid), null, null);
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
                                update.add(ContentProviderOperation.newUpdate(task.getContentUri()).withValues(task.getDifference(entity))
                                        .withSelection(LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.BaseLionColumns.Uid, uid), null).build());
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

                        Object uid = cvs[count].get(LionMetaData.LTaskContract.UidProject);
                        if(uid != null && hasProjectWithQuite(uid.toString())) {
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
                    cr.delete(TaskFileContract.CONTENT_URI, TaskFileContract.selectionFieldFileUid(uid), null);
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
            cr.notifyChange(TaskFileContract.CONTENT_URI, null);
        }

        final Cursor cursor = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionFieldFileUid(uid),
                null, null);

        try {
            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    new File(app.getAppFolder(), cursor.getString(cursor.getColumnIndex(ContactsFileContract.FIELD_FILENAME))).delete();
                    cr.delete(ContactsFileContract.CONTENT_URI, ContactsFileContract.selectionFieldFileUid(uid), null);
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
            cr.notifyChange(ContactsFileContract.CONTENT_URI, null);
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
            final String selection = LeaderTaskProviderMetaData.SelectionKeeper.equals(sb, TaskFileContract.FIELD_UID, fileNew.getId());
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

    public String getError() {
        return mErrorCode;
    }
}