package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="Selling", keyFields = "created")
@ServerInfo(name="Selling")
public class Selling extends Order {
    public static final String PAY_TYPE_CASH = "1";
    public static final String PAY_TYPE_BANK = "2";

    public int bmark = 0;

    public String title = "";
    public String payType = "";
}
