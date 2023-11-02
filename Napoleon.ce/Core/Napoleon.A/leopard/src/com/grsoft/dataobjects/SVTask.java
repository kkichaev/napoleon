package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="svtask",keyFields="created")
public class SVTask extends CreateDocDataObject {
    public Date execDate;
    public Date appointDate;
    public String category;
    public String text = "";
    @Scale(value=1)
    public int flags = 0;
    public Date script = new Date();
}
