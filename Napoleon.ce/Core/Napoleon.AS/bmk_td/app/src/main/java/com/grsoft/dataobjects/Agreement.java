package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name = "agreements", keyFields = "id")
@ServerInfo(name="Agreement")
public class Agreement extends DataObject{
    public String id = "";
    public String name = "";
    public List<AgreementItem> items = new ArrayList<>();

    @Override public String toString() {return name;}
}
