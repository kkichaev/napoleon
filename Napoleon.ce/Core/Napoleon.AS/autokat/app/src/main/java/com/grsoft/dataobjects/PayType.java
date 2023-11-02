package com.grsoft.dataobjects;


import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PayType", keyFields = "id")
@ServerInfo(name="PayTypes")
public class PayType extends DataObject{

    public static final String PSA2 = "ПСА 2";
    static final String CASH = "Наличная оплата";
    static final String NONCASH = "Безналичная оплата";

    public String id = "";
    public String name = "";
    public int pos = 0;

    @Override
    public String toString() { return name; }

    public boolean isPSA2() { return name.equals(PSA2); }
    public boolean isCash() { return name.equals(CASH);}
    public boolean isNonCash() {return name.equals(NONCASH); }
}
