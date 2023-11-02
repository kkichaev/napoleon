package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.List;

@TableInfo(name = "AgentInfo", keyFields = "userid")
@ServerInfo(name = "AgentInfo")
public class AgentInfo extends DataObject {
    public String userid = "";
    public String id = "";

    @Scale(value = Consts.SUM_SCALE)
    public int income = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int debet = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int overdue = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int limit = 0;

    public static AgentInfo get() {
        List<AgentInfo> ai = DbReader.fetch(AgentInfo.class, "id=userid");
        return ai.size() > 0? ai.get(0) : new AgentInfo();
    }
}
