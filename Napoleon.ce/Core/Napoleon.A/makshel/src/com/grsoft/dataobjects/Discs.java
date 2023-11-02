package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="discs", indexes="id")
@ServerInfo(name="Discs")
public class Discs extends DataObject {
	public static final int REL_ITEM = 1;
	public static final int REL_MFROWR = 2;
	public static final int REL_MFR = 3;
	public static final int REL_OWR = 4;
	
	public String id = "";
	public int type = 0;
	public int relation = 0;
	public String idItem = "";
	public String idMfr = "";
	public String idOwr = "";
	@Scale(value=Consts.SUM_SCALE)
	public int maxdisc = 0;
	public int mindisc = 0;
}
