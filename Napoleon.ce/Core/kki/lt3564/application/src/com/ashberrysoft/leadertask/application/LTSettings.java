package com.ashberrysoft.leadertask.application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.Email.OrderInstruct;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.enums.TaskStatusBehavior;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SetBlocking;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.modern.domains.menu.LatestMenu;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.providers.SyncProvider;
import com.ashberrysoft.leadertask.service.AuthService;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.application.BaseApplicationSettings;
import com.v2soft.AndLib.services.AndroidAccountHelper;

/**
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 */

public class LTSettings extends BaseApplicationSettings<LeaderTaskUser> {

    private static final String SYNC_ADDRESS = "SYNC_ADDRESS";
    private static final String SYNC_URI = "SYNC_URI";
    private static final String SYNC_SEND_ERROR = "SYNC_SEND_ERROR";
    private static final String SYNC_ADD_EMP = "SYNC_ADD_EMP";
    private static final String SYNC_DEL_EMP = "SYNC_DEL_EMP";
    private static final String SYNC_PORT = "SYNC_PORT";
    private static final String APP_FIRST_LAUNCH = "APP_FIRST_LAUNCH";
    public static final String APP_PASSWORD_TO_START = "APP_PASSWORD_TO_START";
    public static final String APP_PIN_TO_START = "APP_PIN_TO_START";
    public static final String APP_FINGER_TO_START = "APP_FINGER_TO_START";
    public static final String ONE_WEEK = "ONE_WEEK";
    public static final String OVERDUE_IN_TODAY = "OVERDUE_IN_TODAY";
    private static final String THEME = "theme";
    private static final String TASK_MODE = "TASK_MODE";
    private static final String TASK_EMAIL = "TASK_EMAIL";
    private static final String TASK_PROJECT_ID = "TASK_PROJECT_ID";
    private static final String TASK_PROJECT_NAME = "TASK_PROJECT_NAME";
    private static final String TASK_CATEGORY_ID = "TASK_CATEGORY_ID";
    private static final String TASK_CATEGORY_NAME = "TASK_CATEGORY_NAME";
    private static final String TASK_EMAIL_INSTRACT = "TASK_EMAIL_INSTRACT";
    private static final String SHOW_MAKE_TASKS = "show_make_tasks";
    private static final String SHOW_CHRONO = "show_chrono";
    private static final String SHOW_PANEL = "show_panel";
    private static final String CONTACTS_ENABLED = "contacts_enabled";
    private static final String REMINDER = "reminder";
    private static final String FILTER_SELECTED_DATE = "filter_selected_date";
    private static final String SHOW_UNREAD_TASKS = "SHOW_UNREAD_TASKS";
    private static final String SHOW_INBOX_TASKS = "SHOW_INBOX_TASKS";
    private static final String SHOW_CATEGORIES_IN_NAVIGATOR = "SHOW_CATEGORIES_IN_NAVIGATOR";
    private static final String SHOW_COLORS_IN_NAVIGATOR = "SHOW_COLORS_IN_NAVIGATOR";
    private static final String SHOW_WEEK_COUNT_IN_NAV = "SHOW_WEEK_COUNT_IN_NAV";
    private static final String SHOW_WEEK_COUNT_FIRST = "SHOW_WEEK_COUNT_FIRST";
    private static final String SHOW_EMPS_IN_NAVIGATOR = "SHOW_EMPS_IN_NAVIGATOR";
    private static final String SHOW_TASKS_COUNT_IN_NAVIGATOR = "SHOW_TASKS_COUNT_IN_NAVIGATOR";
    private static final String NOTIFY_VIBRATION = "notify_vibration";
    private static final String NOTIFY_SOUND = "notify_sound";
    private static final String PRE_TIME_NOTIFY = "pre_time_notify";
    private static final String NOTIFY_FOR_ME = "notify_for_me";
    private static final String NOTIFY_BY_MY_CANCELED = "notify_by_me_canceled";
    private static final String NOTIFY_COMMENTS = "notify_comments";
    private static final String NOTIFY_OVERDUE = "notify_overdue";
    private static final String NOTIFY_UNREAD = "notify_unread";
    private static final String NOTIFY_TODAY = "notify_today";

    private static final String KEY_SLIDING_INSTRUCTI_EXPANDED = "KEY_SLIDING_INSTRUCTI_EXPANDED";
    private static final String KEY_SLIDING_INSTRUCTMY_EXPANDED = "KEY_SLIDING_INSTRUCTMY_EXPANDED";
    private static final String KEY_SLIDING_PROJECT_EXPANDED = "KEY_SLIDING_PROJECT_EXPANDED";
    private static final String KEY_SLIDING_AVALAIBLEPROJECT_EXPANDED = "KEY_SLIDING_AVALAIBLEPROJECT_EXPANDED";
    private static final String KEY_SLIDING_CATEGORY_EXPANDED = "KEY_SLIDING_CATEGORY_EXPANDED";
    private static final String KEY_SLIDING_MENU_OPEN = "KEY_SLIDING_MENU_OPEN";
    private static final String NEED_TO_ADD_UNBOARDING_CAT_MAR = "NEED_TO_ADD_UNBOARDING_CAT_MAR";
    private static final String NEED_TO_SHOW_LOADING = "NEED_TO_SHOW_LOADING";
    private static final String AUTONOMY_MODE = "AUTONOMY_MODE";
    private static final String NEED_SHOW_INVITE = "NEED_SHOW_INVITE";
    private static final String PUT_SETTINGS = "PUT_SETTINGS";

    // NEW SETTINGS
    public static final String KEY_AUTOSYNC = "KEY_AUTOSYNC";
    public static final String KEY_CALENDAR_IN_NAVIGATOR = "KEY_CALENDAR_IN_NAVIGATOR";
    public static final String KEY_STRIKETHROUGH_TASKS = "KEY_STRIKETHROUGH_TASKS";
    public static final String KEY_LOGIN_AFTER_REGISTRATION = "KEY_LOGIN_AFTER_REGISTRATION";
    public static final String KEY_ADD_UNBOARDING_TASKS = "KEY_ADD_UNBOARDING_TASKS";


    public static final String KEY_LANGUAGE_LOCALE = "KEY_LANGUAGE_LOCALE";
    public static final String KEY_STATUS_BEHAVIOR = "KEY_STATUS_BEHAVIOR";
    public static final String KEY_MAXIMUM_ORDER = "KEY_MAXIMUM_ORDER";
    public static final String KEY_MAXIMUM_VERTICAL = "KEY_MAXIMUM_VERTICAL";

    public static final String KEY_LAST_MENU_ITEM = "KEY_LAST_MENU_ITEM";
    public static final String KEY_CREATE_SET_BLOCKING = "KEY_CREATE_SET_BLOCKING";
    public static final String ONBACKPRESSED_SAVE = "ONBACKPRESSED_SAVE";
    public static final String ADDING_TASKS_TO_TOP = "ADDING_TASKS_TO_TOP";
    public static final String TASKS_ORDER = "TASKS_ORDER";
    public static final String CONTACTS_ORDER = "CONTACTS_ORDER";
    public static final String KEY_VERIFY_END_DATE = "KEY_VERIFY_END_DATE";
    public static final String KEY_VERIFY_AVAILABLE_BYTES = "KEY_VERIFY_AVAILABLE_BYTES";
    public static final String KEY_VERIFY_BYTES = "KEY_VERIFY_BYTES";
    public static final String KEY_VERIFY_NAME_ORG = "KEY_VERIFY_NAME_ORG";
    public static final String KEY_VERIFY_USER_ID = "KEY_VERIFY_USER_ID";
    public static final String KEY_VERIFY_ADDINS = "KEY_VERIFY_ADDINS";
    public static final String KEY_VERIFY_EMAIL_DIRECTOR = "KEY_VERIFY_EMAIL_DIRECTOR";
    public static final String KEY_VERIFY_NAME_DIRECTOR = "KEY_VERIFY_NAME_DIRECTOR";
    public static final String KEY_VERIFY_KEY = "KEY_VERIFY_KEY";
    public static final String KEY_VERIFY_COUNT= "KEY_VERIFY_COUNT";
    public static final String KEY_EMPLOYEE_VERIFY_COUNT = "KEY_EMPLOYEE_VERIFY_COUNT";
    public static final String KEY_LAST_SYNC = "KEY_LAST_SYNC";
    public static final int LICENSE_TYPE_NONE = 0;
    public static final int LICENSE_TYPE_FREE = 1;
    public static final int LICENSE_TYPE_PREMIUM = 2;
    public static final int LICENSE_TYPE_BUSINESS = 3;
    public static final String CHECKED_ITEMS_IN_TASKS_LIST = "CHECKED_ITEMS_IN_TASKS_LIST";
    public static final String HEADER_AVAILABLE_PROJECTS_DROP = "HEADER_AVAILABLE_PROJECTS_DROP";
    public static final String HEADER_BY_ME_DROP = "HEADER_BY_ME_DROP";
    public static final String HEADER_CATEGORIES_DROP = "HEADER_CATEGORIES_DROP";
    public static final String HEADER_COLOR_DROP = "HEADER_COLOR_DROP";
    public static final String HEADER_EMP_DROP = "HEADER_EMP_DROP";
    public static final String HEADER_FOR_ME_DROP = "HEADER_FOR_ME_DROP";
    public static final String HEADER_PROJECTS_DROP = "HEADER_PROJECTS_DROP";
    public static final String UID_SESSION = "UID_SESSION";
    public static final String HAS_ANY_TASKS = "HAS_ANY_TASKS";
    public static final String SOUND = "SOUND";
    public static final String LAST_DAY = "LAST_DAY";
    public static final String NEED_DOWNLOAD_PROTO = "NEED_DOWNLOAD_PROTO";
    public static final String DOWNLOAD_LINK = "DOWNLOAD_LINK";
    public static final String MIN_HOUR = "MIN_HOUR";
    public static final String MAX_HOUR = "MAX_HOUR";
    public static final String SETTINGS_JSON = "SETTINGS_JSON";



    public static final String PREFS_NAME = "PREFS_NAME";
    private String mSyncUri;
    private String mSyncSendError;
    private String mSyncAddEmp;
    private String mSyncDelEmp;
    private int mSyncPort;

    // SINGLETON
    private static LTSettings sInstance;

    private List<Email> mInstructI;
    private List<Email> mInstructMe;
    private boolean mToRebootAfterChanges;

    private int mLastFeatureOrder;
    private long mFilterSelectedDate;
    private boolean mCalendarInNavigator;
    private boolean mMakeTaskHide;
    private boolean mShowChrono;
    private boolean mShowPanel;
    private boolean mContactsEnabled;
    private boolean mReminder;

    private Editor mEditor;
    private long mLastDay;

    private boolean mIsSlidingInstructIExpanded;
    private boolean mIsSlidingInstructMyExpanded;
    private boolean mIsSlidingProjectExpanded;
    private boolean mIsSlidingAvalaibleProjectExpanded;
    private boolean mIsSlidingCategoryExpanded;
    private boolean mIsSlidingMenuOpen;
    private AndroidAccountHelper mAccountHelper;

    private boolean mThemeDark;
    private boolean mUnread;
    private boolean mInbox;
    private boolean mCategoriesInNavigator;
    private boolean mColorsInNavigator;
    private boolean mWeekCountInCalendar;
    private boolean mWeekCountFromFirstJan;
    private boolean mEmpsInNavigator;
    private boolean mTaskCountInNavigator;
    private boolean mNotifyVibration;
    private boolean mNotifyStandartSound;
    private boolean mNotifyForMe;
    private boolean mNotifyByMeCanceled;
    private boolean mNotifyComments;
    private boolean mNotifyOverdue;
    private boolean mNotifyUnread;
    private boolean mNotifyToday;
    private int mNotifyPreTime;
    private boolean mStrikethruTask;
    private int mTaskMode;
    private boolean mLoginAfterRegistration;
    private boolean mNeedToAddUnboardingTasks;
    private Integer mLTCalendarWidthWidth;
    private boolean mRunSyncAfterVersionUpgrade;
    private Boolean mSmallScreen;
    private boolean mTaskFromNotify;
    private boolean mPasswordToStart;
    private boolean mPinToStart;
    private boolean mFingerToStart;
    private boolean mOneWeek;
    private boolean mOverdueInToday;

    private Locale mLanguageLocale;
    private boolean mLocaleWasChanged;

    private TaskStatusBehavior mStatusBehavior;
    private int mMaximumOrder;
    private int mMaximumVertical;

    private BaseMenuItem mMenuItem;
    private boolean mCreateSetBlocking;
    private boolean  mOnBackpressedSave;
    private boolean  mAddingTasksToTop;
    private boolean mIsMySync;
    private LTask TaskFromLink;
    private int mTasksOrder;
    private int mContactsOrder;
    private static Context mContext;
    private ArrayList <String> mCheckedTasks;
    private ArrayList <String> mBufferCopyTasks;
    private ArrayList <String> mBufferCutTasks;
    private ArrayList <String> mAllTimers;
    private ArrayList <String> mAllTimersNames;
    private ArrayList <String> mTasksToUpdate;
    private ArrayList <String> mTasksToDelete;
    private boolean mIsSlidingAvailableProjectsDrop;
    private boolean mIsSlidingByMeDrop;
    private boolean mIsSlidingForMeDrop;
    private boolean mIsSlidingCategoriesDrop;
    private boolean mIsSlidingColorDrop;
    private boolean mIsSlidingEmpsDrop;
    private boolean mIsSlidingProjectsDrop;
    private String mUUIDSession;
    private boolean mHasAnyTasks;
    private boolean mSoundEnabled;
    public static boolean iCanBuyLeadertask = false;
    public static boolean isNeedToRunLoadingScreen = false;
    public static boolean isNeedDownLoadEmpFotos = false;
    public static ArrayList <Employee> allInvitedUsers = new ArrayList<>();
    public static ArrayList <Employee> allInvitedUsersWas = new ArrayList<>();
    public static ArrayList <Employee> allInvitedAcceptedUsers = new ArrayList<>();
    public static boolean needToShowToastAfterAddTask = false;
    public static boolean needToShowToastAfterAddProject = false;
    public static boolean needToShowToastAfterAddUser = false;
    public static boolean needToShowToastAfterAssign = false;
    private boolean needToDownloadPhotoGoogleFacebook = false;
    private String downloadUriGoogleFacebook = "";
    private boolean isNeedPutSettings = false;
    private String mSyncNamespace;
    private boolean mNeedToAddUnboardingCatMar;
    private boolean mNeedToShowLoadingScreen;
    private boolean mNeedAutonomy;
    private boolean mNeedShowInvite;
    public static boolean needToShowAddMessage = false;
    private int mMinHour;
    private int mMaxHour;
    private String mSettingsJson;


    public static LTSettings getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LTSettings.class) {
                if (sInstance == null) {
                    mContext = context;
                    sInstance = new LTSettings(context);
                }
            }
        }

        return sInstance;
    }

    /** Только после инициализации */
    public static LTSettings getInstance() {
        return sInstance;
    }

    private LTSettings(Context context) {
        super(context.getApplicationContext());

        mEditor = mSettings.edit();
        setAccountHelper();
    }

    @Override
    protected void loadSettings() {
        mSyncNamespace = mSettings.getString(SYNC_ADDRESS, Config.SOAP_NAMESPACE_DEFAULT);
        mSyncUri = mSettings.getString(SYNC_URI, Config.NETWORK_METHOD_URI);
        mSyncSendError = mSettings.getString(SYNC_SEND_ERROR, Config.NETWORK_SEND_ERROR);
        mSyncAddEmp = mSettings.getString(SYNC_ADD_EMP, Config.NETWROK_ADD_EMP);
        mSyncDelEmp = mSettings.getString(SYNC_DEL_EMP, Config.NETWROK_DEL_EMP);
        mSyncPort = mSettings.getInt(SYNC_PORT, 0);
        TaskFromLink = null;
        mIsSlidingInstructIExpanded = mSettings.getBoolean(KEY_SLIDING_INSTRUCTI_EXPANDED, true);
        mIsSlidingInstructMyExpanded = mSettings.getBoolean(KEY_SLIDING_INSTRUCTMY_EXPANDED, true);
        mIsSlidingProjectExpanded = mSettings.getBoolean(KEY_SLIDING_PROJECT_EXPANDED, true);
        mIsSlidingAvalaibleProjectExpanded = mSettings.getBoolean(KEY_SLIDING_AVALAIBLEPROJECT_EXPANDED, true);
        mIsSlidingCategoryExpanded = mSettings.getBoolean(KEY_SLIDING_CATEGORY_EXPANDED, true);
        mIsSlidingMenuOpen = mSettings.getBoolean(KEY_SLIDING_MENU_OPEN, true);
        mPasswordToStart = mSettings.getBoolean(APP_PASSWORD_TO_START, false);
        mPinToStart = mSettings.getBoolean(APP_PIN_TO_START, false);
        mFingerToStart = mSettings.getBoolean(APP_FINGER_TO_START, false);
        mOneWeek = mSettings.getBoolean(ONE_WEEK, false);
        mOverdueInToday = mSettings.getBoolean(OVERDUE_IN_TODAY, true);
        //
        mIsSlidingAvailableProjectsDrop = mSettings.getBoolean(HEADER_AVAILABLE_PROJECTS_DROP, false);
        mIsSlidingByMeDrop = mSettings.getBoolean(HEADER_BY_ME_DROP, false);
        mIsSlidingForMeDrop = mSettings.getBoolean(HEADER_FOR_ME_DROP, false);
        mIsSlidingCategoriesDrop = mSettings.getBoolean(HEADER_CATEGORIES_DROP, false);
        mIsSlidingColorDrop = mSettings.getBoolean(HEADER_COLOR_DROP, false);
        mIsSlidingEmpsDrop = mSettings.getBoolean(HEADER_EMP_DROP, false);
        mIsSlidingProjectsDrop = mSettings.getBoolean(HEADER_PROJECTS_DROP, false);
        //
        mFilterSelectedDate = mSettings.getLong(FILTER_SELECTED_DATE, 0);
        mLastDay = mSettings.getLong(LAST_DAY, 0);
        mOnBackpressedSave = mSettings.getBoolean(ONBACKPRESSED_SAVE, true);
        mAddingTasksToTop = mSettings.getBoolean(ADDING_TASKS_TO_TOP, false);
        mNeedToAddUnboardingCatMar = mSettings.getBoolean(NEED_TO_ADD_UNBOARDING_CAT_MAR, true);
        mNeedToShowLoadingScreen = mSettings.getBoolean(NEED_TO_SHOW_LOADING, false);
        mNeedAutonomy = mSettings.getBoolean(AUTONOMY_MODE, false);
        mNeedShowInvite = mSettings.getBoolean(NEED_SHOW_INVITE, false);
        isNeedPutSettings = mSettings.getBoolean(PUT_SETTINGS, false);
        mCheckedTasks = new ArrayList<String>();
        mBufferCopyTasks = new ArrayList<String>();
        mBufferCutTasks = new ArrayList<String>();
        mAllTimers = new ArrayList<String>();
        mAllTimersNames = new ArrayList<String>();
        mTasksToUpdate = new ArrayList<String>();
        mTasksToDelete = new ArrayList<String>();
        mCalendarInNavigator = true;
        mMakeTaskHide = mSettings.getBoolean(SHOW_MAKE_TASKS, true);
        mShowChrono = mSettings.getBoolean(SHOW_CHRONO, true);
        mShowPanel = mSettings.getBoolean(SHOW_PANEL, true);
        mContactsEnabled = mSettings.getBoolean(CONTACTS_ENABLED, false);
        mReminder = mSettings.getBoolean(REMINDER, true);
        needToDownloadPhotoGoogleFacebook = mSettings.getBoolean(NEED_DOWNLOAD_PROTO, false);
        downloadUriGoogleFacebook = mSettings.getString(DOWNLOAD_LINK, "");
        mIsMySync = false;
        setUserProfile(new LeaderTaskUser(mSettings));

        mThemeDark = mSettings.getBoolean(THEME, false);
        mUnread = mSettings.getBoolean(SHOW_UNREAD_TASKS, true);
        mInbox = mSettings.getBoolean(SHOW_INBOX_TASKS, true);
        mCategoriesInNavigator = mSettings.getBoolean(SHOW_CATEGORIES_IN_NAVIGATOR, true);
        mColorsInNavigator = mSettings.getBoolean(SHOW_COLORS_IN_NAVIGATOR, true);
        mWeekCountInCalendar = mSettings.getBoolean(SHOW_WEEK_COUNT_IN_NAV, false);
        mWeekCountFromFirstJan = mSettings.getBoolean(SHOW_WEEK_COUNT_FIRST, false);
        mEmpsInNavigator = mSettings.getBoolean(SHOW_EMPS_IN_NAVIGATOR, true);
        mTaskCountInNavigator = mSettings.getBoolean(SHOW_TASKS_COUNT_IN_NAVIGATOR, true);
        mNotifyVibration = mSettings.getBoolean(NOTIFY_VIBRATION, true);
        mNotifyStandartSound = mSettings.getBoolean(NOTIFY_SOUND, false);
        mNotifyForMe = mSettings.getBoolean(NOTIFY_FOR_ME, true);
        mNotifyByMeCanceled = mSettings.getBoolean(NOTIFY_BY_MY_CANCELED, true);
        mNotifyComments = mSettings.getBoolean(NOTIFY_COMMENTS, true);
        mNotifyOverdue = mSettings.getBoolean(NOTIFY_OVERDUE, true);
        mNotifyUnread = mSettings.getBoolean(NOTIFY_UNREAD, true);
        mNotifyToday = mSettings.getBoolean(NOTIFY_TODAY, true);
        mNotifyPreTime = mSettings.getInt(PRE_TIME_NOTIFY, 0);
        mStrikethruTask = mSettings.getBoolean(KEY_STRIKETHROUGH_TASKS, true);
        mTaskMode = mSettings.getInt(TASK_MODE, TaskMode.INBOX);
        mTasksOrder = mSettings.getInt(TASKS_ORDER, 0);
        mContactsOrder = mSettings.getInt(CONTACTS_ORDER, 0);
        mMinHour = mSettings.getInt(MIN_HOUR, 8);
        mMaxHour = mSettings.getInt(MAX_HOUR, 20);
        mLoginAfterRegistration = mSettings.getBoolean(KEY_LOGIN_AFTER_REGISTRATION, false);
        mNeedToAddUnboardingTasks = mSettings.getBoolean(KEY_ADD_UNBOARDING_TASKS, false);

        if (mSettings.contains(KEY_LANGUAGE_LOCALE)) {
            mLanguageLocale = new Locale(mSettings.getString(KEY_LANGUAGE_LOCALE, Locale.getDefault().getLanguage()));
        }

        mStatusBehavior = TaskStatusBehavior.values()[mSettings.getInt(KEY_STATUS_BEHAVIOR,//
                TaskStatusBehavior.FINISH.ordinal())];
        mMaximumOrder = mSettings.getInt(KEY_MAXIMUM_ORDER, 0);
        mMaximumVertical = mSettings.getInt(KEY_MAXIMUM_VERTICAL, 0);
        if (mSettings.contains(KEY_LAST_MENU_ITEM)) {
            mMenuItem = new LatestMenu(mSettings.getString(KEY_LAST_MENU_ITEM, null));

        } else {
            mMenuItem = MenuItemType.TODAY;
        }
        mCreateSetBlocking = mSettings.getBoolean(KEY_CREATE_SET_BLOCKING, true);
        mUUIDSession = mSettings.getString(UID_SESSION, null);
        mSettingsJson = mSettings.getString(SETTINGS_JSON, "");
        mHasAnyTasks = mSettings.getBoolean(HAS_ANY_TASKS, false);
        mSoundEnabled = mSettings.getBoolean(SOUND, true);

        //new NotifySettings().start();
    }

    public String getUserName() {
        return mUserProfile.getName();
    }

    public void saveUser(String name, String password) {
        mUserProfile.setNamePassword(name, password);
        mUserProfile.save();
    }

    /** false - светлая, true - темная */
    public boolean isThemeDark() {
        return false;
    }

    public void setThemeDark(boolean dark) {
        mThemeDark = dark;

        mEditor.putBoolean(THEME, mThemeDark);
        mEditor.commit();
    }

    /** false - не показываем в навигаторе непрочитанные, true - показываем */
    public boolean isShowUnreadTasks() {
        //return mUnread;
        return true;
    }

    public void setShowUnreadTask(boolean show) {
        mUnread = show;

        mEditor.putBoolean(SHOW_UNREAD_TASKS, mUnread);
        mEditor.commit();
    }

    /** false - не показываем в навигаторе входящие если их нет, true - показываем */
    public boolean isShowInboxTasks() {
        //return mInbox;
        if (getUserName().equals("tedorius@yandex.ru") || getUserName().equals("anton.sobolev@leadertask.com")) {
            return true;
        } else {
            return false;
        }
    }

    public void setShowInboxTask(boolean show) {
        mInbox = show;

        mEditor.putBoolean(SHOW_INBOX_TASKS, mInbox);
        mEditor.commit();
    }

    public boolean showCategoriesInNavigator() {
        return mCategoriesInNavigator;
    }

    public void setShowCategoriesInNavigator(boolean show) {
        mCategoriesInNavigator = show;

        mEditor.putBoolean(SHOW_CATEGORIES_IN_NAVIGATOR, mCategoriesInNavigator);
        mEditor.commit();
    }

    public boolean showColorsInNavigator() {
        return mColorsInNavigator;
    }

    public void setShowColorsInNavigator(boolean show) {
        mColorsInNavigator = show;

        mEditor.putBoolean(SHOW_COLORS_IN_NAVIGATOR, mColorsInNavigator);
        mEditor.commit();
    }

    public boolean isShowWeekCountInCalendar() {
        return mWeekCountInCalendar;
    }

    public void setShowWeekCountInCalendar(boolean show) {
        mWeekCountInCalendar = show;

        mEditor.putBoolean(SHOW_WEEK_COUNT_IN_NAV, mWeekCountInCalendar);
        mEditor.commit();
    }

    public boolean isWeekCountFromFirstJan() {
        return mWeekCountFromFirstJan;
    }

    public void setWeekCountFromFirstJan(boolean show) {
        mWeekCountFromFirstJan = show;

        mEditor.putBoolean(SHOW_WEEK_COUNT_FIRST, mWeekCountFromFirstJan);
        mEditor.commit();
    }

    public boolean isEmpsInNavigator() {
        return mEmpsInNavigator;
    }

    public void setShowEmpsInNavigator(boolean show) {
        mEmpsInNavigator = show;

        mEditor.putBoolean(SHOW_EMPS_IN_NAVIGATOR, mEmpsInNavigator);
        mEditor.commit();
    }

    public boolean showTaskCountInNavigator() {
        return mTaskCountInNavigator;
        //return false;
    }

    public void setShowTaskCountInNavigator(boolean show) {
        mTaskCountInNavigator = show;

        mEditor.putBoolean(SHOW_TASKS_COUNT_IN_NAVIGATOR, mTaskCountInNavigator);
        mEditor.commit();
    }



    public int getNotifyPreTime() {
        return mNotifyPreTime;
    }

    public void setNotifyPreTime(int preTime) {
        mNotifyPreTime = preTime;

        mEditor.putInt(PRE_TIME_NOTIFY, mNotifyPreTime);
        mEditor.commit();
    }


    public boolean isNotifyVibration() {
        return mNotifyVibration;
    }

    public void setNotifyVibration(boolean vibration) {
        mNotifyVibration = vibration;

        mEditor.putBoolean(NOTIFY_VIBRATION, mNotifyVibration);
        mEditor.commit();
    }

    public boolean isNotifyStandartSound() {
        return mNotifyStandartSound;
    }

    public void setNotifyStandartSound(boolean sound) {
        mNotifyStandartSound = sound;

        mEditor.putBoolean(NOTIFY_SOUND, mNotifyStandartSound);
        mEditor.commit();
    }

    public boolean isNotifyForMe() {
        return mNotifyForMe;
    }

    public void setNotifyForMe(boolean notify) {
        mNotifyForMe = notify;

        mEditor.putBoolean(NOTIFY_FOR_ME, mNotifyForMe);
        mEditor.commit();
    }

    public boolean isNotifyByMeCanceled() {
        return mNotifyByMeCanceled;
    }

    public void setNotifyByMyCanceled(boolean notify) {
        mNotifyByMeCanceled = notify;

        mEditor.putBoolean(NOTIFY_BY_MY_CANCELED, mNotifyByMeCanceled);
        mEditor.commit();
    }

    public boolean isNotifyComments() {
        return mNotifyComments;
    }

    public void setNotifyComments(boolean notify) {
        mNotifyComments = notify;

        mEditor.putBoolean(NOTIFY_COMMENTS, mNotifyComments);
        mEditor.commit();
    }

    public boolean isNotifyOverdue() {
        return mNotifyOverdue;
    }

    public void setNotifyOverdue(boolean notify) {
        mNotifyOverdue = notify;

        mEditor.putBoolean(NOTIFY_OVERDUE, mNotifyOverdue);
        mEditor.commit();
    }

    public boolean isNotifyUnread() {
        return mNotifyUnread;
    }

    public void setNotifyUnread(boolean notify) {
        mNotifyUnread = notify;

        mEditor.putBoolean(NOTIFY_UNREAD, mNotifyUnread);
        mEditor.commit();
    }

    public boolean isNotifyToday() {
        return mNotifyToday;
    }

    public void setNotifyToday(boolean notify) {
        mNotifyToday = notify;

        mEditor.putBoolean(NOTIFY_TODAY, mNotifyToday);
        mEditor.commit();
    }

    /** false - выключены, true - вкл. */
    public boolean isReminder() {
        return mReminder;
    }

    public void setReminder(boolean data) {
        mReminder = data;

        mEditor.putBoolean(REMINDER, mReminder);
        mEditor.commit();
    }

    //
    public boolean needToDownloadPhotoGoogleFacebook() {
        return needToDownloadPhotoGoogleFacebook;
    }

    public void setNeedToDownloadPhotoGoogleFacebook(boolean data) {
        needToDownloadPhotoGoogleFacebook = data;

        mEditor.putBoolean(NEED_DOWNLOAD_PROTO, needToDownloadPhotoGoogleFacebook);
        mEditor.commit();
    }

    public String downloadUriGoogleFacebook () {
        return downloadUriGoogleFacebook;
    }

    public void setDownloadUriGoogleFacebook(String data) {
        downloadUriGoogleFacebook = data;

        mEditor.putString(DOWNLOAD_LINK, downloadUriGoogleFacebook);
        mEditor.commit();
    }
    //
    public ArrayList <String> getCheckedTasks() {
        return mCheckedTasks;
    }

    public ArrayList <String> getBufferCopyTasks() {
        return mBufferCopyTasks;
    }

      public ArrayList <String> getBufferCutTasks() {
        return mBufferCutTasks;
    }

    public ArrayList <String> getAllTimersNames() {
        return mAllTimersNames;
    }

    public ArrayList <String> getTasksToUpdate() {
        return mTasksToUpdate;
    }

    public ArrayList <String> getTasksToDelete() {
        return mTasksToDelete;
    }

    public void setSessionUUID(String uuid) {
        mUUIDSession = uuid;

        mEditor.putString(UID_SESSION, mUUIDSession);
        mEditor.commit();
    }

    public String getSessionUUID() {
        return mUUIDSession;
    }

    public void setSettingsJson(String settingsJson) {
        mSettingsJson = settingsJson;

        mEditor.putString(SETTINGS_JSON, mSettingsJson);
        mEditor.commit();
    }

    public String getSettingsJson() {
        return mSettingsJson;
    }

    public void setAlreadyHasAnyTasks() {
        mHasAnyTasks = true;

        mEditor.putBoolean(HAS_ANY_TASKS, mHasAnyTasks);
        mEditor.commit();
    }

    public void setHasNotTasks() {
        mHasAnyTasks = false;

        mEditor.putBoolean(HAS_ANY_TASKS, mHasAnyTasks);
        mEditor.commit();
    }

    public boolean isHasAnyTask() {
        return mHasAnyTasks;
    }


    public void setSoundEnabled(boolean enabled) {
        mSoundEnabled = enabled;

        mEditor.putBoolean(SOUND, mSoundEnabled);
        mEditor.commit();
    }

    public boolean isSoundEnabled() {
        return mSoundEnabled;
    }

    public ArrayList <String> getAllTimers() {
        return mAllTimers;
    }

    public void clearUserData(final Runnable runnable) {
        boolean accountNotNull = mAccountHelper.getPrimaryAccount() != null;
        saveUser(SharedStrings.EMPTY, SharedStrings.EMPTY);

        if (accountNotNull) {
            mAccountHelper.removePrimaryAccount(new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> manager) {
                    LTSettings.super.clearUserData();
                    runnable.run();
                }
            });

        } else {
            super.clearUserData();
            runnable.run();
        }
    }

    public void cleanDataBase() {
        DbHelper.getInstance(mContext).cleanDatabase();

        SyncInfo.initialization(mContext);
        SetBlocking.create(mContext);
    }

    public AndroidAccountHelper getAccountHelper() {
        return mAccountHelper;
    }

    public void setAccountHelper() {
        mAccountHelper = new AndroidAccountHelper(mContext, AuthService.ACCOUNT_TYPE, SyncProvider.PROVIDER_NAME);
    }

    public void setSyncPeriod(int pos) {
        if (getAccountHelper().getAccountByType(0) == null) {
            setAccountHelper();

            if (getAccountHelper().getPrimaryAccount() == null) {
                final Account account = new Account(getUserName(), AuthService.ACCOUNT_TYPE);
                try {
                    getAccountHelper().addPrimaryAccount(account, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (getAccountHelper().getPrimaryAccount() != null) {
            switch (pos) {
                case 0:
                    getAccountHelper().enableAutoSync(false);
                    getAccountHelper().enablePeriodicSync(0, false);
                    break;

                case 1:
                    getAccountHelper().enableAutoSync(true);
                    getAccountHelper().enablePeriodicSync(2 * 60, true);
                    break;

                case 2:
                    getAccountHelper().enableAutoSync(true);
                    getAccountHelper().enablePeriodicSync(15 * 60, true);
                    break;

                case 3:
                    getAccountHelper().enableAutoSync(true);
                    getAccountHelper().enablePeriodicSync(30 * 60, true);
                    break;

                case 4:
                    getAccountHelper().enableAutoSync(true);
                    getAccountHelper().enablePeriodicSync(60 * 60, true);
                default:
                    break;
            }
        }
    }

    /**
     * режим отображения задач: 0 - сегодня; 1 - входящие; 2 - поручено; 3 - проекты и доступные мне; 4 - категории; 5 -
     * Назначенные мне
     *
     * @return
     */
    public interface TaskMode {
        int TODAY = 0;
        int INBOX = 1;
        int ASSIGNED_BY_ME = 2;
        int PROJECTS = 3;
        int CATEGORIES = 4;
        int ASSIGNED_TO_ME = 5;
    }

    public int getTaskMode() {
        return mTaskMode;
    }

    public void setTaskMode(int mode) {
        mTaskMode = mode;

        mEditor.putInt(TASK_MODE, mTaskMode);
        mEditor.commit();
    }

    public int getTasksOrder() {
        return 0; // сортировка дефолтная
    }

    public void setTasksOrder(int order) {
        mTasksOrder = order;

        mEditor.putInt(TASKS_ORDER, mTasksOrder);
        mEditor.commit();
    }

    public int getContactsOrder() {
        return mContactsOrder;
    }

    public void setContactsOrder(int order) {
        mContactsOrder = order;

        mEditor.putInt(CONTACTS_ORDER, mContactsOrder);
        mEditor.commit();
    }

    public boolean isFirstLaunch() {
        return mSettings.getBoolean(APP_FIRST_LAUNCH, true);
    }

    public void setIsFirstLaunch(boolean isFirstLaunch) {
        mEditor.putBoolean(APP_FIRST_LAUNCH, isFirstLaunch);
        mEditor.commit();
    }

    /**
     * get selected date for filtering in milliseconds
     */
    public long getFilterSelectedDate() {
        return mFilterSelectedDate;
    }

    /**
     * get selected date for filtering in milliseconds
     */
    public void setFilterSelectedDate(long filterSelectedDate) {
        mFilterSelectedDate = filterSelectedDate;

        mEditor.putLong(FILTER_SELECTED_DATE, mFilterSelectedDate);
        mEditor.commit();
    }
    
    public long getLastDay() {
        return mLastDay;
    }

    public void setLastDay(long lastDay) {
        mLastDay = lastDay;

        mEditor.putLong(LAST_DAY, mLastDay);
        mEditor.commit();
    }

    public Email getChooseEmail() {
        return new Email(mSettings.getString(TASK_EMAIL, null), mSettings.getBoolean(TASK_EMAIL_INSTRACT, false));
    }

    public void setChooseEmail(Email data) {
        mEditor.putString(TASK_EMAIL, data.getName());
        mEditor.putBoolean(TASK_EMAIL_INSTRACT, (data.getOrderInstruct() == OrderInstruct.INSTRUCTI));
        mEditor.commit();
    }

    public Project getChooseProject() {
        final Project project = new Project();
        final String uid = mSettings.getString(TASK_PROJECT_ID, null);
        if (uid != null) {
            project.setId(UUID.fromString(uid));
        }
        project.setName(mSettings.getString(TASK_PROJECT_NAME, null));

        return project;
    }

    public void setChooseProject(Project data) {
        mEditor.putString(TASK_PROJECT_ID, data.getId().toString());
        mEditor.putString(TASK_PROJECT_NAME, data.getName());
        mEditor.commit();
    }

    public Category getChooseCategory() {
        final String uid = mSettings.getString(TASK_CATEGORY_ID, null);
        if (TextUtils.isEmpty(uid)) {
            return null;
        }

        final String name = mSettings.getString(TASK_CATEGORY_NAME, null);

        final Category category = new Category();
        category.setId(UUID.fromString(uid));
        category.setName(name);

        return category;
    }

    public void setChooseCategory(Category data) {
        mEditor.putString(TASK_CATEGORY_ID, data.getId().toString());
        mEditor.putString(TASK_CATEGORY_NAME, data.getName());
        mEditor.commit();
    }

    /**
     * if true скрываются задачи, у которых установлен статус (или у родительской задачи установлен статус): 1.
     * Завершено или отменено 2. если заказчик задачи не текущий пользователь и установлен статус готово к сдаче или
     * отклонено
     *
     */
    public boolean isMakeTaskHide() {
        return mMakeTaskHide;
    }

    public void setMakeTaskHide(boolean data) {
        mMakeTaskHide = data;

        mEditor.putBoolean(SHOW_MAKE_TASKS, mMakeTaskHide);
        mEditor.commit();
    }

    public boolean isShowChrono() {
        return mShowChrono;
    }

    public void setShowChrono(boolean data) {
        mShowChrono = data;

        mEditor.putBoolean(SHOW_CHRONO, mShowChrono);
        mEditor.commit();
    }

    public boolean isShowPanel() {
        return mShowPanel;
    }

    public void setShowPanel(boolean data) {
        mShowPanel = data;

        mEditor.putBoolean(SHOW_PANEL, mShowPanel);
        mEditor.commit();
    }

    public boolean isContactsEnabled() {
        return mContactsEnabled;
    }

    public void setContactsEnable(boolean data) {
        mContactsEnabled = data;

        mEditor.putBoolean(CONTACTS_ENABLED, mContactsEnabled);
        mEditor.commit();
    }

    // =================================================================
    // mIsSlidingInstructIExpanded
    // =================================================================

    public void setIsSlidingInstructIExpande(boolean expanded) {
        mIsSlidingInstructIExpanded = expanded;
        mEditor.putBoolean(KEY_SLIDING_INSTRUCTI_EXPANDED, expanded);
        mEditor.commit();
    }

    public boolean isSlidingInstructIExpande() {
        return mIsSlidingInstructIExpanded;
    }

    // =================================================================
    // mIsSlidingInstructMyExpanded
    // =================================================================

    public void setIsSlidingInstructMyExpande(boolean expanded) {
        mIsSlidingInstructMyExpanded = expanded;
        mEditor.putBoolean(KEY_SLIDING_INSTRUCTMY_EXPANDED, expanded);
        mEditor.commit();
    }

    public boolean isSlidingInstructMyExpande() {
        return mIsSlidingInstructMyExpanded;
    }

    // =================================================================
    // mIsSlidingProjectExpanded
    // =================================================================

    public void setIsSlidingProjectExpanded(boolean expanded) {
        mIsSlidingProjectExpanded = expanded;
        mEditor.putBoolean(KEY_SLIDING_PROJECT_EXPANDED, expanded);
        mEditor.commit();
    }

    public boolean isSlidingProjectExpanded() {
        return mIsSlidingProjectExpanded;
    }

    // =================================================================
    // mIsSlidingAvalaibleProjectExpanded
    // =================================================================

    public void setIsSlidingAvalaibleProjectExpanded(boolean expanded) {
        mIsSlidingAvalaibleProjectExpanded = expanded;
        mEditor.putBoolean(KEY_SLIDING_AVALAIBLEPROJECT_EXPANDED, expanded);
        mEditor.commit();
    }

    public boolean isSlidingAvalaibleProjectExpanded() {
        return mIsSlidingAvalaibleProjectExpanded;
    }

    // =================================================================
    // mIsSlidingCategoryExpanded
    // =================================================================

    public void setIsSlidingCategoryExpanded(boolean expanded) {
        mIsSlidingCategoryExpanded = expanded;
        mEditor.putBoolean(KEY_SLIDING_CATEGORY_EXPANDED, expanded);
        mEditor.commit();
    }

    public boolean isSlidingCategoryExpanded() {
        return mIsSlidingCategoryExpanded;
    }

    public List<Email> getInstructI() {
        return mInstructI;
    }

    public void setInstructI(List<Email> mInstructI) {
        this.mInstructI = mInstructI;
    }

    public List<Email> getInstructMe() {
        return mInstructMe;
    }

    public void setInstructMe(List<Email> mInstructMe) {
        this.mInstructMe = mInstructMe;
    }

    public boolean isSlidingMenuOpen() {
        return mIsSlidingMenuOpen;
    }

    public void setIsSlidingMenuOpen(boolean isOpen) {
        mIsSlidingMenuOpen = isOpen;

        mEditor.putBoolean(KEY_SLIDING_MENU_OPEN, isOpen);
        mEditor.commit();
    }

    public int getAutosyncModeInt() {
        return 0;
    }

    public boolean isCalendarInNavigator() {
        return mCalendarInNavigator;
    }

    public void setCalendarInNavigator(boolean calendarInNavigator) {
        mCalendarInNavigator = calendarInNavigator;

        mEditor.putBoolean(KEY_CALENDAR_IN_NAVIGATOR, mCalendarInNavigator);
        mEditor.commit();
    }

    public boolean isNeedPasswordToStart() {
        return mPasswordToStart;
    }

    public void setNeedPasswordToStart(boolean need) {
        mPasswordToStart = need;

        mEditor.putBoolean(APP_PASSWORD_TO_START, mPasswordToStart);
        mEditor.commit();
    }

    public boolean isNeedPinToStart() { return mPinToStart;}

    public void setNeedPinToStart(boolean need){
        mPinToStart = need;
        mEditor.putBoolean(APP_PIN_TO_START, mPinToStart);
        mEditor.commit();
    }

    public boolean isNeedFingerToStart() { return mFingerToStart;}

    public void setNeedFingerToStart(boolean need){
        mFingerToStart = need;
        mEditor.putBoolean(APP_FINGER_TO_START, mFingerToStart);
        mEditor.commit();
    }

    public boolean isOneWeekInNav() {
        return mOneWeek;
    }

    public void setOneWeekInNav(boolean need) {
        mOneWeek = need;

        mEditor.putBoolean(ONE_WEEK, mOneWeek);
        mEditor.commit();
    }

    public boolean isOverdueInToday() {
        return mOverdueInToday;
    }

    public void setOverdueInToday(boolean overdueInToday) {
        mOverdueInToday = overdueInToday;

        mEditor.putBoolean(OVERDUE_IN_TODAY, mOverdueInToday);
        mEditor.commit();
    }

    public boolean isDroppedHeader(BaseMenuItem menuItem) {
        switch (menuItem.getMenuItemType()) {
            case HEADER_AVAILABLE_PROJECTS:
                return mIsSlidingAvailableProjectsDrop;

            case HEADER_BY_ME:
                return mIsSlidingByMeDrop;

            case HEADER_CATEGORIES:
                return mIsSlidingCategoriesDrop;

            case HEADER_COLORS:
                return mIsSlidingColorDrop;

            case HEADER_EMPS:
                return mIsSlidingEmpsDrop;

            case HEADER_FOR_ME:
                return mIsSlidingForMeDrop;

            case HEADER_PROJECTS:
                return mIsSlidingProjectsDrop;

            default:
                return  false;
        }
    }

    public void setDropMenuHeaders(BaseMenuItem menuItem, boolean drop) {
        switch (menuItem.getMenuItemType()) {
            case HEADER_AVAILABLE_PROJECTS:
                mEditor.putBoolean(HEADER_AVAILABLE_PROJECTS_DROP, drop);
                mIsSlidingAvailableProjectsDrop = drop;
                break;

            case HEADER_BY_ME:
                mEditor.putBoolean(HEADER_BY_ME_DROP, drop);
                mIsSlidingByMeDrop = drop;
                break;

            case HEADER_CATEGORIES:
                mEditor.putBoolean(HEADER_CATEGORIES_DROP, drop);
                mIsSlidingCategoriesDrop = drop;
                break;

            case HEADER_COLORS:
                mEditor.putBoolean(HEADER_COLOR_DROP, drop);
                mIsSlidingColorDrop = drop;
                break;

            case HEADER_EMPS:
                mEditor.putBoolean(HEADER_EMP_DROP, drop);
                mIsSlidingEmpsDrop = drop;
                break;

            case HEADER_FOR_ME:
                mEditor.putBoolean(HEADER_FOR_ME_DROP, drop);
                mIsSlidingForMeDrop = drop;
                break;

            case HEADER_PROJECTS:
                mEditor.putBoolean(HEADER_PROJECTS_DROP, drop);
                mIsSlidingProjectsDrop = drop;
                break;

            default:
                break;
        }
        mEditor.commit();
    }

    public boolean isToRebootAfterChanges() {
        return mToRebootAfterChanges;
    }

    public void setToRebootAfterChanges(boolean toRebootAfterChanges) {
        mToRebootAfterChanges = toRebootAfterChanges;
    }

    public int getLastFeatureOrder() {
        return mLastFeatureOrder;
    }

    public void setLastFeatureOrder(int lastFeatureOrder) {
        mLastFeatureOrder = lastFeatureOrder;
    }

    public boolean isStrikethruTask() {
        return mStrikethruTask;
    }

    public void setStrikethruTask(boolean strikethruTask) {
        mStrikethruTask = strikethruTask;

        mEditor.putBoolean(KEY_STRIKETHROUGH_TASKS, mStrikethruTask);
        mEditor.commit();
    }

    public boolean isLoginAfterRegistration() {
        return mLoginAfterRegistration;
    }

    public void setLoginAfterRegistration(boolean loginAfterRegistration) {
        mLoginAfterRegistration = loginAfterRegistration;

        mEditor.putBoolean(KEY_LOGIN_AFTER_REGISTRATION, mLoginAfterRegistration);
        mEditor.commit();
    }

    public boolean isNeedToAddUnboardingTasks() {
        return mNeedToAddUnboardingTasks;
    }

    public void setNeedToAddUnboardingTasks(boolean needToAddUnboardingTasks) {
        mNeedToAddUnboardingTasks = needToAddUnboardingTasks;

        mEditor.putBoolean(KEY_ADD_UNBOARDING_TASKS, mNeedToAddUnboardingTasks);
        mEditor.commit();
    }

    public Integer getLTCalendarWidth() {
        return mLTCalendarWidthWidth;
    }

    public void setLTCalendarWidth(Integer slidingMenuWidth) {
        mLTCalendarWidthWidth = slidingMenuWidth;
    }

    public boolean isRunSyncAfterVersionUpgrade() {
        return mRunSyncAfterVersionUpgrade;
    }

    public void setRunSyncAfterVersionUpgrade(boolean runSyncAfterVersionUpgrade) {
        mRunSyncAfterVersionUpgrade = runSyncAfterVersionUpgrade;
    }

    public Boolean getSmallScreen() {
        return mSmallScreen;
    }

    public Boolean setSmallScreen(Boolean smallScreen) {
        mSmallScreen = smallScreen;
        return getSmallScreen();
    }

    public boolean isTaskFromNotify() {
        return mTaskFromNotify;
    }

    public void setTaskFromNotify(boolean taskFromNotify) {
        mTaskFromNotify = taskFromNotify;
    }

    public Locale getLanguageLocale() {
        return mLanguageLocale;
    }

    public void setLanguageLocale(Locale languageLocale) {
        mLanguageLocale = languageLocale;
        setLocaleWasChanged(true);

        if (mLanguageLocale == null) {
            mEditor.remove(KEY_LANGUAGE_LOCALE);

        } else {
            mEditor.putString(KEY_LANGUAGE_LOCALE, mLanguageLocale.getLanguage());
        }
        mEditor.commit();
    }

    public boolean isLocaleWasChanged() {
        return mLocaleWasChanged;
    }

    public void setLocaleWasChanged(boolean localeWasChanged) {
        mLocaleWasChanged = localeWasChanged;
    }

    public TaskStatusBehavior getStatusBehavior() {
        //return mStatusBehavior;
        return TaskStatusBehavior.SELECT;
    }

    public void setStatusBehavior(TaskStatusBehavior statusBehavior) {
        mStatusBehavior = statusBehavior;

        if (mStatusBehavior == null) {
            setStatusBehavior(TaskStatusBehavior.SELECT);
            return;
        }

        mEditor.putInt(KEY_STATUS_BEHAVIOR, mStatusBehavior.ordinal());
        mEditor.commit();
    }

    public int getMaximumOrder() {
        return mMaximumOrder;
    }

    public void setMaximumOrder(int maximumOrder) {
        mMaximumOrder = maximumOrder;

        mEditor.putInt(KEY_MAXIMUM_ORDER, mMaximumOrder);
        mEditor.commit();
    }

    public int getMaximumVertical() {
        return mMaximumVertical;
    }

    public void setMaximumVertical(int maximumVertical) {
        mMaximumVertical = maximumVertical;

        mEditor.putInt(KEY_MAXIMUM_VERTICAL, mMaximumVertical);
        mEditor.commit();
    }

    public int getMinHour() {
        return mMinHour;
    }

    public void setMinHour(int value) {
        mMinHour = value;

        mEditor.putInt(MIN_HOUR, mMinHour);
        mEditor.commit();
    }

    public int getMaxHour() {
        return mMaxHour;
    }

    public void setMaxHour(int value) {
        mMaxHour = value;

        mEditor.putInt(MAX_HOUR, mMaxHour);
        mEditor.commit();
    }

    public BaseMenuItem getMenuItem() {
        return mMenuItem;
    }

    public String getSyncNamespaceToEdit() {
        if (mSyncNamespace.equals(Config.SOAP_NAMESPACE_DEFAULT) && getSyncPort() == 0) {
            return "";
        }
        if (getSyncPort() != 0) {
            return mSyncNamespace.substring(0,mSyncNamespace.length()-1)+SharedStrings.COLON_C+getSyncPort()+SharedStrings.SPLIT;
        } else {
            return mSyncNamespace;
        }
    }

    public String getSyncNamespace() {
        if (IPCConstants.BOX && getSyncPort() != 0 ) {
            return mSyncNamespace.substring(0,mSyncNamespace.length()-1)+SharedStrings.COLON_C+getSyncPort()+SharedStrings.SPLIT;
        }
        return mSyncNamespace;
    }

    public String getSyncUri() {
        return mSyncUri;
    }

    public String getSyncSendError() {
        return mSyncSendError;
    }

    public String getSyncAddEmp() {
        return mSyncAddEmp;
    }

    public String getSyncDelEmp() {
        return mSyncDelEmp;
    }

    public int getSyncPort() {
        return mSyncPort;
    }

    public void setSyncNamespace(String address) {
        int syncPort = 0;
        if (address.equals("") || address == null) {
            address = Config.SOAP_NAMESPACE_DEFAULT;
        } else {
            //
            int indexOfPort = address.lastIndexOf(":");
            if (indexOfPort != -1 && indexOfPort > 7 && indexOfPort + 1 < address.length()) {
                try {
                    if (address.substring(address.length() - 1, address.length()).equals(String.valueOf(SharedStrings.SPLIT))) {
                        syncPort = Integer.parseInt(address.substring(indexOfPort + 1, address.length() - 1));

                    } else {
                        syncPort = Integer.parseInt(address.substring(indexOfPort + 1, address.length()));
                    }
                } catch (Exception e){
                }
                address = address.substring(0, indexOfPort);
            }
            //
            if (address.length() > 0 && !address.substring(address.length()-1, address.length()).equals(String.valueOf(SharedStrings.SPLIT))) {
                address = address+SharedStrings.SPLIT;
            }
        }
        mSyncNamespace = address;

        mSyncPort = syncPort;
        mEditor.putInt(SYNC_PORT, mSyncPort);
        mEditor.commit();

        mSyncUri = mSyncNamespace+"LeaderTaskSyncService.asmx?op=";
        mEditor.putString(SYNC_URI, mSyncUri);
        mEditor.commit();

        mSyncSendError = mSyncNamespace+"User/senderror.aspx";
        mEditor.putString(SYNC_SEND_ERROR, mSyncSendError);
        mEditor.commit();

        mSyncAddEmp = mSyncNamespace+"Leadertask/Org/AddEmp2.ashx";
        mEditor.putString(SYNC_ADD_EMP, mSyncAddEmp);
        mEditor.commit();

        mSyncDelEmp = mSyncNamespace+"Leadertask/Org/DelEmp.ashx";
        mEditor.putString(SYNC_DEL_EMP, mSyncDelEmp);
        mEditor.commit();

        mEditor.putString(SYNC_ADDRESS, address);
        mEditor.commit();
    }

    public void setLastSynchronization(Date date)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("KEY_LAST_SYNC", date.getTime());
        editor.commit();
    }

    public boolean wasSync() {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        long tmp = settings.getLong(KEY_LAST_SYNC, -1);
        return  tmp != -1 ? true :false;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void setVerifyEmployeesCount(int employeesCount)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_EMPLOYEE_VERIFY_COUNT, employeesCount);
        editor.commit();
    }

    public int getVerifyEmployeesCount() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        int tmp = settings.getInt(LTSettings.KEY_EMPLOYEE_VERIFY_COUNT, -1);
        if(tmp != -1) {
            return tmp;
        }
        else {
            return -1;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void setVerifyEndDate(String endDate)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_END_DATE, endDate);
        editor.commit();
    }

    public void setVerifyUserId(String userId)
    {
        if (!userId.equals(getVerifyUserId())) {
            Utils.fixUserUIDForAnalytics(mContext, userId);
        }
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_USER_ID, userId);
        editor.commit();
    }

    public String getVerifyUserId() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_USER_ID, null);
        return tmp;
    }

    public void setAddins(String addins)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_ADDINS, addins);
        editor.commit();
    }

    public String getVerifyAddins() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_ADDINS, null);
        return tmp;
    }



    public void setVerifyAvailableBytes(String AvailableBytes)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_AVAILABLE_BYTES, AvailableBytes);
        editor.commit();
    }

    public String getVerifyAvailableBytes() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_AVAILABLE_BYTES, null);
        return tmp;
    }

    public void setVerifyBytes(String Bytes)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_BYTES, Bytes);
        editor.commit();
    }

    public String getVerifyBytes() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_BYTES, null);
        return tmp;
    }

    public void setVerifyOrgName(String OrgName)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_NAME_ORG, OrgName);
        editor.commit();
    }

    public String getVerifyOrgName() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_NAME_ORG, null);
        return tmp;
    }

    public void setVerifyEmailDirector(String EmailDirector)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_EMAIL_DIRECTOR, EmailDirector);
        editor.commit();
    }

    public void setVerifyNameDirector(String nameDirector)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_NAME_DIRECTOR, nameDirector);
        editor.commit();
    }

    public String getVerifyNameDirector() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_NAME_DIRECTOR, null);
        return tmp;
    }

    public String getVerifyEmailDirector() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_EMAIL_DIRECTOR, null);
        return tmp;
    }

    public void setVerifyCount(String key)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_COUNT, key);
        editor.commit();
    }

    public String getVerifyCount() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_COUNT, null);
        return tmp;
    }

    public void setVerifyKey(String key)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_VERIFY_KEY, key);
        editor.commit();
    }

    public String getVerifyKey() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_KEY, null);
        return tmp;
    }

    public String getVerifyUserIdForUri() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_USER_ID, null);
        return "&luid="+tmp;
    }

    public long getVerifyEndDateInLong() {
        SharedPreferences settings = mContext.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        String tmp = settings.getString(LTSettings.KEY_VERIFY_END_DATE, null);
        if(tmp != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            formatter.setLenient(false);
            try {
                return formatter.parse(tmp).getTime();
            } catch (ParseException e) {
                return -1;
            }
        }
        else {
            return -1;
        }
    }

    public int getLicenseType() {
        int verifyEmployees = getVerifyEmployeesCount();
        long verifyEndDate = getVerifyEndDateInLong();

        if (verifyEndDate == -1 || verifyEmployees == -1) {
            return LICENSE_TYPE_NONE;
        } else {
            if (verifyEndDate < TimeHelper.currentTimeMillisWithoutTimeZone()) {
                return LICENSE_TYPE_FREE;
            } else {
                switch (verifyEmployees) {
                    case 0:
                        return LICENSE_TYPE_FREE;
                    case 1:
                        return LICENSE_TYPE_PREMIUM;
                    default:
                        return LICENSE_TYPE_BUSINESS;
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void setMenuItem(BaseMenuItem menuItem) {
        mMenuItem = menuItem;

        mEditor.putString(KEY_LAST_MENU_ITEM, LatestMenu.baseMenuItemToString(mMenuItem));
        mEditor.commit();
    }

    public boolean isCreateSetBlocking() {
        return mCreateSetBlocking;
    }

    public void setCreateSetBlocking(boolean createSetBlocking) {
        mCreateSetBlocking = createSetBlocking;

        mEditor.putBoolean(KEY_CREATE_SET_BLOCKING, mCreateSetBlocking);
        mEditor.commit();
    }

    public  boolean ismOnBackpressedSave()
    {
        return false;
    } // mOnBackpressedSave

    public void setmOnBackpressedSave(boolean change)
    {
        mOnBackpressedSave = change;
        mEditor.putBoolean(ONBACKPRESSED_SAVE, mOnBackpressedSave);
        mEditor.commit();
    }

    public  boolean isAddingTasksToTop()
    {
        return mAddingTasksToTop;
    }

    public void setAddingTasksToTop(boolean change)
    {
        mAddingTasksToTop = change;
        mEditor.putBoolean(ADDING_TASKS_TO_TOP, mAddingTasksToTop);
        mEditor.commit();
    }

    public  boolean isNeedToAddUnboardingCatMar()
    {
        return mNeedToAddUnboardingCatMar;
    }

    public void setIsNeedToAddUnboardingCatMar(boolean change)
    {
        mNeedToAddUnboardingCatMar = change;
        mEditor.putBoolean(NEED_TO_ADD_UNBOARDING_CAT_MAR, mNeedToAddUnboardingCatMar);
        mEditor.commit();
    }

    public  boolean isNeedToShowLoadingScreen()
    {
        return mNeedToShowLoadingScreen;
    }

    public void setIsNeedToShowLoadingScreen(boolean change)
    {
        mNeedToShowLoadingScreen = change;
        mEditor.putBoolean(NEED_TO_SHOW_LOADING, mNeedToShowLoadingScreen);
        mEditor.commit();
    }

    public  boolean isAutonomyMode(){
        return mNeedAutonomy;
    }

    public void setAutonomyMode(boolean change)
    {
        mNeedAutonomy = change;
        mEditor.putBoolean(AUTONOMY_MODE, mNeedAutonomy);
        mEditor.commit();
    }

    public  boolean isNeedShowInvite(){
        return mNeedShowInvite;
    }

    public void setNeedShowInvite(boolean change)
    {
        mNeedShowInvite = change;
        mEditor.putBoolean(NEED_SHOW_INVITE, mNeedShowInvite);
        mEditor.commit();
    }

    public  boolean isNeedToPutSettings(){
        return isNeedPutSettings;
    }

    public void setNeedToPutSettings(boolean change)
    {
        isNeedPutSettings = change;
        mEditor.putBoolean(PUT_SETTINGS, isNeedPutSettings);
        mEditor.commit();
    }

    public void setIsMySync(boolean IsMySync) {
        mIsMySync = IsMySync;
    }

    public boolean IsMySync() {
        return mIsMySync;
    }

    public void setLinkTask(LTask task)
    {
        TaskFromLink = task;
    }

    public LTask getLinkTask()
    {
        return TaskFromLink;
    }
}