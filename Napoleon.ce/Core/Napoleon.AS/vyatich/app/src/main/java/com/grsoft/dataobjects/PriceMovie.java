package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="priceMovie", keyFields = "id")
@ServerInfo(name="PriceMovie")
public class PriceMovie extends DataObject {
    public String id = "";
    public String url = "";
    public String file = "";
    public int received = 1;
}
