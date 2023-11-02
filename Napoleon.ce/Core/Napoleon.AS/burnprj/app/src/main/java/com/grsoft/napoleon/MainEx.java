package com.grsoft.napoleon;

import com.grsoft.util.MenuHandler;

import java.util.ArrayList;

public class MainEx extends Main{
    protected ArrayList<MenuHandler> createDocMenuList() {
        docMenu = new ArrayList<MenuHandler>();

        docMenu.add(new MenuHandler(getString(R.string.doc_list), new Runnable() {
            @Override
            public void run() {
                DocList.open(MainEx.this);
            }
        }));

        docMenu.add(new MenuHandler(getString(R.string.msg_list), new Runnable() {
            @Override
            public void run() {
                Messages.open(MainEx.this);
            }
        }));

        docMenuPrepared.menuPrepared(docMenu, MainEx.this);
        return docMenu;
    }
}
