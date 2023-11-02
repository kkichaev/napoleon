package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name = "target", keyFields = "created")
public class Target extends CreateDocDataObject {
    public int closed = 0;
}
