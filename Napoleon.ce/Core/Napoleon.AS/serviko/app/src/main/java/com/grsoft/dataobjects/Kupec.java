package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="kupec", keyFields = "created")
public class Kupec extends CreateDocDataObject{
    public String regionID;
    public List<KupecItem> items = new ArrayList<>();
}
