package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name = "Orgmatrix", keyFields = "id")
@ServerInfo(name="OrgMatrix")
public class OrgMatrix extends DataObject {
    public String id = "";
    public List<MatrixItem> items = new ArrayList<>();
}
