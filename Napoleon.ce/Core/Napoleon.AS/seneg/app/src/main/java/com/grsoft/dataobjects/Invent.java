package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Invent", keyFields = "created")
public class Invent extends Order {
    public String link = "";
}
