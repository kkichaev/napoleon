package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="DivisionStock", keyFields = "id")
public class DivisionStock extends DataObject{
    public String id = "";
    public List<DivisionStockItem> items = new ArrayList<>();
}
