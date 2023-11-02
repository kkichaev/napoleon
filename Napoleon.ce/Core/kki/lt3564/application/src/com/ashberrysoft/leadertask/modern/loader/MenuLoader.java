package com.ashberrysoft.leadertask.modern.loader;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.LinearLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.domains.link.BaseCollapsibleTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.FocusTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ForMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.InboxTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.InworkTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.OverdueTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ReadyTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.UnreadTotalLink;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.modern.loader.BaseByEmailTaskLoader.OnEmailWithEmployeesLoaderListener;
import com.ashberrysoft.leadertask.modern.loader.BaseCollapsibleTaskLoader.OnCollapsibleTaskLoaderListener;
import com.ashberrysoft.leadertask.utils.Utils;

import static android.R.attr.action;
import static android.R.attr.fragment;
import static android.R.attr.order;
import static android.R.id.list;
import static com.ashberrysoft.leadertask.R.drawable.cursor;
import static com.ashberrysoft.leadertask.R.drawable.employee;
import static com.ashberrysoft.leadertask.R.id.date;
import static com.ashberrysoft.leadertask.R.string.task;
import static com.ashberrysoft.leadertask.enums.MenuItemType.PROJECTS;
import static com.ashberrysoft.leadertask.enums.MenuItemType.PROJECTS_SHARED;

public class MenuLoader {

    public interface OnMenuLoaderResult {

        void onMenuResult(List<BaseMenuItem> list);

        void onMenuResultFor(List<BaseMenuItem> list, MenuItemType item);
    }

    // SINGLETON
    private static MenuLoader sInstance;

    // BASE
    private final Context mContext;

    // VALUE's
    private final CursorLoader mCursorLoader;

    private final List<BaseMenuItem> mAllItems;
    private final int MAGIG_VALUE_FOR_ITEMS_COUNT = 6;
    private int mItemsCount = MAGIG_VALUE_FOR_ITEMS_COUNT;

    private final CalendarTotalLink mTodayItem;
    private final OverdueTotalLink mOverdueItem;
    private final UnreadTotalLink mUnread;
    private final ReadyTotalLink mReady;
    private final InworkTotalLink mInwork;
    private final InboxTotalLink mInboxItem;
    private final List<ByMeTotalLink> mByMeItems;
    private final List<ForMeTotalLink> mForMeItems;
    private final List<ProjectTotalLink> mProjectItems;
    private final List<ProjectTotalLink> mAvailableProjectItems;
    private final List<CategoryTotalLink> mCategoryItems;
    private final List<ColorTotalLink> mColorItems;
    private final List<EmpTotalLink> mEmpItems;
    private final FocusTotalLink mFocus;
    public EmailsMenuItem emailsMenuItem;

    /*private static BaseMenuItem mAddNewProject;
    private static BaseMenuItem mAddNewCategory;
    private static BaseMenuItem mAddNewColor;
    private static BaseMenuItem mAddNewEmp;*/

    public static boolean justAddNewProject = false;
    public static boolean justAddNewCategory = false;
    public static boolean justAddNewColor = false;

    private static WeakReference<MenuFragment> mFragment;
    private LTApplication mApp;

    public static MenuLoader getInstance(final Context context) {
        /*mAddNewProject = new BaseMenuItem() {
            @Override
            public long getUniqueId() {
                return 124655754;
            }

            @Override
            public String getUid() {
                return null;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItemType getMenuItemType() {
                return MenuItemType.ADD_PROJECT;
            }

            @Override
            public String getName() {
                return context.getString(R.string.project_add);
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public boolean hasBelow() {
                return false;
            }

            @Override
            public boolean isOpened() {
                return false;
            }

            @Override
            public void setOpened(boolean opened) {

            }

            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public void setVisible(boolean visible) {

            }

            @Override
            public int getTasks() {
                return 0;
            }

            @Override
            public int getTasksUnreaded() {
                return 0;
            }

            @Override
            public int getTasksUncompleted() {
                return 0;
            }

            @Override
            public int getTasksUncompletedUnreaded() {
                return 0;
            }

            @Override
            public int getTasksNotes() {
                return 0;
            }
        };

        mAddNewCategory = new BaseMenuItem() {
            @Override
            public long getUniqueId() {
                return 934574736;
            }

            @Override
            public String getUid() {
                return null;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItemType getMenuItemType() {
                return MenuItemType.ADD_CATEGORY;
            }

            @Override
            public String getName() {
                return context.getString(R.string.add_categoty);
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public boolean hasBelow() {
                return false;
            }

            @Override
            public boolean isOpened() {
                return false;
            }

            @Override
            public void setOpened(boolean opened) {

            }

            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public void setVisible(boolean visible) {

            }

            @Override
            public int getTasks() {
                return 0;
            }

            @Override
            public int getTasksUnreaded() {
                return 0;
            }

            @Override
            public int getTasksUncompleted() {
                return 0;
            }

            @Override
            public int getTasksUncompletedUnreaded() {
                return 0;
            }

            @Override
            public int getTasksNotes() {
                return 0;
            }
        };

        mAddNewColor = new BaseMenuItem() {
            @Override
            public long getUniqueId() {
                return 935849304;
            }

            @Override
            public String getUid() {
                return null;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItemType getMenuItemType() {
                return MenuItemType.ADD_COLOR;
            }

            @Override
            public String getName() {
                return context.getString(R.string.add_color);
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public boolean hasBelow() {
                return false;
            }

            @Override
            public boolean isOpened() {
                return false;
            }

            @Override
            public void setOpened(boolean opened) {

            }

            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public void setVisible(boolean visible) {

            }

            @Override
            public int getTasks() {
                return 0;
            }

            @Override
            public int getTasksUnreaded() {
                return 0;
            }

            @Override
            public int getTasksUncompleted() {
                return 0;
            }

            @Override
            public int getTasksUncompletedUnreaded() {
                return 0;
            }

            @Override
            public int getTasksNotes() {
                return 0;
            }
        };

        mAddNewEmp = new BaseMenuItem() {
            @Override
            public long getUniqueId() {
                return 435349105;
            }

            @Override
            public String getUid() {
                return null;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItemType getMenuItemType() {
                return MenuItemType.ADD_EMP;
            }

            @Override
            public String getName() {
                return context.getString(R.string.add_emp);
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public boolean hasBelow() {
                return false;
            }

            @Override
            public boolean isOpened() {
                return false;
            }

            @Override
            public void setOpened(boolean opened) {

            }

            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public void setVisible(boolean visible) {

            }

            @Override
            public int getTasks() {
                return 0;
            }

            @Override
            public int getTasksUnreaded() {
                return 0;
            }

            @Override
            public int getTasksUncompleted() {
                return 0;
            }

            @Override
            public int getTasksUncompletedUnreaded() {
                return 0;
            }

            @Override
            public int getTasksNotes() {
                return 0;
            }
        };*/

        if (sInstance == null) {
            synchronized (MenuLoader.class) {
                if (sInstance == null) {
                    sInstance = new MenuLoader(context);
                }
            }
        }
        return sInstance;
    }

    private MenuLoader(Context context) {
        mContext = context.getApplicationContext();
        mApp = (LTApplication) context.getApplicationContext();

        mCursorLoader = new CursorLoader();

        mTodayItem = new CalendarTotalLink();
        mOverdueItem = new OverdueTotalLink();
        mUnread = new UnreadTotalLink();
        mReady = new ReadyTotalLink();
        mInwork = new InworkTotalLink();
        mInboxItem = new InboxTotalLink();
        mByMeItems = new ArrayList<>();
        mForMeItems = new ArrayList<>();
        mProjectItems = new ArrayList<>();
        mAvailableProjectItems = new ArrayList<>();
        mCategoryItems = new ArrayList<>();
        mColorItems = new ArrayList<>();
        mEmpItems = new ArrayList<>();
        mFocus = new FocusTotalLink();
        emailsMenuItem = new EmailsMenuItem();

        mAllItems = new ArrayList<>();
        mAllItems.add(mTodayItem);
        //mAllItems.add(mUnread);
        mAllItems.add(mInboxItem);
    }

    public List<BaseMenuItem> process(MenuFragment fragment) {
        mFragment = new WeakReference<>(fragment);
        restartLoader();

        return new ArrayList<>(mAllItems);
    }

    public List<BaseMenuItem> processFor(MenuFragment fragment, boolean full, MenuItemType itemType) {
        mFragment = new WeakReference<>(fragment);
        if (full) {
            restartLoader();
            return new ArrayList<>(mAllItems);
        } else {
            List<BaseMenuItem> mItems = new ArrayList<>();
            if (fragment != null) {
                final LoaderManager lm = fragment.getLoaderManager();
                switch (itemType) {
                    case BY_ME:
                        lm.restartLoader(MenuItemType.BY_ME.ordinal(), null, mCursorLoader);
                        mItems.addAll(mByMeItems);
                        break;

                    case FOR_ME:
                        lm.restartLoader(MenuItemType.FOR_ME.ordinal(), null, mCursorLoader);
                        mItems.addAll(mForMeItems);
                        break;

                    case PROJECTS:
                        lm.restartLoader(PROJECTS.ordinal(), null, mCursorLoader);
                        mItems.addAll(mProjectItems);
                        break;

                    case AVAILABLE_PROJECTS:
                        lm.restartLoader(MenuItemType.AVAILABLE_PROJECTS.ordinal(), null, mCursorLoader);
                        mItems.addAll(mAvailableProjectItems);
                        break;

                    case CATEGORIES:
                        lm.restartLoader(MenuItemType.CATEGORIES.ordinal(), null, mCursorLoader);
                        mItems.addAll(mCategoryItems);
                        break;

                    case COLOR:
                        lm.restartLoader(MenuItemType.COLOR.ordinal(), null, mCursorLoader);
                        mItems.addAll(mColorItems);
                        break;

                    case EMP:
                        lm.restartLoader(MenuItemType.EMP.ordinal(), null, mCursorLoader);
                        mItems.addAll(mEmpItems);
                        break;

                    default:
                        break;
                }
            }

            return new ArrayList<>(mItems);
        }
    }

    public void resetCalendar() {
        if (mFragment != null) {
            final MenuFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.resetCalendarView();
            }
        }
    }

    public void resetCount() {
        mItemsCount = MAGIG_VALUE_FOR_ITEMS_COUNT;
    }

    public void resetMenu() {
        mAllItems.clear();

        mTodayItem.resetValues();
        mOverdueItem.resetValues();
        mUnread.resetValues();
        mInboxItem.resetValues();
        mReady.resetValues();
        mInwork.resetValues();
        mFocus.resetValues();

        mAllItems.add(mTodayItem);
    }

    public void resetTodayItem() {
        mTodayItem.resetValues();
    }

    public void resetInboxItem() {
        mInboxItem.resetValues();
    }

    /** Use just when change MakeTaskHide */
    public void restartLoader() {
        try {
            final MenuFragment fragment = mFragment.get();
            if (fragment != null) {
                final LoaderManager lm = fragment.getLoaderManager();

                mItemsCount = MAGIG_VALUE_FOR_ITEMS_COUNT; // если меня это то нужно поменять еще в строках по // TODO:
                lm.restartLoader(PROJECTS.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.AVAILABLE_PROJECTS.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.CATEGORIES.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.TODAY.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.READY.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.OVERDUE.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.INWORK.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.UNREAD.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.INBOX.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.BY_ME.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.FOR_ME.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.COLOR.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.EMP.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.FOCUS.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.EMAILS.ordinal(), null, mCursorLoader);
            }
        } catch (Exception e) {
            int m = 0;
            m++;
        }
    }

    public void restartLoaderSimple() {
        try {
            final MenuFragment fragment = mFragment.get();
            if (fragment != null) {
                final LoaderManager lm = fragment.getLoaderManager();

                mItemsCount = MAGIG_VALUE_FOR_ITEMS_COUNT; // если меня это то нужно поменять еще в строках по // TODO:
                lm.restartLoader(MenuItemType.TODAY.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.READY.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.OVERDUE.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.INWORK.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.UNREAD.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.INBOX.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.FOCUS.ordinal(), null, mCursorLoader);
                lm.restartLoader(MenuItemType.EMAILS.ordinal(), null, mCursorLoader);
            }
        } catch (Exception e) {

        }
    }

    private final class CursorLoader //
            implements LoaderCallbacks<Cursor>, OnEmailWithEmployeesLoaderListener, OnCollapsibleTaskLoaderListener {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle b) {
            final MenuItemType item = MenuItemType.values()[id];

            switch (item) {
            case TODAY:
                return new CalendarDayLoader(mContext);

            case UNREAD:
                return new UnreadLoader(mContext, true);

            case READY:
                return new ReadyLoader(mContext, true);

            case INWORK:
                return new InworkLoader(mContext, true);

            case INBOX:
                return new InboxLoader(mContext, true);

            case OVERDUE:
                return new OverdueLoader(mContext, true);

            case BY_ME:
                return new ByMeLoader(mContext, this);

            case EMP:
                return new EmpLoader(mContext, this);

            case FOR_ME:
                return new ForMeLoader(mContext, this);

            case PROJECTS:
            case AVAILABLE_PROJECTS:
                return new ProjectsLoader(mContext, this, item);

            case CATEGORIES:
                return new CategoriesLoader(mContext, this);

            case COLOR:
                return new ColorLoader(mContext, this);

            case FOCUS:
                return new FocusLoader(mContext, true);

            case EMAILS:
                return new EmailsLoader(mContext, emailsMenuItem);

            default:
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            final MenuItemType item = MenuItemType.values()[loader.getId()];

            switch (item) {
            case TODAY:
                if (cursor.getCount() == 0) {
                    mTodayItem.resetValues();
                }
                if (cursor.moveToFirst()) {
                    mTodayItem.fillFromCursor(cursor);
                }
                --mItemsCount;
                break;

            case OVERDUE:
                if (cursor.getCount() == 0) {
                    mOverdueItem.resetValues();
                }
                if (cursor.moveToFirst()) {
                    mOverdueItem.fillFromCursor(cursor);
                }
                --mItemsCount;
                break;

            case UNREAD:
                if (cursor.getCount() == 0) {
                    mUnread.resetValues();
                }
                if (cursor.moveToFirst()) {
                    mUnread.fillFromCursor(cursor);
                }
                --mItemsCount;
                break;

            case READY:
                if (cursor.getCount() == 0) {
                    mReady.resetValues();
                }
                if (cursor.moveToFirst()) {
                    mReady.fillFromCursor(cursor);
                }
                --mItemsCount;
                break;

            case INWORK:
                if (cursor.getCount() == 0) {
                    mInwork.resetValues();
                }
                if (cursor.moveToFirst()) {
                    mInwork.fillFromCursor(cursor);
                }
                --mItemsCount;
                break;

            case INBOX:
                if (cursor.getCount() == 0) {
                    mInboxItem.resetValues();
                }
                if (cursor.moveToFirst()) {
                    mInboxItem.fillFromCursor(cursor);
                }
                --mItemsCount;
                break;

            case FOCUS:
                if (cursor.getCount() == 0) {
                    mFocus.resetValues();
                }
                if (cursor.moveToFirst()) {
                    mFocus.fillFromCursor(cursor);
                }
                --mItemsCount;
                break;

            default:
                break;
            }

            //if (mItemsCount <= 0) {
                updateItemsList();
                mItemsCount = MAGIG_VALUE_FOR_ITEMS_COUNT; // TODO: 09.11.2017 тут менять
            //}
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //if (--mItemsCount <= 0) {
                updateItemsList();
                mItemsCount = MAGIG_VALUE_FOR_ITEMS_COUNT; // TODO: 09.11.2017 тут менять
            //}
        }

        @Override
        public void onByMeLinksLoad(List<ByMeTotalLink> tasks) {
            mByMeItems.clear();
            mByMeItems.addAll(tasks);

            updateItemsListByMe();
        }

        @Override
        public void onEmpLinksLoad(List<EmpTotalLink> tasks) {
            mEmpItems.clear();
            mEmpItems.addAll(tasks);

            updateItemsListEmp();
        }

        @Override
        public void onForMeLinksLoad(List<ForMeTotalLink> tasks) {
            mForMeItems.clear();
            mForMeItems.addAll(tasks);

            updateItemsListForMe();
        }

        @Override
        public void onProjectLinksLoad(List<ProjectTotalLink> links) {
            mProjectItems.clear();
            mProjectItems.addAll(links);

            updateItemsListProject();
        }

        @Override
        public void onProjectLinksLoadAvailable(List<ProjectTotalLink> links) {
            mAvailableProjectItems.clear();
            mAvailableProjectItems.addAll(links);

            updateItemsListProjectAvailable();
        }

        @Override
        public void onCategoryLinksLoad(List<CategoryTotalLink> links) {
            mCategoryItems.clear();
            mCategoryItems.addAll(links);

            updateItemsListCategories();
        }

        @Override
        public void onColorLinksLoad(List<ColorTotalLink> links) {
            mColorItems.clear();
            mColorItems.addAll(links);

            updateItemsListColors();
        }
    }

    public void updateItemsListProject() {
        ArrayList <BaseMenuItem> items = new ArrayList<>();
//        Utils.timeChecker("updateItemsListProject");
        if (mProjectItems.size() > 0) {
            items.add(MenuItemType.HEADER_PROJECTS);
            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_PROJECTS)) {
                items.addAll(mProjectItems);
                // Добавить проект кнопка
                //items.add(mAddNewProject);
            }
        } else {
            items.add(MenuItemType.HEADER_PROJECTS);
            // Добавить проект кнопка
            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_PROJECTS)) {
                // Добавить проект кнопка
                //items.add(mAddNewProject);
            }
        }
//        Utils.timeChecker("updateItemsListProject");

        deliverResultFor(items, MenuItemType.PROJECTS);

    }

    public void updateItemsListProjectAvailable() {
        ArrayList <BaseMenuItem> items = new ArrayList<>();
//        Utils.timeChecker("updateItemsListProjectAvailable");
        if (mAvailableProjectItems.size() > 0) {
            items.add(MenuItemType.HEADER_AVAILABLE_PROJECTS);
            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_AVAILABLE_PROJECTS)) {
                items.addAll(mAvailableProjectItems);
            }
        }
//        Utils.timeChecker("updateItemsListProjectAvailable");

        deliverResultFor(items, MenuItemType.AVAILABLE_PROJECTS);


    }

    public void updateItemsListByMe() {
        ArrayList <BaseMenuItem> items = new ArrayList<>();
//        Utils.timeChecker("updateItemsListByMe");
        if (mByMeItems.size() > 0) {
            items.add(MenuItemType.HEADER_BY_ME);
            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_BY_ME)) {
                items.addAll(mByMeItems);
            }
        }
//        Utils.timeChecker("updateItemsListByMe");

        deliverResultFor(items, MenuItemType.BY_ME);

    }

    public void updateItemsListForMe() {
        ArrayList <BaseMenuItem> items = new ArrayList<>();

//        Utils.timeChecker("updateItemsListForMe");
        if (mForMeItems.size() > 0) {
            items.add(MenuItemType.HEADER_FOR_ME);
            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_FOR_ME)) {
                items.addAll(mForMeItems);
            }
        }

//        Utils.timeChecker("updateItemsListForMe");
        deliverResultFor(items, MenuItemType.FOR_ME);

    }

    public void updateItemsListCategories() {
        ArrayList <BaseMenuItem> items = new ArrayList<>();

//        Utils.timeChecker("updateItemsListCategories");
        if (LTSettings.getInstance().showCategoriesInNavigator()) {
            items.add(MenuItemType.HEADER_CATEGORIES);
            if (mCategoryItems.size() > 0) {
                final List<Category> list = DbHelper.getInstance(mContext).getAllMyCategories();
                for (CategoryTotalLink categoryTotalLink : mCategoryItems) {
                    for (Category category : list) {
                        if (category.getId() != null) {
                            if (category.getId().equals(UUID.fromString(categoryTotalLink.getUid()))) {
                                if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_CATEGORIES)) {
                                    items.add(categoryTotalLink);
                                }
                                break;
                            }
                        }
                    }
                }
            }

            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_CATEGORIES)) {
                // Добавить категорию
                //items.add(mAddNewCategory);
            }
        }
//
//        Utils.timeChecker("updateItemsListCategories");
        deliverResultFor(items, MenuItemType.CATEGORIES);

    }

    public void updateItemsListColors() {
        ArrayList <BaseMenuItem> items = new ArrayList<>();

//        Utils.timeChecker("updateItemsListColors");
        if (LTSettings.getInstance().showColorsInNavigator()) {
            final List<Marker> list = DbHelper.getInstance(mContext).getAllMarkersNew();
            items.add(MenuItemType.HEADER_COLORS);
            for (Marker Color : list) {
                if (Color.getId() != null && Color.getCreator() != null) {
                    if (Color.getCreator().equals(LTSettings.getInstance().getUserName())) {
                        if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_COLORS)) {
                            if (mColorItems.size() > 0) { // если вообще есть задачи в с цетами
                                boolean isFounded = false;
                                for (ColorTotalLink ColorTotalLink : mColorItems) {
                                    if (Color.getId().toString().toLowerCase().equals(ColorTotalLink.getUid().toLowerCase())) {
                                        if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_COLORS)) {
                                            ColorTotalLink.setName(Color.getName());
                                            items.add(ColorTotalLink);
                                        }
                                        isFounded = true;
                                        break;
                                    }
                                }
                                if (!isFounded) { // если не нашли значит там задачи нет и нужно создать TotalLink
                                    ColorTotalLink totalLink = UpdateFeatureLinkHelper.createColorTotal(mContext, Color);
                                    if (totalLink != null) {
                                        if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_COLORS)) {
                                            items.add(totalLink);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_COLORS)) {
                // Добавить цвет
                //items.add(mAddNewColor);
            }
        }
//        Utils.timeChecker("updateItemsListColors");

        deliverResultFor(items, MenuItemType.COLOR);

    }

    public void updateItemsListEmp() {
        ArrayList <BaseMenuItem> items = new ArrayList<>();
        ArrayList <BaseMenuItem> finalItems = new ArrayList<>();

        Utils.timeChecker("updateItemsListEmpNew");
        if (LTSettings.getInstance().isEmpsInNavigator()) {
            final List<Employee> list = DbHelper.getListEmployeesForNavNew(mContext);

            finalItems.add(MenuItemType.HEADER_EMPS);
            if (list.size() > 0) {
                for (Employee employee : list) {
                    if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_EMPS)) {
                        boolean fonded = false;
                        for (EmpTotalLink EmpTotalLink : mEmpItems) {
                            if (employee != null && EmpTotalLink != null) {
                                if (employee.getEmail().equals(EmpTotalLink.getUid())) {
                                    if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_EMPS)) {
                                        if (EmpTotalLink.getName() == null && EmpTotalLink.getUid().equals(LTSettings.getInstance().getUserName())) {
                                            EmpTotalLink.setName("" + EmployeeCache.getInstance(mContext).find(EmpTotalLink.getUid()));
                                        }
                                        items.add(EmpTotalLink);
                                        fonded = true;
                                    }
                                    break;
                                }
                            }
                        }
                        if (!fonded) {
                            try {
                                EmpTotalLink totalLink = EmpTotalLink.class.newInstance();
                                totalLink.setUid(employee.getEmail());
                                totalLink.setName("" + EmployeeCache.getInstance(mContext).find(employee.getEmail()));

                                if (totalLink != null) {
                                    if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_EMPS)) {
                                        items.add(totalLink);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }

                    }
                }
            }
        }

        //сортировка
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null, null,
                    null, LeaderTaskProviderMetaData.EmpContract.ORDERS);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String login = cursor.getString(cursor.getColumnIndex(LeaderTaskProviderMetaData.EmpContract.LOGIN));
                        if (login != null) {
                            for (BaseMenuItem item : items) {
                                if (login.equals(item.getUid())) {
                                    finalItems.add(item);
                                    //break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // проверка на дубли
        /*for (BaseMenuItem item :finalItems) {
            boolean founded = false;
            for (BaseMenuItem finalI : finalSuperItems) {
                if (finalI.getUid() != null) {
                    if (finalI.getUid().equals(item.getUid())) {
                        founded = true;
                    }
                }
            }
            if (!founded) {
                finalSuperItems.add(item);
            }
        }*/

        if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
            if (!LTSettings.getInstance().isDroppedHeader(MenuItemType.HEADER_EMPS) && LTSettings.getInstance().isEmpsInNavigator()) {
                // Добавить сотрудника
                //finalItems.add(mAddNewEmp);
            }
        }

        //finalItems.addAll(items);

        Utils.timeChecker("updateItemsListEmpNew");

        deliverResultFor(finalItems, MenuItemType.EMP);

    }

    public void updateItemsList() {
        Utils.timeChecker("updateItemsList");
        mAllItems.clear();
        mAllItems.add(mTodayItem);

        if (!LTSettings.getInstance().isOverdueInToday() && mOverdueItem.getTasksUncompleted() > 0) {
            mAllItems.add(mOverdueItem);
        }

        if (emailsMenuItem.countItems > 0)
            mAllItems.add(emailsMenuItem);

        if (mInboxItem.getTasksNotes() != 0 || ((LTSettings.getInstance().isMakeTaskHide() ? mInboxItem.getTasksUncompleted() != 0 : mInboxItem.getTasks() != 0))) {
            mAllItems.add(mInboxItem);
        }

        // включены ли у нас непрочитанные в навигаторе в настройках и есть ли непрочитанные задачи
        if (mUnread.getTasksUncompletedUnreaded() > 0 && LTSettings.getInstance().isShowUnreadTasks()) {
            mAllItems.add(mUnread);
        }

        if (mInwork.getTasksUncompleted() > 0) {
            mAllItems.add(mInwork);
        }

        if (mReady.getTasksUncompleted() > 0) {
            mAllItems.add(mReady);
        }

        if (mFocus.getTasksFocus() > 0)
            mAllItems.add(mFocus);

        Utils.timeChecker("updateItemsList");
        deliverResult();

    }

    public void resetMyFoto() {
        if (mFragment != null) {
            final MenuFragment fragment = mFragment.get();
            fragment.resetMyFoto();
        }
    }

    public void deliverResult() {
        final MenuFragment fragment = mFragment.get();
        if (fragment != null) {
            fragment.onMenuResult(new ArrayList<>(mAllItems));
        }
    }

    public void deliverResultFor(ArrayList <BaseMenuItem> arrayList, MenuItemType item) {
        final MenuFragment fragment = mFragment.get();
        if (fragment != null) {
            fragment.onMenuResultFor(new ArrayList<>(arrayList), item);
        }
    }

    public ProjectTotalLink findProjectTask(String uid) {
        if (uid == null) {
            return null;
        }

        final ProjectTotalLink value = findCollapsible(mProjectItems, uid);
        if (value != null) {
            return value;
        }

        return findCollapsible(mAvailableProjectItems, uid);
    }

    public CategoryTotalLink findCategoryTask(String uid) {
        return uid == null ? null : findCollapsible(mCategoryItems, uid);
    }

    private static <T extends BaseCollapsibleTotalLink<T>> T findCollapsible(List<T> values, String uid) {
        T value = null;
        for (T parent : values) {
            if (parent.getUid().equals(uid)) {
                return parent;
            }
            if (parent.getChilds() != null) {
                value = findCollapsibleChild(parent.getChilds(), uid);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    private static <T extends BaseCollapsibleTotalLink<T>> T findCollapsibleChild(List<T> childs, String uid) {
        T value = null;
        for (T child : childs) {
            if (child.getUid().equals(uid)) {
                return child;
            }
            if (child.getChilds() != null) {
                value = findCollapsibleChild(child.getChilds(), uid);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }
}