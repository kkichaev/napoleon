package com.ashberrysoft.leadertask.modern.domains.menu;

import com.ashberrysoft.leadertask.enums.MenuItemType;

public class CalendarMenuItem implements BaseMenuItem {

    private static final long serialVersionUID = 1L;

    private final long mDate;

    public CalendarMenuItem(long date) {
        mDate = date;
    }

    @Override
    public long getUniqueId() {
        return mDate;
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
        return MenuItemType.CALENDAR_DAY;
    }

    @Override
    public String getName() {
        return null;
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
        return false;
    }

    @Override
    public void setVisible(boolean visible) {}

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

    @Override
    public int getTasksFocus() {
        return 0;
    }
}