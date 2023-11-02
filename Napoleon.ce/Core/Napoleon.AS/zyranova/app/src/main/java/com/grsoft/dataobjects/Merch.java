package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="Merch", keyFields = "created")
public class Merch extends CreateDocDataObject {
    public List<MerchItem> items = new ArrayList<>();
}
