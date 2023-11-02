package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="AgentProcent")
@ServerInfo(name="AgentProcent")
public class AgentProcent extends DataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int procent;

	public static double getProcent() {
		double res = 0;
		DbReader r = new DbReader();
		AgentProcent data = new AgentProcent();
		if( r.select(data, data.getTableName(), "")  )
			res = (double)data.procent / Consts.SUM_SCALE;
		r.close();
		return res;
	}
}
