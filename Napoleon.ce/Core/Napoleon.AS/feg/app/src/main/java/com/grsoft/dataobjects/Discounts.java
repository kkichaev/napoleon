package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="Dscnts", keyFields = "id")
@ServerInfo(name="Discounts")
public class Discounts extends DataObject {
    public static int DSC_TYPE_IN_HLIST = 0;
    public static int DSC_NOT_TYPE_IN_HLIST = 1;

    public String id = "";

    @Scale(value = Consts.SUM_SCALE)
    public int discount = 0;

    public int type = DSC_TYPE_IN_HLIST;

    public List<DiscountItem> items = new ArrayList<>();
}
