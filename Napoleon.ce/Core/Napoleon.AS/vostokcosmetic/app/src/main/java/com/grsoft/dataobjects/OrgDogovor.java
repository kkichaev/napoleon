package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="dogovors", keyFields = "ido,id")
@ServerInfo(name = "OrgDogovor")
public class OrgDogovor extends DataObject {
    public String id = "";
    public String name = "";
    public String ido = "";

    public List<OrgFolderDiscount> fldDsc = new ArrayList<>();
    public List<OrgPriceCost> price = new ArrayList<>();
    public List<OrgDisabledFolder> disabled = new ArrayList<>();

    @Override public String toString() { return name; }
}
