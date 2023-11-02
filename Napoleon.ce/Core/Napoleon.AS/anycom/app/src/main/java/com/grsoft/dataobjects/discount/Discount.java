package com.grsoft.dataobjects.discount;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="Discount", keyFields = "id")
@ServerInfo(name="Discount")
public class Discount extends DiscountElement {
    public Date start = new Date();
    public Date finish = new Date();

    public List<DiscountItem> items = new ArrayList<>();
}
