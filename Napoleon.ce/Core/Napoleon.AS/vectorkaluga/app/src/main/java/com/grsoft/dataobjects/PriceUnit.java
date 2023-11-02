package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="PriceUnits", keyFields = "id")
@ServerInfo(name="PriceUnits")
public class PriceUnit extends DataObject {
    public String id = "";

    public List<UnitItem> items = new ArrayList<>();
}
