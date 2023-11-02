package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="Selling", keyFields = "created")
@ServerInfo(name="Selling")
public class Selling extends Order {
    public int bmark = 0;

    public String title = "";
}
