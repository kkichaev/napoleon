package com.ashberrysoft.leadertask.modern.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.accounts.Account;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.LoginActivity;
import com.ashberrysoft.leadertask.application.Config;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.data_providers.network.SynchronizationTask;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;
import com.ashberrysoft.leadertask.instance_sync.MyInstanceIDListenerService;
import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;
import com.ashberrysoft.leadertask.modern.fragment.NavigationDrawerFragment;
import com.ashberrysoft.leadertask.modern.fragment.TasksFragment;
import com.ashberrysoft.leadertask.modern.helper.FullTasksResetHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.providers.SyncProvider;
import com.ashberrysoft.leadertask.service.AuthService;
import com.ashberrysoft.leadertask.utils.AppRater;
import com.ashberrysoft.leadertask.utils.LTPowerManager;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static com.ashberrysoft.leadertask.R.id.status;
import static com.ashberrysoft.leadertask.R.string.task;
import static com.ashberrysoft.leadertask.application.Config.IN_APP_DAYS;
import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID_UUID;
import static com.ashberrysoft.leadertask.application.Config.NETWROK_BUY_LEADERTASK;
import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.sendNotif;
import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.webSync;
import static com.ashberrysoft.leadertask.utils.Utils.isMyServiceRunning;

public class SlidingActivity extends BaseActivity implements NavigationDrawerFragment
        .NavigationDrawerCallbacks, TextView.OnEditorActionListener{

    public static final int MENU_CONTAINER = R.id.behind_menu;
    public static final int FRAGMENT_CONTAINER = R.id.main_fragment;

    private Toolbar mToolbar;
    //private Toolbar mToolbarChooser;
    private LinearLayout mSimleToolbarContainer;
    private RelativeLayout mMultiToolbarContainer;
    private LinearLayout mEditToolbarContainer;
    private TextView mToolbarCooserChechCount;
    private ImageView mToolbarCooserDelete;
    private TextView mToolbarCustomTitle;
    private TextView mToolbarCustomSubtitle;
    public static SlidingMenu mSlidingMenu;
    public static DrawerLayout mTaskDrawerLayout;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static FrameLayout mNavigation_drawer_task;
    public static boolean isSetToolbarTitle;
    public static boolean isFirstOpen;

    public static final String ACTION_OPEN_NOTIFY_TASK = "ACTION_OPEN_NOTIFY_TASK";
    public static final String ACTION_ACTION_SEND_TEXT = "ACTION_ACTION_SEND_TEXT";
    public static final String ACTION_ACTION_LOGIN = "ACTION_ACTION_LOGIN";
    private static final String ACTION_SHOW_ERROR = "ACTION_SHOW_ERROR";
    public static final String ACTION_CHANGE_DATE = "ACTION_CHANGE_DATE";

    public static final String EXTRA_TASK = "EXTRA_TASK";
    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    public static final String EXTRA_CODE = "EXTRA_CODE";
    private EditText mEtTitleNameMenu;
    private TextView mTvTitleNameMenu;
    private Project mCheckedProject;
    public static Project mDelProject;
    private TimeHelper mTimeHelper;

    public static String mAmount = "";
    public static String mCurrency = "";
    public static String mPackageName = "";
    public static String mProductId = "";
    public static String mPurchaseToken = "";
    public static String mSignature = "";
    public static String mPurchaseData = "";


    public static Intent newInstance(Context context) {
        final Intent intent = new Intent(context, SlidingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return intent;
    }

    public static Intent newInstanceActionLogin(Context context) {
        final Intent intent = newInstance(context);
        intent.setAction(ACTION_ACTION_LOGIN);

        return intent;
    }

    public static Intent newInstance(Context context, LTask task) {
        final Intent intent = newInstance(context);
        intent.setAction(ACTION_OPEN_NOTIFY_TASK);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.activity_drawer_main);

        isFirstOpen = true;
        mTimeHelper = TimeHelper.getInstance();

        new Utils.checkIsDateChanged(SlidingActivity.this).execute();

        if (!isLandOrientation()) {
            mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
            if (mSlidingMenu != null) {
                //mSlidingMenu.setBehindOffset(getDisplayWidth() - getSettings().getLTCalendarWidth());

                mSlidingMenu.setMode(SlidingMenu.LEFT);
                mSlidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
//                mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
                mSlidingMenu.setShadowDrawable(R.drawable.shadow);
                mSlidingMenu.setFadeDegree(0.35f);
            }
        }

        mTaskDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_task);
        mTaskDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mNavigation_drawer_task = (FrameLayout) findViewById(R.id.navigation_drawer_task);
        mNavigation_drawer_task.setBackgroundColor(Color.argb((int) (0.75 * 255.0f), 189, 85, 0));
        // Set a toolbar which will replace the action bar.
        setActionBar();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (SynchronizationTask.isSwipeSync) {
                    mSwipeRefreshLayout.setRefreshing(getApp().isSync());
                }
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SynchronizationTask.isSwipeSync = true;
                synchronize();
            }
        });
        mSwipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (!isSetToolbarTitle) {
                            isSetToolbarTitle = !isSetToolbarTitle;
                            getSupportActionBar().setDisplayShowTitleEnabled(false);

                            mEditToolbarContainer.setVisibility(View.GONE);
                            mSimleToolbarContainer.setVisibility(View.VISIBLE);
                            Utils.hideInput(mEtTitleNameMenu);
                            mEtTitleNameMenu.clearFocus();
                            mToolbarCustomTitle.setText(getResources().getString(R.string.synchronization_updated));
                            mToolbarCustomSubtitle.setText(getLastSynchronization(getApp(), Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE), false));
                            getSupportActionBar().setDisplayUseLogoEnabled(false);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isSetToolbarTitle = !isSetToolbarTitle;

                        getSupportActionBar().setDisplayShowTitleEnabled(true);

                        mEditToolbarContainer.setVisibility(View.VISIBLE);
                        mSimleToolbarContainer.setVisibility(View.GONE);

                        if (!TasksFragment.hasParent) {
                            //нет родителя, можно скролится, кнопка сендвич
                            if(Utils.isLandOrientation(getApp())) {
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                getSupportActionBar().setHomeButtonEnabled(false);
                            }
                            else {
                                getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                getSupportActionBar().setHomeButtonEnabled(true);
                            }
                        }
                        else {
                            //есть родитель, отключаем навигатор, меняем кнопку в екшнбаше на "назад"
                            if(!Utils.isLandOrientation(getApp())) {
                                SlidingActivity.mSlidingMenu.showContent();
                                SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE); // нет свайпа
                            }
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setHomeButtonEnabled(true);
                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24);
                        }
                        break;
                }
                return false;
            }
        });

        if (getSupportFragmentManager().findFragmentByTag(MenuFragment.CLASS_PATH) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(MENU_CONTAINER, MenuFragment.newInstance(), MenuFragment.CLASS_PATH);
            ft.commit();
        }

        if (b == null) {
            startFragment(TasksFragment.newInstance(getSettings().getMenuItem(), null), false);
        }

        getSupportLoaderManager().restartLoader(R.id.lm_sync_info, null, this);

        onIntentAction(getIntent());

        if (getSettings().isNeedShowInvite()) {
            Utils.startSync(getApp());
        }

        /*IntentFilter s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);

        this.registerReceiver(new CheckTimeReceiver(), s_intentFilter);*/

        LTApplication mApp = (LTApplication) getApplicationContext();
        MyInstanceIDListenerService.regToken(mApp);
        webSync();
    }

    /*public class CheckTimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    //TasksFragment.mAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    }*/

    public void addCheckedTasks(boolean add, LTask task) {
        if (add) {
            getSettings().getCheckedTasks().add(task.getUid());
        } else {
            getSettings().getCheckedTasks().remove(task.getUid());
        }
    }

    public void clearCheckedTasks() {
        getSettings().getCheckedTasks().clear();
        return ;
    }

    public void setActionBarTitle(String name, boolean isICanChange, Project project) {
        mCheckedProject = project;
        mEtTitleNameMenu.setText(name);
        mTvTitleNameMenu.setText(name);
        setOnClickActionBarText(isICanChange);
    }

    public void setOnClickActionBarText(boolean isICanChange) {
        mEtTitleNameMenu.clearFocus();
        mTvTitleNameMenu.setVisibility(View.VISIBLE);
        mEtTitleNameMenu.setVisibility(View.GONE);
        Utils.hideInput(mEtTitleNameMenu);
        if (isICanChange) {

            mTvTitleNameMenu.setClickable(true);

            mTvTitleNameMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTvTitleNameMenu.setVisibility(View.GONE);
                    mEtTitleNameMenu.setVisibility(View.VISIBLE);
                    mEtTitleNameMenu.requestFocus();
                    mEtTitleNameMenu.setSelection(mEtTitleNameMenu.getText().length());
                    Utils.showInput(mEtTitleNameMenu);
                }
            });
            mEtTitleNameMenu.setOnEditorActionListener(this);
        } else {
            mTvTitleNameMenu.setClickable(false);
        }
    }

    public void setActionBar()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbarChooser = (Toolbar) findViewById(R.id.toolbarChooserItems);
        mSimleToolbarContainer = (LinearLayout) findViewById(R.id.simple_toolbar_container);
        mMultiToolbarContainer = (RelativeLayout) findViewById(R.id.multi_toolbar_container);
        mEditToolbarContainer = (LinearLayout) findViewById(R.id.edit_toolbar_container);
        mToolbarCooserChechCount = (TextView) findViewById(R.id.toolbar_count);
        /*mToolbarCooserDelete = (ImageView) findViewById(R.id.delete_icon);
        mToolbarCooserDelete.setVisibility(View.VISIBLE);
        mToolbarCooserDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Удаляем выбранные
                if (getSettings().getCheckedTasks().size() > 0 && getSettings().getCheckedTasks() != null) {
                    Utils.getSimpleDialog(SlidingActivity.this, getDeleteDialogListener(), R.string.confirm_delete_title, R.string.confirm_delete_text_mass);
                } else {
                    Utils.showToast(SlidingActivity.this, getString(R.string.add_checked_tasks));
                }
            }
        });*/

        mEtTitleNameMenu = (EditText) findViewById(R.id.toolbar_edit_name);
        mTvTitleNameMenu = (TextView) findViewById(R.id.toolbar_text_name);
        mToolbarCustomTitle = (TextView) findViewById(R.id.toolbar_custom_title);
        mToolbarCustomSubtitle = (TextView) findViewById(R.id.toolbar_custom_subtitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
    }

    public TasksFragment getTasksFragment() {
        return  (TasksFragment) findFragmentInContainer();
    }

    public void swapToolbarModeToCheck(boolean swapToCheck) {
        TasksFragment fragment = (TasksFragment) findFragmentInContainer();
        getSettings().getCheckedTasks().clear();
        if (swapToCheck) {
            TasksFragment.isCheckModeOn = true;
            mEditToolbarContainer.setVisibility(View.GONE);
            mSimleToolbarContainer.setVisibility(View.GONE);
            mMultiToolbarContainer.setVisibility(View.VISIBLE);
            mToolbarCooserChechCount.setText("0");
            SlidingActivity.mSwipeRefreshLayout.setEnabled(false);
            TasksFragment.mActionButton.setVisibility(View.GONE);
            fragment.clearOptionsMenu(1);
        } else {
            TasksFragment.isCheckModeOn = false;
            mMultiToolbarContainer.setVisibility(View.GONE);
            mEditToolbarContainer.setVisibility(View.VISIBLE);
            mSimleToolbarContainer.setVisibility(View.GONE);
            TasksFragment.mAdapter.notifyDataSetChanged();
            SlidingActivity.mSwipeRefreshLayout.setEnabled(true);
            TasksFragment.mActionButton.setVisibility(View.VISIBLE);
            fragment.clearOptionsMenu(0);
        }
        resetHomeButtonAfterChangeToolbarMode();
    }

    public void swapToolbarModeToAddTasks(boolean swapToAdd) {
        TasksFragment fragment = (TasksFragment) findFragmentInContainer();
        if (swapToAdd) {
            //отключаем навигатор - нельзя скроллиться
            if (!Utils.isLandOrientation(getApp())) {
                if (SlidingActivity.mSlidingMenu != null)
                    SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE); // нет свайпа
            }
            TasksFragment.isAddModeOn = true;
            mEditToolbarContainer.setVisibility(View.INVISIBLE);
            mSimleToolbarContainer.setVisibility(View.INVISIBLE);
            SlidingActivity.mSwipeRefreshLayout.setEnabled(false);
            if (fragment != null) {
                fragment.clearOptionsMenu(2);
            }
        } else {
            // можно скролится
            if (!Utils.isLandOrientation(getApp())) {
                if (SlidingActivity.mSlidingMenu != null)
//                    SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); // свайп
                    SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.LEFT); // свайп
            }
            fragment.closeTaskAddMode();
            TasksFragment.isAddModeOn = false;
            mEditToolbarContainer.setVisibility(View.VISIBLE);
            mSimleToolbarContainer.setVisibility(View.INVISIBLE);
            SlidingActivity.mSwipeRefreshLayout.setEnabled(true);
            if (fragment != null) {
                fragment.clearOptionsMenu(0);
            }
        }
        resetHomeButtonAfterChangeToolbarModeToAdd();
    }

    private void resetHomeButtonAfterChangeToolbarModeToAdd() {
        if (TasksFragment.isAddModeOn) {
            //если включен мультиМод то показываем стрелку
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        } else {
            // а если не включен то показывываем или гамбургер или стрелку(если есть родители)
            if (!TasksFragment.hasParent) {
                //нет родителя, можно скролится, кнопка сендвич
                if(Utils.isLandOrientation(getApp())) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                else {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger); //1
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                }
            }
            else {
                //есть родитель, меняем кнопку в екшнбаше на "назад"
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            }

        }
    }

    private void resetHomeButtonAfterChangeToolbarMode() {
        if (TasksFragment.isCheckModeOn) {
            //если включен мультиМод то показываем стрелку
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        } else {
            // а если не включен то показывываем или гамбургер или стрелку(если есть родители)
            if (!TasksFragment.hasParent) {
                //нет родителя, можно скролится, кнопка сендвич
                if(Utils.isLandOrientation(getApp())) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                else {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                }
            }
            else {
                //есть родитель, меняем кнопку в екшнбаше на "назад"
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            }

        }
    }

    public void setDeleteIconVisibility(boolean isVisible) {
        mToolbarCooserDelete.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.fixActivityForAnalytics(getApp(), "Main");
    }

    public void setCheckedItemsCount(String count) {
        mToolbarCooserChechCount.setText(count);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mTaskDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (isFirstOpen) {
            // TODO ОЦЕНИТЕ НАШЕ ПРИЛОЖЕНИЕ
            //AppRater.app_launched(SlidingActivity.this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            boolean wasCheckMode = TasksFragment.isCheckModeOn;
            boolean wasAddMode = TasksFragment.isAddModeOn;
            onBackPressedToolbar();
            if (wasCheckMode) {
                return true;
            }
            if (wasAddMode) {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressedToolbar() {
        if (TasksFragment.isCheckModeOn) {
            swapToolbarModeToCheck(false);
        } else {
            if (TasksFragment.isAddModeOn) {
                swapToolbarModeToAddTasks(false);
            } else {
                if (TasksFragment.hasParent) {
                    getFragmentManager().popBackStack();
                } else {
                    closeOrOpenMenu();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Utils.sendListToWear(this);
        if (TasksFragment.isCheckModeOn) {
            swapToolbarModeToCheck(false);
        }
        else{
            if (TasksFragment.isAddModeOn) {
                swapToolbarModeToAddTasks(false);
            } else {
                if (!isLandOrientation()) {
                    if (mSlidingMenu.isMenuShowing()) {
                        closeOrOpenMenu();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHOW_ERROR);
        filter.addAction(MenuFragment.ACTION_MENU_ITEM);
        filter.addAction(ACTION_CHANGE_DATE);

        return filter;
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
        case ACTION_SHOW_ERROR:
            final String message = intent.getStringExtra(EXTRA_TEXT);
            final int code = intent.getIntExtra(EXTRA_CODE, 0);
            //проверяем по КОДУ ошибки нужно ли выводить ТОСТ
            if(code == LTServerError.WRONG_SERV_1.getCode() ||  code ==  LTServerError.INTERNET_ACCESS.getCode()
            || code == LTServerError.WRONG_SERV_503.getCode() || code == LTServerError.WRONG_SERV_504.getCode()) {
                Utils.showToast(this, message);
            }
            else {//если не получилось  проверяем по ИМЕНИ ошибки нужно ли выводить ТОСТ
                if (message.equals(getString(R.string.error_internet_access)) || message.equals(getString(R.string.error_wrong_serv))) {
                    Utils.showToast(this, message);
                } else {
                    if(message.equals(getString(R.string.error_account_expired))) {
                    // если ограничение по лицензии то выводим кастомный диалог с кнопкой перехода на экран продления
                        LicenseDialog.newInstance().showDialog(getFragmentManager());
                    }
                    else {
                        //если все нет то выводим ДИАЛОГИ просто в информативном виде
                        if (!message.contains("ENOENT")) {
                            ErrorDialog.newInstance(message).showDialog(getSupportFragmentManager());
                        }
                    }
                }
            }
            break;
        case MenuFragment.ACTION_MENU_ITEM:
            if (!isLandOrientation()) {
                closeOrOpenMenu();
            }
        default:
            break;
        }
    }

    private void  closeOrOpenMenu() {
        if (mSlidingMenu != null) {
            if (mSlidingMenu.isMenuShowing()) {
                mSlidingMenu.showContent();
            } else {
                mSlidingMenu.showMenu();
            }
        }
    }

    @Override
    public int getContainerId() {
        return FRAGMENT_CONTAINER;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        switch (id) {
        case R.id.lm_sync_info:
            return new CursorLoader(getApp(), SyncInfoContract.CONTENT_URI, null, null, null, null);

        default:
            return super.onCreateLoader(id, b);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> l, Cursor c) {
        switch (l.getId()) {
        case R.id.lm_sync_info:
            if (c.moveToFirst()) {
                onAllStatusesChanged(c);
            }
            break;

        default:
            super.onLoadFinished(l, c);
            break;
        }
    }

    private void onAllStatusesChanged(Cursor cursor) {
        final SyncInfo si = new SyncInfo(cursor);

        if (si.getSyncStatus() == SyncInfoErrorType.ENDED || si.getSyncStatus() == SyncInfoErrorType.ERROR) {
            final ContentValues cv = new ContentValues(7);

            switch (si.getSyncStatus()) {
            case ENDED:
                cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.NONE.ordinal());
                break;

            case ERROR:
                if (isActive()) {
                    onShowError(si);

                    cv.put(SyncInfoContract.ERROR_MESSAGE, (String) null);
                    cv.put(SyncInfoContract.ERROR_CODE, (int) 0);
                    cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.NONE.ordinal());

                } else {
                    cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.ERROR.ordinal());
                }
                break;

            default:
                break;
            }

            cv.put(SyncInfoContract.SYNC_STATUS, SyncInfoErrorType.NONE.ordinal());
            cv.put(SyncInfoContract.MENU_STATUS, SyncInfoErrorType.NONE.ordinal());
            cv.put(SyncInfoContract.LIST_STATUS, SyncInfoErrorType.NONE.ordinal());
            cv.put(SyncInfoContract.LAST_SYNC_TIME, System.currentTimeMillis());

            getApp().cancelSynchronize();
            LTPowerManager.getInstance(getApp()).sleepUnlock();
            SyncInfo.updateSynchronizationInfo(getApp(), cv);

            if (isFirstOpen) {
                isFirstOpen = false;
            }
        }
    }

    private void onShowError(SyncInfo si) {
        final ContentValues cv = new ContentValues(2);

        if (isActive()) {
            final Intent intent = new Intent(ACTION_SHOW_ERROR);
            intent.putExtra(EXTRA_TEXT, si.getErrorMessage());
            intent.putExtra(EXTRA_CODE, si.getErrorCode());
            sendLocalBroadcast(intent);

            cv.put(SyncInfoContract.ERROR_MESSAGE, (String) null);
            cv.put(SyncInfoContract.ERROR_CODE, (int) 0);
            cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.NONE.ordinal());

        } else {
            cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.ERROR.ordinal());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Waiting for time when activity can showError
                try {
                    Thread.sleep(0);

                } catch (InterruptedException e) {}

                SyncInfo.updateSynchronizationInfo(getApp(), cv);
            }
        }).start();
    }

    private boolean isActive() {
        final Fragment f = getSupportFragmentManager().findFragmentByTag(MenuFragment.CLASS_PATH);
        return f != null && f.isAdded();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> l) {
        super.onLoaderReset(l);
    }

    private boolean onIntentAction(Intent intent) {
        if (getSettings().getUserProfile().isValid()) {
            if (intent == null || intent.getAction() == null) {
                return false;
            }

            switch (intent.getAction()) {

            case ACTION_OPEN_NOTIFY_TASK: {
                final LTask task = (LTask) getIntent().getSerializableExtra(EXTRA_TASK);
                getIntent().setAction(null);
                new OpenNotifyTask(this, task).start();
            }
                break;

            case ACTION_ACTION_SEND_TEXT: {
                final String text = getIntent().getStringExtra(EXTRA_TEXT);
                final LTask task = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), getSettings().getUserName(), 0, null, null, null, null);
                task.setName(text);

                startActivity(EditTaskActivity.newInstance(this, task, true, false));
            }
                break;

            case ACTION_ACTION_LOGIN:
                finish();
                startActivity(LoginActivity.newInstance(this));
                break;

            default:
                break;
            }

        } else {
            finish();
            startActivity(LoginActivity.newInstance(this));
        }
        return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                mEtTitleNameMenu.clearFocus();
                mTvTitleNameMenu.setVisibility(View.VISIBLE);
                mEtTitleNameMenu.setVisibility(View.GONE);
                Utils.hideInput(mEtTitleNameMenu);
                mTvTitleNameMenu.setText(mEtTitleNameMenu.getText().toString().trim());
                if (mCheckedProject != null) {
                    mCheckedProject.setUsn(0);
                    mCheckedProject.setName(mEtTitleNameMenu.getText().toString().trim());
                    mCheckedProject.setUsnName(mCheckedProject.getUsnName() + 1);
                    saveProject(mCheckedProject);

                }
                return true;

            default:
                return false;
        }
    }

    public void saveProject (final Project project) {
        Runnable mSaveProjectRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getDbHelper().getProjectDao().update(project);
                } catch (SQLException e) {
                    Utils.toLog(e);
                }
                final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(getApp());
                linkHelper.updateProjectTotalLink(project);
            }
        };
        try {
            new Thread(mSaveProjectRunnable).start();
        } finally {
            Utils.startSync(getApp());
        }
    }

    public void leaveProject (final Project project) {
        Runnable mSaveProjectRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mDelProject = project;
                    getDbHelper().getProjectDao().update(project);
                } catch (SQLException e) {
                    Utils.toLog(e);
                }
                final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(getApp());
                linkHelper.deleteTotalLink(SlidingActivity.this, project);
            }
        };
        try {
            new Thread(mSaveProjectRunnable).start();
        } finally {
            try {
                Utils.startSync(getApp());
            } finally {

            }
        }
    }


    private static final class OpenNotifyTask extends Thread {

        private final WeakReference<Activity> mActivity;
        private final Context mContext;
        private final LTask mTask;

        public OpenNotifyTask(Activity activity, LTask task) {
            super(OpenNotifyTask.class.getSimpleName());

            mActivity = new WeakReference<>(activity);
            mContext = activity;
            mTask = resetTask(task);
        }

        @Override
        public void run() {
            super.run();

            Activity activity = mActivity.get();
            if (activity != null) {
                LTaskCache.getInstance(activity).refreshCache(mTask);
                activity = null;
            }

            activity = mActivity.get();
            if (activity != null) {
                activity.startActivity(EditTaskActivity.newInstance(activity, mTask, false, false));
                activity = null;
            }
        }

        private LTask resetTask(LTask taskOld) {
            StringBuilder mSb = new StringBuilder();
            Cursor c = mContext.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.LTaskContract._ID, taskOld.getIdTask()), null, null);;
            try {

                if (c.moveToFirst()) {
                    return new LTask(c);

                } else {
                    return taskOld;
                }

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }

    private void synchronize() {
        if (Utils.isNetworkAvailable(getApp())) {
            //if (!getApp().isSync()) {
                getApp().setSyncingOngoingNow(true);
                getApp().getSettings().setIsMySync(true);
                LTPowerManager.getInstance(getApp()).sleepLock();


                Account account = getSettings().getAccountHelper().getPrimaryAccount();
                boolean accountNotExist = false;

                if (account == null || (accountNotExist = getSettings().getAccountHelper().getAccountByType(0) == null)) {
                    if (accountNotExist) {
                        getSettings().setAccountHelper();
                    }

                    account = new Account(getSettings().getUserName(), AuthService.ACCOUNT_TYPE);
                    getSettings().getAccountHelper().addPrimaryAccount(account, true);
                    getSettings().setSyncPeriod(getSettings().getAutosyncModeInt());
                }
                final Bundle settingsBundle = new Bundle(2);
                settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                /*
                 * Request the sync for the default account, authority, and manual sync settings
                 */

                ContentResolver.requestSync(getSettings().getAccountHelper().getPrimaryAccount(),//
                        SyncProvider.PROVIDER_NAME, settingsBundle);
            /*} else {
                //если в данный момент синхронизируемся - ставим метку синхронизироваться еще раз после синхронизации
                LeaderTaskSyncService.mIsNeedToResync = true;
            }*/
        }
        else
        {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (SynchronizationTask.isSwipeSync) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
            Utils.showToast(SlidingActivity.this, R.string.error_internet_access);
        }
    }

    private String getLastSynchronization(Context context, Calendar calendar, boolean onlyDate) {
        SharedPreferences settings = context.getSharedPreferences(LTSettings.PREFS_NAME, 0);
        long tmp = settings.getLong(LTSettings.KEY_LAST_SYNC, -1);
        if(tmp != -1) {
            Date tmpDate = new Date();
            tmpDate.setTime(tmp);
            return mTimeHelper.getDateForSyncOrSimple(tmpDate, calendar, context, onlyDate, true);
        }
        else {
            return "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1002) {
            mPurchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    //
                    try {
                        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                        mSignature = dataSignature;
                    } catch (Exception e) {

                    }
                    //
                    JSONObject jo = new JSONObject(mPurchaseData);
                    mPackageName = jo.getString("packageName");
                    mProductId = jo.getString("productId");
                    mPurchaseToken = jo.getString("purchaseToken");

                    buyInWebreg();

                } catch (JSONException e) {
                    android.util.Log.v("Tedorius", e.getMessage());
                }
            }
        }
    }

    private void buyInWebreg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("email", LTSettings.getInstance().getUserName()));
                    nameValuePairs.add(new BasicNameValuePair("packagename", mPackageName));
                    nameValuePairs.add(new BasicNameValuePair("productid", IN_APP_ID_UUID));
                    nameValuePairs.add(new BasicNameValuePair("token", mPurchaseToken));
                    nameValuePairs.add(new BasicNameValuePair("language", Locale.getDefault().getLanguage()));
                    nameValuePairs.add(new BasicNameValuePair("currency", mCurrency));
                    nameValuePairs.add(new BasicNameValuePair("amount", mAmount));
                    long endDate = getSettings().getVerifyEndDateInLong();
                    if (endDate < 0 || endDate < TimeHelper.currentTimeMillisWithoutTimeZone()) {
                        endDate = TimeHelper.currentTimeMillisWithoutTimeZone();
                    }
                    nameValuePairs.add(new BasicNameValuePair("days", TimeHelper.getInstance().getIntDifferencesDateInDays(endDate, TimeHelper.currentTimeMillisWithoutTimeZone())+IN_APP_DAYS+""));
                    nameValuePairs.add(new BasicNameValuePair("signature", mSignature));
                    nameValuePairs.add(new BasicNameValuePair("full_purchase", mPurchaseData));

                    String message = OkHttpConnection.postWithParams(nameValuePairs, NETWROK_BUY_LEADERTASK);

                    if (message.equals("OK")) {
                        SlidingActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Utils.showToast(SlidingActivity.this, "Покупка прошла успешно!");
                            }
                        });
                        Utils.startSync(((LTApplication) getApplicationContext()));
                    } else {
                        // ошибка
                        SlidingActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Utils.showToast(SlidingActivity.this, getResources().getString(R.string.exception_unknown));
                            }
                        });
                    }
                } catch (Exception e) {
                    SlidingActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Utils.showToast(SlidingActivity.this, getResources().getString(R.string.exception_unknown));
                        }
                    });
                }
            }
        }).start();
    }
}