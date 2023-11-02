package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="orddlv",keyFields="userid,created")
@ServerInfo(name="OrdDlv")
public class OrdDlv extends DataObject {
	public String userid = "";
	public String agent = "";
	public Date created;
	@Scale(value=Consts.SUM_SCALE)
	public int sum = 0;
	@Scale(value=Consts.SUM_SCALE)
	public int sumd = 0;
	public String id = "";
	public String org = "";
	public String address = "";
	public Date sended;
}
