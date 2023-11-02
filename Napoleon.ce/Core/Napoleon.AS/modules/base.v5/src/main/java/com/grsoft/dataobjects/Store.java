package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;

@ServerInfo(name="Stores")
public class Store extends DataObject {
    public static String CFG_KEY = "Склады";

    public String id = "";
    public String name = "";
}
