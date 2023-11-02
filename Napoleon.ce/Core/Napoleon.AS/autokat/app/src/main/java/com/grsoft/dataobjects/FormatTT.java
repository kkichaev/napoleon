package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="FormatTT", keyFields = "id")
@ServerInfo(name="FormatTT")
public class FormatTT extends DataObject{
    public String id = "";
    public String name = "";
    public int pos = 0;

    public List<FormatTT> items = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
