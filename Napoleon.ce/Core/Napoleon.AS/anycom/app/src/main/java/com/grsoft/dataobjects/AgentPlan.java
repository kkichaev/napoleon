package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="agentPlan", keyFields = "begin")
@ServerInfo(name="AgentPlan")
public class AgentPlan extends DataObject{
    public Date begin = new Date();

    public List<AgentPlanItem> plans = new ArrayList<>();
}
