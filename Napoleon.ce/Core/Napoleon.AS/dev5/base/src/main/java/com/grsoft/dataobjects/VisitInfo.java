package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="visitinfo", keyFields = "created")
@ServerInfo(name="VisitInfo")
public class VisitInfo extends CreateDocDataObject {

}
