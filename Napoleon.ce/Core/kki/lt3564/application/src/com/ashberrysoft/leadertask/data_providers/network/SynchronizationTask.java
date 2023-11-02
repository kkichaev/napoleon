package com.ashberrysoft.leadertask.data_providers.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.enums.LeaderTaskLanguage;
import com.ashberrysoft.leadertask.fragments.SubtasksListFragment;
import com.ashberrysoft.leadertask.fragments.TasksListFragment;
import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;
import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.dialog.AddEmpDialog;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.modern.helper.SimpleLinkReset;
import com.ashberrysoft.leadertask.modern.helper.SingleTaskResetHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.helper.FullTasksResetHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.CursorySyncLogger;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.ZipCompres;
import com.ashberrysoft.leadertask.views.LTCalendarView;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

import okhttp3.Response;

import static com.ashberrysoft.leadertask.utils.Utils.isMyServiceRunning;
import static com.ashberrysoft.leadertask.utils.Utils.writeToFullLog;

public class SynchronizationTask extends Thread {

    private final Context mContext;
    private final LeaderTaskUser mUser;
    private final LTApplication mApp;
    private final String LogsFileName = "logs.zip";
    private final String Android = "Android ";
    private final String OK = "OK";
    private static final String DEFAULT_LIMIT = LionMetaData.TotalLinkContract._ID + SharedStrings.LIMIT + 1;
    private static final String  SendErrorParametersNames[] = new String[] {"email", "type_system", "device", "app_version", "error_msg", "add_info", "datafile"};
    private final CursorySyncLogger syncLogger;
    private final MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    private ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
    private int TypeOfException = 0;
    private LeaderTaskException LTException;
    private LeaderException LException;
    private Exception Exception;
    public static boolean isBigSync = true;
    public static boolean isSwipeSync = false;
    public static boolean isHasCanceledTaskInUpdate = false;
    private boolean isAfterException;
    private LTSettings mSettings;
    private DbHelper dbHelper;
    private List <String> mTasksUUIDSNewForMeNotifies;
    private List <String> mTasksUUIDSByMeCanceledNotifies;
    private List <String> mTasksUUIDSWhereNeedShowMessagesNotifies;

    public SynchronizationTask(Context context, LeaderTaskUser user) {
        super(SynchronizationTask.class.getSimpleName());

        mContext = context;
        mUser = user;
        mApp = (LTApplication) context.getApplicationContext();
        syncLogger = CursorySyncLogger.getInstance(mApp);
        mSettings = LTSettings.getInstance();
        dbHelper = DbHelper.getInstance(mContext);
        mTasksUUIDSNewForMeNotifies = new ArrayList<>();
        mTasksUUIDSByMeCanceledNotifies = new ArrayList<>();
        mTasksUUIDSWhereNeedShowMessagesNotifies = new ArrayList<>();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        android.util.Log.v("Tedorius", "Синхр старт");
        // Засекаем время старта синхронизации
        final long startTime = System.currentTimeMillis();
        final double begin = System.currentTimeMillis();
        TasksListFragment.clearData();
        if (!SubtasksListFragment.isDataEmpty()) {
            final List<Task> data = SubtasksListFragment.getLastListOfData();
            SubtasksListFragment.clearData();
            SubtasksListFragment.addListToDataAtFirst(data);
        }


        final SyncInfo si = new SyncInfo();
        si.setSyncStatus(SyncInfoErrorType.IN_PROGRESS);
        mApp.getContentResolver().update(SyncInfoContract.CONTENT_URI, si.getContentValues(), null, null);
        isAfterException = true;

        try {
            mApp.clearAppFolderLogs();

            syncLogger.openFile();
            syncLogger.toLog("Synchronization started\n");
            writeToFullLog("Synchronization started\n", mContext);
            BaseSOAP.setIsSynchronize(true);

            Utils.fixEventForAnalytics(mContext, "Sync", "Sync started");
            //isBigSync = mSettings.isSyncCrashedLastTime() || mSettings.getSessionUUID() == null ? true : false ;
            isBigSync = (mSettings.getSessionUUID() == null ? true : false) || isSwipeSync ;
            if (isBigSync) { //  && IPCConstants.DEBUG
                // большой круг
                // VerifyUser

                if (BaseSOAP.isSynchronize()) {
                    Utils.timeChecker("VerifyUser");
                    final VerifyUser verifyUser = new VerifyUser(mContext, mUser);
                    verifyUser.execute(null);
                    Utils.timeChecker("VerifyUser");
                    final String error = verifyUser.getResult();

                    if (verifyUser.getInviteUUID() != null) {
                        if (!verifyUser.getInviteUUID().isEmpty()) {
                            mSettings.setNeedShowInvite(true);
                            Utils.showInviteDialog(mContext, verifyUser.getInviteName(), verifyUser.getInviteEmail(), verifyUser.getInviteOrg(), verifyUser.getInviteUUID());
                        }
                    }
                    Utils.showToastsInviteAccepted(mContext);

                    if (error != null) {
                        if (verifyUser.getErrorCode() == 16) {
                            new CreateSession(mContext, mUser).execute(null);
                        }
                        throw LeaderException.create(verifyUser.getErrorCode());
                    }
                }
                // ProcessAll полностью c ююидом сессии---------------------------------------------
                if (BaseSOAP.isSynchronize()) {
                    try {
                        mSettings.getTasksToUpdate().clear();
                        mSettings.getTasksToDelete().clear();
                        mSettings.getAllTimersNames().clear();
                        mSettings.getAllTimers().clear();

                        Utils.timeChecker("CreateSession");
                        new CreateSession(mContext, mUser).execute(null);

                        Utils.timeChecker("CreateSession");
                        Utils.timeChecker("ProcessAll");
                        // ProcessAll---------------------------------------------------------------
                        final ProcessAll process = new ProcessAll(mApp, mUser, dbHelper.getAllProjectsForSynchronization(), dbHelper.getAllCategories(),
                                dbHelper.getAllContactsForSynchronization(), dbHelper.getAllContactsGroupsForSynchronization(),
                                dbHelper.getMarkerDao().queryBuilder().selectColumns(Marker.FIELD_UID, Marker.FIELD_USN).query(), dbHelper.getAllTasksMessages(), false);

                        process.execute(null);
                        Utils.timeChecker("ProcessAll");
                        mTasksUUIDSNewForMeNotifies = process.getTasksForMeNew();
                        mTasksUUIDSByMeCanceledNotifies = process.getTasksByMeCanceled();
                        mTasksUUIDSWhereNeedShowMessagesNotifies = process.getTasksWhereMessages();

                        //--------------------------------------------------------------------------

                        Utils.timeChecker("PreparePutAll");
                        final List<Category> entitiesCategories = dbHelper.getCategoryDao().queryBuilder().where().in(Category.FIELD_UID, process.getListSendCategories()).query();
                        final List<Project> entitiesProjects = dbHelper.getProjectDao().queryBuilder().where().in(Project.FIELD_UID, process.getListSendProjects()).query();
                        final List<ContactsGroup> entitiesContactsGroups = dbHelper.getContactsGroupDao().queryBuilder().where().in(ContactsGroup.FIELD_UID, process.getListSendContactGroups()).query();
                        final List<Contact> entitiesContacts = dbHelper.getContactDao().queryBuilder().where().in(ContactContract.UID, process.getListSendContacts()).query();
                        final List<Marker> entitiesMarkers = dbHelper.getMarkerDao().queryBuilder().where().in(Marker.FIELD_UID, process.getListSendMarkers()).query();
                        final List<TaskMessage> entitiesTaskMessages = dbHelper.getTaskMessageDao().queryBuilder().where().in(TaskMessage.FIELD_UID, process.getListSendTaskMessages()).query();
                        final List<LTask> entitiesTasks = getTasksToSend(process.getListSendTasks(), mContext);
                        //final List<TaskFile> entitiesTaskFiles = dbHelper.getTaskFiles().queryBuilder().where().in(LeaderTaskProviderMetaData.TaskFileContract.FIELD_UID, process.getListSendTaskFiles()).query();
                        final List<TaskFile> entitiesTaskFiles = dbHelper.getAllTaskFilesForPutAll();
                        Utils.timeChecker("PreparePutAll");
                        // PutAll-------------------------------------------------------------------

                        Utils.timeChecker("PutAll");
                        new PutAll(mApp, mUser, entitiesCategories, entitiesProjects, entitiesContactsGroups, entitiesContacts, entitiesMarkers, entitiesTaskMessages, entitiesTasks, entitiesTaskFiles, false).execute(null);
                        Utils.timeChecker("PutAll");
                        isAfterException = false;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        updateCountersAndCache();
                    }
                }
                //----------------------------------------------------------------------------------
            } else {
                // малый круг
                // VerifyUser
                if (BaseSOAP.isSynchronize()) {
                    final VerifyUser verifyUser = new VerifyUser(mContext, mUser);
                    verifyUser.execute(null);
                    final String error = verifyUser.getResult();
                    if (verifyUser.getInviteUUID() != null) {
                        if (!verifyUser.getInviteUUID().isEmpty()) {
                            mSettings.setNeedShowInvite(true);
                            Utils.showInviteDialog(mContext, verifyUser.getInviteName(), verifyUser.getInviteEmail(), verifyUser.getInviteOrg(), verifyUser.getInviteUUID());
                        }
                    }

                    Utils.showToastsInviteAccepted(mContext);

                    if (error != null) {
                        if (verifyUser.getErrorCode() == 16) {
                            new CreateSession(mContext, mUser).execute(null);
                        }
                        throw LeaderException.create(verifyUser.getErrorCode());
                    }
                }
                //
                if (BaseSOAP.isSynchronize()) {
                    try {
                        // ProcessAll для фоток-------------------------------------------------------------
                        mSettings.getTasksToUpdate().clear();
                        mSettings.getTasksToDelete().clear();
                        mSettings.getAllTimersNames().clear();
                        mSettings.getAllTimers().clear();
                        boolean needPutDeleted;
                        boolean sessionNotCreated = false;
                        String order;
                        ProcessAll processAll = new ProcessAll(mApp, mUser, null, null, null, null, null, null, true);
                        processAll.execute(null);
                        //если сессия крашнулась
                        if (processAll.getError().equals(""+LeaderTaskException.ERROR_SESSION_NOT_CREATED)) {
                            sessionNotCreated = true;
                        }

                        //-------------GetSessionChanges
                        GetSessionChanges changes = new GetSessionChanges(mApp, mUser);
                        changes.execute(null);

                        mTasksUUIDSNewForMeNotifies = changes.getTasksForMeNew();
                        mTasksUUIDSByMeCanceledNotifies = changes.getTasksByMeCanceled();
                        mTasksUUIDSWhereNeedShowMessagesNotifies = changes.getTasksWhereMessages();

                        //или крашнулась вот тут
                        if (changes.getError().equals("" + LeaderTaskException.ERROR_SESSION_NOT_CREATED)) {
                            sessionNotCreated = true;
                        }
                        if (sessionNotCreated) {
                            // сессия крашнулась /////////////////////////////////////////////////////////////////////////////////////////////////////////
                            mApp.clearAppFolderLogs();
                            mSettings.setSessionUUID(null);
                            isBigSync = true;

                            new CreateSession(mContext, mUser).execute(null);

                            mSettings.getTasksToUpdate().clear();
                            mSettings.getTasksToDelete().clear();
                            mSettings.getAllTimersNames().clear();
                            mSettings.getAllTimers().clear();

                            final ProcessAll process = new ProcessAll(mApp, mUser, dbHelper.getAllProjectsForSynchronization(), dbHelper.getAllCategories(),
                                    dbHelper.getAllContactsForSynchronization(), dbHelper.getAllContactsGroupsForSynchronization(),
                                    dbHelper.getMarkerDao().queryBuilder().selectColumns(Marker.FIELD_UID, Marker.FIELD_USN).query(), dbHelper.getAllTasksMessages(), false);

                            process.execute(null);

                            mTasksUUIDSNewForMeNotifies = process.getTasksForMeNew();
                            mTasksUUIDSByMeCanceledNotifies = process.getTasksByMeCanceled();
                            mTasksUUIDSWhereNeedShowMessagesNotifies = process.getTasksWhereMessages();

                            //--------------------------------------------------------------------------
                            final List<Category> entitiesCategories = dbHelper.getCategoryDao().queryBuilder().where().in(Category.FIELD_UID, process.getListSendCategories()).query();
                            final List<Project> entitiesProjects = dbHelper.getProjectDao().queryBuilder().where().in(Project.FIELD_UID, process.getListSendProjects()).query();
                            final List<ContactsGroup> entitiesContactsGroups = dbHelper.getContactsGroupDao().queryBuilder().where().in(ContactsGroup.FIELD_UID, process.getListSendContactGroups()).query();
                            final List<Contact> entitiesContacts = dbHelper.getContactDao().queryBuilder().where().in(ContactContract.UID, process.getListSendContacts()).query();
                            final List<Marker> entitiesMarkers = dbHelper.getMarkerDao().queryBuilder().where().in(Marker.FIELD_UID, process.getListSendMarkers()).query();
                            final List<TaskMessage> entitiesTaskMessages = dbHelper.getTaskMessageDao().queryBuilder().where().in(TaskMessage.FIELD_UID, process.getListSendTaskMessages()).query();
                            final List<LTask> entitiesTasks = getTasksToSend(process.getListSendTasks(), mContext);
                            //final List<TaskFile> entitiesTaskFiles = dbHelper.getTaskFiles().queryBuilder().where().in(LeaderTaskProviderMetaData.TaskFileContract.FIELD_UID, process.getListSendTaskFiles()).query();
                            final List<TaskFile> entitiesTaskFiles = dbHelper.getAllTaskFilesForPutAll();
                            final List<Emp> entitiesEmps = dbHelper.getEmpDao().queryBuilder().where().in(EmpContract.UID, process.getListSendEmps()).query();;
                            // PutAll-------------------------------------------------------------------
                            new PutAll(mApp, mUser, entitiesCategories, entitiesProjects, entitiesContactsGroups, entitiesContacts, entitiesMarkers, entitiesTaskMessages, entitiesTasks, entitiesTaskFiles, false).execute(null);

                            LeaderTaskSyncService.mIsAfterSessionDeleted = true;
                            // сессия крашнулась /////////////////////////////////////////////////////////////////////////////////////////////////////////
                        } else {
                            order = changes.getSessionOrder();
                            needPutDeleted = hasDeletedInfo();

                            //ClearSessionChanges
                            if (order != null && !order.equals("0")) {
                                new ClearSessionChanges(mContext, mUser, mSettings.getSessionUUID(), changes.getSessionOrder()).execute(null);
                            }
                            //--------------
                            final List<Project> entitiesProjects = dbHelper.getAllProjectsForPutAll();
                            final List<Category> entitiesCategories = dbHelper.getAllCategoriesForPutAll();
                            final List<ContactsGroup> entitiesContactsGroups = dbHelper.getAllContactsGroupsForPutAll();
                            final List<Contact> entitiesContacts = dbHelper.getAllContactForPutAll();
                            final List<Marker> entitiesMarkers = dbHelper.getAllMarkersForPutAll();
                            final List<TaskMessage> entitiesTaskMessages = dbHelper.getAllTaskMessagesForPutAll();
                            final List<LTask> entitiesTasks = dbHelper.getAllLTaskForPutAll();
                            final List<TaskFile> entitiesTaskFiles = dbHelper.getAllTaskFilesForPutAll();
                            final List<Emp> entitiesEmps = dbHelper.getAllEmpsForPutAll();
                            // PutAll-------------------------------------------------------------------
                            if (mSettings.isNeedToPutSettings()  || needPutDeleted || (!entitiesProjects.isEmpty() || !entitiesCategories.isEmpty() || !entitiesContactsGroups.isEmpty() || !entitiesContacts.isEmpty() || !entitiesMarkers.isEmpty() || !entitiesTaskMessages.isEmpty() || !entitiesTaskFiles.isEmpty() || !entitiesTasks.isEmpty() || !entitiesEmps.isEmpty())) {
                                // если есть что отправить
                                //если нечего отправить но есть что удалить
                                new PutAll(mApp, mUser, entitiesCategories, entitiesProjects, entitiesContactsGroups, entitiesContacts, entitiesMarkers, entitiesTaskMessages, entitiesTasks, entitiesTaskFiles, needPutDeleted).execute(null);
                            }
                        }

                        isAfterException = false;
                    }
                    finally {
                        updateCountersAndCache();
                    }
                }

            }
        }
        catch (LeaderTaskException e) {
            TypeOfException = 1;
            LTException = e;
            writeToFullLog(e, mContext);
            syncLogger.toLog(e);
            if(isExeptionNeedToOpenDialog(LTException.getCode())) {  // если коды ошибок соответствуют
                    canselSyncAndShowDialog(); // выводим диалоги
            }
            else {
                SendErrorToServer(); // отправляем и если не ок то выводим диалог
            }
            return;
        }
        catch (LeaderException e) {
            TypeOfException = 2;
            LException = e;
            writeToFullLog(e, mContext);
            syncLogger.toLog(e);
            if(LException != null && isExeptionNeedToOpenDialog(LException.getCode())) {  // если коды ошибок соответствуют
                canselSyncAndShowDialog(); // выводим диалоги
            }
            else {
                SendErrorToServer(); // отправляем и если не ок то выводим диалог
            }
        }
        catch (Exception e) {
            TypeOfException = 3;
            Exception = e;
            writeToFullLog(e, mContext);
            syncLogger.toLog(e);
            SendErrorToServer(); // просто отправляем, если не ок - ничего не делаем
            return;
        }
        finally {
            mApp.cancelSynchronize();
            SynchronizationTask.isHasCanceledTaskInUpdate = false;

            Utils.updateTodayWidget(mContext);

            if (LTSettings.getInstance().needToDownloadPhotoGoogleFacebook()) {
                Utils.googleImageDownload(mContext, LTSettings.getInstance().downloadUriGoogleFacebook());
            }

            LeaderTaskSyncService.syncWear();

            if (!LTSettings.getInstance().isNeedToShowLoadingScreen()) {
                // notifies
                /*if (mTasksUUIDSNewForMeNotifies != null && mSettings.isNotifyForMe()) {
                    for (int i = 0; i < mTasksUUIDSNewForMeNotifies.size(); i++) {
                        TaskNotifyHelper.getInstance(mApp.getApplicationContext()).showTaskNotifyNewAssignmentToMe(getTask(mContext, mTasksUUIDSNewForMeNotifies.get(i)), i == 0);
                    }
                }*/
                if (mTasksUUIDSByMeCanceledNotifies != null && mSettings.isNotifyByMeCanceled()) {
                    for (int i = 0; i < mTasksUUIDSByMeCanceledNotifies.size(); i++) {
                        TaskNotifyHelper.getInstance(mApp.getApplicationContext()).showTaskNotifyCancelAssignmentFromMe(getTask(mContext, mTasksUUIDSByMeCanceledNotifies.get(i)), i == 0);
                    }
                }
                if (mTasksUUIDSWhereNeedShowMessagesNotifies != null && mSettings.isNotifyComments()) {
                    for (int i = 0; i < mTasksUUIDSWhereNeedShowMessagesNotifies.size(); i++) {
                        boolean hasManyComments = false;
                        int comments = 0;
                        for (String uid : mTasksUUIDSWhereNeedShowMessagesNotifies) {
                            if (mTasksUUIDSWhereNeedShowMessagesNotifies.get(i).equals(uid)) {
                                comments++;
                            }
                        }
                        hasManyComments = comments > 1;
                        TaskNotifyHelper.getInstance(mApp.getApplicationContext()).showTaskNotifyNewComment(getTask(mContext, mTasksUUIDSWhereNeedShowMessagesNotifies.get(i)), i == 0, hasManyComments);
                    }
                }
            }

            if (isSwipeSync) {
                isSwipeSync = false;
            }

            //Установка времени последней синхронизации, если все удачно прошло без ошибок
            //if (isBigSync) {
                TaskNotifyHelper.getInstance(mContext).convertTasksToNotify();
            //}
            if (!isAfterException) {
                LTSettings.isNeedToRunLoadingScreen = false;
                mSettings.setLastSynchronization(new Date(System.currentTimeMillis()));
            }
            if (LTSettings.getInstance().isNeedToShowLoadingScreen() && isAfterException) {
                Utils.startSyncAlways(mApp);
            }
            //
            final double eend = System.currentTimeMillis();
            final double result = (eend-begin)/1000;
            android.util.Log.v("Tedorius","Synchronisation: "+Double.toString(result));
            MenuLoader.getInstance(mApp).resetMyFoto();
            //MenuLoader.getInstance(mContext).updateItemsList(true);

            final Date endDate = new Date(System.currentTimeMillis() - startTime);
            if (syncLogger != null) {
                try {
                    syncLogger.toLog("Synchronisation complete\n");
                    syncLogger.toLog("The duration of synchronization ", endDate);
                } finally {
                    syncLogger.closeFile();
                }
            }

            writeToFullLog("Synchronization complete\n", mContext);

            Utils.toLog(Task.SDF.format(endDate));

            LTCalendarView.clearCalendarData(mContext);
            mContext.getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);
            canselSync();

            try {
                if (LeaderTaskSyncService.mIsAfterSessionDeleted || isBigSync) {
                    LeaderTaskSyncService.mIsAfterSessionDeleted = false;
                    LeaderTaskSyncService.webSync();
                }

                if (LeaderTaskSyncService.mIsNeedToResync) {
                    LeaderTaskSyncService.mIsNeedToResync = false;
                    Utils.startSyncAlways(mApp);
                }

                if (!isMyServiceRunning(LeaderTaskSyncService.class, mApp)) {
                    Intent intent = new Intent(mApp, LeaderTaskSyncService.class);
                    mApp.startService(intent);
                } else {
                    //если запущен и сессия null
                    if (LeaderTaskSyncService.mUidSession == null) {
                        LeaderTaskSyncService.webSync();
                    }
                }
            } catch (Exception e) {

            }

        }
    }


////////////////////////////////////////////////// FUNCTIONS ////////////////////////////////////////
    private void checkDublicateTasksFiles() {
        String where = "task_files._id NOT IN ( SELECT MIN(task_files._id) FROM task_files GROUP BY task_files.Uid )";
        mContext.getContentResolver().delete(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, where, null);
    }

    private boolean hasDeletedInfo() {

        Cursor category = null;
        Cursor project = null;
        Cursor contact = null;
        Cursor contactFiles = null;
        Cursor marker = null;
        Cursor tasks = null;
        Cursor taskFiles = null;
        Cursor emps = null;

        category = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(Category.SERVER_CLASS), null, null);
        if (category.getCount() > 0) {
            category.close();
            return true;
        }
        if (category != null) {
            category.close();
        }
        //
        project = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(Project.SERVER_CLASS), null, null);
        if (project.getCount() > 0) {
            project.close();
            return true;
        }
        if (project != null) {
            project.close();
        }
        //
        contact = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(LeaderTaskProviderMetaData.ContactContract.SERVER_CLASS), null, null);
        if (contact.getCount() > 0) {
            contact.close();
            return true;
        }
        if (contact != null) {
            contact.close();
        }
        //
        contactFiles = mContext.getContentResolver().query(LeaderTaskProviderMetaData.ContactsFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.ContactsFileContract.selectionDeleteObject(true),
                null, null);
        if (contactFiles.getCount() > 0) {
            contactFiles.close();
            return true;
        }
        if (contactFiles != null) {
            contactFiles.close();
        }
        //
        marker = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(Marker.SERVER_CLASS), null, null);
        if (marker.getCount() > 0) {
            marker.close();
            return true;
        }
        if (marker != null) {
            marker.close();
        }
        //
        tasks = mContext.getContentResolver().query(LionMetaData.DeleteUidContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.SelectionKeeper.equals(null, LionMetaData.DeleteUidContract.LionName, LionMetaData.LTaskContract.TABLE_NAME), null, null);
        if (tasks.getCount() > 0) {
            tasks.close();
            return true;
        }
        if (tasks != null) {
            tasks.close();
        }
        //
        taskFiles = mContext.getContentResolver().query(LeaderTaskProviderMetaData.TaskFileContract.CONTENT_URI, null, LeaderTaskProviderMetaData.TaskFileContract.selectionDeleteObject(true),
                null, null);
        if (taskFiles.getCount() > 0) {
            taskFiles.close();
            return true;
        }
        if (taskFiles != null) {
            taskFiles.close();
        }
        //
        emps = mContext.getContentResolver().query(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, null,
                LeaderTaskProviderMetaData.UidToDeleteContract.selectionServerClass(LeaderTaskProviderMetaData.EmpContract.SERVER_CLASS), null, null);
        if (emps.getCount() > 0) {
            emps.close();
            return true;
        }
        if (emps != null) {
            emps.close();
        }
        return false;

    }

    private LTask getTask(Context context, String uid) {
        //ищем задачу
        Cursor cursorTask = null;
        LTask task = null;
        try {
            cursorTask = context.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(new StringBuilder(), LionMetaData.LTaskContract.Uid, uid), null, null);
            if (cursorTask.getCount() > 0) {
                cursorTask.moveToFirst();
                task = new LTask(cursorTask);
            }
        } finally {
            if (cursorTask != null) {
                cursorTask.close();
            }
        }
        return task;
    }

    private void checkEmpsIsCreated () {
        final Cursor r = mContext.getContentResolver().query(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null, null, null, null);
        try {
            List<Employee> notFonded = new ArrayList<>();
            final List<Employee> list = DbHelper.getListEmployeesForNavNew(mContext);


            if (r.getCount() > 0) {
                final Emp emp = new Emp();
                for (Employee employee : list) {
                    boolean founded = false;
                    for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                        emp.setData(r);
                            if (employee.getEmail().equals(emp.getLogin())) {

                                founded = true;
                                break;
                            }
                        }

                    if (!founded) {
                        notFonded.add(employee);
                    }
                }
            }

            if (!notFonded.isEmpty()) {
                for (Employee employee : notFonded) {
                    AddEmpDialog.updateListAfterUserAdd(employee, mContext);
                }
                ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

                if (LTApplication.mBackStackActivities.containsKey(cn.getClassName()) ) {
                    final BaseActivity activity = LTApplication.mBackStackActivities.get(cn.getClassName());
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            MenuLoader.getInstance(mApp).updateItemsListEmp();
                        }
                    });
                }
            }

        } catch (Exception e) {

        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

    private void updateCountersAndCache() {
        EmployeeCache.getInstance(mContext).refreshCache();
        //
        MarkerCache.getInstance(mContext).refreshCache();
        //
        checkEmpsIsCreated();
        //
        if (isBigSync) {
            //если потянули - обновляем все счетчики
            checkDublicateTasksFiles();
            TaskFileCache.getInstance(mContext).refreshCache();
            new FullTasksResetHelper(mContext, isAfterException);
        } else {
            TaskFileCache.getInstance(mContext).refreshCache();
            // если автоматическое обновление - быстро обновляем
            ArrayList <String> tasksToUpdate = new ArrayList<>();
            tasksToUpdate.addAll(mSettings.getTasksToUpdate());

            if (tasksToUpdate.size() > 5) {
                new FullTasksResetHelper(mContext, isAfterException);
            } else {
                if (tasksToUpdate.size() == 0) {
                    // обновление категорий и проектов
                    new SimpleLinkReset(mContext, isAfterException, false);
                } else {
                    // от 1 до 5 ПОЗАДАЧНОЕ ОБНОВЛЕНИЕ
                    for (String uid : tasksToUpdate) {
                        new SingleTaskResetHelper(mContext, isAfterException, uid);
                    }
                    new SimpleLinkReset(mContext, isAfterException, false);
                }
            }
        }
    }

    private ArrayList <LTask> getTasksToSend(List<String> uids, Context context) {
        ///
        ArrayList tasks = new ArrayList();
        for (int i = 0; i < uids.size(); i++) {
            uids.get(i).toLowerCase();
        }
        try {
            Cursor c = null;
            try {
                c = context.getContentResolver().query(LTaskContract.CONTENT_URI, null, null, null, null);
                if (c.getCount() > 0) {
                    final int uid = c.getColumnIndex(LTaskContract.Uid);
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        if (uids.contains(c.getString(uid))) {
                            LTask tempTask = new LTask(c);
                            if (!tasks.contains(tempTask)) {
                                tasks.add(tempTask);
                            }
                        }
                    }
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        } finally {
            return tasks;
        }
    }

    private boolean isExeptionNeedToOpenDialog(int ExeptionCode) {
        //отправлять на сервер все ошибки кроме 3 6 9 15 16 11111 24 26(new!) 14(new!)
        if(ExeptionCode == LTServerError.WRONG_AUTH.getCode()
                || ExeptionCode == LTServerError.ACCOUNT_EXPIRED.getCode()
                || ExeptionCode == LTServerError.ACCOUNT_FROZEN.getCode()
                || ExeptionCode == LTServerError.STANDARD_VERSION_DURING_SYNCHRONIZATION.getCode()
                || ExeptionCode == LTServerError.END_EMP_LIMIT.getCode()
                || ExeptionCode == LTServerError.INTERNET_ACCESS.getCode()
                || ExeptionCode == LTServerError.NEED_CONFIRM_REGISTRATION.getCode()
                || ExeptionCode == LTServerError.API_DISABLED.getCode()
                || ExeptionCode == LTServerError.ACCOUNT_BLOCKED.getCode()
                || ExeptionCode == LTServerError.NO_SPACE_ON_DEVISE.getCode()
                || ExeptionCode == LTServerError.WRONG_SERV_1.getCode()
                || ExeptionCode == LTServerError.WRONG_SERV_503.getCode()
                || ExeptionCode == LTServerError.WRONG_SERV_504.getCode()) {
            return true;
        }
        return false;
    }

    private File pressLogsToZIP() {
        final File[] logs = mApp.getAppFolderLogs().listFiles();
        final File zipFile = new File(mApp.getAppFolderLogs(), LogsFileName);
        final File dbFile = IPCConstants.DEBUG ? mApp.getDatabasePath(DbHelper.DATABASE_NAME) : null;
        new ZipCompres(logs, dbFile, zipFile).toZip();
        return zipFile;
    }

    private void canselSync() {
        mSettings.setIsMySync(false);
        final ContentValues cv = new ContentValues(1);
        cv.put(SyncInfoContract.SYNC_STATUS, SyncInfoErrorType.ENDED.ordinal());
        SyncInfo.updateSynchronizationInfo(mApp, cv);

        if (SlidingActivity.mSwipeRefreshLayout != null) {
            SlidingActivity.mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    SlidingActivity.mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void canselSyncAndShowDialog() {
        mSettings.setIsMySync(false);
        final ContentValues cv = new ContentValues(2);
        cv.put(SyncInfoContract.SYNC_STATUS, SyncInfoErrorType.ERROR.ordinal());
        switch (TypeOfException)
        {
            case 1:
                cv.put(SyncInfoContract.ERROR_MESSAGE, LTException.toString());
                cv.put(SyncInfoContract.ERROR_CODE, LTException.getCode());
                break;
            case 2:
                cv.put(SyncInfoContract.ERROR_MESSAGE, LException.toString());
                cv.put(SyncInfoContract.ERROR_CODE, LException.getCode());
                break;
        }
        SyncInfo.updateSynchronizationInfo(mApp, cv);
    }

    private String wirteTag(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(CursorySyncLogger.COLON);
        return s;
    }

    private String writeValue(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append(s);
        return s;
    }

    private void SendErrorToServer()
    {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[0], mSettings.getUserName()));
            nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[1], Android+Build.VERSION.RELEASE));
            nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[2], Build.MODEL));
            nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[3],((LTApplication) mContext.getApplicationContext()).getApplicationBuildVersion()));
            switch (TypeOfException)
            {
                case 1:
                    nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[4], "LeaderTaskException: " + LTException.toString()));
                    nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[5], stackTraseToString(LTException).toString()));
                    break;
                case 2:
                    nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[4], "LeaderException: " + LException.toString()));
                    nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[5], stackTraseToString(LException).toString()));
                    break;
                case 3:
                    nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[4], "Exception: " + LTException.toString()));
                    nameValuePairs.add(new BasicNameValuePair(SendErrorParametersNames[5], stackTraseToString(Exception).toString()));
                    break;
                default:
                    break;
            }


            Response response = OkHttpConnection.uploadFile(LTSettings.getInstance().getSyncSendError(), pressLogsToZIP(), nameValuePairs);
            response.body().string();
            final int statusCode = response.code();
            if (statusCode != 200 && TypeOfException != 3) { //если ответ НЕ ОК, и Exception 1 или 2
                canselSyncAndShowDialog(); // то выводим диалог
            } else { //если ответ ОК или выводить диалог не нужно(Ответ не ОК, но вид ошибки не для диалога)
                canselSync(); // то заканчиваем  без собщения
            }
        } catch (Exception e_new) {
            syncLogger.toLog(e_new);
        }
    }

    private StringBuilder stackTraseToString(Exception e) {
        StringBuilder str = new StringBuilder();
        str.append(e.getMessage());
        str.append(SharedStrings.NEW_LINE_C);
        str.append(e.getClass().getName());
        for (StackTraceElement s : e.getStackTrace()) {
            str.append(SharedStrings.NEW_LINE_C);
            str.append(s.toString());
        }
        return  str;
    }

}