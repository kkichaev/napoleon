package com.ashberrysoft.leadertask.enums;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;

public enum MenuItemType implements BaseMenuItem {


    CALENDAR_DAY(R.string.task_today, R.drawable.icon_today_new, null), //
    TODAY(R.string.task_today, R.drawable.icon_today_new, null), //
    UNREAD(R.string.task_unread, R.drawable.not_readed, null), //
    INBOX(R.string.task_inbox, R.drawable.inbox, null), //
    READY(R.string.task_ready, R.drawable.status5, null), //
    INWORK(R.string.task_in_work, R.drawable.status4, null), //
    OVERDUE(R.string.overdue_tasks, R.drawable.calendar_overdue, null), //
    FOCUS(R.string.task_focus, R.drawable.focus_active, null),
    EMAILS(R.string.task_access_nav, R.drawable.access, null),
    //
    HEADER_BY_ME(R.string.sm_instruct_i, 0, null), //
    BY_ME(0, R.drawable.emp_from_me, HEADER_BY_ME), //
    //
    HEADER_FOR_ME(R.string.sm_instruct_me, 0, null), //
    FOR_ME(0, R.drawable.emp_to_me, HEADER_FOR_ME), //
    //
    HEADER_PROJECTS(R.string.sm_projects, 0, null), //
    PROJECTS(0, R.drawable.project, HEADER_PROJECTS), //
    PROJECTS_SHARED(0, R.drawable.project_shared, HEADER_PROJECTS), //

    ADD_PROJECT(0, R.drawable.add_black, HEADER_PROJECTS), //
    ADD_CATEGORY(0, R.drawable.add_black, HEADER_PROJECTS), //
    ADD_COLOR(0, R.drawable.add_black, HEADER_PROJECTS), //
    ADD_EMP(0, R.drawable.add_black, HEADER_PROJECTS), //
    //
    HEADER_AVAILABLE_PROJECTS(R.string.sm_available_me, 0, null), //
    AVAILABLE_PROJECTS(0, R.drawable.project_available, HEADER_AVAILABLE_PROJECTS), //
    //
    HEADER_CATEGORIES(R.string.sm_categories, 0, null), //
    CATEGORIES(0, R.drawable.category_white_big, HEADER_CATEGORIES),

    HEADER_COLORS(R.string.settings_Markers, 0, null), //
    COLOR(0, R.drawable.marker_black, HEADER_COLORS),

    HEADER_EMPS(R.string.title_emp, 0, null), //
    EMP(0, R.drawable.emp_simple, HEADER_EMPS);//

    final int mNameId;
    final int mImageId;
    final MenuItemType mHeader;

    int mTasks;
    int mTasksUnreaded;
    int mTasksUncompleted;
    int mTasksUncompletedUnreaded;
    int mTasksNotes;
    int mTasksFocus;

    MenuItemType(int nameId, int imageId, MenuItemType header) {
        mNameId = nameId;
        mImageId = imageId;
        mHeader = header;
    }

    public MenuItemType getHeader() {
        return mHeader;
    }

    @Override
    public long getUniqueId() {
        return -ordinal();
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
        return this;
    }

    @Override
    public String getName() {
        return null;
    }

    public int getNameId() {
        return mNameId;
    }

    public int getImageId() {
        return mImageId;
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
    public void setOpened(boolean opened) {}

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {}

    @Override
    public int getTasks() {
        return mTasks;
    }

    @Override
    public int getTasksUnreaded() {
        return mTasksUnreaded;
    }

    @Override
    public int getTasksUncompleted() {
        return mTasksUncompleted;
    }

    @Override
    public int getTasksUncompletedUnreaded() {
        return mTasksUncompletedUnreaded;
    }

    @Override
    public int getTasksNotes() {
        return mTasksNotes;
    }

    public void setTasks(int count) {
        mTasks = count;
    }

    public void setTasksUnreaded(int count) {
        mTasksUnreaded = count;
    }

    public void setTasksUncompleted(int count) {
        mTasksUncompleted = count;
    }

    public void setTasksUncompletedUnreaded(int count) {
        mTasksUncompletedUnreaded = count;
    }

    public void setTasksNotes(int count) {
        mTasksNotes = count;
    }

    @Override
    public int getTasksFocus() {
        return mTasksFocus;
    }

    public void setTaskFocus(int focus){
        mTasksFocus = focus;
    }


}