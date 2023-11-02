package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="requestpa", keyFields="created")
public class Procuration extends CreateDocDataObject {
	public String route = "";
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	public String fio = "";
	public String org = "";
	public String firm = "";
}
