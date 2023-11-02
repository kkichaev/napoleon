package com.ashberrysoft.leadertask.instance_sync;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.modern.activity.AddNewTaskWidgetActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.receivers.NetworkStateReceiver;
import com.ashberrysoft.leadertask.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fm.SingleAction;
import fm.websync.Client;
import fm.websync.ConnectArgs;
import fm.websync.ConnectFailureArgs;
import fm.websync.ConnectRetryMode;
import fm.websync.ConnectSuccessArgs;
import fm.websync.StreamFailureArgs;
import fm.websync.SubscribeArgs;
import fm.websync.SubscribeFailureArgs;
import fm.websync.SubscribeReceiveArgs;
import fm.websync.SubscribeSuccessArgs;

public class LeaderTaskSyncService extends IntentService implements NetworkStateReceiver.NetworkStateReceiverListener , GoogleApiClient.ConnectionCallbacks {

    private static final SimpleDateFormat SDF_24H = getSimpleDateFormat(true);
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    public static String mLastSeesionMassage = "";
    private NetworkStateReceiver networkStateReceiver;
    public static boolean mIsNeedToResync;
    public static boolean mIsAfterSessionDeleted ;
    public static String mUidSession ;
    public static Client client;
    public static LTApplication mApp;
    private static GoogleApiClient mGoogleApiClient;
    private static final String KEY = "/todaytasks";
    private static final String COUNT_KEY_LOGIN = "com.ashberrysoft.leadertask.login";
    private static final String COUNT_KEY = "com.ashberrysoft.leadertask.tasks";
    private static final String COUNT_KEY2 = "com.ashberrysoft.leadertask.tasksuuids";
    private static final String COUNT_KEY3 = "com.ashberrysoft.leadertask.emails";
    private static final String COUNT_KEY10 = "com.ashberrysoft.leadertask.emailsemails";
    private static final String COUNT_KEY4 = "com.ashberrysoft.leadertask.taskstome";
    private static final String COUNT_KEY5 = "com.ashberrysoft.leadertask.taskstomeids";
    private static final String COUNT_KEY6 = "com.ashberrysoft.leadertask.taskstomeuidcustomer";

    private static final String COUNT_KEY7 = "com.ashberrysoft.leadertask.myemail";
    private static final String COUNT_KEY8 = "com.ashberrysoft.leadertask.taskstodaycustomer";
    private static final String COUNT_KEY9 = "com.ashberrysoft.leadertask.taskstodayperformers";
    public static  NotificationManager nm;


    public LeaderTaskSyncService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            startForeground(1,new Notification());

        mIsNeedToResync = false;
        mIsAfterSessionDeleted = false;
        mLastSeesionMassage = "";
        mUidSession = null;

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        IntentFilter s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);

        this.registerReceiver(new CheckTimeForNotifyReceiver(), s_intentFilter);
    }



    public int onStartCommand(Intent intent, int flags, int startId) {
        //
        mApp = (LTApplication) getApplicationContext();
        MyInstanceIDListenerService.regToken(mApp);
        webSync();

        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(Wearable.API)
                    .build();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {

        }

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //
        if (!mApp.getSettings().isFirstLaunch()) {
            sendNotif(mApp);
        } else {
            mApp.getSettings().setIsFirstLaunch(false);
        }

        //return super.onStartCommand(intent, flags, startId);
         return START_STICKY;
    }

    public static void sendNotif(Context context) {
        try {
            if (nm == null) {
                nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            }

            if (LTSettings.getInstance().isShowPanel() && LTSettings.getInstance().getUserProfile().isValid()) {
                //Intent intent = new Intent(this, AddTaskWidget.class);
                Intent firstDialogIntent = new Intent(context, AddNewTaskWidgetActivity.class);
                // Old activities shouldn't be in the history stack
                firstDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, firstDialogIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //PendingIntent.getActivity(this, requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                Notification.Builder builder = new Notification.Builder(context);

                builder.setAutoCancel(false);
                //builder.setTicker("this is ticker text");
                builder.setContentTitle(context.getResources().getString(R.string.menu_add_task));
                //builder.setContentText("You have a new message");
                //builder.setSmallIcon(R.drawable.icon_lt);
                builder.setSmallIcon(R.drawable.notification_icon);
                builder.setContentIntent(pendingIntent);
                builder.setOngoing(true);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    builder.setShowWhen(false);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    final String DEFAULT_CHANNEL_ID = "default_channel";
                    @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                            DEFAULT_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                    nm.createNotificationChannel(channel);
                    builder.setChannelId(DEFAULT_CHANNEL_ID);
                }

                //builder.setSubText("This is subtext...");
                //builder.setNumber(100);
                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_NO_CLEAR;

                nm.notify(365365, notification);
            } else {
                nm.cancel(365365);
            }
        } catch (Exception e) {

        }

    }



    public static void closeNotify() {
        if (nm != null) {
            nm.cancel(365365);
        } else {
            if (mApp != null) {
                nm = (NotificationManager) mApp.getSystemService(NOTIFICATION_SERVICE);
                if (nm != null) {
                    nm.cancel(365365);
                }
            }
        }
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("Tedorius", "onConnected");
    }

    public static  void syncWearFull() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                new DataTask3().execute();
                new DataTask().execute();
            }
        }
    }
    public static  void syncWear() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                new DataTask().execute();
            }
        }
    }

    public static  void syncWearLogIn() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                new DataTask2().execute();
                new DataTask().execute();
            }
        }
    }

    static class DataTask2  extends AsyncTask<Node, Void, Void> {
        public DataTask2 () {
        }

        @Override
        protected Void doInBackground(Node... nodes) {
            if (mApp != null) {
                    PutDataMapRequest putDataMapReq = PutDataMapRequest.create(KEY);
                    //putDataMapReq.getDataMap().putBoolean(COUNT_KEY_LOGIN, LTSettings.getInstance().getUserProfile().isValid());
                    putDataMapReq.getDataMap().putBoolean(COUNT_KEY_LOGIN, false);

                    PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                    if (mGoogleApiClient != null) {
                        if (mGoogleApiClient.isConnected()) {
                            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                            Log.e("Tedorius", "Часам послали инфу params");
                        }
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    PutDataMapRequest putDataMapReq2 = PutDataMapRequest.create(KEY);
                    putDataMapReq.getDataMap().putBoolean(COUNT_KEY_LOGIN, LTSettings.getInstance().getUserProfile().isValid());
                    //putDataMapReq.getDataMap().putBoolean(COUNT_KEY_LOGIN, false);

                    PutDataRequest putDataReq2 = putDataMapReq.asPutDataRequest();
                    if (mGoogleApiClient != null) {
                        if (mGoogleApiClient.isConnected()) {
                            PendingResult<DataApi.DataItemResult> pendingResult2 = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                            Log.e("Tedorius", "Часам послали инфу params");
                        }
                    }

            }
            return null;
        }
    }

    static class DataTask  extends AsyncTask<Node, Void, Void> {
        public DataTask () {
        }

        @Override
        protected Void doInBackground(Node... nodes) {
            if (mApp != null) {

                ArrayList<String> listNamesToMe = new ArrayList<>();
                ArrayList<String> listUUIDsToMe = new ArrayList<>();
                ArrayList<String> emailsCustomersToMe = new ArrayList<>();
                ArrayList<String> listNames = new ArrayList<>(); // names of tasks
                ArrayList<String> listUUIDs = new ArrayList<>(); // UUIDs of tasks
                ArrayList<String> customersToday = new ArrayList<>();
                ArrayList<String> performerToday = new ArrayList<>();
                ArrayList<String> emailsCoworkers = new ArrayList<>(); // emailsCoworkers to adding task
                ArrayList<String> namesCoworkers = new ArrayList<>(); // emailsCoworkers to adding task
                try {
                    // ПОРУЧЕНО МНЕ

                    Cursor cursor = null;
                    try {
                        TaskSelectionBuilder mSelectionBuilder = new TaskSelectionBuilder(new StringBuilder());
                        cursor = mApp.getContentResolver().query(LTaskContract.CONTENT_URI, null,
                                mSelectionBuilder.getForMeTasksAll(LTSettings.getInstance().getUserName()).build(), null, new TaskSelectionBuilder().getOrderForTasks().build());
                        if (cursor.getCount() > 0) {
                            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                LTask task = new LTask(cursor);
                                listNamesToMe.add(task.getName());
                                listUUIDsToMe.add(task.getUid());
                                emailsCustomersToMe.add(task.getEmailCustomer());
                            }
                        }
                    } catch (Exception e) {

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }

                    // ЗАДАЧИ НА СЕГОДНЯ
                    Cursor c = null;
                    try {
                        TaskSelectionBuilder mSelectionBuilder = new TaskSelectionBuilder(new StringBuilder());
                        c = mApp.getContentResolver().query(LTaskContract.CONTENT_URI, null,
                                mSelectionBuilder.getCalendarByDay(TimeHelper.currentTimeMillisWithoutTimeZone()).build(), null, new TaskSelectionBuilder().getOrderForTasks().build());
                        if (c.getCount() > 0) {
                            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                LTask task = new LTask(c);
                                listNames.add(task.getName());
                                listUUIDs.add(task.getUid());
                                customersToday.add(task.getEmailCustomer());
                                performerToday.add(task.getEmailPerformer());
                            }

                        }
                    } catch (Exception e) {

                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                    //

                    List<Employee> mEmployees = DbHelper.getListEmployees(mApp);

                    for (Employee emp : mEmployees) {
                        namesCoworkers.add(emp.getName());
                        emailsCoworkers.add(emp.getEmail());
                    }

                } catch (Exception e) {

                } finally {
                    PutDataMapRequest putDataMapReq = PutDataMapRequest.create(KEY);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY, listNames);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY2, listUUIDs);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY3, namesCoworkers);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY10, emailsCoworkers);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY4, listNamesToMe);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY5, listUUIDsToMe);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY6, emailsCustomersToMe);
                    putDataMapReq.getDataMap().putString(COUNT_KEY7, LTSettings.getInstance().getUserName());
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY8, customersToday);
                    putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY9, performerToday);
                    //
                    //
                    //putDataMapReq.getDataMap().putBoolean(COUNT_KEY_LOGIN, LTSettings.getInstance().getUserProfile().isValid());
                    //

                    /*try {
                        List<String> mEmps = DbHelper.getListEmpsForWear(mApp);
                        for (String empEmail : mEmps) {
                            File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + empEmail);
                            if (cacheImgFile.exists()) { // если есть уменьшенная закешированная фотка
                                byte[] byteArray = null;
                                try {
                                    byteArray = readFile(cacheImgFile);
                                } catch (Exception e) {

                                } finally {
                                    if (byteArray != null) {
                                        putDataMapReq.getDataMap().putByteArray(empEmail, byteArray);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }*/
                    try {
                        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                        if (mGoogleApiClient != null) {
                            if (mGoogleApiClient.isConnected()) {
                                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
            return null;
        }
    }

    static class DataTask3  extends AsyncTask<Node, Void, Void> {
        public DataTask3 () {
        }

        @Override
        protected Void doInBackground(Node... nodes) {
            if (mApp != null) {

                ArrayList<String> listNamesToMe = new ArrayList<>();
                ArrayList<String> listUUIDsToMe = new ArrayList<>();
                ArrayList<String> emailsCustomersToMe = new ArrayList<>();
                ArrayList<String> listNames = new ArrayList<>(); // names of tasks
                ArrayList<String> listUUIDs = new ArrayList<>(); // UUIDs of tasks
                ArrayList<String> customersToday = new ArrayList<>();
                ArrayList<String> performerToday = new ArrayList<>();

                PutDataMapRequest putDataMapReq = PutDataMapRequest.create(KEY);
                putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY, listNames);
                putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY2, listUUIDs);
                putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY4, listNamesToMe);
                putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY5, listUUIDsToMe);
                putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY6, emailsCustomersToMe);
                putDataMapReq.getDataMap().putString(COUNT_KEY7, LTSettings.getInstance().getUserName());
                putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY8, customersToday);
                putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY9, performerToday);

                PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                if (mGoogleApiClient != null) {
                    if (mGoogleApiClient.isConnected()) {
                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                    }
                }

            }
            return null;
        }
    }

    public static byte[] readFile(File file) throws IOException {

        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Tedorius", "onConnectionSuspended");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            subscribeTopics(token);

            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    public static void webSync()
    {
        try {
            if (client != null) {
                client.disconnect();
                client = null;
            }
            if (LTSettings.getInstance().getSessionUUID() == null) {
                mUidSession = null;
            } else {
                mUidSession = LTSettings.getInstance().getSessionUUID().toLowerCase();
            }
            writeLine("mUidSession="+mUidSession);
            String nameSpace = LTSettings.getInstance().getSyncNamespace();

            client = new Client(nameSpace+"websync.ashx?uid_session="+mUidSession);
//            RetryMode = ConnectRetryMode.Aggressive
            client.connect(new ConnectArgs()
            {{
                setOnSuccess(new SingleAction<ConnectSuccessArgs>()
                {
                    public void invoke(ConnectSuccessArgs e)
                    {
                        writeLine("Connect success!"+mUidSession);

                    }
                });
                setOnFailure(new SingleAction<ConnectFailureArgs>()
                {
                    public void invoke(ConnectFailureArgs e)
                    {
                        //writeLine("Connect failure.");
                        e.setRetry(true);
                        //writeLine(e.getException().getMessage());
                    }
                });
                setOnStreamFailure(new SingleAction<StreamFailureArgs>()
                {
                    public void invoke(StreamFailureArgs e)
                    {
                        //writeLine("Stream failure.");
                        //writeLine("Reconnecting...");
                        e.setRetry(true);
                        if (e.getException().getMessage().equals("604::Invalid client and/or session ID.")) {
                            Utils.startSync(mApp);
                        } else {
                            //writeLine(e.getException().getMessage());
                        }
                    }
                });
                setRetryMode(ConnectRetryMode.Aggressive);
            }});

            // subscribe to receive messages
            client.subscribe(new SubscribeArgs("/"+mUidSession)
            {{
                setOnSuccess(new SingleAction<SubscribeSuccessArgs>()
                {
                    public void invoke(SubscribeSuccessArgs e)
                    {
                        writeLine("Subscribe success!"+mUidSession);
                    }
                });
                setOnFailure(new SingleAction<SubscribeFailureArgs>()
                {
                    public void invoke(SubscribeFailureArgs e)
                    {
                        //writeLine("Subscribe failure.");
                        e.setRetry(true);
                        //writeLine(e.getException().getMessage());
                    }
                });
                setOnReceive(new SingleAction<SubscribeReceiveArgs>()
                {
                    public void invoke(SubscribeReceiveArgs e)
                    {
                        String message = e.getDataJson();
                        if (!mLastSeesionMassage.equals(message)) {
                            mLastSeesionMassage = message;
                            if (message.contains("SendGetChanges")) {
                                writeLine("Синхронизируйся! Uid= "+mUidSession+" || message= "+message);
                                Utils.startSync(mApp);
                            }
                        }
                    }
                });
            }});
        }
        catch (Exception ex)
        {
            //writeLine(ex.getMessage());
        }
    }

    private static void writeLine(final String text)
    {
        //if (IPCConstants.DEBUG) {
            //Log.v("Tedorius", text);
        //}
    }

    @Override
    public void networkAvailable() {

        Utils.startSync((LTApplication)getApplicationContext());
    }

    @Override
    public void networkUnavailable() {

    }

    public class CheckTimeForNotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_CHANGED:
                case Intent.ACTION_TIMEZONE_CHANGED:
                case Intent.ACTION_TIME_TICK:

                    Utils.updateTodayWidget(mApp);
                    if (LTSettings.getInstance().getUserProfile().isValid()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    boolean isTime = false;
                                    String yourTime = "9:00";
                                    //get your today date as string
                                    try {
                                        String today = (String) android.text.format.DateFormat.format("HH:mm", new java.util.Date());

                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        Date date1 = sdf.parse(yourTime);
                                        Date date2 = sdf.parse(today);
                                        isTime = date1.equals(date2);
                                    } catch (Exception e) {

                                    }

                                    if (isTime) {
                                        if (LTSettings.getInstance().isNotifyOverdue()) {
                                            try {
                                                if (checkOverdue()) {
                                                    final Intent myIntent = SlidingActivity.newInstance(getApplicationContext());
                                                    final PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 999998, myIntent, 0);

                                                    Notification.Builder builder;
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        builder = new Notification.Builder(getApplicationContext(), TaskNotifyHelper.CHANNEL_ID);
                                                    } else {
                                                        builder = new Notification.Builder(getApplicationContext());
                                                    }

                                                    builder.setTicker(getApplicationContext().getResources().getString(R.string.app_name));
                                                    builder.setSmallIcon(R.drawable.notification_icon);
                                                    builder.setWhen(System.currentTimeMillis());
                                                    builder.setContentIntent(pending);
                                                    TaskNotifyHelper.setSound(getApplicationContext(), builder);
                                                    builder.setContentText(getApplicationContext().getResources().getString(R.string.notify_overdue_sub));
                                                    builder.setContentTitle(getApplicationContext().getResources().getString(R.string.notify_overdue));
                                                    builder.setAutoCancel(true);
                                                    ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(999998, builder.build());
                                                }
                                            } catch (Exception e) {

                                            }
                                        }
                                        //
                                        /*if (LTSettings.getInstance().isNotifyToday()) {
                                            try {
                                                if (checkToday(mApp)) {
                                                    final Intent myIntent = SlidingActivity.newInstance(getApplicationContext());
                                                    final PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 999996, myIntent, 0);

                                                    Notification.Builder builder;
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        builder = new Notification.Builder(getApplicationContext(), TaskNotifyHelper.CHANNEL_ID);
                                                    } else {
                                                        builder = new Notification.Builder(getApplicationContext());
                                                    }

                                                    builder.setTicker(getApplicationContext().getResources().getString(R.string.app_name));
                                                    builder.setSmallIcon(R.drawable.notification_icon);
                                                    builder.setWhen(System.currentTimeMillis());
                                                    builder.setContentIntent(pending);
                                                    TaskNotifyHelper.setSound(getApplicationContext(), builder);
                                                    builder.setContentText(getApplicationContext().getResources().getString(R.string.notify_overdue_sub));
                                                    builder.setContentTitle(getApplicationContext().getResources().getString(R.string.notify_today));
                                                    builder.setAutoCancel(true);
                                                    ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(999996, builder.build());
                                                }

                                            } catch (Exception e) {

                                            }
                                        }*/
                                    }
                                } finally {

                                }
                            }
                        }).start();
                    }
                    break;
                default:
                    break;

            }
        }
    }

    private class openNotify extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }

    private boolean checkOverdue() {
        boolean hasOverdue = false;
        Cursor c = null;

        try {
            c = getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null,//
                    new TaskSelectionBuilder(new StringBuilder()).getOverdueLinkTasks(null).build(), null, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    hasOverdue = true;
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
            return hasOverdue;
        }
    }

    private boolean checkUnread() {
        boolean hasUnread = false;
        Cursor c = null;

        try {
            c = getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null,//
                    new TaskSelectionBuilder(new StringBuilder()).getUnreadTasks(null).build(), null, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    hasUnread = true;
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
            return hasUnread;
        }
    }

    public static boolean checkToday(Context context) {
        boolean hasToday = false;
        Cursor c = null;

        try {
            c = context.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null,//
                    new TaskSelectionBuilder(new StringBuilder()).getCalendarByDay(TimeHelper.currentTimeMillisWithoutTimeZone()).build(), null, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    hasToday = true;
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
            return hasToday;
        }
    }

    private static SimpleDateFormat getSimpleDateFormat(boolean is24h) {
        final SimpleDateFormat sdf = new SimpleDateFormat(is24h ? "HH:mm" : "hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf;
    }
}
