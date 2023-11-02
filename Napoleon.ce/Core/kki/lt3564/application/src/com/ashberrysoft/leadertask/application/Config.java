package com.ashberrysoft.leadertask.application;

import static android.os.Build.ID;

public interface Config {
    public static final boolean DEBUG = true;
    public static final String SOAP_NAMESPACE_DEFAULT = "https://sync.leadertask.net/";
    public static final String NETWORK_METHOD_URI = "https://sync.leadertask.net/LeaderTaskSyncService.asmx?op=";
    public static final String NETWORK_SEND_ERROR = "https://leadertask.net/User/senderror.aspx";
    public static final String LT_SYNC_SERVICE = "LeadertaskSyncService";
    public static final String LT_PUSH_TO_SERV = "https://sync.leadertask.net/Pushing/androidtoken.ashx";
    public static final String NETWROK_ADD_EMP = "https://sync.leadertask.net/Leadertask/Org/AddEmp2.ashx";
    public static final String NETWROK_DEL_EMP = "https://sync.leadertask.net/Leadertask/Org/DelEmp.ashx";
    public static final String NETWROK_BUY_LEADERTASK= "https://www.leadertask.net/Registrators/googleplay.ashx";
//    public static final String NETWROK_ACCEPT_INVITE= "https://www.leadertask.net/Leadertask/Org/Invite.ashx";
    public static final String NETWROK_ACCEPT_INVITE= "https://sync.leadertask.net/Leadertask/Org/Invite.ashx";
    public static final String IN_APP_ID = "sync_leadertask";
    public static final String IN_APP_ID_TEST = "sync_leadertask_test";
    public static final String IN_APP_ID_UUID = "8b5c3314-3b90-‎4093-8807-ad7ef5f491ed";
    public static final String IN_APP_ID_CHONO = "chrono_leadertask";
    public static final String IN_APP_ID_CHONO_UUID = "7b32674b-c991-42bd-b8e6-a0e7a4650c2c";
    //public static final String IN_APP_ID = "sync_leadertask_1_month";
    //public static final int IN_APP_DAYS = 30;
    public static final int IN_APP_DAYS = 365;
}
