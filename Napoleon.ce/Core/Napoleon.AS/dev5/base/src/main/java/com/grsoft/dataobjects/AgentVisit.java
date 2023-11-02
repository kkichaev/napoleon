package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="agentvisit", keyFields="date")
@ServerInfo(name="AgentVisit")
public class AgentVisit extends DataObject {
    public Date date;
    public long count;
}
