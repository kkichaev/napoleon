package com.ashberrysoft.leadertask.modern.loader;

import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;

public class EmailsMenuItem implements BaseMenuItem {
    public int countItems = 0;

    @Override
    public long getUniqueId() {
        return 0;
    }

    @Override
    public String getUid() {
        return "";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public MenuItemType getMenuItemType() {
        return MenuItemType.EMAILS;
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

    @Override
    public int getTasksFocus() {
        return 0;
    }
}
