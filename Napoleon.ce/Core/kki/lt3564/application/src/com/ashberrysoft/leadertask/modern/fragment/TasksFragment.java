package com.ashberrysoft.leadertask.modern.fragment;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SearchActivity;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.CalendarDataContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.DownloadFile;
import com.ashberrysoft.leadertask.data_providers.network.SynchronizationTask;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.adapter.LTasksCursorAdapter;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.changer.TaskStatusAnimationChanger;
import com.ashberrysoft.leadertask.modern.dialog.CalendarDialog;
import com.ashberrysoft.leadertask.modern.dialog.ChangeCategoryDialog;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.dialog.MultiTaskPerformerDialog;
import com.ashberrysoft.leadertask.modern.dialog.MultiTasksCategoriesDialog;
import com.ashberrysoft.leadertask.modern.dialog.MultiTasksProjectDialog;
import com.ashberrysoft.leadertask.modern.dialog.MultiTasksTermDialog;
import com.ashberrysoft.leadertask.modern.dialog.ProjectMembersDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskCategoriesDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskEmailsDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskMarkerDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskPerformerDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskProjectDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskStatusDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskTermDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskTermDialogNew;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.modern.domains.menu.CalendarMenuItem;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskFootstepHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSeriesHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.modern.loader.ByMeLoader;
import com.ashberrysoft.leadertask.modern.loader.CalendarDayLoader;
import com.ashberrysoft.leadertask.modern.loader.CategoriesLoader;
import com.ashberrysoft.leadertask.modern.loader.ColorLoader;
import com.ashberrysoft.leadertask.modern.loader.EmailsLoader;
import com.ashberrysoft.leadertask.modern.loader.EmpLoader;
import com.ashberrysoft.leadertask.modern.loader.FocusLoader;
import com.ashberrysoft.leadertask.modern.loader.ForMeLoader;
import com.ashberrysoft.leadertask.modern.loader.InboxLoader;
import com.ashberrysoft.leadertask.modern.loader.InworkLoader;
import com.ashberrysoft.leadertask.modern.loader.OverdueLoader;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.modern.loader.ProjectsLoader;
import com.ashberrysoft.leadertask.modern.loader.ReadyLoader;
import com.ashberrysoft.leadertask.modern.loader.TaskChildsLoader;
import com.ashberrysoft.leadertask.modern.loader.UnreadLoader;
import com.ashberrysoft.leadertask.modern.view.list_item.LTaskItemView.OnLTaskItemListener;
import com.ashberrysoft.leadertask.utils.LTPowerManager;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.BadgeView;
import com.ashberrysoft.leadertask.views.CustomEditTextNewNew;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.software.shell.fab.ActionButton;
import com.v2soft.AndLib.dao.ITreePureNode;

import static com.ashberrysoft.leadertask.enums.MenuItemType.EMP;
import static com.ashberrysoft.leadertask.enums.MenuItemType.FOR_ME;
import static com.ashberrysoft.leadertask.enums.MenuItemType.READY;
import static com.ashberrysoft.leadertask.enums.MenuItemType.INWORK;
import static com.ashberrysoft.leadertask.enums.MenuItemType.OVERDUE;
import static com.ashberrysoft.leadertask.enums.MenuItemType.UNREAD;
import static com.ashberrysoft.leadertask.modern.fragment.MenuFragment.ACTION_MENU_ITEM;
import static com.ashberrysoft.leadertask.modern.helper.TimeHelper.DEFAULT_TIME_ZONE;


@SuppressLint("InflateParams")
public class TasksFragment extends BaseSyncStatusFragment//
        implements OnLTaskItemListener, CustomEditTextNewNew.BackPressedListener{

    public static final String CLASS_PATH = TasksFragment.class.getSimpleName();
    private static final String EXTRA_MAKE_TASK_HIDE = CLASS_PATH + "EXTRA_MAKE_TASK_HIDE";
    private static final String EXTRA_MENU_ITEM = CLASS_PATH + "EXTRA_MENU_ITEM";
    private static final String EXTRA_NOW_EDIT_TASK = CLASS_PATH + "EXTRA_NOW_EDIT_TASK";
    private static final String EXTRA_PARENT_TASK = CLASS_PATH + "EXTRA_PARENT_TASK";

    private static final String EXTRA_TEMP_TASK = CLASS_PATH + "EXTRA_TEMP_TASK";

    public static final String ACTION_CALENDAR_ITEM = "ACTION_CALENDAR_ITEM";

    private static final int BADGE_GREEN_COLOR = Color.argb(200, 150, 150, 150);

    private int longClickDuration = 300;
    private boolean isPressedLong = false;
    private boolean isPressed = false;

    public static boolean canSwipeToRefresh;
    public static boolean isScrolling = false;

    public static int scrollToPos = -1;
    public static int scrollFirstVisible = -1;
    public static int scrollVisibleCount = -1;
    // ADAPTER
    public static LTasksCursorAdapter mAdapter;
    private boolean mInitAfterCreation;
    private ListView mListView;
    private RelativeLayout mUnboardingAddTaskContainer;
    private LinearLayout mNoTasksContainer;
    public static ActionButton mActionButton;
    public static ActionButton mActionUnreadTop;
    public static ActionButton mActionUnreadBottom;
    public CustomEditTextNewNew mEtAddTask;
    public RelativeLayout mFastTerm;
    public ImageView mFastTermIc;
    public RelativeLayout mFastPerformer;
    public ImageView mFastPerformerIc;
    public ImageView mFastPerformerIcCustom;
    public RelativeLayout mFastProject;
    public ImageView mFastProjectIc;
    public RelativeLayout mFastMarker;
    public RelativeLayout mFastCategory;
    public RelativeLayout mFastEmails;
    public ImageView mFastMarkerIc;
    public ImageView mFastCategoryIc;
    public LinearLayout mFooterAddTask;
    public LinearLayout mFooterCheckTask;
    public RelativeLayout mFooterCheckTaskSet;
    public RelativeLayout mFooterCheckTaskBuffer;
    public LinearLayout mFooterCheckTaskDel;
    private int toTop = -1;
    private int toBottom = -1;

    private boolean mMakeTaskHide;
    public static boolean isCheckModeOn;
    public static boolean isAddModeOn;

    // VALUE's
    private BadgeView mBvChildsCount;
    public static BaseMenuItem mMenuItem;
    private MenuItem mMenuItemSortColor;
    private MenuItem mMenuItemSortTerm;
    private MenuItem mMenuItemSortCreation;
    private MenuItem mMenuItemShowMakeTask;
    private MenuItem mMenuItemFollowProject;
    private MenuItem mMenuItemLeaveProject;
    private MenuItem mMenuItemCanDeleteProject;
    private MenuItem mMenuItemCanDeleteCategory;
    private MenuItem mMenuItemCanChangeCategory;
    private MenuItem mMenuItemCanShareProject;
    private boolean canSetForCustomer;
    private boolean canCut;
    private boolean canPaste;
    private LTask mParent;
    public static boolean hasParent;
    private Bundle mBundle;
    private ProgressBar mProgressBar;
    private int isSimpleMenu = 0;
    private boolean mPasteIntoYourself = false;
    private boolean mReAssign = false;
    private boolean isNeedSetProjectEditFunctions = false;
    private boolean isProjectAnCanLeave = false;
    private boolean isProjectCanDelete = false;
    private boolean isCategory = false;
    private Project mCheckedProject;
    private Category mCheckedCategory;
    private ProgressDialog mProgress;
    // VIEW's
    private boolean isAddedNewTask;

    private int mTasksCount;
    private int mTypeSort;

    private static ImageView mTempIv;
    public Menu mMenu;
    private MenuInflater mMenuInflater;
    private static boolean firstTouch = true;
    private SlidingActivity mActivity; 
    private FragmentManager mFm;

    // TEMP
    public static LTask mTempTask;
    public static boolean mIsAfterSwipe;
    public static LTask mTempAddTask;
    public ArrayList <LTask> mTempTasks;
    private static final String SPLIT_SYMBOL = "\\.\\.";
    private static final String DOUBLE_DOTS = "..";
    //BackSwipe
    public static final int X_MAX = 750;
    public static final int X_MIN = 130;
    public static final int X_MIN_MINUS = -130;
    public static final int Y_MAX = 150;
    private ArrayList mScrollUnreadArray = new ArrayList();



    @Override
    public void onClickTask(LTask task, boolean hasChilds, int position) {
        if (!isAddModeOn) {
            if (!isCheckModeOn) {
                if (hasChilds) {
                    startFragment(TasksFragment.newInstance(mMenuItem, task), true);
                } else {
                    mActivity.swapToolbarModeToAddTasks(false);
                    mActivity.startActivity(EditTaskActivity.newInstance(mActivity, task, false, false));
                }
            } else {
                onClickToAddChecked(task);
            }
        } else {
            addNewTaskMulti();
        }
    }

    public void startFragment(BaseFragment fragment, boolean toBackStack) {
        final FragmentTransaction ft = mFm.beginTransaction();
        int container = ((BaseActivity) mActivity).getContainerId();
        ft.replace(container, fragment);
        if (toBackStack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }

        //ft.commit();
        ft.commitAllowingStateLoss();
    }

    private void onClickToAddChecked(LTask task) {
        SlidingActivity activity = mActivity;
        boolean hasNotMyTasks = false;
        //добавляем в список которые надо удалить
        //подсвечиваем задачи
        if (getSettings().getCheckedTasks().contains(task.getUid())) {
            activity.addCheckedTasks(false, task);
        } else {
            activity.addCheckedTasks(true, task);
        }
        //меняем кол-во выбраных
        activity.setCheckedItemsCount(""+getSettings().getCheckedTasks().size());
        Cursor cursor = mAdapter.getCursor();

        final int uid = cursor.getColumnIndex(LionMetaData.LTaskContract.Uid);
        final int emailCustomer = cursor.getColumnIndex(LionMetaData.LTaskContract.EmailCustomer);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            final String stringUUID = cursor.getString(uid);
            final String stringEmailCustomer = cursor.getString(emailCustomer);
            for (String checkedUid : getSettings().getCheckedTasks()) {
                if (stringUUID.equals(checkedUid)) {
                    if (!stringEmailCustomer.equals(getSettings().getUserName())) {
                        hasNotMyTasks = true;
                        break;
                    }
                }
            }
        }
        boolean isHasCheckedTasks = getSettings().getCheckedTasks().size() > 0;

        canSetForCustomer = isHasCheckedTasks && !hasNotMyTasks;
        canCut = !hasNotMyTasks && getSettings().getCheckedTasks().size() > 0;
        canPaste = (getSettings().getBufferCopyTasks().size() > 0 || getSettings().getBufferCutTasks().size() > 0 ) && !mMenuItem.getMenuItemType().equals(FOR_ME) && !mMenuItem.getMenuItemType().equals(READY) && !mMenuItem.getMenuItemType().equals(INWORK) && !mMenuItem.getMenuItemType().equals(OVERDUE);
        mFooterCheckTaskDel.setVisibility(canSetForCustomer ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClickTaskStatus(LTask task, ImageView iv, boolean select) {
        if (!isAddModeOn) {
            if (select) {
                mTempTask = task;
                mTempIv = iv;
                if(isPerformerOrCustomerInTask(task) && !isCheckModeOn) {
                    TasksFragment fragment = mActivity.getTasksFragment();
                    TaskStatusDialog.newInstance(fragment, task, true).showDialog(fragment.getFragmentManager());
                }
            } else {
                mTempTask = task;
                mTempIv = iv;
                if(isPerformerOrCustomerInTask(task) && !isCheckModeOn) {
                    if (mTempTask != null) {
                        int status = task.getStatus();
                        if (task.getEmailCustomer().equals(getSettings().getUserName())) {
                            status = TaskStatus.COMPLETED.getCode();
                        } else {
                            if (task.getEmailPerformer().equals(getSettings().getUserName())) {
                                status = TaskStatus.READY.getCode();
                            }
                        }
                        if (status != mTempTask.getStatus()) {
                            new TaskStatusAnimationChanger(getApp(), mTempTask, mTempIv, TaskStatus.getTaskStatus(status)).start();
                            Utils.playAudio(mActivity, 1);
                        }
                        mTempTask = null;
                        mTempIv = null;
                    }

                }

            }
        } else {
            addNewTaskMulti();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (SlidingActivity) activity;
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        long term = 0;
        switch (item.getItemId()) {
            case R.id.menu_subtasks:
                startFragment(TasksFragment.newInstance(mMenuItem, mTempTask));
                mTempTask = null;
                return true;

            case R.id.menu_term:
                TaskTermDialogNew.newInstance(this, mTempTask).showDialog(getFragmentManager());
                return true;

            case R.id.menu_assign:
                mReAssign = false;
                TaskPerformerDialog.newInstance(this, mTempTask, false).showDialog(getFragmentManager());
                return true;

            case R.id.to_today:
                final LTask taskOld = mTempTask.clone();
                term = TimeHelper.currentTimeMillisWithoutTimeZone();
                if (mTempTask.getEmailCustomer().equals(getSettings().getUserName())) {
                    mTempTask.setTermBegin(setTimeTo(term, true));
                    mTempTask.setTermEnd(setTimeTo(term, false));
                    mTempTask.setTermBeginCustomer(setTimeTo(term, true));
                    mTempTask.setTermEndCustomer(setTimeTo(term, false));

                    mTempTask.setUsnFieldTerm(mTempTask.getUsnFieldTerm() + 1);
                    mTempTask.setUsnFieldCustomerTerm(mTempTask.getUsnFieldCustomerTerm() + 1);
                } else {
                    mTempTask.setTermBegin(setTimeTo(term, true));
                    mTempTask.setTermEnd(setTimeTo(term, false));

                    mTempTask.setUsnFieldTerm(mTempTask.getUsnFieldTerm() + 1);
                }

                saveTask(mTempTask, taskOld);
                return true;

            case R.id.to_tomorrow:
                final LTask taskOld2 = mTempTask.clone();
                term = TimeHelper.currentTimeMillisWithoutTimeZone()+86400000;
                if (mTempTask.getEmailCustomer().equals(getSettings().getUserName())) {
                    mTempTask.setTermBegin(setTimeTo(term, true));
                    mTempTask.setTermEnd(setTimeTo(term, false));
                    mTempTask.setTermBeginCustomer(setTimeTo(term, true));
                    mTempTask.setTermEndCustomer(setTimeTo(term, false));

                    mTempTask.setUsnFieldTerm(mTempTask.getUsnFieldTerm() + 1);
                    mTempTask.setUsnFieldCustomerTerm(mTempTask.getUsnFieldCustomerTerm() + 1);
                } else {
                    mTempTask.setTermBegin(setTimeTo(term, true));
                    mTempTask.setTermEnd(setTimeTo(term, false));

                    mTempTask.setUsnFieldTerm(mTempTask.getUsnFieldTerm() + 1);
                }

                saveTask(mTempTask, taskOld2);
                return true;

            case R.id.menu_reassign:
                mReAssign = true;
                TaskPerformerDialog.newInstance(this, mTempTask, false).showDialog(getFragmentManager());
                return true;

            case R.id.menu_take_on_exec:
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mActivity.getString(R.string.take_on_exec)+"?");
                builder.setNegativeButton(mActivity.getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(mActivity.getString(R.string.txt_just_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPerformerAfterExec();
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;

            case R.id.menu_dell:
                //Utils.getSimpleDialog(mActivity, getDeleteDialogListener(), R.string.confirm_delete_title, R.string.confirm_delete_text);
                final AlertDialog.Builder ad = new AlertDialog.Builder(mActivity);
                ad.setCancelable(true);
                ad.setTitle(getResources().getString(R.string.confirm_delete_title));
                ad.setMessage(getResources().getString(R.string.confirm_delete_text));
                ad.setPositiveButton(R.string.txt_just_yes, getDeleteDialogListener());
                ad.setNegativeButton(R.string.txt_just_nono, getDeleteDialogListener());

                ad.show();
                return true;

            case R.id.menu_properties:
                startActivity(EditTaskActivity.newInstance(mActivity, mTempTask, false, false));
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private long setTimeTo(long date, boolean startOfDay) {
        Calendar mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        mCalendar.setTimeInMillis(date == 0 ? System.currentTimeMillis() : date);
        return TimeHelper.roundCalendar(mCalendar, startOfDay).getTimeInMillis();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mBundle = b == null ? getArguments() : b;
        mMenuItem = (BaseMenuItem) mBundle.getSerializable(EXTRA_MENU_ITEM);
        mParent = (LTask) mBundle.getSerializable(EXTRA_PARENT_TASK);
        hasParent = mBundle.getSerializable(EXTRA_PARENT_TASK) != null ? true : false;
        mMakeTaskHide = mBundle.getBoolean(EXTRA_MAKE_TASK_HIDE, getSettings().isMakeTaskHide());

        mTempTask = (LTask) mBundle.getSerializable(EXTRA_TEMP_TASK);

        mAdapter = new LTasksCursorAdapter(mActivity, this);
        mInitAfterCreation = true;
        isCheckModeOn = false;
        isAddModeOn = false;
        mTempAddTask = null;
        mFm = getFragmentManager();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        if (id == R.id.lm_sync_info) {
            return super.onCreateLoader(id, b);
        } else {
            Utils.changeVisibility(mProgressBar, View.VISIBLE);
            if (mParent == null) {
                switch (mMenuItem.getMenuItemType()) {
                    case TODAY:
                        return new CalendarDayLoader(mActivity, TimeHelper.currentTimeMillisWithoutTimeZone());

                    case CALENDAR_DAY:
                        return new CalendarDayLoader(mActivity, mMenuItem.getUniqueId());

                    case INBOX:
                        return new InboxLoader(mActivity);

                    case UNREAD:
                        return new UnreadLoader(mActivity);

                    case FOCUS:
                        return new FocusLoader(mActivity);

                    case READY:
                        return new ReadyLoader(mActivity);

                    case INWORK:
                        return new InworkLoader(mActivity);

                    case OVERDUE:
                        return new OverdueLoader(mActivity);

                    case BY_ME:
                        return new ByMeLoader(mActivity, mMenuItem.getUid());

                    case EMP:
                        return new EmpLoader(mActivity, mMenuItem.getUid());

                    case FOR_ME:
                        return new ForMeLoader(mActivity, mMenuItem.getUid());

                    case PROJECTS:
                    case PROJECTS_SHARED:
                    case AVAILABLE_PROJECTS:
                        return new ProjectsLoader(mActivity, mMenuItem.getUid());

                    case CATEGORIES:
                        return new CategoriesLoader(mActivity, mMenuItem.getUid());

                    case COLOR:
                        return new ColorLoader(mActivity, mMenuItem.getUid());

                    case EMAILS:
                        MenuLoader mu = MenuLoader.getInstance(getContext());
                        return new EmailsLoader(mActivity, mu.emailsMenuItem);

                    default:
                        return null;
                }

            } else {
                return new TaskChildsLoader(mActivity, mParent.getIdTask());
            }
        }
    }

    private void setNeedSetProjectEditFunctions() {
        isNeedSetProjectEditFunctions = !isCheckModeOn && !hasParent && ( mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS_SHARED ) || mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS ));
        boolean isProject = !isCheckModeOn && !hasParent && ( mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS_SHARED)  || mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS) || mMenuItem.getMenuItemType().equals(MenuItemType.AVAILABLE_PROJECTS));
        isCategory = !isCheckModeOn && !hasParent && mMenuItem.getMenuItemType().equals(MenuItemType.CATEGORIES);
        isProjectAnCanLeave = !isCheckModeOn && !hasParent && mMenuItem.getMenuItemType().equals(MenuItemType.AVAILABLE_PROJECTS);
        isProjectCanDelete = !isCheckModeOn && !hasParent && (mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS) || mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS_SHARED));
        if (isProject) {
            final String uid = mMenuItem.getUid();
            mCheckedProject = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCheckedProject = getDbHelper().getProjectByUUId(UUID.fromString(uid));
                    } catch (Exception e) {
                        mCheckedProject = null;
                    } finally {

                    }
                }
            }).start();


        }

        if (isCategory) {
            final String uid = mMenuItem.getUid();
            mCheckedCategory = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCheckedCategory = getDbHelper().getCategoryByUUId(UUID.fromString(uid));
                    } catch (Exception e) {
                        mCheckedCategory = null;
                    } finally {

                    }
                }
            }).start();

        }
    }

    private void setPerformerAfterExec() {
        if (mTempTask != null) {
            final String performer = getSettings().getUserName();
            if (!performer.equalsIgnoreCase(mTempTask.getEmailPerformer())) {
                final LTask taskOld = mTempTask.clone();

                mTempTask.setEmailPerformer(performer.toLowerCase());
                mTempTask.setUsnFieldEmailPerformer(mTempTask.getUsnFieldEmailPerformer() + 1);

                mTempTask.setPerformTime(System.currentTimeMillis());
                mTempTask.setUsnFieldPerformtime(mTempTask.getUsnFieldPerformtime() + 1);

                saveTask(mTempTask, taskOld);
            }
            mTempTask = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenuInflater = inflater;
        menu.clear();
        if (isSimpleMenu == 0) {
            if (mFooterCheckTask != null) {
                mFooterCheckTask.setVisibility(View.GONE);
            }
            mMenuInflater.inflate(R.menu.tasks_fragment, menu);
            mMenu = menu;
            mMenuItemShowMakeTask = menu.findItem(R.id.show_hide_make_task);
            mMenuItemShowMakeTask.setTitle(getSettings().isMakeTaskHide() ? R.string.menu_show_make_task : R.string.menu_hide_make_task);
            mMenuItemFollowProject = menu.findItem(R.id.follow_project);
            mMenuItemSortColor = menu.findItem(R.id.sort_color);
            mMenuItemSortTerm= menu.findItem(R.id.sort_date);
            mMenuItemSortCreation = menu.findItem(R.id.sort_date_creation);
            mMenuItemLeaveProject = menu.findItem(R.id.leave_project);
            mMenuItemCanShareProject = menu.findItem(R.id.share_project);
            mMenuItemCanDeleteProject = menu.findItem(R.id.del_project);
            mMenuItemCanDeleteCategory= menu.findItem(R.id.del_category);
            mMenuItemCanChangeCategory= menu.findItem(R.id.change_category);

            if (mCheckedProject != null && !mCheckedProject.getCreator().equals(getSettings().getUserName())) {
                mMenuItemFollowProject.setVisible(true);
                mMenuItemFollowProject.setTitle(!mCheckedProject.isQuiet() ? R.string.not_follow : R.string.follow);
                mMenuItemCanShareProject.setVisible(true);
            } else {
                mMenuItemFollowProject.setVisible(false);
                mMenuItemCanShareProject.setVisible(false);
            }
            if (isProjectAnCanLeave) {
                mMenuItemLeaveProject.setVisible(true);
                mMenuItemLeaveProject.setTitle(R.string.leave_project);
            } else {
                mMenuItemLeaveProject.setVisible(false);
            }
            if (isProjectCanDelete) {
                mMenuItemCanDeleteProject.setVisible(true);
            } else {
                mMenuItemCanDeleteProject.setVisible(false);
            }
            if (isCategory) {
                mMenuItemCanChangeCategory.setVisible(true);
                mMenuItemCanDeleteCategory.setVisible(true);
            } else {
                mMenuItemCanChangeCategory.setVisible(false);
                mMenuItemCanDeleteCategory.setVisible(false);
            }


            //if (!mMenuItem.getMenuItemType().equals(READY) || !mMenuItem.getMenuItemType().equals(FOR_ME) || (mMenuItem.getMenuItemType().equals(FOR_ME) && mParent != null) || mCheckedProject == null ? false : !mCheckedProject.getCreator().equals(getSettings().getUserName())) {
            if (mMenuItem.getMenuItemType().equals(OVERDUE) || mMenuItem.getMenuItemType().equals(INWORK) || mMenuItem.getMenuItemType().equals(READY) || mMenuItem.getMenuItemType().equals(FOR_ME) || mParent != null ) {
                // если это пункт меню не готово к сдаче
                // если это пункт меню не порученнные мне
                // если это подзадача
                // если это мой проект
                // - то скрываем сортировку
                mMenuItemSortColor.setVisible(false);
                mMenuItemSortTerm.setVisible(false);
                mMenuItemSortCreation.setVisible(false);
            } else {
                if (mCheckedProject != null && !mCheckedProject.getCreator().equals(getSettings().getUserName())) {
                    mMenuItemSortColor.setVisible(false);
                    mMenuItemSortTerm.setVisible(false);
                    mMenuItemSortCreation.setVisible(false);
                } else {
                    mMenuItemSortColor.setVisible(true);
                    mMenuItemSortTerm.setVisible(true);
                    mMenuItemSortCreation.setVisible(true);
                }
            }
        } else {
            if (isSimpleMenu == 1) {
                if (mFooterCheckTask != null) {
                    mFooterCheckTask.setVisibility(View.VISIBLE);
                }
                mMenuInflater.inflate(R.menu.multi_mode_menu, menu);
                boolean no_enabled = false;
                boolean hasTasks = false;
                mPasteIntoYourself = false;
                if (getSettings().getBufferCopyTasks().size() > 0 || getSettings().getBufferCutTasks().size() > 0) {
                    hasTasks = true;
                    ArrayList<String> tasksUids = new ArrayList<>();
                    tasksUids.addAll(getSettings().getBufferCutTasks());
                    tasksUids.addAll(getSettings().getBufferCopyTasks());

                    if (tasksUids.size() > 0) {
                        if ((mMenuItem.getMenuItemType().equals(FOR_ME) && mParent == null)  || mMenuItem.getMenuItemType().equals(UNREAD) || mMenuItem.getMenuItemType().equals(READY) || mMenuItem.getMenuItemType().equals(INWORK) || mMenuItem.getMenuItemType().equals(OVERDUE) || mMenuItem.getMenuItemType().equals(EMP)) {
                            no_enabled = true;
                        }

                        if (mParent != null) {
                            ArrayList<LTask> tasks = getTasksFromUids(tasksUids);
                            for (LTask task : tasks) {
                                if (mParent.getIdTask() == task.getIdTask()) {
                                    mPasteIntoYourself = true;
                                } else {
                                    findAllChildren("" + task.getIdTask());
                                }
                            }
                        }
                    }
                }

                canPaste = (hasTasks && !no_enabled && !mPasteIntoYourself);
            } else {
                if (isSimpleMenu == 2) {
                    /*mMenuInflater.inflate(R.menu.add_mode_menu, menu);
                    mMenu = menu;
                    mMenuCloseAddMode = menu.findItem(R.id.done);*/
                }
            }
        }
    }

    public void clearOptionsMenu(int simpleMenu) {
        isSimpleMenu = simpleMenu;
        if (mMenu != null) {
            onCreateOptionsMenu(mMenu, mMenuInflater);
        }
        if (isSimpleMenu == 0) {
            canSwipeToRefresh = true;
            resetTodayNoTasksContainerVisible();
            resetUnboardindAddTask();
        } else {
            canSwipeToRefresh = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.fragment_tasks_list, container, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (loader.getId() == R.id.lm_sync_info) {
            super.onLoadFinished(loader, c);
        } else {
            // если доделывать - нужно еще добавить в ProcessAll isHasCanceledTaskInUpdate = true
            if (SynchronizationTask.isHasCanceledTaskInUpdate) {
                // не нужно обновлять список задач
            }
            mTasksCount = c.getCount();
            //
            Utils.changeVisibility(mProgressBar, View.GONE);

            mAdapter.swapCursor(c);
            setActionButtonDefault();
            resetActionsUndeaded();
            if (isAddedNewTask) {
                isAddedNewTask = false;
                Toast.makeText(mActivity, getString(R.string.task_added), Toast.LENGTH_SHORT).show(); // тост
                if (LTSettings.getInstance().isAddingTasksToTop()) {
                    mListView.smoothScrollToPosition(0);
                } else {
                    mListView.smoothScrollToPosition(mAdapter.getCount());
                }
            }
            final ContentValues cv = new ContentValues(1);
            cv.put(SyncInfoContract.LIST_STATUS, SyncInfoErrorType.ENDED.ordinal());

            SyncInfo.updateSynchronizationInfo(getApp(), cv);
        }
        resetTodayNoTasksContainerVisible();
        resetUnboardindAddTask();

    }

    private void resetUnboardindAddTask() {
        if (getSettings().isHasAnyTask() || isCheckModeOn) {
            mUnboardingAddTaskContainer.setVisibility(View.GONE);
        } else {
            // если список задач пуст - подгружать картинку со стрелкой на добавить задачу
            if (getSettings().getVerifyKey() == "") { // если триал
                mUnboardingAddTaskContainer.setVisibility(View.VISIBLE);
            }
        }
    }


    private void setActionButtonDefault() {
        if (mActionButton != null) {
            mActionButton.setImageResource(R.drawable.fab_plus_icon);
            mActionButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isPressed = false;
                    isPressedLong = false;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isPressed) {
                                isPressedLong = true;
                                getSettings().setAlreadyHasAnyTasks();
                                addNewTask();
                                resetUnboardindAddTask();
                            }
                        }
                    }, longClickDuration);
                } else {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!isPressedLong) {
                            isPressed = true;
                            mFooterAddTask.setVisibility(View.VISIBLE);
                            mActionButton.setVisibility(View.GONE);
                            // тут создаем новую темповую задачу
                            createNewTaskToAdding();
                            showKeyboard();
                            getSettings().setAlreadyHasAnyTasks();
                            mUnboardingAddTaskContainer.setVisibility(View.GONE);
                            mActivity.swapToolbarModeToAddTasks(true);

                            setupUI(mListView);
                        }
                    }
                }
                return true;
                }
            });


        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { // TODO: 08.06.2018
        super.onCreateContextMenu(menu, v, menuInfo);
        if (!mIsAfterSwipe) {
            mActivity.getMenuInflater().inflate(R.menu.task_item_menu, menu);

            if (!getSettings().getUserName().equals(mTempTask.getEmailCustomer())) {
                if (!getSettings().getUserName().equals(mTempTask.getEmailCustomer()) && mTempTask.getEmailPerformer().equals(mTempTask.getEmailCustomer())) {
                    menu.findItem(R.id.menu_take_on_exec).setVisible(true);
                    menu.findItem(R.id.menu_reassign).setVisible(false);
                } else {
                    menu.findItem(R.id.menu_take_on_exec).setVisible(false);
                }
                menu.findItem(R.id.menu_assign).setVisible(false);
                menu.findItem(R.id.menu_dell).setVisible(false);
            } else {
                menu.findItem(R.id.menu_reassign).setVisible(false);
                menu.findItem(R.id.menu_take_on_exec).setVisible(false);
            }
        } else {
            mActivity.getMenuInflater().inflate(R.menu.task_item_menu_swipe, menu);

            if (getSettings().getUserName().equals(mTempTask.getEmailCustomer()) || getSettings().getUserName().equals(mTempTask.getEmailPerformer())) {
                menu.findItem(R.id.to_today).setVisible(true);
                menu.findItem(R.id.to_tomorrow).setVisible(true);
                menu.findItem(R.id.menu_term).setVisible(true);

            } else {
                menu.findItem(R.id.to_today).setVisible(false);
                menu.findItem(R.id.to_tomorrow).setVisible(false);
                menu.findItem(R.id.menu_term).setVisible(false);
            }

        }
    }

    @Override
    public void onLongClickTask(LTask task, View v, boolean swiping) {
        mIsAfterSwipe = swiping;
        if (!isAddModeOn) {
            if (!isCheckModeOn) {
                mTempTask = task;
                mActivity.openContextMenu(v);
            } else {
                SlidingActivity.mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        SlidingActivity.mSwipeRefreshLayout.setEnabled(false);
                    }
                });
                onClickToAddChecked(task);
            }
        } else {
            addNewTaskMulti();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mParent != null) {
                    getFragmentManager().popBackStack();
                }
                return true;

            case R.id.share_project:
                if (mMenuItem.getMenuItemType().equals(MenuItemType.AVAILABLE_PROJECTS)) {
                    // только смотрим кому расшарен проект
                    ProjectMembersDialog.newInstance(this, mCheckedProject, false).showDialog(getFragmentManager());
                } else {
                    // можем менять тех кому расшарен проект, проект наш
                    ProjectMembersDialog.newInstance(this, mCheckedProject, true).showDialog(getFragmentManager());
                }
                return true;

            case R.id.del_project:
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mActivity.getString(R.string.d_project_remove_title));
                builder.setMessage(mActivity.getString(R.string.d_project_remove_message));
                builder.setNegativeButton(mActivity.getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(mActivity.getString(R.string.txt_just_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setBlockingProcess(true);
                        new Thread(mRemoveRunProject).start();
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;

            case R.id.change_category:
                ChangeCategoryDialog.newInstance(this, mCheckedCategory).showDialog(getFragmentManager());
                return true;

            case R.id.del_category:
                AlertDialog.Builder builderCategoryDel = new AlertDialog.Builder(mActivity);
                builderCategoryDel.setTitle(mActivity.getString(R.string.del_category));
                builderCategoryDel.setMessage(mActivity.getString(R.string.d_category_remove_message));
                builderCategoryDel.setNegativeButton(mActivity.getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderCategoryDel.setPositiveButton(mActivity.getString(R.string.txt_just_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setBlockingProcess(true);
                        new Thread(mRemoveRunCategory).start();
                        dialog.dismiss();
                    }
                });
                builderCategoryDel.create().show();
                return true;

            case R.id.leave_project:
                final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setMessage(getResources().getString(R.string.confirm_leave_project));
                ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //перейти во входящие
                        MenuFragment.lastCheckedMenuItemUUID = null;
                        getSettings().setMenuItem(MenuItemType.TODAY);
                        final Intent intent = new Intent(ACTION_MENU_ITEM);
                        intent.putExtra(MenuFragment.EXTRA_MENU_ITEM, getSettings().getMenuItem());
                        sendLocalBroadcast(intent);

                        // выдрать все свои задачи из проекта в список и все сохранить убрав проект
                        StringBuilder mSb = new StringBuilder();
                        ArrayList <LTask> myTasksFromProject = new ArrayList<>();
                        Cursor cursorTasks = null;
                        try {
                            Utils.clearStringBuilder(mSb);
                            cursorTasks =  mActivity.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.LTaskContract.UidProject, mCheckedProject.getId().toString().toUpperCase()), null, null);
                            if (cursorTasks.getCount() > 0) {
                                while (cursorTasks.moveToNext()) {
                                    myTasksFromProject.add(new LTask(cursorTasks));
                                }
                            }
                        } finally {
                            if (cursorTasks != null) {
                                cursorTasks.close();
                            }
                        }
                        //
                        for (LTask task: myTasksFromProject) {
                            if (task.getEmailCustomer().equals(getSettings().getUserName())) {
                                LTask newTask = task.clone();
                                newTask.setUidProject(null);
                                newTask.setUsnFieldUidProject(newTask.getUsnFieldUidProject() + 1);
                                new TaskSaveHelper(false, getApp(), newTask, false, null, task, 0,//
                                        new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
                            }
                        }
                        //сохранить проект без себя
                        final String[] users = mCheckedProject.getSharedUsers().split(SPLIT_SYMBOL);
                        ArrayList<String> performers = new ArrayList<String>();
                        for (String u : users) {
                            performers.add(u);
                        }

                        performers.remove(performers.indexOf(getSettings().getUserName()));

                        final StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < performers.size(); i++) {
                            stringBuilder.append(performers.get(i));
                            if (i < performers.size() - 1) {
                                stringBuilder.append(DOUBLE_DOTS);
                            }
                        }
                        final String newPerformers = stringBuilder.toString();

                        mCheckedProject.setUsn(0);
                        if (TextUtils.isEmpty(newPerformers)) {
                            mCheckedProject.setSharedUsers(null);
                        } else {
                            mCheckedProject.setSharedUsers(newPerformers);
                        }
                        mCheckedProject.setUsnSharedUsers(mCheckedProject.getUsnSharedUsers()+1);
                        mActivity.leaveProject(mCheckedProject);
                        dialog.dismiss();
                    }
                });
                ad.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
                return true;

            case R.id.follow_project:
                mMenuItemFollowProject.setTitle(mCheckedProject.isQuiet() ? R.string.not_follow : R.string.follow);
                mCheckedProject.setUsn(0);
                mCheckedProject.setQuiet(!mCheckedProject.isQuiet());
                mCheckedProject.setUsnQuiet(mCheckedProject.getUsnQuiet()+1);
                mActivity.saveProject(mCheckedProject);
                return true;

            case R.id.show_hide_make_task:
                Utils.showToast(mActivity, getSettings().isMakeTaskHide() ? R.string.menu_show_make_task : R.string.menu_hide_make_task);
                getSettings().setMakeTaskHide(!getSettings().isMakeTaskHide());
                MenuLoader.getInstance(mActivity).restartLoader();

                getApp().getContentResolver().notifyChange(CalendarDataContract.CONTENT_URI, null);

                makeTaskHideDifferent();
                return true;

            case R.id.sort_color:
                // расставить по цвету
                mTypeSort = 0;
                ArrayList <LTask> mTasksArray = new ArrayList<>();
                Cursor tasksUids = mAdapter.getCursor();
                for (tasksUids.moveToFirst(); !tasksUids.isAfterLast(); tasksUids.moveToNext()) {
                    mTasksArray.add(new LTask(tasksUids));
                }
                sortTasks(mTasksArray);
                Utils.startSync(getApp());
                return true;

            case R.id.sort_date:
                // расставить по дате
                mTypeSort = 1;
                ArrayList <LTask> mTasksArray1 = new ArrayList<>();
                Cursor tasksUids1 = mAdapter.getCursor();
                for (tasksUids1.moveToFirst(); !tasksUids1.isAfterLast(); tasksUids1.moveToNext()) {
                    mTasksArray1.add(new LTask(tasksUids1));
                }
                sortTasks(mTasksArray1);
                Utils.startSync(getApp());
                return true;

            case R.id.sort_date_creation:
               // расставить по дате создания
                mTypeSort = 2;
                ArrayList <LTask> mTasksArray2 = new ArrayList<>();
                Cursor tasksUids2 = mAdapter.getCursor();
                for (tasksUids2.moveToFirst(); !tasksUids2.isAfterLast(); tasksUids2.moveToNext()) {
                    mTasksArray2.add(new LTask(tasksUids2));
                }
                sortTasks(mTasksArray2);
                Utils.startSync(getApp());
                return true;

            case R.id.search_tasks:
                if (getSettings().getLicenseType() == getSettings().LICENSE_TYPE_FREE ||
                        getSettings().getLicenseType() == getSettings().LICENSE_TYPE_NONE){
                    LicenseDialog.newInstance().showDialog(mActivity.getFragmentManager());
                }
                else {
                    startActivity(SearchActivity.newInstance(mActivity));
                }
                return true;

            case R.id.done:
                mActivity.swapToolbarModeToAddTasks(false);
                return true;
            case R.id.multitask_mode:
                openMultiMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable mRemoveRunProject = new Runnable() {
        @Override
        public void run() {
            MenuFragment.lastCheckedMenuItemUUID = null;
            getSettings().setMenuItem(MenuItemType.TODAY);
            final Intent intent = new Intent(ACTION_MENU_ITEM);
            intent.putExtra(MenuFragment.EXTRA_MENU_ITEM, getSettings().getMenuItem());
            sendLocalBroadcast(intent);

            delMyTasks();
            removeProject();
            UpdateFeatureLinkHelper.updateProjectTotalLink(getApp());
            Utils.startSync(getApp());
            setBlockingProcess(false);
        }
    };

    private Runnable mRemoveRunCategory = new Runnable() {
        @Override
        public void run() {
            MenuFragment.lastCheckedMenuItemUUID = null;
            getSettings().setMenuItem(MenuItemType.TODAY);
            final Intent intent = new Intent(ACTION_MENU_ITEM);
            intent.putExtra(MenuFragment.EXTRA_MENU_ITEM, getSettings().getMenuItem());
            sendLocalBroadcast(intent);

            removeCategory();
            Utils.startSync(getApp());
            setBlockingProcess(false);
        }
    };

    private void removeCategory() {
        try {
            updateOrdersToIndentCategory(mCheckedCategory.getParent());

            getApp().getContentResolver().insert(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, UidToDelete.getContentValues(mCheckedCategory));

            getDbHelper().getCategoryDao().delete(mCheckedCategory);


            getApp().getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        UpdateFeatureLinkHelper.deleteTotalLink(getApp(), mCheckedCategory);
    }

    private void delMyTasks() {
        if (mCheckedProject != null) {
            // выдрать все свои задачи из проекта и удалить их
            StringBuilder mSb = new StringBuilder();
            ArrayList<LTask> myTasksFromProject = new ArrayList<>();
            Cursor cursorTasks = null;
            try {
                Utils.clearStringBuilder(mSb);
                cursorTasks = mActivity.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.LTaskContract.UidProject, mCheckedProject.getId().toString().toUpperCase()), null, null);
                if (cursorTasks.getCount() > 0) {
                    while (cursorTasks.moveToNext()) {
                        myTasksFromProject.add(new LTask(cursorTasks));
                    }
                }
            } catch (Exception e) {
            } finally {
                if (cursorTasks != null) {
                    cursorTasks.close();
                }
            }
            //
            for (LTask task : myTasksFromProject) {
                if (task.getEmailCustomer().equals(getSettings().getUserName())) {
                    new TaskDeleteHelper(getApp(), task, true).start();
                }
            }
        }
    }

    private void updateOrdersToIndentProject(Project oldParent) {
        final List<ITreePureNode> projectsAll = SimpleFeatureListAdapter.getListProjects(getSettings(), DbHelper.getInstance(mActivity));
        final List<Project> projects;
        if (oldParent == null) {
            projects = new ArrayList<Project>();
            for (ITreePureNode i : projectsAll) {
                final Project p = (Project) i;
                if (p.getParentId() == null) {
                    projects.add(p);
                }
            }
        } else {
            projects = oldParent.getSubnodes();
        }

        projects.remove(mCheckedProject);

        for (int i = 0; i < projects.size(); i++) {
            final Project p = projects.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        DbHelper.getInstance(mActivity).updateProjects(projects);
    }

    private void updateOrdersToIndentCategory(Category oldParent) {
        final List<ITreePureNode> categoriesAll = SimpleFeatureListAdapter.getListCategories(getSettings(), DbHelper.getInstance(mActivity));
            final List<Category> categories;
            if (oldParent == null) {
                categories = new ArrayList<Category>();
                for (ITreePureNode i : categoriesAll) {
                    final Category p = (Category) i;
                    if (p.getParentId() == null) {
                        categories.add(p);
                    }
                }
            } else {
                categories = oldParent.getSubnodes();
            }

            categories.remove(mCheckedCategory);

            for (int i = 0; i < categories.size(); i++) {
                final Category p = categories.get(i);
                p.setUsnPlusPlus();
                p.setOrder(i + 1);
                p.setUsnOrder(p.getUsnOrder() + 1);
            }

            getDbHelper().updateCategories(categories);
    }

    private void removeProject() {
        try {
            updateOrdersToIndentProject(mCheckedProject.getParent());

            getApp().getContentResolver().insert(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI,
                    UidToDelete.getContentValues(mCheckedProject));
            DbHelper.getInstance(mActivity).getProjectDao().delete(mCheckedProject);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        UpdateFeatureLinkHelper.deleteTotalLink(getApp(), mCheckedProject);

    }

    public void closeTaskAddMode() {
        mEtAddTask.getText().clear();
        Utils.hideInput(mEtAddTask);
        mFooterAddTask.setVisibility(View.GONE);
        //mActionButton.setVisibility(View.VISIBLE);
        if ((mMenuItem.getMenuItemType().equals(FOR_ME) && mParent == null)  || mMenuItem.getMenuItemType().equals(UNREAD) || mMenuItem.getMenuItemType().equals(READY) || mMenuItem.getMenuItemType().equals(INWORK) || mMenuItem.getMenuItemType().equals(OVERDUE)) {
            mActionButton.setVisibility(View.GONE);
        } else {
            mActionButton.setVisibility(View.VISIBLE);
        }
    }

    private void sortTasks(ArrayList <LTask> cTasks) {
        ArrayList <LTask> tasksForSortOld = new ArrayList<>();
        ArrayList <LTask> tasksForSort = new ArrayList<>();

        for (LTask task : cTasks) {
            //если задачи мои
            if (task.getEmailCustomer().equals(getSettings().getUserName())) {
                tasksForSortOld.add(task);
            }
        }
        tasksForSort.addAll(tasksForSortOld);
        //
        //расставляем в правильном порядке
        for (int i = tasksForSort.size() - 1; i >= 0; i--) {
            tasksForSort.get(i).setUsnOrderNew(tasksForSort.get(i).getUsnOrderNew()+1);
            tasksForSort.get(i).setUsnEntity(0);
            for (int j = 0; j < i; j++) {
                switch (mTypeSort) {
                    case 0: // цвет
                        if (tasksForSort.get(j).getMarkerOrder() < tasksForSort.get(j + 1).getMarkerOrder()) {
                            LTask t = tasksForSort.get(j);
                            tasksForSort.set(j, tasksForSort.get(j + 1));
                            tasksForSort.set(j + 1, t);
                        }
                        break;

                    case 1: // срок
                        if (tasksForSort.get(j).getTermBeginCustomer() > tasksForSort.get(j + 1).getTermBeginCustomer()) {
                            LTask t = tasksForSort.get(j);
                            tasksForSort.set(j, tasksForSort.get(j + 1));
                            tasksForSort.set(j + 1, t);
                        }
                        break;

                    case 2: //дата создания
                        if (tasksForSort.get(j).getCreateTime() > tasksForSort.get(j + 1).getCreateTime()) {
                            LTask t = tasksForSort.get(j);
                            tasksForSort.set(j, tasksForSort.get(j + 1));
                            tasksForSort.set(j + 1, t);
                        }
                        break;

                    default:
                        break;
                }

            }
        }

        //выставляем правильный порядок
        for (int i = tasksForSort.size() - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (tasksForSort.get(j).getOrderNew() > tasksForSort.get(j + 1).getOrderNew()) {
                    double m = tasksForSort.get(j).getOrderNew();
                    tasksForSort.get(j).setOrderNew(tasksForSort.get(j + 1).getOrderNew());
                    tasksForSort.get(j + 1).setOrderNew(m);
                }
            }
        }

        for (LTask task: tasksForSort) {
            final ContentValues cvTask = new ContentValues();
            cvTask.put(LionMetaData.LTaskContract.OrderNew, task.getOrderNew());
            cvTask.put(LionMetaData.LTaskContract.UsnOrderNew, task.getUsnOrderNew());
            cvTask.put(LionMetaData.LTaskContract.UsnEntity, task.getUsnEntity());
            mActivity.getContentResolver().update(LionMetaData.LTaskContract.CONTENT_URI, cvTask, LionMetaData.LTaskContract.Uid+" = '"+task.getUid()+"'", null);
            mActivity.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
        }

        for (LTask task : cTasks) {
            sortAllChildren(""+task.getUid()); // проверить наличие детей у всех задач
        }
    }

    private void sortAllChildren(String taskUid) { // задача на проверку родитель ли
        ArrayList <LTask> cTasks = new ArrayList<>();
        Cursor c = null;
        try {
            // нашли все задачи у которых родитель текучая задача
            c = mActivity.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LionMetaData.LTaskContract.UIDParent+" = '"+taskUid+"'", null, null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                LTask task = new LTask(c);
                cTasks.add(task);
            }

            //просортировать всех детей у задачи-родителя
            if (cTasks.size() > 0) {
                sortTasks(cTasks);
            }
            //
        } finally {
            c.close();
        }
    }

    private void findAllChildren(String taskId) {
        Cursor c = null;
        try {
            c = mActivity.getContentResolver().query(LionMetaData.VerticalDepthTaskContract.CONTENT_URI, null, null, null, null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(LionMetaData.VerticalDepthTaskContract._ID));
                String parentId = c.getString(c.getColumnIndex(LionMetaData.VerticalDepthTaskContract.ParentId));
                if (taskId.equals(parentId)) {
                    if ((""+mParent.getIdTask()).equals(id)) {
                        mPasteIntoYourself = true;
                    } else {
                        findAllChildren(id);
                    }
                }
            }
        } finally {
            c.close();
        }
    }

    private void pasteTasks(final ArrayList <LTask> tasks, final boolean isCopy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<LTask> newTasks = new ArrayList<>();
                String performer = null;
                long term = 0;
                String projectId = null;
                String categoryId = null;
                String markerId = null;

                final int[] i = {0};
                for (LTask task : tasks) {
                    newTasks.add(task.clone());
                    if (isCopy) {
                        newTasks.get(i[0]).setUid(UUID.randomUUID().toString().toUpperCase());
                    }
                    i[0]++;
                }

                i[0] = 0;
                for (final LTask task : newTasks) {
                    ////////////////////////////////////////////////////////////////////////////////////////
                    //обнуляем все
                    if (task.getStatus() != TaskStatus.NOTE.getCode()) {
                        task.setStatus(0);
                        task.setUsnFieldStatus(task.getUsnFieldStatus() + 1);
                    }
                    task.setUIDParent(null);
                    task.setUsnEntity(0);
                    task.setReaded(true);
                    task.setPerformerReaded(false);
                    //task.setCategories(null);
                    task.setTermBegin(term);
                    task.setTermBeginCustomer(term);
                    task.setTermEnd(term);
                    task.setTermEndCustomer(term);
                    task.setEmailCustomer(getSettings().getUserName());
                    task.setEmailPerformer(getSettings().getUserName());
                    if (!isCopy) {
                        task.setUidMarker("");
                        //task.setUidProject(null);
                    }
                    TaskSeriesHelper.resetTaskSeries(task, true);
                    //
                    task.setUsnEntity(task.getUsnEntity() + 1);
                    task.setUsnFieldUidParent(task.getUsnFieldUidParent() + 1);
                    task.setUsnFieldReaded(task.getUsnFieldReaded() + 1);
                    task.setUsnFieldPerformerReaded(task.getUsnFieldPerformerReaded() + 1);
                    task.setUsnFieldTerm(task.getUsnFieldTerm() + 1);
                    task.setUsnFieldCustomerTerm(task.getUsnFieldCustomerTerm() + 1);
                    task.setUsnFieldEmailPerformer(task.getUsnFieldPerformerReaded() + 1);
                    if (!isCopy) {

                    }
                    //
                    if (mParent == null) {// если не подзадачи
                        switch (mMenuItem.getMenuItemType()) {
                            case TODAY:
                                term = TimeHelper.currentTimeMillisWithoutTimeZone();
                                break;

                            case CALENDAR_DAY:
                                term = mMenuItem.getUniqueId();
                                break;

                            case UNREAD:
                                task.setReaded(false);
                                break;

                            case BY_ME:
                                performer = mMenuItem.getUid();
                                task.setEmailPerformer(performer);
                                break;

                            case FOR_ME:
                                return;

                            case PROJECTS:
                            case PROJECTS_SHARED:
                            case AVAILABLE_PROJECTS:
                                projectId = mMenuItem.getUid();
                                task.setUidProject(projectId);
                                task.setUsnFieldUidProject(task.getUsnFieldUidProject() + 1);
                                break;

                            case CATEGORIES:
                                categoryId = mMenuItem.getUid();
                                task.setCategories(categoryId);
                                task.setUsnFieldCategories(task.getUsnFieldCategories() + 1);
                                break;

                            case COLOR:
                                markerId = mMenuItem.getUid();
                                task.setUidMarker(markerId.toUpperCase());
                                task.setUsnFieldUidMarker(task.getUsnFieldUidMarker() + 1);
                                break;

                            case INBOX:
                            default:
                                break;
                        }

                    } else {
                        task.setUIDParent(mParent.getUid());
                        task.setUidProject(mParent.getUidProject());
                    }
                    // Сохраняем
                    if (term != 0) {
                        final Calendar calendar = Calendar.getInstance(DEFAULT_TIME_ZONE);
                        calendar.setTimeInMillis(term);

                        TimeHelper.roundCalendar(calendar, false);
                        task.setTermEnd(calendar.getTimeInMillis());

                        TimeHelper.roundCalendar(calendar, true);
                        task.setTermBegin(calendar.getTimeInMillis());
                    }

                    TaskSaveHelper.savingTask = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new TaskSaveHelper(true, getApp(), task, isCopy, new ArrayList<TaskMessage>(), tasks.get(i[0]), 0, copyTaskFiles(UUID.fromString(tasks.get(i[0]).getUid()), UUID.fromString(task.getUid()), isCopy), new ArrayList<TaskFile>(0), false).start();
                            i[0]++;
                        }
                    }).start();

                    while (TaskSaveHelper.savingTask) {
                        try {
                            Thread.sleep(50);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }).start();

        ////////////////////////////////////////////////////////////////////////////////////////

    }

    private List<TaskFile> copyTaskFiles(UUID taskOldUid, UUID taskUid, boolean isCopy) {
        final List<TaskFile> taskFiles;

        List<TaskFile> files = TaskFileCache.getInstance(mActivity).find(taskOldUid.toString().toLowerCase().hashCode());
        taskFiles = new ArrayList<TaskFile>(0);

        if (files == null) {
            files = new ArrayList<TaskFile>(0);
        }

        if ( files.size() == 0 || !isCopy) {
            return files;
        }

        final File appFolder = ((LTApplication) mActivity.getApplicationContext()).getAppFolder();
        int count = 1;


        for (TaskFile file : files) {
            try {
                if (!file.isFileExist()) {
                    // скачать
                    try {
                        new SimpleDownloadFileThread(getApp(), file).start();
                    } finally {
                        Utils.FileWorker.copyFile(file.getFileName(), appFolder, "c_"+file.getFileName());
                    }
                } else {
                    Utils.FileWorker.copyFile(file.getFileName(), appFolder, "c_"+file.getFileName());
                }

            } catch (IOException e) {
                Utils.toLog(e);
            }

            TaskFile tmpFile = new TaskFile(UUID.randomUUID(), taskUid, UUID.randomUUID(), "c_"+file.getFileName(), file.getFileSize(), getSettings().getUserName(), count++ );
            taskFiles.add(tmpFile);
        }
        TaskFileCache.getInstance(mActivity).updateCache(taskFiles);
        return taskFiles;
    }

    private ArrayList <LTask> getTasksFromUids(ArrayList <String> stringArrayUids) {
        ArrayList <LTask> tasks = new ArrayList<>();

        Cursor cursor = null;
        StringBuilder selection = new StringBuilder();

        try {
            for (int i = 0; i < stringArrayUids.size() ; i++) {
                String taskUid = stringArrayUids.get(i);
                StringBuilder sb = new StringBuilder();
                selection.append(LeaderTaskProviderMetaData.SelectionKeeper.equals(sb, LionMetaData.LTaskContract.Uid, taskUid));
                if (i + 1 < stringArrayUids.size()) {
                    selection.append(SharedStrings.OR);
                }
            }
            cursor = getApp().getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, selection.toString(), null, null);
            if (cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    LTask task = new LTask(cursor);
                    tasks.add(task);
                }
            }
        } finally {
            cursor.close();
            return tasks;
        }
    }
    private void openMultiMode() {
        isCheckModeOn = true;
        SlidingActivity activity = (SlidingActivity) mActivity;
        activity.clearCheckedTasks();
        activity.swapToolbarModeToCheck(true);
        if (getSettings().getBufferCopyTasks().isEmpty() && getSettings().getBufferCutTasks().isEmpty()) {
            mBvChildsCount.hide();
        } else {
            if (!getSettings().getBufferCopyTasks().isEmpty()) {
                mBvChildsCount.setText(""+getSettings().getBufferCopyTasks().size());
                mBvChildsCount.show();
            } else {
                if (!getSettings().getBufferCutTasks().isEmpty()) {
                    mBvChildsCount.setText(""+getSettings().getBufferCutTasks().size());
                    mBvChildsCount.show();
                }
            }
        }
        resetTodayNoTasksContainerVisible();
        resetUnboardindAddTask();
    }

    private void closeMultiMode() {
        getSettings().getCheckedTasks().clear();
        isCheckModeOn = false;
        (mActivity).swapToolbarModeToCheck(false);
        resetTodayNoTasksContainerVisible();
        resetUnboardindAddTask();
    }

    private void resetTempTasks() {
        mTempTasks = new ArrayList<>();
        if (getSettings().getCheckedTasks().size() > 0 && getSettings().getCheckedTasks() != null) {
            for (String uuid : getSettings().getCheckedTasks()) {
                Cursor cursor = mAdapter.getCursor();
                final int uid = cursor.getColumnIndex(LionMetaData.LTaskContract.Uid);
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    final String stringUUID = cursor.getString(uid);
                    if (stringUUID.equals(uuid)) {
                        LTask task = new LTask(cursor);
                        mTempTasks.add(task);
                    }
                }
            }
        }
    }

    public void setMarker(LTask task) {
        try {
            Marker color = DbHelper.getInstance(getContext()).getMarkerByUUId(UUID.fromString(task.getUidMarker())) ;
            if(color.getBackColor() != null && !color.getBackColor().equals(Marker.NO_COLOR)) {
                mFastMarkerIc.setImageBitmap(Utils.getColorDrawable(getApp(), color.getBackColor()));
            }
            else {
                mFastMarkerIc.setImageBitmap(Utils.getColorDrawable(getApp(), null));

            }
        }
        catch (Exception e) {
            mFastMarkerIc.setImageBitmap(Utils.getColorDrawable(getApp(), null));
        }
    }

    public void setCategory(LTask task) {
        mFastCategoryIc.setImageResource(R.drawable.category);
    }

    public void setProject(LTask task) {
        String uid = task.getUidProject();
        final ProjectTotalLink project = MenuLoader.getInstance(getContext()).findProjectTask(uid);
        if (project != null) {
            try {
                Project currentProject = DbHelper.getInstance(getContext()).getProjectByUUId(UUID.fromString(uid));
                if(currentProject.getCreator().equals(getSettings().getUserName())) {
                    if (currentProject.getSharedUsers() != null) {
                        mFastProjectIc.setImageResource(R.drawable.project_shared);
                    }
                    else {
                        mFastProjectIc.setImageResource(R.drawable.project);
                    }
                }
                else {
                    mFastProjectIc.setImageResource(R.drawable.project_available);
                }
            }
            catch (Exception e) {
                mFastProjectIc.setImageResource(R.drawable.project_simple);
            }
        }
        else {
            mFastProjectIc.setImageResource(R.drawable.project_simple);
        }
    }

    public void setPerformer(LTask task) {
        final boolean customer = getSettings().getUserName().equals(task.getEmailCustomer());
        final boolean performer = getSettings().getUserName().equals(task.getEmailPerformer());
        //mPerformer.setCustomDrawableToInvisible();
        mFastPerformerIcCustom.setVisibility(View.GONE);
        if (TextUtils.isEmpty(task.getEmailPerformer()) || customer && performer) {
            mFastPerformerIc.setImageResource(R.drawable.emp_simple);

        } else {
            if (customer && !performer) {
                setPerformerImage(task.getEmailPerformer(), 0);

            } else if (!customer && performer) {
                setPerformerImage(task.getEmailCustomer(), 1);

            } else {
                setPerformerImage(task.getEmailCustomer(), 2);
            }
        }
    }

    private void setPerformerImage(String uid, int type) {
        try {
            Emp emp = DbHelper.getInstance(getContext()).getEmpByLogin(uid);
            LTApplication mApp = (LTApplication) getContext().getApplicationContext();
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, emp.getLogin());
            if (roundedBitmapDrawable != null) {
                mFastPerformerIc.setImageDrawable(roundedBitmapDrawable);
                mFastPerformerIcCustom.setVisibility(View.VISIBLE);
                switch (type) {
                    case 0:
                        mFastPerformerIcCustom.setImageResource(R.drawable.emp_circle_from_me);
                        break;

                    case 1:
                        mFastPerformerIcCustom.setImageResource(R.drawable.emp_circle_to_me);
                        break;

                    case 2:
                    default:
                        mFastPerformerIcCustom.setImageResource(R.drawable.emp_circle_simple);
                        break;
                }
            } else {
                switch (type) {
                    case 0:
                        mFastPerformerIc.setImageResource(R.drawable.emp_from_me);
                        break;

                    case 1:
                        mFastPerformerIc.setImageResource(R.drawable.emp_to_me);
                        break;

                    case 2:
                    default:
                        mFastPerformerIc.setImageResource(R.drawable.emp_simple);
                        break;
                }
            }
        }
        catch (Exception e) {
            mFastPerformerIc.setImageResource(R.drawable.emp_simple);
        }
    }

    private void setIconFastAdding (LTask task) {
        String term = TimeHelper.getInstance().taskTermFormatter(task, true, false);
        if (!TextUtils.isEmpty(term)) {
            //есть картинка - есть срок
            mFastTermIc.setImageResource(R.drawable.term_red_big);
        } else {
            // картинки нет - срока нет
            mFastTermIc.setImageResource(R.drawable.term_gray_small);
        }
    }

    public void showKeyboard() {
        if (mTempAddTask != null) {
            mEtAddTask.requestFocus();
            Utils.showInput(mEtAddTask);
        }
    }

    @Override
    public void onReceivingObjects(int code, Object... objects) {
        switch (code) {
            case ProjectMembersDialog.CODE:
                final String newPerformers = (String) objects[0];
                //
                mCheckedProject.setUsn(0);
                if (TextUtils.isEmpty(newPerformers)) {
                    mCheckedProject.setSharedUsers(null);
                } else {
                    mCheckedProject.setSharedUsers(newPerformers);
                }
                mCheckedProject.setUsnSharedUsers(mCheckedProject.getUsnSharedUsers()+1);
                mActivity.saveProject(mCheckedProject);
                break;

            case TaskStatusDialog.CODE:
                if (mTempTask != null) {
                    final int status = (int) objects[0];
                    if (status != mTempTask.getStatus()) {
                        new TaskStatusAnimationChanger(getApp(), mTempTask, mTempIv, TaskStatus.getTaskStatus(status)).start();
                    }
                    mTempTask = null;
                    mTempIv = null;
                }
                break;

            case TaskTermDialog.CODE2:
                if (mTempAddTask != null) {
                    final LTask task = (LTask) objects[0];
                    if (!TimeHelper.termsEquals(task, mTempAddTask)) {
                        mTempAddTask.setTermBegin(task.getTermBegin());
                        mTempAddTask.setTermEnd(task.getTermEnd());

                        mTempAddTask.setTermBeginCustomer(task.getTermBeginCustomer());
                        mTempAddTask.setTermEndCustomer(task.getTermEndCustomer());

                        mTempAddTask.setUsnFieldTerm(task.getUsnFieldTerm() + 1);
                        mTempAddTask.setUsnFieldCustomerTerm(task.getUsnFieldCustomerTerm() + 1);

                        setIconFastAdding(mTempAddTask);

                    }
                }
                break;

            case TaskTermDialogNew.CODE:
                MenuLoader.getInstance(getApp()).resetCalendar();
                if (mTempTask != null) {
                    final LTask task = (LTask) objects[0];
                    if (!TimeHelper.termsEquals(task, mTempTask)) {
                        final LTask taskOld = mTempTask.clone();

                        mTempTask.setTermBegin(task.getTermBegin());
                        mTempTask.setTermEnd(task.getTermEnd());

                        mTempTask.setTermBeginCustomer(task.getTermBeginCustomer());
                        mTempTask.setTermEndCustomer(task.getTermEndCustomer());

                        mTempTask.setUsnFieldTerm(task.getUsnFieldTerm() + 1);
                        mTempTask.setUsnFieldCustomerTerm(task.getUsnFieldCustomerTerm() + 1);

                        saveTask(mTempTask, taskOld);
                    }
                    mTempTask = null;
                }
                break;

            case MultiTaskPerformerDialog.CODE:
                final String performerMulti = (String) objects[0];
                for (LTask oldTask : mTempTasks) {
                    if (!Utils.equals(performerMulti, oldTask.getEmailPerformer())) {
                        LTask task = oldTask.clone();
                        task.setEmailPerformer(TextUtils.isEmpty(performerMulti) ? null : performerMulti);
                        task.setUsnFieldEmailPerformer(task.getUsnFieldEmailPerformer() + 1);
                        saveTaskMulti(task, oldTask);
                    }
                }
                break;

            case MultiTasksProjectDialog.CODE:
                final Project project = (Project) objects[0];
                final String uidProject = project == null ? null : String.valueOf(project.getId()).toUpperCase();
                for (LTask oldTask : mTempTasks) {
                    if (!Utils.equals(uidProject, oldTask.getUidProject())) {
                        LTask task = oldTask.clone();
                        task.setUidProject(uidProject);
                        task.setUsnFieldUidProject(task.getUsnFieldUidProject() + 1);
                        saveTaskMulti(task, oldTask);
                    }
                }
                break;

            case MultiTasksCategoriesDialog.CODE:
                final String categories = (String) objects[0];
                for (LTask oldTask : mTempTasks) {
                    if (!Utils.equals(categories, oldTask.getUidProject())) {
                        LTask task = oldTask.clone();
                        task.setCategories(TextUtils.isEmpty(categories) ? null : categories);
                        task.setUsnFieldCategories(task.getUsnFieldCategories() + 1);
                        saveTaskMulti(task, oldTask);
                    }
                }
                break;

            case MultiTasksTermDialog.CODE:
                MenuLoader.getInstance(getApp()).resetCalendar();
                final ArrayList <LTask> tasks = (ArrayList <LTask>) objects[0];
                for (LTask task : tasks) {
                    for (LTask oldTask : mTempTasks) {
                        if (task.getUid().equals(oldTask.getUid())) {
                            if (!TimeHelper.termsEquals(task, oldTask)) {
                                saveTaskMulti(task, oldTask);
                            }
                        }
                    }
                }

                break;

            case TaskMarkerDialog.CODE:
                final String marker = (String) objects[0];
                if (!Utils.equals(marker, mTempAddTask.getUidMarker())) {
                    mTempAddTask.setUidMarker(marker);
                    mTempAddTask.setUsnFieldUidMarker(mTempAddTask.getUsnFieldUidMarker() + 1);
                    mTempAddTask.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(getApp(), marker));

                    setMarker(mTempAddTask);
                }
                break;

            case TaskCategoriesDialog.CODE:
                final String categoriesTasks = (String) objects[0];
                if (!Utils.equals(categoriesTasks, mTempAddTask.getUidProject())) {
                    mTempAddTask.setCategories(TextUtils.isEmpty(categoriesTasks) ? null : categoriesTasks);
                    mTempAddTask.setUsnFieldCategories(mTempAddTask.getUsnFieldCategories() + 1);
                }
                break;

            case TaskProjectDialog.CODE2:
                final Project projectFast = (Project) objects[0];
                final String uidprojectFast = projectFast == null ? null : String.valueOf(projectFast.getId()).toUpperCase();

                if (!Utils.equals(uidprojectFast, mTempAddTask.getUidProject())) {
                    mTempAddTask.setUidProject(uidprojectFast);
                    mTempAddTask.setUsnFieldUidProject(mTempAddTask.getUsnFieldUidProject() + 1);

                    setProject(mTempAddTask);
                }

                break;

            case TaskPerformerDialog.CODE2:
                if (mTempAddTask != null) {
                    final String performer = (String) objects[0];
                    if (!performer.equalsIgnoreCase(mTempAddTask.getEmailPerformer())) {
                        mTempAddTask.setEmailPerformer(performer.toLowerCase());
                        mTempAddTask.setUsnFieldEmailPerformer(mTempAddTask.getUsnFieldEmailPerformer() + 1);

                        mTempAddTask.setPerformTime(System.currentTimeMillis());
                        mTempAddTask.setUsnFieldPerformtime(mTempAddTask.getUsnFieldPerformtime() + 1);

                        setPerformer(mTempAddTask);
                    }
                }
                break;

            case TaskPerformerDialog.CODE:
                final String performer = (String) objects[0];
                if (mReAssign) {
                    //
                    LTask newTask = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), performer.toLowerCase(), 0, mTempTask.getUid(), mTempTask.getUidProject(), mTempTask.getCategories(), mTempTask.getUidMarker());
                    newTask.setName(mTempTask.getName());
                    newTask.setComment(mTempTask.getComment());
                    //copyTaskFiles(UUID.fromString(mTempTask.getUid()) ,UUID.fromString(newTask.getUid()), true);
                    startActivity(EditTaskActivity.newInstance(mActivity, newTask, true, false));
                    //
                } else {
                    if (!performer.equalsIgnoreCase(mTempTask.getEmailPerformer())) {
                        final LTask taskOld = mTempTask.clone();

                        mTempTask.setEmailPerformer(performer.toLowerCase());
                        mTempTask.setUsnFieldEmailPerformer(mTempTask.getUsnFieldEmailPerformer() + 1);

                        mTempTask.setPerformTime(System.currentTimeMillis());
                        mTempTask.setUsnFieldPerformtime(mTempTask.getUsnFieldPerformtime() + 1);

                        saveTask(mTempTask, taskOld);
                    }
                }
                mTempTask = null;

            break;

            case CalendarDialog.CODE:
                final Date date = (Date) objects[0];
                if (date != null) {
                    final BaseMenuItem item;
                    if (TimeHelper.getInstance().isToday(date.getTime())) {
                        item = MenuItemType.TODAY;

                    } else {
                        item = new CalendarMenuItem(date.getTime());
                    }

                    final Intent intent = new Intent(ACTION_CALENDAR_ITEM);
                    intent.putExtra(MenuFragment.EXTRA_MENU_ITEM, item);
                    sendLocalBroadcast(intent);
                }
                break;

            case TaskEmailsDialog.CODE:
                final String emails = (String) objects[0];
                mTempAddTask.setEmails(TextUtils.isEmpty(emails) ? null : emails);
                mTempAddTask.setUsnFieldListMembers(mTempAddTask.getUsnFieldListMembers() + 1);
                break;

            default:
                super.onReceivingObjects(code, objects);
                break;
        }
    }

    private void saveTask(LTask taskNew, LTask taskOld) {
        new TaskSaveHelper(false, getApp(), taskNew, false, null, taskOld, 0,//
                new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
    }

    private void saveTaskMulti(final LTask taskNew, final LTask taskOld) {
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
                TaskSaveHelper.savingTask = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new TaskSaveHelper(false, getApp(), taskNew, false, null, taskOld, 0,//
                                new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
                    }
                }).start();
                while (TaskSaveHelper.savingTask) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {

                    }
                }
            /*}
        }).start();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        setSwipeRefreshLayoutRefresh(false);
        if (SynchronizationTask.isSwipeSync) {
            setRefreshing(getApp().isSync());
        }
        hasParent = mBundle.getSerializable(EXTRA_PARENT_TASK) != null ? true : false;

        getActivity().registerReceiver(animationComplete,  new IntentFilter(TaskStatusAnimationChanger.COMPLETE_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(animationComplete);
    }

    BroadcastReceiver animationComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (b != null) {

            b.putSerializable(EXTRA_MENU_ITEM, mMenuItem);
            b.putSerializable(EXTRA_PARENT_TASK, mParent);
            b.putBoolean(EXTRA_MAKE_TASK_HIDE, mMakeTaskHide);
            b.putSerializable(EXTRA_TEMP_TASK, mTempTask);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setActionBar();
        try {
            if (mParent == null) {
                if (!Utils.isLandOrientation(getApp())) {
                    if (SlidingActivity.mSlidingMenu != null)
                        SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.LEFT); // свайп
//                        SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); // свайп
                }
                //нет родителя, можно скролится, кнопка сендвич
                if (Utils.isLandOrientation(getApp())) {

                    mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    mActivity.getSupportActionBar().setHomeButtonEnabled(false);
                } else {
                    mActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
                    mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mActivity.getSupportActionBar().setHomeButtonEnabled(true);
                }
            } else {
                //есть родитель, отключаем навигатор, меняем кнопку в екшнбаше на "назад"
                if (!Utils.isLandOrientation(getApp())) {
                    if (SlidingActivity.mSlidingMenu != null)
                        SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE); // нет свайпа
                }
                mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mActivity.getSupportActionBar().setHomeButtonEnabled(true);
                mActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onSyncStatusChange(SyncInfo si) {
        switch (si.getListStatus()) {
            case NONE:
                final ContentValues cv = new ContentValues(1);
                cv.put(SyncInfoContract.LIST_STATUS, SyncInfoErrorType.IN_PROGRESS.ordinal());

                SyncInfo.updateSynchronizationInfo(getApp(), cv);
                break;

            default:
                break;
        }
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);

        makeTaskHideDifferent();

        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);

        mListView = (ListView) v.findViewById(R.id.listTasks);
        mFooterAddTask = (LinearLayout) v.findViewById(R.id.add_task_footer);
        mFooterCheckTask = (LinearLayout) v.findViewById(R.id.check_tasks_footer);
        mFooterCheckTaskSet = (RelativeLayout) v.findViewById(R.id.check_tasks_footer_set);
        mFooterCheckTaskBuffer = (RelativeLayout) v.findViewById(R.id.check_tasks_footer_buffer);
        mFooterCheckTaskDel = (LinearLayout) v.findViewById(R.id.check_tasks_footer_del);
        mEtAddTask = (CustomEditTextNewNew) v.findViewById(R.id.et_add_task);
        //
        mBvChildsCount = new BadgeView(getContext(), mFooterCheckTaskBuffer);
        mBvChildsCount.setTextColor(Color.WHITE);
        mBvChildsCount.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
        final int paddingH = getResources().getDimensionPixelSize(R.dimen.univ_padding_small);
        final int paddingB = getResources().getDimensionPixelSize(R.dimen.univ_padding_tiny);
        final int tsBadge = getResources().getDimensionPixelSize(R.dimen.text_size_less);
        mBvChildsCount.setPadding(paddingH, 0, paddingH, paddingB);
        mBvChildsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, tsBadge);
        mBvChildsCount.setBadgeBackgroundColor(BADGE_GREEN_COLOR);
        //

        mFastTerm = (RelativeLayout) v.findViewById(R.id.add_mode_term_task);
        mFastTermIc = (ImageView) v.findViewById(R.id.add_mode_term_task_ic);
        mFastPerformer = (RelativeLayout) v.findViewById(R.id.add_mode_emp_task);
        mFastPerformerIc = (ImageView) v.findViewById(R.id.add_mode_image_view);
        mFastPerformerIcCustom = (ImageView) v.findViewById(R.id.add_mode_iv_img_custom);
        mFastProject = (RelativeLayout) v.findViewById(R.id.add_mode_project_task);
        mFastProjectIc = (ImageView) v.findViewById(R.id.add_mode_project_ic);
        mFastMarker = (RelativeLayout) v.findViewById(R.id.add_mode_marker_task);
        mFastMarkerIc = (ImageView) v.findViewById(R.id.add_mode_marker_ic);
        mFastCategory = (RelativeLayout) v.findViewById(R.id.add_mode_category_task);
        mFastCategoryIc = (ImageView) v.findViewById(R.id.add_mode_category_ic);
        mFastEmails = v.findViewById(R.id.add_mode_emails_task);

        v.findViewById(R.id.save_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTaskMulti();
            }
        });

        mFastEmails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskEmailsDialog.newInstance(TasksFragment.this, mTempAddTask).showDialog(getFragmentManager());
            }
        });

        mFastTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskTermDialog.newInstance(TasksFragment.this, mTempAddTask, true).showDialog(getFragmentManager());
            }
        });

        mFastPerformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskPerformerDialog.newInstance(TasksFragment.this, mTempAddTask, true).showDialog(getFragmentManager());
            }
        });

        mFastProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskProjectDialog.newInstance(TasksFragment.this, mTempAddTask, true).showDialog(getFragmentManager());
            }
        });

        mFastMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskMarkerDialog.newInstance(TasksFragment.this, mTempAddTask.getUidMarker()).showDialog(getFragmentManager());
            }
        });

        mFastCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskCategoriesDialog.newInstance(TasksFragment.this, mTempAddTask).showDialog(getFragmentManager());
            }
        });

        mFooterCheckTaskBuffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList <String> items = new ArrayList<>();
                if (getSettings().getCheckedTasks().size() > 0 && canCut) {
                    items.add(getString(R.string.cut));
                }
                if (getSettings().getCheckedTasks().size() > 0) {
                    items.add(getString(R.string.copy));
                    items.add(getString(R.string.copy_link));
                }
                if (canPaste) {
                    items.add(getString(R.string.paste));
                }

                final String[] itemsS = new String[items.size()];
                int i=0;
                for (String s: items) {
                    itemsS[i] = s;
                    i++;
                }

                if (!items.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setItems(itemsS, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (items.get(item).equals(getString(R.string.cut))) {
                                getSettings().getBufferCopyTasks().clear();
                                getSettings().getBufferCutTasks().clear();
                                if (getSettings().getCheckedTasks().size() > 0) {
                                    getSettings().getBufferCutTasks().addAll(getSettings().getCheckedTasks());
                                    Utils.showToast(mActivity, getResources().getString(R.string.saved_clipboard));
                                    closeMultiMode();
                                }
                            } else {
                                if (items.get(item).equals(getString(R.string.copy))) {
                                    getSettings().getBufferCopyTasks().clear();
                                    getSettings().getBufferCutTasks().clear();

                                    if (getSettings().getCheckedTasks().size() > 0) {
                                        getSettings().getBufferCopyTasks().addAll(getSettings().getCheckedTasks());
                                        Utils.showToast(mActivity, getResources().getString(R.string.saved_clipboard));
                                        closeMultiMode();
                                    }
                                } else {
                                    if (items.get(item).equals(getString(R.string.copy_link))) {
                                        if (getSettings().getCheckedTasks().size() > 0) {
                                            // скопировать все ссылки
                                            StringBuilder sb = new StringBuilder();
                                            for (String taskUid : getSettings().getCheckedTasks()) {
                                                sb.append("lt://planning?{" + taskUid + "}\n");
                                            }
                                            ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(mActivity.CLIPBOARD_SERVICE);
                                            ClipData clip = ClipData.newPlainText("label", sb.toString());
                                            clipboard.setPrimaryClip(clip);
                                            Utils.showToast(mActivity, getResources().getString(R.string.saved_clipboard));
                                            //
                                            closeMultiMode();
                                        }
                                    } else {
                                        if (items.get(item).equals(getString(R.string.paste))) {
                                            if (getSettings().getBufferCopyTasks().size() > 0) {
                                                //вставляем скопированное
                                                pasteTasks(getTasksFromUids(getSettings().getBufferCopyTasks()), true);
                                                //
                                                getSettings().getBufferCopyTasks().clear();
                                                closeMultiMode();
                                            } else if (getSettings().getBufferCutTasks().size() > 0) {
                                                //вставляем вырезанное
                                                pasteTasks(getTasksFromUids(getSettings().getBufferCutTasks()), false);
                                                //
                                                getSettings().getBufferCutTasks().clear();
                                                closeMultiMode();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });

                    builder.setCancelable(true);
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Utils.showToast(getActivity(), getString(R.string.add_checked_tasks));
                }

            }
        });

        //
        mFooterCheckTaskSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList <String> items = new ArrayList<>();

                if (getSettings().getCheckedTasks().size() > 0) {
                    items.add(getString(R.string.set_as_read));
                    items.add(getString(R.string.task_term));
                }
                if (getSettings().getCheckedTasks().size() > 0 && canSetForCustomer) {
                    items.add(getString(R.string.task_assign));
                    items.add(getString(R.string.task_project));
                    items.add(getString(R.string.task_category));
                }


                final String[] itemsS = new String[items.size()];
                int i=0;
                for (String s: items) {
                    itemsS[i] = s;
                    i++;
                }
                if (!items.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setItems(itemsS, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (items.get(item).equals(getString(R.string.set_as_read))) {
                                //выбранные делаем прочитанными
                                resetTempTasks();
                                for (LTask oldTask : mTempTasks) {
                                    if (!oldTask.getReaded()) {
                                        LTask newTask = oldTask.clone();
                                        newTask.setReaded(true);
                                        newTask.setUsnFieldReaded(newTask.getUsnFieldReaded() + 1);
                                        newTask.setUsnEntity(0);
                                        saveTask(newTask, oldTask);
                                        //
                                        if (!oldTask.getReaded()) {
                                            new TaskFootstepHelper(getApp()).changeTotalUnreadedAndApply(newTask, false, false);
                                        }
                                    }
                                }
                                closeMultiMode();
                            } else {
                                if (items.get(item).equals(getString(R.string.task_term))) {
                                    resetTempTasks();
                                    ArrayList <LTask> tasks = new ArrayList<>();
                                    if (getSettings().getCheckedTasks().size() > 0 && getSettings().getCheckedTasks() != null) {
                                        for (String uuid : getSettings().getCheckedTasks()) {
                                            Cursor cursor = mAdapter.getCursor();
                                            final int uid = cursor.getColumnIndex(LionMetaData.LTaskContract.Uid);
                                            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                                final String stringUUID = cursor.getString(uid);
                                                if (stringUUID.equals(uuid)) {
                                                    LTask task = new LTask(cursor);
                                                    tasks.add(task);
                                                }
                                            }
                                        }
                                        MultiTasksTermDialog.newInstance(TasksFragment.this, tasks).showDialog(getFragmentManager());
                                        closeMultiMode();
                                    }
                                } else {
                                    if (items.get(item).equals(getString(R.string.task_assign))) {
                                        resetTempTasks();
                                        if (getSettings().getCheckedTasks().size() > 0 && getSettings().getCheckedTasks() != null) {
                                            MultiTaskPerformerDialog.newInstance(TasksFragment.this).showDialog(getFragmentManager());
                                            closeMultiMode();
                                        }
                                    } else {
                                        if (items.get(item).equals(getString(R.string.task_project))) {
                                            resetTempTasks();
                                            if (getSettings().getCheckedTasks().size() > 0 && getSettings().getCheckedTasks() != null) {
                                                MultiTasksProjectDialog.newInstance(TasksFragment.this).showDialog(getFragmentManager());
                                                closeMultiMode();
                                            }
                                        } else {
                                            if (items.get(item).equals(getString(R.string.task_category))) {
                                                resetTempTasks();
                                                if (getSettings().getCheckedTasks().size() > 0 && getSettings().getCheckedTasks() != null) {
                                                    MultiTasksCategoriesDialog.newInstance(TasksFragment.this).showDialog(getFragmentManager());
                                                    closeMultiMode();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    builder.setCancelable(true);
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Utils.showToast(getActivity(), getString(R.string.add_checked_tasks));
                }
            }
        });

        mFooterCheckTaskDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Удаляем выбранные
                if (getSettings().getCheckedTasks().size() > 0 && canSetForCustomer) {
                    Utils.getSimpleDialog(getActivity(), getDeleteDialogListener2(), R.string.confirm_delete_title, R.string.confirm_delete_text_mass);
                }
            }
        });
        mFooterCheckTaskDel.setVisibility(View.GONE);
        //

        mEtAddTask.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mEtAddTask.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mEtAddTask.setListener(this);
        mEtAddTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                //if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addNewTaskMulti();
                    handled = true;
                //}
                return handled;
            }
        });


        @SuppressLint("RestrictedApi") FrameLayout footerLayout = (FrameLayout) getLayoutInflater(b).inflate(R.layout.footer_view, null);
        mUnboardingAddTaskContainer = (RelativeLayout) v.findViewById(R.id.unboarding_add_task_container);
        mNoTasksContainer = (LinearLayout) v.findViewById(R.id.container_no_tasks);
        mUnboardingAddTaskContainer.setVisibility(View.GONE);
        @SuppressLint("RestrictedApi") FrameLayout footerDivider = (FrameLayout) getLayoutInflater(b).inflate(R.layout.footer_divider, null);
        mListView.addFooterView(footerDivider, null, true);
        mListView.addFooterView(footerLayout, null, false);
        mListView.setAdapter(mAdapter);
        mListView.setBackgroundColor(getSettings().isThemeDark() ? Color.BLACK : Color.WHITE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH) {
            mListView.setFooterDividersEnabled(false);
        }
        registerForContextMenu(mListView);

        if (mParent != null) {
            mListView.setOnTouchListener(new TouchEvent(getFragmentManager()));
        } else {
            mListView.setOnTouchListener(new TouchEvent2(getFragmentManager()));
        }


        setSwipeRefreshLayoutEnabled();

        final int topRowVerticalPosition = (mListView == null || mListView.getChildCount() == 0) ?
                0 : mListView.getChildAt(0).getTop();
        canSwipeToRefresh = (topRowVerticalPosition >= 0 && mListView.getFirstVisiblePosition() == 0);

        if (mInitAfterCreation) {
            mInitAfterCreation = false;
            getLoaderManager().initLoader(R.id.lm_tasks_loader, null, this);
        }
        mActionButton = (ActionButton) v.findViewById(R.id.action_button);
        if ((mMenuItem.getMenuItemType().equals(FOR_ME) && mParent == null)  || mMenuItem.getMenuItemType().equals(UNREAD) || mMenuItem.getMenuItemType().equals(READY) || mMenuItem.getMenuItemType().equals(INWORK) || mMenuItem.getMenuItemType().equals(OVERDUE)) {
            mActionButton.setVisibility(View.GONE);
        } else {
            mActionButton.setVisibility(View.VISIBLE);
        }
        mActionUnreadTop = (ActionButton) v.findViewById(R.id.action_button_unreaded_top);
        mActionUnreadBottom = (ActionButton) v.findViewById(R.id.action_button_unreaded_bottom);
    }

    public void setupUI(View view) {
        if (isAddModeOn) {
            // Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof CustomEditTextNewNew)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        mListView.setFocusable(false);
                        mListView.setFocusableInTouchMode(false);
                        if (isAddModeOn) {
                            addNewTaskMulti();

                            mListView.setFocusable(false);
                            mListView.setFocusableInTouchMode(false);
                        }
                        return false;
                    }
                });
            }

            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);

                    setupUI(innerView);
                }
            }
        } else {
            //if (mParent != null) {
                mListView.setOnTouchListener(new TouchEvent(getFragmentManager()));
            /*} else {
                mListView.setOnTouchListener(new TouchEvent2(getFragmentManager()));
            }*/
            mListView.setFocusable(true);
            mListView.setFocusableInTouchMode(true);
        }
    }

    private void resetTodayNoTasksContainerVisible() {
        if (mParent == null && mMenuItem.getMenuItemType().equals(MenuItemType.TODAY) && !isCheckModeOn) {
            if (!LeaderTaskSyncService.checkToday(getApp())) {
                // если задач нет - выводить
                mNoTasksContainer.setVisibility(View.VISIBLE);
            } else {
                // есть есть то не показываем
                mNoTasksContainer.setVisibility(View.GONE);
            }
        } else {
            mNoTasksContainer.setVisibility(View.GONE);
        }
    }

    private void addNewTaskMulti() {
        String taskName =  mEtAddTask.getText().toString().trim();
        mEtAddTask.getText().clear();
        if (taskName.length() > 0) {
            if (mTempAddTask != null) {
                final LTask task = mTempAddTask.clone();
                createNewTaskToAdding();
                //startActivity(EditTaskActivity.newInstance(mActivity, task, true));
                task.setName(taskName);
                new TaskSaveHelper(false, getApp(), task, true, null, null, 0, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
            }
            mActivity.swapToolbarModeToAddTasks(false);
        } else {
            //mNowEditTask = false;
            mActivity.swapToolbarModeToAddTasks(false);
            //Toast.makeText(mActivity, getString(R.string.error_empty_task_title), Toast.LENGTH_SHORT).show();
        }

        setupUI(mListView);
    }

    private void addNewTask() {
        String performer = null;
        long term = 0;
        String parentId = null;
        String projectId = null;
        String categoryId = null;
        String colorId = null;

        if (mParent == null) {
            switch (mMenuItem.getMenuItemType()) {
                case CALENDAR_DAY:
                    term = mMenuItem.getUniqueId();
                    break;

                case TODAY:
                    term = TimeHelper.currentTimeMillisWithoutTimeZone();
                    break;

                case BY_ME:
                case EMP:
                    performer = mMenuItem.getUid();
                    break;

                case PROJECTS:
                case PROJECTS_SHARED:
                case AVAILABLE_PROJECTS:
                    projectId = mMenuItem.getUid();
                    break;

                case CATEGORIES:
                    categoryId = mMenuItem.getUid();
                    break;

                case COLOR:
                    colorId = mMenuItem.getUid();
                    break;

                default:
                    break;
            }

        } else {
            parentId = mParent.getUid();
            switch (mMenuItem.getMenuItemType()) {
                case BY_ME:
                case EMP:
                    performer = mMenuItem.getUid();
                    break;

                default:
                    break;
            }

            projectId = mParent.getUidProject();
        }

        final LTask task = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), performer, term, parentId, projectId, categoryId, colorId);
        startActivity(EditTaskActivity.newInstance(mActivity, task, true, false));
    }

    private void createNewTaskToAdding() {
        String performer = null;
        long term = 0;
        String parentId = null;
        String projectId = null;
        String categoryId = null;
        String colorId = null;

        if (mParent == null) {
            switch (mMenuItem.getMenuItemType()) {
                case CALENDAR_DAY:
                    term = mMenuItem.getUniqueId();
                    break;

                case TODAY:
                    term = TimeHelper.currentTimeMillisWithoutTimeZone();
                    break;

                case BY_ME:
                case EMP:
                    performer = mMenuItem.getUid();
                    break;

                case PROJECTS:
                case PROJECTS_SHARED:
                case AVAILABLE_PROJECTS:
                    projectId = mMenuItem.getUid();
                    break;

                case CATEGORIES:
                    categoryId = mMenuItem.getUid();
                    break;

                case COLOR:
                    colorId = mMenuItem.getUid();
                    break;

                default:
                    break;
            }

        } else {
            parentId = mParent.getUid();
            switch (mMenuItem.getMenuItemType()) {
                case BY_ME:
                case EMP:
                    performer = mMenuItem.getUid();
                    break;

                default:
                    break;
            }

            projectId = mParent.getUidProject();
        }

        mTempAddTask = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), performer, term, parentId, projectId, categoryId, colorId);

        //mTempAddTask.setEmailPerformer(getSettings().getUserName());
        //mTempAddTask.setEmailCustomer(getSettings().getUserName());

        setIconFastAdding(mTempAddTask);
        setPerformer(mTempAddTask);
        setProject(mTempAddTask);
        setMarker(mTempAddTask);
        setCategory(mTempAddTask);
    }

    private DialogInterface.OnClickListener getDeleteDialogListener2() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {*/
                            final ArrayList <String> tasksUIDS = new ArrayList<>();
                            tasksUIDS.addAll(getSettings().getCheckedTasks());
                            for (String uuid : tasksUIDS) {
                                Cursor cursor = TasksFragment.mAdapter.getCursor();
                                final int uid = cursor.getColumnIndex(LionMetaData.LTaskContract.Uid);
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    final String stringUUID = cursor.getString(uid);
                                    if (stringUUID.equals(uuid)) {
                                        final LTask task = new LTask(cursor);
                                        TaskDeleteHelper.deletingTask = true;
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new TaskDeleteHelper(getApp(), task, true).start();
                                            }
                                        }).start();
                                        while (TaskDeleteHelper.deletingTask) {
                                            try {
                                                Thread.sleep(50);
                                            } catch (Exception e) {

                                            }
                                        }
                                    }
                                }
                            }

                            closeMultiMode();

                       /* }
                    }).start();*/

                }
            }
        };
    }

    private DialogInterface.OnClickListener getDeleteDialogListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    new TaskDeleteHelper(getApp(), mTempTask, true).start();
                    mTempTask = null;
                }
            }
        };
    }

    private void makeTaskHideDifferent() {
        if (mMakeTaskHide != getSettings().isMakeTaskHide()) {
            mMakeTaskHide = getSettings().isMakeTaskHide();
            getLoaderManager().restartLoader(R.id.lm_tasks_loader, null, this);

            if (mMenuItemShowMakeTask != null) {
                mMenuItemShowMakeTask.setTitle(//
                        getSettings().isMakeTaskHide() ? R.string.menu_show_make_task : R.string.menu_hide_make_task);
            }
        }
    }

    private void setActionBar() {
        if (mParent == null) {
            final String title;
            switch (mMenuItem.getMenuItemType()) {
                case CALENDAR_DAY:
                    setDateTitle(mMenuItem.getUniqueId());
                    return;

                case TODAY:
                    setDateTitle2(new Date(System.currentTimeMillis()));
                    return;

                case INBOX:
                    title = getString(mMenuItem.getMenuItemType().getNameId());
                    break;

                case UNREAD:
                    title = getString(mMenuItem.getMenuItemType().getNameId());
                    break;

                case READY:
                    title = getString(mMenuItem.getMenuItemType().getNameId());
                    break;

                case INWORK:
                    title = getString(mMenuItem.getMenuItemType().getNameId());
                    break;

                case OVERDUE:
                    title = getString(mMenuItem.getMenuItemType().getNameId());
                    break;

                case FOCUS:
                    title = getString(mMenuItem.getMenuItemType().getNameId());
                    break;

                case EMAILS:
                    title = getString(mMenuItem.getMenuItemType().getNameId());
                    break;

                case BY_ME:
                case FOR_ME:
                case PROJECTS:
                case PROJECTS_SHARED:
                case AVAILABLE_PROJECTS:
                case CATEGORIES:
                case COLOR:
                case EMP:
                    title = mMenuItem.getName();
                    break;

                default:
                    return;
            }

            setNeedSetProjectEditFunctions();
            (mActivity).setActionBarTitle(title, isNeedSetProjectEditFunctions, mCheckedProject);

        } else {
            setNeedSetProjectEditFunctions();
            (mActivity).setActionBarTitle(mParent.getName(), false, mCheckedProject);
        }
    }

    private void setDateTitle(long time) {
        // это для вчера и завтра. не для сегодня
        // ошибки
        final View v = LayoutInflater.from(mActivity).inflate(R.layout.custom_title, null);
        v.setId(R.id.action_bar);
        Calendar calendar = Calendar.getInstance(DEFAULT_TIME_ZONE);
        calendar.setTimeInMillis(time);
        //calendar.add(Calendar.HOUR, new Date().getTimezoneOffset() / 60);


        ((ImageView) v.findViewById(R.id.triangle)).setVisibility(View.GONE);

        setNeedSetProjectEditFunctions();
        (mActivity).setActionBarTitle(TimeHelper.getInstance().getCuteDateTitleS(new Date(calendar.getTimeInMillis())), false, mCheckedProject);
    }

    private void setDateTitle2(Date date) {
        final View v = LayoutInflater.from(mActivity).inflate(R.layout.custom_title, null);
        v.setId(R.id.action_bar);
        /*Date dateTMP = date;
        dateTMP.setHours(dateTMP.getHours() + new Date().getTimezoneOffset() / 60);*/
        ((ImageView) v.findViewById(R.id.triangle)).setVisibility(View.GONE);

        setNeedSetProjectEditFunctions();
        (mActivity).setActionBarTitle(TimeHelper.getInstance().getCuteDateTitle(date), false, mCheckedProject);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MENU_ITEM);
        filter.addAction(TaskSaveHelper.ACTION_SCROLL_TO_NEW_TASK);
        filter.addAction(IPCConstants.ACTION_SYNCHRONIZATION_STATE_CHANGED);
        filter.addAction(MenuFragment.ACTION_UPDATE_ACTION_BAR);
        filter.addAction(ACTION_CALENDAR_ITEM);
        return filter;
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        super.onBroadcastReceive(context, intent);

        switch (intent.getAction()) {
            case ACTION_CALENDAR_ITEM:
            case ACTION_MENU_ITEM:
                final BaseMenuItem menuItem = (BaseMenuItem) intent.getSerializableExtra(MenuFragment.EXTRA_MENU_ITEM);
                if ((mParent == null && (menuItem.getMenuItemType() != mMenuItem.getMenuItemType() || menuItem.getUniqueId() != mMenuItem.getUniqueId())) ||
                        mParent != null && Utils.isLandOrientation(getApp())) {
                    if (mActivity != null && mActivity.getFragmentManager() != null) {
                        mActivity.getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    LTaskCache.getInstance(getApp()).clear();
                    startFragment(TasksFragment.newInstance(menuItem, null), false);
                }

                break;

            case TaskSaveHelper.ACTION_SCROLL_TO_NEW_TASK:
                isAddedNewTask = true;
                break;

            case IPCConstants.ACTION_SYNCHRONIZATION_STATE_CHANGED:
                if (SynchronizationTask.isSwipeSync) {
                    setRefreshing(getApp().isSync());
                    if (!getApp().isSync()) {
                        SynchronizationTask.isSwipeSync = false;
                    }
                }
                break;

            case MenuFragment.ACTION_UPDATE_ACTION_BAR:
                setActionBar();
                break;

            default:
                break;
        }
    }

    protected void setRefreshing(boolean refreshing) {
        if (refreshing) {
            LTPowerManager.getInstance(getApp());
        } else {
            LTPowerManager.getInstance(getApp()).sleepUnlock();
        }
        setSwipeRefreshLayoutRefresh(refreshing);
    }

    private void setSwipeRefreshLayoutEnabled() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    isScrolling = false;
                    if (mScrollUnreadArray.size() > 0) {
                        if (mActionUnreadTop != null && mActionUnreadBottom != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(700);
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isScrolling) { // если вдруг изменилось состояние за пол секунды
                                                    mActionUnreadTop.setVisibility(View.GONE);
                                                    mActionUnreadBottom.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    } catch (InterruptedException e) {

                                    }

                                }
                            }).start();
                            // если остановились но еще не долистали - то листаем еще
                            if (scrollToPos != -1) {
                                if (scrollFirstVisible <= scrollToPos &&  scrollToPos <= scrollFirstVisible+scrollVisibleCount) {
                                    scrollToPos = -1;
                                    //android.util.Log.v("Tedorius", "Закончили");
                                } else {
                                    mListView.smoothScrollToPosition(scrollToPos);
                                    //android.util.Log.v("Tedorius", "Опять скроллим");
                                }
                            }

                            //
                        }
                    }
                }
                if (scrollState == 1) {
                    isScrolling = true;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int topRowVerticalPosition = (mListView == null || mListView.getChildCount() == 0) ?
                        0 : mListView.getChildAt(0).getTop();
                canSwipeToRefresh = (topRowVerticalPosition >= 0 && mListView.getFirstVisiblePosition() == 0);
                SlidingActivity.mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        SlidingActivity.mSwipeRefreshLayout.setEnabled(canSwipeToRefresh && !isCheckModeOn && !isAddModeOn);
                    }
                });

                //
                setActionUnreadButtons();

                scrollFirstVisible = firstVisibleItem;
                scrollVisibleCount = visibleItemCount;

            }
        });
    }

    private void setActionUnreadButtons() {
        if (mActionUnreadTop != null && mActionUnreadBottom != null) {

            if (isScrolling) {
                boolean alreadySetTop = false;
                boolean alreadySetBottom = false;

                if (mScrollUnreadArray.size() > 0) {
                    for (int i = 0; i < mScrollUnreadArray.size(); i++) {
                        if (!alreadySetTop) {
                            if ((int) mScrollUnreadArray.get(i) < mListView.getFirstVisiblePosition()) {
                                // нижнюю показываем
                                toTop = (int) mScrollUnreadArray.get(i);
                                alreadySetTop = true;
                            }
                        }
                        if (!alreadySetBottom) {
                            if ((int) mScrollUnreadArray.get(i) > mListView.getLastVisiblePosition()) {
                                // показываем верхнюю
                                toBottom = (int) mScrollUnreadArray.get(i);
                                alreadySetBottom = true;
                            }
                        }
                    }
                }

                mActionUnreadTop.setVisibility(alreadySetTop ? View.VISIBLE : View.GONE);
                mActionUnreadBottom.setVisibility(alreadySetBottom ? View.VISIBLE : View.GONE);

                if (alreadySetTop) {
                    mActionUnreadTop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListView.post(new Runnable() {
                                @Override
                                public void run() {
                                scrollToPos = toTop;
                                mListView.smoothScrollToPosition(toTop);
//                                    mListView.setSelection(toTop);
                                }
                            });
                        }
                    });
                }
                if (alreadySetBottom) {
                    mActionUnreadBottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListView.post(new Runnable() {
                                @Override
                                public void run() {
                                scrollToPos = toBottom;
                                mListView.smoothScrollToPosition(toBottom);
//                                mListView.setSelection(toBottom);
                                }
                            });
                        }
                    });
                }
            } else {
                mActionUnreadTop.setVisibility(View.GONE);
                mActionUnreadBottom.setVisibility(View.GONE);
            }
        }
    }

    private void setSwipeRefreshLayoutRefresh(final boolean refresh) {
        SlidingActivity.mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                SlidingActivity.mSwipeRefreshLayout.setRefreshing(refresh);
                Calendar calendar = Calendar.getInstance(DEFAULT_TIME_ZONE);
            }
        });
    }

    public static TasksFragment newInstance(BaseMenuItem menuItem, LTask parent) {
        final Bundle b = new Bundle(2);
        b.putSerializable(EXTRA_MENU_ITEM, menuItem);
        b.putSerializable(EXTRA_PARENT_TASK, parent);

        final TasksFragment f = new TasksFragment();
        f.setArguments(b);

        return f;
    }

    private boolean isPerformerOrCustomerInTask(LTask task)
    {
        String userName = LTSettings.getInstance().getUserName();
        boolean isCustomer = userName.equals(task.getEmailCustomer());
        boolean isPerformer = userName.equals(task.getEmailPerformer());
        if(isCustomer || isPerformer) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Boolean showSlidingMenu() {
        return mParent == null;
    }

    public void MenuItemSelectEvent(int id) {
        switch (id) {
            case R.id.menu_subtasks_icon:
                startFragment(TasksFragment.newInstance(mMenuItem, mTempTask));
                mTempTask = null;
                break;
            case R.id.menu_properties_icon:
                //startActivity(EditTaskActivity.newInstance(mActivity, mTempTask, false));
                break;
            case R.id.menu_assign_icon:
                //TaskPerformerDialog.newInstance(this, mTempTask).showDialog(getFragmentManager());
                break;
            case R.id.menu_term_icon:
                //TaskTermDialog.newInstance(this, mTempTask).showDialog(getFragmentManager());
                break;
            case R.id.menu_delete_icon:
                //Utils.getSimpleDialog(mActivity, getDeleteDialogListener(), R.string.confirm_delete_title, R.string.confirm_delete_text);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickBack() {
        addNewTaskMulti();
        //mTempAddTask = null;
        //mActivity.swapToolbarModeToAddTasks(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    public final class TouchEvent implements View.OnTouchListener {

        private final WeakReference<FragmentManager> mManager;
        private float mX;
        private float mY;

        public TouchEvent(FragmentManager manager) {
            mManager = new WeakReference<FragmentManager>(manager);
        }

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(firstTouch) {
                        mX = e.getX();
                        mY = e.getY();
                        firstTouch = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(firstTouch) {
                        mX = e.getX();
                        mY = e.getY();
                        firstTouch = false;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    final float x = Math.abs(e.getX() - mX);
                    final float y = Math.abs(e.getY() - mY);
                    firstTouch = true;
                    if (e.getX() > mX && x > X_MIN && x < X_MAX && y > 0 && y < Y_MAX) {
                        final FragmentManager manager = mManager.get();
                        if (manager != null) {
                            manager.popBackStack();
                        }
                        return true;
                    }
                    //
                    if (e.getX() < mX && x > X_MIN && x < X_MAX && y > 0 && y < Y_MAX) {
                        openMultiMode();
                        return true;
                    }
                    break;
            }

            return false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public final class TouchEvent2 implements View.OnTouchListener {

        private final WeakReference<FragmentManager> mManager;
        private float mX;
        private float mY;

        public TouchEvent2(FragmentManager manager) {
            mManager = new WeakReference<FragmentManager>(manager);
        }

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(firstTouch) {
                        mX = e.getX();
                        mY = e.getY();
                        firstTouch = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(firstTouch) {
                        mX = e.getX();
                        mY = e.getY();
                        firstTouch = false;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    final float x = Math.abs(e.getX() - mX);
                    final float y = Math.abs(e.getY() - mY);
                    firstTouch = true;
                    if (e.getX() < mX && x > X_MIN && x < X_MAX && y > 0 && y < Y_MAX) {
                        openMultiMode();
                        return true;
                    }
                    break;
            }

            return false;
        }
    }


    private static final class SimpleDownloadFileThread extends Thread {

        private final LTApplication mApp;
        private final TaskFile mFile;

        public SimpleDownloadFileThread(LTApplication app, TaskFile file) {
            super(SimpleDownloadFileThread.class.getSimpleName());

            mApp = app;
            mFile = file;
        }

        @Override
        public void run() {
            super.run();

            final String fileUid = String.valueOf(mFile.getFileId());
            final String fileName = mFile.getFileName();

            try {
                new DownloadFile(mApp, fileUid, fileName, mApp.getSettings().getUserProfile(), mApp.getAppFolder(), 0).downloadFile();

            } catch (Exception e) {

            }
        }
    }

    public void setBlockingProcess(boolean value) {
        lockOrientation(value);

        if (value) {
            if (mProgress == null) {
                mProgress = new ProgressDialog(mActivity);
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setMessage(getString(R.string.blocking_process));
            }
            mProgress.show();
        } else {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

    private void lockOrientation(boolean lock) {
        mActivity.setRequestedOrientation(lock ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    private void resetActionsUndeaded() {
        mScrollUnreadArray = findUnreadPositions();
    }

    public ArrayList findUnreadPositions() {
        ArrayList positions = new ArrayList();
        Cursor cursor = mAdapter.getCursor();
        //
        LTaskCache mTaskCache = LTaskCache.getInstance(getActivity());

        if (cursor != null && !cursor.isClosed() && mTaskCache != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                LTask task = new LTask(cursor);
                if (mTaskCache.find(task.getIdTask()) != null) {
                    final TaskTotalLink totalLink = mTaskCache.find(task.getIdTask()).getTaskTotal();
                    boolean unreaded = false;
                    if (totalLink != null) {
                        if (LTSettings.getInstance().isMakeTaskHide()) {
                            unreaded = totalLink.getTasksUncompletedUnreaded() > 0;

                        } else {
                            unreaded = totalLink.getTasksUnreaded() > 0;
                        }
                    }

                    if (!task.getReaded() && task.getStatus() != Status.TASK_CANCELLED.getStatusCode() && task.getStatus() != Status.TASK_COMPLETED.getStatusCode()) {
                        positions.add(cursor.getPosition());
                    } else {
                        if (unreaded) {
                            positions.add(cursor.getPosition());
                        }
                    }
                }
            }
        }
        return positions;
    }
}