package com.ashberrysoft.leadertask.modern.domains.menu;

import java.io.Serializable;

import com.ashberrysoft.leadertask.enums.MenuItemType;

public interface BaseMenuItem extends Serializable {

    /** some unique number */
    long getUniqueId();

    /** uid */
    String getUid();

    /** order in list */
    int getOrder();

    MenuItemType getMenuItemType();

    String getName();

    /** used to create cascade */
    int getLevel();

    /** used to show childs */
    boolean hasBelow();

    /** used to controll opening status */
    boolean isOpened();

    void setOpened(boolean opened);

    /** used to controll visibility status */
    boolean isVisible();

    void setVisible(boolean visible);

    int getTasks();

    int getTasksUnreaded();

    int getTasksUncompleted();

    int getTasksUncompletedUnreaded();

    int getTasksNotes();

    int getTasksFocus();
}