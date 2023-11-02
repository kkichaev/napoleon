package com.ashberrysoft.leadertask.activities;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.fragments.BaseTasksListFragment;
import com.ashberrysoft.leadertask.fragments.SlidingMenuFragment;
import com.ashberrysoft.leadertask.fragments.SubtasksListFragment;
import com.ashberrysoft.leadertask.fragments.TabViewFragment;
import com.ashberrysoft.leadertask.fragments.TasksListFragment;
import com.ashberrysoft.leadertask.interfaces.FragmentsCommunicationInterface;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.LTPowerManager;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.SimpleNotifications;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * Стартовая активити приложения
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @deprecated
 */
public class HomeActivity extends BaseSlidingActivity implements FragmentsCommunicationInterface, LoaderCallbacks<Cursor> {

    public interface TaskCommunicationInterface {

        public void removeTask(Task task);

        public void addTask(Task task);

        public void changeTask(Task task);
    }

    private static final String EXTRA_INTENT_CLOSE = "EXTRA_INTENT_CLOSE";
    private static final String EXTRA_INTENT_RESTART = "EXTRA_INTENT_RESTART";
    private static final String EXTRA_SLIDING_ACTIVITY_HELPER = "SlidingActivityHelper.open";
    private static final String SLIDING_MENU_TAG = "SLIDING_MENU_TAG";

    public static final int SLIDING_CONTAINER = R.id.menu_frame;
    public static final int FRAGMENT_CONTAINER = R.id.main_fragment;

    public static final String TASK_LIST_TAG = TasksListFragment.class.getSimpleName();
    public static final String SUBTASK_LIST_TAG = SubtasksListFragment.class.getSimpleName();
    public static final String TAB_VIEW_TAG = TabViewFragment.class.getSimpleName();

    private boolean mStoped;

    private Integer mSlidingWidth;
    private Integer mDisplayWidth;

    private Handler mHandler;

    private WeakReference<FrameLayout> mFrameLayout;

    public static Intent newInstance(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    public static Intent closeInstance(Context context) {
        final Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(EXTRA_INTENT_CLOSE, true);
        return intent;
    }

    public static Intent restartInstance(Context context) {
        final Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(EXTRA_INTENT_RESTART, true);

        return intent;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mHandler = new Handler();

        if (finishMe(getIntent())) {
            return;
        }

        // create SlidingMenuFragment only if it not exists yet
        if (getSupportFragmentManager().findFragmentByTag(SLIDING_MENU_TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(SLIDING_CONTAINER, SlidingMenuFragment.newInstance(), SLIDING_MENU_TAG);
            ft.commit();
        }

        if (mApp.getSettings().getUserProfile().isValid()) {
            // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            if (b == null) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(FRAGMENT_CONTAINER, TasksListFragment.newInstance(true, false), TASK_LIST_TAG);
                ft.commit();

                if (SimpleNotifications.ACTION_OPEN_TASK.equals(getIntent().getAction())) {
                    final Task task = (Task) getIntent().getSerializableExtra(SimpleNotifications.EXTRA_TASK);
                    if (task != null) {
                        openTabViewFragment(task);
                    }
                }

            }

        } else {
            finish();
            startActivity(LoginActivity.newInstance(getApplicationContext()));
            return;
        }

        setSlidingMenu();
        getSupportLoaderManager().restartLoader(R.id.lm_sync_info, null, this);
    }

    private void setSlidingMenu() {
        enableSlidingMenu();

        final int slidingCustomWidth = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);

        if (mApp.getSettings().setSmallScreen(slidingCustomWidth >= getDisplayWidth())) {
            mSlidingWidth = getDisplayWidth() - getResources().getDimensionPixelSize(R.dimen.slidingmenu_to_small);

        } else {
            mSlidingWidth = (int) (getDisplayWidth() / 3.2);
            if (mSlidingWidth < slidingCustomWidth) {
                mSlidingWidth = slidingCustomWidth;
            }
        }

        mApp.getSettings().setLTCalendarWidth(mSlidingWidth);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        if (SimpleNotifications.ACTION_OPEN_TASK.equals(intent.getAction())) {
            final Task task = (Task) intent.getSerializableExtra(SimpleNotifications.EXTRA_TASK);

            if (task != null) {
                try {
                    openTabViewFragment(task);

                } catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    });
                    return;
                }
            }
        }

//        if (mApp.getSettings().isCleanData()) {
//            mApp.getSettings().setCleanData(false);
//            finish();
//            startActivity(HomeActivity.newInstance(mApp));
//
//        } else {
//            finishMe(intent);
//        }

        super.onNewIntent(intent);
    }

    private void openTabViewFragment(Task task) {
        final FragmentManager fm = getSupportFragmentManager();
        for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
            if (fm.getBackStackEntryAt(i).getName().equals(TAB_VIEW_TAG)) {
                fm.popBackStack();
                break;
            }
        }

        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(FRAGMENT_CONTAINER, TabViewFragment.newInstance(null, task));
        ft.addToBackStack(TAB_VIEW_TAG);
        ft.commit();

        mApp.getSettings().setTaskFromNotify(true);
    }

    @Override
    public void onResume() {
        mApp.setTheme(this);
        super.onResume();

        if (mApp.getSettings().isLocaleWasChanged()) {
            mApp.getSettings().setLocaleWasChanged(false);
            finish();
            startActivity(HomeActivity.newInstance(mApp));
            return;
        }

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceConstants.ACTION_SERVICE_ERROR);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        if (isLandOrientation()) {
            setSlidingMenuHorizontal(mSlidingWidth, mApp.getSettings().isSlidingMenuOpen());

        } else {

        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        SubtasksListFragment.sIsScreenMinimized = true;
        super.onStop();
    }

    private boolean finishMe(Intent intent) {
        if (intent == null) {
            return false;
        }

        final Bundle bundle = intent.getExtras();
        if (bundle == null || bundle.isEmpty()) {
            return false;
        }

        if (bundle.getBoolean(EXTRA_INTENT_CLOSE, false)) {
            super.finish();
            return true;
        }

        if (bundle.getBoolean(EXTRA_INTENT_RESTART, false)) {
            super.finish();
            startActivity(HomeActivity.newInstance(mApp));
            return true;
        }

        if (intent.getAction() == Intent.ACTION_SEND && SharedStrings.MIME_TYPE_PLAIN.equals(intent.getType())) {
            final String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (extraText == null) {
                return false;
            }

            mApp.setTextPlainSend(extraText);
            if (mApp.getSettings().getUserProfile().isValid()) {
                finish();
                startActivity(LoginActivity.newInstance(this));
                return false;
            }
        }

        return false;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public void showErrorMessage(String message) {
        if (!mStoped) {
            showError(message);
        }
    }

    private int getDisplayWidth() {
        if (mDisplayWidth == null) {
            final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            mDisplayWidth = display.getWidth();
        }
        return mDisplayWidth;
    }

    private void setSlidingMenuHorizontal(Integer offset, boolean setToFrame) {
        if (mSlidingWidth != null) {
        }

        if (setToFrame) {
            FrameLayout container = mFrameLayout.get();
            if (container == null) {
                container = mFrameLayout.get();
            }

            if (container != null) {
                container.getLayoutParams().width = getDisplayWidth() - offset;
                container.requestLayout();
            }
        }
    }

    /**
     * in landscape orientation if application first starting then enable sliding menu
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
            if (mApp.getSettings().isFirstLaunch()) {
                savedInstanceState.putBoolean(EXTRA_SLIDING_ACTIVITY_HELPER, isLandOrientation());
                mApp.getSettings().setIsFirstLaunch(false);

            } else {
                savedInstanceState.putBoolean(EXTRA_SLIDING_ACTIVITY_HELPER, mApp.getSettings().isSlidingMenuOpen());
            }
        }
        super.onPostCreate(savedInstanceState);
    }

    // save sliding menu state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mStoped = true;
    }

    // delete task from particular tasks screen
    @Override
    public void onTaskDeleted(Task task, boolean isFromDialog, boolean isChangeStatusDirectly) {
        final FragmentManager fm = getSupportFragmentManager();
        for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
            final BackStackEntry entry = fm.getBackStackEntryAt(i);
            try {
                if (entry.getName().equals(SUBTASK_LIST_TAG) || entry.getName().equals(TASK_LIST_TAG)) {
                    ((BaseTasksListFragment) fm.findFragmentByTag(entry.getName())).removeTask(task);
                    return;
                }
            } catch (Exception e) {
                Utils.toLog(e);
            }
        }
        final BaseTasksListFragment f = (BaseTasksListFragment) fm.findFragmentByTag(TASK_LIST_TAG);
        if (f != null) {
            f.removeTask(task);
        }
    }

    // add task to particular tasks screen
    @Override
    public void onTaskAdded(Task task) {
        final FragmentManager fm = getSupportFragmentManager();
        for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
            final BackStackEntry entry = fm.getBackStackEntryAt(i);
            try {
                if (entry.getName().equals(SUBTASK_LIST_TAG) || entry.getName().equals(TASK_LIST_TAG)) {
                    ((BaseTasksListFragment) fm.findFragmentByTag(entry.getName())).addTask(task);
                    return;
                }
            } catch (Exception e) {
                Utils.toLog(e);
            }
        }
        final BaseTasksListFragment f = (BaseTasksListFragment) fm.findFragmentByTag(TASK_LIST_TAG);
        if (f != null) {
            f.addTask(task);
        }
    }

    @Override
    public void onTaskChanged(Task task) {
        final FragmentManager fm = getSupportFragmentManager();
        for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
            final BackStackEntry entry = fm.getBackStackEntryAt(i);
            try {
                if (entry.getName().equals(SUBTASK_LIST_TAG) || entry.getName().equals(TASK_LIST_TAG)) {
                    ((BaseTasksListFragment) fm.findFragmentByTag(entry.getName())).changeTask(task);
                    return;
                }
            } catch (Exception e) {
                Utils.toLog(e);
            }
        }
        final BaseTasksListFragment f = (BaseTasksListFragment) fm.findFragmentByTag(TASK_LIST_TAG);
        if (f != null) {
            f.changeTask(task);
        }
    }

    @Override
    public void onBackPressed() {
        boolean toPressBack = true;
        final Fragment f = getSupportFragmentManager().findFragmentById(FRAGMENT_CONTAINER);
        if (f != null && f instanceof OnBackClickListener) {
            toPressBack = ((OnBackClickListener) f).onBackClick();
        }

        if (toPressBack) {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        switch (id) {
        case R.id.lm_sync_info:
            return new CursorLoader(mApp, SyncInfoContract.CONTENT_URI, null, null, null, null);

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
        case R.id.lm_sync_info:
            onAllStatusesChanged(cursor);
        default:
            break;
        }
    }

    private void onAllStatusesChanged(Cursor cursor) {
        cursor.moveToFirst();
        final SyncInfo si = new SyncInfo(cursor);

        if (si.getSyncStatus() == SyncInfoErrorType.NONE && //
                si.getMenuStatus() == SyncInfoErrorType.NONE && //
                si.getListStatus() == SyncInfoErrorType.NONE && //
                si.getErrorStatus() == SyncInfoErrorType.ERROR) {
            onShowError(si);
        }

        else if ((si.getSyncStatus() == SyncInfoErrorType.ENDED || si.getSyncStatus() == SyncInfoErrorType.ERROR) && //
                si.getMenuStatus() == SyncInfoErrorType.ENDED && //
                si.getListStatus() == SyncInfoErrorType.ENDED) {
            final ContentValues cv = new ContentValues(7);

            switch (si.getSyncStatus()) {
            case ENDED:
                cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.NONE.ordinal());
                break;

            case ERROR:
                if (isActive()) {
                    showError(si.getErrorMessage());
                    cv.put(SyncInfoContract.ERROR_MESSAGE, (String) null);
                    cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.NONE.ordinal());
                } else {
                    cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.ERROR.ordinal());
                }
            default:
                break;
            }

            cv.put(SyncInfoContract.SYNC_STATUS, SyncInfoErrorType.NONE.ordinal());
            cv.put(SyncInfoContract.MENU_STATUS, SyncInfoErrorType.NONE.ordinal());
            cv.put(SyncInfoContract.LIST_STATUS, SyncInfoErrorType.NONE.ordinal());
            cv.put(SyncInfoContract.LAST_SYNC_TIME, System.currentTimeMillis());

            mApp.cancelSynchronize();
            LTPowerManager.getInstance(mApp).sleepUnlock();
            SyncInfo.updateSynchronizationInfo(mApp, cv);
        }
    }

    private void onShowError(SyncInfo si) {
        final ContentValues cv = new ContentValues(2);

        if (isActive()) {
            showError(si.getErrorMessage());
            cv.put(SyncInfoContract.ERROR_MESSAGE, (String) null);
            cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.NONE.ordinal());
        } else {
            cv.put(SyncInfoContract.ERROR_STATUS, SyncInfoErrorType.ERROR.ordinal());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Waiting for time when activity can showError
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}

                SyncInfo.updateSynchronizationInfo(mApp, cv);
            }
        }).start();
    }

    private boolean isActive() {
        final Fragment f = getSupportFragmentManager().findFragmentByTag(SLIDING_MENU_TAG);
        return f != null && f.isAdded();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}