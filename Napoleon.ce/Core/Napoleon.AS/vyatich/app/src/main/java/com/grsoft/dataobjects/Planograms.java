package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Planograms", keyFields = "id")
public class Planograms extends DataObject {
    public String id = "";
    public String name = "";
    public byte[] photo = null;
}
