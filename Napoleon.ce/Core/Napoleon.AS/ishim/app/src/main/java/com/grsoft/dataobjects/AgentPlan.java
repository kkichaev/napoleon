package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="agentPlans", keyFields = "begin,id")
@ServerInfo(name="AgentPlanRcv")
public class AgentPlan extends DataObject {
	public Date begin = new Date();
	public String id = "";

	@Scale(value = Consts.WEIGHT_SCALE)
	public int weight = 0;
	public int akb = 0;
	public String name = "";

	public List<MatrixItem> items = new ArrayList<>();
}
