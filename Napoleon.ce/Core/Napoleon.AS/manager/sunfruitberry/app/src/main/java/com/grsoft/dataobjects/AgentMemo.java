package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

@TableInfo(name="agent_memo", keyFields = "created")
@ServerInfo(name="AgentMemo")
public class AgentMemo extends CreateDocDataObject {
    public int orgColor = 0;
    public int dogColor = 0;

    public String idDog = "";
    public String topic = "";

    // use as linked doc in create invoice request
    public Date till = new Date();

    @Scale(value=Consts.SUM_SCALE)
    public long sum = 0;

    @Scale(value=Consts.SUM_SCALE)
    public long sumD = 0;

    @Scale(value=Consts.SUM_SCALE)
    public long overdueSum = 0;
    public int overdue = 0;

    public String email = "";
    public String topicName = "";

    public String deliveries = "";
}
