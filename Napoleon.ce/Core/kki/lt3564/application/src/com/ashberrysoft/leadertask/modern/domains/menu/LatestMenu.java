package com.ashberrysoft.leadertask.modern.domains.menu;

import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LatestMenu implements BaseMenuItem {

    private static final long serialVersionUID = 1L;

    private static final String KEY_UNIQUE_ID = "KEY_UNIQUE_ID";
    private static final String KEY_UID = "KEY_UID";
    private static final String KEY_ORDER = "KEY_ORDER";
    private static final String KEY_MENU_ITEM_TYPE = "KEY_MENU_ITEM_TYPE";
    private static final String KEY_NAME = "KEY_NAME";

    private final long mUniqueId;
    private final String mUid;
    private final int mOrder;
    private final MenuItemType mMenuItemType;
    private final String mName;

    public LatestMenu(String string) {
        final JsonElement je = new JsonParser().parse(string);
        final JsonObject jo = je.getAsJsonObject();

        mUniqueId = jo.get(KEY_UNIQUE_ID).getAsLong();
        mUid = Utils.getStringFromJo(jo, KEY_UID);
        mOrder = jo.get(KEY_ORDER).getAsInt();
        mMenuItemType = MenuItemType.values()[jo.get(KEY_MENU_ITEM_TYPE).getAsInt()];
        mName = Utils.getStringFromJo(jo, KEY_NAME);
    }

    @Override
    public long getUniqueId() {
        return mUniqueId;
    }

    @Override
    public String getUid() {
        return mUid;
    }

    @Override
    public int getOrder() {
        return mOrder;
    }

    @Override
    public MenuItemType getMenuItemType() {
        return mMenuItemType;
    }

    @Override
    public String getName() {
        return mName;
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

    public static String baseMenuItemToString(BaseMenuItem menuItem) {
        final JsonObject jo = new JsonObject();

        jo.addProperty(KEY_UID, menuItem.getUid());
        jo.addProperty(KEY_NAME, menuItem.getName());
        jo.addProperty(KEY_ORDER, menuItem.getOrder());
        jo.addProperty(KEY_UNIQUE_ID, menuItem.getUniqueId());
        jo.addProperty(KEY_MENU_ITEM_TYPE, menuItem.getMenuItemType().ordinal());

        return String.valueOf(jo);
    }
}